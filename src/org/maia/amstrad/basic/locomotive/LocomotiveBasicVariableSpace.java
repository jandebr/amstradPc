package org.maia.amstrad.basic.locomotive;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicNumericRepresentation.NumberOverflowException;
import org.maia.amstrad.basic.locomotive.token.FloatingPointTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.IntegerTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.TypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.UntypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.VariableToken;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.util.StringUtils;

public class LocomotiveBasicVariableSpace implements LocomotiveBasicMemoryMap {

	private AmstradMemory memory;

	private static final byte PAYLOAD_TYPE_INTEGER = 0x1;

	private static final byte PAYLOAD_TYPE_STRING = 0x2;

	private static final byte PAYLOAD_TYPE_FLOATINGPOINT = 0x4;

	private static final Charset STRING_CHARSET = Charset.forName("ISO-8859-1");

	public LocomotiveBasicVariableSpace(AmstradMemory memory) {
		this.memory = memory;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(2048);
		sb.append("Basic Variable Space [\n");
		getMemory().startThreadExclusiveSession();
		try {
			Collection<TypedVariableToken> variables = getAllVariables();
			for (VariableToken variable : variables) {
				sb.append("  ").append(variable);
				try {
					if (variable instanceof IntegerTypedVariableToken) {
						sb.append(" = ").append(getValue((IntegerTypedVariableToken) variable));
					} else if (variable instanceof FloatingPointTypedVariableToken) {
						sb.append(" = ").append(getValue((FloatingPointTypedVariableToken) variable));
					} else if (variable instanceof StringTypedVariableToken) {
						sb.append(" = \"").append(getValue((StringTypedVariableToken) variable)).append('"');
					}
				} catch (VariableNotFoundException e) {
					// cannot happen
				}
				sb.append('\n');
			}
		} finally {
			getMemory().endThreadExclusiveSession();
		}
		sb.append(']');
		return sb.toString();
	}

	public String toStringMemoryRange() {
		return toStringMemoryRange(getVariableSpaceStartAddress(), getVariableSpaceEndAddress() - 1);
	}

