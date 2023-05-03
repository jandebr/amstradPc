package org.maia.amstrad.program.repo;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maia.amstrad.gui.ImageProxy;
import org.maia.amstrad.gui.components.ScrollableItem;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.cover.CoverImage;
import org.maia.amstrad.program.repo.cover.CoverImageImpl;
import org.maia.amstrad.util.KeyedCacheLRU;

public abstract class AmstradProgramRepository {

	private static AmstradProgramCache programCache = new AmstradProgramCache(10);

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
	}

	public static abstract class Node implements ScrollableItem {

		private String name;

		private CoverImage coverImage;

		private boolean coverImageVerified;

		protected Node(String name) {
			this.name = name;
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

		public CoverImage getCoverImage() {
			if (coverImage == null && !coverImageVerified) {
				coverImage = readCoverImage();
				coverImageVerified = true;
			}
			return coverImage;
		}

		protected abstract CoverImage readCoverImage();

	}

	public static abstract class FolderNode extends Node {

		private List<Node> childNodes;

		protected FolderNode(String name) {
			super(name);
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

	}

	public static abstract class ProgramNode extends Node {

		protected ProgramNode(String name) {
			super(name);
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
		protected CoverImage readCoverImage() {
			CoverImage cover = null;
			ImageProxy proxy = getProgram().getCoverImage();
			if (proxy != null) {
				cover = new CoverImageImpl(this, proxy);
			}
			return cover;
		}

	}

	private static class AmstradProgramCache extends KeyedCacheLRU<ProgramNode, AmstradProgram> {

		public AmstradProgramCache(int capacity) {
			super(capacity);
		}

		@Override
		protected void evicted(ProgramNode key, AmstradProgram value) {
			super.evicted(key, value);
			value.dispose();
		}

	}

}