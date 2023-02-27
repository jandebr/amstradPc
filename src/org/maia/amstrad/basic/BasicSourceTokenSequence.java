package org.maia.amstrad.basic;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BasicSourceTokenSequence implements Cloneable, Iterable<BasicSourceToken> {

	private List<BasicSourceToken> tokens;

	private boolean modified; // is modified structurally after creation

	public BasicSourceTokenSequence() {
		setTokens(new Vector<BasicSourceToken>());
	}

	public BasicSourceTokenSequence(List<BasicSourceToken> tokens) {
		setTokens(new Vector<BasicSourceToken>(tokens));
	}

	@Override
	public BasicSourceTokenSequence clone() {
		return new BasicSourceTokenSequence(getTokens());
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		return getTokens().size();
	}

	public BasicSourceToken get(int index) {
		return getTokens().get(index);
	}

	public BasicSourceToken getFirstToken() {
		BasicSourceToken token = null;
		if (!isEmpty())
			token = get(0);
		return token;
	}

	public BasicSourceToken getLastToken() {
		BasicSourceToken token = null;
		if (!isEmpty())
			token = get(size() - 1);
		return token;
	}

	public int getFirstIndexOf(BasicSourceToken token) {
		return getTokens().indexOf(token);
	}

	public int getLastIndexOf(BasicSourceToken token) {
		return getTokens().lastIndexOf(token);
	}

	public int getNextIndexOf(BasicSourceToken token, int startIndex) {
		if (startIndex < 0 || startIndex >= size())
			return -1;
		int index = subSequence(startIndex, size()).getFirstIndexOf(token);
		if (index >= 0)
			index += startIndex;
		return index;
	}

	public int getPreviousIndexOf(BasicSourceToken token, int startIndex) {
		if (startIndex < 0 || startIndex >= size())
			return -1;
		return subSequence(0, startIndex + 1).getLastIndexOf(token);
	}

	public int getFirstIndexOf(Class<? extends BasicSourceToken> tokenType) {
		for (int i = 0; i < size(); i++) {
			if (tokenType.isAssignableFrom(get(i).getClass()))
				return i;
		}
		return -1;
	}

	public int getLastIndexOf(Class<? extends BasicSourceToken> tokenType) {
		for (int i = size() - 1; i >= 0; i--) {
			if (tokenType.isAssignableFrom(get(i).getClass()))
				return i;
		}
		return -1;
	}

	public int getNextIndexOf(Class<? extends BasicSourceToken> tokenType, int startIndex) {
		if (startIndex < 0 || startIndex >= size())
			return -1;
		int index = subSequence(startIndex, size()).getFirstIndexOf(tokenType);
		if (index >= 0)
			index += startIndex;
		return index;
	}

	public int getPreviousIndexOf(Class<? extends BasicSourceToken> tokenType, int startIndex) {
		if (startIndex < 0 || startIndex >= size())
			return -1;
		return subSequence(0, startIndex + 1).getLastIndexOf(tokenType);
	}

	public int getIndexFollowing(BasicSourceToken token, int startIndex) {
		int i = startIndex;
		while (i < size() && get(i).equals(token))
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public int getIndexFollowing(Class<? extends BasicSourceToken> tokenType, int startIndex) {
		int i = startIndex;
		while (i < size() && tokenType.isAssignableFrom(get(i).getClass()))
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public int getIndexFollowingWhitespace(int startIndex) {
		int i = startIndex;
		while (i < size() && get(i).isBlank())
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public int getIndexPreceding(BasicSourceToken token, int startIndex) {
		int i = startIndex;
		while (i >= 0 && get(i).equals(token))
			i--;
		return i;
	}

	public int getIndexPreceding(Class<? extends BasicSourceToken> tokenType, int startIndex) {
		int i = startIndex;
		while (i >= 0 && tokenType.isAssignableFrom(get(i).getClass()))
			i--;
		return i;
	}

	public int getIndexPrecedingWhitespace(int startIndex) {
		int i = startIndex;
		while (i >= 0 && get(i).isBlank())
			i--;
		return i;
	}

	public boolean startsWith(BasicSourceToken token) {
		return !isEmpty() && getFirstToken().equals(token);
	}

	public boolean startsWith(Class<? extends BasicSourceToken> tokenType) {
		return !isEmpty() && tokenType.isAssignableFrom(getFirstToken().getClass());
	}

	public boolean startsWithLineNumber() {
		return startsWith(BasicLineNumberToken.class);
	}

	public boolean endsWith(BasicSourceToken token) {
		return !isEmpty() && getLastToken().equals(token);
	}

	public boolean endsWith(Class<? extends BasicSourceToken> tokenType) {
		return !isEmpty() && tokenType.isAssignableFrom(getLastToken().getClass());
	}

	public boolean contains(BasicSourceToken token) {
		return getFirstIndexOf(token) >= 0;
	}

	public boolean contains(Class<? extends BasicSourceToken> tokenType) {
		return getFirstIndexOf(tokenType) >= 0;
	}

	public BasicSourceTokenSequence prepend(BasicSourceToken... tokens) {
		return insert(0, tokens);
	}

	public BasicSourceTokenSequence prepend(BasicSourceTokenSequence sequence) {
		return prepend(sequence.getTokensArray());
	}

	public BasicSourceTokenSequence append(BasicSourceToken... tokens) {
		return insert(size(), tokens);
	}

	public BasicSourceTokenSequence append(BasicSourceTokenSequence sequence) {
		return append(sequence.getTokensArray());
	}

	public BasicSourceTokenSequence insert(int index, BasicSourceToken... tokens) {
		if (tokens.length > 0) {
			for (int i = tokens.length - 1; i >= 0; i--) {
				getTokens().add(index, tokens[i]);
			}
			setModified(true);
		}
		return this;
	}

	public BasicSourceTokenSequence insert(int index, BasicSourceTokenSequence sequence) {
		return insert(index, sequence.getTokensArray());
	}

	public BasicSourceTokenSequence clear() {
		if (!isEmpty()) {
			getTokens().clear();
			setModified(true);
		}
		return this;
	}

	public BasicSourceTokenSequence removeFirst(BasicSourceToken token) {
		int index = getFirstIndexOf(token);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removeLast(BasicSourceToken token) {
		int index = getLastIndexOf(token);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removeNext(BasicSourceToken token, int startIndex) {
		int index = getNextIndexOf(token, startIndex);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removePrevious(BasicSourceToken token, int startIndex) {
		int index = getPreviousIndexOf(token, startIndex);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removeAll(BasicSourceToken token) {
		int index = getFirstIndexOf(token);
		while (index >= 0) {
			remove(index);
			index = getNextIndexOf(token, index);
		}
		return this;
	}

	public BasicSourceTokenSequence removeFirst(Class<? extends BasicSourceToken> tokenType) {
		int index = getFirstIndexOf(tokenType);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removeLast(Class<? extends BasicSourceToken> tokenType) {
		int index = getLastIndexOf(tokenType);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removeNext(Class<? extends BasicSourceToken> tokenType, int startIndex) {
		int index = getNextIndexOf(tokenType, startIndex);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removePrevious(Class<? extends BasicSourceToken> tokenType, int startIndex) {
		int index = getPreviousIndexOf(tokenType, startIndex);
		if (index >= 0) {
			remove(index);
		}
		return this;
	}

	public BasicSourceTokenSequence removeAll(Class<? extends BasicSourceToken> tokenType) {
		int index = getFirstIndexOf(tokenType);
		while (index >= 0) {
			remove(index);
			index = getNextIndexOf(tokenType, index);
		}
		return this;
	}

	public BasicSourceToken remove(int index) {
		setModified(true);
		return getTokens().remove(index);
	}

	public BasicSourceTokenSequence removeRange(int fromIndex, int toIndex) {
		for (int i = fromIndex; i < toIndex; i++) {
			remove(fromIndex);
		}
		return this;
	}

	public BasicSourceTokenSequence replaceFirst(BasicSourceToken tokenToReplace, BasicSourceToken replacementToken) {
		int index = getFirstIndexOf(tokenToReplace);
		if (index >= 0) {
			replace(index, replacementToken);
		}
		return this;
	}

	public BasicSourceTokenSequence replaceLast(BasicSourceToken tokenToReplace, BasicSourceToken replacementToken) {
		int index = getLastIndexOf(tokenToReplace);
		if (index >= 0) {
			replace(index, replacementToken);
		}
		return this;
	}

	public BasicSourceTokenSequence replaceNext(BasicSourceToken tokenToReplace, BasicSourceToken replacementToken,
			int startIndex) {
		int index = getNextIndexOf(tokenToReplace, startIndex);
		if (index >= 0) {
			replace(index, replacementToken);
		}
		return this;
	}

	public BasicSourceTokenSequence replacePrevious(BasicSourceToken tokenToReplace, BasicSourceToken replacementToken,
			int startIndex) {
		int index = getPreviousIndexOf(tokenToReplace, startIndex);
		if (index >= 0) {
			replace(index, replacementToken);
		}
		return this;
	}

	public BasicSourceTokenSequence replaceAll(BasicSourceToken tokenToReplace, BasicSourceToken replacementToken) {
		int index = getFirstIndexOf(tokenToReplace);
		while (index >= 0) {
			replace(index, replacementToken);
			index = getNextIndexOf(tokenToReplace, index);
		}
		return this;
	}

	public BasicSourceTokenSequence replace(int index, BasicSourceToken... tokens) {
		remove(index);
		return insert(index, tokens);
	}

	public BasicSourceTokenSequence replace(int index, BasicSourceTokenSequence sequence) {
		return replace(index, sequence.getTokensArray());
	}

	public BasicSourceTokenSequence replaceRange(int fromIndex, int toIndex, BasicSourceToken... tokens) {
		removeRange(fromIndex, toIndex);
		return insert(fromIndex, tokens);
	}

	public BasicSourceTokenSequence replaceRange(int fromIndex, int toIndex, BasicSourceTokenSequence sequence) {
		return replaceRange(fromIndex, toIndex, sequence.getTokensArray());
	}

	/**
	 * Creates a new token sequence that is a subset of this sequence.
	 * <p>
	 * The returned token sequence is independent from this sequence. Any changes in the returned sequence are
	 * <em>not</em> reflected in this sequence, and vice-versa.
	 * </p>
	 * 
	 * @param fromIndex
	 *            Low token index (inclusive) of the sub sequence
	 * @param toIndex
	 *            High token index (exclusive) of the sub sequence
	 * @return A new token sequence that is a subset of this sequence
	 */
	public BasicSourceTokenSequence subSequence(int fromIndex, int toIndex) {
		BasicSourceTokenSequence sub = new BasicSourceTokenSequence();
		for (int i = fromIndex; i < toIndex; i++) {
			sub.append(get(i));
		}
		sub.setModified(false);
		return sub;
	}

	@Override
	public Iterator<BasicSourceToken> iterator() {
		return Collections.unmodifiableList(getTokens()).iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		for (BasicSourceToken token : this) {
			if (sb.length() > 0)
				sb.append(' ');
			sb.append(token);
		}
		return sb.toString();
	}

	public String getSourceCode() {
		StringBuilder sb = new StringBuilder(256);
		boolean firstToken = true;
		for (BasicSourceToken token : this) {
			sb.append(token.getSourceFragment());
			if (firstToken && token instanceof BasicLineNumberToken) {
				sb.append(' ');
			}
			firstToken = false;
		}
		return sb.toString();
	}

	private BasicSourceToken[] getTokensArray() {
		BasicSourceToken[] array = new BasicSourceToken[size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = getTokens().get(i);
		}
		return array;
	}

	private List<BasicSourceToken> getTokens() {
		return tokens;
	}

	private void setTokens(List<BasicSourceToken> tokens) {
		this.tokens = tokens;
	}

	public boolean isModified() {
		return modified;
	}

	private void setModified(boolean modified) {
		this.modified = modified;
	}

}