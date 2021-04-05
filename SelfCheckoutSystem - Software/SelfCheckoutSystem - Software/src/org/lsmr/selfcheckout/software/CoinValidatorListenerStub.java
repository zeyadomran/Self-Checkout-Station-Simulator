package org.lsmr.selfcheckout.software;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinValidatorListener;

public class CoinValidatorListenerStub implements CoinValidatorListener {

	private boolean disabled = false;
	private boolean validCoinDetected = false;
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
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		// TODO Auto-generated method stub
		this.validCoinDetected = true;
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		// TODO Auto-generated method stub
		this.validCoinDetected = false;
	}
	
	public boolean getIsValid() { return validCoinDetected; }

}
