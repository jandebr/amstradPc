package org.maia.amstrad.tape.gui.config;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.tape.config.TapeReaderTaskConfiguration;
import org.maia.amstrad.tape.gui.AudioFileExtendedView;
import org.maia.amstrad.tape.gui.AudioFileView;
import org.maia.amstrad.tape.gui.UIResourcesTape;
import org.maia.amstrad.tape.gui.AudioFileView.SelectionListener;
import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.read.AudioFile;
import org.maia.amstrad.tape.read.AudioWaveFile;
import org.maia.swing.file.FileInputField;
import org.maia.swing.file.FolderInputField;
import org.maia.swing.file.GenericFileInputField;
import org.maia.swing.file.GenericFileInputFieldListener;

@SuppressWarnings("serial")
public class TapeReaderTaskConfigurator extends JPanel implements SelectionListener {

	private TapeReaderTaskConfiguration state;

	private FileInputField audioFileField;

	private FolderInputField outputDirectoryField;

	private JCheckBox cleanupOutputDirectoryCheckBox;

	private JSpinner programFolderNumberOffsetSpinner;

	private JComponent centerComponent;

	private WavePlaceholder wavePlaceholder;

	private WaveLoadingView waveLoadingView;

	private AudioFileExtendedView waveView;

	private int waveViewPixelsPerSecond = 20;

	private List<StateListener> stateListeners;

	private static final String MONITOR_UNSPECIFIED = "<not specified>";

	public TapeReaderTaskConfigurator(TapeReaderTaskConfiguration state, int width) {
		super(new BorderLayout(0, 16));
		this.state = state;
		this.audioFileField = createAudioFileField();
		this.outputDirectoryField = createOutputDirectoryField();
		this.cleanupOutputDirectoryCheckBox = createCleanupOutputDirectoryCheckBox();
		this.programFolderNumberOffsetSpinner = createProgramFolderNumberOffsetSpinner();
		this.wavePlaceholder = new WavePlaceholder(width, UIResourcesTape.audioExtendedViewHeight);
		this.waveLoadingView = new WaveLoadingView(width, UIResourcesTape.audioExtendedViewHeight);
		this.stateListeners = new Vector<StateListener>();
		buildUI();
		updateWaveViewAsync();
	}

	private void buildUI() {
		add(createNorthComponent(), BorderLayout.NORTH);
		replaceCenterComponent(getWavePlaceholder());
	}

	private JComponent createNorthComponent() {
		Box box = Box.createVerticalBox();
		box.add(createFileInOutComponent());
		box.add(Box.createVerticalStrut(16));
		box.add(createMetaDataComponent());
		return box;
	}

