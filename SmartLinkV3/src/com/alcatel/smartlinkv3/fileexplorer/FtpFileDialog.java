package com.alcatel.smartlinkv3.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPFile;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.BaseAdapter;

import com.alcatel.smartlinkv3.R;

public class FtpFileDialog extends FileDialog {
    
    private static final String TAG = "Moveto";
    private static final int MSG_SHOW_TOAST = 1;
    
    private static FtpFileCommandTask mCmdTask = new FtpFileCommandTask();
    private ArrayList<FileInfo> mInfos;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
         mCmdTask.init(this);
         mCmdTask.setFtpCommandListener(ftpCommandListener);
         mCmdTask.start();
    }
    
    @Override
    protected void onDestroy() {
        mCmdTask.stop();
        //mCmdTask.ftp_close();
        super.onDestroy();
    }
    
    private FtpFileCommandTask.FtpCommandListener ftpCommandListener = 
            new FtpFileCommandTask.FtpCommandListener() {
                @Override
                public void ftpMsgHandler(Message msg) {
                    Log.d(TAG, "at ftp msg handler" + msg.toString());
                    switch(msg.what) {
                    case MSG_SHOW_TOAST :
                        notifyDataChanged();
                        break;
                    }
                }
            };
    
    @Override
    protected List<Map<String, Object>> getData () {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        
        if (mInfos == null)
            return list;
        
        ArrayList<FileInfo> files = filter(mInfos);
        
        if (files != null) {
            for (FileInfo file : files) {
                map = new HashMap<String, Object>();
                map.put("title", file.fileName);
                map.put("info", Util.makePath(file.filePath, file.fileName));
                if (file.IsDir)
                    map.put("img", R.drawable.ex_folder);
                else
                    map.put("img", R.drawable.ex_doc);
                list.add(map);
            }
        }
        
        return list;
    }
    
    private ArrayList<FileInfo> filter (ArrayList<FileInfo> infos) {
        String title = new String(getTitle().toString());
        
        ArrayList<FileInfo> list = new ArrayList<FileInfo>();
        for (FileInfo info : infos) {
            if (info.IsDir)
            if (!info.fileName.equals(title))
                list.add(info);
        }
        return list;
    }
    
    @Override
    protected void notifyDataChanged () {
        mCmdTask.setOnCallResponse(new FtpFileCommandTask.OnCallResponse() {
            @Override
            public void callResponse(Object obj) {
                try {
                    mInfos = (ArrayList<FileInfo>) obj;
                    FtpFileDialog.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mData = getData();
                            BaseAdapter adapter = (BaseAdapter) mListView.getAdapter();
                            adapter.notifyDataSetChanged();
                            
                            showTextNavigationDir();
                        }
                    });
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
        mCmdTask.ftp_list_files(this.mDir);
    }
    
    @Override
    protected boolean doOperationNewFoler(String folerPath) {
        
        mCmdTask.ftp_create_folder(folerPath);
        
        return true;
    }
}