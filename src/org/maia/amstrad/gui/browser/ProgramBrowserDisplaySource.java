package org.maia.amstrad.gui.browser;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.components.FolderItemList;
import org.maia.amstrad.gui.browser.components.ItemList;
import org.maia.amstrad.gui.browser.components.ProgramFileReferencesSheet;
import org.maia.amstrad.gui.browser.components.ProgramImageGallery;
import org.maia.amstrad.gui.browser.components.ProgramInfoLine;
import org.maia.amstrad.gui.browser.components.ProgramInfoSheet;
import org.maia.amstrad.gui.browser.components.ProgramInfoTextSpan;
import org.maia.amstrad.gui.browser.components.ProgramMenu;
import org.maia.amstrad.gui.browser.components.ProgramMenuItem;
import org.maia.amstrad.gui.browser.components.ProgramSheet;
import org.maia.amstrad.gui.browser.components.StackedFolderItemList;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradWindowDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.ProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.util.StringUtils;

public class ProgramBrowserDisplaySource extends AmstradWindowDisplaySource {

	private AmstradProgramRepository programRepository;

	private StackedFolderItemList stackedFolderItemList;

	private Window currentWindow;

	private ProgramMenu programMenu;

	private ProgramInfoSheet programInfoSheet;

	private ProgramImageGallery programImageGallery;

	private ProgramFileReferencesSheet programFileReferencesSheet;

	private List<ProgramBrowserListener> browserListeners;

	private long itemListCursorBlinkOffsetTime;

	private static long itemListCursorBlinkTimeInterval = 500L;

	private static int COLOR_BORDER = 1;

	private static int COLOR_PAPER = 1;

	private static int COLOR_MODAL_BACKGROUND = 0;

	private static int LABEL_WIDTH = 18;

	private ProgramBrowserDisplaySource(AmstradPc amstradPc, String windowTitle, Window initialWindow) {
		super(amstradPc, windowTitle);
		this.currentWindow = initialWindow;
		this.browserListeners = new Vector<ProgramBrowserListener>();
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
		setFollowPrimaryDisplaySourceResolution(false);
		resetItemListCursorBlinkOffsetTime();
		getAmstradPc().getMonitor().setMonitorMode(AmstradMonitorMode.COLOR);
		getAmstradPc().getMonitor().setMonitorBilinearEffect(false);
		getAmstradPc().getMonitor().setMonitorScanLinesEffect(false);
		canvas.border(COLOR_BORDER).paper(COLOR_PAPER);
		canvas.symbol(254, 255, 129, 129, 129, 255, 24, 126, 0); // monitor
		canvas.symbol(255, 24, 60, 126, 255, 126, 110, 110, 124); // home
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
		canvas.locate(1, 1).print("  ").paper(COLOR_PAPER);
		canvas.move(8, 399).drawChrMonospaced(255);
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
		// left column
		FolderItemList itemList = stack.peek(stack.size() > 1 ? 1 : 0);
		renderFolderItemList(itemList, 2, 4, stack.size() == 1, canvas);
		renderStackLeftExtentHint(stack, 2, 4, canvas);
		// Right column
		if (stack.size() > 1) {
			itemList = stack.peek();
			renderFolderItemList(itemList, 22, 4, true, canvas);
		}
	}

