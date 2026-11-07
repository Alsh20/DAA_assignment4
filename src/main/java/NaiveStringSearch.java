import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaiveStringSearch {
    private NaiveStringSearch() {}

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

        int lastStart = text.length() - pattern.length();
        for (int start = 0; start <= lastStart; start++) {
            int offset = 0;
            while (offset < pattern.length()) {
                metrics.incrementWorkUnits();
                if (text.charAt(start + offset) != pattern.charAt(offset)) {
                    break;
                }
                offset++;
            }
            if (offset == pattern.length()) {
                matches.add(start);
            }
        }
        return matches;
    }
}
