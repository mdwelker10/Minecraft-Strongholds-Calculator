package model;

import java.io.Serial;

/**
 * Custom Compile time exception that signals that a set of coordinates is not valid.
 * Will be thrown when given coordinates are not appropriate for a particular situation
 * @author Matthew Welker
 *
 */
public class IllegalCoordsException extends Exception
{
	@Serial
	private static final long serialVersionUID = 1L;

	/** 
	 * New exception with specified message
	 * @param msg The message to give the exception
	 */
	public IllegalCoordsException(String msg) {super(msg);}
	/**
	 * Default exception with message "Illegal Coordinates"
	 */
	public IllegalCoordsException() {
		this("Illegal Coordinates");
	}
}
