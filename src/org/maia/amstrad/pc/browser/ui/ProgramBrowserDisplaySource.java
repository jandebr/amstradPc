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

	private ProgramMenu programMenu;

	private Rectangle modalCloseButtonBounds;

	private boolean mouseOverButton;

	private static int COLOR_BORDER = 1;

	private static int COLOR_PAPER = 1;

	private static int COLOR_MODAL_BACKGROUND = 0;

	private static int LABEL_WIDTH = 18;

	public ProgramBrowserDisplaySource(AmstradPc amstradPc, AmstradProgramRepository programRepository) {
		super(amstradPc);
		this.programRepository = programRepository;
		this.stackedFolderItemList = new StackedFolderItemList(programRepository, 20);
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		getAmstradPc().setMonitorMode(AmstradMonitorMode.COLOR);
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
		if (isInProgramMenu()) {
			renderProgramMenu(getProgramMenu(), canvas);
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
		if (!isInProgramMenu() && isMouseOverHomeButton(canvas)) {
			setMouseOverButton(true);
			canvas.paper(14).pen(24);
		} else {
			canvas.paper(5).pen(26);
		}
		canvas.locate(1, 1).print("  ").paper(COLOR_PAPER);
		canvas.move(8, 399).drawChr(255);
	}

	private void renderCloseButton(AmstradDisplayCanvas canvas) {
		if (!isInProgramMenu() && isMouseOverCloseButton(canvas)) {
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
						canvas.pen(24).locate(tx0 - 1, ty).printChr(133);
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
		renderModalWindow(canvas.getTextAreaBoundsOnCanvas(8, 8, 33, 18), canvas);
		canvas.pen(26).locate(9, 9).print(fitLabel(menu.getProgram().getProgramName(), 24));
		canvas.pen(22);
		int tx0 = 9, ty0 = 11, ty = ty0;
		int i = menu.getIndexOfFirstItemShowing();
		while (i < menu.size() && ty < ty0 + menu.getMaxItemsShowing()) {
			ProgramMenuItem item = menu.getItem(i);
			String label = fitLabel(item.getLabel(), LABEL_WIDTH);
			boolean highlighted = menu.getIndexOfSelectedItem() == i;
			if (highlighted) {
				canvas.pen(24).locate(tx0 - 1, ty).printChr(133);
				canvas.paper(1);
			}
			canvas.pen(21).locate(tx0, ty).print(label);
			canvas.paper(COLOR_MODAL_BACKGROUND);
			ty++;
			i++;
		}
		canvas.paper(COLOR_PAPER);
	}

	private void renderModalWindow(Rectangle bounds, AmstradDisplayCanvas canvas) {
		canvas.paper(COLOR_MODAL_BACKGROUND).clearRect(bounds);
		// close button
		Rectangle closeBounds = new Rectangle(bounds.x + bounds.width - 32, bounds.y, 32, 16);
		setModalCloseButtonBounds(closeBounds);
		if (isMouseOverModalCloseButton()) {
			setMouseOverButton(true);
			canvas.paper(6).pen(24);
		} else {
			canvas.paper(3).pen(26);
		}
		canvas.clearRect(closeBounds);
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
		if (!isInProgramMenu()) {
			if (isMouseOverHomeButton(canvas)) {
				home();
			} else if (isMouseOverCloseButton(canvas)) {
				close();
			}
		} else {
			if (isMouseOverModalCloseButton()) {
				closeProgramMenu();
			}
		}
	}

	public void home() {
		getStackedFolderItemList().reset();
	}

	public void closeProgramMenu() {
		setProgramMenu(null);
		setModalCloseButtonBounds(null);
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if (!isInProgramMenu()) {
			handleKeyInBrowser(e);
		} else {
			handleKeyInProgramMenu(e);
		}
	}

	private void handleKeyInBrowser(KeyEvent e) {
		StackedFolderItemList stack = getStackedFolderItemList();
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_DOWN) {
			stack.browseDown();
		} else if (keyCode == KeyEvent.VK_UP) {
			stack.browseUp();
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			stack.browseRight();
		} else if (keyCode == KeyEvent.VK_LEFT) {
			stack.browseLeft();
		} else if (keyCode == KeyEvent.VK_ENTER) {
			setProgramMenu(stack.createProgramMenu(7));
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			close();
		}
	}

	private void handleKeyInProgramMenu(KeyEvent e) {
		ProgramMenu menu = getProgramMenu();
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_DOWN) {
			menu.browseOneItemDown();
		} else if (keyCode == KeyEvent.VK_UP) {
			menu.browseOneItemUp();
		} else if (keyCode == KeyEvent.VK_ENTER) {
			menu.getSelectedItem().execute();
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			closeProgramMenu();
		}
	}

	private boolean isInProgramMenu() {
		return getProgramMenu() != null;
	}

	private boolean isMouseOverHomeButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(1, 1, 2, 1));
	}

	private boolean isMouseOverCloseButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(39, 1, 40, 1));
	}

	private boolean isMouseOverModalCloseButton() {
		Rectangle bounds = getModalCloseButtonBounds();
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

	private Rectangle getModalCloseButtonBounds() {
		return modalCloseButtonBounds;
	}

	private void setModalCloseButtonBounds(Rectangle bounds) {
		this.modalCloseButtonBounds = bounds;
	}

	public AmstradProgramRepository getProgramRepository() {
		return programRepository;
	}

	private StackedFolderItemList getStackedFolderItemList() {
		return stackedFolderItemList;
	}

	private ProgramMenu getProgramMenu() {
		return programMenu;
	}

	private void setProgramMenu(ProgramMenu programMenu) {
		this.programMenu = programMenu;
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
			if (size() > 0) {
				FolderItemList itemList = getStack().get(0);
				itemList.refresh();
				getStack().clear();
				getStack().push(itemList);
			}
		}

		public void browseDown() {
			if (size() > 0) {
				getStack().peek().browseOneItemDown();
			}
		}

		public void browseUp() {
			if (size() > 0) {
				getStack().peek().browseOneItemUp();
			}
		}

		public void browseRight() {
			if (size() > 0) {
				Node selectedItem = getStack().peek().getSelectedItem();
				if (selectedItem != null && selectedItem.isFolder()) {
					push(new FolderItemList(selectedItem.asFolder(), getMaxItemsShowing()));
				}
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
			initMenuItems();
		}

		private void initMenuItems() {
			addMenuItem(new ProgramLoadMenuItem(getProgram()));
			addMenuItem(new ProgramRunMenuItem(getProgram()));
			addMenuItem(new ProgramCloseMenuItem(getProgram()));
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

	private class ProgramLoadMenuItem extends ProgramMenuItem {

		public ProgramLoadMenuItem(AmstradProgram program) {
			super(program, "Load");
		}

		@Override
		protected void execute() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					close(); // TODO close only after successful load
					try {
						getProgram().loadInto(getAmstradPc());
					} catch (AmstradProgramException exc) {
						System.err.println(exc);
					}
				}
			}).start();
		}

	}

	private class ProgramRunMenuItem extends ProgramMenuItem {

		public ProgramRunMenuItem(AmstradProgram program) {
			super(program, "Run");
		}

		@Override
		protected void execute() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					close(); // TODO close only after successful run
					try {
						getProgram().runWith(getAmstradPc());
					} catch (AmstradProgramException exc) {
						System.err.println(exc);
					}
				}
			}).start();
		}

	}

	private class ProgramCloseMenuItem extends ProgramMenuItem {

		public ProgramCloseMenuItem(AmstradProgram program) {
			super(program, "Close");
		}

		@Override
		protected void execute() {
			closeProgramMenu();
		}

	}

}