package com.alcatel.smartlinkv3.fileexplorer;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.util.Log;

import com.alcatel.smartlinkv3.mediaplayer.proxy.GetMetaDataProxy;
import com.alcatel.smartlinkv3.mediaplayer.upnp.MediaItem;

class SyncMetaDataExecutor implements Executor {
    
    private static final String TAG = "MediaItem";
    
    private Activity mActivity;
    private ArrayList<FileInfo> mFileList;
    private int count;
    private final int size;
    private GetMetaDataProxy m_getmetadataproxy; 
    
    public SyncMetaDataExecutor(ArrayList<FileInfo> fileList,
            Activity activity) {
        this.mActivity = activity;
        this.mFileList = fileList;
        this.count = 0;
        this.size = fileList.size();
        m_getmetadataproxy = new GetMetaDataProxy();
    }
    
    private void actionRun(Runnable r) {
        boolean result;
        synchronized (this) {
            this.count++;
            result = (this.count >= this.size);
        }
        
        if (result)
            mActivity.runOnUiThread(r);
    }
    
    @Override
    public void execute(Runnable r) {
        
        for (int i = 0; i < this.size; i++) {
            FileInfo info = mFileList.get(i);
            if (!FileIconHelper.isMediaFile(info.fileName)) {
                actionRun(r);
                continue;
            }
            
            String uriString = Util.makePath(info.filePath, info.fileName);
            m_getmetadataproxy.syncGetMetaData(
                    this.mActivity, uriString, new MediaItemCallBack(info,r));
        }
        
        if (this.size == 0) {
            actionRun(r);
        }
    }
    
    class MediaItemCallBack implements GetMetaDataProxy.GetMetaDataRequestCallback {
        private final FileInfo mInfo;
        private final Runnable runnable;
        public MediaItemCallBack(FileInfo info, Runnable r) {
            this.mInfo = info;
            this.runnable = r;
        }
        
        @Override
        public void onGetItemMetaData(MediaItem item) {
            if(item != null) {
                Log.d(TAG, "item res is " + item.getRes());
                Log.d(TAG, "file name is " + mInfo.fileName);
                mInfo.item = item;
            } else
                Log.d(TAG, "GetMetaData failed!");
            actionRun(this.runnable);
        }
    }
}