package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.tape.TapeReaderTask;
import org.maia.amstrad.tape.config.TapeReaderTaskConfiguration;
import org.maia.amstrad.tape.config.TapeReaderTaskConfigurationIO;
import org.maia.amstrad.tape.gui.config.TapeReaderTaskConfiguratorDialog;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialogAdapter;

@SuppressWarnings("serial")
public class TapeReaderApplicationView extends JPanel {

	private File taskConfigurationFile;

	private TapeReaderTaskConfiguration taskConfiguration;

	private JComponent centerComponent;

	private EmblemView emblemView;

	private ReadingView readingView;

	private ErrorView errorView;

	private AudioTapeIndexExtendedView resultsView;

	private int waveViewPixelsPerSecond = 20;

	public TapeReaderApplicationView(File taskConfigurationFile) throws IOException {
		super(new BorderLayout());
		this.taskConfigurationFile = taskConfigurationFile;
		this.taskConfiguration = TapeReaderTaskConfigurationIO.readFromFile(taskConfigurationFile);
		this.emblemView = new EmblemView();
		this.readingView = new ReadingView();
		this.errorView = new ErrorView();
		replaceCenterComponent(getEmblemView());
	}

	public void openTaskConfigurationDialog() {
		openTaskConfigurationDialog("Read audio file");
	}

	public void openTaskConfigurationDialog(String dialogTitle) {
		Window window = SwingUtilities.getWindowAncestor(this);
		TapeReaderTaskConfiguratorDialog dialog = new TapeReaderTaskConfiguratorDialog(window, dialogTitle,
				getTaskConfiguration().clone());
		dialog.addListener(new TaskConfigurationDialogHandler());
		dialog.setVisible(true);
	}

	private void cleanupOutputDirectory() {
		TapeReaderTaskConfiguration cfg = getTaskConfiguration();
		if (cfg != null) {
			File dir = cfg.getOutputDirectory();
			if (dir != null) {
				cleanupRecursively(dir);
				dir.mkdirs();
			}
		}
	}

	private void cleanupRecursively(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					cleanupRecursively(child);
				}
			}
			file.delete();
		}
	}

	private AudioTapeIndexExtendedView createResultsView(int maxWidth) throws Exception {
		AudioTapeIndexExtendedView view = null;
		TapeReaderTaskConfiguration cfg = getTaskConfiguration();
		if (cfg != null && cfg.getAudioFile() != null && cfg.getOutputDirectory() != null) {
			if (cfg.isCleanupOutputDirectory()) {
				cleanupOutputDirectory();
			}
			AmstradFactory.getInstance().getAmstradContext().setProgramRepositoryRootFolder(cfg.getOutputDirectory());
			TapeReaderTask task = new TapeReaderTask(cfg);
			task.readTape();
			AudioFileProfileExtendedView profileView = new AudioFileProfileExtendedView(cfg.getAudioFile(),
					task.getTapeProfile(), getWaveViewPixelsPerSecond(), maxWidth);
			view = new AudioTapeIndexExtendedView(task.getTapeIndex(), profileView);
		}
		return view;
	}

	private synchronized void updateResultsViewAsync() {
		replaceCenterComponent(getReadingView());
		UIFactoryTape.getApplicationViewer().getTextEditor().discardAllDocuments(); // fresh editor
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					setResultsView(createResultsView(UIFactoryTape.getScreenSize().width)); // can take time
				} catch (Exception e) {
					System.err.println(e);
					setResultsView(null);
				}
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (getResultsView() != null) {
							replaceCenterComponent(getResultsView());
						} else {
							replaceCenterComponent(getErrorView());
						}
					}
				});
			}
		}).start();
	}

	private synchronized void replaceCenterComponent(JComponent replacement) {
		JComponent center = getCenterComponent();
		if (center != null)
			remove(center);
		setCenterComponent(replacement);
		add(replacement, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	public File getTaskConfigurationFile() {
		return taskConfigurationFile;
	}

	public TapeReaderTaskConfiguration getTaskConfiguration() {
		return taskConfiguration;
	}

	private void setTaskConfiguration(TapeReaderTaskConfiguration cfg) {
		this.taskConfiguration = cfg;
	}

	private JComponent getCenterComponent() {
		return centerComponent;
	}

	private void setCenterComponent(JComponent component) {
		this.centerComponent = component;
	}

	private EmblemView getEmblemView() {
		return emblemView;
	}

	private ReadingView getReadingView() {
		return readingView;
	}

	private ErrorView getErrorView() {
		return errorView;
	}

	private AudioTapeIndexExtendedView getResultsView() {
		return resultsView;
	}

	private void setResultsView(AudioTapeIndexExtendedView view) {
		this.resultsView = view;
	}

	private int getWaveViewPixelsPerSecond() {
		return waveViewPixelsPerSecond;
	}

	private static class EmblemView extends JLabel {

		public EmblemView() {
			super(UIResourcesTape.amstradIcon);
		}

	}

	private static class ReadingView extends JLabel {

		public ReadingView() {
			super("Reading...", UIResourcesTape.spinnerIcon, SwingConstants.CENTER);
			setIconTextGap(8);
		}

	}

	private static class ErrorView extends JLabel {

		public ErrorView() {
			super(UIResourcesTape.errorIcon);
		}

	}

	private class TaskConfigurationDialogHandler extends ActionableDialogAdapter {

		public TaskConfigurationDialogHandler() {
		}

		@Override
		public void dialogConfirmed(ActionableDialog dialog) {
			updateTaskConfiguration((TapeReaderTaskConfiguratorDialog) dialog);
			updateResultsViewAsync();
		}

		private void updateTaskConfiguration(TapeReaderTaskConfiguratorDialog dialog) {
			TapeReaderTaskConfiguration cfg = dialog.getState();
			setTaskConfiguration(cfg);
			try {
				TapeReaderTaskConfigurationIO.writeToFile(cfg, getTaskConfigurationFile());
			} catch (IOException e) {
				System.err.println(e);
			}
		}

	}

}