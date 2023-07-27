package org.maia.amstrad.program;

import org.maia.amstrad.gui.ImageProxy;
import org.maia.amstrad.gui.components.ScrollableItem;

public interface AmstradProgramImage extends ImageProxy, ScrollableItem {

	String getCaption();

}