package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;

public class BanknoteSlotListenerStub implements BanknoteSlotListener {

	private boolean disabled = false;
	private boolean inserted = false;
	private boolean ejected = false;
	private boolean removed = false;
	
	
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
	public void banknoteInserted(BanknoteSlot slot) {
		// TODO Auto-generated method stub
		this.inserted = true;
	}

	@Override
	public void banknoteEjected(BanknoteSlot slot) {
		// TODO Auto-generated method stub
		this.ejected = true;
		this.removed = false;
	}

	@Override
	public void banknoteRemoved(BanknoteSlot slot) {
		// TODO Auto-generated method stub
		this.ejected = false;
		this.removed = true;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @return the inserted
	 */
	public boolean isInserted() {
		return inserted;
	}

	/**
	 * @return the ejected
	 */
	public boolean isEjected() {
		return ejected;
	}

	/**
	 * @return the removed
	 */
	public boolean isRemoved() {
		return removed;
	}

}
