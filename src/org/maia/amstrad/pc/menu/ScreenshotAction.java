package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.AmstradPc;

public class ScreenshotAction extends FileChooserAction {

	public ScreenshotAction(AmstradPc amstradPc) {
		this(amstradPc, "Capture screen image...");
	}

	public ScreenshotAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public ScreenshotAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		BufferedImage image = getAmstradPc().makeScreenshot(includeMonitorEffect());
		int returnValue = getFileChooser().showSaveDialog(getAmstradPc().getDisplayPane());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = getSelectedFileWithExtension(".png");
			try {
				ImageIO.write(image, "png", file);
			} catch (Exception e) {
				System.err.println("Failed to export screenshot: " + e.getMessage());
				showErrorMessageDialog("Error saving image", "Failed to save " + file.getName(), e);
			}
		}
	}

	@Override
	protected JFileChooser buildFileChooser() {
		JFileChooser fileChooser = new JFileChooser(getHomeDirectory());
		fileChooser.setDialogTitle(getName());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Screenshot files (*.png)", "png");
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

	protected boolean includeMonitorEffect() {
		return false;
	}

}