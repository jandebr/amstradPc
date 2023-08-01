package org.maia.amstrad.gui.memory;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace.VariableNotFoundException;
import org.maia.amstrad.basic.locomotive.token.FloatingPointNumberToken;
import org.maia.amstrad.basic.locomotive.token.FloatingPointTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.IntegerTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.TypedVariableToken;
import org.maia.amstrad.gui.components.ColoredTextArea;
import org.maia.amstrad.gui.components.ColoredTextLine;
import org.maia.amstrad.gui.components.ColoredTextSpan;
import org.maia.amstrad.gui.memory.model.MemoryOutline;
import org.maia.amstrad.gui.memory.model.MemoryOutlineBuilder;
import org.maia.amstrad.gui.memory.model.MemorySegment;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradWindowDisplaySource;
import org.maia.amstrad.util.StringUtils;

public class BasicMemoryDisplaySource extends AmstradWindowDisplaySource {

	private MemoryOutline memoryOutline;

	private ColoredTextArea variablesTextArea;

	private static int COLOR_BORDER = 1;

	private static int COLOR_PAPER = 1;

	private static int COLOR_OUTLINE_BG = 0;

	public static int SYMBOL_CODE_REFRESH = 176;

	public BasicMemoryDisplaySource(AmstradPc amstradPc) {
		super(amstradPc, "64K Basic Memory");
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		getAmstradPc().getMonitor().setMonitorMode(AmstradMonitorMode.COLOR);
		canvas.border(COLOR_BORDER).paper(COLOR_PAPER);
		canvas.symbol(SYMBOL_CODE_REFRESH, 46, 76, 138, 129, 81, 50, 116, 0); // refresh
		refresh();
	}

	@Override
	protected void renderWindowTitleBar(AmstradDisplayCanvas canvas) {
		super.renderWindowTitleBar(canvas);
		renderRefreshButton(canvas);
	}

	private void renderRefreshButton(AmstradDisplayCanvas canvas) {
		if (isFocusOnRefreshButton(canvas)) {
			setMouseOverButton(true);
			canvas.paper(14).pen(24);
		} else {
			canvas.paper(5).pen(26);
		}
		canvas.locate(1, 1).print("  ").paper(COLOR_PAPER);
		canvas.move(8, 399).drawChrMonospaced(SYMBOL_CODE_REFRESH);
	}

	private boolean isFocusOnRefreshButton(AmstradDisplayCanvas canvas) {
		return !isModalWindowOpen() && isMouseOverRefreshButton(canvas);
	}

	private boolean isMouseOverRefreshButton(AmstradDisplayCanvas canvas) {
		return isMouseInCanvasBounds(canvas.getTextAreaBoundsOnCanvas(1, 1, 2, 1));
	}

	@Override
	protected void renderWindowContent(AmstradDisplayCanvas canvas) {
		renderMemoryOutline(getMemoryOutline(), canvas);
		renderBasicVariables(canvas);
	}

	private void renderMemoryOutline(MemoryOutline outline, AmstradDisplayCanvas canvas) {
		fillTextAreaWithSolidColor(1, 3, 40, 10, COLOR_OUTLINE_BG, canvas);
		Rectangle barArea = canvas.getTextAreaBoundsOnCanvas(1, 3, 40, 5);
		int padding = 8;
		int x0 = barArea.x + padding;
		int x1 = x0;
		int xsize = barArea.width - 2 * padding;
		double xscale = xsize / (double) outline.getByteLength();
		int y0 = barArea.y - padding;
		int ysize = barArea.height - 2 * padding;
		for (int i = 0; i < outline.getSegmentCount(); i++) {
			MemorySegment segment = outline.getSegment(i);
			// Bar
			int x2 = x0 + (int) Math.round(xscale * segment.getByteEnd());
			if (x2 >= x1) {
				boolean free = MemoryOutlineBuilder.LABEL_FREE.equals(segment.getLabel());
				int dy = free ? 2 : 0;
				canvas.paper(segment.getColorIndex());
				canvas.clearRect(x1, y0 - dy, x2 - x1 + 1, ysize - 2 * dy);
				x1 = x2 + 1;
			}
			// Label
			int txlabel = 2 + 20 * (i / 4);
			int tylabel = 6 + i % 4;
			canvas.paper(COLOR_OUTLINE_BG).locate(txlabel, tylabel);
			canvas.pen(13).print(StringUtils.fitWidthRightAlign(String.valueOf(segment.getByteLength()), 5));
			canvas.pen(segment.getColorIndex()).print(" " + StringUtils.fitWidth(segment.getLabel(), 12));
		}
		canvas.paper(COLOR_PAPER);
	}

