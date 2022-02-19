package org.maia.amstrad.jemu;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class AmstradPcFrame extends JFrame implements AmstradPcStateListener {

	private AmstradPc amstradPc;

	public AmstradPcFrame(AmstradPc amstradPc) {
		this(amstradPc, "JavaCPC - Amstrad CPC Emulator");
	}

	public AmstradPcFrame(AmstradPc amstradPc, String title) {
		super(title);
		this.amstradPc = amstradPc;
		amstradPc.addStateListener(this);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setFocusable(false);
		buildUI();
	}

	protected void processWindowEvent(WindowEvent we) {
		super.processWindowEvent(we);
		if (we.getID() == WindowEvent.WINDOW_CLOSING) {
			terminate();
		}
	}

	protected void buildUI() {
		getContentPane().add(getAmstradPc().getDisplayPane(), BorderLayout.CENTER);
	}

	public void terminate() {
		getAmstradPc().terminate();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		setVisible(true);
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
		dispose();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}