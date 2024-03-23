package org.maia.amstrad.tape.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JPanel;

import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.read.AudioFile;

@SuppressWarnings("serial")
public class AudioFilePositionView extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {

	private AudioFile audioFile;

	private AudioRange displayRange;

	private int audioFileSampleRate;

	private int positionOnScreen;

	private long positionInFile;

	private boolean positionLabelInTimeNotation = true;

	private boolean timeNotationInMillisPrecision = false;

	private static NumberFormat positionNumberFormat;

	public static Color STRIP_AUDIOFILE_COLOR = new Color(30, 30, 30);

	public static Color STRIP_POSITION_COLOR = new Color(120, 120, 120);

	static {
		positionNumberFormat = NumberFormat.getIntegerInstance();
		positionNumberFormat.setGroupingUsed(true);
	}

	public AudioFilePositionView(AudioFile audioFile) throws IOException {
		this(audioFile, new AudioRange(0L, audioFile.getNumberOfSamples()));
	}

	public AudioFilePositionView(AudioFile audioFile, AudioRange displayRange) throws IOException {
		this.audioFile = audioFile;
		this.audioFileSampleRate = audioFile.getSampleRate();
		this.displayRange = displayRange;
		clearPosition();
		setBackground(Color.BLACK);
		setForeground(Color.YELLOW);
	}

	public void track(AudioFilePositionSource source) {
		source.addMouseListener(this);
		source.addMouseMotionListener(this);
		source.addComponentListener(this);
		followWidth(source);
	}

	public void untrack(AudioFilePositionSource source) {
		source.removeMouseListener(this);
		source.removeMouseMotionListener(this);
		source.removeComponentListener(this);
	}

	private void followWidth(AudioFilePositionSource source) {
		int width = source.getWidthForDisplayRange();
		setSize(width, UIResourcesTape.audioPositionViewHeight);
		setPreferredSize(getSize());
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		paintStrip(g2);
		paintCurrentPosition(g2);
	}

	private void paintStrip(Graphics2D g2) {
		Font fnt = g2.getFont();
		Font fntBold = fnt.deriveFont(Font.BOLD, 14);
		String str = getAudioFile().getSourceFile().getName();
		int w = g2.getFontMetrics(fntBold).stringWidth(str);
		int x = 4;
		while (x < getWidth()) {
			int xp = x + w + 8;
			g2.setColor(STRIP_AUDIOFILE_COLOR);
			g2.setFont(fntBold);
			g2.drawString(str, x, 16);
			g2.setColor(STRIP_POSITION_COLOR);
			g2.setFont(fnt);
			g2.drawString(getPositionLabel(mapScreenToFilePosition(xp)), xp, 16);
			x += w + UIResourcesTape.audioPositionFileNameRepeatGap;
		}
		g2.setFont(fnt);
	}

	private void paintCurrentPosition(Graphics2D g2) {
		int x = getPositionOnScreen();
		if (x >= 0) {
			paintPositionLabel(g2, x);
			paintPositionMarker(g2, x);
		}
	}

	private void paintPositionLabel(Graphics2D g2, int x) {
		String label = getPositionLabel();
		int w = g2.getFontMetrics().stringWidth(label);
		if (x + w >= getWidth()) {
			x = getWidth() - 1 - w;
		}
		g2.setColor(getBackground());
		g2.fillRect(x - 2, 6, w + 4, 14);
		g2.setColor(getForeground());
		g2.drawString(label, x, 18);
	}

	private void paintPositionMarker(Graphics2D g2, int x) {
		g2.setColor(getForeground());
		for (int i = 0; i <= 5; i++) {
			g2.drawLine(x - i, i, x + i, i);
		}
	}

	private String getPositionLabel() {
		return getPositionLabel(getPositionInFile());
	}

	private String getPositionLabel(long positionInFile) {
		if (isPositionLabelInTimeNotation()) {
			return UIResourcesTape.formatTimeOfAudioSamplePosition(positionInFile, getAudioFileSampleRate(),
					isTimeNotationInMillisPrecision());
		} else {
			return positionNumberFormat.format(positionInFile);
		}
	}

	private void clearPosition() {
		setPositionOnScreen(-1);
		setPositionInFile(-1L);
		repaint();
	}

	private void recordPosition(int x) {
		if (getDisplayRange() != null) {
			setPositionOnScreen(x);
			setPositionInFile(mapScreenToFilePosition(x));
			repaint();
		}
	}

	private long mapScreenToFilePosition(int x) {
		double r = x / (double) (getWidth() - 1);
		return getDisplayRange().getSampleOffset() + Math.round(r * getDisplayRange().getSampleLength());
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		recordPosition(event.getX());
	}

	@Override
	public void mouseExited(MouseEvent event) {
		clearPosition();
	}

	@Override
	public void mousePressed(MouseEvent event) {
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}

	@Override
	public void mouseDragged(MouseEvent event) {
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		recordPosition(event.getX());
	}

	@Override
	public void componentHidden(ComponentEvent event) {
	}

	@Override
	public void componentMoved(ComponentEvent event) {
	}

	@Override
	public void componentResized(ComponentEvent event) {
		followWidth((AudioFilePositionSource) event.getComponent());
	}

	@Override
	public void componentShown(ComponentEvent event) {
	}

	public AudioFile getAudioFile() {
		return audioFile;
	}

	public AudioRange getDisplayRange() {
		return displayRange;
	}

	private int getAudioFileSampleRate() {
		return audioFileSampleRate;
	}

	private int getPositionOnScreen() {
		return positionOnScreen;
	}

	private void setPositionOnScreen(int positionOnScreen) {
		this.positionOnScreen = positionOnScreen;
	}

	private long getPositionInFile() {
		return positionInFile;
	}

	private void setPositionInFile(long positionInFile) {
		this.positionInFile = positionInFile;
	}

	public boolean isPositionLabelInTimeNotation() {
		return positionLabelInTimeNotation;
	}

	public void setPositionLabelInTimeNotation(boolean timeNotation) {
		this.positionLabelInTimeNotation = timeNotation;
	}

	public boolean isTimeNotationInMillisPrecision() {
		return timeNotationInMillisPrecision;
	}

	public void setTimeNotationInMillisPrecision(boolean millisPrecision) {
		this.timeNotationInMillisPrecision = millisPrecision;
	}

}