package org.maia.amstrad.program;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.util.StringUtils;

public abstract class AmstradProgram implements Cloneable {

	private AmstradProgramType programType;

	private BasicLanguage basicLanguage;

	private String programName;

	private String programDescription;

	private String authoringInformation;

	private String author;

	private int productionYear;

	private String nameOfTape;

	private int blocksOnTape;

	private List<UserControl> userControls;

	private List<AmstradProgramImage> images;

	private List<FileReference> fileReferences;

	private List<String> flags;

	private AmstradMonitorMode preferredMonitorMode;

	private AmstradProgramImage coverImage;

	private AmstradProgramPayload payload;

	protected AmstradProgram(AmstradProgramType programType, String programName) {
		if (programType == null)
			throw new NullPointerException("Unidentified program type");
		if (programName == null)
			throw new NullPointerException("Unidentified program name");
		this.programType = programType;
		this.programName = programName;
		this.userControls = new Vector<UserControl>();
		this.images = new Vector<AmstradProgramImage>();
		this.fileReferences = new Vector<FileReference>();
		this.flags = new Vector<String>();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AmstradProgram [programType=");
		builder.append(programType.name());
		builder.append(", basicLanguage='");
		builder.append(basicLanguage.name());
		builder.append("', programName='");
		builder.append(programName);
		builder.append("', programDescription='");
		builder.append(programDescription);
		builder.append("', authoringInformation='");
		builder.append(authoringInformation);
		builder.append("', author='");
		builder.append(author);
		builder.append("', productionYear=");
		builder.append(productionYear);
		builder.append(", nameOfTape='");
		builder.append(nameOfTape);
		builder.append("', blocksOnTape=");
		builder.append(blocksOnTape);
		builder.append(", userControls=");
		builder.append(userControls);
		builder.append(", images=");
		builder.append(images);
		builder.append(", fileReferences=");
		builder.append(fileReferences);
		builder.append(", flags=");
		builder.append(flags);
		builder.append(", preferredMonitorMode=");
		builder.append(preferredMonitorMode);
		builder.append(", coverImage=");
		builder.append(coverImage);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public AmstradProgram clone() {
		AmstradProgram clone = null;
		try {
			clone = (AmstradProgram) super.clone();
			clone.setUserControls(new Vector<UserControl>(getUserControls()));
			clone.setImages(new Vector<AmstradProgramImage>(getImages()));
			clone.setFileReferences(new Vector<FileReference>(getFileReferences()));
			clone.setFlags(new Vector<String>(getFlags()));
		} catch (CloneNotSupportedException e) {
			// not the case
		}
		return clone;
	}

	public void dispose() {
		setPayload(null);
		for (AmstradProgramImage image : getImages()) {
			image.dispose();
		}
		// no dispose on cover image because these are managed externally
	}

	public boolean hasDescriptiveInfo() {
		return !StringUtils.isEmpty(getProgramDescription()) || !StringUtils.isEmpty(getAuthoringInformation())
				|| !StringUtils.isEmpty(getAuthor()) || getProductionYear() > 0 || !StringUtils.isEmpty(getNameOfTape())
				|| getBlocksOnTape() > 0 || !getUserControls().isEmpty();
	}

	public void clearUserControls() {
		getUserControls().clear();
	}

	public void addUserControl(UserControl userControl) {
		getUserControls().add(userControl);
	}

	public void clearImages() {
		getImages().clear();
	}

	public void addImage(AmstradProgramImage image) {
		getImages().add(image);
	}

	public void clearFileReferences() {
		getFileReferences().clear();
	}

	public void addFileReference(FileReference fileReference) {
		getFileReferences().add(fileReference);
	}

	public FileReference lookupFileReference(String sourceFilename) {
		for (FileReference reference : getFileReferences()) {
			if (reference.getSourceFilename().equals(sourceFilename))
				return reference;
		}
		return null;
	}

	public void clearFlags() {
		getFlags().clear();
	}

	public void addFlag(String flag) {
		getFlags().add(flag);
	}

	public AmstradProgramType getProgramType() {
		return programType;
	}

	public void setProgramType(AmstradProgramType programType) {
		this.programType = programType;
	}

	public BasicLanguage getBasicLanguage() {
		return basicLanguage;
	}

	public void setBasicLanguage(BasicLanguage basicLanguage) {
		this.basicLanguage = basicLanguage;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramDescription() {
		return programDescription;
	}

	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}

	public String getAuthoringInformation() {
		return authoringInformation;
	}

	public void setAuthoringInformation(String authoringInformation) {
		this.authoringInformation = authoringInformation;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getProductionYear() {
		return productionYear;
	}

	public void setProductionYear(int productionYear) {
		this.productionYear = productionYear;
	}

	public String getNameOfTape() {
		return nameOfTape;
	}

	public void setNameOfTape(String nameOfTape) {
		this.nameOfTape = nameOfTape;
	}

	public int getBlocksOnTape() {
		return blocksOnTape;
	}

	public void setBlocksOnTape(int blocksOnTape) {
		this.blocksOnTape = blocksOnTape;
	}

	public AmstradMonitorMode getPreferredMonitorMode() {
		return preferredMonitorMode;
	}

	public void setPreferredMonitorMode(AmstradMonitorMode preferredMonitorMode) {
		this.preferredMonitorMode = preferredMonitorMode;
	}

	public List<UserControl> getUserControls() {
		return userControls;
	}

	private void setUserControls(List<UserControl> userControls) {
		this.userControls = userControls;
	}

	public List<AmstradProgramImage> getImages() {
		return images;
	}

	private void setImages(List<AmstradProgramImage> images) {
		this.images = images;
	}

	public AmstradProgramImage getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(AmstradProgramImage coverImage) {
		this.coverImage = coverImage;
	}

	public List<FileReference> getFileReferences() {
		return fileReferences;
	}

	private void setFileReferences(List<FileReference> fileReferences) {
		this.fileReferences = fileReferences;
	}

	public List<String> getFlags() {
		return flags;
	}

	private void setFlags(List<String> flags) {
		this.flags = flags;
	}

	public AmstradProgramPayload getPayload() throws AmstradProgramException {
		if (payload == null) {
			setPayload(loadPayload());
		}
		return payload;
	}

	protected abstract AmstradProgramPayload loadPayload() throws AmstradProgramException;

	private void setPayload(AmstradProgramPayload payload) {
		this.payload = payload;
	}

	public static class UserControl {

		private String key;

		private String description;

		private String heading;

		public UserControl(String key, String description) {
			this.key = key;
			this.description = description;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("UserControl [key='");
			builder.append(key);
			builder.append("', description='");
			builder.append(description);
			builder.append("', heading='");
			builder.append(heading);
			builder.append("']");
			return builder.toString();
		}

		public String getKey() {
			return key;
		}

		public String getDescription() {
			return description;
		}

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

	}

	public static abstract class FileReference {

		private String sourceFilename;

		private String targetFilename;

		private String metadataFilename;

		protected FileReference(String sourceFilename, String targetFilename) {
			this(sourceFilename, targetFilename, null);
		}

		protected FileReference(String sourceFilename, String targetFilename, String metadataFilename) {
			this.sourceFilename = sourceFilename;
			this.targetFilename = targetFilename;
			this.metadataFilename = metadataFilename;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("FileReference [sourceFilename=");
			builder.append(sourceFilename);
			builder.append(", targetFilename=");
			builder.append(targetFilename);
			builder.append(", metadataFilename=");
			builder.append(metadataFilename);
			builder.append("]");
			return builder.toString();
		}

		public File getTargetFile() {
			if (getTargetFilename() != null) {
				return getFile(getTargetFilename());
			} else {
				return null;
			}
		}

		public File getMetadataFile() {
			if (getMetadataFilename() != null) {
				return getFile(getMetadataFilename());
			} else {
				return null;
			}
		}

		protected abstract File getFile(String filename);

		public String getSourceFilename() {
			return sourceFilename;
		}

		public String getTargetFilename() {
			return targetFilename;
		}

		public String getMetadataFilename() {
			return metadataFilename;
		}

	}

}