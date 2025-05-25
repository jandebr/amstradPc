package org.maia.amstrad.gui.covers;

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
		if (node.isRoot()) {
			return "COVER-IMAGE-" + imageProducer.getClass().getName();
		} else {
			return createImageIdentifierFor(node.getParent(), imageProducer) + "|:|" + node.getName();
		}
	}

	private static ImagePool getCoverImagePool() {
		return AmstradFactory.getInstance().getAmstradContext().getSharedImagePool();
	}

	@Override
	public AmstradCoverImageProducer getImageProducer() {
		return imageProducer;
	}

	public Node getNode() {
		return node;
	}

}