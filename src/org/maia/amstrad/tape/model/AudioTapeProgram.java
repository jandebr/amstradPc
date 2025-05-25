package org.maia.amstrad.tape.model;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.tape.decorate.AudioTapeBitDecorator;
import org.maia.amstrad.tape.decorate.BytecodeAudioDecorator;
import org.maia.amstrad.tape.decorate.SourcecodeBytecodeDecorator;
import org.maia.amstrad.tape.model.profile.TapeProfile;
import org.maia.amstrad.tape.read.AudioFile;
import org.maia.util.io.IOUtils;

public class AudioTapeProgram extends TapeProgram {

	private BasicSourceCode sourceCodeOnTape;

	private SourcecodeBytecodeDecorator sourceCodeDecorator;

	private BytecodeAudioDecorator byteCodeDecorator;

	private AudioFile audioFile;

	private AudioTapeBitDecorator audioDecorator;

	private TapeProfile profileOnTape;

	private File fileStoringSourceCodeOnTape;

	private File fileStoringModifiedSourceCode;

	private File fileStoringProgramMetadata;

	private AudioTapeProgram() {
	}

	public static AudioTapeProgram createFrom(TapeProgram program, BasicSourceCode sourceCodeOnTape,
			SourcecodeBytecodeDecorator sourceCodeDecorator, BytecodeAudioDecorator byteCodeDecorator,
			AudioFile audioFile, AudioTapeBitDecorator audioDecorator, TapeProfile profileOnTape) {
		AudioTapeProgram audioTapeProgram = new AudioTapeProgram();
		for (Block block : program.getBlocks()) {
			audioTapeProgram.addBlock(block);
		}
		audioTapeProgram.setSourceCodeOnTape(sourceCodeOnTape);
		audioTapeProgram.setSourceCodeDecorator(sourceCodeDecorator);
		audioTapeProgram.setByteCodeDecorator(byteCodeDecorator);
		audioTapeProgram.setAudioFile(audioFile);
		audioTapeProgram.setAudioDecorator(audioDecorator);
		audioTapeProgram.setProfileOnTape(profileOnTape);
		return audioTapeProgram;
	}

	public AmstradProgram asAmstradProgram() throws AmstradProgramException {
		File codeFile = hasModifiedSourceCode() ? getFileStoringModifiedSourceCode() : getFileStoringSourceCodeOnTape();
		File metadataFile = getFileStoringProgramMetadata();
		return AmstradFactory.getInstance().createBasicDescribedProgram(getProgramName(), codeFile, metadataFile);
	}

	public void saveModifiedSourceCode(BasicSourceCode modifiedSourceCode) throws IOException {
		IOUtils.writeTextFileContents(getFileStoringModifiedSourceCode(), modifiedSourceCode.getText());
	}

	public void revertSourceCodeModifications() {
		if (hasModifiedSourceCode()) {
			getFileStoringModifiedSourceCode().delete();
		}
	}

	public boolean hasModifiedSourceCode() {
		return getFileStoringModifiedSourceCode() != null && getFileStoringModifiedSourceCode().exists();
	}

	public BasicSourceCode getLatestSourceCode() {
		if (hasModifiedSourceCode()) {
			return getModifiedSourceCode();
		} else {
			return getSourceCodeOnTape();
		}
	}

	public BasicSourceCode getModifiedSourceCode() {
		BasicSourceCode modifiedSourceCode = null;
		if (hasModifiedSourceCode()) {
			try {
				modifiedSourceCode = new LocomotiveBasicSourceCode(
						IOUtils.readTextFileContents(getFileStoringModifiedSourceCode()));
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		return modifiedSourceCode;
	}

	public BasicSourceCode getSourceCodeOnTape() {
		return sourceCodeOnTape;
	}

	private void setSourceCodeOnTape(BasicSourceCode sourceCodeOnTape) {
		this.sourceCodeOnTape = sourceCodeOnTape;
	}

	public SourcecodeBytecodeDecorator getSourceCodeDecorator() {
		return sourceCodeDecorator;
	}

	private void setSourceCodeDecorator(SourcecodeBytecodeDecorator sourceCodeDecorator) {
		this.sourceCodeDecorator = sourceCodeDecorator;
	}

	public BytecodeAudioDecorator getByteCodeDecorator() {
		return byteCodeDecorator;
	}

	private void setByteCodeDecorator(BytecodeAudioDecorator byteCodeDecorator) {
		this.byteCodeDecorator = byteCodeDecorator;
	}

	public AudioFile getAudioFile() {
		return audioFile;
	}

	private void setAudioFile(AudioFile audioFile) {
		this.audioFile = audioFile;
	}

	public AudioTapeBitDecorator getAudioDecorator() {
		return audioDecorator;
	}

	private void setAudioDecorator(AudioTapeBitDecorator audioDecorator) {
		this.audioDecorator = audioDecorator;
	}

	public TapeProfile getProfileOnTape() {
		return profileOnTape;
	}

	private void setProfileOnTape(TapeProfile profileOnTape) {
		this.profileOnTape = profileOnTape;
	}

	public File getFileStoringSourceCodeOnTape() {
		return fileStoringSourceCodeOnTape;
	}

	public void setFileStoringSourceCodeOnTape(File file) {
		this.fileStoringSourceCodeOnTape = file;
	}

	public File getFileStoringModifiedSourceCode() {
		return fileStoringModifiedSourceCode;
	}

	public void setFileStoringModifiedSourceCode(File file) {
		this.fileStoringModifiedSourceCode = file;
	}

	public File getFileStoringProgramMetadata() {
		return fileStoringProgramMetadata;
	}

	public void setFileStoringProgramMetadata(File file) {
		this.fileStoringProgramMetadata = file;
	}

}