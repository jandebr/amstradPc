package org.maia.amstrad.tape.gui;

import javax.swing.JPanel;

import org.maia.amstrad.tape.read.AudioFile;

public abstract class AudioFilePositionSource extends JPanel {

	private AudioFile audioFile;

	protected AudioFilePositionSource(AudioFile audioFile) {
		this.audioFile = audioFile;
	}

	public abstract int getWidthForDisplayRange();

	public AudioFile getAudioFile() {
		return audioFile;
	}

}