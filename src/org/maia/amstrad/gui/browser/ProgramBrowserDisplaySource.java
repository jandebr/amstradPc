package org.maia.amstrad.gui.browser;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.gui.browser.components.FolderItemList;
import org.maia.amstrad.gui.browser.components.ProgramFileReferencesSheet;
import org.maia.amstrad.gui.browser.components.ProgramImageGallery;
import org.maia.amstrad.gui.browser.components.ProgramInfoSheet;
import org.maia.amstrad.gui.browser.components.ProgramMenu;
import org.maia.amstrad.gui.browser.components.ProgramMenuItem;
import org.maia.amstrad.gui.browser.components.ProgramRunMenuItem;
import org.maia.amstrad.gui.browser.components.ProgramSheet;
import org.maia.amstrad.gui.browser.components.StackedFolderItemList;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.pc.monitor.display.source.AmstradWindowDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemSettings;
import org.maia.util.GenericListenerList;
import org.maia.util.StringUtils;

public class ProgramBrowserDisplaySource extends AmstradWindowDisplaySource {

	private AmstradProgramRepository programRepository;

	private StackedFolderItemList stackedFolderItemList;

	private Window currentWindow;

	private ProgramMenu programMenu;

	private ProgramInfoSheet programInfoSheet;

	private ProgramImageGallery programImageGallery;

	private ProgramFileReferencesSheet programFileReferencesSheet;

	private GenericListenerList<ProgramBrowserListener> browserListeners;

	private ProgramBrowserTheme theme;

	private boolean programInfoShortcutActive;

	private static final String SETTING_SHOW_COVER_IMAGES = "program_browser.cover_images.show";

	private static final String SETTING_SHOW_MINI_INFO = "program_browser.mini_info.show";

	private static final int LABEL_WIDTH = 18;

	public static int SYMBOL_CODE_MONITOR = 176;

	public static int SYMBOL_CODE_HOME = 177;

	private ProgramBrowserDisplaySource(AmstradPc amstradPc, String windowTitle, Window initialWindow) {
		super(amstradPc, windowTitle);
		this.currentWindow = initialWindow;
		this.browserListeners = new GenericListenerList<ProgramBrowserListener>();
		setTheme(new ProgramBrowserThemeFromSettings());
		setRestoreMonitorSettingsOnDispose(!isStandaloneInfo()); // as this source may switch to COLOR
	}

	public static ProgramBrowserDisplaySource createProgramRepositoryBrowser(AmstradPc amstradPc,
			AmstradProgramRepository programRepository) {
		ProgramBrowserDisplaySource ds = new ProgramBrowserDisplaySource(amstradPc, "Program  Browser", Window.MAIN);
		ds.setProgramRepository(programRepository);
		ds.setStackedFolderItemList(ds.createStackedFolderItemList(20));
		return ds;
	}

	public static ProgramBrowserDisplaySource createProgramInfo(AmstradPc amstradPc, AmstradProgram program) {
		ProgramBrowserDisplaySource ds = new ProgramBrowserDisplaySource(amstradPc, program.getProgramName(),
				Window.PROGRAM_INFO_STANDALONE);
		ds.setProgramInfoSheet(ds.createProgramInfoSheet(program));
		return ds;
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		if (!isStandaloneInfo()) {
			getAmstradPc().getMonitor().setMode(AmstradMonitorMode.COLOR);
		}
		canvas.border(getTheme().getMainWindowBorderInk()).paper(getTheme().getMainWindowBackgroundInk());
		canvas.symbol(SYMBOL_CODE_MONITOR, 255, 129, 129, 129, 255, 24, 126, 0); // monitor icon
		canvas.symbol(SYMBOL_CODE_HOME, 24, 60, 126, 255, 126, 110, 110, 124); // home icon
	}

	@Override
	protected void renderWindowTitleBar(AmstradDisplayCanvas canvas) {
		if (!isStandaloneInfo()) {
			super.renderWindowTitleBar(canvas);
			renderHomeButton(canvas);
		}
	}

