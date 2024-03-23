package org.maia.amstrad.tape.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.tape.decorate.AudioTapeBitDecorator;
import org.maia.amstrad.tape.decorate.BytecodeAudioDecorator;
import org.maia.amstrad.tape.decorate.SourcecodeBytecodeDecorator;
import org.maia.amstrad.tape.decorate.BytecodeAudioDecorator.BytecodeAudioDecoration;
import org.maia.amstrad.tape.decorate.SourcecodeBytecodeDecorator.SourcecodeBytecodeDecoration;
import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.amstrad.tape.model.ByteCodeRange;
import org.maia.amstrad.tape.model.ByteSequence;
import org.maia.amstrad.tape.model.SourceCodePosition;
import org.maia.amstrad.tape.model.SourceCodeRange;
import org.maia.amstrad.tape.read.AudioFile;

public class CodeInspectorView extends JPanel implements SourceCodeView.SourceCodeCaretListener {

	private AudioTapeProgram audioTapeProgram;

	private SourceCodeView sourceCodeView;

	private ByteCodeView byteCodeView;

	private JPanel audioPane;

	public CodeInspectorView(AudioTapeProgram audioTapeProgram) {
		super(new BorderLayout());
		this.audioTapeProgram = audioTapeProgram;
		this.sourceCodeView = buildSourceCodeView(audioTapeProgram.getSourceCodeOnTape());
		this.byteCodeView = buildByteCodeView(audioTapeProgram.getByteCode());
		this.audioPane = buildAudioPane();
		buildView();
	}

	private void buildView() {
		add(getSourceCodeView(), BorderLayout.NORTH);
		add(getByteCodeView(), BorderLayout.CENTER);
		add(getAudioPane(), BorderLayout.SOUTH);
	}

	private SourceCodeView buildSourceCodeView(BasicSourceCode sourceCode) {
		SourceCodeView view = new SourceCodeView(sourceCode);
		view.setPreferredSize(UIResourcesTape.sourceCodeInspectorViewSize);
		view.addCaretListener(this);
		return view;
	}

	private ByteCodeView buildByteCodeView(ByteSequence byteCode) {
		ByteCodeView view = new ByteCodeView(byteCode);
		view.setPreferredSize(UIResourcesTape.byteCodeInspectorViewSize);
		return view;
	}

	private JPanel buildAudioPane() {
		JPanel pane = new JPanel(new GridLayout(1, 0, 8, 0));
		pane.setPreferredSize(UIResourcesTape.audioInspectorViewSize);
		pane.setBackground(Color.BLACK);
		return pane;
	}

	@Override
	public void caretUpdate(SourceCodePosition position, SourceCodeView source) {
		selectTokenAtSourceCodePosition(position);
	}

	public void selectTokenAtSourceCodePosition(SourceCodePosition position) {
		if (position != null) {
			List<SourcecodeBytecodeDecoration> scds = getSourceCodeDecorator().getDecorationsOverlappingRange(position,
					position);
			if (!scds.isEmpty()) {
				changeSourceCodeSelection(scds.get(0).getSourceCodeRange());
			}
		} else {
			clearSourceCodeSelection();
		}
	}

	public void changeSourceCodeSelection(SourceCodeRange range) {
		clearSourceCodeSelection();
		getSourceCodeView().selectSourceCode(range);
		updateByteCodeSelection();
	}

	public void clearSourceCodeSelection() {
		if (getSourceCodeSelection() != null) {
			getSourceCodeView().clearSourceCodeSelection();
			clearByteCodeSelection();
		}
	}

	private void updateByteCodeSelection() {
		clearByteCodeSelection();
		if (getSourceCodeSelection() != null) {
			List<SourcecodeBytecodeDecoration> scds = getSourceCodeDecorator().getDecorationsInsideRange(
					getSourceCodeSelection().getStartPosition(), getSourceCodeSelection().getEndPosition());
			if (!scds.isEmpty()) {
				getByteCodeView().selectByteCode(scds.get(0).getByteCodeRange());
				getByteCodeView().scrollToVisibleSelection();
				updateAudioSelection();
			}
		}
	}

	private void clearByteCodeSelection() {
		if (getByteCodeView().getByteCodeSelection() != null) {
			getByteCodeView().clearByteCodeSelection();
			clearAudioSelection();
		}
	}

