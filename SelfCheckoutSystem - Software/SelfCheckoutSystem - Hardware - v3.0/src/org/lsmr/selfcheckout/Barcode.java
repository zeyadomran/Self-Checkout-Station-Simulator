package org.lsmr.selfcheckout;

import java.util.ArrayList;
import java.util.Arrays;

import org.lsmr.selfcheckout.devices.SimulationException;

/**
 * Represents a barcode value (not the graphic barcode itself). Real-world
 * barcodes are a sequence of digits, so that is what is modelled here.
 */
public class Barcode {
	private Numeral[] digits;

	/**
	 * Constructs a barcode from a string of digits.
	 * 
	 * @param code
	 *            A string of digits.
	 * @throws SimulationException
	 *             If any character in the input is not a digit between 0 and 9,
	 *             inclusive.
	 * @throws SimulationException
	 *             If the code is null
	 * @throws SimulationException
	 *             If the code's length is &lt;1 or &gt;48.
	 */
	public Barcode(String code) {
		if(code == null)
			throw new SimulationException(new NullPointerException("code is null"));

		char[] charArray = code.toCharArray();
		digits = new Numeral[charArray.length];

		if(code.length() < 1)
			throw new SimulationException(
				new IllegalArgumentException("A barcode cannot contain less than one digit."));
	
		if(code.length() > 48)
			throw new SimulationException(
				new IllegalArgumentException("A barcode cannot contain more than forty-eight digits."));

		for(int i = 0; i < charArray.length; i++)
			try {
				digits[i] = Numeral.valueOf((byte)Character.digit(charArray[i], 10));
			}
			catch(IllegalDigitException e) {
				throw new SimulationException(e);
			}
	}

	/**
	 * Gets the count of digits in this code.
	 * 
	 * @return The count of digits.
	 */
	public int digitCount() {
		return digits.length;
	}

	/**
	 * Gets the digit at the indicated index within the code.
	 * 
	 * @param index
	 *            The index of the digit, &ge;0 and &lt;count.
	 * @return The digit at the indicated index.
	 * @throws SimulationException
	 *             If the index is outside the legal range.
	 */
	public Numeral getDigitAt(int index) {
		try {
			return digits[index];
		}
		catch(IndexOutOfBoundsException e) {
			throw new SimulationException(e);
		}
	}

	@Override
	public String toString() {
		char[] characters = new char[digits.length];

		for(int i = 0; i < digits.length; i++)
			characters[i] = Character.forDigit(digits[i].getValue(), 10);

		return new String(characters);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Barcode) {
			Barcode other = (Barcode)object;

			if(other.digits.length != digits.length)
				return false;

			for(int i = 0; i < digits.length; i++)
				if(!digits[i].equals(other.digits[i]))
					return false;

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(digits);
	}
}