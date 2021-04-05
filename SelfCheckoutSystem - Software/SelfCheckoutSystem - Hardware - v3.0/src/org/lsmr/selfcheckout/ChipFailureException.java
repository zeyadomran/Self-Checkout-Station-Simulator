package org.lsmr.selfcheckout;

import java.io.IOException;

/**
 * Represents exceptions arising from failures of the chip.
 */
public class ChipFailureException extends IOException {
	private static final long serialVersionUID = 3518203688837080092L;

	/**
	 * Create an exception.
	 */
	public ChipFailureException() {}
}
