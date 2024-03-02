package org.maia.amstrad.program.repo.filter;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.DelegatingAmstradProgramRepository;

public class FilteredAmstradProgramRepository extends DelegatingAmstradProgramRepository {

	private FilteredFolderNode rootNode;

	public FilteredAmstradProgramRepository(AmstradProgramRepository sourceRepository) {
		super(sourceRepository);
		this.rootNode = new FilteredFolderNode(sourceRepository.getRootNode());
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
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
		protected AmstradProgramImage readCoverImage() {
			return getDelegate().getCoverImage();
		}

		@Override
		protected List<Node> listChildNodes() {
			List<Node> delegateChildNodes = getDelegate().getChildNodes();
			List<Node> filteredNodes = new Vector<Node>(delegateChildNodes.size());
			for (Node node : delegateChildNodes) {
				if (node.isFolder()) {
					filteredNodes.add(new FilteredFolderNode(node.asFolder()));
				} else {
					if (!node.asProgram().getProgram().isHidden()) {
						filteredNodes.add(node);
					}
				}
			}
			return filteredNodes;
		}

		private FolderNode getDelegate() {
			return delegate;
		}

	}

}