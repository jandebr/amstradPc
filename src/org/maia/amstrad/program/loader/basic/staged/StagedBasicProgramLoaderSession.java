package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public class StagedBasicProgramLoaderSession extends AmstradProgramLoaderSession implements LocomotiveBasicMemoryMap {

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	private int upperMemoryTrapAddress;

	public StagedBasicProgramLoaderSession(StagedBasicProgramLoader loader, AmstradProgramRuntime programRuntime) {
		super(loader, programRuntime);
		setUpperMemoryTrapAddress(ADDRESS_HIMEM);
	}

	public synchronized int claimMemoryTrapAddress() {
		return claimMemoryTrapAddress((byte) 0);
	}

	public synchronized int claimMemoryTrapAddress(byte memoryValueOff) {
		int address = getUpperMemoryTrapAddress() - 1;
		setUpperMemoryTrapAddress(address);
		getAmstradPc().getBasicRuntime().poke(address, memoryValueOff);
		return address;
	}

	public synchronized int getClaimedMemoryTrapAddresses() {
		return ADDRESS_HIMEM - getUpperMemoryTrapAddress();
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

	private int getUpperMemoryTrapAddress() {
		return upperMemoryTrapAddress;
	}

	private void setUpperMemoryTrapAddress(int memoryAddress) {
		this.upperMemoryTrapAddress = memoryAddress;
	}

}