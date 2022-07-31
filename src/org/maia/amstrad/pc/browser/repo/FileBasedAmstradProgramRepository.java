package org.maia.amstrad.pc.browser.repo;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.basic.BasicRuntime;

public class FileBasedAmstradProgramRepository extends AmstradProgramRepository {

	private FileBasedFolderNode rootNode;

	private boolean folderPerProgram;

	private AmstradMonitorMode defaultMonitorMode;

	public FileBasedAmstradProgramRepository(File rootFolder, boolean folderPerProgram,
			AmstradMonitorMode defaultMonitorMode) {
		if (!rootFolder.isDirectory())
			throw new IllegalArgumentException("The root must be a directory");
		this.rootNode = new FileBasedFolderNode(rootFolder);
		this.folderPerProgram = folderPerProgram;
		this.defaultMonitorMode = defaultMonitorMode;
	}

	private static boolean containsSubFolders(File folder) {
		boolean result = false;
		File[] files = folder.listFiles();
		int i = 0;
		while (!result && i < files.length)
			result = files[i++].isDirectory();
		return result;
	}

	private static boolean isProgramFile(File file) {
		return BasicRuntime.isBasicSourceFile(file);
	}

	private static boolean isRemasteredProgramFile(File file) {
		return isProgramFile(file) && file.getName().toLowerCase().contains("remastered");
	}

	private static File selectProgramFileInFolder(File folder) {
		File result = null;
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (isProgramFile(file)) {
				if (result == null) {
					result = file;
				} else if (isRemasteredProgramFile(file) && !isRemasteredProgramFile(result)) {
					result = file;
				}
			}
		}
		return result;
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	public boolean isFolderPerProgram() {
		return folderPerProgram;
	}

	public AmstradMonitorMode getDefaultMonitorMode() {
		return defaultMonitorMode;
	}

	private class FileBasedFolderNode extends FolderNode {

		private File folder;

		public FileBasedFolderNode(File folder) {
			super(folder.getName());
			this.folder = folder;
		}

		@Override
		protected List<Node> listChildNodes() {
			List<FileBasedFolderNode> childFolderNodes = new Vector<FileBasedFolderNode>();
			List<FileBasedProgramNode> childProgramNodes = new Vector<FileBasedProgramNode>();
			List<File> files = Arrays.asList(getFolder().listFiles());
			Collections.sort(files);
			for (File file : files) {
				if (isFolderPerProgram()) {
					if (file.isDirectory()) {
						if (!containsSubFolders(file)) {
							File pf = selectProgramFileInFolder(file);
							if (pf != null) {
								childProgramNodes.add(new FileBasedProgramNode(file.getName(), pf));
							}
						} else {
							childFolderNodes.add(new FileBasedFolderNode(file));
						}
					}
				} else {
					if (file.isDirectory()) {
						childFolderNodes.add(new FileBasedFolderNode(file));
					} else if (isProgramFile(file)) {
						childProgramNodes.add(new FileBasedProgramNode(file.getName(), file));
					}
				}
			}
			// Sort: folders first
			List<Node> childNodes = new Vector<Node>(childFolderNodes.size() + childProgramNodes.size());
			childNodes.addAll(childFolderNodes);
			childNodes.addAll(childProgramNodes);
			return childNodes;
		}

		public File getFolder() {
			return folder;
		}

	}

	private class FileBasedProgramNode extends ProgramNode {

		private File file;

		public FileBasedProgramNode(String name, File file) {
			super(name);
			this.file = file;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(128);
			sb.append(super.toString());
			sb.append(" (").append(getFile().getPath()).append(')');
			return sb.toString();
		}

		@Override
		protected AmstradProgram readProgram() {
			// TODO pass info file (if any)
			return new FileBasedProgram(this);
		}

		public File getFile() {
			return file;
		}

	}

	private class FileBasedProgram extends AmstradProgram {

		private FileBasedProgramNode programNode;

		private File infoFile;

		public FileBasedProgram(FileBasedProgramNode programNode) {
			this(programNode, null);
		}

		public FileBasedProgram(FileBasedProgramNode programNode, File infoFile) {
			this.programNode = programNode;
			this.infoFile = infoFile;
		}

		@Override
		public String getProgramName() {
			String programName = getProgramNode().getName();
			if (hasInfoFile()) {
				// TODO read from info
			}
			return programName;
		}

		@Override
		public AmstradMonitorMode getPreferredMonitorMode() {
			AmstradMonitorMode mode = getDefaultMonitorMode();
			if (hasInfoFile()) {
				// TODO read from info
			}
			return mode;
		}

		@Override
		public void loadInto(AmstradPc amstradPc) throws AmstradProgramException {
			File sourceCodeFile = getProgramNode().getFile();
			try {
				amstradPc.getBasicRuntime().loadSourceCodeFromFile(sourceCodeFile);
			} catch (Exception e) {
				throw new AmstradProgramException("Could not load as Basic source file: " + sourceCodeFile.getPath(), e);
			}
		}

		private boolean hasInfoFile() {
			return getInfoFile() != null;
		}

		private File getInfoFile() {
			return infoFile;
		}

		private FileBasedProgramNode getProgramNode() {
			return programNode;
		}

	}

}