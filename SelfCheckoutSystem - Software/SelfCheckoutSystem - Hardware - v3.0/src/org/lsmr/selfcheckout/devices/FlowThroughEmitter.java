package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.Coin;

/**
 * A simple interface for devices that emit things.
 * 
 * @param <T>
 *            The type of the things to emit.
 */
public interface FlowThroughEmitter<T> {
	/**
	 * Instructs the device to emit a specific thing, meaning that the device is
	 * being handed this thing to pass onwards.
	 * 
	 * @param thing
	 *            The thing to emit.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws OverloadException
	 *             If the receiving device is already full.
	 */
	public void emit(T thing) throws DisabledException, OverloadException;
}
