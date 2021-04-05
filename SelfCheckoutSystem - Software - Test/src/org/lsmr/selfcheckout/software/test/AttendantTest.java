package org.lsmr.selfcheckout.software.test;

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
	}
	

}
