package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;

public class ElectronicScale extends AbstractDevice<ElectronicScaleListener> {
	private ArrayList<Item> items = new ArrayList<>();

	private double weightLimitInGrams;
	private double currentWeightInGrams = 0;
	private double weightAtLastEvent = 0;
	private double sensitivity;

	/**
	 * Constructs an electronic scale with the indicated maximum weight that it can
	 * handle before going into overload.
	 * 
	 * @param weightLimitInGrams
	 *            The weight threshold beyond which the scale will overload.
	 * @param sensitivity
	 *            The number of grams that can be added or removed since the last
	 *            change event, without causing a new change event.
	 * @throws SimulationException
	 *             If either argument is &le;0.
	 */
	public ElectronicScale(int weightLimitInGrams, int sensitivity) {
		if(weightLimitInGrams <= 0)
			throw new SimulationException(new IllegalArgumentException("The maximum weight cannot be zero or less."));

		if(sensitivity <= 0)
			throw new SimulationException(new IllegalArgumentException("The sensitivity cannot be zero or less."));

		this.weightLimitInGrams = weightLimitInGrams;
		this.sensitivity = sensitivity;
	}

	/**
	 * Gets the weight limit for the scale.
	 * 
	 * @return The weight limit.
	 */
	public double getWeightLimit() {
		return weightLimitInGrams;
	}

	/**
	 * Gets the current weight on the scale.
	 * 
	 * @return The current weight.
	 * @throws OverloadException
	 *             If the weight has overloaded the scale.
	 */
	public double getCurrentWeight() throws OverloadException {
		if(currentWeightInGrams <= weightLimitInGrams)
			return currentWeightInGrams;

		throw new OverloadException();
	}

	/**
	 * Gets the sensitivity of the scale. Changes smaller than this limit are not
	 * noticed or announced.
	 * 
	 * @return The sensitivity.
	 */
	public double getSensitivity() {
		return sensitivity;
	}

	/**
	 * Adds an item to the scale.
	 * 
	 * @param item
	 *            The item to add.
	 * @throws SimulationException
	 *             If the same item is added more than once.
	 */
	public void add(Item item) {
		if(items.contains(item))
			throw new SimulationException("The same item cannot be added more than once to the scale.");

		currentWeightInGrams += item.getWeight();

		items.add(item);

		if(currentWeightInGrams > weightLimitInGrams)
			notifyOverload();
		else if(currentWeightInGrams - weightAtLastEvent > sensitivity)
			notifyWeightChanged();
	}

	/**
	 * Removes an item from the scale.
	 * 
	 * @param item
	 *            The item to remove.
	 * @throws SimulationException
	 *             If the item is not on the scale.
	 */
	public void remove(Item item) {
		if(!items.remove(item))
			throw new SimulationException("The item was not found amongst those on the scale.");

		// To avoid drift in the sum due to round-off error, recalculate the weight.
		double newWeightInGrams = 0.0;
		for(Item itemOnScale : items)
			newWeightInGrams += itemOnScale.getWeight();

		double original = currentWeightInGrams;
		currentWeightInGrams = newWeightInGrams;

		if(original > weightLimitInGrams && newWeightInGrams <= weightLimitInGrams)
			notifyOutOfOverload();

		if(currentWeightInGrams <= weightLimitInGrams && weightAtLastEvent - currentWeightInGrams >= sensitivity)
			notifyWeightChanged();
	}

	private void notifyOverload() {
		for(ElectronicScaleListener l : listeners)
			l.overload(this);
	}

	private void notifyOutOfOverload() {
		weightAtLastEvent = currentWeightInGrams;

		for(ElectronicScaleListener l : listeners)
			l.outOfOverload(this);
	}

	private void notifyWeightChanged() {
		weightAtLastEvent = currentWeightInGrams;

		for(ElectronicScaleListener l : listeners)
			l.weightChanged(this, currentWeightInGrams);
	}
}
