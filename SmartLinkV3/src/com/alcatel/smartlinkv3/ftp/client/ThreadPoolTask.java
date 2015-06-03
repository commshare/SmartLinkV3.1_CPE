package com.alcatel.smartlinkv3.ftp.client;

import com.alcatel.smartlinkv3.fileexplorer.FtpFileCommandTask.OnCallResponse;

public class ThreadPoolTask implements Runnable {
	public String name;

	private TaskPoolOnCallResponse mOnCallResponse;

	public ThreadPoolTask(String name, TaskPoolOnCallResponse onCallResponse) {
		this.name = name;
		this.mOnCallResponse = onCallResponse;
	}

	public void setOnCallResponse(TaskPoolOnCallResponse response) {
		this.mOnCallResponse = response;
	}

	public interface TaskPoolOnCallResponse {
		void taskCallResponse(Object obj);
	}

	public TaskPoolOnCallResponse getOnCallResponse() {
		return this.mOnCallResponse;
	}

	@Override
	public void run() {
		// String name=Thread.currentThread().getName();
	/*	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		System.out.println("ftp-" + name + " executed OK!");
		mOnCallResponse.taskCallResponse(null);
	}

	public String getFileId() {
		return name;
	}

}
