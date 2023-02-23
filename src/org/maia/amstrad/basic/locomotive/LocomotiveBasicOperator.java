package org.maia.amstrad.basic.locomotive;

public class LocomotiveBasicOperator {

	private byte codeByte;

	private String sourceForm;

	public LocomotiveBasicOperator(byte codeByte, String sourceForm) {
		this.codeByte = codeByte;
		this.sourceForm = sourceForm;
	}

	@Override
	public String toString() {
		return getSourceForm();
	}

	@Override
	public int hashCode() {
		return sourceForm.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocomotiveBasicOperator other = (LocomotiveBasicOperator) obj;
		return getSourceForm().equals(other.getSourceForm());
	}

	public byte getCodeByte() {
		return codeByte;
	}

	public String getSourceForm() {
		return sourceForm;
	}

}