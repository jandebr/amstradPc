package org.maia.amstrad.tape.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.tape.model.AudioRange;
import org.maia.amstrad.tape.read.AudioFile;
import org.maia.amstrad.tape.read.AudioFileSubsampler;

@SuppressWarnings("serial")
public class AudioFileView extends AudioFilePositionSource {

	private short[] amplitudes;

	private boolean selectionEnabled = true;

	private SelectionController selectionController;

	private SelectionOnView selectionOnView;

	private Color selectionColor = new Color(166, 210, 245);

	private List<SelectionListener> selectionListeners;

	public AudioFileView(AudioFile audioFile, int width, int height) throws IOException {
		super(audioFile);
		this.amplitudes = AudioFileSubsampler.getInstance().subsampleUnsigned(audioFile, width);
		setSize(width, height);
		setPreferredSize(getSize());
		setBackground(Color.WHITE);
		setForeground(new Color(20, 20, 20));
		setupSelectionController();
		this.selectionListeners = new Vector<SelectionListener>();
	}

	@Override
	public int getWidthForDisplayRange() {
		return amplitudes.length;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		paintBackgroundAndSelection(g2);
		paintAmplitudes(g2);
	}

	private void paintBackgroundAndSelection(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		SelectionOnView sov = getSelectionOnView();
		if (sov != null) {
			g2.setColor(getSelectionColor());
			g2.fillRect(sov.getMinX(), 0, sov.getWidth(), getHeight());
		}
	}

	private void paintAmplitudes(Graphics2D g2) {
		Color c1 = getForeground();
		Color c2 = c1.brighter().brighter();
		int ybase = getHeight() / 2;
		double yscale = ybase / (double) Short.MAX_VALUE;
		for (int x = 0; x < amplitudes.length; x++) {
			int e = (int) Math.floor(amplitudes[x] * yscale);
			g2.setColor(c1);
			g2.drawLine(x, ybase - e, x, ybase + e);
			e = Math.round(e * 0.6f);
			g2.setColor(c2);
			g2.drawLine(x, ybase - e, x, ybase + e);
		}
	}

	public void addSelectionListener(SelectionListener listener) {
		getSelectionListeners().add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		getSelectionListeners().remove(listener);
	}

	protected void fireSelectionChanged() {
		for (SelectionListener listener : getSelectionListeners()) {
			listener.selectionChanged(this);
		}
	}

	private void setupSelectionController() {
		SelectionController ctr = new SelectionController();
		addMouseListener(ctr);
		addMouseMotionListener(ctr);
		this.selectionController = ctr;
	}

	private SelectionController getSelectionController() {
		return selectionController;
	}

	private void initSelection(int x) {
		setSelectionOnView(new SelectionOnView(x, x));
		fireSelectionChanged();
		repaint();
	}

	public void clearSelection() {
		setSelectionOnView(null);
		fireSelectionChanged();
		repaint();
	}

	public boolean hasSelection() {
		return getSelectionOnView() != null;
	}

	public AudioRange getSelection() {
		AudioRange selection = null;
		SelectionOnView sov = getSelectionOnView();
		if (sov != null) {
			double upperX = getWidthForDisplayRange() - 1;
			long upperSample = getAudioFile().getNumberOfSamples() - 1L;
			long sampleOffset = Math.round(sov.getMinX() / upperX * upperSample);
			long sampleEnd = Math.round(sov.getMaxX() / upperX * upperSample);
			selection = new AudioRange(sampleOffset, sampleEnd - sampleOffset + 1L);
		}
		return selection;
	}

	public void setSelection(AudioRange selection) {
		if (selection != null) {
			int minX = mapFilePositionToView(selection.getSampleOffset());
			int maxX = mapFilePositionToView(selection.getSampleEnd());
			setSelectionOnView(new SelectionOnView(minX, maxX));
			fireSelectionChanged();
			repaint();
		} else {
			clearSelection();
		}
	}

	public Rectangle getViewBounds(AudioRange range) {
		int minX = mapFilePositionToView(range.getSampleOffset());
		int maxX = mapFilePositionToView(range.getSampleEnd());
		return new Rectangle(minX, 0, maxX - minX + 1, getHeight());
	}

	private int mapFilePositionToView(long pos) {
		int x = -1;
		double upperX = getWidthForDisplayRange() - 1;
		double upperSample = getAudioFile().getNumberOfSamples() - 1L;
		x = (int) Math.round(pos / upperSample * upperX);
		return x;
	}

	protected SelectionOnView getSelectionOnView() {
		return selectionOnView;
	}

	protected void setSelectionOnView(SelectionOnView selection) {
		this.selectionOnView = selection;
	}

	public boolean isSelectionEnabled() {
		return selectionEnabled;
	}

	public void setSelectionEnabled(boolean enabled) {
		if (!enabled)
			clearSelection();
		this.selectionEnabled = enabled;
	}

	public Color getSelectionColor() {
		return selectionColor;
	}

	public void setSelectionColor(Color color) {
		this.selectionColor = color;
	}

	private List<SelectionListener> getSelectionListeners() {
		return selectionListeners;
	}

	private class SelectionController extends MouseAdapter {

		public SelectionController() {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!isSelectionEnabled())
				return;
			if (e.getButton() == MouseEvent.BUTTON1) {
				int x = e.getX();
				boolean shift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) > 0;
				if (hasSelection() && shift) {
					getSelectionOnView().adjustEnds(x);
					fireSelectionChanged();
					repaint();
				} else {
					initSelection(x);
				}
			} else {
				clearSelection();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (!isSelectionEnabled())
				return;
			fireSelectionChanged();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!isSelectionEnabled())
				return;
			boolean pressed = (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) > 0;
			if (hasSelection() && pressed) {
				int x = e.getX();
				getSelectionOnView().adjustEnds(x);
				repaint();
			}
		}

	}

	private static class SelectionOnView {

		private int minX;

		private int maxX;

		public SelectionOnView(int minX, int maxX) {
			this.minX = minX;
			this.maxX = maxX;
		}

		public void adjustEnds(int x) {
			if (x <= getMinX()) {
				setMinX(x);
			} else if (x >= getMaxX()) {
				setMaxX(x);
			} else {
				int dmin = Math.abs(x - getMinX());
				int dmax = Math.abs(x - getMaxX());
				if (dmax <= dmin) {
					setMaxX(x);
				} else {
					setMinX(x);
				}
			}
		}

		public int getWidth() {
			return getMaxX() - getMinX() + 1;
		}

		public int getMinX() {
			return minX;
		}

		public void setMinX(int minX) {
			this.minX = minX;
		}

		public int getMaxX() {
			return maxX;
		}

		public void setMaxX(int maxX) {
			this.maxX = maxX;
		}

	}

	public static interface SelectionListener {

		void selectionChanged(AudioFileView view);

	}

}