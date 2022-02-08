package jemu.core.cpu;

/**
 * Base implementation of the ProgramCounterObserver interface
 * to observe a single program counter value.
 *
 * @author John Girvin
 */
public abstract class ProgramCounterObserverImpl implements ProgramCounterObserver {

	//
	// INSTANCE VARIABLES
	//

	private int address = 0xa0000;

	//
	// INSTANCE METHODS
	//

	/**
	 * Return the program counter value being observed.
	 *
	 * @return the program counter value being observed.
	 */
	public int getAddress() {
		return address;
	}

	/**
	 * Set the program counter value to be observed.
	 *
	 * @param address - the program counter value to observe
	 */
	public void setAddress(int address) {
		this.address = address;
	}

	//
	// ProgramCounterObserver INTERFACE
	//

	/**
	 * @see jemu.core.cpu.ProgramCounterObserver#update(int)
	 */
	@Override
	public abstract void update(int address);

	//
	// CONSTRUCTORS
	//

	/**
	 * Create a ProgramCounterObserver on the given address.
	 *
	 * @param address - the program counter value to observe
	 */
	public ProgramCounterObserverImpl(int address) {
		super();
		setAddress(address);
	}
}
