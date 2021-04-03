package org.lsmr.selfcheckout.software;

import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteValidatorListener;

public class BanknoteValidatorListenerStub implements BanknoteValidatorListener {

	private boolean disabled = false;
	private boolean validBanknote = false;
	
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
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		// TODO Auto-generated method stub
		this.validBanknote = true;
		
	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		// TODO Auto-generated method stub
		this.validBanknote = false;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @return the validBanknote
	 */
	public boolean isValidBanknote() {
		return validBanknote;
	}
	
}