	private void updateAudioSelection() {
		clearAudioSelection();
		List<AudioRangeView> rangeViews = new Vector<AudioRangeView>();
		for (AudioRange range : getSelectedAudioRanges()) {
			try {
				AudioRangeView rangeView = AudioRangeView.create(range, getAudioFile(), getAudioDecorator());
				getAudioPane().add(rangeView);
				rangeViews.add(rangeView);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		if (!rangeViews.isEmpty()) {
			validate();
			for (AudioRangeView rangeView : rangeViews) {
				rangeView.scrollToCenter();
			}
		}
	}

	private List<AudioRange> getSelectedAudioRanges() {
		List<AudioRange> ranges = new Vector<AudioRange>();
		ByteCodeRange selection = getByteCodeView().getByteCodeSelection();
		if (selection != null) {
			List<BytecodeAudioDecoration> bads = getByteCodeDecorator()
					.getDecorationsInsideRange(selection.getByteCodeOffset(), selection.getByteCodeEnd());
			int i0 = 0;
			for (int i = 1; i < bads.size(); i++) {
				BytecodeAudioDecoration previousBad = bads.get(i - 1);
				BytecodeAudioDecoration bad = bads.get(i);
				if (bad.getAudioSampleOffset() != previousBad.getAudioSampleEnd() + 1L) {
					// Discontinuity
					long offset = bads.get(i0).getAudioSampleOffset();
					long length = previousBad.getAudioSampleEnd() - offset + 1L;
					ranges.add(new AudioRange(offset, length));
					i0 = i;
				}
			}
			// Remaining range
			long offset = bads.get(i0).getAudioSampleOffset();
			long length = bads.get(bads.size() - 1).getAudioSampleEnd() - offset + 1L;
			ranges.add(new AudioRange(offset, length));
		}
		return ranges;
	}

	private void clearAudioSelection() {
		getAudioPane().removeAll();
		repaint();
	}

	public SourceCodeRange getSourceCodeSelection() {
		return getSourceCodeView().getSourceCodeSelection();
	}

	public ByteCodeRange getByteCodeSelection() {
		return getByteCodeView().getByteCodeSelection();
	}

	public AudioTapeProgram getAudioTapeProgram() {
		return audioTapeProgram;
	}

	private SourcecodeBytecodeDecorator getSourceCodeDecorator() {
		return getAudioTapeProgram().getSourceCodeDecorator();
	}

	private BytecodeAudioDecorator getByteCodeDecorator() {
		return getAudioTapeProgram().getByteCodeDecorator();
	}

	private AudioFile getAudioFile() {
		return getAudioTapeProgram().getAudioFile();
	}

	private AudioTapeBitDecorator getAudioDecorator() {
		return getAudioTapeProgram().getAudioDecorator();
	}

	private SourceCodeView getSourceCodeView() {
		return sourceCodeView;
	}

	private ByteCodeView getByteCodeView() {
		return byteCodeView;
	}

	private JPanel getAudioPane() {
		return audioPane;
	}

	private static class AudioRangeView extends JScrollPane {

		private AudioFileWaveformView audioView;

		private AudioFilePositionView audioPositionView;

		private static long RANGE_EXTENSION = 1000L; // in samples (equally divided over both sides)

		private AudioRangeView(JComponent view) {
			super(view);
		}

		public static AudioRangeView create(AudioRange audioRange, AudioFile audioFile,
				AudioTapeBitDecorator bitDecorator) throws IOException {
			AudioRange displayRange = extendedRangeForDisplay(audioRange, audioFile);
			AudioFileWaveformView audioView = new AudioFileWaveformView(audioFile, displayRange, bitDecorator);
			audioView.setSize(2 * (int) displayRange.getSampleLength(), 240);
			audioView.setPreferredSize(audioView.getSize());
			audioView.setSelectedRange(audioRange);
			AudioFilePositionView audioPositionView = new AudioFilePositionView(audioFile, displayRange);
			audioPositionView.setTimeNotationInMillisPrecision(true);
			audioPositionView.track(audioView);
			Box box = Box.createVerticalBox();
			box.add(audioView);
			box.add(audioPositionView);
			AudioRangeView audioRangeView = new AudioRangeView(box);
			audioRangeView.audioView = audioView;
			audioRangeView.audioPositionView = audioPositionView;
			return audioRangeView;
		}

		private static AudioRange extendedRangeForDisplay(AudioRange range, AudioFile audioFile) {
			long pmax = audioFile.getNumberOfSamples() - 1L;
			long p0 = Math.max(0, range.getSampleOffset() - RANGE_EXTENSION / 2);
			long p1 = Math.min(pmax, range.getSampleEnd() + RANGE_EXTENSION / 2);
			return new AudioRange(p0, p1 - p0 + 1L);
		}

		public void scrollToCenter() {
			JScrollBar scrollBar = getHorizontalScrollBar();
			scrollBar.setValue((scrollBar.getMaximum() - scrollBar.getVisibleAmount()) / 2);
		}

		public AudioFileWaveformView getAudioView() {
			return audioView;
		}

		public AudioFilePositionView getAudioPositionView() {
			return audioPositionView;
		}

	}

}