package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.ProgramBrowserStartupAnimationControl;
import org.maia.amstrad.gui.browser.carousel.CarouselComponent.CarouselOutline;
import org.maia.amstrad.gui.browser.carousel.CarouselCoverImageFactory.CassetteCoverImageFactory;
import org.maia.amstrad.gui.browser.carousel.action.CarouselAction;
import org.maia.amstrad.gui.browser.carousel.action.CarouselEnterFolderAction;
import org.maia.amstrad.gui.browser.carousel.action.CarouselHighlightItemAction;
import org.maia.amstrad.gui.browser.carousel.action.CarouselRunProgramAction;
import org.maia.amstrad.gui.browser.carousel.action.CarouselStartupAction;
import org.maia.amstrad.gui.browser.carousel.animation.CarouselAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.CarouselAnimationFactory;
import org.maia.amstrad.gui.browser.carousel.animation.startup.CarouselStartupAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselEnterFolderHost;
import org.maia.amstrad.gui.browser.carousel.api.CarouselRunProgramHost;
import org.maia.amstrad.gui.browser.carousel.api.CarouselStartupHost;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumb;
import org.maia.amstrad.gui.browser.carousel.breadcrumb.CarouselBreadcrumbItem;
import org.maia.amstrad.gui.browser.carousel.cursor.CarouselCursorRenderer;
import org.maia.amstrad.gui.browser.carousel.cursor.CarouselCursorSymbolRenderer;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselRepositoryItem;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserDefaultTheme;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.SpriteImageRLE;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.pc.monitor.display.source.AmstradAwtDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.impl.CarouselAmstradProgramBrowser;
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
import org.maia.util.ColorUtils;

