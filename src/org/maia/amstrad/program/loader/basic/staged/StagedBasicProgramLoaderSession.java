package org.maia.amstrad.program.loader.basic.staged;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;
import org.maia.amstrad.program.loader.basic.staged.EndingBasicPreprocessor.EndingMacro;
import org.maia.amstrad.program.loader.basic.staged.PreambleBasicPreprocessor.PreambleLineMacro;

public class StagedBasicProgramLoaderSession extends AmstradProgramLoaderSession implements LocomotiveBasicMemoryMap {

	private int himemAddress;

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	private Set<StagedBasicMacro> macrosAdded;

	private boolean produceRemarks;

	public StagedBasicProgramLoaderSession(StagedBasicProgramLoader loader, AmstradProgramRuntime programRuntime) {
		super(loader, programRuntime);
		setHimemAddress(ADDRESS_HIMEM);
		this.macrosAdded = new HashSet<StagedBasicMacro>();
	}

	public int reserveMemoryTrapAddress() {
		return reserveMemory(1);
	}

	public synchronized int reserveMemory(int numberOfBytes) {
		setHimemAddress(getHimemAddress() - numberOfBytes);
		int memoryOffset = getHimemAddress() + 1;
		getAmstradPc().getMemory().eraseBytes(memoryOffset, numberOfBytes);
		return memoryOffset;
	}

	public synchronized int getReservedMemoryInBytes() {
		return ADDRESS_HIMEM - getHimemAddress();
	}

	public synchronized int acquireFirstAvailablePreambleLineNumber() {
		Iterator<PreambleLineMacro> it = getMacrosAdded(PreambleLineMacro.class).iterator();
		if (!it.hasNext())
			return -1;
		PreambleLineMacro macroLow = it.next();
		int lnLow = macroLow.getLineNumberStart();
		while (it.hasNext()) {
			PreambleLineMacro macro = it.next();
			int ln = macro.getLineNumberStart();
			if (ln < lnLow) {
				lnLow = ln;
				macroLow = macro;
			}
		}
		removeMacro(macroLow);
		return lnLow;
	}

	public synchronized int getEndingMacroLineNumber() {
		EndingMacro macro = getEndingMacro();
		if (macro == null) {
			return -1;
		} else {
			return macro.getLineNumberStart();
		}
	}

	public synchronized EndingMacro getEndingMacro() {
		return getMacroAdded(EndingMacro.class);
	}

	public synchronized void addMacro(StagedBasicMacro macro) {
		getMacrosAdded().add(macro);
	}

	public synchronized void removeMacro(StagedBasicMacro macro) {
		getMacrosAdded().remove(macro);
	}

	public synchronized boolean hasMacrosAdded(Class<? extends StagedBasicMacro> macroType) {
		return getMacroAdded(macroType) != null;
	}

	public synchronized <T extends StagedBasicMacro> T getMacroAdded(Class<T> macroType) {
		for (StagedBasicMacro macro : getMacrosAdded()) {
			if (macroType.isAssignableFrom(macro.getClass())) {
				return macroType.cast(macro);
			}
		}
		return null;
	}

	public synchronized <T extends StagedBasicMacro> Set<T> getMacrosAdded(Class<T> macroType) {
		Set<T> macros = new HashSet<T>();
		for (StagedBasicMacro macro : getMacrosAdded()) {
			if (macroType.isAssignableFrom(macro.getClass())) {
				macros.add(macroType.cast(macro));
			}
		}
		return macros;
	}

	public Set<StagedBasicMacro> getMacrosAdded() {
		return macrosAdded;
	}

	public BasicLineNumberScope getScopeOfMacros() {
		return new BasicLineNumberScope() {

			@Override
			public boolean isInScope(int lineNumber) {
				for (StagedBasicMacro macro : getMacrosAdded()) {
					if (macro.containsLine(lineNumber))
						return true;
				}
				return false;
			}
		};
	}

	public BasicLineNumberScope getScopeExcludingMacros() {
		return new BasicLineNumberScope() {

			@Override
			public boolean isInScope(int lineNumber) {
				for (StagedBasicMacro macro : getMacrosAdded()) {
					if (macro.containsLine(lineNumber))
						return false;
				}
				return true;
			}
		};
	}

	@Override
	public StagedBasicProgramLoader getLoader() {
		return (StagedBasicProgramLoader) super.getLoader();
	}

	public BasicRuntime getBasicRuntime() {
		return getAmstradPc().getBasicRuntime();
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

	public boolean produceRemarks() {
		return produceRemarks;
	}

	public void setProduceRemarks(boolean produceRemarks) {
		this.produceRemarks = produceRemarks;
	}

}