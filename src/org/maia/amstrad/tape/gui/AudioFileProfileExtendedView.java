package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.maia.amstrad.tape.gui.AudioFileProfileView.TapeSectionListener;
import org.maia.amstrad.tape.model.AudioTapeIndex;
import org.maia.amstrad.tape.model.profile.TapeProfile;
import org.maia.amstrad.tape.read.AudioFile;

public class AudioFileProfileExtendedView extends JPanel {

	private AudioFile audioFile;

	private TapeProfile tapeProfile;

	private AudioFileProfileView profileView;

	private AudioFileProfileLegendView legendView;

	private AudioFilePositionView positionView;

	public AudioFileProfileExtendedView(AudioFile audioFile, TapeProfile tapeProfile, int pixelsPerSecond, int maxWidth)
			throws IOException {
		super(new BorderLayout());
		this.audioFile = audioFile;
		this.tapeProfile = tapeProfile;
		buildView(pixelsPerSecond, maxWidth);
	}

	private void buildView(int pixelsPerSecond, int maxWidth) throws IOException {
		setBackground(Color.BLACK);
		add(buildLegendView(), BorderLayout.NORTH);
		add(buildCoreView(pixelsPerSecond, maxWidth), BorderLayout.CENTER);
	}

	private AudioFileProfileLegendView buildLegendView() {
		AudioFileProfileLegendView legend = new AudioFileProfileLegendView();
		this.legendView = legend;
		return legend;
	}

	private JComponent buildCoreView(int pixelsPerSecond, int maxWidth) throws IOException {
		int width = defineViewWidth(pixelsPerSecond);
		AudioFile audioFile = getAudioFile();
		AudioFileProfileView pv = new AudioFileProfileView(audioFile, getTapeProfile(), getLegendView(), width,
				UIResourcesTape.audioProfileViewHeight);
		AudioFilePositionView posv = new AudioFilePositionView(audioFile);
		posv.track(pv);
		this.profileView = pv;
		this.positionView = posv;
		return assembleCoreView(pv, posv, width, maxWidth);
	}

	private JComponent assembleCoreView(AudioFileProfileView pv, AudioFilePositionView posv, int width, int maxWidth) {
		JComponent view = new JPanel(new BorderLayout());
		view.setBackground(getBackground());
		view.add(pv, BorderLayout.NORTH);
		view.add(posv, BorderLayout.SOUTH);
		if (width <= maxWidth) {
			return view;
		} else {
			return makeViewScrollable(view, maxWidth);
		}
	}

	private JScrollPane makeViewScrollable(JComponent view, int maxWidth) {
		JScrollPane scrollPane = new JScrollPane(view);
		scrollPane.setPreferredSize(new Dimension(maxWidth, UIResourcesTape.audioProfileViewHeight
				+ UIResourcesTape.audioPositionViewHeight + 20));
		scrollPane.getViewport().setBackground(getBackground());
		scrollPane.setBorder(null);
		return scrollPane;
	}

	private int defineViewWidth(int pixelsPerSecond) throws IOException {
		AudioFile audioFile = getAudioFile();
		int seconds = (int) (audioFile.getNumberOfSamples() / audioFile.getSampleRate());
		return seconds * pixelsPerSecond;
	}

	public void showTapeIndex(AudioTapeIndex tapeIndex) {
		getProfileView().showTapeIndex(tapeIndex);
	}

	public void hideTapeIndex() {
		getProfileView().hideTapeIndex();
	}

	public void clearSelection() {
		getProfileView().clearSelection();
	}

	public void changeSelection(TapeProfile selection, boolean scrollToSelected) {
		getProfileView().setSelectedProfile(selection);
		if (selection != null && scrollToSelected) {
			getProfileView().scrollToVisibleSelection();
		}
	}

	public void addSectionListener(TapeSectionListener listener) {
		getProfileView().addSectionListener(listener);
	}

	public void removeSectionListener(TapeSectionListener listener) {
		getProfileView().removeSectionListener(listener);
	}

	public AudioFile getAudioFile() {
		return audioFile;
	}

	public TapeProfile getTapeProfile() {
		return tapeProfile;
	}

	public AudioFileProfileView getProfileView() {
		return profileView;
	}

	public AudioFileProfileLegendView getLegendView() {
		return legendView;
	}

	public AudioFilePositionView getPositionView() {
		return positionView;
	}

}