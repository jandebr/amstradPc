package org.maia.amstrad.tape.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.tape.decorate.AudioTapeBitDecorator;
import org.maia.amstrad.tape.decorate.AudioTapeBitDecorator.AudioTapeBitDecoration;
import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.model.Bit;
import org.maia.amstrad.tape.read.AudioFile;

public class AudioFileWaveformView extends AudioFilePositionSource {

	private AudioRange displayRange;

	private AudioRange selectedRange;

	private AudioTapeBitDecorator bitDecorator;

	private static Color BACKGROUND_COLOR = Color.BLACK;

	private static Color SELECTION_FILL_COLOR = new Color(11, 3, 23);

	private static Color SELECTION_SEPERATOR_COLOR = new Color(43, 22, 74);

	private static Font SELECTION_INFO_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	private static Color SELECTION_INFO_COLOR = Color.WHITE;

	private static Color SELECTION_INFO_BACKDROP_COLOR = new Color(11, 3, 23, 160);

	private static Color SELECTION_WAVE_ZERO_COLOR = new Color(156, 175, 230);

	private static Color SELECTION_WAVE_ONE_COLOR = new Color(240, 161, 161);

	private static Color SELECTION_WAVE_COLOR = new Color(200, 200, 200);

	private static Font SELECTION_WAVE_LABEL_FONT = new Font(Font.MONOSPACED, Font.BOLD, 50);

	private static Color SELECTION_WAVE_LABEL_COLOR = new Color(190, 169, 222, 20);

	private static Color WAVE_ZERO_COLOR = new Color(12, 23, 54);

	private static Color WAVE_ONE_COLOR = new Color(54, 12, 12);

	private static Color WAVE_COLOR = new Color(30, 30, 30);

	public AudioFileWaveformView(AudioFile audioFile, AudioRange displayRange, AudioTapeBitDecorator bitDecorator) {
		super(audioFile);
		setDisplayRange(displayRange);
		setBitDecorator(bitDecorator);
		setBackground(BACKGROUND_COLOR);
	}

