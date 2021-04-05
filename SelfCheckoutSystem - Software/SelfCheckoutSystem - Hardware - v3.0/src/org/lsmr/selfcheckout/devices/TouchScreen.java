package org.lsmr.selfcheckout.devices;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.lsmr.selfcheckout.devices.listeners.TouchScreenListener;

/**
 * A device for display and customer input. It displays a {@link JFrame} object
 * to which the graphical user interface may be deployed. Touch screens announce
 * no events. To listen to events there, one has to listen to events from
 * individual graphical objects.
 */
public final class TouchScreen extends AbstractDevice<TouchScreenListener> {
	private JFrame frame;
	private volatile boolean ready = false;

	/**
	 * Creates a touch screen. The frame herein will initially be invisible.
	 */
	public TouchScreen() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = createGUI();
				ready = true;
			}
		});
		while(!ready)
			;
	}

	private JFrame createGUI() {
		JFrame frame = new JFrame();

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				ready = true;
				frame.setVisible(false);
			}
		});

		return frame;
	}

	/**
	 * Gets the {@link JFrame} object that should be in fullscreen mode for the
	 * touch screen. The frame will be invisible until it is explicitly made
	 * visible, by calling {@link #setVisible(boolean)} with a {@code true}
	 * argument.
	 * 
	 * @return The frame on which the graphical user interface can be deployed.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Allows the visibility of the {@link JFrame} to be changed.
	 * 
	 * @param state
	 *            True if the frame should be made visible; otherwise, false.
	 */
	public void setVisible(boolean state) {
		frame.setVisible(state);
	}
}
