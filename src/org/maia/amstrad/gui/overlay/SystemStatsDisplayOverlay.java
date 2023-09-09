package org.maia.amstrad.gui.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcPerformanceListener;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

import com.sun.management.OperatingSystemMXBean;

public class SystemStatsDisplayOverlay extends AbstractDisplayOverlay implements AmstradPcPerformanceListener {

	private int fps;

	private int fpsSkipped;

	private double cpuLaggingRatio;

	private double cpuThrottlingRatio;

	private List<String> lines;

	private OperatingSystemMXBean osBean;

	private Font font;

	private static NumberFormat percentageFormat = NumberFormat.getPercentInstance();

	private static Color BOX_COLOR = new Color(0, 0, 0, 100);

	private static Color LINE_COLOR = Color.WHITE;

	public SystemStatsDisplayOverlay(AmstradPc amstradPc) {
		super(amstradPc);
		amstradPc.addPerformanceListener(this);
		this.lines = new Vector<String>();
		this.osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().getMonitor().isShowSystemStats() && !offscreenImage) {
			drawStatLines(produceStatLines(), display, displayBounds, monitorInsets, graphicsContext);
		}
	}

	private void drawStatLines(List<String> lines, Graphics2D display, Rectangle displayBounds, Insets monitorInsets,
			AmstradGraphicsContext graphicsContext) {
		if (lines.isEmpty())
			return;
		display.setFont(getFont(graphicsContext));
		FontMetrics fm = display.getFontMetrics();
		// Box
		int lineHeight = fm.getHeight();
		int boxHeight = lines.size() * lineHeight;
		int boxWidth = computeBoxWidth(lines, fm);
		int xcenter = displayBounds.x + displayBounds.width / 2;
		int ytop = displayBounds.y + Math.min(monitorInsets.top, displayBounds.height / 17);
		display.setColor(BOX_COLOR);
		display.fillRect(xcenter - boxWidth / 2, ytop - 4, boxWidth, boxHeight + 6);
		// Lines
		display.setColor(LINE_COLOR);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			int xleft = xcenter - fm.stringWidth(line) / 2;
			int ybase = ytop + fm.getAscent() + i * lineHeight;
			display.drawString(line, xleft, ybase);
		}
	}

	private int computeBoxWidth(List<String> lines, FontMetrics fm) {
		int width = 0;
		for (String line : lines) {
			width = Math.max(width, fm.stringWidth(line));
		}
		return (width / 16 + 2) * 16;
	}

	private List<String> produceStatLines() {
		Runtime jrt = Runtime.getRuntime();
		long jTotal = jrt.totalMemory();
		long jUsed = jTotal - jrt.freeMemory();
		long jMax = jrt.maxMemory();
		BasicRuntime brt = getAmstracPc().getBasicRuntime();
		long bTotal = brt.getTotalMemory();
		long bUsed = brt.getUsedMemory();
		lines.clear();
		lines.add("MEM Basic: " + formatMemorySize(bUsed) + " used of " + formatMemorySize(bTotal));
		lines.add("MEM Java: " + formatMemorySize(jUsed) + " used of " + formatMemorySize(jTotal)
				+ (jMax < Long.MAX_VALUE ? " (max " + formatMemorySize(jMax) + ")" : ""));
		lines.add("CPU: " + percentageFormat.format(getCpuLoad()) + " lag " + percentageFormat.format(cpuLaggingRatio)
				+ " throttle " + percentageFormat.format(cpuThrottlingRatio));
		lines.add("FPS: " + fps + " with " + fpsSkipped + " skipped");
		return lines;
	}

	private String formatMemorySize(long bytes) {
		if (bytes < 1024L) {
			return String.valueOf(bytes) + "B";
		} else if (bytes < 1024L * 1024L) {
			return String.valueOf(bytes / 1024L) + "K";
		} else {
			return String.valueOf(bytes / (1024L * 1024L)) + "M";
		}
	}

	@Override
	public void displayPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int framesPainted,
			int framesSkipped) {
		double tu = 1000.0 / (double) timeIntervalMillis;
		fps = (int) Math.round(framesPainted * tu);
		fpsSkipped = (int) Math.round(framesSkipped * tu);
	}

	@Override
	public void processorPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int timerSyncs,
			int laggingSyncs, int throttledSyncs) {
		cpuLaggingRatio = laggingSyncs / (double) timerSyncs;
		cpuThrottlingRatio = throttledSyncs / (double) timerSyncs;
	}

	private double getCpuLoad() {
		return Math.max(0, osBean.getSystemCpuLoad());
	}

	private Font getFont(AmstradGraphicsContext graphicsContext) {
		if (font == null) {
			font = graphicsContext.getSystemFont().deriveFont(8f);
		}
		return font;
	}

}