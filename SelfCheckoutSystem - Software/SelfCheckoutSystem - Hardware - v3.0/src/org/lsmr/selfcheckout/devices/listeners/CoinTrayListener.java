package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinTray;

/**
 * Listens for events emanating from a coin tray. Coin trays are dumb devices so
 * very few kinds of events can be announced by them.
 */
public interface CoinTrayListener extends AbstractDeviceListener {
	/**
	 * Announces that a coin has been added to the indicated tray.
	 * 
	 * @param tray
	 *            The tray where the event occurred.
	 */
	void coinAdded(CoinTray tray);
}
