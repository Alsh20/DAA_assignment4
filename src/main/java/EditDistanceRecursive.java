import java.util.Objects;

public final class EditDistanceRecursive {
    private EditDistanceRecursive() {}

    public static int distance(String a, String b) {
        return distance(a, b, new Metrics());
    }

    public static int distance(String a, String b, Metrics metrics) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        Objects.requireNonNull(metrics, "metrics");
        return solve(a, b, a.length(), b.length(), metrics);
    }

    private static int solve(String a, String b, int i, int j, Metrics metrics) {
        metrics.incrementWorkUnits();
        if (i == 0) {
            return j;
        }
        if (j == 0) {
            return i;
        }

        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return solve(a, b, i - 1, j - 1, metrics);
        }

        int delete = solve(a, b, i - 1, j, metrics);
        int insert = solve(a, b, i, j - 1, metrics);
        int replace = solve(a, b, i - 1, j - 1, metrics);
        return 1 + Math.min(delete, Math.min(insert, replace));
    }
}
