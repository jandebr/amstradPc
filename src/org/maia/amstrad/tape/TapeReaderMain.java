package org.maia.amstrad.tape;

import org.maia.amstrad.tape.gui.TapeReaderApplicationViewer;
import org.maia.amstrad.tape.gui.UIFactoryTape;

public class TapeReaderMain {

	/**
	 * Starts the Amstrad Tape Reader application
	 */
	public static void main(String[] args) throws Exception {
		TapeReaderApplicationViewer viewer = UIFactoryTape.createApplicationViewer(args);
		viewer.show();
		viewer.openTaskConfigurationDialog();
	}

}