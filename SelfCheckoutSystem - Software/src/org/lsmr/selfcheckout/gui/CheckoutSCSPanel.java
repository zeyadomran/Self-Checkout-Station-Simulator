package org.lsmr.selfcheckout.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class CheckoutSCSPanel extends JPanel {
	private SelfCheckoutSoftware control;
	private JTextArea receipt;
	private JTextArea infoText;
	private BigDecimal entered;
	private ArrayList<Coin> coins = new ArrayList<Coin>();
	private ArrayList<Banknote> banknotes = new ArrayList<Banknote>();
	/**
	 * Create the panel.
	 */
	public CheckoutSCSPanel(SelfCheckoutSoftware control) {
		this.control = control;
		entered = control.amountEntered;

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
		returnToAddingItems.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				control.loadMainGUI();
			}
		});
		returnToAddingItems.setOpaque(true);
		returnToAddingItems.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		returnToAddingItems.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		returnToAddingItems.setBackground(new Color(204, 62, 68));
		returnToAddingItems.setBounds(980, 475, 280, 55);
		add(returnToAddingItems);

		JButton credit = new JButton("Pay with Credit Card");
		credit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String cardNum = JOptionPane.showInputDialog("Please enter the number of the credit card!", "");
				if(cardNum.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(control.creditCards.containsKey(cardNum)) {
					Card card = control.creditCards.get(cardNum);
					Object[] options = { "Tap", "Insert", "Swipe" };
					int cardPayType = JOptionPane.showOptionDialog(
						new JPanel(), 
						"Please choose how you wish to use your card.", 
						"Pay With Credit Card!", 
						JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.PLAIN_MESSAGE, 
						null, 
						options, 
						options[0]
					);
					boolean success = false;
					try {
						if(cardPayType == 0) {
							success = control.tapCard(card);
						} else if(cardPayType == 1) {
							String pin = JOptionPane.showInputDialog("Please enter the card's pin!", "");
							success = control.insertCard(card, pin);
						} else if(cardPayType == 2) {
							success = control.swipeCard(card, null);
						} else {
							JOptionPane.showMessageDialog(new JPanel(),
								"You did not choose a valid method!",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
								return;
						}
					} catch (Exception err) {
						success = false;
					}
					if(success) {
						entered = entered.add(control.getTotal());
						control.amountEntered = entered;
						JOptionPane.showMessageDialog(new JPanel(),
							"Payment Successful!",
							"Success!",
							JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(new JPanel(),
						"Payment Failed!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Card DNE!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
						return;
				}
				updatePanel();
			}
		});
		credit.setOpaque(true);
		credit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		credit.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		credit.setBackground(new Color(255, 203, 107));
		credit.setBounds(980, 400, 280, 55);
		add(credit);
		
		JButton debit = new JButton("Pay with Debit Card");
		debit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String cardNum = JOptionPane.showInputDialog("Please enter the number of the debit card!", "");
				if(cardNum.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(control.debitCards.containsKey(cardNum)) {
					Card card = control.debitCards.get(cardNum);
					Object[] options = { "Tap", "Insert", "Swipe" };
					int cardPayType = JOptionPane.showOptionDialog(
						new JPanel(), 
						"Please choose how you wish to use your card.", 
						"Pay With Debit Card!", 
						JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.PLAIN_MESSAGE, 
						null, 
						options, 
						options[0]
					);
					boolean success = false;
					try {
						if(cardPayType == 0) {
							success = control.tapCard(card);
						} else if(cardPayType == 1) {
							String pin = JOptionPane.showInputDialog("Please enter the card's pin!", "");
							success = control.insertCard(card, pin);
						} else if(cardPayType == 2) {
							success = control.swipeCard(card, null);
						} else {
							JOptionPane.showMessageDialog(new JPanel(),
								"You did not choose a valid method!",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
								return;
						}
					} catch (Exception err) {
						success = false;
					}
					if(success) {
						entered = entered.add(control.getTotal());
						control.amountEntered = entered;
						JOptionPane.showMessageDialog(new JPanel(),
							"Payment Successful!",
							"Success!",
							JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(new JPanel(),
						"Payment Failed!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Card DNE!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
						return;
				}
				updatePanel();
			}
		});
		debit.setOpaque(true);
		debit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		debit.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		debit.setBackground(new Color(255, 203, 107));
		debit.setBounds(980, 325, 280, 55);
		add(debit);
		
		JButton giftCard = new JButton("Pay with Giftcard");
		giftCard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String cardNum = JOptionPane.showInputDialog("Please enter the number of the gift card!", "");
				if(cardNum.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(control.giftCards.containsKey(cardNum)) {
					Card card = control.giftCards.get(cardNum);
					Object[] options = { "Swipe", "Insert" };
					int cardPayType = JOptionPane.showOptionDialog(
						new JPanel(), 
						"Please choose how you wish to use your card.", 
						"Pay With Gift Card!", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.PLAIN_MESSAGE, 
						null, 
						options, 
						options[0]
					);
					boolean success = false;
					try {
						if(cardPayType == 0) {
							success = control.swipeCard(card, null);
						} else if(cardPayType == 1) {
							String pin = JOptionPane.showInputDialog("Please enter the card's pin!", "");
							success = control.insertCard(card, pin);
						} else {
							JOptionPane.showMessageDialog(new JPanel(),
								"You did not choose a valid method!",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
								return;
						}
					} catch (Exception err) {
						success = false;
					}
					if(success) {
						entered = entered.add(control.getTotal());
						control.amountEntered = entered;
						JOptionPane.showMessageDialog(new JPanel(),
							"Payment Successful!",
							"Success!",
							JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(new JPanel(),
						"Payment Failed!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Card DNE!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
						return;
				}
				updatePanel();
			}
		});
		giftCard.setOpaque(true);
		giftCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		giftCard.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		giftCard.setBackground(new Color(255, 203, 107));
		giftCard.setBounds(980, 250, 280, 55);
		add(giftCard);
		
		JButton cash = new JButton("Pay with Cash");
		cash.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sBanknoteVal = JOptionPane.showInputDialog("Enter the value of the coin you wish to enter.", "");
				if(sBanknoteVal.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				int value = Integer.parseInt(sBanknoteVal);
				Banknote banknote = new Banknote(value, control.getStation().coinValidator.currency);
				banknotes.add(banknote);
				boolean success = false; 
				try {
					success = control.payWithCash(banknotes);
					if(control.banknoteValidatorListener.isValidBanknote()) {
						entered = entered.add(new BigDecimal(value));
						control.amountEntered = entered;
					} else {
						JOptionPane.showMessageDialog(new JPanel(),
							"Invalid Denomination!",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
							return;
					}
					if(success) {
						JOptionPane.showMessageDialog(new JPanel(),
							"You have payed enough!",
							"Banknote Entered Success!",
							JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(new JPanel(),
							"You still need to pay more!",
							"Not Enough!",
							JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception err) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Could not pay with banknote entered!",
						"Banknote Enter Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
				updatePanel();
			}
		});
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
			if(coinValue.equals("")) {
				JOptionPane.showMessageDialog(new JPanel(),
					"Invalid Inputs!",
					"Please Try Again!",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			BigDecimal value = new BigDecimal(coinValue);
			Coin coin = new Coin(value, control.getStation().coinValidator.currency);
			coins.add(coin);
			boolean success = false; 
			try {
				success = control.payWithCoin(coins);
				if(control.coinValidatorListener.getIsValid()) {
					entered = entered.add(value);
					control.amountEntered = entered;
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Denomination!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
						return;
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"You have payed enough!",
						"Coin Entered Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"You still need to pay more!",
						"Not Enough!",
						JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception err) {
				JOptionPane.showMessageDialog(new JPanel(),
					"Could not pay with coin entered!",
					"Coin Enter Failed!",
					JOptionPane.ERROR_MESSAGE);
			}
			updatePanel();
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
				if(entered.compareTo(control.getTotal()) >= 0) {
					control.generateReceipt();
					receipt.setText("" + control.getReceipt());
				}
			}
		});
	}

	private String getTextAreaText() {
		return "\n Total: " + control.getTotal().toString() + "\n Entered: " + entered.doubleValue() + "\n Change Due: "+ entered.subtract(control.getTotal());
	}
}
