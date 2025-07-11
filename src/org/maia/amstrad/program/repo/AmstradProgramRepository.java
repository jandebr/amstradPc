package org.maia.amstrad.program.repo;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.components.ScrollableItem;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.util.KeyedCacheLRU;

public abstract class AmstradProgramRepository {

	private static AmstradProgramCache programCache;

	private static final int PROGRAM_CACHE_DEFAULT_CAPACITY = 50;

	private static final String PROGRAM_CACHE_CAPACITY_SETTING = "program_repo.cache_capacity";

	static {
		int capacity = PROGRAM_CACHE_DEFAULT_CAPACITY;
		try {
			capacity = Integer.parseInt(AmstradFactory.getInstance().getAmstradContext().getUserSettings()
					.get(PROGRAM_CACHE_CAPACITY_SETTING, String.valueOf(capacity)));
		} catch (NumberFormatException e) {
			// use default
		}
		System.out.println("Init program repository cache, capacity=" + capacity);
		programCache = new AmstradProgramCache(capacity * 2); // considering renaming programs
	}

	protected AmstradProgramRepository() {
	}

	@Override
	public String toString() {
		return getRootNode().toString();
	}

	public abstract FolderNode getRootNode();

	public void refresh() {
		programCache.clear();
		getRootNode().refresh();
		AmstradFactory.getInstance().getAmstradContext().clearImagePools();
	}

	public static abstract class Node implements ScrollableItem {

		private String name;

		private FolderNode parent;

		private AmstradProgramImage coverImage;

		private boolean coverImageVerified;

		protected Node(String name, FolderNode parent) {
			this.name = name;
			this.parent = parent;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(32);
			sb.append('[').append(isFolder() ? "F" : "P").append(']');
			sb.append(' ').append(getName());
			return sb.toString();
		}

		protected void refresh() {
			coverImage = null;
			coverImageVerified = false;
		}

		public abstract boolean isFolder();

		public final boolean isProgram() {
			return !isFolder();
		}

		public FolderNode asFolder() {
			if (!isFolder())
				throw new ClassCastException("This is no folder");
			return (FolderNode) this;
		}

		public ProgramNode asProgram() {
			if (!isProgram())
				throw new ClassCastException("This is no program");
			return (ProgramNode) this;
		}

		public String getName() {
			return name;
		}

		protected void setName(String name) {
			this.name = name;
		}

		public boolean isRoot() {
			return getParent() == null;
		}

		public FolderNode getParent() {
			return parent;
		}

		public AmstradProgramImage getCoverImage() {
			if (coverImage == null && !coverImageVerified) {
				coverImage = readCoverImage();
				coverImageVerified = true;
			}
			return coverImage;
		}

		protected abstract AmstradProgramImage readCoverImage();

	}

	public static abstract class FolderNode extends Node {

		private List<Node> childNodes;

		protected FolderNode(String name, FolderNode parent) {
			super(name, parent);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(1024);
			sb.append(super.toString());
			for (Node node : getChildNodes()) {
				StringTokenizer st = new StringTokenizer(node.toString(), "\n");
				while (st.hasMoreTokens()) {
					sb.append('\n').append('\t').append(st.nextToken());
				}
			}
			return sb.toString();
		}

		@Override
		protected void refresh() {
			super.refresh();
			childNodes = null;
		}

		public boolean isEmpty() {
			return getChildNodes().isEmpty();
		}

		@Override
		public final boolean isFolder() {
			return true;
		}

		public List<Node> getChildNodes() {
			if (childNodes == null) {
				childNodes = new Vector<Node>(listChildNodes());
			}
			return childNodes;
		}

		protected abstract List<Node> listChildNodes();

		public ProgramNode getFeaturedProgramNode() {
			for (Node child : getChildNodes()) {
				if (child.isProgram()) {
					ProgramNode childProgram = child.asProgram();
					if (childProgram.getProgram().isFeatured())
						return childProgram;
				}
			}
			return null;
		}

	}

	public static abstract class ProgramNode extends Node {

		protected ProgramNode(String name, FolderNode parent) {
			super(name, parent);
		}

		@Override
		public final boolean isFolder() {
			return false;
		}

		public AmstradProgram getProgram() {
			AmstradProgram program = null;
			synchronized (programCache) {
				program = programCache.fetchFromCache(this);
				if (program == null) {
					program = readProgram();
					programCache.storeInCache(this, program);
				}
			}
			return program;
		}

		protected abstract AmstradProgram readProgram();

		@Override
		protected AmstradProgramImage readCoverImage() {
			return getProgram().getCoverImage();
		}

	}

	private static class AmstradProgramCache extends KeyedCacheLRU<ProgramNode, AmstradProgram> {

		public AmstradProgramCache(int capacity) {
			super(capacity);
		}

		@Override
		protected void evicted(ProgramNode node, AmstradProgram program) {
			super.evicted(node, program);
			program.dispose();
			// System.out.println("CACHE-EVICTED program " + program.getProgramName());
		}

	}

}