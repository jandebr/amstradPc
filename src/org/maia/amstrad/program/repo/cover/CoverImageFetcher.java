package org.maia.amstrad.program.repo.cover;

import java.util.Queue;

import org.maia.amstrad.program.repo.cover.CoverImageImpl.FetchTask;
import org.maia.amstrad.util.AsyncSerialTaskWorker;

public class CoverImageFetcher extends AsyncSerialTaskWorker<FetchTask> {

	private static CoverImageFetcher instance;

	private CoverImageFetcher() {
		super("Cover image fetcher");
	}

	@Override
	protected void addTaskToQueue(FetchTask task, Queue<FetchTask> queue) {
		FetchTask currentTask = queue.peek();
		queue.clear(); // discard any backlog
		if (currentTask != null) {
			queue.add(currentTask); // likely this task is in progress
		}
		if (currentTask == null || !task.getNode().equals(currentTask.getNode())) {
			queue.add(task);
			// System.out.println("Queued fetching cover image for " + task.getNode().getName());
		}
	}

	public static CoverImageFetcher getInstance() {
		if (instance == null) {
			setInstance(new CoverImageFetcher());
		}
		return instance;
	}

	private static synchronized void setInstance(CoverImageFetcher fetcher) {
		if (instance == null) {
			instance = fetcher;
			instance.start();
		}
	}

}