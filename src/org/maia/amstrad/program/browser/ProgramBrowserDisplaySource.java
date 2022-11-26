package org.maia.amstrad.program.browser;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.display.AmstradWindowDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.ProgramImage;
import org.maia.amstrad.program.AmstradProgram.UserControl;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.util.StringUtils;

public class ProgramBrowserDisplaySource extends AmstradWindowDisplaySource {

	private AmstradProgramRepository programRepository;

	private StackedFolderItemList stackedFolderItemList;

	private Window currentWindow;

	private ProgramMenu programMenu;

	private ProgramInfoSheet programInfoSheet;

	private ProgramImageGallery programImageGallery;

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

	public void addListener(ProgramBrowserListener listener) {
		getBrowserListeners().add(listener);
	}

	public void removeListener(ProgramBrowserListener listener) {
		getBrowserListeners().remove(listener);
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		setFollowPrimaryDisplaySourceResolution(false);
		resetItemListCursorBlinkOffsetTime();
		getAmstradPc().setMonitorMode(AmstradMonitorMode.COLOR);
		getAmstradPc().setMonitorBilinearEffect(false);
		getAmstradPc().setMonitorScanLinesEffect(false);
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
			renderProgramInfoSheet(getProgramInfoSheet(), canvas);
		} else {
			renderStack(getStackedFolderItemList(), canvas);
			if (Window.PROGRAM_MENU_MODAL.equals(getCurrentWindow())) {
				renderProgramMenu(getProgramMenu(), canvas);
			} else if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow())) {
				renderProgramInfoSheet(getProgramInfoSheet(), canvas);
			} else if (Window.PROGRAM_IMAGE_GALLERY_MODAL.equals(getCurrentWindow())) {
				renderProgramImageGallery(getProgramImageGallery(), canvas);
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
		renderModalWindow(8, 8, 33, 18, menu.getProgram().getProgramName(), COLOR_MODAL_BACKGROUND, canvas);
		int tx0 = 10, ty0 = 12, ty = ty0;
		int i = menu.getIndexOfFirstItemShowing();
		while (i < menu.size() && ty < ty0 + menu.getMaxItemsShowing()) {
			ProgramMenuItem item = menu.getItem(i);
			String label = StringUtils.fitWidth(item.getLabel(), LABEL_WIDTH);
			if (menu.getIndexOfSelectedItem() == i) {
				if (isItemListCursorBlinkOn()) {
					canvas.pen(item.isEnabled() ? 24 : 13).locate(tx0 - 1, ty).printChr(133);
				}
				canvas.paper(9);
			}
			if (item.isEnabled()) {
				canvas.pen(22);
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

	private void renderProgramInfoSheet(ProgramInfoSheet infoSheet, AmstradDisplayCanvas canvas) {
		renderModalWindow(4, 3, 37, 24, infoSheet.getProgram().getProgramName(), COLOR_MODAL_BACKGROUND, canvas);
		int tx0 = 6, ty0 = 6, ty = ty0;
		int i = infoSheet.getIndexOfFirstItemShowing();
		while (i < infoSheet.size() && ty < ty0 + infoSheet.getMaxItemsShowing()) {
			canvas.locate(tx0, ty);
			ProgramInfoLine line = infoSheet.getLineItem(i);
			for (ProgramInfoTextSpan span : line.getTextSpans()) {
				canvas.paper(span.getPaperColorIndex()).pen(span.getPenColorIndex());
				canvas.print(span.getText());
			}
			canvas.paper(COLOR_MODAL_BACKGROUND);
			if (infoSheet.getIndexOfSelectedItem() == i) {
				canvas.pen(13).locate(tx0 - 1, ty).printChr(133);
			}
			ty++;
			i++;
		}
		// top extent hint
		if (infoSheet.getIndexOfFirstItemShowing() > 0) {
			canvas.pen(13).move(560, 319).drawChrMonospaced(196);
		}
		// bottom extent hint
		if (infoSheet.getIndexOfLastItemShowing() < infoSheet.size() - 1) {
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
				setProgramMenu(stack.createProgramMenu(6));
				setCurrentWindow(Window.PROGRAM_MENU_MODAL);
			}
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			if (stack.size() > 1) {
				stack.browseBack();
			} else {
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
	protected void closeModalWindow() {
		super.closeModalWindow();
		if (isStandaloneInfo()) {
			close();
		} else if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow())
				|| Window.PROGRAM_IMAGE_GALLERY_MODAL.equals(getCurrentWindow())) {
			setCurrentWindow(Window.PROGRAM_MENU_MODAL);
		} else {
			setCurrentWindow(Window.MAIN);
		}
	}

	public void home() {
		getStackedFolderItemList().reset();
	}

	private ProgramInfoSheet createProgramInfoSheet(AmstradProgram program) {
		ProgramInfoSheet sheet = new ProgramInfoSheet(program, 18);
		int bg = COLOR_MODAL_BACKGROUND;
		int maxWidth = 30;
		AmstradMonitorMode mode = program.getPreferredMonitorMode();
		if (mode != null) {
			if (mode.equals(AmstradMonitorMode.GREEN)) {
				sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.spaces(maxWidth - 6), bg, bg),
						new ProgramInfoTextSpan("\u00FE GREEN", 0, 9)));
			} else if (mode.equals(AmstradMonitorMode.GRAY)) {
				sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.spaces(maxWidth - 5), bg, bg),
						new ProgramInfoTextSpan("\u00FE GRAY", 0, 13)));
			} else if (mode.equals(AmstradMonitorMode.COLOR)) {
				ProgramInfoLine line = new ProgramInfoLine(
						new ProgramInfoTextSpan(StringUtils.spaces(maxWidth - 6), bg, bg),
						new ProgramInfoTextSpan("\u00FE ", 0, 25));
				for (int i = 0; i < 5; i++)
					line.add(new ProgramInfoTextSpan(String.valueOf("COLOR".charAt(i)), 0, 14 + i));
				sheet.add(line);
			}
		}
		if (!StringUtils.isEmpty(program.getAuthor())) {
			if (sheet.isEmpty())
				sheet.add(new ProgramInfoLine());
			sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan("Author", bg, 25)));
			for (String text : StringUtils.splitOnNewlinesAndWrap(program.getAuthor(), maxWidth)) {
				sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(text, bg, 26)));
			}
		}
		if (program.getProductionYear() > 0) {
			if (!sheet.isEmpty())
				sheet.add(new ProgramInfoLine());
			sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan("Year", bg, 25)));
			sheet.add(
					new ProgramInfoLine(new ProgramInfoTextSpan(String.valueOf(program.getProductionYear()), bg, 26)));
		}
		if (!StringUtils.isEmpty(program.getNameOfTape()) || program.getBlocksOnTape() > 0) {
			if (!sheet.isEmpty())
				sheet.add(new ProgramInfoLine());
			String tape = !StringUtils.isEmpty(program.getNameOfTape()) ? program.getNameOfTape() : "?";
			String blocks = program.getBlocksOnTape() > 0 ? String.valueOf(program.getBlocksOnTape()) : "?";
			sheet.add(
					new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.fitWidth("Tape", 20) + " Blocks", bg, 25)));
			sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.fitWidth(tape, 20) + " ", bg, 26),
					new ProgramInfoTextSpan(blocks, bg, 26)));
		}
		if (!StringUtils.isEmpty(program.getProgramDescription())) {
			if (!sheet.isEmpty())
				sheet.add(new ProgramInfoLine());
			for (String text : StringUtils.splitOnNewlinesAndWrap(program.getProgramDescription(), maxWidth)) {
				sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(text, bg, 17)));
			}
		}
		if (!StringUtils.isEmpty(program.getAuthoringInformation())) {
			if (!sheet.isEmpty())
				sheet.add(new ProgramInfoLine());
			for (String text : StringUtils.splitOnNewlinesAndWrap(program.getAuthoringInformation(), maxWidth)) {
				sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(text, bg, 4)));
			}
		}
		if (!program.getUserControls().isEmpty()) {
			if (!sheet.isEmpty()) {
				sheet.add(new ProgramInfoLine());
				sheet.add(new ProgramInfoLine());
			}
			sheet.add(new ProgramInfoLine(
					new ProgramInfoTextSpan("\u008F\u008F\u00D4 User controls \u00D5\u008F\u008F", bg, 7)));
			for (UserControl uc : program.getUserControls()) {
				sheet.add(new ProgramInfoLine());
				if (uc.getHeading() != null) {
					List<String> hlines = StringUtils.splitOnNewlinesAndWrap(uc.getHeading(), maxWidth - 6);
					for (int i = 0; i < hlines.size(); i++) {
						String text = hlines.get(i);
						if (hlines.size() == 1) {
							sheet.add(new ProgramInfoLine(
									new ProgramInfoTextSpan("\u00CF\u00DC " + text + " \u00DD\u00CF", bg, 7)));
						} else if (i == 0) {
							sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(
									"\u00CF\u00DC " + StringUtils.fitWidth(text, maxWidth - 6) + " \u00DD\u00CF", bg,
									7)));
						} else {
							sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(
									"\u00CF  " + StringUtils.fitWidth(text, maxWidth - 6) + "  \u00CF", bg, 7)));
						}
					}
					sheet.add(new ProgramInfoLine());
				}
				sheet.add(new ProgramInfoLine(
						new ProgramInfoTextSpan(StringUtils.fitWidth(uc.getKey(), maxWidth), bg, 16)));
				for (String text : StringUtils.splitOnNewlinesAndWrap(uc.getDescription(), maxWidth - 2)) {
					sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.spaces(2) + text, bg, 26)));
				}
			}
		}
		sheet.add(new ProgramInfoLine());
		if (mode != null) {
			sheet.browseOneItemDown();
		}
		return sheet;
	}

	private StackedFolderItemList createStackedFolderItemList(int maxItemsShowing) {
		return new StackedFolderItemList(maxItemsShowing);
	}

	private AmstradProgram getCurrentProgram() {
		return getProgramMenu().getProgram();
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

	public boolean isStandaloneInfo() {
		return Window.PROGRAM_INFO_STANDALONE.equals(getCurrentWindow());
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

		PROGRAM_IMAGE_GALLERY_MODAL;

	}

	private class StackedFolderItemList {

		private Stack<FolderItemList> stack;

		private int maxItemsShowing;

		public StackedFolderItemList(int maxItemsShowing) {
			this.stack = new Stack<FolderItemList>();
			this.maxItemsShowing = maxItemsShowing;
			reset();
		}

		public void reset() {
			getProgramRepository().refresh();
			getStack().clear();
			getStack().push(new FolderItemList(getProgramRepository().getRootNode(), getMaxItemsShowing()));
		}

		public void browseIntoSelectedItem() {
			if (canBrowseIntoSelectedItem()) {
				push(new FolderItemList(getSelectedItem().asFolder(), getMaxItemsShowing()));
			}
		}

		public void browseBack() {
			if (size() > 1) {
				pop();
			}
		}

		public boolean canBrowseIntoSelectedItem() {
			Node selectedItem = getSelectedItem();
			if (selectedItem != null) {
				return selectedItem.isFolder();
			} else {
				return false;
			}
		}

		public boolean canCreateProgramMenu() {
			Node selectedItem = getSelectedItem();
			if (selectedItem != null) {
				return selectedItem.isProgram();
			} else {
				return false;
			}
		}

		public ProgramMenu createProgramMenu(int maxItemsShowing) {
			ProgramMenu menu = null;
			if (canCreateProgramMenu()) {
				AmstradProgram program = getSelectedItem().asProgram().getProgram();
				menu = new ProgramMenu(program, maxItemsShowing);
			}
			return menu;
		}

		public Node getSelectedItem() {
			Node selectedItem = null;
			if (size() > 0) {
				selectedItem = getStack().peek().getSelectedItem();
			}
			return selectedItem;
		}

		public int size() {
			return getStack().size();
		}

		public FolderItemList peek() {
			return getStack().peek();
		}

		public FolderItemList peek(int distanceFromTop) {
			return getStack().get(size() - 1 - distanceFromTop);
		}

		private void push(FolderItemList itemList) {
			getStack().push(itemList);
		}

		private void pop() {
			getStack().pop();
		}

		private Stack<FolderItemList> getStack() {
			return stack;
		}

		public int getMaxItemsShowing() {
			return maxItemsShowing;
		}

	}

	private abstract class ItemList {

		private int maxItemsShowing;

		private int indexOfFirstItemShowing;

		private int indexOfSelectedItem;

		protected ItemList(int maxItemsShowing) {
			this.maxItemsShowing = maxItemsShowing;
		}

		public void browseHome() {
			setIndexOfFirstItemShowing(0);
			setIndexOfSelectedItem(0);
		}

		public void browseEnd() {
			setIndexOfFirstItemShowing(Math.max(size() - getMaxItemsShowing(), 0));
			setIndexOfSelectedItem(size() - 1);
		}

		public void browseOneItemDown() {
			int i = getIndexOfSelectedItem();
			if (i < size() - 1) {
				if (i - getIndexOfFirstItemShowing() + 1 == getMaxItemsShowing()) {
					setIndexOfFirstItemShowing(getIndexOfFirstItemShowing() + 1);
				}
				setIndexOfSelectedItem(i + 1);
			}
		}

		public void browseOneItemUp() {
			int i = getIndexOfSelectedItem();
			if (i > 0) {
				if (i == getIndexOfFirstItemShowing()) {
					setIndexOfFirstItemShowing(getIndexOfFirstItemShowing() - 1);
				}
				setIndexOfSelectedItem(i - 1);
			}
		}

		public void browseOnePageDown() {
			for (int i = 0; i < getMaxItemsShowing(); i++)
				browseOneItemDown();
		}

		public void browseOnePageUp() {
			for (int i = 0; i < getMaxItemsShowing(); i++)
				browseOneItemUp();
		}

		public boolean isEmpty() {
			return size() == 0;
		}

		public abstract int size();

		public int getIndexOfLastItemShowing() {
			return Math.min(getIndexOfFirstItemShowing() + getMaxItemsShowing(), size()) - 1;
		}

		public int getMaxItemsShowing() {
			return maxItemsShowing;
		}

		public int getIndexOfFirstItemShowing() {
			return indexOfFirstItemShowing;
		}

		private void setIndexOfFirstItemShowing(int index) {
			this.indexOfFirstItemShowing = index;
		}

		public int getIndexOfSelectedItem() {
			return indexOfSelectedItem;
		}

		private void setIndexOfSelectedItem(int index) {
			this.indexOfSelectedItem = index;
		}

	}

	private class FolderItemList extends ItemList {

		private FolderNode folderNode;

		public FolderItemList(FolderNode folderNode, int maxItemsShowing) {
			super(maxItemsShowing);
			this.folderNode = folderNode;
		}

		@Override
		public int size() {
			return getItems().size();
		}

		public Node getSelectedItem() {
			Node selectedItem = null;
			if (!isEmpty()) {
				return getItem(getIndexOfSelectedItem());
			}
			return selectedItem;
		}

		public Node getItem(int index) {
			return getItems().get(index);
		}

		public List<Node> getItems() {
			return getFolderNode().getChildNodes();
		}

		private FolderNode getFolderNode() {
			return folderNode;
		}

	}

	private class ProgramMenu extends ItemList {

		private AmstradProgram program;

		private List<ProgramMenuItem> menuItems;

		public ProgramMenu(AmstradProgram program, int maxItemsShowing) {
			super(maxItemsShowing);
			this.program = program;
			this.menuItems = new Vector<ProgramMenuItem>();
			populateMenu();
		}

		private void populateMenu() {
			AmstradProgram program = getProgram();
			addMenuItem(new ProgramRunMenuItem(program));
			addMenuItem(new ProgramLoadMenuItem(program));
			addMenuItem(new ProgramInfoMenuItem(program));
			addMenuItem(new ProgramImagesMenuItem(program));
			addMenuItem(new ProgramCloseMenuItem(program));
		}

		private void addMenuItem(ProgramMenuItem menuItem) {
			getMenuItems().add(menuItem);
		}

		@Override
		public int size() {
			return getMenuItems().size();
		}

		public ProgramMenuItem getSelectedItem() {
			ProgramMenuItem selectedItem = null;
			if (!isEmpty()) {
				return getItem(getIndexOfSelectedItem());
			}
			return selectedItem;
		}

		public ProgramMenuItem getItem(int index) {
			return getMenuItems().get(index);
		}

		public AmstradProgram getProgram() {
			return program;
		}

		public List<ProgramMenuItem> getMenuItems() {
			return menuItems;
		}

	}

	private abstract class ProgramMenuItem {

		private AmstradProgram program;

		private String label;

		protected ProgramMenuItem(AmstradProgram program, String label) {
			this.program = program;
			this.label = label;
		}

		public abstract void execute();

		public boolean isEnabled() {
			return true;
		}

		public AmstradProgram getProgram() {
			return program;
		}

		public String getLabel() {
			return label;
		}

	}

	private abstract class ProgramLaunchMenuItem extends ProgramMenuItem {

		private long executeStartTime;

		private boolean failed;

		protected ProgramLaunchMenuItem(AmstradProgram program, String label) {
			super(program, label);
		}

		@Override
		public void execute() {
			if (isEnabled()) {
				executeStartTime = System.currentTimeMillis();
				new Thread(new Runnable() {
					@Override
					public void run() {
						AmstradMonitorMode mode = getProgram().getPreferredMonitorMode();
						try {
							releaseKeyboard();
							getAmstradPc().reboot(true, true);
							launchProgram();
							failed = false;
							closeModalWindow();
							close(); // restores monitor mode & settings
							if (mode != null) {
								getAmstradPc().setMonitorMode(mode);
							}
						} catch (AmstradProgramException exc) {
							System.err.println(exc);
							acquireKeyboard();
							failed = true;
						} finally {
							executeStartTime = 0L;
						}
					}
				}).start();
			}
		}

		protected abstract void launchProgram() throws AmstradProgramException;

		@Override
		public boolean isEnabled() {
			return !failed;
		}

		@Override
		public String getLabel() {
			String label = super.getLabel();
			if (executeStartTime > 0L) {
				int t = (int) ((System.currentTimeMillis() - executeStartTime) / 100L);
				label += ' ';
				label += (char) (192 + t % 4);
			} else if (failed) {
				label += ' ';
				label += (char) 225;
			}
			return label;
		}

	}

	private class ProgramLoadMenuItem extends ProgramLaunchMenuItem {

		public ProgramLoadMenuItem(AmstradProgram program) {
			super(program, "Load");
		}

		@Override
		protected void launchProgram() throws AmstradProgramException {
			getAmstradPc().getBasicRuntime().loadProgram(getProgram());
			for (ProgramBrowserListener listener : getBrowserListeners()) {
				listener.programLoadedFromBrowser(ProgramBrowserDisplaySource.this, getProgram());
			}
		}

	}

	private class ProgramRunMenuItem extends ProgramLaunchMenuItem {

		public ProgramRunMenuItem(AmstradProgram program) {
			super(program, "Run");
		}

		@Override
		protected void launchProgram() throws AmstradProgramException {
			getAmstradPc().getBasicRuntime().loadProgram(getProgram()).run();
			for (ProgramBrowserListener listener : getBrowserListeners()) {
				listener.programRunFromBrowser(ProgramBrowserDisplaySource.this, getProgram());
			}
		}

	}

	private class ProgramInfoMenuItem extends ProgramMenuItem {

		public ProgramInfoMenuItem(AmstradProgram program) {
			super(program, "Info");
		}

		@Override
		public void execute() {
			if (isEnabled()) {
				setProgramInfoSheet(createProgramInfoSheet(getProgram()));
				closeModalWindow();
				setCurrentWindow(Window.PROGRAM_INFO_MODAL);
			}
		}

		@Override
		public boolean isEnabled() {
			return getProgram().hasDescriptiveInfo();
		}

	}

	private class ProgramImagesMenuItem extends ProgramMenuItem {

		public ProgramImagesMenuItem(AmstradProgram program) {
			super(program, "Images");
		}

		@Override
		public void execute() {
			if (isEnabled()) {
				setProgramImageGallery(new ProgramImageGallery(getProgram()));
				closeModalWindow();
				setCurrentWindow(Window.PROGRAM_IMAGE_GALLERY_MODAL);
			}
		}

		@Override
		public boolean isEnabled() {
			return !getProgram().getImages().isEmpty();
		}

	}

	private class ProgramCloseMenuItem extends ProgramMenuItem {

		public ProgramCloseMenuItem(AmstradProgram program) {
			super(program, "Close");
		}

		@Override
		public void execute() {
			closeModalWindow();
		}

	}

	private class ProgramInfoSheet extends ItemList {

		private AmstradProgram program;

		private List<ProgramInfoLine> lineItems;

		public ProgramInfoSheet(AmstradProgram program, int maxItemsShowing) {
			super(maxItemsShowing);
			this.program = program;
			this.lineItems = new Vector<ProgramInfoLine>();
		}

		public void add(ProgramInfoLine lineItem) {
			getLineItems().add(lineItem);
		}

		@Override
		public int size() {
			return getLineItems().size();
		}

		public ProgramInfoLine getLineItem(int index) {
			return getLineItems().get(index);
		}

		public AmstradProgram getProgram() {
			return program;
		}

		public List<ProgramInfoLine> getLineItems() {
			return lineItems;
		}

	}

	private static class ProgramInfoLine {

		private List<ProgramInfoTextSpan> textSpans;

		public ProgramInfoLine() {
			this.textSpans = new Vector<ProgramInfoTextSpan>();
		}

		public ProgramInfoLine(ProgramInfoTextSpan... textSpans) {
			this();
			for (int i = 0; i < textSpans.length; i++) {
				add(textSpans[i]);
			}
		}

		public void add(ProgramInfoTextSpan textSpan) {
			getTextSpans().add(textSpan);
		}

		public List<ProgramInfoTextSpan> getTextSpans() {
			return textSpans;
		}

	}

	private static class ProgramInfoTextSpan {

		private String text;

		private int paperColorIndex;

		private int penColorIndex;

		public ProgramInfoTextSpan(String text, int paperColorIndex, int penColorIndex) {
			this.text = text;
			this.paperColorIndex = paperColorIndex;
			this.penColorIndex = penColorIndex;
		}

		public String getText() {
			return text;
		}

		public int getPaperColorIndex() {
			return paperColorIndex;
		}

		public int getPenColorIndex() {
			return penColorIndex;
		}

	}

	private class ProgramImageGallery extends ItemList {

		private AmstradProgram program;

		public ProgramImageGallery(AmstradProgram program) {
			super(1);
			this.program = program;
		}

		public ProgramImage getCurrentImage() {
			return getImage(getIndexOfSelectedItem());
		}

		public ProgramImage getImage(int index) {
			return getProgram().getImages().get(index);
		}

		@Override
		public int size() {
			return getProgram().getImages().size();
		}

		public boolean hasCaptions() {
			for (int i = 0; i < size(); i++) {
				if (!StringUtils.isEmpty(getImage(i).getCaption()))
					return true;
			}
			return false;
		}

		public AmstradProgram getProgram() {
			return program;
		}

	}

}