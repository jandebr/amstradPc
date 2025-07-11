package org.maia.amstrad.gui.browser.carousel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.carousel.CarouselComponent.CarouselOutline;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumb;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselRepositoryItem;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserDefaultTheme;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.cassette.CassetteFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.cassette.CassetteProgramCoverImageProducer;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.pc.monitor.display.source.AmstradAwtDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.browser.impl.CarouselAmstradProgramBrowser;
import org.maia.amstrad.program.load.AmstradProgramLoader;
import org.maia.amstrad.program.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.program.load.AmstradProgramRuntime;
import org.maia.amstrad.program.load.basic.staged.EndingBasicAction;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.DirectionalFocusManager.Direction;
import org.maia.swing.DirectionalFocusManager.FocusListener;
import org.maia.swing.animate.itemslide.SlidingCursor;
import org.maia.swing.animate.itemslide.SlidingItem;
import org.maia.swing.animate.itemslide.SlidingItemListAdapter;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;

public abstract class CarouselProgramBrowserDisplaySourceSkeleton extends AmstradAwtDisplaySource
		implements ProgramBrowserDisplaySource, CarouselHost {

	private CarouselAmstradProgramBrowser programBrowser;

	private CarouselProgramBrowserTheme theme;

	private AmstradProgramCoverImageProducer programCoverImageProducer;

	private AmstradFolderCoverImageProducer folderCoverImageProducer;

	private CarouselFocusManager focusManager;

	private CarouselComponentFactory componentFactory;

	private CarouselComponent carouselComponent;

	private CarouselOutline carouselOutline;

	private CarouselBreadcrumb carouselBreadcrumb;

	private Node lastVisitedNode;

	private ProgramNode programNodeFailedToRun;

	protected CarouselProgramBrowserDisplaySourceSkeleton(CarouselAmstradProgramBrowser programBrowser) {
		super(programBrowser.getAmstradPc());
		this.programBrowser = programBrowser;
		setRestoreMonitorSettingsOnDispose(true); // as this source switches to COLOR
		setAutoPauseResume(true);
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		setTheme(createTheme(graphicsContext));
		setBackground(getTheme().getBackgroundColor());
		super.init(displayComponent, graphicsContext); // invokes createLayoutManager() and buildUI()
		getAmstradPc().getMonitor().setMode(AmstradMonitorMode.COLOR);
		setFocusManager(createFocusManager());
		initFocusManager();
		initCarousel();
	}

	protected CarouselProgramBrowserTheme createTheme(AmstradGraphicsContext graphicsContext) {
		return new CarouselProgramBrowserDefaultTheme(graphicsContext);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new CarouselLayoutManager(getDisplaySize());
	}

	@Override
	protected void buildUI() {
		initCoverImageProducers();
		setComponentFactory(createComponentFactory());
		buildCarousel();
		buildCarouselOutline();
		buildCarouselBreadcrumb();
	}

	protected void initCoverImageProducers() {
		Insets padding = getLayout().getCarouselPadding();
		int height = getLayout().getCarouselBounds().height - padding.top - padding.bottom;
		int width = Math.round(0.6f * height);
		Dimension imageSize = new Dimension(width, height);
		initProgramCoverImageProducer(imageSize);
		initFolderCoverImageProducer(imageSize);
	}

	protected void initProgramCoverImageProducer(Dimension imageSize) {
		AmstradProgramCoverImageProducer producer = getProgramCoverImageProducer();
		if (producer == null || !producer.getImageSize().equals(imageSize)) {
			producer = createProgramCoverImageProducer(imageSize);
		}
		producer.setBackgroundColor(getTheme().getBackgroundColor());
		setProgramCoverImageProducer(producer);
	}

	protected void initFolderCoverImageProducer(Dimension imageSize) {
		AmstradFolderCoverImageProducer producer = getFolderCoverImageProducer();
		if (producer == null || !producer.getImageSize().equals(imageSize)) {
			producer = createFolderCoverImageProducer(imageSize);
		}
		producer.setBackgroundColor(getTheme().getBackgroundColor());
		setFolderCoverImageProducer(producer);
	}

	protected AmstradProgramCoverImageProducer createProgramCoverImageProducer(Dimension imageSize) {
		return new CassetteProgramCoverImageProducer(imageSize, getTheme().getBackgroundColor(),
				getTheme().getCarouselProgramTitleFont(), getTheme().getCarouselProgramTitleColor(),
				getTheme().getCarouselProgramTitleBackgroundColor(),
				getTheme().getCarouselProgramTitleRelativeVerticalPosition());
	}

	protected AmstradFolderCoverImageProducer createFolderCoverImageProducer(Dimension imageSize) {
		return new CassetteFolderCoverImageProducer(imageSize, getTheme().getBackgroundColor(),
				getTheme().getCarouselFolderTitleFont(), getTheme().getCarouselFolderTitleColor());
	}

	protected CarouselComponentFactory createComponentFactory() {
		return new CarouselComponentFactory(getTheme(), getLayout(), getProgramCoverImageProducer(),
				getFolderCoverImageProducer());
	}

	private void buildCarousel() {
		CarouselComponent comp = getComponentFactory().createCarouselComponent(this);
		comp.addListener(new CarouselComponentItemTracker());
		add(comp.getUI(), CarouselLayoutManager.CAROUSEL);
		setCarouselComponent(comp);
	}

	private void buildCarouselOutline() {
		CarouselOutline outline = getComponentFactory().createCarouselOutline(getCarouselComponent());
		add(outline, CarouselLayoutManager.CAROUSEL_OUTLINE);
		setCarouselOutline(outline);
	}

	private void buildCarouselBreadcrumb() {
		CarouselBreadcrumb breadcrumb = getComponentFactory().createCarouselBreadcrumb(this);
		add(breadcrumb.getUI(), CarouselLayoutManager.CAROUSEL_BREADCRUMB);
		setCarouselBreadcrumb(breadcrumb);
	}

	protected CarouselFocusManager createFocusManager() {
		CarouselFocusManager focusManager = new CarouselFocusManager();
		focusManager.setRequestFocusOnComponents(false); // keep focus on display (to catch key events)
		return focusManager;
	}

	protected void initFocusManager() {
		CarouselFocusManager focusManager = getFocusManager();
		focusManager.addListener(new FocusTracker());
		focusManager.addFocusTransferBidirectional(getCarouselComponent().getUI(), Direction.DOWN,
				getCarouselBreadcrumb().getUI());
		focusManager.clearFocusOwner();
	}

	protected void initCarousel() {
		notifyCursorLeftRepositoryNode();
		Node node = getLastVisitedNode();
		if (node != null && !node.isRoot()) {
			enterFolder(node.getParent(), node);
		} else {
			enterFolder(getProgramBrowser().getProgramRepository().getRootNode());
		}
	}

	@Override
	protected void renderContent(Graphics2D g, int width, int height) {
		super.renderContent(g, width, height);
		renderFocus(g);
	}

	private void renderFocus(Graphics2D g) {
		Component focusOwner = getFocusManager().getFocusOwner();
		if (focusOwner != null) {
			if (!isCarouselComponent(focusOwner) && !isBreadcrumbComponent(focusOwner)) {
				renderFocus(g, focusOwner);
			}
		}
	}

	protected void renderFocus(Graphics2D g, Component focusOwner) {
		// Subclasses may override
	}

	protected void notifyCursorAtRepositoryNode(Node node) {
		// Subclasses may override
	}

	protected void notifyCursorLeftRepositoryNode() {
		// Subclasses may override
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		Component focusOwner = getFocusManager().getFocusOwner();
		if (focusOwner != null) {
			handleKeyboardKeyInFocusOwner(e, focusOwner);
		}
		if (!e.isConsumed()) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_F5) {
				reset();
				e.consume();
			} else if (keyCode == KeyEvent.VK_F11 && getSystemSettings().isFullscreenToggleEnabled()) {
				// Toggle fullscreen by re-init
				close();
				getAmstradPc().getMonitor().toggleFullscreen();
				show();
				e.consume();
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				changeFocusToCarousel();
				e.consume();
			} else {
				if (getFocusManager().transferFocus(getDirection(e))) {
					e.consume();
				}
			}
		}
	}

	@Override
	protected void keyboardKeyReleased(KeyEvent e) {
		if (isCarouselComponent(getFocusManager().getFocusOwner())) {
			getCarouselComponent().keyReleased(e); // not for navigation but landing logic
		}
	}

	protected void handleKeyboardKeyInFocusOwner(KeyEvent e, Component focusOwner) {
		if (isCarouselComponent(focusOwner)) {
			getCarouselComponent().keyPressed(e); // not for navigation but landing logic
			handleKeyboardKeyInCarousel(e);
		} else if (isBreadcrumbComponent(focusOwner)) {
			handleKeyboardKeyInBreadcrumb(e);
		}
	}

	protected void handleKeyboardKeyInCarousel(KeyEvent e) {
		CarouselComponent comp = getCarouselComponent();
		navigateInItemList(e, comp);
		if (!e.isConsumed()) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_NUMPAD5) {
				CarouselItem item = comp.getSelectedItem();
				if (item != null) {
					item.execute(this);
					e.consume();
				}
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				Node parent = comp.getFolderNode();
				if (parent != null && !parent.isRoot()) {
					enterFolder(parent.getParent(), parent);
					e.consume();
				}
			}
		}
	}

	protected void handleKeyboardKeyInBreadcrumb(KeyEvent e) {
		CarouselBreadcrumb breadcrumb = getCarouselBreadcrumb();
		int keyCode = e.getKeyCode();
		Direction dir = getDirection(e);
		if (Direction.LEFT.equals(dir)) {
			int i = breadcrumb.getSelectedItemIndex() - 1;
			while (i > 0 && breadcrumb.getItem(i).isSeparator())
				i--;
			breadcrumb.slideToItemIndex(i);
			e.consume();
		} else if (Direction.RIGHT.equals(dir)) {
			int i = breadcrumb.getSelectedItemIndex() + 1;
			while (i < breadcrumb.getItemCount() && breadcrumb.getItem(i).isSeparator())
				i++;
			breadcrumb.slideToItemIndex(i);
			e.consume();
		} else if (keyCode == KeyEvent.VK_HOME) {
			breadcrumb.moveToFirstItem();
			e.consume();
		} else if (keyCode == KeyEvent.VK_END) {
			breadcrumb.moveToLastItem();
			e.consume();
		} else if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_NUMPAD5) {
			CarouselBreadcrumbItem item = breadcrumb.getSelectedItem();
			if (item != null && !item.isSeparator()) {
				enterFolder(item.getFolderNode());
				e.consume();
			}
		}
	}

	protected void navigateInItemList(KeyEvent e, SlidingItemListComponent comp) {
		int keyCode = e.getKeyCode();
		Direction dir = getDirection(e);
		if (comp.isHorizontalLayout()) {
			if (Direction.LEFT.equals(dir)) {
				comp.slideToPreviousItem();
				e.consume();
			} else if (Direction.RIGHT.equals(dir)) {
				comp.slideToNextItem();
				e.consume();
			}
		} else {
			if (Direction.UP.equals(dir)) {
				comp.slideToPreviousItem();
				e.consume();
			} else if (Direction.DOWN.equals(dir)) {
				comp.slideToNextItem();
				e.consume();
			}
		}
		if (keyCode == KeyEvent.VK_PAGE_UP) {
			comp.moveToPreviousPage();
			e.consume();
		} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
			comp.moveToNextPage();
			e.consume();
		} else if (keyCode == KeyEvent.VK_HOME) {
			comp.moveToFirstItem();
			e.consume();
		} else if (keyCode == KeyEvent.VK_END) {
			comp.moveToLastItem();
			e.consume();
		}
	}

	protected Direction getDirection(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_NUMPAD4) {
			return Direction.LEFT;
		} else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_NUMPAD6) {
			return Direction.RIGHT;
		} else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_NUMPAD8) {
			return Direction.UP;
		} else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_NUMPAD2) {
			return Direction.DOWN;
		} else {
			return null;
		}
	}

	@Override
	public void reset() {
		clearProgramNodeFailedToRun();
		AmstradProgramRepository repo = getProgramBrowser().getProgramRepository();
		repo.refresh();
		enterFolder(repo.getRootNode());
	}

	@Override
	public void enterFolder(FolderNode folderNode) {
		enterFolder(folderNode, null);
	}

	protected void enterFolder(FolderNode folderNode, Node childNodeInFocus) {
		getCarouselComponent().populateFolderContents(folderNode, childNodeInFocus);
		getCarouselOutline().setVisible(getCarouselComponent().getItemCount() > 1);
		getCarouselBreadcrumb().syncWith(getCarouselComponent());
		changeFocusToCarousel();
	}

	protected void changeFocusToCarousel() {
		getFocusManager().changeFocusOwner(getCarouselComponent().getUI());
	}

	@Override
	public void runProgram(AmstradProgram program) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				AmstradMonitorMode mode = program.getPreferredMonitorMode();
				try {
					releaseKeyboard();
					getAmstradPc().reboot(true, true);
					getProgramLoader(program).load(program).run();
					getProgramBrowser().fireProgramRun(program);
					notifyProgramRunFailState(program, false);
					close();
					if (mode != null) {
						getAmstradPc().getMonitor().setMode(mode);
					}
				} catch (AmstradProgramException exc) {
					exc.printStackTrace();
					acquireKeyboard();
					notifyProgramRunFailState(program, true);
				}
			}
		}).start();
	}

	private AmstradProgramLoader getProgramLoader(AmstradProgram program) {
		if (getProgramBrowser().isStagedRun(program)) {
			return AmstradProgramLoaderFactory.getInstance().createStagedBasicProgramLoader(getAmstradPc(),
					new EndingBasicAction() {

						@Override
						public void perform(AmstradProgramRuntime programRuntime) {
							AmstradFactory.getInstance().getAmstradContext().showProgramBrowser(getAmstradPc());
							notifyProgramRunFailState(program, programRuntime.getExitCode() != 0);
						}
					});
		} else {
			return AmstradProgramLoaderFactory.getInstance().createLoaderFor(program, getAmstradPc());
		}
	}

	private void notifyProgramRunFailState(AmstradProgram program, boolean failed) {
		clearProgramNodeFailedToRun();
		CarouselItem item = getCurrentCarouselItem();
		if (item instanceof CarouselProgramItem) {
			CarouselProgramItem programItem = (CarouselProgramItem) item;
			ProgramNode programNode = programItem.getProgramNode();
			if (programNode.getProgram().getProgramName().equals(program.getProgramName())) {
				programItem.setPreviousRunFailed(failed);
				if (failed) {
					setProgramNodeFailedToRun(programNode);
				}
			}
		}
	}

	private void clearProgramNodeFailedToRun() {
		setProgramNodeFailedToRun(null);
	}

	@Override
	public AmstradProgram getCurrentProgram() {
		AmstradProgram program = null;
		Node node = getCurrentRepositoryNode();
		if (node != null && node.isProgram()) {
			program = node.asProgram().getProgram();
		}
		return program;
	}

	protected Node getCurrentRepositoryNode() {
		Node node = null;
		CarouselItem item = getCurrentCarouselItem();
		if (item instanceof CarouselRepositoryItem) {
			node = ((CarouselRepositoryItem) item).getRepositoryNode();
		}
		return node;
	}

	protected CarouselItem getCurrentCarouselItem() {
		return getCarouselComponent().getSelectedItem();
	}

	private boolean isCarouselComponent(Component comp) {
		return getCarouselComponent().getUI().equals(comp);
	}

	private boolean isBreadcrumbComponent(Component comp) {
		return getCarouselBreadcrumb().getUI().equals(comp);
	}

	@Override
	public boolean isFailedToRun(ProgramNode programNode) {
		return programNode != null && programNode.equals(getProgramNodeFailedToRun());
	}

	@Override
	public boolean isFocusOnCarousel() {
		return isCarouselComponent(getFocusManager().getFocusOwner());
	}

	@Override
	public boolean isFocusOnBreadcrumb() {
		return isBreadcrumbComponent(getFocusManager().getFocusOwner());
	}

	@Override
	public boolean isStretchToFullscreen() {
		return true;
	}

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		return AmstradAlternativeDisplaySourceType.PROGRAM_BROWSER;
	}

	@Override
	protected CarouselLayoutManager getLayout() {
		return (CarouselLayoutManager) super.getLayout();
	}

	@Override
	public CarouselAmstradProgramBrowser getProgramBrowser() {
		return programBrowser;
	}

	public CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

	public void setTheme(CarouselProgramBrowserTheme theme) {
		this.theme = theme;
	}

	protected AmstradProgramCoverImageProducer getProgramCoverImageProducer() {
		return programCoverImageProducer;
	}

	private void setProgramCoverImageProducer(AmstradProgramCoverImageProducer imageProducer) {
		this.programCoverImageProducer = imageProducer;
	}

	protected AmstradFolderCoverImageProducer getFolderCoverImageProducer() {
		return folderCoverImageProducer;
	}

	private void setFolderCoverImageProducer(AmstradFolderCoverImageProducer imageProducer) {
		this.folderCoverImageProducer = imageProducer;
	}

	protected CarouselFocusManager getFocusManager() {
		return focusManager;
	}

	private void setFocusManager(CarouselFocusManager focusManager) {
		this.focusManager = focusManager;
	}

	protected CarouselComponentFactory getComponentFactory() {
		return componentFactory;
	}

	private void setComponentFactory(CarouselComponentFactory factory) {
		this.componentFactory = factory;
	}

	protected CarouselComponent getCarouselComponent() {
		return carouselComponent;
	}

	private void setCarouselComponent(CarouselComponent comp) {
		this.carouselComponent = comp;
	}

	protected CarouselOutline getCarouselOutline() {
		return carouselOutline;
	}

	private void setCarouselOutline(CarouselOutline outline) {
		this.carouselOutline = outline;
	}

	protected CarouselBreadcrumb getCarouselBreadcrumb() {
		return carouselBreadcrumb;
	}

	private void setCarouselBreadcrumb(CarouselBreadcrumb breadcrumb) {
		this.carouselBreadcrumb = breadcrumb;
	}

	private Node getLastVisitedNode() {
		return lastVisitedNode;
	}

	private void setLastVisitedNode(Node node) {
		this.lastVisitedNode = node;
	}

	private ProgramNode getProgramNodeFailedToRun() {
		return programNodeFailedToRun;
	}

	private void setProgramNodeFailedToRun(ProgramNode programNode) {
		this.programNodeFailedToRun = programNode;
	}

	private class CarouselComponentItemTracker extends SlidingItemListAdapter {

		private boolean landed;

		public CarouselComponentItemTracker() {
		}

		@Override
		public synchronized void notifyItemSelectionChanged(SlidingItemListComponent component,
				SlidingItem selectedItem, int selectedItemIndex) {
			if (isLanded()) {
				setLanded(false);
				notifyCursorLeftRepositoryNode();
			}
			if (selectedItem instanceof CarouselRepositoryItem) {
				Node node = ((CarouselRepositoryItem) selectedItem).getRepositoryNode();
				setLastVisitedNode(node);
			}
		}

		@Override
		public synchronized void notifyItemSelectionLanded(SlidingItemListComponent component, SlidingItem selectedItem,
				int selectedItemIndex) {
			if (selectedItem instanceof CarouselRepositoryItem) {
				setLanded(true);
				Node node = ((CarouselRepositoryItem) selectedItem).getRepositoryNode();
				notifyCursorAtRepositoryNode(node);
			}
		}

		private boolean isLanded() {
			return landed;
		}

		private void setLanded(boolean landed) {
			this.landed = landed;
		}

	}

	private class FocusTracker implements FocusListener {

		private SlidingCursor carouselCursor;

		private SlidingCursor breadcrumbCursor;

		public FocusTracker() {
			this.carouselCursor = getCarouselComponent().getSlidingCursor();
			this.breadcrumbCursor = getCarouselBreadcrumb().getSlidingCursor();
		}

		@Override
		public void notifyComponentGainedFocus(Component newFocusOwner) {
			if (isCarouselComponent(newFocusOwner)) {
				getCarouselComponent().setSlidingCursor(getCarouselCursor());
			} else if (isBreadcrumbComponent(newFocusOwner)) {
				getCarouselBreadcrumb().setSlidingCursor(getBreadcrumbCursor());
			}
		}

		@Override
		public void notifyComponentLostFocus(Component oldFocusOwner) {
			if (isCarouselComponent(oldFocusOwner)) {
				getCarouselComponent().setSlidingCursor(null);
			} else if (isBreadcrumbComponent(oldFocusOwner)) {
				getCarouselBreadcrumb().moveToLastItem();
				getCarouselBreadcrumb().setSlidingCursor(null);
			}
		}

		@Override
		public void notifyFocusOwnerCleared() {
			getCarouselComponent().setSlidingCursor(null);
			getCarouselBreadcrumb().setSlidingCursor(null);
		}

		private SlidingCursor getCarouselCursor() {
			return carouselCursor;
		}

		private SlidingCursor getBreadcrumbCursor() {
			return breadcrumbCursor;
		}

	}

}