package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.software.AttendantDatabase;
import org.lsmr.selfcheckout.software.ReceiptPrinterListenerStub;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

/*
 * Test class for testing the receipt printer at the self checkout station
 */
public class ReceiptTest 
{
	//Initialize a hardware station
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	
	/*
	 * Testing printing a normal receipt after scanning items and paying
	 */
	@Test
	public void testPrintReceiptTest() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b1 = new Barcode("12");
		Barcode b2 = new Barcode("1234");
		Barcode b3 = new Barcode("12566");

		BarcodedProduct bp1 = new BarcodedProduct(b1, "Cheese", new BigDecimal("12"));
		BarcodedProduct bp2 = new BarcodedProduct(b2, "Milk", new BigDecimal("10"));
		BarcodedProduct bp3 = new BarcodedProduct(b3, "Juice", new BigDecimal("5"));
		
		control.addProduct(bp1, 2);
		control.scanItem(b1, 10);
		
		control.addProduct(bp2, 2);
		control.scanItem(b2, 15);
		
		control.addProduct(bp3, 2);
		control.scanItem(b3, 20);
		
		Coin twoonie = new Coin(new BigDecimal("2.00"), c);
		ArrayList<Coin> coins = new ArrayList<Coin>();
		for(int i = 0; i < 15; i++)
		{
			coins.add(twoonie);
		}
		
		control.payWithCoin(coins);
		
		control.generateReceipt();
		
		String receipt = control.getReceipt();
		
		//receipt should not be null
		assertTrue(receipt != null);
		
