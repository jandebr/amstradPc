package jemu.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Variation to the <code>DisplayClassicRenderDelegate</code> whereby all rendering takes place on a smaller-sized
 * <em>staged</em> image, which then is upscaled to fill the entire <code>Display</code>
 * 
 * <p>
 * This strategy usually is faster than <code>DisplayClassicRenderDelegate</code> at larger resolutions since it avoids
 * full-scale rendering of the monitor mask image as well as bilinear interpolation at large scale. The downside is a
 * degraded image quality due to the upscaled bilinear interpolation
 * </p>
 */
public class DisplayStagedRenderDelegate extends DisplayClassicRenderDelegate {

	private BufferedImage stagedImage;

	private static final Dimension stagedImageSize = new Dimension(768, 544);

	public static final String NAME = "Staged";

	public DisplayStagedRenderDelegate() {
		super(NAME);
	}

	@Override
	public void init(Display display) {
		super.init(display);
		stagedImage = new BufferedImage(stagedImageSize.width, stagedImageSize.height, BufferedImage.TYPE_INT_RGB);
		stagedImage.setAccelerationPriority(1);
	}

	@Override
	public boolean isDoubleBufferingEnabled() {
		return false;
	}

	@Override
	protected void paintDisplay(Graphics g, boolean offscreenImage, boolean monitorEffect) {
		Graphics graphics = g;
		Rectangle imageRect = getImageRect();
		Rectangle origImageRect = imageRect;
		final boolean staging = useStagedImage();
		if (staging) {
			graphics = stagedImage.createGraphics();
			origImageRect = new Rectangle(imageRect);
			imageRect.x = 0;
			imageRect.y = 0;
			imageRect.width = stagedImage.getWidth();
			imageRect.height = stagedImage.getHeight();
		}
		super.paintDisplay(graphics, offscreenImage, monitorEffect);
		if (staging) {
			graphics.dispose();
			imageRect.x = origImageRect.x;
			imageRect.y = origImageRect.y;
			imageRect.width = origImageRect.width;
			imageRect.height = origImageRect.height;
			g.drawImage(stagedImage, imageRect.x, imageRect.y, imageRect.width, imageRect.height, getDisplay());
		}
	}

	private boolean useStagedImage() {
		return getImageRect().width > stagedImageSize.width && getImageRect().height > stagedImageSize.height;
	}

}