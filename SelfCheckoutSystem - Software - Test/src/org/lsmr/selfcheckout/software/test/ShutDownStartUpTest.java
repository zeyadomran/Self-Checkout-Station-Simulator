package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.AttendantDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class ShutDownStartUpTest {
	
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

	
	/**
	 * test starting up the station from shutdown state and then using the scanner to scan items
	 * which should work as expected
	 * 
	 * @throws SimulationException
	 * @throws OverloadException
	 */
	@Test
	public void startUpStationUseNormally() throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		control.shutDownStation();
		
		
		control.startUpStation();
		
		//test normal scan use
		Barcode b = new Barcode("12");
		BarcodedProduct bp = new BarcodedProduct(b, "TestItem", new BigDecimal("12"));
		control.addProduct(bp, 2);
		control.scanItem(b, 12);
		BigDecimal expectedTotal = new BigDecimal("12");
		BigDecimal returnedTotal = control.getTotal();
		assertEquals(expectedTotal, returnedTotal);
		int expectedInventory = 1;
		int returnedInventory = control.getInventoryDB().get(bp);
		assertEquals(expectedInventory, returnedInventory);
		boolean returnedBool = control.getScannedItems().get(0).getBarcode().equals(b);
		assertEquals(returnedBool, true);
		
		
		assertEquals(false, control.isShutDown());
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	
	
	@Test (expected = DisabledException.class)
	public void shutDownStationDevicesDisabled() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12346");
		control.attendantLogin("12346");
		control.shutDownStation();
		
		assertEquals(true, control.isShutDown());
		
			
		//test normal paying with coins, slot shoud be disabled
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(new Coin((new BigDecimal("2.00")), c));
		control.payWithCoin(coins);
		
		
		
	}

}
