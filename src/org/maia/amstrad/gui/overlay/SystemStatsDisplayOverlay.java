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

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcPerformanceListener;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.graphics2d.image.pool.ImagePool;
import org.maia.util.SystemUtils;

import com.sun.management.OperatingSystemMXBean;

public class SystemStatsDisplayOverlay extends AbstractDisplayOverlay implements AmstradPcPerformanceListener {

	private int fps; // frames painted per second

	private int ips; // image updates per second

	private double cpuLaggingRatio;

	private double cpuThrottlingRatio;

	private boolean turbo;

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
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().getMonitor().isShowSystemStats() && !offscreenImage) {
			synchronized (lines) {
				drawStatLines(produceStatLines(), displayView, displayBounds, monitorInsets, graphicsContext);
			}
		}
	}

	private void drawStatLines(List<String> lines, AmstradDisplayView displayView, Rectangle displayBounds,
			Insets monitorInsets, AmstradGraphicsContext graphicsContext) {
		if (lines.isEmpty())
			return;
		// Box
		Font font = getFont(graphicsContext);
		FontMetrics fm = displayView.getFontMetrics(font);
		int[] lineWidths = computeLineWidths(lines, fm);
		int boxWidth = computeBoxWidth(lineWidths);
		int lineHeight = fm.getHeight();
		int boxHeight = lines.size() * lineHeight + 10;
		int xcenter = displayBounds.x + displayBounds.width / 2;
		int xleft = xcenter - boxWidth / 2;
		int ytop = displayBounds.y + Math.min(monitorInsets.top, displayBounds.height / 17) - 4;
		Graphics2D g = displayView.createDisplayViewport(xleft, ytop, boxWidth, boxHeight);
		g.setFont(font);
		g.setColor(BOX_COLOR);
		g.fillRect(0, 0, boxWidth, boxHeight);
		// Lines
		g.setColor(LINE_COLOR);
		int y = 5 + fm.getAscent();
		for (int i = 0; i < lines.size(); i++) {
			int x = (boxWidth - lineWidths[i]) / 2;
			g.drawString(lines.get(i), x, y);
			y += lineHeight;
		}
		g.dispose();
	}

	private int[] computeLineWidths(List<String> lines, FontMetrics fm) {
		int[] widths = new int[lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			widths[i] = fm.stringWidth(lines.get(i));
		}
		return widths;
	}

	private int computeBoxWidth(int[] lineWidths) {
		int width = 0;
		for (int i = 0; i < lineWidths.length; i++) {
			width = Math.max(width, lineWidths[i]);
		}
		return (width / 16 + 2) * 16;
	}

	private List<String> produceStatLines() {
		long jTotal = SystemUtils.getTotalMemoryInBytes();
		long jUsed = SystemUtils.getUsedMemoryInBytes();
		long jMax = SystemUtils.getMaxMemoryInBytes();
		BasicRuntime brt = getAmstracPc().getBasicRuntime();
		long bTotal = brt.getTotalMemory();
		long bUsed = brt.getUsedMemory();
		ImagePool iPool = AmstradFactory.getInstance().getAmstradContext().getSharedImagePool();
		lines.clear();
		lines.add("MEM Basic: " + formatMemorySize(bUsed) + " used of " + formatMemorySize(bTotal));
		lines.add("MEM Java: " + formatMemorySize(jUsed) + " used of " + formatMemorySize(jTotal)
				+ (jMax < Long.MAX_VALUE ? " (max " + formatMemorySize(jMax) + ")" : ""));
		lines.add("IMG: " + iPool.getSize() + " cached (max " + iPool.getCapacity() + ")");
		lines.add("CPU: " + percentageFormat.format(getCpuLoad()) + (turbo ? " TURBO" : "") + " lag "
				+ percentageFormat.format(cpuLaggingRatio) + " throttle "
				+ percentageFormat.format(cpuThrottlingRatio));
		lines.add("FPS: " + fps + " IPS: " + ips);
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
			int imagesUpdated) {
		double tu = 1000.0 / (double) timeIntervalMillis;
		fps = (int) Math.round(framesPainted * tu);
		ips = (int) Math.round(imagesUpdated * tu);
	}

	@Override
	public void processorPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int timerSyncs,
			int laggingSyncs, int throttledSyncs) {
		cpuLaggingRatio = laggingSyncs / (double) timerSyncs;
		cpuThrottlingRatio = throttledSyncs / (double) timerSyncs;
	}

	@Override
	public void turboModeChanged(AmstradPc amstradPc) {
		turbo = amstradPc.isTurboMode();
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