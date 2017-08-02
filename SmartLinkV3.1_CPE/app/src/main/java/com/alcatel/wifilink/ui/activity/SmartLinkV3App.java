package com.alcatel.wifilink.ui.activity;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.common.NotificationService;
import com.alcatel.wifilink.mediaplayer.proxy.AllShareProxy;
import com.alcatel.wifilink.utils.WifiUtils;

import org.cybergarage.upnp.ControlPoint;
import org.xutils.x;

public class SmartLinkV3App extends MultiDexApplication {

    private static SmartLinkV3App m_instance = null;

    private AllShareProxy mAllShareProxy;

    private ControlPoint mControlPoint;

    private String mapp_password = new String();
    private String mapp_username = new String();
    private boolean mapp_changeisforce = false;

    public static SmartLinkV3App getInstance() {
        return m_instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        m_instance = this;
        Log.d("HttpService", "Application onCreate ");
        BusinessManager.getInstance();
        NotificationService.startService();
        HandlerUtils.replaceHandler();
        mAllShareProxy = AllShareProxy.getInstance(this);
    }

    public void setControlPoint(ControlPoint controlPoint) {
        mControlPoint = controlPoint;
    }

    public ControlPoint getControlPoint() {
        return mControlPoint;
    }

    //first  LOGIN password
    public String getLoginPassword() {
        return mapp_password;
    }

    public void setLoginPassword(String password) {
        mapp_password = password;
    }

    //	
    //first LOGIN username
    //	
    public String getLoginUsername() {
        return mapp_username;
    }

    public void setLoginUsername(String username) {
        mapp_username = username;
    }

    //first is forcelogin
    //	
    public boolean IsForcesLogin() {
        return mapp_changeisforce;
    }

    public void setForcesLogin(boolean forceLogin) {
        mapp_changeisforce = forceLogin;
    }
}
