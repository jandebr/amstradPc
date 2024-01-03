package org.maia.amstrad.program.repo.rename;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.DelegatingAmstradProgramRepository;

public class RenamingAmstradProgramRepository extends DelegatingAmstradProgramRepository {

	private RenamingFolderNode rootNode;

	private boolean hideSequenceNumbers;

	private static Pattern sequenceNumberPattern = Pattern.compile("\\d+[\\s\\._]");

	private RenamingAmstradProgramRepository(AmstradProgramRepository sourceRepository) {
		super(sourceRepository);
		this.rootNode = new RenamingFolderNode(sourceRepository.getRootNode()); // no rename of root node
	}

	public static RenamingAmstradProgramRepository withSequenceNumbersHidden(
			AmstradProgramRepository sourceRepository) {
		RenamingAmstradProgramRepository repository = new RenamingAmstradProgramRepository(sourceRepository);
		repository.setHideSequenceNumbers(true);
		return repository;
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	public boolean isHideSequenceNumbers() {
		return hideSequenceNumbers;
	}

	private void setHideSequenceNumbers(boolean hide) {
		this.hideSequenceNumbers = hide;
	}

	private class RenamingFolderNode extends FolderNode {

		private FolderNode delegate;

		public RenamingFolderNode(FolderNode delegate) {
			this(delegate.getName(), delegate);
		}

		public RenamingFolderNode(String name, FolderNode delegate) {
			super(name);
			this.delegate = delegate;
		}

		@Override
		protected AmstradProgramImage readCoverImage() {
			return getDelegate().getCoverImage();
		}

		@Override
		protected List<Node> listChildNodes() {
			List<Node> delegateChildNodes = getDelegate().getChildNodes();
			List<Node> childNodes = new Vector<Node>(delegateChildNodes.size());
			boolean sequenceNumbers = isHideSequenceNumbers() && hasSequenceNumbers(delegateChildNodes);
			for (Node node : delegateChildNodes) {
				String name = node.getName();
				if (sequenceNumbers) {
					String modifiedName = stripSequenceNumber(name);
					if (!modifiedName.isEmpty())
						name = modifiedName;
				}
				Node childNode = node.isFolder() ? new RenamingFolderNode(name, node.asFolder())
						: new RenamingProgramNode(name, node.asProgram());
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

	private class RenamingProgramNode extends ProgramNode {

		private ProgramNode delegate;

		public RenamingProgramNode(String name, ProgramNode delegate) {
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
			String delegateName = getDelegate().getName();
			if (getName().equals(delegateName))
				return false; // no renaming
			if (!getDelegate().getProgram().getProgramName().equals(delegateName))
				return false; // keep distinct program name
			return true; // propagate rename
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