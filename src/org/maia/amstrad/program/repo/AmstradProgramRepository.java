package org.maia.amstrad.program.repo;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maia.amstrad.gui.components.Item;
import org.maia.amstrad.program.AmstradProgram;

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

	public static abstract class Node implements Item {

		private String name;

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

		protected void refresh() {
		}

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

		public boolean isEmpty() {
			return getChildNodes().isEmpty();
		}

		@Override
		public final boolean isFolder() {
			return true;
		}

		protected abstract List<Node> listChildNodes();

		public List<Node> getChildNodes() {
			if (childNodes == null) {
				childNodes = new Vector<Node>(listChildNodes());
			}
			return childNodes;
		}

		@Override
		protected void refresh() {
			super.refresh();
			childNodes = null;
		}

	}

	public static abstract class ProgramNode extends Node {

		protected ProgramNode(String name) {
			super(name);
		}

		@Override
		public final boolean isFolder() {
			return false;
		}

		protected abstract AmstradProgram readProgram();

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

	}

	private static class AmstradProgramCache {

		private int capacity;

		private List<ProgramNode> recentProgramNodes;

		private List<AmstradProgram> recentPrograms;

		public AmstradProgramCache(int capacity) {
			this.capacity = capacity;
			this.recentProgramNodes = new Vector<ProgramNode>(capacity);
			this.recentPrograms = new Vector<AmstradProgram>(capacity);
		}

		public int size() {
			return getRecentProgramNodes().size();
		}

		public synchronized void clear() {
			getRecentProgramNodes().clear();
			getRecentPrograms().clear();
		}

		public synchronized void storeInCache(ProgramNode node, AmstradProgram program) {
			if (!getRecentProgramNodes().contains(node)) {
				if (size() == getCapacity()) {
					evictOne();
				}
				getRecentProgramNodes().add(node);
				getRecentPrograms().add(program);
			}
		}

		public synchronized AmstradProgram fetchFromCache(ProgramNode node) {
			AmstradProgram program = null;
			int index = getRecentProgramNodes().indexOf(node);
			if (index >= 0) {
				program = getRecentPrograms().get(index);
				if (index < size() - 1) {
					// move to front
					getRecentProgramNodes().add(getRecentProgramNodes().remove(index));
					getRecentPrograms().add(getRecentPrograms().remove(index));
				}
			}
			return program;
		}

		private void evictOne() {
			getRecentProgramNodes().remove(0);
			getRecentPrograms().remove(0).flush();
		}

		public int getCapacity() {
			return capacity;
		}

		private List<ProgramNode> getRecentProgramNodes() {
			return recentProgramNodes;
		}

		private List<AmstradProgram> getRecentPrograms() {
			return recentPrograms;
		}

	}

}