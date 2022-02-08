package jemu.core.breakpoint;

import jemu.core.cpu.Processor;

/**
 * Base class for breakpoints that need access to their Processor object
 *
 * @author John Girvin
 */
public abstract class ProcessorBreakpoint extends Breakpoint {

	//
	// INSTANCE VARIABLES
	//

	// Processor object to observe
	private Processor processor;

	//
	// INSTANCE METHODS
	//

	/**
	 * Return the Processor object being observed
	 *
	 * @return the Processor object being observed
	 */
	public Processor getProcessor() {
		return processor;
	}

	/**
	 * Set the Processor object to be observed
	 *
	 * @param processor - the Processor object to be observed
	 */
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	//
	// CONSTRUCTORS
	//

	/**
	 * Create a Breakpoint on the given processor at the given address.
	 *
	 * @param processor - the processor to observe
	 * @param address - the program counter value to observe
	 */
	public ProcessorBreakpoint(Processor processor, int address) {
		super(address);
		setProcessor(processor);
	}


}