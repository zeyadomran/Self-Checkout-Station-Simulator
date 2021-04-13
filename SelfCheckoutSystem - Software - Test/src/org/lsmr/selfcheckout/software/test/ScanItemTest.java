package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

public class ScanItemTest {
    Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

    /* Checks behavior when an item with a null Barcode is scanned */
	@Test(expected = NullPointerException.class)
	public void scanItemNULLBarcode() throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.scanItem(null, 12);
	}
	
	/* Checks behavior when an item with negative weight is scanned */
	@Test(expected = IllegalArgumentException.class)
	public void scanItemNegativeWeight() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.scanItem(new Barcode("12"), -5);
	}
	
	/* Checks behavior when an item with zero weight is scanned */
	@Test(expected = IllegalArgumentException.class)
	public void scanItemZeroWeight()throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.scanItem(new Barcode("12"), 0);
	}
	
	/* Checks behavior when an item that is not in the database is scanned */
	@Test
	public void scanItemNotInDatabase()throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		boolean pass = control.scanItem(new Barcode("2314"), 12);
		assertEquals(pass, false);
	}
	
	/* Checks that when an item is scanned the proper values are stored correctly */
	@Test
	public void scanItemExpectedUse() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BigDecimal expectedTotal = new BigDecimal("12");
		BigDecimal returnedTotal = control.getTotal();
		assertEquals(expectedTotal, returnedTotal);
		int expectedInventory = 1;
		int returnedInventory = control.getInventoryDB().get(bp);
		assertEquals(expectedInventory, returnedInventory);
		boolean returnedBool = control.getScannedItems().get(0).getBarcode().equals(b);
		assertEquals(returnedBool, true);
	}

    /* Checks that when an item that is out of stock is scanned the function returns
	 * false */
	@Test
	public void scanItemNoStock() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 1);
		control.scanItem(b, 12);
		boolean retValue = control.scanItem(b, 12);
		assertEquals(retValue, false);
	}

    /* Tests removing null scanned item */
	@Test(expected = NullPointerException.class)
	public void removeNullScannedItem()throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.removeScannedItem(null);
	}
	
	/* Removing scanned item that has not been scanned */
	@Test
	public void removeNotScannedItem() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		BarcodedItem item = new BarcodedItem(new Barcode("1234"), 12);
		boolean retVal = control.removeScannedItem(item);
		assertFalse(retVal);
	}
	
	/* Tests that the function removes the scanned item correctly */
	@Test
	public void removeScannedItemExpectedUse() throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BarcodedItem item = control.getScannedItems().get(0);
		control.removeScannedItem(item);
		int size = control.getScannedItems().size();
		assertEquals(size, 0);
	}
	
    /* Tests getScannedItems returns the correct items */
	@Test
	public void getScannedItemsTest() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		assertEquals(control.getScannedItems().size(), 1);
		assertEquals(control.getScannedItems().get(0).getBarcode(), b);
	}

    /* Tests getTotal returns the correct total */
	@Test
	public void getTotalTest() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BigDecimal price = new BigDecimal("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", price);
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		assertEquals(control.getTotal(), price);
	}

	/* Tests enterNumberOfBags with a negative number and test that it throws an illegal argument exception */
	@Test(expected = IllegalArgumentException.class)
	public void invalidBagTest() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.enterNumberOfBags(-1); 
	}
	
	/* Tests enterNumberOfBags with a valid number of bags */
	@Test
	public void validBagTest() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.enterNumberOfBags(4); 
	}
	
	
}
