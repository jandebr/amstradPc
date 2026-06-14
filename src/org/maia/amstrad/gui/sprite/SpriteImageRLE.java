package org.maia.amstrad.gui.sprite;

import java.awt.Graphics2D;

public class SpriteImageRLE implements SpriteImage {

	private int width;

	private int height;

	/**
	 * Patterns: <br>
	 * &lt;colorIndex : 0,1,2,...&gt; &lt;length : 1,2,...&gt;<br>
	 * &lt;move : -1&gt; &lt;length : 1,2,...&gt;<br>
	 * &lt;newline : -2&gt;
	 */
	private int[] imageData;

	public SpriteImageRLE(int width, int height, int[] imageData) {
		this.width = width;
		this.height = height;
		this.imageData = imageData;
	}

	@Override
	public void draw(Graphics2D g, SpriteColorMap colorMap) {
		int[] data = getImageData();
		int di = 0;
		int x = 0, y = 0;
		while (di < data.length) {
			int ci = data[di++];
			if (ci >= 0) {
				int len = data[di++];
				g.setColor(colorMap.getColor(ci));
				g.fillRect(x, y, len, 1);
				x += len;
			} else if (ci == -1) {
				x += data[di++];
			} else if (ci == -2) {
				x = 0;
				y++;
			}
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private int[] getImageData() {
		return imageData;
	}

}