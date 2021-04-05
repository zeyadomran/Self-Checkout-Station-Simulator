package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ReceiptPrinterListener;

public class ReceiptPrinterListenerStub implements ReceiptPrinterListener {

	private boolean paperAdded;
	private boolean inkAdded;

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outOfPaper(ReceiptPrinter printer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outOfInk(ReceiptPrinter printer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paperAdded(ReceiptPrinter printer)
	{
		paperAdded = true;
	}

	@Override
	public void inkAdded(ReceiptPrinter printer) {
		inkAdded = true;
	}

	public boolean isInkAdded() {
		return inkAdded;
	}


	public boolean isPaperAdded() {
		return paperAdded;
	}

}


