package org.lsmr.selfcheckout.software;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.external.ProductDatabases;
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
	private BigDecimal amountPaid;
	private BigDecimal changeDue = new BigDecimal("0.00");

	// Listeners
	private CardReaderListenerStub cardReaderListener = new CardReaderListenerStub();
	private BanknoteSlotListenerStub banknoteSlotListener = new BanknoteSlotListenerStub();
	private BanknoteValidatorListenerStub banknoteValidatorListener = new BanknoteValidatorListenerStub();
	private BanknoteDispenserListenerStub banknoteDispenserListener = new BanknoteDispenserListenerStub();
	private CoinValidatorListenerStub coinValidatorListener = new CoinValidatorListenerStub();
	private CoinDispenserListenerStub coinDispenserListener = new CoinDispenserListenerStub();
	private boolean shutDown = false;
	 
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
		
		// load initial bank notes into dispensers 
		for(int i : this.station.banknoteDenominations) {
		    for(int j = 0; j < 100; j++) {
		    	try {
					this.station.banknoteDispensers.get(i).load(new Banknote(i, Currency.getInstance(Locale.CANADA)));
				} catch (SimulationException e) {
					e.printStackTrace();
				} catch (OverloadException e) {
					e.printStackTrace();
				}
		    }
		    //register a listener
		    this.station.banknoteDispensers.get(i).register(banknoteDispenserListener);
		}

		// load coins into dispensers
		for(BigDecimal i : this.station.coinDenominations) {	
		    for(int j = 0; j < 100; j++) {
				try {
					this.station.coinDispensers.get(i).load(new Coin(i, Currency.getInstance(Locale.CANADA)));
				} catch (SimulationException e) {
					e.printStackTrace();
				} catch (OverloadException e) {
					e.printStackTrace();
				}
		    }
		    //register a listener
		    this.station.coinDispensers.get(i).register(coinDispenserListener);
		}

		//load receipt machine with paper and ink
		this.station.printer.addInk(1000);
		this.station.printer.addPaper(10);
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
				this.total = this.total.add(prod.getPrice());
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
			this.total.subtract(prod.getPrice());
			this.inventoryDatabase.put(prod, (this.inventoryDatabase.get(prod) + 1));
			return true;
		} else {
			return false;
		}
	}
	
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
				
				BigDecimal priceOfItem = prod.getPrice().multiply(new BigDecimal(weight));
				
				this.total = this.total.add(priceOfItem);
				
				this.inventoryDatabase.put(prod, inventoryLeft - 1);
				
				this.pluItems.add(item);
				return true;
			}
		} else {
			return false;
		}
		
	}
	
	public boolean removePluItem(PLUCodedItem item) {
		if(item == null) throw new NullPointerException("No argument may be null.");
		if(this.pluItems.contains(item)) {
			PLUCodedProduct prod = this.pluProductDatabase.get(item.getPLUCode());
			this.pluItems.remove(item);
			this.total.subtract(prod.getPrice());
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
	 * Removes an item that was added to the bagging area.
	 * 
	 * @param item
	 * 			The item you wish to remove.
	 * @return If removing the item from the bagging area was a success.
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
	
	public PriceLookupCode lookUpProductCode(String productName)
	{
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
	
	public ArrayList<PLUCodedItem> getPluItems() { return this.pluItems; }
	
	/**
	 * Gets the items on the bagging area.
	 * 
	 * @return An ArrayList of the items in the bagging area.
	 */
	public ArrayList<BarcodedItem> getBaggingArea() { return this.baggingAreaItems; }
	
	public ArrayList<PLUCodedItem> getBaggingAreaPlu() { return this.baggingAreaPluItems; }
	
	/**
	 * Gets the product database.
	 * 
	 * @return The product database.
	 */
	public Map<Barcode, BarcodedProduct> getProductDB() { return this.productDatabase; }
	
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
		if(banknotes == null) throw new NullPointerException("No argument may be null."); // Checks for null params.
		double value = 0.0;
		for(Banknote banknote : banknotes) { // Add value of banknotes.
			value += banknote.getValue();
		}
		this.amountPaid = new BigDecimal(value);

		BigDecimal valueAsBigDecimal = new BigDecimal(value);
		this.changeDue = valueAsBigDecimal.subtract(this.total);

		if (value < this.total.doubleValue()) return false; // return false if banknotes value is not enough.
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
		if(coins == null) throw new NullPointerException("No argument may be null."); // Checks for null params.
		BigDecimal value = new BigDecimal("0");
		for(Coin coin : coins) { // Add value of coins.
			value = value.add(coin.getValue());
		}
		
		this.amountPaid = value;
		this.changeDue = value.subtract(this.total);

		
		if (value.compareTo(this.total) < 0) return false; // return false if coins value is not enough.
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
			this.station.printer.print('\n');
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
			this.station.printer.print('\n');
		}
		
		
		StringBuilder sb = new StringBuilder();
		String total = "Sub Total: ";
		String AmountPaid = "Amount Paid: ";
		sb.append('\n');
		sb.append(total);
		sb.append("    ");
		sb.append(this.total);
		sb.append('\n'); 
		sb.append(AmountPaid);
		sb.append("  ");
		sb.append(this.amountPaid);
		sb.append("\n");
		sb.append("Change Due: ");
		sb.append("   ");
		sb.append(this.changeDue);
		for(int i = 0; i < sb.length(); i++) this.station.printer.print(sb.charAt(i));
		this.station.printer.cutPaper();
		this.receipt = this.station.printer.removeReceipt();
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
	 * @return whether the card used was successfull in paying.
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
		if (totalWeight != getBaggingAreaWeight()) {
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
		this.baggingAreaPluItems.clear();
		this.baggingAreaItems.clear();
		this.personalBags.clear();
		this.total = new BigDecimal("0");
		this.receipt = null;
		this.currentMember = null;
		this.amountPaid = null;
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
		this.station.printer.addInk(amount);
		return true;
	}

	/**
	 * Attendant adds paper to the receipt printer.
	 * 
	 * @return Whether adding paper was successful.
	 */
	public boolean addPaperToPrinter(int amount) {
		this.station.printer.addPaper(amount);
		return true;
	}
	
	/*
	 * Disable all external devices the user can use like the scanners, coin slots, card readers, etc... 
	 */
	public void attendantShutDownStation()
	{
		//if(attendant == logedin)
		{
			this.station.scale.disable();
			this.station.baggingArea.disable();
			this.station.handheldScanner.disable();
			this.station.mainScanner.disable();
			this.station.cardReader.disable();
			this.station.screen.disable();
			this.station.printer.disable();
			this.station.coinSlot.disable();
			this.station.banknoteInput.disable();
			shutDown = true;
		}
	}
	
	/*
	 * enable all external devices the user can use like the scanners, coin slots, card readers, etc... 
	 */
	public void startUpStation()
	{
		//if(attendant == logedin)
		{
			this.station.scale.enable();
			this.station.baggingArea.enable();
			this.station.handheldScanner.enable();
			this.station.mainScanner.enable();
			this.station.cardReader.enable();
			this.station.screen.enable();
			this.station.printer.enable();
			this.station.coinSlot.enable();
			this.station.banknoteInput.enable();
			shutDown  = false;
		}
	}

	
	public boolean isShutDown()
	{
		return this.shutDown;
	}
}


