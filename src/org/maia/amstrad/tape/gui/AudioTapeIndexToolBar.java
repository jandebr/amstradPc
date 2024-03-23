package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.tape.gui.AmstradPcPlugin.AmstradPcListener;
import org.maia.amstrad.tape.gui.AudioTapeIndexView.IndexSelectionListener;
import org.maia.amstrad.tape.gui.editor.ProgramMetadataDocument;
import org.maia.amstrad.tape.gui.editor.ProgramSourceCodeDocument;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.swing.text.pte.PlainTextDocumentEditor;
import org.maia.swing.text.pte.PlainTextDocumentEditorAdapter;
import org.maia.swing.text.pte.PlainTextDocumentException;
import org.maia.swing.text.pte.PlainTextEditor;
import org.maia.swing.text.pte.model.PlainTextDocument;

public class AudioTapeIndexToolBar extends Box implements IndexSelectionListener, AmstradPcListener {

	private AudioTapeIndexView indexView;

	private CodeInspectorAction codeInspectorAction;

	private CodeRevertAction codeRevertAction;

	private CodeEditAction codeEditAction;

	private MetadataEditAction metadataEditAction;

	private ProgramLoadAction programLoadAction;

	private ProgramRunAction programRunAction;

	private ClearSelectionAction clearSelectionAction;

	private ReadAudioFileAction readAudioFileAction;

	private AmstradPcPlugin amstradPcPlugin;

	private List<ToolBarListener> listeners;

	public AudioTapeIndexToolBar(AudioTapeIndexView indexView) {
		super(BoxLayout.X_AXIS);
		this.indexView = indexView;
		this.codeInspectorAction = new CodeInspectorAction();
		this.codeRevertAction = new CodeRevertAction();
		this.codeEditAction = new CodeEditAction();
		this.metadataEditAction = new MetadataEditAction();
		this.programLoadAction = new ProgramLoadAction();
		this.programRunAction = new ProgramRunAction();
		this.clearSelectionAction = new ClearSelectionAction();
		this.readAudioFileAction = new ReadAudioFileAction();
		this.amstradPcPlugin = new AmstradPcPlugin();
		this.listeners = new Vector<ToolBarListener>();
		indexView.addSelectionListener(this);
		getAmstradPcPlugin().addListener(this);
		buildUI();
	}

	private void buildUI() {
		add(new ProgramButton(getReadAudioFileAction()));
		add(Box.createHorizontalStrut(4));
		add(new ProgramButton(getCodeInspectorAction()));
		add(Box.createHorizontalStrut(4));
		add(new ProgramButton(getProgramLoadAction()));
		add(Box.createHorizontalStrut(4));
		add(new ProgramButton(getProgramRunAction()));
		add(Box.createHorizontalStrut(4));
		add(new ProgramButton(getMetadataEditAction()));
		add(Box.createHorizontalStrut(4));
		add(new ProgramButton(getCodeEditAction()));
		add(Box.createHorizontalStrut(4));
		add(new ProgramButton(getCodeRevertAction()));
		add(Box.createHorizontalStrut(4));
		add(new ProgramButton(getClearSelectionAction()));
	}

	public void addListener(ToolBarListener listener) {
		getListeners().add(listener);
	}

	public void removeListener(ToolBarListener listener) {
		getListeners().remove(listener);
	}

	@Override
	public void indexSelectionUpdate(AudioTapeIndexView source) {
		boolean programSelection = getSelectedProgram() != null;
		getCodeInspectorAction().setEnabled(programSelection);
		getCodeEditAction().setEnabled(programSelection);
		getMetadataEditAction().setEnabled(programSelection);
		getProgramLoadAction().setEnabled(programSelection);
		getProgramRunAction().setEnabled(programSelection);
		getClearSelectionAction().setEnabled(programSelection);
		updateCodeRevertEnablement();
	}

	private void updateCodeRevertEnablement() {
		AudioTapeProgram tapeProgram = getSelectedProgram();
		getCodeRevertAction().setEnabled(tapeProgram != null && tapeProgram.hasModifiedSourceCode());
	}

	@Override
	public void notifyModifiedSourceCodeSaved(AudioTapeProgram tapeProgram) {
		// modified inside AmstradPc
		fireModifiedSourceCodeSaved(tapeProgram); // propagate to index view
		updateSourceCodeInEditor(tapeProgram, false);
		updateCodeRevertEnablement();
	}

	private void fireModifiedSourceCodeSaved(AudioTapeProgram tapeProgram) {
		for (ToolBarListener listener : getListeners()) {
			listener.notifyModifiedSourceCodeSaved(tapeProgram);
		}
	}

	private void fireModifiedSourceCodeReverted(AudioTapeProgram tapeProgram) {
		for (ToolBarListener listener : getListeners()) {
			listener.notifyModifiedSourceCodeReverted(tapeProgram);
		}
	}

