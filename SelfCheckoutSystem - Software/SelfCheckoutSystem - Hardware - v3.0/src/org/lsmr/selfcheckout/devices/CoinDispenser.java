package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.listeners.CoinDispenserListener;

/**
 * Represents a device that stores coins of a particular denomination to
 * dispense them as change.
 * <p>
 * Coin dispensers can receive coins from other sources. To simplify the
 * simulation, no check is performed on the value of each coin, meaning it is an
 * external responsibility to ensure the correct routing of coins.
 * </p>
 */
public final class CoinDispenser extends AbstractDevice<CoinDispenserListener>
	implements Acceptor<Coin>, FromStorageEmitter<Coin> {
	private int maxCapacity;
	private Queue<Coin> queue = new LinkedList<Coin>();
	private UnidirectionalChannel<Coin> sink;

	/**
	 * Creates a coin dispenser with the indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of coins that can be stored in the dispenser.
	 *            Must be positive.
	 * @throws SimulationException
	 *             if capacity is not positive.
	 */
	public CoinDispenser(int capacity) {
		if(capacity <= 0)
			throw new SimulationException(new IllegalArgumentException("Capacity must be positive: " + capacity));

		this.maxCapacity = capacity;
	}

	/**
	 * Accesses the current number of coins in the dispenser.
	 * 
	 * @return The number of coins currently in the dispenser.
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * Allows a set of coins to be loaded into the dispenser directly. Existing
	 * coins in the dispenser are not removed. Causes a "coinsLoaded" event to be
	 * announced.
	 * 
	 * @param coins
	 *            A sequence of coins to be added. Each cannot be null.
	 * @throws OverloadException
	 *             if the number of coins to be loaded exceeds the capacity of the
	 *             dispenser.
	 * @throws SimulationException
	 *             If any coin is null.
	 */
	public void load(Coin... coins) throws SimulationException, OverloadException {
		if(maxCapacity < queue.size() + coins.length)
			throw new OverloadException("Capacity of dispenser is exceeded by load");

		for(Coin coin : coins)
			if(coin == null)
				throw new SimulationException(new NullPointerException("A coin is null"));
			else
				queue.add(coin);

		notifyLoad(coins);
	}

	private void notifyLoad(Coin[] coins) {
		for(CoinDispenserListener listener : listeners)
			listener.coinsLoaded(this, coins);
	}

	/**
	 * Unloads coins from the dispenser directly. Causes a "coinsUnloaded" event to
	 * be announced.
	 * 
	 * @return A list of the coins unloaded. May be empty. Will never be null.
	 */
	public List<Coin> unload() {
		List<Coin> result = new ArrayList<>(queue);
		queue.clear();

		notifyUnload(result.toArray(new Coin[result.size()]));

		return result;
	}

	private void notifyUnload(Coin[] coins) {
		for(CoinDispenserListener listener : listeners)
			listener.coinsUnloaded(this, coins);
	}

	/**
	 * Connects an output channel to this coin dispenser. Any existing output
	 * channels are disconnected. Causes no events to be announced.
	 * 
	 * @param sink
	 *            The new output device to act as output. Can be null, which leaves
	 *            the channel without an output.
	 */
	public void connect(UnidirectionalChannel<Coin> sink) {
		this.sink = sink;
	}

	/**
	 * Returns the maximum capacity of this coin dispenser.
	 * 
	 * @return The capacity. Will be positive.
	 */
	public int getCapacity() {
		return maxCapacity;
	}

	/**
	 * Causes the indicated coin to be added into the dispenser. If successful, a
	 * "coinAdded" event is announced to its listeners. If a successful coin
	 * addition causes the dispenser to become full, a "coinsFull" event is
	 * announced to its listeners.
	 * 
	 * @throws DisabledException
	 *             If the coin dispenser is currently disabled.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws OverloadException
	 *             If the coin dispenser is already full.
	 */
	@Override
	public void accept(Coin coin) throws OverloadException, DisabledException {
		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new SimulationException(new NullPointerException("coin is null"));

		if(queue.size() >= maxCapacity)
			throw new OverloadException();

		queue.add(coin);
		notifyCoinAdded(coin);

		if(queue.size() >= maxCapacity)
			notifyCoinsFull();
	}

	/**
	 * Releases a single coin from this coin dispenser. If successful, a
	 * "coinRemoved" event is announced to its listeners. If a successful coin
	 * removal causes the dispenser to become empty, a "coinsEmpty" event is
	 * announced to its listeners.
	 * 
	 * @throws OverloadException
	 *             If the output channel is unable to accept another coin.
	 * @throws EmptyException
	 *             If no coins are present in the dispenser to release.
	 * @throws DisabledException
	 *             If the dispenser is currently disabled.
	 */
	public void emit() throws OverloadException, EmptyException, DisabledException {
		if(isDisabled())
			throw new DisabledException();

		if(queue.size() == 0)
			throw new EmptyException();

		Coin coin = queue.remove();

		notifyCoinRemoved(coin);
		sink.deliver(coin);

		if(queue.isEmpty())
			notifyCoinsEmpty();
	}

	/**
	 * Returns whether this coin dispenser has enough space to accept at least one
	 * more coin. Announces no events.
	 */
	@Override
	public boolean hasSpace() {
		return queue.size() < maxCapacity;
	}

	private void notifyCoinAdded(Coin coin) {
		for(CoinDispenserListener listener : listeners)
			listener.coinAdded(this, coin);
	}

	private void notifyCoinRemoved(Coin coin) {
		for(CoinDispenserListener listener : listeners)
			listener.coinRemoved(this, coin);
	}

	private void notifyCoinsFull() {
		for(CoinDispenserListener listener : listeners)
			listener.coinsFull(this);
	}

	private void notifyCoinsEmpty() {
		for(CoinDispenserListener listener : listeners)
			listener.coinsEmpty(this);
	}
}
