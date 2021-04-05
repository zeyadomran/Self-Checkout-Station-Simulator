package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;

/**
 * Listens for events emanating from a banknote dispenser.
 */
public interface BanknoteDispenserListener extends AbstractDeviceListener {
	/**
	 * Announces that the indicated banknote dispenser is full of banknotes.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	void banknotesFull(BanknoteDispenser dispenser);

	/**
	 * Announces that the indicated banknote dispenser is empty of banknotes.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 */
	void banknotesEmpty(BanknoteDispenser dispenser);

	/**
	 * Announces that the indicated banknote has been added to the indicated banknote dispenser.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param banknote
	 *             The banknote that was added.
	 */
	void banknoteAdded(BanknoteDispenser dispenser, Banknote banknote);

	/**
	 * Announces that the indicated banknote has been added to the indicated banknote dispenser.
	 * 
	 * @param dispenser
	 *             The dispenser where the event occurred.
	 * @param banknote
	 *             The banknote that was removed.
	 */
	void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote);

	/**
	 * Announces that the indicated sequence of banknotes has been added to the
	 * indicated banknote dispenser. Used to simulate direct, physical loading of the dispenser.
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param banknotes
	 *              The banknotes that were loaded.
	 */
	void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes);

	/**
	 * Announces that the indicated sequence of banknotes has been removed to the
	 * indicated banknote dispenser. Used to simulate direct, physical unloading of the dispenser.
	 * 
	 * @param dispenser
	 *              The dispenser where the event occurred.
	 * @param banknotes
	 *              The banknotes that were unloaded.
	 */
	void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes);
}
