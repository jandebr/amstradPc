package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

import org.maia.amstrad.gui.covers.cassette.CassetteCoverImageMaker.CoverImageEmbedding;
import org.maia.amstrad.gui.covers.stock.fabric.ClothTextureImageMaker;
import org.maia.amstrad.gui.covers.stock.fabric.FabricCoverImageMaker;
import org.maia.amstrad.gui.covers.stock.fabric.TestPatchPatternGenerator;
import org.maia.amstrad.gui.covers.util.ResourcePaths;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.Randomizer;

public class CassetteCoverImageMakerTest implements ResourcePaths {

	private static NumberFormat nf;

	private static FabricCoverImageMaker coverMaker;

	private static final String TEST_PATH = "resources/test/covers/";

	static {
		nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(3);
		coverMaker = new FabricCoverImageMaker(new TestPatchPatternGenerator());
		// coverMaker = new FabricCoverImageMaker(new AxialPatchPatternGenerator(Color.BLACK));
		// coverMaker.setConceptMode(true);
	}

	public static void main(String[] args) {
		new CassetteCoverImageMakerTest().start();
	}

	private void start() {
		// createClothTextureImage();
		// createCassetteTextureImage();
		// createCassetteGlossImage();
		createFabricPosterImage(450);
		// createClosedCassetteImage("Dinosaurs II", 450, Color.BLACK);
		// createOpenCassetteImage("Dinosaurs III", 450, Color.BLACK);
	}

	private void createClothTextureImage() {
		BufferedImage texture = new ClothTextureImageMaker().createTextureImage();
		ImageUtils.writeToFile(texture, CLOTH_PATH + "cloth-texture.png");
	}

	private void createCassetteTextureImage() {
		BufferedImage texture = new CassetteTextureImageMaker().createTextureImage();
		ImageUtils.writeToFile(texture, CASSETTE_OPEN_PATH + "cassette-texture-300x480.png");
		ImageUtils.writeToFile(texture, CASSETTE_CLOSED_PATH + "cassette-texture-300x480.png");
	}

	private void createCassetteGlossImage() {
		BufferedImage gloss = new CassetteGlossImageMaker().createGlossImage();
		ImageUtils.writeToFile(gloss, CASSETTE_OPEN_PATH + "cassette-open-gloss-300x586.png");
	}

	private void createFabricPosterImage(int height) {
		double scaleFactor = height / ClosedCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		ClosedCassetteCoverImageMaker imageMaker = new ClosedCassetteCoverImageMaker(null, scaleFactor);
		for (int i = 0; i < 10; i++) {
			ImageUtils.writeToFile(createCoverImage(imageMaker), TEST_PATH + "poster_" + nf.format(i) + ".png");
		}
	}

	private void createClosedCassetteImage(String title, int height, Color background) {
		double scaleFactor = height / ClosedCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		Randomizer rnd = new Randomizer(title);
		ClosedCassetteCoverImageMaker imageMaker = new ClosedCassetteCoverImageMaker(title, rnd, scaleFactor);
		imageMaker.setTitleRelativeVerticalPosition(0.1f);
		Dimension size = imageMaker.scaleSize(ClosedCassetteCoverImageMaker.CANONICAL_SIZE);
		CoverImageEmbedding embedding = new CoverImageEmbedding(size, background);
		embedding.setPadTopFraction(0.32f);
		BufferedImage posterImage = createCoverImage(imageMaker);
		BufferedImage cassetteImage = imageMaker.makeCoverImage(posterImage, embedding);
		ImageUtils.writeToFile(cassetteImage, TEST_PATH + "cassette-closed.png");
		long t0 = System.nanoTime();
		for (int i = 0; i < 100; i++) {
			imageMaker.makeCoverImage(createCoverImage(imageMaker), embedding);
		}
		System.out.println((System.nanoTime() - t0) / 1000000f / 100f + " ms.");
	}

	private void createOpenCassetteImage(String title, int height, Color background) {
		double scaleFactor = height / OpenCassetteCoverImageMaker.CANONICAL_SIZE.getHeight();
		Randomizer rnd = new Randomizer(title);
		OpenCassetteCoverImageMaker imageMaker = new OpenCassetteCoverImageMaker(title, rnd, scaleFactor);
		Dimension size = imageMaker.scaleSize(OpenCassetteCoverImageMaker.CANONICAL_SIZE);
		CoverImageEmbedding embedding = new CoverImageEmbedding(size, background);
		embedding.setPadTopFraction(0);
		BufferedImage posterImage = createCoverImage(imageMaker);
		BufferedImage cassetteImage = imageMaker.makeCoverImage(posterImage, embedding);
		ImageUtils.writeToFile(cassetteImage, TEST_PATH + "cassette-open.png");
		long t0 = System.nanoTime();
		for (int i = 0; i < 100; i++) {
			imageMaker.makeCoverImage(createCoverImage(imageMaker), embedding);
		}
		System.out.println((System.nanoTime() - t0) / 1000000f / 100f + " ms.");
	}

	private static BufferedImage createCoverImage(CassetteCoverImageMaker imageMaker) {
		Dimension size = imageMaker.scaleSize(ClosedCassetteCoverImageMaker.CANONICAL_POSTER_REGION.getSize());
		return coverMaker.makeCoverImage(size);
	}

}