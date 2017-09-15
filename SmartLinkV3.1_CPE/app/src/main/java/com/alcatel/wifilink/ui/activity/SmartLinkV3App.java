package com.alcatel.wifilink.ui.activity;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import com.alcatel.wifilink.mediaplayer.proxy.AllShareProxy;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.HostnameUtils;

import org.cybergarage.upnp.ControlPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SmartLinkV3App extends MultiDexApplication {

    private static SmartLinkV3App m_instance = null;

    private AllShareProxy mAllShareProxy;

    private ControlPoint mControlPoint;

    private String mapp_password = new String();
    private String mapp_username = new String();
    private boolean mapp_changeisforce = false;
    private static List<Activity> contexts;

    public static SmartLinkV3App getInstance() {
        return m_instance;
    }

    public static List<Activity> getContextInstance() {
        return contexts;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        m_instance = this;
        contexts = new ArrayList<>();
        // BusinessManager.getInstance();
        // NotificationService.startService();
        //HandlerUtils.replaceHandler();
        // mAllShareProxy = AllShareProxy.getInstance(this);
        /* checked hostNameVerify  */
        HostnameUtils.setVerifyHostName();
        // CrashHandler crashHandler = CrashHandler.getInstance();
        // crashHandler.init(getApplicationContext());
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
