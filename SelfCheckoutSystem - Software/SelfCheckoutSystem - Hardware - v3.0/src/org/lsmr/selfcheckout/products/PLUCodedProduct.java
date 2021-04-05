package org.lsmr.selfcheckout.products;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SimulationException;

/**
 * Represents products with price-lookup (PLU) codes. Such products always have
 * prices per-kilogram.
 */
public class PLUCodedProduct extends Product {
	private final PriceLookupCode pluCode;
	private final String description;

	/**
	 * Create a product.
	 * 
	 * @param pluCode
	 *            The PLU code of the product.
	 * @param description
	 *            The description of the product.
	 * @param price
	 *            The price per-kilogram of the product.
	 */
	public PLUCodedProduct(PriceLookupCode pluCode, String description, BigDecimal price) {
		super(price, false);

		if(pluCode == null)
			throw new SimulationException(new NullPointerException("barcode is null"));

		if(description == null)
			throw new SimulationException(new NullPointerException("description is null"));

		this.pluCode = pluCode;
		this.description = description;
	}

	/**
	 * Get the PLU code.
	 * 
	 * @return The PLU code. Cannot be null.
	 */
	public PriceLookupCode getPLUCode() {
		return pluCode;
	}

	/**
	 * Get the description.
	 * 
	 * @return The description. Cannot be null.
	 */
	public String getDescription() {
		return description;
	}
}
