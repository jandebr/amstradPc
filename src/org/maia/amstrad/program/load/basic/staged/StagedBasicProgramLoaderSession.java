package org.maia.amstrad.program.load.basic.staged;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.basic.locomotive.token.VariableToken;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.load.AmstradProgramLoaderSession;
import org.maia.amstrad.program.load.AmstradProgramRuntime;
import org.maia.amstrad.program.load.basic.staged.EndingBasicPreprocessor.EndingMacro;
import org.maia.amstrad.program.load.basic.staged.ErrorOutBasicPreprocessor.ErrorOutMacro;
import org.maia.amstrad.program.load.basic.staged.PreambleBasicPreprocessor.PreambleLineMacro;
import org.maia.amstrad.program.load.basic.staged.file.TextFileReader;
import org.maia.amstrad.program.load.basic.staged.file.TextFileWriter;

public class StagedBasicProgramLoaderSession extends AmstradProgramLoaderSession implements LocomotiveBasicMemoryMap {

	private boolean produceRemarks;

	private int himemAddress;

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	private Set<StagedBasicMacro> macrosAdded;

	private StagedLineNumberMapping originalToStagedLineNumberMapping;

	private Map<VariableToken, VariableToken> originalToStagedVariableMapping;

	private List<AmstradProgram> chainedPrograms;

	private TextFileWriter textFileWriter;

	private TextFileReader textFileReader;

	public StagedBasicProgramLoaderSession(StagedBasicProgramLoader loader, AmstradProgramRuntime programRuntime) {
		super(loader, programRuntime);
		setHimemAddress(INITIAL_HIMEM);
		this.macrosAdded = new HashSet<StagedBasicMacro>();
		this.chainedPrograms = new Vector<AmstradProgram>();
		addProgramToChain(getProgram());
		setOriginalToStagedVariableMapping(new HashMap<VariableToken, VariableToken>());
	}

	public StagedBasicProgramLoaderSession createNewSession() {
		StagedBasicProgramLoaderSession session = new StagedBasicProgramLoaderSession(getLoader(), getProgramRuntime());
		session.setProduceRemarks(produceRemarks());
		session.setEndingAction(getEndingAction());
		session.setCodeDisclosure(getCodeDisclosure());
		session.getChainedPrograms().clear();
		for (AmstradProgram program : getChainedPrograms())
			session.addProgramToChain(program);
		return session;
	}

	public synchronized TextFileWriter openTextFileWriter(File fileOut) throws IOException {
		closeTextFileWriter();
		setTextFileWriter(new TextFileWriter(fileOut));
		return getTextFileWriter();
	}

	public synchronized void closeTextFileWriter() {
		if (getTextFileWriter() != null) {
			try {
				getTextFileWriter().close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				setTextFileWriter(null);
			}
		}
	}

	public synchronized TextFileReader openTextFileReader(File fileIn) throws IOException {
		closeTextFileReader();
		setTextFileReader(new TextFileReader(fileIn));
		return getTextFileReader();
	}

	public synchronized void closeTextFileReader() {
		if (getTextFileReader() != null) {
			try {
				getTextFileReader().close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				setTextFileReader(null);
			}
		}
	}

	public synchronized int reserveMemory(int numberOfBytes) {
		setHimemAddress(getHimemAddress() - numberOfBytes);
		int memoryOffset = getHimemAddress() + 1;
		getAmstradPc().getMemory().eraseBytes(memoryOffset, numberOfBytes);
		return memoryOffset;
	}

	public synchronized int getReservedMemoryInBytes() {
		return INITIAL_HIMEM - getHimemAddress();
	}

