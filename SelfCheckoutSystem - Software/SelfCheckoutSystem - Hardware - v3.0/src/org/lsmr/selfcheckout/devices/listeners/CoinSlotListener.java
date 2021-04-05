package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinSlot;

/**
 * Listens for events emanating from a coin slot.
 */
public interface CoinSlotListener extends AbstractDeviceListener {
	/**
	 * An event announcing that a coin has been inserted.
	 * 
	 * @param slot
	 *             The device on which the event occurred.
	 */
	void coinInserted(CoinSlot slot);
}
