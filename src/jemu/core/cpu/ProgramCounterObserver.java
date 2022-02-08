package jemu.core.cpu;

/**
 * Interface for observers of a program counter value.
 *
 * @author John Girvin
 */
public interface ProgramCounterObserver {

	/**
	 * Notify of a program counter change.
	 *
	 * @param address - the new program counter value.
	 */
	public void update(int address);

}
