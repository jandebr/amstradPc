package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JComponent;

import org.maia.amstrad.gui.browser.carousel.info.InfoSection;
import org.maia.amstrad.gui.browser.carousel.info.SlidingInfoSection;
import org.maia.amstrad.program.browser.impl.CarouselAmstradProgramBrowser;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.swing.animate.imageslide.show.SlidingImageShow;
import org.maia.swing.animate.textslide.SlidingTextLabel;
import org.maia.swing.compose.DirectionalFocusManager.Direction;

public class CarouselProgramBrowserDisplaySource extends CarouselProgramBrowserCoreDisplaySource {

	private SlidingTextLabel headingComponent;

	private JComponent captionComponent;

	private JComponent infoComponent;

	private JComponent infoOutlineComponent;

	private List<InfoSection> infoSections;

	private int currentInfoSectionIndex;

	private SlidingImageShow imageShow;

	private ImageShowLoader imageShowLoader;

	private Object imageShowSemaphore = new Object();

	public CarouselProgramBrowserDisplaySource(CarouselAmstradProgramBrowser programBrowser) {
		super(programBrowser);
	}

	@Override
	protected void renderFocus(Graphics2D g, Component focusOwner) {
		super.renderFocus(g, focusOwner);
		Rectangle bounds = focusOwner.getBounds();
		g.setColor(getFocusManager().isFocusTransferingLocked() ? Color.MAGENTA : Color.YELLOW);
		g.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
	}

	@Override
	protected synchronized void notifyCursorAtRepositoryNode(Node node) {
		super.notifyCursorAtRepositoryNode(node);
		setInfoSections(getComponentFactory().createInfoSectionsForNode(node));
		setCurrentInfoSectionIndex(getInfoSections().isEmpty() ? -1 : 0);
		loadHeading(node);
		loadCaption(node);
		loadImageShow(node);
		updateInfo();
	}

	@Override
	protected synchronized void notifyCursorLeftRepositoryNode() {
		super.notifyCursorLeftRepositoryNode();
		unloadHeading();
		unloadCaption();
		unloadImageShow();
		unloadInfo();
		setInfoSections(null);
		setCurrentInfoSectionIndex(-1);
	}

	private void loadHeading(Node node) {
		unloadHeading();
		setHeadingComponent(getComponentFactory().createHeadingComponent(node));
		add(getHeadingComponent().getUI(), CarouselLayoutManager.HEADING);
	}

	private void unloadHeading() {
		if (getHeadingComponent() != null) {
			remove(getHeadingComponent().getUI());
			setHeadingComponent(null);
		}
	}

	private void loadCaption(Node node) {
		unloadCaption();
		setCaptionComponent(getComponentFactory().createCaptionComponent(node, getInfoSections()));
		if (getCaptionComponent() != null) {
			add(getCaptionComponent(), CarouselLayoutManager.CAPTION);
		}
	}

	private void unloadCaption() {
		if (getCaptionComponent() != null) {
			remove(getCaptionComponent());
			setCaptionComponent(null);
		}
	}

	private void loadImageShow(Node node) {
		synchronized (getImageShowSemaphore()) {
			unloadImageShow();
			if (getComponentFactory().hasImageShow(node)) {
				ImageShowLoader loader = new ImageShowLoader(node);
				setImageShowLoader(loader);
				loader.start();
			}
		}
	}

	private void unloadImageShow() {
		synchronized (getImageShowSemaphore()) {
			setImageShowLoader(null);
			if (getImageShow() != null) {
				getImageShow().stopAnimating();
				remove(getImageShow().getUI());
				setImageShow(null);
			}
		}
	}

	private void loadInfo(InfoSection infoSection) {
		unloadInfo();
		setInfoComponent(infoSection.getInfoView());
		setInfoOutlineComponent(infoSection.getInfoOutlineView());
		add(getInfoComponent(), CarouselLayoutManager.INFO);
		add(getInfoOutlineComponent(), CarouselLayoutManager.INFO_OUTLINE);
		updateInfoOutlineVisibility(infoSection);
		getFocusManager().addFocusTransferBidirectional(getCarouselComponent().getUI(), Direction.UP,
				getInfoComponent());
	}

	private void unloadInfo() {
		getFocusManager().removeComponent(getInfoComponent());
		remove(getInfoComponent());
		remove(getInfoOutlineComponent());
		setInfoComponent(null);
		setInfoOutlineComponent(null);
	}

	protected boolean previousInfo() {
		if (hasInfoSections() && getInfoSections().size() > 1) {
			int i = getCurrentInfoSectionIndex() - 1;
			if (i < 0)
				i = getInfoSections().size() - 1;
			setCurrentInfoSectionIndex(i);
			updateInfo();
			return true;
		} else {
			return false;
		}
	}

	protected boolean nextInfo() {
		if (hasInfoSections() && getInfoSections().size() > 1) {
			int i = (getCurrentInfoSectionIndex() + 1) % getInfoSections().size();
			setCurrentInfoSectionIndex(i);
			updateInfo();
			return true;
		} else {
			return false;
		}
	}

	protected void updateInfo() {
		InfoSection infoSection = getCurrentInfoSection();
		if (infoSection != null) {
			for (InfoSection section : getInfoSections()) {
				section.getIcon().setSelected(section.equals(infoSection));
			}
			loadInfo(infoSection);
		} else {
			unloadInfo();
		}
	}

