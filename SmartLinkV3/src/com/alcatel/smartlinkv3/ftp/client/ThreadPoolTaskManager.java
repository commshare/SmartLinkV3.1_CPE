package com.alcatel.smartlinkv3.ftp.client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import android.R.bool;

public class ThreadPoolTaskManager {
	private static final String TAG = "DownloadTaskManager";

	private LinkedList<ThreadPoolTask> downloadTasks;

	private Set<String> taskIdSet;

	private static ThreadPoolTaskManager downloadTaskMananger;

	private ThreadPoolTaskManager() {

		downloadTasks = new LinkedList<ThreadPoolTask>();
		taskIdSet = new HashSet<String>();

	}

	public static synchronized ThreadPoolTaskManager getInstance() {
		if (downloadTaskMananger == null) {
			downloadTaskMananger = new ThreadPoolTaskManager();
		}
		return downloadTaskMananger;
	}

	public boolean addDownloadTask(ThreadPoolTask downloadTask) {
		synchronized (downloadTasks) {
			if (!isTaskRepeat(downloadTask.getFileId())) {
				System.out.println("ThreadPool 1: add task name = "
						+ downloadTask.getFileId());
				downloadTasks.addLast(downloadTask);
				return true;
			} else {
				System.out.println("ThreadPool:task ["
						+ downloadTask.getFileId() + "] is repeated");
				return false;
			}
		}

	}

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

	public ThreadPoolTask getDownloadTask() {
		synchronized (downloadTasks) {
			if (downloadTasks.size() > 0) {
				ThreadPoolTask downloadTask = downloadTasks.removeFirst();
				return downloadTask;
			}
		}
		return null;
	}
}
