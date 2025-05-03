package org.maia.amstrad.gui.browser.carousel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import org.maia.swing.VerticalAlignment;
import org.maia.swing.compose.BandedLayoutManager;

public class CarouselLayoutManager extends BandedLayoutManager implements LayoutManager {

	private Dimension displaySize;

	private Rectangle headingBounds;

	private Rectangle captionBounds;

	private Rectangle infoBounds;

	private Rectangle infoOutlineBounds;

	private Rectangle previewBounds;

	private Rectangle carouselBounds;

	private Rectangle carouselOutlineBounds;

	private Rectangle carouselBreadcrumbBounds;

	public static final String HEADING = "Heading";

	public static final String CAPTION = "Caption";

	public static final String INFO = "Info";

	public static final String INFO_OUTLINE = "InfoOutline";

	public static final String PREVIEW = "Preview";

	public static final String CAROUSEL = "Carousel";

	public static final String CAROUSEL_OUTLINE = "CarouselOutline";

	public static final String CAROUSEL_BREADCRUMB = "CarouselBreadcrumb";

	public CarouselLayoutManager(Dimension displaySize) {
		this.displaySize = displaySize;
		doLayoutUpfront();
	}

	private void doLayoutUpfront() {
		Band windowBand = createVerticalContainerBand(1.0f);
		// North
		Band northBand = windowBand.addSubBand(createHorizontalContainerBand(0.45f));
		Band detailBand = northBand.addSubBand(createVerticalContainerBand(0.45f, VerticalAlignment.TOP));
		Band previewBand = northBand.addSubBand(createVerticalContainerBand(0.55f, VerticalAlignment.TOP));
		Band headingBand = detailBand.addSubBand(createHorizontalContainerBand(0.2f));
		Band captionParentBand = detailBand.addSubBand(createHorizontalContainerBand(0.08f));
		Band captionBand = captionParentBand.addSubBand(createHorizontalContainerBand(0.98f));
		Band captionSpacerBand = captionParentBand.addSubBand(createFlexibleSpacerBand(0.02f));
		detailBand.addSubBand(createMinimumSpacerBand(0.02f, 8));
		Band infoSectionBand = detailBand.addSubBand(createHorizontalContainerBand(0.7f));
		Band infoBand = infoSectionBand.addSubBand(createHorizontalContainerBand(0.98f));
		Band infoOutlineBand = infoSectionBand.addSubBand(createHorizontalContainerBand(0.02f));
		// Spacer
		windowBand.addSubBand(createMinimumSpacerBand(0.02f, 8));
		// South
		Band carouselBand = windowBand.addSubBand(createHorizontalContainerBand(0.43f));
		windowBand.addSubBand(createMinimumSpacerBand(0.02f, 8));
		Band carouselOutlineBand = windowBand.addSubBand(createHorizontalContainerBand(0.02f));
		windowBand.addSubBand(createMinimumSpacerBand(0.02f, 8));
		Band carouselBreadcrumbBand = windowBand.addSubBand(createHorizontalContainerBand(0.03f));
		windowBand.addSubBand(createMaximumSpacerBand(0.01f, 8));
		// Finetuning
		infoBand.setMargin(new Insets(1, 1, 1, 1));
		infoOutlineBand.setMargin(infoBand.getMargin());
		infoOutlineBand.setSizeRange(14, 24);
		captionSpacerBand.setSizeRange(infoOutlineBand.getMinimumSize(), infoOutlineBand.getMaximumSize());
		carouselOutlineBand.setSizeRange(14, 24);
		carouselBreadcrumbBand.setSizeRange(20, 30);
		// Layout
		layout(windowBand, getDisplaySize());
		setHeadingBounds(headingBand.getBounds());
		setCaptionBounds(captionBand.getBounds());
		setInfoBounds(infoBand.getBounds());
		setInfoOutlineBounds(infoOutlineBand.getBounds());
		setPreviewBounds(previewBand.getBounds());
		setCarouselBounds(carouselBand.getBounds());
		setCarouselOutlineBounds(carouselOutlineBand.getBounds());
		setCarouselBreadcrumbBounds(carouselBreadcrumbBand.getBounds());
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		if (HEADING.equals(name)) {
			comp.setLocation(getHeadingBounds().getLocation());
		} else if (CAPTION.equals(name)) {
			comp.setLocation(getCaptionBounds().getLocation());
		} else if (INFO.equals(name)) {
			comp.setLocation(getInfoBounds().getLocation());
		} else if (INFO_OUTLINE.equals(name)) {
			comp.setLocation(getInfoOutlineBounds().getLocation());
		} else if (PREVIEW.equals(name)) {
			comp.setBounds(getPreviewBounds());
		} else if (CAROUSEL.equals(name)) {
			comp.setBounds(getCarouselBounds());
		} else if (CAROUSEL_OUTLINE.equals(name)) {
			comp.setBounds(getCarouselOutlineBounds());
		} else if (CAROUSEL_BREADCRUMB.equals(name)) {
			comp.setBounds(getCarouselBreadcrumbBounds());
		}
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// no action
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return getDisplaySize();
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return getDisplaySize();
	}

	@Override
	public void layoutContainer(Container parent) {
		// done upfront
	}

	public Dimension getDisplaySize() {
		return displaySize;
	}

	public Rectangle getHeadingBounds() {
		return headingBounds;
	}

	private void setHeadingBounds(Rectangle bounds) {
		this.headingBounds = bounds;
	}

	public Rectangle getCaptionBounds() {
		return captionBounds;
	}

	private void setCaptionBounds(Rectangle bounds) {
		this.captionBounds = bounds;
	}

	public Rectangle getInfoBounds() {
		return infoBounds;
	}

	private void setInfoBounds(Rectangle bounds) {
		this.infoBounds = bounds;
	}

	public Rectangle getInfoOutlineBounds() {
		return infoOutlineBounds;
	}

	private void setInfoOutlineBounds(Rectangle bounds) {
		this.infoOutlineBounds = bounds;
	}

	public Rectangle getPreviewBounds() {
		return previewBounds;
	}

	private void setPreviewBounds(Rectangle bounds) {
		this.previewBounds = bounds;
	}

	public Rectangle getCarouselBounds() {
		return carouselBounds;
	}

	private void setCarouselBounds(Rectangle bounds) {
		this.carouselBounds = bounds;
	}

	public Rectangle getCarouselOutlineBounds() {
		return carouselOutlineBounds;
	}

	private void setCarouselOutlineBounds(Rectangle bounds) {
		this.carouselOutlineBounds = bounds;
	}

	public Rectangle getCarouselBreadcrumbBounds() {
		return carouselBreadcrumbBounds;
	}

	private void setCarouselBreadcrumbBounds(Rectangle bounds) {
		this.carouselBreadcrumbBounds = bounds;
	}

}