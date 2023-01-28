package org.maia.amstrad.program.loader.basic.staged;

import java.util.List;

import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberLinearMappingImpl;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;

public class StagedLineNumberMapping implements BasicLineNumberLinearMapping {

	private BasicLineNumberLinearMapping delegate;

	private StagedLineNumberMapping(BasicLineNumberLinearMapping delegate) {
		setDelegate(delegate);
	}

	public static StagedLineNumberMapping identityMapping(BasicSourceCode sourceCode, BasicLineNumberScope scope) {
		BasicLineNumberLinearMappingImpl mapping = new BasicLineNumberLinearMappingImpl();
		List<Integer> lineNumbers = sourceCode.getAscendingLineNumbers();
		for (int i = 0; i < lineNumbers.size(); i++) {
			int ln = lineNumbers.get(i);
			if (scope.isInScope(ln)) {
				mapping.addMapping(ln, ln);
			}
		}
		return new StagedLineNumberMapping(mapping);
	}

	public static StagedLineNumberMapping renumMapping(BasicLineNumberLinearMapping renumMapping,
			BasicLineNumberScope renumScope) {
		return new StagedLineNumberMapping(new ScopedMapping(renumMapping, renumScope));
	}

	public void union(BasicLineNumberLinearMapping mapping) {
		setDelegate(new UnionMapping(getDelegate(), mapping));
	}

	public void union(BasicLineNumberLinearMapping mapping, BasicLineNumberScope scope) {
		setDelegate(new UnionMapping(getDelegate(), new ScopedMapping(mapping, scope)));
	}

	@Override
	public boolean isMapped(int oldLineNumber) {
		return getDelegate().isMapped(oldLineNumber);
	}

	@Override
	public int getNewLineNumber(int oldLineNumber) {
		return getDelegate().getNewLineNumber(oldLineNumber);
	}

	private BasicLineNumberLinearMapping getDelegate() {
		return delegate;
	}

	private void setDelegate(BasicLineNumberLinearMapping delegate) {
		this.delegate = delegate;
	}

	private static class ScopedMapping implements BasicLineNumberLinearMapping {

		private BasicLineNumberLinearMapping mapping;

		private BasicLineNumberScope scope;

		public ScopedMapping(BasicLineNumberLinearMapping mapping, BasicLineNumberScope scope) {
			this.mapping = mapping;
			this.scope = scope;
		}

		@Override
		public boolean isMapped(int oldLineNumber) {
			return getScope().isInScope(oldLineNumber) && getMapping().isMapped(oldLineNumber);
		}

		@Override
		public int getNewLineNumber(int oldLineNumber) {
			if (isMapped(oldLineNumber)) {
				return getMapping().getNewLineNumber(oldLineNumber);
			} else {
				return -1;
			}
		}

		private BasicLineNumberLinearMapping getMapping() {
			return mapping;
		}

		private BasicLineNumberScope getScope() {
			return scope;
		}

	}

	private static class UnionMapping implements BasicLineNumberLinearMapping {

		private BasicLineNumberLinearMapping[] mappings;

		public UnionMapping(BasicLineNumberLinearMapping... mappings) {
			this.mappings = mappings;
		}

		@Override
		public boolean isMapped(int oldLineNumber) {
			for (BasicLineNumberLinearMapping mapping : getMappings()) {
				if (mapping.isMapped(oldLineNumber))
					return true;
			}
			return false;
		}

		@Override
		public int getNewLineNumber(int oldLineNumber) {
			for (BasicLineNumberLinearMapping mapping : getMappings()) {
				if (mapping.isMapped(oldLineNumber))
					return mapping.getNewLineNumber(oldLineNumber);
			}
			return -1;
		}

		private BasicLineNumberLinearMapping[] getMappings() {
			return mappings;
		}

	}

}