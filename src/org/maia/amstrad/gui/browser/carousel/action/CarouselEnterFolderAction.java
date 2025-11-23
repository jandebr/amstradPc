package org.maia.amstrad.gui.browser.carousel.action;

import org.maia.amstrad.gui.browser.carousel.animation.CarouselAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselEnterFolderHost;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public class CarouselEnterFolderAction extends CarouselAction {

	private FolderNode folderNode;

	private Node childNodeInFocus;

	public CarouselEnterFolderAction(CarouselEnterFolderHost host, FolderNode folderNode, Node childNodeInFocus,
			CarouselAnimation animation) {
		super(host, animation);
		this.folderNode = folderNode;
		this.childNodeInFocus = childNodeInFocus;
	}

	@Override
	protected void doPerform() {
		CarouselEnterFolderHost host = getHost();
		host.getCarouselComponent().populateFolderContentsAsync(getFolderNode(), getChildNodeInFocus(), new Runnable() {

			@Override
			public void run() {
				stopAnimation();
				host.notifyEnterFolderCompleted(CarouselEnterFolderAction.this);
			}
		}, new Runnable() {

			@Override
			public void run() {
				stopAnimation();
			}
		});
	}

	public void cancel() {
		CarouselEnterFolderHost host = getHost();
		host.getCarouselComponent().cancelPopulateFolderContentsAsync(new Runnable() {

			@Override
			public void run() {
				stopAnimation();
				host.notifyEnterFolderCancelled(CarouselEnterFolderAction.this);
			}
		});
	}

	@Override
	protected CarouselEnterFolderHost getHost() {
		return (CarouselEnterFolderHost) super.getHost();
	}

	public FolderNode getFolderNode() {
		return folderNode;
	}

	public Node getChildNodeInFocus() {
		return childNodeInFocus;
	}

}