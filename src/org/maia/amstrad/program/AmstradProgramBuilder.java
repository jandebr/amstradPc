package org.maia.amstrad.program;

import java.awt.Image;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram.ProgramImage;
import org.maia.amstrad.program.AmstradProgram.UserControl;
import org.maia.amstrad.util.StringUtils;

public class AmstradProgramBuilder implements AmstradProgramMetaDataConstants {

	private AmstradProgram program;

	private AmstradProgramBuilder(AmstradProgram program) {
		this.program = program;
	}

	public static AmstradProgramBuilder createFor(AmstradProgram program) {
		return new AmstradProgramBuilder(program);
	}

	public AmstradProgramBuilder withProgramType(AmstradProgramType programType) {
		getProgram().setProgramType(programType);
		return this;
	}

	public AmstradProgramBuilder withBasicLanguage(BasicLanguage basicLanguage) {
		getProgram().setBasicLanguage(basicLanguage);
		return this;
	}

	public AmstradProgramBuilder withProgramName(String programName) {
		getProgram().setProgramName(programName);
		return this;
	}

	public AmstradProgramBuilder withProgramDescription(String programDescription) {
		getProgram().setProgramDescription(programDescription);
		return this;
	}

	public AmstradProgramBuilder withAuthoringInformation(String authoringInformation) {
		getProgram().setAuthoringInformation(authoringInformation);
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

	public AmstradProgramBuilder withImages(List<ProgramImage> images) {
		getProgram().clearImages();
		for (ProgramImage image : images) {
			getProgram().addImage(image);
		}
		return this;
	}

	public AmstradProgramBuilder loadAmstradMetaData(File file) throws IOException {
		if (file != null) {
			Reader reader = new FileReader(file);
			loadAmstradMetaData(reader, file.getParentFile());
			reader.close();
		}
		return this;
	}

	public AmstradProgramBuilder loadAmstradMetaData(Reader reader, File relativePath) throws IOException {
		if (reader != null) {
			Properties props = new Properties();
			props.load(reader);
			AmstradProgramType programType = parseProgramTypeFromMetaData(props.getProperty(AMD_TYPE));
			if (programType != null)
				withProgramType(programType);
			BasicLanguage basicLanguage = parseBasicLanguageFromMetaData(props.getProperty(AMD_TYPE));
			if (basicLanguage != null)
				withBasicLanguage(basicLanguage);
			withProgramName(props.getProperty(AMD_NAME, getProgram().getProgramName()));
			withAuthor(props.getProperty(AMD_AUTHOR));
			withProductionYear(StringUtils.toInt(props.getProperty(AMD_YEAR), 0));
			withNameOfTape(props.getProperty(AMD_TAPE));
			withBlocksOnTape(StringUtils.toInt(props.getProperty(AMD_BLOCKS), 0));
			withPreferredMonitorMode(StringUtils.toMonitorMode(props.getProperty(AMD_MONITOR), null));
			withProgramDescription(props.getProperty(AMD_DESCRIPTION));
			withAuthoringInformation(props.getProperty(AMD_AUTHORING));
			// User controls
			List<UserControl> userControls = new Vector<UserControl>();
			int i = 1;
			String key = props.getProperty(AMD_CONTROLS_PREFIX + '[' + i + ']' + AMD_CONTROLS_SUFFIX_KEY);
			while (key != null) {
				String desc = props.getProperty(AMD_CONTROLS_PREFIX + '[' + i + ']' + AMD_CONTROLS_SUFFIX_DESCRIPTION);
				String heading = props.getProperty(AMD_CONTROLS_PREFIX + '[' + i + ']' + AMD_CONTROLS_SUFFIX_HEADING);
				UserControl control = new UserControl(key, desc);
				control.setHeading(heading);
				userControls.add(control);
				i++;
				key = props.getProperty(AMD_CONTROLS_PREFIX + '[' + i + ']' + AMD_CONTROLS_SUFFIX_KEY);
			}
			withUserControls(userControls);
			// Images
			List<ProgramImage> images = new Vector<ProgramImage>();
			i = 1;
			String fileRef = props.getProperty(AMD_IMAGES_PREFIX + '[' + i + ']' + AMD_IMAGES_SUFFIX_FILEREF);
			while (fileRef != null) {
				File file = new File(relativePath, fileRef);
				String caption = props.getProperty(AMD_IMAGES_PREFIX + '[' + i + ']' + AMD_IMAGES_SUFFIX_CAPTION);
				ProgramImage image = new FileReferenceProgramImage(file, caption);
				images.add(image);
				i++;
				fileRef = props.getProperty(AMD_IMAGES_PREFIX + '[' + i + ']' + AMD_IMAGES_SUFFIX_FILEREF);
			}
			withImages(images);
		}
		return this;
	}

	private AmstradProgramType parseProgramTypeFromMetaData(String value) {
		if (AMD_TYPE_LOCOMOTIVE_BASIC_PROGRAM.equals(value)) {
			return AmstradProgramType.BASIC_PROGRAM;
		} else {
			return null;
		}
	}

	private BasicLanguage parseBasicLanguageFromMetaData(String value) {
		if (AMD_TYPE_LOCOMOTIVE_BASIC_PROGRAM.equals(value)) {
			return BasicLanguage.LOCOMOTIVE_BASIC;
		} else {
			return null;
		}
	}

	public AmstradProgram build() {
		return getProgram();
	}

	private AmstradProgram getProgram() {
		return program;
	}

	private static class FileReferenceProgramImage extends ProgramImage {

		private File file;

		public FileReferenceProgramImage(File file, String caption) {
			super(caption);
			this.file = file;
		}

		@Override
		protected Image loadVisual() throws IOException {
			return ImageIO.read(getFile());
		}

		public File getFile() {
			return file;
		}

	}

}