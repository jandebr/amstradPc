package org.maia.amstrad.program.repo.search;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.DelegatingAmstradProgramRepository;
import org.maia.amstrad.util.StringUtils;

public class SearchingAmstradProgramRepository extends DelegatingAmstradProgramRepository {

	private SearchingFolderNode rootNode;

	private boolean searchByProgramName;

	private String searchString;

	private SearchingAmstradProgramRepository(AmstradProgramRepository sourceRepository) {
		super(sourceRepository);
		this.rootNode = new SearchingFolderNode(sourceRepository.getRootNode());
	}

	public static SearchingAmstradProgramRepository withSearchByProgramName(AmstradProgramRepository sourceRepository,
			String searchString) {
		SearchingAmstradProgramRepository repository = new SearchingAmstradProgramRepository(sourceRepository);
		repository.setSearchByProgramName(true);
		repository.setSearchString(searchString);
		return repository;
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	public boolean isSearchByProgramName() {
		return searchByProgramName;
	}

	private void setSearchByProgramName(boolean byName) {
		this.searchByProgramName = byName;
	}

	public String getSearchString() {
		return searchString;
	}

	private void setSearchString(String str) {
		this.searchString = str;
	}

	private class SearchingFolderNode extends FolderNode {

		private FolderNode delegate;

		public SearchingFolderNode(FolderNode delegate) {
			this(delegate.getName(), delegate);
		}

		public SearchingFolderNode(String name, FolderNode delegate) {
			super(name);
			this.delegate = delegate;
		}

		@Override
		protected List<Node> listChildNodes() {
			List<Node> matchingNodes = new Vector<Node>();
			collectMatchingNodesRecursively(getDelegate(), matchingNodes);
			return matchingNodes;
		}

		private void collectMatchingNodesRecursively(FolderNode currentNode, List<Node> matchingNodes) {
			for (Node node : currentNode.getChildNodes()) {
				if (node.isFolder()) {
					collectMatchingNodesRecursively(node.asFolder(), matchingNodes);
				} else if (node.isProgram()) {
					if (isMatching(node.asProgram())) {
						matchingNodes.add(node);
					}
				}
			}
		}

		private boolean isMatching(ProgramNode node) {
			boolean match = false;
			String str = getSearchString();
			if (!match && isSearchByProgramName() && str != null) {
				match = StringUtils.containsIgnoringCase(node.getProgram().getProgramName(), str);
			}
			return match;
		}

		private FolderNode getDelegate() {
			return delegate;
		}

	}

}