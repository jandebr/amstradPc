package org.maia.amstrad.tape.model;

public class TapeProgramMetaData implements Cloneable {

	private String author;

	private String year;

	private String tape;

	private String monitor;

	private String description;

	private String authoring;

	public TapeProgramMetaData() {
	}

	@Override
	public TapeProgramMetaData clone() {
		TapeProgramMetaData clone = new TapeProgramMetaData();
		clone.setAuthor(getAuthor());
		clone.setYear(getYear());
		clone.setTape(getTape());
		clone.setMonitor(getMonitor());
		clone.setDescription(getDescription());
		clone.setAuthoring(getAuthoring());
		return clone;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getTape() {
		return tape;
	}

	public void setTape(String tape) {
		this.tape = tape;
	}

	public String getMonitor() {
		return monitor;
	}

	public void setMonitor(String monitor) {
		this.monitor = monitor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthoring() {
		return authoring;
	}

	public void setAuthoring(String authoring) {
		this.authoring = authoring;
	}

}