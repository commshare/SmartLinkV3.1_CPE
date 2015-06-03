package com.alcatel.smartlinkv3.ftp.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTaskManagerThread implements Runnable {

	private ThreadPoolTaskManager downloadTaskManager;

	private ExecutorService pool;

	private final int POOL_SIZE = 5;

	private final int SLEEP_TIME = 1000;

	private boolean isStop = false;

	public ThreadPoolTaskManagerThread() {
		downloadTaskManager = ThreadPoolTaskManager.getInstance();
		pool = Executors.newFixedThreadPool(POOL_SIZE);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!isStop) {
			ThreadPoolTask downloadTask = downloadTaskManager.getDownloadTask();
			if (downloadTask != null) {
				pool.execute(downloadTask);
			} else {
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		if (isStop) {
			pool.shutdown();
		}

	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

}