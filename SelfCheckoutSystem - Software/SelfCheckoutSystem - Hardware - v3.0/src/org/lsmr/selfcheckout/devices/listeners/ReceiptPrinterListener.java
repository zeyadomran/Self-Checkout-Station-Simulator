package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.devices.ReceiptPrinter;

/**
 * Listens for events emanating from a receipt printer.
 */
public interface ReceiptPrinterListener extends AbstractDeviceListener {
	/**
	 * Announces that the indicated printer is out of paper.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void outOfPaper(ReceiptPrinter printer);

	/**
	 * Announces that the indicated printer is out of ink.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void outOfInk(ReceiptPrinter printer);

	/**
	 * Announces that paper has been added to the indicated printer.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void paperAdded(ReceiptPrinter printer);

	/**
	 * Announces that ink has been added to the indicated printer.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void inkAdded(ReceiptPrinter printer);
}
