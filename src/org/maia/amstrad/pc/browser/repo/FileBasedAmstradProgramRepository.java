package org.maia.amstrad.pc.browser.repo;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.basic.BasicRuntime;

public class FileBasedAmstradProgramRepository extends AmstradProgramRepository {

	private FileBasedFolderNode rootNode;

	public FileBasedAmstradProgramRepository(File rootFolder, boolean folderPerProgram,
			AmstradMonitorMode preferredMonitorMode) {
		if (!rootFolder.isDirectory())
			throw new IllegalArgumentException("The root must be a directory");
		this.rootNode = new FileBasedFolderNode(rootFolder, folderPerProgram, preferredMonitorMode);
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	private static class FileBasedFolderNode extends FolderNode {

		private File folder;

		private boolean folderPerProgram;

		private AmstradMonitorMode preferredMonitorMode;

		public FileBasedFolderNode(File folder, boolean folderPerProgram, AmstradMonitorMode preferredMonitorMode) {
			super(folder.getName());
			this.folder = folder;
			this.folderPerProgram = folderPerProgram;
			this.preferredMonitorMode = preferredMonitorMode;
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
		protected List<Node> listChildNodes() {
			AmstradMonitorMode mode = getPreferredMonitorMode();
			List<Node> childNodes = new Vector<Node>();
			File[] files = getFolder().listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (isFolderPerProgram()) {
					if (file.isDirectory()) {
						if (!containsSubFolders(file)) {
							File pf = selectProgramFileInFolder(file);
							if (pf != null) {
								childNodes.add(new FileBasedProgramNode(file.getName(), pf, mode));
							}
						} else {
							childNodes.add(new FileBasedFolderNode(file, isFolderPerProgram(), mode));
						}
					}
				} else {
					if (file.isDirectory()) {
						childNodes.add(new FileBasedFolderNode(file, isFolderPerProgram(), mode));
					} else if (isProgramFile(file)) {
						childNodes.add(new FileBasedProgramNode(file.getName(), file, mode));
					}
				}
			}
			return childNodes;
		}

		public File getFolder() {
			return folder;
		}

		public boolean isFolderPerProgram() {
			return folderPerProgram;
		}

		public AmstradMonitorMode getPreferredMonitorMode() {
			return preferredMonitorMode;
		}

	}

	private static class FileBasedProgramNode extends ProgramNode {

		private File file;

		private AmstradMonitorMode preferredMonitorMode;

		public FileBasedProgramNode(String name, File file, AmstradMonitorMode preferredMonitorMode) {
			super(name);
			this.file = file;
			this.preferredMonitorMode = preferredMonitorMode;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(128);
			sb.append(super.toString());
			sb.append(" (").append(getFile().getPath()).append(')');
			return sb.toString();
		}

		@Override
		protected AmstradProgramInfo readProgramInfo() {
			// TODO pass info file (if any)
			return new FileBasedProgramInfo(this);
		}

		@Override
		public void loadInto(AmstradPc amstradPc) throws AmstradProgramException {
			try {
				amstradPc.getBasicRuntime().loadSourceCodeFromFile(getFile());
			} catch (Exception e) {
				throw new AmstradProgramException("Could not load as Basic source file: " + file.getPath(), e);
			}
		}

		public File getFile() {
			return file;
		}

		public AmstradMonitorMode getPreferredMonitorMode() {
			return preferredMonitorMode;
		}

	}

	private static class FileBasedProgramInfo extends AmstradProgramInfo {

		private FileBasedProgramNode programNode;

		private File infoFile;

		public FileBasedProgramInfo(FileBasedProgramNode programNode) {
			this(programNode, null);
		}

		public FileBasedProgramInfo(FileBasedProgramNode programNode, File infoFile) {
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
			AmstradMonitorMode mode = getProgramNode().getPreferredMonitorMode();
			if (hasInfoFile()) {
				// TODO read from info
			}
			return mode;
		}

		private FileBasedProgramNode getProgramNode() {
			return programNode;
		}

		private boolean hasInfoFile() {
			return getInfoFile() != null;
		}

		private File getInfoFile() {
			return infoFile;
		}

	}

}