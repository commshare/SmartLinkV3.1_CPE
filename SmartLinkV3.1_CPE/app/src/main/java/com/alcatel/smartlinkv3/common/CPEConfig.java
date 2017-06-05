package com.alcatel.smartlinkv3.common;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

import java.io.File;

public class CPEConfig {
    private static CPEConfig mInstance = null;
    private CPESettings mSettings = null;

    private Context m_context = null;

    // get instance
    public static CPEConfig getInstance() {
        if (mInstance == null) {
            mInstance = new CPEConfig();
            mInstance.setContext(SmartLinkV3App.getInstance().getApplicationContext());
            mInstance.initConfig();
        }
        return mInstance;
    }

    private void setContext(Context context) {
        m_context = context;
    }

    // initialize configure include settings and data
    private void initConfig() {
        initSettings();
    }

    // settings
    private void initSettings() {
        mSettings = new CPESettings(m_context);
    }

    // log switch
    public boolean getNotificationSwitch() {
        return mSettings.getNotificationSwitch();
    }

    public void setNotificationSwitch(boolean bSwitch) {
        boolean bPre = getNotificationSwitch();
        mSettings.setNotificationSwitch(bSwitch);
        if (bPre != bSwitch) {
            Intent megIntent = new Intent(MessageUti.CPE_CHANGED_ALERT_SWITCH);
            m_context.sendBroadcast(megIntent);
        }
    }

    //default directory
    public String getDefaultDir() {
        String strDir = mSettings.getDefaultDir();
        if (strDir != null && strDir.length() > 0) {
            File file = new File(strDir);
            if (file.exists() == true) {
                return strDir;
            } else {
                if (file.mkdirs() == true) {
                    return strDir;
                }
            }
        }

        strDir = Environment.getExternalStorageDirectory().getPath() + "/Smart Router";
        File file = new File(strDir);
        if (file.exists() == true) {
            mSettings.setDefaultDir(strDir);
            return strDir;
        }
        if (file.mkdirs() == true) {
            mSettings.setDefaultDir(strDir);
            return strDir;
        } else {
            mSettings.setDefaultDir("");
            return "";
        }
    }

    public void setDefaultDir(String strDir) {
        mSettings.setDefaultDir(strDir);
    }

    // log volume value
    public int getNotificationVolumeValue() {
        return mSettings.getNotificationVolumeValue();
    }

    public void setNotificationVolumeValue(int nVal) {
        mSettings.setNotificationVolumeValue(nVal);
    }

    //change wifi password
                /*public boolean getChangeWifiPwdSwitch() {
		return mSettings.getChangeWifiPwdSwitch();
	}

	public void setChangeWifiPwdSwitch(boolean bSwitch) {
		mSettings.setChangeWifiPwdSwitch(bSwitch);
	}*/


    //LOGIN password

    public String getLoginPassword() {
        return mSettings.getLoginPassword();
    }

    public void setLoginPassword(String password) {
        mSettings.setLoginPassword(password);
    }

    //LOGIN username

    public String getLoginUsername() {
        return mSettings.getLoginUsername();
    }

    public void setLoginUsername(String username) {
        mSettings.setLoginUsername(username);
    }

    //LOGIN status

    public boolean getAutoLoginFlag() {
        return mSettings.getAutoLoginFlag();
    }

    public void userLogout() {
        mSettings.userLogout();
    }

    //		public void setAutoLoginFlag(boolean blAutoLogin) {
    //			mSettings.setAutoLoginFlag(blAutoLogin);
    //		}

    public boolean getInitialLaunchedFlag() {
        return mSettings.getInitialLaunchedFlag();
    }

    public void setInitialLaunchedFlag() {
        mSettings.setInitialLaunchedFlag();
    }

    public boolean getQuickSetupFlag() {
        return mSettings.getQuickSetupFlag();
    }

    public void setQuickSetupFlag() {
        mSettings.setQuickSetupFlag();
    }

    public void cleanAllSetupFlag() {
        mSettings.cleanAllSetting();
    }
}
