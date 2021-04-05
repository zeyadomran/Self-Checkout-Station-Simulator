package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.*;

public class FailToPlaceItemInBaggingAreaTest {
	
	/*
	 * Test to scanning two items and not placing the second item into the bagging area
	 * This means the weight does not equal the expected weight so the the system throws a simulation exception
	 */
	@Test (expected = SimulationException.class)
	public void testFailToPlaceItem_true() {
		
			SelfCheckoutStation scs = createStation();
			SelfCheckoutSoftware control = new SelfCheckoutSoftware(scs);
			Barcode barcode1 = new Barcode("12345");
			Barcode barcode2 = new Barcode("24680");
			
			BarcodedProduct bp = new BarcodedProduct(barcode1, "TestItem1", new BigDecimal("1"));
			BarcodedProduct bp2 = new BarcodedProduct(barcode2, "TestItem2", new BigDecimal("9"));

			Item item1 = new BarcodedItem(barcode1, 50);
			Item item2 = new BarcodedItem(barcode2, 50);
			
			control.addProduct(bp, 10);
			control.addProduct(bp2, 40);
			double totalWeight = 100;
			control.scanItem(barcode1, 50);
			control.placeItemInBaggingArea(barcode1);
			control.scanItem(barcode2, 50);
			
			
			control.failToPlaceItem();
	}

	/*
	 * Test to scanning two items and placing both in the bagging area
	 * Failure to place item in bagging area should return false since the weights are equal
	 */
	@Test 
	public void testFailToPlaceItem_false() throws OverloadException {
		SelfCheckoutStation scs = createStation();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(scs);
		Barcode barcode1 = new Barcode("12345");
		Barcode barcode2 = new Barcode("24680");
		BarcodedProduct bp = new BarcodedProduct(barcode1, "TestItem1", new BigDecimal("1"));
		BarcodedProduct bp2 = new BarcodedProduct(barcode2, "TestItem2", new BigDecimal("9"));

		Item item1 = new BarcodedItem(barcode1, 50);
		Item item2 = new BarcodedItem(barcode2, 50);
		
		control.addProduct(bp, 10);
		control.addProduct(bp2, 40);
		double totalWeight = 100;
		control.scanItem(barcode1, 50);
		control.placeItemInBaggingArea(barcode1);
		control.scanItem(barcode2, 50);
		control.placeItemInBaggingArea(barcode2);

		assertFalse(control.failToPlaceItem());
		assertTrue(totalWeight == scs.baggingArea.getCurrentWeight());
	}
	
	private SelfCheckoutStation createStation() {
		Currency c = Currency.getInstance(Locale.CANADA);
		int[] note_denom = {5, 10, 20, 50, 100, 500};
		BigDecimal[] coin_denom = {new BigDecimal(0.05), new BigDecimal(0.10), new BigDecimal(0.25), new BigDecimal(1.00), new BigDecimal(2.00)};
		SelfCheckoutStation scs = new SelfCheckoutStation(c, note_denom, coin_denom, 500, 1);
		
		return scs;
	}
}
