package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class RemovePurchasedItemsTest {

    SelfCheckoutSoftware control = null;

    /* Initialize a new SelfCheckoutSoftware object before each test. */
    @Before
    public void init() {
        Currency c = Currency.getInstance(Locale.CANADA);
        int[] noteDenom = {5, 10, 20, 50, 100};
        BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
        SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
        control = new SelfCheckoutSoftware(s);
    }

    @Test
    public void removePurchasedItemsTest1() {
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 2);
		control.scanItem(b, 2);
		control.placeItemInBaggingArea(b);
		boolean retVal = control.removePurchasedItems();
		assertTrue(retVal);
    }
    
    @Test
    public void removePurchasedItemsTest2() {
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp, 2);
		control.scanItem(b, 2);
		control.placeItemInBaggingArea(b);
		Barcode b2 = new Barcode("1234");
		BarcodedProduct bp2 = new BarcodedProduct(b, "TestItem", new BigDecimal("1"));
		control.addProduct(bp2, 2);
		control.scanItem(b2, 2);
		control.placeItemInBaggingArea(b2);
		boolean retVal = control.removePurchasedItems();
		assertTrue(retVal);
    }
}
