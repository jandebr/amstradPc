package org.maia.amstrad.pc.browser.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.browser.repo.AmstradProgram;
import org.maia.amstrad.pc.browser.repo.AmstradProgramException;
import org.maia.amstrad.pc.browser.repo.AmstradProgramRepository;
import org.maia.amstrad.pc.browser.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.pc.browser.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.pc.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.display.AmstradEmulatedDisplaySource;

public class ProgramBrowserDisplaySource extends AmstradEmulatedDisplaySource {

	private AmstradProgramRepository programRepository;

	private StackedFolderItemList stackedFolderItemList;

	private Window currentWindow;

	private ProgramMenu programMenu;

	private ProgramInfoSheet programInfoSheet;

	private Rectangle modalWindowCloseButtonBounds;

	private boolean mouseOverButton;

	private long itemListCursorBlinkOffsetTime;

	private static long itemListCursorBlinkTimeInterval = 500L;

	private static int COLOR_BORDER = 1;

	private static int COLOR_PAPER = 1;

	private static int COLOR_MODAL_BACKGROUND = 0;

	private static int LABEL_WIDTH = 18;

	public ProgramBrowserDisplaySource(AmstradPc amstradPc, AmstradProgramRepository programRepository) {
		super(amstradPc);
		this.programRepository = programRepository;
		this.stackedFolderItemList = new StackedFolderItemList(programRepository, 20);
		this.currentWindow = Window.MAIN;
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		resetItemListCursorBlinkOffsetTime();
		getAmstradPc().setMonitorMode(AmstradMonitorMode.COLOR);
		getAmstradPc().setMonitorBilinearEffect(false);
		getAmstradPc().setMonitorScanLinesEffect(false);
		canvas.border(COLOR_BORDER).paper(COLOR_PAPER);
		canvas.symbol(255, 24, 60, 126, 255, 126, 110, 110, 124); // home
	}

	@Override
	protected void renderContent(AmstradDisplayCanvas canvas) {
		setMouseOverButton(false);
		renderTitle(canvas);
		renderHomeButton(canvas);
		renderCloseButton(canvas);
		renderStack(getStackedFolderItemList(), canvas);
		if (Window.PROGRAM_MENU.equals(getCurrentWindow())) {
			renderProgramMenu(getProgramMenu(), canvas);
		} else if (Window.PROGRAM_INFO.equals(getCurrentWindow())) {
			renderProgramInfoSheet(getProgramInfoSheet(), canvas);
		}
		updateCursor();
	}

	private void renderTitle(AmstradDisplayCanvas canvas) {
		canvas.pen(23).locate(12, 1).print("Program  Browser");
		canvas.locate(1, 2);
		for (int i = 0; i < 40; i++)
			canvas.printChr(216);
	}

	private void renderHomeButton(AmstradDisplayCanvas canvas) {
		if (!isModalWindowOpen() && isMouseOverHomeButton(canvas)) {
			setMouseOverButton(true);
			canvas.paper(14).pen(24);
		} else {
			canvas.paper(5).pen(26);
		}
		canvas.locate(1, 1).print("  ").paper(COLOR_PAPER);
		canvas.move(8, 399).drawChr(255);
	}

