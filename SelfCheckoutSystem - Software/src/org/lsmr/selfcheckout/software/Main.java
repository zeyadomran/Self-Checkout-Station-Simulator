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
	private static SelfCheckoutSoftware control;

	public static void main(String[] args) {
		initializeGUI();
		control.startGUI();
	}

	private static void initializeGUI() {
		// Control
		Currency c = Currency.getInstance(Locale.CANADA);
		int[] noteDenom = { 5, 10, 20, 50, 100 };
		BigDecimal[] coinDenom = { new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2") };
		SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
		control = new SelfCheckoutSoftware(s);

		// Barcode Items
		Barcode code1 = new Barcode("1234");
		Barcode code2 = new Barcode("2341");
		Barcode code3 = new Barcode("3412");
		Barcode code4 = new Barcode("4123");
		Barcode code5 = new Barcode("4321");
		Barcode code6 = new Barcode("3214");
		control.addProduct(new BarcodedProduct(code1, "Mint Gum", new BigDecimal("1.2")), 2);
		control.addProduct(new BarcodedProduct(code2, "Protein Bar", new BigDecimal("2.8")), 4);
		control.addProduct(new BarcodedProduct(code3, "IPhone Charger", new BigDecimal("19.99")), 42);
		control.addProduct(new BarcodedProduct(code4, "1TB Hard Drive", new BigDecimal("102.3")), 1);
		control.addProduct(new BarcodedProduct(code5, "Mask", new BigDecimal("0.99")), 7);
		control.addProduct(new BarcodedProduct(code6, "PS5 Game", new BigDecimal("49.99")), 5);

		// PLU Items
		PriceLookupCode pluC1 = new PriceLookupCode("1234");
		PriceLookupCode pluC2 = new PriceLookupCode("2341");
		PriceLookupCode pluC3 = new PriceLookupCode("3412");
		PriceLookupCode pluC4 = new PriceLookupCode("4123");
		PriceLookupCode pluC5 = new PriceLookupCode("4321");
		PriceLookupCode pluC6 = new PriceLookupCode("3214");
		control.addPLUProduct(new PLUCodedProduct(pluC1, "Apple", new BigDecimal("2.3")), 2);
		control.addPLUProduct(new PLUCodedProduct(pluC2, "Strawberry", new BigDecimal("5")), 8);
		control.addPLUProduct(new PLUCodedProduct(pluC3, "Potato", new BigDecimal("4.2")), 20);
		control.addPLUProduct(new PLUCodedProduct(pluC4, "Cucumber", new BigDecimal("2.1")), 11);
		control.addPLUProduct(new PLUCodedProduct(pluC5, "Mango", new BigDecimal("7")), 22);
		control.addPLUProduct(new PLUCodedProduct(pluC6, "Peach", new BigDecimal("1.99")), 12);

		// Attendants
		control.registerAttendant("1234");
		control.registerAttendant("4321");

		// Cards
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);

		// Debit
		Card dCard = new Card("Debit", "123456", "John Doe", "123", "1234", true, true);
		Card dCard2 = new Card("Debit", "654321", "John Doe", "123", "1234", false, false);
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("123456", "John Doe", cal, "123", new BigDecimal("2000"));
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("654321", "John Doe", cal, "123", new BigDecimal("20"));
		control.debitCards.put("123456", dCard);
		control.debitCards.put("654321", dCard2);

		// Credit
		Card cCard = new Card("Credit", "123456", "John Doe", "123", "1234", true, true);
		Card cCard2 = new Card("Credit", "654321", "John Doe", "123", "1234", false, false);
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("123456", "John Doe", cal, "123", new BigDecimal("2000"));
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("654321", "John Doe", cal, "123", new BigDecimal("20"));
		control.creditCards.put("123456", cCard);
		control.creditCards.put("654321", cCard2);

		// Gift
		Card gCard = new Card("Gift", "123456", "John Doe", "123", "1234", true, true);
		Card gCard2 = new Card("Gift", "654321", "John Doe", "123", "1234", false, false);
		CardIssuersDatabase.GIFT_CARD_ISSUER.addCardData("123456", "John Doe", cal, "123", new BigDecimal("2000"));
		CardIssuersDatabase.GIFT_CARD_ISSUER.addCardData("654321", "John Doe", cal, "123", new BigDecimal("20"));
		control.giftCards.put("123456", gCard);
		control.giftCards.put("654321", gCard2);
	}

}
