package org.maia.amstrad.basic;

public interface BasicDecompiler {

	BasicSourceCode decompile(BasicByteCode byteCode) throws BasicException;

}