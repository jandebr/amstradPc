package org.maia.amstrad.pc;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class AmstradPcFrame extends JFrame implements AmstradPcStateListener, WindowListener {

	private AmstradPc amstradPc;

	private boolean closing;

	public AmstradPcFrame(AmstradPc amstradPc, boolean exitOnClose) {
		this(amstradPc, "JavaCPC - Amstrad CPC Emulator", exitOnClose);
	}

	public AmstradPcFrame(AmstradPc amstradPc, String title, boolean exitOnClose) {
		super(title);
		this.amstradPc = amstradPc;
		amstradPc.addStateListener(this);
		addWindowListener(this);
		setFocusable(false);
		setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		buildUI();
	}

	protected void buildUI() {
		getContentPane().add(getAmstradPc().getDisplayPane(), BorderLayout.CENTER);
	}

	public void installSimpleMenuBar() {
		setJMenuBar(AmstradFactory.getInstance().createMenuBar(getAmstradPc()));
	}

	public boolean isFullscreen() {
		return getAmstradPc().isWindowFullscreen();
	}

	public void toggleFullscreen() {
		getAmstradPc().toggleWindowFullscreen();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		setVisible(true);
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
	}

	@Override
	public synchronized void amstradPcTerminated(AmstradPc amstradPc) {
		if (!isClosing()) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void windowActivated(WindowEvent event) {
	}

	@Override
	public void windowClosed(WindowEvent event) {
	}

	@Override
	public synchronized void windowClosing(WindowEvent event) {
		setClosing(true);
		if (!getAmstradPc().isTerminated()) {
			getAmstradPc().terminate();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent event) {
	}

	@Override
	public void windowDeiconified(WindowEvent event) {
	}

	@Override
	public void windowIconified(WindowEvent event) {
	}

	@Override
	public void windowOpened(WindowEvent event) {
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private boolean isClosing() {
		return closing;
	}

	private void setClosing(boolean closing) {
		this.closing = closing;
	}

}