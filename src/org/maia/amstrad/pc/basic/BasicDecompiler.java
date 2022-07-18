package org.maia.amstrad.pc.basic;

public interface BasicDecompiler {

	CharSequence decompile(byte[] byteCode) throws BasicDecompilationException;

}