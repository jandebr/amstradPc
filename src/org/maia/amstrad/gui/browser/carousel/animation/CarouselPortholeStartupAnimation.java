package org.maia.amstrad.gui.browser.carousel.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;

public abstract class CarouselPortholeStartupAnimation extends CarouselBaseAnimation
		implements CarouselStartupAnimation {

	private Dimension portholeSize;

	private BufferedImage portholeMask;

	private static final BufferedImage portholeMaskSmall = UIResources.loadImage("porthole-gradientmask400.png");

	private static final BufferedImage portholeMaskLarge = UIResources.loadImage("porthole-gradientmask800.png");

	protected CarouselPortholeStartupAnimation() {
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setPortholeSize(derivePortholeSize(displayWidth, displayHeight));
		setPortholeMask(derivePortholeMask());
	}

	@Override
	public final void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		float alpha = Math.min((elapsedTimeMillis - 200L) / 1000f, 1f);
		if (alpha > 0f) {
			Dimension size = getPortholeSize();
			int x = (displayWidth - size.width) / 2;
			int y = (displayHeight - size.height) / 2;
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(x, y); // center porthole in display
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			renderInPorthole(g2, elapsedTimeMillis);
			g2.setComposite(AlphaComposite.SrcOver);
			g2.drawImage(getPortholeMask(), 0, 0, size.width, size.height, null);
			g2.dispose();
		}
	}

	protected abstract void renderInPorthole(Graphics2D g, long elapsedTimeMillis);

	protected Dimension derivePortholeSize(int displayWidth, int displayHeight) {
		int s = 40 + (int) Math.round(Math.sqrt(displayHeight) * 6.0);
		s = Math.min(Math.min(s, displayHeight - 8), displayWidth - 8);
		return new Dimension(s, s);
	}

	protected BufferedImage derivePortholeMask() {
		Dimension size = getPortholeSize();
		if (size.width <= portholeMaskSmall.getWidth() && size.height <= portholeMaskSmall.getHeight()) {
			return portholeMaskSmall;
		} else {
			return portholeMaskLarge;
		}
	}

	@Override
	public Color getDisplayBackgroundColor() {
		return Color.BLACK;
	}

	protected Dimension getPortholeSize() {
		return portholeSize;
	}

	private void setPortholeSize(Dimension size) {
		this.portholeSize = size;
	}

	private BufferedImage getPortholeMask() {
		return portholeMask;
	}

	private void setPortholeMask(BufferedImage mask) {
		this.portholeMask = mask;
	}

}