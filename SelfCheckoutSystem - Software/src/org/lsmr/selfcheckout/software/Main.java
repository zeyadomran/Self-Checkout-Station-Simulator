package org.lsmr.selfcheckout.software;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class Main {

	public static void main(String[] args) {
		Currency c = Currency.getInstance(Locale.CANADA);
        int[] noteDenom = {5, 10, 20, 50, 100};
        BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
        SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
        SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode code = new Barcode("1234");
		control.addProduct(new BarcodedProduct(code, "test", new BigDecimal("1.2")), 2);
		control.scanItem(code, 1);
		control.refreshGUI();
	}

}
