package org.maia.amstrad.gui;

import java.awt.Image;

public interface ImageOnDemand {

	/**
	 * Returns the image when immediately available
	 * 
	 * @return The image, when immediately available, otherwise returns <code>null</code>
	 */
	Image probeImage();

	/**
	 * Returns the image when immediately available, otherwise requests the image to become available
	 * 
	 * @return The image, when immediately available, otherwise returns <code>null</code> and retrieves the image
	 *         asynchronously in the background
	 */
	Image getOrRequestImage();

}