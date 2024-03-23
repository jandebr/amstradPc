package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.maia.amstrad.tape.gui.AudioFileProfileView.TapeSectionListener;
import org.maia.amstrad.tape.gui.AudioTapeIndexToolBar.ToolBarListener;
import org.maia.amstrad.tape.gui.AudioTapeIndexView.IndexSelectionListener;
import org.maia.amstrad.tape.model.AudioTapeIndex;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.amstrad.tape.model.profile.TapeSection;

@SuppressWarnings("serial")
public class AudioTapeIndexExtendedView extends JPanel
		implements IndexSelectionListener, TapeSectionListener, ToolBarListener {

	private AudioTapeIndex tapeIndex;

	private AudioTapeIndexView indexView;

	private AudioTapeIndexToolBar indexToolBar;

	private AudioFileProfileExtendedView profileExtendedView;

	private JComponent detailPane;

	public AudioTapeIndexExtendedView(AudioTapeIndex tapeIndex, AudioFileProfileExtendedView profileExtendedView) {
		super(new BorderLayout());
		this.tapeIndex = tapeIndex;
		this.indexView = buildIndexView();
		this.indexToolBar = buildIndexToolBar();
		this.profileExtendedView = profileExtendedView;
		profileExtendedView.showTapeIndex(tapeIndex);
		profileExtendedView.addSectionListener(this);
		buildView();
	}

	private void buildView() {
		add(getIndexToolBar(), BorderLayout.NORTH);
		add(getIndexView(), BorderLayout.WEST);
		add(getProfileExtendedView(), BorderLayout.SOUTH);
		updateDetailPane(buildStubDetailPane());
	}

	private AudioTapeIndexView buildIndexView() {
		AudioTapeIndexView view = new AudioTapeIndexView(getTapeIndex());
		view.addSelectionListener(this);
		return view;
	}

	private AudioTapeIndexToolBar buildIndexToolBar() {
		AudioTapeIndexToolBar toolBar = new AudioTapeIndexToolBar(getIndexView());
		toolBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		toolBar.addListener(this);
		return toolBar;
	}

	private JComponent buildProgramDetailPane(AudioTapeProgram program) {
		SourceCodeView scv = new SourceCodeView(program.getLatestSourceCode());
		return scv;
	}

	private JComponent buildStubDetailPane() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(UIResourcesTape.amstradIcon), BorderLayout.CENTER);
		return panel;
	}

	public void updateDetailPane() {
		AudioTapeProgram program = getIndexView().getSelectedProgram();
		if (program != null) {
			updateDetailPane(buildProgramDetailPane(program));
		}
	}

	private void updateDetailPane(JComponent newDetailPane) {
		if (getDetailPane() != null) {
			remove(getDetailPane());
		}
		setDetailPane(newDetailPane);
		add(newDetailPane, BorderLayout.CENTER);
		revalidate();
	}

	@Override
	public void indexSelectionUpdate(AudioTapeIndexView source) {
		AudioTapeProgram program = source.getSelectedProgram();
		if (program != null) {
			getProfileExtendedView().changeSelection(program.getProfileOnTape(), true);
			updateDetailPane(buildProgramDetailPane(program));
		} else {
			getProfileExtendedView().clearSelection();
			updateDetailPane(buildStubDetailPane());
		}
	}

	@Override
	public void sectionClicked(TapeSection section, AudioFileProfileView source) {
		AudioTapeProgram program = getTapeIndex().findProgramContaining(section);
		if (program != null) {
			getIndexView().changeSelection(program);
		} else {
			getIndexView().clearSelection();
		}
	}

	@Override
	public void notifyModifiedSourceCodeSaved(AudioTapeProgram tapeProgram) {
		notifyModifiedSourceCode(tapeProgram);
	}

	@Override
	public void notifyModifiedSourceCodeReverted(AudioTapeProgram tapeProgram) {
		notifyModifiedSourceCode(tapeProgram);
	}

	private void notifyModifiedSourceCode(AudioTapeProgram tapeProgram) {
		getIndexView().repaint(); // update program edit status icon
		if (tapeProgram.equals(getIndexView().getSelectedProgram())) {
			updateDetailPane();
		}
	}

	public AudioTapeIndex getTapeIndex() {
		return tapeIndex;
	}

	public AudioTapeIndexView getIndexView() {
		return indexView;
	}

	public AudioTapeIndexToolBar getIndexToolBar() {
		return indexToolBar;
	}

	public AudioFileProfileExtendedView getProfileExtendedView() {
		return profileExtendedView;
	}

	public JComponent getDetailPane() {
		return detailPane;
	}

	private void setDetailPane(JComponent detailPane) {
		this.detailPane = detailPane;
	}

}