package com.alcatel.smartlinkv3.ui.activity;

import org.cybergarage.upnp.ControlPoint;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;

import android.app.*;
import android.util.Log;

public class SmartLinkV3App extends Application {
	
	private static SmartLinkV3App m_instance = null;  
	
	private AllShareProxy mAllShareProxy;

	private ControlPoint mControlPoint;
	
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
		BusinessMannager.getInstance();
//		Log.d("HttpService", "Application onCreate ");
//		HttpService.startService();		
		
		HandlerUtils.replaceHandler();
		
		mAllShareProxy = AllShareProxy.getInstance(this);
	}
	
	public void setControlPoint(ControlPoint controlPoint){
		mControlPoint = controlPoint;
	}
	
	public ControlPoint getControlPoint(){
		return mControlPoint;
	}
	
	//login password
//	public String getLoginPassword() {
//		return mapp_password;
//	}
//
//	public void setLoginPassword(String password) {
//		mapp_password = password;
//	}
//	
	//login username
//	
//	public String getLoginUsername() {
//		return mapp_username;
//	}
//
//	public void setLoginUsername(String username) {
//		mapp_username = username;
//	}
}
