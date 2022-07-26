package org.maia.amstrad.pc.browser.model;

import java.util.List;
import java.util.Vector;

public abstract class AmstradProgramRepository {

	protected AmstradProgramRepository() {
	}

	public abstract Node getRootNode();

	public void refresh() {
		getRootNode().refresh();
	}

	public static abstract class Node {

		private String name;

		protected Node(String name) {
			this.name = name;
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

		private AmstradProgramInfo programInfo;

		protected ProgramNode(String name) {
			super(name);
		}

		@Override
		public boolean isFolder() {
			return false;
		}

		@Override
		public void refresh() {
			programInfo = null;
		}

		protected abstract AmstradProgramInfo readProgramInfo();

		public AmstradProgramInfo getProgramInfo() {
			if (programInfo == null) {
				programInfo = readProgramInfo();
			}
			return programInfo;
		}

	}

}