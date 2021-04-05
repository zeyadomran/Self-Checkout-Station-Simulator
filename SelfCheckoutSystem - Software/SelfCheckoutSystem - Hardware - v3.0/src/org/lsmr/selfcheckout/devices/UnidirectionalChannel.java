package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.Coin;

/**
 * Represents a simple device (like, say, a tube) that allows things to move in
 * one direction between other devices.
 * 
 * @param <T>
 *            The type of the things to be transported.
 */
public class UnidirectionalChannel<T> {
	private Acceptor<T> sink;

	/**
	 * Constructs a new coin channel whose output is connected to the indicated
	 * sink.
	 * 
	 * @param sink
	 *            The device at the output end of the channel.
	 */
	public UnidirectionalChannel(Acceptor<T> sink) {
		this.sink = sink;
	}

	/**
	 * Moves the indicated thing to the sink. This method should be called by the
	 * source device, and not by an external application.
	 * 
	 * @param thing
	 *            The thing to transport via the channel.
	 * @throws OverloadException
	 *             If the sink has no space for the thing.
	 * @throws DisabledException
	 *             If the sink is currently disabled.
	 */
	public void deliver(T thing) throws OverloadException, DisabledException {
		sink.accept(thing);
	}

	/**
	 * Returns whether the sink has space for at least one more thing.
	 * 
	 * @return true if the channel can accept a thing; false otherwise.
	 */
	public boolean hasSpace() {
		return sink.hasSpace();
	}
}
