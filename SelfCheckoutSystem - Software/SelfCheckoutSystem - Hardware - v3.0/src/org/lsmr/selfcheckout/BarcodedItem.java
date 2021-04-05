package org.lsmr.selfcheckout;

import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.Product;

/**
 * Represents items for sale, each with a particular barcode and weight.
 */
public class BarcodedItem extends Item {
	private Barcode barcode;

	/**
	 * Basic constructor.
	 * 
	 * @param kind
	 *            The kind of product that this item is.
	 * @param weightInGrams
	 *            The weight of the item.
	 * @throws SimulationException
	 *             If the barcode is null.
	 * @throws SimulationException
	 *             If the weight is &le;0.
	 */
	public BarcodedItem(Barcode barcode, double weightInGrams) {
		super(weightInGrams);

		if(barcode == null)
			throw new SimulationException(new NullPointerException("barcode is null"));

		this.barcode = barcode;
	}

	/**
	 * Gets the barcode of this item.
	 * 
	 * @return The barcode.
	 */
	public Barcode getBarcode() {
		return barcode;
	}
}
