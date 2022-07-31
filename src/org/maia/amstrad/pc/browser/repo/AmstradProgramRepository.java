package org.maia.amstrad.pc.browser.repo;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public abstract class AmstradProgramRepository {

	protected AmstradProgramRepository() {
	}

	@Override
	public String toString() {
		return getRootNode().toString();
	}

	public abstract FolderNode getRootNode();

	public void refresh() {
		getRootNode().refresh();
	}

	public static abstract class Node {

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

		public boolean isProgram() {
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

		public void refresh() {
		}

		public String getName() {
			return name;
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
		public boolean isFolder() {
			return true;
		}

		@Override
		public void refresh() {
			childNodes = null;
		}

		protected abstract List<Node> listChildNodes();

		public List<Node> getChildNodes() {
			if (childNodes == null) {
				childNodes = new Vector<Node>(listChildNodes());
			}
			return childNodes;
		}

	}

	public static abstract class ProgramNode extends Node {

		private AmstradProgram program;

		protected ProgramNode(String name) {
			super(name);
		}

		@Override
		public boolean isFolder() {
			return false;
		}

		@Override
		public void refresh() {
			program = null;
		}

		protected abstract AmstradProgram readProgram();

		public AmstradProgram getProgram() {
			if (program == null) {
				program = readProgram();
			}
			return program;
		}

	}

}