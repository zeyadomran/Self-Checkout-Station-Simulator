package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class AddOwnBagToBaggingAreaTest {
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	
	/**
	 * Tests adding a bag with a negative weight.
	 */
	@Test
	public void addOwnBagNegative() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		boolean retVal = control.addOwnBag(-2);
		assertFalse(retVal);
	}
	
	/**
	 * Tests adding a bag with zero weight.
	 */
	@Test
	public void addOwnBagZero() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		boolean retVal = control.addOwnBag(0);
		assertFalse(retVal);
	}
	
	/**
	 * Tests that the getter function for the number of personal bags works correctly
	 */
	@Test
	public void getOwnBagTest() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		ArrayList<BarcodedItem> retVal = control.getPersonalBags();
		assertEquals(retVal.size(), 0);
	}
	
	/**
	 * Tests that the number and weight of personal bags is updated correctly.
	 */
	@Test
	public void addOwnBagExpectedUse() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		boolean retVal = control.addOwnBag(4);
		assertTrue(retVal);
		double retWeight = control.getPersonalBags().get(0).getWeight();
		int retSize = control.getPersonalBags().size();
		assertEquals(retWeight, 4, 0);
		assertEquals(retSize, 1);
	}
	
	/**
	 * Tests that the number of personal bags updates correctly when removing a bag.
	 */
	@Test
	public void removeOwnBagExpectedUse() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.addOwnBag(10);
		BarcodedItem bag = control.getPersonalBags().get(0);
		boolean retVal = control.removeOwnBag(bag);
		assertTrue(retVal);
		int retNum = control.getPersonalBags().size();
		assertEquals(retNum, 0);
	}
	
	/**
	 * Tests that removing a bag that is not in bagging area returns false.
	 */
	@Test
	public void removeOwnBagNotInBaggingArea() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		BarcodedItem bag = new BarcodedItem(new Barcode("1234"), 10);
		boolean retVal = control.removeOwnBag(bag);
		assertFalse(retVal);
	}
	
	/**
	 * Tests that removing a null bag returns false.
	 */
	@Test
	public void removeOwnBagNull() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		boolean retVal = control.removeOwnBag(null);
		assertFalse(retVal);
	}
}