	private JComponent createFileInOutComponent() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 4, 4, 4);
		c.gridy = 0;
		c.gridx = 0;
		panel.add(new JLabel("Input WAV file:"), c);
		c.gridx++;
		c.weightx = 1.0;
		panel.add(getAudioFileField(), c);
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		panel.add(new JLabel("Output folder:"), c);
		c.gridx++;
		panel.add(getOutputDirectoryField(), c);
		c.gridy++;
		c.gridx = 0;
		panel.add(new JLabel("Start index:"), c);
		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		panel.add(getProgramFolderNumberOffsetSpinner(), c);
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(getCleanupOutputDirectoryCheckBox(), c);
		panel.setBorder(BorderFactory.createTitledBorder("Files"));
		return panel;
	}

	private FileInputField createAudioFileField() {
		FileInputField field = new FileInputField();
		AudioFile af = getState().getAudioFile();
		if (af != null) {
			field.setFile(af.getSourceFile());
		} else {
			field.setCurrentDirectory(getState().getOutputDirectory());
		}
		field.setShowAbsolutePath(true);
		field.setFileChooserDialogTitle("Select the WAV file to process");
		field.setFileChooserFilter(new FileNameExtensionFilter("WAV Audio files", "wav"));
		field.addListener(new GenericFileInputFieldListener() {

			@Override
			public void fileSelectionChanged(GenericFileInputField inputField) {
				if (inputField.isCleared()) {
					getState().setAudioFile(null);
				} else {
					try {
						getState().setAudioFile(new AudioWaveFile(inputField.getFile()));
						if (getOutputDirectoryField().isCleared()) {
							getOutputDirectoryField().setCurrentDirectory(inputField.getFile().getParentFile());
						}
					} catch (IOException e) {
						System.err.println(e);
					}
				}
				getState().setSelectionInAudioFile(null); // initially no selection
				fireStateChanged();
				updateWaveViewAsync();
			}
		});
		return field;
	}

	private FolderInputField createOutputDirectoryField() {
		FolderInputField field = new FolderInputField(getState().getOutputDirectory());
		field.setShowAbsolutePath(true);
		field.setFileChooserDialogTitle("Select the root folder for generated programs");
		field.addListener(new GenericFileInputFieldListener() {

			@Override
			public void fileSelectionChanged(GenericFileInputField inputField) {
				getState().setOutputDirectory(inputField.getFile());
				fireStateChanged();
			}
		});
		return field;
	}

	private JCheckBox createCleanupOutputDirectoryCheckBox() {
		final JCheckBox checkBox = new JCheckBox("Cleanup output folder", getState().isCleanupOutputDirectory());
		checkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getState().setCleanupOutputDirectory(checkBox.isSelected());
				fireStateChanged();
			}
		});
		return checkBox;
	}

	private JSpinner createProgramFolderNumberOffsetSpinner() {
		final JSpinner spinner = new JSpinner(
				new SpinnerNumberModel(getState().getProgramFolderNumberOffset(), 1, 99, 1));
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int value = ((Integer) spinner.getValue()).intValue();
				getState().setProgramFolderNumberOffset(value);
				fireStateChanged();
			}
		});
		return spinner;
	}

	private JComponent createMetaDataComponent() {
		JPanel panel = new JPanel(new GridBagLayout());
		Insets in = new Insets(4, 4, 4, 4);
		Insets inSpacer = new Insets(4, 4, 4, 40);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		// Row 1
		c.insets = in;
		c.gridy = 0;
		c.gridx = 0;
		panel.add(new JLabel("Author"), c);
		c.insets = inSpacer;
		c.gridx++;
		c.weightx = 1.0;
		panel.add(createAuthorMetaDataField(), c);
		c.insets = in;
		c.gridx++;
		c.weightx = 0;
		panel.add(new JLabel("Year"), c);
		c.gridx++;
		c.weightx = 1.0;
		panel.add(createYearMetaDataField(), c);
		// Row 2
		c.weightx = 0;
		c.gridy++;
		c.gridx = 0;
		panel.add(new JLabel("Tape"), c);
		c.insets = inSpacer;
		c.gridx++;
		panel.add(createTapeMetaDataField(), c);
		c.insets = in;
		c.gridx++;
		panel.add(new JLabel("Monitor"), c);
		c.gridx++;
		panel.add(createMonitorMetaDataField(), c);
		// Row 3
		c.gridy++;
		c.gridx = 0;
		panel.add(new JLabel("Description"), c);
		c.insets = inSpacer;
		c.gridx++;
		panel.add(createDescriptionMetaDataField(), c);
		c.insets = in;
		c.gridx++;
		panel.add(new JLabel("Authoring"), c);
		c.gridx++;
		panel.add(createAuthoringMetaDataField(), c);
		panel.setBorder(BorderFactory.createTitledBorder("Default program metadata"));
		return panel;
	}

	private JTextField createAuthorMetaDataField() {
		final JTextField field = createMetaDataTextField(getState().getDefaultProgramMetaData().getAuthor());
		field.getDocument().addDocumentListener(new TextFieldListener() {

			@Override
			protected void notifyTextChange() {
				String text = field.getText();
				getState().getDefaultProgramMetaData().setAuthor(text.isEmpty() ? null : text);
			}
		});
		return field;
	}

	private JTextField createYearMetaDataField() {
		final JTextField field = createMetaDataTextField(getState().getDefaultProgramMetaData().getYear());
		field.getDocument().addDocumentListener(new TextFieldListener() {

			@Override
			protected void notifyTextChange() {
				String text = field.getText();
				getState().getDefaultProgramMetaData().setYear(text.isEmpty() ? null : text);
			}
		});
		return field;
	}

	private JTextField createTapeMetaDataField() {
		final JTextField field = createMetaDataTextField(getState().getDefaultProgramMetaData().getTape());
		field.getDocument().addDocumentListener(new TextFieldListener() {

			@Override
			protected void notifyTextChange() {
				String text = field.getText();
				getState().getDefaultProgramMetaData().setTape(text.isEmpty() ? null : text);
			}
		});
		return field;
	}

	private JComboBox<String> createMonitorMetaDataField() {
		Vector<String> items = createMonitorValueSet();
		final JComboBox<String> box = new JComboBox<String>(items);
		String monitor = getState().getDefaultProgramMetaData().getMonitor();
		if (items.contains(monitor)) {
			box.setSelectedItem(monitor);
		}
		box.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String item = box.getSelectedItem().toString();
				getState().getDefaultProgramMetaData().setMonitor(MONITOR_UNSPECIFIED.equals(item) ? null : item);
			}
		});
		return box;
	}

	private Vector<String> createMonitorValueSet() {
		Vector<String> monitors = new Vector<String>();
		monitors.add(MONITOR_UNSPECIFIED);
		for (AmstradMonitorMode mode : AmstradMonitorMode.values()) {
			monitors.add(mode.name());
		}
		return monitors;
	}

	private JTextField createDescriptionMetaDataField() {
		final JTextField field = createMetaDataTextField(getState().getDefaultProgramMetaData().getDescription());
		field.getDocument().addDocumentListener(new TextFieldListener() {

			@Override
			protected void notifyTextChange() {
				String text = field.getText();
				getState().getDefaultProgramMetaData().setDescription(text.isEmpty() ? null : text);
			}
		});
		return field;
	}

	private JTextField createAuthoringMetaDataField() {
		final JTextField field = createMetaDataTextField(getState().getDefaultProgramMetaData().getAuthoring());
		field.getDocument().addDocumentListener(new TextFieldListener() {

			@Override
			protected void notifyTextChange() {
				String text = field.getText();
				getState().getDefaultProgramMetaData().setAuthoring(text.isEmpty() ? null : text);
			}
		});
		return field;
	}

	private JTextField createMetaDataTextField(String initialValue) {
		return new JTextField(initialValue);
	}

	private AudioFileExtendedView createWaveView() {
		AudioFileExtendedView view = null;
		AudioFile audioFile = getState().getAudioFile();
		AudioRange range = getState().getSelectionInAudioFile();
		if (audioFile != null) {
			try {
				view = new AudioFileExtendedView(audioFile, getWaveViewPixelsPerSecond(), getWidth());
				view.getAudioFileView().setSelection(range);
				view.getAudioFileView().addSelectionListener(this);
				view.makeVisible(range);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		return view;
	}

	private void updateWaveViewAsync() {
		final AudioFile audioFile = getState().getAudioFile();
		if (audioFile != null) {
			replaceCenterComponent(getWaveLoadingView());
			new Thread(new Runnable() {

				@Override
				public void run() {
					final AudioFileExtendedView view = createWaveView(); // can take time
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (audioFile.equals(getState().getAudioFile())) { // still actual
								setWaveView(view);
								replaceCenterComponent(getWaveView());
							}
						}
					});
				}
			}).start();
		} else {
			replaceCenterComponent(getWavePlaceholder());
		}
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

	@Override
	public void selectionChanged(AudioFileView view) {
		getState().setSelectionInAudioFile(view.getSelection());
		fireStateChanged();
	}

	public void addStateListener(StateListener listener) {
		getStateListeners().add(listener);
	}

	public void removeStateListener(StateListener listener) {
		getStateListeners().remove(listener);
	}

	protected void fireStateChanged() {
		for (StateListener listener : getStateListeners()) {
			listener.stateChanged(getState());
		}
	}

	public TapeReaderTaskConfiguration getState() {
		return state;
	}

	private FileInputField getAudioFileField() {
		return audioFileField;
	}

	private FolderInputField getOutputDirectoryField() {
		return outputDirectoryField;
	}

	private JCheckBox getCleanupOutputDirectoryCheckBox() {
		return cleanupOutputDirectoryCheckBox;
	}

	private JSpinner getProgramFolderNumberOffsetSpinner() {
		return programFolderNumberOffsetSpinner;
	}

	private JComponent getCenterComponent() {
		return centerComponent;
	}

	private void setCenterComponent(JComponent component) {
		this.centerComponent = component;
	}

	private WavePlaceholder getWavePlaceholder() {
		return wavePlaceholder;
	}

	private WaveLoadingView getWaveLoadingView() {
		return waveLoadingView;
	}

	private AudioFileExtendedView getWaveView() {
		return waveView;
	}

	private void setWaveView(AudioFileExtendedView waveView) {
		this.waveView = waveView;
	}

	private int getWaveViewPixelsPerSecond() {
		return waveViewPixelsPerSecond;
	}

	private List<StateListener> getStateListeners() {
		return stateListeners;
	}

	public static interface StateListener {

		void stateChanged(TapeReaderTaskConfiguration state);

	}

	private static abstract class RigidAreaLabel extends JLabel {

		protected RigidAreaLabel() {
		}

		protected RigidAreaLabel(String text, Icon icon, int horizontalAlignment) {
			super(text, icon, horizontalAlignment);
		}

		protected void fixSize(int width, int height) {
			setSize(width, height);
			setMinimumSize(getSize());
			setPreferredSize(getSize());
			setMaximumSize(getSize());
		}

	}

	private static class WavePlaceholder extends RigidAreaLabel {

		private Image backgroundImage;

		public WavePlaceholder(int width, int height) {
			super();
			this.backgroundImage = ((ImageIcon) UIResourcesTape.wavesIcon).getImage();
			fixSize(width, height);
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
		}

	}

	private static class WaveLoadingView extends RigidAreaLabel {

		public WaveLoadingView(int width, int height) {
			super("Loading...", UIResourcesTape.spinnerIcon, SwingConstants.CENTER);
			setIconTextGap(8);
			fixSize(width, height);
		}

	}

	private static abstract class TextFieldListener implements DocumentListener {

		protected TextFieldListener() {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			notifyTextChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			notifyTextChange();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		protected abstract void notifyTextChange();

	}

}