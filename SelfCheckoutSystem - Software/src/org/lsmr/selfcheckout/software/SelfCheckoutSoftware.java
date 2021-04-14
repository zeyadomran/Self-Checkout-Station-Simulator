package org.lsmr.selfcheckout.software;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.gui.AttendantSCSPanel;
import org.lsmr.selfcheckout.gui.CheckoutSCSPanel;
import org.lsmr.selfcheckout.gui.MainSCSPanel;
import org.lsmr.selfcheckout.gui.StartScreenPanel;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/** This class will store information about the products in the database and the state of the station. */
public class SelfCheckoutSoftware {
	private Map<Barcode, BarcodedProduct> productDatabase = ProductDatabases.BARCODED_PRODUCT_DATABASE;
	private Map<Product, Integer> inventoryDatabase = ProductDatabases.INVENTORY;
	private Map<PriceLookupCode, PLUCodedProduct> pluProductDatabase = ProductDatabases.PLU_PRODUCT_DATABASE;

	private ArrayList<BarcodedItem> scannedItems = new ArrayList<BarcodedItem>();
	private ArrayList<PLUCodedItem> pluItems = new ArrayList<PLUCodedItem>();
	private ArrayList<BarcodedItem> baggingAreaItems = new ArrayList<BarcodedItem>();
	private ArrayList<PLUCodedItem> baggingAreaPluItems = new ArrayList<PLUCodedItem>();
	private ArrayList<BarcodedItem> personalBags = new ArrayList<BarcodedItem>();
	private SelfCheckoutStation station = null;
	private BigDecimal total = new BigDecimal("0");
	private String receipt;
	private String currentMember;
	public BigDecimal amountPaid;
	private BigDecimal changeDue = new BigDecimal("0.00");
	private boolean attendantLoggedIn;
	private boolean blocked = false;
	private double approvedWeightDifference = 0;
	private boolean lowInk = false;
	private boolean lowPaper = false; 
	private int inkLeft = 0;
	private int paperLeft = 0; 
	private int numberOfBags;
	private boolean addingItems = false;
	private boolean shutDown = false;
	private boolean attendentLoggedin;
	public Attendant currentAttendant;
	public BigDecimal amountEntered = new BigDecimal("0.00");
	public Map<String, Card> creditCards = new HashMap<>();
	public Map<String, Card> debitCards = new HashMap<>();
	public Map<String, Card> giftCards = new HashMap<>();


	// Listeners
	private CardReaderListenerStub cardReaderListener = new CardReaderListenerStub();
	private BanknoteSlotListenerStub banknoteSlotListener = new BanknoteSlotListenerStub();
	public BanknoteValidatorListenerStub banknoteValidatorListener = new BanknoteValidatorListenerStub();
	private BanknoteDispenserListenerStub banknoteDispenserListener = new BanknoteDispenserListenerStub();
	public CoinValidatorListenerStub coinValidatorListener = new CoinValidatorListenerStub();
	private CoinDispenserListenerStub coinDispenserListener = new CoinDispenserListenerStub();
	private CoinStorageUnitListenerStub coinStorageUnitListener = new CoinStorageUnitListenerStub();
	private BanknoteStorageUnitListenerStub banknoteStorageUnitListener = new BanknoteStorageUnitListenerStub();
	 
	/**
	 * Creates an instance of SelfCheckoutSoftware.
	 * 
	 * @param station
	 * 			The station that the software will control.
	 */
	public SelfCheckoutSoftware(SelfCheckoutStation station) {
		if(station == null) throw new NullPointerException("No argument may be null.");
	
		this.station = station;
		this.station.cardReader.register(cardReaderListener);
		this.station.banknoteInput.register(banknoteSlotListener);
		this.station.banknoteValidator.register(banknoteValidatorListener);
		this.station.coinValidator.register(coinValidatorListener);
		this.station.coinStorage.register(coinStorageUnitListener);
		this.station.banknoteStorage.register(banknoteStorageUnitListener);
		 
		//register super user
		this.registerAttendant("12345");
		this.attendantLogin("12345"); 
		
		for(int i : this.station.banknoteDenominations) {
			for(int j = 0; j < 50; j++) {
				this.loadBanknoteDispenser(new Banknote(i, Currency.getInstance(Locale.CANADA)));
		    }
			//register a listener
		    this.station.banknoteDispensers.get(i).register(banknoteDispenserListener);
		}
		
		for(BigDecimal i : this.station.coinDenominations) {
			for(int j = 0; j < 100; j++) {
				this.loadCoinDispenser(new Coin(i, Currency.getInstance(Locale.CANADA)));
		    }
		    //register a listener
		    this.station.coinDispensers.get(i).register(coinDispenserListener);
		}
		
		//logout and remove superuser
		this.attendantLogOut();
		AttendantDatabase.REGISTERED_ATTENDANTS.remove("12345");

		//load receipt machine with paper and ink
		this.station.printer.addInk(1000);
		this.station.printer.addPaper(10);
		this.inkLeft = 1000;
		this.paperLeft = 10;
	}
	
	/**
	 * Adds a product to the product database.
	 * 
	 * @param product
	 * 			The product that will be added to the database.
	 * @param amountAvailable
	 * 			The stock of the product.
	 */
	public void addProduct(BarcodedProduct product, int amountAvailable) {
		if(product == null) throw new NullPointerException("No argument may be null.");
		if(amountAvailable <= 0) throw new IllegalArgumentException("The amount availabe should be greater than 0.");
		this.productDatabase.put(product.getBarcode(), product);
		this.inventoryDatabase.put(product, amountAvailable);
	}
	
