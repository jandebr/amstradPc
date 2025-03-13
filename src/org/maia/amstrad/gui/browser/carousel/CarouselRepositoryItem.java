package org.maia.amstrad.gui.browser.carousel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public abstract class CarouselRepositoryItem extends CarouselItem {

	private Node repositoryNode;

	protected CarouselRepositoryItem(Node repositoryNode, CarouselComponent carouselComponent, Dimension size,
			Insets margin, Font font) {
		super(carouselComponent, size, margin, font);
		this.repositoryNode = repositoryNode;
	}

	@Override
	public String getTitle() {
		return getRepositoryNode().getName();
	}

	public Node getRepositoryNode() {
		return repositoryNode;
	}

}