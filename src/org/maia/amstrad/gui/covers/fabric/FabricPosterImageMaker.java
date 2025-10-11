package org.maia.amstrad.gui.covers.fabric;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.covers.util.RandomImageMaker;
import org.maia.graphics2d.geometry.ApproximatingCurve2D;
import org.maia.graphics2d.geometry.Curve2D;
import org.maia.graphics2d.geometry.Point2D;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.graphics2d.image.ops.BandedImageDeformation;
import org.maia.graphics2d.image.ops.BandedImageDeformation.HorizontalImageBand;
import org.maia.graphics2d.image.ops.BandedImageDeformation.VerticalImageBand;
import org.maia.graphics2d.transform.TransformMatrix2D;
import org.maia.graphics2d.transform.Transformation2D;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public class FabricPosterImageMaker extends RandomImageMaker {

	private FabricTexture fabricTexture;

	private List<FabricPatchPatternGenerator> patternGenerators;

	private boolean conceptMode;

	public FabricPosterImageMaker() {
		this(new Randomizer());
	}

	public FabricPosterImageMaker(Randomizer randomizer) {
		this(randomizer, UIResources.loadImage("covers/cloth-texture-300x480.png"));
	}

	public FabricPosterImageMaker(Randomizer randomizer, BufferedImage texture) {
		super(randomizer);
		this.fabricTexture = new FabricTexture(texture);
		this.patternGenerators = new Vector<FabricPatchPatternGenerator>();
	}

	public void addPatternGenerator(FabricPatchPatternGenerator patternGenerator) {
		List<FabricPatchPatternGenerator> generators = getPatternGenerators();
		synchronized (generators) {
			generators.add(patternGenerator);
		}
	}

	public void removePatternGenerator(FabricPatchPatternGenerator patternGenerator) {
		List<FabricPatchPatternGenerator> generators = getPatternGenerators();
		synchronized (generators) {
			generators.remove(patternGenerator);
		}
	}

	public void removePatternGenerators() {
		List<FabricPatchPatternGenerator> generators = getPatternGenerators();
		synchronized (generators) {
			generators.clear();
		}
	}

	public BufferedImage makePosterImage(Dimension size) {
		return makePosterImage(size.width, size.height);
	}

	public BufferedImage makePosterImage(int width, int height) {
		BufferedImage image = null;
		FabricPatchPattern pattern = createPatchPattern(width, height);
		if (pattern != null) {
			image = ImageUtils.createImage(width, height, pattern.getBackgroundColor());
			paintPatchPattern(pattern, image);
			if (!pattern.isStraightEdges()) {
				image = applyPatchEdges(pattern, image, width, height);
			}
		}
		return image;
	}

	private FabricPatchPattern createPatchPattern(int width, int height) {
		FabricPatchPattern pattern = null;
		FabricPatchPatternGenerator generator = drawPatternGenerator();
		if (generator != null) {
			pattern = generator.generatePattern(width, height);
		}
		return pattern;
	}

	public FabricPatchPatternGenerator drawPatternGenerator() {
		FabricPatchPatternGenerator generator = null;
		List<FabricPatchPatternGenerator> generators = getPatternGenerators();
		synchronized (generators) {
			if (!generators.isEmpty()) {
				generator = generators.get(drawIntegerNumber(0, generators.size() - 1));
			}
		}
		return generator;
	}

	private void paintPatchPattern(FabricPatchPattern pattern, BufferedImage canvas) {
		for (FabricPatch patch : pattern.getPatches()) {
			if (isConceptMode()) {
				paintPatchContour(patch, canvas);
			} else {
				paintPatch(patch, canvas);
			}
		}
	}

	private void paintPatchContour(FabricPatch patch, BufferedImage canvas) {
		Graphics2D g = canvas.createGraphics();
		g.setColor(patch.getBaseColor());
		g.drawRect(patch.getOffsetX(), patch.getOffsetY(), patch.getWidth() - 1, patch.getHeight() - 1);
		g.dispose();
	}

	private void paintPatch(FabricPatch patch, BufferedImage canvas) {
		FabricTexture texture = getFabricTexture();
		int textureWidth = texture.getWidth();
		int textureHeight = texture.getHeight();
		int baseRgb = patch.getBaseColor().getRGB();
		int colorRgb = baseRgb & 0x00ffffff;
		int colorAlpha = baseRgb >>> 24;
		int x0 = patch.getOffsetX();
		int y0 = patch.getOffsetY();
		int width = patch.getWidth();
		int height = patch.getHeight();
		int ax0 = drawIntegerNumber(0, Math.max(textureWidth - width, 0));
		int ay0 = drawIntegerNumber(0, Math.max(textureHeight - height, 0));
		for (int y = y0; y < y0 + height; y++) {
			for (int x = x0; x < x0 + width; x++) {
				int tx = (ax0 + x - x0) % textureWidth;
				int ty = (ay0 + y - y0) % textureHeight;
				int front = texture.texturizeRGB(colorAlpha, colorRgb, tx, ty);
				int back = canvas.getRGB(x, y);
				canvas.setRGB(x, y, ColorUtils.combineByTransparency(front, back));
			}
		}
	}

	private BufferedImage applyPatchEdges(FabricPatchPattern pattern, BufferedImage canvas, int width, int height) {
		int maxExtent = Math.max((int) Math.ceil(Math.min(width, height) / 150.0), 1);
		canvas = applyVerticalPatchEdges(pattern, canvas, width, height, maxExtent);
		canvas = applyHorizontalPatchEdges(pattern, canvas, width, height, maxExtent);
		return canvas;
	}

	private BufferedImage applyVerticalPatchEdges(FabricPatchPattern pattern, BufferedImage canvas, int width,
			int height, int maxExtent) {
		int[] edgeCoords = pattern.getInnerVerticalPatchCoords(width);
		int[] patchWidths = getEdgePartitionSizes(edgeCoords, width);
		Curve2D[] patchEdges = new Curve2D[edgeCoords.length];
		Curve2D normalizedEdge = createNormalizedVerticalPatchEdge();
		for (int i = 0; i < edgeCoords.length; i++) {
			int extent = Math.min(Math.min(patchWidths[i], patchWidths[i + 1]), maxExtent);
			TransformMatrix2D translation = Transformation2D.getTranslationMatrix(edgeCoords[i], 0);
			TransformMatrix2D scale = Transformation2D.getScalingMatrix(extent, height);
			patchEdges[i] = normalizedEdge.transform(scale.postMultiply(translation));
		}
		BandedImageDeformation<VerticalImageBand> deformation = BandedImageDeformation
				.createCurvedVerticalBandedImageDeformation(patchWidths, patchEdges);
		return deformation.deform(canvas);
	}

	private Curve2D createNormalizedVerticalPatchEdge() {
		int n = drawIntegerNumber(6, 10);
		List<Point2D> controlPoints = new Vector<Point2D>(n);
		for (int i = 0; i < n; i++) {
			float x = drawFloatUnitNumber() - 0.5f;
			float y = i / (n - 1f);
			controlPoints.add(new Point2D(x, y));
		}
		return ApproximatingCurve2D.createStandardCurve(controlPoints);
	}

	private BufferedImage applyHorizontalPatchEdges(FabricPatchPattern pattern, BufferedImage canvas, int width,
			int height, int maxExtent) {
		int[] edgeCoords = pattern.getInnerHorizontalPatchCoords(height);
		int[] patchHeights = getEdgePartitionSizes(edgeCoords, height);
		Curve2D[] patchEdges = new Curve2D[edgeCoords.length];
		Curve2D normalizedEdge = createNormalizedHorizontalPatchEdge();
		for (int i = 0; i < edgeCoords.length; i++) {
			int extent = Math.min(Math.min(patchHeights[i], patchHeights[i + 1]), maxExtent);
			TransformMatrix2D translation = Transformation2D.getTranslationMatrix(0, edgeCoords[i]);
			TransformMatrix2D scale = Transformation2D.getScalingMatrix(width, extent);
			patchEdges[i] = normalizedEdge.transform(scale.postMultiply(translation));
		}
		BandedImageDeformation<HorizontalImageBand> deformation = BandedImageDeformation
				.createCurvedHorizontalBandedImageDeformation(patchHeights, patchEdges);
		return deformation.deform(canvas);
	}

	private Curve2D createNormalizedHorizontalPatchEdge() {
		int n = drawIntegerNumber(6, 10);
		List<Point2D> controlPoints = new Vector<Point2D>(n);
		for (int i = 0; i < n; i++) {
			float x = i / (n - 1f);
			float y = drawFloatUnitNumber() - 0.5f;
			controlPoints.add(new Point2D(x, y));
		}
		return ApproximatingCurve2D.createStandardCurve(controlPoints);
	}

	private int[] getEdgePartitionSizes(int[] edgeCoords, int totalSize) {
		int[] sizes = new int[edgeCoords.length + 1];
		sizes[0] = edgeCoords[0];
		for (int i = 1; i < edgeCoords.length; i++) {
			sizes[i] = edgeCoords[i] - edgeCoords[i - 1];
		}
		sizes[edgeCoords.length] = totalSize - edgeCoords[edgeCoords.length - 1];
		return sizes;
	}

	private FabricTexture getFabricTexture() {
		return fabricTexture;
	}

	private List<FabricPatchPatternGenerator> getPatternGenerators() {
		return patternGenerators;
	}

	public boolean isConceptMode() {
		return conceptMode;
	}

	public void setConceptMode(boolean conceptMode) {
		this.conceptMode = conceptMode;
	}

	private static class FabricTexture {

		private BufferedImage textureImage;

		private float[][] alphaFactors;

		public FabricTexture(BufferedImage textureImage) {
			this.textureImage = textureImage;
			initData();
		}

		private void initData() {
			BufferedImage image = getTextureImage();
			int width = getWidth();
			int height = getHeight();
			alphaFactors = new float[width][height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					alphaFactors[x][y] = (image.getRGB(x, y) & 0xff) / 255f;
				}
			}
		}

		public int texturizeRGB(int colorAlpha, int colorRgb, int x, int y) {
			return (Math.round(colorAlpha * alphaFactors[x][y]) << 24) | colorRgb;
		}

		public int getWidth() {
			return ImageUtils.getWidth(getTextureImage());
		}

		public int getHeight() {
			return ImageUtils.getHeight(getTextureImage());
		}

		public BufferedImage getTextureImage() {
			return textureImage;
		}

	}

}