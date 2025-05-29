package org.maia.amstrad.gui.covers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.pool.PooledImage;
import org.maia.graphics2d.image.pool.RetryablePooledImageProducerException;

public abstract class AmstradProgramCoverImageProducer extends AmstradCoverImageProducer {

	protected AmstradProgramCoverImageProducer(Dimension imageSize) {
		super(imageSize);
	}

	protected AmstradProgramCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		super(imageSize, backgroundColor);
	}

	@Override
	public Image produceImage(PooledImage pooledImage) throws RetryablePooledImageProducerException {
		Image image = null;
		if (pooledImage instanceof AmstradProgramCoverImage) {
			ProgramNode programNode = ((AmstradProgramCoverImage) pooledImage).getProgramNode();
			image = produceImage(programNode);
		}
		return image;
	}

	protected abstract Image produceImage(ProgramNode programNode) throws RetryablePooledImageProducerException;

}