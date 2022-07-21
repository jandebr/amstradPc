package jemu.ui;

import java.awt.Graphics2D;

public interface SecondaryDisplaySource {

	void notifyPrimaryDisplaySourceResolution(int width, int height);

	void renderOntoDisplay(Graphics2D g2, int width, int height);

	void dispose();

}