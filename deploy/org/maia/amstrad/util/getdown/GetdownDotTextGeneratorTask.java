package org.maia.amstrad.util.getdown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GetdownDotTextGeneratorTask extends Task {

	private File destinationFile;

	private File programSourceDir;

	private String programTargetBasePath = "";

	private String applicationBaseUrl = "https://localhost/amstradpc/";

	private static final String VAR_AMSTRADPC_APPBASE = "%AMSTRADPC_APPBASE%";

	private static final String VAR_AMSTRADPC_VERSION = "%AMSTRADPC_VERSION%";

	private static final String VAR_AMSTRADPC_PROGRAMS = "%AMSTRADPC_PROGRAMS%";

	private static final String VAR_AMSTRADPC_PROGRAMBASE = "%AMSTRADPC_PROGRAMBASE%";

	public GetdownDotTextGeneratorTask() {
	}

	public void execute() throws BuildException {
		if (getDestinationFile() == null)
			throw new BuildException("No destination file specified");
		try {
			PrintWriter out = new PrintWriter(getDestinationFile());
			writeGetdownDotText(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new BuildException("Failed to generate " + getDestinationFile().getAbsolutePath(), e);
		}
	}

	private void writeGetdownDotText(PrintWriter out) throws IOException {
		String version = generateVersionString();
		System.out.println("INFO version: " + version);
		BufferedReader reader = new BufferedReader(new FileReader("resources/dist/getdown.template.txt"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#") && line.contains(VAR_AMSTRADPC_PROGRAMS)) {
				if (getProgramSourceDir() != null && getProgramSourceDir().exists()) {
					String rootPath = getProgramSourceDir().getAbsolutePath().replace('\\', '/');
					System.out.println("INFO scanning program repository in " + rootPath);
					writeProgramsRecursively(getProgramSourceDir(), rootPath, out);
				} else {
					System.out.println("WARNING no programs inserted");
				}
			} else {
				out.println(replaceVariables(line, version));
			}
		}
		reader.close();
	}

	private void writeProgramsRecursively(File current, String rootPath, PrintWriter out) throws IOException {
		if (current.isFile()) {
			String path = current.getAbsolutePath().replace('\\', '/');
			if (path.startsWith(rootPath)) {
				path = path.substring(rootPath.length());
				if (path.startsWith("/"))
					path = path.substring(1);
				String resourcePath = new File(getProgramTargetBasePath(), path).getPath();
				out.println("resource = " + resourcePath);
				System.out.println("INFO adding resource " + resourcePath);
			}
		} else if (current.isDirectory()) {
			for (File child : current.listFiles()) {
				writeProgramsRecursively(child, rootPath, out);
			}
		}
	}

	private String replaceVariables(String line, String version) {
		if (line.contains(VAR_AMSTRADPC_APPBASE)) {
			line = line.replace(VAR_AMSTRADPC_APPBASE, getApplicationBaseUrl());
		}
		if (line.contains(VAR_AMSTRADPC_VERSION)) {
			line = line.replace(VAR_AMSTRADPC_VERSION, version);
		}
		if (line.contains(VAR_AMSTRADPC_PROGRAMBASE)) {
			line = line.replace(VAR_AMSTRADPC_PROGRAMBASE, getProgramTargetBasePath());
		}
		return line;
	}

	private String generateVersionString() {
		return new SimpleDateFormat("'v'yyyyMMdd'-'HHmm").format(new Date());
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public void setDestfile(File file) {
		this.destinationFile = file;
	}

	public File getProgramSourceDir() {
		return programSourceDir;
	}

	public void setProgramsource(File dir) {
		this.programSourceDir = dir;
	}

	public String getProgramTargetBasePath() {
		return programTargetBasePath;
	}

	public void setProgrambase(String basePath) {
		this.programTargetBasePath = basePath;
	}

	public String getApplicationBaseUrl() {
		return applicationBaseUrl;
	}

	public void setAppbase(String baseUrl) {
		this.applicationBaseUrl = baseUrl;
	}

}