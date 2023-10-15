package jemu.ui;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;

public class MonitorMask {

	private String name;

	/**
	 * The image can be of any size and will be scaled accordingly.
	 * <p>
	 * A typical base image size is 768x544 pixels
	 * </p>
	 */
	private Image image;

	private Insets insetsToInnerArea;

	private Color ambientBackgroundColor;

	public MonitorMask(String name, Image image, Insets insetsToInnerArea) {
		this(name, image, insetsToInnerArea, Color.BLACK);
	}

	public MonitorMask(String name, Image image, Insets insetsToInnerArea, Color ambientBackgroundColor) {
		this.name = name;
		this.image = image;
		this.insetsToInnerArea = insetsToInnerArea;
		this.ambientBackgroundColor = ambientBackgroundColor;
	}

	public String getName() {
		return name;
	}

	public Image getImage() {
		return image;
	}

	public Insets getInsetsToInnerArea() {
		return insetsToInnerArea;
	}

	public Color getAmbientBackgroundColor() {
		return ambientBackgroundColor;
	}

}