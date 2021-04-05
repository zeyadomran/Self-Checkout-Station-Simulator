package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.devices.listeners.ReceiptPrinterListener;

/**
 * Represents printers used for printing receipts. A printer has a finite amount
 * of paper (measured in lines that can be printed) and ink (measured in
 * characters that can be printed).
 * <p>
 * Since this is a simulation, each character is assumed to require the same
 * amount of ink (except blanks and newlines) and the font size is fixed.
 * </p>
 */
public class ReceiptPrinter extends AbstractDevice<ReceiptPrinterListener> {
	public static final int MAXIMUM_INK = 1 << 20;
	public static final int MAXIMUM_PAPER = 1 << 10;
	private int charactersOfInkRemaining = 0;
	private int linesOfPaperRemaining = 0;
	private StringBuilder sb = new StringBuilder();
	private int charactersOnCurrentLine = 0;

	/**
	 * Represents the maximum number of characters that can fit on one line of the
	 * receipt. This is a simulation, so the font is assumed monospaced and of fixed
	 * size.
	 */
	public final static int CHARACTERS_PER_LINE = 60;

	/**
	 * Creates a receipt printer.
	 */
	public ReceiptPrinter() {}

	/**
	 * Prints a single character to the receipt. Whitespace characters are ignored,
	 * with the exception of ' ' (blank) and '\n', which signals to move to the
	 * start of the next line.
	 * 
	 * @param c
	 *            The character to print.
	 * @throws SimulationException
	 *             If there is no ink or no paper in the printer.
	 * @throws SimulationException
	 *             If the extra character would spill off the end of the line.
	 */
	public void print(char c) {
		if(c == '\n') {
			--linesOfPaperRemaining;
			charactersOnCurrentLine = 0;
		}
		else if(c != ' ' && Character.isWhitespace(c))
			return;
		else if(charactersOnCurrentLine == CHARACTERS_PER_LINE)
			throw new SimulationException("The line is too long. Add a newline");
		else if(linesOfPaperRemaining == 0)
			throw new SimulationException("There is no paper in the printer.");
		else
			charactersOnCurrentLine++;

		if(!Character.isWhitespace(c)) {
			if(charactersOfInkRemaining == 0)
				throw new SimulationException("There is no ink in the printer");

			charactersOfInkRemaining--;
		}

		sb.append(c);

		if(charactersOfInkRemaining == 0)
			notifyOutOfInk();

		if(linesOfPaperRemaining == 0)
			notifyOutOfPaper();
	}

	/**
	 * The receipt is finished printing, so cut it so that the customer can easily
	 * remove it. Failure to cut the paper means that the receipt will not be
	 * retrievable by the customer.
	 */
	public void cutPaper() {
		lastReceipt = sb.toString();
	}

	private String lastReceipt = null;

	/**
	 * Simulates the customer removing the receipt. Failure to cut the receipt
	 * first, or to always remove the receipt means that the customer will end up
	 * with other customers' receipts too!
	 * 
	 * @return The receipt if it has been cut; otherwise, null.
	 */
	public String removeReceipt() {
		String receipt = lastReceipt;

		if(lastReceipt != null) {
			lastReceipt = null;
			sb = new StringBuilder();
		}

		return receipt;
	}

	/**
	 * Adds ink to the printer. Simulates a human doing the adding.
	 * 
	 * @param quantity
	 *            The quantity of characters-worth of ink to add.
	 * @throws SimulationException
	 *             If the quantity is negative.
	 * @throws SimulationException
	 *             If the total of the existing ink plus the added quantity is
	 *             greater than the printer's capacity.
	 */
	public void addInk(int quantity) {
		if(quantity < 0)
			throw new SimulationException("Are you trying to remove ink?");

		if(charactersOfInkRemaining + quantity > MAXIMUM_INK)
			throw new SimulationException("You spilled a bunch of ink!");

		if(quantity > 0) {
			charactersOfInkRemaining += quantity;
			notifyInkAdded();
		}
	}

	/**
	 * Adds paper to the printer. Simulates a human doing the adding.
	 * 
	 * @param quantity
	 *            The quantity of lines-worth of paper to add.
	 * @throws SimulationException
	 *             If the quantity is negative.
	 * @throws SimulationException
	 *             If the total of the existing paper plus the added quantity is
	 *             greater than the printer's capacity.
	 */
	public void addPaper(int units) {
		if(units < 0)
			throw new SimulationException("Are you trying to remove paper?");

		if(linesOfPaperRemaining + units > MAXIMUM_PAPER)
			throw new SimulationException("You may have broken the printer, jamming so much in there!");

		if(units > 0) {
			linesOfPaperRemaining += units;
			notifyPaperAdded();
		}
	}

	private void notifyOutOfInk() {
		for(ReceiptPrinterListener l : listeners)
			l.outOfInk(this);
	}

	private void notifyInkAdded() {
		for(ReceiptPrinterListener l : listeners)
			l.inkAdded(this);
	}

	private void notifyOutOfPaper() {
		for(ReceiptPrinterListener l : listeners)
			l.outOfPaper(this);
	}

	private void notifyPaperAdded() {
		for(ReceiptPrinterListener l : listeners)
			l.paperAdded(this);
	}
}
