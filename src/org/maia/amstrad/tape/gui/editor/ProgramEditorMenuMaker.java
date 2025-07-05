package org.maia.amstrad.tape.gui.editor;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgramMetaDataConstants;
import org.maia.swing.text.pte.PlainTextDocumentEditor;
import org.maia.swing.text.pte.PlainTextDocumentEditorActions;
import org.maia.swing.text.pte.PlainTextEditorActions;
import org.maia.swing.text.pte.menu.PlainTextEditorDefaultMenuMaker;

public class ProgramEditorMenuMaker extends PlainTextEditorDefaultMenuMaker implements AmstradProgramMetaDataConstants {

	public static String INSERT_MENU_LABEL = "Insert";

	public ProgramEditorMenuMaker() {
	}

	@Override
	protected List<JMenu> createMenusForMenuBar(PlainTextEditorActions actions,
			PlainTextDocumentEditorActions docActions) {
		List<JMenu> menus = super.createMenusForMenuBar(actions, docActions);
		if (docActions instanceof ProgramEditorActions) {
			ProgramEditorActions programActions = (ProgramEditorActions) docActions;
			if (isMetadataEditor(programActions.getDocumentEditor())) {
				menus.add(getIndexOfMenu(menus, EDIT_MENU_LABEL) + 1, createMetadataInsertMenu(programActions));
			}
		}
		return menus;
	}

	@Override
	protected JPopupMenu createPopupMenu(PlainTextDocumentEditorActions docActions) {
		JPopupMenu popupMenu = super.createPopupMenu(docActions);
		if (docActions instanceof ProgramEditorActions) {
			ProgramEditorActions programActions = (ProgramEditorActions) docActions;
			if (isMetadataEditor(programActions.getDocumentEditor())) {
				popupMenu.add(new JSeparator());
				popupMenu.add(createMetadataInsertMenu(programActions));
			}
		}
		return popupMenu;
	}

	protected JMenu createMetadataInsertMenu(ProgramEditorActions programActions) {
		JMenu menu = new JMenu(INSERT_MENU_LABEL);
		menu.add(decorateForMenu(
				createInsertTextMenuItem("Include", formatMetadatum(AMD_INCLUDE, "$.amd"), programActions)));
		menu.add(createMetadataInsertTypeMenu(programActions));
		menu.add(decorateForMenu(createInsertTextMenuItem("Name", formatMetadatum(AMD_NAME), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Author", formatMetadatum(AMD_AUTHOR), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Year", formatMetadatum(AMD_YEAR, "####"), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Tape", formatMetadatum(AMD_TAPE), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Blocks", formatMetadatum(AMD_BLOCKS, "#"), programActions)));
		menu.add(createMetadataInsertMonitorMenu(programActions));
		menu.add(decorateForMenu(
				createInsertTextMenuItem("Description", formatMetadatum(AMD_DESCRIPTION), programActions)));
		menu.add(
				decorateForMenu(createInsertTextMenuItem("Authoring", formatMetadatum(AMD_AUTHORING), programActions)));
		menu.add(createMetadataInsertControlsMenu(programActions));
		menu.add(createMetadataInsertImagesMenu(programActions));
		menu.add(createMetadataInsertFileRefsMenu(programActions));
		menu.add(createMetadataInsertFlagsMenu(programActions));
		return menu;
	}

	protected JMenu createMetadataInsertTypeMenu(ProgramEditorActions programActions) {
		JMenu menu = new JMenu("Type");
		menu.add(decorateForMenu(createInsertTextMenuItem("Locomotive Basic program",
				formatMetadatum(AMD_TYPE, AMD_TYPE_LOCOMOTIVE_BASIC_PROGRAM), programActions)));
		return menu;
	}

	protected JMenu createMetadataInsertMonitorMenu(ProgramEditorActions programActions) {
		JMenu menu = new JMenu("Monitor");
		menu.add(decorateForMenu(createInsertTextMenuItem("Color",
				formatMetadatum(AMD_MONITOR, AmstradMonitorMode.COLOR.name()), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Green",
				formatMetadatum(AMD_MONITOR, AmstradMonitorMode.GREEN.name()), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Gray",
				formatMetadatum(AMD_MONITOR, AmstradMonitorMode.GRAY.name()), programActions)));
		return menu;
	}

