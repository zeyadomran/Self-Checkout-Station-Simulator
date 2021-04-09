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

public class AttendantLooksUpProductTest {
	
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("0.50"), new BigDecimal("1.00"), new BigDecimal("2.00")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

	
	@Test
	public void attendantLooksUpCodeLoggedIn() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		PriceLookupCode testCode = control.attendantLookUpProductCode("Red Apple");
		assertEquals(testCode, plc);
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		
		
	}

	
	@Test
	public void attendantLooksUpProdNameLoggedIn() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 20);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		String name = control.attendantFindProductName(plc);
		
		assertEquals(name, apple.getDescription());
		
		
		
	}

}