	/**
	 * Removes a product from the database.
	 * 
	 * @param barcode
	 * 			The barcode of the product that will be removed from the database.
	 * 
	 * @return If the product was successfully removed.
	 */
	public boolean removeProduct(Barcode barcode) {
		if(barcode == null) throw new NullPointerException("No argument may be null.");
		if(this.productDatabase.containsKey(barcode)) { // Checking if item is in database.
			BarcodedProduct product = this.productDatabase.get(barcode);
			this.inventoryDatabase.remove(product);
			this.productDatabase.remove(barcode);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a Plu product to the Plu product database.
	 * 
	 * @param product
	 * 			The Plu product that will be added to the database.
	 * @param amountAvailable
	 * 			The stock of the product.
	 */
	public void addPLUProduct(PLUCodedProduct product, int amountAvailable) {
		if(product == null) throw new NullPointerException("No argument may be null.");
		if(amountAvailable <= 0) throw new IllegalArgumentException("The amount availabe should be greater than 0.");
		this.pluProductDatabase.put(product.getPLUCode(), product);
		this.inventoryDatabase.put(product, amountAvailable);
	}
	
	/**
	 * removes a Plu product to the Plu product database.
	 * 
	 * @param plc
	 * 			The code of the product to remove from the database.
	 */
	public boolean removePluProduct(PriceLookupCode plc) {
		if(plc == null) throw new NullPointerException("No argument may be null.");
		if(this.pluProductDatabase.containsKey(plc)) { // Checking if item is in database.
			PLUCodedProduct product = this.pluProductDatabase.get(plc);
			this.inventoryDatabase.remove(product);
			this.pluProductDatabase.remove(plc);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Scans an item if there is inventory available.
	 * 
	 * @param barcode 
	 * 			The barcode of the item.
	 * @param weight 
	 * 			The weight of the item.
	 * 
	 * @return If scanning the item was a success.
	 */
	public boolean scanItem(Barcode barcode, double weight) {
		if(barcode == null) throw new NullPointerException("No argument may be null.");
		if(weight <= 0.0) throw new IllegalArgumentException("The weight of the item should be greater than 0.0.");
		if(this.productDatabase.containsKey(barcode)) { // Checking if item is in database.
			BarcodedProduct prod = this.productDatabase.get(barcode);
			int inventoryLeft = this.inventoryDatabase.get(prod);
			if(inventoryLeft == 0) {
				return false;
			} else {
				BarcodedItem item = new BarcodedItem(barcode, weight);
				this.station.mainScanner.scan(item);
				this.total = this.total.add(prod.getPrice()).setScale(2, RoundingMode.CEILING);;
				this.inventoryDatabase.put(prod, inventoryLeft - 1);
				this.scannedItems.add(item);
				return true;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Removes an item that was scanned.
	 * 
	 * @param item
	 * 			The item you wish to remove.
	 * @return If removing the scanned item was a success.
	 */
	public boolean removeScannedItem(BarcodedItem item) {
		if(item == null) throw new NullPointerException("No argument may be null.");
		if(this.scannedItems.contains(item)) {
			BarcodedProduct prod = this.productDatabase.get(item.getBarcode());
			this.scannedItems.remove(item);
			this.total = this.total.subtract(prod.getPrice());
			this.inventoryDatabase.put(prod, (this.inventoryDatabase.get(prod) + 1));
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a plu item to the checked items with the entered code and weight
	 * 
	 * @param plu 
	 * 			The code of the plu item to add
	 * @param weight 
	 * 			The weight of the item being added
	 * 
	 * @return If adding the item was a success.
	 */
	public boolean addPLUItem(PriceLookupCode plu, double weight)
	{
		
		if(plu == null) throw new NullPointerException("No argument may be null.");
		if(weight <= 0.0) throw new IllegalArgumentException("The weight of the item should be greater than 0.0.");
		if(this.pluProductDatabase.containsKey(plu)) { // Checking if item is in database.
			PLUCodedProduct prod = this.pluProductDatabase.get(plu);
			int inventoryLeft = this.inventoryDatabase.get(prod);
			if(inventoryLeft == 0) {
				return false;
			} else {
				PLUCodedItem item = new PLUCodedItem(plu, weight);
				this.station.scale.add(item);
				
				weight = weight/1000;
				
				BigDecimal priceOfItem = prod.getPrice().multiply(new BigDecimal(weight)).setScale(2, RoundingMode.CEILING);
				
				this.total = this.total.add(priceOfItem);
				
				this.inventoryDatabase.put(prod, inventoryLeft - 1);
				
				this.pluItems.add(item);
				return true;
			}
		} else {
			return false;
		}
		
	}
	
	/**
	 * Removes a plu item that was scanned.
	 * 
	 * @param item
	 * 			The plu item you wish to remove.
	 * @return If removing the checked plus item was a success.
	 */
	public boolean removePluItem(PLUCodedItem item) {
		if(item == null) throw new NullPointerException("No argument may be null.");
		if(this.pluItems.contains(item)) {
			PLUCodedProduct prod = this.pluProductDatabase.get(item.getPLUCode());
			this.pluItems.remove(item);
			BigDecimal weight = new BigDecimal(item.getWeight()/1000);
			this.total = this.total.subtract(prod.getPrice().multiply(weight));
			this.inventoryDatabase.put(prod, (this.inventoryDatabase.get(prod) + 1));
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Adds an item to the bagging area if it has already been scanned.
	 * 
	 * @param barcode 
	 * 			The barcode of the item.
	 * @return If adding the item to the bagging area was a success.
	 */
	public boolean placeItemInBaggingArea(Barcode barcode) {
		if(barcode == null) throw new NullPointerException("No argument may be null.");
		if(this.productDatabase.containsKey(barcode)) { // Checking if item is in database.
			BarcodedItem item = null;
			for(int i = 0; i < this.scannedItems.size(); i++) {
				if(this.scannedItems.get(i).getBarcode().equals(barcode)) {
					item = this.scannedItems.get(i);
				}
			}
			if(item != null) {
				this.station.baggingArea.add(item);
				this.baggingAreaItems.add(item);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	/**
	 * Adds a plu item to the bagging area if it has already been added to the checked items
	 * 
	 * @param plu 
	 * 			The plu code of the item.
	 * @return If adding the item to the bagging area was a success.
	 */
	public boolean placePluItemInBaggingArea(PriceLookupCode plu) {
		if(plu == null) throw new NullPointerException("No argument may be null.");
		if(this.pluProductDatabase.containsKey(plu)) { // Checking if item is in database.
			PLUCodedItem item = null;
			for(int i = 0; i < this.pluItems.size(); i++) {
				if(this.pluItems.get(i).getPLUCode().equals(plu)) {
					item = this.pluItems.get(i);
				}
			}
			if(item != null) {
				this.station.baggingArea.add(item);
				this.baggingAreaPluItems.add(item);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	/**
	 * Removes an item that was added to the bagging area.
	 * 
	 * @param item
	 * 			The item you wish to remove.
	 * @return If removing the item from the bagging area was a success.
	 */
	public boolean removeItemBaggingArea(BarcodedItem item) {
		if(item == null) throw new NullPointerException("No argument may be null.");
		if(this.baggingAreaItems.contains(item)) { // Checking if item is in database.
			this.station.baggingArea.remove(item);
			this.baggingAreaItems.remove(item);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes a plu item that was added to the bagging area.
	 * 
	 * @param item
	 * 			The plu item you wish to remove.
	 * @return If removing the plu item from the bagging area was a success.
	 */
	public boolean removePluItemBaggingArea(PLUCodedItem item) {
		if(item == null) throw new NullPointerException("No argument may be null.");
		if(this.baggingAreaPluItems.contains(item)) { // Checking if item is in database.
			this.station.baggingArea.remove(item);
			this.baggingAreaPluItems.remove(item);
			return true;
		} else {
			return false;
		}
	}
	

	/**
	 * Looks up a product name in the system to get the code
	 * 
	 * @param productName
	 * 			The name of the product the customer wants to find the code for
	 * @return the plu code corresponding to the product the customer has entered
	 */
	public PriceLookupCode lookUpProductCode(String productName)
	{
		this.addingItems = false;
		if(productName == null)
		{
			throw new SimulationException("Must enter product name to search");

		}
		for(PriceLookupCode plu : pluProductDatabase.keySet())
		{
			PLUCodedProduct prod = pluProductDatabase.get(plu);
			if(prod.getDescription().equals(productName))
			{
				return plu;
			}
		}
		return null;
		
	}
	
	
	/**
	 * Gets the receipt
	 * 
	 * @return the receipt.
	 */
	public String getReceipt() {
		return this.receipt;
	}

	/**
	 * Gets the scanned items.
	 * 
	 * @return An ArrayList of the items scanned.
	 */
	public ArrayList<BarcodedItem> getScannedItems() { return this.scannedItems; }
	
	/**
	 * Get the plu items checked out
	 * 
	 * @return An ArrayList of the plu items checked out.
	 */
	public ArrayList<PLUCodedItem> getPluItems() { return this.pluItems; }
	
	/**
	 * Gets the items on the bagging area.
	 * 
	 * @return An ArrayList of the items in the bagging area.
	 */
	public ArrayList<BarcodedItem> getBaggingArea() { return this.baggingAreaItems; }
	
	
	/**
	 * Gets the items in the bagging area.
	 * 
	 * @return the items in the Plu bagging area
	 */
	public ArrayList<PLUCodedItem> getBaggingAreaPlu() { return this.baggingAreaPluItems; }
	
	/**
	 * Gets the product database.
	 * 
	 * @return The product database.
	 */
	public Map<Barcode, BarcodedProduct> getProductDB() { return this.productDatabase; }
	
	
	/**
	 * Gets the Plu product database.
	 * 
	 * @return The Plu product database.
	 */
	public Map<PriceLookupCode, PLUCodedProduct> getProductPLUDB() { return this.pluProductDatabase; }
	
	/**
	 * Gets the inventory database.
	 * 
	 * @return The inventory database.
	 */
	public Map<Product, Integer> getInventoryDB() { return this.inventoryDatabase; }
	
	/**
	 * Gets the weight of the items in the bagging area.
	 * 
	 * @return The total weight of all the items in the bagging area.
	 */
	public double getBaggingAreaWeight() { 
		try {
			return this.station.baggingArea.getCurrentWeight();	
		} catch (OverloadException e) {
			return -1;
		}
	}
	
	/**
	 * Gets the total price of the items in the bagging area.
	 * 
	 * @return The total price of the items.
	 */
	public BigDecimal getTotal() { return this.total; }

	/**
	 * Accepts banknotes from the customer.
	 * 
	 * @param banknotes
	 * 			Array with all banknote the customer wishes to use.
	 * @return whether enough valid banknotes were used to pay the total.
	 * @throws EmptyException 
	 */
	public boolean payWithCash(ArrayList<Banknote> banknotes) throws SimulationException, OverloadException, DisabledException, EmptyException {
		this.addingItems = false;
		if(banknotes == null) throw new NullPointerException("No argument may be null."); // Checks for null params.
		double value = 0.0;
		for(Banknote banknote : banknotes) { // Add value of banknotes.
			value += banknote.getValue();
		}
		this.amountPaid = new BigDecimal(value);

		BigDecimal valueAsBigDecimal = new BigDecimal(value);
		this.changeDue = valueAsBigDecimal.subtract(this.total);

		boolean isSuccess = false;
		for(Banknote banknote : banknotes) { // Accepts banknotes.
			isSuccess = acceptBanknote(banknote);
			if(!isSuccess) value -= banknote.getValue();
		}
		
		if(value < this.total.doubleValue()) return false; // return false if valid banknotes value is less than total.
		
		if(this.currentMember != null) addPoints(this.total.intValue());
		this.amountPaid = this.total;

		//return true if enough change was dispensed correctly
		if(value > this.total.doubleValue()) {
			BigDecimal changeValue = valueAsBigDecimal.subtract(this.total);
			return dispenseChange(changeValue);
		}
		
		return true;
	}
	
	
	/**
	 * Accepts a Banknote from the BanknoteSlot 
	 * 
	 * @param banknote
	 * 			The banknote that will be validated.
	 * @return If getting a Banknote was successful.
	 */
	private boolean acceptBanknote(Banknote banknote) throws SimulationException, OverloadException, DisabledException {
		if(this.banknoteSlotListener.isEjected()) { // Checks whether there is an ejected banknote.
			this.station.banknoteInput.removeDanglingBanknote();
		}  
		if(this.station.banknoteStorage.hasSpace()) {
			this.station.banknoteInput.accept(banknote);
			if(this.banknoteValidatorListener.isValidBanknote()) {
				return true;
			}
		} else {
			this.station.banknoteInput.disable();
		}
		return false;
	}
	 
	/**
	 * Accepts coins from the customer.
	 * 
	 * @param coins
	 * 			Array with all coins the customer wishes to use.
	 * @return whether enough valid coins were used to pay the total.
	 * @throws EmptyException 
	 * @throws OverloadException 
	 */
	public boolean payWithCoin(ArrayList<Coin> coins) throws DisabledException, OverloadException, EmptyException {
		this.addingItems = false;
		if(coins == null) throw new NullPointerException("No argument may be null."); // Checks for null params.
		BigDecimal value = new BigDecimal("0");
		for(Coin coin : coins) { // Add value of coins.
			value = value.add(coin.getValue());
		}
		
		this.amountPaid = value;
		this.changeDue = value.subtract(this.total);

		
		boolean isSuccess = false;
		for(Coin coin : coins) { // Accepts coin
			isSuccess = acceptCoin(coin);
			if(!isSuccess) value = value.subtract(coin.getValue());
		}
		
		if(value.compareTo(this.total) < 0) return false; // return false if valid coin value is less than total.
		
		if(this.currentMember != null) addPoints(this.total.intValue());
		this.amountPaid = this.total;

		// return true if enough change was dispensed correctly
		if(value.compareTo(this.total) > 0) {
			BigDecimal changeValue = value.subtract(this.total);
			return dispenseChange(changeValue);
		}
		return true;
	}
	
	/**
	 * Accepts a coin from the coinSlot 
	 * 
	 * @param coin
	 * 			The coin that will be validated.
	 * @return If getting a Coin was successful.
	 */
	private boolean acceptCoin(Coin coin) throws DisabledException { 
		if(this.station.coinStorage.hasSpace()) {
			this.station.coinSlot.accept(coin);
			if(this.coinValidatorListener.getIsValid()) return true;
		} else {
			this.station.coinSlot.disable();
		}
		return false;
	}

	/**
	 * Method to dispense the correct amount of change for the customer
	 * 
	 * Implemented to handle any denomination of coins the system accepts
	 * Used the cashiers algorithm to pick the largest coin or note that isn't larger than the value
	 * 
	 * @param changeValue
	 * 			The value to dispense
	 * @return If the correct amount of change was dispensed.
	 */
	public boolean dispenseChange(BigDecimal changeValue) throws OverloadException, EmptyException, DisabledException {
		BigDecimal amountDispensed = new BigDecimal("0.0");
		BigDecimal amountLeft = changeValue;
		List<BigDecimal> coinDenominations = this.station.coinDenominations;
		Collections.sort(coinDenominations);
		Collections.reverse(coinDenominations);

		List<Integer> banknoteDenominations = new ArrayList<Integer>();
		for(int i : this.station.banknoteDenominations) banknoteDenominations.add(Integer.valueOf(i));
		Collections.sort(banknoteDenominations);
		Collections.reverse(banknoteDenominations);
		
		while(amountLeft.compareTo(new BigDecimal("0")) > 0) {
			boolean dispensed = false;
			BigDecimal lowestCoin = coinDenominations.get(coinDenominations.size() - 1);
			if(amountLeft.compareTo(lowestCoin) < 0) {
				this.station.coinDispensers.get(lowestCoin).emit();
				amountDispensed = changeValue;
				amountLeft = new BigDecimal("0");
				break;
			}
				for(int i = 0; i < banknoteDenominations.size(); i++) {
					int val = banknoteDenominations.get(i);
					if(amountLeft.compareTo(new BigDecimal("" + val)) >= 0 && this.station.banknoteDispensers.get(val).size() > 0) {
						this.station.banknoteDispensers.get(val).emit();
						Banknote ejected = this.station.banknoteOutput.removeDanglingBanknote();
						amountDispensed = amountDispensed.add(new BigDecimal("" + val));
						amountLeft = amountLeft.subtract(new BigDecimal("" + val));
						dispensed = true;
						i = banknoteDenominations.size();
					}
				}
			if(!dispensed) {
				for(int i = 0; i < coinDenominations.size(); i++) {
					BigDecimal val = coinDenominations.get(i);
					if(amountLeft.compareTo(val) >= 0 && this.station.coinDispensers.get(val).size() > 0) {
						this.station.coinDispensers.get(val).emit();
						amountDispensed = amountDispensed.add(val);
						amountLeft = amountLeft.subtract(val);
						dispensed = true;
						i = coinDenominations.size();
					}
				}
			}
		}
		if(amountLeft.compareTo(new BigDecimal("0")) == 0) {
			Banknote ejected = this.station.banknoteOutput.removeDanglingBanknote();
			//List<Coin> coinList = this.station.coinTray.collectCoins();
		}
		return (amountLeft.compareTo(new BigDecimal("0")) == 0);
	}

	public void checkLowPaper() {
		paperLeft--;
		if(this.paperLeft <= 3)
			this.lowPaper = true;
	}
	
	/**
	 * Method to generate a receipt for the transaction with the scanned items and the amount payed, total and the change value
	 */
	public void generateReceipt() {
		StringBuilder headersb = new StringBuilder();
		headersb.append("Receipt\n");
		if(this.currentMember != null) {
			headersb.append("Member Name:   " + MemberDatabase.REGISTERED_MEMBERS.get(this.currentMember).getName());
			headersb.append("\nMember Number: " + this.currentMember);
			headersb.append("\nMember Points: " + MemberDatabase.REGISTERED_MEMBERS.get(this.currentMember).getPoints() + "\n\n");
		}
		for(int i = 0; i < headersb.length(); i++) this.station.printer.print(headersb.charAt(i));
		for(int i = 0; i < headersb.length(); i++) {
			if (!(Character.isWhitespace(headersb.charAt(i)))) {
				--this.inkLeft;
				if (this.inkLeft <= 100) {
					this.lowInk = true;
				}
			}
		}
		for(BarcodedItem item : scannedItems) {
			Barcode barcode = item.getBarcode();
			BarcodedProduct product = this.productDatabase.get(barcode);
			StringBuilder sb = new StringBuilder();
			String description = product.getDescription();
			sb.append(description);
			sb.append("\t");
			int padnum = 15 - description.length();
			if(padnum > 0) {
				char[] pad = new char[padnum];
				Arrays.fill(pad, ' ');
				sb.append(pad).append(product.getPrice());
			}
			for(int i = 0; i < sb.length(); i++) this.station.printer.print(sb.charAt(i));
			for(int i = 0; i < sb.length(); i++) {
				if (!(Character.isWhitespace(sb.charAt(i)))) {
					--this.inkLeft;
					if (this.inkLeft <= 100) {
						this.lowInk = true;
					}
				}
			}
			this.station.printer.print('\n');
			checkLowPaper();
		}
		for(PLUCodedItem item : pluItems) {
			PriceLookupCode plc = item.getPLUCode();
			PLUCodedProduct product = this.pluProductDatabase.get(plc);
			StringBuilder sb = new StringBuilder();
			String description = product.getDescription();
			sb.append(description);
			sb.append("\t");
			int padnum = 15 - description.length();
			if(padnum > 0) {
				char[] pad = new char[padnum];
				Arrays.fill(pad, ' ');
				sb.append(pad).append(product.getPrice());
			}
			for(int i = 0; i < sb.length(); i++) this.station.printer.print(sb.charAt(i));
			for(int i = 0; i < sb.length(); i++) {
				if (!(Character.isWhitespace(sb.charAt(i)))) {
					--this.inkLeft;
					if (this.inkLeft <= 100) {
						this.lowInk = true;
					}
				}
			}
			this.station.printer.print('\n');
			checkLowPaper();
		}
		
		
		StringBuilder sb = new StringBuilder();
		String bagsUsed = "Bags used: ";
		String total = "Sub Total: ";
		String AmountPaid = "Amount Paid: ";
		sb.append('\n');
		checkLowPaper();
		sb.append(bagsUsed);
		sb.append("    ");
		sb.append(this.numberOfBags);
		sb.append('\n');
		checkLowPaper();
		sb.append(total);
		sb.append("    ");
		sb.append(this.total);
		sb.append('\n'); 
		checkLowPaper();
		sb.append(AmountPaid);
		sb.append("  ");
		sb.append(this.amountEntered);
		sb.append("\n");
		checkLowPaper();
		sb.append("Change Due: ");
		sb.append("   ");
		sb.append(this.amountEntered.subtract(this.total));
		for(int i = 0; i < sb.length(); i++) this.station.printer.print(sb.charAt(i));
		for(int i = 0; i < sb.length(); i++) {
			if (!(Character.isWhitespace(sb.charAt(i)))) {
				--this.inkLeft;
				if (this.inkLeft <= 100) {
					this.lowInk = true;
				}
			}
		}
		this.station.printer.cutPaper();
		this.receipt = this.station.printer.removeReceipt();
	}
	
	/**
	 * Add an attendant to the attendant Database.
	 * 
	 * @param attendantID
	 * 			The code/id of the attendant that will be added.
	 * 
	 */
	public void registerAttendant(String attendantID) {
		if(attendantID == null) throw new NullPointerException("No argument may be null.");
		Attendant attendant = new Attendant(attendantID);
		
		if(AttendantDatabase.REGISTERED_ATTENDANTS.containsKey(attendantID)) throw new IllegalArgumentException("This Member already exists.");
		AttendantDatabase.REGISTERED_ATTENDANTS.put(attendantID, attendant);
	}
	
	
	/**
	 * Log an attendant in to the system
	 * 
	 * @param attendantID
	 * 			The code/id of the attendant that will be used to log in.
	 * 
	 */
	public void attendantLogin(String attendantID)
	{
		if(currentAttendant!= null)
		{
			throw new SimulationException("An attendant is already logged in");
		}
		if(AttendantDatabase.REGISTERED_ATTENDANTS.containsKey(attendantID))
		{
			attendantLoggedIn = true;
			currentAttendant = AttendantDatabase.REGISTERED_ATTENDANTS.get(attendantID);
		}
		else
		{
			throw new SimulationException("Invalid attendant Id entered");
		}
	}
	
	/**
	 * Log an attendant out of the system
	 * 
	 */
	public void attendantLogOut()
	{
		if(currentAttendant != null)
		{
			attendantLoggedIn = false;
			currentAttendant = null;
		}
		else
		{
			throw new SimulationException("No attendant logged in to log out");
		}
	}
	
	
	/**
	 * Adds a Member to the Members Database.
	 * 
	 * @param name
	 * 			The name of the Member that will be added.
	 * @param memberID
	 * 			The Numerical ID of the Member.
	 */
	public void addMember(String name, String memberID) {
		if(name == null || memberID == null) throw new NullPointerException("No argument may be null.");
		Member member = new Member(name, memberID);
		if(MemberDatabase.REGISTERED_MEMBERS.containsKey(memberID)) throw new IllegalArgumentException("This Member already exists.");
		MemberDatabase.REGISTERED_MEMBERS.put(memberID, member);
	}

	/**
	 * Removes a Member from the Members Database.
	 * 
	 * @param memberID
	 * 			The Member's ID that will be removed.
	 */
	public void removeMember(String memberID) {
		if(memberID == null) throw new NullPointerException("No argument may be null.");
		if(!MemberDatabase.REGISTERED_MEMBERS.containsKey(memberID)) throw new IllegalArgumentException("This Member does not exist.");
		MemberDatabase.REGISTERED_MEMBERS.remove(memberID);
	}

	/**
	 * Allows a user to tap their membership card.
	 * 
	 * @param mCard
	 * 			The user's membership card.
	 * @return Whether the tap was the success and membership found in database.
	 */
	public boolean swipeMembershipCard(Card mCard) {
		if(mCard == null) throw new NullPointerException("No argument may be null.");
		try {
			this.station.cardReader.swipe(mCard, null);
		} catch (IOException e) {
			return false;
		}
		CardData cardData = this.cardReaderListener.getLatestCard();
		if(!MemberDatabase.REGISTERED_MEMBERS.containsKey(cardData.getNumber())) throw new IllegalArgumentException("This Member does not exist.");
		this.currentMember = cardData.getNumber();
		return true;
	}

	/**
	 * Add points to a Member's account.
	 * 
	 * @param points
	 * 			The points that will be added.
	 */
	private void addPoints(int points) {
		MemberDatabase.REGISTERED_MEMBERS.get(this.currentMember).addPoints(points);
	}

	/**
	 * Adds a item that emulates a customer owned bag to the bagging area.
	 * 
	 * @param weightOfBag
	 *        Weight of the bag. Must be positive int.
	 * @return If adding the bag was successful
	 */
	public boolean addOwnBag(double weightOfBag) {
		if (weightOfBag <= 0) {
			return false;
		}
		BarcodedItem bag = new BarcodedItem(new Barcode("12345"), weightOfBag);
		this.personalBags.add(bag);
		this.station.baggingArea.add(bag);
		return true;
	}
	
  	/**
   	 * Removes a bag from the bagging area.

	 * @param bag
	 *        	Bag to be removed.
	 * @return If removing the bag was successful
	 */
	public boolean removeOwnBag(BarcodedItem bag) {
		if (bag == null) {
			return false;
		}
		if (!this.personalBags.contains(bag)) {
			return false;
		}
		this.personalBags.remove(bag);
		this.station.baggingArea.remove(bag);
		return true;
	}

	/**
	 * Gets the list of personal bags in the bagging area
	 * 
	 * @return The list of personal bags in the bagging area
	 */
	public ArrayList<BarcodedItem> getPersonalBags() {
		return this.personalBags;
	}
	
	public BigDecimal getAmountPaid() {
		return this.amountPaid;
	}

	/**
	 * Accepts Card payment from the customer.
	 * 
	 * @param cd 
	 * 			card data of card the customer wishes to use.
	 * @return whether the card used was successful in paying.
	 */
	 private boolean payWithCard(CardData cd) {
		CardIssuer cardIssuer;
		if(cd.getType() == "Debit") {
			cardIssuer = CardIssuersDatabase.DEBIT_CARD_ISSUER;
		}
		else if (cd.getType() == "Credit") {
			cardIssuer = CardIssuersDatabase.CREDIT_CARD_ISSUER;
		} else {
			cardIssuer = CardIssuersDatabase.GIFT_CARD_ISSUER;
		}

		int holdNum = cardIssuer.authorizeHold(cd.getNumber(), this.total);
		if(holdNum == -1) return false;
		boolean isSuccess = cardIssuer.postTransaction(cd.getNumber(), holdNum, this.total);
		
		if(!isSuccess)
		{
			return false;
		}
		
		this.amountPaid = this.total;

		if(this.currentMember != null) addPoints(this.amountPaid.intValue());
		return true;
	}
	
	/**
	 * Accepts Debit or Credit card from the customer.
	 * 
	 * @param card
	 * 			card the customer wishes to use.
	 * @return whether the card used was tapped successfully.
	 */
	public boolean tapCard(Card card) throws IOException {
		if (card == null) throw new NullPointerException("No argument may be null."); // Checks for null params.
		CardData cd = this.station.cardReader.tap(card);
		// Separated debit and credit and if not either it isnt valid card therefore return false
		if (cd.getType() == "Debit") {
			return payWithCard(cd); 
		} else if (cd.getType() == "Credit") {
			return payWithCard(cd);
		} else {
			return false;
		}
	}

	/**
	 * Accepts Debit or Credit card from the customer.
	 * 
	 * @param card
	 * 			card the customer wishes to use.
	 * @return whether the card used was swiped successfully.
	 */
	public boolean swipeCard(Card card, BufferedImage signature) throws IOException {
		if (card == null) throw new NullPointerException("No argument may be null."); // Checks for null params.
		CardData cd = this.station.cardReader.swipe(card, signature);
		if (cd.getType() == "Debit") {
			return payWithCard(cd);
		} else if (cd.getType() == "Credit") {
			return payWithCard(cd);
		}else if (cd.getType() == "Gift") {
			return payWithCard(cd);
		} else {
			return false;
		}
	}
	
	/**
	 * Accepts Debit or Credit card from the customer.
	 * 
	 * @param card
	 * 			card the customer wishes to use.
	 * @return whether the card used was inserted successfully.
	 */
	public boolean insertCard(Card card, String pin) throws IOException {
		if (card == null) throw new NullPointerException("No argument may be null."); // Checks for null params.
		CardData cd = this.station.cardReader.insert(card, pin);
		if (cd.getType() == "Debit") {
			return payWithCard(cd);
		} else if (cd.getType() == "Credit") {
			return payWithCard(cd);
		} else if (cd.getType() == "Gift") {
			return payWithCard(cd);
		} else {
			return false;
		}
	}

	/**
	 * Notifies the customer if they have failed to add an item to the bagging area after scanning.
	 * 
	 * @return whether the customer failed to placed the item in the bagging area
	 */
	public boolean failToPlaceItem(){
		double totalWeight = 0;
		for (int i = 0; i < scannedItems.size(); i++) {
			totalWeight += scannedItems.get(i).getWeight();
		}
		for(int i = 0; i < this.pluItems.size(); i++)
		{
			totalWeight += pluItems.get(i).getWeight();
		}
		if (totalWeight != (getBaggingAreaWeight()) + this.approvedWeightDifference) {
			throw new SimulationException("Please place item in bagging area.");
		}
		return false;
	}

	/**
	 * Resets the software to its starting state.
	 */
	public void resetStation() {
		this.scannedItems.clear();
		this.pluItems.clear();
		for(PLUCodedItem i : this.baggingAreaPluItems) this.station.baggingArea.remove(i);
		this.baggingAreaPluItems.clear();
		for(BarcodedItem i : this.baggingAreaItems) this.station.baggingArea.remove(i);
		this.baggingAreaItems.clear();
		this.personalBags.clear();
		this.total = new BigDecimal("0");
		this.receipt = null;
		this.currentMember = null;
		this.amountPaid = null;
		this.amountEntered = new BigDecimal("0.00");
		this.currentAttendant = null;
		this.approvedWeightDifference = 0.0;
		this.attendantLoggedIn = false;
		this.changeDue = new BigDecimal("0.00");
	}

	/**
	 * Customer finished scanning items.
	 * 
	 * @return whether everything is valid.
	 */
	public boolean finishedScanningItems() {
		return !failToPlaceItem();
	}
	
	/**
	 * Attendant adds ink to the receipt printer.
	 * 
	 * @return Whether adding ink was successful.
	 */
	public boolean addInkToPrinter(int amount) {
		if(this.currentAttendant != null) {
			this.station.printer.addInk(amount);
			this.inkLeft += amount;
			return true;
		}
		return false;
	}

	/**
	 * Attendant adds paper to the receipt printer.
	 * 
	 * @return Whether adding paper was successful.
	 */
	public boolean addPaperToPrinter(int amount) {
		if(this.currentAttendant != null) {
			this.station.printer.addPaper(amount);
			this.paperLeft += amount; 
			return true;
		}
		return false;
	}
	
	/**
	 * Gets whether the station is blocked or not
	 * @return boolean that determines whether the station is blocked or not
	 */
	public boolean isBlocked() {
		return this.blocked;
	}
	
	/**
	 * Sets the station to be blocked or unblocked
	 * @param the value to set blocked too
	 * @return Whether setting the state was successful
	 */
	public boolean setBlocked(boolean state) {
		this.blocked = state;
		return true;
	}
	
	/**
	 * Sets the maximum approved weight difference
	 * @param the maximum approved weight difference
	 * @return Whether setting the value was successful
	 */
	public boolean setMaxWeightDiff(double diff) {
		this.approvedWeightDifference = this.approvedWeightDifference + diff;
		return true;
	}
	
	/**
	 * Gets the maximum approved weight difference
	 * @return Maximum approved weight difference
	 */
	public double getMaxWeightDiff() {
		return this.approvedWeightDifference;
	}
	
	/**
	 * gets the value of inkLeft
	 * @return value of inkLeft
	 */
	public int getInkLeft() {
		return this.inkLeft;
	}
	
	/**
	 * gets the value of lowInk
	 * @return value if lowInk
	 */
	public boolean getLowInk() {
		return this.lowInk;
	}
	
	/**
	 * gets the value of paperLeft
	 * @return value of paperLeft
	 */
	public int getPaperLeft() {
		return this.paperLeft;
	}
	
	/**
	 * gets the value of lowPaper
	 * @return value of lowPaper
	 */
	
	public boolean getLowPaper() {
		return this.lowPaper; 
	}
	
	/**
	 * shuts down the station if the attendant is logged in
	 * 
	 * @return if shut down was a success
	 */
	public boolean shutDownStation()
	{
		if(this.currentAttendant != null)
		{
			boolean success = currentAttendant.attendantShutDownStation(this);
			this.setShutDown(true);
			return success;
		}
			
		else
		{
			return false;
		}
	}
	
	/**
	 * starts up a station if the attendant is logged in
	 * 
	 * @return if shut down was a success
	 */
	public boolean startUpStation()
	{
		if(this.currentAttendant != null)
		{
			boolean success = currentAttendant.attendantStartUpStation(this);
			this.setShutDown(false);
			return success;
		}
			
		else
		{
			return false;
		}
	}
	
	/**
	 * gets if the system is shutdown 
	 * @return if the system is shut down
	 */
	public boolean isShutDown()
	{
		return this.shutDown;
	}

	
	/**
	 * gets if the attendant is logged in 
	 * @return if an attendant is logged in
	 */
	public boolean getattendantLoggedIn() {
		return this.attendantLoggedIn;
	}

	/**
	 * gets the current station that is being worked on
	 * @return the checkout station
	 */
	public SelfCheckoutStation getStation() {
		// TODO Auto-generated method stub
		return this.station;
	}

	/**
	 * sets the system to the correct shutdown value
	 * @param shutDown
	 * 			value to set the shutdown 
	 */
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}
	
	/**
	 * Attendant loads coins into the coin dispensers.
	 * @param coins are the coins to be loaded into the dispenser.
	 * @return true if all coins were successfully loaded.
	 */
	public boolean loadCoinDispenser(Coin... coins) {
		// For each coin, find the respective dispenser, and then loads the coin in there.
		if (coins == null) {
			return false;
		}
		if (this.currentAttendant != null) {
			for (Coin c: coins) {
				if (c == null) {
					return false;
				}
				BigDecimal v = c.getValue();
				try {
					this.station.coinDispensers.get(v).load(c);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Coin Dispenser for coins of value " + v.doubleValue() + " is full.");
				} catch (NullPointerException e) {
					throw new SimulationException("This coin type does not exist.");
				}
			}
		}
		
		return this.coinDispenserListener.isLoaded();
	}
	
	/**
	 * Attendant loads banknotes into the banknote dispensers.
	 * @param banknotes are the baknotes to be loaded into the dispensers.
	 * @return true of all banknotes were successfully loaded.
	 */
	public boolean loadBanknoteDispenser(Banknote ...banknotes) {
		if (banknotes == null) {
			return false;
		}
		if (this.currentAttendant != null) {
			for (Banknote b: banknotes) {
				if (b == null) {
					return false;
				}
				int v = b.getValue();
				try {
					this.station.banknoteDispensers.get(v).load(b);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Banknote Dispenser for banknotes of value " + v + " is full.");
				} catch (NullPointerException e) {
					throw new SimulationException("This banknote type does not exist.");
				}
			}
		}
		
		return this.banknoteDispenserListener.isBanknoteLoaded();
	}
	
	/**
	 * Attendant empties the coin storage.
	 * @return true if there are no coins in the storage after unloading, false if there is no attendant.
	 */
	public boolean emptyCoinStorage() {
		if (this.currentAttendant != null) {
			this.station.coinStorage.unload();
		} else {
			return false;
		}
		//should always return true
		return !this.coinStorageUnitListener.isLoaded();
	}
	
	/**
	 * Attendant empties the banknote storage.
	 * @return true if there are no banknotes in the storage after unloading, false if there is no attendant.
	 */
	public boolean emptyBanknoteStorage() {
		if (this.currentAttendant != null) {
			this.station.banknoteStorage.unload();
		} else {
			return false;
		}
		// should always return true
		return !this.banknoteStorageUnitListener.isLoaded();
	}
	
	
	/**
	 * Customer enters their membership card information.
	 */
	public boolean enterMembershipInfo(String mNumber) {
		if(mNumber == null)
			throw new NullPointerException("No argument may be null.");
		if(!MemberDatabase.REGISTERED_MEMBERS.containsKey(mNumber))
			throw new IllegalArgumentException("This Member does not exist.");
		this.currentMember = mNumber;
		return true;
	}
	
	
	/**
	 * Customer removes purchased items from bagging area.
	 * @return True if all items were successfully removed from the bagging area.
	 */
	public boolean removePurchasedItems() {
		for (BarcodedItem item : this.baggingAreaItems)
			removeItemBaggingArea(item);
		for (PLUCodedItem item : this.baggingAreaPluItems)
			removePluItemBaggingArea(item);
		return true;
	}
	
	
	/**
	 * Customer enters number of plastic bags used.
	 */
	public void enterNumberOfBags(int num) {
		if (num < 0) throw new IllegalArgumentException("Number of plastic bags used must be 0 or greater.");
		this.numberOfBags = num;
		if (num == 0) return;
		// add bag price to total (5 cents per bag)
		BigDecimal bagPrice = new BigDecimal(0.05 * num).setScale(2, RoundingMode.CEILING);
		this.total = this.total.add(bagPrice);
	}


	/**
	 * Looks up a product name in the system to get the code
	 * 
	 * @param productName
	 * 			The name of the product the customer wants to find the code for
	 * @return the plu code corresponding to the product the customer has entered
	 */
	public PriceLookupCode attendantLookUpProductCode(String productName)
	{
		if(this.currentAttendant != null)
		{
			if(productName == null)
			{
				throw new SimulationException("Must enter product name to search");
	
			}
			
			return currentAttendant.lookupProdCode(this, productName);
		}
		return null;
		
	}
	
	/**
	 * Looks up a product name in the system to get the code
	 * 
	 * @param productName
	 * 			The name of the product the customer wants to find the code for
	 * @return the plu code corresponding to the product the customer has entered
	 */
	public String attendantFindProductName(PriceLookupCode code)
	{
		if(this.currentAttendant != null)
		{
			if(code == null)
			{
				throw new SimulationException("Must enter a code to search");
			}
			
			return currentAttendant.lookupProdName(this, code);
			
		}
		return "Not logged in";
		
	}
	
	
	/**
	 * Compares the actual weight on the scale to the expected weight (comprised of all items added to bagging area)- use case i
	 * @return Return 'true' if the actual and expected weights are the same, false otherwise
	 */
	public boolean checkWeight() {
		
		double expected = 0;
		double actual = 0;
		
		actual = this.getBaggingAreaWeight();
		
		//Add the weight of all items in bagging area to expected weight
		for(int i = 0; i < this.scannedItems.size(); i++) {
			expected += this.scannedItems.get(i).getWeight();
		}
		
		for(int i = 0; i < this.pluItems.size(); i++) {
			expected += this.pluItems.get(i).getWeight();
		}
		
		for(int i = 0; i < this.personalBags.size(); i++) {
			expected += this.personalBags.get(i).getWeight();
		}

		expected -= this.approvedWeightDifference;
		
		//Compare actual and expected weights
		if(actual == expected) return true;
		
		return false;
	}
	
	
	/**
	 * Sets boolean addingItems to 'true'- use case a
	 * (Functions dealing with payment (payWithBanknote/payWithCoin) and lookUpProductCode set addingItems to false)
	 */
	public void returnToAddingItems() {
		this.addingItems = true;
	}

	/**
	 * loads the main GUI.
	 */
	public void loadMainGUI() {
		JFrame frame = this.station.screen.getFrame(); // Gets The JFrame used by the touchscreen listener.
		this.station.screen.setVisible(false);
		frame.getContentPane().removeAll();
		frame.setLayout(new BorderLayout());
		MainSCSPanel mainPanel = new MainSCSPanel(this);
		JPanel fixedPanel = new JPanel(new GridBagLayout());
		fixedPanel.setPreferredSize(frame.getSize());
		fixedPanel.setBackground(new Color(9, 11, 16));
		fixedPanel.add(mainPanel);
		frame.getContentPane().add(fixedPanel);
		frame.validate();
		frame.repaint();
		this.station.screen.setVisible(true); // Displays the JFrame.
	}

	/**
	 * Loads up the attendant GUI.
	 */
	public void changeToAttendantGUI() {
		JFrame frame = this.station.screen.getFrame(); // Gets The JFrame used by the touchscreen listener.
		this.station.screen.setVisible(false);
		frame.getContentPane().removeAll();
		frame.setLayout(new BorderLayout());
		AttendantSCSPanel attendantPanel = new AttendantSCSPanel(this);
		JPanel fixedPanel = new JPanel(new GridBagLayout());
		fixedPanel.setPreferredSize(frame.getSize());
		fixedPanel.setBackground(new Color(9, 11, 16));
		fixedPanel.add(attendantPanel);
		frame.getContentPane().add(fixedPanel);
		frame.validate();
		frame.repaint();
		this.station.screen.setVisible(true); // Displays the JFrame.
		this.addingItems = false;
	}

	/**
	 * Loads up the checkout GUI.
	 */
	public void changeToCheckOutGUI() {
		JFrame frame = this.station.screen.getFrame(); // Gets The JFrame used by the touchscreen listener.
		this.station.screen.setVisible(false);
		frame.getContentPane().removeAll();
		frame.setLayout(new BorderLayout());
		CheckoutSCSPanel checkoutPanel = new CheckoutSCSPanel(this);
		JPanel fixedPanel = new JPanel(new GridBagLayout());
		fixedPanel.setPreferredSize(frame.getSize());
		fixedPanel.setBackground(new Color(9, 11, 16));
		fixedPanel.add(checkoutPanel);
		frame.getContentPane().add(fixedPanel);
		frame.validate();
		frame.repaint();
		this.station.screen.setVisible(true); // Displays the JFrame.
		this.addingItems = false;
	}

	/**
	 * Loads up the checkout GUI.
	 */
	public void startGUI() {
		JFrame frame = this.station.screen.getFrame(); // Gets The JFrame used by the touchscreen listener.
		this.station.screen.setVisible(false);
		frame.getContentPane().removeAll();
		frame.setLayout(new BorderLayout());
		StartScreenPanel checkoutPanel = new StartScreenPanel(this);
		JPanel fixedPanel = new JPanel(new GridBagLayout());
		fixedPanel.setPreferredSize(frame.getSize());
		fixedPanel.setBackground(new Color(9, 11, 16));
		fixedPanel.add(checkoutPanel);
		frame.getContentPane().add(fixedPanel);
		frame.validate();
		frame.repaint();
		this.station.screen.setVisible(true); // Displays the JFrame.
		resetStation();
		this.addingItems = false;
	}

	/**
	 * Disables the GUI.
	 */
	public void disableGUI() {
		this.station.screen.setVisible(false);
	}

	/**
	 * Builds a String to be displayed in the textArea of the Main GUI Screen.
	 * 
	 * @return The String built.
	 */
	public String buildTextAreaString() {
		return "\n  Bagging Scale Weight: " + this.getBaggingAreaWeight() + "\n  Total Price: $" + this.getTotal() + "\n  Member ID: " + this.currentMember;
	}

	public String getCurrentMember() {
		return currentMember;
	}
	
}
