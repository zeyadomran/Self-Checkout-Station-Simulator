package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class ChangeTest 
{
    Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	Coin nickel;
	Coin dime;
	Coin quarter;
	Coin loonie;
	Coin twoonie;
	
	Banknote five;
	Banknote ten;
	Banknote twenty;
	Banknote fifty;
	Banknote hundred;
	@Before
	public void setUp() throws Exception
	{
		
		//valid coins
		nickel = new Coin(new BigDecimal("0.05"), c);
		dime = new Coin(new BigDecimal("0.10"), c);
		quarter = new Coin(new BigDecimal("0.25"), c);
		loonie = new Coin(new BigDecimal("1.00"), c);
		twoonie = new Coin(new BigDecimal("2.00"), c);
		
		//create bank notes for valid bills
		five = new Banknote(5, c);
		ten = new Banknote(10, c);
		twenty = new Banknote(20, c);
		fifty = new Banknote(50, c);
		hundred = new Banknote(100, c);
		
	}
    /* Tests paying with one coin to get change */
	@Test
	public void payWithCoinForChangeTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1.75"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(twoonie);
		
		
		assertTrue(control.payWithCoin(coins));

		List<Coin> coinList = s.coinTray.collectCoins();
		assertTrue(coinList.get(0).getValue().compareTo(new BigDecimal("0.25")) == 0);
	}
	
	/* Tests paying with lot's of coins to get change in coins */
	@Test
	public void payWithCoinLotsForChangeTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("4.50"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(twoonie);
		coins.add(loonie);
		coins.add(twoonie);
		coins.add(loonie);
		
		assertTrue(control.payWithCoin(coins));
		
		List<Coin> coinList = s.coinTray.collectCoins();
		assertTrue(coinList.get(0).getValue().compareTo(loonie.getValue()) == 0);
	}
	
	/* Tests paying and having change value less than lowest coin. 
	 * 
	 * We handle this by rounding the answer up to give them the lowest coin
	 * 
	 * So if 4 cents change left we give them a nickel (5 cents)
	 *   
	 */
	@Test
	public void payWithCoinsChangeLessThanLowestCoinTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("1.96"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(twoonie);
		
		assertTrue(control.payWithCoin(coins));
		
		List<Coin> coinList = s.coinTray.collectCoins();
		assertTrue(coinList.get(0).getValue().compareTo(nickel.getValue()) == 0);
	}
	
	
	/* Tests paying with notes and receiving notes back
	 * 
	 */
	@Test
	public void payWithNotesChangeInNotes() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("25.00"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Banknote> notes = new ArrayList<Banknote>();
		notes.add(fifty);
		
		int before = s.banknoteDispensers.get(20).size();
		int before2 = s.banknoteDispensers.get(5).size();

		assertTrue(control.payWithCash(notes));
		assertTrue(s.banknoteStorage.getBanknoteCount() == 1);
		assertTrue(s.banknoteDispensers.get(20).size() == (before - 1));
		assertTrue(s.banknoteDispensers.get(5).size() == (before2 - 1));

		
	}
	
	
	/* 
	 * Tests paying with notes and receiving notes back
	 * 
	 */
	@Test
	public void payWithLotsOfNotesChangeInNotes() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("100.00"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Banknote> notes = new ArrayList<Banknote>();
		notes.add(hundred);
		notes.add(hundred);
		notes.add(hundred);
		
		int before = s.banknoteDispensers.get(100).size();

		assertTrue(control.payWithCash(notes));
		assertTrue(s.banknoteDispensers.get(100).size() == (before - 2));
		assertTrue(s.banknoteStorage.getBanknoteCount() == 3 || s.banknoteStorage.getBanknoteCount() == 2);


		
	}
	
	/* Tests paying with coins and recieving optimal change value (notes and coins mixed)
	 * 
	 */
	@Test
	public void payWithCoinsChangeMixed() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("10.52"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(twoonie);
		coins.add(twoonie);
		coins.add(twoonie);
		coins.add(twoonie);
		coins.add(twoonie);
		coins.add(twoonie);
		coins.add(twoonie);
		coins.add(twoonie);
		
		int before = s.banknoteDispensers.get(5).size();
		
		assertTrue(control.payWithCoin(coins));
		assertTrue(s.banknoteDispensers.get(5).size() == (before - 1));
		
		List<Coin> coinList = s.coinTray.collectCoins();
		assertTrue(coinList.get(0).getValue().compareTo(quarter.getValue()) == 0);

		
	}
	
	/* Tests paying with notes and recieving optimal change value (notes and coins mixed)
	 * 
	 */
	@Test
	public void payWithNotesChangeMixed() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("43.60"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Banknote> notes = new ArrayList<Banknote>();
		notes.add(hundred);
		
		int before1 = s.banknoteDispensers.get(5).size();
		int before2 = s.banknoteDispensers.get(50).size();

		
		assertTrue(control.payWithCash(notes));
		
		assertTrue(s.banknoteDispensers.get(5).size() == (before1 - 1));
		assertTrue(s.banknoteDispensers.get(50).size() == (before2 - 1));

		
		List<Coin> coinList = s.coinTray.collectCoins();
		assertTrue(coinList.get(0).getValue().compareTo(loonie.getValue()) == 0);

		
	}
	
	/* Tests paying with coins and recieving optimal change value (notes and coins mixed)
	 * 
	 */
	@Test
	public void noChangeTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("10.00"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Banknote> notes = new ArrayList<Banknote>();
		notes.add(ten);
		
		int before = s.banknoteDispensers.get(10).size();
		
		assertTrue(control.payWithCash(notes));
		assertTrue(s.banknoteDispensers.get(5).size() == (before));
		
		List<Coin> coinList = s.coinTray.collectCoins();
		assertTrue(coinList.get(0) == null);

		
	}
	
	
	/* Tests loading notes into dispenser
	 * 
	 */
	@Test
	public void loadNoteDispensersTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutStation control = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
		
		int before = control.banknoteDispensers.get(10).size();
		int before2 = control.banknoteDispensers.get(20).size();
		
		Banknote[] notes = new Banknote[1];
		notes[0] = ten;
		control.banknoteDispensers.get(10).load(notes);
		notes[0] = twenty;
		control.banknoteDispensers.get(20).load(notes);
		
		assertTrue(control.banknoteDispensers.get(10).size() == before + 1);
		assertTrue(control.banknoteDispensers.get(20).size() == before2 + 1);

		
	}
	
	/* Tests loading coins into the dispensers
	 * 
	 */
	@Test
	public void loadCoinDispensersTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutStation control = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
		
		int before = control.coinDispensers.get(nickel.getValue()).size();
		int before2 = control.coinDispensers.get(loonie.getValue()).size();
		
		Coin[] coins = new Coin[1];
		coins[0] = nickel;
		control.coinDispensers.get(nickel.getValue()).load(coins);
		coins[0] = loonie;
		control.coinDispensers.get(loonie.getValue()).load(coins);
		
		assertTrue(control.coinDispensers.get(loonie.getValue()).size() == before + 1);
		assertTrue(control.coinDispensers.get(loonie.getValue()).size() == before2 + 1);

		
	}
	
	/* Tests loading coins into the dispensers by paying/entering them
	 * 
	 */
	@Test
	public void CoinsAddedToDispensersOncePayedTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("2.50"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(loonie);
		coins.add(loonie);
		coins.add(loonie);
		
		int before = s.coinDispensers.get(loonie.getValue()).size();
		
		assertTrue(control.payWithCoin(coins));
		
		assertTrue(s.coinDispensers.get(loonie.getValue()).size() == before + 3);

		
	}
	
	/* Tests overloading dispenser with coins 
	 * 
	 */
	@Test
	(expected = SimulationException.class)
	public void overflowDispenserCoinTest() throws DisabledException, EmptyException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		
		for(int i = 0; i < 200; i++)
		{
			coins.add(loonie);
		}
		
		
		assertTrue(control.payWithCoin(coins));


		
	}
	
	/* Tests overloading dispenser with notes 
	 * 
	 */
	@Test
	(expected = OverloadException.class)
	public void overflowDispenserNotesTest() throws DisabledException, EmptyException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		Banknote[] notes = new Banknote[200];
		
		for(int i = 0; i < 200; i++)
		{
			notes[i] = ten;
		}
		
		
		s.banknoteDispensers.get(10).load(notes);
		
	}
	
	/* Tests overloading coin tray and we expect a simulation exception
	 * 
	 */
	@Test (expected = SimulationException.class)
	public void overloadCoinTrayException() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("0.25"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		for(int i = 0; i < 50; i++)
		{
			coins.add(dime);
		}
		
		assertTrue(control.payWithCoin(coins));
		for(int i = 0; i < 50; i++)
		{
			coins.add(dime);
		}
		assertTrue(control.payWithCoin(coins));

		
	}
	
	/* Tests someone leaving their coins and then someone else collecting those coins to avoid an overflow for thier purchase
	 * 
	 */
	@Test
	public void loadCoinTrayLeaveCoinsCollectToAvoidOverflowTest() throws DisabledException, OverloadException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("0.25"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		
		ArrayList<Coin> coins = new ArrayList<Coin>();
		for(int i = 0; i < 50; i++)
		{
			coins.add(dime);
		}
		
		assertTrue(control.payWithCoin(coins));
		List<Coin> coinList = s.coinTray.collectCoins();
		
		assertTrue(control.payWithCoin(coins));
		coinList = s.coinTray.collectCoins();
	}
	


		
	
	
}