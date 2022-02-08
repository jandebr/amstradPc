package jemu.core.breakpoint;

import jemu.core.Util;
import jemu.core.cpu.Processor;
import jemu.core.samples.Samples;

/**
 * Breakpoint implementation to stop execution when a
 * processor program counter reaches a certain address.
 *
 * @author John Girvin
 */
public class StopBreakpoint extends ProcessorBreakpoint {

	//
	// ProgramCounterObserver INTERFACE
	//

	/**
	 * @see jemu.core.cpu.ProgramCounterObserver#update(int)
	 */
	@Override
	public void update(int address) {
		// If the processor PC matches the break address, stop the processor
		if (address == getAddress()) {
			getProcessor().stop();
			// TODO: remove direct reference to debugger
			jemu.ui.Debugger.setDisass(address);
            System.out.println("StopBreakpoint at address &"+Util.hex(address));
            Samples.BREAK.play();
		}
	}

	//
	// CONSTRUCTORS
	//

	/**
	 * @see ProcessorBreakpoint#ProcessorBreakpoint(Processor, int)
	 */
	public StopBreakpoint(Processor processor, int address) {
		super(processor, address);
	}

}
