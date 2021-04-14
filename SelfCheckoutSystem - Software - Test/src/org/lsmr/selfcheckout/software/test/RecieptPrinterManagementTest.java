package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.software.AttendantDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class RecieptPrinterManagementTest {
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	
	
	/**
	 * Checks that inkLeft updates correctly as the printer is used
	 */
	@Test
	public void inkLeftUpdate() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.generateReceipt();
		int inkLeft = control.getInkLeft();
		assertEquals(inkLeft, 954);
	}
	
	/**
	 * Checks that when adding ink the inkLeft values updates correctly
	 */
	
	@Test
	public void inkLeftAdding() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		control.addInkToPrinter(1000);
		assertEquals(control.getInkLeft(), 2000);
	}
	
	/**
	 * Checks that low ink boolean updates correctly
	 */
	@Test
	public void lowInkUpdate() {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		assertFalse(control.getLowInk());
		for(int i = 0; i < 20; i++) {
			control.generateReceipt();
			control.addPaperToPrinter(10);
		}
		assertTrue(control.getLowInk());
		AttendantDatabase.REGISTERED_ATTENDANTS.remove("1234");
	}
}
