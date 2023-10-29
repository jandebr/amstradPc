package org.maia.amstrad.gui.browser.components;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.components.ColoredTextLine;
import org.maia.amstrad.gui.components.ColoredTextSpan;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgram.FileReference;
import org.maia.amstrad.program.load.basic.staged.file.DiscoveredFileReference;
import org.maia.amstrad.program.load.basic.staged.file.FileReferenceDiscoveryService;
import org.maia.util.StringUtils;
import org.maia.amstrad.program.AmstradProgramException;

public class ProgramFileReferencesSheet extends ProgramSheet {

	private AmstradPc amstradPc;

	public ProgramFileReferencesSheet(AmstradProgram program, AmstradPc amstradPc, int maxItemsShowing, int maxWidth,
			int backgroundColorIndex) {
		super(program, maxItemsShowing, maxWidth, backgroundColorIndex);
		this.amstradPc = amstradPc;
	}

	@Override
	protected void populateSheet(int maxWidth, int bg) {
		try {
			List<DiscoveredFileReference> refs = getSortedFileReferences(getProgram());
			String previousFilename = null;
			for (int i = 0; i < refs.size(); i++) {
				DiscoveredFileReference ref = refs.get(i);
				String filename = ref.getSourceFilenameWithoutFlags();
				FileReference reference = getProgram().lookupFileReference(filename);
				boolean linked = reference != null;
				int c1 = linked ? 22 : 7;
				int c2 = linked ? 9 : 13;
				int c3 = linked ? 21 : 26;
				if (!filename.equals(previousFilename)) {
					// new filename
					if (i > 0)
						add(new ColoredTextLine()); // spacer
					String symbol = String.valueOf(linked ? (char) 186 : (char) 187);
					add(new ColoredTextLine(new ColoredTextSpan(StringUtils.fitWidth(symbol, 2), bg, c1),
							new ColoredTextSpan("\"" + filename + "\"", bg, c1)));
				}
				add(new ColoredTextLine(new ColoredTextSpan("  " + ref.getInstruction().getSourceForm(), bg, c1),
						new ColoredTextSpan(" on line ", bg, c2),
						new ColoredTextSpan(String.valueOf(ref.getLineNumber()), bg, c3)));
				previousFilename = filename;
			}
		} catch (AmstradProgramException e) {
			add(new ColoredTextLine(new ColoredTextSpan(((char) 225) + " error", bg, 13)));
		}
	}

	private List<DiscoveredFileReference> getSortedFileReferences(AmstradProgram program)
			throws AmstradProgramException {
		List<DiscoveredFileReference> refs = new Vector<DiscoveredFileReference>(getFileReferences(program));
		Collections.sort(refs, new Comparator<DiscoveredFileReference>() {

			@Override
			public int compare(DiscoveredFileReference ref1, DiscoveredFileReference ref2) {
				return ref1.getSourceFilenameWithoutFlags().compareTo(ref2.getSourceFilenameWithoutFlags());
			}

		});
		return refs;
	}

	private Collection<DiscoveredFileReference> getFileReferences(AmstradProgram program)
			throws AmstradProgramException {
		return new FileReferenceDiscoveryService(getAmstradPc()).discover(program);
	}

	private AmstradPc getAmstradPc() {
		return amstradPc;
	}

}