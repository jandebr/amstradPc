package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public abstract class AmstradAwtDisplaySource extends AmstradAbstractDisplaySource {

	private Color background = Color.BLACK;

	protected AmstradAwtDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		super.init(displayComponent, graphicsContext);
		setLayout(createLayoutManager());
		buildUI();
		validate();
	}

	@Override
	public void dispose(JComponent displayComponent) {
		super.dispose(displayComponent);
		removeAll();
		setLayout(null);
		validate();
	}

	@Override
	public final void renderOntoDisplay(Graphics2D display, Rectangle displayBounds,
			AmstradGraphicsContext graphicsContext) {
		int width = displayBounds.width;
		int height = displayBounds.height;
		Graphics2D g = (Graphics2D) display.create(displayBounds.x, displayBounds.y, width, height);
		if (!getAmstradContext().isLowPerformance()) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		renderContent(g, width, height);
		g.dispose();
	}

	protected void renderContent(Graphics2D g, int width, int height) {
		paintBackground(g, width, height);
		paintAddedComponents(g);
	}

	private void paintBackground(Graphics2D g, int width, int height) {
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
	}

	private void paintAddedComponents(Graphics2D g) {
		getDisplayComponent().paintComponents(g);
	}

	protected abstract LayoutManager createLayoutManager();

	protected abstract void buildUI();

	protected void validate() {
		getDisplayComponent().validate();
	}

	protected void add(Component component) {
		add(component, -1);
	}

	protected void add(Component component, int index) {
		add(component, null, index);
	}

	protected void add(Component component, Object constraints) {
		add(component, constraints, -1);
	}

	protected void add(Component component, Object constraints, int index) {
		getDisplayComponent().add(component, constraints, index);
	}

	protected void remove(Component component) {
		if (component != null) {
			getDisplayComponent().remove(component);
		}
	}

	protected void remove(int index) {
		getDisplayComponent().remove(index);
	}

	protected void removeAll() {
		getDisplayComponent().removeAll();
	}

	protected LayoutManager getLayout() {
		return getDisplayComponent().getLayout();
	}

	private void setLayout(LayoutManager layout) {
		getDisplayComponent().setLayout(layout);
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

}