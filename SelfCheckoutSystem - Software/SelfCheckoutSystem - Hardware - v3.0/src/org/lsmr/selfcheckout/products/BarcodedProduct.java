package org.lsmr.selfcheckout.products;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.SimulationException;

/**
 * Represents products with barcodes. Such products always have prices per-unit.
 */
public class BarcodedProduct extends Product {
	private final Barcode barcode;
	private final String description;

	/**
	 * Create a product.
	 * 
	 * @param barcode
	 *            The barcode of the product.
	 * @param description
	 *            The description of the product.
	 * @param price
	 *            The price per-unit of the product.
	 * @throws SimulationException
	 *             If any argument is null.
	 * @throws SimulationException
	 *             If the price is &le;0.
	 */
	public BarcodedProduct(Barcode barcode, String description, BigDecimal price) {
		super(price, true);

		if(barcode == null)
			throw new SimulationException(new NullPointerException("barcode is null"));

		if(description == null)
			throw new SimulationException(new NullPointerException("description is null"));

		this.barcode = barcode;
		this.description = description;
	}

	/**
	 * Get the barcode.
	 * 
	 * @return The barcode. Cannot be null.
	 */
	public Barcode getBarcode() {
		return barcode;
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
