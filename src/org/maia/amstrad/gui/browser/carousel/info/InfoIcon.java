package org.maia.amstrad.gui.browser.carousel.info;

import java.awt.Color;
import java.awt.Image;

import org.maia.swing.image.ImageComponent;

public class InfoIcon extends ImageComponent {

	private boolean selected;

	private Image unselectedImage;

	private Color unselectedBackground;

	private Image selectedImage;

	private Color selectedBackground;

	public InfoIcon(Image image, Color unselectedBackground, Color selectedBackground) {
		this(image, unselectedBackground, image, selectedBackground);
	}

	public InfoIcon(Image unselectedImage, Color unselectedBackground, Image selectedImage,
			Color selectedBackground) {
		super(unselectedImage, false, unselectedBackground);
		this.unselectedImage = unselectedImage;
		this.unselectedBackground = unselectedBackground;
		this.selectedImage = selectedImage;
		this.selectedBackground = selectedBackground;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		setBackground(selected ? getSelectedBackground() : getUnselectedBackground());
		setImage(selected ? getSelectedImage() : getUnselectedImage());
	}

	public Image getUnselectedImage() {
		return unselectedImage;
	}

	public Color getUnselectedBackground() {
		return unselectedBackground;
	}

	public Image getSelectedImage() {
		return selectedImage;
	}

	public Color getSelectedBackground() {
		return selectedBackground;
	}

}