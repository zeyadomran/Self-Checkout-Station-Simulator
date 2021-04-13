package org.lsmr.selfcheckout.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.software.AttendantDatabase;
import org.lsmr.selfcheckout.software.MemberDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class MainSCSPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTextArea textArea;
	private ArrayList<ArrayList<String>> scannedItems;
	private SelfCheckoutSoftware control;
	/**
	 * Create the panel.
	 */
	public MainSCSPanel(SelfCheckoutSoftware control) {
		this.control = control;
		control.returnToAddingItems();
		
		this.setForeground(new Color(9, 11, 16));
		this.setBackground(new Color(9, 11, 16));
		this.setMinimumSize(new Dimension(1280, 720));
		this.setMaximumSize(new Dimension(1280, 720));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(new Dimension(1280, 720));
		this.setLayout(null);

		table = new JTable();
		table.setEnabled(false);
		table.setFocusTraversalKeysEnabled(false);
		table.setFocusable(false);
		table.setRequestFocusEnabled(false);
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setForeground(new Color(137, 221, 255));
		table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		table.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		table.setBounds(0, 0, 480, 510);
		table.setBackground(new Color(15, 17, 26));

		JScrollPane tableScrollPanel = new JScrollPane();
		tableScrollPanel.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		tableScrollPanel.setBounds(20, 20, 480, 510);
		tableScrollPanel.setBackground(new Color(15, 17, 26));
		tableScrollPanel.setForeground(new Color(137, 221, 255));
		tableScrollPanel.setLayout(null);
		tableScrollPanel.add(table);
		add(tableScrollPanel);

		textArea = new JTextArea();
		textArea.validate();
		textArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		textArea.setForeground(new Color(137, 221, 255));
		textArea.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setBounds(20, 550, 480, 150);
		textArea.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		textArea.setBackground(new Color(15, 17, 26));
		add(textArea);
		updatePanel(control.buildTextAreaString());

		JButton scanItemButton = new JButton("Scan Item");
		scanItemButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the Barcode of the item you wish to scan.", "");
				String sWeight = JOptionPane.showInputDialog("Enter the weight of the item you wish to scan.", "");
				if(code.equals("") || sWeight.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				double weight = Double.parseDouble(sWeight);
				if(weight <= 0) return;
				Barcode barcode = new Barcode(code);
				boolean success = control.scanItem(barcode, weight);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was Scanned!",
						"Scan Item Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was not Scanned!",
						"Scan Item Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		scanItemButton.setOpaque(true);
		scanItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		scanItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		scanItemButton.setBackground(new Color(255, 203, 107));
		scanItemButton.setBounds(520, 100, 280, 55);
		add(scanItemButton);

		JButton removeItemButton = new JButton("Remove Barcoded Item");
		removeItemButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the Barcode of the item you wish to remove.", "");
				if(code.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				Barcode barcode = new Barcode(code);
 				BarcodedItem item = null;
				for(BarcodedItem i : control.getScannedItems()) {
					if(i.getBarcode().equals(barcode)) item = i;
				}
				boolean success;
				if(item != null) {
					success = control.removeScannedItem(item);
				} else {
					success = false;
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was removed!",
						"Remove Item Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was not removed!",
						"Remove Item Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		removeItemButton.setOpaque(true);
		removeItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removeItemButton.setBackground(new Color(255, 203, 107));
		removeItemButton.setBounds(980, 100, 280, 55);
		add(removeItemButton);

		JButton enterPLUButton = new JButton("Enter Item PLU Code");
		enterPLUButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the PLU of the item you wish to add.", "");
				String sWeight = JOptionPane.showInputDialog("Enter the weight of the item you wish to add.", "");
				if(code.equals("") || sWeight.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				double weight = Double.parseDouble(sWeight);
				if(weight <= 0) return;
				PriceLookupCode plu = new PriceLookupCode(code);
				boolean success = control.addPLUItem(plu, weight);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was added!",
						"PLU Item Add Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was not added!",
						"PLU Item Add Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		enterPLUButton.setOpaque(true);
		enterPLUButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		enterPLUButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		enterPLUButton.setBackground(new Color(130, 170, 255));
		enterPLUButton.setBounds(520, 170, 280, 55);
		add(enterPLUButton);

		JButton removePLUItemButton = new JButton("Remove PLU Item");
		removePLUItemButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the Barcode of the item you wish to remove.", "");
				if(code.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				PriceLookupCode plu = new PriceLookupCode(code);
 				PLUCodedItem item = null;
				for(PLUCodedItem i : control.getPluItems()) {
					if(i.getPLUCode().equals(plu)) item = i;
				}
				boolean success;
				if(item != null) {
					success = control.removePluItem(item);
				} else {
					success = false;
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was removed!",
						"Remove PLU Item Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was not removed!",
						"Remove PLU Item Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		removePLUItemButton.setOpaque(true);
		removePLUItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removePLUItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removePLUItemButton.setBackground(new Color(130, 170, 255));
		removePLUItemButton.setBounds(980, 175, 280, 55);
		add(removePLUItemButton);

		JButton lookupProductButton = new JButton("Lookup Barcoded Products");
		lookupProductButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Object itemsInDB[] = ProductDatabases.BARCODED_PRODUCT_DATABASE.values().toArray();
				int i = 0;
				int ctn = JOptionPane.YES_OPTION;
				while(i < itemsInDB.length && ctn == JOptionPane.YES_OPTION) {
					String text = "";
						for(int j = 0; j < 4; j++) {
							if(itemsInDB.length > i) {
								text += "\nBarcode: " + ((BarcodedProduct) itemsInDB[i]).getBarcode() + " | Description: " + ((BarcodedProduct) itemsInDB[i]).getDescription();
								i++;
							}
						}
					Object[] options = { "Next Page", "Go Back" };
					ctn = JOptionPane.showOptionDialog(
						new JPanel(), 
						text, 
						"Barcoded Items", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.PLAIN_MESSAGE, 
						null, 
						options, 
						options[0]
					);
				}
				if(i == itemsInDB.length && ctn == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(new JPanel(),
						"No more items to display!",
						"You viewed all the items!",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		lookupProductButton.setOpaque(true);
		lookupProductButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lookupProductButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		lookupProductButton.setBackground(new Color(40, 167, 69));
		lookupProductButton.setBounds(980, 250, 280, 55);
		add(lookupProductButton);

		JButton lookupPLUButton = new JButton("Lookup PLU Products");
		lookupPLUButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Object itemsInDB[] = ProductDatabases.PLU_PRODUCT_DATABASE.values().toArray();
				int i = 0;
				int ctn = JOptionPane.YES_OPTION;
				while(i < itemsInDB.length && ctn == JOptionPane.YES_OPTION) {
					String text = "";
						for(int j = 0; j < 4; j++) {
							if(itemsInDB.length > i) {
								text += "\nPLU: " + ((PLUCodedProduct) itemsInDB[i]).getPLUCode() + " | Description: " + ((PLUCodedProduct) itemsInDB[i]).getDescription();
								i++;
							}
						}
					Object[] options = { "Next Page", "Go Back" };
					ctn = JOptionPane.showOptionDialog(
						new JPanel(), 
						text, 
						"PLU Items", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.PLAIN_MESSAGE, 
						null, 
						options, 
						options[0]
					);
				}
				if(i == itemsInDB.length && ctn == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(new JPanel(),
						"No more items to display!",
						"You viewed all the items!",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		lookupPLUButton.setOpaque(true);
		lookupPLUButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lookupPLUButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		lookupPLUButton.setBackground(new Color(40, 167, 69));
		lookupPLUButton.setBounds(980, 325, 280, 55);
		add(lookupPLUButton);

		JButton bagItemButton = new JButton("Bag Item");
		bagItemButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the Barcode of the item you wish to bag.", "");
				if(code.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				Barcode barcode = new Barcode(code);
				boolean success = control.placeItemInBaggingArea(barcode);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was added to bagging area!",
						"Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was not added to bagging area!",
						"Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		bagItemButton.setOpaque(true);
		bagItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bagItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		bagItemButton.setBackground(new Color(137, 221, 255));
		bagItemButton.setBounds(520, 250, 280, 55);
		add(bagItemButton);

		JButton addPLUItemBagButton = new JButton("Add PLU Item to bagging area");
		addPLUItemBagButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the PLU of the item you wish to bag.", "");
				if(code.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				PriceLookupCode plu = new PriceLookupCode(code);
				boolean success = control.placePluItemInBaggingArea(plu);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was added to bagging area!",
						"Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was not added to bagging area!",
						"Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		addPLUItemBagButton.setOpaque(true);
		addPLUItemBagButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addPLUItemBagButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		addPLUItemBagButton.setBackground(new Color(137, 221, 255));
		addPLUItemBagButton.setBounds(520, 325, 280, 55);
		add(addPLUItemBagButton);

		JButton removeItemFromBAButton = new JButton("Remove Item from bagging area");
		removeItemFromBAButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the Barcode of the item you wish to remove from the bagging area.", "");
				if(code.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				Barcode barcode = new Barcode(code);
 				BarcodedItem item = null;
				for(BarcodedItem i : control.getScannedItems()) {
					if(i.getBarcode().equals(barcode)) item = i;
				}
				boolean success;
				if(item != null) {
					success = control.removeItemBaggingArea(item);
				} else {
					success = false;
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was removed!",
						"Remove Item Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was not removed!",
						"Remove Item Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		removeItemFromBAButton.setOpaque(true);
		removeItemFromBAButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeItemFromBAButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removeItemFromBAButton.setBackground(new Color(137, 221, 255));
		removeItemFromBAButton.setBounds(520, 400, 280, 55);
		add(removeItemFromBAButton);

		JButton removePLUItemFromBAButton = new JButton("Remove PLU Item from bagging area");
		removePLUItemFromBAButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the PLU of the item you wish to remove from the bagging area.", "");
				if(code.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				PriceLookupCode plu = new PriceLookupCode(code);
 				PLUCodedItem item = null;
				for(PLUCodedItem i : control.getBaggingAreaPlu()) {
					if(i.getPLUCode().equals(plu)) item = i;
				}
				boolean success;
				if(item != null) {
					success = control.removePluItemBaggingArea(item);
				} else {
					success = false;
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was removed!",
						"Remove Item Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + plu + " was not removed!",
						"Remove Item Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		removePLUItemFromBAButton.setOpaque(true);
		removePLUItemFromBAButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removePLUItemFromBAButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removePLUItemFromBAButton.setBackground(new Color(137, 221, 255));
		removePLUItemFromBAButton.setBounds(980, 400, 280, 55);
		add(removePLUItemFromBAButton);

		JButton addOwnBagButton = new JButton("Add Own Bag");
		addOwnBagButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sWeight = JOptionPane.showInputDialog("Enter the weight of your bag.", "");
				if(sWeight.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				double weight = Double.parseDouble(sWeight);
				boolean success = control.addOwnBag(weight);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Bag was successfully added!",
						"Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Bag was not added!",
						"Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		addOwnBagButton.setOpaque(true);
		addOwnBagButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addOwnBagButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		addOwnBagButton.setBackground(new Color(137, 221, 255));
		addOwnBagButton.setBounds(980, 495, 280, 55);
		add(addOwnBagButton);

		JButton swipeMembCardButton = new JButton("Swipe Membership Card");
		swipeMembCardButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String memberID = JOptionPane.showInputDialog("Please enter your membership card number: ", "");
				if(memberID.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				boolean success = false;
				if(MemberDatabase.REGISTERED_MEMBERS.containsKey(memberID)) {
					success = control.swipeMembershipCard(MemberDatabase.REGISTERED_MEMBERS.get(memberID).getMemberCard());
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Log In Successful!",
						"Member Logged In!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Couldn't Log In!",
						"Membership Log In Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		swipeMembCardButton.setOpaque(true);
		swipeMembCardButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		swipeMembCardButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		swipeMembCardButton.setBackground(new Color(193, 142, 227));
		swipeMembCardButton.setBounds(520, 495, 280, 55);
		add(swipeMembCardButton);

		JButton enterMembNumButton = new JButton("Enter Membership Number");
		enterMembNumButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String memberID = JOptionPane.showInputDialog("Please enter your ID: ", "");
				if(memberID.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				boolean success = false;
				if(MemberDatabase.REGISTERED_MEMBERS.containsKey(memberID)) {
					success = control.enterMembershipInfo(memberID);
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Log In Successful!",
						"Member Logged In!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Couldn't Log In!",
						"Membership Log In Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		enterMembNumButton.setOpaque(true);
		enterMembNumButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		enterMembNumButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		enterMembNumButton.setBackground(new Color(193, 142, 227));
		enterMembNumButton.setBounds(520, 570, 280, 55);
		add(enterMembNumButton);

		JButton createMembButton = new JButton("Create Membership");
		createMembButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String name = JOptionPane.showInputDialog("Please enter your name: ", "");
				if(name.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				String memberID = (MemberDatabase.REGISTERED_MEMBERS.size() + 1) + "";
				control.addMember(name, memberID);
				if(MemberDatabase.REGISTERED_MEMBERS.containsKey(memberID)) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Member: " + name + " was created with ID:" + memberID + "!",
						"Membership Created!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Couldn't Create Membership!",
						"Membership Creation Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel(control.buildTextAreaString());
			}
		});
		createMembButton.setOpaque(true);
		createMembButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		createMembButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		createMembButton.setBackground(new Color(193, 142, 227));
		createMembButton.setBounds(980, 570, 280, 55);
		add(createMembButton);

		JButton checkOutButton = new JButton("Check Out");
		checkOutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if(!control.checkWeight()) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Please add all items to the bagging area, or call an attendant!",
						"Weight Discrepancy!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				int bagsUsed = Integer.parseInt(JOptionPane.showInputDialog("How many bags did you use?", ""));
				control.enterNumberOfBags(bagsUsed);
				control.changeToCheckOutGUI();
			}
		});
		checkOutButton.setOpaque(true);
		checkOutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		checkOutButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		checkOutButton.setBackground(new Color(239, 152, 39));
		checkOutButton.setBounds(520, 645, 280, 55);
		add(checkOutButton);

		JButton attendantLogInButton = new JButton("Attendant Login");
		attendantLogInButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String attendantID = JOptionPane.showInputDialog("Please enter your ID: ", "");
				if(attendantID.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(AttendantDatabase.REGISTERED_ATTENDANTS.containsKey(attendantID)) {
					control.attendantLogin(attendantID);
				}
				if(control.getattendantLoggedIn()) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Log In Successful!",
						"Attendant Logged In!",
						JOptionPane.PLAIN_MESSAGE);
					control.changeToAttendantGUI();
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Couldn't Log In!",
						"Attendant Log In Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		attendantLogInButton.setOpaque(true);
		attendantLogInButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		attendantLogInButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		attendantLogInButton.setBackground(new Color(40, 167, 69));
		attendantLogInButton.setBounds(980, 645, 280, 55);
		add(attendantLogInButton);

		JLabel TitleLabel = new JLabel("Self Checkout Station");
		TitleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 60));
		TitleLabel.setForeground(new Color(64, 224, 208));
		TitleLabel.setBounds(520, 20, 656, 72);
		add(TitleLabel);
	}

	private void updatePanel(String textAreaText) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textArea.setText(""+textAreaText);
				DefaultTableModel model = new DefaultTableModel();
				model.addColumn("Barcode");
				model.addColumn("Description");
				model.addColumn("Price");
				model.addColumn("Weight");
				initScannedItems();
				for (ArrayList<String> i : scannedItems) {
					String data[] = new String[4];
					data[0] = i.get(0);
					data[1] = i.get(1);
					data[2] = i.get(2);
					data[3] = i.get(3);
					model.addRow(data);
				}
				table.setModel(model);
			}
		});
	}

	private void initScannedItems() {
		ArrayList<BarcodedItem> scannedItemss = control.getScannedItems();
		ArrayList<PLUCodedItem> pluItems = control.getPluItems();
		scannedItems = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < scannedItemss.size(); i++) {
			BarcodedItem item = scannedItemss.get(i);
			BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(item.getBarcode());
			scannedItems.add(new ArrayList<String>());
			scannedItems.get(i).add(item.getBarcode().toString());
			scannedItems.get(i).add(product.getDescription());
			scannedItems.get(i).add(product.getPrice().toString());
			scannedItems.get(i).add(item.getWeight() + "");
		}

		for (int i = 0; i < pluItems.size(); i++) {
			PLUCodedItem item = pluItems.get(i);
			PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(item.getPLUCode());
			scannedItems.add(new ArrayList<String>());
			int index = scannedItems.size() - 1;
			scannedItems.get(index).add(item.getPLUCode().toString());
			scannedItems.get(index).add(product.getDescription());
			BigDecimal price = product.getPrice().multiply(new BigDecimal((item.getWeight()/1000) + "")).setScale(2, RoundingMode.CEILING);
			scannedItems.get(index).add(price.toString());
			scannedItems.get(index).add(item.getWeight() + "");
		}
	}
}
