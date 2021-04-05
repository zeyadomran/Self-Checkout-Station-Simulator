package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;

public class BanknoteDispenserListenerStub implements BanknoteDispenserListener  {
	
	private boolean disabled = false;
	private boolean banknoteFull = false;
	private boolean banknoteAdded = false;
	private boolean banknoteLoaded = false;
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		this.disabled = false;
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		this.disabled = true;
	}

	@Override
	public void banknotesFull(BanknoteDispenser dispenser) {
		// TODO Auto-generated method stub
		this.banknoteFull = true;
	}

	@Override
	public void banknotesEmpty(BanknoteDispenser dispenser) {
		// TODO Auto-generated method stub
		this.banknoteFull = false;
	}

	@Override
	public void banknoteAdded(BanknoteDispenser dispenser, Banknote banknote) {
		// TODO Auto-generated method stub
		this.banknoteAdded = true;
	}

	@Override
	public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
		// TODO Auto-generated method stub
		this.banknoteAdded = false;
	}

	@Override
	public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		// TODO Auto-generated method stub
		this.banknoteLoaded = true;
	}

	@Override
	public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		// TODO Auto-generated method stub
		this.banknoteLoaded = false;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @return the banknoteFull
	 */
	public boolean isBanknoteFull() {
		return banknoteFull;
	}

	/**
	 * @return the banknoteAdded
	 */
	public boolean isBanknoteAdded() {
		return banknoteAdded;
	}

	/**
	 * @return the banknoteLoaded
	 */
	public boolean isBanknoteLoaded() {
		return banknoteLoaded;
	}

}
