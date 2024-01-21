package org.maia.amstrad.gui.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.maia.amstrad.gui.AmstradSymbolRenderer;
import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard.Key;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboard.KeyGroup;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboardGridLayout;
import org.maia.amstrad.pc.keyboard.virtual.AmstradVirtualKeyboardLayout;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class VirtualKeyboardDisplayOverlay extends AbstractDisplayOverlay {

	private AmstradSymbolRenderer symbolRenderer;

	private Sizes smallSizes;

	private Sizes largeSizes;

	private Sizes currentSizes;

	private static Color BOARD_BACKGROUND = new Color(0, 0, 0, 235);

	private static Color[] KEY_GROUP_BACKGROUND = new Color[] { new Color(17, 29, 128), new Color(77, 23, 105),
			new Color(43, 117, 14), new Color(40, 40, 40), new Color(99, 87, 41), new Color(138, 33, 41),
			new Color(180, 180, 180) };

	private static Color KEY_PRESSED_BACKGROUND = Color.WHITE;

	private static Color KEY_PRESSED_LABEL_COLOR = Color.BLACK;

	private static Color KEY_BORDER_COLOR = Color.YELLOW;

	public VirtualKeyboardDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		super.init(displayComponent, graphicsContext);
		setSymbolRenderer(new AmstradSymbolRenderer(graphicsContext, null)); // inject Graphics2D later
		setSmallSizes(createSmallSizes());
		setLargeSizes(createLargeSizes());
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		AmstradVirtualKeyboard keyboard = getAmstracPc().getVirtualKeyboard();
		if (keyboard != null && keyboard.isActive() && !offscreenImage) {
			AmstradVirtualKeyboardLayout layout = keyboard.getLayout();
			if (layout instanceof AmstradVirtualKeyboardGridLayout) {
				updateCurrentSizes(displayBounds.height);
				renderVirtualKeyboard(keyboard, (AmstradVirtualKeyboardGridLayout) layout, displayView, displayBounds,
						monitorInsets);
			} else {
				ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.autotypeOverlayIcon
						: UIResources.autotypeSmallOverlayIcon;
				drawIconTopLeft(icon, displayView, displayBounds, monitorInsets);
			}
		}
	}

	protected void renderVirtualKeyboard(AmstradVirtualKeyboard keyboard, AmstradVirtualKeyboardGridLayout layout,
			AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets) {
		Sizes sizes = getCurrentSizes();
		int gridWidth = computeKeyRowWidth(keyboard, layout);
		int gridHeight = computeKeyColumnHeight(keyboard, layout);
		int totalWidth = sizes.getBoardMarginLeft() + gridWidth + sizes.getBoardMarginRight() + monitorInsets.right;
		int totalHeight = Math.max(gridHeight, displayBounds.height);
		int x0 = displayBounds.x + displayBounds.width - totalWidth;
		int y0 = displayBounds.y;
		Graphics2D g = displayView.createDisplayViewport(x0, y0, totalWidth, totalHeight);
		getSymbolRenderer().replaceGraphics2D(g);
		RenderContext context = new RenderContext(keyboard, layout, g);
		context.setSizes(sizes);
		context.setLocationOfUpperLeftKey(new Point(sizes.getBoardMarginLeft(), (totalHeight - gridHeight) / 2));
		renderBoard(context, totalWidth, totalHeight);
		renderAllKeys(context);
		g.dispose();
	}

	protected int computeKeyRowWidth(AmstradVirtualKeyboard keyboard, AmstradVirtualKeyboardGridLayout layout) {
		int cols = layout.getGridColumns(keyboard);
		return cols * getCurrentSizes().getKeyUnitWidth() + (cols - 1) * getCurrentSizes().getKeyHorizontalGap();
	}

	protected int computeKeyColumnHeight(AmstradVirtualKeyboard keyboard, AmstradVirtualKeyboardGridLayout layout) {
		int rows = layout.getGridRows(keyboard);
		return rows * getCurrentSizes().getKeyUnitHeight() + (rows - 1) * getCurrentSizes().getKeyVerticalGap();
	}

	protected void renderBoard(RenderContext context, int width, int height) {
		Graphics2D g = context.getGraphics2D();
		g.setColor(BOARD_BACKGROUND);
		g.fillRect(0, 0, width, height);
	}

	protected void renderAllKeys(RenderContext context) {
		Object antialiasBefore = context.getGraphics2D().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		context.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		getSymbolRenderer().scale(context.getSizes().getKeyLabelScale());
		List<KeyGroup> keyGroups = context.getKeyboard().getKeyGroups();
		for (int i = 0; i < keyGroups.size(); i++) {
			KeyGroup keyGroup = keyGroups.get(i);
			renderKeyGroup(keyGroup, i, context);
		}
		context.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasBefore);
	}

	protected void renderKeyGroup(KeyGroup keyGroup, int keyGroupIndex, RenderContext context) {
		for (Key key : keyGroup.getKeys()) {
			renderKey(key, keyGroup, keyGroupIndex, context);
		}
	}

	protected void renderKey(Key key, KeyGroup keyGroup, int keyGroupIndex, RenderContext context) {
		Graphics2D g = context.getGraphics2D();
		Rectangle bounds = context.getKeyboardLayout().getCellBoundsOfKey(key, context.getKeyboard());
		Sizes sizes = context.getSizes();
		Point origin = context.getLocationOfUpperLeftKey();
		int x0 = origin.x + bounds.x * (sizes.getKeyUnitWidth() + sizes.getKeyHorizontalGap());
		int y0 = origin.y + bounds.y * (sizes.getKeyUnitHeight() + sizes.getKeyVerticalGap());
		int keyWidth = bounds.width * sizes.getKeyUnitWidth() + (bounds.width - 1) * sizes.getKeyHorizontalGap();
		int keyHeight = bounds.height * sizes.getKeyUnitHeight() + (bounds.height - 1) * sizes.getKeyVerticalGap();
		int diam = sizes.getKeyCornerRoundDiameter();
		boolean cursor = key.equals(context.getKeyboard().getKeyAtCursor());
		boolean pressed = key.equals(context.getKeyboard().getKeyBeingPressed());
		if (cursor) {
			int t = sizes.getKeyBorderThickness();
			g.setColor(KEY_BORDER_COLOR);
			g.fillRoundRect(x0 - t, y0 - t, keyWidth + 2 * t, keyHeight + 2 * t, diam + t, diam + t);
		}
		g.setColor(getBackgroundColor(keyGroup, keyGroupIndex, pressed));
		g.fillRoundRect(x0, y0, keyWidth, keyHeight, diam, diam);
		renderKeyLabel(g, x0, y0, keyWidth, keyHeight, key, keyGroup, keyGroupIndex, pressed, context);
	}

	protected void renderKeyLabel(Graphics2D g, int x0, int y0, int keyWidth, int keyHeight, Key key, KeyGroup keyGroup,
			int keyGroupIndex, boolean pressed, RenderContext context) {
		int labelCharLength = 0;
		String labelStr = null;
		char labelChar = key.getKeyChar();
		if (labelChar != KeyEvent.CHAR_UNDEFINED) {
			labelCharLength = 1;
		} else {
			labelStr = getLabelStringForKey(key);
			labelCharLength = labelStr.length();
		}
		float scale = context.getSizes().getKeyLabelScale();
		int labelWidth = Math.round(scale * 8 * labelCharLength);
		int labelHeight = Math.round(scale * 8);
		int x = x0 + (keyWidth - labelWidth) / 2 + 1;
		int y = y0 + (keyHeight - labelHeight) / 2 + 1;
		getSymbolRenderer().color(getLabelColor(keyGroup, keyGroupIndex, pressed));
		if (labelStr == null) {
			getSymbolRenderer().drawChr(labelChar, x, y);
		} else {
			getSymbolRenderer().drawStr(labelStr, x, y);
		}
	}

	protected String getLabelStringForKey(Key key) {
		int code = key.getKeyCode();
		if (code == KeyEvent.VK_UP) {
			return String.valueOf((char) 240);
		} else if (code == KeyEvent.VK_DOWN) {
			return String.valueOf((char) 241);
		} else if (code == KeyEvent.VK_LEFT) {
			return String.valueOf((char) 242);
		} else if (code == KeyEvent.VK_RIGHT) {
			return String.valueOf((char) 243);
		} else if (code == KeyEvent.VK_BACK_SPACE) {
			return "DEL";
		} else if (code == KeyEvent.VK_ENTER) {
			return "ENTER";
		} else if (code == KeyEvent.VK_ESCAPE) {
			return "ESC";
		} else if (code == KeyEvent.VK_SPACE) {
			return "SPACE";
		} else {
			return "";
		}
	}

	protected Color getBackgroundColor(KeyGroup keyGroup, int keyGroupIndex, boolean pressed) {
		if (pressed) {
			return KEY_PRESSED_BACKGROUND;
		} else {
			return KEY_GROUP_BACKGROUND[keyGroupIndex % KEY_GROUP_BACKGROUND.length];
		}
	}

	protected Color getLabelColor(KeyGroup keyGroup, int keyGroupIndex, boolean pressed) {
		if (pressed) {
			return KEY_PRESSED_LABEL_COLOR;
		} else {
			float brightness = getBrightness(getBackgroundColor(keyGroup, keyGroupIndex, pressed));
			return brightness >= 0.6f ? Color.BLACK : Color.WHITE;
		}
	}

	protected Sizes createSmallSizes() {
		Sizes sizes = new Sizes(540); // suitable for 540p display
		sizes.setBoardMarginLeft(8);
		sizes.setBoardMarginRight(4);
		sizes.setKeyUnitWidth(15);
		sizes.setKeyUnitHeight(15);
		sizes.setKeyHorizontalGap(5);
		sizes.setKeyVerticalGap(5);
		sizes.setKeyCornerRoundDiameter(6);
		sizes.setKeyBorderThickness(2);
		sizes.setKeyLabelScale(1f);
		return sizes;
	}

	protected Sizes createLargeSizes() {
		Sizes sizes = new Sizes(1080); // suitable for 1080p display
		sizes.setBoardMarginLeft(16);
		sizes.setBoardMarginRight(4);
		sizes.setKeyUnitWidth(28);
		sizes.setKeyUnitHeight(28);
		sizes.setKeyHorizontalGap(9);
		sizes.setKeyVerticalGap(9);
		sizes.setKeyCornerRoundDiameter(8);
		sizes.setKeyBorderThickness(3);
		sizes.setKeyLabelScale(2f);
		return sizes;
	}

	private Sizes updateCurrentSizes(int availableHeight) {
		if (getCurrentSizes() == null || getCurrentSizes().getSuitableAvailableHeight() != availableHeight) {
			Sizes sizes = Sizes.interpolate(getSmallSizes(), getLargeSizes(), availableHeight);
			// Prefer integer font scale values, as they are free of artefacts
			float scale = sizes.getKeyLabelScale();
			float scaleInt = Math.round(scale);
			if (Math.abs(scale - scaleInt) <= 0.1)
				sizes.setKeyLabelScale(scaleInt);
			setCurrentSizes(sizes);
		}
		return getCurrentSizes();
	}

	protected AmstradSymbolRenderer getSymbolRenderer() {
		return symbolRenderer;
	}

	private void setSymbolRenderer(AmstradSymbolRenderer symbolRenderer) {
		this.symbolRenderer = symbolRenderer;
	}

	private Sizes getSmallSizes() {
		return smallSizes;
	}

	private void setSmallSizes(Sizes sizes) {
		this.smallSizes = sizes;
	}

	private Sizes getLargeSizes() {
		return largeSizes;
	}

	private void setLargeSizes(Sizes sizes) {
		this.largeSizes = sizes;
	}

	protected Sizes getCurrentSizes() {
		return currentSizes;
	}

	private void setCurrentSizes(Sizes sizes) {
		this.currentSizes = sizes;
	}

	protected static class RenderContext {

		private AmstradVirtualKeyboard keyboard;

		private AmstradVirtualKeyboardGridLayout keyboardLayout;

		private Graphics2D graphics2D;

		private Point locationOfUpperLeftKey;

		private Sizes sizes;

		public RenderContext(AmstradVirtualKeyboard keyboard, AmstradVirtualKeyboardGridLayout keyboardLayout,
				Graphics2D graphics2D) {
			this.keyboard = keyboard;
			this.keyboardLayout = keyboardLayout;
			this.graphics2D = graphics2D;
		}

		public AmstradVirtualKeyboard getKeyboard() {
			return keyboard;
		}

		public AmstradVirtualKeyboardGridLayout getKeyboardLayout() {
			return keyboardLayout;
		}

		public Graphics2D getGraphics2D() {
			return graphics2D;
		}

		public void setGraphics2D(Graphics2D graphics2D) {
			this.graphics2D = graphics2D;
		}

		public Point getLocationOfUpperLeftKey() {
			return locationOfUpperLeftKey;
		}

		public void setLocationOfUpperLeftKey(Point location) {
			this.locationOfUpperLeftKey = location;
		}

		public Sizes getSizes() {
			return sizes;
		}

		public void setSizes(Sizes sizes) {
			this.sizes = sizes;
		}

	}

	protected static class Sizes {

		private int suitableAvailableHeight;

		private int boardMarginLeft;

		private int boardMarginRight;

		private int keyUnitWidth;

		private int keyUnitHeight;

		private int keyHorizontalGap;

		private int keyVerticalGap;

		private int keyCornerRoundDiameter;

		private int keyBorderThickness;

		private float keyLabelScale;

		public Sizes(int suitableAvailableHeight) {
			this.suitableAvailableHeight = suitableAvailableHeight;
		}

		public static Sizes interpolate(Sizes s1, Sizes s2, int availableHeight) {
			Sizes sizes = new Sizes(availableHeight);
			sizes.setBoardMarginLeft(
					interpolate(s1, s1.getBoardMarginLeft(), s2, s2.getBoardMarginLeft(), availableHeight));
			sizes.setBoardMarginRight(
					interpolate(s1, s1.getBoardMarginRight(), s2, s2.getBoardMarginRight(), availableHeight));
			sizes.setKeyUnitWidth(interpolate(s1, s1.getKeyUnitWidth(), s2, s2.getKeyUnitWidth(), availableHeight));
			sizes.setKeyUnitHeight(interpolate(s1, s1.getKeyUnitHeight(), s2, s2.getKeyUnitHeight(), availableHeight));
			sizes.setKeyHorizontalGap(
					interpolate(s1, s1.getKeyHorizontalGap(), s2, s2.getKeyHorizontalGap(), availableHeight));
			sizes.setKeyVerticalGap(
					interpolate(s1, s1.getKeyVerticalGap(), s2, s2.getKeyVerticalGap(), availableHeight));
			sizes.setKeyCornerRoundDiameter(interpolate(s1, s1.getKeyCornerRoundDiameter(), s2,
					s2.getKeyCornerRoundDiameter(), availableHeight));
			sizes.setKeyBorderThickness(
					interpolate(s1, s1.getKeyBorderThickness(), s2, s2.getKeyBorderThickness(), availableHeight));
			sizes.setKeyLabelScale(interpolate(s1, s1.getKeyLabelScale(), s2, s2.getKeyLabelScale(), availableHeight));
			return sizes;
		}

		private static int interpolate(Sizes s1, int value1, Sizes s2, int value2, int availableHeight) {
			return Math.round(interpolate(s1, (float) value1, s2, (float) value2, availableHeight));
		}

		private static float interpolate(Sizes s1, float value1, Sizes s2, float value2, int availableHeight) {
			float h1 = s1.getSuitableAvailableHeight();
			float h2 = s2.getSuitableAvailableHeight();
			return value1 + (value2 - value1) * (availableHeight - h1) / (h2 - h1);
		}

		public int getSuitableAvailableHeight() {
			return suitableAvailableHeight;
		}

		public int getBoardMarginLeft() {
			return boardMarginLeft;
		}

		public void setBoardMarginLeft(int margin) {
			this.boardMarginLeft = margin;
		}

		public int getBoardMarginRight() {
			return boardMarginRight;
		}

		public void setBoardMarginRight(int margin) {
			this.boardMarginRight = margin;
		}

		public int getKeyUnitWidth() {
			return keyUnitWidth;
		}

		public void setKeyUnitWidth(int width) {
			this.keyUnitWidth = width;
		}

		public int getKeyUnitHeight() {
			return keyUnitHeight;
		}

		public void setKeyUnitHeight(int height) {
			this.keyUnitHeight = height;
		}

		public int getKeyHorizontalGap() {
			return keyHorizontalGap;
		}

		public void setKeyHorizontalGap(int gap) {
			this.keyHorizontalGap = gap;
		}

		public int getKeyVerticalGap() {
			return keyVerticalGap;
		}

		public void setKeyVerticalGap(int gap) {
			this.keyVerticalGap = gap;
		}

		public int getKeyCornerRoundDiameter() {
			return keyCornerRoundDiameter;
		}

		public void setKeyCornerRoundDiameter(int diameter) {
			this.keyCornerRoundDiameter = diameter;
		}

		public int getKeyBorderThickness() {
			return keyBorderThickness;
		}

		public void setKeyBorderThickness(int thickness) {
			this.keyBorderThickness = thickness;
		}

		public float getKeyLabelScale() {
			return keyLabelScale;
		}

		public void setKeyLabelScale(float scale) {
			this.keyLabelScale = scale;
		}

	}

}