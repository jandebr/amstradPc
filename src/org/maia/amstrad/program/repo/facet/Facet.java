package org.maia.amstrad.program.repo.facet;

import javax.swing.Icon;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.util.StringUtils;

public abstract class Facet {

	public static final String VALUE_UNKNOWN = "?";

	protected Facet() {
	}

	public String getLabel() {
		return toExternalForm();
	}

	public abstract Icon getIcon();

	public final String valueOf(AmstradProgram program) {
		String value = extractValueFrom(program);
		if (StringUtils.isEmpty(value)) {
			return VALUE_UNKNOWN;
		} else {
			return value;
		}
	}

	protected abstract String extractValueFrom(AmstradProgram program);

	abstract String toExternalForm();

}