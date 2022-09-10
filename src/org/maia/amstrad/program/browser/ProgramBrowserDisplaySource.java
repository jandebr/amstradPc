package org.maia.amstrad.program.browser;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.display.AmstradWindowDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
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

	private AmstradProgram lastLaunchedProgram;

	private long itemListCursorBlinkOffsetTime;

	private static long itemListCursorBlinkTimeInterval = 500L;

	private static int COLOR_BORDER = 1;

	private static int COLOR_PAPER = 1;

	private static int COLOR_MODAL_BACKGROUND = 0;

	private static int LABEL_WIDTH = 18;

	public ProgramBrowserDisplaySource(AmstradPc amstradPc, AmstradProgramRepository programRepository) {
		super(amstradPc, "Program  Browser");
		this.programRepository = programRepository;
		this.stackedFolderItemList = new StackedFolderItemList(20);
		this.currentWindow = Window.MAIN;
	}

	public ProgramBrowserDisplaySource createStandaloneInfoDisplaySource(AmstradProgram program) {
		ProgramBrowserDisplaySource infoDS = new ProgramBrowserDisplaySource(getAmstradPc(), getProgramRepository());
		infoDS.setWindowTitle(program.getProgramName());
		infoDS.setCurrentWindow(Window.PROGRAM_INFO_STANDALONE);
		infoDS.setProgramInfoSheet(createProgramInfoSheet(program));
		return infoDS;
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
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
		canvas.move(8, 399).drawChr(255);
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
		} else {
			int ty = ty0;
			int i = itemList.getIndexOfFirstItemShowing();
			while (i < itemList.size() && ty < ty0 + itemList.getMaxItemsShowing()) {
				Node item = itemList.getItem(i);
				String label = StringUtils.fitWidth(item.getName(), LABEL_WIDTH);
				if (itemList.getIndexOfSelectedItem() == i) {
					if (hasFocus) {
						canvas.pen(11).locate(1, 25).print(item.getName());
						if (!isModalWindowOpen() && isItemListCursorBlinkOn()) {
							canvas.pen(24).locate(tx0 - 1, ty).printChr(133);
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

	private void renderItemListTopExtentHint(ItemList itemList, int tx0, int ty0, AmstradDisplayCanvas canvas) {
		if (itemList.getIndexOfFirstItemShowing() > 0) {
			canvas.pen(13).locate(tx0, ty0 - 1);
			for (int i = 0; i < LABEL_WIDTH; i += 2) {
				canvas.printChr(196).printChr(' ');
			}
		}
	}

	private void renderItemListBottomExtentHint(ItemList itemList, int tx0, int ty0, AmstradDisplayCanvas canvas) {
		if (itemList.getIndexOfLastItemShowing() < itemList.size() - 1) {
			canvas.pen(13).locate(tx0, ty0 + itemList.getMaxItemsShowing());
			for (int i = 0; i < LABEL_WIDTH; i += 2) {
				canvas.printChr(198).printChr(' ');
			}
		}
	}

	private void renderStackLeftExtentHint(StackedFolderItemList stack, int tx0, int ty0, AmstradDisplayCanvas canvas) {
		if (stack.size() > 2) {
			canvas.pen(13);
			for (int i = 0; i < stack.getMaxItemsShowing(); i += 3) {
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
					canvas.pen(24).locate(tx0 - 1, ty).printChr(133);
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
		renderModalWindow(4, 5, 37, 22, infoSheet.getProgram().getProgramName(), COLOR_MODAL_BACKGROUND, canvas);
		int tx0 = 6, ty0 = 8, ty = ty0;
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
			canvas.pen(13).move(560, 287).drawChr(196);
		}
		// bottom extent hint
		if (infoSheet.getIndexOfLastItemShowing() < infoSheet.size() - 1) {
			canvas.pen(13).move(560, 79).drawChr(198);
		}
		canvas.paper(COLOR_PAPER);
	}

	public void home() {
		getStackedFolderItemList().reset();
	}

	@Override
	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		super.mouseClickedOnCanvas(canvas, canvasPosition);
		if (isFocusOnHomeButton(canvas)) {
			home();
		}
	}

	@Override
	protected void closeModalWindow() {
		super.closeModalWindow();
		if (isStandaloneInfo()) {
			close();
		} else if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow())) {
			setCurrentWindow(Window.PROGRAM_MENU_MODAL);
		} else {
			setCurrentWindow(Window.MAIN);
		}
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		super.keyboardKeyPressed(e);
		resetItemListCursorBlinkOffsetTime();
		if (!isModalWindowOpen()) {
			handleKeyboardKeyInMainWindow(e);
		} else if (Window.PROGRAM_MENU_MODAL.equals(getCurrentWindow())) {
			handleKeyboardKeyInProgramMenu(e);
		} else if (Window.PROGRAM_INFO_MODAL.equals(getCurrentWindow()) || isStandaloneInfo()) {
			handleKeyboardKeyInProgramInfoSheet(e);
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

	private ProgramInfoSheet createProgramInfoSheet(AmstradProgram program) {
		ProgramInfoSheet sheet = new ProgramInfoSheet(program, 14);
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
				ProgramInfoLine line = new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.spaces(maxWidth - 6),
						bg, bg), new ProgramInfoTextSpan("\u00FE ", 0, 25));
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
			sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(String.valueOf(program.getProductionYear()), bg, 26)));
		}
		if (!StringUtils.isEmpty(program.getNameOfTape()) || program.getBlocksOnTape() > 0) {
			if (!sheet.isEmpty())
				sheet.add(new ProgramInfoLine());
			String tape = !StringUtils.isEmpty(program.getNameOfTape()) ? program.getNameOfTape() : "?";
			String blocks = program.getBlocksOnTape() > 0 ? String.valueOf(program.getBlocksOnTape()) : "?";
			sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.fitWidth("Tape", 20) + " Blocks", bg, 25)));
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
			sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(
					"\u008F\u008F\u00D4 User controls \u00D5\u008F\u008F", bg, 7)));
			for (UserControl uc : program.getUserControls()) {
				sheet.add(new ProgramInfoLine());
				if (uc.getHeading() != null) {
					List<String> hlines = StringUtils.splitOnNewlinesAndWrap(uc.getHeading(), maxWidth - 6);
					for (int i = 0; i < hlines.size(); i++) {
						String text = hlines.get(i);
						if (hlines.size() == 1) {
							sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan("\u00CF\u00DC " + text
									+ " \u00DD\u00CF", bg, 7)));
						} else if (i == 0) {
							sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan("\u00CF\u00DC "
									+ StringUtils.fitWidth(text, maxWidth - 6) + " \u00DD\u00CF", bg, 7)));
						} else {
							sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan("\u00CF  "
									+ StringUtils.fitWidth(text, maxWidth - 6) + "  \u00CF", bg, 7)));
						}
					}
					sheet.add(new ProgramInfoLine());
				}
				sheet.add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.fitWidth(uc.getKey(), maxWidth), bg,
						16)));
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

	public AmstradProgramRepository getProgramRepository() {
		return programRepository;
	}

	private StackedFolderItemList getStackedFolderItemList() {
		return stackedFolderItemList;
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

	public boolean isStandaloneInfo() {
		return Window.PROGRAM_INFO_STANDALONE.equals(getCurrentWindow());
	}

	public AmstradProgram getInfoSheetProgram() {
		return getProgramInfoSheet() != null ? getProgramInfoSheet().getProgram() : null;
	}

	public AmstradProgram getLastLaunchedProgram() {
		return lastLaunchedProgram;
	}

	private void setLastLaunchedProgram(AmstradProgram program) {
		this.lastLaunchedProgram = program;
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

		PROGRAM_INFO_STANDALONE;

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
							setLastLaunchedProgram(getProgram());
							failed = false;
							closeModalWindow();
							close(); // restores monitor settings
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
			getProgram().loadInto(getAmstradPc());
		}

	}

	private class ProgramRunMenuItem extends ProgramLaunchMenuItem {

		public ProgramRunMenuItem(AmstradProgram program) {
			super(program, "Run");
		}

		@Override
		protected void launchProgram() throws AmstradProgramException {
			getProgram().runWith(getAmstradPc());
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

}