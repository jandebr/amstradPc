package org.maia.amstrad.pc.action;

import org.maia.amstrad.gui.symbols.BasicSymbolsDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public class BasicSymbolsDisplayAction extends ToggleDisplaySourceAction {

	private BasicSymbolsDisplaySource displaySource;

	public BasicSymbolsDisplayAction(AmstradPc amstradPc) {
		super(amstradPc, "Show Basic symbols", "Hide Basic symbols");
	}

	@Override
	protected boolean isDisplaySourceShowing() {
		return isBasicSymbolsShowing();
	}

	public boolean isBasicSymbolsShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getMonitor()
				.getCurrentAlternativeDisplaySource();
		return altDisplaySource != null && altDisplaySource instanceof BasicSymbolsDisplaySource;
	}

	@Override
	protected BasicSymbolsDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = new BasicSymbolsDisplaySource(getAmstradPc());
		}
		return displaySource;
	}

}