package com.alcatel.smartlinkv3.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CPESettings {
	private static final String SETTING_FILE = "CPESetting";
	private static final String ITEM_NOTIFICATION_SWITCH_STATE = "UsageNotificationSwitch";
	private static final String ITEM_NOTIFICATION_V1_VOLUME_VALUE = "UsageNotificationValue";
	private static final String ITEM_WIFI_PASSWORD_SWITCH_STATE = "WifiPasswordSwitch";
	private static final String ITEM_DEFAULT_DIRECTORY = "DefaultDirectory";
	private static final String ITEM_LOGIN_PWD = "LoginPwd";

	private Context mContext = null;
	private boolean mNotificationSwitchOn = false;
	private int m_NotificationVolumeValue = 0;
	private boolean mWifiPasswordSwitchOn = false;
	private String m_defaultDir = new String();
	private String m_password = new String();
	public CPESettings(Context context)
	{
		mContext = context;
		loadSettings();
	}
	
	public void setDefaultDir(String strDir) {
		m_defaultDir = strDir;
		saveSettings();
	}
	
	public String getDefaultDir() {
		return m_defaultDir;
	}
	
	public void setNotificationSwitch(boolean bOn)
	{
		mNotificationSwitchOn = bOn;
		saveSettings();
	}
	
	public boolean getNotificationSwitch() {
		return mNotificationSwitchOn;
	}
	
	public void setNotificationVolumeValue(int nVal)
	{
		m_NotificationVolumeValue = nVal;
		saveSettings();
	}
	
	public int getNotificationVolumeValue() {
		return m_NotificationVolumeValue;
	}
	
	private void loadSettings()
	{
		SharedPreferences sp = mContext.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
		mNotificationSwitchOn = sp.getBoolean(ITEM_NOTIFICATION_SWITCH_STATE, false);
		m_NotificationVolumeValue = sp.getInt(ITEM_NOTIFICATION_V1_VOLUME_VALUE, 0);
		mWifiPasswordSwitchOn = sp.getBoolean(ITEM_WIFI_PASSWORD_SWITCH_STATE, false);
		m_defaultDir = sp.getString(ITEM_DEFAULT_DIRECTORY, "");
		m_password = sp.getString(ITEM_LOGIN_PWD, "");
	}
	
	private void saveSettings()
	{
		SharedPreferences sp = mContext.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
		Editor edt = sp.edit();
		edt.putBoolean(ITEM_NOTIFICATION_SWITCH_STATE, mNotificationSwitchOn);
		edt.putInt(ITEM_NOTIFICATION_V1_VOLUME_VALUE, m_NotificationVolumeValue);
		edt.putBoolean(ITEM_WIFI_PASSWORD_SWITCH_STATE, mWifiPasswordSwitchOn);
		edt.putString(ITEM_DEFAULT_DIRECTORY, m_defaultDir);
		edt.putString(ITEM_LOGIN_PWD, m_password);		
		edt.commit();	
	}
	
	//change wifi password
	public boolean getChangeWifiPwdSwitch() {
		return mWifiPasswordSwitchOn;
	}

	public void setChangeWifiPwdSwitch(boolean bSwitch) {
		mWifiPasswordSwitchOn = bSwitch;
		saveSettings();
	}
	
	//login password
	public String getLoginPassword() {
		return m_password;
	}

	public void setLoginPassword(String password) {
		m_password = password;
		saveSettings();
	}
}
