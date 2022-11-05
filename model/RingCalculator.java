package model;


import java.util.LinkedList;

/**
 *This class does all calculations that actually make the program function using various methods to process
 *the coordinates the user gives and provide a list of coordinates that the other strongholds should be close to 
 *using Trigonometry. The coordinates should be meaured by standing in the center of the starter staircase,
 *more info on how to properly get the coordinates in the README.
 *
 * @author Matthew Welker
 */
public class RingCalculator
{
	/** x coordinate of the stronghold that has been found*/
	private double x;
	/** z coordinate of the stronghold that has been found*/
	private double z;
	/** what ring the stronghold is in using calculation or manual input*/
	private int ring;
	/**The mininum distance that a stronghold can form from the origin in the ring of the given stronghold*/
	private int min;
	/**The maxinum distance that a stronghold can form from the origin in the ring of the given stronghold*/
	private int max;
	/** The number of strongholds in the given ring*/
	private int numStrongholds;
	//Values for number of strongholds in the given ring
	public static final int NUM1 = 3;
	public static final int NUM2 = 6;
	public static final int NUM3 = 10;
	public static final int NUM4 = 15;
	public static final int NUM5 = 21;
	public static final int NUM6 = 28;
	public static final int NUM7 = 36;
	public static final int NUM8 = 9;
	//Values for where the stronghold should generate as distance from the origin.
	public static final int MIN1 = 1280;
	public static final int MAX1 = 2816;
	public static final int MIN2 = 4352;
	public static final int MAX2 = 5888;
	public static final int MIN3 = 7424;
	public static final int MAX3 = 8960;
	public static final int MIN4 = 10496;
	public static final int MAX4 = 12032;
	public static final int MIN5 = 13568;
	public static final int MAX5 = 15104;
	public static final int MIN6 = 16640;
	public static final int MAX6 = 18176;
	public static final int MIN7 = 19712;
	public static final int MAX7 = 21248;
	public static final int MIN8 = 22784;
	public static final int MAX8 = 24320;
	/**Array of minimum distances for all Stronghold rings*/
	public static final int[] MIN = new int[] 
			{MIN1, MIN2, MIN3, MIN4, MIN5, MIN6, MIN7, MIN8};
	/**Array of maximum distances for all Stronghold rings*/
	public static final int[] MAX = new int[]
			{MAX1, MAX2, MAX3, MAX4, MAX5, MAX6, MAX7, MAX8};
	
