package org.maia.amstrad.tape.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.maia.amstrad.tape.model.AudioTapeIndex;
import org.maia.amstrad.tape.model.AudioTapeProgram;
import org.maia.amstrad.tape.model.profile.TapeProfile;
import org.maia.amstrad.tape.model.profile.TapeSection;
import org.maia.amstrad.tape.model.profile.TapeSectionType;
import org.maia.amstrad.tape.read.AudioFile;
import org.maia.amstrad.tape.read.AudioFileSubsampler;

public class AudioFileProfileView extends AudioFilePositionSource implements MouseListener {

	private TapeProfile displayProfile;

	private TapeProfile selectedProfile;

	private AudioFileProfileLegendView legend;

	private short[] amplitudes;

	private TapeSection[] sections;

	private Map<TapeSection, SectionRange> sectionRanges;

	private AudioTapeIndex tapeIndex;

	private List<TapeSectionListener> sectionListeners;

	public AudioFileProfileView(AudioFile audioFile, TapeProfile displayProfile, AudioFileProfileLegendView legend,
			int width, int height) throws IOException {
		super(audioFile);
		this.displayProfile = displayProfile;
		this.legend = legend;
		this.sectionRanges = new HashMap<TapeSection, SectionRange>();
		this.sectionListeners = new Vector<TapeSectionListener>();
		this.amplitudes = AudioFileSubsampler.getInstance().subsampleUnsigned(audioFile, width);
		this.sections = projectSections(audioFile, displayProfile, width);
		setSize(width, height);
		setPreferredSize(getSize());
		setBackground(Color.BLACK);
		addMouseListener(this);
	}

	private TapeSection[] projectSections(AudioFile audioFile, TapeProfile profile, int length) throws IOException {
		TapeSection[] sections = new TapeSection[length];
		long ns = audioFile.getNumberOfSamples();
		double f = length / (double) ns;
		for (TapeSection section : profile.getSections()) {
			int x0 = (int) Math.floor(section.getStartPosition() * f);
			int x1 = (int) Math.floor(section.getEndPosition() * f);
			for (int x = x0; x <= x1; x++) {
				sections[x] = section;
				SectionRange range = getSectionRanges().get(section);
				if (range == null) {
					range = new SectionRange(section, x, x);
					getSectionRanges().put(section, range);
				} else {
					range.extendToInclude(x);
				}
			}
		}
		return sections;
	}

	public void addSectionListener(TapeSectionListener listener) {
		getSectionListeners().add(listener);
	}

	public void removeSectionListener(TapeSectionListener listener) {
		getSectionListeners().remove(listener);
	}

