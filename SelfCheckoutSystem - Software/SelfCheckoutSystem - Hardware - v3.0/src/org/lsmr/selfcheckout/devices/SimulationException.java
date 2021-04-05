package org.lsmr.selfcheckout.devices;

/**
 * An exception that can be raised when the behaviour within the simulator makes
 * no sense, typically when it has not been configured correctly. This is
 * different from an exception being raised because the preconditions of a
 * component are violated. For example, it makes no sense to move a coin from
 * the coin slot to the coin tray if the coin tray does not exist or is not
 * connected to the coin slot; this would cause a SimulationException.
 * <p>
 * Sometimes, the SimulationException wraps another exception, called the nested
 * exception.
 */
@SuppressWarnings("serial")
public class SimulationException extends RuntimeException {
	private Exception nested;

	/**
	 * Constructor used to nest other exceptions.
	 * 
	 * @param nested
	 *            An underlying exception that is to be wrapped.
	 */
	public SimulationException(Exception nested) {
		this.nested = nested;
	}

	/**
	 * Basic constructor.
	 * 
	 * @param message
	 *            An explanatory message of the problem.
	 */
	public SimulationException(String message) {
		nested = new Exception(message);
	}	
}
