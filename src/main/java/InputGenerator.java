import java.util.Random;

public final class InputGenerator {
    public static final long SEED = 42L;
    private static final String EDIT_ALPHABET = "ABCD";
    private static final String SEARCH_ALPHABET = "ABCDEFGH";

    private InputGenerator() {}

    public record Pair(String first, String second) {}

    public static Pair editPair(String inputType, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be nonnegative");
        }
        return switch (inputType) {
            case "random" -> {
                Random random = new Random(seedFor(inputType, n));
                yield new Pair(randomString(random, n, EDIT_ALPHABET),
                        randomString(random, n, EDIT_ALPHABET));
            }
            case "hard-mismatch" -> new Pair("X".repeat(n), "Y".repeat(n));
            default -> throw new IllegalArgumentException("unknown edit input: " + inputType);
        };
    }

    public static Pair searchPair(String inputType, int textLength, int patternLength) {
        if (textLength < 0 || patternLength < 1) {
            throw new IllegalArgumentException("invalid text or pattern length");
        }
        return switch (inputType) {
            case "random" -> {
                Random random = new Random(seedFor(inputType, textLength));
                yield new Pair(randomString(random, textLength, SEARCH_ALPHABET),
                        randomString(random, patternLength, SEARCH_ALPHABET));
            }
            case "repeated-prefix" -> new Pair(
                    "A".repeat(textLength), "A".repeat(patternLength - 1) + "B");
            default -> throw new IllegalArgumentException("unknown search input: " + inputType);
        };
    }

    private static long seedFor(String inputType, int n) {
        return SEED + 1_000_003L * n + inputType.hashCode();
    }

    private static String randomString(Random random, int length, String alphabet) {
        StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return value.toString();
    }
}
