package org.maia.amstrad.pc.browser.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.browser.repo.AmstradProgramRepository;
import org.maia.amstrad.pc.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.display.AmstradEmulatedDisplaySource;

public class ProgramBrowserDisplaySource extends AmstradEmulatedDisplaySource {

	private AmstradProgramRepository programRepository;

	private boolean mouseOverButton;

	public ProgramBrowserDisplaySource(AmstradPc amstradPc, AmstradProgramRepository programRepository) {
		super(amstradPc);
		this.programRepository = programRepository;
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		System.out.println(getProgramRepository());
		getAmstradPc().setMonitorMode(AmstradMonitorMode.COLOR);
		canvas.border(4).paper(0);
		canvas.symbol(255, 0, 192, 51, 12, 192, 51, 12, 0);
	}

	@Override
	protected void renderContent(AmstradDisplayCanvas canvas) {
		setMouseOverButton(false);
		canvas.pen(11).move(0, 399).draw(639, 0);
		canvas.pen(23).move(0, 0).draw(639, 399);
		canvas.pen(24).locate(1, 1).print("Ready ").pen(15).print("Steady ").pen(6).print("GO");
		canvas.pen(26).locate(1, 2);
		for (int i = 0; i < 15; i++)
			canvas.printChr(255);
		canvas.pen(24).locate(1, 9).print("Ready").locate(1, 10).printChr(143);
		canvas.pen(11).move(4,271).drawStr("Ready");
		renderCloseButton(canvas);
		updateCursor();
	}

	private void renderCloseButton(AmstradDisplayCanvas canvas) {
		if (isMouseOverCloseButton(canvas)) {
			setMouseOverButton(true);
			canvas.pen(26);
		} else {
			canvas.pen(13);
		}
		canvas.locate(40, 1).printChr(203);
	}

	private void updateCursor() {
		if (isMouseOverButton()) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			resetCursor();
		}
	}

	@Override
	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		super.mouseClickedOnCanvas(canvas, canvasPosition);
		if (isMouseOverCloseButton(canvas)) {
			close();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			close();
		}
	}

	private boolean isMouseOverCloseButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextCursorBoundsOnCanvas(40, 1));
	}

	private boolean isMouseOverButton() {
		return mouseOverButton;
	}

	private void setMouseOverButton(boolean mouseOverButton) {
		this.mouseOverButton = mouseOverButton;
	}

	public AmstradProgramRepository getProgramRepository() {
		return programRepository;
	}

}