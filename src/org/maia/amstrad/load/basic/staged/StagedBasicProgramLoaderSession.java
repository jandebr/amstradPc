package org.maia.amstrad.load.basic.staged;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.load.AmstradProgramLoaderSession;
import org.maia.amstrad.load.basic.staged.EndingBasicPreprocessor.EndingMacro;
import org.maia.amstrad.load.basic.staged.PreambleBasicPreprocessor.PreambleLineMacro;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class StagedBasicProgramLoaderSession extends AmstradProgramLoaderSession implements LocomotiveBasicMemoryMap {

	private boolean produceRemarks;

	private int himemAddress;

	private int endingMacroLineNumber;

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	private Set<StagedBasicMacro> macrosAdded;

	private StagedLineNumberMapping originalToStagedLineNumberMapping;

	public StagedBasicProgramLoaderSession(StagedBasicProgramLoader loader, AmstradProgramRuntime programRuntime) {
		super(loader, programRuntime);
		setHimemAddress(ADDRESS_HIMEM);
		this.endingMacroLineNumber = -1;
		this.macrosAdded = new HashSet<StagedBasicMacro>();
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

	public synchronized int acquireSmallestAvailablePreambleLineNumber() {
		Iterator<PreambleLineMacro> it = getMacrosAdded(PreambleLineMacro.class).iterator();
		if (!it.hasNext())
			return -1;
		PreambleLineMacro macroMin = it.next();
		int lnMin = macroMin.getLineNumberStart();
		while (it.hasNext()) {
			PreambleLineMacro macro = it.next();
			int ln = macro.getLineNumberStart();
			if (ln < lnMin) {
				lnMin = ln;
				macroMin = macro;
			}
		}
		removeMacro(macroMin);
		return lnMin;
	}

	public synchronized int acquireLargestAvailablePreambleLineNumber() {
		Iterator<PreambleLineMacro> it = getMacrosAdded(PreambleLineMacro.class).iterator();
		if (!it.hasNext())
			return -1;
		PreambleLineMacro macroMax = it.next();
		int lnMax = macroMax.getLineNumberStart();
		while (it.hasNext()) {
			PreambleLineMacro macro = it.next();
			int ln = macro.getLineNumberStart();
			if (ln > lnMax) {
				lnMax = ln;
				macroMax = macro;
			}
		}
		removeMacro(macroMax);
		return lnMax;
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

	public void renumMacros(BasicLineNumberLinearMapping mapping) {
		EndingMacro ending = getEndingMacro();
		for (StagedBasicMacro macro : getMacrosAdded()) {
			macro.renum(mapping);
			if (macro.equals(ending)) {
				setEndingMacroLineNumber(macro.getLineNumberStart());
			}
		}
	}

	@Override
	public StagedBasicProgramLoader getLoader() {
		return (StagedBasicProgramLoader) super.getLoader();
	}

	public AmstradProgram getLastProgramInChain() {
		// TODO
		return getProgram();
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

	public synchronized int getEndingMacroLineNumber() {
		if (endingMacroLineNumber < 0) {
			EndingMacro macro = getEndingMacro();
			if (macro != null) {
				endingMacroLineNumber = macro.getLineNumberStart();
			}
		}
		return endingMacroLineNumber;
	}

	private void setEndingMacroLineNumber(int lineNumber) {
		endingMacroLineNumber = lineNumber;
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

	public Set<StagedBasicMacro> getMacrosAdded() {
		return macrosAdded;
	}

	public StagedLineNumberMapping getOriginalToStagedLineNumberMapping() {
		return originalToStagedLineNumberMapping;
	}

	public void setOriginalToStagedLineNumberMapping(StagedLineNumberMapping mapping) {
		this.originalToStagedLineNumberMapping = mapping;
	}

}