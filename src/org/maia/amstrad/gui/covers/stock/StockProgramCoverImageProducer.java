package org.maia.amstrad.gui.covers.stock;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.covers.AmstradFolderCoverImage;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.ImageDetailLevel;
import org.maia.amstrad.gui.covers.stock.badge.EmbossedBadgeCoverImageMaker;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.ImageUtils;

public class StockProgramCoverImageProducer extends AmstradProgramCoverImageProducer
		implements AmstradProgramPosterImageMaker {

	private StockFolderCoverImageProducer folderImageMaker; // background

	private EmbossedBadgeCoverImageMaker badgeImageMaker; // badge

	public static final float BADGE_PADDING_RATIO = 0.05f;

	public StockProgramCoverImageProducer(StockFolderCoverImageProducer folderImageMaker,
			EmbossedBadgeCoverImageMaker badgeImageMaker) {
		super(folderImageMaker.getImageSize(), folderImageMaker.getBackgroundColor());
		this.folderImageMaker = folderImageMaker;
		this.badgeImageMaker = badgeImageMaker;
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		return makePosterImage(programNode, getImageSize(), ImageDetailLevel.FULL);
	}

	@Override
	public Image makePosterImage(ProgramNode programNode, Dimension size, ImageDetailLevel detailLevel) {
		BufferedImage poster = makePosterBackground(programNode.getParent(), size);
		if (ImageDetailLevel.FULL.equals(detailLevel)) {
			Insets insets = EmbossedBadgeCoverImageMaker.computeCenteredBadgeInsets(poster, BADGE_PADDING_RATIO);
			getBadgeImageMaker().setRandomizer(createRandomizer(programNode));
			String badgeId = programNode.getProgram().getCoverImageBadgeId();
			poster = getBadgeImageMaker().overlayEmbossedBadge(poster, insets, badgeId);
		}
		return poster;
	}

	protected BufferedImage makePosterBackground(FolderNode folderNode, Dimension size) {
		StockFolderCoverImageProducer imageProducer = getFolderImageMaker().deriveForImageSize(size);
		AmstradFolderCoverImage folderImage = new AmstradFolderCoverImage(folderNode, imageProducer);
		return ImageUtils.convertToBufferedImage(folderImage.getImage()); // cached (shared between sibling programs)
	}

	private StockFolderCoverImageProducer getFolderImageMaker() {
		return folderImageMaker;
	}

	private EmbossedBadgeCoverImageMaker getBadgeImageMaker() {
		return badgeImageMaker;
	}

}