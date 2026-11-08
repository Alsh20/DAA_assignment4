import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Benchmark {
    private static final int[] RECURSIVE_SIZES = {2, 4, 6, 10};
    private static final int[] DP_SIZES = {128, 256, 512, 1_024};
    private static final int[] SEARCH_SIZES = {2_048, 8_192, 32_768, 131_072};
    private static final int SEARCH_PATTERN_LENGTH = 64;
    private static final int[] EDIT_REPETITIONS = {3, 5};
    private static final String[] EDIT_INPUTS = {"random", "hard-mismatch"};
    private static final String[] SEARCH_INPUTS = {"random", "repeated-prefix"};
    private static final Path OUTPUT = Path.of("results", "results.csv");

    private Benchmark() {}

    @FunctionalInterface
    private interface EditRunner {
        int run(String a, String b, Metrics metrics);
    }

    @FunctionalInterface
    private interface SearchRunner {
        List<Integer> run(String text, String pattern, Metrics metrics);
    }

    private record Row(String algorithm, String input, int n, int patternLength, int run,
                       double timeMs, long workUnits, long storageCells, String result) {}

    public static void main(String[] args) throws IOException {
        List<Row> rows = new ArrayList<>();
        runEditFamily(rows, "recursive", RECURSIVE_SIZES, 3,
                EditDistanceRecursive::distance);
        runEditFamily(rows, "table", DP_SIZES, 5,
                EditDistanceTable::distance);
        runEditFamily(rows, "two-rows", DP_SIZES, 5,
                EditDistanceTwoRows::distance);
        runSearchFamily(rows, "naive", NaiveStringSearch::search);
        runSearchFamily(rows, "kmp", KmpStringSearch::search);
        writeRows(rows);
        System.out.printf("Wrote %d raw timing rows to %s (seed %d).%n",
                rows.size(), OUTPUT, InputGenerator.SEED);
    }

    private static void runEditFamily(List<Row> rows, String algorithm, int[] sizes,
                                     int repetitions, EditRunner runner) {
        for (String input : EDIT_INPUTS) {
            for (int n : sizes) {
                InputGenerator.Pair pair = InputGenerator.editPair(input, n);
                int expected = EditDistanceTable.distance(pair.first(), pair.second());
                int warmup = runner.run(pair.first(), pair.second(), new Metrics());
                requireEqual(expected, warmup, algorithm, input, n);

                for (int run = 1; run <= repetitions; run++) {
                    Metrics metrics = new Metrics();
                    long start = System.nanoTime();
                    int answer = runner.run(pair.first(), pair.second(), metrics);
                    long elapsed = System.nanoTime() - start;
                    requireEqual(expected, answer, algorithm, input, n);
                    rows.add(new Row(algorithm, input, n, 0, run,
                            elapsed / 1_000_000.0, metrics.workUnits(),
                            metrics.storageCells(), Integer.toString(answer)));
                }
            }
        }
    }

    private static void runSearchFamily(List<Row> rows, String algorithm, SearchRunner runner) {
        for (String input : SEARCH_INPUTS) {
            for (int n : SEARCH_SIZES) {
                InputGenerator.Pair pair = InputGenerator.searchPair(
                        input, n, SEARCH_PATTERN_LENGTH);
                List<Integer> expected = NaiveStringSearch.search(pair.first(), pair.second());
                List<Integer> warmup = runner.run(pair.first(), pair.second(), new Metrics());
                if (!expected.equals(warmup)) {
                    throw new IllegalStateException(algorithm + " warm-up disagrees with naive search");
                }

                for (int run = 1; run <= 5; run++) {
                    Metrics metrics = new Metrics();
                    long start = System.nanoTime();
                    List<Integer> answer = runner.run(pair.first(), pair.second(), metrics);
                    long elapsed = System.nanoTime() - start;
                    if (!expected.equals(answer)) {
                        throw new IllegalStateException(algorithm + " disagrees with naive search");
                    }
                    rows.add(new Row(algorithm, input, n, SEARCH_PATTERN_LENGTH, run,
                            elapsed / 1_000_000.0, metrics.workUnits(),
                            metrics.storageCells(), Integer.toString(answer.size())));
                }
            }
        }
    }

    private static void requireEqual(int expected, int actual, String algorithm,
                                     String input, int n) {
        if (expected != actual) {
            throw new IllegalStateException(algorithm + " disagrees on " + input + ", n=" + n);
        }
    }

    private static void writeRows(List<Row> rows) throws IOException {
        Files.createDirectories(OUTPUT.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(OUTPUT, StandardCharsets.UTF_8)) {
            writer.write("algorithm,input,n,pattern_length,run,time_ms,work_units,storage_cells,result");
            writer.newLine();
            for (Row row : rows) {
                writer.write(String.format(java.util.Locale.ROOT,
                        "%s,%s,%d,%d,%d,%.6f,%d,%d,%s",
                        row.algorithm(), row.input(), row.n(), row.patternLength(), row.run(),
                        row.timeMs(), row.workUnits(), row.storageCells(), row.result()));
                writer.newLine();
            }
        }
    }
}
