package org.maia.amstrad.basic.locomotive;

public interface LocomotiveBasicMemoryMap {

	int ADDRESS_BYTECODE_START = 0x170;

	int ADDRESS_BYTECODE_END_POINTER = 0xAE83; // points to memory address following 0x0000

	int ADDRESS_BYTECODE_END_POINTER_BIS = 0xAE85;

	int ADDRESS_HEAP_END_POINTER = 0xAE87; // points to memory address following heap space

	int ADDRESS_HEAP_END_POINTER_BIS = 0xAE89;

	int ADDRESS_HIMEM = 0xA67B;
	
}