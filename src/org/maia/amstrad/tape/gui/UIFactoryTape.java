package org.maia.amstrad.tape.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.tape.config.TapeReaderTaskConfigurationIO;
import org.maia.amstrad.tape.model.AudioTapeIndex;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.amstrad.tape.model.ByteSequence;
import org.maia.amstrad.tape.model.profile.TapeProfile;
import org.maia.amstrad.tape.read.AudioFile;

public class UIFactoryTape {

	private static TapeReaderApplicationViewer applicationViewer;

	private UIFactoryTape() {
	}

	public static TapeReaderApplicationViewer createApplicationViewer(String[] args) throws IOException {
		File taskConfigurationFile = new File("task.ini");
		TapeReaderApplicationView view = createApplicationView(taskConfigurationFile);
		TapeReaderTaskConfigurationIO.applyStartupArguments(view.getTaskConfiguration(), args);
		TapeReaderApplicationViewer viewer = new TapeReaderApplicationViewer(view);
		setApplicationViewer(viewer);
		return viewer;
	}

	public static Viewer createAudioFileViewer(AudioFile audioFile, int pixelsPerSecond, boolean exitOnClose)
			throws IOException {
		JComponent view = createAudioFileExtendedView(audioFile, pixelsPerSecond);
		String title = audioFile.getSourceFile().getName();
		Viewer viewer = new Viewer(view, title, exitOnClose);
		viewer.build();
		return viewer;
	}

	public static Viewer createAudioFileProfileViewer(AudioFile audioFile, TapeProfile tapeProfile, int pixelsPerSecond,
			boolean exitOnClose) throws IOException {
		JComponent view = createExtendedProfileView(audioFile, tapeProfile, pixelsPerSecond);
		String title = "Tape profile of " + audioFile;
		Viewer viewer = new Viewer(view, title, exitOnClose);
		viewer.build();
		return viewer;
	}

	public static Viewer createAudioTapeIndexViewer(AudioTapeIndex tapeIndex, boolean exitOnClose) {
		JComponent view = new AudioTapeIndexView(tapeIndex);
		String title = "Index of " + tapeIndex.getAudioFile().getSourceFile().getName();
		Viewer viewer = new Viewer(view, title, exitOnClose);
		viewer.build();
		return viewer;
	}

	public static Viewer createAudioTapeIndexExtendedViewer(AudioTapeIndex tapeIndex, TapeProfile tapeProfile,
			int pixelsPerSecond, boolean exitOnClose) throws IOException {
		JComponent view = new AudioTapeIndexExtendedView(tapeIndex,
				createExtendedProfileView(tapeIndex.getAudioFile(), tapeProfile, pixelsPerSecond));
		String title = "Index of " + tapeIndex.getAudioFile().getSourceFile().getName();
		Viewer viewer = new Viewer(view, title, exitOnClose);
		viewer.build();
		return viewer;
	}

	public static Viewer createCodeInspectorViewer(AudioTapeProgram audioTapeProgram, boolean exitOnClose) {
		JComponent view = new CodeInspectorView(audioTapeProgram);
		String title = "Code inspection of " + audioTapeProgram.getProgramName();
		Viewer viewer = new Viewer(view, title, exitOnClose);
		viewer.build();
		return viewer;
	}

	public static Viewer createSourceCodeViewer(BasicSourceCode sourceCode, String programName, boolean exitOnClose) {
		JComponent view = new SourceCodeView(sourceCode);
		String title = "Source code of " + programName;
		Viewer viewer = new Viewer(view, title, exitOnClose);
		viewer.build();
		return viewer;
	}

	public static Viewer createByteCodeViewer(ByteSequence byteCode, String programName, boolean exitOnClose) {
		JComponent view = new ByteCodeView(byteCode);
		String title = "Byte code of " + programName;
		Viewer viewer = new Viewer(view, title, exitOnClose);
		viewer.build();
		return viewer;
	}

	private static TapeReaderApplicationView createApplicationView(File taskConfigurationFile) throws IOException {
		return new TapeReaderApplicationView(taskConfigurationFile);
	}

	private static AudioFileExtendedView createAudioFileExtendedView(AudioFile audioFile, int pixelsPerSecond)
			throws IOException {
		int maxWidth = (int) (getScreenSize().getWidth() * 0.94);
		return new AudioFileExtendedView(audioFile, pixelsPerSecond, maxWidth);
	}

	private static AudioFileProfileExtendedView createExtendedProfileView(AudioFile audioFile, TapeProfile tapeProfile,
			int pixelsPerSecond) throws IOException {
		int maxWidth = (int) (getScreenSize().getWidth() * 0.94);
		return new AudioFileProfileExtendedView(audioFile, tapeProfile, pixelsPerSecond, maxWidth);
	}

	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static TapeReaderApplicationViewer getApplicationViewer() {
		return applicationViewer;
	}

	private static void setApplicationViewer(TapeReaderApplicationViewer viewer) {
		applicationViewer = viewer;
	}

}