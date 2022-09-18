package org.maia.amstrad.program.repo.config;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class AmstradProgramRepositoryConfigurator extends Box {

	private AmstradProgramRepositoryConfiguration state;

	public AmstradProgramRepositoryConfigurator(AmstradProgramRepositoryConfiguration state) {
		super(BoxLayout.Y_AXIS);
		this.state = state;
		buildUI();
	}

	private void buildUI() {
		// TODO
	}

	public AmstradProgramRepositoryConfiguration getState() {
		return state;
	}

}