	private void renderHomeButton(AmstradDisplayCanvas canvas) {
		if (isFocusOnHomeButton(canvas)) {
			setMouseOverButton(true);
			canvas.paper(14).pen(24);
		} else {
			canvas.paper(5).pen(26);
		}
		canvas.locate(1, 1).print("  ").paper(getTheme().getMainWindowBackgroundInk());
		canvas.move(8, 399).drawChrMonospaced(SYMBOL_CODE_HOME);
	}

	private boolean isFocusOnHomeButton(AmstradDisplayCanvas canvas) {
		return !isModalWindowOpen() && isMouseOverHomeButton(canvas);
	}

	private boolean isMouseOverHomeButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(1, 1, 2, 1));
	}

	@Override
	protected void renderWindowContent(AmstradDisplayCanvas canvas) {
		if (isStandaloneInfo()) {
			renderProgramSheet(getProgramInfoSheet(), canvas);
		} else {
			renderStack(getStackedFolderItemList(), canvas);
			if (Window.PROGRAM_MENU_MODAL.equals(getCurrentWindow())) {
				renderProgramMenu(getProgramMenu(), canvas);
			} else if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow())) {
				renderProgramSheet(getProgramInfoSheet(), canvas);
			} else if (Window.PROGRAM_IMAGE_GALLERY_MODAL.equals(getCurrentWindow())) {
				renderProgramImageGallery(getProgramImageGallery(), canvas);
			} else if (Window.PROGRAM_FILE_REFERENCES_MODAL.equals(getCurrentWindow())) {
				renderProgramSheet(getProgramFileReferencesSheet(), canvas);
			}
		}
	}

	private void renderStack(StackedFolderItemList stack, AmstradDisplayCanvas canvas) {
		Image image = getCurrentCoverImage(); // may be null when not yet available
		// left side
		if (image != null && stack.size() > 1) {
			renderImageCenterFit(image, canvas, canvas.getTextAreaBoundsOnCanvas(1, 3, 20, 25));
		} else {
			FolderItemList itemList = stack.peek(stack.size() > 1 ? 1 : 0);
			renderFolderItemList(itemList, 2, 4, stack.size() == 1,
					canShowMiniInfo() && (!hasCurrentCoverImage() || getCurrentNode().isFolder()), canvas);
			if (stack.size() > 2) {
				renderStackLeftExtentHint(stack, 2, 4, canvas);
			}
		}
		// right side
		if (stack.size() > 1) {
			FolderItemList itemList = stack.peek();
			renderFolderItemList(itemList, 22, 4, true, canShowMiniInfo() && !hasCurrentCoverImage(), canvas);
		} else if (image != null) {
			renderImageCenterFit(image, canvas, canvas.getTextAreaBoundsOnCanvas(21, 3, 40, 25));
		}
	}

	private void renderFolderItemList(FolderItemList itemList, int tx0, int ty0, boolean hasFocus, boolean showMiniInfo,
			AmstradDisplayCanvas canvas) {
		if (itemList.isEmpty()) {
			canvas.pen(getTheme().getEmptyEntryInk()).locate(tx0, ty0).print("<empty>");
			if (!isModalWindowOpen() && isItemListCursorBlinkOn()) {
				canvas.locate(tx0 - 1, ty0).printChr(133); // cursor
			}
		} else {
			int ty = ty0;
			int i = itemList.getIndexOfFirstItemShowing();
			while (i < itemList.size() && ty < ty0 + itemList.getMaxItemsShowing()) {
				Node item = itemList.getItem(i);
				String label = StringUtils.fitWidth(item.getName(), LABEL_WIDTH);
				if (itemList.getIndexOfSelectedItem() == i) {
					if (hasFocus) {
						if (!isModalWindowOpen() && isItemListCursorBlinkOn()) {
							canvas.pen(getTheme().getEntryCursorInk()).locate(tx0 - 1, ty).printChr(133); // cursor
						}
						if (showMiniInfo) {
							renderMiniInfo(item, canvas);
						}
						canvas.paper(getTheme().getFocusEntryHighlightInk());
					} else {
						canvas.paper(getTheme().getEntryHighlightInk());
					}
				}
				if (item.isFolder()) {
					canvas.pen(getTheme().getFolderEntryInk()).locate(tx0, ty).print(label);
					canvas.paper(getTheme().getMainWindowBackgroundInk()).printChr(246);
				} else {
					canvas.pen(getTheme().getProgramEntryInk()).locate(tx0, ty).print(label);
					canvas.paper(getTheme().getMainWindowBackgroundInk());
				}
				ty++;
				i++;
			}
			if (hasFocus) {
				renderFolderItemListTopExtentHint(itemList, tx0, ty0, canvas);
				renderFolderItemListBottomExtentHint(itemList, tx0, ty0, canvas);
			}
		}
	}

	private void renderMiniInfo(Node node, AmstradDisplayCanvas canvas) {
		if (node.isProgram()) {
			AmstradProgram program = node.asProgram().getProgram();
			AmstradMonitorMode mode = program.getPreferredMonitorMode();
			int c1 = 11, c2 = 20;
			if (AmstradMonitorMode.GREEN.equals(mode)) {
				c1 = 9;
				c2 = 22;
			} else if (AmstradMonitorMode.GRAY.equals(mode)) {
				c1 = 13;
				c2 = 12;
			} else if (AmstradMonitorMode.COLOR.equals(mode)) {
				c1 = 8;
				c2 = 17;
			}
			canvas.move(0, 15).pen(c2).drawStrProportional(program.getProgramName(), 0.5f);
			if (!StringUtils.isEmpty(program.getAuthor())) {
				canvas.pen(c1).drawStrProportional(" by ", 0.5f);
				canvas.pen(c2).drawStrProportional(program.getAuthor(), 0.5f);
			}
			if (program.getProductionYear() > 0) {
				canvas.pen(c1).drawStrProportional(", ", 0.5f);
				canvas.pen(c2).drawStrProportional(String.valueOf(program.getProductionYear()), 0.5f);
			}
			if (!StringUtils.isEmpty(program.getProgramDescription())) {
				String desc = StringUtils.splitOnNewlines(program.getProgramDescription()).get(0);
				canvas.move(0, 7).pen(c1).drawStrProportional(desc, 0.5f);
			}
		} else {
			canvas.move(0, 15).pen(getTheme().getFolderEntryInk()).drawStrProportional(node.getName(), 0.5f);
		}
	}

	private void renderFolderItemListTopExtentHint(FolderItemList itemList, int tx0, int ty0,
			AmstradDisplayCanvas canvas) {
		if (itemList.getIndexOfFirstItemShowing() > 0) {
			canvas.pen(getTheme().getExtentHintInk())
					.move(canvas.getTextCursorBoundsOnCanvas(tx0, ty0 - 1).getLocation()).mover(0, 2);
			for (int i = 0; i < LABEL_WIDTH; i++) {
				canvas.drawChrMonospaced(244);
			}
		}
	}

	private void renderFolderItemListBottomExtentHint(FolderItemList itemList, int tx0, int ty0,
			AmstradDisplayCanvas canvas) {
		if (itemList.getIndexOfLastItemShowing() < itemList.size() - 1) {
			canvas.pen(getTheme().getExtentHintInk())
					.move(canvas.getTextCursorBoundsOnCanvas(tx0, ty0 + itemList.getMaxItemsShowing()).getLocation())
					.mover(0, 2);
			for (int i = 0; i < LABEL_WIDTH; i++) {
				canvas.drawChrMonospaced(245);
			}
		}
	}

	private void renderStackLeftExtentHint(StackedFolderItemList stack, int tx0, int ty0, AmstradDisplayCanvas canvas) {
		canvas.pen(getTheme().getExtentHintInk());
		for (int i = 0; i < stack.getMaxItemsShowing(); i++) {
			canvas.locate(tx0 - 1, ty0 + i).printChr(247);
		}
	}

	private void renderProgramMenu(ProgramMenu menu, AmstradDisplayCanvas canvas) {
		renderModalWindow(8, 7, 33, 19, menu.getProgram().getProgramName(), getTheme().getModalWindowBackgroundInk(),
				canvas);
		int tx0 = 10, ty0 = 11, ty = ty0;
		int i = menu.getIndexOfFirstItemShowing();
		while (i < menu.size() && ty < ty0 + menu.getMaxItemsShowing()) {
			ProgramMenuItem item = menu.getItem(i);
			String label = StringUtils.fitWidth(item.getLabel(), LABEL_WIDTH);
			boolean enabled = item.isEnabled();
			if (menu.getIndexOfSelectedItem() == i) {
				if (isItemListCursorBlinkOn()) {
					canvas.pen(enabled ? getTheme().getEntryCursorInk() : getTheme().getDisabledMenuItemInk())
							.locate(tx0 - 1, ty).printChr(133); // cursor
				}
				canvas.paper(item.getFocusBackgroundColor());
			}
			if (enabled) {
				canvas.pen(item.getLabelColor());
			} else {
				canvas.pen(getTheme().getDisabledMenuItemInk()).paper(getTheme().getModalWindowBackgroundInk());
			}
			canvas.locate(tx0, ty).print(label);
			canvas.paper(getTheme().getModalWindowBackgroundInk());
			ty++;
			i++;
		}
		canvas.paper(getTheme().getMainWindowBackgroundInk());
	}

	private void renderProgramSheet(ProgramSheet sheet, AmstradDisplayCanvas canvas) {
		renderModalWindow(4, 3, 37, 24, sheet.getProgram().getProgramName(), getTheme().getModalWindowBackgroundInk(),
				canvas);
		renderColoredTextArea(sheet, 6, 6, 30, canvas);
		canvas.paper(getTheme().getMainWindowBackgroundInk());
	}

	private void renderProgramImageGallery(ProgramImageGallery gallery, AmstradDisplayCanvas canvas) {
		renderModalWindow(4, 3, 37, 24, gallery.getProgram().getProgramName(), getTheme().getModalWindowBackgroundInk(),
				canvas);
		// Visual
		Rectangle bounds = deriveProgramImageVisualBounds(gallery, canvas);
		AmstradProgramImage image = gallery.getSelectedItem();
		renderProgramImageCenterFit(image, canvas, bounds);
		// Index
		boolean hasCaptions = gallery.hasCaptions();
		int yt = hasCaptions ? 22 : 23;
		int i = gallery.getIndexOfSelectedItem();
		int n = gallery.size();
		if (n > 32) {
			canvas.pen(17).locate(5, yt);
			canvas.print(StringUtils.fitWidthCenterAlign((i + 1) + " of " + n, 32));
		} else if (n > 1) {
			canvas.move(320 - n * 8, canvas.getTextCursorBoundsOnCanvas(5, yt).y + 4);
			for (int j = 0; j < n; j++) {
				canvas.pen(j == i ? 8 : 17).drawChrMonospaced(j == i ? 233 : 232);
			}
		}
		// Caption
		if (!StringUtils.isEmpty(image.getCaption())) {
			canvas.pen(14).locate(5, 23);
			canvas.print(StringUtils.fitWidthCenterAlign(image.getCaption(), 32));
		}
	}

	private Rectangle deriveProgramImageVisualBounds(ProgramImageGallery gallery, AmstradDisplayCanvas canvas) {
		int y1 = 6;
		int y2 = 23;
		int padding = 2;
		if (gallery.hasCaptions()) {
			y2--;
			padding = 8;
		}
		if (gallery.size() > 1) {
			y2--;
			padding = 8;
		}
		Rectangle rect = canvas.getTextAreaBoundsOnCanvas(5, y1, 36, y2);
		rect.height -= padding;
		return rect;
	}

	private void renderProgramImageCenterFit(AmstradProgramImage programImage, AmstradDisplayCanvas canvas,
			Rectangle bounds) {
		Image image = programImage.getImage();
		if (image != null) {
			renderImageCenterFit(image, canvas, bounds);
		} else {
			canvas.move(bounds.x + bounds.width / 2 - 8, bounds.y - bounds.height / 2 + 8);
			canvas.pen(13).drawChrMonospaced(225);
		}
	}

	private void renderImageCenterFit(Image image, AmstradDisplayCanvas canvas, Rectangle bounds) {
		int iWidth = image.getWidth(null);
		int iHeight = image.getHeight(null);
		double sx = Math.min(bounds.width / (double) iWidth, 1.0);
		double sy = Math.min(bounds.height / (double) iHeight, 1.0);
		double s = Math.min(sx, sy); // scaling factor
		int cWidth = (int) Math.floor(s * iWidth);
		int cHeight = (int) Math.floor(s * iHeight);
		int x0 = bounds.x + (bounds.width - cWidth) / 2;
		int y0 = bounds.y - (bounds.height - cHeight) / 2;
		canvas.drawImage(image, x0, y0, cWidth, cHeight);
	}

	@Override
	protected int getWindowTitleColorIndex() {
		return getTheme().getMainWindowTitleInk();
	}

	@Override
	protected int getModalWindowTitleColorIndex() {
		return getTheme().getModalWindowTitleInk();
	}

	@Override
	protected int getModalWindowBorderColorIndex() {
		return getTheme().getModalWindowBorderInk();
	}

	@Override
	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		super.mouseClickedOnCanvas(canvas, canvasPosition);
		if (isFocusOnHomeButton(canvas)) {
			home();
		}
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		resetItemListCursorBlinkOffsetTime();
		handleShortcutKey(e);
		if (Window.MAIN.equals(getCurrentWindow())) {
			handleKeyboardKeyInMainWindow(e);
		} else if (Window.PROGRAM_MENU_MODAL.equals(getCurrentWindow())) {
			handleKeyboardKeyInProgramMenu(e);
		} else if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow()) || isStandaloneInfo()) {
			handleKeyboardKeyInProgramInfoSheet(e);
		} else if (Window.PROGRAM_IMAGE_GALLERY_MODAL.equals(getCurrentWindow())) {
			handleKeyboardKeyInProgramImageGallery(e);
		} else if (Window.PROGRAM_FILE_REFERENCES_MODAL.equals(getCurrentWindow())) {
			handleKeyboardKeyInProgramFileReferencesSheet(e);
		}
	}

	private void handleKeyboardKeyInMainWindow(KeyEvent e) {
		StackedFolderItemList stack = getStackedFolderItemList();
		handleKeyboardKeyInItemList(e, stack.peek());
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_NUMPAD6) {
			stack.browseIntoSelectedItem();
		} else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_NUMPAD4) {
			stack.browseBack();
		} else if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_NUMPAD5) {
			if (stack.canCreateProgramMenu()) {
				AmstradProgram program = stack.getSelectedItem().asProgram().getProgram();
				setProgramMenu(createProgramMenu(program));
				setCurrentWindow(Window.PROGRAM_MENU_MODAL);
			} else {
				stack.browseIntoSelectedItem();
			}
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			if (stack.size() > 1) {
				stack.browseBack();
			} else if (!getSystemSettings().isProgramBrowserCentric()) {
				close();
			}
		} else if (keyCode == KeyEvent.VK_F5) {
			home();
		}
	}

	private void handleKeyboardKeyInProgramMenu(KeyEvent e) {
		ProgramMenu menu = getProgramMenu();
		handleKeyboardKeyInItemList(e, menu);
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_NUMPAD5) {
			menu.getSelectedItem().execute();
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			closeModalWindow();
		}
	}

	private void handleKeyboardKeyInProgramInfoSheet(KeyEvent e) {
		ProgramInfoSheet sheet = getProgramInfoSheet();
		handleKeyboardKeyInItemList(e, sheet);
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ESCAPE) {
			closeModalWindow();
		}
	}

	private void handleKeyboardKeyInProgramImageGallery(KeyEvent e) {
		ProgramImageGallery gallery = getProgramImageGallery();
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_NUMPAD6) {
			gallery.browseOneItemDown();
		} else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_NUMPAD4) {
			gallery.browseOneItemUp();
		} else if (keyCode == KeyEvent.VK_HOME) {
			gallery.browseHome();
		} else if (keyCode == KeyEvent.VK_END) {
			gallery.browseEnd();
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			closeModalWindow();
		}
	}

	private void handleKeyboardKeyInProgramFileReferencesSheet(KeyEvent e) {
		ProgramFileReferencesSheet sheet = getProgramFileReferencesSheet();
		handleKeyboardKeyInItemList(e, sheet);
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ESCAPE) {
			closeModalWindow();
		}
	}

	private void handleShortcutKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_F1) {
			// Info
			if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow())
					|| Window.PROGRAM_INFO_STANDALONE.equals(getCurrentWindow())) {
				// Already showing info
			} else {
				AmstradProgram program = getCurrentProgram();
				if (program != null && program.hasDescriptiveInfo()) {
					setProgramInfoShortcutActive(Window.MAIN.equals(getCurrentWindow()));
					openProgramInfoModalWindow(program);
				}
			}
		} else if (keyCode == KeyEvent.VK_SPACE) {
			// Run
			if (isStandaloneInfo()) {
				closeModalWindow(); // Resume run
			} else if (Window.PROGRAM_MENU_MODAL.equals(getCurrentWindow())) {
				ProgramRunMenuItem runItem = getProgramMenu().getItemTyped(ProgramRunMenuItem.class);
				if (runItem != null && runItem.isEnabled()) {
					runItem.execute();
				}
			} else {
				AmstradProgram program = getCurrentProgram();
				if (program != null) {
					ProgramRunMenuItem runItem = createProgramMenu(program).getItemTyped(ProgramRunMenuItem.class);
					if (runItem != null && runItem.isEnabled()) {
						close();
						runItem.execute();
					}
				}
			}
		}
	}

	@Override
	public void closeModalWindow() {
		super.closeModalWindow();
		if (isStandaloneInfo()) {
			close();
		} else if ((Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow()) && !isProgramInfoShortcutActive())
				|| Window.PROGRAM_IMAGE_GALLERY_MODAL.equals(getCurrentWindow())
				|| Window.PROGRAM_FILE_REFERENCES_MODAL.equals(getCurrentWindow())) {
			// Return to program menu
			setCurrentWindow(Window.PROGRAM_MENU_MODAL);
		} else {
			// Return to main window
			setProgramInfoShortcutActive(false);
			setCurrentWindow(Window.MAIN);
		}
	}

	@Override
	public void closeMainWindow() {
		if (getSystemSettings().isProgramBrowserCentric()) {
			getAmstradPc().getActions().getPowerOffAction().powerOff();
		} else {
			super.closeMainWindow();
		}
	}

	public void home() {
		getStackedFolderItemList().reset();
	}

	public void openProgramInfoModalWindow(AmstradProgram program) {
		setProgramInfoSheet(createProgramInfoSheet(program));
		setCurrentWindow(Window.PROGRAM_INFO_MODAL);
	}

	public void openProgramImageGalleryModalWindow(AmstradProgram program) {
		setProgramImageGallery(new ProgramImageGallery(program));
		setCurrentWindow(Window.PROGRAM_IMAGE_GALLERY_MODAL);
	}

	public void openProgramFileReferencesModalWindow(AmstradProgram program) {
		setProgramFileReferencesSheet(new ProgramFileReferencesSheet(program, getAmstradPc(), 18, 30,
				getTheme().getModalWindowBackgroundInk()));
		setCurrentWindow(Window.PROGRAM_FILE_REFERENCES_MODAL);
	}

	public void addListener(ProgramBrowserListener listener) {
		getBrowserListeners().addListener(listener);
	}

	public void removeListener(ProgramBrowserListener listener) {
		getBrowserListeners().removeListener(listener);
	}

	public void notifyProgramLoaded(AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programLoadedFromBrowser(this, program);
		}
	}

	public void notifyProgramRun(AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programRunFromBrowser(this, program);
		}
	}

	private StackedFolderItemList createStackedFolderItemList(int maxItemsShowing) {
		return new StackedFolderItemList(getProgramRepository(), maxItemsShowing);
	}

	private ProgramMenu createProgramMenu(AmstradProgram program) {
		return new ProgramMenu(this, program);
	}

	private ProgramInfoSheet createProgramInfoSheet(AmstradProgram program) {
		ProgramInfoSheet sheet = new ProgramInfoSheet(program, 18, 30, getTheme().getModalWindowBackgroundInk());
		if (program.getPreferredMonitorMode() != null) {
			sheet.browseOneItemDown();
		}
		return sheet;
	}

	private boolean canShowMiniInfo() {
		if (getUserSettings().getBool(SETTING_SHOW_MINI_INFO, true)) {
			return !isModalWindowOpen() || Window.PROGRAM_MENU_MODAL.equals(getCurrentWindow());
		} else {
			return false;
		}
	}

	private boolean hasCurrentCoverImage() {
		if (getUserSettings().getBool(SETTING_SHOW_COVER_IMAGES, true)) {
			Node node = getCurrentNode();
			return node != null && node.getCoverImage() != null;
		} else {
			return false;
		}
	}

	private Image getCurrentCoverImage() {
		if (hasCurrentCoverImage()) {
			return getCurrentNode().getCoverImage().demandImage();
		} else {
			return null;
		}
	}

	private Node getCurrentNode() {
		return getStackedFolderItemList().peek().getSelectedItem();
	}

	public AmstradProgram getCurrentProgram() {
		AmstradProgram program = null;
		if (isStandaloneInfo()) {
			program = getProgramInfoSheet().getProgram();
		} else {
			Node node = getCurrentNode();
			if (node != null && node.isProgram()) {
				program = node.asProgram().getProgram();
			}
		}
		return program;
	}

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		if (isStandaloneInfo())
			return AmstradAlternativeDisplaySourceType.PROGRAM_STANDALONE_INFO;
		else
			return AmstradAlternativeDisplaySourceType.PROGRAM_BROWSER;
	}

	public AmstradProgramRepository getProgramRepository() {
		return programRepository;
	}

	private void setProgramRepository(AmstradProgramRepository programRepository) {
		this.programRepository = programRepository;
	}

	private StackedFolderItemList getStackedFolderItemList() {
		return stackedFolderItemList;
	}

	private void setStackedFolderItemList(StackedFolderItemList stackedFolderItemList) {
		this.stackedFolderItemList = stackedFolderItemList;
	}

	private Window getCurrentWindow() {
		return currentWindow;
	}

	private void setCurrentWindow(Window currentWindow) {
		this.currentWindow = currentWindow;
	}

	private ProgramMenu getProgramMenu() {
		return programMenu;
	}

	private void setProgramMenu(ProgramMenu programMenu) {
		this.programMenu = programMenu;
	}

	private ProgramInfoSheet getProgramInfoSheet() {
		return programInfoSheet;
	}

	private void setProgramInfoSheet(ProgramInfoSheet programInfoSheet) {
		this.programInfoSheet = programInfoSheet;
	}

	private ProgramImageGallery getProgramImageGallery() {
		return programImageGallery;
	}

	private void setProgramImageGallery(ProgramImageGallery programImageGallery) {
		this.programImageGallery = programImageGallery;
	}

	private ProgramFileReferencesSheet getProgramFileReferencesSheet() {
		return programFileReferencesSheet;
	}

	private void setProgramFileReferencesSheet(ProgramFileReferencesSheet programFileReferencesSheet) {
		this.programFileReferencesSheet = programFileReferencesSheet;
	}

	public boolean isStandaloneInfo() {
		return Window.PROGRAM_INFO_STANDALONE.equals(getCurrentWindow());
	}

	private AmstradSettings getUserSettings() {
		return getAmstradContext().getUserSettings();
	}

	public AmstradSystemSettings getSystemSettings() {
		return getAmstradSystem().getSystemSettings();
	}

	private AmstradSystem getAmstradSystem() {
		return getAmstradContext().getAmstradSystem();
	}

	private AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	private GenericListenerList<ProgramBrowserListener> getBrowserListeners() {
		return browserListeners;
	}

	public ProgramBrowserTheme getTheme() {
		return theme;
	}

	public void setTheme(ProgramBrowserTheme theme) {
		this.theme = theme;
	}

	private boolean isProgramInfoShortcutActive() {
		return programInfoShortcutActive;
	}

	private void setProgramInfoShortcutActive(boolean shortcutActive) {
		this.programInfoShortcutActive = shortcutActive;
	}

	private static enum Window {

		MAIN,

		PROGRAM_MENU_MODAL,

		PROGRAM_INFO_MODAL,

		PROGRAM_INFO_STANDALONE,

		PROGRAM_IMAGE_GALLERY_MODAL,

		PROGRAM_FILE_REFERENCES_MODAL;

	}

}