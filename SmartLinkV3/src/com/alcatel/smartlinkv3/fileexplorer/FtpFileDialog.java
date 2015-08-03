package com.alcatel.smartlinkv3.fileexplorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.BaseAdapter;

import com.alcatel.smartlinkv3.R;

public class FtpFileDialog extends FileDialog {
    
    private static final String TAG = "Moveto";
    private static final int MSG_SHOW_TOAST = 1;
    private static final int MSG_CREATE_FOLDER = 16;
    private static final int SUCCESS = 0;
    private static final int FAIL = -1;
    
    private FtpFileCommandTask mCmdTask = new FtpFileCommandTask();
    private ArrayList<FileInfo> mInfos;
    private String fileName[];
    
    TransparentWaitDialog mWaitDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        fileName = bundle.getStringArray("files_path");

        mCmdTask.init(this);
        mCmdTask.setFtpCommandListener(ftpCommandListener);
        mCmdTask.start();
        
        mWaitDialog = new TransparentWaitDialog(this);
        mWaitDialog.show();
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
                        break;
                    case MSG_CREATE_FOLDER :
                        if (((Integer) msg.obj).intValue() == SUCCESS)
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
    
    private boolean filesContains(String[] files, String file) {
        for (String f : files) {
            if (f.equals(file)) return true;
        }
        return false;
    }
    
    private ArrayList<FileInfo> filter (ArrayList<FileInfo> infos) {
        ArrayList<FileInfo> list = new ArrayList<FileInfo>();
        for (FileInfo info : infos) {
            if (info.IsDir)
            if (!filesContains(fileName, Util.makePath(info.filePath, info.fileName)))
            if (! ".".equals(info.fileName))
            if (! "..".equals(info.fileName))
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
                    if (mWaitDialog != null) {
                        mWaitDialog.dismiss();
                        mWaitDialog = null;
                    }
                    
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
        System.out.println("ftp,doOperationNewFoler "+folerPath);
        mCmdTask.ftp_create_folder(folerPath);
        
        return true;
    }
}