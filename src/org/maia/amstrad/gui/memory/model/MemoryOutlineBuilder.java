package org.maia.amstrad.gui.memory.model;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.pc.AmstradPc;

public class MemoryOutlineBuilder {

	public static final String LABEL_FREE = "Free";

	public MemoryOutlineBuilder() {
	}

	public MemoryOutline buildFor(AmstradPc amstradPc) {
		BasicRuntime rt = amstradPc.getBasicRuntime();
		if (rt instanceof LocomotiveBasicRuntime) {
			return createLocomotiveBasicMemoryOutline((LocomotiveBasicRuntime) rt);
		} else {
			return createBasicMemoryOutline(rt);
		}
	}

	private MemoryOutline createBasicMemoryOutline(BasicRuntime rt) {
		MemoryOutline outline = new MemoryOutline();
		outline.appendSegment(rt.getUsedMemory(), "Used", 26);
		outline.appendSegment(rt.getFreeMemory(), LABEL_FREE, 22);
		outline.appendSegment(Math.max(65536 - outline.getByteLength(), 0), "System", 5);
		return outline;
	}

	private MemoryOutline createLocomotiveBasicMemoryOutline(LocomotiveBasicRuntime rt) {
		MemoryOutline outline = new MemoryOutline();
		outline.appendSegment(rt.getUsedMemoryForByteCode(), "Code", 15);
		outline.appendSegment(rt.getUsedMemoryForVariables(), "Variables", 26);
		outline.appendSegment(rt.getFreeMemory(), LABEL_FREE, 22);
		outline.appendSegment(rt.getUsedMemoryForHeap(), "Heap", 17);
		outline.appendSegment(rt.getReservedMemory(), "Reserved", 7);
		outline.appendSegment(Math.max(65536 - 16384 - outline.getByteLength(), 0), "System", 5);
		outline.appendSegment(16384, "Display", 11);
		return outline;
	}

}