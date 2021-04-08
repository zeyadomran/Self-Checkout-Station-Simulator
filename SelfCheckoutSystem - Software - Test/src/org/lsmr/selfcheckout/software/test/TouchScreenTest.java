package org.lsmr.selfcheckout.devices.test;

import static org.junit.Assert.assertEquals;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.TouchScreen;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.TouchScreenListener;

public class TouchScreenTest {
	private TouchScreen screen;
	private volatile int found;

	@Before
	public void setup() {
		screen = new TouchScreen();
		screen.register(new TouchScreenListener() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {}
		});
		screen.disable();
		screen.enable();
		screen.deregister(null);
		screen.deregisterAll();
		found = 0;
	}

	@Test
	public void testFrame() {
		final JFrame f = screen.getFrame();
		Container panel = f.getContentPane();

		// f.add(panel);

		JButton foo = new JButton("foo");
		foo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				found++;
				f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
			}
		});

		panel.add(foo);

		screen.setVisible(true);

		try {
			Thread.sleep(2000);
		}
		catch(InterruptedException e) {}

		foo.doClick();

		assertEquals(1, found);
	}
}
