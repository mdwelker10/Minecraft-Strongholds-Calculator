package model;

/**
 * Represents a ring where strongholds generate. Each ring holds the range of blocks from the origin
 * a stronghold can generate and the number of strongholds that generates in that ring.
 */
public enum Ring {

    RING1(1280, 2816, 3, 1),
    RING2(4352, 5888, 6, 2),
    RING3(7424, 8960, 10, 3),
    RING4(10496, 12032, 15, 4),
    RING5(13568, 15104, 21, 5),
    RING6(16640, 18176, 28, 6),
    RING7(19712, 21248, 36, 7),
    RING8(22784, 24320, 9, 8);

    /**
     * maximum distance from origin a stronghold will generate in the ring
     */
    private final int max;
    /**
     * minimum distance from origin a stronghold will generate in the ring
     */
    private final int min;
    /**
     * number of strongholds that generates in the ring
     */
    private final int numStrongholds;

    /**
     * Average of the min and max of this Stronghold
     */
    private final int averageDist;

    /**
     * What number ring this Ring is (1 through 8) - used for display purposes in GUI
     */
    private final int ringNum;

    Ring(final int max, final int min, final int numStrongholds, final int ringNum) {
        this.max = max;
        this.min = min;
        this.numStrongholds = numStrongholds;
        this.ringNum = ringNum;
        averageDist = (min + max) / 2;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getNumStrongholds() {
        return numStrongholds;
    }

    public int getRingNum() {
        return ringNum;
    }

    public double getAverageDistance() {
        return averageDist;
    }
}
