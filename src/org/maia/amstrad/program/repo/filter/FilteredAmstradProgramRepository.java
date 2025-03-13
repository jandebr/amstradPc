package org.maia.amstrad.program.repo.filter;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.DelegatingAmstradProgramRepository;

public class FilteredAmstradProgramRepository extends DelegatingAmstradProgramRepository {

	private FilteredFolderNode rootNode;

	public FilteredAmstradProgramRepository(AmstradProgramRepository sourceRepository) {
		super(sourceRepository);
		this.rootNode = new FilteredFolderNode(null, sourceRepository.getRootNode());
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	private class FilteredFolderNode extends FolderNode {

		private FolderNode delegate;

		public FilteredFolderNode(FilteredFolderNode parent, FolderNode delegate) {
			super(delegate.getName(), parent);
			this.delegate = delegate;
		}

		@Override
		protected AmstradProgramImage readCoverImage() {
			return getDelegate().getCoverImage();
		}

		@Override
		protected List<Node> listChildNodes() {
			List<Node> delegateChildNodes = getDelegate().getChildNodes();
			List<Node> filteredNodes = new Vector<Node>(delegateChildNodes.size());
			for (Node node : delegateChildNodes) {
				if (node.isFolder()) {
					filteredNodes.add(new FilteredFolderNode(this, node.asFolder()));
				} else {
					if (!node.asProgram().getProgram().isHidden()) {
						filteredNodes.add(new FilteredProgramNode(this, node.asProgram()));
					}
				}
			}
			return filteredNodes;
		}

		private FolderNode getDelegate() {
			return delegate;
		}

	}

	private class FilteredProgramNode extends ProgramNode {

		private ProgramNode delegate;

		public FilteredProgramNode(FilteredFolderNode parent, ProgramNode delegate) {
			super(delegate.getName(), parent);
			this.delegate = delegate;
		}

		@Override
		protected AmstradProgram readProgram() {
			return getDelegate().getProgram();
		}

		private ProgramNode getDelegate() {
			return delegate;
		}

	}

}