package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.SpriteImageRLE;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class DolphinTestAnimation extends CarouselPortholePixelatedAnimation {

	private Dolphin dolphin;

	public DolphinTestAnimation(AmstradGraphicsContext graphicsContext) {
		super(graphicsContext);
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setDolphin(new Dolphin());
	}

	@Override
	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		super.renderInPorthole(g, elapsedTimeMillis);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(getPixelSize(), getPixelSize());
		renderDolphin(g2, getDolphin(), elapsedTimeMillis);
		g2.dispose();
	}

	protected void renderDolphin(Graphics2D g, Dolphin dolphin, long elapsedTimeMillis) {
		dolphin.update(elapsedTimeMillis);
		g.setColor(new Color(200, 200, 200));
		g.fillRect(dolphin.getX() - 1, dolphin.getY() - 1, dolphin.getWidth() + 2, dolphin.getHeight() + 2);
		g.setColor(Color.BLACK);
		g.fillRect(dolphin.getX(), dolphin.getY(), dolphin.getWidth(), dolphin.getHeight());
		dolphin.draw(g);
	}

	protected SpriteColorMap createDolphinColors() {
		SpriteColorMapImpl colors = new SpriteColorMapImpl();
		colors.setColor(0, new Color(16, 29, 51));
		colors.setColor(1, new Color(127, 140, 161));
		colors.setColor(2, new Color(1, 7, 18));
		return colors;
	}

	protected SpriteImage createDolphinImage() {
		return new SpriteImageRLE(33, 23,
				new int[] { -1, 23, 0, 1, -2, -1, 22, 0, 3, -2, -1, 22, 0, 4, -2, -1, 23, 0, 4, -2, -1, 17, 0, 10, -2,
						-1, 14, 0, 14, -2, -1, 13, 0, 15, -2, -1, 12, 0, 16, -2, -1, 11, 0, 18, -2, -1, 1, 0, 3, -1, 6,
						0, 20, -2, 0, 6, -1, 3, 0, 4, 1, 4, 0, 14, -2, -1, 1, 0, 6, -1, 1, 0, 3, 1, 9, 0, 12, -2, -1, 3,
						0, 6, 1, 5, -1, 2, 1, 5, 0, 11, -2, -1, 3, 0, 5, 1, 3, -1, 7, 1, 3, 0, 12, -2, -1, 3, 0, 4, 1,
						2, -1, 8, 0, 16, -2, -1, 4, 0, 3, 1, 2, -1, 7, 0, 4, 1, 3, 0, 5, 2, 2, 0, 3, -2, -1, 3, 0, 3, 1,
						2, -1, 7, 0, 3, 1, 7, 0, 3, 2, 2, 0, 3, -2, -1, 3, 0, 3, 1, 2, -1, 8, 1, 2, -1, 4, 1, 4, 0, 6,
						-2, -1, 3, 0, 2, 1, 2, -1, 16, 1, 5, 0, 4, -2, -1, 3, 0, 2, 1, 1, -1, 19, 1, 4, 0, 4, -2, -1, 2,
						0, 3, -1, 22, 1, 3, 0, 3, -2, -1, 3, 0, 1, -1, 24, 1, 4, -2, -1, 30, 1, 2 });
	}

	@Override
	protected int getTargetPixelWidth() {
		return 64;
	}

	private Dolphin getDolphin() {
		return dolphin;
	}

	private void setDolphin(Dolphin dolphin) {
		this.dolphin = dolphin;
	}

	private class Dolphin extends Sprite {

		public Dolphin() {
			super(createDolphinImage(), createDolphinColors());
		}

		public void update(long elapsedTimeMillis) {
			move(12, 12);
			setRotationDegrees(elapsedTimeMillis / 5f);
		}

	}

}