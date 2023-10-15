package jemu.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import jemu.settings.Settings;

/**
 * Optimized rendering of a <code>Display</code> using a full-scale <em>canvas</em> image that is manipulated on a
 * per-pixel basis
 * 
 * <p>
 * Unlike <code>DisplayStagedRenderDelegate</code>, no compromise is made to the rendering precision, featuring an
 * accurate representation of the bilinear effect and scanlines, for example
 * </p>
 * <p>
 * This strategy employs several performance optimizations
 * <ul>
 * <li>All rendering is done in a separate thread. As a result, the <code>Computer</code> thread is not hold up by any
 * (slower) rendering</li>
 * <li>Detection of areas in the <em>primary display source</em> and the <em>custom display overlay</em> that are
 * constant between renderings</li>
 * <li>Pre-scaling the monitor mask image for efficient masking</li>
 * <li>Pre-computed bilinear interpolation pixel weights</li>
 * <li>Pre-computed alpha compositing</li>
 * </ul>
 * </p>
 * <p>
 * Whether this rendering strategy is actually faster depends on the native runtime and hardware. In particular, this
 * strategy is very CPU intensive and recommended only on faster, multi-core CPUs
 * </p>
 * <p>
 * Note that this rendering strategy requires more heap-allocated memory (depending on <code>Display</code> resolution)
 * because of cached images and data structures
 * </p>
 */
public class DisplayCanvasRenderDelegate extends DisplayRenderDelegate implements DisplayPerformanceListener {

	public static final String NAME = "Canvas";

	private boolean logEnabled; // from Settings

	private boolean logVerboseEnabled = false;

	private DisplayState displayState;

	private TransitState transitState;

	private PaintState paintState;

	private CanvasState canvasState;

	private BufferedImage imageToPaint;

	private RenderThread renderThread;

	private Semaphore transitSemaphore;

	private Semaphore paintSemaphore;

	private long scheduledFullCanvasUpdateTimestamp; // after this time there should be a full canvas repaint

	private static int GRID_CELL_MIN_SIZE_BITS = 3; // size 8 by 8

	private static int GRID_CELL_MAX_SIZE_BITS = 6; // size 64 by 64

	private static final int ALPHA_MASK = 0xff000000;

	private static final int RED_MASK = 0xff0000;

	private static final int GREEN_MASK = 0xff00;

	private static final int BLUE_MASK = 0xff;

	private static final int[] RGB_MASKS = new int[] { RED_MASK, GREEN_MASK, BLUE_MASK };

	private static final int[] RGBA_MASKS = new int[] { RED_MASK, GREEN_MASK, BLUE_MASK, ALPHA_MASK };

