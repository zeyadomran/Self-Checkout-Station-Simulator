package org.lsmr.selfcheckout.software.test;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.*;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.software.*;

public class LoadCoinDispensersTest {
	public SelfCheckoutSoftware checkout;
	
	
	@Before
	public void setUp() {
		int [] banknoteDenoms = new int[] {5, 10, 20, 50, 100};
		BigDecimal [] coinDenoms = new BigDecimal[] {new BigDecimal(0.05), new BigDecimal (0.10), new BigDecimal(0.25), new BigDecimal(1.00), new BigDecimal(2.00)};
		SelfCheckoutStation station = new SelfCheckoutStation(Currency.getInstance("CAD"), banknoteDenoms, coinDenoms, 10000, 1);
		checkout = new SelfCheckoutSoftware(station);
		checkout.registerAttendant("12345");
	}
	
	
	@Test
	public void loadOneCoinTest() {
		checkout.attendantLogin("12345");
		Coin c = new Coin(new BigDecimal(0.10), Currency.getInstance("CAD"));
		Assert.assertTrue(checkout.loadCoinDispenser(c));
		
	}
	
	@Test
	public void loadMultipleCoinsTest() {
		checkout.attendantLogin("12345");
		Coin c1 = new Coin(new BigDecimal(0.10), Currency.getInstance("CAD"));
		Coin c2 = new Coin(new BigDecimal(1.00), Currency.getInstance("CAD"));
		Coin [] coins = new Coin[] {c1, c2};
		Assert.assertTrue(checkout.loadCoinDispenser(coins));
	}
	
	@Test
	public void loadNoCoinsTest() {
		checkout.attendantLogin("12345");
		Assert.assertFalse(checkout.loadCoinDispenser());
	}
	
	@Test
	public void loadInvalidCoinTest() {
		Assert.assertFalse(checkout.loadCoinDispenser(null));
	}
	
	@Test(expected = SimulationException.class)
	public void loadInvalidValueCoinTest() {
		checkout.attendantLogin("12345");
		Coin c = new Coin(new BigDecimal(0.15), Currency.getInstance("CAD"));
		checkout.loadCoinDispenser(c);
	}
	
	@Test
	public void loadValidAndInvalidCoinsTest() {
		checkout.attendantLogin("12345");
		Coin c1 = new Coin(new BigDecimal(0.10), Currency.getInstance("CAD"));
		Coin c2 = new Coin(new BigDecimal(0.25), Currency.getInstance("CAD"));
		Coin [] coins = new Coin[] {c1, null, c2};
		Assert.assertFalse(checkout.loadCoinDispenser(coins));
	}
	
	@Test (expected = SimulationException.class)
	public void overloadCoinsTest() {
		checkout.attendantLogin("12345");
		int capacity = checkout.getStation().coinDispensers.get(new BigDecimal(0.10)).getCapacity();
		for (int i = 0; i < capacity + 5; i++) {
			Coin c = new Coin(new BigDecimal(0.10), Currency.getInstance("CAD"));
			checkout.loadCoinDispenser(c);
		}
	}
	
	@Test
	public void loadWithoutAttendantTest() {
		Coin c = new Coin(new BigDecimal(0.10), Currency.getInstance("CAD"));
		Assert.assertFalse(checkout.loadCoinDispenser(c));
	}
	
	@After
	public void takedown() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		checkout.resetStation();
	}

}