	@Override
	public int getWidthForDisplayRange() {
		return amplitudes.length;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		int x = event.getX();
		if (x >= 0 && x < sections.length) {
			TapeSection section = sections[x];
			if (section != null) {
				for (TapeSectionListener listener : getSectionListeners()) {
					listener.sectionClicked(section, this);
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		// no interest
	}

	@Override
	public void mouseExited(MouseEvent event) {
		// no interest
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// no interest
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// no interest
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		paintTapeProfile(g2);
		if (getTapeIndex() != null) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			paintIndexLabels(g2);
		}
	}

	private void paintTapeProfile(Graphics2D g2) {
		int ybase = getHeight() / 2;
		double yscale = (ybase - 20) / (double) Short.MAX_VALUE;
		TapeSection previousSection = null;
		Color color = adjustColorForSelection(getBaseColorFor(previousSection), previousSection);
		Color brighterColor = color.brighter();
		for (int i = 0; i < amplitudes.length; i++) {
			int x = i;
			int y0 = (int) Math.floor(amplitudes[i] * yscale);
			int y1 = (int) (y0 * 0.6);
			TapeSection section = sections[i];
			if (!isSameSection(section, previousSection)) {
				color = adjustColorForSelection(getBaseColorFor(section), section);
				brighterColor = color.brighter();
			}
			g2.setColor(color);
			g2.drawLine(x, 2, x, 20);
			g2.drawLine(x, getHeight() - 20, x, getHeight() - 3);
			g2.drawLine(x, ybase - y0, x, ybase + y0);
			g2.setColor(brighterColor);
			g2.drawLine(x, ybase - y1, x, ybase + y1);
			previousSection = section;
		}
	}

	private void paintIndexLabels(Graphics2D g2) {
		AudioTapeIndex tapeIndex = getTapeIndex();
		for (int pi = 0; pi < tapeIndex.size(); pi++) {
			AudioTapeProgram program = tapeIndex.getPrograms().get(pi);
			TapeProfile programProfile = program.getProfileOnTape();
			if (programProfile != null) {
				List<TapeSection> programHeaderSections = programProfile.getSectionsOfType(TapeSectionType.HEADER);
				List<TapeSection> programDataSections = programProfile.getSectionsOfType(TapeSectionType.DATA);
				for (int i = 0; i < programHeaderSections.size(); i++) {
					paintProgramIndexLabel(g2, programHeaderSections.get(i), String.valueOf(pi + 1));
				}
				for (int i = 0; i < programDataSections.size(); i++) {
					paintBlockIndexLabel(g2, programDataSections.get(i), "B" + String.valueOf(i + 1));
				}
			}
		}
	}

	private void paintProgramIndexLabel(Graphics2D g2, TapeSection anchorSection, String label) {
		SectionRange anchorRange = getSectionRanges().get(anchorSection);
		if (anchorRange != null) {
			g2.setFont(g2.getFont().deriveFont(Font.BOLD));
			int labelWidth = g2.getFontMetrics().stringWidth(label);
			int labelHeight = g2.getFontMetrics().getAscent();
			int xc = anchorRange.getCenterX();
			int yc = getHeight() / 2;
			int r = Math.max(2 + labelWidth / 2, 10);
			g2.setColor(getIndexFillColorFor(anchorSection));
			g2.fillOval(xc - r, yc - r, 2 * r, 2 * r);
			g2.setColor(getIndexStrokeColorFor(anchorSection));
			g2.drawOval(xc - r, yc - r, 2 * r, 2 * r);
			g2.drawString(label, xc - labelWidth / 2, yc + labelHeight / 2 - 1);
		}
	}

	private void paintBlockIndexLabel(Graphics2D g2, TapeSection anchorSection, String label) {
		SectionRange anchorRange = getSectionRanges().get(anchorSection);
		if (anchorRange != null) {
			g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
			int labelWidth = g2.getFontMetrics().stringWidth(label);
			int labelHeight = g2.getFontMetrics().getAscent();
			int xc = anchorRange.getCenterX();
			int yc = getHeight() / 2;
			int rectWidth = Math.max(labelWidth + 4, 26);
			int rectHeight = labelHeight + 4;
			g2.setColor(getIndexFillColorFor(anchorSection));
			g2.fillRect(xc - rectWidth / 2, yc - rectHeight / 2, rectWidth, rectHeight);
			g2.setColor(getIndexStrokeColorFor(anchorSection));
			g2.drawRect(xc - rectWidth / 2, yc - rectHeight / 2, rectWidth, rectHeight);
			g2.drawString(label, xc - labelWidth / 2, yc + labelHeight / 2 - 1);
		}
	}

	private Color getIndexFillColorFor(TapeSection section) {
		Color color = UIResourcesTape.adjustBrightness(getBaseColorFor(section), 0.8);
		return UIResourcesTape.setTransparency(adjustColorForSelection(color, section), 0.3);
	}

	private Color getIndexStrokeColorFor(TapeSection section) {
		Color color = UIResourcesTape.adjustBrightness(getBaseColorFor(section), -0.8);
		return UIResourcesTape.setTransparency(adjustColorForSelection(color, section), 0.3);
	}

	private Color getBaseColorFor(TapeSection section) {
		if (section != null) {
			return getLegend().getColorFor(section.getType());
		} else {
			return Color.GRAY;
		}
	}

	private Color adjustColorForSelection(Color color, TapeSection section) {
		if (hasSelection() && !isInsideSelection(section)) {
			color = UIResourcesTape.adjustBrightness(color, -0.9);
		}
		return color;
	}

	private boolean isInsideSelection(TapeSection section) {
		boolean inside = false;
		if (section != null && hasSelection()) {
			inside = getSelectedProfile().getSections().contains(section);
		}
		return inside;
	}

	private boolean isSameSection(TapeSection s1, TapeSection s2) {
		if (s1 == null) {
			return s2 == null;
		} else {
			return s2 != null && s1 == s2;
		}
	}

	public void clearSelection() {
		setSelectedProfile(null);
	}

	public boolean hasSelection() {
		return getSelectedProfile() != null;
	}

	public void scrollToVisibleSelection() {
		if (hasSelection()) {
			Rectangle rect = getSelectionDisplayArea();
			int x1 = (int) Math.max(rect.getMinX() - 100, 0);
			int x2 = (int) Math.min(rect.getMaxX() + 100, getWidth());
			Rectangle extendedRect = new Rectangle(x1, 0, x2 - x1 + 1, getHeight());
			scrollRectToVisible(extendedRect);
		}
	}

	private Rectangle getSelectionDisplayArea() {
		Rectangle area = null;
		if (hasSelection()) {
			int minX = -1;
			int maxX = -1;
			for (TapeSection section : getSelectedProfile().getSections()) {
				SectionRange range = getSectionRanges().get(section);
				if (range != null) {
					if (minX < 0 || range.getLeftX() < minX)
						minX = range.getLeftX();
					if (maxX < 0 || range.getRightX() > maxX)
						maxX = range.getRightX();
				}
			}
			if (minX >= 0) {
				area = new Rectangle(minX, 0, maxX - minX + 1, getHeight());
			}
		}
		return area;
	}

	public void showTapeIndex(AudioTapeIndex tapeIndex) {
		setTapeIndex(tapeIndex);
	}

	public void hideTapeIndex() {
		setTapeIndex(null);
	}

	public TapeProfile getDisplayProfile() {
		return displayProfile;
	}

	public AudioFileProfileLegendView getLegend() {
		return legend;
	}

	public TapeProfile getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(TapeProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
		repaint();
	}

	private Map<TapeSection, SectionRange> getSectionRanges() {
		return sectionRanges;
	}

	private AudioTapeIndex getTapeIndex() {
		return tapeIndex;
	}

	private void setTapeIndex(AudioTapeIndex tapeIndex) {
		this.tapeIndex = tapeIndex;
		repaint();
	}

	private List<TapeSectionListener> getSectionListeners() {
		return sectionListeners;
	}

	private static class SectionRange {

		private TapeSection section;

		private int leftX;

		private int rightX;

		public SectionRange(TapeSection section, int leftX, int rightX) {
			this.section = section;
			this.leftX = leftX;
			this.rightX = rightX;
		}

		public void extendToInclude(int x) {
			leftX = Math.min(leftX, x);
			rightX = Math.max(rightX, x);
		}

		public TapeSection getSection() {
			return section;
		}

		public int getCenterX() {
			return (getLeftX() + getRightX()) / 2;
		}

		public int getLeftX() {
			return leftX;
		}

		public int getRightX() {
			return rightX;
		}

	}

	public static interface TapeSectionListener {

		void sectionClicked(TapeSection section, AudioFileProfileView source);

	}

}