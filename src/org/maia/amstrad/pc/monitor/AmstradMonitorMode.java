package org.maia.amstrad.pc.monitor;

import org.maia.util.StringUtils;

public enum AmstradMonitorMode {

	COLOR,

	GREEN,

	GRAY;

	public static AmstradMonitorMode toMonitorMode(String str, AmstradMonitorMode defaultMode) {
		AmstradMonitorMode result = defaultMode;
		if (!StringUtils.isEmpty(str)) {
			try {
				result = AmstradMonitorMode.valueOf(str);
			} catch (IllegalArgumentException e) {
			}
		}
		return result;
	}

}