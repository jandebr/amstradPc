package jemu.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * The original <code>Display</code> rendering from <em>JEMU</em>, retrofitted to the <code>DisplayRenderDelegate</code>
 * design and extended to support <code>SecondaryDisplaySource</code>
 */
public class DisplayClassicRenderDelegate extends DisplayRenderDelegate {

	private BufferedImage image;
	private WritableRaster raster;

	private float co, cn, cm, cl;
	private int ck, cj, ci, ch = 50;
	private int zoom = 1;

	private int flashkey = 0;

	public static final String NAME = "Classic";

	public DisplayClassicRenderDelegate() {
		this(NAME);
	}

	protected DisplayClassicRenderDelegate(String name) {
		super(name);
	}

	@Override
	public boolean isDoubleBufferingEnabled() {
		return true;
	}

	@Override
	public void displayImageChangedSize(int imageWidth, int imageHeight) {
		super.displayImageChangedSize(imageWidth, imageHeight);
		image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		image.setAccelerationPriority(1);
		raster = image.getRaster();
	}

	@Override
	public void displayPixelsReadyForPainting() {
		updateImage(true);
	}

	@Override
	public void refreshDisplay() {
		updateImage(false);
	}

	private void updateImage(boolean wait) {
		raster.setDataElements(0, 0, getImageWidth(), getImageHeight(), getDisplayPixels());
		repaintDisplay(wait);
	}

	@Override
	public void paintDisplayOnscreen(Graphics g, boolean monitorEffect) {
		paintDisplay(g, false, monitorEffect);
	}

	@Override
	public void paintDisplayOffscreen(Graphics g, boolean monitorEffect) {
		paintDisplay(g, true, monitorEffect);
	}

