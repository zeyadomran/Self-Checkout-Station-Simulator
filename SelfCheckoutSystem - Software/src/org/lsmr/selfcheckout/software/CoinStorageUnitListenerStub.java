package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;

public class CoinStorageUnitListenerStub implements CoinStorageUnitListener{

	private boolean disabled = false;
	private boolean coinsFull = false;
	private boolean coinsAdded = false;
	private boolean coinsLoaded = false;
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
	public void coinsFull(CoinStorageUnit unit) {
		// TODO Auto-generated method stub
		this.coinsFull = true;
	}

	@Override
	public void coinAdded(CoinStorageUnit unit) {
		// TODO Auto-generated method stub
		this.coinsAdded = true;
	}

	@Override
	public void coinsLoaded(CoinStorageUnit unit) {
		// TODO Auto-generated method stub
		this.coinsLoaded = true;
		
	}

	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {
		// TODO Auto-generated method stub
		this.coinsLoaded = false;
	}
	
	public boolean getIsFull() {
		return coinsFull;
	}
	
	public boolean isLoaded() {
		return coinsLoaded;
	}
}

