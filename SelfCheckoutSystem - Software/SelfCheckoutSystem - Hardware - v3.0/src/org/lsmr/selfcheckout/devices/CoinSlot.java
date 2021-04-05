package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.listeners.CoinSlotListener;

/**
 * Represents a simple coin slot device that has one output channel. The slot is
 * stupid: it has no functionality other than being enabled/disabled, and cannot
 * determine the value and currency of the coin.
 */
public final class CoinSlot extends AbstractDevice<CoinSlotListener> implements Acceptor<Coin> {
	private UnidirectionalChannel<Coin> sink;

	/**
	 * Creates a coin slot.
	 */
	public CoinSlot() {}

	/**
	 * Connects channels to the coin slot. Causes no events.
	 * 
	 * @param sink
	 *            Where coins will always be passed.
	 */
	public void connect(UnidirectionalChannel<Coin> sink) {
		this.sink = sink;
	}

	/**
	 * Tells the coin slot that the indicated coin is being inserted. If the slot is
	 * enabled, this causes a "coinInserted" event to be announced to its listeners.
	 * 
	 * @param coin
	 *            The coin to be added. Cannot be null.
	 * @throws DisabledException
	 *             If the coin slot is currently disabled.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws NullPointerException
	 *             If the coin is null.
	 */
	public void accept(Coin coin) throws DisabledException {
		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new SimulationException(new NullPointerException("coin is null"));

		notifyCoinInserted();

		if(sink.hasSpace()) {
			try {
				sink.deliver(coin);
			}
			catch(OverloadException e) {
				// Should never happen
				throw new SimulationException(e);
			}
		}
		else
			throw new SimulationException("Unable to route coin: Output channel is full");
	}

	@Override
	public boolean hasSpace() {
		return sink.hasSpace();
	}

	private void notifyCoinInserted() {
		for(CoinSlotListener listener : listeners)
			listener.coinInserted(this);
	}
}
