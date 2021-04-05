package org.lsmr.selfcheckout;

import java.util.ArrayList;
import java.util.Arrays;

import org.lsmr.selfcheckout.devices.SimulationException;

/**
 * Represents a PLU value, a sequence of digits that, in principle, could lie
 * anywhere in the range 0000-99999. Specific subranges are reserved for specific
 * purposes in the real world, but we will not worry about that here.
 */
public class PriceLookupCode {
	private Numeral[] numerals;

	/**
	 * Constructs a PLU code from a string of numerals. There must be at least 4
	 * digits and at most 5.
	 * 
	 * @param code
	 *            A string of digits.
	 * @throws SimulationException
	 *             If any character in the input is not a digit between 0 and 9,
	 *             inclusive.
	 * @throws SimulationException
	 *             If the code contains less than 4 digits or more than 5 digits.
	 * @throws NullPointerException
	 *             If code is null.
	 */
	public PriceLookupCode(String code) {
		if(code == null)
			throw new SimulationException(new NullPointerException("code is null"));

		char[] charArray = code.toCharArray();
		numerals = new Numeral[charArray.length];

		if(code.length() > 5)
			throw new SimulationException(
				new IllegalArgumentException("The code cannot contain more than five digits."));

		if(code.length() < 4)
			throw new SimulationException(
				new IllegalArgumentException("The code cannot contain less than four digits."));

		for(int i = 0; i < charArray.length; i++) {
			try {
				numerals[i] = Numeral.valueOf((byte)Character.digit(charArray[i], 10));
			}
			catch(IllegalDigitException e) {
				throw new SimulationException(e);
			}
		}
	}

	/**
	 * Gets the count of numerals in this code.
	 * 
	 * @return The count of numerals.
	 */
	public int numeralCount() {
		return numerals.length;
	}

	/**
	 * Gets the numeral at the indicated index within the code.
	 * 
	 * @param index
	 *            The index of the numeral, &ge;0 and &lt;count.
	 * @return The numeral at the indicated index.
	 * @throws SimulationException
	 *             If the index is outside the legal range.
	 */
	public Numeral getNumeralAt(int i) {
		try {
			return numerals[i];
		}
		catch(IndexOutOfBoundsException e) {
			throw new SimulationException(e);
		}
	}

	@Override
	public String toString() {
		char[] characters = new char[numerals.length];

		for(int i = 0; i < numerals.length; i++)
			characters[i] = Character.forDigit(numerals[i].getValue(), 10);

		return new String(characters);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof PriceLookupCode) {
			PriceLookupCode other = (PriceLookupCode)object;

			if(other.numerals.length != numerals.length)
				return false;

			for(int i = 0; i < numerals.length; i++)
				if(!numerals[i].equals(other.numerals[i]))
					return false;

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(numerals);
	}
}
