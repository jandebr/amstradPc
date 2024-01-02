package org.maia.amstrad.system.impl;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import org.maia.amstrad.gui.terminate.AmstradTerminationDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardListener;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.util.SystemUtils;

public class AmstradSystemAnimatedTermination extends AmstradSystemElementaryTermination
		implements AmstradKeyboardListener {

	private AmstradTerminationDisplaySource terminationDisplaySource;

	private boolean commandCancelled;

	public AmstradSystemAnimatedTermination() {
	}

	@Override
	public void terminate(AmstradSystem system) {
		showAnimation(system);
		super.terminate(system);
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			setCommandCancelled(event.isShiftDown());
			getTerminationDisplaySource().forceQuit();
		}
	}

	@Override
	public void amstradKeyboardBreakEscaped(AmstradKeyboard keyboard) {
		// nothing
	}

	private void showAnimation(AmstradSystem system) {
		AmstradPc amstradPc = system.getAmstradPc();
		AmstradTerminationDisplaySource ds = new AmstradTerminationDisplaySource(amstradPc);
		setTerminationDisplaySource(ds);
		amstradPc.getMonitor().swapDisplaySource(ds);
		amstradPc.getMonitor().setCustomDisplayOverlay(new VoidDisplayOverlay());
		amstradPc.getKeyboard().addKeyboardListener(this);
		while (!ds.isAnimationCompleted()) {
			SystemUtils.sleep(50L);
		}
	}

	@Override
	protected boolean isExecuteCommand() {
		return !isCommandCancelled();
	}

	private AmstradTerminationDisplaySource getTerminationDisplaySource() {
		return terminationDisplaySource;
	}

	private void setTerminationDisplaySource(AmstradTerminationDisplaySource displaySource) {
		this.terminationDisplaySource = displaySource;
	}

	private boolean isCommandCancelled() {
		return commandCancelled;
	}

	private void setCommandCancelled(boolean cancel) {
		if (cancel)
			System.out.println("Cancelled termination command");
		this.commandCancelled = cancel;
	}

	private static class VoidDisplayOverlay implements AmstradDisplayOverlay {

		public VoidDisplayOverlay() {
		}

		@Override
		public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		}

		@Override
		public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
				boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
			// render nothing
		}

		@Override
		public void dispose(JComponent displayComponent) {
		}

	}

}