	private void updateSourceCodeInEditor(AudioTapeProgram tapeProgram, boolean discardEdits) {
		PlainTextDocumentEditor documentEditor = getProgramSourceCodeEditor(tapeProgram);
		if (documentEditor != null && (discardEdits || !documentEditor.isTextChangedSinceLastSave())) {
			try {
				documentEditor.revert(false);
			} catch (PlainTextDocumentException e) {
				e.printStackTrace();
			}
		}
	}

	private PlainTextDocumentEditor getProgramSourceCodeEditor(AudioTapeProgram tapeProgram) {
		for (Iterator<PlainTextDocumentEditor> it = getTextEditor().getDocumentEditorsIterator(); it.hasNext();) {
			PlainTextDocumentEditor documentEditor = it.next();
			PlainTextDocument document = documentEditor.getDocument();
			if (document instanceof ProgramSourceCodeDocument) {
				if (((ProgramSourceCodeDocument) document).getProgram().equals(tapeProgram)) {
					return documentEditor;
				}
			}
		}
		return null;
	}

	private PlainTextEditor getTextEditor() {
		return UIFactoryTape.getApplicationViewer().getTextEditor();
	}

	public AudioTapeProgram getSelectedProgram() {
		return getIndexView().getSelectedProgram();
	}

	public AudioTapeIndexView getIndexView() {
		return indexView;
	}

	private CodeInspectorAction getCodeInspectorAction() {
		return codeInspectorAction;
	}

	private CodeRevertAction getCodeRevertAction() {
		return codeRevertAction;
	}

	private CodeEditAction getCodeEditAction() {
		return codeEditAction;
	}

	private MetadataEditAction getMetadataEditAction() {
		return metadataEditAction;
	}

	private ProgramLoadAction getProgramLoadAction() {
		return programLoadAction;
	}

	private ProgramRunAction getProgramRunAction() {
		return programRunAction;
	}

	private ClearSelectionAction getClearSelectionAction() {
		return clearSelectionAction;
	}

	private ReadAudioFileAction getReadAudioFileAction() {
		return readAudioFileAction;
	}

	private AmstradPcPlugin getAmstradPcPlugin() {
		return amstradPcPlugin;
	}

	private List<ToolBarListener> getListeners() {
		return listeners;
	}

	private class ProgramButton extends JButton {

