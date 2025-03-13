package org.maia.amstrad.gui.browser.carousel.caption;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

import org.maia.amstrad.gui.browser.carousel.CarouselComponentFactory;
import org.maia.amstrad.gui.browser.carousel.info.InfoSection;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.swing.HorizontalAlignment;
import org.maia.swing.text.TextLabel;
import org.maia.swing.text.TextLabel.TextOverflowMode;
import org.maia.swing.util.SwingUtils;
import org.maia.util.StringUtils;

public abstract class CarouselCaptionComponent extends Box {

	private CarouselComponentFactory factory;

	protected CarouselCaptionComponent(CarouselComponentFactory factory) {
		super(BoxLayout.X_AXIS);
		this.factory = factory;
	}

	protected JComponent buildInfoIconsPanel(List<InfoSection> infoSections) {
		Box box = Box.createHorizontalBox();
		int iconSpacing = Math.max(Math.min(getCaptionHeight() / 6, 8), 2);
		for (int i = 0; i < infoSections.size(); i++) {
			if (i > 0) {
				box.add(Box.createHorizontalStrut(iconSpacing));
			}
			box.add(infoSections.get(i).getIcon());
		}
		return box;
	}

	protected TextLabel buildTextElement(String text, Color textColor, Font baseFont) {
		TextLabel element = null;
		if (!StringUtils.isEmpty(text)) {
			Font font = baseFont.deriveFont(TextLabel.getFontSizeForLineHeight(baseFont, getCaptionHeight()));
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
		return getFactory().getLayout().getCaptionBounds().getSize();
	}

	protected CarouselProgramBrowserTheme getTheme() {
		return getFactory().getTheme();
	}

	protected CarouselComponentFactory getFactory() {
		return factory;
	}

}