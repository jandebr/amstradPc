package org.maia.amstrad.program.repo.config;

import java.io.File;

import org.maia.amstrad.program.repo.facet.FacetList;

public class AmstradProgramRepositoryConfiguration {

	private File rootFolder;

	private boolean sequenceNumberFiltered;

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

	public boolean isSequenceNumberFiltered() {
		return sequenceNumberFiltered;
	}

	public void setSequenceNumberFiltered(boolean filtered) {
		this.sequenceNumberFiltered = filtered;
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