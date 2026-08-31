package org.maia.amstrad.program.browser.config;

import org.maia.amstrad.program.repo.AmstradProgramRepository;

public enum AmstradProgramBrowserCoverImageOption {

	/**
	 * Cover images are originating from the program repository
	 * 
	 * @see AmstradProgramRepository.Node#getCoverImage()
	 */
	REPOSITORY,

	/**
	 * Only applies to folders. The featured program inside a folder lends its cover image to the folder
	 * 
	 * @see AmstradProgramRepository.FolderNode#getFeaturedProgramNode()
	 */
	FEATURED,

	/**
	 * The program browser either shows no cover image or autogenerates a (placeholder) cover image
	 */
	NONE;

}