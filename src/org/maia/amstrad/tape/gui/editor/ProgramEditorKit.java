package org.maia.amstrad.tape.gui.editor;

import java.awt.Color;

import org.maia.swing.text.pte.PlainTextDocumentEditor;
import org.maia.swing.text.pte.PlainTextDocumentEditorActions;
import org.maia.swing.text.pte.PlainTextEditor;
import org.maia.swing.text.pte.PlainTextEditorKit;
import org.maia.swing.text.pte.menu.PlainTextEditorMenuMaker;
import org.maia.swing.text.pte.model.PlainTextDocument;

public class ProgramEditorKit extends PlainTextEditorKit {

	private static Color METADATA_DOC_COLOR = new Color(231, 232, 197);

	private static Color SOURCECODE_DOC_COLOR = new Color(211, 235, 214);

	public ProgramEditorKit() {
	}

	@Override
	public Color getColorForDocumentEditor(PlainTextDocumentEditor documentEditor) {
		PlainTextDocument document = documentEditor.getDocument();
		if (document instanceof ProgramMetadataDocument) {
			return METADATA_DOC_COLOR;
		} else if (document instanceof ProgramSourceCodeDocument) {
			return SOURCECODE_DOC_COLOR;
		} else {
			return super.getColorForDocumentEditor(documentEditor);
		}
	}

	@Override
	protected PlainTextDocumentEditorActions createDocumentEditorActions(PlainTextDocumentEditor documentEditor,
			PlainTextEditor editor) {
		return new ProgramEditorActions(documentEditor, editor);
	}

	@Override
	protected PlainTextEditorMenuMaker createEditorMenuMaker(PlainTextEditor editor) {
		return new ProgramEditorMenuMaker();
	}

}