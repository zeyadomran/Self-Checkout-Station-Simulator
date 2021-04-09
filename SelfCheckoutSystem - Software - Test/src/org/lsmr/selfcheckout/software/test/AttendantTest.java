package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.lsmr.selfcheckout.software.Attendant;
import org.lsmr.selfcheckout.software.AttendantDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class AttendantTest {
	Currency c = Currency.getInstance(Locale.CANADA);
	int[] noteDenom = {5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
	SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
	
    
	@Test
	public void login() throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutSoftware control = new SelfCheckoutSoftware(s);
		//Attendant employee = new Attendant("12345");
		//AttendantDatabase.REGISTERED_ATTENDANTS.put(employee.getAttendantID(), employee);
		control.registerAttendant("12345");
		control.attendantLogin("12345");
		System.out.println(control.getattendantLoggedIn());
		control.shutDownStation();
		System.out.println(control.isShutDown());
		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(new Coin(new BigDecimal("0.05"), c));
		control.startUpStation();
		System.out.println(control.isShutDown());
		control.payWithCoin(coins);
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
	
	

}
