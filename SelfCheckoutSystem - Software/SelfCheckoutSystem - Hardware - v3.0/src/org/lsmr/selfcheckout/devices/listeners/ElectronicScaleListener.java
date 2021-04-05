package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.ElectronicScale;

/**
 * Listens for events emanating from an electronic scale.
 */
public interface ElectronicScaleListener extends AbstractDeviceListener {
	/**
	 * Announces that the weight on the indicated scale has changed.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 * @param weightInGrams
	 *            The new weight.
	 */
	void weightChanged(ElectronicScale scale, double weightInGrams);

	/**
	 * Announces that excessive weight has been placed on the indicated scale.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	void overload(ElectronicScale scale);

	/**
	 * Announces that the former excessive weight has been removed from the
	 * indicated scale, and it is again able to measure weight.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	void outOfOverload(ElectronicScale scale);
}
