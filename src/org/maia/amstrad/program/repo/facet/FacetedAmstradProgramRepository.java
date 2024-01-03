package org.maia.amstrad.program.repo.facet;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.image.AmstradProgramImage;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.DelegatingAmstradProgramRepository;

public class FacetedAmstradProgramRepository extends DelegatingAmstradProgramRepository {

	private FacetList facets;

	private FacetedFolderNode rootNode;

	private Map<Facet, Map<String, Set<FacetedProgramNode>>> index;

	private Set<FacetedProgramNode> allProgramNodes;

	public FacetedAmstradProgramRepository(AmstradProgramRepository sourceRepository, FacetList facets) {
		super(sourceRepository);
		this.facets = facets.clone();
		buildIndex();
	}

	private void buildIndex() {
		this.index = new HashMap<Facet, Map<String, Set<FacetedProgramNode>>>();
		this.allProgramNodes = new HashSet<FacetedProgramNode>(100);
		populateIndex(getSourceRepository().getRootNode());
		this.rootNode = new FacetedFolderNode("ROOT", getAllProgramNodes(), getFacets());
	}

	private void populateIndex(FolderNode node) {
		for (Node child : node.getChildNodes()) {
			if (child.isFolder()) {
				populateIndex(child.asFolder());
			} else {
				addToIndex(child.asProgram());
			}
		}
	}

	private void addToIndex(ProgramNode node) {
		AmstradProgram program = node.getProgram();
		FacetedProgramNode indexNode = new FacetedProgramNode(node.getName(), node);
		FacetList facets = getFacets();
		for (int i = 0; i < facets.size(); i++) {
			Facet facet = facets.getFacet(i);
			Map<String, Set<FacetedProgramNode>> facetIndex = getIndex().get(facet);
			if (facetIndex == null) {
				facetIndex = new HashMap<String, Set<FacetedProgramNode>>();
				getIndex().put(facet, facetIndex);
			}
			String value = facet.valueOf(program);
			Set<FacetedProgramNode> valueNodes = facetIndex.get(value);
			if (valueNodes == null) {
				valueNodes = new HashSet<FacetedProgramNode>();
				facetIndex.put(value, valueNodes);
			}
			valueNodes.add(indexNode);
		}
		getAllProgramNodes().add(indexNode);
	}

	@Override
	public void refresh() {
		super.refresh();
		buildIndex();
	}

	public AmstradProgramRepository getSourceRepository() {
		return getDelegate();
	}

	public FacetList getFacets() {
		return facets;
	}

	@Override
	public FolderNode getRootNode() {
		return rootNode;
	}

	private Map<Facet, Map<String, Set<FacetedProgramNode>>> getIndex() {
		return index;
	}

	private Set<FacetedProgramNode> getAllProgramNodes() {
		return allProgramNodes;
	}

	private class FacetedFolderNode extends FolderNode {

		private Set<FacetedProgramNode> programNodesInScope;

		private FacetList subFacets;

		public FacetedFolderNode(String name, Set<FacetedProgramNode> programNodesInScope, FacetList subFacets) {
			super(name);
			this.programNodesInScope = programNodesInScope;
			this.subFacets = subFacets;
		}

		@Override
		protected AmstradProgramImage readCoverImage() {
			return null; // no such image
		}

		@Override
		protected List<Node> listChildNodes() {
			if (getPivotFacet() == null || getProgramNodesInScope().isEmpty()) {
				return listLeafProgramNodesInOrder();
			} else {
				return listChildFacetNodesInOrder();
			}
		}

		private List<Node> listLeafProgramNodesInOrder() {
			List<Node> leafNodes = new Vector<Node>(getProgramNodesInScope());
			Collections.sort(leafNodes, new NodeSorter());
			return leafNodes;
		}

		private List<Node> listChildFacetNodesInOrder() {
			Map<String, Set<FacetedProgramNode>> facetIndex = getIndex().get(getPivotFacet());
			List<String> facetValues = new Vector<String>(facetIndex.keySet());
			Collections.sort(facetValues, new FacetValueSorter());
			List<Node> childNodes = new Vector<Node>(facetValues.size());
			FacetList childFacets = getSubFacets().subList(1);
			for (String facetValue : facetValues) {
				Set<FacetedProgramNode> childProgramNodesInScope = new HashSet<FacetedProgramNode>(
						getProgramNodesInScope());
				childProgramNodesInScope.retainAll(facetIndex.get(facetValue));
				if (!childProgramNodesInScope.isEmpty()) {
					childNodes.add(new FacetedFolderNode(facetValue, childProgramNodesInScope, childFacets));
				}
			}
			return childNodes;
		}

		public Facet getPivotFacet() {
			return getSubFacets().isEmpty() ? null : getSubFacets().getFacet(0);
		}

		private Set<FacetedProgramNode> getProgramNodesInScope() {
			return programNodesInScope;
		}

		private FacetList getSubFacets() {
			return subFacets;
		}

	}

	private class FacetedProgramNode extends ProgramNode {

		private ProgramNode sourceNode;

		public FacetedProgramNode(String name, ProgramNode sourceNode) {
			super(name);
			this.sourceNode = sourceNode;
		}

		@Override
		protected AmstradProgram readProgram() {
			return getSourceNode().getProgram();
		}

		private ProgramNode getSourceNode() {
			return sourceNode;
		}

	}

	private static class NodeSorter implements Comparator<Node> {

		public NodeSorter() {
		}

		@Override
		public int compare(Node oneNode, Node otherNode) {
			return oneNode.getName().compareToIgnoreCase(otherNode.getName());
		}

	}

	private static class FacetValueSorter implements Comparator<String> {

		public FacetValueSorter() {
		}

		@Override
		public int compare(String oneValue, String otherValue) {
			if (Facet.VALUE_UNKNOWN.equals(oneValue)) {
				if (Facet.VALUE_UNKNOWN.equals(otherValue)) {
					return 0;
				} else {
					return 1; // unknown comes last
				}
			} else if (Facet.VALUE_UNKNOWN.equals(otherValue)) {
				return -1;
			} else {
				return oneValue.compareToIgnoreCase(otherValue);
			}
		}

	}

}