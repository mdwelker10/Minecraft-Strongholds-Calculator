package model;


import java.util.HashSet;

/**
 * This class does all calculations that actually make the program function using various methods to process
 * the coordinates the user gives and provide a list of coordinates that the other strongholds should be close to
 * using Trigonometry. The coordinates should be meaured by standing in the center of the starter staircase,
 * more info on how to properly get the coordinates in the README.
 *
 * @author Matthew Welker
 */
public class RingCalculator {
    /**
     * x coordinate of the stronghold that has been found
     */
    private final double x;
    /**
     * z coordinate of the stronghold that has been found
     */
    private final double z;
    /**
     * what ring the stronghold is in using calculation or manual input
     */
    private Ring ring;

    /**
     * Initializes necessary values using given coords
     * Adds 4 to both coordinates in order to do calculations from the chunk center, which is the point that
     * the Stronghold actually generates from and where a thrown Eye of Ender will point.
     *
     * @param x The given x coordinate
     * @param z The given z coordinate
     * @param ignore Whether to catch the ringCoords exception or not, will only be used if the user will be
     * manually entering the ring after an IllegalCoordsException was already thrown
     * @throws IllegalCoordsException If the coordinates are not in a Stronghold ring and ignore is false
     */
    public RingCalculator(double x, double z, boolean ignore) throws IllegalCoordsException {
        try {
            ring = getRing(x, z);
        } catch (IllegalCoordsException e) {
            if (!ignore) throw new IllegalCoordsException();
        }
        //because stronghold staircase is 4-4 but generation is 8-8
        this.x = x + 4;
        this.z = z + 4;
    }

    /**
     * Gets the given x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the given z coordinate
     */
    public double getZ() {
        return z;
    }

    /**
     * Gets the number of strongholds in the ring
     */
    public int getNumStrongholds() {
        return ring.getNumStrongholds();
    }

    /**
     * Gets the ring that the stronghold is in
     */
    public int getRing() {
        return ring.getRingNum();
    }

    /**
     * Sets the ring the Stronghold is in manually
     *
     * @param n The number to set the ring to
     * @throws IllegalArgumentException If the ring given is not a valid ring (1 to 8)
     */
    public void setRing(int n) {
        if (n < 1 || n > 8) {
            throw new IllegalArgumentException("Invalid ring number, must be 1-8.");
        }
        for (Ring r : Ring.values()) {
            if (r.getRingNum() == n) {
                ring = r;
                return;
            }
        }
    }

    /**
     * Guesses what ring a set of coordinates are in.
     * This method assumes that the stronghold snaps outside the ring meaning that the ring could not be
     * set by the ringCoords() method.
     * The guess is made by calculating the distance from the origin and finding which ring has the closest
     * minimum or maximum Stronghold spawn distance.
     *
     * @param x The x coordinate of the supposed Stronghold
     * @param z The z coordinate of the supposed Stronghold
     * @return The Ring that the set of coordinates is estimated to be in
     */
    public Ring guessRing(double x, double z) {
        double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        if (dist < Ring.RING1.getMin())
            return Ring.RING1;
        //check if dist is between one min and another max, if so see which is closer
        for (int i = 0; i < Ring.values().length - 1; i++) {
            Ring r1 = Ring.values()[i];
            Ring r2 = Ring.values()[i + 1];
            if (dist > r1.getMax() && dist < r2.getMin()) {
                if (dist - r1.getMin() < r2.getMax() - dist) {
                    return r1;
                }
                return r2;
            }
        }
        return Ring.RING8;
    }

    /**
     * Given a set of coordinates returns which stronghold ring those coordinates are in
     *
     * @param x x coordinate
     * @param z z coordinate
     * @return The stronghold ring that would contain those coordinates
     * @throws IllegalCoordsException If the coordinates are not within the bounds of a ring
     */
    public static Ring getRing(double x, double z) throws IllegalCoordsException {
        double dist = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)));
        for (Ring r : Ring.values()) {
            if (r.getMin() > dist && r.getMax() < dist)
                return r;
        }
        throw new IllegalCoordsException("Coordinates not inside of a Stronghold Ring");
    }

    /**
     * Method that actually does the calculation for stronghold locations based on the given x and z values
     * along with the calculated values for the ring, angle, and number of strongholds in the ring
     *
     * @param nether Whether to have the coordinates as nether coordinates or not
     * @return A HashSet of ideal nether travel coordinates or overworld coordinates
     */
    public HashSet<Coords> calcStrongholds(boolean nether) {
        double angleBetween = 360.0 / ring.getNumStrongholds(); //The angle between strongholds
        int dist = (ring.getMax() + ring.getMin()) / 2; //Mean distance in the ring for the Stronghold
        double angle = getAngle();
        double temp = angle; //temp value to use for calculations
        HashSet<Coords> coords = new HashSet<>();
        //get all other coordinate values
        temp += angleBetween;
        while (temp < 180) {
            coords.add(getCoords(temp, dist, nether));
            temp += angleBetween;
        }
        temp = angle;
        temp -= angleBetween;
        while (temp > -180) {
            coords.add(getCoords(temp, dist, nether));
            temp -= angleBetween;
        }
        return coords;
    }

    //Returns the angle from the origin this stronghold is in
    //uses the x and z values
    private double getAngle() {
        double angle = -180.0; //to be returned, is default for when x=0 and z<0
        //Formula based on soh-cah-toa for MC coord system
        if (z > 0) {
            angle = Math.toDegrees(Math.atan(x / z));
        } else {
            if (x != 0) {
                angle = Math.abs(Math.toDegrees(Math.atan(z / x)));
                if (angle > 0)
                    angle += 90;
                else
                    angle -= 90;
            }
        }
        //pos x means negative angle
        if (x > 0)
            angle *= -1;
        return angle;
    }

    //Gets a set of coordinates (overworld) based on angle and distance
    //throws IAE if angle is not valid (between -180 and 180)
    private Coords getCoords(double angle, int dist, boolean nether) {
        if (-180 > angle || 180 < angle)
            throw new IllegalArgumentException("Invalid angle");
        Coords c;
        //Cases when one is 0
        if (angle == 0) c = new Coords(0, dist);
        else if (angle == 90) c = new Coords(-1 * dist, 0);
        else if (angle == -90) c = new Coords(dist, 0);
        else if (angle == -180) c = new Coords(0, -1 * dist);
            //all other cases
        else if (angle > 0 && angle < 90)
            c = new Coords(-1 * trig(0, angle, dist), trig(1, angle, dist));
        else if (angle > 90 && angle < 180)
            c = new Coords(-1 * trig(1, 90 - angle, dist), -1 * trig(0, 90 - angle, dist));
        else if (angle > -90 && angle < 0)
            c = new Coords(trig(0, angle * -1, dist), trig(1, angle * -1, dist));
        else
            c = new Coords(trig(1, 90 - (angle * -1), dist), -1 * trig(0, 90 - (angle * -1), dist));
        if (nether)
            c.convertNether();
        return c;
    }

    /**
     * Gets value x for function sin(angle) = x/h or cos(angle) = x/h based on input
     * For int value, 0 means sin and 1 means cos, anything else results in IAE
     *
     * @throws IllegalArgumentException If f is not 0 or 1
     */
    private static double trig(int f, double angle, int hyp) {
        double d = Math.toRadians(angle);
        return switch (f) {
            case 0 -> Math.round(Math.abs(Math.sin(d)) * hyp);
            case 1 -> Math.round(Math.abs(Math.cos(d)) * hyp);
            default ->
                    throw new UnsupportedOperationException("Illegal function value, must be 0 or 1");
        };
    }
}
