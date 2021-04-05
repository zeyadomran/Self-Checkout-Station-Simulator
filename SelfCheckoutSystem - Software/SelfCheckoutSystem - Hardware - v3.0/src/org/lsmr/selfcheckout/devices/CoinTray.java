package org.lsmr.selfcheckout.devices;

import java.util.Arrays;
import java.util.List;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.listeners.CoinTrayListener;

/**
 * Simulates the tray where dispensed coins go for the user to collect them.
 */
public class CoinTray extends AbstractDevice<CoinTrayListener> implements Acceptor<Coin> {
	private Coin[] coins;
	private int nextIndex = 0;

	/**
	 * Creates a coin tray.
	 * 
	 * @param capacity
	 *            The maximum number of coins that this tray can hold without
	 *            overflowing.
	 * @throws SimulationException
	 *             If the capacity is &le;0.
	 */
	public CoinTray(int capacity) {
		if(capacity <= 0)
			throw new SimulationException(new IllegalArgumentException("capacity must be positive."));

		coins = new Coin[capacity];
	}

	/**
	 * Causes the indicated coin to be added to the tray. A "coinAdded" event is
	 * announced to listeners.
	 * 
	 * @param coin
	 *            The coin to add.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws OverloadException
	 *             If the tray overflows.
	 */
	public void accept(Coin coin) throws OverloadException, DisabledException {
		if(coin == null)
			throw new SimulationException(new NullPointerException("coin is null"));

		if(nextIndex < coins.length) {
			coins[nextIndex++] = coin;
			notifyCoinAdded();
		}
		else
			throw new OverloadException("The tray has overflowed.");
	}

	public List<Coin> collectCoins() {
		List<Coin> result = Arrays.asList(coins);

		coins = new Coin[coins.length];
		nextIndex = 0;

		return result;
	}

	/**
	 * Returns whether this coin receptacle has enough space to accept at least one
	 * more coin: always true. Causes no events.
	 */
	@Override
	public boolean hasSpace() {
		return nextIndex < coins.length;
	}

	private void notifyCoinAdded() {
		for(CoinTrayListener l : listeners)
			l.coinAdded(this);
	}
}
