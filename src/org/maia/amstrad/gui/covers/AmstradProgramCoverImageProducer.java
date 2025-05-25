package org.maia.amstrad.gui.covers;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.pool.PooledImage;

public abstract class AmstradProgramCoverImageProducer extends AmstradCoverImageProducer {

	protected AmstradProgramCoverImageProducer(Dimension imageSize) {
		super(imageSize);
	}

	@Override
	public Image produceImage(PooledImage pooledImage) {
		Image image = null;
		if (pooledImage instanceof AmstradProgramCoverImage) {
			ProgramNode programNode = ((AmstradProgramCoverImage) pooledImage).getProgramNode();
			image = produceImage(programNode, getImageSize());
		}
		return image;
	}

	protected abstract Image produceImage(ProgramNode programNode, Dimension imageSize);

}