package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public abstract class ScreenshotAction extends FileChooserAction {

	protected ScreenshotAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		amstradPc.getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		makeScreenshot();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (!isTriggeredByMenuKeyBindings()) {
			if (invokeOn(event)) {
				makeScreenshot();
			}
		}
	}

	protected abstract boolean invokeOn(AmstradKeyboardEvent keyEvent);

	private void makeScreenshot() {
		BufferedImage image = captureImage();
		int returnValue = getFileChooser().showSaveDialog(getDisplayComponent());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			updateCurrentDirectoryFromSelectedFile();
			File file = getSelectedFileWithExtension(".png");
			try {
				ImageIO.write(image, "png", file);
			} catch (Exception e) {
				System.err.println("Failed to export screenshot: " + e.getMessage());
				showErrorMessageDialog("Error saving image", "Failed to save " + file.getName(), e);
			}
		}
	}

	protected abstract BufferedImage captureImage();

	@Override
	protected JFileChooser buildFileChooser(File currentDirectory) {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		fileChooser.setDialogTitle(getName());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Screenshot files (*.png)", "png");
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

}