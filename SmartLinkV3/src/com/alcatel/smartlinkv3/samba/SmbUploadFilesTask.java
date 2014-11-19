package com.alcatel.smartlinkv3.samba;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;

public class SmbUploadFilesTask extends Thread {
	private ArrayList<FileItem> mFiles;
	private String mSmbDir;
	private Context mContext;
	private String mSmbTemp;
	private boolean mError;
	private boolean mCancelled;
	private String mCancelFile;

	public SmbUploadFilesTask(ArrayList<FileItem> files, String smbDir,
			Context context) {
		mFiles = files;
		mSmbDir = smbDir;
		mContext = context;
		mSmbTemp = FileUtils.combinePath(mSmbDir, SmbUtils.SMB_TEMP_DIR);
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
		SmbUtils.deleteFile(mSmbTemp);
		SmbUtils.createDir(mSmbTemp);

		for (int i = 0; i < mFiles.size(); i++) {
			uploadFiles(mFiles.get(i).path, mSmbTemp);
		}
		SmbUtils.deleteFile(mSmbTemp);
	}

	private void uploadFiles(String localFile, String smbDir) {
		if (!mError) {
			File file = new File(localFile);
			if (file.isFile()) {
				uploadFile(file, smbDir);
			} else if (file.isDirectory()) {
				String smbDirTmp = FileUtils
						.combinePath(smbDir, file.getName());
				SmbUtils.createDir(smbDirTmp);
				String dstPath = getDstPathByTemp(smbDirTmp);
				File[] files = file.listFiles();

				// start for UI fresh
				if (SmbUtils.createDir(dstPath)) {
					onFinish(dstPath);
				} else {
					onError(dstPath);
				}

				for (File f : files) {
					uploadFiles(f.getAbsolutePath(), smbDirTmp);
				}

				// end for UI fresh
				if (SmbUtils.createDir(dstPath)) {
					onFinish(dstPath);
				} else {
					onError(dstPath);
				}
			}
		}
	}

	private void uploadFile(File localFile, String smbDir) {
		FileInputStream in = null;
		SmbFileOutputStream out = null;
		String fileName = localFile.getName();
		long totalSize = localFile.length();
		String tmpPath = FileUtils.combinePath(smbDir, fileName);
		String dstPath = getDstPathByTemp(tmpPath);
		onUpdate(dstPath, 0);
		try {
			SmbFile smbTmpFile = new SmbFile(tmpPath, SmbUtils.AUTH);
			in = new FileInputStream(localFile);
			out = new SmbFileOutputStream(smbTmpFile);
			byte[] buffer = new byte[SmbUtils.BUFFER_SIZE];
			long size = 0;
			while (in.read(buffer) != -1) {
				out.write(buffer);
				size += SmbUtils.BUFFER_SIZE;
				if (size % SmbUtils.UPDATE_BUFFER_SIZE == 0)
					onUpdate(dstPath,
							SmbUtils.getPercentNumber(size, totalSize));

				if (mCancelled && mCancelFile.equalsIgnoreCase(dstPath)) {
					Log.e("upload cancel", mCancelFile);
					break;
				}
			}
			if (mCancelled && mCancelFile.equalsIgnoreCase(dstPath)) {
				Log.e("upload cancel", "sucessful");
				mCancelled = false;
				onFinish(dstPath);
			} else {
				if (copyFile(smbTmpFile, dstPath)) {
					onFinish(dstPath);
				} else {
					onError(dstPath);
				}
			}

		} catch (Exception e) {
			HttpAccessLog.getInstance().writeLogToFile("Samba error: uploadfiles: "+ e.getMessage());
			onError(dstPath);
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					HttpAccessLog.getInstance().writeLogToFile("Samba error: uploadfiles: "+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					HttpAccessLog.getInstance().writeLogToFile("Samba error: uploadfiles: "+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private String getDstPathByTemp(String strTemp) {
		String dstPath = mSmbDir;
		if (strTemp.indexOf(mSmbTemp) == 0) {
			dstPath = FileUtils.combinePath(mSmbDir,
					strTemp.substring(mSmbTemp.length()));
		}
		return dstPath;
	}

	private boolean copyFile(SmbFile src, String dst) {
		boolean bRes = false;

		String parentPath = FileUtils.getParent(dst);
		SmbFile parentFile;
		try {
			parentFile = new SmbFile(parentPath, SmbUtils.AUTH);
			try {
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				SmbFile dstFile = new SmbFile(dst, SmbUtils.AUTH);	
				if(dstFile.exists())
				{
					dstFile.delete();
				}
				src.renameTo(dstFile);			
				bRes = true;
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile("Samba error: uploadfiles: "+ e.getMessage());
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			HttpAccessLog.getInstance().writeLogToFile("Samba error: uploadfiles: "+ e.getMessage());
			e.printStackTrace();
		}

		return bRes;
	}

	private void onFinish(String path) {
		Intent intent = new Intent(SmbUtils.SMB_MSG_UPLOAD_FILES_FINISH);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PATH, path);
		mContext.sendBroadcast(intent);
	}

	private void onError(String path) {
		mError = true;
		Intent intent = new Intent(SmbUtils.SMB_MSG_UPLOAD_FILES_ERROR);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PATH, path);
		mContext.sendBroadcast(intent);
	}

	private void onUpdate(String path, int progress) {
		Intent intent = new Intent(SmbUtils.SMB_MSG_UPLOAD_FILES_UPDATE);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PATH, path);
		intent.putExtra(SmbUtils.SMB_OPT_FILES_PROGRESS, progress);
		mContext.sendBroadcast(intent);
	}
}