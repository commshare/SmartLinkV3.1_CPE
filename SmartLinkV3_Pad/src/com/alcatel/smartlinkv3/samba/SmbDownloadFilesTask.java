package com.alcatel.smartlinkv3.samba;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;

public class SmbDownloadFilesTask extends Thread {

	private ArrayList<FileItem> mFiles;
	private String mLocalPath;
	private Context mContext;
	private String mLocalTemp;
	private boolean mError;
	private boolean mCancelled;
	private String mCancelFile;

	public SmbDownloadFilesTask(ArrayList<FileItem> files, Context context) {
		mFiles = files;
		mContext = context;
		mLocalPath = CPEConfig.getInstance().getDefaultDir();
		mLocalTemp = FileUtils.combinePath(mLocalPath, SmbUtils.LOCAL_TEMP_DIR);
		mError = false;
		mCancelled = false;
		mCancelFile = new String();
	}

	public void setCancel(boolean bCancel, String path) {
		mCancelled = bCancel;
		mCancelFile = path;
	}

	@Override
	public void run() {
		FileUtils.deleteFile(mLocalTemp);
		FileUtils.createDir(mLocalTemp);

		for (int i = 0; i < mFiles.size(); i++) {			
			downloadFiles(mFiles.get(i).path, mLocalTemp);			
		}
		FileUtils.deleteFile(mLocalTemp);
	}

	private void downloadFiles(String smbPath, String localDir) {
		if (!mError) {
		SmbFile file;
		try {
			file = new SmbFile(smbPath, SmbUtils.AUTH);
			try {
				if (file.isFile()) {
					downloadFile(file, localDir);
				} else
					try {
						if (file.isDirectory()) {
							String localDirTmp = FileUtils.combinePath(
									localDir, file.getName());
							FileUtils.createDir(localDirTmp);
							String dstPath = getDstPathByTemp(localDirTmp);

							// first fresh
							if (FileUtils.createDir(dstPath)) {
								onFinish(dstPath);
							} else {
								onError(dstPath);
							}

							SmbFile[] files = file.listFiles();
							for (SmbFile f : files) {
								downloadFiles(f.getPath(), localDirTmp);
							}

							// end for UI fresh
							if (FileUtils.createDir(dstPath)) {
								onFinish(dstPath);
							} else {
								onError(dstPath);
							}
						}
					} catch (SmbException e) {
						HttpAccessLog.getInstance().writeLogToFile("Samba error: downloadFiles: "+ e.getMessage());
						e.printStackTrace();
					}
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile("Samba error: downloadFiles: "+ e.getMessage());
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			HttpAccessLog.getInstance().writeLogToFile("Samba error: downloadFiles: "+ e1.getMessage());
			e1.printStackTrace();
		}
		}
	}

	private void downloadFile(SmbFile smbFile, String localDir) {		
		SmbFileInputStream in = null;
		FileOutputStream out = null;
		String fileName = smbFile.getName();
		String tmpPath = FileUtils.combinePath(localDir, fileName);
		String dstPath = getDstPathByTemp(tmpPath);
		onUpdate(dstPath, 0);

		try {
			long totalSize = smbFile.length();
			in = new SmbFileInputStream(smbFile);
			out = new FileOutputStream(tmpPath);
			byte[] buffer = new byte[SmbUtils.BUFFER_SIZE];
			int len = -1;
			long size = 0;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				size += SmbUtils.BUFFER_SIZE;
				if (size % SmbUtils.UPDATE_BUFFER_SIZE == 0)
					onUpdate(dstPath,
							SmbUtils.getPercentNumber(size, totalSize));

				if (mCancelled && mCancelFile.equalsIgnoreCase(dstPath)) {
					Log.e("download cancel", mCancelFile);
					break;
				}
			}
			if (mCancelled && mCancelFile.equalsIgnoreCase(dstPath)) {
				Log.e("download cancel", "sucessful");
				mCancelled = false;
				onFinish(dstPath);
			} else {
				if (copyFile(tmpPath, dstPath)) {
					onFinish(dstPath);
				} else {
					onError(dstPath);
				}
			}

		} catch (Exception e) {
			onError(dstPath);
			e.printStackTrace();
			HttpAccessLog.getInstance().writeLogToFile("Samba error: downloadFiles: "+ e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					HttpAccessLog.getInstance().writeLogToFile("Samba error: downloadFiles: "+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					HttpAccessLog.getInstance().writeLogToFile("Samba error: downloadFiles: "+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private String getDstPathByTemp(String strTemp) {
		String dstPath = mLocalPath;
		if (strTemp.indexOf(mLocalTemp) == 0) {
			dstPath = FileUtils.combinePath(mLocalPath,
					strTemp.substring(mLocalTemp.length()));
		}
		return dstPath;
	}

	private boolean copyFile(String src, String dst) {
		String parentPath = FileUtils.getParent(dst);
		File parentFile = new File(parentPath);
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		File srcFile = new File(src);
		File dstFile = new File(dst);
		return srcFile.renameTo(dstFile);
	}

	private void onFinish(String path) {
		Intent intent = new Intent(SmbUtils.SMB_MSG_DOWNLOAD_FILES_FINISH);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PATH, path);
		mContext.sendBroadcast(intent);
	}

	private void onError(String path) {
		mError = true;
		Intent intent = new Intent(SmbUtils.SMB_MSG_DOWNLOAD_FILES_ERROR);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PATH, path);
		mContext.sendBroadcast(intent);
	}

	private void onUpdate(String path, int progress) {
		Intent intent = new Intent(SmbUtils.SMB_MSG_DOWNLOAD_FILES_UPDATE);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PATH, path);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PROGRESS, progress);
		mContext.sendBroadcast(intent);
	}
}