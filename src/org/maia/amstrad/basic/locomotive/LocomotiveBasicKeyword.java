package org.maia.amstrad.basic.locomotive;

public class LocomotiveBasicKeyword {

	private byte prefixByte;

	private byte codeByte;

	private String sourceForm;

	public LocomotiveBasicKeyword(byte codeByte, String sourceForm) {
		this((byte) 0, codeByte, sourceForm);
	}

	public LocomotiveBasicKeyword(byte prefixByte, byte codeByte, String sourceForm) {
		this.prefixByte = prefixByte;
		this.codeByte = codeByte;
		this.sourceForm = sourceForm;
	}

	@Override
	public String toString() {
		return getSourceForm();
	}

	@Override
	public int hashCode() {
		return 31 + ((sourceForm == null) ? 0 : sourceForm.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocomotiveBasicKeyword other = (LocomotiveBasicKeyword) obj;
		return getSourceForm().equals(other.getSourceForm());
	}

	public boolean canBeFollowedByLineNumber() {
		String sf = getSourceForm();
		return sf.equals("GOTO") || sf.equals("GOSUB") || sf.equals("ON ERROR GOTO") || sf.equals("THEN")
				|| sf.equals("ELSE") || sf.equals("DELETE") || sf.equals("EDIT") || sf.equals("LIST")
				|| sf.equals("RESUME") || sf.equals("RENUM") || sf.equals("RESTORE") || sf.equals("RUN");
	}

	public boolean isRemark() {
		String sf = getSourceForm();
		return sf.equals("REM") || sf.equals("'");
	}

	public boolean isData() {
		String sf = getSourceForm();
		return sf.equals("DATA");
	}

	public boolean isPrecededByInstructionSeparator() {
		String sf = getSourceForm();
		return sf.equals("ELSE") || sf.equals("'");
	}

	public boolean isBasicKeyword() {
		return getPrefixByte() == 0;
	}

	public boolean isExtendedKeyword() {
		return !isBasicKeyword();
	}

	public byte getPrefixByte() {
		return prefixByte;
	}

	public byte getCodeByte() {
		return codeByte;
	}

	public String getSourceForm() {
		return sourceForm;
	}

}