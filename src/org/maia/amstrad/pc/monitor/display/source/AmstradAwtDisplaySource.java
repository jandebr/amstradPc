package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.swing.FillMode;
import org.maia.swing.util.BackBufferedComponent;

public abstract class AmstradAwtDisplaySource extends AmstradAbstractDisplaySource {

	private Color background = Color.BLACK;

	protected AmstradAwtDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		super.init(displayComponent, graphicsContext);
		getDisplayComponent().setLayout(createLayoutManager());
		addElements();
		getDisplayComponent().validate();
	}

	@Override
	public void dispose(JComponent displayComponent) {
		super.dispose(displayComponent);
		getDisplayComponent().removeAll();
		getDisplayComponent().setLayout(null);
		getDisplayComponent().validate();
	}

	@Override
	public final void renderOntoDisplay(Graphics2D display, Rectangle displayBounds,
			AmstradGraphicsContext graphicsContext) {
		int width = displayBounds.width;
		int height = displayBounds.height;
		Graphics2D g = (Graphics2D) display.create(displayBounds.x, displayBounds.y, width, height);
		renderContent(g, width, height);
		g.dispose();
	}

	protected void renderContent(Graphics2D g, int width, int height) {
		paintBackground(g, width, height);
		paintElements(g);
	}

	private void paintBackground(Graphics2D g, int width, int height) {
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
	}

	private void paintElements(Graphics2D g) {
		getDisplayComponent().paintComponents(g);
	}

	protected abstract LayoutManager createLayoutManager();

	protected abstract void addElements();

	protected DisplayElement addElement(JComponent component) {
		return addElement(component, -1);
	}

	protected DisplayElement addElement(JComponent component, int index) {
		return addElement(component, null, index);
	}

	protected DisplayElement addElement(JComponent component, Object constraints) {
		return addElement(component, constraints, -1);
	}

	protected DisplayElement addElement(JComponent component, Object constraints, int index) {
		getDisplayComponent().add(component, constraints, index);
		return new DisplayElementImpl(component);
	}

	protected DisplayElement addElementPaintingIncrementally(JComponent component) {
		return addElementPaintingIncrementally(component, -1);
	}

	protected DisplayElement addElementPaintingIncrementally(JComponent component, int index) {
		return addElementPaintingIncrementally(component, null, index);
	}

	protected DisplayElement addElementPaintingIncrementally(JComponent component, Object constraints) {
		return addElementPaintingIncrementally(component, constraints, -1);
	}

	protected DisplayElement addElementPaintingIncrementally(JComponent component, Object constraints, int index) {
		BackBufferedComponent bbc = new BackBufferedComponent(component);
		bbc.setFillMode(FillMode.FIT); // TODO
		return addElement(bbc, constraints, index);
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	protected static interface DisplayElement {

		Rectangle getBounds();

	}

	private static class DisplayElementImpl implements DisplayElement {

		private JComponent component;

		public DisplayElementImpl(JComponent component) {
			this.component = component;
		}

		@Override
		public Rectangle getBounds() {
			return getComponent().getBounds();
		}

		public JComponent getComponent() {
			return component;
		}

	}

}