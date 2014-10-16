package com.alcatel.smartlinkv3.samba;

import java.net.MalformedURLException;
import java.util.ArrayList;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;

import android.os.Handler;

public class SmbMoveFilesTask extends Thread {
	private ArrayList<String> mFiles;
	private String mDstDir;
	private Handler mHandler;

	public SmbMoveFilesTask(ArrayList<String> files, String dstDir,
			Handler handler) {
		mFiles = files;
		mDstDir = dstDir;
		mHandler = handler;
	}

	@Override
	public void run() {		
		for (int i = 0; i < mFiles.size(); i++) {			
			moveFile(mFiles.get(i), mDstDir);						
		}

		onFinish();
	}
	
	private void moveFile(String smbPath, String dstDir) {		
		SmbFile smbfile;
		try {
			smbfile = new SmbFile(smbPath, SmbUtils.AUTH);
			String dstFile = FileUtils.combinePath(dstDir, smbfile.getName());	
			SmbFile smbDstFile = new SmbFile(dstFile, SmbUtils.AUTH);	
			try {
				smbfile.renameTo(smbDstFile);
			} catch (SmbException e) {	
				HttpAccessLog.getInstance().writeLogToFile("Samba error: moveFiles: "+ e.getMessage());
				e.printStackTrace();
				onError();
			}
		} catch (MalformedURLException e) {		
			HttpAccessLog.getInstance().writeLogToFile("Samba error: moveFiles: "+ e.getMessage());
			e.printStackTrace();
			onError();
		}		
	}
	
	private void onFinish()
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_TASK_FINISH, null)
				.sendToTarget();		
	}
	
	private void onError()
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_TASK_ERROR, null)
				.sendToTarget();	
	}	
}