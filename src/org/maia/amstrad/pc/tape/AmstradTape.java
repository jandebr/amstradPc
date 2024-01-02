package org.maia.amstrad.pc.tape;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.pc.AmstradPcDevice;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.io.util.IOUtils;
import org.maia.util.GenericListenerList;

public abstract class AmstradTape extends AmstradPcDevice {

	private GenericListenerList<AmstradTapeListener> tapeListeners;

	private boolean reading;

	private boolean writing;

	private boolean suppressMessages;

	private String filenameAtTapeHead;

	protected AmstradTape(AmstradPc amstradPc) {
		super(amstradPc);
		this.tapeListeners = new GenericListenerList<AmstradTapeListener>();
	}

	public void loadSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException {
		getBasicRuntime().load(readSourceCodeFromFile(sourceCodeFile));
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(sourceCodeFile.getParentFile());
		System.out.println("Loaded source code from " + sourceCodeFile.getPath());
	}

	public void loadByteCodeFromFile(File byteCodeFile) throws IOException, BasicException {
		getBasicRuntime().load(readByteCodeFromFile(byteCodeFile));
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(byteCodeFile.getParentFile());
		System.out.println("Loaded byte code from " + byteCodeFile.getPath());
	}

	public abstract BasicSourceCode readSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException;

	public abstract BasicByteCode readByteCodeFromFile(File byteCodeFile) throws IOException, BasicException;

	public void saveSourceCodeToFile(File file) throws IOException, BasicException {
		PrintWriter pw = new PrintWriter(file);
		pw.print(getBasicRuntime().exportSourceCode().getText());
		pw.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported source code to " + file.getPath());
	}

	public void saveByteCodeToFile(File file) throws IOException, BasicException {
		FileOutputStream os = new FileOutputStream(file);
		os.write(getBasicRuntime().exportByteCode().getBytes());
		os.flush();
		os.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported byte code to " + file.getPath());
	}

	public void loadBinaryFile(File binaryFile, int memoryStartAddress) throws IOException {
		getBasicRuntime().loadBinaryData(IOUtils.readBinaryFileContents(binaryFile), memoryStartAddress);
	}

	public void saveBinaryFile(File binaryFile, int memoryStartAddress, int memoryLength) throws IOException {
		IOUtils.writeBinaryFileContents(binaryFile,
				getBasicRuntime().exportBinaryData(memoryStartAddress, memoryLength));
	}

	public void addTapeListener(AmstradTapeListener listener) {
		getTapeListeners().addListener(listener);
	}

	public void removeTapeListener(AmstradTapeListener listener) {
		getTapeListeners().removeListener(listener);
	}

	public void notifyTapeReading(String filename, boolean suppressMessages) {
		setReading(true);
		setWriting(false);
		setSuppressMessages(suppressMessages);
		setFilenameAtTapeHead(filename);
		fireTapeReadingEvent();
	}

	public void notifyTapeStoppedReading() {
		setReading(false);
		setSuppressMessages(false);
		setFilenameAtTapeHead(null);
		fireTapeStoppedReadingEvent();
	}

	public void notifyTapeWriting(String filename, boolean suppressMessages) {
		setWriting(true);
		setReading(false);
		setSuppressMessages(suppressMessages);
		setFilenameAtTapeHead(filename);
		fireTapeWritingEvent();
	}

	public void notifyTapeStoppedWriting() {
		setWriting(false);
		setSuppressMessages(false);
		setFilenameAtTapeHead(null);
		fireTapeStoppedWritingEvent();
	}

	protected void fireTapeReadingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeReading(this);
	}

	protected void fireTapeStoppedReadingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeStoppedReading(this);
	}

	protected void fireTapeWritingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeWriting(this);
	}

	protected void fireTapeStoppedWritingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeStoppedWriting(this);
	}

	protected GenericListenerList<AmstradTapeListener> getTapeListeners() {
		return tapeListeners;
	}

	protected BasicRuntime getBasicRuntime() {
		return getAmstradPc().getBasicRuntime();
	}

	public boolean isActive() {
		return isReading() || isWriting();
	}

	public boolean isReading() {
		return reading;
	}

	private void setReading(boolean reading) {
		this.reading = reading;
	}

	public boolean isWriting() {
		return writing;
	}

	private void setWriting(boolean writing) {
		this.writing = writing;
	}

	public boolean isSuppressMessages() {
		return suppressMessages;
	}

	private void setSuppressMessages(boolean suppressMessages) {
		this.suppressMessages = suppressMessages;
	}

	public String getFilenameAtTapeHead() {
		return filenameAtTapeHead;
	}

	private void setFilenameAtTapeHead(String filename) {
		this.filenameAtTapeHead = filename;
	}

}