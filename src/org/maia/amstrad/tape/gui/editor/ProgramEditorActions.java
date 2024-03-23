package org.maia.amstrad.tape.gui.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;

import org.maia.amstrad.program.AmstradProgramMetaDataConstants;
import org.maia.swing.text.pte.PlainTextDocumentEditor;
import org.maia.swing.text.pte.PlainTextDocumentEditorActions;
import org.maia.swing.text.pte.PlainTextDocumentEditorAdapter;
import org.maia.swing.text.pte.PlainTextEditor;

@SuppressWarnings("serial")
public class ProgramEditorActions extends PlainTextDocumentEditorActions {

	private Map<String, InsertTextAction> insertTextActions;

	public ProgramEditorActions(PlainTextDocumentEditor documentEditor, PlainTextEditor editor) {
		super(documentEditor, editor);
		this.insertTextActions = new HashMap<String, InsertTextAction>();
	}

	public Action getInsertTextAction(String textToInsert) {
		return getInsertTextAction(textToInsert, textToInsert);
	}

	public Action getInsertTextAction(String name, String textToInsert) {
		String cacheKey = name + ":@:" + textToInsert;
		InsertTextAction action = getInsertTextActions().get(cacheKey);
		if (action == null) {
			action = new InsertTextAction(name, textToInsert);
			getInsertTextActions().put(cacheKey, action);
		}
		return action;
	}

	public Action getInsertMetadataFlagAction(String flag) {
		return getInsertMetadataFlagAction(flag, flag);
	}

	public Action getInsertMetadataFlagAction(String name, String flag) {
		String cacheKey = "FLAG:@:" + name + ":@:" + flag;
		InsertTextAction action = getInsertTextActions().get(cacheKey);
		if (action == null) {
			action = new InsertMetadataFlagAction(name, flag);
			getInsertTextActions().put(cacheKey, action);
		}
		return action;
	}

	private Map<String, InsertTextAction> getInsertTextActions() {
		return insertTextActions;
	}

	private class InsertTextAction extends DocumentEditorAction {

		private String textToInsert;

		public InsertTextAction(String name, String textToInsert) {
			super(name, null);
			this.textToInsert = textToInsert;
			updateEnablement();
			getDocumentEditor().addListener(new InsertTextActionManager());
		}

		@Override
		protected void doAction(PlainTextDocumentEditor documentEditor) {
			documentEditor.type(getTextToInsert());
		}

		private void updateEnablement() {
			setEnabled(getDocumentEditor().isEditable());
		}

		public String getTextToInsert() {
			return textToInsert;
		}

		private class InsertTextActionManager extends PlainTextDocumentEditorAdapter {

			@Override
			public void documentEditableChanged(PlainTextDocumentEditor documentEditor) {
				updateEnablement();
			}

		}

	}

	private class InsertMetadataFlagAction extends InsertTextAction {

		public InsertMetadataFlagAction(String name, String flag) {
			super(name, flag);
		}

		@Override
		protected void doAction(PlainTextDocumentEditor documentEditor) {
			Pattern pattern = Pattern.compile("^\\Q" + AmstradProgramMetaDataConstants.AMD_FLAGS + "\\E\\s*\\:.*$",
					Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(documentEditor.getText());
			if (matcher.find()) {
				documentEditor.setCaretPosition(matcher.end());
				documentEditor.type("," + getFlag());
			} else {
				documentEditor.type(AmstradProgramMetaDataConstants.AMD_FLAGS + ": " + getFlag());
			}
		}

		public String getFlag() {
			return getTextToInsert();
		}

	}

}