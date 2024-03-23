package org.maia.amstrad.tape.read;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.tape.decorate.BytecodeAudioDecorator;
import org.maia.amstrad.tape.decorate.TapeDecorator;
import org.maia.amstrad.tape.model.Block;
import org.maia.amstrad.tape.model.BlockHeader;
import org.maia.amstrad.tape.model.ByteSequence;
import org.maia.amstrad.tape.model.TapeProgram;

public class TapeReader {

	private List<TapeReaderListener> listeners;

	public TapeReader() {
		this.listeners = new Vector<TapeReaderListener>();
	}

	public void addListener(TapeReaderListener listener) {
		getListeners().add(listener);
	}

	public void removeListener(TapeReaderListener listener) {
		getListeners().remove(listener);
	}

	public void read(TapeInputStream tape, TapeDecorator tapeDecorator) throws Exception {
		tapeDecorator.atStartOfTape(tape);
		fireStartReadingTape();
		TapeProgram program = new TapeProgram();
		ByteSequence programBytecodeBuffer = new ByteSequence();
		BytecodeAudioDecorator byteCodeDecorator = new BytecodeAudioDecorator();
		final BlockReader br = new BlockReader();
		br.addListener(tapeDecorator);
		br.addListener(byteCodeDecorator);
		Block block;
		do {
			block = null;
			BlockHeader header = br.findAndReadNextBlockHeader(tape);
			if (header != null) {
				if (!program.accept(header)) {
					// Switch to next program
					tapeDecorator.enteredNewProgram(tape);
					fireEndReadingProgram(program, byteCodeDecorator);
					program = new TapeProgram();
					programBytecodeBuffer = new ByteSequence();
					br.removeListener(byteCodeDecorator);
					byteCodeDecorator = new BytecodeAudioDecorator();
					br.addListener(byteCodeDecorator);
				}
				block = br.findAndReadNextBlockData(tape, header, programBytecodeBuffer);
				if (block != null) {
					program.addBlock(block);
					if (program.getNumberOfBlocks() == 1) {
						fireStartReadingProgram(program);
					}
					fireFoundNewBlock(block);
				}
			}
		} while (block != null);
		if (program.getNumberOfBlocks() > 0) {
			// Last program on tape
			fireEndReadingProgram(program, byteCodeDecorator);
		}
		tapeDecorator.atEndOfTape(tape);
		fireEndReadingTape();
	}

	private void fireStartReadingTape() {
		for (TapeReaderListener listener : getListeners())
			listener.startReadingTape();
	}

	private void fireEndReadingTape() {
		for (TapeReaderListener listener : getListeners())
			listener.endReadingTape();
	}

	private void fireFoundNewBlock(Block block) {
		for (TapeReaderListener listener : getListeners())
			listener.foundNewBlock(block);
	}

	private void fireStartReadingProgram(TapeProgram program) {
		for (TapeReaderListener listener : getListeners())
			listener.startReadingProgram(program);
	}

	private void fireEndReadingProgram(TapeProgram program, BytecodeAudioDecorator byteCodeDecorator) {
		for (TapeReaderListener listener : getListeners())
			listener.endReadingProgram(program, byteCodeDecorator);
	}

	private List<TapeReaderListener> getListeners() {
		return listeners;
	}

}