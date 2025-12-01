package org.maia.amstrad.tape.gui.editor;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import org.maia.amstrad.AmstradFactory;
import org.maia.svg.phylopic.PhylopicSvgImage;
import org.maia.svg.phylopic.db.PhylopicSvgOfflineDatabase;
import org.maia.svg.phylopic.gui.PhylopicSvgImageChooser;
import org.maia.svg.phylopic.gui.PhylopicSvgImageChooser.ImageChooserListener;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialogAdapter;
import org.maia.swing.dialog.ActionableDialogOption;

public class InsertPhylopicImageDialog {

	private PhylopicSvgImageChooser chooserPanel;

	private ActionableDialog dialog;

	private ChosenImageCallback callback;

	private static final ActionableDialogOption INSERT_OPTION = new InsertOption();

	public InsertPhylopicImageDialog() {
		this(null);
	}

	public InsertPhylopicImageDialog(Window windowOwner) {
		this(windowOwner, "Select image to insert");
	}

	public InsertPhylopicImageDialog(Window windowOwner, String windowTitle) {
		this.chooserPanel = createChooserPanel();
		this.dialog = createDialog(windowOwner, windowTitle);
	}

	protected PhylopicSvgImageChooser createChooserPanel() {
		PhylopicSvgOfflineDatabase db = AmstradFactory.getInstance().getAmstradContext().getPhylopicDatabase();
		PhylopicSvgImageChooser chooser = new PhylopicSvgImageChooser(db);
		chooser.addImageChooserListener(new ImageChooserListener() {

			@Override
			public void notifyNoImageChosen(PhylopicSvgImageChooser arg0) {
				getDialog().disableConfirmation();
			}

			@Override
			public void notifyImageChosen(PhylopicSvgImageChooser arg0) {
				getDialog().enableConfirmation();
			}
		});
		return chooser;
	}

	protected ActionableDialog createDialog(Window windowOwner, String windowTitle) {
		List<ActionableDialogOption> dialogOptions = Arrays.asList(INSERT_OPTION, ActionableDialog.CANCEL_OPTION);
		ActionableDialog dialog = new ActionableDialog(windowOwner, windowTitle, true, getChooserPanel(),
				dialogOptions);
		dialog.addListener(new ActionableDialogAdapter() {

			@Override
			public void dialogConfirmed(ActionableDialog dialog) {
				ChosenImageCallback callback = getCallback();
				if (callback != null) {
					callback.imageChosen(getChosenImage());
				}
			}

		});
		dialog.disableConfirmation();
		return dialog;
	}

	public void show(ChosenImageCallback callback) {
		setCallback(callback);
		getDialog().setVisible(true);
	}

	public void close() {
		getDialog().dispatchEvent(new WindowEvent(getDialog(), WindowEvent.WINDOW_CLOSING));
	}

	public PhylopicSvgImage getChosenImage() {
		return getChooserPanel().getChosenImage();
	}

	protected PhylopicSvgImageChooser getChooserPanel() {
		return chooserPanel;
	}

	public ActionableDialog getDialog() {
		return dialog;
	}

	private ChosenImageCallback getCallback() {
		return callback;
	}

	private void setCallback(ChosenImageCallback callback) {
		this.callback = callback;
	}

	public static interface ChosenImageCallback {

		void imageChosen(PhylopicSvgImage image);

	}

	private static class InsertOption extends ActionableDialogOption {

		public InsertOption() {
			super("INSERT", "Insert");
		}

		@Override
		public boolean isConfirmation() {
			return true;
		}

		@Override
		public boolean isCancellation() {
			return false;
		}

	}

}