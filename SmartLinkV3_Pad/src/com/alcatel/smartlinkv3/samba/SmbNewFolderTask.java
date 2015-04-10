package com.alcatel.smartlinkv3.samba;

import java.net.MalformedURLException;

import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;

import android.os.Handler;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SmbNewFolderTask extends Thread {
	private String mPath;
	private Handler mHandler;

	public SmbNewFolderTask(String path, Handler handler) {
		mPath = FileUtils.addLastFileSeparator(path);
		mHandler = handler;
	}

	@Override
	public void run() {
		try {

			SmbFile smbFile = new SmbFile(mPath, SmbUtils.AUTH);
			try {
				if (smbFile.exists()) {
					onExists();
				} else {
					smbFile.mkdirs();
				}
				onFinish();
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile(
						"Samba error: newfolder: " + e.getMessage());
				e.printStackTrace();
				if (SmbError.SMB_ERR_DISK_FULL == e.getNtStatus()) {
					onDiskFull();
				} else {
					onError();
				}
			}
		} catch (MalformedURLException e) {
			HttpAccessLog.getInstance().writeLogToFile(
					"Samba error: newfolder: " + e.getMessage());
			e.printStackTrace();
			onError();
		}
	}

	private void onFinish() {
		mHandler.obtainMessage(SmbUtils.SMB_MSG_TASK_FINISH, null)
				.sendToTarget();
	}

	private void onError() {
		mHandler.obtainMessage(SmbUtils.SMB_MSG_TASK_ERROR, null)
				.sendToTarget();
	}

	private void onExists() {
		mHandler.obtainMessage(SmbUtils.SMB_MSG_FILE_EXISTS, null)
				.sendToTarget();
	}

	private void onDiskFull() {
		mHandler.obtainMessage(SmbUtils.SMB_MSG_DISK_FULL, null).sendToTarget();
	}
}