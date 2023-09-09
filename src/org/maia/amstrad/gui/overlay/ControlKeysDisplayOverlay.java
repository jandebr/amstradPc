package org.maia.amstrad.gui.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class ControlKeysDisplayOverlay extends AbstractDisplayOverlay {

	private static final String SETTING_SHOW_CONTROLKEYS = "show_controlkeys";

	private static final String SETTING_AUTOHIDE_CONTROLKEYS = "show_controlkeys.autohide";

	private static long FADEOUT_TIME_MILLIS = 8000L;

	private static int BOX_HOR_PADDING = 16;

	private static int HOR_SEPARATION = 40;

	private static int KEY_PADDING = 4;

	private static Color BOX_COLOR = new Color(0, 0, 0, 100);

	private static Color KEY_COLOR = new Color(200, 200, 200, 150);

	private static Color KEY_BORDER_COLOR = new Color(50, 50, 50, 150);

	private static Color LABEL_COLOR = new Color(200, 200, 200, 150);

	private Color boxColor = BOX_COLOR;

	private Color keyColor = KEY_COLOR;

	private Color keyBorderColor = KEY_BORDER_COLOR;

	private Color labelColor = LABEL_COLOR;

	private Font font;

	private long lastTimeBasicDirectModus;

	private List<ControlKey> controlKeys;

	public ControlKeysDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
		this.controlKeys = new Vector<ControlKey>();
		populateControlKeys();
	}

	private void populateControlKeys() {
		getControlKeys().add(new PopupMenuControlKey());
		getControlKeys().add(new ProgramMenuControlKey());
		getControlKeys().add(new ProgramInfoControlKey());
		getControlKeys().add(new ProgramRunControlKey());
	}

	private boolean isShowControlKeysEnabled() {
		return getAmstradContext().getUserSettings().getBool(SETTING_SHOW_CONTROLKEYS, true);
	}

	private boolean isAutohideControlKeysEnabled() {
		return getAmstradContext().getUserSettings().getBool(SETTING_AUTOHIDE_CONTROLKEYS, true);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (offscreenImage)
			return;
		if (!isShowControlKeysEnabled())
			return;
		if (getAmstradContext().isTerminationShowing(getAmstracPc()))
			return;
		double r = 0;
		if (isAutohideControlKeysEnabled()) {
			long now = System.currentTimeMillis();
			if (getAmstracPc().getBasicRuntime().isDirectModus())
				setLastTimeBasicDirectModus(now);
			r = (now - getLastTimeBasicDirectModus()) / (double) FADEOUT_TIME_MILLIS;
		}
		if (r <= 1.0) {
			double fadeout = r <= 0.4 ? 0.0 : Math.sqrt((r - 0.4) / 0.6);
			updateColors(fadeout);
			renderControlKeysBar(display, displayBounds, monitorInsets, graphicsContext);
		}
	}

	private void updateColors(double fadeout) {
		setBoxColor(makeColorMoreTransparent(BOX_COLOR, fadeout));
		setKeyColor(makeColorMoreTransparent(KEY_COLOR, fadeout));
		setKeyBorderColor(makeColorMoreTransparent(KEY_BORDER_COLOR, fadeout));
		setLabelColor(makeColorMoreTransparent(LABEL_COLOR, fadeout));
	}

	private void renderControlKeysBar(Graphics2D display, Rectangle displayBounds, Insets monitorInsets,
			AmstradGraphicsContext graphicsContext) {
		Font font = getFont(graphicsContext);
		int barWidth = displayBounds.width - monitorInsets.left - monitorInsets.right;
		int barHeight = display.getFontMetrics(font).getHeight() * 3;
		int barTop = displayBounds.y + displayBounds.height - Math.min(monitorInsets.bottom, displayBounds.height / 17)
				- barHeight;
		Graphics2D g2 = (Graphics2D) display.create(displayBounds.x + monitorInsets.left, barTop, barWidth, barHeight);
		g2.setFont(font);
		renderControlKeysBar(g2, barWidth, barHeight);
		g2.dispose();
	}

	private void renderControlKeysBar(Graphics2D g2, int barWidth, int barHeight) {
		FontMetrics fm = g2.getFontMetrics();
		int totalWidth = computeVisibleControlKeysWidth(g2);
		if (totalWidth > 0) {
			// Box
			g2.setColor(getBoxColor());
			g2.fillRect((barWidth - totalWidth) / 2 - BOX_HOR_PADDING, 0, totalWidth + 2 * BOX_HOR_PADDING, barHeight);
			// Controls
			int xLeft = (barWidth - totalWidth) / 2;
			int yBaseline = (barHeight + fm.getMaxAscent()) / 2;
			for (ControlKey controlKey : getControlKeys()) {
				if (controlKey.isVisible()) {
					renderControlKey(controlKey, g2, fm, xLeft, yBaseline);
					xLeft += computeControlKeyWidth(controlKey, fm);
					xLeft += HOR_SEPARATION;
				}
			}
		}
	}

	private void renderControlKey(ControlKey controlKey, Graphics2D g2, FontMetrics fm, int xLeft, int yBaseline) {
		int kw = fm.stringWidth(controlKey.getKey());
		int kh = fm.getMaxAscent();
		int kp = KEY_PADDING;
		g2.setColor(getBoxColor());
		g2.fillRoundRect(xLeft - kp, yBaseline - kh - kp, kw + 2 * kp - 1, kh + 2 * kp, kp, kp);
		g2.setColor(getKeyBorderColor());
		g2.drawRoundRect(xLeft - kp, yBaseline - kh - kp, kw + 2 * kp - 1, kh + 2 * kp, kp, kp);
		g2.setColor(getLabelColor());
		g2.drawString(controlKey.toString(), xLeft, yBaseline);
		g2.setColor(getKeyColor());
		g2.drawString(controlKey.getKey(), xLeft, yBaseline);
	}

	private int computeVisibleControlKeysWidth(Graphics2D g2) {
		int width = 0;
		int visible = 0;
		FontMetrics fm = g2.getFontMetrics();
		for (ControlKey controlKey : getControlKeys()) {
			if (controlKey.isVisible()) {
				width += computeControlKeyWidth(controlKey, fm);
				visible++;
			}
		}
		if (visible > 1)
			width += (visible - 1) * HOR_SEPARATION;
		return width;
	}

	private int computeControlKeyWidth(ControlKey controlKey, FontMetrics fm) {
		return fm.stringWidth(controlKey.toString());
	}

	private List<ControlKey> getControlKeys() {
		return controlKeys;
	}

	private Font getFont(AmstradGraphicsContext graphicsContext) {
		if (font == null) {
			font = graphicsContext.getSystemFont().deriveFont(8f);
		}
		return font;
	}

	private Color getBoxColor() {
		return boxColor;
	}

	private void setBoxColor(Color boxColor) {
		this.boxColor = boxColor;
	}

	private Color getKeyColor() {
		return keyColor;
	}

	private void setKeyColor(Color keyColor) {
		this.keyColor = keyColor;
	}

	private Color getKeyBorderColor() {
		return keyBorderColor;
	}

	private void setKeyBorderColor(Color keyBorderColor) {
		this.keyBorderColor = keyBorderColor;
	}

	private Color getLabelColor() {
		return labelColor;
	}

	private void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	private long getLastTimeBasicDirectModus() {
		return lastTimeBasicDirectModus;
	}

	private void setLastTimeBasicDirectModus(long time) {
		this.lastTimeBasicDirectModus = time;
	}

	private abstract class ControlKey {

		private String key;

		private String label;

		protected ControlKey(String key, String label) {
			this.key = key;
			this.label = label;
		}

		public abstract boolean isAvailable();

		public boolean isVisible() {
			return isAvailable();
		}

		protected boolean isProgramBrowserShowing() {
			return getAmstradContext().isProgramBrowserShowing(getAmstracPc());
		}

		@Override
		public String toString() {
			return getKey() + "  " + getLabel();
		}

		public String getKey() {
			return key;
		}

		public String getLabel() {
			return label;
		}

	}

	private class PopupMenuControlKey extends ControlKey {

		public PopupMenuControlKey() {
			super("F2", "Options");
		}

		@Override
		public boolean isAvailable() {
			return getAmstracPc().getFrame().isPopupMenuEnabled();
		}

	}

	private class ProgramInfoControlKey extends ControlKey {

		public ProgramInfoControlKey() {
			super("F1", "Info");
		}

		@Override
		public boolean isAvailable() {
			return isProgramBrowserShowing() || getAmstracPc().getActions().getProgramInfoAction().isEnabled();
		}

	}

	private class ProgramRunControlKey extends ControlKey {

		public ProgramRunControlKey() {
			super("SPACE", "Run");
		}

		@Override
		public boolean isAvailable() {
			return isProgramBrowserShowing();
		}

	}

	private class ProgramMenuControlKey extends ControlKey {

		public ProgramMenuControlKey() {
			super("ENTER", "Menu");
		}

		@Override
		public boolean isAvailable() {
			return isProgramBrowserShowing();
		}

	}

}