	@Override
	protected void handleKeyboardKeyInFocusOwner(KeyEvent e, Component focusOwner) {
		super.handleKeyboardKeyInFocusOwner(e, focusOwner);
		if (!e.isConsumed()) {
			if (focusOwner.equals(getInfoComponent())) {
				InfoSection infoSection = getCurrentInfoSection();
				if (infoSection != null) {
					handleKeyboardKeyInInfo(e, infoSection);
				}
			}
		}
	}

	protected void handleKeyboardKeyInInfo(KeyEvent e, InfoSection infoSection) {
		boolean sliding = infoSection instanceof SlidingInfoSection;
		if (sliding && getFocusManager().isFocusTransferingLocked()) {
			navigateInItemList(e, ((SlidingInfoSection) infoSection).getSlidingInfoView());
		}
		if (!e.isConsumed()) {
			int keyCode = e.getKeyCode();
			Direction dir = getDirection(e);
			if (Direction.LEFT.equals(dir)) {
				if (previousInfo()) {
					changeFocusToInfo();
					e.consume();
				}
			} else if (Direction.RIGHT.equals(dir)) {
				if (nextInfo()) {
					changeFocusToInfo();
					e.consume();
				}
			} else if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_NUMPAD5) {
				if (sliding && !getFocusManager().isFocusTransferingLocked()) {
					getFocusManager().lockFocusTransfering();
					e.consume();
				}
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				if (sliding && getFocusManager().isFocusTransferingLocked()) {
					getFocusManager().unlockFocusTransfering();
					e.consume();
				}
			}
			updateInfoOutlineVisibility();
		}
	}

	private void updateInfoOutlineVisibility() {
		InfoSection infoSection = getCurrentInfoSection();
		if (infoSection != null) {
			updateInfoOutlineVisibility(infoSection);
		}
	}

	private void updateInfoOutlineVisibility(InfoSection infoSection) {
		boolean visible = true;
		JComponent outline = infoSection.getInfoOutlineView();
		if (infoSection instanceof SlidingInfoSection) {
			CarouselFocusManager fm = getFocusManager();
			boolean fits = ((SlidingInfoSection) infoSection).getSlidingInfoView().isItemListFitsInsideViewport();
			boolean focus = infoSection.getInfoView().equals(fm.getFocusOwner());
			boolean active = fm.isFocusTransferingLocked();
			visible = !fits && focus && active;
		}
		remove(getInfoOutlineComponent());
		if (visible) {
			add(getInfoOutlineComponent(), CarouselLayoutManager.INFO_OUTLINE);
		}
	}

	protected void changeFocusToInfo() {
		getFocusManager().changeFocusOwner(getInfoComponent());
	}

	@Override
	public void reset() {
		super.reset();
		getFocusManager().unlockFocusTransfering();
	}

	private SlidingTextLabel getHeadingComponent() {
		return headingComponent;
	}

	private void setHeadingComponent(SlidingTextLabel comp) {
		this.headingComponent = comp;
	}

	private JComponent getCaptionComponent() {
		return captionComponent;
	}

	private void setCaptionComponent(JComponent comp) {
		this.captionComponent = comp;
	}

	private JComponent getInfoComponent() {
		return infoComponent;
	}

	private void setInfoComponent(JComponent comp) {
		this.infoComponent = comp;
	}

	private JComponent getInfoOutlineComponent() {
		return infoOutlineComponent;
	}

	private void setInfoOutlineComponent(JComponent comp) {
		this.infoOutlineComponent = comp;
	}

	private List<InfoSection> getInfoSections() {
		return infoSections;
	}

	private void setInfoSections(List<InfoSection> infoSections) {
		this.infoSections = infoSections;
	}

	protected boolean hasInfoSections() {
		return getInfoSections() != null && !getInfoSections().isEmpty();
	}

	protected InfoSection getCurrentInfoSection() {
		InfoSection section = null;
		int i = getCurrentInfoSectionIndex();
		if (i >= 0 && getInfoSections() != null && i < getInfoSections().size()) {
			section = getInfoSections().get(i);
		}
		return section;
	}

	private int getCurrentInfoSectionIndex() {
		return currentInfoSectionIndex;
	}

	private void setCurrentInfoSectionIndex(int index) {
		this.currentInfoSectionIndex = index;
	}

	private SlidingImageShow getImageShow() {
		return imageShow;
	}

	private void setImageShow(SlidingImageShow imageShow) {
		this.imageShow = imageShow;
	}

	private ImageShowLoader getImageShowLoader() {
		return imageShowLoader;
	}

	private void setImageShowLoader(ImageShowLoader loader) {
		this.imageShowLoader = loader;
	}

	private Object getImageShowSemaphore() {
		return imageShowSemaphore;
	}

	private class ImageShowLoader extends Thread {

		private Node node;

		public ImageShowLoader(Node node) {
			super("ImageShow Loader");
			setDaemon(true);
			this.node = node;
		}

		@Override
		public void run() {
			SlidingImageShow show = getComponentFactory().createImageShow(getNode());
			if (show != null) {
				synchronized (getImageShowSemaphore()) {
					if (equals(getImageShowLoader())) {
						setImageShow(show);
						add(show.getUI(), CarouselLayoutManager.PREVIEW);
						show.startAnimating();
					} else {
						// no longer current node, dismiss show
					}
				}
			}
		}

		public Node getNode() {
			return node;
		}

	}

}