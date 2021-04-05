package org.lsmr.selfcheckout;

import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.Product;

/**
 * Represents items for sale, each with a particular barcode and weight.
 */
public class PLUCodedItem extends Item {
	private PriceLookupCode pluCode;

	/**
	 * Basic constructor.
	 * 
	 * @param kind
	 *            The kind of product that this item is.
	 * @param weightInGrams
	 *            The weight of the item.
	 */
	public PLUCodedItem(PriceLookupCode pluCode, double weightInGrams) {
		super(weightInGrams);
		
		if(pluCode == null)
			throw new SimulationException(new NullPointerException("pluCode is null"));
		
		this.pluCode = pluCode;
	}

	/**
	 * Gets the PLU code of this item.
	 * 
	 * @return The PLU code.
	 */
	public PriceLookupCode getPLUCode() {
		return pluCode;
	}
}
