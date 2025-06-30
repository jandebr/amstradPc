package org.maia.amstrad.gui.covers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.pool.PooledImage;
import org.maia.graphics2d.image.pool.RetryablePooledImageProducerException;

public abstract class AmstradFolderCoverImageProducer extends AmstradCoverImageProducer {

	protected AmstradFolderCoverImageProducer(Dimension imageSize, Color backgroundColor) {
		super(imageSize, backgroundColor);
	}

	@Override
	public Image produceImage(PooledImage pooledImage) throws RetryablePooledImageProducerException {
		Image image = null;
		if (pooledImage instanceof AmstradFolderCoverImage) {
			FolderNode folderNode = ((AmstradFolderCoverImage) pooledImage).getFolderNode();
			ProgramNode showcaseProgramNode = ((AmstradFolderCoverImage) pooledImage).getShowcaseProgramNode();
			image = produceImage(folderNode, showcaseProgramNode);
		}
		return image;
	}

	protected abstract Image produceImage(FolderNode folderNode, ProgramNode showcaseProgramNode)
			throws RetryablePooledImageProducerException;

}