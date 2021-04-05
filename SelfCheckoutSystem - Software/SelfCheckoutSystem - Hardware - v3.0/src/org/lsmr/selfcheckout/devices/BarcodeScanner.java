package org.lsmr.selfcheckout.devices;

import java.util.Random;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;

/**
 * A complex device hidden behind a simple simulation. They can scan and that is
 * about all.
 */
public class BarcodeScanner extends AbstractDevice<BarcodeScannerListener> {
	/**
	 * Create a barcode scanner.
	 */
	public BarcodeScanner() {}

	private Random random = new Random();
	private static final int PROBABILITY_OF_FAILED_SCAN = 10; /* out of 100 */

	/**
	 * Simulates the customer's action of scanning an item. The result of the scan
	 * is only announced to any registered listeners.
	 * 
	 * @param item
	 *            The item to scan. Of course, it will only work if the item has a
	 *            barcode, and maybe not even then.
	 * @throws SimulationException
	 *             If item is null.
	 */
	public void scan(Item item) {
		if(isDisabled())
			return; // silently ignore it

		if(item == null)
			throw new SimulationException(new NullPointerException("item is null"));

		if(item instanceof BarcodedItem && random.nextInt(100) >= PROBABILITY_OF_FAILED_SCAN)
			notifyBarcodeScanned(((BarcodedItem)item).getBarcode());

		// otherwise, silently ignore it
	}

	private void notifyBarcodeScanned(Barcode barode) {
		for(BarcodeScannerListener l : listeners)
			l.barcodeScanned(this, barode);
	}
}