	private void renderCloseButton(AmstradDisplayCanvas canvas) {
		if (!isModalWindowOpen() && isMouseOverMainCloseButton(canvas)) {
			setMouseOverButton(true);
			canvas.paper(6).pen(24);
		} else {
			canvas.paper(3).pen(26);
		}
		canvas.locate(39, 1).print("  ").paper(COLOR_PAPER);
		canvas.move(616, 399).drawChr('x');
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
				String label = fitLabel(item.getName(), LABEL_WIDTH);
				boolean highlighted = itemList.getIndexOfSelectedItem() == i;
				if (highlighted) {
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

	private String fitLabel(String label, int width) {
		int n = label.length();
		if (n == width) {
			return label;
		} else if (n > width) {
			return label.substring(0, width - 2) + "..";
		} else {
			StringBuilder sb = new StringBuilder(width);
			sb.append(label);
			for (int i = 0; i < width - n; i++)
				sb.append(' ');
			return sb.toString();
		}
	}

	private void renderProgramMenu(ProgramMenu menu, AmstradDisplayCanvas canvas) {
		renderModalWindow(8, 8, 33, 18, menu.getProgram().getProgramName(), canvas);
		int tx0 = 10, ty0 = 12, ty = ty0;
		int i = menu.getIndexOfFirstItemShowing();
		while (i < menu.size() && ty < ty0 + menu.getMaxItemsShowing()) {
			ProgramMenuItem item = menu.getItem(i);
			String label = fitLabel(item.getLabel(), LABEL_WIDTH);
			boolean highlighted = menu.getIndexOfSelectedItem() == i;
			if (highlighted) {
				if (isItemListCursorBlinkOn()) {
					canvas.pen(24).locate(tx0 - 1, ty).printChr(133);
				}
				canvas.paper(9);
			}
			canvas.pen(22).locate(tx0, ty).print(label);
			canvas.paper(COLOR_MODAL_BACKGROUND);
			ty++;
			i++;
		}
		canvas.paper(COLOR_PAPER);
	}

	private void renderProgramInfoSheet(ProgramInfoSheet infoSheet, AmstradDisplayCanvas canvas) {
		renderModalWindow(4, 5, 37, 22, infoSheet.getProgram().getProgramName(), canvas);
		int tx0 = 5, ty0 = 9, ty = ty0;
		int i = infoSheet.getIndexOfFirstItemShowing();
		while (i < infoSheet.size() && ty < ty0 + infoSheet.getMaxItemsShowing()) {
			ProgramInfoLine line = infoSheet.getLineItem(i);
			String label = fitLabel(line.getText(), 32);
			boolean highlighted = infoSheet.getIndexOfSelectedItem() == i;
			if (highlighted) {
				canvas.paper(1);
			}
			canvas.pen(25).locate(tx0, ty).print(label);
			canvas.paper(COLOR_MODAL_BACKGROUND);
			ty++;
			i++;
		}
		// top extent hint
		if (infoSheet.getIndexOfFirstItemShowing() > 0) {
			canvas.pen(13).move(560, 271).drawChr(196);
		}
		// bottom extent hint
		if (infoSheet.getIndexOfLastItemShowing() < infoSheet.size() - 1) {
			canvas.pen(13).move(560, 79).drawChr(198);
		}
		canvas.paper(COLOR_PAPER);
	}

	private void renderModalWindow(int tx1, int ty1, int tx2, int ty2, String windowTitle, AmstradDisplayCanvas canvas) {
		canvas.paper(COLOR_MODAL_BACKGROUND);
		canvas.clearRect(canvas.getTextAreaBoundsOnCanvas(tx1, ty1, tx2, ty2));
		renderModalWindowTitle(tx1, ty1, tx2, ty2, windowTitle, canvas);
		renderModalWindowBorder(tx1, ty1, tx2, ty2, canvas);
		renderModalWindowCloseButton(tx1, ty1, tx2, ty2, canvas);
	}

	private void renderModalWindowTitle(int tx1, int ty1, int tx2, int ty2, String windowTitle,
			AmstradDisplayCanvas canvas) {
		int maxTitleWidth = tx2 - tx1 - 1;
		canvas.pen(23).locate(tx1 + 1, ty1 + 1).print(fitLabel(windowTitle, maxTitleWidth));
		canvas.locate(tx1 + 1, ty1 + 2);
		for (int i = 0; i < maxTitleWidth; i++)
			canvas.printChr(216);
	}

	private void renderModalWindowBorder(int tx1, int ty1, int tx2, int ty2, AmstradDisplayCanvas canvas) {
		canvas.pen(14);
		for (int i = tx1 + 1; i <= tx2 - 1; i++) {
			canvas.locate(i, ty1).printChr(154).locate(i, ty2).printChr(154);
		}
		for (int i = ty1 + 1; i <= ty2 - 1; i++) {
			canvas.locate(tx1, i).printChr(149).locate(tx2, i).printChr(149);
		}
		canvas.locate(tx1, ty1).printChr(150);
		canvas.locate(tx2, ty1).printChr(156);
		canvas.locate(tx1, ty2).printChr(147);
		canvas.locate(tx2, ty2).printChr(153);
	}

	private void renderModalWindowCloseButton(int tx1, int ty1, int tx2, int ty2, AmstradDisplayCanvas canvas) {
		Rectangle closeBounds = canvas.getTextAreaBoundsOnCanvas(tx2 - 1, ty1, tx2, ty1);
		setModalWindowCloseButtonBounds(closeBounds);
		if (isMouseOverModalWindowCloseButton()) {
			setMouseOverButton(true);
			canvas.paper(6).pen(24);
		} else {
			canvas.paper(3).pen(26);
		}
		canvas.locate(tx2 - 1, ty1).print("  ");
		canvas.move(closeBounds.x + 8, closeBounds.y).drawChr('x');
		canvas.paper(COLOR_MODAL_BACKGROUND);
	}

	private void updateCursor() {
		if (isMouseOverButton()) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			resetCursor();
		}
	}

	@Override
	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		super.mouseClickedOnCanvas(canvas, canvasPosition);
		if (!isModalWindowOpen()) {
			if (isMouseOverHomeButton(canvas)) {
				home();
			} else if (isMouseOverMainCloseButton(canvas)) {
				close();
			}
		} else {
			if (isMouseOverModalWindowCloseButton()) {
				closeModalWindow();
			}
		}
	}

