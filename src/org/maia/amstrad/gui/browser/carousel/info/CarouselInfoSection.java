package org.maia.amstrad.gui.browser.carousel.info;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.maia.amstrad.gui.browser.carousel.CarouselLayoutManager;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.animate.itemslide.outline.SlidingItemListOutlineView;
import org.maia.swing.animate.itemslide.outline.SolidFillOutlineRenderer;

public abstract class CarouselInfoSection extends InfoSection implements SlidingInfoSection {

	private CarouselProgramBrowserTheme theme;

	private CarouselLayoutManager layout;

	private SlidingItemListComponent infoComponent;

	protected CarouselInfoSection(InfoIcon icon, CarouselProgramBrowserTheme theme, CarouselLayoutManager layout) {
		super(icon);
		this.theme = theme;
		this.layout = layout;
	}

	@Override
	public synchronized SlidingItemListComponent getSlidingInfoView() {
		if (infoComponent == null) {
			infoComponent = createInfoComponent();
		}
		return infoComponent;
	}

	protected abstract SlidingItemListComponent createInfoComponent();

	protected InfoText createInfoText() {
		return new InfoText(getSizeOfInfoView(), getTheme());
	}

	@Override
	protected final JComponent createInfoView() {
		return getSlidingInfoView().getUI();
	}

	@Override
	protected JComponent createInfoOutlineView() {
		int thickness = getSizeOfInfoOutlineView().width;
		SlidingItemListOutlineView outline = getSlidingInfoView()
				.createOutlineViewMatchingOrientationAndLength(thickness);
		outline.setBorder(BorderFactory.createLineBorder(getTheme().getInfoOutlineBorderColor()));
		outline.setExtentMargin(new Insets(1, 1, 1, 1));
		outline.setExtentBorder(BorderFactory.createLineBorder(getTheme().getInfoOutlineCursorBorderColor()));
		outline.setExtentRenderer(new SolidFillOutlineRenderer(getTheme().getInfoOutlineCursorFillColor()));
		outline.setCursorRenderer(null);
		return outline;
	}

	protected Dimension getSizeOfInfoView() {
		return getLayout().getInfoBounds().getSize();
	}

	protected Dimension getSizeOfInfoOutlineView() {
		return getLayout().getInfoOutlineBounds().getSize();
	}

	protected CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

	protected CarouselLayoutManager getLayout() {
		return layout;
	}

}