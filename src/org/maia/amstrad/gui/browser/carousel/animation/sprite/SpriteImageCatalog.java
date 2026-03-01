package org.maia.amstrad.gui.browser.carousel.animation.sprite;

import java.awt.Graphics2D;

public class SpriteImageCatalog {

	private SpriteImage orcaFinSmall;

	private SpriteImage sharkFinSmall;

	private SpriteImage sharkFinLarge;

	public SpriteImageCatalog() {
	}

	public SpriteImage getOrcaFinSmall() {
		if (orcaFinSmall == null) {
			orcaFinSmall = new SpriteImageRLE(23, 16,
					new int[] { -1, 12, 0, 3, -2, -1, 10, 0, 5, -2, -1, 9, 0, 6, -2, -1, 8, 0, 7, -2, -1, 7, 0, 9, -2,
							-1, 6, 0, 10, -2, -1, 6, 0, 10, -2, -1, 5, 0, 11, -2, -1, 5, 0, 11, -2, -1, 4, 0, 12, -2,
							-1, 4, 0, 12, -2, -1, 3, 0, 14, -2, -1, 3, 0, 15, -2, -1, 2, 0, 17, -2, -1, 2, 0, 19, -2, 0,
							23 });
		}
		return orcaFinSmall;
	}

	public SpriteImage getSharkFinSmall() {
		if (sharkFinSmall == null) {
			sharkFinSmall = new SpriteImageRLE(23, 16,
					new int[] { -1, 12, 0, 3, -2, -1, 10, 0, 5, -2, -1, 9, 0, 6, -2, -1, 8, 0, 7, -2, -1, 7, 0, 9, -2,
							-1, 6, 0, 10, -2, -1, 6, 0, 9, -2, -1, 5, 0, 11, -2, -1, 5, 0, 10, -2, -1, 4, 0, 12, -2, -1,
							4, 0, 12, -2, -1, 3, 0, 14, -2, -1, 3, 0, 15, -2, -1, 2, 0, 17, -2, -1, 2, 0, 19, -2, 0,
							23 });
		}
		return sharkFinSmall;
	}

	public SpriteImage getSharkFinLarge() {
		if (sharkFinLarge == null) {
			sharkFinLarge = new SpriteImageRLE(40, 32,
					new int[] { -1, 21, 0, 3, -2, -1, 19, 0, 5, -2, -1, 17, 0, 8, -2, -1, 16, 0, 9, -2, -1, 15, 0, 10,
							-2, -1, 14, 0, 10, -2, -1, 14, 0, 11, -2, -1, 13, 0, 12, -2, -1, 12, 0, 13, -2, -1, 11, 0,
							14, -2, -1, 11, 0, 14, -2, -1, 10, 0, 15, -2, -1, 10, 0, 15, -2, -1, 9, 0, 16, -2, -1, 8, 0,
							17, -2, -1, 8, 0, 16, -2, -1, 7, 0, 18, -2, -1, 7, 0, 19, -2, -1, 6, 0, 20, -2, -1, 6, 0,
							21, -2, -1, 5, 0, 22, -2, -1, 5, 0, 23, -2, -1, 4, 0, 24, -2, -1, 4, 0, 25, -2, -1, 4, 0,
							26, -2, -1, 3, 0, 27, -2, -1, 3, 0, 28, -2, -1, 3, 0, 29, -2, -1, 2, 0, 32, -2, -1, 2, 0,
							35, -2, 0, 40, -2, 0, 40 });
		}
		return sharkFinLarge;
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
					g.drawLine(x, y, x + len - 1, y);
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