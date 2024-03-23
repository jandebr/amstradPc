package org.maia.amstrad.tape.model.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.tape.model.AudioRange;

public class TapeProfile implements Serializable {

	private static final long serialVersionUID = -396706659339973964L;

	private List<TapeSection> sections;

	public TapeProfile() {
		this.sections = new Vector<TapeSection>();
	}

	public void save(File file) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(this);
		out.close();
	}

	public static TapeProfile load(File file) throws IOException {
		TapeProfile profile = null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		try {
			profile = (TapeProfile) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		in.close();
		return profile;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("Tape profile [\n");
		for (TapeSection section : getSections()) {
			sb.append("\t").append(section).append('\n');
		}
		sb.append("]");
		return sb.toString();
	}

	public void addSection(TapeSection section) {
		getSections().add(section);
	}

	public void lastSilenceSeparatesPrograms() {
		TapeSection silence = null;
		int i = getSections().size();
		while (silence == null && i > 0) {
			TapeSection section = getSections().get(--i);
			if (section.getType().equals(TapeSectionType.SILENCE))
				silence = section;
		}
		if (silence != null)
			silence.changeType(TapeSectionType.SILENCE_BETWEEN_PROGRAMS);
	}

	public AudioRange getAudioRange() {
		long sampleOffset = getSections().get(0).getStartPosition();
		long sampleEnd = getSections().get(getSections().size() - 1).getEndPosition();
		return new AudioRange(sampleOffset, sampleEnd - sampleOffset + 1L);
	}

	public List<TapeSection> getSectionsOfType(TapeSectionType type) {
		List<TapeSection> typedSections = new Vector<TapeSection>();
		for (TapeSection section : getSections()) {
			if (section.getType().equals(type)) {
				typedSections.add(section);
			}
		}
		return typedSections;
	}

	public List<TapeSection> getSections() {
		return sections;
	}

}