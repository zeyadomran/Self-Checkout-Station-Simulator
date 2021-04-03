package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class PayWithCoinTest {
    Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	

    /* Tests payWithCoin accepts valid coin */
	@Test
	public void payWithCoinTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("0.25"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Coin coin = new Coin(new BigDecimal("0.25"), c);
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(coin);
		assertTrue(control.payWithCoin(coins));
	}
	
	/* Tests payWithCoin null coin */
	@Test(expected = NullPointerException.class)
	public void payWithCoinNullTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1.75"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Coin coin = null;
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(coin);
		control.payWithCoin(null);
	}
	
	/* Tests payWithCoin accepts valid coin */
	@Test
	public void payWithCoinNotEnoughTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1.75"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Coin coin = new Coin(new BigDecimal("1"), Currency.getInstance("CAD"));
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(coin);
		assertFalse(control.payWithCoin(coins));
	}
	
	/* Tests payWithCoin denies invalid coin */
	@Test
	public void payWithCoinNotValidTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1.75"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Coin coin = new Coin(new BigDecimal("2"), Currency.getInstance("AED"));
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(coin);
		assertFalse(control.payWithCoin(coins));
	}
	
	/* Tests payWithCoin if CoinStorage is full */
	@Test (expected = SimulationException.class)
	public void payWithCoinFullTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1.75"));
		control.addProduct(bp, 2000);
		Coin coin = new Coin(new BigDecimal("2"), c);
		ArrayList<Coin> coins = new ArrayList<Coin>();
		for(int i = 0; i < 2000; i++) {
			control.scanItem(b, 12);
			coins.add(coin);
		}
		control.payWithCoin(coins);
	}
}
