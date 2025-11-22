package org.maia.amstrad.gui.covers.stock.badge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;

import org.maia.svg.phylopic.PhylopicSvgImage;
import org.maia.svg.phylopic.db.PhylopicSvgOfflineDatabase;
import org.maia.util.Randomizer;

public class PhylopicBadgeCoverImageMaker extends EmbossedBadgeCoverImageMaker {

	private PhylopicSvgOfflineDatabase phylopicDatabase;

	public PhylopicBadgeCoverImageMaker(File phylopicDatabaseFile) {
		super(new Randomizer());
		this.phylopicDatabase = new PhylopicSvgOfflineDatabase(phylopicDatabaseFile);
	}

	@Override
	protected MonochromeBadge drawBadge() {
		MonochromeBadge badge = null;
		PhylopicSvgOfflineDatabase db = getPhylopicDatabase();
		if (db != null && db.size() > 0) {
			int index = getRandomizer().drawIntegerNumber(0, db.size() - 1);
			PhylopicSvgImage svgImage = db.getImageEntity(index).getSvgImage();
			if (svgImage != null) {
				badge = new PhylopicBadge(svgImage);
			}
		}
		return badge;
	}

	private PhylopicSvgOfflineDatabase getPhylopicDatabase() {
		return phylopicDatabase;
	}

	private static class PhylopicBadge implements MonochromeBadge {

		private PhylopicSvgImage phylopicSvgImage;

		public PhylopicBadge(PhylopicSvgImage phylopicSvgImage) {
			this.phylopicSvgImage = phylopicSvgImage;
		}

		@Override
		public void render(Graphics2D g) {
			PhylopicSvgImage phylopic = getPhylopicSvgImage();
			Color origFillColor = phylopic.getFillColor();
			phylopic.setFillColor(g.getColor());
			phylopic.render(g);
			phylopic.setFillColor(origFillColor);
		}

		@Override
		public Dimension getSize() {
			return getPhylopicSvgImage().getSize();
		}

		public PhylopicSvgImage getPhylopicSvgImage() {
			return phylopicSvgImage;
		}

	}

}