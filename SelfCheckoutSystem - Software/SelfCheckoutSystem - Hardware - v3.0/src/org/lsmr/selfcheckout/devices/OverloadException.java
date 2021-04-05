package org.lsmr.selfcheckout.devices;

/**
 * Represents situations where a device has been overloaded, in terms of weight,
 * quantity of items, etc.
 */
public class OverloadException extends Exception {
	private static final long serialVersionUID = 7813659161520664284L;

	/**
	 * Create an exception without an error message.
	 */
	public OverloadException() {}

	/**
	 * Create an exception with an error message.
	 * 
	 * @param message
	 *            The error message to use.
	 */
	public OverloadException(String message) {
		super(message);
	}
}
