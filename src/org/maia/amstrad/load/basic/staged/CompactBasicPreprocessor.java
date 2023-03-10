package org.maia.amstrad.load.basic.staged;

import java.util.Map;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicMinifier;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.minify.LocomotiveBasicMinifierFactory;
import org.maia.amstrad.basic.locomotive.token.VariableToken;

public class CompactBasicPreprocessor extends StagedBasicPreprocessor {

	private static final int DEFAULT_MINIFICATION_LEVEL = LocomotiveBasicMinifierFactory.LEVEL_NON_INVASIVE;

	public CompactBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0;
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return false;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		boolean printStats = shouldPrintStatistics();
		int bytesBefore = printStats ? session.getBasicRuntime().getCompiler().compile(sourceCode).getByteCount() : 0;
		int level = getMinificationLevel();
		Map<VariableToken, VariableToken> variableRenameMap = session.getOriginalToStagedVariableMapping();
		BasicMinifier minifier = LocomotiveBasicMinifierFactory.getInstance().createMinifier(level, variableRenameMap);
		minifier.minify(sourceCode);
		if (printStats) {
			int bytesAfter = session.getBasicRuntime().getCompiler().compile(sourceCode).getByteCount();
			int bytesReduction = bytesBefore - bytesAfter;
			System.out.println("Compacted source code on level " + level + " with " + bytesReduction + " bytes (from "
					+ bytesBefore + " to " + bytesAfter + " bytes)");
		}
	}

	private int getMinificationLevel() {
		int level = DEFAULT_MINIFICATION_LEVEL;
		AmstradSettings settings = AmstradFactory.getInstance().getAmstradContext().getUserSettings();
		String value = settings.get("basic_staging.minify.level", String.valueOf(level));
		try {
			level = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			System.err.println(e);
		}
		return level;
	}

	private boolean shouldPrintStatistics() {
		return AmstradFactory.getInstance().getAmstradContext().getUserSettings()
				.getBool("basic_staging.minify.printStats", false);
	}

}