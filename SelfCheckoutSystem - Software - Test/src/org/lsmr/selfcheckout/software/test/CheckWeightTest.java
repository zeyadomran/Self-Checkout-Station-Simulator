package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;


public class CheckWeightTest {

	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation station = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	
	Barcode b = new Barcode("1234");
	BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
	PriceLookupCode plu = new PriceLookupCode("1234");
	PLUCodedProduct p = new PLUCodedProduct(plu, "TestPLUItem", new BigDecimal("1.00"));
	
	

	
	
	@Test
	public void testAddingBarcodedItems() {
		SelfCheckoutSoftware checkout = new SelfCheckoutSoftware(station);
		checkout.addProduct(bp, 5);
		checkout.scanItem(b, 5.0);
		checkout.placeItemInBaggingArea(b);
		
		boolean actual = checkout.checkWeight();
		
		assertEquals(actual, true);
		
	}
	
	@Test
	public void testAddingPLUItems() {
		SelfCheckoutSoftware checkout = new SelfCheckoutSoftware(station);
		checkout.addPLUProduct(p, 5);
		checkout.addPLUItem(plu, 5.0);
		checkout.placePluItemInBaggingArea(plu);
		
		boolean actual = checkout.checkWeight();
		
		assertEquals(actual, true);
	}
	
	@Test
	public void testAddingCustomerBag() {
		SelfCheckoutSoftware checkout = new SelfCheckoutSoftware(station);
		checkout.addOwnBag(5.0);
		
		boolean actual = checkout.checkWeight();
		
		assertEquals(actual, true);
	}
	
	@Test
	public void testAddItemsAndBags() {
		SelfCheckoutSoftware checkout = new SelfCheckoutSoftware(station);
		checkout.addProduct(bp, 5);
		checkout.scanItem(b, 5.0);
		checkout.placeItemInBaggingArea(b);
		
		checkout.addPLUProduct(p, 5);
		checkout.addPLUItem(plu, 5.0);
		checkout.placePluItemInBaggingArea(plu);
		
		checkout.addOwnBag(5.0);
		
		boolean actual = checkout.checkWeight();
		
		assertEquals(actual, true);
	}
	
	@Test
	public void testRemoveItem() {
		SelfCheckoutSoftware checkout = new SelfCheckoutSoftware(station);
		checkout.addProduct(bp, 5);
		checkout.scanItem(b, 5.0);
		checkout.placeItemInBaggingArea(b);
		BarcodedItem item = checkout.getBaggingArea().get(0);
		
		checkout.removeItemBaggingArea(item);
		boolean actual = checkout.checkWeight();
		
		assertEquals(actual, false);
	}
	
	@Test
	public void addUnscannedItem() {
		SelfCheckoutSoftware checkout = new SelfCheckoutSoftware(station);
		checkout.addProduct(bp, 5);
		checkout.placeItemInBaggingArea(b);
		
		boolean actual = checkout.checkWeight();
		
		assertEquals(actual, true);
	}
	
	

}
