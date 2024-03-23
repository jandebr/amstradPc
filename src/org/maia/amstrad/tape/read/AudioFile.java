package org.maia.amstrad.tape.read;

import java.io.File;
import java.io.IOException;

/**
 * An audio file as the recording of an Amstrad tape
 */
public abstract class AudioFile {

	private File sourceFile;

	protected AudioFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String toString() {
		return getSourceFile().getPath();
	}

	public abstract void close() throws IOException;

	public abstract int getSampleRate();

	public abstract long getNumberOfSamples();

	public abstract short getSample(long index) throws IOException;

	public short getAbsoluteSample(long index) throws IOException {
		short sample = getSample(index);
		if (sample >= 0) {
			return sample;
		} else {
			return (short) -sample;
		}
	}

	public File getSourceFile() {
		return sourceFile;
	}

}
