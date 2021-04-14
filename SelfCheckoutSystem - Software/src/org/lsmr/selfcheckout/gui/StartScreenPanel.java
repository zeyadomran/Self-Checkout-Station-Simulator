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

public class StartScreenPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private SelfCheckoutSoftware control;
	
	public StartScreenPanel(SelfCheckoutSoftware control) {
		this.control = control;
		
		this.setForeground(new Color(9, 11, 16));
		this.setBackground(new Color(9, 11, 16));
		this.setMinimumSize(new Dimension(1280, 720));
		this.setMaximumSize(new Dimension(1280, 720));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(new Dimension(1280, 720));
		this.setLayout(null);
		
		JButton goToMainScreen = new JButton("Start");
		goToMainScreen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				control.loadMainGUI();
			}
		});
		goToMainScreen.setOpaque(true);
		goToMainScreen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		goToMainScreen.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		goToMainScreen.setBackground(new Color(40, 167, 69));
		goToMainScreen.setBounds(500, 332, 280, 55);
		add(goToMainScreen);

		JLabel TitleLabel = new JLabel("Self Checkout Station");
		TitleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 60));
		TitleLabel.setForeground(new Color(64, 224, 208));
		TitleLabel.setBounds(312, 20, 656, 72);
		add(TitleLabel);
	}

}
