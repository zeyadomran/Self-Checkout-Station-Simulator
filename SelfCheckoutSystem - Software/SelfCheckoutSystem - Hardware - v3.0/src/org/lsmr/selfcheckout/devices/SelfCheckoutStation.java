package org.lsmr.selfcheckout.devices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioSystem;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;

/**
 * Simulates the overall self-checkout station.
 * <p>
 * A self-checkout possesses the following units of hardware that the customer
 * can see and interact with:
 * <ul>
 * <li>two electronic scales (one in the bagging area and one near the scanner), 
 * with a configurable maximum weight before it overloads;</li>
 * <li>one touch screen;</li>
 * <li>one receipt printer;</li>
 * <li>one card reader;</li>
 * <li>two scanners (the main one and the handheld one);</li>
 * <li>one input slot for banknotes;</li>
 * <li>one output slot for banknotes;</li>
 * <li>one input slot for coins;</li>
 * <li>one output tray for coins; and,</li>
 * <li>one speaker for audio output (note: you should directly use the
 * {@link AudioSystem} class, if you want to produce sounds).</li>
 * </ul>
 * </p>
 * <p>
 * In addition, these units of hardware are accessible to personnel with a key
 * to unlock the front of the station:
 * <li>one banknote storage unit, with configurable capacity;</li>
 * <li>one or more banknote dispensers, one for each supported denomination of
 * banknote, as configured;</li>
 * <li>one coin storage unit, with configurable capacity; and,</li>
 * <li>one or more coin dispensers, one for each supported denomination of coin,
 * as configured.</li>
 * </ul>
 * </p>
 * <p>
 * And finally, there are certain, additional units of hardware that would only
 * be accessible to someone with the appropriate tools (like a screwdriver,
 * crowbar, or sledge hammer):
 * <ul>
 * <li>one banknote validator; and</li>
 * <li>one coin validator.</li>
 * </ul>
 * </p>
 * <p>
 * Many of these devices are interconnected, to permit coins or banknotes to
 * pass between them. Specifically:
 * <ul>
 * <li>the coin slot is connected to the coin validator (this is a
 * one-directional chain of devices);</li>
 * <li>the coin validator is connected to each of the coin dispensers (i.e., the
 * coin dispensers can be replenished with coins entered by customers), to the
 * coin storage unit (for any overflow coins that do not fit in the dispensers),
 * and to the coin tray for any rejected coins either because the coins are
 * invalid or because even the overflow storage unit is full (this is a
 * one-directional chain of devices);
 * <li>each coin dispenser is connected to the coin tray, to provide change
 * (this is a one-directional chain of devices);</li>
 * <li>the banknote input slot is connected to the banknote validator (this is a
 * <b>two</b>-directional chain of devices as an entered banknotes that are
 * rejected by the validator can be returned to the customer);</li>
 * <li>the banknote validator is connected to the banknote storage unit (this is
 * a one-directional chain of devices); and,</li>
 * <li>each banknote dispenser is connected to the output banknote slot; these
 * dispensers cannot be replenished by banknotes provided by customers (this is
 * a one-directional chain of devices).</li>
 * </ul>
 * </p>
 * <p>
 * All other functionality of the system must be performed in software,
 * installed on the self-checkout station through custom listener classes
 * implementing the various listener interfaces provided.
 * </p>
 * <p>
 * Note that banknote denominations are required to be positive integers, while
 * coin denominations are positive decimal values ({@link BigDecimal} is used
 * for the latter to avoid roundoff problems arising from floating-point
 * operations).
 */
public class SelfCheckoutStation {
	public final ElectronicScale scale;
	public final ElectronicScale baggingArea;
	public final TouchScreen screen;
	public final ReceiptPrinter printer;
	public final CardReader cardReader;
	public final BarcodeScanner mainScanner;
	public final BarcodeScanner handheldScanner;

	public final BanknoteSlot banknoteInput, banknoteOutput;
	public final BanknoteValidator banknoteValidator;
	public final BanknoteStorageUnit banknoteStorage;
	public final static int BANKNOTE_STORAGE_CAPACITY = 1000;
	public final int[] banknoteDenominations;
	public final Map<Integer, BanknoteDispenser> banknoteDispensers;
	public final static int BANKNOTE_DISPENSER_CAPACITY = 100;

	public final CoinSlot coinSlot;
	public final CoinValidator coinValidator;
	public final CoinStorageUnit coinStorage;
	public static final int COIN_STORAGE_CAPACITY = 1000;
	public final List<BigDecimal> coinDenominations;
	public final Map<BigDecimal, CoinDispenser> coinDispensers;
	public static final int COIN_DISPENSER_CAPACITY = 200;
	public final CoinTray coinTray;
	public static final int COIN_TRAY_CAPACITY = 20;

