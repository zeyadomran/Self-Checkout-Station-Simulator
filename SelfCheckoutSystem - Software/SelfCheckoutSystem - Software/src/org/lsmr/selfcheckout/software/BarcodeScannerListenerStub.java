package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;

public class BarcodeScannerListenerStub implements BarcodeScannerListener {
	
	private boolean disabled = false;
	private Barcode latestScannedBarcode = null;
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) { this.disabled = false; }

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) { this.disabled = true; }

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) { this.latestScannedBarcode = barcode; }
	
	/**
	 * Get the status of the device.
	 * @return The status of the device.
	 */
	public boolean getIsDisabled() { return this.disabled; }
	
	/**
	 * Get the barcode of the latest scanned item.
	 * @return The latest scanned barcode.
	 */
	public Barcode getLatestScannedBarcode() { return this.latestScannedBarcode; }
	
}
