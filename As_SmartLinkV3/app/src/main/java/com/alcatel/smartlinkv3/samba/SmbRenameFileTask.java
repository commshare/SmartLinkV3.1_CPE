package com.alcatel.smartlinkv3.samba;

import java.net.MalformedURLException;

import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.os.Handler;

public class SmbRenameFileTask extends Thread {
	private String mPath;
	private String mName;
	private Handler mHandler;

	public SmbRenameFileTask(String path, String name, Handler handler) {
		mPath = path;
		mName = name;
		mHandler = handler;
	}

	@Override
	public void run() {				
		try {			
			SmbFile srcFile = new SmbFile(mPath, SmbUtils.AUTH);
			String dstPath = FileUtils.combinePath(srcFile.getParent(), mName);
			SmbFile dstFile = new SmbFile(dstPath, SmbUtils.AUTH);	
			try {
				if(dstFile.exists())
				{
					onExists();
				}
				else
				{
					srcFile.renameTo(dstFile);	
				}
				
				onFinish();
				
			} catch (SmbException e) {	
				HttpAccessLog.getInstance().writeLogToFile("Samba error: renamefile: "+ e.getMessage());
				onError();
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {	
			HttpAccessLog.getInstance().writeLogToFile("Samba error: renamefile: "+ e.getMessage());
			onError();
			e.printStackTrace();
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
	
	private void onExists()
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_FILE_EXISTS, null)
				.sendToTarget();		
	}	

}