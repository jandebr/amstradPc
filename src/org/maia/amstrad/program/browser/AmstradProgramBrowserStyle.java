package org.maia.amstrad.program.browser;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public abstract class AmstradProgramBrowserStyle {

	private String displayName;

	protected AmstradProgramBrowserStyle(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int hashCode() {
		return getDisplayName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmstradProgramBrowserStyle other = (AmstradProgramBrowserStyle) obj;
		return getDisplayName().equals(other.getDisplayName());
	}

	public AmstradProgramBrowser createProgramBrowser(AmstradPc amstradPc) {
		AmstradProgramRepository repository = AmstradFactory.getInstance().createProgramRepository();
		return createProgramBrowser(amstradPc, repository);
	}

	protected abstract AmstradProgramBrowser createProgramBrowser(AmstradPc amstradPc,
			AmstradProgramRepository repository);

	public String getDisplayName() {
		return displayName;
	}

}