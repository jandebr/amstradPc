package org.maia.amstrad.gui.browser.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.maia.amstrad.gui.browser.carousel.action.CarouselEnterFolderAction;
import org.maia.amstrad.gui.browser.carousel.api.CarouselHost;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselItemMaker;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.gui.browser.carousel.item.CarouselRepositoryItem;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;
import org.maia.swing.animate.itemslide.SlidingCursorMovement;
import org.maia.swing.animate.itemslide.SlidingItemListComponent;
import org.maia.swing.animate.itemslide.outline.SlidingItemListOutlineView;
import org.maia.util.AsyncSerialTaskWorker;
import org.maia.util.AsyncSerialTaskWorker.AsyncTask;

public class CarouselComponent extends SlidingItemListComponent {

	private CarouselHost host;

	private CarouselItemMaker itemMaker;

	private FolderNode folderNode;

	private static CarouselPopulationTaskWorker carouselPopulationTaskWorker;

	static {
		carouselPopulationTaskWorker = new CarouselPopulationTaskWorker();
		carouselPopulationTaskWorker.start();
	}

	public CarouselComponent(Dimension size, Insets padding, Color background, SlidingCursorMovement cursorMovement,
			CarouselHost host, CarouselItemMaker itemMaker) {
		super(size, padding, background, cursorMovement);
		this.host = host;
		this.itemMaker = itemMaker;
	}

	public void populateFolderContentsAsync(FolderNode folderNode, Node childNodeInFocus, Runnable callbackOnComplete,
			Runnable callbackOnAbort) {
		getCarouselPopulationTaskWorker()
				.addTask(new CarouselPopulationTask(folderNode, childNodeInFocus, callbackOnComplete, callbackOnAbort));
	}

	public void cancelPopulateFolderContentsAsync(Runnable callback) {
		getCarouselPopulationTaskWorker().addTask(new CarouselCancelPopulationTask(callback));
	}

	protected ProgramNode selectFeaturedProgramNode(FolderNode folderNode) {
		ProgramNode featured = folderNode.getFeaturedProgramNode();
		if (featured == null) {
			ProgramNode winnerWithCoverImage = null;
			ProgramNode winnerWithoutCoverImage = null;
			int winnerWithCoverImageBlocksOnTape = -1;
			int winnerWithoutCoverImageBlocksOnTape = -1;
			for (Node node : folderNode.getChildNodes()) {
				if (node.isProgram()) {
					ProgramNode programNode = node.asProgram();
					int blocks = programNode.getProgram().getBlocksOnTape();
					if (programNode.getCoverImage() != null) {
						if (blocks > winnerWithCoverImageBlocksOnTape) {
							winnerWithCoverImageBlocksOnTape = blocks;
							winnerWithCoverImage = programNode;
						}
					} else {
						if (blocks > winnerWithoutCoverImageBlocksOnTape) {
							winnerWithoutCoverImageBlocksOnTape = blocks;
							winnerWithoutCoverImage = programNode;
						}
					}
				}
			}
			if (winnerWithCoverImage != null) {
				featured = winnerWithCoverImage;
			} else if (winnerWithoutCoverImage != null) {
				featured = winnerWithoutCoverImage;
			}
		}
		return featured;
	}

	public CarouselOutline createOutline(int thickness) {
		return new CarouselOutline(this, thickness);
	}

	public CarouselItem getItem(Node node) {
		for (int i = 0; i < getItemCount(); i++) {
			CarouselItem item = getItem(i);
			if (item instanceof CarouselRepositoryItem) {
				if (((CarouselRepositoryItem) item).getRepositoryNode().equals(node)) {
					return item;
				}
			}
		}
		return null;
	}

	@Override
	public CarouselItem getItem(int index) {
		return (CarouselItem) super.getItem(index);
	}

	@Override
	public CarouselItem getSelectedItem() {
		return (CarouselItem) super.getSelectedItem();
	}

	public CarouselHost getHost() {
		return host;
	}

	private CarouselItemMaker getItemMaker() {
		return itemMaker;
	}

	public FolderNode getFolderNode() {
		return folderNode;
	}

	private void setFolderNode(FolderNode folderNode) {
		this.folderNode = folderNode;
	}

	private static CarouselPopulationTaskWorker getCarouselPopulationTaskWorker() {
		return carouselPopulationTaskWorker;
	}

	public static class CarouselOutline extends SlidingItemListOutlineView {

		private CarouselOutline(CarouselComponent component, int thickness) {
			super(component, thickness, component.getBackground());
		}

		@Override
		public CarouselComponent getComponent() {
			return (CarouselComponent) super.getComponent();
		}

	}

	private class CarouselPopulationTask implements AsyncTask {

		private FolderNode folderNodeToPopulate;

