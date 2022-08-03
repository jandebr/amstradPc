package org.maia.amstrad.program;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradProgram {

	private String programName;

	private String programDescription;

	private AmstradProgramType programType;

	private String author;

	private int productionYear;

	private String nameOfTape;

	private int blocksOnTape;

	private List<UserControl> userControls;

	private AmstradMonitorMode preferredMonitorMode;

	protected AmstradProgram(String programName) {
		this.programName = programName;
		this.userControls = new Vector<UserControl>();
	}

	public abstract void loadInto(AmstradPc amstradPc) throws AmstradProgramException;

	public void runWith(AmstradPc amstradPc) throws AmstradProgramException {
		loadInto(amstradPc);
		amstradPc.getBasicRuntime().run();
	}

	public boolean hasDescriptiveInfo() {
		return getProgramDescription() != null || getAuthor() != null || getProductionYear() > 0
				|| getNameOfTape() != null || getBlocksOnTape() > 0 || !getUserControls().isEmpty();
	}

	public void clearUserControls() {
		getUserControls().clear();
	}

	public void addUserControl(UserControl userControl) {
		getUserControls().add(userControl);
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

	public AmstradProgramType getProgramType() {
		return programType;
	}

	public void setProgramType(AmstradProgramType programType) {
		this.programType = programType;
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

	public static class UserControl {

		private String key;

		private String description;

	}

}