package org.maia.amstrad.pc.monitor.display;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import org.maia.amstrad.pc.AmstradPc;

public class AmstradSystemColorsDisplaySource extends AmstradWindowDisplaySource {

	private static Insets gridInsets = new Insets(40, 40, 4, 40);

	private static Insets cellInsets = new Insets(4, 4, 4, 4);

	public AmstradSystemColorsDisplaySource(AmstradPc amstradPc) {
		super(amstradPc, "Amstrad Colors");
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		getAmstradPc().getMonitor().setMonitorBilinearEffect(false);
		getAmstradPc().getMonitor().setMonitorScanLinesEffect(false);
		canvas.border(1).paper(1);
	}

	@Override
	protected void renderWindowContent(AmstradDisplayCanvas canvas) {
		renderGrid(canvas, canvas.getGraphicsContext().getSystemColors(), 6);
	}

	private void renderGrid(AmstradDisplayCanvas canvas, AmstradSystemColors systemColors, int cellsPerRow) {
		int n = systemColors.getNumberOfColors();
		int rows = 1 + (n - 1) / cellsPerRow;
		double cellWidth = (canvas.getWidth() - gridInsets.left - gridInsets.right) / (double) cellsPerRow;
		double cellHeight = (canvas.getHeight() - gridInsets.top - gridInsets.bottom) / (double) rows;
		Dimension cellSize = new Dimension((int) Math.round(cellWidth), (int) Math.round(cellHeight));
		for (int i = 0; i < n; i++) {
			int rowi = i / cellsPerRow;
			int coli = i % cellsPerRow;
			int y0 = (int) Math.round(canvas.getHeight() - gridInsets.top - (rowi + 1) * cellHeight);
			int x0 = (int) Math.round(gridInsets.left + coli * cellWidth);
			canvas.origin(x0, y0);
			renderCell(canvas, cellSize, i);
		}
		canvas.origin();
	}

	private void renderCell(AmstradDisplayCanvas canvas, Dimension cellSize, int cellColorIndex) {
		int rectWidth = cellSize.width - cellInsets.left - cellInsets.right;
		int rectHeight = cellSize.height - cellInsets.top - cellInsets.bottom;
		Rectangle rect = new Rectangle(cellInsets.left, cellSize.height - cellInsets.top, rectWidth, rectHeight);
		canvas.pen(cellColorIndex).fillRect(rect);
		canvas.pen(0).drawRect(rect);
		renderCellLabel(canvas, cellSize, String.valueOf(cellColorIndex));
	}

	private void renderCellLabel(AmstradDisplayCanvas canvas, Dimension cellSize, String label) {
		Dimension labelSize = canvas.getTextAreaSizeOnCanvas(label.length(), 1);
		int x = (cellSize.width - labelSize.width) / 2;
		int y = (cellSize.height + labelSize.height) / 2;
		canvas.pen(0).move(x + 2, y - 2).drawStrMonospaced(label);
		canvas.pen(26).move(x, y).drawStrMonospaced(label);
	}

}