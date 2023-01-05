package org.maia.amstrad.basic.locomotive;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.util.StringUtils;

public class LocomotiveBasicSourceCode extends BasicSourceCode {

	public LocomotiveBasicSourceCode() {
		super(BasicLanguage.LOCOMOTIVE_BASIC);
	}

	public LocomotiveBasicSourceCode(CharSequence sourceCode) throws BasicSyntaxException {
		super(BasicLanguage.LOCOMOTIVE_BASIC, sourceCode);
	}

	@Override
	protected List<BasicSourceCodeLine> parse(CharSequence sourceCode) throws BasicSyntaxException {
		List<BasicSourceCodeLine> lines = new Vector<BasicSourceCodeLine>(100);
		boolean strictlyIncreasingLineNumbers = true;
		StringTokenizer st = new StringTokenizer(sourceCode.toString(), "\n\r");
		while (st.hasMoreTokens()) {
			String text = st.nextToken();
			if (!StringUtils.isBlank(text)) {
				BasicSourceCodeLine line = new LocomotiveBasicSourceCodeLine(text);
				lines.add(line);
				if (strictlyIncreasingLineNumbers && lines.size() > 1) {
					strictlyIncreasingLineNumbers = line.compareTo(lines.get(lines.size() - 2)) > 0;
				}
			}
		}
		if (!strictlyIncreasingLineNumbers) {
			Map<Integer, BasicSourceCodeLine> indexedLines = new HashMap<Integer, BasicSourceCodeLine>(lines.size());
			for (BasicSourceCodeLine line : lines) {
				indexedLines.put(line.getLineNumber(), line);
			}
			lines.clear();
			lines.addAll(indexedLines.values());
			Collections.sort(lines); // by increasing line number
		}
		return lines;
	}

	@Override
	public LocomotiveBasicSourceCode clone() {
		return (LocomotiveBasicSourceCode) super.clone();
	}

}