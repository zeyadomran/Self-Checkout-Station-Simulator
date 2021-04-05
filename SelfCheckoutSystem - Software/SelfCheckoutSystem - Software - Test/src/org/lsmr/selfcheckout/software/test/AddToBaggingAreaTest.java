package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class AddToBaggingAreaTest {
    Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

    /* Tests placing an item with a null barcode in bagging area */
	@Test(expected = NullPointerException.class)
	public void placeNullItemInBaggingArea() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.placeItemInBaggingArea(null);
	}
	
	/* Tests placing an item that is not in the database in bagging area */
	@Test
	public void placeItemNotInDataInBaggingArea() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12345");
		control.scanItem(b, 12);
		boolean retVal = control.placeItemInBaggingArea(b);
		assertFalse(retVal);
	}
	
	/* Tests what happens when placing an item in the bagging area that has not
	 * been scanned */
	@Test
	public void placeUnscannedItemInBaggingArea() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 2);
		boolean retVal = control.placeItemInBaggingArea(b);
		assertEquals(retVal, false);
		// Found a bug here
		// It seems that .equals() cant be used on null so just change it to == null
		// and it should pass the test
	}
	
	/* Checks that the appropriate values are updated when an item is placed in the 
	 * bagging area */
	@Test
	public void placeItemInBaggingAreaExpectedUse() throws SimulationException, OverloadException{
		// Come back to this test
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 2);
		control.scanItem(b, 2);
		boolean retVal = control.placeItemInBaggingArea(b);
		assertEquals(retVal, true);
		Boolean retBoolVal = control.getBaggingArea().get(0).getBarcode().equals(b);
		assertEquals(retBoolVal, true);
	}
	
	/* Checks that the correct value is returned when the bagging area is overloaded */
	@Test
	public void getBaggingAreaWeightOverload() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 2);
		control.scanItem(b, 20000);
		control.placeItemInBaggingArea(b);
		double retWeight = control.getBaggingAreaWeight();
		assertEquals(retWeight, -1, 0.1);
	}
	
	/* Checks that the bagging area weight is returned correctly */
	@Test
	public void getBaggingAreaWeightExpectedUse()throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 2);
		control.scanItem(b, 5);
		control.placeItemInBaggingArea(b);
		double retVal = control.getBaggingAreaWeight();
		assertEquals(retVal, 5, 0);
	}
	
	/* Tests removing null item from bagging area */
	@Test(expected = NullPointerException.class)
	public void removeNullItemBaggingArea() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.removeItemBaggingArea(null);
	}
	
	/* Tests removing item that is not in bagging area from bagging area */
	@Test
	public void removeItemNotBaggingArea() throws SimulationException, OverloadException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		BarcodedItem bi = new BarcodedItem(b, 2);
		control.addProduct(bp, 2);
		control.scanItem(b, 20);
		boolean retVal = control.removeItemBaggingArea(bi);
		assertFalse(retVal);
	}
	
	/* Tests removing item from bagging area */
	@Test
	public void removeItemBaggingAreaExpectedUse() throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 2);
		control.scanItem(b, 20);
		control.placeItemInBaggingArea(b);
		BarcodedItem item = control.getBaggingArea().get(0);
		boolean retVal = control.removeItemBaggingArea(item);
		int size = control.getBaggingArea().size();
		assertTrue(retVal);
		assertEquals(size, 0);
	}

    /* Tests getBaggingArea returns the correct items */
	@Test
	public void getBaggingAreaTest() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		control.placeItemInBaggingArea(b);
		assertEquals(control.getBaggingArea().size(), 1);
		assertEquals(control.getBaggingArea().get(0).getBarcode(), b);
	}

}