	private void renderFolderItemList(FolderItemList itemList, int tx0, int ty0, boolean hasFocus,
			AmstradDisplayCanvas canvas) {
		if (itemList.isEmpty()) {
			canvas.pen(13).locate(tx0, ty0).print("<empty>");
			if (!isModalWindowOpen() && isItemListCursorBlinkOn()) {
				canvas.locate(tx0 - 1, ty0).printChr(133);
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
							canvas.pen(24).locate(tx0 - 1, ty).printChr(133);
						}
						if (!isModalWindowOpen() || Window.PROGRAM_MENU_MODAL.equals(getCurrentWindow())) {
							renderMiniInfo(item, canvas);
						}
						canvas.paper(2);
					} else {
						canvas.paper(3);
					}
				}
				if (item.isFolder()) {
					canvas.pen(16).locate(tx0, ty).print(label);
					canvas.paper(COLOR_PAPER).printChr(246);
				} else {
					canvas.pen(26).locate(tx0, ty).print(label);
					canvas.paper(COLOR_PAPER);
				}
				ty++;
				i++;
			}
			if (hasFocus) {
				renderItemListTopExtentHint(itemList, tx0, ty0, canvas);
				renderItemListBottomExtentHint(itemList, tx0, ty0, canvas);
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
			canvas.move(0, 15).pen(16).drawStrProportional(node.getName(), 0.5f);
		}
	}

	private void renderItemListTopExtentHint(ItemList itemList, int tx0, int ty0, AmstradDisplayCanvas canvas) {
		if (itemList.getIndexOfFirstItemShowing() > 0) {
			canvas.pen(13).move(canvas.getTextCursorBoundsOnCanvas(tx0, ty0 - 1).getLocation()).mover(0, -2);
			for (int i = 0; i < (LABEL_WIDTH + 1) / 2; i++) {
				canvas.drawChrMonospaced(196).mover(16, 0);
			}
		}
	}

	private void renderItemListBottomExtentHint(ItemList itemList, int tx0, int ty0, AmstradDisplayCanvas canvas) {
		if (itemList.getIndexOfLastItemShowing() < itemList.size() - 1) {
			canvas.pen(13)
					.move(canvas.getTextCursorBoundsOnCanvas(tx0, ty0 + itemList.getMaxItemsShowing()).getLocation())
					.mover(0, 4);
			for (int i = 0; i < (LABEL_WIDTH + 1) / 2; i++) {
				canvas.drawChrMonospaced(198).mover(16, 0);
			}
		}
	}

	private void renderStackLeftExtentHint(StackedFolderItemList stack, int tx0, int ty0, AmstradDisplayCanvas canvas) {
		if (stack.size() > 2) {
			canvas.pen(13);
			for (int i = 0; i < stack.getMaxItemsShowing(); i += 2) {
				canvas.locate(tx0 - 1, ty0 + i).printChr(199);
			}
		}
	}

	private void renderProgramMenu(ProgramMenu menu, AmstradDisplayCanvas canvas) {
		renderModalWindow(8, 7, 33, 19, menu.getProgram().getProgramName(), COLOR_MODAL_BACKGROUND, canvas);
		int tx0 = 10, ty0 = 11, ty = ty0;
		int i = menu.getIndexOfFirstItemShowing();
		while (i < menu.size() && ty < ty0 + menu.getMaxItemsShowing()) {
			ProgramMenuItem item = menu.getItem(i);
			String label = StringUtils.fitWidth(item.getLabel(), LABEL_WIDTH);
			if (menu.getIndexOfSelectedItem() == i) {
				if (isItemListCursorBlinkOn()) {
					canvas.pen(item.isEnabled() ? 24 : 13).locate(tx0 - 1, ty).printChr(133);
				}
				canvas.paper(item.getFocusBackgroundColor());
			}
			if (item.isEnabled()) {
				canvas.pen(item.getLabelColor());
			} else {
				canvas.pen(13).paper(COLOR_MODAL_BACKGROUND);
			}
			canvas.locate(tx0, ty).print(label);
			canvas.paper(COLOR_MODAL_BACKGROUND);
			ty++;
			i++;
		}
		canvas.paper(COLOR_PAPER);
	}

	private void renderProgramSheet(ProgramSheet sheet, AmstradDisplayCanvas canvas) {
		renderModalWindow(4, 3, 37, 24, sheet.getProgram().getProgramName(), COLOR_MODAL_BACKGROUND, canvas);
		int tx0 = 6, ty0 = 6, ty = ty0;
		int i = sheet.getIndexOfFirstItemShowing();
		while (i < sheet.size() && ty < ty0 + sheet.getMaxItemsShowing()) {
			canvas.locate(tx0, ty);
			ProgramInfoLine line = sheet.getLineItem(i);
			for (ProgramInfoTextSpan span : line.getTextSpans()) {
				canvas.paper(span.getPaperColorIndex()).pen(span.getPenColorIndex());
				canvas.print(span.getText());
			}
			canvas.paper(COLOR_MODAL_BACKGROUND);
			if (sheet.getIndexOfSelectedItem() == i) {
				canvas.pen(13).locate(tx0 - 1, ty).printChr(133);
			}
			ty++;
			i++;
		}
		// top extent hint
		if (sheet.getIndexOfFirstItemShowing() > 0) {
			canvas.pen(13).move(560, 319).drawChrMonospaced(196);
		}
		// bottom extent hint
		if (sheet.getIndexOfLastItemShowing() < sheet.size() - 1) {
			canvas.pen(13).move(560, 47).drawChrMonospaced(198);
		}
		canvas.paper(COLOR_PAPER);
	}

	private void renderProgramImageGallery(ProgramImageGallery gallery, AmstradDisplayCanvas canvas) {
		renderModalWindow(4, 3, 37, 24, gallery.getProgram().getProgramName(), COLOR_MODAL_BACKGROUND, canvas);
		// Visual
		Rectangle bounds = deriveProgramImageVisualBounds(gallery, canvas);
		ProgramImage image = gallery.getCurrentImage();
		renderProgramImageVisual(image, canvas, bounds);
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

	private void renderProgramImageVisual(ProgramImage image, AmstradDisplayCanvas canvas, Rectangle bounds) {
		Image visual = image.getVisual();
		if (visual != null) {
			int vWidth = visual.getWidth(null);
			int vHeight = visual.getHeight(null);
			double sx = Math.min(bounds.width / (double) vWidth, 1.0);
			double sy = Math.min(bounds.height / (double) vHeight, 1.0);
			double s = Math.min(sx, sy); // scaling factor
			int iWidth = (int) Math.floor(s * vWidth);
			int iHeight = (int) Math.floor(s * vHeight);
			int ix0 = bounds.x + (bounds.width - iWidth) / 2; // center
			int iy0 = bounds.y - (bounds.height - iHeight) / 2; // center
			canvas.drawImage(visual, ix0, iy0, iWidth, iHeight);
		} else {
			canvas.move(312, bounds.y - bounds.height / 2 + 8);
			canvas.pen(13).drawChrMonospaced(225);
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
		if (!isModalWindowOpen()) {
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
		if (keyCode == KeyEvent.VK_RIGHT) {
			stack.browseIntoSelectedItem();
		} else if (keyCode == KeyEvent.VK_LEFT) {
			stack.browseBack();
		} else if (keyCode == KeyEvent.VK_ENTER) {
			if (stack.canBrowseIntoSelectedItem()) {
				stack.browseIntoSelectedItem();
			} else if (stack.canCreateProgramMenu()) {
				AmstradProgram program = stack.getSelectedItem().asProgram().getProgram();
				setProgramMenu(createProgramMenu(program, 7));
				setCurrentWindow(Window.PROGRAM_MENU_MODAL);
			}
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			if (stack.size() > 1) {
				stack.browseBack();
			} else if (!isKioskMode()) {
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
		if (keyCode == KeyEvent.VK_ENTER) {
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
		if (keyCode == KeyEvent.VK_RIGHT) {
			gallery.browseOneItemDown();
		} else if (keyCode == KeyEvent.VK_LEFT) {
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

	private void handleKeyboardKeyInItemList(KeyEvent e, ItemList itemList) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_DOWN) {
			itemList.browseOneItemDown();
		} else if (keyCode == KeyEvent.VK_UP) {
			itemList.browseOneItemUp();
		} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
			itemList.browseOnePageDown();
		} else if (keyCode == KeyEvent.VK_PAGE_UP) {
			itemList.browseOnePageUp();
		} else if (keyCode == KeyEvent.VK_HOME) {
			itemList.browseHome();
		} else if (keyCode == KeyEvent.VK_END) {
			itemList.browseEnd();
		}
	}

	@Override
	public void closeModalWindow() {
		super.closeModalWindow();
		if (isStandaloneInfo()) {
			close();
		} else if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow())
				|| Window.PROGRAM_IMAGE_GALLERY_MODAL.equals(getCurrentWindow())
				|| Window.PROGRAM_FILE_REFERENCES_MODAL.equals(getCurrentWindow())) {
			setCurrentWindow(Window.PROGRAM_MENU_MODAL);
		} else {
			setCurrentWindow(Window.MAIN);
		}
	}

	@Override
	public void closeMainWindow() {
		if (isKioskMode()) {
			getAmstradPc().terminate();
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
		setProgramFileReferencesSheet(
				new ProgramFileReferencesSheet(program, getAmstradPc(), 18, 30, COLOR_MODAL_BACKGROUND));
		setCurrentWindow(Window.PROGRAM_FILE_REFERENCES_MODAL);
	}

	public void addReturnToProgramMenu() {
		if (getProgramMenu() != null) {
			getProgramMenu().addReturnMenuItem();
		}
	}

	public void addListener(ProgramBrowserListener listener) {
		getBrowserListeners().add(listener);
	}

	public void removeListener(ProgramBrowserListener listener) {
		getBrowserListeners().remove(listener);
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

	private ProgramMenu createProgramMenu(AmstradProgram program, int maxItemsShowing) {
		return new ProgramMenu(this, program, maxItemsShowing);
	}

	private ProgramInfoSheet createProgramInfoSheet(AmstradProgram program) {
		ProgramInfoSheet sheet = new ProgramInfoSheet(program, 18, 30, COLOR_MODAL_BACKGROUND);
		if (program.getPreferredMonitorMode() != null) {
			sheet.browseOneItemDown();
		}
		return sheet;
	}

	private AmstradProgram getCurrentProgram() {
		AmstradProgram program = null;
		if (getProgramMenu() != null) {
			program = getProgramMenu().getProgram();
		}
		return program;
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

	public boolean isKioskMode() {
		return AmstradFactory.getInstance().getAmstradContext().isKioskMode();
	}

	private List<ProgramBrowserListener> getBrowserListeners() {
		return browserListeners;
	}

	private boolean isItemListCursorBlinkOn() {
		long t = (System.currentTimeMillis() - itemListCursorBlinkOffsetTime) / itemListCursorBlinkTimeInterval;
		return t % 2 == 0;
	}

	private void resetItemListCursorBlinkOffsetTime() {
		this.itemListCursorBlinkOffsetTime = System.currentTimeMillis();
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