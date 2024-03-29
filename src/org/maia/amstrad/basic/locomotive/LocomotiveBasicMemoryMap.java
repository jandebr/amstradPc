package org.maia.amstrad.basic.locomotive;

public interface LocomotiveBasicMemoryMap {

	int ADDRESS_BYTECODE_START = 0x170;

	int ADDRESS_BYTECODE_END_POINTER = 0xAE83; // points to memory address following code ending 0x0000

	int ADDRESS_BYTECODE_END_POINTER_BIS = 0xAE85;

	int ADDRESS_VARIABLE_SPACE_END_POINTER = 0xAE87; // points to memory address following variable space

	int ADDRESS_VARIABLE_SPACE_END_POINTER_BIS = 0xAE89;
	
	int ADDRESS_HEAP_SPACE_POINTER = 0xB08D; // points to memory address preceding heap space

	int ADDRESS_HIMEM_POINTER = 0xAE7B; // points to HIMEM memory address

	int INITIAL_HIMEM = 0xA67B;

	int ADDRESS_GRAPHICS_DISPLAY_START = 0xC000;

	int ADDRESS_GRAPHICS_DISPLAY_END = 0xFFFF;

}