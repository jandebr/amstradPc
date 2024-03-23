package org.maia.amstrad.tape.gui.config;

import java.awt.Window;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.tape.config.TapeReaderTaskConfiguration;
import org.maia.amstrad.tape.gui.config.TapeReaderTaskConfigurator.StateListener;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialogOption;

@SuppressWarnings("serial")
public class TapeReaderTaskConfiguratorDialog extends ActionableDialog implements StateListener {

	private static List<ActionableDialogOption> dialogOptions;

	static {
		dialogOptions = new Vector<ActionableDialogOption>(2);
		dialogOptions.add(OK_OPTION);
		dialogOptions.add(CANCEL_OPTION);
	}

	public TapeReaderTaskConfiguratorDialog(Window owner, String title, TapeReaderTaskConfiguration state) {
		super(owner, title, true, new TapeReaderTaskConfigurator(state, (int) (owner.getWidth() * 0.94)),
				dialogOptions);
		getConfigurator().addStateListener(this);
		updateConfirmationEnabled(state);
	}

	@Override
	public void stateChanged(TapeReaderTaskConfiguration state) {
		updateConfirmationEnabled(state);
	}

	protected void updateConfirmationEnabled(TapeReaderTaskConfiguration state) {
		boolean enabled = state.getAudioFile() != null && state.getOutputDirectory() != null;
		setConfirmationEnabled(enabled);
	}

	public TapeReaderTaskConfiguration getState() {
		return getConfigurator().getState();
	}

	protected TapeReaderTaskConfigurator getConfigurator() {
		return (TapeReaderTaskConfigurator) getMainComponent();
	}

}