	public synchronized int acquireSmallestAvailablePreambleLineNumber() {
		Iterator<PreambleLineMacro> it = getMacrosAdded(PreambleLineMacro.class).iterator();
		if (!it.hasNext())
			return -1;
		PreambleLineMacro macroMin = it.next();
		int lnMin = macroMin.getLineNumberFrom();
		while (it.hasNext()) {
			PreambleLineMacro macro = it.next();
			int ln = macro.getLineNumberFrom();
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
		int lnMax = macroMax.getLineNumberFrom();
		while (it.hasNext()) {
			PreambleLineMacro macro = it.next();
			int ln = macro.getLineNumberFrom();
			if (ln > lnMax) {
				lnMax = ln;
				macroMax = macro;
			}
		}
		removeMacro(macroMax);
		return lnMax;
	}

	public synchronized int getEndingMacroLineNumber() {
		EndingMacro macro = getEndingMacro();
		if (macro != null) {
			return macro.getLineNumberFrom();
		} else {
			return -1;
		}
	}

	public synchronized int getErrorOutMacroLineNumber() {
		ErrorOutMacro macro = getErrorOutMacro();
		if (macro != null) {
			return macro.getLineNumberFrom();
		} else {
			return -1;
		}
	}

	public synchronized EndingMacro getEndingMacro() {
		return getMacroAdded(EndingMacro.class);
	}

	public synchronized ErrorOutMacro getErrorOutMacro() {
		return getMacroAdded(ErrorOutMacro.class);
	}

	public synchronized void addMacrosFrom(StagedBasicProgramLoaderSession otherSession) {
		for (StagedBasicMacro macro : otherSession.getMacrosAdded()) {
			addMacro(macro);
		}
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

	public BasicLineNumberScope getSnapshotScopeOfMacros() {
		return new MacrosSnapshotScope(getMacrosAdded());
	}

	public BasicLineNumberScope getSnapshotScopeOfCodeExcludingMacros(BasicSourceCode sourceCode) {
		return new ExcludingScope(new CodeSnapshotScope(sourceCode), getSnapshotScopeOfMacros());
	}

	public void renumMacros(BasicLineNumberLinearMapping mapping) {
		for (StagedBasicMacro macro : getMacrosAdded()) {
			macro.renum(mapping);
		}
	}

	public void addProgramToChain(AmstradProgram program) {
		getChainedPrograms().add(program);
	}

	public AmstradProgram getLastProgramInChain() {
		return getChainedPrograms().get(getChainedPrograms().size() - 1);
	}

	public List<AmstradProgram> getChainedPrograms() {
		return chainedPrograms;
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

	public Set<StagedBasicMacro> getMacrosAdded() {
		return macrosAdded;
	}

	public StagedLineNumberMapping getOriginalToStagedLineNumberMapping() {
		return originalToStagedLineNumberMapping;
	}

	public void setOriginalToStagedLineNumberMapping(StagedLineNumberMapping mapping) {
		this.originalToStagedLineNumberMapping = mapping;
	}

	public Map<VariableToken, VariableToken> getOriginalToStagedVariableMapping() {
		return originalToStagedVariableMapping;
	}

	public void setOriginalToStagedVariableMapping(Map<VariableToken, VariableToken> mapping) {
		this.originalToStagedVariableMapping = mapping;
	}

	public TextFileWriter getTextFileWriter() {
		return textFileWriter;
	}

	private void setTextFileWriter(TextFileWriter textFileWriter) {
		this.textFileWriter = textFileWriter;
	}

	public TextFileReader getTextFileReader() {
		return textFileReader;
	}

	private void setTextFileReader(TextFileReader textFileReader) {
		this.textFileReader = textFileReader;
	}

	private static class MacrosSnapshotScope extends BasicLineNumberScope {

		private Collection<BasicLineNumberRange> ranges;

		public MacrosSnapshotScope(Set<StagedBasicMacro> macros) {
			this.ranges = new Vector<BasicLineNumberRange>(macros.size());
			for (StagedBasicMacro macro : macros) {
				this.ranges.add(macro.getLineNumberRange());
			}
		}

		@Override
		public boolean isInScope(int lineNumber) {
			for (BasicLineNumberRange range : getRanges()) {
				if (range.containsLineNumber(lineNumber)) {
					return true;
				}
			}
			return false;
		}

		private Collection<BasicLineNumberRange> getRanges() {
			return ranges;
		}

	}

	private static class CodeSnapshotScope extends BasicLineNumberScope {

		private Set<Integer> lineNumbersInScope;

		public CodeSnapshotScope(BasicSourceCode sourceCode) {
			this.lineNumbersInScope = new HashSet<Integer>(sourceCode.getAscendingLineNumbers());
		}

		@Override
		public boolean isInScope(int lineNumber) {
			return getLineNumbersInScope().contains(lineNumber);
		}

		private Set<Integer> getLineNumbersInScope() {
			return lineNumbersInScope;
		}

	}

	private static class ExcludingScope extends BasicLineNumberScope {

		private BasicLineNumberScope scopeIn;

		private BasicLineNumberScope scopeOut;

		public ExcludingScope(BasicLineNumberScope scopeIn, BasicLineNumberScope scopeOut) {
			this.scopeIn = scopeIn;
			this.scopeOut = scopeOut;
		}

		@Override
		public boolean isInScope(int lineNumber) {
			return getScopeIn().isInScope(lineNumber) && !getScopeOut().isInScope(lineNumber);
		}

		private BasicLineNumberScope getScopeIn() {
			return scopeIn;
		}

		private BasicLineNumberScope getScopeOut() {
			return scopeOut;
		}

	}

}