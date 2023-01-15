package org.maia.amstrad.program.loader.basic.staged;

import java.util.HashSet;
import java.util.Set;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public class StagedBasicProgramLoaderSession extends AmstradProgramLoaderSession implements LocomotiveBasicMemoryMap {

	private int himemAddress;

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	private Set<StagedBasicMacro> macrosAdded;

	public StagedBasicProgramLoaderSession(StagedBasicProgramLoader loader, AmstradProgramRuntime programRuntime) {
		super(loader, programRuntime);
		setHimemAddress(ADDRESS_HIMEM);
		this.macrosAdded = new HashSet<StagedBasicMacro>();
	}

	public int reserveMemoryTrapAddress() {
		return reserveMemory(1);
	}

	public synchronized int reserveMemory(int numberOfBytes) {
		int memoryOffset = getHimemAddress() - numberOfBytes;
		setHimemAddress(memoryOffset);
		getAmstradPc().getMemory().eraseBytes(memoryOffset, numberOfBytes);
		return memoryOffset;
	}

	public synchronized int getReservedMemoryInBytes() {
		return ADDRESS_HIMEM - getHimemAddress();
	}

	public void addMacro(StagedBasicMacro macro) {
		getMacrosAdded().add(macro);
	}

	public <T extends StagedBasicMacro> T getMacroAdded(Class<T> macroType) {
		for (StagedBasicMacro macro : getMacrosAdded()) {
			if (macroType.isAssignableFrom(macro.getClass()))
				return macroType.cast(macro);
		}
		return null;
	}

	public boolean isMacroAdded(Class<? extends StagedBasicMacro> macroType) {
		return getMacroAdded(macroType) != null;
	}

	public Set<StagedBasicMacro> getMacrosAdded() {
		return macrosAdded;
	}

	@Override
	public StagedBasicProgramLoader getLoader() {
		return (StagedBasicProgramLoader) super.getLoader();
	}

	private int getHimemAddress() {
		return himemAddress;
	}

	private void setHimemAddress(int himemAddress) {
		this.himemAddress = himemAddress;
	}

	public EndingBasicAction getEndingAction() {
		return endingAction;
	}

	public void setEndingAction(EndingBasicAction endingAction) {
		this.endingAction = endingAction;
	}

	public EndingBasicCodeDisclosure getCodeDisclosure() {
		return codeDisclosure;
	}

	public void setCodeDisclosure(EndingBasicCodeDisclosure codeDisclosure) {
		this.codeDisclosure = codeDisclosure;
	}

}