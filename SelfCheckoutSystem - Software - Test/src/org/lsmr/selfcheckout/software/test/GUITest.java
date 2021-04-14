package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class GUITest {
    SelfCheckoutSoftware control = null;

    /* Initialize a new SelfCheckoutSoftware object before each test. */
    @Before
    public void init() {
        Currency c = Currency.getInstance(Locale.CANADA);
        int[] noteDenom = {5, 10, 20, 50, 100};
        BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
        SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
        control = new SelfCheckoutSoftware(s);
    }

    @Test
    public void guiTest() {
        control.startGUI();
        control.disableGUI();
        control.loadMainGUI();
        control.disableGUI();
        control.changeToAttendantGUI();
        control.disableGUI();
        control.changeToCheckOutGUI();
        assertFalse(control.disableGUI());
    }

    /* Clear SelfCheckoutSoftware instance */
    @After
    public void Teardown() {
        control = null;
    }
}
