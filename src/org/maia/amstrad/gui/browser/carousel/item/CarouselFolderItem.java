package org.maia.amstrad.gui.browser.carousel.item;

import java.awt.Insets;

import org.maia.amstrad.gui.browser.carousel.CarouselComponent;
import org.maia.amstrad.gui.browser.carousel.CarouselHost;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImage;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CarouselFolderItem extends CarouselRepositoryItem {

	public CarouselFolderItem(FolderNode folderNode, ProgramNode featuredProgramNode,
			CarouselComponent carouselComponent, AmstradFolderCoverImageProducer coverImageProducer, Insets margin) {
		this(folderNode, carouselComponent,
				new AmstradFolderCoverImage(folderNode, featuredProgramNode, coverImageProducer), margin);
	}

	public CarouselFolderItem(FolderNode folderNode, CarouselComponent carouselComponent,
			AmstradFolderCoverImage coverImage, Insets margin) {
		super(folderNode, carouselComponent, coverImage, margin);
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	@Override
	protected void doExecute(CarouselHost host) {
		super.doExecute(host);
		host.enterFolder(getFolderNode());
	}

	@Override
	public AmstradFolderCoverImage getCoverImage() {
		return (AmstradFolderCoverImage) super.getCoverImage();
	}

	public FolderNode getFolderNode() {
		return getRepositoryNode().asFolder();
	}

}