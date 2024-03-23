package org.maia.amstrad.tape.gui.editor;

import java.io.IOException;

import javax.swing.Icon;

import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.tape.gui.UIResourcesTape;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.swing.text.pte.PlainTextDocumentException;
import org.maia.swing.text.pte.model.PlainTextAbstractDocument;

public class ProgramSourceCodeDocument extends PlainTextAbstractDocument implements ProgramDocument {

	private AudioTapeProgram program;

	public ProgramSourceCodeDocument(AudioTapeProgram program) {
		this.program = program;
	}

	@Override
	public int hashCode() {
		return getProgram().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProgramSourceCodeDocument other = (ProgramSourceCodeDocument) obj;
		return getProgram().equals(other.getProgram());
	}

	@Override
	public String readText() {
		return getProgram().getLatestSourceCode().getText();
	}

	@Override
	protected void doWriteText(String text) throws PlainTextDocumentException {
		try {
			BasicSourceCode sourceCode = new LocomotiveBasicSourceCode(text);
			getProgram().saveModifiedSourceCode(sourceCode);
		} catch (BasicSyntaxException e) {
			throw new PlainTextDocumentException(this, "Syntax error in source code", e);
		} catch (IOException e) {
			throw new PlainTextDocumentException(this, "Failed to write source code to file", e);
		}
	}

	@Override
	public String getLongDocumentName() {
		return "Source code of " + getProgram().getProgramName();
	}

	@Override
	public String getShortDocumentName() {
		return getProgram().getProgramName();
	}

	@Override
	public Icon getLargeDocumentIcon() {
		return UIResourcesTape.sourceCodeDocumentLargeIcon;
	}

	@Override
	public Icon getSmallDocumentIcon() {
		return UIResourcesTape.sourceCodeDocumentSmallIcon;
	}

	@Override
	public AudioTapeProgram getProgram() {
		return program;
	}

}