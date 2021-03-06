package com.alcatel.wifilink.httpservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

/**
 * A log class that simply wrapper android.utils.Log.
 * @author shenlong.xu.sz
 *
 */
public class HttpAccessLog {
	private static HttpAccessLog m_instance;

    public static HttpAccessLog getInstance() {
        if(m_instance == null) {
            m_instance = new HttpAccessLog();
        }
        return m_instance;
    }
    
    private HttpAccessLog() {
    }  
    
    public void writeLogToFile(String strLog) {
    	if(isLogSwitchOn() == false)
    		return;
    	if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
    		FileOutputStream fos = null;
    		try {
	    		File logFile = new File(Environment.getExternalStorageDirectory(),ConstValue.LOG_FILE_NAME);
	    		if(!logFile.exists()){
					logFile.createNewFile();
	    		}
	    		
	    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
	    		String log = format.format(Calendar.getInstance().getTime()) + " " + strLog + "\r\n";
	    		synchronized(this){
		    		fos = new FileOutputStream(logFile,true);
		    		fos.write(log.getBytes());
		    		fos.close();
		    		fos = null;
	    		}
    		}catch(FileNotFoundException e){
    			e.printStackTrace();
    		}catch(IOException e){
    			e.printStackTrace();
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			if(fos != null) {
    				try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
    	}else{
    	}
    }
    
    private boolean isLogSwitchOn() {
		String strLoginFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/CPE/LogEnable";
		File f = new File(strLoginFile);
		return f.exists();
	}
    
}
