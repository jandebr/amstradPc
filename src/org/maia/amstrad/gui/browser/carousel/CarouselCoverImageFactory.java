package org.maia.amstrad.gui.browser.carousel;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.carousel.theme.CarouselProgramBrowserTheme;
import org.maia.amstrad.gui.covers.AmstradFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradFolderPosterImageMaker;
import org.maia.amstrad.gui.covers.AmstradProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.AmstradProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.FeaturedProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.cascade.CascadedFolderPosterImageMaker;
import org.maia.amstrad.gui.covers.cascade.CascadedProgramPosterImageMaker;
import org.maia.amstrad.gui.covers.cassette.CassetteFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.cassette.CassetteProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.repo.RepositoryFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.repo.RepositoryProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.stock.StockFolderCoverImageProducer;
import org.maia.amstrad.gui.covers.stock.StockProgramCoverImageProducer;
import org.maia.amstrad.gui.covers.stock.badge.EmbossedBadgeCoverImageMaker;
import org.maia.amstrad.gui.covers.stock.badge.PhylopicBadgeCoverImageMaker;
import org.maia.amstrad.gui.covers.stock.fabric.CheckerboardPatchPatternGenerator;
import org.maia.amstrad.gui.covers.stock.fabric.FabricCoverImageMaker;
import org.maia.amstrad.program.browser.config.AmstradProgramBrowserCoverImageOption;
import org.maia.svg.phylopic.db.PhylopicSvgOfflineDatabase;

public abstract class CarouselCoverImageFactory {

	private Dimension imageSize;

	private CarouselProgramBrowserTheme theme;

	protected CarouselCoverImageFactory(Dimension imageSize, CarouselProgramBrowserTheme theme) {
		this.imageSize = imageSize;
		this.theme = theme;
	}

	public abstract AmstradProgramCoverImageProducer createProgramCoverImageProducer(
			AmstradProgramBrowserCoverImageOption programOption);

	public abstract AmstradFolderCoverImageProducer createFolderCoverImageProducer(
			AmstradProgramBrowserCoverImageOption folderOption, AmstradProgramBrowserCoverImageOption programOption);

	public Dimension getImageSize() {
		return imageSize;
	}

	public CarouselProgramBrowserTheme getTheme() {
		return theme;
	}

	public static class CassetteCoverImageFactory extends CarouselCoverImageFactory {

		private Map<AmstradProgramBrowserCoverImageOption, CassetteProgramCoverImageProducer> programCoverImageProducerMap;

		private Map<AmstradProgramBrowserCoverImageOption, CassetteFolderCoverImageProducer> folderCoverImageProducerMap;

		private StockProgramCoverImageProducer stockProgramCoverImageProducer;

		private StockFolderCoverImageProducer stockFolderCoverImageProducer;

		public CassetteCoverImageFactory(Dimension imageSize, CarouselProgramBrowserTheme theme) {
			super(imageSize, theme);
			this.programCoverImageProducerMap = new HashMap<AmstradProgramBrowserCoverImageOption, CassetteProgramCoverImageProducer>();
			this.folderCoverImageProducerMap = new HashMap<AmstradProgramBrowserCoverImageOption, CassetteFolderCoverImageProducer>();
		}

		@Override
		public CassetteProgramCoverImageProducer createProgramCoverImageProducer(
				AmstradProgramBrowserCoverImageOption programOption) {
			CassetteProgramCoverImageProducer imageProducer = programCoverImageProducerMap.get(programOption);
			if (imageProducer == null) {
				CarouselProgramBrowserTheme theme = getTheme();
				AmstradProgramPosterImageMaker posterImageMaker = createProgramPosterImageMaker(programOption);
				imageProducer = new CassetteProgramCoverImageProducer(getImageSize(), theme.getBackgroundColor(),
						theme.getCarouselProgramTitleFont(), theme.getCarouselProgramTitleColor(),
						theme.getCarouselProgramTitleBackgroundColor(),
						theme.getCarouselProgramTitleRelativeVerticalPosition(), posterImageMaker);
				programCoverImageProducerMap.put(programOption, imageProducer);
			}
			return imageProducer;
		}

