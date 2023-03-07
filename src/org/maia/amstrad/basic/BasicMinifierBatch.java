package org.maia.amstrad.basic;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BasicMinifierBatch implements BasicMinifier, Iterable<BasicMinifier> {

	private List<BasicMinifier> minifiers;

	public BasicMinifierBatch() {
		this.minifiers = new Vector<BasicMinifier>();
	}

	@Override
	public void minify(BasicSourceCode sourceCode) throws BasicException {
		for (BasicMinifier minifier : getMinifiers()) {
			minifier.minify(sourceCode);
		}
	}

	public void add(BasicMinifier minifier) {
		getMinifiers().add(minifier);
	}

	public void remove(BasicMinifier minifier) {
		getMinifiers().remove(minifier);
	}

	@Override
	public Iterator<BasicMinifier> iterator() {
		return getMinifiers().iterator();
	}

	private List<BasicMinifier> getMinifiers() {
		return minifiers;
	}

}