package org.lsmr.selfcheckout.software.test;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.*;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.software.*;

public class LoadBanknoteDispensersTest {
	public SelfCheckoutSoftware checkout;
	@Before
	public void setUp() {
		int [] banknoteDenoms = new int[] {5, 10, 20, 50, 100};
		BigDecimal [] coinDenoms = new BigDecimal[] {new BigDecimal(0.05), new BigDecimal (0.10), new BigDecimal(0.25), new BigDecimal(1.00), new BigDecimal(2.00)};
		SelfCheckoutStation station = new SelfCheckoutStation(Currency.getInstance("CAD"), banknoteDenoms, coinDenoms, 10000, 1);
		checkout = new SelfCheckoutSoftware(station);
		checkout.registerAttendant("12345");
	}

	/**
	 * Test for loading one banknote into the banknote dispenser
	 */
	@Test
	public void loadOneBanknoteTest() {
		checkout.attendantLogin("12345");
		Banknote b = new Banknote(10, Currency.getInstance("CAD"));
		Assert.assertTrue(checkout.loadBanknoteDispenser(b));
		
	}

	/**
	 * Test for loading multiple banknotes into the banknote dispenser
	 */
	@Test
	public void loadMultipleBanknotesTest() {
		checkout.attendantLogin("12345");
		Banknote b1 = new Banknote(10, Currency.getInstance("CAD"));
		Banknote b2 = new Banknote(20, Currency.getInstance("CAD"));
		Assert.assertTrue(checkout.loadBanknoteDispenser(b1, b2));
	}

	/**
	 * Test for loading zero banknotes into the banknote dispenser
	 */
	@Test
	public void loadNoBanknotesTest() {
		checkout.attendantLogin("12345");
		Assert.assertFalse(checkout.loadBanknoteDispenser());
	}

	/**
	 * Test for loading an invalid banknote in the banknote dispenser
	 */
	@Test
	public void loadInvalidBanknoteTest() {
		Assert.assertFalse(checkout.loadBanknoteDispenser(null));
	}

	/**
	 * Test for loading a banknote with an invalid value
	 */
	@Test(expected = SimulationException.class)
	public void loadInvalidValueBanknoteTest() {
		checkout.attendantLogin("12345");
		Banknote b = new Banknote(15, Currency.getInstance("CAD"));
		checkout.loadBanknoteDispenser(b);
	}

	/**
	 * Test for loading multiple banknotes which are valid and invalid
	 */
	@Test
	public void loadValidAndInvalidBanknotesTest() {
		checkout.attendantLogin("12345");
		Banknote b1 = new Banknote(10, Currency.getInstance("CAD"));
		Banknote b2 = new Banknote(10, Currency.getInstance("CAD"));
		Banknote[] banknotes = new Banknote[] {b1, null, b2};
		Assert.assertFalse(checkout.loadBanknoteDispenser(banknotes));
	}

	/**
	 * Test for overloading the banknote dispenser
	 */
	@Test (expected = SimulationException.class)
	public void overloadBanknotesTest() {
		checkout.attendantLogin("12345");
		int capacity = checkout.getStation().banknoteDispensers.get(10).getCapacity();
		for (int i = 0; i < capacity + 5; i++) {
			Banknote b = new Banknote(10, Currency.getInstance("CAD"));
			checkout.loadBanknoteDispenser(b);
		}
	}

	/**
	 * Test for loading the banknote dispenser withoutn nthe help of an attendant
	 */
	@Test
	public void loadWithoutAttendantTest() {
		Banknote b = new Banknote(10, Currency.getInstance("CAD"));
		Assert.assertFalse(checkout.loadBanknoteDispenser(b));
	}

	/**
	 *
	 */
	@After
	public void takedown() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		checkout.resetStation();
	}
}
