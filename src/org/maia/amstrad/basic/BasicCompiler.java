package org.maia.amstrad.basic;

public interface BasicCompiler {

	BasicByteCode compile(BasicSourceCode sourceCode) throws BasicException;

}