package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.software.Attendant;
import org.lsmr.selfcheckout.software.AttendantDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;


/*
 * This class tests the functionality that an attendant who is logged in can do
 * It also tests some of the things that require an attendant to be logged in
 * 
 */
public class AttendantTest {
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	
    
	
	/**
	 * Tests a normal use for an attendant to log in to the system
	 */
	@Test
	public void login() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		assertTrue(control.getattendantLoggedIn());
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	
	/**
	 * Tests a normal use when an attendant is logging out
	 */
	@Test
	public void logout() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		control.attendantLogOut();
		assertFalse(control.getattendantLoggedIn());
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	
	/**
	 * Tests an attendant trying to log out when they haven't logged in
	 * should never happen
	 */
	@Test (expected = SimulationException.class)
	public void logoutWithNoLoggedIn() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.attendantLogOut();
		
	}
	
	
	/**
	 * Tests an attendant trying to login when another attendant has not logged out before 
	 */
	@Test (expected = SimulationException.class)
	public void loginTwoAttendants() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		control.registerAttendant("12445");
		control.attendantLogin("12445");
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

	}
	
	
	
	/**
	 * Tests an attendant logging in who is not registered in the system
	 */
	@Test (expected = SimulationException.class)
	public void loginNotRegistered() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.attendantLogin("12345");
	
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

	}
	
	
	/**
	 * Tests an attendant entering the wrong code for their login 
	 * 
	 */
	@Test (expected = SimulationException.class)
	public void loginWrongCode() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		control.attendantLogin("123456");
	
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();

	}
	
	
	/**
	 * Tests shutting down a system with an attendant logged in
	 */
	@Test
	public void shutDownSystemLoggedIn() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		control.shutDownStation();
		
		assertTrue(control.isShutDown());
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests starting up the system with an attendant logged in
	 * 
	 */
	@Test
	public void startUpSystemLoggedIn() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		
		control.startUpStation();
		
		assertFalse(control.isShutDown());
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests shutting down a system without an attendant logged in
	 */
	@Test
	public void shutDownSystemNotLoggedIn() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		
		control.shutDownStation();
		
		assertFalse(control.isShutDown());
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests starting up the system without an attendant logged in
	 */
	@Test
	public void startUpSystemNotLoggedIn() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		
		control.registerAttendant("12345");
		control.setShutDown(true);
		
		control.startUpStation();
		
		assertTrue(control.isShutDown());
		
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	/**
	 * Tests adding ink to printer
	 */
	@Test
	public void addInkTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		boolean actual = a.addInkToPrinter(control, 10);
		assertTrue(actual);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding negative amount of ink to printer
	 */
	@Test (expected = SimulationException.class)
	public void addNegativeInk() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		a.addInkToPrinter(control, -10);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding zero ink to printer
	 */
	@Test
	public void addZeroInk() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		control.attendantLogin("12");
		Attendant a = new Attendant("12");
		boolean actual = a.addInkToPrinter(control, 0);
		assertTrue(actual);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding paper to printer
	 */
	@Test
	public void addPaperTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		boolean actual = a.addPaperToPrinter(control, 10);
		assertTrue(actual);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding negative amount of paper to printer
	 */
	@Test (expected = SimulationException.class)
	public void addNegativePaper() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		a.addPaperToPrinter(control, -10);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding zero paper to printer
	 */
	@Test
	public void addZeroPaper() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		control.attendantLogin("12");
		Attendant a = new Attendant("12");
		boolean actual = a.addPaperToPrinter(control, 0);
		assertTrue(actual);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding ink while not logged in
	 */
	@Test
	public void addInkNotLoggedIn() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		Attendant a = new Attendant("12");
		boolean actual = a.addInkToPrinter(control, 0);
		assertFalse(actual);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding paper while not logged in
	 */
	@Test
	public void addPaperNotLoggedIn() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		Attendant a = new Attendant("12");
		boolean actual = a.addPaperToPrinter(control, 0);
		assertFalse(actual);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests blocking the station updates the field correctly
	 */
	@Test
	public void blockStationTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		control.attendantLogin("12");
		Attendant a = new Attendant("12");
		boolean actual = a.blockStation(control);
		assertTrue(actual);
		boolean blocked = control.isBlocked();
		assertTrue(blocked);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests unblocking the station updates the field correctly
	 */
	@Test
	public void unblockStationTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		control.attendantLogin("12");
		Attendant a = new Attendant("12");
		boolean actual = a.unBlockStation(control);
		assertTrue(actual);
		boolean blocked = control.isBlocked();
		assertFalse(blocked);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests blocking the station while not logged in
	 */
	@Test
	public void blockStationNotLoggedInTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		Attendant a = new Attendant("12");
		boolean actual = a.blockStation(control);
		assertFalse(actual);
		boolean blocked = control.isBlocked();
		assertFalse(blocked);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests unblocking the station while not logged in
	 */
	@Test
	public void unblockStationNotLoggedInTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		Attendant a = new Attendant("12");
		boolean actual = a.unBlockStation(control);
		assertFalse(actual);
		boolean blocked = control.isBlocked();
		assertFalse(blocked);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests unblocking null software
	 */
	@Test (expected = NullPointerException.class)
	public void unblockStationNullTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		Attendant a = new Attendant("12");
		a.unBlockStation(null);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests blocking null software
	 */
	@Test (expected = NullPointerException.class)
	public void blockStationNullTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("12");
		Attendant a = new Attendant("12");
		a.blockStation(null);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding ink to null software
	 */
	@Test (expected = NullPointerException.class)
	public void addInkNullTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		a.addInkToPrinter(null, 10);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests adding paper to null software
	 */
	@Test (expected = NullPointerException.class)
	public void addPaperNullTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		a.addPaperToPrinter(null, 10);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests removing null item from scanned items
	 */
	@Test (expected = NullPointerException.class)
	public void removeNullItem() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		a.removeItemFromPurchase(control, null);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests removing item that is not scanned
	 */
	@Test (expected = IndexOutOfBoundsException.class)
	public void removeItemNotScannedTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "Test", new BigDecimal("12"));
		control.addProduct(bp, 1);
		BarcodedItem bi = control.getScannedItems().get(0);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests removing item from scanned items
	 */
	@Test
	public void removeItemTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "Test", new BigDecimal("12"));
		control.addProduct(bp, 1);
		control.scanItem(b, 12);
		BarcodedItem bi = control.getScannedItems().get(0);
		assertEquals(control.getScannedItems().size(), 1);
		a.removeItemFromPurchase(control, bi);
		assertEquals(control.getScannedItems().size(), 0);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests removing item while not logged in
	 */
	@Test
	public void removeItemNotLoggedInTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		Attendant a = new Attendant("1234");
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "Test", new BigDecimal("12"));
		control.addProduct(bp, 1);
		control.scanItem(b, 12);
		BarcodedItem bi = control.getScannedItems().get(0);
		boolean removed = a.removeItemFromPurchase(control, bi);
		assertFalse(removed);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests removing item from null software
	 */
	@Test (expected = NullPointerException.class)
	public void removeItemNullSoftware() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = null;
		control.registerAttendant("1234");
		Attendant a = new Attendant("1234");
		Barcode b = new Barcode("1234");
		BarcodedProduct bp = new BarcodedProduct(b, "Test", new BigDecimal("12"));
		control.addProduct(bp, 1);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests approving weight discrepancy
	 */
	@Test
	public void approveDiffTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		a.approveWeightDiscrepency(control, 12);
		assertEquals(control.getMaxWeightDiff(), 12, 0);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests approving weight discrepancy while not logged in
	 */
	@Test
	public void approveDiffNotLoggedInTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		Attendant a = new Attendant("1234");
		boolean value = a.approveWeightDiscrepency(control, 12);
		assertFalse(value);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests approving weight discrepancy on null software
	 */
	@Test (expected = NullPointerException.class)
	public void approveDiffNullSoftwareTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = null;
		control.registerAttendant("1234");
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	
	/**
	 * Tests approving weight discrepancy to a max of zero difference
	 */
	@Test
	public void approveZeroDiffTest() {
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		control.registerAttendant("1234");
		control.attendantLogin("1234");
		Attendant a = new Attendant("1234");
		a.approveWeightDiscrepency(control, 0);
		assertEquals(control.getMaxWeightDiff(), 0, 0);
		AttendantDatabase.REGISTERED_ATTENDANTS.clear();
	}
	

}
