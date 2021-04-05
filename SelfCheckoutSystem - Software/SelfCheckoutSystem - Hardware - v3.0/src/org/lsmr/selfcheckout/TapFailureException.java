package org.lsmr.selfcheckout;

import java.io.IOException;

/**
 * Represents exceptions arising from failures of taps.
 */
public class TapFailureException extends IOException {
	private static final long serialVersionUID = -8812895797883270979L;

	/**
	 * Create an exception.
	 */
	public TapFailureException() {}
}
