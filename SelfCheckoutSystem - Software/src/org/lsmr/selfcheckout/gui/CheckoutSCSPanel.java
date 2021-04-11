package org.lsmr.selfcheckout.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class CheckoutSCSPanel extends JPanel {
	private SelfCheckoutSoftware control;

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
	}

}
