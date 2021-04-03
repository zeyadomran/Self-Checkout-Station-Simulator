package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteStorageUnitListener;

public class BanknoteStorageUnitListenerStub implements BanknoteStorageUnitListener{
	
	private boolean disabled = false;
	
	private boolean full = false;
	private boolean added = false;
	private boolean loaded = false;

	/**
	 * Announces that the indicated banknote storage unit is full of banknotes.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknotesFull(BanknoteStorageUnit unit) {this.full = true;}

	/**
	 * Announces that a banknote has been added to the indicated storage unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknoteAdded(BanknoteStorageUnit unit) {this.added = true;}

	/**
	 * Announces that the indicated storage unit has been loaded with banknotes.
	 * Used to simulate direct, physical loading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknotesLoaded(BanknoteStorageUnit unit) {this.loaded = true;}

	/**
	 * Announces that the storage unit has been emptied of banknotes. Used to
	 * simulate direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit){this.loaded = false;}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) 
	{this.disabled = false;}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) 
	{this.disabled = true;}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @return the full
	 */
	public boolean isFull() {
		return full;
	}

	/**
	 * @return the added
	 */
	public boolean isAdded() {
		return added;
	}

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

}
