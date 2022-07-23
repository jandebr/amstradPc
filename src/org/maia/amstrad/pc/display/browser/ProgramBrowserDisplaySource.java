package org.maia.amstrad.pc.display.browser;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.display.AmstradEmulatedDisplaySource;

public class ProgramBrowserDisplaySource extends AmstradEmulatedDisplaySource {

	public ProgramBrowserDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected void renderContent(AmstradDisplayCanvas canvas) {
		canvas.border(0).paper(1);
		canvas.pen(26).move(0, 399).draw(639, 0);
		canvas.pen(15).move(0, 0).draw(639, 399);
		canvas.pen(24).locate(1, 1).print("Ready ");
		canvas.pen(20).print("Steady ");
		canvas.pen(25).print("GO");
		canvas.pen(26).locate(1, 2);
		for (int i = 0; i < 15; i++)
			canvas.print((char) 206);
		canvas.pen(24).locate(1, 9).print("Ready").locate(1, 10).print((char) 143);
	}

	@Override
	protected Cursor getDefaultCursor() {
		return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}

	@Override
	protected void mouseClickedOnCanvas(Point canvasPoint) {
		System.out.println("Mouse clicked at " + canvasPoint.x + "," + canvasPoint.y);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			close();
		}
	}

}