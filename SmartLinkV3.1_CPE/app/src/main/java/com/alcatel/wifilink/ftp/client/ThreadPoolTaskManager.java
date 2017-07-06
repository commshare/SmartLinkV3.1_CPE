package com.alcatel.wifilink.ftp.client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTaskManager {
	private static final String TAG = "ThreadPoolTaskManager";

	// task lists
	private LinkedList<ThreadPoolTask> taskList;

	private Set<String> taskIdSet;

	private static ThreadPoolTaskManager s_TaskMananger;

	private Thread poolThread;

	private pubLog logger;
	
	private ThreadPool pool;

	public ThreadPoolTaskManager() {
		taskList = new LinkedList<ThreadPoolTask>();
		taskIdSet = new HashSet<String>();
		pool = new ThreadPool();
		poolThread = new Thread(pool);
		logger = pubLog.getLogger();
	}

	public void start() {
		poolThread.start();

		int time = 0;
		while (!poolThread.isAlive()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (time >= 10) {
				poolThread.start();
			}
			time++;
		}
	}

	public void stop() {
		if (!poolThread.isAlive()) {
			pool.isRun = false;
			return;
		}
		pool.isRun = false;
		poolThread.stop();
	}

	public static synchronized ThreadPoolTaskManager getInstance() {
		if (s_TaskMananger == null) {
			s_TaskMananger = new ThreadPoolTaskManager();
		}
		return s_TaskMananger;
	}

	public synchronized boolean addTask(ThreadPoolTask task) {
		synchronized (taskList) {
			if (!isTaskRepeat(task.getFileId())) {
				System.out.println("ThreadPool: add task name = "
						+ task.getFileId());
				taskList.addLast(task);
				return true;
			} else {
				// TODO
				System.out.println("ThreadPool:task [" + task.getFileId()
						+ "] is repeated");
				taskList.addLast(task);
				return false;
			}
		}

	}
	
	public synchronized boolean removeTask(ThreadPoolTask task) {
		synchronized (taskList) {
			if (task == null) {
				return false;
			}

			if (taskList.contains(task)) {
				taskList.remove(task);
			}

		}
		return false;
	}

	// TODO
	public boolean isTaskRepeat(String fileId) {
		synchronized (taskIdSet) {
			if (taskIdSet.contains(fileId)) {
				return true;
			} else {
				System.out.println("ThreadPool 2: add task name = " + fileId);
				taskIdSet.add(fileId);
				return false;
			}
		}
	}

	public ThreadPoolTask getTask() {
		synchronized (taskList) {
			if (taskList.size() > 0) {
				ThreadPoolTask firstTask = taskList.removeFirst();
				return firstTask;
			}
		}
		return null;
	}

	private class ThreadPool implements Runnable {
		private ExecutorService mPool;

		private final int POOL_SIZE = 5;

		private final int SLEEP_TIME = 1000;

		private boolean isRun = true;

		public ThreadPool() {
			mPool = Executors.newFixedThreadPool(POOL_SIZE);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRun) {
				ThreadPoolTask task = getTask();
				if (task != null) {
					mPool.execute(task);
				} else {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			if (!isRun) {
				// not destroy the thread,it forbid add the tasks
				mPool.shutdown();
			}

		}

		// immediately close the thread pool
		public synchronized void shutdown() {
			if (mPool != null && (!mPool.isShutdown() || mPool.isTerminated())) {
				mPool.shutdownNow();
				this.isRun = false;
			}
		}

		// gently close the single task thread pool,but will ensure that all
		// have to join the task will be completed before closing
		public void stop() {
			if (mPool != null && (!mPool.isShutdown() || mPool.isTerminated())) {
				mPool.shutdown();
				this.isRun = false;
			}
		}

	}
}
