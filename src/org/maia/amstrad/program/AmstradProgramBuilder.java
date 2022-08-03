package org.maia.amstrad.program;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram.UserControl;

public class AmstradProgramBuilder implements AmstradMetaDataConstants {

	private AmstradProgram program;

	private AmstradProgramBuilder(AmstradProgram program) {
		this.program = program;
	}

	public static AmstradProgramBuilder createFor(AmstradProgram program) {
		return new AmstradProgramBuilder(program);
	}

	public AmstradProgramBuilder withProgramName(String programName) {
		getProgram().setProgramName(programName);
		return this;
	}

	public AmstradProgramBuilder withProgramDescription(String programDescription) {
		getProgram().setProgramDescription(programDescription);
		return this;
	}

	public AmstradProgramBuilder withProgramType(AmstradProgramType programType) {
		getProgram().setProgramType(programType);
		return this;
	}

	public AmstradProgramBuilder withAuthor(String author) {
		getProgram().setAuthor(author);
		return this;
	}

	public AmstradProgramBuilder withProductionYear(int productionYear) {
		getProgram().setProductionYear(productionYear);
		return this;
	}

	public AmstradProgramBuilder withNameOfTape(String nameOfTape) {
		getProgram().setNameOfTape(nameOfTape);
		return this;
	}

	public AmstradProgramBuilder withBlocksOnTape(int blocksOnTape) {
		getProgram().setBlocksOnTape(blocksOnTape);
		return this;
	}

	public AmstradProgramBuilder withPreferredMonitorMode(AmstradMonitorMode preferredMonitorMode) {
		getProgram().setPreferredMonitorMode(preferredMonitorMode);
		return this;
	}

	public AmstradProgramBuilder withUserControls(List<UserControl> userControls) {
		getProgram().clearUserControls();
		for (UserControl userControl : userControls) {
			getProgram().addUserControl(userControl);
		}
		return this;
	}

	public AmstradProgramBuilder loadAmstradMetaData(File file) throws IOException {
		if (file != null) {
			Reader reader = new FileReader(file);
			loadAmstradMetaData(reader);
			reader.close();
		}
		return this;
	}

	public AmstradProgramBuilder loadAmstradMetaData(Reader reader) throws IOException {
		if (reader != null) {
			Properties props = new Properties();
			props.load(reader);
			withProgramName(props.getProperty(AMD_NAME, getProgram().getProgramName()));
			withAuthor(props.getProperty(AMD_AUTHOR));
			// TODO more
		}
		return this;
	}

	public AmstradProgram build() {
		return getProgram();
	}

	private AmstradProgram getProgram() {
		return program;
	}

}