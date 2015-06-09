package com.alcatel.smartlinkv3.fileexplorer;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

import com.alcatel.smartlinkv3.mediaplayer.proxy.GetMetaDataProxy;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;

class SyncMetaData {
    
    private static final String TAG = "MediaItem";
    
    private Activity mActivity;
    private Runnable mAction;
    private ArrayList<FileInfo> mFileList;
    private int count;
    private final int size;
    
    public SyncMetaData(ArrayList<FileInfo> fileList,
            Activity activity, Runnable action) {
        this.mAction = action;
        this.mActivity = activity;
        this.mFileList = fileList;
        this.count = 0;
        this.size = fileList.size();
    }
    
    private synchronized void incCount() {
        this.count++;
        if (this.count >= this.size) {
            mActivity.runOnUiThread(mAction);
        }
    }
    
    public void execute() {
        
        for (int i = 0; i < this.size; i++) {
            FileInfo info = mFileList.get(i);
            if (!FileIconHelper.isMediaFile(info.fileName)) {
                incCount();
                continue;
            }
            
            String uriString = Util.makePath(info.filePath, info.fileName);
            GetMetaDataProxy.syncGetMetaData(
                    this.mActivity, uriString, new MediaItemCallBack(info));
        }
    }
    
    class MediaItemCallBack implements GetMetaDataProxy.GetMetaDataRequestCallback {
        private final FileInfo mInfo;
        public MediaItemCallBack(FileInfo info) {
            this.mInfo = info;
        }
        
        @Override
        public void onGetItemMetaData(MediaItem item) {
            if(item != null) {
                Log.d(TAG, "item res is " + item.getRes());
                Log.d(TAG, "file name is " + mInfo.fileName);
                mInfo.item = item;
            } else
                Log.d(TAG, "GetMetaData failed!");
            incCount();
        }
    }
}