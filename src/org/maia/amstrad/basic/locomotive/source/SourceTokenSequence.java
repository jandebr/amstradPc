package org.maia.amstrad.basic.locomotive.source;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicSyntaxException;

public class SourceTokenSequence implements Iterable<SourceToken> {

	private List<SourceToken> tokens;

	public SourceTokenSequence() {
		setTokens(new Vector<SourceToken>());
	}

	public SourceTokenSequence(List<SourceToken> tokens) {
		this();
		for (SourceToken token : tokens)
			append(token);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		return getTokens().size();
	}

	public SourceToken get(int index) {
		return getTokens().get(index);
	}

	public int getFirstIndexOf(SourceToken token) {
		return getTokens().indexOf(token);
	}

	public int getLastIndexOf(SourceToken token) {
		return getTokens().lastIndexOf(token);
	}

	public int getNextIndexOf(SourceToken token, int fromIndex) {
		if (fromIndex >= size())
			return -1;
		int index = subSequence(fromIndex, size()).getFirstIndexOf(token);
		if (index >= 0)
			index += fromIndex;
		return index;
	}

	public int getFirstIndexOf(Class<? extends SourceToken> tokenType) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getClass().equals(tokenType))
				return i;
		}
		return -1;
	}

	public int getLastIndexOf(Class<? extends SourceToken> tokenType) {
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).getClass().equals(tokenType))
				return i;
		}
		return -1;
	}

	public int getNextIndexOf(Class<? extends SourceToken> tokenType, int fromIndex) {
		if (fromIndex >= size())
			return -1;
		int index = subSequence(fromIndex, size()).getFirstIndexOf(tokenType);
		if (index >= 0)
			index += fromIndex;
		return index;
	}

	public int getIndexFollowing(SourceToken token, int fromIndex) {
		int i = fromIndex;
		while (i < size() && get(i).equals(token))
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public int getIndexFollowing(Class<? extends SourceToken> tokenType, int fromIndex) {
		int i = fromIndex;
		while (i < size() && get(i).getClass().equals(tokenType))
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public int getIndexFollowingWhitespace(int fromIndex) {
		int i = fromIndex;
		while (i < size() && get(i).getClass().equals(LiteralToken.class) && ((LiteralToken) get(i)).isBlank())
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public boolean contains(SourceToken token) {
		return getFirstIndexOf(token) >= 0;
	}

	public boolean contains(Class<? extends SourceToken> tokenType) {
		return getFirstIndexOf(tokenType) >= 0;
	}

	public void prepend(SourceToken... tokens) {
		insert(0, tokens);
	}

	public void append(SourceToken... tokens) {
		insert(size(), tokens);
	}

	public void insert(int index, SourceToken... tokens) {
		for (int i = tokens.length - 1; i >= 0; i--) {
			getTokens().add(index, tokens[i]);
		}
	}

	public void clear() {
		getTokens().clear();
	}

	public void removeFirst(SourceToken token) {
		int index = getFirstIndexOf(token);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeLast(SourceToken token) {
		int index = getLastIndexOf(token);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeNext(SourceToken token, int fromIndex) {
		int index = getNextIndexOf(token, fromIndex);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeAll(SourceToken token) {
		int index = getFirstIndexOf(token);
		while (index >= 0) {
			remove(index);
			index = getNextIndexOf(token, index);
		}
	}

	public void removeFirst(Class<? extends SourceToken> tokenType) {
		int index = getFirstIndexOf(tokenType);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeLast(Class<? extends SourceToken> tokenType) {
		int index = getLastIndexOf(tokenType);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeNext(Class<? extends SourceToken> tokenType, int fromIndex) {
		int index = getNextIndexOf(tokenType, fromIndex);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeAll(Class<? extends SourceToken> tokenType) {
		int index = getFirstIndexOf(tokenType);
		while (index >= 0) {
			remove(index);
			index = getNextIndexOf(tokenType, index);
		}
	}

	public SourceToken remove(int index) {
		return getTokens().remove(index);
	}

	public void removeRange(int fromIndex, int toIndex) {
		for (int i = fromIndex; i < toIndex; i++) {
			remove(fromIndex);
		}
	}

	public void replaceFirst(SourceToken tokenToReplace, SourceToken replacementToken) {
		int index = getFirstIndexOf(tokenToReplace);
		if (index >= 0) {
			replace(index, replacementToken);
		}
	}

	public void replaceAll(SourceToken tokenToReplace, SourceToken replacementToken) {
		int index = getFirstIndexOf(tokenToReplace);
		while (index >= 0) {
			replace(index, replacementToken);
			index = getNextIndexOf(tokenToReplace, index);
		}
	}

	public void replace(int index, SourceToken... tokens) {
		remove(index);
		insert(index, tokens);
	}

	public void replaceRange(int fromIndex, int toIndex, SourceToken... tokens) {
		removeRange(fromIndex, toIndex);
		insert(fromIndex, tokens);
	}

	public SourceTokenSequence subSequence(int fromIndex, int toIndex) {
		SourceTokenSequence sub = new SourceTokenSequence();
		sub.setTokens(getTokens().subList(fromIndex, toIndex));
		return sub;
	}

	@Override
	public Iterator<SourceToken> iterator() {
		return getTokens().iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		for (SourceToken token : this) {
			if (sb.length() > 0)
				sb.append(' ');
			sb.append(token);
		}
		return sb.toString();
	}

	public String toStringInSourceForm() {
		StringBuilder sb = new StringBuilder(256);
		boolean firstToken = true;
		for (SourceToken token : this) {
			sb.append(token.getSourceFragment());
			if (firstToken && token instanceof LineNumberToken) {
				sb.append(' ');
			}
			firstToken = false;
		}
		return sb.toString();
	}

	public BasicSourceCodeLine assemble() throws BasicSyntaxException {
		return new BasicSourceCodeLine(toStringInSourceForm());
	}

	private List<SourceToken> getTokens() {
		return tokens;
	}

	private void setTokens(List<SourceToken> tokens) {
		this.tokens = tokens;
	}

}