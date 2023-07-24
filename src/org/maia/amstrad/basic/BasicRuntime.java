package org.maia.amstrad.basic;

import java.util.List;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.util.AmstradUtils;

public abstract class BasicRuntime {

	private AmstradPc amstradPc;

	protected BasicRuntime(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	/**
	 * Tells whether Basic is operating in "direct modus" and ready to accept instructions from the keyboard
	 * 
	 * @return <code>true</code> when in direct modus, <code>false</code> otherwise
	 */
	public abstract boolean isReady();

	public void waitUntilReady() {
		while (!isReady()) {
			AmstradUtils.sleep(100L);
		}
	}

	public void waitUntilReady(long maximumWaitTimeMillis) {
		long timeout = System.currentTimeMillis() + maximumWaitTimeMillis;
		while (!isReady() && System.currentTimeMillis() < timeout) {
			AmstradUtils.sleep(100L);
		}
	}

	public void sendKeyboardInputIfReadyAndWait(CharSequence input) {
		if (isReady()) {
			getAmstradPc().getKeyboard().enter(input);
			waitUntilReady();
		}
	}

	public void sendKeyboardInputIfReady(CharSequence input) {
		if (isReady()) {
			getAmstradPc().getKeyboard().enter(input);
		}
	}

	/**
	 * Clears any loaded program code and variables.
	 * <p>
	 * This has a similar effect as when executing the Basic <code>NEW</code> instruction.
	 * </p>
	 * <p>
	 * This method should be used only when no program is being run (in the Basic "direct modus"). When a program is
	 * still running, it may lead to unexpected behaviour even blocking the entire Amstrad computer.
	 * </p>
	 * 
	 * @throws BasicException
	 *             If a problem is encountered while renewing
	 * 
	 * @see #isReady()
	 */
	public abstract void renew() throws BasicException;

	/**
	 * Clears any variables but keeping the loaded program code.
	 * <p>
	 * This has a similar effect as when executing the Basic <code>CLEAR</code> instruction.
	 * </p>
	 * <p>
	 * This method should be used only when no program is being run (in the Basic "direct modus"). When a program is
	 * still running, it may lead to unexpected behaviour even blocking the entire Amstrad computer.
	 * </p>
	 * 
	 * @throws BasicException
	 *             If a problem is encountered while clearing
	 * 
	 * @see #isReady()
	 */
	public abstract void clear() throws BasicException;

	/**
	 * Loads the given program code, replacing any previous code and clearing all variables
	 * 
	 * @param code
	 *            The new code to load
	 * @throws BasicException
	 *             If the language of the <code>code</code> does not match this runtime, is unrecognized, cannot be
	 *             compiled or cannot be loaded for some reason (e.g., does not fit into memory)
	 * @see #getLanguage()
	 */
	public final void load(BasicCode code) throws BasicException {
		if (!code.getLanguage().equals(getLanguage()))
			throw new BasicException("Basic language mismatch");
		if (code instanceof BasicByteCode) {
			loadByteCode((BasicByteCode) code);
		} else if (code instanceof BasicSourceCode) {
			loadByteCode(getCompiler().compile((BasicSourceCode) code));
		} else {
			throw new BasicException("Unrecognized Basic code");
		}
	}

	protected abstract void loadByteCode(BasicByteCode code) throws BasicException;

	public final void run(BasicCode code) throws BasicException {
		load(code);
		run();
	}

	public final void run(BasicCode code, int lineNumber) throws BasicException {
		load(code);
		run(lineNumber);
	}

	public abstract void run();

	public abstract void run(int lineNumber);

	public BasicLineNumberLinearMapping renum() throws BasicException {
		return renum(10, 10);
	}

	public abstract BasicLineNumberLinearMapping renum(int lineNumberStart, int lineNumberStep) throws BasicException;

	public abstract void renum(BasicLineNumberLinearMapping mapping) throws BasicException;

	/**
	 * Replaces the loaded program code with the given code while preserving variables.
	 * 
	 * <p>
	 * This method should be used with extreme care. Swapping code may lead to unexpected behaviour even blocking the
	 * entire Amstrad computer. In general, it is only safe to swap when no program is being run (in the Basic "direct
	 * modus"). When a program is still running (<em>hot swap</em>) it will only succeed under conditions that cause no
	 * interference with the running Basic interpreter.
	 * </p>
	 * <p>
	 * If it is not needed to preserve variables, it is advised to use the {@link #load(BasicCode)} method.
	 * </p>
	 * 
	 * @param newCode
	 *            The new code to swap in.
	 * @see #isReady()
	 */
	public final void swap(BasicCode newCode) throws BasicException {
		if (!newCode.getLanguage().equals(getLanguage()))
			throw new BasicException("Basic language mismatch");
		if (newCode instanceof BasicByteCode) {
			swapByteCode((BasicByteCode) newCode);
		} else if (newCode instanceof BasicSourceCode) {
			swapByteCode(getCompiler().compile((BasicSourceCode) newCode));
		} else {
			throw new BasicException("Unrecognized Basic code");
		}
	}

	protected abstract void swapByteCode(BasicByteCode newByteCode) throws BasicException;

	public BasicSourceCode exportSourceCode() throws BasicException {
		return getDecompiler().decompile(exportByteCode());
	}

	public abstract BasicByteCode exportByteCode() throws BasicException;

	public void loadBinaryData(byte[] data, int memoryStartAddress) {
		loadBinaryData(data, 0, data.length, memoryStartAddress);
	}

	public void loadBinaryData(byte[] data, int dataOffset, int dataLength, int memoryStartAddress) {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			memory.writeBytes(memoryStartAddress, data, dataOffset, dataLength);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	public byte[] exportBinaryData(int memoryStartAddress, int memoryLength) {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			return memory.readBytes(memoryStartAddress, memoryLength);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	public abstract int getHimem();

	public abstract int getFreeMemory();

	public abstract int getUsedMemory();

	public abstract int getTotalMemory();

	public byte peek(int memoryAddress) {
		return getMemory().readByte(memoryAddress);
	}

	public void poke(int memoryAddress, byte value) {
		getMemory().writeByte(memoryAddress, value);
	}

	public abstract int getDisplayCanvasWidth();

	public abstract int getDisplayCanvasHeight();

	public abstract int getDisplayTextColumns();

	public abstract int getDisplayTextRows();

	public abstract int getDefaultBorderColorIndex();

	public abstract int getDefaultPaperColorIndex();

	public abstract int getDefaultPenColorIndex();

	public abstract int getMinimumLineNumber();

	public abstract int getMaximumLineNumber();

	public int getNextAvailableLineNumber() {
		return getNextAvailableLineNumber(1);
	}

	public abstract int getNextAvailableLineNumber(int lineNumberStep);

	public abstract List<Integer> getAscendingLineNumbers();

	public abstract BasicCompiler getCompiler();

	public abstract BasicDecompiler getDecompiler();

	public abstract BasicLanguage getLanguage();

	protected AmstradMemory getMemory() {
		return getAmstradPc().getMemory();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}