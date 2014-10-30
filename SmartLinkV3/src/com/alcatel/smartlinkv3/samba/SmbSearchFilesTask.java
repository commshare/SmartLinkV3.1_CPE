package com.alcatel.smartlinkv3.samba;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.alcatel.smartlinkv3.common.file.FileItem;
import com.alcatel.smartlinkv3.common.file.FileUtils;
import com.alcatel.smartlinkv3.httpservice.HttpAccessLog;
import com.alcatel.smartlinkv3.samba.SmbUtils.LIST_FILE_MODEL;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import android.os.Handler;

public class SmbSearchFilesTask extends Thread {
	private String mPath;
	private String mKey;
	private LIST_FILE_MODEL mListModel;
	private Handler mHandler;
	private ArrayList<FileItem> mFiles = new ArrayList<FileItem>();
	private ExecutorService mThreadPool;
	private boolean mCancelled;
	
	public SmbSearchFilesTask(String path, String key, LIST_FILE_MODEL model, Handler handler) {
		mPath = path;
		mKey = key;
		mListModel = model;
		mHandler = handler;
		mThreadPool = Executors.newFixedThreadPool(4);
		mCancelled = false;
	}
	
	public void setCancel(boolean bCancel) {
		mCancelled = bCancel;
	}

	@Override
	public void run() {		
		mFiles.clear();	
		if(!mCancelled)
			findFiles(mPath);		
	}

	private void findFiles(String path) {	
		if(mCancelled)
			return;
		SmbFile smbFile;
		try {
			smbFile = new SmbFile(path, SmbUtils.AUTH);
			SmbFile[] fs;
			try {
				fs = smbFile.listFiles();
				for (SmbFile f : fs) {								
					if (!f.isHidden()) {	
						if(mCancelled)
							return;
						mThreadPool.submit(new SearchFileRunnable(f));
					}
				}
				mThreadPool.shutdown();
				
				try {					
					while (!mThreadPool.awaitTermination(10, TimeUnit.SECONDS))
					{
						;					
					}	
					
					onFinish();
						
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile("Samba error: searchfiles: "+ e.getMessage());
				onError();
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			HttpAccessLog.getInstance().writeLogToFile("Samba error: searchfiles: "+ e.getMessage());
			onError();
			e.printStackTrace();
		}
	}

	private void addFiles(SmbFile file) {	
		
		if(mCancelled)
			return;
		
		String name = file.getName().toLowerCase();
		String key = mKey.toLowerCase();
		
		if (name.indexOf(key) != -1) {
			FileItem item = new FileItem();
			item.name = FileUtils.trimLastFileSeparator(file.getName());
			item.path = file.getPath();
			try {
				item.isDir = file.isDirectory();
			} catch (SmbException e) {
				HttpAccessLog.getInstance().writeLogToFile("Samba error: searchfiles: "+ e.getMessage());
				e.printStackTrace();
			}
			String parentDir = FileUtils
					.trimLastFileSeparator(file.getParent());
		//	item.parentDir = SmbUtils.trimSambaUrl(parentDir);		
		
			switch(mListModel)
			{
			case ALL_FILE:
				mFiles.add(item);	
				break;
				
			case DIR_ONLY:
				if(item.isDir)
				{
					mFiles.add(item);	
				}
				break;
				
			case FILE_ONLY:
				if(!item.isDir)
				{
					mFiles.add(item);	
				}
				break;
				
			default:
				break;				
			}	
			
			onUpdate();
			
		}
	}
	
	private void searchFile(SmbFile file)
	{	
		if(mCancelled)
			return;
		try {
			if (!file.isHidden()) {
				addFiles(file);
				if (file.isDirectory()) {
					SmbFile[] files = file.listFiles();
					for (SmbFile f : files) {
						searchFile(f);
					}
				}
			}
		} catch (SmbException e) {		
			HttpAccessLog.getInstance().writeLogToFile("Samba error: searchfiles: "+ e.getMessage());
			e.printStackTrace();
		}		
	}
	

	class SearchFileRunnable implements Runnable {

		private SmbFile mSmbFile;

		public SearchFileRunnable(SmbFile file) {
			mSmbFile = file;
		}

		@Override
		public void run() {
			searchFile(mSmbFile);
		}
	}
	
	private void onFinish()
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_TASK_FINISH, mFiles)
				.sendToTarget();		
	}
	
	private void onError()
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_TASK_ERROR, null)
				.sendToTarget();	
	}
	
	private void onUpdate()
	{
		if(mFiles.size() % 5 == 0)
		{
			mHandler.obtainMessage(
					SmbUtils.SMB_MSG_TASK_UPDATE, mFiles)
					.sendToTarget();
		}
	}
}