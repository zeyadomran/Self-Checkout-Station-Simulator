package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinTrayListener;

public class CoinTrayListenerStub implements CoinTrayListener {

	private boolean disabled = false;
	private boolean coinAdded = false;
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
	public void coinAdded(CoinTray tray) {
		// TODO Auto-generated method stub
		this.coinAdded = true;
		
	}

}
