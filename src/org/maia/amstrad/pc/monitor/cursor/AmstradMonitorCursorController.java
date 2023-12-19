package org.maia.amstrad.pc.monitor.cursor;

import java.awt.Cursor;

public interface AmstradMonitorCursorController {

	void hideCursor();

	void unhideCursor();

	void setCursor(Cursor cursor);

	Cursor getCursor();

	boolean isCursorHidden();

	boolean isAutoHideCursor();

	void setAutoHideCursor(boolean autoHide);

}