package org.lsmr.selfcheckout;

import java.io.IOException;

/**
 * Represents exceptions arising from a blocked card.
 */
public class BlockedCardException extends IOException {
	private static final long serialVersionUID = 8824192400137175094L;

	/**
	 * Create an exception.
	 */
	public BlockedCardException() {}
}
