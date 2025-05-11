package org.maia.amstrad.gui.browser.carousel.info;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.swing.layout.FillMode;
import org.maia.swing.image.ImageComponent;
import org.maia.swing.image.ImageUtils;
import org.maia.swing.SwingUtils;

public class InfoIcon extends ImageComponent {

	private boolean selected;

	private Image unselectedImage;

	private Color unselectedBackground;

	private Dimension unselectedSize;

	private Image selectedImage;

	private Color selectedBackground;

	private Dimension selectedSize;

	public InfoIcon(Image unselectedImage, Color unselectedBackground, Image selectedImage, Color selectedBackground) {
		this(unselectedImage, unselectedBackground, ImageUtils.getSize(unselectedImage), selectedImage,
				selectedBackground, ImageUtils.getSize(selectedImage));
	}

	public InfoIcon(Image unselectedImage, Color unselectedBackground, Dimension unselectedSize, Image selectedImage,
			Color selectedBackground, Dimension selectedSize) {
		super(unselectedImage, false, unselectedBackground);
		this.unselectedImage = unselectedImage;
		this.unselectedBackground = unselectedBackground;
		this.unselectedSize = unselectedSize;
		this.selectedImage = selectedImage;
		this.selectedBackground = selectedBackground;
		this.selectedSize = selectedSize;
		setFillMode(FillMode.FIT);
		SwingUtils.fixSize(this, unselectedSize);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		setBackground(selected ? getSelectedBackground() : getUnselectedBackground());
		setImage(selected ? getSelectedImage() : getUnselectedImage());
		SwingUtils.fixSize(this, selected ? getSelectedSize() : getUnselectedSize());
	}

	public Image getUnselectedImage() {
		return unselectedImage;
	}

	public Color getUnselectedBackground() {
		return unselectedBackground;
	}

	public Dimension getUnselectedSize() {
		return unselectedSize;
	}

	public Image getSelectedImage() {
		return selectedImage;
	}

	public Color getSelectedBackground() {
		return selectedBackground;
	}

	public Dimension getSelectedSize() {
		return selectedSize;
	}

}