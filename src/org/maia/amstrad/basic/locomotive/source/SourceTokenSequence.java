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
		return getTokens().subList(fromIndex, size()).indexOf(token);
	}

	public boolean contains(SourceToken token) {
		return getTokens().contains(token);
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
		getTokens().remove(token);
	}

	public void removeAll(SourceToken token) {
		while (getTokens().remove(token))
			;
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
			index = getFirstIndexOf(tokenToReplace);
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