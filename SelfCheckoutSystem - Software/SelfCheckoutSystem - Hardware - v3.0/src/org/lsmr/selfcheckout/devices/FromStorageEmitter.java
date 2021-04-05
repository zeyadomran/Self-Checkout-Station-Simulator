package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.Coin;

/**
 * A simple interface for devices that emit things.
 * 
 * @param <T>
 *            The type of the things to emit.
 */
public interface FromStorageEmitter<T> {
	/**
	 * Instructs the device to emit one thing, meaning that the device stores a set
	 * of things and one of them is to be emitted.
	 * 
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws EmptyException
	 *             If the device is empty and cannot emit.
	 * @throws OverloadException
	 *             If the receiving device is already full.
	 */
	public void emit() throws DisabledException, EmptyException, OverloadException;
}
