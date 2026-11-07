public final class Metrics {
    private long workUnits;
    private long storageCells;

    public void incrementWorkUnits() {
        workUnits++;
    }

    public void addWorkUnits(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be nonnegative");
        }
        workUnits += amount;
    }

    public void addStorageCells(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be nonnegative");
        }
        storageCells += amount;
    }

    public long workUnits() {
        return workUnits;
    }

    public long storageCells() {
        return storageCells;
    }
}
