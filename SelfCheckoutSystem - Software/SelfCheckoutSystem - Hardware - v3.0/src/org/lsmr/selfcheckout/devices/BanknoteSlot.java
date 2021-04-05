package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;

/**
 * Represents a simple banknote slot device that can either accept a banknote or
 * eject the most recently inserted banknote, leaving it dangling until the
 * customer removes it, via {@link #removeDanglingBanknote()}.
 */
public class BanknoteSlot extends AbstractDevice<BanknoteSlotListener>
	implements Acceptor<Banknote>, FlowThroughEmitter<Banknote> {
	private BidirectionalChannel<Banknote> sink;
	private boolean invert;

	/**
	 * Creates a banknote slot.
	 * 
	 * @param invert
	 *            If the slot is to be inverted.
	 */
	public BanknoteSlot(boolean invert) {
		this.invert = invert;
	}

	/**
	 * Connects an output channel to the banknote slot. Causes no events.
	 * 
	 * @param sink
	 *            Where banknotes are passed into the machine.
	 */
	public void connect(BidirectionalChannel<Banknote> sink) {
		this.sink = sink;
	}

	/**
	 * Tells the banknote slot that the indicated banknote is being inserted. If the
	 * sink can accept the banknote, the banknote is passed to the sink and a
	 * "banknoteInserted" event is announced to the slot's listeners; otherwise, a
	 * "banknoteEjected" event is announced to the slot's listeners, meaning that
	 * the banknote is returned to the user.
	 * 
	 * @param banknote
	 *            The banknote to be added. Cannot be null.
	 * @throws DisabledException
	 *             if the banknote slot is currently disabled.
	 * @throws NullPointerException
	 *             If the banknote is null.
	 * @throws OverloadException
	 *             If a banknote is dangling from the slot.
	 */
	public void accept(Banknote banknote) throws DisabledException, OverloadException {
		if(isDisabled())
			throw new DisabledException();

		if(danglingEjectedBanknote != null)
			throw new OverloadException("A banknote is dangling from the slot. Remove that before adding another.");

		notifyBanknoteInserted();

		if(!invert && sink.hasSpace()) {
			try {
				sink.deliver(banknote);
			}
			catch(OverloadException e) {
				// Should never happen
				throw new SimulationException(e);
			}
		}
		else {
			danglingEjectedBanknote = banknote;
			notifyBanknoteEjected();
		}
	}

	private Banknote danglingEjectedBanknote = null;

	/**
	 * Ejects the indicated banknote, leaving it dangling until the customer grabs
	 * it.
	 * 
	 * @param banknote
	 *            The banknote to be ejected.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws SimulationException
	 *             If the argument is null.
	 * @throws SimulationException
	 *             If a banknote is already dangling from the slot.
	 */
	public void emit(Banknote banknote) throws DisabledException, SimulationException {
		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new SimulationException(new NullPointerException("banknote is null"));

		if(danglingEjectedBanknote != null)
			throw new SimulationException(
				"A banknote is already dangling from the slot. Remove that before ejecting another.");

		danglingEjectedBanknote = banknote;

		notifyBanknoteEjected();
	}

	/**
	 * Simulates the user removing a banknote that is dangling from the slot.
	 * 
	 * @return The formerly dangling banknote.
	 */
	public Banknote removeDanglingBanknote() {
		if(danglingEjectedBanknote == null)
			return null;

		Banknote b = danglingEjectedBanknote;
		danglingEjectedBanknote = null;

		notifyBanknoteRemoved();
		return b;
	}

	/**
	 * Tests whether a banknote can be accepted by or ejected from this slot.
	 * 
	 * @return True if the slot is not occupied by a dangling banknote; otherwise,
	 *             false.
	 */
	public boolean hasSpace() {
		return danglingEjectedBanknote == null;
	}

	private void notifyBanknoteInserted() {
		for(BanknoteSlotListener listener : listeners)
			listener.banknoteInserted(this);
	}

	private void notifyBanknoteEjected() {
		for(BanknoteSlotListener listener : listeners)
			listener.banknoteEjected(this);
	}

	private void notifyBanknoteRemoved() {
		for(BanknoteSlotListener listener : listeners)
			listener.banknoteRemoved(this);
	}
}
