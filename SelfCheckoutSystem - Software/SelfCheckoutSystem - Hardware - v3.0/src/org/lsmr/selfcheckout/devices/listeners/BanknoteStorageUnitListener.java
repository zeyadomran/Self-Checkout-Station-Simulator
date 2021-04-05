package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;

/**
 * Listens for events emanating from a banknote storage unit.
 */
public interface BanknoteStorageUnitListener extends AbstractDeviceListener {
	/**
	 * Announces that the indicated banknote storage unit is full of banknotes.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknotesFull(BanknoteStorageUnit unit);

	/**
	 * Announces that a banknote has been added to the indicated storage unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknoteAdded(BanknoteStorageUnit unit);

	/**
	 * Announces that the indicated storage unit has been loaded with banknotes.
	 * Used to simulate direct, physical loading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknotesLoaded(BanknoteStorageUnit unit);

	/**
	 * Announces that the storage unit has been emptied of banknotes. Used to
	 * simulate direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknotesUnloaded(BanknoteStorageUnit unit);
}
