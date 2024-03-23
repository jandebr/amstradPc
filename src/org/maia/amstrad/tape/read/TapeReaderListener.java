package org.maia.amstrad.tape.read;

import org.maia.amstrad.tape.decorate.BytecodeAudioDecorator;
import org.maia.amstrad.tape.model.Block;
import org.maia.amstrad.tape.model.TapeProgram;

public interface TapeReaderListener {

	void startReadingTape();

	void endReadingTape();

	void foundNewBlock(Block block);

	void startReadingProgram(TapeProgram program);

	void endReadingProgram(TapeProgram program, BytecodeAudioDecorator byteCodeDecorator);

}