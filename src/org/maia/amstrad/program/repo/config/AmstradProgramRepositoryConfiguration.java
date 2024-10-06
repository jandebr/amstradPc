package org.maia.amstrad.program.repo.config;

import java.io.File;

import org.maia.amstrad.program.repo.facet.FacetList;

public class AmstradProgramRepositoryConfiguration {

	private File rootFolder;

	private boolean hideSequenceNumbers;

	private boolean searchByProgramName;

	private String searchString;

	private boolean faceted;

	private FacetList facets;

	public AmstradProgramRepositoryConfiguration() {
	}

	public File getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(File rootFolder) {
		if (!rootFolder.isDirectory())
			throw new IllegalArgumentException("The root folder must be a directory");
		this.rootFolder = rootFolder;
	}

	public boolean isHideSequenceNumbers() {
		return hideSequenceNumbers;
	}

	public void setHideSequenceNumbers(boolean hide) {
		this.hideSequenceNumbers = hide;
	}

	public boolean isSearchByProgramName() {
		return searchByProgramName;
	}

	public void setSearchByProgramName(boolean byName) {
		this.searchByProgramName = byName;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String str) {
		this.searchString = str;
	}

	public boolean isFaceted() {
		return faceted;
	}

	public void setFaceted(boolean faceted) {
		this.faceted = faceted;
	}

	public FacetList getFacets() {
		return facets;
	}

	public void setFacets(FacetList facets) {
		this.facets = facets;
	}

}