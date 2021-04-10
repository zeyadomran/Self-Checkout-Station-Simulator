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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class MainSCSPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTextArea textArea;
	private ArrayList<ArrayList<String>> scannedItems = new ArrayList<ArrayList<String>>();
	private SelfCheckoutSoftware control;

	/**
	 * Create the panel.
	 */
	public MainSCSPanel(String textAreaText, SelfCheckoutSoftware control) {
		this.control = control;
		ArrayList<BarcodedItem> scannedItems = this.control.getScannedItems();
		ArrayList<PLUCodedItem> pluItems = this.control.getPluItems();

		for (int i = 0; i < scannedItems.size(); i++) {
			BarcodedItem item = scannedItems.get(i);
			BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(item.getBarcode());
			this.scannedItems.add(new ArrayList<String>());
			this.scannedItems.get(i).add(item.getBarcode().toString());
			this.scannedItems.get(i).add(product.getDescription());
			this.scannedItems.get(i).add(product.getPrice().toString());
			this.scannedItems.get(i).add(item.getWeight() + "");
		}

		for (int i = 0; i < pluItems.size(); i++) {
			PLUCodedItem item = pluItems.get(i);
			PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(item.getPLUCode());
			this.scannedItems.add(new ArrayList<String>());
			int index = this.scannedItems.size() - 1;
			this.scannedItems.get(index).add(item.getPLUCode().toString());
			this.scannedItems.get(index).add(product.getDescription());
			BigDecimal price = product.getPrice().multiply(new BigDecimal(item.getWeight())).setScale(2, RoundingMode.CEILING);
			this.scannedItems.get(index).add(price.toString());
			this.scannedItems.get(index).add(item.getWeight() + "");
		}

		this.setForeground(new Color(9, 11, 16));
		this.setBackground(new Color(9, 11, 16));
		this.setMinimumSize(new Dimension(1280, 720));
		this.setMaximumSize(new Dimension(1280, 720));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(new Dimension(1280, 720));
		this.setLayout(null);

		Object tableRows[][] = new Object[this.scannedItems.size() + 1][4];
		Object tableColumns[] = { "Barcode", "Description", "Price", "Weight" };
		tableRows[0][0] = "Barcode";
		tableRows[0][1] = "Description";
		tableRows[0][2] = "Price";
		tableRows[0][3] = "Weight";
		for (int i = 0; i < this.scannedItems.size(); i++) {
			for (int j = 0; j < 4; j++) {
				tableRows[i + 1][j] = this.scannedItems.get(i).get(j);
			}
		}

		table = new JTable(tableRows, tableColumns);
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

		JPanel tablePanel = new JPanel();
		tablePanel.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		tablePanel.setBounds(20, 20, 480, 510);
		tablePanel.setBackground(new Color(15, 17, 26));
		tablePanel.setForeground(new Color(137, 221, 255));
		tablePanel.setLayout(null);
		tablePanel.add(table);
		add(tablePanel);

		textArea = new JTextArea();
		textArea.setText(textAreaText);
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

		JButton scanItemButton = new JButton("Scan Item");
		scanItemButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the Barcode of the item you wish to scan.", "");
				String sWeight = JOptionPane.showInputDialog("Enter the weight of the item you wish to scan.", "");
				if(code.equals("") || sWeight.equals("")) return;
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
				control.refreshGUI();
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
				if(code.equals("")) return;
				Barcode barcode = new Barcode(code);
 				BarcodedItem item = null;
				for(BarcodedItem i : scannedItems) {
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
				control.refreshGUI();
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
				if(code.equals("") || sWeight.equals("")) return;
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
				control.refreshGUI();
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
				if(code.equals("")) return;
				PriceLookupCode plu = new PriceLookupCode(code);
 				PLUCodedItem item = null;
				for(PLUCodedItem i : pluItems) {
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
				control.refreshGUI();
			}
		});
		removePLUItemButton.setOpaque(true);
		removePLUItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removePLUItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removePLUItemButton.setBackground(new Color(130, 170, 255));
		removePLUItemButton.setBounds(980, 175, 280, 55);
		add(removePLUItemButton);

		JButton lookupProductButton = new JButton("Lookup Products");
		lookupProductButton.setOpaque(true);
		lookupProductButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lookupProductButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		lookupProductButton.setBackground(new Color(40, 167, 69));
		lookupProductButton.setBounds(980, 250, 280, 55);
		add(lookupProductButton);

		JButton bagItemButton = new JButton("Bag Item");
		bagItemButton.setOpaque(true);
		bagItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bagItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		bagItemButton.setBackground(new Color(137, 221, 255));
		bagItemButton.setBounds(520, 250, 280, 55);
		add(bagItemButton);

		JButton addOwnBagButton = new JButton("Add Own Bag");
		addOwnBagButton.setOpaque(true);
		addOwnBagButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addOwnBagButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		addOwnBagButton.setBackground(new Color(137, 221, 255));
		addOwnBagButton.setBounds(520, 325, 280, 55);
		add(addOwnBagButton);

		JButton swipeMembCardButton = new JButton("Swipe Membership Card");
		swipeMembCardButton.setOpaque(true);
		swipeMembCardButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		swipeMembCardButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		swipeMembCardButton.setBackground(new Color(193, 142, 227));
		swipeMembCardButton.setBounds(520, 495, 280, 55);
		add(swipeMembCardButton);

		JButton enterMembNumButton = new JButton("Enter Membership Number");
		enterMembNumButton.setOpaque(true);
		enterMembNumButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		enterMembNumButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		enterMembNumButton.setBackground(new Color(193, 142, 227));
		enterMembNumButton.setBounds(520, 570, 280, 55);
		add(enterMembNumButton);

		JButton checkOutButton = new JButton("Check Out");
		checkOutButton.setOpaque(true);
		checkOutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		checkOutButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		checkOutButton.setBackground(new Color(239, 152, 39));
		checkOutButton.setBounds(520, 645, 280, 55);
		add(checkOutButton);

		JButton attendantLogInButton = new JButton("Attendant Login");
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
}
