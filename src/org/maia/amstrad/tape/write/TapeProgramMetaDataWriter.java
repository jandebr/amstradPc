package org.maia.amstrad.tape.write;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.program.AmstradProgramMetaDataConstants;
import org.maia.amstrad.program.load.basic.staged.file.DiscoveredFileReference;
import org.maia.amstrad.program.load.basic.staged.file.FileReferenceDiscoveryService;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.amstrad.tape.model.TapeProgramMetaData;
import org.maia.util.StringUtils;

public class TapeProgramMetaDataWriter implements AmstradProgramMetaDataConstants {

	public TapeProgramMetaDataWriter() {
	}

	public void writeMetaData(TapeProgramMetaData metaData, AudioTapeProgram program, File metaDataFile)
			throws IOException {
		PrintWriter pw = new PrintWriter(metaDataFile, "UTF-8");
		pw.println(AMD_TYPE + ": " + AMD_TYPE_LOCOMOTIVE_BASIC_PROGRAM);
		pw.println(AMD_NAME + ": " + program.getProgramName());
		pw.println(AMD_AUTHOR + ": " + StringUtils.emptyForNull(metaData.getAuthor()));
		pw.println(AMD_YEAR + ": " + StringUtils.emptyForNull(metaData.getYear()));
		pw.println(AMD_TAPE + ": " + StringUtils.emptyForNull(metaData.getTape()));
		pw.println(AMD_BLOCKS + ": " + program.getNumberOfBlocks());
		pw.println(AMD_MONITOR + ": " + StringUtils.emptyForNull(metaData.getMonitor()));
		pw.println(AMD_DESCRIPTION + ": " + StringUtils.emptyForNull(metaData.getDescription()));
		pw.println(AMD_AUTHORING + ": " + StringUtils.emptyForNull(metaData.getAuthoring()));
		writeFileReferences(program, pw);
		pw.close();
	}

	private void writeFileReferences(AudioTapeProgram program, PrintWriter pw) {
		List<DiscoveredFileReference> references = getFileReferencesSortedBySourceFilename(program);
		String previousFilename = null;
		boolean previousFilenameIsProgram = false;
		for (DiscoveredFileReference reference : references) {
			String filename = reference.getSourceFilenameWithoutFlags();
			if (previousFilename != null && !previousFilename.equals(filename)) {
				writeFileReference(previousFilename, previousFilenameIsProgram, pw);
				previousFilenameIsProgram = false;
			}
			pw.print("#--> " + reference.getInstruction().getSourceForm());
			pw.print(" \"" + reference.getSourceFilename() + "\"");
			pw.print(" on line " + reference.getLineNumber() + " in original code");
			pw.println();
			previousFilename = filename;
			previousFilenameIsProgram = previousFilenameIsProgram || reference.getInstruction().isProgramReference();
		}
		if (previousFilename != null) {
			writeFileReference(previousFilename, previousFilenameIsProgram, pw);
		}
	}

	private void writeFileReference(String filename, boolean fileIsProgram, PrintWriter pw) {
		pw.print("#" + AMD_FILEREFS_PREFIX + "[" + filename.replace(" ", "\\ ") + "]: ");
		if (fileIsProgram) {
			String path = "../00_" + filename + "/";
			pw.print(path + "$.bas " + AMD_FILEREFS_DESCRIBED_BY + " " + path + "$.amd");
		} else {
			pw.print("$target");
		}
		pw.println();
	}

	private List<DiscoveredFileReference> getFileReferencesSortedBySourceFilename(AudioTapeProgram program) {
		try {
			return FileReferenceDiscoveryService
					.sortBySourceFilename(FileReferenceDiscoveryService.discover(program.getSourceCodeOnTape()));
		} catch (BasicException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}