		public ProgramButton(ProgramAction action) {
			super(action);
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
					BorderFactory.createEmptyBorder(2, 6, 2, 6)));
			setFocusPainted(false);
		}

	}

	private abstract class ProgramAction extends AbstractAction {

		protected ProgramAction(Icon icon) {
			this(null, icon);
		}

		protected ProgramAction(String name, Icon icon) {
			super(name, icon);
			setEnabled(false);
		}

		protected void setName(String name) {
			putValue(Action.NAME, name);
		}

		protected void setToolTipText(String text) {
			putValue(Action.SHORT_DESCRIPTION, text);
		}

	}

	private class ReadAudioFileAction extends ProgramAction {

		public ReadAudioFileAction() {
			super(UIResourcesTape.readAudioFileLabel, UIResourcesTape.readAudioFileIcon);
			setToolTipText(UIResourcesTape.readAudioFileTooltip);
			setEnabled(true);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			UIFactoryTape.getApplicationViewer().openTaskConfigurationDialog();
		}

	}

	private class ClearSelectionAction extends ProgramAction {

		public ClearSelectionAction() {
			super(UIResourcesTape.clearSelectionLabel, UIResourcesTape.clearSelectionIcon);
			setToolTipText(UIResourcesTape.clearSelectionTooltip);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			getIndexView().clearSelection();
		}

	}

	private class CodeInspectorAction extends ProgramAction {

		public CodeInspectorAction() {
			super(UIResourcesTape.openCodeInspectorLabel, UIResourcesTape.openCodeInspectorIcon);
			setToolTipText(UIResourcesTape.openCodeInspectorTooltip);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			AudioTapeProgram program = getSelectedProgram();
			if (program != null) {
				new Viewer(createView(program), "Code inspection of " + program.getProgramName(), false).buildAndShow();
			}
		}

		private JComponent createView(AudioTapeProgram program) {
			JPanel panel = new JPanel(new BorderLayout());
			if (program.hasModifiedSourceCode()) {
				panel.add(createModifiedSourceCodeWarning(), BorderLayout.NORTH);
			}
			panel.add(new CodeInspectorView(program), BorderLayout.CENTER);
			return panel;
		}

		private JComponent createModifiedSourceCodeWarning() {
			JLabel label = new JLabel("Changes were made to the original source code on tape, shown here",
					UIResourcesTape.pencilIcon, SwingConstants.LEADING);
			label.setOpaque(true);
			label.setBackground(Color.BLACK);
			label.setForeground(Color.YELLOW);
			label.setFont(label.getFont().deriveFont(Font.ITALIC));
			label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			return label;
		}

	}

	private class CodeRevertAction extends ProgramAction {

		public CodeRevertAction() {
			super(UIResourcesTape.revertCodeLabel, UIResourcesTape.revertCodeIcon);
			setToolTipText(UIResourcesTape.revertCodeTooltip);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			AudioTapeProgram tapeProgram = getSelectedProgram();
			if (tapeProgram != null) {
				tapeProgram.revertSourceCodeModifications();
				fireModifiedSourceCodeReverted(tapeProgram); // propagate to index view
				updateSourceCodeInEditor(tapeProgram, true);
				if (tapeProgram.equals(getAmstradPcPlugin().getProgramInAmstradPc())) {
					getAmstradPcPlugin().closeAmstradPc();
				}
				updateCodeRevertEnablement();
			}
		}

	}

	private abstract class ProgramEditorAction extends ProgramAction {

		protected ProgramEditorAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			AudioTapeProgram tapeProgram = getSelectedProgram();
			if (tapeProgram != null) {
				PlainTextEditor editor = getTextEditor();
				PlainTextDocument document = createDocument(tapeProgram);
				editor.showInFrame("", false, false);
				try {
					PlainTextDocumentEditor documentEditor = editor.openDocument(document);
					handleDocumentOpened(documentEditor);
				} catch (PlainTextDocumentException error) {
					editor.showErrorMessageDialog("Error", "Error while opening document", error);
				}
			}
		}

		protected abstract PlainTextDocument createDocument(AudioTapeProgram tapeProgram);

		protected void handleDocumentOpened(PlainTextDocumentEditor documentEditor) {
			// Handle for subclasses
		}

	}

	private class CodeEditAction extends ProgramEditorAction {

		public CodeEditAction() {
			super(UIResourcesTape.editCodeLabel, UIResourcesTape.editCodeIcon);
			setToolTipText(UIResourcesTape.editCodeTooltip);
		}

		@Override
		protected PlainTextDocument createDocument(AudioTapeProgram tapeProgram) {
			return new ProgramSourceCodeDocument(tapeProgram);
		}

		@Override
		protected void handleDocumentOpened(PlainTextDocumentEditor documentEditor) {
			super.handleDocumentOpened(documentEditor);
			documentEditor.addListener(new PlainTextDocumentEditorAdapter() {

				@Override
				public void documentSaved(PlainTextDocumentEditor documentEditor) {
					AudioTapeProgram tapeProgram = ((ProgramSourceCodeDocument) documentEditor.getDocument())
							.getProgram();
					fireModifiedSourceCodeSaved(tapeProgram); // propagate to index view
					updateCodeRevertEnablement();
					// note: leave code in AmstradPc as-is
				}

			});
		}

	}

	private class MetadataEditAction extends ProgramEditorAction {

		public MetadataEditAction() {
			super(UIResourcesTape.editMetadataLabel, UIResourcesTape.editMetadataIcon);
			setToolTipText(UIResourcesTape.editMetadataTooltip);
		}

		@Override
		protected PlainTextDocument createDocument(AudioTapeProgram tapeProgram) {
			return new ProgramMetadataDocument(tapeProgram);
		}

	}

	private abstract class ProgramLaunchAction extends ProgramAction {

		protected ProgramLaunchAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			final AudioTapeProgram tapeProgram = getSelectedProgram();
			if (tapeProgram != null) {
				setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							launchProgram(tapeProgram);
						} catch (AmstradProgramException e) {
							System.err.println(e);
						} finally {
							setEnabled(true);
						}
					}
				}).start();
			}
		}

		protected abstract void launchProgram(AudioTapeProgram tapeProgram) throws AmstradProgramException;

	}

	private class ProgramLoadAction extends ProgramLaunchAction {

		public ProgramLoadAction() {
			super(UIResourcesTape.loadProgramLabel, UIResourcesTape.loadProgramIcon);
			setToolTipText(UIResourcesTape.loadProgramTooltip);
		}

		@Override
		protected void launchProgram(AudioTapeProgram tapeProgram) throws AmstradProgramException {
			getAmstradPcPlugin().load(tapeProgram);
			getAmstradPcPlugin().bringAmstradPcToFront();
		}

	}

	private class ProgramRunAction extends ProgramLaunchAction {

		public ProgramRunAction() {
			super(UIResourcesTape.runProgramLabel, UIResourcesTape.runProgramIcon);
			setToolTipText(UIResourcesTape.runProgramTooltip);
		}

		@Override
		protected void launchProgram(AudioTapeProgram tapeProgram) throws AmstradProgramException {
			getAmstradPcPlugin().runStaged(tapeProgram);
			getAmstradPcPlugin().bringAmstradPcToFront();
		}

	}

	public static interface ToolBarListener {

		void notifyModifiedSourceCodeSaved(AudioTapeProgram tapeProgram);

		void notifyModifiedSourceCodeReverted(AudioTapeProgram tapeProgram);

	}

}