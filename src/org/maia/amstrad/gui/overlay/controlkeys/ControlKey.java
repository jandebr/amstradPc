package org.maia.amstrad.gui.overlay.controlkeys;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;

public abstract class ControlKey {

	private AmstradPc amstradPc;

	private String key;

	private String label;

	protected ControlKey(AmstradPc amstradPc, String key, String label) {
		this.amstradPc = amstradPc;
		this.key = key;
		this.label = label;
	}

	public abstract boolean isAvailable();

	protected boolean isProgramBrowserShowing() {
		return getAmstradContext().isProgramBrowserShowing(getAmstradPc());
	}

	@Override
	public String toString() {
		return getKey() + "  " + getLabel();
	}

	protected AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

}