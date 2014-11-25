package com.alcatel.smartlinkv3.samba;

import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.samba.SmbUtils.LIST_FILE_MODEL;

import android.os.Handler;

public class SmbListFilesTask extends Thread {
	public class ListFilesResult {

		public ArrayList<FileItem> mFiles;
		public String mParentPath;

		public ListFilesResult() {
			mFiles = new ArrayList<FileItem>();
			mParentPath = new String();
		}
	}

	private LIST_FILE_MODEL mListModel;
	private String mPath;
	private Handler mHandler;
	private ListFilesResult mRes;

	public SmbListFilesTask(String path, ArrayList<FileItem> files,
			LIST_FILE_MODEL model, Handler handler) {
		HttpAccessLog.getInstance().writeLogToFile(
				"smaba  SmbListFilesTask: path = " + path);
		mPath = path;
		mListModel = model;
		mHandler = handler;
		mRes = new ListFilesResult();
	}

	@Override
	public void run() {

		SmbFile smbFile;
		try {
			smbFile = new SmbFile(mPath, SmbUtils.AUTH);
			mRes.mParentPath = smbFile.getParent();

			SmbFile[] fs;
			try {

				fs = smbFile.listFiles();
				for (SmbFile f : fs) {
					addFiles(f);
				}
				onFinish();
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile(
						"Samba error: listfiles: " + e.getMessage()
								+ "   path: " + mPath);

				HttpAccessLog.getInstance()
						.writeLogToFile(
								"Samba error: listfiles: netstatus: "
										+ e.getNtStatus());
				
				Throwable t = e.getRootCause();				
				if(t != null)
					HttpAccessLog.getInstance().writeLogToFile(
							"Samba error: listfiles: getRootCause "
									+ t.toString());
				e.printStackTrace();
				onError(e.getMessage());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			HttpAccessLog.getInstance().writeLogToFile(
					"Samba error: listfiles: " + e.getMessage() + "   path: "
							+ mPath);
			onError(e.getMessage());
		}
	}

	private void addFiles(SmbFile file) {
		try {
			if (!file.isHidden()) {
				FileItem item = new FileItem();
				item.name = FileUtils.trimLastFileSeparator(file.getName());
				item.path = file.getPath();
				try {
					item.isDir = file.isDirectory();
				} catch (SmbException e) {
					HttpAccessLog.getInstance().writeLogToFile(
							"Samba error: listfiles: " + e.getMessage());
					HttpAccessLog.getInstance().writeLogToFile(
							"Samba error: listfiles: netstatus: "
									+ e.getNtStatus());
					Throwable t = e.getRootCause();				
					if(t != null)
						HttpAccessLog.getInstance().writeLogToFile(
								"Samba error: listfiles: getRootCause "
										+ t.toString());
					e.printStackTrace();
				}

				switch (mListModel) {
				case ALL_FILE:
					mRes.mFiles.add(item);
					break;

				case DIR_ONLY:
					if (item.isDir) {
						mRes.mFiles.add(item);
					}
					break;

				case FILE_ONLY:
					if (!item.isDir) {
						mRes.mFiles.add(item);
					}
					break;

				default:
					break;
				}
			}

		} catch (SmbException e) {
			HttpAccessLog.getInstance().writeLogToFile(
					"Samba error: listfiles: " + e.getMessage());
			HttpAccessLog.getInstance().writeLogToFile(
					"Samba error: listfiles: netstatus: " + e.getNtStatus());
			Throwable t = e.getRootCause();				
			if(t != null)
				HttpAccessLog.getInstance().writeLogToFile(
						"Samba error: listfiles: getRootCause "
								+ t.toString());
			e.printStackTrace();
		}
	}

	private void onFinish() {
		mHandler.obtainMessage(SmbUtils.SMB_MSG_TASK_FINISH, mRes)
				.sendToTarget();
	}

	private void onError(String strErr) {
		mHandler.obtainMessage(SmbUtils.SMB_MSG_TASK_ERROR, strErr)
				.sendToTarget();
	}
}