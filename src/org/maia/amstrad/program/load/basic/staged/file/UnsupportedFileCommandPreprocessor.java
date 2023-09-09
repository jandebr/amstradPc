package org.maia.amstrad.program.load.basic.staged.file;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.program.load.basic.staged.StagedBasicProgramLoaderSession;

public class UnsupportedFileCommandPreprocessor extends FileCommandBasicPreprocessor {

	public UnsupportedFileCommandPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // no need for macros
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		return Collections.emptyList();
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		Collection<DiscoveredFileReference> references = new FileReferenceDiscoveryService(session.getAmstradPc())
				.discover(sourceCode);
		if (!references.isEmpty()) {
			DiscoveredFileReference reference = references.iterator().next();
			throw new BasicException("Unsupported file instruction " + reference.getInstruction().getSourceForm()
					+ " \"" + reference.getSourceFilename() + "\" (staged line " + reference.getLineNumber() + ")");
		}
	}

}