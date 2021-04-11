package org.lsmr.selfcheckout.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class AttendantSCSPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private SelfCheckoutSoftware control;


	/**
	 * Create the panel.
	 */
	public AttendantSCSPanel(SelfCheckoutSoftware control) {
		this.control = control;
		
		this.setForeground(new Color(9, 11, 16));
		this.setBackground(new Color(9, 11, 16));
		this.setMinimumSize(new Dimension(1280, 720));
		this.setMaximumSize(new Dimension(1280, 720));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(new Dimension(1280, 720));
		this.setLayout(null);

		JLabel TitleLabel = new JLabel("Attendant ID: " + control.currentAttendant.getAttendantID());
		TitleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 60));
		TitleLabel.setForeground(new Color(64, 224, 208));
		TitleLabel.setBounds(20, 20, 720, 72);
		add(TitleLabel);

		JButton logOutButton = new JButton("Log Out");
		logOutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				control.attendantLogOut();
				control.loadMainGUI();
			}
		});
		logOutButton.setOpaque(true);
		logOutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		logOutButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		logOutButton.setBackground(new Color(204, 62, 68));
		logOutButton.setBounds(60, 645, 280, 55);
		add(logOutButton);

		JButton approveWeightButton = new JButton("Approve Weight Discrepancy");
		approveWeightButton.setOpaque(true);
		approveWeightButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		approveWeightButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		approveWeightButton.setBackground(new Color(230, 152, 39));
		approveWeightButton.setBounds(60, 185, 280, 55);
		add(approveWeightButton);

		JButton lookUpProduct = new JButton("Lookup Product");
		lookUpProduct.setOpaque(true);
		lookUpProduct.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lookUpProduct.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		lookUpProduct.setBackground(new Color(230, 152, 39));
		lookUpProduct.setBounds(60, 335, 280, 55);
		add(lookUpProduct);

		JButton removePurchaseButton = new JButton("Remove Product from Purchases");
		removePurchaseButton.setOpaque(true);
		removePurchaseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removePurchaseButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removePurchaseButton.setBackground(new Color(230, 152, 39));
		removePurchaseButton.setBounds(60, 480, 280, 55);
		add(removePurchaseButton);

		JButton startUpButton = new JButton("Start Up Station");
		startUpButton.setOpaque(true);
		startUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		startUpButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		startUpButton.setBackground(new Color(40, 167, 69));
		startUpButton.setBounds(500, 645, 280, 55);
		add(startUpButton);

		JButton addPaperButton = new JButton("Add Paper to Printer");
		addPaperButton.setOpaque(true);
		addPaperButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addPaperButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		addPaperButton.setBackground(new Color(230, 152, 39));
		addPaperButton.setBounds(500, 185, 280, 55);
		add(addPaperButton);

		JButton addInkButton = new JButton("Add Ink to Printer");
		addInkButton.setOpaque(true);
		addInkButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addInkButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		addInkButton.setBackground(new Color(230, 152, 39));
		addInkButton.setBounds(500, 335, 280, 55);
		add(addInkButton);

		JButton blockStationButton = new JButton("Block Station");
		blockStationButton.setOpaque(true);
		blockStationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		blockStationButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		blockStationButton.setBackground(new Color(230, 152, 39));
		blockStationButton.setBounds(500, 480, 280, 55);
		add(blockStationButton);

		JButton shutDownButton = new JButton("Shut Down Station");
		shutDownButton.setOpaque(true);
		shutDownButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		shutDownButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		shutDownButton.setBackground(new Color(204, 62, 68));
		shutDownButton.setBounds(940, 645, 280, 55);
		add(shutDownButton);

		JButton emptyCoinButton = new JButton("Empty Coin Storage");
		emptyCoinButton.setOpaque(true);
		emptyCoinButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		emptyCoinButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		emptyCoinButton.setBackground(new Color(230, 152, 39));
		emptyCoinButton.setBounds(940, 185, 280, 55);
		add(emptyCoinButton);

		JButton emptyBankNoteButton = new JButton("Empty Banknote Storage");
		emptyBankNoteButton.setOpaque(true);
		emptyBankNoteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		emptyBankNoteButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		emptyBankNoteButton.setBackground(new Color(230, 152, 39));
		emptyBankNoteButton.setBounds(940, 335, 280, 55);
		add(emptyBankNoteButton);

		JButton refillCoinButton = new JButton("Refill Coin Dispensers");
		refillCoinButton.setOpaque(true);
		refillCoinButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		refillCoinButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		refillCoinButton.setBackground(new Color(230, 152, 39));
		refillCoinButton.setBounds(940, 480, 280, 55);
		add(refillCoinButton);

		JButton refillBankNoteButton = new JButton("Refill Banknote Dispensers");
		refillBankNoteButton.setOpaque(true);
		refillBankNoteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		refillBankNoteButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		refillBankNoteButton.setBackground(new Color(230, 152, 39));
		refillBankNoteButton.setBounds(940, 565, 280, 55);
		add(refillBankNoteButton);
	}
}
