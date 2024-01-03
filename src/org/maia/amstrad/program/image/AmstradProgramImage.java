package org.maia.amstrad.program.image;

import org.maia.amstrad.gui.components.ScrollableItem;
import org.maia.image.pool.PooledImage;

public interface AmstradProgramImage extends PooledImage, ScrollableItem {

	String getCaption();

}