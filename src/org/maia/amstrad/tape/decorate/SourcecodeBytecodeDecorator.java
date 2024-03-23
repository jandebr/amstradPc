package org.maia.amstrad.tape.decorate;

import java.util.List;

import org.maia.amstrad.tape.decorate.SourcecodeBytecodeDecorator.SourcecodeBytecodeDecoration;
import org.maia.amstrad.tape.model.ByteCodeRange;
import org.maia.amstrad.tape.model.SourceCodePosition;
import org.maia.amstrad.tape.model.SourceCodeRange;

public class SourcecodeBytecodeDecorator extends SequenceDecorator<SourcecodeBytecodeDecoration> {

	public SourcecodeBytecodeDecorator() {
		super(2000);
	}

	public void decorate(SourceCodeRange sourceCodeRange, ByteCodeRange byteCodeRange) {
		addDecoration(new SourcecodeBytecodeDecoration(sourceCodeRange, byteCodeRange));
	}

	public List<SourcecodeBytecodeDecoration> getDecorationsInsideRange(SourceCodePosition from,
			SourceCodePosition until) {
		return getDecorationsInRange(convertToSequentialOffset(from), convertToSequentialOffset(until), false);
	}

	public List<SourcecodeBytecodeDecoration> getDecorationsOverlappingRange(SourceCodePosition from,
			SourceCodePosition until) {
		return getDecorationsInRange(convertToSequentialOffset(from), convertToSequentialOffset(until), true);
	}

	private static long convertToSequentialOffset(SourceCodePosition position) {
		// line numbers in Basic are in range [1, 65535] meaning 16 bit integer
		return ((long) position.getLineNumber() << 16) | (long) position.getLinePosition();
	}

	public static class SourcecodeBytecodeDecoration extends SequenceDecoration {

		private SourceCodeRange sourceCodeRange;

		private ByteCodeRange byteCodeRange;

		public SourcecodeBytecodeDecoration(SourceCodeRange sourceCodeRange, ByteCodeRange byteCodeRange) {
			super(convertToSequentialOffset(sourceCodeRange.getStartPosition()),
					convertToSequentialOffset(sourceCodeRange.getEndPosition())
							- convertToSequentialOffset(sourceCodeRange.getStartPosition()) + 1L);
			this.sourceCodeRange = sourceCodeRange;
			this.byteCodeRange = byteCodeRange;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(48);
			SourceCodePosition from = getSourceCodeRange().getStartPosition();
			SourceCodePosition until = getSourceCodeRange().getEndPosition();
			sb.append(from);
			sb.append(" -> ");
			sb.append(until);
			sb.append(" (bytecode@");
			sb.append(getByteCodeRange().getByteCodeOffset());
			sb.append("->");
			sb.append(getByteCodeRange().getByteCodeEnd());
			sb.append(")");
			return sb.toString();
		}

		@Override
		protected String getHumanReadableDecoration() {
			return null;
		}

		public SourceCodeRange getSourceCodeRange() {
			return sourceCodeRange;
		}

		public ByteCodeRange getByteCodeRange() {
			return byteCodeRange;
		}

	}

}