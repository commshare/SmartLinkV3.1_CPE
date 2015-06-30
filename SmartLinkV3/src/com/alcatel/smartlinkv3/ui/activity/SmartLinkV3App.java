package com.alcatel.smartlinkv3.ui.activity;

import org.cybergarage.upnp.ControlPoint;

import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.NotificationService;
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
	private boolean mapp_changeisforce = false;;  
	
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
		NotificationService.startService();
		
		HandlerUtils.replaceHandler();
		
		mAllShareProxy = AllShareProxy.getInstance(this);
	}
	
	public void setControlPoint(ControlPoint controlPoint){
		mControlPoint = controlPoint;
	}
	
	public ControlPoint getControlPoint(){
		return mControlPoint;
	}

	//first  login password
	public String getLoginPassword() {
		return mapp_password;
	}

	public void setLoginPassword(String password) {
		mapp_password = password;
	}
//	
	//first login username
//	
	public String getLoginUsername() {
		return mapp_username;
	}

	public void setLoginUsername(String username) {
		mapp_username = username;
	}
	
	//first is forcelogin
//	
	public boolean getIsforcesLogin() {
		return mapp_changeisforce;
	}

	public void setIsforcesLogin(boolean istrue) {
		mapp_changeisforce = istrue;
	}
}
