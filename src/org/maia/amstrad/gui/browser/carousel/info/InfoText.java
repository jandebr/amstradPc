package org.maia.amstrad.gui.browser.carousel.info;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.swing.animate.textslide.SlidingTextComponent;
import org.maia.swing.animate.textslide.SlidingTextComponent.TextSpacer;
import org.maia.swing.animate.textslide.SlidingTextComponentBuilder;

public class InfoText {

	private Dimension size;

	private CarouselProgramBrowserTheme theme;

	private TextSpacer textSeparator;

	private SlidingTextComponent component;

	public InfoText(Dimension size, CarouselProgramBrowserTheme theme) {
		this.size = size;
		this.theme = theme;
	}

	public boolean isEmpty() {
		return getComponent() == null || !getComponent().hasTextLines();
	}

	public InfoText makeEmpty() {
		setComponent(createEmptyTextComponent());
		return this;
	}

	public InfoText appendTextSeparator(TextSpacer separator) {
		setTextSeparator(separator);
		return this;
	}

	public InfoText appendText(String text, Color textColor, Font baseFont) {
		return appendText(text, textColor, baseFont, 1f);
	}

	public InfoText appendText(String text, Color textColor, Font baseFont, float magnifier) {
		SlidingTextComponent component = getComponent();
		if (component == null) {
			setComponent(createTextComponent(text, textColor, baseFont, magnifier));
		} else {
			SlidingTextComponent stc = createTextComponent(text, textColor, baseFont, magnifier);
			component.addTextLinesFrom(stc, consumeTextSeparator());
		}
		return this;
	}

	private SlidingTextComponent createEmptyTextComponent() {
		SlidingTextComponent comp = createTextComponent("", Color.BLACK, getTheme().getDefaultFont(), 1f);
		comp.removeAllTextLines();
		return comp;
	}

	private SlidingTextComponent createTextComponent(String text, Color textColor, Font baseFont, float magnifier) {
		Dimension size = getSize();
		CarouselProgramBrowserTheme theme = getTheme();
		SlidingTextComponentBuilder builder = new SlidingTextComponentBuilder(text);
		builder.withPadding(new Insets(0, 0, 0, 0));
		builder.withBackgroundColor(theme.getBackgroundColor());
		builder.withTextColor(textColor);
		builder.withFont(baseFont);
		builder.withLineWidth(size.width);
		builder.withInterParagraphSpacing(Math.round(theme.getInfoInterParagraphSpacing() * magnifier));
		builder.withInterLineSpacing(Math.round(theme.getInfoInterLineSpacing() * magnifier));
		builder.withLinesInView(theme.getInfoPreferredLinesInView() / magnifier);
		builder.withLinesInOverflowGradient(theme.getInfoOverflowGradientLines());
		builder.withTextDimension(builder.deriveTextDimensionForComponentHeight(size.height,
				theme.getInfoMinimumFontSize() * magnifier, theme.getInfoMaximumFontSize() * magnifier));
		builder.withMaximumSize(size);
		builder.withRepaintClientDriven(true);
		return builder.build();
	}

	private TextSpacer consumeTextSeparator() {
		TextSpacer separator = getTextSeparator();
		setTextSeparator(null);
		return separator;
	}

	private TextSpacer getTextSeparator() {
		return textSeparator;
	}

	private void setTextSeparator(TextSpacer separator) {
		this.textSeparator = separator;
	}

	public Dimension getSize() {
		return size;
	}

	public CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

	public SlidingTextComponent getComponent() {
		return component;
	}

	private void setComponent(SlidingTextComponent component) {
		this.component = component;
	}

}