	/**
	 * Initializes necessary values using given coords
	 * Adds 4 to both coordinates in order to do calculations from the chunk center, which is the point that
	 * the Stronghold actually generates from and where a thrown Eye of Ender will point.
	 * @param x The given x coordinate
	 * @param z The given z coordinate
	 * @param ignore Whether to catch the ringCoords exception or not, will only be used if the user will be
	 * manually entering the ring after an IllegalCoordsException was already thrown
	 * @throws IllegalCoordsException If ringCoords throws and IllegalCoordsException
	 * {@link #ringCoords(double, double)}
	 * */
	public RingCalculator(double x, double z, boolean ignore) throws IllegalCoordsException {
		try {
			ringCoords(x, z);
		}
		catch (IllegalCoordsException e) {
			if (!ignore) throw new IllegalCoordsException();
		}
		this.x = x + 4;
		this.z = z + 4;
	}
	/**Gets the given x coordinate*/
	public double getX() {
		return x;
	}
	/**Gets the given z coordinate*/
	public double getZ() {
		return z;
	}
	/**Gets the number of strongholds in the ring*/
	public int getNumStrongholds() {
		return numStrongholds;
	}
	/**Gets the ring that the stronghold is in*/
	public int getRing() {
		return ring;
	}
	/**
	 * Sets the ring the Stronghold is in
	 * @param r The ring to set
	 * @return The average distance of a Stronghold in this ring
	 * @throws IllegalArgumentException If the ring given is not a valid ring (1 to 8)
	 */
	public int setRing(int r) {
		if (r < 1 || r > 8) {
			throw new IllegalArgumentException("Invalid ring number, must be 1-8.");
		}
		ring = r;
		setNumStrongholds(ring);
		min = MIN[ring-1];
		max = MAX[ring-1];
		return (max + min) / 2;
	}
	/**
	 * Guesses what ring a set of coordinates are in.
	 * This method assumes that the stronghold snaps outside the ring meaning that the ring could not be
	 * set by the ringCoords() method.
	 * The guess is made by calculating the distance from the origin and finding which ring has the closest
	 * minimum or maximum Stronghold spawn distance.
	 * @param x The x coordinate of the supposed Stronghold
	 * @param z The z coordinate of the supposed Stronghold
	 * @return The average distance of a Stronghold in the calculated ring
	 */
	public int guessRing(double x, double z) {
		double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
		if (dist < MIN[0]) 
			return setRing(1);
		for (int i = 0; i < MIN.length - 1; i++) {
			if (dist > MAX[i] && dist < MIN[i+1]) {
				if (dist-MAX[i] > MIN[i+1]-dist) {
					return setRing(i+2);
				}
				else {
					return setRing(i+1);
				}
			}
		}
		return setRing(8);
	}
	/**
	 * Will check the given coordinates using the Pythagorean theorem
	 * If they are valid then they are set in the constructor.
	 * The ring field is set here (what ring the stronghold is in).
	 * @param x The x coordinate of the given coordinates
	 * @param z The z coordinate of the given coordinates
	 * @throws IllegalCoordsException If the coordinates are not within the bounds of a Stronghold ring
	 * @return true if the ring was set and the coords are legitimate Stronghold coords
	 */
	private boolean ringCoords(double x, double z) throws IllegalCoordsException {
		double dist = Math.pow(x, 2) + Math.pow(z, 2);
		dist = Math.sqrt(dist);
		for (int i = 0; i < MIN.length; i++)
		{
			if (dist >= MIN[i] && dist <= MAX[i])
			{
				setRing(i+1);
				return true;
			}
		}
		throw new IllegalCoordsException("Invalid Stronghold Coordinates");
	}
	/**
	 * Sets the amount of strongholds in the ring
	 * @throws IllegalArgumentException If ring is not an integer 1 through 8
	 */
	private void setNumStrongholds(int ring) {
		try {
			switch (ring) {
			case 1:
				numStrongholds = NUM1;
				break;
			case 2:
				numStrongholds = NUM2;
				break;
			case 3:
				numStrongholds = NUM3;
				break;
			case 4:
				numStrongholds = NUM4;
				break;
			case 5:
				numStrongholds = NUM5;
				break;
			case 6:
				numStrongholds = NUM6;
				break;
			case 7:
				numStrongholds = NUM7;
				break;
			case 8:
				numStrongholds = NUM8;
				break;
			default:
				throw new Exception();
			}
		} 
		catch (Exception e) {
			throw new IllegalArgumentException("Invalid ring number");
		}
	}
	/**
	 * Method that actually does the calculation for stronghold locations based on the given x and z values
	 * along with the calculated values for the ring, angle, and number of strongholds in the ring
	 * @param nether Whether to have the coordinates as nether coordinates or not
	 * @return A LinkedList of ideal nether travel coordinates or overworld coordinates
	 */
	public LinkedList<Coords> calcStrongholds(boolean nether) {
		double angleBetween = 360.0 / numStrongholds; //The angle between strongholds
		int dist = (max + min) / 2; //Mean distance in the ring for the Stronghold
		double angle = getAngle();
		double temp = angle; //temp value to use for calculations
		LinkedList<Coords> coords = new LinkedList<Coords>();
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
			temp-= angleBetween;
		}
		return coords;
	}
	//Returns the angle from the origin this stronghold is in
	//uses the x and z values
	private double getAngle() {
		double angle = -180.0; //to be returned, is default for when x=0 and z<0
		//Formula based on soh-cah-toa for MC coord system
		if (z > 0) {
			angle = Math.toDegrees(Math.atan(x/z));
		}
		else {
			if (x != 0) {
				angle = Math.abs(Math.toDegrees(Math.atan(z/x)));
				if (angle > 0) 
					angle += 90;
				else
					angle -= 90;
			}
		}
		//pos x means negative angle
		if (x > 0) {
			angle *= -1;
		}
		return round(angle);
	}
	//Gets a set of coordinates (overworld) based on angle and distance
	//throws IAE if angle is not valid (between -180 and 180)
	private Coords getCoords(double angle, int dist, boolean nether) {
		if (-180 > angle || 180 < angle) throw new IllegalArgumentException("Invalid angle");
		Coords c;
		//Cases when one is 0
		if (angle == 0) c = new Coords(0, dist);
		else if (angle == 90) c = new Coords(-1 * dist, 0);
		else if (angle == -90) c = new Coords(dist, 0);
		else if (angle == -180) c = new Coords(0, -1 * dist);
		//all other cases
		else if (angle > 0 && angle < 90) c = new Coords(-1 * trig(0, angle, dist), trig(1, angle, dist));
		else if (angle > 90 && angle < 180) c = new Coords(-1 * trig(1, 90 - angle, dist),-1 * trig(0, 90 - angle, dist));
		else if (angle > -90 && angle < 0) c = new Coords(trig(0, angle * -1, dist), trig(1, angle * -1, dist));
		else c = new Coords(trig(1, 90 - (angle * -1), dist), -1 * trig(0, 90 - (angle * -1), dist));
		if (nether) 
			c.convertNether();
		return c;
	}
	/**
	 * Rounds the given value, ideally to two decimal places
	 * @param value The value to round
	 * @return The rounded value
	 */
	private static double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
	/**
	 * Gets value x for function sin(angle) = x/h or cos(angle) = x/h based on input
	 * For int value, 0 means sin and 1 means cos, anything else results in IAE
	 * @throws IllegalArgumentException If f is not 0 or 1
	 */
	private static double trig(int f, double angle, int hyp) {
		double d = Math.toRadians(angle);
		return switch (f) {
			case 0 -> round(Math.abs(Math.sin(d)) * hyp);
			case 1 -> round(Math.abs(Math.cos(d)) * hyp);
			default -> throw new UnsupportedOperationException("Illegal function value, must be 0 or 1");
		};
	}
	/**
	 * Returns 2D string array of Coords given a LinkedList of coords to be used by GUI
	 * Does this by creating arrays based on Quadrant and then converting them to a 2D array
	 * The array will have null values unless all quadrants have the same number of strongholds
	 * @return A 2D string array of Coords in the given list
	 */ 
	public String[][] CoordsToArray(LinkedList<Coords> list) {
		String[] pp = new String[list.size()]; int ppCount = 0;
		String[] pn = new String[list.size()]; int pnCount = 0;
		String[] np = new String[list.size()]; int npCount = 0;
		String[] nn = new String[list.size()]; int nnCount = 0;
		for (Coords c : list) {
			switch (c.getQuadrant()) {
				case POSPOS -> pp[ppCount++] = c.toString();
				case POSNEG -> pn[pnCount++] = c.toString();
				case NEGPOS -> np[npCount++] = c.toString();
				case NEGNEG -> nn[nnCount++] = c.toString();
				default -> throw new NullPointerException("Coords with no Quadrant cannot be added");
			}
		}
		int largest = Math.max(Math.max(ppCount, pnCount), Math.max(npCount, nnCount));
		String[][] ret = new String[largest][4];
		for (int i = 0; i < ret.length; i++) {
			ret[i][0] = pp[i];
			ret[i][1] = pn[i];
			ret[i][2] = np[i];
			ret[i][3] = nn[i];
		}
		return ret;
	}
}
