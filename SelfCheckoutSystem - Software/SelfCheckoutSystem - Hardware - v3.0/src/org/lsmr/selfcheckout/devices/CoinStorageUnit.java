package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;

/**
 * Represents devices that store coins. They only receive coins, not dispense
 * them. To access the coins inside, a human operator needs to physically remove
 * the coins, simulated with the {@link #unload()} method. A
 * {@link #load(Coin...)} method is provided for symmetry.
 */
public class CoinStorageUnit extends AbstractDevice<CoinStorageUnitListener> implements Acceptor<Coin> {
	private Coin[] storage;
	private int nextIndex = 0;

	/**
	 * Creates a coin storage unit that can hold the indicated number of coins.
	 * 
	 * @param capacity
	 *            The maximum number of coins that the unit can hold.
	 * @throws SimulationException
	 *             If the capacity is not positive.
	 */
	public CoinStorageUnit(int capacity) {
		if(capacity <= 0)
			throw new SimulationException(new IllegalArgumentException("The capacity must be positive."));

		storage = new Coin[capacity];
	}

	/**
	 * Gets the maximum number of coins that this storage unit can hold.
	 * 
	 * @return The capacity.
	 */
	public int getCapacity() {
		return storage.length;
	}

	/**
	 * Gets the current count of coins contained in this storage unit.
	 * 
	 * @return The current count.
	 */
	public int getCoinCount() {
		return nextIndex;
	}

	/**
	 * Allows a set of coins to be loaded into the storage unit directly. Existing
	 * coins in the dispenser are not removed. Causes a "coinsLoaded" event to be
	 * announced. Disabling has no effect on loading/unloading.
	 * 
	 * @param coins
	 *            A sequence of coins to be added. Each cannot be null.
	 * @throws SimulationException
	 *             if the number of coins to be loaded exceeds the capacity of the
	 *             unit.
	 * @throws SimulationException
	 *             If coins is null.
	 * @throws SimulationException
	 *             If any coin is null.
	 * @throws OverloadException
	 *             If too many coins are loaded.
	 */
	public void load(Coin... coins) throws SimulationException, OverloadException {
		if(coins == null)
			throw new SimulationException(new NullPointerException("coins is null"));

		if(coins.length + nextIndex > storage.length)
			throw new OverloadException("You tried to stuff too many coins in the storage unit.");

		for(Coin coin : coins)
			if(coin == null)
				throw new SimulationException(new NullPointerException("No coin may be null"));

		System.arraycopy(coins, 0, storage, nextIndex, coins.length);
		nextIndex += coins.length;

		notifyCoinsLoaded();
	}

	/**
	 * Unloads coins from the storage unit directly. Causes a "coinsUnloaded" event
	 * to be announced.
	 * 
	 * @return A list of the coins unloaded. May be empty. Will never be null.
	 */
	public List<Coin> unload() {
		List<Coin> coins = Arrays.asList(storage);

		storage = new Coin[storage.length];
		nextIndex = 0;
		notifyCoinsUnloaded();

		return coins;
	}

	/**
	 * Causes the indicated coin to be added to the storage unit. If successful, a
	 * "coinAdded" event is announced to its listeners. If a successful coin
	 * addition instead causes the unit to become full, a "coinsFull" event is
	 * announced to its listeners.
	 * 
	 * @throws DisabledException
	 *             If the unit is currently disabled.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws OverloadException
	 *             If the unit is already full.
	 */
	public void accept(Coin coin) throws DisabledException, OverloadException {
		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new SimulationException(new NullPointerException("coin is null"));

		if(nextIndex < storage.length) {
			storage[nextIndex++] = coin;

			if(nextIndex == storage.length)
				notifyCoinsFull();
			else
				notifyCoinAdded();
		}
		else
			throw new OverloadException();
	}

	@Override
	public boolean hasSpace() {
		return nextIndex < storage.length;
	}

	private void notifyCoinsLoaded() {
		for(CoinStorageUnitListener l : listeners)
			l.coinsLoaded(this);
	}

	private void notifyCoinsUnloaded() {
		for(CoinStorageUnitListener l : listeners)
			l.coinsUnloaded(this);
	}

	private void notifyCoinsFull() {
		for(CoinStorageUnitListener l : listeners)
			l.coinsFull(this);
	}

	private void notifyCoinAdded() {
		for(CoinStorageUnitListener l : listeners)
			l.coinAdded(this);
	}
}
