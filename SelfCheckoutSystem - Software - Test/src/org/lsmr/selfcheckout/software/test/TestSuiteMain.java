package org.lsmr.selfcheckout.software.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)				
@Suite.SuiteClasses({
	SelfCheckoutSoftwareTest.class,
	ScanItemTest.class,
	AddToBaggingAreaTest.class,
	PayWithCashTest.class,
	PayWithCoinTest.class,
	MemberTest.class,
	ChangeTest.class,
	AddOwnBagToBaggingAreaTest.class,
	ReceiptTest.class,
	PayWithCardTest.class,
	FailToPlaceItemInBaggingAreaTest.class,
	SystemTest.class,
	ReceiptPrinterManagementTest.class,
	PluProductTest.class,
	EmptyStorageTest.class,
	LoadBanknoteDispensersTest.class,
	LoadCoinDispensersTest.class,
	AttendantTest.class,
	ShutDownStartUpTest.class,
	AttendantLooksUpProductTest.class,
	CheckWeightTest.class,
	GUITest.class
})
public class TestSuiteMain {}
