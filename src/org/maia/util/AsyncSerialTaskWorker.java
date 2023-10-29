package org.maia.util;

import java.util.LinkedList;
import java.util.Queue;

import org.maia.util.AsyncSerialTaskWorker.AsyncTask;

public class AsyncSerialTaskWorker<T extends AsyncTask> extends Thread {

	private boolean stop;

	private Queue<T> taskQueue;

	public AsyncSerialTaskWorker(String threadName) {
		super(threadName);
		setDaemon(true);
		this.taskQueue = new LinkedList<T>();
	}

	@Override
	public synchronized final void run() {
		System.out.println("Thread '" + getName() + "' started");
		while (!isStopped()) {
			T task = null;
			synchronized (getTaskQueue()) {
				task = getTaskQueue().peek();
			}
			boolean shouldWait = task == null;
			if (task != null) {
				processTask(task);
				synchronized (getTaskQueue()) {
					getTaskQueue().remove(task);
					shouldWait = getTaskQueue().isEmpty();
				}
			}
			if (shouldWait) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		}
		System.out.println("Thread '" + getName() + "' stopped");
	}

	public final void addTask(T task) {
		boolean shouldNotify = false;
		synchronized (getTaskQueue()) {
			boolean wasEmpty = getTaskQueue().isEmpty();
			addTaskToQueue(task, getTaskQueue());
			shouldNotify = wasEmpty && !getTaskQueue().isEmpty();
		}
		if (shouldNotify) {
			synchronized (this) {
				notify();
			}
		}
	}

	protected void addTaskToQueue(T task, Queue<T> queue) {
		queue.add(task);
	}

	protected void processTask(T task) {
		task.process();
	}

	public void stopProcessing() {
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}

	private Queue<T> getTaskQueue() {
		return taskQueue;
	}

	public static interface AsyncTask {

		void process();

	}

}