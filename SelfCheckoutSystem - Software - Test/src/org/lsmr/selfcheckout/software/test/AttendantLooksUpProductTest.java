package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.software.AttendantDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

/**
 * 
 * Test class for an attendant to look up products without bar codes
 *
 */
public class AttendantLooksUpProductTest {
	
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("0.50"), new BigDecimal("1.00"), new BigDecimal("2.00")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

	/**
	 * Test an attendant looking up a plu product name to get the code
	 * 
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpCodeLoggedIn() throws SimulationException, OverloadException
	{
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Blueberries", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		PriceLookupCode testCode = control.attendantLookUpProductCode("Blueberries");
		assertEquals(testCode, plc);
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		
		
	}

	/**
	 * Test an attendant looking up a plu code to get the product name of the item
	 * 
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpProdNameLoggedIn() throws SimulationException, OverloadException
	{
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		String name = control.attendantFindProductName(plc);
		
		assertEquals(name, apple.getDescription());
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
	}
	
	/**
	 * Test an attendant looking up a plu code to get the product name not in the system
	 * will return no results
	 * 
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpProdNameLoggedInNotinSystem() throws SimulationException, OverloadException
	{
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		String name = control.attendantFindProductName(new PriceLookupCode("1235"));
		
		assertEquals(name, "no results");
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
	}
	
	/**
	 * Test an attendant looking up a plu product name to get the code not in the system
	 * will return  null
	 * 
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpCodeLoggedInNotinSystem() throws SimulationException, OverloadException
	{
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		PriceLookupCode test = control.attendantLookUpProductCode("Cabbage");
		
		assertEquals(test, null);
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
	}
	
	/**
	 * Test an attendant looking up a plu product name to get the code but 
	 * will return nothing since the attendant is not logged in
	 * 
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpCodeNotLoggedIn() throws SimulationException, OverloadException
	{
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		
		PriceLookupCode test = control.attendantLookUpProductCode("Red Apple");
		
		assertEquals(test, null);
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
	}
	
	
	/**
	 * Test an attendant looking up a a plu code to get the name but will not return since the attendant is not logged in
	 * 
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpNameNotLoggedIn() throws SimulationException, OverloadException
	{
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		
		String name = control.attendantFindProductName(plc);
		
		assertEquals(name, "Not logged in");
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

	}
	
	/**
	 * Tests a attendant logging in to get the code for an item with the name
	 * 
	 * Then entering it as a checked item for the customer
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpCodeLoggedInForCustomer() throws SimulationException, OverloadException
	{
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("3421");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		PriceLookupCode testCode = control.attendantLookUpProductCode("Apple");
		assertEquals(testCode, plc);
		
		control.addPLUItem(testCode, 2000);
		
		assertEquals(control.getPluItems().get(0).getPLUCode(), plc);
		assertEquals(control.getTotal(), new BigDecimal("2.00"));
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		
		
	}
	
	
	/**
	 * Tests a attendant logging in to get the name for an item and entering the code for the customer
	 * 
	 * Then entering it as a checked item for the customer
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void attendantLooksUpProdNameLoggedInForCustomer() throws SimulationException, OverloadException
	{
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Strawberries", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		String name = control.attendantFindProductName(plc);
		assertEquals(name, "Strawberries");
		
		PriceLookupCode code = control.attendantLookUpProductCode(name);
		control.addPLUItem(code, 2000);
		
		assertEquals(control.getPluItems().get(0).getPLUCode(), plc);
		assertEquals(control.getTotal(), new BigDecimal("2.00"));
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		
		
	}

	/**
	 * Test looking up a product with a null code
	 * Not likely to happen, but done to avoid malicious use 
	 **/
	@Test (expected = SimulationException.class)
	public void attendantLooksUpCodeLoggedInNullName() throws SimulationException, OverloadException
	{
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("7777");

		PLUCodedProduct apple = new PLUCodedProduct(plc, "Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		control.attendantLookUpProductCode(null);
		
		
	}
	
	/*
	 * Test looking up a product with a null code
	 * Not likely to happen, but done to avoid malicious use 
	 */
	@Test (expected = SimulationException.class)
	public void attendantLooksUpNameLoggedInNullCode() throws SimulationException, OverloadException
	{
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("7177");

		PLUCodedProduct apple = new PLUCodedProduct(plc, "Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		control.attendantFindProductName(null);
		
		
	}
	

}
