package org.maia.amstrad.program;

import java.awt.Image;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.util.StringUtils;

public abstract class AmstradProgram implements Cloneable {

	private String programName;

	private String programDescription;

	private String authoringInformation;

	private String author;

	private int productionYear;

	private String nameOfTape;

	private int blocksOnTape;

	private List<UserControl> userControls;

	private List<ProgramImage> images;

	private AmstradMonitorMode preferredMonitorMode;

	protected AmstradProgram(String programName) {
		this.programName = programName;
		this.userControls = new Vector<UserControl>();
		this.images = new Vector<ProgramImage>();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AmstradProgram [programName='");
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
		builder.append(", preferredMonitorMode=");
		builder.append(preferredMonitorMode);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public AmstradProgram clone() {
		AmstradProgram clone = null;
		try {
			clone = (AmstradProgram) super.clone();
			clone.setUserControls(new Vector<UserControl>(getUserControls()));
			clone.setImages(new Vector<ProgramImage>(getImages()));
		} catch (CloneNotSupportedException e) {
			// not the case
		}
		return clone;
	}

	public void flush() {
		for (ProgramImage image : getImages()) {
			image.flush();
		}
	}

	public abstract void loadInto(AmstradPc amstradPc) throws AmstradProgramException;

	public void runWith(AmstradPc amstradPc) throws AmstradProgramException {
		loadInto(amstradPc);
		amstradPc.getBasicRuntime().run();
	}

	public boolean hasDescriptiveInfo() {
		return !StringUtils.isEmpty(getProgramDescription()) || !StringUtils.isEmpty(getAuthoringInformation())
				|| !StringUtils.isEmpty(getAuthor()) || getProductionYear() > 0
				|| !StringUtils.isEmpty(getNameOfTape()) || getBlocksOnTape() > 0 || !getUserControls().isEmpty();
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

	public void addImage(ProgramImage image) {
		getImages().add(image);
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

	public List<ProgramImage> getImages() {
		return images;
	}

	private void setImages(List<ProgramImage> images) {
		this.images = images;
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

	public static abstract class ProgramImage {

		private Image visual;

		private boolean visualFailedLoading;

		private String caption;

		protected ProgramImage(String caption) {
			this.caption = caption;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ProgramImage [caption=");
			builder.append(caption);
			builder.append("]");
			return builder.toString();
		}

		public void flush() {
			if (visual != null) {
				visual.flush();
				visual = null;
			}
		}

		protected abstract Image loadVisual() throws Exception;

		public Image getVisual() {
			if (visual == null && !visualFailedLoading) {
				try {
					visual = loadVisual();
				} catch (Exception e) {
					visualFailedLoading = true;
					System.err.println("Failed to load program image: " + e.toString());
				}
			}
			return visual;
		}

		public String getCaption() {
			return caption;
		}

	}

}