	protected JMenu createMetadataInsertControlsMenu(ProgramEditorActions programActions) {
		JMenu menu = new JMenu("Controls");
		menu.add(decorateForMenu(createInsertTextMenuItem("Heading",
				formatMetadatum(AMD_CONTROLS_PREFIX + "[#]" + AMD_CONTROLS_SUFFIX_HEADING), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Control",
				formatMetadatum(AMD_CONTROLS_PREFIX + "[#]" + AMD_CONTROLS_SUFFIX_KEY) + "\n"
						+ formatMetadatum(AMD_CONTROLS_PREFIX + "[#]" + AMD_CONTROLS_SUFFIX_DESCRIPTION),
				programActions)));
		return menu;
	}

	protected JMenu createMetadataInsertImagesMenu(ProgramEditorActions programActions) {
		JMenu menu = new JMenu("Images");
		menu.add(decorateForMenu(createInsertTextMenuItem(
				"Image reference", formatMetadatum(AMD_IMAGES_PREFIX + "[#]" + AMD_IMAGES_SUFFIX_FILEREF, "$.png")
						+ "\n" + formatMetadatum(AMD_IMAGES_PREFIX + "[#]" + AMD_IMAGES_SUFFIX_CAPTION),
				programActions)));
		menu.add(decorateForMenu(
				createInsertTextMenuItem("Cover image", formatMetadatum(AMD_COVER_IMAGE, "$.png"), programActions)));
		return menu;
	}

	protected JMenu createMetadataInsertFileRefsMenu(ProgramEditorActions programActions) {
		JMenu menu = new JMenu("Files");
		menu.add(decorateForMenu(createInsertTextMenuItem("File reference",
				formatMetadatum(AMD_FILEREFS_PREFIX + "[$source]", "$target"), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Program reference",
				formatMetadatum(AMD_FILEREFS_PREFIX + "[$source]", "$.bas"), programActions)));
		menu.add(decorateForMenu(createInsertTextMenuItem("Program described by",
				formatMetadatum(AMD_FILEREFS_PREFIX + "[$source]", "$.bas " + AMD_FILEREFS_DESCRIBED_BY + " $.amd"),
				programActions)));
		return menu;
	}

	protected JMenu createMetadataInsertFlagsMenu(ProgramEditorActions programActions) {
		JMenu menu = new JMenu("Flags");
		menu.add(decorateForMenu(createInsertFlagMenuItem("Hide", AMD_FLAG_HIDE, programActions)));
		menu.add(decorateForMenu(createInsertFlagMenuItem("Featured", AMD_FLAG_FEATURED, programActions)));
		menu.add(decorateForMenu(createInsertFlagMenuItem("No launch", AMD_FLAG_NOLAUNCH, programActions)));
		menu.add(decorateForMenu(createInsertFlagMenuItem("No stage", AMD_FLAG_NOSTAGE, programActions)));
		menu.add(decorateForMenu(createInsertFlagMenuItem("No direct joystick", AMD_FLAG_NODIRECTJOY, programActions)));
		return menu;
	}

	protected JMenuItem createInsertTextMenuItem(String name, String textToInsert,
			ProgramEditorActions programActions) {
		JMenuItem menuItem = new JMenuItem(programActions.getInsertTextAction(name, textToInsert));
		return menuItem;
	}

	protected JMenuItem createInsertFlagMenuItem(String name, String flag, ProgramEditorActions programActions) {
		JMenuItem menuItem = new JMenuItem(programActions.getInsertMetadataFlagAction(name, flag));
		return menuItem;
	}

	private String formatMetadatum(String key) {
		return formatMetadatum(key, "");
	}

	private String formatMetadatum(String key, String value) {
		return key + ": " + value;
	}

	protected boolean isMetadataEditor(PlainTextDocumentEditor documentEditor) {
		return documentEditor.getDocument() instanceof ProgramMetadataDocument;
	}

}