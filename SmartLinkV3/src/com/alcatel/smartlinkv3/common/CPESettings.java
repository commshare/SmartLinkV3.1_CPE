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
	
	private static final String LOGIN_SETTING_FILE = "LoginSetting";
	private static final String ITEM_LOGIN_PWD = "LoginPwd";
	private static final String ITEM_LOGIN_USERNAME = "LoginUsername";
	private static final String ITEM_LOGIN_STATUS = "LoginStatus";
  private static final String ITEM_INIT_LAUNCH = "InitialLaunch";

	private Context mContext = null;
	private boolean mNotificationSwitchOn = false;
	private int m_NotificationVolumeValue = 0;
	private boolean mWifiPasswordSwitchOn = false;
	private boolean mLaunched = false;
	private String m_defaultDir = new String();
	private String m_password = new String();
	private String m_username = new String();
	//private boolean m_blAutoLogin = false;
	
	
	public CPESettings(Context context)
	{
		mContext = context;
		loadSettings();
		loadLoginSettings();
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
		mLaunched = sp.getBoolean(ITEM_INIT_LAUNCH, false);	
	}
	
	private void saveSettings()
	{
		SharedPreferences sp = mContext.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
		Editor edt = sp.edit();
		edt.putBoolean(ITEM_NOTIFICATION_SWITCH_STATE, mNotificationSwitchOn);
		edt.putInt(ITEM_NOTIFICATION_V1_VOLUME_VALUE, m_NotificationVolumeValue);
		edt.putBoolean(ITEM_WIFI_PASSWORD_SWITCH_STATE, mWifiPasswordSwitchOn);
		edt.putString(ITEM_DEFAULT_DIRECTORY, m_defaultDir);
		edt.putBoolean(ITEM_INIT_LAUNCH, mLaunched);
		edt.commit();	
	}
	
	private void loadLoginSettings()
	{
		SharedPreferences sp = mContext.getSharedPreferences(LOGIN_SETTING_FILE, Context.MODE_PRIVATE);
		m_password = sp.getString(ITEM_LOGIN_PWD, "");
		m_username = sp.getString(ITEM_LOGIN_USERNAME, "");
		//m_blAutoLogin = sp.getBoolean(ITEM_LOGIN_STATUS, false);		
	}
	
	private void saveLoginSettings()
	{
		SharedPreferences sp = mContext.getSharedPreferences(LOGIN_SETTING_FILE, Context.MODE_PRIVATE);
		Editor edt = sp.edit();
		edt.putString(ITEM_LOGIN_PWD, m_password);		
		edt.putString(ITEM_LOGIN_USERNAME, m_username);
		//edt.putBoolean(ITEM_LOGIN_STATUS, m_blAutoLogin);
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
		saveLoginSettings();
	}
	
	//login username
	public String getLoginUsername() {
		return m_username;
	}

	public void setLoginUsername(String username) {
		m_username = username;
		saveLoginSettings();
	}
	
	//login status
//	public  void setAutoLoginFlag(boolean blAutoLogin){
//		m_blAutoLogin = blAutoLogin;
//		saveLoginSettings();
//	}
	
	public  boolean getAutoLoginFlag( ){
		if(m_username != "" && m_password != "")
		{
			return true;			
		}
		else
		{
			return false;			
		}
	}
	
	public void userLogout()
	{
		m_username = "";
		m_password = "";
		saveLoginSettings();
	}
	
	/*  
	 * Flag to mark whether the SmartLink is launched after 
	 * install or update.
	 */
	public boolean getInitialLaunchedFlag() {
	  return mLaunched;
	}
	
  public void setInitialLaunchedFlag() {
    mLaunched = true;
    saveSettings();
  }	
}