	public void home() {
		getStackedFolderItemList().reset();
	}

	public void closeModalWindow() {
		setModalWindowCloseButtonBounds(null);
		if (Window.PROGRAM_INFO.equals(getCurrentWindow())) {
			setCurrentWindow(Window.PROGRAM_MENU);
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
		} else if (Window.PROGRAM_MENU.equals(getCurrentWindow())) {
			handleKeyboardKeyInProgramMenu(e);
		} else if (Window.PROGRAM_INFO.equals(getCurrentWindow())) {
			handleKeyboardKeyInProgramInfoSheet(e);
		}
	}

	private void handleKeyboardKeyInMainWindow(KeyEvent e) {
		StackedFolderItemList stack = getStackedFolderItemList();
		handleKeyboardKeyInItemList(e, stack.peek());
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_RIGHT) {
			stack.browseRight();
		} else if (keyCode == KeyEvent.VK_LEFT) {
			stack.browseLeft();
		} else if (keyCode == KeyEvent.VK_ENTER) {
			ProgramMenu menu = stack.createProgramMenu(6);
			if (menu != null) {
				setProgramMenu(menu);
				setCurrentWindow(Window.PROGRAM_MENU);
			}
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			close();
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
		return new ProgramInfoSheet(program, 13);
	}

	private boolean isModalWindowOpen() {
		return !Window.MAIN.equals(getCurrentWindow());
	}

