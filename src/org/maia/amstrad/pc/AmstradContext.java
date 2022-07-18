package org.maia.amstrad.pc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import jemu.settings.Settings;

import org.maia.amstrad.pc.basic.BasicRuntime;

public abstract class AmstradContext {

	protected AmstradContext() {
	}

	public abstract AmstradSettings getUserSettings();

	public abstract PrintStream getConsoleOutputStream();

	public abstract PrintStream getConsoleErrorStream();

	public static void sleep(long milliseconds) {
		if (milliseconds > 0L) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

	public static CharSequence readTextFileContents(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder(2048);
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}
		reader.close();
		return sb;
	}

	public static byte[] readBinaryFileContents(File file) throws IOException {
		byte[] data = new byte[(int) file.length()];
		byte[] buffer = new byte[2048];
		int dataIndex = 0;
		FileInputStream in = new FileInputStream(file);
		int bytesRead = in.read(buffer);
		while (bytesRead >= 0) {
			System.arraycopy(buffer, 0, data, dataIndex, bytesRead);
			dataIndex += bytesRead;
			bytesRead = in.read(buffer);
		}
		in.close();
		return data;
	}

	public static void printInfoMessage(AmstradPc amstradPc, String message) {
		printInfoMessage(amstradPc, message, false, true);
	}

	public static void printInfoMessage(AmstradPc amstradPc, String message, boolean continuation, boolean close) {
		if (amstradPc.isStarted() && !amstradPc.isTerminated() && showMessagesAtPrompt()) {
			BasicRuntime rt = amstradPc.getBasicRuntime();
			if (!continuation)
				rt.keyboardType("'"); // REMark
			rt.keyboardType(message);
			if (close)
				rt.keyboardEnter();
		}
	}

	public static boolean showMessagesAtPrompt() {
		return Settings.getBoolean(Settings.PROMPT_MESSAGES, true);
	}

	public static void setShowMessagesAtPrompt(boolean show) {
		Settings.setBoolean(Settings.PROMPT_MESSAGES, show);
	}

}