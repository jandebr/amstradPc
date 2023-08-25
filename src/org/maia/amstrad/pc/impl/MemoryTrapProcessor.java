package org.maia.amstrad.pc.impl;

import org.maia.amstrad.util.AsyncSerialTaskWorker;

public class MemoryTrapProcessor extends AsyncSerialTaskWorker<MemoryTrapTask> {

	public MemoryTrapProcessor() {
		super("Memorytrap processor");
	}

}