	private static final ColorModel RGB_COLOR_MODEL = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1],
			RGB_MASKS[2]);

	private static final ColorModel RGBA_COLOR_MODEL = new DirectColorModel(32, RGBA_MASKS[0], RGBA_MASKS[1],
			RGBA_MASKS[2], RGBA_MASKS[3]);

	private static final Color TRANSLUCENT_COLOR = new Color(0x0, true);

	private static int[][][] alphaMap; // base color, overlay color, overlay alpha -> composed color (all [0,255])

	static {
		alphaMap = new int[256][256][256];
		for (int c0 = 0; c0 < 256; c0++) {
			for (int c1 = 0; c1 < 256; c1++) {
				for (int a = 0; a < 256; a++) {
					float au = a / 255f;
					alphaMap[c0][c1][a] = Math.round(c0 * (1f - au) + c1 * au);
				}
			}
		}
	}

	public DisplayCanvasRenderDelegate() {
		super(NAME);
		this.logEnabled = Settings.getBoolean(Settings.DISPLAY_RENDER_CANVAS_LOG, false);
		this.transitSemaphore = new Semaphore();
		this.paintSemaphore = new Semaphore();
	}

	@Override
	public void init(Display display) {
		super.init(display);
		getDisplay().addPerformanceListener(this);
		setDisplayState(DisplayState.createFrom(this));
		Switches.autonomousDisplayRendering = true;
	}

	@Override
	public void dispose() {
		super.dispose();
		getDisplay().removePerformanceListener(this);
		if (getRenderThread() != null) {
			getRenderThread().stopRendering();
		}
	}

	@Override
	public void displayPerformanceUpdate(Display display, long timeIntervalMillis, int framesPainted,
			int imagesUpdated) {
		if (logEnabled) {
			double tu = 1000.0 / (double) timeIntervalMillis;
			int fps = (int) Math.round(framesPainted * tu);
			int ips = (int) Math.round(imagesUpdated * tu);
			log("FPS: " + fps + " IPS: " + ips);
		}
	}

	@Override
	public void displayChangedSize(int width, int height) {
		super.displayChangedSize(width, height);
		synchronized (paintSemaphore) {
			if (getCanvasState() == null || width != getCanvasState().getCanvasWidth()
					|| height != getCanvasState().getCanvasHeight()) {
				setCanvasState(new CanvasState(width, height));
				requestFullCanvasUpdate();
				log("Created canvas " + width + "x" + height);
			}
		}
	}

	@Override
	public void displayImageChangedSize(int imageWidth, int imageHeight) {
		super.displayImageChangedSize(imageWidth, imageHeight);
		synchronized (transitSemaphore) {
			synchronized (paintSemaphore) {
				setTransitState(new TransitState());
				setPaintState(new PaintState());
				requestFullCanvasUpdate();
				startRenderThread();
			}
		}
	}

	private void startRenderThread() {
		if (getRenderThread() == null) {
			int maximumFps = Integer.parseInt(Settings.get(Settings.DISPLAY_RENDER_CANVAS_MAXFPS, "50"));
			int fullCanvasUpdateSecondInterval = Integer
					.parseInt(Settings.get(Settings.DISPLAY_RENDER_CANVAS_KEYFRAME_INTERVAL, "0"));
			RenderThread rt = new RenderThread(maximumFps, fullCanvasUpdateSecondInterval);
			setRenderThread(rt);
			rt.start();
		}
	}

	@Override
	public void displayPixelsReadyForPainting() {
		synchronized (transitSemaphore) {
			getTransitState().update();
		}
	}

	@Override
	public void refreshDisplay() {
		requestFullCanvasUpdate();
	}

	@Override
	public void paintDisplayOnscreen(Graphics g, boolean monitorEffect) {
		Rectangle imageRect = getImageRect();
		if (hasSecondaryDisplaySource()) {
			getSecondaryDisplaySource().renderOntoDisplay((Graphics2D) g, imageRect);
			paintDisplayOverlays(g, false, monitorEffect);
		} else {
			BufferedImage image = getImageToPaint();
			if (image != null) {
				long t0 = System.currentTimeMillis();
				g.drawImage(image, imageRect.x, imageRect.y, getDisplay());
				logVerbose("Painted canvas in " + (System.currentTimeMillis() - t0) + "ms");
			}
		}
	}

	@Override
	public void paintDisplayOffscreen(Graphics g, boolean monitorEffect) {
		// Used ad-hoc for screenshots e.g.
		// The 'monitorEffect' might be different than what is produced for on-screen
		// Solution is to use a one-time-use (classic) delegate
		DisplayRenderDelegate ord = new DisplayClassicRenderDelegate();
		ord.init(getDisplay());
		ord.displayImageChangedSize(getImageWidth(), getImageHeight());
		ord.refreshDisplay();
		ord.paintDisplayOffscreen(g, monitorEffect);
		ord.dispose();
	}

	private void requestFullCanvasUpdate() {
		setScheduledFullCanvasUpdateTimestamp(System.currentTimeMillis());
	}

	private void log(String msg) {
		if (logEnabled)
			System.out.println(msg);
	}

	private void logVerbose(String msg) {
		if (logVerboseEnabled)
			log(msg);
	}

	private int computeGridCellSizeBits(int pixelWidth, int horizontalBlocksHint) {
		int n = (int) Math.ceil(Math.log10(pixelWidth / (double) horizontalBlocksHint) / Math.log10(2.0));
		return Math.max(Math.min(n, GRID_CELL_MAX_SIZE_BITS), GRID_CELL_MIN_SIZE_BITS);
	}

	private DisplayState getDisplayState() {
		return displayState;
	}

	private void setDisplayState(DisplayState state) {
		this.displayState = state;
	}

	private TransitState getTransitState() {
		return transitState;
	}

	private void setTransitState(TransitState state) {
		this.transitState = state;
	}

	private PaintState getPaintState() {
		return paintState;
	}

	private void setPaintState(PaintState state) {
		this.paintState = state;
	}

	private CanvasState getCanvasState() {
		return canvasState;
	}

	private void setCanvasState(CanvasState state) {
		this.canvasState = state;
	}

	private long getScheduledFullCanvasUpdateTimestamp() {
		return scheduledFullCanvasUpdateTimestamp;
	}

	private void setScheduledFullCanvasUpdateTimestamp(long timestamp) {
		this.scheduledFullCanvasUpdateTimestamp = timestamp;
	}

	private BufferedImage getImageToPaint() {
		return imageToPaint;
	}

	private void setImageToPaint(BufferedImage image) {
		this.imageToPaint = image;
	}

	private RenderThread getRenderThread() {
		return renderThread;
	}

	private void setRenderThread(RenderThread renderThread) {
		this.renderThread = renderThread;
	}

	private class RenderThread extends Thread {

		private boolean stop;

		private int maximumFps;

		private int fullCanvasUpdateSecondInterval; // disabled when <= 0

		public RenderThread(int maximumFps, int fullCanvasUpdateSecondInterval) {
			super("DisplayRenderThread");
			this.maximumFps = maximumFps;
			this.fullCanvasUpdateSecondInterval = fullCanvasUpdateSecondInterval;
			setDaemon(true);
		}

		@Override
		public void run() {
			log("Display render thread started");
			long frameTimeMs = 1000L / getMaximumFps();
			while (!isStopped()) {
				long t0 = System.currentTimeMillis();
				renderFrame();
				long sleepTimeMs = frameTimeMs - (System.currentTimeMillis() - t0);
				if (sleepTimeMs > 0L) {
					try {
						Thread.sleep(sleepTimeMs);
					} catch (InterruptedException e) {
					}
				}
			}
			log("Display render thread stopped");
		}

		private void renderFrame() {
			if (hasSecondaryDisplaySource()) {
				renderSecondaryDisplaySource();
			} else {
				renderPrimaryDisplaySource();
			}
		}

		private void renderSecondaryDisplaySource() {
			setImageToPaint(null);
			repaintDisplay(true);
		}

		private void renderPrimaryDisplaySource() {
			TransitState transitState = null;
			PaintState paintState = null;
			CanvasState canvasState = null;
			synchronized (transitSemaphore) {
				synchronized (paintSemaphore) {
					// get references to a consistent set of states
					transitState = getTransitState();
					paintState = getPaintState();
					canvasState = getCanvasState();
				}
			}
			if (transitState != null && transitState.isUpdated() && paintState != null && canvasState != null) {
				synchronized (transitSemaphore) {
					paintState.update(transitState);
				}
				synchronized (paintSemaphore) {
					boolean fullUpdate = shouldUpdateFullCanvas();
					if (fullUpdate) {
						int fullUpdateSecondInterval = getFullCanvasUpdateSecondInterval();
						setScheduledFullCanvasUpdateTimestamp(fullUpdateSecondInterval > 0
								? System.currentTimeMillis() + fullUpdateSecondInterval * 1000L
								: Long.MAX_VALUE);
						log("Full canvas update");
					}
					canvasState.update(paintState, fullUpdate);
					setImageToPaint(canvasState.getCanvas());
				}
				repaintDisplay(true);
			}
		}

		private boolean shouldUpdateFullCanvas() {
			if (System.currentTimeMillis() >= getScheduledFullCanvasUpdateTimestamp()) {
				return true;
			} else if (getDisplayState().isDifferentFrom(DisplayCanvasRenderDelegate.this)) {
				getDisplayState().update(DisplayCanvasRenderDelegate.this);
				return true;
			} else {
				return false;
			}
		}

		public void stopRendering() {
			stop = true;
		}

		public boolean isStopped() {
			return stop;
		}

		public int getMaximumFps() {
			return maximumFps;
		}

		public int getFullCanvasUpdateSecondInterval() {
			return fullCanvasUpdateSecondInterval;
		}

	}

	private static class DisplayState {

		private String monitorMaskName;

		private boolean monitorEffect;

		private boolean bilinear;

		private boolean verticalScanLines;

		private boolean horizontalScanLines;

		private DisplayState() {
		}

		public static DisplayState createFrom(DisplayRenderDelegate delegate) {
			DisplayState state = new DisplayState();
			state.update(delegate);
			return state;
		}

		public void update(DisplayRenderDelegate delegate) {
			monitorMaskName = deriveMonitorMaskName(delegate);
			monitorEffect = delegate.isMonitorEffectEnabled();
			bilinear = delegate.isBilinearEnabled();
			verticalScanLines = delegate.isPaintScanLinesVertically();
			horizontalScanLines = delegate.isPaintScanLinesHorizontally();
		}

		public boolean isDifferentFrom(DisplayRenderDelegate delegate) {
			if (isMonitorEffect() != delegate.isMonitorEffectEnabled())
				return true;
			if (isBilinear() != delegate.isBilinearEnabled())
				return true;
			if (isVerticalScanLines() != delegate.isPaintScanLinesVertically())
				return true;
			if (isHorizontalScanLines() != delegate.isPaintScanLinesHorizontally())
				return true;
			if (!getMonitorMaskName().equals(deriveMonitorMaskName(delegate)))
				return true;
			return false;
		}

		private String deriveMonitorMaskName(DisplayRenderDelegate delegate) {
			MonitorMask mask = delegate.getMonitorMask();
			if (mask != null) {
				return mask.getName();
			} else {
				return "";
			}
		}

		public String getMonitorMaskName() {
			return monitorMaskName;
		}

		public boolean isMonitorEffect() {
			return monitorEffect;
		}

		public boolean isBilinear() {
			return bilinear;
		}

		public boolean isVerticalScanLines() {
			return verticalScanLines;
		}

		public boolean isHorizontalScanLines() {
			return horizontalScanLines;
		}

	}

	private abstract class PixelsState {

		private int[] pixels;

		private int pixelsWidth;

		private int pixelsHeight;

		private GridMarks gridMarks; // marks cells that need repainting

		protected PixelsState() {
			pixelsWidth = getImageWidth();
			pixelsHeight = getImageHeight();
			if (pixelsWidth <= 0 || pixelsHeight <= 0)
				throw new IllegalStateException("Empty pixels " + pixelsWidth + "x" + pixelsHeight);
			pixels = new int[getDisplayPixels().length];
			gridMarks = new GridMarks(pixelsWidth, pixelsHeight, computeGridCellSizeBits(pixelsWidth, 80));
		}

		public int[] getPixels() {
			return pixels;
		}

		public int getPixelsWidth() {
			return pixelsWidth;
		}

		public int getPixelsHeight() {
			return pixelsHeight;
		}

		public GridMarks getGridMarks() {
			return gridMarks;
		}

	}

	private class TransitState extends PixelsState {

		private boolean updated;

		public TransitState() {
		}

		public void update() {
			GridMarks grid = getGridMarks();
			grid.clear();
			int[] displayPixels = getDisplayPixels();
			int[] transitPixels = this.getPixels();
			int[] paintPixels = getPaintState().getPixels();
			int height = getImageHeight();
			int width = getImageWidth();
			int pi = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (displayPixels[pi] != paintPixels[pi]) {
						grid.markCellAtCoords(x, y);
					}
					transitPixels[pi] = displayPixels[pi];
					pi++;
				}
			}
			updated = true;
		}

		public boolean isUpdated() {
			return updated;
		}

	}

	private class PaintState extends PixelsState {

		public PaintState() {
		}

		public void update(TransitState transitState) {
			// Copy pixels
			int[] transitPixels = transitState.getPixels();
			int[] paintPixels = this.getPixels();
			int pn = paintPixels.length;
			for (int pi = 0; pi < pn; pi++) {
				paintPixels[pi] = transitPixels[pi];
			}
			// Copy grid marks
			getGridMarks().copyFrom(transitState.getGridMarks());
		}

	}

	private class CanvasState {

		private int canvasWidth;

		private int canvasHeight;

		private DataBuffer canvasBuffer;

		private BufferedImage canvas;

		private CanvasProjection canvasProjection;

		private MonitorMaskArtefact monitorMaskArtefact;

		private DisplayOverlayArtefact overlayArtefact;

		private GridMarks gridMarks; // marks changed areas

		private GridMarks previousGridMarks; // marks previously changed areas

		private int[] gridRowsYmin, gridRowsYmax, gridColumnsXmin, gridColumnsXmax;

		private int[] scanArgb = new int[4];

		public CanvasState(int width, int height) {
			if (width <= 0 || height <= 0)
				throw new IllegalStateException("Empty canvas " + width + "x" + height);
			canvasWidth = width;
			canvasHeight = height;
			canvasBuffer = new DataBufferInt(width * height); // can be natively optimized
			WritableRaster raster = Raster.createPackedRaster(canvasBuffer, width, height, width, RGB_MASKS, null);
			canvas = new BufferedImage(RGB_COLOR_MODEL, raster, false, null);
			initGridMarks();
		}

		private void initGridMarks() {
			int cellSizeBits = computeGridCellSizeBits(canvasWidth, 40);
			gridMarks = new GridMarks(canvasWidth, canvasHeight, cellSizeBits);
			previousGridMarks = new GridMarks(canvasWidth, canvasHeight, cellSizeBits);
			int rows = gridMarks.getRowCount();
			int columns = gridMarks.getColumnCount();
			gridRowsYmin = new int[rows];
			gridRowsYmax = new int[rows];
			gridColumnsXmin = new int[columns];
			gridColumnsXmax = new int[columns];
			for (int i = 0; i < rows; i++) {
				gridRowsYmin[i] = gridMarks.getMinimumYofRowIndex(i);
				gridRowsYmax[i] = gridMarks.getMaximumYofRowIndex(i);
			}
			for (int j = 0; j < columns; j++) {
				gridColumnsXmin[j] = gridMarks.getMinimumXofColumnIndex(j);
				gridColumnsXmax[j] = gridMarks.getMaximumXofColumnIndex(j);
			}
		}

		public void update(PaintState paintState, boolean fullUpdate) {
			long t0 = System.currentTimeMillis();
			MonitorMask monitorMask = isMonitorEffectEnabled() ? getMonitorMask() : null;
			updateDisplayOverlay(monitorMask);
			updateCanvas(paintState, monitorMask, fullUpdate);
			logVerbose("Updated canvas in " + (System.currentTimeMillis() - t0) + "ms");
		}

		private void updateDisplayOverlay(MonitorMask monitorMask) {
			DisplayOverlay overlay = getCustomDisplayOverlay();
			GridMarks grid = getGridMarks();
			Graphics2D g2 = getOverlayArtefact().getGraphics2D();
			g2.setBackground(TRANSLUCENT_COLOR);
			g2.clearRect(0, 0, canvasWidth, canvasHeight);
			grid.clear();
			if (overlay != null) {
				DisplayView displayView = new GridMarksDisplayView(g2, grid);
				Rectangle rect = new Rectangle(canvasWidth, canvasHeight);
				overlay.renderOntoDisplay(displayView, rect, monitorMask, false);
			}
		}

		private void updateCanvas(PaintState paintState, MonitorMask monitorMask, boolean fullUpdate) {
			CanvasProjection projection = getCanvasProjection(paintState);
			int[] pixels = paintState.getPixels();
			int[] maskPixels = getMonitorMaskArtefact(monitorMask).getMaskPixels();
			int[] overlayPixels = getOverlayArtefact().getOverlayPixels();
			int[] c2pi = projection.getPixelIndices();
			byte[] c2pw = projection.getPixelWeights();
			boolean bilinear = isBilinearEnabled();
			boolean scanVer = isPaintScanLinesVertically();
			boolean scanHor = isPaintScanLinesHorizontally();
			int scanColor = Display.SCAN.getRGB();
			scanArgb[0] = (scanColor & ALPHA_MASK) >>> 24;
			scanArgb[1] = (scanColor & RED_MASK) >>> 16;
			scanArgb[2] = (scanColor & GREEN_MASK) >>> 8;
			scanArgb[3] = scanColor & BLUE_MASK;
			GridMarks grid = getGridMarks();
			GridMarks previousGrid = getPreviousGridMarks();
			GridMarks pixelsGrid = paintState.getGridMarks();
			int rows = grid.getRowCount();
			int columns = grid.getColumnCount();
			int cellsUpdated = 0;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if (fullUpdate || shouldUpdateCanvasCell(i, j, grid, previousGrid, pixelsGrid, projection)) {
						updateCanvasCell(i, j, pixels, maskPixels, overlayPixels, c2pi, c2pw, bilinear, scanVer,
								scanHor);
						previousGrid.copyFromCell(grid, i, j); // used in the next update
						cellsUpdated++;
					}
				}
			}
			logVerbose("Updated " + cellsUpdated + " out of " + grid.getCellCount() + " cells");
		}

		private boolean shouldUpdateCanvasCell(int row, int column, GridMarks grid, GridMarks previousGrid,
				GridMarks pixelsGrid, CanvasProjection projection) {
			// Overlay
			if (grid.isCellMarkedAtIndex(row, column))
				return true;
			if (previousGrid.isCellMarkedAtIndex(row, column))
				return true;
			// Screen pixels
			int pi1 = projection.getPixelsGridCoverageYmin()[row][column];
			int pi2 = projection.getPixelsGridCoverageYmax()[row][column];
			int pj1 = projection.getPixelsGridCoverageXmin()[row][column];
			int pj2 = projection.getPixelsGridCoverageXmax()[row][column];
			for (int i = pi1; i <= pi2; i++) {
				for (int j = pj1; j <= pj2; j++) {
					if (pixelsGrid.isCellMarkedAtIndex(i, j))
						return true;
				}
			}
			return false;
		}

		private void updateCanvasCell(int row, int column, int[] pixels, int[] maskPixels, int[] overlayPixels,
				int[] c2pi, byte[] c2pw, boolean bilinear, boolean scanVer, boolean scanHor) {
			int sample, s1, s2, s3, s4, w, mask;
			int alpha, r, g, b;
			int ci, k;
			int y1 = gridRowsYmin[row];
			int y2 = gridRowsYmax[row];
			int x1 = gridColumnsXmin[column];
			int x2 = gridColumnsXmax[column];
			int ci0 = y1 * canvasWidth + x1;
			for (int y = y1; y <= y2; y++) {
				ci = ci0;
				for (int x = x1; x <= x2; x++) {
					mask = maskPixels[ci];
					if ((mask & ALPHA_MASK) == ALPHA_MASK) {
						// opaque mask pixel
						canvasBuffer.setElem(0, ci, mask);
					} else {
						// screen
						k = ci << 2;
						if (bilinear) {
							// interpolate 4 neighbouring pixels
							s1 = pixels[c2pi[k]];
							s2 = pixels[c2pi[++k]];
							s3 = pixels[c2pi[++k]];
							s4 = pixels[c2pi[++k]];
							if (s2 == s1 && s3 == s1 && s4 == s1) {
								r = (s1 & RED_MASK) >>> 16;
								g = (s1 & GREEN_MASK) >>> 8;
								b = s1 & BLUE_MASK;
							} else {
								k -= 3;
								w = c2pw[k] & 0xff;
								r = w * ((s1 & RED_MASK) >>> 16);
								g = w * ((s1 & GREEN_MASK) >>> 8);
								b = w * (s1 & BLUE_MASK);
								w = c2pw[++k] & 0xff;
								r += w * ((s2 & RED_MASK) >>> 16);
								g += w * ((s2 & GREEN_MASK) >>> 8);
								b += w * (s2 & BLUE_MASK);
								w = c2pw[++k] & 0xff;
								r += w * ((s3 & RED_MASK) >>> 16);
								g += w * ((s3 & GREEN_MASK) >>> 8);
								b += w * (s3 & BLUE_MASK);
								w = c2pw[++k] & 0xff;
								r += w * ((s4 & RED_MASK) >>> 16);
								g += w * ((s4 & GREEN_MASK) >>> 8);
								b += w * (s4 & BLUE_MASK);
								r >>>= 8;
								g >>>= 8;
								b >>>= 8;
							}
						} else {
							// closest neighbouring pixel
							sample = pixels[c2pi[k]];
							r = (sample & RED_MASK) >>> 16;
							g = (sample & GREEN_MASK) >>> 8;
							b = sample & BLUE_MASK;
						}
						// scanlines
						if ((scanVer && x % 2 == 0) || (scanHor && y % 2 == 0)) {
							alpha = scanArgb[0];
							r = alphaMap[r][scanArgb[1]][alpha];
							g = alphaMap[g][scanArgb[2]][alpha];
							b = alphaMap[b][scanArgb[3]][alpha];
						}
						// custom overlay
						sample = overlayPixels[ci];
						if (sample != 0) {
							alpha = (sample & ALPHA_MASK) >>> 24;
							r = alphaMap[r][(sample & RED_MASK) >>> 16][alpha];
							g = alphaMap[g][(sample & GREEN_MASK) >>> 8][alpha];
							b = alphaMap[b][sample & BLUE_MASK][alpha];
						}
						// monitor mask
						if (mask != 0) {
							alpha = (mask & ALPHA_MASK) >>> 24;
							r = alphaMap[r][(mask & RED_MASK) >>> 16][alpha];
							g = alphaMap[g][(mask & GREEN_MASK) >>> 8][alpha];
							b = alphaMap[b][mask & BLUE_MASK][alpha];
						}
						// compose
						canvasBuffer.setElem(0, ci, r << 16 | g << 8 | b);
					}
					ci++;
				}
				ci0 += canvasWidth;
			}
		}

		private CanvasProjection getCanvasProjection(PaintState paintState) {
			if (canvasProjection == null || !canvasProjection.isCompatibleWith(this, paintState)) {
				canvasProjection = new CanvasProjection(this, paintState);
				logVerbose("Created canvas projection");
			}
			return canvasProjection;
		}

		private MonitorMaskArtefact getMonitorMaskArtefact(MonitorMask monitorMask) {
			if (monitorMaskArtefact == null || !monitorMaskArtefact.isCompatibleWith(this, monitorMask)) {
				monitorMaskArtefact = new MonitorMaskArtefact(this, monitorMask);
				logVerbose("Created monitor mask artefact");
			}
			return monitorMaskArtefact;
		}

		private DisplayOverlayArtefact getOverlayArtefact() {
			if (overlayArtefact == null || !overlayArtefact.isCompatibleWith(this)) {
				overlayArtefact = new DisplayOverlayArtefact(this);
				logVerbose("Created display overlay artefact");
			}
			return overlayArtefact;
		}

		public BufferedImage getCanvas() {
			return canvas;
		}

		public int getCanvasWidth() {
			return canvasWidth;
		}

		public int getCanvasHeight() {
			return canvasHeight;
		}

		public GridMarks getGridMarks() {
			return gridMarks;
		}

		private GridMarks getPreviousGridMarks() {
			return previousGridMarks;
		}

	}

	private static abstract class CacheableArtefact {

		private String cacheKey;

		protected CacheableArtefact(String cacheKey) {
			this.cacheKey = cacheKey;
		}

		protected boolean isEqualCacheKey(String otherCacheKey) {
			return getCacheKey().equals(otherCacheKey);
		}

		private String getCacheKey() {
			return cacheKey;
		}

	}

	private static class CanvasProjection extends CacheableArtefact {

		private byte[] pixelWeights;

		private int[] pixelIndices;

		private int[][] pixelsGridCoverageXmin;

		private int[][] pixelsGridCoverageXmax;

		private int[][] pixelsGridCoverageYmin;

		private int[][] pixelsGridCoverageYmax;

		public CanvasProjection(CanvasState canvasState, PaintState paintState) {
			super(deriveCacheKey(canvasState, paintState));
			initPixelProjection(canvasState, paintState);
			initGridProjection(canvasState, paintState);
		}

		private void initPixelProjection(CanvasState canvasState, PaintState paintState) {
			int canvasWidth = canvasState.getCanvasWidth();
			int canvasHeight = canvasState.getCanvasHeight();
			int pixelsWidth = paintState.getPixelsWidth();
			int pixelsHeight = paintState.getPixelsHeight();
			pixelWeights = new byte[canvasHeight * canvasWidth * 4];
			pixelIndices = new int[canvasHeight * canvasWidth * 4];
			double scaleX = pixelsWidth / (double) canvasWidth;
			double scaleY = pixelsHeight / (double) canvasHeight;
			double py0, py0f, py0w, py1w, px0, px0f, px0w, px1w;
			int py0i, py1i, px0i, px1i;
			int k = 0;
			for (int cy = 0; cy < canvasHeight; cy++) {
				py0 = cy * scaleY;
				py0f = Math.floor(py0);
				py0i = (int) py0f;
				py1i = Math.min(py0i + 1, pixelsHeight - 1);
				py1w = py0 - py0f;
				py0w = 1.0 - py1w;
				for (int cx = 0; cx < canvasWidth; cx++) {
					px0 = cx * scaleX;
					px0f = Math.floor(px0);
					px0i = (int) px0f;
					px1i = Math.min(px0i + 1, pixelsWidth - 1);
					px1w = px0 - px0f;
					px0w = 1.0 - px1w;
					int w00 = (int) Math.round(255.0 * py0w * px0w);
					int w01 = (int) Math.round(255.0 * py0w * px1w);
					int w10 = (int) Math.round(255.0 * py1w * px0w);
					int w11 = 255 - w00 - w01 - w10;
					while (w11 < 0) {
						if (w00 > 0)
							w00--;
						else if (w01 > 0)
							w01--;
						else
							w10--;
						w11++;
					}
					pixelWeights[k] = (byte) w00;
					pixelIndices[k++] = py0i * pixelsWidth + px0i;
					pixelWeights[k] = (byte) w01;
					pixelIndices[k++] = py0i * pixelsWidth + px1i;
					pixelWeights[k] = (byte) w10;
					pixelIndices[k++] = py1i * pixelsWidth + px0i;
					pixelWeights[k] = (byte) w11;
					pixelIndices[k++] = py1i * pixelsWidth + px1i;
				}
			}
		}

		private void initGridProjection(CanvasState canvasState, PaintState paintState) {
			GridMarks canvasGrid = canvasState.getGridMarks();
			int rows = canvasGrid.getRowCount();
			int columns = canvasGrid.getColumnCount();
			pixelsGridCoverageXmin = new int[rows][columns];
			pixelsGridCoverageXmax = new int[rows][columns];
			pixelsGridCoverageYmin = new int[rows][columns];
			pixelsGridCoverageYmax = new int[rows][columns];
			GridMarks pixelsGrid = paintState.getGridMarks();
			int pixelsWidth = pixelsGrid.getWidth();
			int pixelsHeight = pixelsGrid.getHeight();
			double scaleX = pixelsWidth / (double) canvasGrid.getWidth();
			double scaleY = pixelsHeight / (double) canvasGrid.getHeight();
			for (int i = 0; i < rows; i++) {
				int y1 = canvasGrid.getMinimumYofRowIndex(i);
				int y2 = canvasGrid.getMaximumYofRowIndex(i);
				int py1 = (int) Math.floor(y1 * scaleY);
				int py2 = Math.min((int) Math.floor(y2 * scaleY) + 1, pixelsHeight - 1);
				int pi1 = pixelsGrid.mapYcoordToRowIndex(py1);
				int pi2 = pixelsGrid.mapYcoordToRowIndex(py2);
				for (int j = 0; j < columns; j++) {
					int x1 = canvasGrid.getMinimumXofColumnIndex(j);
					int x2 = canvasGrid.getMaximumXofColumnIndex(j);
					int px1 = (int) Math.floor(x1 * scaleX);
					int px2 = Math.min((int) Math.floor(x2 * scaleX) + 1, pixelsWidth - 1);
					int pj1 = pixelsGrid.mapXcoordToColumnIndex(px1);
					int pj2 = pixelsGrid.mapXcoordToColumnIndex(px2);
					pixelsGridCoverageXmin[i][j] = pj1;
					pixelsGridCoverageXmax[i][j] = pj2;
					pixelsGridCoverageYmin[i][j] = pi1;
					pixelsGridCoverageYmax[i][j] = pi2;
				}
			}
		}

		private static String deriveCacheKey(CanvasState canvasState, PaintState paintState) {
			return canvasState.getCanvasWidth() + ":" + canvasState.getCanvasHeight() + ":"
					+ paintState.getPixelsWidth() + ":" + paintState.getPixelsHeight();
		}

		public boolean isCompatibleWith(CanvasState canvasState, PaintState paintState) {
			return isEqualCacheKey(deriveCacheKey(canvasState, paintState));
		}

		public byte[] getPixelWeights() {
			return pixelWeights;
		}

		public int[] getPixelIndices() {
			return pixelIndices;
		}

		public int[][] getPixelsGridCoverageXmin() {
			return pixelsGridCoverageXmin;
		}

		public int[][] getPixelsGridCoverageXmax() {
			return pixelsGridCoverageXmax;
		}

		public int[][] getPixelsGridCoverageYmin() {
			return pixelsGridCoverageYmin;
		}

		public int[][] getPixelsGridCoverageYmax() {
			return pixelsGridCoverageYmax;
		}

	}

	private static class MonitorMaskArtefact extends CacheableArtefact {

		private int[] maskPixels;

		private MonitorMaskArtefact(CanvasState canvasState, MonitorMask monitorMask) {
			super(deriveCacheKey(canvasState, monitorMask));
			initMaskPixels(canvasState, monitorMask);
		}

		private void initMaskPixels(CanvasState canvasState, MonitorMask monitorMask) {
			int width = canvasState.getCanvasWidth();
			int height = canvasState.getCanvasHeight();
			DataBufferInt dataBuffer = new DataBufferInt(width * height);
			if (monitorMask != null) {
				WritableRaster raster = Raster.createPackedRaster(dataBuffer, width, height, width, RGBA_MASKS, null);
				BufferedImage image = new BufferedImage(RGBA_COLOR_MODEL, raster, false, null);
				Graphics2D g2 = image.createGraphics();
				g2.drawImage(monitorMask.getImage(), 0, 0, width, height, null);
				g2.dispose();
			} else {
				// leave dataBuffer fully translucent
			}
			maskPixels = dataBuffer.getData();
		}

		private static String deriveCacheKey(CanvasState canvasState, MonitorMask monitorMask) {
			return canvasState.getCanvasWidth() + ":" + canvasState.getCanvasHeight() + ":"
					+ (monitorMask != null ? monitorMask.getName() : "");
		}

		public boolean isCompatibleWith(CanvasState canvasState, MonitorMask monitorMask) {
			return isEqualCacheKey(deriveCacheKey(canvasState, monitorMask));
		}

		public int[] getMaskPixels() {
			return maskPixels;
		}

	}

	private static class DisplayOverlayArtefact extends CacheableArtefact {

		private Graphics2D graphics2D;

		private int[] overlayPixels;

		private DisplayOverlayArtefact(CanvasState canvasState) {
			super(deriveCacheKey(canvasState));
			initOverlay(canvasState);
		}

		private void initOverlay(CanvasState canvasState) {
			int width = canvasState.getCanvasWidth();
			int height = canvasState.getCanvasHeight();
			DataBufferInt dataBuffer = new DataBufferInt(width * height);
			WritableRaster raster = Raster.createPackedRaster(dataBuffer, width, height, width, RGBA_MASKS, null);
			BufferedImage image = new BufferedImage(RGBA_COLOR_MODEL, raster, false, null);
			graphics2D = image.createGraphics();
			overlayPixels = dataBuffer.getData();
		}

		private static String deriveCacheKey(CanvasState canvasState) {
			return canvasState.getCanvasWidth() + ":" + canvasState.getCanvasHeight();
		}

		public boolean isCompatibleWith(CanvasState canvasState) {
			return isEqualCacheKey(deriveCacheKey(canvasState));
		}

		public Graphics2D getGraphics2D() {
			return graphics2D;
		}

		public int[] getOverlayPixels() {
			return overlayPixels;
		}

	}

	private static class GridMarks {

		private int width;

		private int height;

		private int cellSizeBits;

		private boolean[][] cells;

		public GridMarks(int width, int height, int cellSizeBits) {
			this.width = width;
			this.height = height;
			this.cellSizeBits = cellSizeBits;
			this.cells = new boolean[getRowCount()][getColumnCount()];
		}

		public void clear() {
			int rows = getRowCount();
			int columns = getColumnCount();
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					cells[i][j] = false;
				}
			}
		}

		public void copyFrom(GridMarks otherGrid) {
			int rows = getRowCount();
			int columns = getColumnCount();
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					copyFromCell(otherGrid, i, j);
				}
			}
		}

		public void copyFromCell(GridMarks otherGrid, int row, int column) {
			cells[row][column] = otherGrid.cells[row][column];
		}

		public void markCellsCoveringArea(int x, int y, int width, int height) {
			int i0 = mapYcoordToRowIndex(y);
			int i1 = mapYcoordToRowIndex(y + height - 1);
			int j0 = mapXcoordToColumnIndex(x);
			int j1 = mapXcoordToColumnIndex(x + width - 1);
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					markCellAtIndex(i, j);
				}
			}
		}

		public void markCellAtCoords(int x, int y) {
			markCellAtIndex(mapYcoordToRowIndex(y), mapXcoordToColumnIndex(x));
		}

		public void markCellAtIndex(int row, int column) {
			cells[row][column] = true;
		}

		public boolean isCellMarkedAtCoords(int x, int y) {
			return isCellMarkedAtIndex(mapYcoordToRowIndex(y), mapXcoordToColumnIndex(x));
		}

		public boolean isCellMarkedAtIndex(int row, int column) {
			return cells[row][column];
		}

		public int getNumberOfMarkedCells() {
			int marked = 0;
			int rows = getRowCount();
			int columns = getColumnCount();
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if (isCellMarkedAtIndex(i, j))
						marked++;
				}
			}
			return marked;
		}

		public int mapYcoordToRowIndex(int y) {
			return y >>> cellSizeBits;
		}

		public int mapXcoordToColumnIndex(int x) {
			return x >>> cellSizeBits;
		}

		public int getRowCount() {
			return 1 + ((height - 1) >>> cellSizeBits);
		}

		public int getColumnCount() {
			return 1 + ((width - 1) >>> cellSizeBits);
		}

		public int getCellCount() {
			return getRowCount() * getColumnCount();
		}

		public int getMinimumYofRowIndex(int row) {
			return row * (1 << cellSizeBits);
		}

		public int getMaximumYofRowIndex(int row) {
			return Math.min((row + 1) * (1 << cellSizeBits), height) - 1;
		}

		public int getMinimumXofColumnIndex(int column) {
			return column * (1 << cellSizeBits);
		}

		public int getMaximumXofColumnIndex(int column) {
			return Math.min((column + 1) * (1 << cellSizeBits), width) - 1;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

	}

	private static class GridMarksDisplayView extends GraphicsDisplayView {

		private GridMarks gridMarks; // marks cells that are painted on

		public GridMarksDisplayView(Graphics2D g2, GridMarks gridMarks) {
			super(g2);
			this.gridMarks = gridMarks;
		}

		@Override
		public Graphics2D createDisplayViewport(int x, int y, int width, int height) {
			getGridMarks().markCellsCoveringArea(x, y, width, height);
			return super.createDisplayViewport(x, y, width, height);
		}

		public GridMarks getGridMarks() {
			return gridMarks;
		}

	}

	private static class Semaphore {

		public Semaphore() {
		}

	}

}