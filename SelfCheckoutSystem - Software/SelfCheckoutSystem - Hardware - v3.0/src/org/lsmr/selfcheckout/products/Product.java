package org.lsmr.selfcheckout.products;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.SimulationException;

/**
 * Abstract base class for products. Note that a "product" is the <b>kind</> of
 * item (e.g., 2 litre container of Dairyland brand 2% milk) and not an
 * individual item, which would be the specific physical object (e.g.,
 * <b>that</b> bottle of milk and not <b>this</b> one).
 */
public abstract class Product {
	private final BigDecimal price;
	private final boolean isPerUnit;

	/**
	 * Create a product instance.
	 * 
	 * @param price
	 *            The price per unit or per kilogram.
	 * @param isPerUnit
	 *            True if the price is per unit; false if it is per kilogram.
	 * @throws SimulationException
	 *             If the price is null or &le;0.
	 */
	protected Product(BigDecimal price, boolean isPerUnit) {
		if(price == null)
			throw new SimulationException(new NullPointerException("price is null"));

		if(price.compareTo(BigDecimal.ZERO) <= 0)
			throw new SimulationException(new IllegalArgumentException("A product's price can only be positive."));

		this.price = price;
		this.isPerUnit = isPerUnit;
	}

	/**
	 * Gets the price of the product.
	 * 
	 * @return The price. Cannot be null. Must be &gt;0.
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * Tests whether the price is per-unit, as opposed to per-kilogram.
	 * 
	 * @return true if the price is per-unit; otherwise, false.
	 */
	public boolean isPerUnit() {
		return isPerUnit;
	}
}
