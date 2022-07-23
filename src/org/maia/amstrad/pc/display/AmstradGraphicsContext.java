package org.maia.amstrad.pc.display;

import java.awt.Dimension;
import java.awt.Font;

import org.maia.amstrad.pc.AmstradMonitorMode;

public interface AmstradGraphicsContext {

	Font getSystemFont();

	AmstradSystemColors getSystemColors();

	AmstradMonitorMode getMonitorMode();

	Dimension getDisplayCanvasResolution();

	Dimension getPrimaryDisplaySourceResolution();

}