package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.read.AudioFile;

public class AudioFileExtendedView extends JPanel {

	private AudioFile audioFile;

	private AudioFileView audioFileView;

	private AudioFilePositionView positionView;

	private JScrollPane scrollPane; // null when not needed

	public AudioFileExtendedView(AudioFile audioFile, int pixelsPerSecond, int maxWidth) throws IOException {
		super(new BorderLayout());
		this.audioFile = audioFile;
		setBackground(Color.WHITE);
		buildView(pixelsPerSecond, maxWidth);
	}

	private void buildView(int pixelsPerSecond, int maxWidth) throws IOException {
		int width = defineViewWidth(pixelsPerSecond);
		AudioFile audioFile = getAudioFile();
		AudioFileView afv = new AudioFileView(audioFile, width, UIResourcesTape.audioViewHeight);
		AudioFilePositionView posv = new AudioFilePositionView(audioFile);
		posv.track(afv);
		this.audioFileView = afv;
		this.positionView = posv;
		add(assembleView(afv, posv, width, maxWidth), BorderLayout.CENTER);
	}

	private JComponent assembleView(AudioFileView afv, AudioFilePositionView posv, int width, int maxWidth) {
		JComponent view = new JPanel(new BorderLayout());
		view.setBackground(getBackground());
		view.add(afv, BorderLayout.NORTH);
		view.add(posv, BorderLayout.SOUTH);
		if (width <= maxWidth) {
			return view;
		} else {
			setScrollPane(makeViewScrollable(view, maxWidth));
			return getScrollPane();
		}
	}

	private JScrollPane makeViewScrollable(JComponent view, int maxWidth) {
		JScrollPane scrollPane = new JScrollPane(view);
		scrollPane.setPreferredSize(new Dimension(maxWidth, UIResourcesTape.audioExtendedViewHeight));
		scrollPane.getViewport().setBackground(getBackground());
		scrollPane.setBorder(null);
		return scrollPane;
	}

	private int defineViewWidth(int pixelsPerSecond) throws IOException {
		AudioFile audioFile = getAudioFile();
		int seconds = (int) (audioFile.getNumberOfSamples() / audioFile.getSampleRate());
		return seconds * pixelsPerSecond;
	}

	public void makeVisible(AudioRange range) {
		JScrollPane scrollPane = getScrollPane();
		if (scrollPane != null && range != null) {
			scrollPane.getViewport().scrollRectToVisible(getAudioFileView().getViewBounds(range));
		}
	}

	public AudioFile getAudioFile() {
		return audioFile;
	}

	public AudioFileView getAudioFileView() {
		return audioFileView;
	}

	public AudioFilePositionView getPositionView() {
		return positionView;
	}

	private JScrollPane getScrollPane() {
		return scrollPane;
	}

	private void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

}