	private boolean isMouseOverHomeButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(1, 1, 2, 1));
	}

	private boolean isMouseOverMainCloseButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(39, 1, 40, 1));
	}

	private boolean isMouseOverModalWindowCloseButton() {
		Rectangle bounds = getModalWindowCloseButtonBounds();
		if (bounds != null) {
			return isMouseInCanvasBounds(bounds);
		} else {
			return false;
		}
	}

	private boolean isMouseOverButton() {
		return mouseOverButton;
	}

	private void setMouseOverButton(boolean mouseOverButton) {
		this.mouseOverButton = mouseOverButton;
	}

	private Rectangle getModalWindowCloseButtonBounds() {
		return modalWindowCloseButtonBounds;
	}

	private void setModalWindowCloseButtonBounds(Rectangle bounds) {
		this.modalWindowCloseButtonBounds = bounds;
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

	private boolean isItemListCursorBlinkOn() {
		long t = (System.currentTimeMillis() - itemListCursorBlinkOffsetTime) / itemListCursorBlinkTimeInterval;
		return t % 2 == 0;
	}

	private void resetItemListCursorBlinkOffsetTime() {
		this.itemListCursorBlinkOffsetTime = System.currentTimeMillis();
	}

	private static enum Window {

		MAIN,

		PROGRAM_MENU,

		PROGRAM_INFO;

	}

	private class StackedFolderItemList {

		private Stack<FolderItemList> stack;

		private int maxItemsShowing;

		public StackedFolderItemList(AmstradProgramRepository repository, int maxItemsShowing) {
			this.stack = new Stack<FolderItemList>();
			this.maxItemsShowing = maxItemsShowing;
			push(new FolderItemList(repository.getRootNode(), maxItemsShowing));
		}

		public void reset() {
			FolderItemList itemList = getStack().get(0);
			itemList.refresh();
			getStack().clear();
			getStack().push(itemList);
		}

		public void browseRight() {
			Node selectedItem = getStack().peek().getSelectedItem();
			if (selectedItem != null && selectedItem.isFolder()) {
				push(new FolderItemList(selectedItem.asFolder(), getMaxItemsShowing()));
			}
		}

		public void browseLeft() {
			if (size() > 1) {
				pop();
			}
		}

		public ProgramMenu createProgramMenu(int maxItemsShowing) {
			ProgramMenu menu = null;
			if (canExecute()) {
				AmstradProgram program = getSelectedItem().asProgram().getProgram();
				menu = new ProgramMenu(program, maxItemsShowing);
			}
			return menu;
		}

		public boolean canExecute() {
			Node selectedItem = getSelectedItem();
			if (selectedItem != null) {
				return selectedItem.isProgram();
			} else {
				return false;
			}
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

		public void refresh() {
			setIndexOfFirstItemShowing(0);
			setIndexOfSelectedItem(0);
		}

		public void browseHome() {
			refresh();
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
		public void refresh() {
			super.refresh();
			getFolderNode().refresh();
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
			populateMenuItems();
		}

		private void populateMenuItems() {
			AmstradProgram program = getProgram();
			addMenuItem(new ProgramRunMenuItem(program));
			addMenuItem(new ProgramLoadMenuItem(program));
			if (program.hasInfo()) {
				addMenuItem(new ProgramInfoMenuItem(program));
			}
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

		protected abstract void execute();

		public AmstradProgram getProgram() {
			return program;
		}

		public String getLabel() {
			return label;
		}

	}

	private abstract class ProgramLaunchMenuItem extends ProgramMenuItem {

		private long executeStartTime;

		protected ProgramLaunchMenuItem(AmstradProgram program, String label) {
			super(program, label);
		}

		@Override
		protected void execute() {
			executeStartTime = System.currentTimeMillis();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						releaseKeyboard();
						getAmstradPc().reboot(true, true);
						launchProgram();
						closeModalWindow();
						close(); // restores monitor settings
						getAmstradPc().setMonitorMode(getProgram().getPreferredMonitorMode());
					} catch (AmstradProgramException exc) {
						System.err.println(exc);
					} finally {
						executeStartTime = 0L;
					}
				}
			}).start();
		}

		protected abstract void launchProgram() throws AmstradProgramException;

		@Override
		public String getLabel() {
			String label = super.getLabel();
			if (executeStartTime > 0L) {
				int t = (int) ((System.currentTimeMillis() - executeStartTime) / 100L);
				label += ' ';
				label += (char) (192 + t % 4);
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
		protected void execute() {
			setProgramInfoSheet(createProgramInfoSheet(getProgram()));
			closeModalWindow();
			setCurrentWindow(Window.PROGRAM_INFO);
		}

	}

	private class ProgramCloseMenuItem extends ProgramMenuItem {

		public ProgramCloseMenuItem(AmstradProgram program) {
			super(program, "Close");
		}

		@Override
		protected void execute() {
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
			populateLineItems();
		}

		private void populateLineItems() {
			for (int i = 1; i <= 500; i++) {
				addLineItem(new ProgramInfoLine("Hello " + i));
			}
		}

		private void addLineItem(ProgramInfoLine lineItem) {
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

	private class ProgramInfoLine {

		private String text;

		public ProgramInfoLine(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

	}

}