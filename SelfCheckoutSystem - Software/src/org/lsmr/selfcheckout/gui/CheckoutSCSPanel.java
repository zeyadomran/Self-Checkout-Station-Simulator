package org.lsmr.selfcheckout.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class CheckoutSCSPanel extends JPanel {
	private SelfCheckoutSoftware control;
	private JTextArea receipt;
	private JTextArea infoText;
	private BigDecimal entered = new BigDecimal("0");
	private ArrayList<Coin> coins = new ArrayList<Coin>();

	/**
	 * Create the panel.
	 */
	public CheckoutSCSPanel(SelfCheckoutSoftware control) {
		this.control = control;
		
		this.setForeground(new Color(9, 11, 16));
		this.setBackground(new Color(9, 11, 16));
		this.setMinimumSize(new Dimension(1280, 720));
		this.setMaximumSize(new Dimension(1280, 720));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(new Dimension(1280, 720));
		this.setLayout(null);
		
		receipt = new JTextArea("\n Receipt:" );
		receipt.validate();
		receipt.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		receipt.setForeground(new Color(137, 221, 255));
		receipt.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		receipt.setEditable(false);
		receipt.setLineWrap(true);
		receipt.setBounds(20, 20, 480, 510);
		receipt.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		receipt.setBackground(new Color(15, 17, 26));
		add(receipt);

		infoText = new JTextArea();
		infoText.validate();
		infoText.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		infoText.setForeground(new Color(137, 221, 255));
		infoText.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		infoText.setEditable(false);
		infoText.setLineWrap(true);
		infoText.setBounds(20, 550, 480, 150);
		infoText.setBorder(new LineBorder(new Color(137, 221, 255), 1, true));
		infoText.setBackground(new Color(15, 17, 26));
		add(infoText);
		
		updatePanel();
		
		JButton returnToAddingItems = new JButton("Return To Adding Items");
		returnToAddingItems.setOpaque(true);
		returnToAddingItems.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		returnToAddingItems.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		returnToAddingItems.setBackground(new Color(204, 62, 68));
		returnToAddingItems.setBounds(980, 475, 280, 55);
		add(returnToAddingItems);

		JButton credit = new JButton("Pay with Credit Card");
		credit.setOpaque(true);
		credit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		credit.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		credit.setBackground(new Color(255, 203, 107));
		credit.setBounds(980, 400, 280, 55);
		add(credit);
		
		JButton debit = new JButton("Pay with Debit Card");
		debit.setOpaque(true);
		debit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		debit.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		debit.setBackground(new Color(255, 203, 107));
		debit.setBounds(980, 325, 280, 55);
		add(debit);
		
		JButton giftCard = new JButton("Pay with Giftcard");
		giftCard.setOpaque(true);
		giftCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		giftCard.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		giftCard.setBackground(new Color(255, 203, 107));
		giftCard.setBounds(980, 250, 280, 55);
		add(giftCard);
		
		JButton cash = new JButton("Pay with Cash");
		cash.setOpaque(true);
		cash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cash.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		cash.setBackground(new Color(255, 203, 107));
		cash.setBounds(980, 175, 280, 55);
		add(cash);
		
		JButton coin = new JButton("Pay with Coins");
		coin.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			String coinValue = JOptionPane.showInputDialog("Enter the value of the coin you wish to enter.", "");
			if(coinValue.equals("") || coinValue.equals("")) {
				JOptionPane.showMessageDialog(new JPanel(),
					"Invalid Inputs!",
					"Please Try Again!",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			BigDecimal value = new BigDecimal(coinValue);
			Coin coin = new Coin(value, Currency.getInstance(getLocale()));
			coins.add(coin);
			entered = entered.add(value);
			boolean success = false; 
			if(entered.compareTo(control.getTotal()) >= 0) {
				try {
					success = control.payWithCoin(coins);
				} catch (Exception err) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Could not pay with coin entered!",
						"Coin Enter Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
			}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"You have payed enough coins!",
						"Coin Entered Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Coin: " + value + " was not entered!",
						"Coin Enter Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		coin.setOpaque(true);
		coin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		coin.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		coin.setBackground(new Color(255, 203, 107));
		coin.setBounds(980, 100, 280, 55);
		add(coin);

		JLabel TitleLabel = new JLabel("Check Out");
		TitleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 60));
		TitleLabel.setForeground(new Color(64, 224, 208));
		TitleLabel.setBounds(520, 20, 656, 72);
		add(TitleLabel);
	}

	private void updatePanel() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoText.setText(""+getTextAreaText());
			}
		});
	}

	private String getTextAreaText() {
		return "\n Total: " + control.getTotal().toString() + "\n Entered: " + control.amountEntered + "\n Change Due: "+ entered.subtract(control.getTotal());
	}
}
