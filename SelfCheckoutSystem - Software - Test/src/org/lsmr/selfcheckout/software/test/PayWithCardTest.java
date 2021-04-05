package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BlockedCardException;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.InvalidPINException;
import org.lsmr.selfcheckout.MagneticStripeFailureException;
import org.lsmr.selfcheckout.TapFailureException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.CardIssuersDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class PayWithCardTest {
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

	/*Tests tapCard with invalid Type */
	@Test
	public void tapInvalidTypeCardTest() throws IOException, SimulationException, ChipFailureException, TapFailureException {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card card = new Card("Membership Card", "1213", "Zeyad", "123", "0000", true, true);
		for(int i = 0; i < 100; i++) { // Test for tap failure probability
			try {
				if(control.tapCard(card)) {   
					successful+= 1;
				} 
				s.cardReader.remove();
			} catch (TapFailureException e) {
				//ignore
			} catch(ChipFailureException e) {
				//ignore
			}
		}
		assertFalse("unsuccessful tap: " + successful + "%", successful > 90);
		
	}
	
	/*Tests tapCard with null card */
	@Test (expected = NullPointerException.class)
	public void tapNullCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card card = null;
		control.tapCard(card);
	}
	
	/*Tests tapCard with null Type */
	@Test (expected = SimulationException.class)
	public void tapNullTypeCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card card = new Card(null, "30062029", "Kylie Sicat", "123", "0000", true, true);
		control.tapCard(card);
	}
	
	/*Tests tapCard with null Number Type */
	@Test (expected = SimulationException.class)
	public void tapNullNumberCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card debitCard = new Card("Debit", null, "Kylie Sicat", "123", "0000", true, true);
		control.tapCard(debitCard);
	}
	
	/*Tests tapCard with null Cardholder Type */
	@Test (expected = SimulationException.class)
	public void tapNullCardholderCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card debitCard = new Card("Debit", "30062029", null, "123", "0000", true, true);
		control.tapCard(debitCard);
	}
	
	/*Tests tapCard with null Pin Type */
	@Test (expected = SimulationException.class)
	public void tapNullPinCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card debitCard = new Card("Debit", "30062029", "Kylie Sicat", "123", null, true, true);
		control.tapCard(debitCard);
	}
	
	/*Tests tapCard with tapEnabled = true on card */
	@Test
	public void tapDebitCardWithTapEnabledTest() throws IOException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card debitCard = new Card("Debit", "3006202", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("3006202", "Kylie Sicat", cal, "123", new BigDecimal("20"));
		assertTrue(control.tapCard(debitCard));
	}
	
	/*Tests tapCard with tapEnabled = false on card */
	@Test (expected = NullPointerException.class) 
	public void tapDebitCardWithTapDisabledTest() throws IOException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card debitCard = new Card("Debit", "30062029", "Kylie Sicat", "123", "0000", false, true);
		control.tapCard(debitCard);
	}
	
	/*Tests tapCard with tapEnabled = true on card */
	@Test
	public void tapCreditCardWithTapEnabledTest() throws IOException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		int successful = 0;
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card card = new Card("Credit", "3006209", "Kylie Sicat", "123", "1111", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("3006209", "Kylie Sicat", cal, "123", new BigDecimal("20000"));
		//assertTrue(control.tapCard(card));
		for(int i = 0; i < 100; i++) { // Test for  Magnetic Stripe probability
			try {
				if(control.tapCard(card)) {   
					successful+= 1;
				} 
			} catch (ChipFailureException e) {
				//ignore
			}
			catch(TapFailureException e) {
				//ignore
			}
		}
        assertTrue("unsuccessful tap: " + successful + "%", successful > 80);
	}
	
	/*Tests swipeCard with null card */
	@Test (expected = NullPointerException.class)
	public void swipeNullCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BufferedImage signature = null;
		Card card = null;
		control.swipeCard(card, signature);
	}
	/*Tests swipeCard with invalid Type */
	@Test //(expected = MagneticStripeFailureException.class)
	public void swipeInvalidTypeCardTest() throws IOException, MagneticStripeFailureException {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BufferedImage signature = null; 
		Card card = new Card("Membership Card", "1213", "Zeyad", "123", "0000", true, true);
		//assertFalse(control.swipeCard(card, signature));
		for(int i = 0; i < 100; i++) { // Test for chip failure probability
			try {
				if(control.swipeCard(card, signature)) {   
					successful += 1;
				} 
				s.cardReader.remove();
			} catch (MagneticStripeFailureException e) {
				//ignore
			}
		}
        assertFalse("unsuccessful swipe", successful > 90); // false bc it should be an invalid card
	}
	
	
	@Test
	public void swipeDebitCardTest() throws IOException, MagneticStripeFailureException {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BufferedImage signature = null; 
		Card card = new Card("Debit", "3006029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("3006029", "Kylie Sicat", cal, "123", new BigDecimal("20000"));
		for(int i = 0; i < 100; i++) { // Test for  Magnetic Stripe probability
			try {
				if(control.swipeCard(card, signature)) {   
					successful+= 1;
				} 
				s.cardReader.remove();
			} catch (MagneticStripeFailureException e) {
				//ignore
			}
		}
        assertTrue("unsuccessful swipe: " + successful + "%", successful > 80);
	}
	
	@Test
	public void swipeCreditCardTest() throws IOException {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BufferedImage signature = null; 
		Card card = new Card("Credit", "3062029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("3062029", "Kylie Sicat", cal, "123", new BigDecimal("20000"));
		for(int i = 0; i < 100; i++) { // Test for  Magnetic Stripe probability
			try {
				if(control.swipeCard(card, signature)) {   
					successful+= 1;
				} 
				s.cardReader.remove();
			} catch (MagneticStripeFailureException e) {
				//ignore
			}
		}
        assertTrue("unsuccessful swipe: " + successful + "%", successful > 80);
	}
	
	/*Tests insertCard with null card */
	@Test (expected = NullPointerException.class)
	public void insertNullCardTest() throws IOException, SimulationException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		String insertedPin = "0000";
		Card card = null;
		control.insertCard(card, insertedPin);
	}
	
	/*Tests insertCard with correctPin*/
	@Test
	public void insertCardCorrectPinTest() throws IOException, SimulationException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		String insertedPin = "0000";
		Card card = new Card("Debit", "0062029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("0062029", "Kylie Sicat", cal, "123", new BigDecimal("20000"));
		assertTrue(control.insertCard(card, insertedPin));
	}

	/*Tests insertCard with incorrectPin and throws an InvalidPINException */
	@Test (expected = InvalidPINException.class)
	public void insertCardIncorrectPinTest() throws IOException, SimulationException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		int successful = 0;
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		String insertedPin = "1234";
		Card card = new Card("Credit", "30062029", "Kylie Sicat", "123", "0000", true, true);
		for(int i = 0; i < 100; i++) { // Test for  chip failure probability
			try {
				s.cardReader.remove();
				if(control.insertCard(card, insertedPin)) {   
					successful+= 1;
				} 
			} catch (ChipFailureException e) {
				//ignore
			}
		}
        assertTrue("unsuccessful insert: " + successful + "%", successful > 80);
	}
	
	/*Tests insertCard with invalid Type */
	@Test 
	public void insertInvalidTypeCardTest() throws IOException, SimulationException, ChipFailureException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		String insertedPin = "0000";
		Card card = new Card("Membership Card", "1213", "Zeyad", "123", "0000", true, true);
		assertFalse(control.insertCard(card, insertedPin));
	}

	/*Tests insertCard and the chipFailureException */
	@Test
	public void insertCreditCardTest() throws IOException, SimulationException, ChipFailureException {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		String insertedPin = "0000";
		Card card = new Card("Credit", "300029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("300029", "Kylie Sicat", cal, "123", new BigDecimal("20000"));
        for(int i = 0; i < 100; i++) { // Test for  chip failure probability
			try {
				s.cardReader.remove();
				if(control.insertCard(card, insertedPin)) {   
					successful+= 1;
				} 
			} catch (ChipFailureException e) {
				//ignore
			}
		}
        assertTrue("unsuccessful insert: " + successful + "%", successful > 90);
	}
	
	@Test
	public void payWithDebitCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
				
		String insertedPin = "0000";
		Card card = new Card("Credit", "300629", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("300629", "Kylie Sicat", cal, "123", new BigDecimal("20000"));

		control.insertCard(card, insertedPin);
		assertEquals(control.getAmountPaid().toString(), "12");
	}
	
	@Test(expected = BlockedCardException.class)
	public void blockCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		String insertedPin1 = "0004";

		Card card = new Card("Credit", "30062029", "Kylie Sicat", "123", "0000", true, true);
		
		for(int i = 0; i < 4; i++) {
			try {
				s.cardReader.remove();

				control.insertCard(card, insertedPin1);
			}
			catch(InvalidPINException e) {
				//ignore
			}
			catch(ChipFailureException e) {
				//ignore
			}

		}
	}

	@Test
	public void debitCardBrokeTest() {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card card = new Card("Debit", "306029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("306029", "Kylie Sicat", cal, "123", new BigDecimal("20"));
		for(int i = 0; i < 100; i++) { // Test for  chip failure probability
			try {
				s.cardReader.remove();
				if(control.insertCard(card, "0000")) {   
					successful += 1;
				} 
			} catch (ChipFailureException e) {
				//ignore
			} catch (IOException e) {
				//ignore
			}
		}
        assertFalse("unsuccessful insert: " + successful + "%", successful > 80);
	}

	@Test
	public void creditCardBrokeTest() {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card card = new Card("Credit", "302029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.CREDIT_CARD_ISSUER.addCardData("302029", "Kylie Sicat", cal, "123", new BigDecimal("20"));
		for(int i = 0; i < 100; i++) { // Test for  chip failure probability
			try {
				s.cardReader.remove();
				if(control.insertCard(card, "0000")) {   
					successful += 1;
				} 
			} catch (ChipFailureException e) {
				//ignore
			} catch (IOException e) {
				//ignore
			}
		}
        assertFalse("unsuccessful insert: " + successful + "%", successful > 80);
	}
	
	@Test
	public void swipeGiftCardTest() throws IOException {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BufferedImage signature = null; 
		Card card = new Card("Gift", "3062029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.GIFT_CARD_ISSUER.addCardData("3062029", "Kylie Sicat", cal, "123", new BigDecimal("2000"));
		for(int i = 0; i < 100; i++) { // Test for  Magnetic Stripe probability
			try {
				if(control.swipeCard(card, signature)) {   
					successful+= 1;
				} 
				s.cardReader.remove();
			} catch (MagneticStripeFailureException e) {
				//ignore
			}
		}
        assertTrue("unsuccessful swipe: " + successful + "%", successful > 80);
	}
	
	@Test
	public void payWithGiftCardTest() throws IOException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
				
		String insertedPin = "0000";
		Card card = new Card("Gift", "300629", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.GIFT_CARD_ISSUER.addCardData("300629", "Kylie Sicat", cal, "123", new BigDecimal("200"));

		control.insertCard(card, insertedPin);
		assertEquals(control.getAmountPaid().toString(), "12");
	}
	
	
	@Test
	public void giftCardBrokeTest() {
		int successful = 0;
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("17.36"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		Card card = new Card("Gift", "302029", "Kylie Sicat", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.GIFT_CARD_ISSUER.addCardData("302029", "Kylie Sicat", cal, "123", new BigDecimal("20"));
		for(int i = 0; i < 100; i++) { // Test for  chip failure probability
			try {
				s.cardReader.remove();
				if(control.insertCard(card, "0000")) {   
					successful += 1;
				} 
			} catch (ChipFailureException e) {
				//ignore
			} catch (IOException e) {
				//ignore
			}
		}
        assertFalse("unsuccessful insert: " + successful + "%", successful > 80);
	}
	
}
