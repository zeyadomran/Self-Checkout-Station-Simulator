package org.lsmr.selfcheckout.gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Cursor;
import javax.swing.JLabel;
import java.awt.Font;

public class MainSCSPanel extends JPanel {
	private JTable table;

	/**
	 * Create the panel.
	 */
	public MainSCSPanel() {
		setForeground(new Color(9, 11, 16));
		setBackground(new Color(9, 11, 16));
		setSize(new Dimension(1280, 720));
		setLayout(null);
		
		table = new JTable();
		table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		table.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		table.setBounds(20, 20, 480, 510);
		table.setBackground(new Color(15, 17, 26));
		add(table);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(20, 550, 480, 150);
		textArea.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		textArea.setBackground(new Color(15, 17, 26));
		add(textArea);
		
		JButton scanItemButton = new JButton("Scan Item");
		scanItemButton.setOpaque(true);
		scanItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		scanItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		scanItemButton.setBackground(new Color(255, 203, 107));
		scanItemButton.setBounds(520, 100, 280, 55);
		add(scanItemButton);

		JButton removeItemButton = new JButton("Remove Item");
		removeItemButton.setOpaque(true);
		removeItemButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeItemButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removeItemButton.setBackground(new Color(255, 203, 107));
		removeItemButton.setBounds(980, 100, 280, 55);
		add(removeItemButton);

		JButton enterPLUButton = new JButton("Enter Item PLU Code");
		enterPLUButton.setOpaque(true);
		enterPLUButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		enterPLUButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		enterPLUButton.setBackground(new Color(130, 170, 255));
		enterPLUButton.setBounds(520, 170, 280, 55);
		add(enterPLUButton);

		JButton lookupPLUButton = new JButton("Lookup Product");
		lookupPLUButton.setOpaque(true);
		lookupPLUButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lookupPLUButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		lookupPLUButton.setBackground(new Color(130, 170, 255));
		lookupPLUButton.setBounds(980, 175, 280, 55);
		add(lookupPLUButton);

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
