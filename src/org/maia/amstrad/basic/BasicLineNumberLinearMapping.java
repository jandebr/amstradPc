package org.maia.amstrad.basic;

/**
 * A linear mapping of one sequence of line numbers to another sequence of line numbers.
 * 
 * <p>
 * The mapping can be <em>partial</em>. Line numbers that are not mapped remain their original value (implicit mapping
 * to self).
 * </p>
 * <p>
 * The mapping is <em>linear</em> and so guarantees to preserve the relative order. Meaning, for any pair of line
 * numbers <code>(M,N)</code> in the original sequence where <code>M &lt; N</code>, it holds that
 * <code>getNewLineNumber(M) &lt; getNewLineNumber(N)</code>.
 * </p>
 */
public interface BasicLineNumberLinearMapping {

	boolean isEmpty();

	boolean isMapped(int oldLineNumber);

	int getNewLineNumber(int oldLineNumber);

}