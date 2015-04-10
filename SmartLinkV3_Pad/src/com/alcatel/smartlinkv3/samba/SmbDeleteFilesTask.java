package com.alcatel.smartlinkv3.samba;

import java.util.ArrayList;

import com.alcatel.smartlinkv3.common.file.FileItem;

import android.os.Handler;

public class SmbDeleteFilesTask extends Thread {
	
	public class DeleteResult {
		
		public int succeeded;
		public int failed;
	
		public DeleteResult() {			
			succeeded = 0;
			failed = 0;		
		}
	}

	private ArrayList<FileItem> mFiles;
	private Handler mHandler;
	private DeleteResult mRes;
	public SmbDeleteFilesTask(ArrayList<FileItem> files, Handler handler) {
		mFiles = files;		
		mHandler = handler;
		mRes = new DeleteResult();
	}

	@Override
	public void run() {		
		for(int i = 0;i < mFiles.size();i++) {
			boolean bChecked = mFiles.get(i).isSelected;
			if(bChecked) {				
				if(SmbUtils.deleteFile(mFiles.get(i).path))
					mRes.succeeded++;
				else
					mRes.failed++;
			}
		}			
		onFinish();	
	}
	
	private void onFinish()
	{
		mHandler.obtainMessage(
				SmbUtils.SMB_MSG_TASK_FINISH, mRes)
				.sendToTarget();		
	}	

}