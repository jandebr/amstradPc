package org.maia.amstrad.program.repo.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradFileType;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.image.AmstradProgramImageSourcedByFile;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.io.util.IOUtils;

public abstract class FileBasedAmstradProgramRepository extends AmstradProgramRepository {

	private FileBasedFolderNode rootNode;

	private boolean folderPerProgram;

	protected FileBasedAmstradProgramRepository(File rootFolder) {
		this(rootFolder, false);
		setFolderPerProgram(couldBeFolderPerProgram(rootFolder));
	}

	protected FileBasedAmstradProgramRepository(File rootFolder, boolean folderPerProgram) {
		if (!rootFolder.isDirectory())
			throw new IllegalArgumentException("The root folder must be a directory");
		setRootNode(new FileBasedFolderNode(rootFolder));
		setFolderPerProgram(folderPerProgram);
	}

	private boolean couldBeFolderPerProgram(File parentFolder) {
		boolean result = true;
		if (parentFolder.isDirectory()) {
			if (containsSubFolders(parentFolder) && selectProgramFileInFolder(parentFolder) != null) {
				result = false;
			} else {
				int programCount = 0;
				File[] files = parentFolder.listFiles();
				int i = 0;
				while (i < files.length && result) {
					File file = files[i++];
					if (file.isDirectory()) {
						result = couldBeFolderPerProgram(file);
					} else {
						if (isProgramFile(file) && !isRemasteredProgramFile(file)) {
							programCount++;
							if (programCount > 1)
								result = false;
						}
					}
				}
			}
		}
		return result;
	}

	private boolean containsSubFolders(File folder) {
		boolean result = false;
		File[] files = folder.listFiles();
		int i = 0;
		while (!result && i < files.length)
			result = files[i++].isDirectory();
		return result;
	}

	private File selectProgramFileInFolder(File folder) {
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

	private File selectMetaDataFileInFolder(File folder) {
		return selectMetaDataFileInFolder(folder, null);
	}

	private File selectMetaDataFileInFolder(File folder, File companionProgramFileInFolder) {
		File result = null;
		File[] files = folder.listFiles();
		int i = 0;
		while (i < files.length && result == null) {
			File file = files[i++];
			if (isMetaDataFile(file)) {
				if (companionProgramFileInFolder == null
						|| equalFilenamesButExtension(file, companionProgramFileInFolder)) {
					result = file;
				}
			}
		}
		return result;
	}

	private File selectCoverImageFileInFolder(File folder) {
		File result = null;
		File[] files = folder.listFiles();
		int i = 0;
		while (i < files.length && result == null) {
			File file = files[i++];
			if (isCoverImageFile(file)) {
				result = file;
			}
		}
		return result;
	}

	private boolean equalFilenamesButExtension(File one, File other) {
		return IOUtils.stripExtension(one).equals(IOUtils.stripExtension(other));
	}

	protected abstract boolean isProgramFile(File file);

	protected abstract boolean isRemasteredProgramFile(File file);

	protected boolean isMetaDataFile(File file) {
		return AmstradFileType.AMSTRAD_METADATA_FILE.matches(file);
	}

	protected boolean isCoverImageFile(File file) {
		String name = file.getName().toLowerCase();
		return name.equals("cover.jpg") || name.equals("cover.png");
	}

	protected boolean isLinkFile(File file) {
		String name = file.getName().toLowerCase();
		return name.equals("link");
	}

	protected File getLinkedDestinationFolder(File sourceFolder) {
		File destination = null;
		if (sourceFolder.isDirectory()) {
			File[] files = sourceFolder.listFiles();
			if (files.length == 1) {
				File file = files[0];
				if (isLinkFile(file)) {
					destination = readDestinationFolderInLinkFile(file);
				}
			}
		}
		return destination;
	}

	protected File readDestinationFolderInLinkFile(File linkFile) {
		File destination = null;
		try {
			// path relative to the link file's location
			String path = IOUtils.readTextFileContents(linkFile).toString();
			int i = path.indexOf('\n');
			if (i >= 0)
				path = path.substring(0, i);
			File folder = new File(linkFile.getParentFile(), path);
			if (folder.exists() && folder.isDirectory()) {
				destination = folder;
			}
		} catch (IOException e) {
		}
		return destination;
	}

	protected abstract AmstradProgram createProgram(String programName, File basicFile, File metadataFile);

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	private void setRootNode(FileBasedFolderNode rootNode) {
		this.rootNode = rootNode;
	}

	public boolean isFolderPerProgram() {
		return folderPerProgram;
	}

	private void setFolderPerProgram(boolean folderPerProgram) {
		this.folderPerProgram = folderPerProgram;
	}

	private class FileBasedFolderNode extends FolderNode {

		private File folder;

		public FileBasedFolderNode(File folder) {
			this(folder.getName(), folder);
		}

		public FileBasedFolderNode(String name, File folder) {
			super(name);
			this.folder = folder;
		}

		@Override
		protected AmstradProgramImage readCoverImage() {
			AmstradProgramImage cover = null;
			File coverFile = selectCoverImageFileInFolder(getFolder());
			if (coverFile != null) {
				cover = new AmstradProgramImageSourcedByFile(coverFile, "COVER");
			}
			return cover;
		}

		@Override
		protected List<Node> listChildNodes() {
			File folder = getFolder();
			List<FileBasedFolderNode> childFolderNodes = new Vector<FileBasedFolderNode>();
			List<FileBasedProgramNode> childProgramNodes = new Vector<FileBasedProgramNode>();
			if (isFolderPerProgram() && !containsSubFolders(folder)) {
				File pf = selectProgramFileInFolder(folder);
				if (pf != null) {
					childProgramNodes.add(new FileBasedProgramNode(folder.getName(), pf));
				}
			} else {
				List<File> files = Arrays.asList(folder.listFiles());
				Collections.sort(files);
				for (File file : files) {
					if (isFolderPerProgram()) {
						if (file.isDirectory()) {
							String name = file.getName();
							File linkedFolder = getLinkedDestinationFolder(file);
							if (linkedFolder != null) {
								file = linkedFolder;
							}
							if (!containsSubFolders(file)) {
								File pf = selectProgramFileInFolder(file);
								if (pf != null) {
									childProgramNodes.add(new FileBasedProgramNode(name, pf));
								}
							} else {
								childFolderNodes.add(new FileBasedFolderNode(name, file));
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
			return createProgram(getName(), getFile(), getCompanionMetaDataFile());
		}

		public File getCompanionMetaDataFile() {
			if (isFolderPerProgram()) {
				return selectMetaDataFileInFolder(getFile().getParentFile());
			} else {
				return selectMetaDataFileInFolder(getFile().getParentFile(), getFile());
			}
		}

		public File getFile() {
			return file;
		}

	}

}