	/**
	 * Creates a self-checkout station.
	 * 
	 * @param currency
	 *            The kind of currency permitted.
	 * @param banknoteDenominations
	 *            The set of denominations (i.e., $5, $10, etc.) to accept.
	 * @param coinDenominations
	 *            The set of denominations (i.e., $0.05, $0.10, etc.) to accept.
	 * @param scaleMaximumWeight
	 *            The most weight that can be placed on the scale before it
	 *            overloads.
	 * @param scaleSensitivity
	 *            Any weight changes smaller than this will not be detected or
	 *            announced.
	 * @throws SimulationException
	 *             If any argument is null or negative.
	 * @throws SimulationException
	 *             If the number of banknote or coin denominations is &lt;1.
	 */
	public SelfCheckoutStation(Currency currency, int[] banknoteDenominations, BigDecimal[] coinDenominations,
		int scaleMaximumWeight, int scaleSensitivity) {
		if(currency == null || banknoteDenominations == null || coinDenominations == null)
			throw new SimulationException(new NullPointerException("No argument may be null."));

		if(scaleMaximumWeight <= 0)
			throw new SimulationException(new IllegalArgumentException("The scale's maximum weight must be positive."));

		if(scaleSensitivity <= 0)
			throw new SimulationException(new IllegalArgumentException("The scale's sensitivity must be positive."));

		if(banknoteDenominations.length == 0)
			throw new SimulationException(
				new IllegalArgumentException("There must be at least one allowable banknote denomination defined."));

		if(coinDenominations.length == 0)
			throw new SimulationException(
				new IllegalArgumentException("There must be at least one allowable coin denomination defined."));

		// Create the devices.
		scale = new ElectronicScale(scaleMaximumWeight, scaleSensitivity);
		baggingArea = new ElectronicScale(scaleMaximumWeight, scaleSensitivity);
		screen = new TouchScreen();
		printer = new ReceiptPrinter();
		cardReader = new CardReader();
		mainScanner = new BarcodeScanner();
		handheldScanner = new BarcodeScanner();

		this.banknoteDenominations = banknoteDenominations;
		banknoteInput = new BanknoteSlot(false);
		banknoteValidator = new BanknoteValidator(currency, banknoteDenominations);
		banknoteStorage = new BanknoteStorageUnit(BANKNOTE_STORAGE_CAPACITY);
		banknoteOutput = new BanknoteSlot(true);

		banknoteDispensers = new HashMap<>();

		for(int i = 0; i < banknoteDenominations.length; i++)
			banknoteDispensers.put(banknoteDenominations[i], new BanknoteDispenser(BANKNOTE_DISPENSER_CAPACITY));

		this.coinDenominations = Arrays.asList(coinDenominations);
		coinSlot = new CoinSlot();
		coinValidator = new CoinValidator(currency, this.coinDenominations);
		coinStorage = new CoinStorageUnit(COIN_STORAGE_CAPACITY);
		coinTray = new CoinTray(COIN_TRAY_CAPACITY);

		coinDispensers = new HashMap<>();

		for(int i = 0; i < coinDenominations.length; i++)
			coinDispensers.put(coinDenominations[i], new CoinDispenser(COIN_DISPENSER_CAPACITY));

		// Hook up everything.
		interconnect(banknoteInput, banknoteValidator);
		interconnect(banknoteValidator, banknoteStorage);

		for(BanknoteDispenser dispenser : banknoteDispensers.values())
			interconnect(dispenser, banknoteOutput);

		interconnect(coinSlot, coinValidator);
		interconnect(coinValidator, coinTray, coinDispensers, coinStorage);

		for(CoinDispenser coinDispenser : coinDispensers.values())
			interconnect(coinDispenser, coinTray);
	}

	private BidirectionalChannel<Banknote> validatorSource;

	private void interconnect(BanknoteSlot slot, BanknoteValidator validator) {
		validatorSource = new BidirectionalChannel<Banknote>(slot, validator);
		slot.connect(validatorSource);
	}

	private void interconnect(BanknoteValidator validator, BanknoteStorageUnit storage) {
		UnidirectionalChannel<Banknote> bc = new UnidirectionalChannel<Banknote>(storage);
		validator.connect(validatorSource, bc);
	}

	private void interconnect(BanknoteDispenser dispenser, BanknoteSlot slot) {
		UnidirectionalChannel<Banknote> bc = new UnidirectionalChannel<Banknote>(slot);
		dispenser.connect(bc);
	}

	private void interconnect(CoinSlot slot, CoinValidator validator) {
		UnidirectionalChannel<Coin> cc = new UnidirectionalChannel<Coin>(validator);
		slot.connect(cc);
	}

	private void interconnect(CoinValidator validator, CoinTray tray, Map<BigDecimal, CoinDispenser> dispensers,
		CoinStorageUnit storage) {
		UnidirectionalChannel<Coin> rejectChannel = new UnidirectionalChannel<Coin>(tray);
		Map<BigDecimal, UnidirectionalChannel<Coin>> dispenserChannels = new HashMap<BigDecimal, UnidirectionalChannel<Coin>>();

		for(BigDecimal denomination : dispensers.keySet())
		{
			CoinDispenser dispenser = dispensers.get(denomination);
			dispenserChannels.put(denomination, new UnidirectionalChannel<Coin>(dispenser));
		}
		
		UnidirectionalChannel<Coin> overflowChannel = new UnidirectionalChannel<Coin>(storage);

		validator.connect(rejectChannel, dispenserChannels, overflowChannel);
	}

	private void interconnect(CoinDispenser dispenser, CoinTray tray) {
		UnidirectionalChannel<Coin> cc = new UnidirectionalChannel<Coin>(tray);
		dispenser.connect(cc);
	}
}
