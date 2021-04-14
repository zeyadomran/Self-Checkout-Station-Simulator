package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class PluProductTest {
	
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);

	/* Checks that when plu product is entered to database we can access it */
	@Test
	public void addProductPluTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 300);
		
		
		assertEquals(control.getInventoryDB().get(apple), Integer.valueOf(300));
		assertTrue(control.getProductPLUDB().get(plc) == apple);
		
		
	}
	
	/* Checks that when plu product is removed from the database we can access it */
	@Test
	public void removeProductPluTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 300);
		
		control.removePluProduct(plc);		
		
		assertEquals(control.getProductPLUDB().get(plc), null);
		
		
	}
	
	/* Checks that when plu product is removed from the item list and bagging area the software
	 * detects the removal
	 */
	@Test
	public void removePlutItemBaggingAreaTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 300);
		
		control.addPLUItem(plc, 4000);
		
		control.placePluItemInBaggingArea(plc);
		
		assertTrue(control.removePluItemBaggingArea(control.getPluItems().get(0)));
		assertTrue(control.removePluItem(control.getPluItems().get(0)));
		assertTrue(control.getBaggingArea().size() == 0);
		assertTrue(control.getBaggingAreaWeight() == 0.0);
		
		
	}
	

	/* tests removing a plu product from the item list and bagging area the software
	 * not registered in the system
	 */
	@Test
	public void removePlutItemNotInBaggingAreaTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 300);
		PLUCodedItem test = new PLUCodedItem(plc, 30);
		assertFalse(control.removePluItemBaggingArea(test));
		assertFalse(control.removePluItem(test));
		assertTrue(control.getBaggingArea().size() == 0);
		assertTrue(control.getBaggingAreaWeight() == 0.0);
		
		
	}
	
	/* Checks that when plu product is removed from the item list and bagging area the software
	 * detects the removal
	 */
	@Test (expected = SimulationException.class)
	public void failToPlacePluInBaggingAreaTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		PriceLookupCode plc2 = new PriceLookupCode("1111");

		
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		PLUCodedProduct garlic = new PLUCodedProduct(plc2, "garlic", new BigDecimal("5.00"));

		
		control.addPLUProduct(apple, 300);
		control.addPLUProduct(garlic, 300);
		
		control.addPLUItem(plc, 4000);
		control.placePluItemInBaggingArea(plc);
		
		control.addPLUItem(plc2, 250);
		
		BigDecimal expectedTotal = new BigDecimal("5.25");
		BigDecimal returnedTotal = control.getTotal();
		
		assertEquals(expectedTotal, returnedTotal);
		control.failToPlaceItem();
		
		
	}
	
	/* Checks that when plu product is added to the checked items not in database the item is null and returns false
	 */
	@Test 
	public void testPlacePluNotCheckedInBaggingAreaNull() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		PriceLookupCode plc2 = new PriceLookupCode("1111");

		
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		PLUCodedProduct garlic = new PLUCodedProduct(plc2, "garlic", new BigDecimal("5.00"));

		assertFalse(control.placePluItemInBaggingArea(plc));
		assertEquals(control.getBaggingAreaPlu().size(), 0);
		
		
		
	}
	
	/* Checks that when a plu product not in the database is checked it is not added to items
	 * 
	*/
	@Test 
	public void testPlacePluNotCheckedInBaggingArea() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("9871");

		
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));

		assertFalse(control.placePluItemInBaggingArea(plc));
		assertEquals(control.getBaggingAreaPlu().size(), 0);
		
		
		
	}
	
	/* Tests removing null plu item */
	@Test(expected = NullPointerException.class)
	public void removeNullScannedItem()throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.removePluItem(null);
	}
	
	/* Tests removing plu product not in database
	 *
	 */
	@Test
	public void removeProductNotInDatabase()throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		PriceLookupCode plc = new PriceLookupCode("9991");
		PLUCodedProduct orange = new PLUCodedProduct(plc, "Orange", new BigDecimal("1.00"));

		assertFalse(control.removePluProduct(plc));
	}
	
	/* Checks that when an a plu item that is out of stock is scanned the function returns
	 * false
	 */
	@Test
	public void checkItemNoStock() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		PriceLookupCode plc = new PriceLookupCode("8888");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
						
		boolean retValue = control.addPLUItem(plc, 4000);
		assertEquals(retValue, false);
	}
	
	/* Checks that when an a plu item that has no stock is scanned the function returns
	 * false
	 */
	@Test
	public void testItemNoInventory() throws SimulationException, OverloadException{
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		PriceLookupCode plc = new PriceLookupCode("1233");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		control.addPLUProduct(apple, 1);
		
		
		control.addPLUItem(plc, 3000);
		boolean retValue = control.addPLUItem(plc, 3000);

		
		assertEquals(retValue, false);
	}
	
	
	/* Checks behavior when a plu item with zero weight is scanned */
	@Test(expected = IllegalArgumentException.class)
	public void scanItemZeroWeight()throws SimulationException, OverloadException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		PriceLookupCode plc = new PriceLookupCode("1233");
		control.addPLUItem(plc, 0.0);
	}
	
	/* Checks that when a plu item is added the proper values are stored correctly */
	@Test
	public void checkoutPluItemNormalTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		
		control.addPLUProduct(apple, 300);
		
		
		control.addPLUItem(plc, 500);
		control.placePluItemInBaggingArea(plc);
		
		BigDecimal expectedTotal = new BigDecimal("0.500");
		BigDecimal returnedTotal = control.getTotal();
	
	
		assertEquals(expectedTotal, returnedTotal);
		int expectedInventory = 299;
		int returnedInventory = control.getInventoryDB().get(apple);
		assertEquals(expectedInventory, returnedInventory);
		boolean returnedBool = control.getPluItems().get(0).getPLUCode().equals(plc);
		assertEquals(returnedBool, true);
	

		
		
	}
	
	/* Checks that when lots of plu items are entered the the proper values are stored correctly */
	@Test
	public void checkoutMultiplePluItemNormalTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		PriceLookupCode plc2 = new PriceLookupCode("1224");

		
		PLUCodedProduct potatoes = new PLUCodedProduct(plc, "Potatoes", new BigDecimal("3.00"));
		PLUCodedProduct apple = new PLUCodedProduct(plc2, "Red Apple", new BigDecimal("1.00"));

		
		control.addPLUProduct(apple, 300);
		control.addPLUProduct(potatoes, 1000);
		
		
		control.addPLUItem(plc2, 500);
		control.placePluItemInBaggingArea(plc2);
		
		control.addPLUItem(plc, 5000);
		control.placePluItemInBaggingArea(plc);
		
		BigDecimal expectedTotal = new BigDecimal("15.500");
		BigDecimal returnedTotal = control.getTotal();
	
	
		assertEquals(expectedTotal, returnedTotal);
		int expectedInventory = 999;
		int returnedInventory = control.getInventoryDB().get(potatoes);
		assertEquals(expectedInventory, returnedInventory);
		boolean returnedBool = control.getPluItems().get(1).getPLUCode().equals(plc);
		assertEquals(returnedBool, true);
	
	
	}
	
	/* Checks that when plu product is removed from the item list and bagging area the software
	 * detects the removal
	 */
	@Test 
	public void mixPluandScannedItemsTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1234");
		PriceLookupCode plc2 = new PriceLookupCode("1111");
		Barcode b = new Barcode("12");
		
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		PLUCodedProduct garlic = new PLUCodedProduct(plc2, "garlic", new BigDecimal("5.00"));
		
		BarcodedProduct crackers = new BarcodedProduct(b, "Crackers", new BigDecimal("5.00"));

		
		control.addPLUProduct(apple, 300);
		control.addPLUProduct(garlic, 300);
		control.addProduct(crackers, 2);
		
		
		control.scanItem(b, 12);
		control.placeItemInBaggingArea(b);
		
		control.addPLUItem(plc, 4000);
		control.placePluItemInBaggingArea(plc);
		
		control.addPLUItem(plc2, 250);
		control.placePluItemInBaggingArea(plc2);

		
		BigDecimal expectedTotal = new BigDecimal("10.2500");
		BigDecimal returnedTotal = control.getTotal();
		
		assertEquals(expectedTotal, returnedTotal);
		assertFalse(control.failToPlaceItem());
		
		boolean returnedBool = control.getPluItems().get(1).getPLUCode().equals(plc2);
		assertEquals(returnedBool, true);
		
		boolean testBool = control.getScannedItems().get(0).getBarcode().equals(b);
		assertEquals(testBool, true);
		
		
	}
	
	/* Checks that when plu product is entered to database we can access it */
	@Test
	public void lookUpProductTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1335");
		PriceLookupCode plc2 = new PriceLookupCode("7638");
		PriceLookupCode plc3 = new PriceLookupCode("1834");
		PriceLookupCode plc4 = new PriceLookupCode("7294");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		PLUCodedProduct apple2 = new PLUCodedProduct(plc2, "Green Apple", new BigDecimal("1.00"));
		PLUCodedProduct potatoes = new PLUCodedProduct(plc3, "Potatoes", new BigDecimal("5.00"));
		PLUCodedProduct  garlic = new PLUCodedProduct(plc4, "Garlic", new BigDecimal("3.00"));
		
		control.addPLUProduct(apple, 300);
		control.addPLUProduct(apple2, 300);
		control.addPLUProduct(potatoes, 300);
		control.addPLUProduct(garlic, 300);

		PriceLookupCode testCode = control.lookUpProductCode("Garlic");
		
		assertEquals(testCode, plc4);
		
		
	}
	
	/* Checks that when plu product is entered to database we can access it */
	@Test
	public void lookUpProductNotInDatabaseTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1335");
		PriceLookupCode plc2 = new PriceLookupCode("7638");
		PriceLookupCode plc3 = new PriceLookupCode("1834");
		PriceLookupCode plc4 = new PriceLookupCode("7294");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		PLUCodedProduct apple2 = new PLUCodedProduct(plc2, "Green Apple", new BigDecimal("1.00"));
		PLUCodedProduct potatoes = new PLUCodedProduct(plc3, "Potatoes", new BigDecimal("5.00"));
		PLUCodedProduct  garlic = new PLUCodedProduct(plc4, "Garlic", new BigDecimal("3.00"));
		
		control.addPLUProduct(apple, 300);
		control.addPLUProduct(apple2, 300);
		control.addPLUProduct(potatoes, 300);
		control.addPLUProduct(garlic, 300);

		PriceLookupCode testCode = control.lookUpProductCode("Onions");
		
		assertEquals(testCode, null);
		
	}
	
	/* Checks that when plu product is entered to database we can access it */
	@Test (expected = SimulationException.class)
	public void lookUpNullProductTest() throws SimulationException, OverloadException
	{
		
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);

		PriceLookupCode plc = new PriceLookupCode("1335");
		PriceLookupCode plc2 = new PriceLookupCode("7638");
		PriceLookupCode plc3 = new PriceLookupCode("1834");
		PriceLookupCode plc4 = new PriceLookupCode("7294");
		
		PLUCodedProduct apple = new PLUCodedProduct(plc, "Red Apple", new BigDecimal("1.00"));
		PLUCodedProduct apple2 = new PLUCodedProduct(plc2, "Green Apple", new BigDecimal("1.00"));
		PLUCodedProduct potatoes = new PLUCodedProduct(plc3, "Potatoes", new BigDecimal("5.00"));
		PLUCodedProduct  garlic = new PLUCodedProduct(plc4, "Garlic", new BigDecimal("3.00"));
		
		control.addPLUProduct(apple, 300);
		control.addPLUProduct(apple2, 300);
		control.addPLUProduct(potatoes, 300);
		control.addPLUProduct(garlic, 300);

		PriceLookupCode testCode = control.lookUpProductCode(null);

	}

}
