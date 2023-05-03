package org.maia.amstrad.program.repo.cover;

import java.awt.Image;

public interface CoverImage {

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
	Image demandImage();

}