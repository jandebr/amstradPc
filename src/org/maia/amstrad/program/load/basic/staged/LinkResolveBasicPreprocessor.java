package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.program.load.basic.staged.ProgramBridgeBasicPreprocessor.DynamicLinkMacro;

public class LinkResolveBasicPreprocessor extends StagedBasicPreprocessor {

	public LinkResolveBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0;
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
		StagedLineNumberMapping stagedMapping = session.getOriginalToStagedLineNumberMapping();
		for (DynamicLinkMacro macro : session.getMacrosAdded(DynamicLinkMacro.class)) {
			int ln = macro.getOriginalLineNumber();
			if (stagedMapping.isMapped(ln)) {
				int lnGoto = stagedMapping.getNewLineNumber(ln);
				substituteLineNumberReference(macro.getLineNumberFrom(), lnGoto, sourceCode);
				session.removeMacro(macro);
			}
		}
	}

}