	@Override
	public int getWidthForDisplayRange() {
		return getWidth();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (getDisplayRange() != null && getDisplayRange().getSampleLength() > 0) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			try {
				List<AudioBit> bits = getBitsInDisplayRange();
				List<AudioBit> selectedBits = filterBitsInSelectedRange(bits);
				paintSelectionBackground(g2);
				paintSelectionBitSeparators(g2, selectedBits);
				paintAudioWave(g2, bits);
				paintSelectionBitLabels(g2, selectedBits);
				paintSelectionRangeInfo(g2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void paintSelectionBackground(Graphics2D g2) {
		Rectangle bounds = getBoundsOfSelectedRange();
		if (bounds != null) {
			g2.setColor(SELECTION_FILL_COLOR);
			g2.fill(bounds);
		}
	}

	private void paintSelectionBitSeparators(Graphics2D g2, List<AudioBit> bits) {
		g2.setColor(SELECTION_SEPERATOR_COLOR);
		for (int i = 0; i < bits.size(); i++) {
			AudioBit bit = bits.get(i);
			// Left edge
			int x = projectSamplePositionToX(bit.getSampleOffset());
			int w = i % 8 == 0 ? 4 : 1;
			g2.fillRect(x - w / 2, 0, w, getHeight());
			// Right edge
			x = projectSamplePositionToX(bit.getSampleEnd() + 1L);
			w = i % 8 == 7 ? 4 : 1;
			g2.fillRect(x - w / 2, 0, w, getHeight());
		}
	}

	private void paintSelectionBitLabels(Graphics2D g2, List<AudioBit> bits) {
		g2.setColor(SELECTION_WAVE_LABEL_COLOR);
		g2.setFont(SELECTION_WAVE_LABEL_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int y = getHeight() - 8;
		for (int i = 0; i < bits.size(); i++) {
			AudioBit bit = bits.get(i);
			String label = bit.isOn() ? "1" : "0";
			int x0 = projectSamplePositionToX(bit.getSampleOffset());
			int x1 = projectSamplePositionToX(bit.getSampleEnd());
			int x = (x0 + x1 - fm.stringWidth(label)) / 2;
			g2.drawString(label, x, y);
		}
	}

	private void paintSelectionRangeInfo(Graphics2D g2) {
		AudioRange range = getSelectedRange();
		if (range != null) {
			int sampleRate = getAudioFile().getSampleRate();
			String info = "[ " + UIResourcesTape.formatTimeOfAudioSamplePosition(range.getSampleOffset(), sampleRate, true)
					+ " , " + UIResourcesTape.formatTimeOfAudioSamplePosition(range.getSampleEnd(), sampleRate, true)
					+ " ]";
			g2.setFont(SELECTION_INFO_FONT);
			FontMetrics fm = g2.getFontMetrics();
			Rectangle bounds = getBoundsOfSelectedRange();
			int infoWidth = fm.stringWidth(info);
			int x = bounds.x + bounds.width / 2 - infoWidth / 2;
			int ybase = 20;
			g2.setColor(SELECTION_INFO_BACKDROP_COLOR);
			g2.fillRect(x, ybase - fm.getAscent(), infoWidth, fm.getAscent() + fm.getDescent());
			g2.setColor(SELECTION_INFO_COLOR);
			g2.drawString(info, x, ybase);
		}
	}

	private void paintAudioWave(Graphics2D g2, List<AudioBit> bits) throws IOException {
		AudioRange sel = getSelectedRange();
		long siLimit = getAudioFile().getNumberOfSamples();
		int n = (int) getDisplayRange().getSampleLength();
		int ybase = getHeight() / 2;
		float dx = getWidth() / (float) n;
		for (int run = 0; run <= 1; run++) {
			long si = getDisplayRange().getSampleOffset();
			int x = 0, y = 0, xprev = 0, yprev = 0;
			AudioBit bit = bits.isEmpty() ? null : bits.get(0);
			int bitIndex = 0;
			int[] xp = new int[4];
			int[] yp = new int[4];
			for (int i = 0; i <= n; i++) {
				// X and Y
				int sample = si == siLimit ? 0 : getAudioFile().getSample(si);
				int ydelta = (int) (sample / (float) Short.MAX_VALUE * ybase);
				y = ybase - (int) (ydelta * (1.0 - run * 0.4));
				x = Math.round(i * dx);
				// Selection
				boolean inSelection = sel != null && si >= sel.getSampleOffset() && si <= sel.getSampleEnd();
				// Bit
				while (bit != null && si > bit.getSampleEnd()) {
					bitIndex++;
					bit = bitIndex < bits.size() ? bits.get(bitIndex) : null;
				}
				AudioBit actualBit = bit;
				if (bit != null && si < bit.getSampleOffset())
					actualBit = null;
				// Draw
				if (i > 0) {
					xp[0] = xprev;
					yp[0] = ybase;
					xp[1] = xprev;
					yp[1] = yprev;
					xp[2] = x;
					yp[2] = y;
					xp[3] = x;
					yp[3] = ybase;
					Color color = getFillColorFor(actualBit, inSelection);
					if (run == 1) {
						color = inSelection ? color.brighter() : BACKGROUND_COLOR;
					}
					g2.setColor(color);
					g2.fillPolygon(xp, yp, xp.length);
				}
				// Advance
				xprev = x;
				yprev = y;
				si++;
			}
		}
	}

	private Color getFillColorFor(AudioBit bit, boolean inSelection) {
		if (bit == null) {
			return inSelection ? SELECTION_WAVE_COLOR : WAVE_COLOR;
		} else if (bit.isOn()) {
			return inSelection ? SELECTION_WAVE_ONE_COLOR : WAVE_ONE_COLOR;
		} else {
			return inSelection ? SELECTION_WAVE_ZERO_COLOR : WAVE_ZERO_COLOR;
		}
	}

	private Rectangle getBoundsOfSelectedRange() {
		Rectangle bounds = null;
		if (getSelectedRange() != null) {
			int x0 = projectSamplePositionToX(getSelectedRange().getSampleOffset());
			int x1 = projectSamplePositionToX(getSelectedRange().getSampleEnd());
			bounds = new Rectangle(x0, 0, x1 - x0 + 1, getHeight());
		}
		return bounds;
	}

	private int projectSamplePositionToX(long samplePosition) {
		float dx = getWidth() / (float) getDisplayRange().getSampleLength();
		int s = (int) (samplePosition - getDisplayRange().getSampleOffset());
		return Math.round(s * dx);
	}

	private List<AudioBit> getBitsInDisplayRange() {
		List<AudioBit> bits = new Vector<AudioBit>(64);
		if (getDisplayRange() != null) {
			List<AudioTapeBitDecoration> decorations = getBitDecorator().getDecorationsOverlappingRange(
					getDisplayRange().getSampleOffset(), getDisplayRange().getSampleEnd());
			for (AudioTapeBitDecoration decoration : decorations) {
				boolean on = Bit.ONE.equals(decoration.getBit());
				bits.add(new AudioBit(decoration.getOffset(), decoration.getLength(), on));
			}
		}
		return bits;
	}

	private List<AudioBit> filterBitsInSelectedRange(List<AudioBit> bits) {
		List<AudioBit> selBits = new Vector<AudioBit>(bits.size());
		AudioRange selection = getSelectedRange();
		if (selection != null) {
			for (AudioBit bit : bits) {
				if (bit.getSampleOffset() >= selection.getSampleOffset()
						&& bit.getSampleEnd() <= selection.getSampleEnd()) {
					selBits.add(bit);
				}
			}
		}
		return selBits;
	}

	public AudioRange getDisplayRange() {
		return displayRange;
	}

	public void setDisplayRange(AudioRange displayRange) {
		this.displayRange = displayRange;
	}

	public AudioRange getSelectedRange() {
		return selectedRange;
	}

	public void setSelectedRange(AudioRange selectedRange) {
		this.selectedRange = selectedRange;
	}

	public AudioTapeBitDecorator getBitDecorator() {
		return bitDecorator;
	}

	private void setBitDecorator(AudioTapeBitDecorator bitDecorator) {
		this.bitDecorator = bitDecorator;
	}

	private static class AudioBit extends AudioRange {

		private boolean on;

		public AudioBit(long sampleOffset, long sampleLength, boolean on) {
			super(sampleOffset, sampleLength);
			this.on = on;
		}

		public boolean isOn() {
			return on;
		}

	}

}