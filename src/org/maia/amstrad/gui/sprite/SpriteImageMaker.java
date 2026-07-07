package org.maia.amstrad.gui.sprite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.PrintStream;

import org.maia.amstrad.gui.UIResources;
import org.maia.graphics2d.image.ImageUtils;

public class SpriteImageMaker {

	public static void main(String[] args) {
		makeImageEncodedAsRLE();
	}

	private static void makeImageEncodedAsRLE() {
		BufferedImage image = UIResources.loadImage("animations/item/rocket_41x19.png");
		EncoderRLE encoder = new EncoderRLE(initSpriteColorMap());
		encoder.encode(image).print(System.out);
	}

	private static SpriteColorMapImpl initSpriteColorMap() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(127, 127, 127));
		colorMap.setColor(1, new Color(255, 255, 255));
		colorMap.setColor(2, new Color(45, 56, 103));
		return colorMap;
	}

	private static class EncoderRLE {

		private SpriteColorMapImpl colorMap;

		public EncoderRLE() {
			this(new SpriteColorMapImpl());
		}

		public EncoderRLE(SpriteColorMapImpl colorMap) {
			this.colorMap = colorMap;
		}

		public ImageDataRLE encode(BufferedImage image) {
			int[] data = new int[100];
			int dataOffset = 0;
			int width = ImageUtils.getWidth(image);
			int height = ImageUtils.getHeight(image);
			int x = 0, y = 0;
			while (y < height) {
				int argb = image.getRGB(x, y);
				int ci = getColorIndex(new Color(argb, true));
				int n = 1;
				while (x + n < width && image.getRGB(x + n, y) == argb)
					n++;
				x += n;
				if (x < width || ci >= 0) {
					data = pushData(data, dataOffset++, ci);
					data = pushData(data, dataOffset++, n);
				}
				if (x == width) {
					x = 0;
					if (++y < height) {
						data = pushData(data, dataOffset++, -2);
					}
				}
			}
			return new ImageDataRLE(getColorMap(), width, height, truncate(data, dataOffset));
		}

		private int[] pushData(int[] data, int dataOffset, int value) {
			int[] dataExt = data;
			if (dataOffset == data.length) {
				dataExt = new int[data.length + 100];
				System.arraycopy(data, 0, dataExt, 0, dataOffset);
			}
			dataExt[dataOffset] = value;
			return dataExt;
		}

		private int[] truncate(int[] data, int endIndexExclusive) {
			if (endIndexExclusive == data.length) {
				return data;
			} else {
				int[] dataTru = new int[endIndexExclusive];
				System.arraycopy(data, 0, dataTru, 0, endIndexExclusive);
				return dataTru;
			}
		}

		private int getColorIndex(Color color) {
			if (color.getAlpha() == 0) {
				// move over transparent pixels
				return -1;
			} else {
				SpriteColorMapImpl colorMap = getColorMap();
				int n = colorMap.getMaxColorIndex();
				for (int i = 0; i <= n; i++) {
					if (color.equals(colorMap.getColor(i)))
						return i;
				}
				// register new color
				colorMap.setColor(++n, color);
				return n;
			}
		}

		public SpriteColorMapImpl getColorMap() {
			return colorMap;
		}

	}

	private static class ImageDataRLE {

		private SpriteColorMap colorMap;

		private int width;

		private int height;

		private int[] data;

		public ImageDataRLE(SpriteColorMap colorMap, int width, int height, int[] data) {
			this.colorMap = colorMap;
			this.width = width;
			this.height = height;
			this.data = data;
		}

		public void print(PrintStream output) {
			printColorMap(output);
			output.println();
			printData(output);
		}

		private void printColorMap(PrintStream output) {
			output.println("SpriteColorMapImpl colorMap = new SpriteColorMapImpl();");
			for (int i = 0; i <= getColorMap().getMaxColorIndex(); i++) {
				Color color = getColorMap().getColor(i);
				if (color != null) {
					output.print("colorMap.setColor(");
					output.print(i);
					output.print(", new Color(");
					output.print(color.getRed());
					output.print(", ");
					output.print(color.getGreen());
					output.print(", ");
					output.print(color.getBlue());
					output.println("));");
				}
			}
		}

		private void printData(PrintStream output) {
			output.print("new SpriteImageRLE(");
			output.print(getWidth());
			output.print(", ");
			output.print(getHeight());
			output.print(", new int[] {");
			for (int i = 0; i < getData().length; i++) {
				if (i > 0)
					output.print(',');
				output.print(' ');
				output.print(getData()[i]);
			}
			output.println(" });");
		}

		private SpriteColorMap getColorMap() {
			return colorMap;
		}

		private int getWidth() {
			return width;
		}

		private int getHeight() {
			return height;
		}

		private int[] getData() {
			return data;
		}

	}

}