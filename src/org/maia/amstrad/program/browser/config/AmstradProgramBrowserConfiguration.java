package org.maia.amstrad.program.browser.config;

import org.maia.amstrad.program.browser.AmstradProgramBrowserStyle;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;

public class AmstradProgramBrowserConfiguration {

	private AmstradProgramRepositoryConfiguration repositoryConfiguration;

	private AmstradProgramBrowserStyle style;

	public AmstradProgramBrowserConfiguration(AmstradProgramRepositoryConfiguration repositoryConfiguration) {
		this.repositoryConfiguration = repositoryConfiguration;
	}

	public AmstradProgramRepositoryConfiguration getRepositoryConfiguration() {
		return repositoryConfiguration;
	}

	public AmstradProgramBrowserStyle getStyle() {
		return style;
	}

	public void setStyle(AmstradProgramBrowserStyle style) {
		this.style = style;
	}

}