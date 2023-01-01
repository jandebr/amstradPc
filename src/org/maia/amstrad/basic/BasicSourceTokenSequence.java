package org.maia.amstrad.basic;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BasicSourceTokenSequence implements Iterable<BasicSourceToken> {

	private List<BasicSourceToken> tokens;

	public BasicSourceTokenSequence() {
		setTokens(new Vector<BasicSourceToken>());
	}

	public BasicSourceTokenSequence(List<BasicSourceToken> tokens) {
		this();
		for (BasicSourceToken token : tokens)
			append(token);
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

	public int getFirstIndexOf(BasicSourceToken token) {
		return getTokens().indexOf(token);
	}

	public int getLastIndexOf(BasicSourceToken token) {
		return getTokens().lastIndexOf(token);
	}

	public int getNextIndexOf(BasicSourceToken token, int fromIndex) {
		if (fromIndex >= size())
			return -1;
		int index = subSequence(fromIndex, size()).getFirstIndexOf(token);
		if (index >= 0)
			index += fromIndex;
		return index;
	}

	public int getFirstIndexOf(Class<? extends BasicSourceToken> tokenType) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getClass().equals(tokenType))
				return i;
		}
		return -1;
	}

	public int getLastIndexOf(Class<? extends BasicSourceToken> tokenType) {
		for (int i = size() - 1; i >= 0; i--) {
			if (get(i).getClass().equals(tokenType))
				return i;
		}
		return -1;
	}

	public int getNextIndexOf(Class<? extends BasicSourceToken> tokenType, int fromIndex) {
		if (fromIndex >= size())
			return -1;
		int index = subSequence(fromIndex, size()).getFirstIndexOf(tokenType);
		if (index >= 0)
			index += fromIndex;
		return index;
	}

	public int getIndexFollowing(BasicSourceToken token, int fromIndex) {
		int i = fromIndex;
		while (i < size() && get(i).equals(token))
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public int getIndexFollowing(Class<? extends BasicSourceToken> tokenType, int fromIndex) {
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
		while (i < size() && get(i).isBlank())
			i++;
		if (i < size())
			return i;
		else
			return -1;
	}

	public boolean contains(BasicSourceToken token) {
		return getFirstIndexOf(token) >= 0;
	}

	public boolean contains(Class<? extends BasicSourceToken> tokenType) {
		return getFirstIndexOf(tokenType) >= 0;
	}

	public void prepend(BasicSourceToken... tokens) {
		insert(0, tokens);
	}

	public void append(BasicSourceToken... tokens) {
		insert(size(), tokens);
	}

	public void insert(int index, BasicSourceToken... tokens) {
		for (int i = tokens.length - 1; i >= 0; i--) {
			getTokens().add(index, tokens[i]);
		}
	}

	public void clear() {
		getTokens().clear();
	}

	public void removeFirst(BasicSourceToken token) {
		int index = getFirstIndexOf(token);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeLast(BasicSourceToken token) {
		int index = getLastIndexOf(token);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeNext(BasicSourceToken token, int fromIndex) {
		int index = getNextIndexOf(token, fromIndex);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeAll(BasicSourceToken token) {
		int index = getFirstIndexOf(token);
		while (index >= 0) {
			remove(index);
			index = getNextIndexOf(token, index);
		}
	}

	public void removeFirst(Class<? extends BasicSourceToken> tokenType) {
		int index = getFirstIndexOf(tokenType);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeLast(Class<? extends BasicSourceToken> tokenType) {
		int index = getLastIndexOf(tokenType);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeNext(Class<? extends BasicSourceToken> tokenType, int fromIndex) {
		int index = getNextIndexOf(tokenType, fromIndex);
		if (index >= 0) {
			remove(index);
		}
	}

	public void removeAll(Class<? extends BasicSourceToken> tokenType) {
		int index = getFirstIndexOf(tokenType);
		while (index >= 0) {
			remove(index);
			index = getNextIndexOf(tokenType, index);
		}
	}

	public BasicSourceToken remove(int index) {
		return getTokens().remove(index);
	}

	public void removeRange(int fromIndex, int toIndex) {
		for (int i = fromIndex; i < toIndex; i++) {
			remove(fromIndex);
		}
	}

	public void replaceFirst(BasicSourceToken tokenToReplace, BasicSourceToken replacementToken) {
		int index = getFirstIndexOf(tokenToReplace);
		if (index >= 0) {
			replace(index, replacementToken);
		}
	}

	public void replaceAll(BasicSourceToken tokenToReplace, BasicSourceToken replacementToken) {
		int index = getFirstIndexOf(tokenToReplace);
		while (index >= 0) {
			replace(index, replacementToken);
			index = getNextIndexOf(tokenToReplace, index);
		}
	}

	public void replace(int index, BasicSourceToken... tokens) {
		remove(index);
		insert(index, tokens);
	}

	public void replaceRange(int fromIndex, int toIndex, BasicSourceToken... tokens) {
		removeRange(fromIndex, toIndex);
		insert(fromIndex, tokens);
	}

	public BasicSourceTokenSequence subSequence(int fromIndex, int toIndex) {
		BasicSourceTokenSequence sub = new BasicSourceTokenSequence();
		sub.setTokens(getTokens().subList(fromIndex, toIndex));
		return sub;
	}

	@Override
	public Iterator<BasicSourceToken> iterator() {
		return getTokens().iterator();
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

	private List<BasicSourceToken> getTokens() {
		return tokens;
	}

	private void setTokens(List<BasicSourceToken> tokens) {
		this.tokens = tokens;
	}

}