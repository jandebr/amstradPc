package org.maia.amstrad.gui.browser.carousel.animation.sprite;

import java.awt.Graphics2D;

public class SpriteImageCatalog {

	private SpriteImage orcaFin;

	private SpriteImage dolphin;

	public SpriteImageCatalog() {
	}

	public SpriteImage getOrcaFin() {
		if (orcaFin == null) {
			orcaFin = new SpriteImageRLE(23, 16,
					new int[] { -1, 12, 0, 3, -2, -1, 10, 0, 5, -2, -1, 9, 0, 6, -2, -1, 8, 0, 7, -2, -1, 7, 0, 9, -2,
							-1, 6, 0, 10, -2, -1, 6, 0, 10, -2, -1, 5, 0, 11, -2, -1, 5, 0, 11, -2, -1, 4, 0, 12, -2,
							-1, 4, 0, 12, -2, -1, 3, 0, 14, -2, -1, 3, 0, 15, -2, -1, 2, 0, 17, -2, -1, 2, 0, 19, -2, 0,
							23 });
		}
		return orcaFin;
	}

	public SpriteImage getDolphin() {
		if (dolphin == null) {
			dolphin = new SpriteImageRLE(33, 23, new int[] { -1, 23, 0, 1, -2, -1, 22, 0, 3, -2, -1, 22, 0, 4, -2, -1,
					23, 0, 4, -2, -1, 17, 0, 10, -2, -1, 14, 0, 14, -2, -1, 13, 0, 15, -2, -1, 12, 0, 16, -2, -1, 11, 0,
					18, -2, -1, 1, 0, 3, -1, 6, 0, 20, -2, 0, 6, -1, 3, 0, 4, 1, 4, 0, 14, -2, -1, 1, 0, 6, -1, 1, 0, 3,
					1, 9, 0, 12, -2, -1, 3, 0, 6, 1, 5, -1, 2, 1, 5, 0, 11, -2, -1, 3, 0, 5, 1, 3, -1, 7, 1, 3, 0, 12,
					-2, -1, 3, 0, 4, 1, 2, -1, 8, 0, 16, -2, -1, 4, 0, 3, 1, 2, -1, 7, 0, 4, 1, 3, 0, 5, 2, 2, 0, 3, -2,
					-1, 3, 0, 3, 1, 2, -1, 7, 0, 3, 1, 7, 0, 3, 2, 2, 0, 3, -2, -1, 3, 0, 3, 1, 2, -1, 8, 1, 2, -1, 4,
					1, 4, 0, 6, -2, -1, 3, 0, 2, 1, 2, -1, 16, 1, 5, 0, 4, -2, -1, 3, 0, 2, 1, 1, -1, 19, 1, 4, 0, 4,
					-2, -1, 2, 0, 3, -1, 22, 1, 3, 0, 3, -2, -1, 3, 0, 1, -1, 24, 1, 4, -2, -1, 30, 1, 2 });
		}
		return dolphin;
	}

	private static class SpriteImageRLE implements SpriteImage {

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

}