package jemu.ui;

import java.awt.event.KeyEvent;

public interface KeyDispatcherFilter {

	boolean accept(KeyEvent event);

}