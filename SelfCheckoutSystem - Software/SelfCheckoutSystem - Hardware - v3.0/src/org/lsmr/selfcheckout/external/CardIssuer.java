package org.lsmr.selfcheckout.external;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.lsmr.selfcheckout.devices.SimulationException;

/**
 * Represents external companies that issue cards and authorize payments made
 * with them.
 * <p>
 * This class uses a transactional model that is simpler than the real thing: to
 * debit a purchase, a hold is first placed on the amount and then the
 * transaction is posted. It does not quite work like that in the real world.
 */
public class CardIssuer {
	private String name;

	/**
	 * Create a card provider.
	 * 
	 * @param name
	 *            The company's name.
	 * @throws SimulationException
	 *             If name is null.
	 */
	public CardIssuer(String name) {
		if(name == null)
			throw new SimulationException(new NullPointerException("name is null"));

		this.name = name;
	}

	public boolean block(String cardNumber) {
		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			cr.isBlocked = true;
		}

		return true;
	}

	public boolean unblock(String cardNumber) {
		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			cr.isBlocked = false;
		}

		return true;
	}

	private class CardRecord {
		boolean isBlocked = false;
		String number;
		String cardholder;
		Calendar expiry;
		String ccv;
		BigDecimal available;
		Map<Integer, BigDecimal> holds = new HashMap<>();

		synchronized BigDecimal holdsTotal() {
			BigDecimal total = BigDecimal.ZERO;

			for(BigDecimal hold : holds.values())
				total = total.add(hold);

			return total;
		}
	}

	private HashMap<String, CardRecord> database = new HashMap<>();

	private boolean isValidCardNumber(String number) {
		if(number == null)
			return false;

		try {
			Long.parseLong(number);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidCardholderName(String name) {
		if(name == null)
			return false;

		if(name.equals(""))
			return false;

		return true;
	}

	private boolean isValidCCV(String ccv) {
		if(ccv == null)
			return false;

		if(ccv.length() != 3)
			return false;

		try {
			Integer.parseInt(ccv);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidDate(Calendar date) {
		if(date == null)
			return false;

		if(date.before(Calendar.getInstance()))
			return false;

		return true;
	}

	/**
	 * Adds information about a card to the company's database. They do this when
	 * they create the card.
	 * 
	 * @param number
	 *            The card number.
	 * @param cardholder
	 *            The name of the cardholder.
	 * @param expiry
	 *            The expiry date of the card. Must be in the future.
	 * @param amount
	 *            For a credit card, this represents the credit limit. For a debit
	 *            card, this is how much money is available. (Yes, it is a
	 *            simplistic simulation.)
	 */
	public void addCardData(String number, String cardholder, Calendar expiry, String ccv, BigDecimal amount) {
		if(!isValidCardNumber(number))
			throw new SimulationException(new IllegalArgumentException("The card number is not valid."));

		if(!isValidCardholderName(cardholder))
			throw new SimulationException(new IllegalArgumentException("The cardholder name is not valid."));

		if(amount == null)
			throw new SimulationException(new NullPointerException("amount is null"));

		if(amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new SimulationException(new NullPointerException("amount must be positive."));

		if(!isValidCCV(ccv))
			throw new SimulationException(new IllegalArgumentException("The CCV is not valid."));

		if(!isValidDate(expiry))
			throw new SimulationException(new IllegalArgumentException("The expiry date is not valid"));

		if(database.containsKey(number))
			throw new SimulationException("The number " + number + " is already in use.");

		CardRecord cr = new CardRecord();
		cr.number = number;
		cr.cardholder = cardholder;
		cr.expiry = expiry;
		cr.ccv = ccv;
		cr.available = amount;

		database.put(number, cr);
	}

	private static final ThreadLocalRandom random = ThreadLocalRandom.current();

	/**
	 * Authorizes a hold on the indicated amount for the card with the indicated
	 * number. If successful, the hold is kept indefinitely until either released
	 * explicitly or the transaction is completed.
	 * 
	 * @param cardNumber
	 *            The number of the card on which to place a hold.
	 * @param amount
	 *            The amount to hold.
	 * @return -1 if the hold failed; otherwise, a non-negative integer representing
	 *             a code to reference the hold.
	 */
	public int authorizeHold(String cardNumber, BigDecimal amount) {
		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return -1;

		synchronized(cr) {
			if(cr.isBlocked)
				return -1;
				
			if(cr.available.subtract(cr.holdsTotal()).compareTo(amount) >= 0) {
				Integer holdNumber;

				while(true) {
					holdNumber = random.nextInt(0, Integer.MAX_VALUE);
					if(!cr.holds.containsKey(holdNumber))
						break;
				}

				cr.holds.put(holdNumber, amount);

				return holdNumber;
			}
		}

		return -1;
	}

	public boolean releaseHold(String cardNumber, int holdNumber) {
		if(holdNumber < 0)
			return false;

		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			if(cr.isBlocked)
				return false;

			cr.holds.remove(holdNumber);
		}

		return true;
	}

	public boolean postTransaction(String cardNumber, int holdNumber, BigDecimal actualAmount) {
		if(holdNumber < 0)
			return false;

		CardRecord cr = database.get(cardNumber);

		if(cr == null)
			return false;

		synchronized(cr) {
			if(cr.isBlocked)
				return false;
			
			BigDecimal heldAmount = cr.holds.get(holdNumber);

			if(heldAmount == null)
				return false;
			
			if(heldAmount.compareTo(actualAmount) >= 0) {
				cr.available = cr.available.subtract(actualAmount);
				cr.holds.remove(holdNumber);
				return true;
			}
			else {
				cr.holds.remove(holdNumber);
				return false;
			}
		}
	}
}
