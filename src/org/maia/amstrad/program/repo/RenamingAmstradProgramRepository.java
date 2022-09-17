package org.maia.amstrad.program.repo;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maia.amstrad.program.AmstradProgram;

public class RenamingAmstradProgramRepository extends DelegatingAmstradProgramRepository {

	private RenamedFolderNode rootNode;

	private boolean sequenceNumberStripped;

	private static Pattern sequenceNumberPattern = Pattern.compile("\\d+[\\s\\._]");

	private RenamingAmstradProgramRepository(AmstradProgramRepository sourceRepository) {
		super(sourceRepository);
		this.rootNode = new RenamedFolderNode(sourceRepository.getRootNode()); // root node is never renamed
	}

	public static RenamingAmstradProgramRepository sequenceNumberStripping(AmstradProgramRepository sourceRepository) {
		RenamingAmstradProgramRepository repository = new RenamingAmstradProgramRepository(sourceRepository);
		repository.setSequenceNumberStripped(true);
		return repository;
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	public boolean isSequenceNumberStripped() {
		return sequenceNumberStripped;
	}

	private void setSequenceNumberStripped(boolean stripped) {
		this.sequenceNumberStripped = stripped;
	}

	private class RenamedFolderNode extends FolderNode {

		private FolderNode delegate;

		public RenamedFolderNode(FolderNode delegate) {
			this(delegate.getName(), delegate);
		}

		public RenamedFolderNode(String name, FolderNode delegate) {
			super(name);
			this.delegate = delegate;
		}

		@Override
		protected List<Node> listChildNodes() {
			List<Node> delegateChildNodes = getDelegate().getChildNodes();
			List<Node> childNodes = new Vector<Node>(delegateChildNodes.size());
			boolean sequenceNumbers = isSequenceNumberStripped() && hasSequenceNumbers(delegateChildNodes);
			for (Node node : delegateChildNodes) {
				String name = node.getName();
				if (sequenceNumbers) {
					String strippedName = stripSequenceNumber(name);
					if (!strippedName.isEmpty())
						name = strippedName;
				}
				Node childNode = node.isFolder() ? new RenamedFolderNode(name, node.asFolder())
						: new RenamedProgramNode(name, node.asProgram());
				childNodes.add(childNode);
			}
			return childNodes;
		}

		private boolean hasSequenceNumbers(List<Node> nodes) {
			if (nodes.isEmpty())
				return false;
			for (Node node : nodes) {
				if (!sequenceNumberPattern.matcher(node.getName()).lookingAt())
					return false;
			}
			return true;
		}

		private String stripSequenceNumber(String str) {
			Matcher m = sequenceNumberPattern.matcher(str);
			if (m.lookingAt()) {
				return str.substring(m.end());
			} else {
				return str;
			}
		}

		private FolderNode getDelegate() {
			return delegate;
		}

	}

	private class RenamedProgramNode extends ProgramNode {

		private ProgramNode delegate;

		public RenamedProgramNode(String name, ProgramNode delegate) {
			super(name);
			this.delegate = delegate;
		}

		@Override
		protected AmstradProgram readProgram() {
			AmstradProgram program = getDelegate().getProgram();
			if (shouldRenameDelegateProgram()) {
				program = getRenamedDelegateProgram();
			}
			return program;
		}

		private boolean shouldRenameDelegateProgram() {
			if (getName().equals(getDelegate().getName())) {
				return false;
			} else {
				String programName = getDelegate().getProgram().getProgramName();
				if (isSequenceNumberStripped() && sequenceNumberPattern.matcher(programName).lookingAt()) {
					return true;
				}
				return false;
			}
		}

		private AmstradProgram getRenamedDelegateProgram() {
			AmstradProgram renamedProgram = getDelegate().getProgram().clone();
			renamedProgram.setProgramName(getName());
			return renamedProgram;
		}

		private ProgramNode getDelegate() {
			return delegate;
		}

	}

}