package org.maia.amstrad.program.repo.facet;

import java.util.List;
import java.util.Vector;

public class FacetList implements Cloneable {

	private List<Facet> facets;

	public FacetList() {
		this.facets = new Vector<Facet>();
	}

	public FacetList(List<Facet> facets) {
		this();
		for (Facet facet : facets)
			add(facet);
	}

	public void add(Facet facet) {
		getFacets().add(facet);
	}

	public boolean isEmpty() {
		return getFacets().isEmpty();
	}

	public int size() {
		return getFacets().size();
	}

	public Facet getFacet(int index) {
		return getFacets().get(index);
	}

	public FacetList subList(int fromIndex) {
		return new FacetList(getFacets().subList(fromIndex, size()));
	}

	@Override
	public FacetList clone() {
		return new FacetList(getFacets());
	}

	private List<Facet> getFacets() {
		return facets;
	}

}