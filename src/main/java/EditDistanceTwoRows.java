import java.util.Objects;

public final class EditDistanceTwoRows {
    private EditDistanceTwoRows() {}

    public static int distance(String a, String b) {
        return distance(a, b, new Metrics());
    }

    public static int distance(String a, String b, Metrics metrics) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        Objects.requireNonNull(metrics, "metrics");
        int n = a.length();
        int m = b.length();
        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];
        metrics.addStorageCells(2L * (m + 1));

        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= n; i++) {
            current[0] = i;
            for (int j = 1; j <= m; j++) {
                metrics.incrementWorkUnits();
                int replaceCost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(
                        previous[j] + 1,
                        Math.min(current[j - 1] + 1, previous[j - 1] + replaceCost));
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[m];
    }
}
