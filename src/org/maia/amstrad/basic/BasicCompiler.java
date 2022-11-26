package org.maia.amstrad.basic;

public interface BasicCompiler {

	byte[] compile(CharSequence sourceCode) throws BasicCompilationException;

}