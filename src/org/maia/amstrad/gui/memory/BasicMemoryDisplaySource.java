package org.maia.amstrad.gui.memory;

import java.awt.event.KeyEvent;

import org.maia.amstrad.gui.components.ColoredTextArea;
import org.maia.amstrad.gui.components.ColoredTextLine;
import org.maia.amstrad.gui.components.ColoredTextSpan;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradWindowDisplaySource;

public class BasicMemoryDisplaySource extends AmstradWindowDisplaySource {

	private ColoredTextArea variablesTextArea;

	private static int COLOR_BORDER = 1;

	private static int COLOR_PAPER = 1;

	public BasicMemoryDisplaySource(AmstradPc amstradPc) {
		super(amstradPc, "64K Basic Memory");
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		getAmstradPc().getMonitor().setMonitorMode(AmstradMonitorMode.COLOR);
		getAmstradPc().getMonitor().setMonitorBilinearEffect(false);
		getAmstradPc().getMonitor().setMonitorScanLinesEffect(false);
		canvas.border(COLOR_BORDER).paper(COLOR_PAPER);
		setVariablesTextArea(createVariablesTextArea());
	}

	@Override
	protected void renderWindowContent(AmstradDisplayCanvas canvas) {
		// TODO
		renderBasicVariables(canvas);
	}

	private void renderBasicVariables(AmstradDisplayCanvas canvas) {
		ColoredTextArea textArea = getVariablesTextArea();
		int tx1 = 3, tx2 = 38, ty1 = 15, ty2 = ty1 + textArea.getMaxItemsShowing() - 1;
		canvas.paper(COLOR_PAPER).clearRect(canvas.getTextAreaBoundsOnCanvas(tx1 - 2, ty1 - 1, tx2 + 2, ty2 + 1));
		canvas.pen(14);
		renderWindowBorder(tx1 - 2, ty1 - 1, tx2 + 2, ty2 + 1, canvas);
		renderColoredTextArea(textArea, tx1, ty1, tx2 - tx1 + 1, canvas);
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		super.keyboardKeyPressed(e);
		handleKeyboardKeyInItemList(e, getVariablesTextArea());
	}

	private ColoredTextArea createVariablesTextArea() {
		// TODO
		ColoredTextArea textArea = new ColoredTextArea(10);
		for (int i = 0; i < 30; i++) {
			textArea.add(new ColoredTextLine(new ColoredTextSpan("Hello " + (i + 1), COLOR_PAPER, 26)));
		}
		return textArea;
	}

	private ColoredTextArea getVariablesTextArea() {
		return variablesTextArea;
	}

	private void setVariablesTextArea(ColoredTextArea variablesTextArea) {
		this.variablesTextArea = variablesTextArea;
	}

}