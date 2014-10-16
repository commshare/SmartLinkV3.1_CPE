package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.common.HttpService;

import android.app.*;
import android.util.Log;

public class SmartLinkV3App extends Application {
	
	private static SmartLinkV3App m_instance = null;  
	  
    public static SmartLinkV3App getInstance() {  
        return m_instance;  
    }  

    
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());	
		m_instance = this;
		Log.d("HttpService", "Application onCreate ");
		HttpService.startService();		
		
		HandlerUtils.replaceHandler();
	}
}
