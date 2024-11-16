package org.maia.amstrad.pc.action;

import org.maia.amstrad.gui.memory.BasicMemoryDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public class BasicMemoryDisplayAction extends ToggleDisplaySourceAction {

	private BasicMemoryDisplaySource displaySource;

	public BasicMemoryDisplayAction(AmstradPc amstradPc) {
		super(amstradPc, "Show Basic memory", "Hide Basic memory");
	}

	@Override
	protected boolean isDisplaySourceShowing() {
		return isBasicMemoryShowing();
	}

	public boolean isBasicMemoryShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getMonitor()
				.getCurrentAlternativeDisplaySource();
		return altDisplaySource != null && altDisplaySource instanceof BasicMemoryDisplaySource;
	}

	@Override
	protected BasicMemoryDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = new BasicMemoryDisplaySource(getAmstradPc());
		}
		return displaySource;
	}

}