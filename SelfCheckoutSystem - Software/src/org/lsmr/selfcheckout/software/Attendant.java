package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.BarcodedItem;

public class Attendant {
	
	private String attendantID;
	
	public Attendant(String attendantID) 
	{
		this.setAttendantID(attendantID);
	}

	public String getAttendantID() {
		return attendantID;
	}

	public void setAttendantID(String attendantID) {
		this.attendantID = attendantID;
	}
	
	/*
	 * enable all external devices the user can use like the scanners, coin slots, card readers, etc... 
	 */
	public boolean attendantStartUpStation(SelfCheckoutSoftware control)
	{
		if(control.getattendantLoggedIn())
		{
			control.getStation().scale.enable();
			control.getStation().baggingArea.enable();
			control.getStation().handheldScanner.enable();
			control.getStation().mainScanner.enable();
			control.getStation().cardReader.enable();
			control.getStation().screen.enable();
			control.getStation().printer.enable();
			control.getStation().coinSlot.enable();
			control.getStation().banknoteInput.enable();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Disable all external devices the user can use like the scanners, coin slots, card readers, etc... 
	 */
	public boolean attendantShutDownStation(SelfCheckoutSoftware control)
	{
		if(control.getattendantLoggedIn())
		{
			control.getStation().scale.disable();
			control.getStation().baggingArea.disable();
			control.getStation().handheldScanner.disable();
			control.getStation().mainScanner.disable();
			control.getStation().cardReader.disable();
			control.getStation().screen.disable();
			control.getStation().printer.disable();
			control.getStation().coinSlot.disable();
			control.getStation().banknoteInput.disable();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Attendant adds ink to the receipt printer.
	 * 
	 * @param the control software of the station
	 * @param the amount of ink to add
	 * @return Whether adding ink was successful.
	 */
	public boolean addInkToPrinter(SelfCheckoutSoftware control, int amount) {
		if (control.getattendantLoggedIn()) {
			control.addInkToPrinter(amount);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Attendant adds paper to the receipt printer.
	 * 
	 * @param The control software of the station.
	 * @param The amount of paper to add.
	 * @return Whether adding paper was successful.
	 */
	public boolean addPaperToPrinter(SelfCheckoutSoftware control, int amount) {
		if (control.getattendantLoggedIn()) {
			control.addPaperToPrinter(amount);
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Attendant blocks the station
	 * @param The control software of the station.
	 * @return Whether blocking the station was successful
	 */
	public boolean blockStation(SelfCheckoutSoftware control) {
		if (control.getattendantLoggedIn()) {
			return control.setBlocked(true);
		}
		return false;
	}
	
	/**
	 * Attendant unblocks the station
	 * @param The control software of the station.
	 * @return Whether unblocking the station was successful
	 */
	public boolean unBlockStation(SelfCheckoutSoftware control) {
		if (control.getattendantLoggedIn()) {
			return control.setBlocked(false);
		}
		return false;
	}
	
	/**
	 * Attendant removes item from purchase
	 * @param The control software of the station.
	 * @param Barcoded item to remove
	 * @return Whether removing the item was successful.
	 */
	public boolean removeItemFromPurchase(SelfCheckoutSoftware control, BarcodedItem item) {
		if (control.getattendantLoggedIn()) {
			return control.removeScannedItem(item);
		}
		else {
			return false;
		}
	}
	
	/**
	 * Attendant approves weight difference by setting a new maximum weight discrepancy
	 * @param The control software of the station.
	 * @param weight difference to set
	 * @return Whether setting new weight difference was successful.
	 */
	public boolean approveWeightDiscrepency(SelfCheckoutSoftware control, double newMaxDiff) {
		if (control.getattendantLoggedIn()) {
			return control.setMaxWeightDiff(newMaxDiff);
		}
		return false;
	}

}
