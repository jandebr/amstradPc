package org.maia.amstrad.program;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.AmstradProgram.UserControl;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.image.AmstradProgramImageSourcedByFile;
import org.maia.util.StringUtils;

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

	public AmstradProgramBuilder withImages(List<AmstradProgramImage> images) {
		getProgram().clearImages();
		for (AmstradProgramImage image : images) {
			getProgram().addImage(image);
		}
		return this;
	}

	public AmstradProgramBuilder withCoverImage(AmstradProgramImage coverImage) {
		getProgram().setCoverImage(coverImage);
		return this;
	}

	public AmstradProgramBuilder withFileReferences(List<FileReference> fileReferences) {
		getProgram().clearFileReferences();
		for (FileReference reference : fileReferences) {
			getProgram().addFileReference(reference);
		}
		return this;
	}

	public AmstradProgramBuilder withFlags(List<String> flags) {
		getProgram().clearFlags();
		for (String flag : flags) {
			getProgram().addFlag(flag);
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
			// Include first
			String includePath = props.getProperty(AMD_INCLUDE);
			if (includePath != null) {
				loadAmstradMetaData(new File(relativePath, includePath));
			}
			// Set or override included metadata
			AmstradProgramType programType = parseProgramTypeFromMetaData(props.getProperty(AMD_TYPE));
			if (programType != null)
				withProgramType(programType);
			BasicLanguage basicLanguage = parseBasicLanguageFromMetaData(props.getProperty(AMD_TYPE));
			if (basicLanguage != null)
				withBasicLanguage(basicLanguage);
			withProgramName(props.getProperty(AMD_NAME, getProgram().getProgramName()));
			String author = props.getProperty(AMD_AUTHOR);
			if (author != null)
				withAuthor(author);
			String year = props.getProperty(AMD_YEAR);
			if (year != null)
				withProductionYear(StringUtils.toInt(year, 0));
			String tape = props.getProperty(AMD_TAPE);
			if (tape != null)
				withNameOfTape(tape);
			String blocks = props.getProperty(AMD_BLOCKS);
			if (blocks != null)
				withBlocksOnTape(StringUtils.toInt(blocks, 0));
			String monitor = props.getProperty(AMD_MONITOR);
			if (monitor != null)
				withPreferredMonitorMode(AmstradMonitorMode.toMonitorMode(monitor, null));
			String description = props.getProperty(AMD_DESCRIPTION);
			if (description != null)
				withProgramDescription(description);
			String authoring = props.getProperty(AMD_AUTHORING);
			if (authoring != null)
				withAuthoringInformation(authoring);
			List<UserControl> controls = extractUserControlsFromMetaData(props);
			if (!controls.isEmpty())
				withUserControls(controls);
			List<AmstradProgramImage> images = extractProgramImagesFromMetaData(props, relativePath);
			if (!images.isEmpty())
				withImages(images);
			AmstradProgramImage cover = extractCoverImageFromMetaData(props, relativePath);
			if (cover != null)
				withCoverImage(cover);
			List<FileReference> fileRefs = extractFileReferencesFromMetaData(props, relativePath);
			if (!fileRefs.isEmpty())
				withFileReferences(fileRefs);
			List<String> flags = extractFlagsFromMetaData(props);
			if (flags != null)
				withFlags(flags);
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

	private List<UserControl> extractUserControlsFromMetaData(Properties props) {
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
		return userControls;
	}

	private List<AmstradProgramImage> extractProgramImagesFromMetaData(Properties props, File relativePath) {
		List<AmstradProgramImage> images = new Vector<AmstradProgramImage>();
		int i = 1;
		String fileRef = props.getProperty(AMD_IMAGES_PREFIX + '[' + i + ']' + AMD_IMAGES_SUFFIX_FILEREF);
		while (fileRef != null) {
			File file = new File(relativePath, fileRef);
			String caption = props.getProperty(AMD_IMAGES_PREFIX + '[' + i + ']' + AMD_IMAGES_SUFFIX_CAPTION);
			AmstradProgramImage image = new AmstradProgramImageSourcedByFile(file, caption);
			images.add(image);
			i++;
			fileRef = props.getProperty(AMD_IMAGES_PREFIX + '[' + i + ']' + AMD_IMAGES_SUFFIX_FILEREF);
		}
		return images;
	}

	private AmstradProgramImage extractCoverImageFromMetaData(Properties props, File relativePath) {
		AmstradProgramImage image = null;
		String fileRef = props.getProperty(AMD_COVER_IMAGE);
		if (fileRef != null) {
			File file = new File(relativePath, fileRef);
			image = new AmstradProgramImageSourcedByFile(file, "COVER");
		}
		return image;
	}

	private List<FileReference> extractFileReferencesFromMetaData(Properties props, File relativePath) {
		List<FileReference> fileReferences = new Vector<FileReference>();
		for (String key : props.stringPropertyNames()) {
			if (key.startsWith(AMD_FILEREFS_PREFIX)) {
				int i = key.indexOf('[');
				int j = key.indexOf(']');
				if (i >= 0 && j >= 0 && j > i) {
					String sourceFilename = key.substring(i + 1, j);
					String targetFilename = null;
					String metadataFilename = null;
					String value = props.getProperty(key);
					int d = value.indexOf(AMD_FILEREFS_DESCRIBED_BY);
					if (d >= 0) {
						targetFilename = value.substring(0, d).trim();
						metadataFilename = value.substring(d + AMD_FILEREFS_DESCRIBED_BY.length()).trim();
					} else {
						targetFilename = value.trim();
					}
					FileReference reference = new RelativePathFileReference(sourceFilename, relativePath,
							targetFilename, metadataFilename);
					fileReferences.add(reference);
				}
			}
		}
		return fileReferences;
	}

	private List<String> extractFlagsFromMetaData(Properties props) {
		String value = props.getProperty(AMD_FLAGS);
		if (value != null) {
			List<String> flags = new Vector<String>();
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				flags.add(st.nextToken().trim());
			}
			return flags;
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

	private static class RelativePathFileReference extends FileReference {

		private File relativePath;

		public RelativePathFileReference(String sourceFilename, File relativePath, String targetFilename,
				String metadataFilename) {
			super(sourceFilename, targetFilename, metadataFilename);
			this.relativePath = relativePath;
		}

		@Override
		protected File getFile(String filename) {
			return new File(getRelativePath(), filename);
		}

		public File getRelativePath() {
			return relativePath;
		}

	}

}