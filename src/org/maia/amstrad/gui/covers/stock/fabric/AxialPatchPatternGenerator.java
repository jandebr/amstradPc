package org.maia.amstrad.gui.covers.stock.fabric;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.maia.graphics2d.function.PiecewiseLinearFunction2D;
import org.maia.graphics2d.function.ProbabilityDensityFunction2D;
import org.maia.graphics2d.geometry.ClosedLineSegment2D;
import org.maia.graphics2d.geometry.Line2D;
import org.maia.graphics2d.geometry.Path2D;
import org.maia.graphics2d.geometry.Path2D.OrthogonalPathDistribution;
import org.maia.graphics2d.geometry.Path2D.PointAlongPath;
import org.maia.graphics2d.geometry.Path2D.PointAroundPath;
import org.maia.graphics2d.geometry.Point2D;
import org.maia.graphics2d.geometry.PolyLine2D;
import org.maia.graphics2d.geometry.Radians;
import org.maia.graphics2d.geometry.Rectangle2D;
import org.maia.graphics2d.geometry.Vector2D;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public class AxialPatchPatternGenerator extends FabricPatchPatternGenerator {

	private float patchBaseColorMinSaturation = 0.5f; // relative unit

	private float patchBaseColorMaxSaturation = 0.8f; // relative unit

	private float patchBaseColorMinBrightness = 0.3f; // relative unit

	private float patchBaseColorMaxBrightness = 0.5f; // relative unit

	private float patchColorMinVariation = 0.4f; // relative unit

	private float patchColorMaxVariation = 0.6f; // relative unit

	private float axisAnchorMaxDeviationFromCenterX = 0.3f; // relative unit

	private float axisAnchorMaxDeviationFromCenterY = 0.3f; // relative unit

	private float axisMinAngle = 35f; // degrees

	private float axisMaxAngle = 80f; // degrees

	private float axisProtrusion = 0.2f; // relative unit

	private int axisFunctionVertices = 3; // at least 2

	private float axisFunctionVertexMaxDeviation = 0.5f; // relative unit

	private float axisFunctionMinLevel = 0.05f; // relative unit

	private float axisFunctionMaxLevel = 1.0f; // relative unit

	private float axisFunctionLowerThreshold = 0.2f; // function should drop below this value

	private float axisFunctionUpperThreshold = 0.8f; // function should rise above this value

	private float patchMaxEdgeRatio = 2.0f;

	private float patchMinSizeAtMinLevel = 0.5f; // relative unit

	private float patchMaxSizeAtMinLevel = 1.0f; // relative unit

	private float patchMinSizeAtMaxLevel = 0.01f; // relative unit

	private float patchMaxSizeAtMaxLevel = 0.05f; // relative unit

	private float patchOutlierMinOutsideness = 0.4f; // relative unit

	private float patchOutlierMaxOutsideness = 0.8f; // relative unit

	private int patchMinCount = 20;

	private int patchMaxCount = 28;

	private int patchMinOutlierCount = 0;

	private int patchMaxOutlierCount = 3;

	public AxialPatchPatternGenerator() {
		this(new Randomizer());
	}

	public AxialPatchPatternGenerator(Randomizer randomizer) {
		super(randomizer);
	}

	@Override
	public FabricPatchPattern generatePattern(int width, int height, FabricHints hints) {
		FabricPatchPattern pattern = new FabricPatchPattern(hints.getBackgroundColor(Color.BLACK));
		Color patchBaseColor = drawPatchBaseColor();
		float patchColorVariation = patchColorMinVariation
				+ (patchColorMaxVariation - patchColorMinVariation) * drawFloatUnitNumber();
		ClosedLineSegment2D axis = drawAxis(width, height);
		Path2D axisPath = createAxisPath(axis);
		ProbabilityDensityFunction2D axisFunction = drawAxisFunction();
		double absoluteSizeFactor = Math.sqrt(width * width + height * height) / 4.0;
		int nrPatches = drawIntegerNumber(patchMinCount, patchMaxCount);
		int nrOutlierPatches = drawIntegerNumber(patchMinOutlierCount, patchMaxOutlierCount);
		for (int i = 0; i < nrPatches; i++) {
			pattern.addPatch(drawPatch(axisPath, axisFunction, width, height, absoluteSizeFactor, patchBaseColor,
					patchColorVariation));
		}
		for (int i = 0; i < nrOutlierPatches; i++) {
			float outsideness = patchOutlierMinOutsideness
					+ (patchOutlierMaxOutsideness - patchOutlierMinOutsideness) * drawFloatUnitNumber();
			pattern.addPatch(drawPatch(axisPath, axisFunction, width, height, absoluteSizeFactor, patchBaseColor,
					patchColorVariation, outsideness));
		}
		return pattern;
	}

	protected Color drawPatchBaseColor() {
		float hue = drawFloatUnitNumber();
		float saturation = patchBaseColorMinSaturation
				+ (patchBaseColorMaxSaturation - patchBaseColorMinSaturation) * drawFloatUnitNumber();
		float brightness = patchBaseColorMinBrightness
				+ (patchBaseColorMaxBrightness - patchBaseColorMinBrightness) * drawFloatUnitNumber();
		return Color.getHSBColor(hue, saturation, brightness);
	}

	private ClosedLineSegment2D drawAxis(int width, int height) {
		PolyLine2D outline = createCenteredOutline(width, height);
		Set<Point2D> outlineIntersections;
		float anchorX = (drawFloatUnitNumber() * 2f - 1f) * axisAnchorMaxDeviationFromCenterX * width / 2f;
		float anchorY = (drawFloatUnitNumber() * 2f - 1f) * axisAnchorMaxDeviationFromCenterY * height / 2f;
		Point2D anchor = new Point2D(anchorX, anchorY);
		do {
			double angle = Radians
					.degreesToRadians(axisMinAngle + drawFloatUnitNumber() * (axisMaxAngle - axisMinAngle));
			if (drawBoolean())
				angle = Math.PI - angle;
			Point2D p = anchor.plus(new Vector2D(Math.cos(angle), Math.sin(angle)));
			outlineIntersections = outline.intersectAll(new Line2D(anchor, p));
		} while (outlineIntersections.size() != 2);
		Iterator<Point2D> it = outlineIntersections.iterator();
		Vector2D v1 = it.next().minus(anchor);
		Vector2D v2 = it.next().minus(anchor);
		v1.scale(1.0 + axisProtrusion);
		v2.scale(1.0 + axisProtrusion);
		return new ClosedLineSegment2D(anchor.plus(v1), anchor.plus(v2));
	}

	private PolyLine2D createCenteredOutline(int width, int height) {
		double w2 = width / 2.0;
		double h2 = height / 2.0;
		List<Point2D> vertices = new Vector<Point2D>(5);
		vertices.add(new Point2D(-w2, h2));
		vertices.add(new Point2D(w2, h2));
		vertices.add(new Point2D(w2, -h2));
		vertices.add(new Point2D(-w2, -h2));
		vertices.add(vertices.get(0));
		return new PolyLine2D(vertices);
	}

	private Path2D createAxisPath(ClosedLineSegment2D axis) {
		Path2D path = new Path2D();
		path.addPoint(axis.getP1(), 0);
		path.addPoint(axis.getP2(), 1.0);
		return path;
	}

	private ProbabilityDensityFunction2D drawAxisFunction() {
		PiecewiseLinearFunction2D function;
		double segmentSize = 1.0 / (axisFunctionVertices - 1.0);
		do {
			function = new PiecewiseLinearFunction2D();
			for (int i = 0; i < axisFunctionVertices; i++) {
				double r = i * segmentSize;
				if (i > 0 && i < axisFunctionVertices - 1) {
					r += (drawFloatUnitNumber() * 2f - 1f) * axisFunctionVertexMaxDeviation * segmentSize / 2.0;
				}
				double level = axisFunctionMinLevel
						+ (axisFunctionMaxLevel - axisFunctionMinLevel) * drawDoubleUnitNumber();
				function.addPoint(r, level);
			}
		} while (!isValidAxisFunction(function));
		return function;
	}

	private boolean isValidAxisFunction(PiecewiseLinearFunction2D axisFunction) {
		Rectangle2D bounds = axisFunction.getBounds();
		if (bounds.getBottom() > axisFunctionLowerThreshold)
			return false;
		if (bounds.getTop() < axisFunctionUpperThreshold)
			return false;
		return true;
	}

	private FabricPatch drawPatch(Path2D axisPath, ProbabilityDensityFunction2D axisFunction, int width, int height,
			double absoluteSizeFactor, Color patchBaseColor, float patchColorVariation) {
		return drawPatch(axisPath, axisFunction, width, height, absoluteSizeFactor, patchBaseColor, patchColorVariation,
				0f);
	}

	private FabricPatch drawPatch(Path2D axisPath, ProbabilityDensityFunction2D axisFunction, int width, int height,
			double absoluteSizeFactor, Color patchBaseColor, float patchColorVariation, float outsideness) {
		Rectangle bounds;
		float colorAdjustment;
		do {
			double r = drawDoubleUnitNumber();
			double maxLevel = axisFunction.evaluate(r);
			double level = outsideness + (1.0 - outsideness) * drawDoubleUnitNumber() * maxLevel;
			colorAdjustment = patchColorVariation * (float) level;
			PointAroundPath centroid = axisPath.sample(r, new OrthogonalPathDistribution() {

				@Override
				public double sample(PointAlongPath point) {
					return level * absoluteSizeFactor * (drawBoolean() ? 1.0 : -1.0);
				}
			});
			int xc = toCanvasCoordinateX(centroid.getPosition().getX(), width);
			int yc = toCanvasCoordinateY(centroid.getPosition().getY(), height);
			double ratio = Math.pow(patchMaxEdgeRatio, drawDoubleUnitNumber() * 2.0 - 1.0);
			int patchWidth = drawPatchSize(level, absoluteSizeFactor);
			int patchHeight = (int) Math.round(patchWidth / ratio);
			bounds = new Rectangle(xc - patchWidth / 2, yc - patchHeight / 2, patchWidth, patchHeight)
					.intersection(new Rectangle(width, height));
		} while (bounds.isEmpty());
		Color color = ColorUtils.adjustBrightness(patchBaseColor, colorAdjustment);
		return new FabricPatch(color, bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private int drawPatchSize(double level, double absoluteSizeFactor) {
		double minSize = (1.0 - level) * patchMinSizeAtMinLevel + level * patchMinSizeAtMaxLevel;
		double maxSize = (1.0 - level) * patchMaxSizeAtMinLevel + level * patchMaxSizeAtMaxLevel;
		double relativeSize = minSize + (maxSize - minSize) * drawDoubleUnitNumber();
		return (int) Math.round(relativeSize * absoluteSizeFactor);
	}

	private int toCanvasCoordinateX(double x, int width) {
		return (int) Math.round(x + width / 2.0);
	}

	private int toCanvasCoordinateY(double y, int height) {
		return height - (int) Math.round(y + height / 2.0);
	}

}