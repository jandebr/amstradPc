package org.maia.amstrad.gui.memory.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.gui.memory.BasicMemoryDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.basic.LocomotiveBasicAction;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public class BasicDisplayMemoryAction extends LocomotiveBasicAction {

	private BasicMemoryDisplaySource displaySource;

	private static String NAME_OPEN = "Show Basic memory";

	private static String NAME_CLOSE = "Hide Basic memory";

	public BasicDisplayMemoryAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.getMonitor().addMonitorListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleBasicMemory();
	}

	public void toggleBasicMemory() {
		if (NAME_OPEN.equals(getName())) {
			showBasicMemory();
		} else {
			hideBasicMemory();
		}
	}

	public void showBasicMemory() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideBasicMemory() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().resetDisplaySource();
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		super.amstradDisplaySourceChanged(monitor);
		updateName();
	}

	private void updateName() {
		if (isBasicMemoryShowing()) {
			changeName(NAME_CLOSE);
		} else {
			changeName(NAME_OPEN);
		}
	}

	public boolean isBasicMemoryShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getMonitor()
				.getCurrentAlternativeDisplaySource();
		return altDisplaySource != null && altDisplaySource instanceof BasicMemoryDisplaySource;
	}

	private BasicMemoryDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = new BasicMemoryDisplaySource(getAmstradPc());
		}
		return displaySource;
	}

}