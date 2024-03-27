package org.maia.amstrad.tape;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.tape.gui.TapeReaderApplicationViewer;
import org.maia.amstrad.tape.gui.UIFactoryTape;
import org.maia.amstrad.tape.task.PartialSourceCodeRestorer;

public class TapeReaderMain {

	/**
	 * Starts the Amstrad Tape Reader application
	 */
	public static void main(String[] args) throws Exception {
		TapeReaderApplicationViewer viewer = UIFactoryTape.createApplicationViewer(args);
		viewer.show();
		viewer.openTaskConfigurationDialog();
	}

	public static void restorePartially(File byteCodeFile) throws IOException {
		new PartialSourceCodeRestorer().restorePartially(byteCodeFile);
	}

}