		s.printer.cutPaper();
		String empty= "";
		assertEquals(s.printer.removeReceipt(), empty);
		
	}
	
	
	/*
	 * Testing printing a normal receipt after scanning items and paying
	 */
	@Test
	public void testPrintReceiptTestMixWithPLUAndScannedItems() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		control.registerAttendant("10000");
		control.attendantLogin("10000");
		Barcode b1 = new Barcode("12");
		Barcode b2 = new Barcode("1234");
		Barcode b3 = new Barcode("12566");
		control.addPaperToPrinter(20);

		BarcodedProduct bp1 = new BarcodedProduct(b1, "Cheese", new BigDecimal("12"));
		BarcodedProduct bp2 = new BarcodedProduct(b2, "Milk", new BigDecimal("10"));
		BarcodedProduct bp3 = new BarcodedProduct(b3, "Juice", new BigDecimal("5"));
		
		PriceLookupCode plc = new PriceLookupCode("1234");
		PriceLookupCode plc2 = new PriceLookupCode("1111");

		
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		PLUCodedProduct garlic = new PLUCodedProduct(plc2, "garlic", new BigDecimal("5.00"));

		
		control.addPLUProduct(apple, 300);
		control.addPLUProduct(garlic, 300);
		
		control.addPLUItem(plc, 4000);
		control.addPLUItem(plc2, 3000);
		
		control.addProduct(bp1, 2);
		control.scanItem(b1, 10);
		
		control.addProduct(bp2, 2);
		control.scanItem(b2, 15);
		
		control.addProduct(bp3, 2);
		control.scanItem(b3, 20);
		
		Banknote fifty = new Banknote(50, c);
		ArrayList<Banknote> notes = new ArrayList<Banknote>();
		
		notes.add(fifty);
		
		control.payWithCash(notes);
		
		control.generateReceipt();
		
		String receipt = control.getReceipt();
		
		//receipt should not be null
		assertTrue(receipt != null);
		
		s.printer.cutPaper();
		String empty= "";
		assertEquals(s.printer.removeReceipt(), empty);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		
	}
	
	/*
	 * Testing overloading the printer with too much paper, should cause an exception
	 */
	@Test (expected = SimulationException.class)
	public void overLoadPaperTest() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		s.printer.addPaper(2000);
	
	}
	
	/*
	 * Testing overloading the printer with ink
	 */
	@Test (expected = SimulationException.class)
	public void overLoadInkTest() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		s.printer.addInk(1000000000);
	
	}
	
	/*
	 * testing removing negative ink from the printer, should cause an exception
	 */
	@Test (expected = SimulationException.class)
	public void removeNegativeInkTest() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		s.printer.addInk(-1000);
	
	}
	
	/*
	 * Test removing a negative amount of paper from the printer, should result in an exception
	 */
	@Test (expected = SimulationException.class)
	public void removeNegativePaperTest() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		s.printer.addPaper(-1000);
	}
	
	/*
	 * Test adding a normal amount of paper into the machine
	 */
	@Test
	public void addPaperNormalTest() throws DisabledException, OverloadException, EmptyException
	{
		ReceiptPrinterListenerStub rls = new ReceiptPrinterListenerStub();
		s.printer.register(rls);
		s.printer.addPaper(100); 
		assertTrue(rls.isPaperAdded());
	}

	/*
	 * Test adding a normal amount of ink into the printer
	 */
	@Test
	public void addInkNormalTest() throws DisabledException, OverloadException, EmptyException
	{
		ReceiptPrinterListenerStub rls = new ReceiptPrinterListenerStub();
		s.printer.register(rls);
		s.printer.addInk(100);
		assertTrue(rls.isInkAdded());
	}
	
	/*
	 * Test trying to place too many characters onto one line of a receipt, should cause a simulation error
	 */
	@Test (expected = SimulationException.class)
	public void tooManyCharactersTest() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b1 = new Barcode("12");

		BarcodedProduct bp1 = new BarcodedProduct(b1, "Cheeseaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", new BigDecimal("12"));
		
		control.addProduct(bp1, 2);
		control.scanItem(b1, 10);
		
		control.generateReceipt();
		
		String receipt = control.getReceipt(); 
		
	}
	/*
	 * Test trying to check if the paper in the printer is over
	 */
	@Test(expected = SimulationException.class)
	public void noPaperInPrinter() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		for(int i = 1; i < 10; i++) {
			Barcode b1 = new Barcode(Integer.toString(i));
	
			BarcodedProduct bp1 = new BarcodedProduct(b1, Integer.toString(i), new BigDecimal(Integer.toString(i)));
			
			control.addProduct(bp1, 2);
			control.scanItem(b1, 1);
			
			control.generateReceipt();
			
			String receipt = control.getReceipt();
		
		}
	} 
	/*
	 * Test trying to check if the paper in the printer is over
	 */
	@Test(expected = SimulationException.class)
	public void noInkInPrinter() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		ReceiptPrinterListenerStub rls = new ReceiptPrinterListenerStub();

		for(int i = 1; i < 100; i++) {
			Barcode b1 = new Barcode(Integer.toString(i));
	
			BarcodedProduct bp1 = new BarcodedProduct(b1, Integer.toString(i), new BigDecimal(Integer.toString(i)));
			
			control.addProduct(bp1, 2);
			control.scanItem(b1, 1);
			
			control.generateReceipt();
			
			String receipt = control.getReceipt();
			s.printer.register(rls);
			s.printer.addPaper(20);
		}
	} 
	/*
	 * Test removing a receipt from the machine
	 * We expect the receipt that we next try to get to be null simulating the customer has left and the 
	 * printer is ready to print a new receipt
	 */
	@Test
	public void receiptRemovedTest() throws DisabledException, OverloadException, EmptyException
	{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		Barcode b1 = new Barcode("12");

		BarcodedProduct bp1 = new BarcodedProduct(b1, "Cheese", new BigDecimal("12"));
		
		control.addProduct(bp1, 2);
		control.scanItem(b1, 10);
		
		control.generateReceipt();
		
		String receipt = control.getReceipt();
		
		String nullReciept = s.printer.removeReceipt();
		//System.out.println(nullReciept);
		assertTrue(receipt != null);
		assertTrue(nullReciept == null);
		
	}
}
