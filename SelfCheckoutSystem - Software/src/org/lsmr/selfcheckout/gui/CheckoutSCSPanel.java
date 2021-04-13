package org.lsmr.selfcheckout.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class CheckoutSCSPanel extends JPanel {
	private SelfCheckoutSoftware control;
	BigDecimal entered = new BigDecimal("0");
	ArrayList<Coin> coins = new ArrayList<Coin>();


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
		
		JTextArea textArea = new JTextArea("Total : " + control.getTotal().toString() + "\n" + "Entered: " + entered + "\n" + "Change Due: 0.00" );
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
		
		
		JButton credit = new JButton("Pay with Credit Card");
		credit.setOpaque(true);
		credit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		credit.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		credit.setBackground(new Color(255, 203, 107));
		credit.setBounds(980, 500, 280, 55);
		add(credit);
		
		JButton debit = new JButton("Pay with Debit Card");
		debit.setOpaque(true);
		debit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		debit.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		debit.setBackground(new Color(255, 203, 107));
		debit.setBounds(980, 400, 280, 55);
		add(debit);
		
		JButton giftCard = new JButton("Pay with Giftcard");
		giftCard.setOpaque(true);
		giftCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		giftCard.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		giftCard.setBackground(new Color(255, 203, 107));
		giftCard.setBounds(980, 300, 280, 55);
		add(giftCard);
		
		JButton cash = new JButton("Pay with Cash");
		cash.setOpaque(true);
		cash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cash.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		cash.setBackground(new Color(255, 203, 107));
		cash.setBounds(980, 200, 280, 55);
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
			textArea.setText("Total : " + control.getTotal().toString() + "\n" + "Entered: " + entered + "\n" + "Change Due: "+ entered.subtract(control.getTotal())); 
			if(entered.compareTo(control.getTotal()) >= 0)
			{
				boolean success = false;
				try {
					success = control.payWithCoin(coins);
				} catch (DisabledException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (OverloadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (EmptyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(success) {
					textArea.setText("Total : " + control.getTotal().toString() + "\n" + "Entered: " + entered + "\n" + "Change Due: "+ entered.subtract(control.getTotal()));
	
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
			
		}
	});
		coin.setOpaque(true);
		coin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		coin.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		coin.setBackground(new Color(255, 203, 107));
		coin.setBounds(980, 100, 280, 55);
		add(coin);
		
		
	}

}
