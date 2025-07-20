package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public abstract class AmstradAwtDisplaySource extends AmstradAbstractDisplaySource {

	private Color background = Color.BLACK;

	private boolean componentAdditionDeferred;

	private List<ComponentAddition> deferredComponentAdditions;

	protected AmstradAwtDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
		this.deferredComponentAdditions = new Vector<ComponentAddition>();
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
		paintBackground(g, width, height);
		if (!getAmstradContext().isLowPerformance()) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		renderContent(g, width, height);
		g.dispose();
	}

	private void paintBackground(Graphics2D g, int width, int height) {
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
	}

	protected void renderContent(Graphics2D g, int width, int height) {
		paintAddedComponents(g);
	}

	private void paintAddedComponents(Graphics2D g) {
		getDisplayComponent().paintComponents(g);
	}

	protected abstract LayoutManager createLayoutManager();

	protected abstract void buildUI();

	public void validate() {
		getDisplayComponent().validate();
	}

	public void add(Component component) {
		add(component, -1);
	}

	public void add(Component component, int index) {
		add(component, null, index);
	}

	public void add(Component component, Object constraints) {
		add(component, constraints, -1);
	}

	public void add(Component component, Object constraints, int index) {
		addImpl(component, constraints, index);
	}

	private void addImpl(Component component, Object constraints, int index) {
		ComponentAddition addition = new ComponentAddition(component, constraints, index);
		if (isComponentAdditionDeferred()) {
			synchronized (getDeferredComponentAdditions()) {
				getDeferredComponentAdditions().add(addition);
			}
		} else {
			addition.execute();
		}
	}

	protected void addDeferred() {
		synchronized (getDeferredComponentAdditions()) {
			for (ComponentAddition addition : getDeferredComponentAdditions()) {
				addition.execute();
			}
			getDeferredComponentAdditions().clear();
		}
	}

	public void remove(Component component) {
		if (component != null) {
			getDisplayComponent().remove(component);
		}
	}

	public void remove(int index) {
		getDisplayComponent().remove(index);
	}

	public void removeAll() {
		getDisplayComponent().removeAll();
	}

	protected Component[] getComponents() {
		synchronized (getDisplayComponent().getTreeLock()) {
			return getDisplayComponent().getComponents();
		}
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

	protected boolean isComponentAdditionDeferred() {
		return componentAdditionDeferred;
	}

	protected void setComponentAdditionDeferred(boolean deferred) {
		this.componentAdditionDeferred = deferred;
	}

	private List<ComponentAddition> getDeferredComponentAdditions() {
		return deferredComponentAdditions;
	}

	private class ComponentAddition {

		private Component component;

		private Object constraints;

		private int index;

		public ComponentAddition(Component component, Object constraints, int index) {
			this.component = component;
			this.constraints = constraints;
			this.index = index;
		}

		public void execute() {
			getDisplayComponent().add(getComponent(), getConstraints(), getIndex());
		}

		public Component getComponent() {
			return component;
		}

		public Object getConstraints() {
			return constraints;
		}

		public int getIndex() {
			return index;
		}

	}

}