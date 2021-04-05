package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.devices.AbstractDevice;

/**
 * This class represents the abstract interface for all device listeners. All
 * subclasses should add their own event notification methods, the first
 * parameter of which should always be the device affected.
 */
public interface AbstractDeviceListener {
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device);

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device);
}
