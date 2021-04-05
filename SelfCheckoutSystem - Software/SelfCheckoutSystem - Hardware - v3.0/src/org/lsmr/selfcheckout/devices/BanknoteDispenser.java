package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;

/**
 * Represents a device that stores banknotes of a particular denomination to
 * dispense them as change.
 * <p>
 * Banknote dispensers can receive banknotes from other sources. To simplify the
 * simulation, no check is performed on the value of each banknote, meaning it
 * is an external responsibility to ensure the correct routing of banknotes.
 * </p>
 */
public final class BanknoteDispenser extends AbstractDevice<BanknoteDispenserListener>
	implements FromStorageEmitter<Banknote> {
	private int maxCapacity;
	private Queue<Banknote> queue = new LinkedList<Banknote>();
	private UnidirectionalChannel<Banknote> sink;

	/**
	 * Creates a banknote dispenser with the indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of banknotes that can be stored in the
	 *            dispenser. Must be positive.
	 * @throws SimulationException
	 *             If capacity is not positive.
	 */
	public BanknoteDispenser(int capacity) {
		if(capacity <= 0)
			throw new SimulationException(new IllegalArgumentException("Capacity must be positive: " + capacity));
		this.maxCapacity = capacity;
	}

	/**
	 * Accesses the current number of banknotes in the dispenser.
	 * 
	 * @return The number of banknotes currently in the dispenser.
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * Allows a set of banknotes to be loaded into the dispenser directly. Existing
	 * banknotes in the dispenser are not removed. Causes a "banknotesLoaded" event
	 * to be announced.
	 * 
	 * @param banknotes
	 *            A sequence of banknotes to be added. Each cannot be null.
	 * @throws OverloadException
	 *             if the number of banknotes to be loaded exceeds the capacity of
	 *             the dispenser.
	 * @throws SimulationException
	 *             If any banknote is null.
	 */
	public void load(Banknote... banknotes) throws SimulationException, OverloadException {
		if(maxCapacity < queue.size() + banknotes.length)
			throw new OverloadException("Capacity of dispenser is exceeded by load");

		for(Banknote banknote : banknotes)
			if(banknote == null)
				throw new SimulationException(new NullPointerException("A banknote is null."));
			else
				queue.add(banknote);

		notifyLoad(banknotes);
	}

	private void notifyLoad(Banknote[] banknotes) {
		for(BanknoteDispenserListener listener : listeners)
			listener.banknotesLoaded(this, banknotes);
	}

	/**
	 * Unloads banknotes from the dispenser directly. Causes a "banknotesUnloaded"
	 * event to be announced.
	 * 
	 * @return A list of the banknotes unloaded. May be empty. Will never be null.
	 */
	public List<Banknote> unload() {
		List<Banknote> result = new ArrayList<>(queue);
		queue.clear();

		notifyUnload(result.toArray(new Banknote[result.size()]));

		return result;
	}

	private void notifyUnload(Banknote[] banknotes) {
		for(BanknoteDispenserListener listener : listeners)
			listener.banknotesUnloaded(this, banknotes);
	}

	/**
	 * Connects an output channel to this banknote dispenser. Any existing output
	 * channels are disconnected. Causes no events to be announced.
	 * 
	 * @param sink
	 *            The new output device to act as output. Can be null, which leaves
	 *            the channel without an output.
	 */
	public void connect(UnidirectionalChannel<Banknote> sink) {
		this.sink = sink;
	}

	/**
	 * Returns the maximum capacity of this banknote dispenser.
	 * 
	 * @return The capacity. Will be positive.
	 */
	public int getCapacity() {
		return maxCapacity;
	}

	/**
	 * Emits a single banknote from this banknote dispenser. If successful, a
	 * "banknoteRemoved" event is announced to its listeners. If a successful
	 * banknote removal causes the dispenser to become empty, a "banknotesEmpty"
	 * event is instead announced to its listeners.
	 * 
	 * @throws OverloadException
	 *             if the output channel is unable to accept another banknote.
	 * @throws EmptyException
	 *             if no banknotes are present in the dispenser to release.
	 * @throws DisabledException
	 *             if the dispenser is currently disabled.
	 */
	public void emit() throws EmptyException, DisabledException, OverloadException {
		if(isDisabled())
			throw new DisabledException();

		if(queue.size() == 0)
			throw new EmptyException();

		Banknote banknote = queue.remove();

		if(sink.hasSpace())
			try {
				sink.deliver(banknote);
			}
			catch(OverloadException e) {
				// Should never happen
				throw new SimulationException(e);
			}
		else
			throw new OverloadException("The sink is full.");

		if(queue.isEmpty())
			notifyBanknotesEmpty();
		else
			notifyBanknoteRemoved(banknote);
	}

	private void notifyBanknoteRemoved(Banknote banknote) {
		for(BanknoteDispenserListener listener : listeners)
			listener.banknoteRemoved(this, banknote);
	}

	private void notifyBanknotesEmpty() {
		for(BanknoteDispenserListener listener : listeners)
			listener.banknotesEmpty(this);
	}
}