	public String toStringMemoryRange(int addressFrom, int addressTo) {
		StringBuilder sb = new StringBuilder(2048);
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			for (int addr = addressFrom; addr <= addressTo; addr++) {
				byte b = memory.readByte(addr);
				int i = b & 0xff;
				int j = b & 0x7f;
				sb.append("&" + StringUtils.leftPad(Integer.toHexString(addr), 4, '0'));
				sb.append(" (").append(StringUtils.leftPad(String.valueOf(addr), 5, ' ')).append(") ");
				sb.append(StringUtils.leftPad(String.valueOf(i), 3, ' '));
				if (i >= 32 && i <= 126) {
					sb.append(" [").append((char) i).append("]");
				} else if (j >= 32 && j <= 126) {
					sb.append("     [").append((char) j).append("]");
				}
				sb.append('\n');
			}
		} finally {
			memory.endThreadExclusiveSession();
		}
		return sb.toString();
	}

	public Set<TypedVariableToken> getAllVariables() {
		Set<TypedVariableToken> variables = new HashSet<TypedVariableToken>();
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			int hs = getVariableSpaceStartAddress();
			int he = getVariableSpaceEndAddress();
			int h = hs;
			while (h < he) {
				h += 2;
				String name = readVariableNameStartingAt(h);
				h += name.length();
				byte payloadTypeIndicator = memory.readByte(h++);
				h += getPayloadValueByteSize(payloadTypeIndicator);
				if (payloadTypeIndicator == PAYLOAD_TYPE_INTEGER) {
					variables.add(IntegerTypedVariableToken.forName(name));
				} else if (payloadTypeIndicator == PAYLOAD_TYPE_STRING) {
					variables.add(StringTypedVariableToken.forName(name));
				} else if (payloadTypeIndicator == PAYLOAD_TYPE_FLOATINGPOINT) {
					variables.add(FloatingPointTypedVariableToken.forName(name));
				}
			}
		} finally {
			memory.endThreadExclusiveSession();
		}
		return variables;
	}

	private String readVariableNameStartingAt(int memoryOffset) {
		StringBuilder sb = new StringBuilder(16);
		AmstradMemory memory = getMemory();
		int h = memoryOffset;
		byte b = memory.readByte(h);
		while ((b & 0xff) < 128) {
			sb.append((char) b);
			b = memory.readByte(++h);
		}
		sb.append((char) (b & 0x7f));
		return sb.toString();
	}

	public int getValue(IntegerTypedVariableToken variable) throws VariableNotFoundException {
		int value = 0;
		getMemory().startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			value = LocomotiveBasicNumericRepresentation.wordToInteger(getMemory().readWord(memoryOffset));
		} finally {
			getMemory().endThreadExclusiveSession();
		}
		return value;
	}

	public void setValue(IntegerTypedVariableToken variable, int value)
			throws VariableNotFoundException, NumberOverflowException {
		getMemory().startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			getMemory().writeWord(memoryOffset, LocomotiveBasicNumericRepresentation.integerToWord(value));
		} finally {
			getMemory().endThreadExclusiveSession();
		}
	}

	public double getValue(FloatingPointTypedVariableToken variable) throws VariableNotFoundException {
		double value = 0;
		getMemory().startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			value = LocomotiveBasicNumericRepresentation.bytesToFloatingPoint(getMemory().readBytes(memoryOffset, 5));
		} finally {
			getMemory().endThreadExclusiveSession();
		}
		return value;
	}

	public void setValue(FloatingPointTypedVariableToken variable, double value) throws VariableNotFoundException {
		getMemory().startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			getMemory().writeBytes(memoryOffset, LocomotiveBasicNumericRepresentation.floatingPointToBytes(value));
		} finally {
			getMemory().endThreadExclusiveSession();
		}
	}

	public double getValue(UntypedVariableToken variable) throws VariableNotFoundException {
		double value = 0;
		getMemory().startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			value = LocomotiveBasicNumericRepresentation.bytesToFloatingPoint(getMemory().readBytes(memoryOffset, 5));
		} finally {
			getMemory().endThreadExclusiveSession();
		}
		return value;
	}

	public void setValue(UntypedVariableToken variable, double value) throws VariableNotFoundException {
		getMemory().startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			getMemory().writeBytes(memoryOffset, LocomotiveBasicNumericRepresentation.floatingPointToBytes(value));
		} finally {
			getMemory().endThreadExclusiveSession();
		}
	}

	public String getValue(StringTypedVariableToken variable) throws VariableNotFoundException {
		String value = null;
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			int strLength = memory.readByte(memoryOffset) & 0xff;
			int strAddr = memory.readWord(memoryOffset + 1);
			byte[] strData = memory.readBytes(strAddr, strLength);
			value = new String(strData, STRING_CHARSET);
		} finally {
			memory.endThreadExclusiveSession();
		}
		return value;
	}

	public void setCharAt(StringTypedVariableToken variable, int charIndex, char c)
			throws VariableNotFoundException, SubscriptOutOfRangeException {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			int memoryOffset = findPayloadValueMemoryOffset(variable);
			int strLength = memory.readByte(memoryOffset) & 0xff;
			if (charIndex < 0 || charIndex >= strLength)
				throw new SubscriptOutOfRangeException(charIndex);
			int strAddr = memory.readWord(memoryOffset + 1);
			memory.writeByte(strAddr + charIndex, (byte) (c & 0xff));
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	public IntegerTypedVariableToken generateNewIntegerVariable() {
		return generateNewIntegerVariable(getAllVariables());
	}

	public static IntegerTypedVariableToken generateNewIntegerVariable(
			Collection<? extends VariableToken> existingVariables) {
		return generateNewTypedVariable(IntegerTypedVariableToken.class, existingVariables);
	}

	public FloatingPointTypedVariableToken generateNewFloatingPointVariable() {
		return generateNewFloatingPointVariable(getAllVariables());
	}

	public static FloatingPointTypedVariableToken generateNewFloatingPointVariable(
			Collection<? extends VariableToken> existingVariables) {
		return generateNewTypedVariable(FloatingPointTypedVariableToken.class, existingVariables);
	}

	public StringTypedVariableToken generateNewStringVariable() {
		return generateNewStringVariable(getAllVariables());
	}

	public static StringTypedVariableToken generateNewStringVariable(
			Collection<? extends VariableToken> existingVariables) {
		return generateNewTypedVariable(StringTypedVariableToken.class, existingVariables);
	}

	private int findPayloadValueMemoryOffset(VariableToken variable) throws VariableNotFoundException {
		int mo = findMemoryOffset(variable);
		return mo + 3 + getVariableNameLengthStartingAt(mo + 2);
	}

	private int findMemoryOffset(VariableToken variable) throws VariableNotFoundException {
		AmstradMemory memory = getMemory();
		int hs = getVariableSpaceStartAddress();
		int he = getVariableSpaceEndAddress();
		int h = hs;
		while (h < he) {
			if (isAtMemoryOffset(variable, h))
				return h;
			h += 2;
			h += getVariableNameLengthStartingAt(h);
			byte payloadTypeIndicator = memory.readByte(h++);
			h += getPayloadValueByteSize(payloadTypeIndicator);
		}
		throw new VariableNotFoundException(variable);
	}

	private boolean isAtMemoryOffset(VariableToken variable, int memoryOffset) {
		AmstradMemory memory = getMemory();
		String name = variable.getVariableNameWithoutTypeIndicator().toUpperCase(); // names are stored in uppercase
		int nl = name.length();
		int h = memoryOffset + 2;
		// Type check
		if (memory.readByte(h + nl) != getPayloadTypeIndicator(variable))
			return false;
		// Name check
		for (int i = 0; i < nl - 1; i++) {
			if (memory.readByte(h++) != (byte) name.charAt(i))
				return false;
		}
		if (memory.readByte(h++) != (byte) (name.charAt(nl - 1) + 128))
			return false;
		return true;
	}

	private int getVariableNameLengthStartingAt(int memoryOffset) {
		AmstradMemory memory = getMemory();
		int h = memoryOffset;
		while ((memory.readByte(h) & 0xff) < 128) {
			h++;
		}
		return h - memoryOffset + 1;
	}

	private byte getPayloadTypeIndicator(VariableToken variable) {
		if (variable instanceof UntypedVariableToken) {
			return PAYLOAD_TYPE_FLOATINGPOINT;
		} else if (variable instanceof FloatingPointTypedVariableToken) {
			return PAYLOAD_TYPE_FLOATINGPOINT;
		} else if (variable instanceof IntegerTypedVariableToken) {
			return PAYLOAD_TYPE_INTEGER;
		} else if (variable instanceof StringTypedVariableToken) {
			return PAYLOAD_TYPE_STRING;
		} else {
			return 0;
		}
	}

	private int getPayloadValueByteSize(byte payloadTypeIndicator) {
		return payloadTypeIndicator + 1; // this simple rule holds
	}

	private int getVariableSpaceStartAddress() {
		return getMemory().readWord(ADDRESS_BYTECODE_END_POINTER); // inclusive
	}

	private int getVariableSpaceEndAddress() {
		return getMemory().readWord(ADDRESS_VARIABLE_SPACE_END_POINTER); // exclusive
	}

	private AmstradMemory getMemory() {
		return memory;
	}

	private static <T extends TypedVariableToken> T generateNewTypedVariable(Class<T> variableType,
			Collection<? extends VariableToken> existingVariables) {
		T variable = null;
		int n = 0;
		int nt = 10;
		int length = 1;
		int lengthMax = 4;
		do {
			if (++n > nt && length < lengthMax) {
				length++;
				nt *= 10;
			}
			variable = createTypedVariable(variableType, generateRandomVariableName(length));
		} while (existingVariables.contains(variable));
		return variable;
	}

	private static <T extends TypedVariableToken> T createTypedVariable(Class<T> variableType,
			String variableNameWithoutTypeIndicator) {
		T variable = null;
		try {
			Method method = variableType.getDeclaredMethod("forName", String.class);
			variable = variableType.cast(method.invoke(null, variableNameWithoutTypeIndicator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return variable;
	}

	private static String generateRandomVariableName(int nameLength) {
		String name = null;
		StringBuilder sb = nameLength > 1 ? new StringBuilder(nameLength) : null;
		do {
			if (nameLength > 1) {
				sb.setLength(0);
				for (int i = 0; i < nameLength; i++) {
					sb.append((char) ('A' + (int) Math.floor(Math.random() * 26.0)));
				}
				name = sb.toString();
			} else {
				name = String.valueOf((char) ('A' + (int) Math.floor(Math.random() * 26.0)));
			}
		} while (LocomotiveBasicKeywords.getInstance().hasKeyword(name));
		return name;
	}

	public static class VariableNotFoundException extends BasicException {

		public VariableNotFoundException(VariableToken variable) {
			super("Variable not found: " + variable.getSourceFragment());
		}

	}

	public static class SubscriptOutOfRangeException extends BasicException {

		public SubscriptOutOfRangeException(int index) {
			super("Subscript out of range: " + index);
		}

	}

}