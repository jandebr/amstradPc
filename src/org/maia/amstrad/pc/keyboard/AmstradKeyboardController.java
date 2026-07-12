package org.maia.amstrad.pc.keyboard;

public interface AmstradKeyboardController {

	void sendKeyboardEventsToComputer(boolean sendToComputer);

	void installKeyboardEventToComputerFilter(AmstradKeyboardEventFilter filter);

	void uninstallKeyboardEventToComputerFilter();

	void resetKeyModifiers();

}