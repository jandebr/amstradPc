package org.maia.amstrad.gui.browser.carousel.caption;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.swing.layout.HorizontalAlignment;
import org.maia.swing.text.TextLabel;
import org.maia.swing.text.TextLabel.TextOverflowMode;
import org.maia.swing.SwingUtils;
import org.maia.util.StringUtils;

public abstract class CarouselCaptionComponent extends Box {

	private Dimension captionSize;

	private CarouselProgramBrowserTheme theme;

	protected CarouselCaptionComponent(Dimension captionSize, CarouselProgramBrowserTheme theme) {
		super(BoxLayout.X_AXIS);
		this.captionSize = captionSize;
		this.theme = theme;
	}

	protected TextLabel buildTextElement(String text, Color textColor, Font baseFont) {
		TextLabel element = null;
		if (!StringUtils.isEmpty(text)) {
			int height = Math.round(getCaptionHeight() * getTheme().getCaptionTextScale());
			Font font = baseFont.deriveFont(TextLabel.getFontSizeForLineHeight(baseFont, height));
			element = TextLabel.createLineLabel(text, font, HorizontalAlignment.LEFT);
			element.setBackground(getTheme().getBackgroundColor());
			element.setForeground(textColor);
		}
		return element;
	}

	protected void addTextElement(String text, Color textColor, Font baseFont) {
		addTextElement(buildTextElement(text, textColor, baseFont));
	}

	protected void addTextElement(TextLabel element) {
		int w = getRemainingWidth();
		if (w > 0 && element != null) {
			String text = element.getText();
			int spaceWidth = 0;
			int leadingSpaces = text.length() - text.stripLeading().length();
			int trailingSpaces = text.length() - text.stripTrailing().length();
			if (leadingSpaces > 0 || trailingSpaces > 0) {
				spaceWidth = TextLabel.getAdvanceOfSpaceCharacter(element.getFont());
			}
			if (leadingSpaces > 0) {
				int ws = Math.min(spaceWidth * leadingSpaces, w);
				add(Box.createHorizontalStrut(ws));
				w -= ws;
			}
			if (w > 0) {
				int elementHeight = element.getPreferredSize().height;
				int elementWidth = element.getPreferredSize().width;
				if (elementWidth > w) {
					element.setPreferredSize(new Dimension(w, elementHeight));
					element.setTextOverflowMode(TextOverflowMode.FADE);
				}
				SwingUtils.fixSize(element, element.getPreferredSize());
				add(element);
				w -= element.getWidth();
				if (w > 0 && trailingSpaces > 0) {
					int ws = Math.min(spaceWidth * trailingSpaces, w);
					add(Box.createHorizontalStrut(ws));
				}
			}
		}
	}

	protected void fixSize() {
		SwingUtils.fixSize(this, getCaptionSize());
	}

	protected boolean isFull() {
		return getRemainingWidth() <= 0;
	}

	protected int getRemainingWidth() {
		return getCaptionWidth() - getPreferredSize().width;
	}

	protected int getCaptionWidth() {
		return getCaptionSize().width;
	}

	protected int getCaptionHeight() {
		return getCaptionSize().height;
	}

	protected Dimension getCaptionSize() {
		return captionSize;
	}

	protected CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

}