package org.maia.amstrad.system;

import java.util.Set;

public interface AmstradSystemScreenSet {

	AmstradSystemScreen getNativeScreen();

	AmstradSystemScreen getUnknownScreen();

	Set<AmstradSystemScreen> getCustomScreens();

}