	protected void paintDisplay(Graphics g, boolean offscreenImage, boolean monitorEffect) {
		Display display = getDisplay();
		Rectangle imageRect = getImageRect();
		if (isBilinearEnabled()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
		if (!jemu.system.cpc.CPC.YM_Play) {
			paintDisplayImage(g);
		} else {
			if (Display.skin == 1)
				g.drawImage(display.YMmode, imageRect.x, imageRect.y, imageRect.width, imageRect.height, null);
			else {
				g.drawImage(display.YMmode2, imageRect.x, imageRect.y, imageRect.width, imageRect.height, null);
			}
			if (!isLowPerformance()) {
				paintBalls(g);
			}
		}
		if (Display.fader >= 24 || Display.fader <= -24) {
			g.setColor(Display.FADE);
			g.fillRect(imageRect.x, imageRect.y, imageRect.width, imageRect.height);
		}
		if (imageRect.height >= 640) {
			zoom = 3;
		} else if (isLargeDisplay()) {
			zoom = 2;
		} else {
			zoom = 1;
		}
		g.setFont(getDisplayFont());
		g.setColor(Color.BLACK);
		if (isPaintScanLines()) {
			paintScanLines(g);
		}
		paintYM(g);
		paintKeys(g);
		paintDisplayOverlays(g, offscreenImage, monitorEffect);
	}

	private void paintDisplayImage(Graphics g) {
		Rectangle imageRect = getImageRect();
		SecondaryDisplaySource secondaryDs = getSecondaryDisplaySource();
		if (secondaryDs != null) {
			secondaryDs.renderOntoDisplay((Graphics2D) g, imageRect);
		} else {
			boolean sameSize = imageRect.width == image.getWidth() && imageRect.height == image.getHeight();
			if (sameSize) {
				g.drawImage(image, imageRect.x, imageRect.y, null);
			} else {
				g.drawImage(image, imageRect.x, imageRect.y, imageRect.width, imageRect.height, null);
			}
		}
	}

	private void paintBalls(Graphics g) {
		Display display = getDisplay();
		for (ck = 0; ck < ch; ck++) {
			cj = ((((int) ((Math.cos((double) co + (double) ((float) ck / 50F) * 12.566370964050293D) * Math.sin(cl)
					* 20D + Math.cos((double) (2.0F * cn) / 2D - (double) (((float) ck / 50F) * 6.28F)) * 40D)
					- Math.sin((double) (cn / 2.0F + cm) + (double) ((float) ck / 50F) * 6.2831850051879883D) * 60D)
					+ 160) - 7) + 40) * zoom;
			ci = (((int) ((Math.cos((double) (cn * 1.5F) + (double) ((float) ck / 50F) * 6.2831850051879883D)
					* Math.sin(cm) * 20D
					- Math.sin((double) (4F * co) / 3D + (double) (((float) ck / 50F) * 6.28F * 2.0F)) * 40D)
					+ Math.sin((double) (co / 2.0F + cl) + (double) ((float) ck / 50F) * 6.2831850051879883D) * 30D)
					+ 100) - 7) * zoom;
			if (zoom >= 3)
				g.drawImage(display.ballbb, cj, ci, null);
			else if (zoom >= 2)
				g.drawImage(display.ballb, cj, ci, null);
			else
				g.drawImage(display.ball, cj, ci, null);
		}
		co += 0.029999999329447746D;
		cn += 0.021900000050663948D;
		cm += 0.027300000190734863D;
		cl += 0.030899999663233757D;
	}

	private void paintScanLines(Graphics g) {
		g.setColor(Display.SCAN);
		Rectangle imageRect = getImageRect();
		if (isPaintScanLinesVertically()) {
			// vertical lines
			for (int i = 0; i < imageRect.width; i = i + 2) {
				g.drawLine(imageRect.x + i, imageRect.y, imageRect.x + i, imageRect.y + imageRect.height);
			}
		} else if (isPaintScanLinesHorizontally()) {
			// horizontal lines
			for (int i = 0; i < imageRect.height; i = i + 2) {
				g.drawLine(imageRect.x, imageRect.y + i, imageRect.x + imageRect.width, imageRect.y + i);
			}
		}
	}

	private void paintYM(Graphics g) {
		Display display = getDisplay();
		if (jemu.system.cpc.CPC.YM_Play) {
			g.drawImage(display.ymplay, 10, 12, display);
			if (Display.title.length() != 1) {
				if (!isLowPerformance()) {
					g.setColor(Display.TRANSBLACK);
					g.drawString("Title: " + Display.title, 36, 20);
					g.drawString("Autor: " + Display.author, 36, 34);
					g.drawString("Creator: " + Display.creator, 36, 48);
				}
				g.setColor(Display.TRANS);
				g.drawString("Title: " + Display.title, 34, 18);
				g.drawString("Autor: " + Display.author, 34, 32);
				g.drawString("Creator: " + Display.creator, 34, 46);
			}
			g.setColor(Display.TRANSBLACK);
			if (jemu.system.cpc.CPC.atari_st_mode && !jemu.system.cpc.CPC.oldYM) {
				if (!isLowPerformance())
					g.drawString("playing 2 MHz Atari ST file...", 36, 62);
				g.setColor(Display.TRANS);
				g.drawString("playing 2 MHz Atari ST file...", 34, 60);
			} else if (jemu.system.cpc.CPC.spectrum_mode && !jemu.system.cpc.CPC.oldYM) {
				if (!isLowPerformance())
					g.drawString("playing 1,77 MHz ZX Spectrum file...", 36, 62);
				g.setColor(Display.TRANS);
				g.drawString("playing 1,77 MHz ZX Spectrum file...", 34, 60);
			} else if (jemu.system.cpc.CPC.atari_st_mode && jemu.system.cpc.CPC.oldYM) {
				if (!isLowPerformance())
					g.drawString("playing old YM3 file...", 36, 62);
				g.setColor(Display.TRANS);
				g.drawString("playing old YM3 file...", 34, 60);
			} else {
				if (!isLowPerformance())
					g.drawString("playing 1 MHz Amstrad CPC file...", 36, 62);
				g.setColor(Display.TRANS);
				g.drawString("playing 1 MHz Amstrad CPC file...", 34, 60);
			}
			g.setFont(getDisplayFont());
			if (!isLowPerformance()) {
				g.setColor(Display.TRANSBLACK);
				g.drawString(jemu.core.device.sound.YMControl.Monitor, 18, 114);
			}
			g.setColor(Display.TRANS);
			g.drawString(jemu.core.device.sound.YMControl.Monitor, 14, 110);
			if (Display.left != 0)
				for (int i = 0; i < Display.left / 2; i++) {
					if (!isLowPerformance()) {
						g.setColor(Display.LED_BORDER);
						g.fillRect(2 + (i * 13), 121, 16, 16);
					}
					g.setColor(Display.LED);
					g.fillRect(4 + (i * 13), 123, 12, 12);
				}
			if (Display.right != 0)
				for (int i = 0; i < Display.right / 2; i++) {
					if (!isLowPerformance()) {
						g.setColor(Display.GREEN_BORDER);
						g.fillRect(2 + (i * 13), 138, 16, 16);
					}
					g.setColor(Display.GREEN);
					g.fillRect(4 + (i * 13), 140, 12, 12);
				}
		}
		if (jemu.system.cpc.CPC.YM_Rec && !isLowPerformance()) {
			g.drawImage(display.ymrec, 10, 12, display);
		}
	}

	private void paintKeys(Graphics g) {
		Display display = getDisplay();
		if (jemu.system.cpc.CPC.recordKeys && !isLowPerformance()) {
			flashkey++;
			if (flashkey > 25) {
				if (isLargeDisplay()) {
					g.drawImage(display.recb, 10, 12, display);
				} else {
					g.drawImage(display.recs, 10, 12, display);
				}
			}
			if (flashkey == 50) {
				flashkey = 0;
			}
		}
		if (jemu.system.cpc.CPC.playKeys && !isLowPerformance()) {
			flashkey++;
			if (flashkey > 25) {
				if (isLargeDisplay()) {
					g.drawImage(display.playb, 10, 12, display);
				} else {
					g.drawImage(display.plays, 10, 12, display);
				}
			}
			if (flashkey == 50) {
				flashkey = 0;
			}
		}
	}

}