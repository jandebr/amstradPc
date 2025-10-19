package org.maia.amstrad.gui.covers.stock;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImage;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImageMaker;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.swing.layout.FillMode;
import org.maia.swing.layout.InnerRegionLayout;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.view.FloatSize;

public class StockProgramCoverImageProducer extends AmstradProgramCoverImageProducer
		implements AmstradProgramPosterImageMaker {

	private StockFolderCoverImageProducer folderImageMaker;

	private static SVGDocument svgImage = UIResources.loadSvg("covers/emblems/Eudyptula.svg");

	public StockProgramCoverImageProducer(StockFolderCoverImageProducer folderImageMaker) {
		super(folderImageMaker.getImageSize(), folderImageMaker.getBackgroundColor());
		this.folderImageMaker = folderImageMaker;
	}

	@Override
	protected Image produceImage(ProgramNode programNode) {
		return makePosterImage(programNode, getImageSize());
	}

	@Override
	public Image makePosterImage(ProgramNode programNode, Dimension size) {
		Image background = makePosterBackground(programNode.getParent(), size);
		Image foreground = makePosterForeground(programNode, size);
		return superimposeImages(background, foreground);
	}

	protected Image makePosterBackground(FolderNode folderNode, Dimension size) {
		StockFolderCoverImageProducer imageProducer = getFolderImageMaker().deriveForImageSize(size);
		AmstradFolderCoverImage folderImage = new AmstradFolderCoverImage(folderNode, imageProducer);
		return folderImage.getImage(); // can be cached
	}

	protected Image makePosterForeground(ProgramNode programNode, Dimension size) {
		// TODO new Randomizer(programNode.getName())
		BufferedImage image = ImageUtils.createImage(size);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		FloatSize svgSize = svgImage.size();
		Dimension svgDim = new Dimension(Math.round(svgSize.width), Math.round(svgSize.height));
		int ix = Math.max(size.width / 10, 4);
		int iy = Math.max(size.height / 10, 4);
		InnerRegionLayout layout = new InnerRegionLayout(size, svgDim, new Insets(iy, ix, iy, ix));
		layout.setFillMode(FillMode.FIT);
		Rectangle svgBounds = layout.getInnerRegionLayoutBounds();
		double scale = svgBounds.getHeight() / svgSize.getHeight();
		g.translate(svgBounds.x, svgBounds.y);
		g.scale(scale, scale);
		svgImage.render(null, g);
		g.dispose();
		return image;
	}

	protected Image superimposeImages(Image background, Image foreground) {
		return ImageUtils.combineByTransparency(ImageUtils.convertToBufferedImage(foreground),
				ImageUtils.convertToBufferedImage(background));
	}

	private StockFolderCoverImageProducer getFolderImageMaker() {
		return folderImageMaker;
	}

}