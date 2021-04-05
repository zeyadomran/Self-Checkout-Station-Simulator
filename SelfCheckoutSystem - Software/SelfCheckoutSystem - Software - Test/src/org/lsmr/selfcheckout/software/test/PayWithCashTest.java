package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class PayWithCashTest {
    Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

    /* Tests payWithCash accepts valid banknote */
	@Test
	public void payWithCashTest() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Banknote banknote = new Banknote(5, c);
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		banknotes.add(banknote);
		banknotes.add(banknote);
		banknotes.add(banknote);
		banknotes.add(banknote);
		banknotes.add(banknote);
		banknotes.add(banknote);
		assertTrue(control.payWithCash(banknotes));
	}
	
	/* Tests payWithCash banknote storage full */
	@Test
	public void payWithCashFullTest() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("5"));
		control.addProduct(bp, 2000);
		Banknote banknote = new Banknote(10, c);
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		for(int i = 0; i < 1500; i++) {
			banknotes.add(banknote);
			control.scanItem(b, 12);
		}
		assertTrue(control.payWithCash(banknotes));
	}
	
	/* Tests payWithCash null banknotes */
	@Test(expected = NullPointerException.class)
	public void payWithCashNullTest() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("5"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Banknote banknote = null;
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		banknotes.add(banknote);
		control.payWithCash(null);
	}
	
	/* Tests payWithCash denies invalid banknote */
	@Test
	public void payWithCashNotValidTest() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("5"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Banknote banknote = new Banknote(10, Currency.getInstance("AED"));
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		banknotes.add(banknote);
		assertFalse(control.payWithCash(banknotes));
	}
	
	/* Tests payWithCash if banknotes value are less than total. */
	@Test
	public void payWithCashNotEnoughTest() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Banknote banknote = new Banknote(10, c);
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		banknotes.add(banknote);
		assertFalse(control.payWithCash(banknotes));
	}
	
	/* Tests payWithCash if multiple invalid and valid banknotes value are less than total. */
	@Test
	public void payWithCashMultipleInvalidTest() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Banknote banknote = new Banknote(10, c);
		Banknote banknote2 = new Banknote(10, Currency.getInstance("AED"));
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		banknotes.add(banknote2);
		banknotes.add(banknote);
		assertFalse(control.payWithCash(banknotes));
	}
	
	/* Tests payWithCash if multiple valid banknotes value are more than total. */
	@Test
	public void payWithCashMultipleValidTest() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Banknote banknote = new Banknote(10, c);
		Banknote banknote2 = new Banknote(20, c);
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		banknotes.add(banknote);
		banknotes.add(banknote2);
		assertTrue(control.payWithCash(banknotes));
	}
}
