package org.maia.amstrad.gui.browser.carousel.info;

import javax.swing.JComponent;

public abstract class InfoSection {

	private InfoIcon icon;

	private JComponent infoView;

	private JComponent infoOutlineView;

	protected InfoSection(InfoIcon icon) {
		this.icon = icon;
	}

	public InfoIcon getIcon() {
		return icon;
	}

	public synchronized JComponent getInfoView() {
		if (infoView == null) {
			infoView = createInfoView();
		}
		return infoView;
	}

	public synchronized JComponent getInfoOutlineView() {
		if (infoOutlineView == null) {
			infoOutlineView = createInfoOutlineView();
		}
		return infoOutlineView;
	}

	protected abstract JComponent createInfoView();

	protected abstract JComponent createInfoOutlineView();

}