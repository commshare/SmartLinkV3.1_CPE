package com.alcatel.wifilink.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

class ExternalFileObtain {
    
    public static final int PICK_CONTACT = 1;
    
    private Activity mActivity;
    private OnGetFilesListener mListener;
    
    private static PolicyAnalysis[] policys = new PolicyAnalysis[] {
            new FileiPolicyAnalysis(),
            new ImagesiPolicyAnalysis(),
            new VideoPolicyAnalysis(),
            new AudioPolicyAnalysis()
    };
    
    public interface OnGetFilesListener {
        void onGetFiles(ArrayList<File> list);
    }
    
    private ExternalFileObtain(Activity activity) {
        this.mActivity = activity;
    }
    
    private static HashMap<Activity,ExternalFileObtain> map = 
            new HashMap<Activity,ExternalFileObtain>();
    
    public static ExternalFileObtain getInstance(Activity activity) {
        if (!map.containsKey(activity)) {
            map.put(activity, new ExternalFileObtain(activity));
        }
        return map.get(activity);
    }
    
    
    public void getFiles (OnGetFilesListener listener) {        
        this.mListener = listener;
        pickFile();
    }   

    private void pickFile() {
        String type = "*/*";
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(type);
        mActivity.startActivityForResult(intent, PICK_CONTACT);
    }
    
    public void analysisFile (Intent data) {
        Uri contactData = data.getData();
        String filePath = null;
        
        for (PolicyAnalysis policy : policys) {
            if (policy.matchedPolicy(contactData)) {
                filePath = policy.processMode(contactData,mActivity);
                break;
            }
        }
        
        if (filePath != null) {
            Log.i("PICK", filePath);
            ArrayList<File> list = new ArrayList<File>();
            list.add(new File(filePath));
            if (mListener != null) mListener.onGetFiles(list);
        }
    }
}

abstract class  PolicyAnalysis {
    public abstract boolean matchedPolicy(Uri contactData);
    public abstract String processMode(Uri contactData, Activity activity);
}

class FileiPolicyAnalysis extends PolicyAnalysis {       
    @Override
    public boolean matchedPolicy(Uri contactData) {
        String scheme = contactData.getScheme();
        return "file".equals(scheme);
    }        
    @Override
    public String processMode(Uri contactData, Activity activity) {
        return contactData.getPath();
    }
}

class ImagesiPolicyAnalysis extends PolicyAnalysis {       
    @Override
    public boolean matchedPolicy(Uri contactData) {
        String scheme = contactData.getScheme();
        String path = contactData.getPath();           
        return "content".equals(scheme) && (path.indexOf("images") > 0);
    }        
    @Override
    public String processMode(Uri contactData, Activity activity) {
        String filePath = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(activity,contactData,proj, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (c.moveToFirst()) { 
            filePath = c.getString(index);
        }
        return filePath;
    }
}

class VideoPolicyAnalysis extends PolicyAnalysis {       
    @Override
    public boolean matchedPolicy(Uri contactData) {
        String scheme = contactData.getScheme();
        String path = contactData.getPath();           
        return "content".equals(scheme) && (path.indexOf("video") > 0);
    }        
    @Override
    public String processMode(Uri contactData, Activity activity) {
        String filePath = null;
        String[] proj = { MediaStore.Video.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(activity,contactData,proj, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        int index = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        if (c.moveToFirst()) { 
            filePath = c.getString(index);
        }
        return filePath;
    }
}

class AudioPolicyAnalysis extends PolicyAnalysis {       
    @Override
    public boolean matchedPolicy(Uri contactData) {
        String scheme = contactData.getScheme();
        String path = contactData.getPath();           
        return "content".equals(scheme) && (path.indexOf("audio") > 0);
    }        
    @Override
    public String processMode(Uri contactData, Activity activity) {
        String filePath = null;
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(activity,contactData,proj, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        int index = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        if (c.moveToFirst()) { 
            filePath = c.getString(index);
        }
        return filePath;
    }
}
