package org.maia.amstrad.program.repo.cover;

import java.awt.Image;

import org.maia.amstrad.gui.ImageProxy;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.util.AsyncSerialTaskWorker.AsyncTask;

public class CoverImageImpl implements CoverImage {

	private Node node;

	private ImageProxy imageProxy;

	public CoverImageImpl(Node node, ImageProxy imageProxy) {
		this.node = node;
		this.imageProxy = imageProxy;
	}

	@Override
	public Image probeImage() {
		Image image = null;
		ImageProxy imageProxy = getCache().fetchFromCache(getNode());
		if (imageProxy != null) {
			image = imageProxy.getImage();
		}
		return image;
	}

	@Override
	public Image demandImage() {
		Image image = probeImage();
		if (image == null) {
			getFetcher().addTask(new FetchTask());
		}
		return image;
	}

	private Node getNode() {
		return node;
	}

	private ImageProxy getImageProxy() {
		return imageProxy;
	}

	private CoverImageCache getCache() {
		return CoverImageCache.getInstance();
	}

	private CoverImageFetcher getFetcher() {
		return CoverImageFetcher.getInstance();
	}

	class FetchTask implements AsyncTask {

		public FetchTask() {
		}

		@Override
		public void process() {
			// System.out.println("Fetching cover image for " + getNode().getName());
			ImageProxy imageProxy = getImageProxy();
			Image image = imageProxy.getImage(); // loads the image and keeps it in memory
			// System.out.println("Caching cover image for " + getNode().getName());
			getCache().storeInCache(getNode(), imageProxy);
		}

		public Node getNode() {
			return CoverImageImpl.this.getNode();
		}

	}

}