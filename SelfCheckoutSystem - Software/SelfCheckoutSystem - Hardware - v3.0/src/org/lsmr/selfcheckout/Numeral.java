package org.lsmr.selfcheckout;

/**
 * Represents numerals (i.e., number characters) referred to as "Arabic numbers"
 * in English.
 */
public enum Numeral {
	one((byte)1), two((byte)2), three((byte)3), four((byte)4), five((byte)5), six((byte)6), seven((byte)7),
	eight((byte)8), nine((byte)9), zero((byte)0);

	private byte value;

	private Numeral(byte value) {
		this.value = value;
	}

	/**
	 * Obtains the numeric value of the numeral.
	 * 
	 * @return The numeric value of the numeral.
	 */
	public byte getValue() {
		return value;
	}

	/**
	 * Converts a number between 0 and 9 into the corresponding numeral.
	 * 
	 * @param number
	 *            The number to convert. It must be between 0 and 9, inclusive.
	 * @return {@link IllegalDigitException} If the number is less than 0 or greater than 9.
	 */
	public static Numeral valueOf(byte number) {
		switch(number) {
		case 0:
			return zero;
		case 1:
			return one;
		case 2:
			return two;
		case 3:
			return three;
		case 4:
			return four;
		case 5:
			return five;
		case 6:
			return six;
		case 7:
			return seven;
		case 8:
			return eight;
		case 9:
			return nine;
		default:
			throw new IllegalDigitException("The number " + number + " does not correspond to a numeral.");
		}
	}
}
