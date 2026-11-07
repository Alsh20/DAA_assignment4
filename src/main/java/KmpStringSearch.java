import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class KmpStringSearch {
    private KmpStringSearch() {}

    public static int[] lps(String pattern) {
        Objects.requireNonNull(pattern, "pattern");
        return buildLps(pattern, new Metrics());
    }

    public static List<Integer> search(String text, String pattern) {
        return search(text, pattern, new Metrics());
    }

    public static List<Integer> search(String text, String pattern, Metrics metrics) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(metrics, "metrics");
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }

        List<Integer> matches = new ArrayList<>();
        if (pattern.length() > text.length()) {
            return matches;
        }

        int[] prefix = buildLps(pattern, metrics);
        int textIndex = 0;
        int patternIndex = 0;
        while (textIndex < text.length()) {
            metrics.incrementWorkUnits();
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
                if (patternIndex == pattern.length()) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = prefix[patternIndex - 1];
                }
            } else if (patternIndex > 0) {
                patternIndex = prefix[patternIndex - 1];
            } else {
                textIndex++;
            }
        }
        return matches;
    }

    private static int[] buildLps(String pattern, Metrics metrics) {
        int[] prefix = new int[pattern.length()];
        int length = 0;
        int i = 1;
        while (i < pattern.length()) {
            metrics.incrementWorkUnits();
            if (pattern.charAt(i) == pattern.charAt(length)) {
                prefix[i++] = ++length;
            } else if (length > 0) {
                length = prefix[length - 1];
            } else {
                prefix[i++] = 0;
            }
        }
        return prefix;
    }
}