		@Override
		public CassetteFolderCoverImageProducer createFolderCoverImageProducer(
				AmstradProgramBrowserCoverImageOption folderOption,
				AmstradProgramBrowserCoverImageOption programOption) {
			CassetteFolderCoverImageProducer imageProducer = folderCoverImageProducerMap.get(folderOption);
			if (imageProducer == null) {
				CarouselProgramBrowserTheme theme = getTheme();
				AmstradFolderPosterImageMaker posterImageMaker = createFolderPosterImageMaker(folderOption,
						programOption);
				imageProducer = new CassetteFolderCoverImageProducer(getImageSize(), theme.getBackgroundColor(),
						theme.getCarouselFolderTitleFont(), theme.getCarouselFolderTitleColor(), posterImageMaker);
				folderCoverImageProducerMap.put(folderOption, imageProducer);
			}
			return imageProducer;
		}

		private AmstradProgramPosterImageMaker createProgramPosterImageMaker(
				AmstradProgramBrowserCoverImageOption programOption) {
			CascadedProgramPosterImageMaker cascade = new CascadedProgramPosterImageMaker();
			if (AmstradProgramBrowserCoverImageOption.REPOSITORY.equals(programOption)) {
				cascade.appendImageMaker(new RepositoryProgramCoverImageProducer(getImageSize(),
						getTheme().getCarouselPosterBackgroundColorDark(),
						getTheme().getCarouselPosterBackgroundColorBright()));
			}
			cascade.appendImageMaker(getStockProgramCoverImageProducer()); // fallback
			return cascade;
		}

		private AmstradFolderPosterImageMaker createFolderPosterImageMaker(
				AmstradProgramBrowserCoverImageOption folderOption,
				AmstradProgramBrowserCoverImageOption programOption) {
			CascadedFolderPosterImageMaker cascade = new CascadedFolderPosterImageMaker();
			if (AmstradProgramBrowserCoverImageOption.REPOSITORY.equals(folderOption)) {
				cascade.appendImageMaker(new RepositoryFolderCoverImageProducer(getImageSize(),
						getTheme().getCarouselPosterBackgroundColorDark(),
						getTheme().getCarouselPosterBackgroundColorBright()));
			} else if (AmstradProgramBrowserCoverImageOption.FEATURED.equals(folderOption)) {
				cascade.appendImageMaker(
						new FeaturedProgramPosterImageMaker(createProgramPosterImageMaker(programOption)));
			}
			cascade.appendImageMaker(getStockFolderCoverImageProducer()); // fallback
			return cascade;
		}

		protected FabricCoverImageMaker createFabricCoverImageMaker() {
			return new FabricCoverImageMaker(new CheckerboardPatchPatternGenerator());
		}

		protected EmbossedBadgeCoverImageMaker createProgramBadgeImageMaker() {
			PhylopicSvgOfflineDatabase db = AmstradFactory.getInstance().getAmstradContext().getPhylopicDatabase();
			return new PhylopicBadgeCoverImageMaker(db);
		}

		private StockProgramCoverImageProducer getStockProgramCoverImageProducer() {
			if (stockProgramCoverImageProducer == null) {
				StockFolderCoverImageProducer stockFolder = getStockFolderCoverImageProducer();
				EmbossedBadgeCoverImageMaker badge = createProgramBadgeImageMaker();
				stockProgramCoverImageProducer = new StockProgramCoverImageProducer(stockFolder, badge);
			}
			return stockProgramCoverImageProducer;
		}

		private StockFolderCoverImageProducer getStockFolderCoverImageProducer() {
			if (stockFolderCoverImageProducer == null) {
				FabricCoverImageMaker fabric = createFabricCoverImageMaker();
				stockFolderCoverImageProducer = new StockFolderCoverImageProducer(getImageSize(), fabric);
			}
			return stockFolderCoverImageProducer;
		}

	}

}