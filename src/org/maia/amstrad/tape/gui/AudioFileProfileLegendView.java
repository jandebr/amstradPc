package org.maia.amstrad.tape.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.maia.amstrad.tape.model.profile.TapeSectionType;

public class AudioFileProfileLegendView extends JPanel {

	private Map<TapeSectionType, Color> colorMap;

	public AudioFileProfileLegendView() {
		this.colorMap = createColorMap();
		setBackground(Color.BLACK);
		setSize(computeWidth(), 24);
		setPreferredSize(getSize());
	}

	private Map<TapeSectionType, Color> createColorMap() {
		Map<TapeSectionType, Color> cmap = new HashMap<TapeSectionType, Color>();
		cmap.put(TapeSectionType.TAPE_BEGIN, new Color(14, 14, 28));
		cmap.put(TapeSectionType.SILENCE, new Color(74, 119, 142));
		cmap.put(TapeSectionType.SILENCE_BETWEEN_PROGRAMS, new Color(244, 217, 66));
		cmap.put(TapeSectionType.HEADER, new Color(124, 234, 110));
		cmap.put(TapeSectionType.HEADER_RESIDUE, new Color(81, 130, 75));
		cmap.put(TapeSectionType.HEADER_SPACER, new Color(200, 226, 197));
		cmap.put(TapeSectionType.DATA, new Color(198, 85, 87));
		cmap.put(TapeSectionType.DATA_RESIDUE, new Color(86, 36, 37));
		cmap.put(TapeSectionType.TAPE_END, new Color(14, 14, 28));
		return cmap;
	}

	public Color getColorFor(TapeSectionType stype) {
		Color c = Color.GRAY;
		if (colorMap.containsKey(stype)) {
			c = colorMap.get(stype);
		}
		return c;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		paintLegend(g2);
	}

	private void paintLegend(Graphics2D g2) {
		int x0 = 2;
		int n = TapeSectionType.values().length;
		for (int i = 0; i < n; i++) {
			TapeSectionType stype = TapeSectionType.values()[i];
			if (i > 0)
				x0 += 16;
			// Color
			Color color = getColorFor(stype);
			g2.setColor(color);
			g2.fillRect(x0, 2, 20, 20);
			g2.setColor(color.brighter());
			g2.drawRect(x0, 2, 20, 20);
			// Label
			x0 += 24;
			g2.setColor(Color.WHITE);
			String label = stype.name().replace('_', ' ');
			g2.drawString(label, x0, 20);
			int labelWidth = g2.getFontMetrics().stringWidth(label);
			x0 += labelWidth;
		}
	}

	private int computeWidth() {
		int x0 = 2;
		int n = TapeSectionType.values().length;
		for (int i = 0; i < n; i++) {
			TapeSectionType stype = TapeSectionType.values()[i];
			if (i > 0)
				x0 += 16;
			x0 += 24;
			x0 += getFontMetrics(getFont()).stringWidth(stype.name());
		}
		return x0;
	}

}