package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class SelfCheckoutSoftwareTest {
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

	@Test
	public void SelfCheckoutSoftwareNormalConstructorTest() throws SimulationException, OverloadException {
		// Checking for unexpected exceptions
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
	}

	/* Checks behavior when null parameters are given in constructor */
	@Test(expected = NullPointerException.class)
	public void SelfCheckoutSoftwareNullConstructorTest()  throws SimulationException, OverloadException {
		SelfCheckoutSoftware s = new SelfCheckoutSoftware(null);
	}

	/* Tests behavior when null Barcode is added to database */
	@Test(expected = NullPointerException.class)
	public void addProductNULLProductTest()  throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.addProduct(null, 1);
	}
	
	/* Tests behavior when an item with negative weight is added to database */
	@Test(expected = IllegalArgumentException.class)
	public void addProductNegativeAmount()  throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.addProduct(new BarcodedProduct(new Barcode("12"), "Test", new BigDecimal("12")), -1);
	}
	
	/* Tests behavior when an item with zero weight is added to database */
	@Test(expected = IllegalArgumentException.class)
	public void addProductZeroAmount()  throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.addProduct(new BarcodedProduct(new Barcode("12"), "Test", new BigDecimal("12")), 0);
	}
	
	/* Checks that when an item is added to the database it gets stored properly */
	@Test
	public void addProductExpectedUse()  throws SimulationException, OverloadException{
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.addProduct(bp, 1);
		BarcodedProduct returnBP = control.getProductDB().get(b);
		assertEquals(bp, returnBP);
	}

	/* Tests behavior when trying to remove an item with a null barcode from
	 * the database */
	@Test(expected = NullPointerException.class)
	public void removeItemNULLBarcode() throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.removeProduct(null);
	}
	
	/* Tests what happens when trying to remove something from the database that
	 * is not in the database */
	@Test
	public void removeItemNotInDatabase() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		boolean retVal = control.removeProduct(new Barcode("1234"));
		assertEquals(retVal, false);
	}
	
	/* Tests that products are removed from the database correctly when the
	 * removeProduct method is used */
	@Test
	public void removeItemExpectedUSe()  throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 4);
		boolean retVal = control.removeProduct(b);
		assertEquals(retVal, true);
		boolean retDataVal = control.getProductDB().containsKey(b);
		assertEquals(retDataVal, false);
		boolean retInVal = control.getInventoryDB().containsKey(bp);
		assertEquals(retInVal, false);
	}

	/* Tests getProductDB returns the correct items */
	@Test
	public void getProductDBTest() throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		ProductDatabases.INVENTORY.clear();
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		assertEquals(control.getProductDB().size(), 1);
		assertEquals(control.getProductDB().get(b), bp);
	}
	
	/* Tests getInventoryDB returns the correct items */
	@Test
	public void getInventoryDBTest()  throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		ProductDatabases.INVENTORY.clear();
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		assertEquals(control.getInventoryDB().size(), 1);
		assertEquals(control.getInventoryDB().get(bp).intValue(), 2);
	}

}
