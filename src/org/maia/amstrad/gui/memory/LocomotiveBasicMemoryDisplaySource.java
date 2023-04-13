package org.maia.amstrad.gui.memory;

import java.awt.event.KeyEvent;

import org.maia.amstrad.gui.components.ColoredTextArea;
import org.maia.amstrad.gui.components.ColoredTextLine;
import org.maia.amstrad.gui.components.ColoredTextSpan;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradWindowDisplaySource;

public class LocomotiveBasicMemoryDisplaySource extends AmstradWindowDisplaySource {

	private ColoredTextArea variablesTextArea;

	public LocomotiveBasicMemoryDisplaySource(AmstradPc amstradPc) {
		super(amstradPc, "64K Basic Memory");
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		getAmstradPc().getMonitor().setMonitorMode(AmstradMonitorMode.COLOR);
		getAmstradPc().getMonitor().setMonitorBilinearEffect(false);
		getAmstradPc().getMonitor().setMonitorScanLinesEffect(false);
		canvas.border(1).paper(1);
		setVariablesTextArea(createVariablesTextArea());
	}

	@Override
	protected void renderWindowContent(AmstradDisplayCanvas canvas) {
		// TODO
		renderBasicVariables(canvas);
	}

	private void renderBasicVariables(AmstradDisplayCanvas canvas) {
		ColoredTextArea textArea = getVariablesTextArea();
		int tx1 = 4, tx2 = 37, ty1 = 12, ty2 = ty1 + textArea.getMaxItemsShowing() - 1;
		canvas.paper(0).clearRect(canvas.getTextAreaBoundsOnCanvas(tx1 - 1, ty1, tx2 + 1, ty2));
		renderColoredTextArea(textArea, tx1, ty1, tx2 - tx1 + 1, 0, 13, 24, true, canvas);
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		super.keyboardKeyPressed(e);
		handleKeyboardKeyInItemList(e, getVariablesTextArea());
	}

	private ColoredTextArea createVariablesTextArea() {
		// TODO
		ColoredTextArea textArea = new ColoredTextArea(5);
		for (int i = 0; i < 10; i++) {
			textArea.add(new ColoredTextLine(new ColoredTextSpan("Hello " + (i + 1), 0, 26)));
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