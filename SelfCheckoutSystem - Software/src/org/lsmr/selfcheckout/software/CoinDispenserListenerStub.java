package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinDispenserListener;

public class CoinDispenserListenerStub implements CoinDispenserListener {

	private boolean disabled = false;
	private boolean coinsFull = false;
	private boolean coinsAdded = false;
	private boolean coinsLoaded = false;
	private boolean coinRemoved = false;
	private boolean noCoins = false;
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
	public void coinsFull(CoinDispenser dispenser) {
		// TODO Auto-generated method stub
		this.coinsFull = true;

	}

	@Override
	public void coinsEmpty(CoinDispenser dispenser) {
		// TODO Auto-generated method stub
		this.noCoins = true;
	

	}

	@Override
	public void coinAdded(CoinDispenser dispenser, Coin coin) {
		// TODO Auto-generated method stub
		this.coinsAdded = true;
		if(dispenser.size() == dispenser.getCapacity())
		{
			this.coinsFull = true;
		}


	}

	@Override
	public void coinRemoved(CoinDispenser dispenser, Coin coin) {

		this.coinRemoved = true;
		if(dispenser.size() == 0)
		{
			this.noCoins = true;
		}

	}

	@Override
	public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub
		if(dispenser.size() > 0)
		{
			this.noCoins = false;
		}
		this.coinsLoaded = true;
		


	}

	@Override
	public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub
		this.coinsLoaded = false;


	}
	
	public boolean getIsFull() {
		return coinsFull;
	}
	public boolean getIsEmpty() {
		return noCoins;
	}
	
	public boolean isLoaded() {
		return coinsLoaded;
	}

}
