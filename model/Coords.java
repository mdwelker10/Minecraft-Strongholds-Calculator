package model;
/**
 * An object representing a pair of coordinates in Minecraft, excluding the y component since it is
 * not relevant to the program. The z coordinate represents south (positive) or north (negative), while
 * the x coordinate represents east (positive) or west (negative). These coordinates are two dimensional 
 * as the excluded y coordinate represents height.
 * 
 * The convertNether() method allows these coordinates to be converted to nether coordinates, where one 
 * nether coordinate unit (blocks) is equals to 8 in the overworld.
 * @author Matthew Welker
 */
public class Coords
{
	/**The x coordinate*/
	private double x;
	/**The z coordinate*/
	private double z;
	/**The quadrant of the coordinates*/
	private Quadrant q;
	/**
	 * Enum to represent what quadrant the coords are in.
	 * If a coord is 0 it is assumed to be positive
	 */
	public enum Quadrant {POSPOS, POSNEG, NEGPOS, NEGNEG};
	/**
	 * Creates new coordinates with the given values
	 * @param x The x coordinate
	 * @param z The z coordinate
	 */
	public Coords(double x, double z) {
		this.x = x;
		this.z = z;
		setQuadrant();
	}
	/**Creates new coordinates with the default values of x=0 and z=0*/
	public Coords() {
		x = 0;
		z = 0;
	}
	/**Gets the x coordinate*/
	public double getX() {
		return x;
	}
	/**Gets the z coordinate*/
	public double getZ() {
		return z;
	}
	/**Gets the quadrant the coordinate is in*/
	public Quadrant getQuadrant() {
		return q;
	}
	/**Represents the coordinates in the form (x, z)*/
	@Override
	public String toString() {
		return "(" + (int) x + ", " + (int) z + ")";
	}
	/**Converts the coordinates to nether coordinates*/
	public void convertNether() {
		x /= 8;
		z /= 8;
	}
	/**Converts the coordinates to overworld coordinates*/
	public void convertOverworld() {
		x *= 8;
		z *= 8;
	}
	/**
	 * Sets the Quadrant that the coordinates are in
	 */
	private void setQuadrant() {
		if (x < 0 && z < 0) q = Quadrant.NEGNEG;
		else if (x >= 0 && z >= 0) q = Quadrant.POSPOS;
		else if (x >= 0) q = Quadrant.POSNEG;
		else q = Quadrant.NEGPOS;
	}
}
