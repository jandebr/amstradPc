package org.maia.amstrad.program.repo.filter;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.DelegatingAmstradProgramRepository;

public class FilteredAmstradProgramRepository extends DelegatingAmstradProgramRepository {

	private FilteredFolderNode rootNode;

	private boolean sequenceNumberFiltered;

	private static Pattern sequenceNumberPattern = Pattern.compile("\\d+[\\s\\._]");

	private FilteredAmstradProgramRepository(AmstradProgramRepository sourceRepository) {
		super(sourceRepository);
		this.rootNode = new FilteredFolderNode(sourceRepository.getRootNode()); // no rename of root node
	}

	public static FilteredAmstradProgramRepository sequenceNumberFilter(AmstradProgramRepository sourceRepository) {
		FilteredAmstradProgramRepository repository = new FilteredAmstradProgramRepository(sourceRepository);
		repository.setSequenceNumberFiltered(true);
		return repository;
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	public boolean isSequenceNumberFiltered() {
		return sequenceNumberFiltered;
	}

	private void setSequenceNumberFiltered(boolean filtered) {
		this.sequenceNumberFiltered = filtered;
	}

	private class FilteredFolderNode extends FolderNode {

		private FolderNode delegate;

		public FilteredFolderNode(FolderNode delegate) {
			this(delegate.getName(), delegate);
		}

		public FilteredFolderNode(String name, FolderNode delegate) {
			super(name);
			this.delegate = delegate;
		}

		@Override
		protected List<Node> listChildNodes() {
			List<Node> delegateChildNodes = getDelegate().getChildNodes();
			List<Node> childNodes = new Vector<Node>(delegateChildNodes.size());
			boolean sequenceNumbers = isSequenceNumberFiltered() && hasSequenceNumbers(delegateChildNodes);
			for (Node node : delegateChildNodes) {
				String name = node.getName();
				if (sequenceNumbers) {
					String modifiedName = stripSequenceNumber(name);
					if (!modifiedName.isEmpty())
						name = modifiedName;
				}
				Node childNode = node.isFolder() ? new FilteredFolderNode(name, node.asFolder())
						: new FilteredProgramNode(name, node.asProgram());
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

	private class FilteredProgramNode extends ProgramNode {

		private ProgramNode delegate;

		public FilteredProgramNode(String name, ProgramNode delegate) {
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