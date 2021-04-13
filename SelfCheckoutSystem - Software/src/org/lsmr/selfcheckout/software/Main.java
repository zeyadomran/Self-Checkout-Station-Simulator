package org.lsmr.selfcheckout.software;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card;
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
		control.registerAttendant("1234");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		Card dCard = new Card("Debit", "123456", "John Doe", "123", "1234", true, true);
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("123456", "John Doe", cal, "123", new BigDecimal("2000"));
		Card cCard = new Card("Credit", "123456", "John Doe", "123", "1234", true, true);
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("123456", "John Doe", cal, "123", new BigDecimal("2000"));
		Card gCard = new Card("Gift", "123456", "John Doe", "123", "1234", true, true);
		CardIssuersDatabase.GIFT_CARD_ISSUER.addCardData("123456", "John Doe", cal, "123", new BigDecimal("20"));
		control.creditCards.put("123456", cCard);
		control.debitCards.put("123456", dCard);
		control.giftCards.put("123456", gCard);
		// control.scanItem(code, 1);
		// control.addPLUItem(pluC, 2400);
		control.loadMainGUI();
	}

}
