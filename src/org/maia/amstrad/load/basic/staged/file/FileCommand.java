package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;

public abstract class FileCommand {

	private String sourceFilename;

	private boolean suppressMessages;

	public static final String FILENAME_FLAG_SUPPRESS_MESSAGES = "!";

	private static String parseFilenameFrom(LiteralQuotedToken token) {
		String str = token.getSourceFragment();
		int i = str.lastIndexOf(LiteralQuotedToken.QUOTE);
		if (i > 0) {
			return str.substring(1, i); // between quotes
		} else {
			return str.substring(1); // no ending quote, as in RUN" or RUN"!
		}
	}

	protected FileCommand() {
	}

	protected FileCommand(LiteralQuotedToken sourceFilenameToken) {
		this(parseFilenameFrom(sourceFilenameToken));
	}

	protected FileCommand(String sourceFilename) {
		this.sourceFilename = sourceFilename;
		this.suppressMessages = sourceFilename.startsWith(FILENAME_FLAG_SUPPRESS_MESSAGES);
	}

	public String getSourceFilenameWithoutFlags() {
		String filename = getSourceFilename();
		if (isSuppressMessages()) {
			filename = filename.substring(FILENAME_FLAG_SUPPRESS_MESSAGES.length());
		}
		return filename;
	}

	public String getSourceFilename() {
		return sourceFilename;
	}

	public boolean isSuppressMessages() {
		return suppressMessages;
	}

}