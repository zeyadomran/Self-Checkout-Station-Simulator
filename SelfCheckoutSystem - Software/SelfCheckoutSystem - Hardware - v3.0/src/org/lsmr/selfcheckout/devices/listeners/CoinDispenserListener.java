package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;

/**
 * Listens for events emanating from a coin dispenser.
 */
public interface CoinDispenserListener extends AbstractDeviceListener {
	/**
	 * Announces that the indicated coin dispenser is full of coins.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	void coinsFull(CoinDispenser dispenser);

	/**
	 * Announces that the indicated coin dispenser is empty of coins.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	void coinsEmpty(CoinDispenser dispenser);

	/**
	 * Announces that the indicated coin has been added to the indicated coin dispenser.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param coin
	 *             The coin that was added.
	 */
	void coinAdded(CoinDispenser dispenser, Coin coin);

	/**
	 * Announces that the indicated coin has been added to the indicated coin dispenser.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param coin
	 *             The coin that was removed.
	 */
	void coinRemoved(CoinDispenser dispenser, Coin coin);

	/**
	 * Announces that the indicated sequence of coins has been added to the
	 * indicated coin dispenser. Used to simulate direct, physical loading of the dispenser.
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param coins
	 *              The coins that were loaded.
	 */
	void coinsLoaded(CoinDispenser dispenser, Coin... coins);

	/**
	 * Announces that the indicated sequence of coins has been removed to the
	 * indicated coin dispenser. Used to simulate direct, physical unloading of the dispenser.
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param coins
	 *              The coins that were unloaded.
	 */
	void coinsUnloaded(CoinDispenser dispenser, Coin... coins);
}
