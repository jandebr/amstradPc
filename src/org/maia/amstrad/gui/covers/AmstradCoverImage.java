package org.maia.amstrad.gui.covers;

import java.awt.Color;
import java.awt.Dimension;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.graphics2d.image.pool.AbstractPooledImage;
import org.maia.graphics2d.image.pool.ImagePool;

public abstract class AmstradCoverImage extends AbstractPooledImage {

	private Node node;

	private AmstradCoverImageProducer imageProducer;

	protected AmstradCoverImage(Node node, AmstradCoverImageProducer imageProducer) {
		super(createImageIdentifierFor(node, imageProducer), getCoverImagePool());
		this.node = node;
		this.imageProducer = imageProducer;
	}

	private static String createImageIdentifierFor(Node node, AmstradCoverImageProducer imageProducer) {
		return "image.node[" + createNodePathString(node) + "]#" + imageProducer.getProducerIdentifier();
	}

	private static String createNodePathString(Node node) {
		if (node.isRoot()) {
			return "$";
		} else {
			return createNodePathString(node.getParent()) + (char) 187 + node.getName();
		}
	}

	private static ImagePool getCoverImagePool() {
		return AmstradFactory.getInstance().getAmstradContext().getSharedImagePool();
	}

	public Dimension getImageSize() {
		return getImageProducer().getImageSize();
	}

	public Color getBackgroundColor() {
		return getImageProducer().getBackgroundColor();
	}

	@Override
	public AmstradCoverImageProducer getImageProducer() {
		return imageProducer;
	}

	public Node getNode() {
		return node;
	}

}