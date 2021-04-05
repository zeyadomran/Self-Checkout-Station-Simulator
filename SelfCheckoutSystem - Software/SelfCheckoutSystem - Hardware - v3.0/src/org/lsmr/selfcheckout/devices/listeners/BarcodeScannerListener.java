package org.lsmr.selfcheckout.devices.listeners;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.BarcodeScanner;

/**
 * Listens for events emanating from a barcode scanner.
 */
public interface BarcodeScannerListener extends AbstractDeviceListener {
	/**
	 * An event announcing that the indicated barcode has been successfully scanned.
	 * 
	 * @param barcodeScanner
	 *            The device on which the event occurred.
	 * @param barcode
	 *            The barcode that was read by the scanner.
	 */
	void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode);

}
