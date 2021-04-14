package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.CardIssuersDatabase;
import org.lsmr.selfcheckout.software.MemberDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class SystemTest {
    SelfCheckoutSoftware control = null;
    Barcode code = null;
    Barcode code2 = null;
    Barcode code3 = null;
    Card memberCard = null;



    /* Initialize a new SelfCheckoutSoftware object before each test. */
    @Before 
    public void init() {
        Currency c = Currency.getInstance(Locale.CANADA);
        int[] noteDenom = {5, 10, 20, 50, 100};
        BigDecimal[] coinDenom = { new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00") };
        SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
        control = new SelfCheckoutSoftware(s);

        code =  new Barcode("123");
		BarcodedProduct bp = new BarcodedProduct(code, "Cheese", new BigDecimal("12"));
		control.addProduct(bp, 2);
		
		code2 = new Barcode("1234");
		BarcodedProduct bp2 = new BarcodedProduct(code2, "Milk", new BigDecimal("3.50"));
		control.addProduct(bp2, 4);
		
		code3 = new Barcode("12345");
		BarcodedProduct bp3 = new BarcodedProduct(code3, "Juice", new BigDecimal("5.00"));
		control.addProduct(bp3, 4);

       

    }

    /* Tests a Scenario of a user using the self checkout station. */
    @Test
    public void Scenario1() {
    	
    	 String name = "Zeyad";
         String id = "1234";
         control.addMember(name, id);
         memberCard = MemberDatabase.REGISTERED_MEMBERS.get(id).getMemberCard();
         
        control.scanItem(code, 0.1);
        assertTrue("Item was not scanned!", (control.getScannedItems().size() == 1));

        control.placeItemInBaggingArea(code);
        assertTrue("Item was not placed in bagging area!", (control.getBaggingArea().size() == 1));

        assertTrue("Items were not placed in bagging area after scanning!", control.finishedScanningItems());

        assertTrue("Membership Card was not scanned!", control.swipeMembershipCard(memberCard));

        Currency c = Currency.getInstance(Locale.CANADA);
        Banknote banknote1 = new Banknote(10, c);
		Banknote banknote2 = new Banknote(5, c);
		ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
		banknotes.add(banknote1);
		banknotes.add(banknote2);
		try {
            assertTrue("Payment Failed", control.payWithCash(banknotes));
        } catch (SimulationException | OverloadException | DisabledException | EmptyException e) {
            // Ignore
        }
        
        control.generateReceipt();
        String receipt = control.getReceipt();
        assertTrue("Receipt was not generated!", receipt != null);
    }
    
    /* Tests a Scenario of a user using the self checkout station. Paying with coins, more than one item, non memeber */
    @Test
    public void Scenario2() {
        control.resetStation();

        control.scanItem(code, 0.1);
        control.placeItemInBaggingArea(code);
        
        control.scanItem(code2, 3);
        control.placeItemInBaggingArea(code2);
        
        control.scanItem(code3, 5);
        control.placeItemInBaggingArea(code3);

        assertTrue("Item was not scanned!", (control.getScannedItems().size() == 3));

        assertTrue("Item was not placed in bagging area!", (control.getBaggingArea().size() == 3));

        assertTrue("Items were not placed in bagging area after scanning!", control.finishedScanningItems());
        

        Currency c = Currency.getInstance(Locale.CANADA);
        Coin coin1 = new Coin(new BigDecimal("2.00"), c);
        
		ArrayList<Coin> coins = new ArrayList<Coin>();
		for(int i = 0; i < 15; i++)
		{
			coins.add(coin1);
		}
		try {
            assertTrue("Payment Failed", control.payWithCoin(coins));
        } catch (SimulationException | OverloadException | DisabledException | EmptyException e) {
            // Ignore
        }
        
        control.generateReceipt();
        String receipt = control.getReceipt();
        assertTrue("Receipt was not generated!", receipt != null);
    }

    
    /* Tests a Scenario of a user using the self checkout station. Paying with card, more than one item and is a Memeber */
    @Test
    public void Scenario3()  {
        control.resetStation();
        
        String name = "Matt";
        String id = "1224";
        control.addMember(name, id);
        memberCard = MemberDatabase.REGISTERED_MEMBERS.get(id).getMemberCard();

        control.scanItem(code, 0.1);
        control.placeItemInBaggingArea(code);
        
        control.scanItem(code2, 3);
        control.placeItemInBaggingArea(code2);
        
        control.scanItem(code3, 5);
        control.placeItemInBaggingArea(code3);

        assertTrue("Item was not scanned!", (control.getScannedItems().size() == 3));

        assertTrue("Item was not placed in bagging area!", (control.getBaggingArea().size() == 3));

        assertTrue("Items were not placed in bagging area after scanning!", control.finishedScanningItems());
        
		Card debitCard = new Card("Debit", "3006201", "Matt Jarrams", "123", "0000", true, true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 4);
		CardIssuersDatabase.DEBIT_CARD_ISSUER.addCardData("3006201", "Matt Jarrams", cal, "123", new BigDecimal("50"));
		try {
			assertTrue(control.tapCard(debitCard));
		} catch (IOException e) {
			e.printStackTrace();
		}

        
        control.generateReceipt();
        String receipt = control.getReceipt();
        assertTrue("Receipt was not generated!", receipt != null);
        assertTrue(control.removePurchasedItems());
    }

    
    /* Resets the control software after every test. */
    @After 
    public void TearDown() {
        control.resetStation();
    }
}
