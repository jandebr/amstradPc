package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class DolphinTestAnimation extends CarouselPortholePixelatedAnimation {

	private Dolphin dolphin;

	public DolphinTestAnimation(AmstradMonitorMode monitorMode) {
		super(monitorMode);
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

	private Dolphin getDolphin() {
		return dolphin;
	}

	private void setDolphin(Dolphin dolphin) {
		this.dolphin = dolphin;
	}

	private class Dolphin extends Sprite {

		public Dolphin() {
			super(getSpriteImageCatalog().getDolphin(), createDolphinColors());
		}

		public void update(long elapsedTimeMillis) {
			move(12, 12);
			setRotationDegrees(elapsedTimeMillis / 5f);
		}

	}

}