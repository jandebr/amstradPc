package org.maia.amstrad.gui.overlay.controlkeys;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import org.maia.amstrad.gui.overlay.AbstractDisplayOverlay;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class ControlKeysDisplayOverlay extends AbstractDisplayOverlay {

	public static boolean DEFAULT_SHOW_CONTROLKEYS = false;

	public static boolean DEFAULT_AUTOHIDE_CONTROLKEYS = true;

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

	private long autohideOffsetTime;

	private List<ControlKey> allControlKeys;

	private List<ControlKey> defaultControlKeys;

	public ControlKeysDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
		this.allControlKeys = new Vector<ControlKey>();
		this.defaultControlKeys = new Vector<ControlKey>();
		populateDefaultControlKeys();
	}

	private void populateDefaultControlKeys() {
		getDefaultControlKeys().add(new ProgramInfoControlKey(getAmstracPc())); // when in native screen
		getDefaultControlKeys().add(new PopupMenuControlKey(getAmstracPc()));
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		super.init(displayComponent, graphicsContext);
		setAutohideOffsetTime(System.currentTimeMillis());
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (offscreenImage)
			return;
		if (!isShowControlKeysEnabled())
			return;
		if (getAmstradContext().isTerminationShowing(getAmstracPc()))
			return;
		double r = 0;
		long now = System.currentTimeMillis();
		if (isAutohideControlKeys()) {
			r = (now - getAutohideOffsetTime()) / (double) FADEOUT_TIME_MILLIS;
		} else {
			setAutohideOffsetTime(now);
		}
		if (r <= 1.0) {
			double fadeout = r <= 0.4 ? 0.0 : Math.sqrt((r - 0.4) / 0.6);
			updateColors((float) fadeout);
			renderControlKeysBar(displayView, displayBounds, monitorInsets, graphicsContext);
		}
	}

	private boolean isShowControlKeysEnabled() {
		if (isAmstradSystemSetup()) {
			return getAmstradSystem().getCurrentScreen().isShowControlKeys();
		} else {
			return DEFAULT_SHOW_CONTROLKEYS;
		}
	}

	private boolean isAutohideControlKeys() {
		if (isAmstradSystemSetup()) {
			return getAmstradSystem().getCurrentScreen().isAutohideControlKeys();
		} else {
			return DEFAULT_AUTOHIDE_CONTROLKEYS;
		}
	}

	private void updateColors(float fadeout) {
		setBoxColor(makeColorMoreTransparent(BOX_COLOR, fadeout));
		setKeyColor(makeColorMoreTransparent(KEY_COLOR, fadeout));
		setKeyBorderColor(makeColorMoreTransparent(KEY_BORDER_COLOR, fadeout));
		setLabelColor(makeColorMoreTransparent(LABEL_COLOR, fadeout));
	}

	private void renderControlKeysBar(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			AmstradGraphicsContext graphicsContext) {
		FontMetrics fm = displayView.getFontMetrics(getFont(graphicsContext));
		int barHeight = fm.getHeight() * 3;
		int barTop = displayBounds.y + displayBounds.height - Math.min(monitorInsets.bottom, displayBounds.height / 17)
				- barHeight;
		int extremeBarWidth = displayBounds.width - monitorInsets.left - monitorInsets.right;
		int extremeBarLeft = displayBounds.x + monitorInsets.left;
		renderControlKeysBar(displayView, fm, extremeBarLeft, barTop, extremeBarWidth, barHeight);
	}

	private void renderControlKeysBar(AmstradDisplayView displayView, FontMetrics fm, int extremeBarLeft, int barTop,
			int extremeBarWidth, int barHeight) {
		List<ControlKey> controlKeys = getAllControlKeys();
		int spanWidth = computeVisibleControlKeysWidth(controlKeys, fm);
		if (spanWidth > 0) {
			// Box
			int barLeft = extremeBarLeft + (extremeBarWidth - spanWidth) / 2 - BOX_HOR_PADDING;
			int barWidth = spanWidth + 2 * BOX_HOR_PADDING;
			Graphics2D g2 = displayView.createDisplayViewport(barLeft, barTop, barWidth, barHeight);
			g2.setColor(getBoxColor());
			g2.fillRect(0, 0, barWidth, barHeight);
			// Controls
			g2.setFont(fm.getFont());
			int xLeft = BOX_HOR_PADDING;
			int yBaseline = (barHeight + fm.getAscent() - fm.getDescent()) / 2 + 1;
			for (ControlKey controlKey : controlKeys) {
				if (controlKey.isAvailable()) {
					renderControlKey(controlKey, fm, g2, xLeft, yBaseline);
					xLeft += computeControlKeyWidth(controlKey, fm);
					xLeft += HOR_SEPARATION;
				}
			}
			g2.dispose();
		}
	}

	private void renderControlKey(ControlKey controlKey, FontMetrics fm, Graphics2D g2, int xLeft, int yBaseline) {
		int kw = fm.stringWidth(controlKey.getKey());
		int kh = fm.getHeight();
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

	private int computeVisibleControlKeysWidth(List<ControlKey> controlKeys, FontMetrics fm) {
		int width = 0;
		int visible = 0;
		for (ControlKey controlKey : controlKeys) {
			if (controlKey.isAvailable()) {
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

	private synchronized List<ControlKey> getAllControlKeys() {
		allControlKeys.clear();
		allControlKeys.addAll(getContextualControlKeys());
		allControlKeys.addAll(getDefaultControlKeys());
		return allControlKeys;
	}

	private List<ControlKey> getDefaultControlKeys() {
		return defaultControlKeys;
	}

	protected List<ControlKey> getContextualControlKeys() {
		List<ControlKey> controlKeys = null;
		if (isAmstradSystemSetup()) {
			controlKeys = getAmstradSystem().getCurrentScreen().getAdditionalControlKeys();
		}
		if (controlKeys != null) {
			return controlKeys;
		} else {
			return Collections.emptyList();
		}
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

	private long getAutohideOffsetTime() {
		return autohideOffsetTime;
	}

	private void setAutohideOffsetTime(long time) {
		this.autohideOffsetTime = time;
	}

}