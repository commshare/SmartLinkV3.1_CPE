package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.HttpService;
import com.alcatel.smartlinkv3.common.MessageUti;

import android.app.*;
import android.util.Log;

public class SmartLinkV3App extends Application {
	
	private static SmartLinkV3App m_instance = null;  
	
	private String mapp_password = new String();
	private String mapp_username = new String();
	  
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
	
//	//login password
//	public String getLoginPassword() {
//		return mapp_password;
//	}
//
//	public void setLoginPassword(String password) {
//		mapp_password = password;
//	}
//		
//		//login username
//	public String getLoginUsername() {
//		return mapp_username;
//	}
//
//	public void setLoginUsername(String username) {
//		mapp_username = username;
//	}
}
