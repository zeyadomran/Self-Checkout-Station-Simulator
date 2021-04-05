package org.lsmr.selfcheckout;

import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.Product;

/**
 * Abstract base class of items for sale, each with a particular weight.
 */
public abstract class Item {
	private double weightInGrams;

	/**
	 * Constructs an item with the indicated weight.
	 * 
	 * @param weightInGrams
	 *            The weight of the item.
	 * @throws SimulationException
	 *             If the weight is &le;0.
	 */
	protected Item(double weightInGrams) {
		if(weightInGrams <= 0.0)
			throw new SimulationException(new IllegalArgumentException("The weight has to be positive."));

		this.weightInGrams = weightInGrams;
	}

	/**
	 * The weight of the item, in grams.
	 * 
	 * @return The weight in grams.
	 */
	public double getWeight() {
		return weightInGrams;
	}
}
