package org.maia.amstrad.tape.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.maia.amstrad.program.AmstradProgramMetaDataConstants;
import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.model.TapeProgramMetaData;
import org.maia.amstrad.tape.read.AudioWaveFile;

public class TapeReaderTaskConfigurationIO implements AmstradProgramMetaDataConstants {

	private static final String KEY_AUDIO_FILE = "audioFile";

	private static final String KEY_AUDIO_RANGE_OFFSET = "audioRange.offset";

	private static final String KEY_AUDIO_RANGE_LENGTH = "audioRange.length";

	private static final String KEY_OUTPUT_DIRECTORY = "outputDirectory";

	private static final String KEY_PROGRAM_FOLDER_NUMBER_OFFSET = "programFolderNumberOffset";

	private static final String KEY_CLEANUP_OUTPUT_DIRECTORY = "cleanupOutputDirectory";

	private static final String KEY_METADATA = "metadata";

	private static final String KEY_METADATA_AUTHOR = KEY_METADATA + "." + AMD_AUTHOR.toLowerCase();

	private static final String KEY_METADATA_YEAR = KEY_METADATA + "." + AMD_YEAR.toLowerCase();

	private static final String KEY_METADATA_TAPE = KEY_METADATA + "." + AMD_TAPE.toLowerCase();

	private static final String KEY_METADATA_MONITOR = KEY_METADATA + "." + AMD_MONITOR.toLowerCase();

	private static final String KEY_METADATA_DESCRIPTION = KEY_METADATA + "." + AMD_DESCRIPTION.toLowerCase();

	private static final String KEY_METADATA_AUTHORING = KEY_METADATA + "." + AMD_AUTHORING.toLowerCase();

	private TapeReaderTaskConfigurationIO() {
	}

	public static void writeToFile(TapeReaderTaskConfiguration cfg, File file) throws IOException {
		Properties props = toProperties(cfg);
		OutputStream out = new FileOutputStream(file);
		props.store(out, null);
		out.close();
	}

	public static TapeReaderTaskConfiguration readFromFile(File file) throws IOException {
		if (file != null && file.exists()) {
			Properties props = new Properties();
			InputStream in = new FileInputStream(file);
			props.load(in);
			in.close();
			return fromProperties(props);
		} else {
			return new TapeReaderTaskConfiguration();
		}
	}

	public static void applyStartupArguments(TapeReaderTaskConfiguration cfg, String[] args) throws IOException {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				File file = new File(args[i]);
				if (file.isFile() && file.getName().toLowerCase().endsWith(".wav")) {
					cfg.setAudioFile(new AudioWaveFile(file));
				} else if (file.isDirectory()) {
					cfg.setOutputDirectory(file);
				}
			}
		}
	}

	private static Properties toProperties(TapeReaderTaskConfiguration cfg) {
		Properties props = new Properties();
		if (cfg.getAudioFile() != null) {
			props.setProperty(KEY_AUDIO_FILE, cfg.getAudioFile().getSourceFile().getAbsolutePath());
		}
		if (cfg.getSelectionInAudioFile() != null) {
			props.setProperty(KEY_AUDIO_RANGE_OFFSET, String.valueOf(cfg.getSelectionInAudioFile().getSampleOffset()));
			props.setProperty(KEY_AUDIO_RANGE_LENGTH, String.valueOf(cfg.getSelectionInAudioFile().getSampleLength()));
		}
		if (cfg.getOutputDirectory() != null) {
			props.setProperty(KEY_OUTPUT_DIRECTORY, cfg.getOutputDirectory().getAbsolutePath());
		}
		props.setProperty(KEY_CLEANUP_OUTPUT_DIRECTORY, String.valueOf(cfg.isCleanupOutputDirectory()));
		props.setProperty(KEY_PROGRAM_FOLDER_NUMBER_OFFSET, String.valueOf(cfg.getProgramFolderNumberOffset()));
		props.putAll(toProperties(cfg.getDefaultProgramMetaData()));
		return props;
	}

	private static Properties toProperties(TapeProgramMetaData md) {
		Properties props = new Properties();
		if (md.getAuthor() != null) {
			props.setProperty(KEY_METADATA_AUTHOR, md.getAuthor());
		}
		if (md.getYear() != null) {
			props.setProperty(KEY_METADATA_YEAR, md.getYear());
		}
		if (md.getTape() != null) {
			props.setProperty(KEY_METADATA_TAPE, md.getTape());
		}
		if (md.getMonitor() != null) {
			props.setProperty(KEY_METADATA_MONITOR, md.getMonitor());
		}
		if (md.getDescription() != null) {
			props.setProperty(KEY_METADATA_DESCRIPTION, md.getDescription());
		}
		if (md.getAuthoring() != null) {
			props.setProperty(KEY_METADATA_AUTHORING, md.getAuthoring());
		}
		return props;
	}

	private static TapeReaderTaskConfiguration fromProperties(Properties props) throws IOException {
		TapeReaderTaskConfiguration cfg = new TapeReaderTaskConfiguration();
		String path = props.getProperty(KEY_AUDIO_FILE);
		if (path != null) {
			try {
				cfg.setAudioFile(new AudioWaveFile(new File(path)));
			} catch (FileNotFoundException e) {
			}
		}
		cfg.setSelectionInAudioFile(extractAudioRange(props));
		path = props.getProperty(KEY_OUTPUT_DIRECTORY);
		if (path != null) {
			File dir = new File(path);
			if (dir.exists() || dir.mkdirs()) {
				cfg.setOutputDirectory(dir);
			}
		}
		cfg.setCleanupOutputDirectory(Boolean.parseBoolean(props.getProperty(KEY_CLEANUP_OUTPUT_DIRECTORY)));
		String offset = props.getProperty(KEY_PROGRAM_FOLDER_NUMBER_OFFSET);
		if (offset != null) {
			try {
				cfg.setProgramFolderNumberOffset(Integer.parseInt(offset));
			} catch (NumberFormatException e) {
			}
		}
		cfg.setDefaultProgramMetaData(extractMetaData(props));
		return cfg;
	}

	private static AudioRange extractAudioRange(Properties props) {
		AudioRange selection = null;
		String offsetStr = props.getProperty(KEY_AUDIO_RANGE_OFFSET);
		String lengthStr = props.getProperty(KEY_AUDIO_RANGE_LENGTH);
		if (offsetStr != null && lengthStr != null) {
			try {
				long offset = Long.valueOf(offsetStr);
				long length = Long.valueOf(lengthStr);
				selection = new AudioRange(offset, length);
			} catch (NumberFormatException e) {
				System.err.println(e);
			}
		}
		return selection;
	}

	private static TapeProgramMetaData extractMetaData(Properties props) {
		TapeProgramMetaData md = new TapeProgramMetaData();
		md.setAuthor(props.getProperty(KEY_METADATA_AUTHOR));
		md.setYear(props.getProperty(KEY_METADATA_YEAR));
		md.setTape(props.getProperty(KEY_METADATA_TAPE));
		md.setMonitor(props.getProperty(KEY_METADATA_MONITOR));
		md.setDescription(props.getProperty(KEY_METADATA_DESCRIPTION));
		md.setAuthoring(props.getProperty(KEY_METADATA_AUTHORING));
		return md;
	}

}