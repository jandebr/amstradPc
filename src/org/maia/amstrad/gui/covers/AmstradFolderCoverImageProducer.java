package org.maia.amstrad.gui.covers;

import java.awt.Dimension;
import java.awt.Image;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.pool.PooledImage;

public abstract class AmstradFolderCoverImageProducer extends AmstradCoverImageProducer {

	protected AmstradFolderCoverImageProducer(Dimension imageSize) {
		super(imageSize);
	}

	@Override
	public Image produceImage(PooledImage pooledImage) {
		Image image = null;
		if (pooledImage instanceof AmstradFolderCoverImage) {
			FolderNode folderNode = ((AmstradFolderCoverImage) pooledImage).getFolderNode();
			ProgramNode showcaseProgramNode = ((AmstradFolderCoverImage) pooledImage).getShowcaseProgramNode();
			image = produceImage(folderNode, showcaseProgramNode, getImageSize());
		}
		return image;
	}

	protected abstract Image produceImage(FolderNode folderNode, ProgramNode showcaseProgramNode, Dimension imageSize);

}