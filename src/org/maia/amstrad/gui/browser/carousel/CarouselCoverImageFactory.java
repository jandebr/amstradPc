package org.maia.amstrad.gui.browser.carousel;

import java.awt.Dimension;

import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.cassette.CassetteFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.cassette.CassetteProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.stock.StockFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.stock.StockProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.stock.fabric.FabricCoverImageMaker;
import org.maia.amstrad.gui.covers.stock.fabric.TestPatchPatternGenerator;

public abstract class CarouselCoverImageFactory {

	private Dimension imageSize;

	private CarouselProgramBrowserTheme theme;

	protected CarouselCoverImageFactory(Dimension imageSize, CarouselProgramBrowserTheme theme) {
		this.imageSize = imageSize;
		this.theme = theme;
	}

	public abstract AmstradProgramCoverImageProducer createProgramCoverImageProducer();

	public abstract AmstradFolderCoverImageProducer createFolderCoverImageProducer();

	public Dimension getImageSize() {
		return imageSize;
	}

	public CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

	public static class CassetteCoverImageFactory extends CarouselCoverImageFactory {

		private AmstradProgramCoverImageProducer programCoverImageProducer;

		private AmstradFolderCoverImageProducer folderCoverImageProducer;

		public CassetteCoverImageFactory(Dimension imageSize, CarouselProgramBrowserTheme theme) {
			super(imageSize, theme);
			init();
		}

		private void init() {
			Dimension imageSize = getImageSize();
			CarouselProgramBrowserTheme theme = getTheme();
			// (nested) stock image producers
			FabricCoverImageMaker fabric = new FabricCoverImageMaker(new TestPatchPatternGenerator());
			StockFolderCoverImageProducer stockFolder = new StockFolderCoverImageProducer(imageSize, fabric);
			StockProgramCoverImageProducer stockProgram = new StockProgramCoverImageProducer(stockFolder);
			// Cassette image producers
			CassetteProgramCoverImageProducer cassetteProgram = new CassetteProgramCoverImageProducer(imageSize,
					theme.getBackgroundColor(), theme.getCarouselProgramPosterBackgroundColorDark(),
					theme.getCarouselProgramPosterBackgroundColorBright(), theme.getCarouselProgramTitleFont(),
					theme.getCarouselProgramTitleColor(), theme.getCarouselProgramTitleBackgroundColor(),
					theme.getCarouselProgramTitleRelativeVerticalPosition(), stockProgram);
			CassetteFolderCoverImageProducer cassetteFolder = new CassetteFolderCoverImageProducer(imageSize,
					theme.getBackgroundColor(), theme.getCarouselFolderTitleFont(), theme.getCarouselFolderTitleColor(),
					cassetteProgram, stockFolder);
			setProgramCoverImageProducer(cassetteProgram);
			setFolderCoverImageProducer(cassetteFolder);
		}

		@Override
		public AmstradProgramCoverImageProducer createProgramCoverImageProducer() {
			return programCoverImageProducer;
		}

		@Override
		public AmstradFolderCoverImageProducer createFolderCoverImageProducer() {
			return folderCoverImageProducer;
		}

		private void setProgramCoverImageProducer(AmstradProgramCoverImageProducer imageProducer) {
			this.programCoverImageProducer = imageProducer;
		}

		private void setFolderCoverImageProducer(AmstradFolderCoverImageProducer imageProducer) {
			this.folderCoverImageProducer = imageProducer;
		}

	}

}