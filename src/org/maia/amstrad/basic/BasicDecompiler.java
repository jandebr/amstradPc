package org.maia.amstrad.basic;

public interface BasicDecompiler {

	CharSequence decompile(byte[] byteCode) throws BasicDecompilationException;

}