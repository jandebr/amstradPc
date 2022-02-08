package jemu.core.breakpoint;

import jemu.core.cpu.ProgramCounterObserverImpl;

/**
 * Base class for breakpoints.
 *
 * @author John Girvin
 */
public abstract class Breakpoint extends ProgramCounterObserverImpl {

	/**
	 * Create a Breakpoint on the given processor at the given address.
	 *
	 * @param processor - the processor to observe
	 * @param address - the program counter value to observe
	 *
	 * @see jemu.core.cpu.ProgramCounterObserverImpl(int)
	 */
	public Breakpoint(int address) {
		super(address);
	}

}