public abstract class CarouselProgramBrowserDisplaySourceBase extends AmstradAwtDisplaySource
		implements ProgramBrowserDisplaySource, CarouselStartupHost, CarouselEnterFolderHost, CarouselRunProgramHost {

	private CarouselAmstradProgramBrowser programBrowser;

	private CarouselProgramBrowserTheme theme;

	private AmstradGraphicsContext graphicsContext;

	private AmstradProgramCoverImageProducer programCoverImageProducer;

	private AmstradFolderCoverImageProducer folderCoverImageProducer;

	private CarouselFocusManager focusManager;

	private CarouselCoverImageFactory coverImageFactory;

	private CarouselComponentFactory componentFactory;

	private CarouselAnimationFactory animationFactory;

	private CarouselCursorRenderer carouselCursorRenderer;

	private CarouselComponent carouselComponent;

	private CarouselOutline carouselOutline;

	private CarouselBreadcrumb carouselBreadcrumb;

	private Node lastVisitedNode;

	private ProgramNode programNodeFailedToRun;

	private CarouselStartupAction startupActionInProgress;

	private CarouselEnterFolderAction enterFolderActionInProgress;

	private CarouselRunProgramAction runProgramActionInProgress;

	private CarouselHighlightItemAction itemHighlightActionInProgress;

	private ProlongedStartupAnimationIndicator prolongedStartupAnimationIndicator;

	private boolean prolongedStartupAnimation;

	private long carouselOutlineShowTimeMillis;

	private static final String SETTING_STARTUP_ANIMATION_COLOR = "program_browser.startup_animation.force_color";

	protected CarouselProgramBrowserDisplaySourceBase(CarouselAmstradProgramBrowser programBrowser) {
		super(programBrowser.getAmstradPc());
		this.programBrowser = programBrowser;
		this.graphicsContext = getAmstradPc().getMonitor().getGraphicsContext();
		setRestoreMonitorSettingsOnDispose(true); // as this source switches to COLOR
		setAutoPauseResume(true);
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		setTheme(createTheme(graphicsContext));
		setBackground(getTheme().getBackgroundColor());
		setAnimationFactory(createAnimationFactory());
		clearCarouselOutlineShowTimeMillis();
		if (getUserSettings().getBool(SETTING_STARTUP_ANIMATION_COLOR, false)) {
			getAmstradPc().getMonitor().setMode(AmstradMonitorMode.COLOR); // ahead of startup action
		}
		initStartupAction(); // ahead of buildUI()
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
		setCarouselCursorRenderer(createCarouselCursorRenderer());
	}

	private void initCoverImageProducers() {
		Dimension imageSize = getImageSizeForCoverImages();
		CarouselProgramBrowserTheme theme = getTheme();
		CarouselCoverImageFactory factory = getCoverImageFactory();
		if (factory == null || !factory.getImageSize().equals(imageSize) || !factory.getTheme().equals(theme)) {
			factory = createCoverImageFactory(imageSize, theme);
			setProgramCoverImageProducer(factory.createProgramCoverImageProducer());
			setFolderCoverImageProducer(factory.createFolderCoverImageProducer());
			setCoverImageFactory(factory);
		}
	}

	protected Dimension getImageSizeForCoverImages() {
		Insets padding = getLayout().getCarouselPadding();
		int height = getLayout().getCarouselBounds().height - padding.top - padding.bottom;
		int width = Math.round(0.6f * height);
		return new Dimension(width, height);
	}

	protected CarouselCoverImageFactory createCoverImageFactory(Dimension imageSize,
			CarouselProgramBrowserTheme theme) {
		return new CassetteCoverImageFactory(imageSize, theme);
	}

	protected CarouselComponentFactory createComponentFactory() {
		return new CarouselComponentFactory(getTheme(), getLayout(), getProgramCoverImageProducer(),
				getFolderCoverImageProducer());
	}

	protected CarouselAnimationFactory createAnimationFactory() {
		return new CarouselAnimationFactory(getTheme());
	}

	private void buildCarousel() {
		CarouselComponent comp = getComponentFactory().createCarouselComponent(this);
		comp.addListener(new CarouselComponentItemTracker());
		add(comp.getUI(), CarouselLayoutManager.CAROUSEL);
		setCarouselComponent(comp);
	}

	private void buildCarouselOutline() {
		CarouselOutline outline = getComponentFactory().createCarouselOutline(getCarouselComponent());
		setCarouselOutline(outline);
	}

	private void buildCarouselBreadcrumb() {
		CarouselBreadcrumb breadcrumb = getComponentFactory().createCarouselBreadcrumb(this);
		add(breadcrumb.getUI(), CarouselLayoutManager.CAROUSEL_BREADCRUMB);
		setCarouselBreadcrumb(breadcrumb);
	}

	protected CarouselCursorRenderer createCarouselCursorRenderer() {
		CarouselCursorSymbolRenderer renderer = new CarouselCursorSymbolRenderer(this,
				getTheme().getCarouselCursorColor());
		renderer.setPulseIntervalTimeMillis(getCursorPulseIntervalTimeMillis());
		return renderer;
	}

	protected long getCursorPulseIntervalTimeMillis() {
		return 2200L;
	}

	protected CarouselFocusManager createFocusManager() {
		CarouselFocusManager focusManager = new CarouselFocusManager();
		focusManager.setRequestFocusOnComponents(false); // keep focus on display (to catch key events)
		return focusManager;
	}

	protected void initFocusManager() {
		CarouselFocusManager focusManager = getFocusManager();
		focusManager.addListener(new BreadcrumbController());
		focusManager.addFocusTransferBidirectional(getCarouselComponent().getUI(), Direction.DOWN,
				getCarouselBreadcrumb().getUI());
		focusManager.clearFocusOwner();
	}

	protected void initCarousel() {
		notifyCursorLeftRepositoryNode();
		Node node = getLastVisitedNode();
		if (node != null && !node.isRoot()) {
			enterFolderAsync(node.getParent(), node);
		} else {
			enterFolderAsync(getProgramBrowser().getProgramRepository().getRootNode());
		}
	}

	protected void initStartupAction() {
		setProlongedStartupAnimation(false);
		CarouselStartupAnimation animation = createAnimationToStartup();
		CarouselStartupAction action = new CarouselStartupAction(this, animation);
		setStartupActionInProgress(action);
		setProlongedStartupAnimationIndicator(new ProlongedStartupAnimationIndicator(animation));
		action.perform();
	}

	protected void startHighlightingNode(Node node) {
		CarouselAnimation animation = createAnimationToHighlightNode(node);
		CarouselHighlightItemAction action = new CarouselHighlightItemAction(this, animation);
		setItemHighlightActionInProgress(action);
		action.perform();
	}

	protected CarouselStartupAnimation createAnimationToStartup() {
		return getAnimationFactory().createAnimationToStartup(this);
	}

	protected CarouselAnimation createAnimationToEnterFolder(FolderNode folderNode) {
		return getAnimationFactory().createAnimationToEnterFolder(folderNode, this);
	}

	protected CarouselAnimation createAnimationToRunProgram(ProgramNode programNode) {
		return getAnimationFactory().createAnimationToRunProgram(programNode, this);
	}

	protected CarouselAnimation createAnimationToHighlightNode(Node node) {
		return getAnimationFactory().createAnimationToHighlightNode(node, this);
	}

	@Override
	public void pauseBuildingUI() {
		setComponentAdditionDeferred(true);
	}

	@Override
	public void resumeBuildingUI() {
		setComponentAdditionDeferred(false);
		addDeferred();
	}

	@Override
	public Color getDisplayBackgroundColor() {
		return getBackground();
	}

	@Override
	public void setDisplayBackgroundColor(Color color) {
		setBackground(color);
	}

	protected void showCarouselOutline() {
		add(getCarouselOutline(), CarouselLayoutManager.CAROUSEL_OUTLINE);
	}

	protected void hideCarouselOutline() {
		remove(getCarouselOutline());
		clearCarouselOutlineShowTimeMillis();
	}

	protected boolean isCarouselOutlineShowing() {
		return getCarouselOutline().isShowing();
	}

	@Override
	protected void renderContent(Graphics2D g, int width, int height) {
		CarouselStartupAction startup = getStartupActionInProgress();
		if (startup != null) {
			renderStartupAnimation(g, width, height, startup);
		} else {
			super.renderContent(g, width, height);
			renderFocus(g);
			renderAnimation(g, width, height, getEnterFolderActionInProgress());
			renderAnimation(g, width, height, getRunProgramActionInProgress());
			renderAnimation(g, width, height, getItemHighlightActionInProgress());
			renderCarouselOutline();
			renderCarouselCursor(g);
		}
	}

	private void renderStartupAnimation(Graphics2D g, int width, int height, CarouselStartupAction startup) {
		boolean browserReady = getEnterFolderActionInProgress() == null;
		boolean endAnimation = browserReady;
		ProgramBrowserStartupAnimationControl control = getStartupAnimationControl();
		if (!ProgramBrowserStartupAnimationControl.NEVER.equals(control)) {
			renderAnimation(g, width, height, startup);
			if (isProlongedStartupAnimation()) {
				renderStartupAnimationProlongedIndication(g, width, height);
			}
			if (ProgramBrowserStartupAnimationControl.ALWAYS.equals(control) || startup.isAnimationStarted()) {
				boolean minAnimationElapsedTime = startup.isAnimationStarted()
						&& startup.getAnimationElapsedTimeMillis() >= startup.getMinimumAnimationDurationMillis();
				endAnimation = browserReady && minAnimationElapsedTime && !isProlongedStartupAnimation();
			}
		}
		if (endAnimation) {
			clearStartupActionInProgress();
		}
	}

	private void renderStartupAnimationProlongedIndication(Graphics2D g, int width, int height) {
		ProlongedStartupAnimationIndicator ind = getProlongedStartupAnimationIndicator();
		int w = ind.getWidth();
		int h = ind.getHeight();
		ind.move(0, 0);
		ind.draw(g);
		ind.move(width - w, 0);
		ind.flipX();
		ind.draw(g);
		ind.move(width - w, height - h);
		ind.flipY();
		ind.draw(g);
		ind.move(0, height - h);
		ind.flipX();
		ind.draw(g);
		ind.flipY();
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
		// Subclasses may extend
	}

	private void renderAnimation(Graphics2D g, int width, int height, CarouselAction action) {
		if (action != null) {
			action.startAnimationWhenAppropriate(width, height);
			if (action.isAnimationStarted()) {
				try {
					action.getAnimation().renderOntoDisplay(g, width, height, action.getAnimationElapsedTimeMillis());
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void renderCarouselOutline() {
		if (isCarouselOutlineShowing()) {
			if (getCarouselComponent().isStationary()) {
				hideCarouselOutline();
			}
		} else {
			long now = System.currentTimeMillis();
			if (now >= getCarouselOutlineShowTimeMillis() && !getCarouselComponent().isItemListFitsInsideViewport()) {
				showCarouselOutline();
				clearCarouselOutlineShowTimeMillis();
			}
		}
	}

	protected void renderCarouselCursor(Graphics2D g) {
		CarouselCursorRenderer renderer = getCarouselCursorRenderer();
		if (renderer != null && !isCarouselOutlineShowing()) {
			Rectangle cursorBounds = getCarouselComponent().getCursorOuterBoundsInComponent();
			Rectangle outlineBounds = getCarouselOutlineBounds();
			int width = cursorBounds.width;
			int height = outlineBounds.height;
			Graphics2D g2 = (Graphics2D) g.create(cursorBounds.x, outlineBounds.y, width, height);
			g2.translate(width / 2, height / 2);
			renderer.render(g2, width, height);
			g2.dispose();
		}
	}

	@Override
	public void dispose(JComponent displayComponent) {
		super.dispose(displayComponent);
		clearProgramNodeFailedToRun();
		clearRunProgramActionInProgress();
		clearEnterFolderActionInProgress();
		clearItemHighlightActionInProgress();
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		Component focusOwner = getFocusManager().getFocusOwner();
		if (getStartupActionInProgress() != null && getStartupActionInProgress().isAnimationStarted()) {
			handleKeyboardKeyInStartupAnimation(e);
		} else if (focusOwner != null) {
			handleKeyboardKeyInFocusOwner(e, focusOwner);
		}
		if (!e.isConsumed()) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_F5) {
				reset();
				e.consume();
			} else if (keyCode == KeyEvent.VK_F11 && getSystemSettings().isFullscreenToggleEnabled()) {
				if (!isStartupInProgress()) {
					// Toggle fullscreen by re-init
					close();
					getAmstradPc().getMonitor().toggleFullscreen();
					show();
				}
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
			if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_NUMPAD5) {
				CarouselItem item = comp.getSelectedItem();
				if (item != null) {
					item.execute(this);
					e.consume();
				}
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				Node parent = comp.getFolderNode();
				if (parent != null && !parent.isRoot()) {
					enterFolderAsync(parent.getParent(), parent);
					e.consume();
				} else {
					if (getEnterFolderActionInProgress() != null && !isStartupInProgress()) {
						cancelEnterFolderAsync();
						e.consume();
					}
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
		} else if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_NUMPAD5) {
			CarouselBreadcrumbItem item = breadcrumb.getSelectedItem();
			if (item != null && !item.isSeparator()) {
				enterFolderAsync(item.getFolderNode());
				e.consume();
			}
		}
	}

	private void handleKeyboardKeyInStartupAnimation(KeyEvent e) {
		if (isProlongedStartupAnimation()) {
			setProlongedStartupAnimation(false);
			e.consume();
		} else if (e.getKeyCode() == KeyEvent.VK_ADD) {
			setProlongedStartupAnimation(true);
			e.consume();
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
		if (isInitialized()) {
			clearProgramNodeFailedToRun();
			AmstradProgramRepository repo = getProgramBrowser().getProgramRepository();
			repo.refresh();
			enterFolderAsync(repo.getRootNode());
		}
	}

	@Override
	public void enterFolderAsync(FolderNode folderNode) {
		enterFolderAsync(folderNode, null);
	}

	protected synchronized void enterFolderAsync(FolderNode folderNode, Node childNodeInFocus) {
		clearItemHighlightActionInProgress(); // one animation at a time
		CarouselAnimation animation = createAnimationToEnterFolder(folderNode);
		CarouselEnterFolderAction action = new CarouselEnterFolderAction(this, folderNode, childNodeInFocus, animation);
		setEnterFolderActionInProgress(action);
		action.perform();
	}

	protected synchronized void cancelEnterFolderAsync() {
		CarouselEnterFolderAction action = getEnterFolderActionInProgress();
		if (action != null) {
			action.cancel();
		}
	}

	@Override
	public synchronized void notifyEnterFolderCompleted(CarouselEnterFolderAction action) {
		getCarouselOutline().setVisible(getCarouselComponent().getItemCount() > 1);
		getCarouselBreadcrumb().syncWith(getCarouselComponent());
		changeFocusToCarousel();
		clearEnterFolderActionInProgress(action);
	}

	@Override
	public synchronized void notifyEnterFolderCancelled(CarouselEnterFolderAction action) {
		clearEnterFolderActionInProgress(action);
	}

	protected void notifyCursorAtRepositoryNode(Node node) {
		startHighlightingNode(node);
		// Subclasses may extend
	}

	protected void notifyCursorLeftRepositoryNode() {
		clearItemHighlightActionInProgress();
		cancelEnterFolderAsync();
		// Subclasses may extend
	}

	private void clearEnterFolderActionInProgress(CarouselEnterFolderAction action) {
		if (action.equals(getEnterFolderActionInProgress())) {
			clearEnterFolderActionInProgress();
		}
	}

	private void clearEnterFolderActionInProgress() {
		clearCarouselAction(getEnterFolderActionInProgress());
		setEnterFolderActionInProgress(null);
	}

	@Override
	public void runProgramAsync(ProgramNode programNode) {
		clearItemHighlightActionInProgress(); // one animation at a time
		CarouselAnimation animation = createAnimationToRunProgram(programNode);
		CarouselRunProgramAction action = new CarouselRunProgramAction(this, programNode, animation);
		setRunProgramActionInProgress(action);
		action.perform();
	}

	@Override
	public synchronized void notifyProgramRunFailState(ProgramNode programNode, boolean failed) {
		clearProgramNodeFailedToRun();
		clearRunProgramActionInProgress();
		CarouselProgramItem programItem = (CarouselProgramItem) getCarouselItem(programNode);
		if (programItem != null) {
			programItem.setPreviousRunFailed(failed);
		}
		if (failed) {
			setProgramNodeFailedToRun(programNode);
		}
		refreshCarouselUI();
	}

	private void clearProgramNodeFailedToRun() {
		setProgramNodeFailedToRun(null);
	}

	private void clearRunProgramActionInProgress() {
		clearCarouselAction(getRunProgramActionInProgress());
		setRunProgramActionInProgress(null);
	}

	private void clearStartupActionInProgress() {
		clearCarouselAction(getStartupActionInProgress());
		setStartupActionInProgress(null);
		setProlongedStartupAnimation(false);
	}

	private void clearItemHighlightActionInProgress() {
		clearCarouselAction(getItemHighlightActionInProgress());
		setItemHighlightActionInProgress(null);
	}

	private void clearCarouselAction(CarouselAction action) {
		if (action != null) {
			action.stopAnimation();
		}
	}

	protected void changeFocusToCarousel() {
		getFocusManager().changeFocusOwner(getCarouselComponent().getUI());
	}

	protected void refreshCarouselUI() {
		getCarouselComponent().refreshUI();
	}

	@Override
	public CarouselItem getCarouselItem(Node node) {
		return getCarouselComponent().getItem(node);
	}

	@Override
	public Rectangle getCarouselItemBounds(Node node) {
		Rectangle bounds = null;
		CarouselItem item = getCarouselItem(node);
		if (item != null) {
			bounds = getCarouselComponent().getItemBoundsInComponent(item);
			if (bounds != null) {
				Point loc = getCarouselComponent().getUI().getLocation();
				bounds.translate(loc.x, loc.y);
			}
		}
		return bounds;
	}

	@Override
	public Rectangle getCarouselOutlineBounds() {
		return getLayout().getCarouselOutlineBounds();
	}

	@Override
	public CarouselBreadcrumbItem getBreadcrumbItem(FolderNode folderNode) {
		return getCarouselBreadcrumb().getItem(folderNode);
	}

	@Override
	public Rectangle getBreadcrumbItemBounds(FolderNode folderNode) {
		Rectangle bounds = null;
		CarouselBreadcrumbItem item = getBreadcrumbItem(folderNode);
		if (item != null) {
			bounds = getCarouselBreadcrumb().getItemBoundsInComponent(item);
			if (bounds != null) {
				Point loc = getCarouselBreadcrumb().getUI().getLocation();
				bounds.translate(loc.x, loc.y);
			}
		}
		return bounds;
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

	private boolean isInitialized() {
		return getCarouselComponent() != null;
	}

	public boolean isStartupInProgress() {
		return getStartupActionInProgress() != null;
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

	@Override
	public AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
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

	protected CarouselCoverImageFactory getCoverImageFactory() {
		return coverImageFactory;
	}

	private void setCoverImageFactory(CarouselCoverImageFactory factory) {
		this.coverImageFactory = factory;
	}

	protected CarouselComponentFactory getComponentFactory() {
		return componentFactory;
	}

	private void setComponentFactory(CarouselComponentFactory factory) {
		this.componentFactory = factory;
	}

	protected CarouselAnimationFactory getAnimationFactory() {
		return animationFactory;
	}

	private void setAnimationFactory(CarouselAnimationFactory factory) {
		this.animationFactory = factory;
	}

	private CarouselCursorRenderer getCarouselCursorRenderer() {
		return carouselCursorRenderer;
	}

	private void setCarouselCursorRenderer(CarouselCursorRenderer renderer) {
		this.carouselCursorRenderer = renderer;
	}

	@Override
	public CarouselComponent getCarouselComponent() {
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

	private CarouselStartupAction getStartupActionInProgress() {
		return startupActionInProgress;
	}

	private void setStartupActionInProgress(CarouselStartupAction action) {
		this.startupActionInProgress = action;
	}

	private ProgramBrowserStartupAnimationControl getStartupAnimationControl() {
		return getAmstradContext().getStartupAnimationControl();
	}

	@Override
	public CarouselEnterFolderAction getEnterFolderActionInProgress() {
		return enterFolderActionInProgress;
	}

	private void setEnterFolderActionInProgress(CarouselEnterFolderAction action) {
		this.enterFolderActionInProgress = action;
		refreshCarouselUI();
	}

	@Override
	public CarouselRunProgramAction getRunProgramActionInProgress() {
		return runProgramActionInProgress;
	}

	private void setRunProgramActionInProgress(CarouselRunProgramAction action) {
		this.runProgramActionInProgress = action;
		refreshCarouselUI();
	}

	private CarouselHighlightItemAction getItemHighlightActionInProgress() {
		return itemHighlightActionInProgress;
	}

	private void setItemHighlightActionInProgress(CarouselHighlightItemAction action) {
		this.itemHighlightActionInProgress = action;
	}

	private boolean isProlongedStartupAnimation() {
		return prolongedStartupAnimation;
	}

	private void setProlongedStartupAnimation(boolean extended) {
		this.prolongedStartupAnimation = extended;
	}

	private ProlongedStartupAnimationIndicator getProlongedStartupAnimationIndicator() {
		return prolongedStartupAnimationIndicator;
	}

	private void setProlongedStartupAnimationIndicator(ProlongedStartupAnimationIndicator indicator) {
		this.prolongedStartupAnimationIndicator = indicator;
	}

	private long getCarouselOutlineShowTimeMillis() {
		return carouselOutlineShowTimeMillis;
	}

	private void setCarouselOutlineShowTimeMillis(long timeMillis) {
		this.carouselOutlineShowTimeMillis = timeMillis;
	}

	private void clearCarouselOutlineShowTimeMillis() {
		setCarouselOutlineShowTimeMillis(Long.MAX_VALUE);
	}

	private class CarouselComponentItemTracker extends SlidingItemListAdapter {

		public CarouselComponentItemTracker() {
		}

		@Override
		public synchronized void notifyItemSelectionChanged(SlidingItemListComponent component,
				SlidingItem selectedItem, int selectedItemIndex) {
			if (component.isLanded()) {
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
			refreshCarouselUI();
			if (selectedItem instanceof CarouselRepositoryItem) {
				Node node = ((CarouselRepositoryItem) selectedItem).getRepositoryNode();
				notifyCursorAtRepositoryNode(node);
			}
		}

		@Override
		public void notifyStartSliding(SlidingItemListComponent component) {
			setCarouselOutlineShowTimeMillis(System.currentTimeMillis() + 1000L);
			if (getAmstradContext().isLowPerformance()) {
				notifyCursorLeftRepositoryNode();
			} else {
				clearItemHighlightActionInProgress();
			}
		}

		@Override
		public void notifyStopSliding(SlidingItemListComponent component) {
			clearCarouselOutlineShowTimeMillis();
		}

	}

	private class BreadcrumbController implements FocusListener {

		private SlidingCursor breadcrumbCursor;

		public BreadcrumbController() {
			this.breadcrumbCursor = getCarouselBreadcrumb().getSlidingCursor();
		}

		@Override
		public void notifyComponentGainedFocus(Component newFocusOwner) {
			if (isBreadcrumbComponent(newFocusOwner)) {
				getCarouselBreadcrumb().setSlidingCursor(getBreadcrumbCursor());
			}
		}

		@Override
		public void notifyComponentLostFocus(Component oldFocusOwner) {
			if (isBreadcrumbComponent(oldFocusOwner)) {
				getCarouselBreadcrumb().moveToLastItem();
				getCarouselBreadcrumb().setSlidingCursor(null);
			}
		}

		@Override
		public void notifyFocusOwnerCleared() {
			getCarouselBreadcrumb().setSlidingCursor(null);
		}

		private SlidingCursor getBreadcrumbCursor() {
			return breadcrumbCursor;
		}

	}

	private static class ProlongedStartupAnimationIndicator extends Sprite {

		public ProlongedStartupAnimationIndicator(CarouselStartupAnimation animation) {
			super(createIndicatorImage(), createColorMap(animation));
		}

		@Override
		public void draw(Graphics2D g) {
			float r = (1f + (float) Math.sin(System.currentTimeMillis() / 500.0)) / 2f;
			getColorMap().setColor(0, ColorUtils.interpolate(getColorMap().getColor(1), getColorMap().getColor(2), r));
			super.draw(g);
		}

		@Override
		public SpriteColorMapImpl getColorMap() {
			return (SpriteColorMapImpl) super.getColorMap();
		}

		private static SpriteImage createIndicatorImage() {
			return new SpriteImageRLE(16, 16,
					new int[] { -1, 15, 0, 1, -2, -1, 15, 0, 1, -2, -1, 15, 0, 1, -2, -1, 5, 0, 5, -1, 5, 0, 1, -2, -1,
							6, 0, 1, -1, 1, 0, 1, -1, 6, 0, 1, -2, -1, 3, 0, 1, -1, 2, 0, 1, -1, 1, 0, 1, -1, 2, 0, 1,
							-1, 3, 0, 1, -2, -1, 3, 0, 4, -1, 1, 0, 4, -1, 3, 0, 1, -2, -1, 3, 0, 1, -1, 7, 0, 1, -1, 3,
							0, 1, -2, -1, 3, 0, 4, -1, 1, 0, 4, -1, 3, 0, 1, -2, -1, 3, 0, 1, -1, 2, 0, 1, -1, 1, 0, 1,
							-1, 2, 0, 1, -1, 3, 0, 1, -2, -1, 6, 0, 1, -1, 1, 0, 1, -1, 6, 0, 1, -2, -1, 5, 0, 5, -1, 4,
							0, 1, -2, -1, 14, 0, 1, -2, -1, 13, 0, 1, -2, -1, 11, 0, 2, -2, 0, 11 });
		}

		private static SpriteColorMap createColorMap(CarouselStartupAnimation animation) {
			Color bg = animation.getDisplayBackgroundColor();
			SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
			if (ColorUtils.getBrightness(bg) < 0.5f) {
				colorMap.setColor(1, ColorUtils.adjustBrightness(bg, 0.5f));
			} else {
				colorMap.setColor(1, ColorUtils.adjustBrightness(bg, -0.5f));
			}
			colorMap.setColor(2, bg);
			return colorMap;
		}

	}

}