package com.alcatel.smartlinkv3.ui.activity;

import android.app.Application;

import com.alcatel.smartlinkv3.common.HostnameUtils;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;

import org.cybergarage.upnp.ControlPoint;

public class SmartLinkV3App extends Application {

    private static SmartLinkV3App m_instance = null;

    private AllShareProxy mAllShareProxy;

    private ControlPoint mControlPoint;

    private String mapp_password = new String();
    private String mapp_username = new String();
    private boolean mapp_changeisforce = false;
    ;

    public static SmartLinkV3App getInstance() {
        return m_instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        m_instance = this;
        HostnameUtils.setVerifyHostName();// https认证
        // x.Ext.init(this);
        
        // 全局异常捕获
        //CrashHandler crashHandler = CrashHandler.getInstance();
        //crashHandler.init(getApplicationContext());
        
        // 初始化接口
        // BusinessMannager.getInstance();
        // NotificationService.startService();
        // HandlerUtils.replaceHandler();
        // mAllShareProxy = AllShareProxy.getInstance(this);

    }

    public void setControlPoint(ControlPoint controlPoint) {
        mControlPoint = controlPoint;
    }

    public ControlPoint getControlPoint() {
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
