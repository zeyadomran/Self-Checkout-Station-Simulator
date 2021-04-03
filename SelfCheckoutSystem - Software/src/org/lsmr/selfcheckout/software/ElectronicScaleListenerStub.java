package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;

public class ElectronicScaleListenerStub implements ElectronicScaleListener {
	
	private boolean disabled = false;
	private boolean overload = false;
	private double currentWeightInGrams = 0.0;
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) { this.disabled = false; }

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) { this.disabled = true; }

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) { this.currentWeightInGrams = weightInGrams; }

	@Override
	public void overload(ElectronicScale scale) { this.overload = true; }

	@Override
	public void outOfOverload(ElectronicScale scale) { this.overload = false; }
	
	/**
	 * Get the status of the device.
	 * @return The status of the device.
	 */
	public boolean getIsDisabled() { return this.disabled; }
	
	/**
	 * Gets whether the scale is overloaded.
	 * @return Whether the scale is overloaded.
	 */
	public boolean getIsOverload() { return this.overload; }
	
	/**
	 * Gets the current weight on the scale.
	 * @return The current weight in grams.
	 */
	public double getCurrentWeight() { return this.currentWeightInGrams; }
}