	private void renderBasicVariables(AmstradDisplayCanvas canvas) {
		ColoredTextArea textArea = getVariablesTextArea();
		if (!textArea.isEmpty()) {
			int tx1 = 3, tx2 = 38, ty1 = 13, ty2 = ty1 + textArea.getMaxItemsShowing() - 1;
			fillTextAreaWithSolidColor(tx1 - 2, ty1 - 1, tx2 + 2, ty2 + 1, COLOR_PAPER, canvas);
			renderWindowBorder(tx1 - 2, ty1 - 1, tx2 + 2, ty2 + 1, 14, "Variables", canvas);
			renderColoredTextArea(textArea, tx1, ty1, tx2 - tx1 + 1, canvas);
		}
	}

	@Override
	protected void mouseClickedOnCanvas(AmstradDisplayCanvas canvas, Point canvasPosition) {
		super.mouseClickedOnCanvas(canvas, canvasPosition);
		if (isFocusOnRefreshButton(canvas)) {
			refresh();
		}
	}

	@Override
	protected void keyboardKeyPressed(KeyEvent e) {
		super.keyboardKeyPressed(e);
		handleKeyboardKeyInItemList(e, getVariablesTextArea());
	}

	public void refresh() {
		setMemoryOutline(new MemoryOutlineBuilder().buildFor(getAmstradPc()));
		setVariablesTextArea(createVariablesTextArea());
	}

	private ColoredTextArea createVariablesTextArea() {
		ColoredTextArea textArea = new ColoredTextArea(12);
		BasicRuntime rt = getAmstradPc().getBasicRuntime();
		if (rt instanceof LocomotiveBasicRuntime) {
			populateTextAreaWithVariables(textArea, 36, ((LocomotiveBasicRuntime) rt).getVariableSpace());
		}
		return textArea;
	}

	private void populateTextAreaWithVariables(ColoredTextArea textArea, int maxWidth,
			LocomotiveBasicVariableSpace varSpace) {
		AmstradMemory memory = getAmstradPc().getMemory();
		memory.startThreadExclusiveSession();
		try {
			List<TypedVariableToken> variables = sortVariablesByName(varSpace.getAllVariables());
			for (TypedVariableToken variable : variables) {
				// Variable name and type
				if (variable instanceof FloatingPointTypedVariableToken) {
					textArea.add(new ColoredTextLine(new ColoredTextSpan(
							StringUtils.truncate(variable.getVariableNameWithoutTypeIndicator(), maxWidth), COLOR_PAPER,
							26)));
				} else {
					int typeColor = variable instanceof StringTypedVariableToken ? 17 : 25;
					textArea.add(new ColoredTextLine(
							new ColoredTextSpan(
									StringUtils.truncate(variable.getVariableNameWithoutTypeIndicator(), maxWidth - 1),
									COLOR_PAPER, 26),
							new ColoredTextSpan(String.valueOf(variable.getTypeIndicator()), COLOR_PAPER, typeColor)));
				}
				// Variable value
				String valueStr = "";
				try {
					if (variable instanceof IntegerTypedVariableToken) {
						valueStr = String.valueOf(varSpace.getValue((IntegerTypedVariableToken) variable, false));
					} else if (variable instanceof FloatingPointTypedVariableToken) {
						valueStr = FloatingPointNumberToken
								.format(varSpace.getValue((FloatingPointTypedVariableToken) variable, false));
					} else if (variable instanceof StringTypedVariableToken) {
						valueStr = '"' + varSpace.getValue((StringTypedVariableToken) variable, false) + '"';
					}
				} catch (VariableNotFoundException e) {
					// should not be possible
				}
				for (String line : StringUtils.splitOnNewlinesAndWrap(valueStr, maxWidth)) {
					textArea.add(new ColoredTextLine(new ColoredTextSpan(line, COLOR_PAPER, 23)));
				}
				textArea.add(new ColoredTextLine());
			}
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	private List<TypedVariableToken> sortVariablesByName(Collection<TypedVariableToken> variables) {
		List<TypedVariableToken> list = new Vector<TypedVariableToken>(variables);
		Collections.sort(list, new Comparator<TypedVariableToken>() {

			@Override
			public int compare(TypedVariableToken var1, TypedVariableToken var2) {
				return var1.getSourceFragment().compareTo(var2.getSourceFragment());
			}

		});
		return list;
	}

	private MemoryOutline getMemoryOutline() {
		return memoryOutline;
	}

	private void setMemoryOutline(MemoryOutline memoryOutline) {
		this.memoryOutline = memoryOutline;
	}

	private ColoredTextArea getVariablesTextArea() {
		return variablesTextArea;
	}

	private void setVariablesTextArea(ColoredTextArea variablesTextArea) {
		this.variablesTextArea = variablesTextArea;
	}

}