		private Node childNodeInFocus;

		private int childNodeIndexInFocus = -1;

		private Runnable callbackOnComplete;

		private Runnable callbackOnAbort;

		private boolean aborted;

		public CarouselPopulationTask(FolderNode folderNode, Node childNodeInFocus, Runnable callbackOnComplete) {
			this(folderNode, childNodeInFocus, callbackOnComplete, null);
		}

		public CarouselPopulationTask(FolderNode folderNode, Node childNodeInFocus, Runnable callbackOnComplete,
				Runnable callbackOnAbort) {
			this.folderNodeToPopulate = folderNode;
			this.childNodeInFocus = childNodeInFocus;
			this.callbackOnComplete = callbackOnComplete;
			this.callbackOnAbort = callbackOnAbort;
		}

		public void abort() {
			setAborted(true);
		}

		@Override
		public void process() {
			List<CarouselItem> items = collectItems();
			if (isAborted()) {
				runCallback(getCallbackOnAbort());
			} else {
				// point of no return
				handleAnimation();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						setFolderNode(getFolderNodeToPopulate());
						removeAllItems();
						for (CarouselItem item : items) {
							addItem(item);
						}
						if (getChildNodeIndexInFocus() >= 0) {
							validateLayout();
							moveToItemIndex(getChildNodeIndexInFocus());
						}
						runCallback(getCallbackOnComplete());
					}
				});
			}
		}

		private List<CarouselItem> collectItems() {
			List<CarouselItem> items = new Vector<CarouselItem>();
			CarouselComponent carousel = CarouselComponent.this;
			FolderNode folderNode = getFolderNodeToPopulate();
			if (folderNode.isEmpty()) {
				items.add(getItemMaker().createCarouselItemForEmptyFolder(carousel));
			} else {
				for (Node node : folderNode.getChildNodes()) {
					if (isAborted())
						break;
					if (node.equals(getChildNodeInFocus())) {
						setChildNodeIndexInFocus(items.size());
					}
					if (node.isFolder()) {
						FolderNode childFolderNode = node.asFolder();
						ProgramNode featuredProgramNode = selectFeaturedProgramNode(childFolderNode);
						items.add(getItemMaker().createCarouselItemForFolder(childFolderNode, featuredProgramNode,
								carousel));
					} else {
						ProgramNode childProgramNode = node.asProgram();
						CarouselProgramItem item = getItemMaker().createCarouselItemForProgram(childProgramNode,
								carousel);
						item.setPreviousRunFailed(getHost().isFailedToRun(childProgramNode));
						items.add(item);
					}
				}
			}
			return items;
		}

		private void handleAnimation() {
			CarouselEnterFolderAction action = getHost().getEnterFolderActionInProgress();
			if (action != null && action.getFolderNode().equals(getFolderNodeToPopulate())) {
				if (action.isAnimationStarted()) {
					action.sleepCurrentThreadUntilMinimumAnimationDuration();
				} else {
					action.suppressAnimation();
				}
			}
		}

		protected void runCallback(Runnable callback) {
			if (callback != null) {
				callback.run();
			}
		}

		private FolderNode getFolderNodeToPopulate() {
			return folderNodeToPopulate;
		}

		private Node getChildNodeInFocus() {
			return childNodeInFocus;
		}

		private int getChildNodeIndexInFocus() {
			return childNodeIndexInFocus;
		}

		private void setChildNodeIndexInFocus(int index) {
			this.childNodeIndexInFocus = index;
		}

		protected Runnable getCallbackOnComplete() {
			return callbackOnComplete;
		}

		protected Runnable getCallbackOnAbort() {
			return callbackOnAbort;
		}

		private boolean isAborted() {
			return aborted;
		}

		private void setAborted(boolean aborted) {
			this.aborted = aborted;
		}

	}

	private class CarouselCancelPopulationTask extends CarouselPopulationTask {

		public CarouselCancelPopulationTask(Runnable callback) {
			super(null, null, callback);
		}

		@Override
		public void process() {
			// adding this task will abort the one in progress and clear any backlog
			runCallback(getCallbackOnComplete());
		}

	}

	/**
	 * Keeps a backlog of at most 1 population task (most recently added) + at most 1 task in progress
	 */
	private static class CarouselPopulationTaskWorker extends AsyncSerialTaskWorker<CarouselPopulationTask> {

		public CarouselPopulationTaskWorker() {
			super("Carousel population task worker");
		}

		@Override
		protected void addTaskToQueue(CarouselPopulationTask task, Queue<CarouselPopulationTask> queue) {
			CarouselPopulationTask currentTask = queue.peek();
			queue.clear(); // discard any backlog
			queue.add(task);
			if (currentTask != null) {
				currentTask.abort(); // abort the task in progress
			}
		}

	}

}