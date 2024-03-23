package org.maia.amstrad.tape.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.tape.config.TapeReaderTaskConfiguration;
import org.maia.amstrad.tape.gui.editor.ProgramEditorKit;
import org.maia.swing.text.pte.PlainTextEditor;
import org.maia.swing.text.pte.model.PlainTextFileDocument;

public class TapeReaderApplicationViewer extends Viewer {

	private PlainTextEditor textEditor;

	public TapeReaderApplicationViewer(TapeReaderApplicationView view) {
		super(view, "Amstrad Tape Reader", true);
		build();
		setJMenuBar(createMenuBar());
		maximize();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new ReadAudioFileAction()));
		menu.add(new JMenuItem(new QuitAction()));
		menuBar.add(menu);
		return menuBar;
	}

	public void openTaskConfigurationDialog() {
		getApplicationView().openTaskConfigurationDialog();
	}

	public TapeReaderTaskConfiguration getTaskConfiguration() {
		return getApplicationView().getTaskConfiguration();
	}

	public TapeReaderApplicationView getApplicationView() {
		return (TapeReaderApplicationView) getView();
	}

	public PlainTextEditor getTextEditor() {
		if (textEditor == null) {
			textEditor = createTextEditor();
		}
		return textEditor;
	}

	private PlainTextEditor createTextEditor() {
		PlainTextFileDocument.setFileNameExtensionFilter(
				new FileNameExtensionFilter("Text files (*.txt, *.bas, *.amd)", "txt", "bas", "amd"));
		// UIManager.put("TabbedPane.selected", Color.WHITE);
		Dimension screenSize = UIFactoryTape.getScreenSize();
		Dimension editorSize = new Dimension((int) Math.floor(screenSize.getWidth() * 0.8),
				(int) Math.floor(screenSize.getHeight() * 0.8));
		PlainTextEditor editor = new PlainTextEditor(new ProgramEditorKit(), editorSize, true);
		return editor;
	}

	private class ReadAudioFileAction extends AbstractAction {

		public ReadAudioFileAction() {
			super(UIResourcesTape.readAudioFileLabel);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			openTaskConfigurationDialog();
		}

	}

	private class QuitAction extends AbstractAction {

		public QuitAction() {
			super("Quit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			close();
		}

	}

}