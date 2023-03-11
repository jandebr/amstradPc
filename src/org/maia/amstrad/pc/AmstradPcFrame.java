package org.maia.amstrad.pc;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.maia.amstrad.AmstradFactory;

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
		getContentPane().add(getAmstradPc().getMonitor().getDisplayPane(), BorderLayout.CENTER);
	}

	public void installMenuBar() {
		setJMenuBar(AmstradFactory.getInstance().createMenuBar(getAmstradPc()));
	}

	public void makeFullscreen() {
		getAmstradPc().getMonitor().makeWindowFullscreen();
	}

	public boolean isFullscreen() {
		return getAmstradPc().getMonitor().isWindowFullscreen();
	}

	public void toggleFullscreen() {
		getAmstradPc().getMonitor().toggleWindowFullscreen();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		setVisible(true);
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public synchronized void amstradPcTerminated(AmstradPc amstradPc) {
		if (!isClosing()) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void amstradPcProgramLoaded(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void windowActivated(WindowEvent event) {
		// no action
	}

	@Override
	public void windowClosed(WindowEvent event) {
		// no action
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
		// no action
	}

	@Override
	public void windowDeiconified(WindowEvent event) {
		// no action
	}

	@Override
	public void windowIconified(WindowEvent event) {
		// no action
	}

	@Override
	public void windowOpened(WindowEvent event) {
		// no action
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