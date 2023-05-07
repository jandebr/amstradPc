package org.maia.amstrad.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GetdownDotTextGeneratorTask extends Task {

	private File destinationFile;

	private File programRepositoryDir;

	private static final String PROGRAM_RESOURCE_PREFIX = "programs/";

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
		BufferedReader reader = new BufferedReader(new FileReader("resources/dist/getdown.template.txt"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("# %PROGRAMS%")) {
				if (getProgramRepositoryDir() != null && getProgramRepositoryDir().exists()) {
					String basePath = getProgramRepositoryDir().getAbsolutePath().replace('\\', '/');
					writeProgramsRecursively(getProgramRepositoryDir(), basePath, out);
				} else {
					System.out.println("WARNING no programs inserted");
				}
			} else {
				out.println(line);
			}
		}
		reader.close();
	}

	private void writeProgramsRecursively(File root, String basePath, PrintWriter out) throws IOException {
		if (root.isFile()) {
			String path = root.getAbsolutePath().replace('\\', '/');
			if (path.startsWith(basePath)) {
				path = path.substring(basePath.length());
				if (path.startsWith("/"))
					path = path.substring(1);
				out.println("resource = " + PROGRAM_RESOURCE_PREFIX + path);
			}
		} else if (root.isDirectory()) {
			for (File child : root.listFiles()) {
				writeProgramsRecursively(child, basePath, out);
			}
		}
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public void setDestfile(File file) {
		this.destinationFile = file;
	}

	public File getProgramRepositoryDir() {
		return programRepositoryDir;
	}

	public void setRepodir(File dir) {
		this.programRepositoryDir = dir;
	}

}