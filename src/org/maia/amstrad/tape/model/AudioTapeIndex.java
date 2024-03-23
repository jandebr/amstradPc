package org.maia.amstrad.tape.model;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.tape.model.profile.TapeProfile;
import org.maia.amstrad.tape.model.profile.TapeSection;
import org.maia.amstrad.tape.read.AudioFile;

public class AudioTapeIndex {

	private AudioFile audioFile;

	private List<AudioTapeProgram> programs;

	public AudioTapeIndex(AudioFile audioFile) {
		this.audioFile = audioFile;
		this.programs = new Vector<AudioTapeProgram>();
	}

	public void addProgram(AudioTapeProgram program) {
		getPrograms().add(program);
	}

	public int size() {
		return getPrograms().size();
	}

	public AudioTapeProgram findProgramContaining(TapeSection section) {
		for (AudioTapeProgram program : getPrograms()) {
			TapeProfile profileOnTape = program.getProfileOnTape();
			if (profileOnTape != null) {
				if (profileOnTape.getSections().contains(section))
					return program;
			}
		}
		return null;
	}

	public AudioFile getAudioFile() {
		return audioFile;
	}

	public List<AudioTapeProgram> getPrograms() {
		return programs;
	}

}