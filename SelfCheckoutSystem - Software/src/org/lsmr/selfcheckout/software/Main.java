package org.lsmr.selfcheckout.software;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class Main {

	public static void main(String[] args) {
		Currency c = Currency.getInstance(Locale.CANADA);
		int[] noteDenom = { 5, 10, 20, 50, 100 };
		BigDecimal[] coinDenom = { new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"),
				new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2") };
		SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode code1 = new Barcode("1234");
		Barcode code2 = new Barcode("12345");
		PriceLookupCode pluC1 = new PriceLookupCode("2345");
		PriceLookupCode pluC2 = new PriceLookupCode("23456");
		control.addProduct(new BarcodedProduct(code1, "testBarcode1", new BigDecimal("1.2")), 2);
		control.addPLUProduct(new PLUCodedProduct(pluC1, "testPLU1", new BigDecimal("3.3")), 2);
		control.addProduct(new BarcodedProduct(code2, "testBarcode2", new BigDecimal("1.2")), 2);
		control.addPLUProduct(new PLUCodedProduct(pluC2, "testPLU2", new BigDecimal("3.3")), 2);
		// control.scanItem(code, 1);
		// control.addPLUItem(pluC, 2400);
		control.loadMainGUI();
	}

}
