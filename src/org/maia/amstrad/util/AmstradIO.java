package org.maia.amstrad.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AmstradIO {

	private AmstradIO() {
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

	public static void writeTextFileContents(File file, CharSequence text) throws IOException {
		PrintWriter pw = new PrintWriter(file);
		pw.print(text);
		pw.flush();
		pw.close();
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

	public static void writeBinaryFileContents(File file, byte[] data) throws IOException {
		writeBinaryFileContents(file, data, 0, data.length);
	}

	public static void writeBinaryFileContents(File file, byte[] data, int offset, int length) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(data, offset, length);
		out.flush();
		out.close();
	}

	public static File stripExtension(File file) {
		String name = file.getName();
		int i = name.lastIndexOf('.');
		if (i > 0) {
			return new File(file.getParentFile(), name.substring(0, i));
		} else {
			return file;
		}
	}

}