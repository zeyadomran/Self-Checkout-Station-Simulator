package org.lsmr.selfcheckout.software.test;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.*;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.software.*;

public class EmptyStorageTest {
	public SelfCheckoutSoftware checkout;

	@Before
	public void setup() {
		int [] banknoteDenoms = new int[] {5, 10, 20, 50, 100};
		BigDecimal [] coinDenoms = new BigDecimal[] {new BigDecimal(0.05), new BigDecimal (0.10), new BigDecimal(0.25), new BigDecimal(1.00), new BigDecimal(2.00)};
		SelfCheckoutStation station = new SelfCheckoutStation(Currency.getInstance("CAD"), banknoteDenoms, coinDenoms, 10000, 1);
		checkout = new SelfCheckoutSoftware(station);
		checkout.registerAttendant("12345");
	}
	
	@Test
	public void testCoinEmpty() {
		//tests to see if storage is emptied on empty
		checkout.attendantLogin("12345");
		Assert.assertTrue(checkout.emptyCoinStorage());
	}
	
	@Test
	public void testCoinEmptyAfterLoading() throws SimulationException, OverloadException {
		//tests to see if storage is emptied after coins are loaded
		checkout.attendantLogin("12345");
		Coin c1 = new Coin(new BigDecimal(0.10), Currency.getInstance("CAD"));
		Coin c2 = new Coin(new BigDecimal(0.25), Currency.getInstance("CAD"));
		checkout.getStation().coinStorage.load(c1, c2);
		Assert.assertTrue(checkout.emptyCoinStorage());
	}
	
	@Test
	public void testCoinEmptyWithoutAttendant() throws SimulationException, OverloadException {
		//empties storage without attendant in there
		Coin c1 = new Coin(new BigDecimal(0.10), Currency.getInstance("CAD"));
		Coin c2 = new Coin(new BigDecimal(0.25), Currency.getInstance("CAD"));
		checkout.getStation().coinStorage.load(c1, c2);
		Assert.assertFalse(checkout.emptyCoinStorage());
	}
	
	@Test
	public void testBanknoteEmpty() {
		//tests to see if storage is emptied on empty
		checkout.attendantLogin("12345");
		Assert.assertTrue(checkout.emptyBanknoteStorage());
	}
	
	@Test
	public void testBanknoteEmptyAfterLoading() throws SimulationException, OverloadException {
		//tests to see if storage is emptied after coins are loaded
		checkout.attendantLogin("12345");
		Banknote b1 = new Banknote(10, Currency.getInstance("CAD"));
		Banknote b2 = new Banknote(20, Currency.getInstance("CAD"));
		checkout.getStation().banknoteStorage.load(b1, b2);
		Assert.assertTrue(checkout.emptyBanknoteStorage());
	}
	
	@Test
	public void testBanknoteEmptyWithoutAttendant() throws SimulationException, OverloadException {
		//empties storage without attendant in there
		Banknote b1 = new Banknote(10, Currency.getInstance("CAD"));
		Banknote b2 = new Banknote(20, Currency.getInstance("CAD"));
		checkout.getStation().banknoteStorage.load(b1, b2);
		Assert.assertFalse(checkout.emptyBanknoteStorage());
	}
	
	
	@After
	public void takedown() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		checkout.resetStation();
	}
}
