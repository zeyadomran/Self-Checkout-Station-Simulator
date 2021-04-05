package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;

import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;

/**
 * The abstract base class for all devices involved in the simulator.
 * <p>
 * This class utilizes the Observer design pattern. Subclasses inherit the
 * register method, but each must define its own notifyXXX methods. The
 * notifyListener method is provided to minimize the work of subclasses.
 * </p>
 * <p>
 * Each device must possess an appropriate listener, which extends
 * AbstractDeviceListener; the type parameter T represents this listener.
 * <p>
 * <p>
 * Any individual device can be disabled, which means it will not permit
 * physical movements to be caused by the software. Any method that could cause
 * a physical movement will declare that it throws DisabledException.
 * </p>
 * 
 * @param <T>
 *            The class of listeners used for this device. For a device whose
 *            class is X, its corresponding listener would typically be
 *            XListener.
 */
public abstract class AbstractDevice<T extends AbstractDeviceListener> {
	/**
	 * A list of the registered listeners on this device.
	 */
	protected ArrayList<T> listeners = new ArrayList<>();

	/**
	 * Locates the indicated listener and removes it such that it will no longer be
	 * informed of events from this device. If the listener is not currently
	 * registered with this device, calls to this method will return false, but
	 * otherwise have no effect.
	 * 
	 * @param listener
	 *            The listener to remove.
	 * @return true if the listener was found and removed, false otherwise.
	 */
	public final boolean deregister(T listener) {
		return listeners.remove(listener);
	}

	/**
	 * All listeners registered with this device are removed. If there are none,
	 * calls to this method have no effect.
	 */
	public final void deregisterAll() {
		listeners.clear();
	}

	/**
	 * Registers the indicated listener to receive event notifications from this
	 * device.
	 * 
	 * @param listener
	 *            The listener to be added.
	 */
	public final void register(T listener) {
		listeners.add(listener);
	}

	private boolean disabled = false;

	/**
	 * Disables this device from receiving input and producing output.
	 */
	public final void disable() {
		disabled = true;
		notifyDisabled();
	}

	private void notifyDisabled() {
		for(T listener : listeners)
			listener.disabled(this);
	}

	/**
	 * Enables this device for receiving input and producing output.
	 */
	public final void enable() {
		disabled = false;
		notifyEnabled();
	}

	private void notifyEnabled() {
		for(T listener : listeners)
			listener.enabled(this);
	}

	/**
	 * Returns whether this device is currently disabled from receiving input and
	 * producing output.
	 * 
	 * @return true if the device is disabled; false if the device is enabled.
	 */
	public final boolean isDisabled() {
		return disabled;
	}
}
