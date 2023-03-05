package org.maia.amstrad.gui.browser.components;

import java.util.List;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.UserControl;
import org.maia.amstrad.util.StringUtils;

public class ProgramInfoSheet extends ProgramSheet {

	public ProgramInfoSheet(AmstradProgram program, int maxItemsShowing, int maxWidth, int backgroundColorIndex) {
		super(program, maxItemsShowing, maxWidth, backgroundColorIndex);
	}

	@Override
	protected void populateSheet(int maxWidth, int bg) {
		AmstradProgram program = getProgram();
		AmstradMonitorMode mode = program.getPreferredMonitorMode();
		if (mode != null) {
			if (mode.equals(AmstradMonitorMode.GREEN)) {
				add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.spaces(maxWidth - 6), bg, bg),
						new ProgramInfoTextSpan("\u00FE GREEN", 0, 9)));
			} else if (mode.equals(AmstradMonitorMode.GRAY)) {
				add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.spaces(maxWidth - 5), bg, bg),
						new ProgramInfoTextSpan("\u00FE GRAY", 0, 13)));
			} else if (mode.equals(AmstradMonitorMode.COLOR)) {
				ProgramInfoLine line = new ProgramInfoLine(
						new ProgramInfoTextSpan(StringUtils.spaces(maxWidth - 6), bg, bg),
						new ProgramInfoTextSpan("\u00FE ", 0, 25));
				for (int i = 0; i < 5; i++)
					line.add(new ProgramInfoTextSpan(String.valueOf("COLOR".charAt(i)), 0, 14 + i));
				add(line);
			}
		}
		if (!StringUtils.isEmpty(program.getAuthor())) {
			if (isEmpty())
				add(new ProgramInfoLine());
			add(new ProgramInfoLine(new ProgramInfoTextSpan("Author", bg, 25)));
			for (String text : StringUtils.splitOnNewlinesAndWrap(program.getAuthor(), maxWidth)) {
				add(new ProgramInfoLine(new ProgramInfoTextSpan(text, bg, 26)));
			}
		}
		if (program.getProductionYear() > 0) {
			if (!isEmpty())
				add(new ProgramInfoLine());
			add(new ProgramInfoLine(new ProgramInfoTextSpan("Year", bg, 25)));
			add(new ProgramInfoLine(new ProgramInfoTextSpan(String.valueOf(program.getProductionYear()), bg, 26)));
		}
		if (!StringUtils.isEmpty(program.getNameOfTape()) || program.getBlocksOnTape() > 0) {
			if (!isEmpty())
				add(new ProgramInfoLine());
			String tape = !StringUtils.isEmpty(program.getNameOfTape()) ? program.getNameOfTape() : "?";
			String blocks = program.getBlocksOnTape() > 0 ? String.valueOf(program.getBlocksOnTape()) : "?";
			add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.fitWidth("Tape", 20) + " Blocks", bg, 25)));
			add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.fitWidth(tape, 20) + " ", bg, 26),
					new ProgramInfoTextSpan(blocks, bg, 26)));
		}
		if (!StringUtils.isEmpty(program.getProgramDescription())) {
			if (!isEmpty())
				add(new ProgramInfoLine());
			for (String text : StringUtils.splitOnNewlinesAndWrap(program.getProgramDescription(), maxWidth)) {
				add(new ProgramInfoLine(new ProgramInfoTextSpan(text, bg, 17)));
			}
		}
		if (!StringUtils.isEmpty(program.getAuthoringInformation())) {
			if (!isEmpty())
				add(new ProgramInfoLine());
			for (String text : StringUtils.splitOnNewlinesAndWrap(program.getAuthoringInformation(), maxWidth)) {
				add(new ProgramInfoLine(new ProgramInfoTextSpan(text, bg, 4)));
			}
		}
		if (!program.getUserControls().isEmpty()) {
			if (!isEmpty()) {
				add(new ProgramInfoLine());
				add(new ProgramInfoLine());
			}
			add(new ProgramInfoLine(
					new ProgramInfoTextSpan("\u008F\u008F\u00D4 User controls \u00D5\u008F\u008F", bg, 7)));
			for (UserControl uc : program.getUserControls()) {
				add(new ProgramInfoLine());
				if (uc.getHeading() != null) {
					List<String> hlines = StringUtils.splitOnNewlinesAndWrap(uc.getHeading(), maxWidth - 6);
					for (int i = 0; i < hlines.size(); i++) {
						String text = hlines.get(i);
						if (hlines.size() == 1) {
							add(new ProgramInfoLine(
									new ProgramInfoTextSpan("\u00CF\u00DC " + text + " \u00DD\u00CF", bg, 7)));
						} else if (i == 0) {
							add(new ProgramInfoLine(new ProgramInfoTextSpan(
									"\u00CF\u00DC " + StringUtils.fitWidth(text, maxWidth - 6) + " \u00DD\u00CF", bg,
									7)));
						} else {
							add(new ProgramInfoLine(new ProgramInfoTextSpan(
									"\u00CF  " + StringUtils.fitWidth(text, maxWidth - 6) + "  \u00CF", bg, 7)));
						}
					}
					add(new ProgramInfoLine());
				}
				add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.fitWidth(uc.getKey(), maxWidth), bg, 16)));
				for (String text : StringUtils.splitOnNewlinesAndWrap(uc.getDescription(), maxWidth - 2)) {
					add(new ProgramInfoLine(new ProgramInfoTextSpan(StringUtils.spaces(2) + text, bg, 26)));
				}
			}
		}
		add(new ProgramInfoLine());
	}

}