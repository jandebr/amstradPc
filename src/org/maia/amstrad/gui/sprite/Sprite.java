package org.maia.amstrad.gui.sprite;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.maia.graphics2d.geometry.Radians;
import org.maia.graphics2d.image.ImageUtils;

public class Sprite {

	private int x; // leftmost coordinate, regardless of orientation

	private int y; // topmost coordinate, regardless of orientation

	private int orientationX = 1; // draw left-to-right (+1) or right-to-left (-1)

	private int orientationY = 1; // draw top-to-bottom (+1) or bottom-to-top (-1)

	private float rotationDegrees;

	private SpriteImage image;

	private SpriteColorMap colorMap;

	private BufferedImage canvas;

	private BufferedImage canvasRotated;

	public Sprite(SpriteImage image, SpriteColorMap colorMap) {
		this.image = image;
		this.colorMap = colorMap;
	}

	public void changeImage(SpriteImage image) {
		setImage(image);
		setCanvas(null);
		setCanvasRotated(null);
	}

	public void draw(Graphics2D g) {
		if (getRotationDegrees() != 0f) {
			drawUsingCanvas(g);
		} else {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(getX(), getY());
			if (isMirroredX())
				g2.translate(getWidth(), 0);
			if (isMirroredY())
				g2.translate(0, getHeight());
			g2.scale(getOrientationX(), getOrientationY());
			getImage().draw(g2, getColorMap());
			g2.dispose();
		}
	}

	private void drawUsingCanvas(Graphics2D g) {
		BufferedImage canvasRotated = drawRotatedOnCanvas();
		double s2 = canvasRotated.getWidth() / 2.0;
		double w2 = getWidth() / 2.0;
		double h2 = getHeight() / 2.0;
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.translate(getX() + w2 - s2 - 0.5, getY() + h2 - s2 - 0.5);
		g2.drawImage(canvasRotated, 0, 0, null);
		g2.dispose();
	}

	private BufferedImage drawRotatedOnCanvas() {
		BufferedImage canvasRotated = getCleanCanvasRotated();
		Graphics2D gc = canvasRotated.createGraphics();
		double s2 = canvasRotated.getWidth() / 2.0;
		double w2 = getWidth() / 2.0;
		double h2 = getHeight() / 2.0;
		gc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		gc.translate(s2, s2);
		gc.rotate(Radians.degreesToRadians(getRotationDegrees()));
		gc.scale(getOrientationX(), getOrientationY());
		gc.translate(-w2, -h2);
		gc.drawImage(drawOnCanvas(), 0, 0, null);
		gc.dispose();
		return canvasRotated;
	}

	private BufferedImage drawOnCanvas() {
		BufferedImage canvas = getCanvas();
		if (canvas == null) {
			canvas = ImageUtils.createImage(getWidth(), getHeight());
			Graphics2D gc = canvas.createGraphics();
			getImage().draw(gc, getColorMap());
			gc.dispose();
			setCanvas(canvas);
		}
		return canvas;
	}

	public void flipX() {
		setOrientationX(-getOrientationX());
	}

	public void flipY() {
		setOrientationY(-getOrientationY());
	}

	public void translate(int dx, int dy) {
		translateX(dx);
		translateY(dy);
	}

	public void translateX(int dx) {
		setX(getX() + dx);
	}

	public void translateY(int dy) {
		setY(getY() + dy);
	}

	public void move(int x, int y) {
		setX(x);
		setY(y);
	}

	public boolean isMirroredX() {
		return getOrientationX() < 0;
	}

	public boolean isMirroredY() {
		return getOrientationY() < 0;
	}

	public int getWidth() {
		return getImage().getWidth();
	}

	public int getHeight() {
		return getImage().getHeight();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	private int getOrientationX() {
		return orientationX;
	}

	private void setOrientationX(int orientationX) {
		this.orientationX = orientationX;
	}

	private int getOrientationY() {
		return orientationY;
	}

	private void setOrientationY(int orientationY) {
		this.orientationY = orientationY;
	}

	public float getRotationDegrees() {
		return rotationDegrees;
	}

	public void setRotationDegrees(float rotationDegrees) {
		this.rotationDegrees = rotationDegrees;
	}

	public SpriteImage getImage() {
		return image;
	}

	private void setImage(SpriteImage image) {
		this.image = image;
	}

	public SpriteColorMap getColorMap() {
		return colorMap;
	}

	private BufferedImage getCleanCanvasRotated() {
		BufferedImage canvas = getCanvasRotated();
		if (canvas == null) {
			int size = 2 * (1 + (int) Math.ceil(Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight())));
			canvas = ImageUtils.createImage(size, size);
			setCanvasRotated(canvas);
		} else {
			ImageUtils.makeFullyTransparent(canvas);
		}
		return canvas;
	}

	private BufferedImage getCanvas() {
		return canvas;
	}

	private void setCanvas(BufferedImage canvas) {
		this.canvas = canvas;
	}

	private BufferedImage getCanvasRotated() {
		return canvasRotated;
	}

	private void setCanvasRotated(BufferedImage canvasRotated) {
		this.canvasRotated = canvasRotated;
	}

}