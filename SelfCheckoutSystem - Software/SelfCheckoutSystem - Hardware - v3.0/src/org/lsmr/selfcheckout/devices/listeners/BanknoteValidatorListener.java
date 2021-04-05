package org.lsmr.selfcheckout.devices.listeners;

import java.util.Currency;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteValidator;

/**
 * Listens for events emanating from a banknote validator.
 */
public interface BanknoteValidatorListener extends AbstractDeviceListener {
	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be valid.
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 * @param currency
	 *            The kind of currency of the inserted banknote.
	 * @param value
	 *            The value of the inserted banknote.
	 */
	void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value);

	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be invalid.
	 * 
	 * @param validator
	 *            The device on which the event occurred.
	 */
	void invalidBanknoteDetected(BanknoteValidator validator);
}
