package com.alcatel.smartlinkv3.common;

import java.util.HashMap;
import java.util.Map;

import com.alcatel.smartlinkv3.business.PowerManager;
import com.alcatel.smartlinkv3.business.LanManager;
import com.alcatel.smartlinkv3.business.SMSManager;
import com.alcatel.smartlinkv3.business.SharingManager;
import com.alcatel.smartlinkv3.business.SimManager;
import com.alcatel.smartlinkv3.business.StatisticsManager;
import com.alcatel.smartlinkv3.business.SystemManager;
import com.alcatel.smartlinkv3.business.UpdateManager;
import com.alcatel.smartlinkv3.business.UserManager;
import com.alcatel.smartlinkv3.business.WanManager;
import com.alcatel.smartlinkv3.business.WlanManager;

public class MessageUti {
	//response result
	public static String RESPONSE_RESULT = "com.alcatel.smartlinkv3.business.response_result";
	public static String RESPONSE_ERROR_CODE = "com.alcatel.smartlinkv3.business.response_error_code";
	public static String RESPONSE_ERROR_MESSAGE = "com.alcatel.smartlinkv3.business.response_error_message";
	/*************************************Pushed to the application layer message. start***********************************************************/
	public static String CPE_WIFI_CONNECT_CHANGE = "com.alcatel.smartlinkv3.business.cpewificonnectchange";
	public static String CPE_BUSINESS_STATUS_CHANGE = "com.alcatel.smartlinkv3.business.cpebusinessstatuschange";
	public static String CPE_NETWORK_CONNECT_CHANGE = "com.alcatel.smartlinkv3.business.cpenetworkconnectchange";
	public static String CPE_CHANGED_BILLING_MONTH = "com.alcatel.smartlinkv3.business.cpebillingmonthchanged";
	public static String CPE_CHANGED_ALERT_SWITCH = "com.alcatel.smartlinkv3.business.cpealertswitchchanged";
	public static String CPE_GET_MM100_ACCESS_POINTS_LIST_START = "com.alcatel.smartlinkv3.business.wlan.getmm100accesspointsliststart";
	
	/**************************User message start*********************************************************************************/
	public static String USER_LOGIN_REQUEST = "com.alcatel.smartlinkv3.business.user.login";
	public static String USER_LOGOUT_REQUEST = "com.alcatel.smartlinkv3.business.user.logout";
	/**************************User message end*********************************************************************************/
	
	/**************************System message start*********************************************************************************/
	public static String SYSTEM_GET_FEATURES_ROLL_REQUSET = 
			"com.alcatel.smartlinkv3.business.system.getFeatures";
	public static String SYSTEM_GET_SYSTEM_INFO_REQUSET =
			"com.alcatel.smartlinkv3.business.system.getsysteminfo";
	public static String SYSTEM_GET_SYSTEM_STATUS_REQUSET =
			"com.alcatel.smartlinkv3.business.system.getsystemstatus";
	public static String SYSTEM_GET_MM100_SYSTEM_INFO_REQUSET =
			"com.alcatel.smartlinkv3.business.system.getmm100systeminfo";
	public static String SYSTEM_GET_EXTERNAL_STORAGE_DEVICE_REQUSET = 
			"com.alcatel.smartlinkv3.business.system.getexternalstoragedevice";
	
	public static String SYSTEM_SET_DEVICE_REBOOT=
			"com.alcatel.smartlinkv3.business.system.setDeviceReboot";
	public static String SYSTEM_SET_DEVICE_RESET=
			"com.alcatel.smartlinkv3.business.system.setDeviceReset";
	public static String SYSTEM_SET_DEVICE_BACKUP=
			"com.alcatel.smartlinkv3.business.system.setDeviceBackup";
	public static String SYSTEM_SET_DEVICE_RESTORE=
			"com.alcatel.smartlinkv3.business.setDeviceRestore";
	/**************************System message end*********************************************************************************/
	
	/**************************Statistics message start*********************************************************************************/
	public static String STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.statistics.getusagesettings";
	public static String STATISTICS_SET_BILLING_DAY_REQUSET = "com.alcatel.smartlinkv3.business.statistics.setbillingday";
	public static String STATISTICS_SET_MONTHLY_PLAN_REQUSET = "com.alcatel.smartlinkv3.business.statistics.setmonthlyplan";
	public static String STATISTICS_SET_USED_DATA_REQUSET = "com.alcatel.smartlinkv3.business.statistics.setuseddata";
	public static String STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET = "com.alcatel.smartlinkv3.business.statistics.settimelimitflag";
	public static String STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET = "com.alcatel.smartlinkv3.business.statistics.settimelimittimes";
	public static String STATISTICS_SET_USED_TIMES_REQUSET = "com.alcatel.smartlinkv3.business.statistics.setusedtimes";
	public static String STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET = "com.alcatel.smartlinkv3.business.statistics.setautodisconnflag";
	public static String STATISTICS_CLEAR_ALL_RECORDS_REQUSET = "com.alcatel.smartlinkv3.business.statistics.clearallrecords";
	public static String STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.statistics.getUsageRecord";
	/**************************Statistics message end*********************************************************************************/
	
	/**************************Sim message start*********************************************************************************/
	public static String SIM_GET_SIM_STATUS_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.sim.getsimstatus";
	public static String SIM_UNLOCK_PIN_REQUEST = "com.alcatel.smartlinkv3.business.sim.unlockpin";
	public static String SIM_UNLOCK_PUK_REQUEST = "com.alcatel.smartlinkv3.business.sim.unlockpuk";
	public static String SIM_CHANGE_PIN_REQUEST = "com.alcatel.smartlinkv3.business.sim.changepin";
	public static String SIM_CHANGE_PIN_STATE_REQUEST = "com.alcatel.smartlinkv3.business.sim.changepinstate";
	public static String SIM_GET_AUTO_ENTER_PIN_STATE_REQUEST = "com.alcatel.smartlinkv3.business.sim.getautopinstate";
	public static String SIM_SET_AUTO_ENTER_PIN_STATE_REQUEST = "com.alcatel.smartlinkv3.business.sim.setautopinstate";
	/**************************Sim message end*********************************************************************************/
	
	/**************************Network message start*********************************************************************************/
	public static String NETWORK_GET_NETWORK_INFO_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.network.getnetworkinfo";
	/**************************Network message end*********************************************************************************/
	
	/**************************SMS message start*********************************************************************************/
	public static String SMS_GET_SMS_INIT_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.sms.getsmsinit";
	public static String SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.sms.getcontactlist";
	public static String SMS_GET_SMS_CONTENT_LIST_REQUSET = "com.alcatel.smartlinkv3.business.sms.getSMSContentListRequest";
	public static String SMS_DELETE_SMS_REQUSET = "com.alcatel.smartlinkv3.business.sms.deletesms";
	public static String SMS_SEND_SMS_REQUSET = "com.alcatel.smartlinkv3.business.sms.sendsms";
	public static String SMS_GET_SEND_STATUS_REQUSET = "com.alcatel.smartlinkv3.business.sms.getsendstatus";
	/**************************SMS message end*********************************************************************************/
	
	/**************************Wan message start*********************************************************************************/
	public static String WAN_GET_CONNECT_STATUS_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.wan.getconnectstatus";
	public static String WAN_CONNECT_REQUSET = "com.alcatel.smartlinkv3.business.wan.connect";
	public static String WAN_DISCONNECT_REQUSET = "com.alcatel.smartlinkv3.business.wan.disconnect";
	/**************************Wan message end*********************************************************************************/
	
	/**************************Wlan message start*********************************************************************************/
	public static String WLAN_GET_NUMBER_OF_HOST_ROLL_REQUSET = "com.alcatel.smartlinkv3.business.wlan.gethostnum";
	public static String WLAN_GET_WLAN_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.wlan.getwalnsetting";
	public static String WLAN_SET_WLAN_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.wlan.setwalnsetting";
	public static String WLAN_GET_MM100_WLAN_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.wlan.getmm100walnsetting";
	public static String WLAN_GET_MM100_ACCESS_POINTS_LIST_REQUSET = "com.alcatel.smartlinkv3.business.wlan.getmm100accesspointslist";
	public static String WLAN_GET_MM100_REMOTE_AP_REQUSET = "com.alcatel.smartlinkv3.business.wlan.getmm100remoteap";
	public static String WLAN_SELECT_MM100_ACCESS_POINT_REQUSET = "com.alcatel.smartlinkv3.business.wlan.selectmm100accesspoint";
	public static String WLAN_DISSCONNECT_MM100_CLIENT_POINT_REQUSET = "com.alcatel.smartlinkv3.business.wlan.dissconnectmm100clientpoint";
	
	public static String WLAN_SET_WPS_PIN_REQUSET = "com.alcatel.smartlinkv3.business.wlan.SetWPSPin";
	public static String WLAN_SET_WPS_PBC_REQUSET = "com.alcatel.smartlinkv3.business.wlan.SetWPSPbc";
	/**************************Wlan message end*********************************************************************************/
	
	/**************************Sharing message start*********************************************************************************/
	public static String SERVICE_SET_SERVICE_STATE_REQUSET = "com.alcatel.smartlinkv3.business.service.setservicestate";
	public static String SERVICE_GET_SERVICE_STATE_REQUSET = "com.alcatel.smartlinkv3.business.service.getservicestate";
	public static String SERVICE_SET_SAMBA_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.service.setsambasetting";
	public static String SERVICE_GET_SAMBA_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.service.getsambasetting";	
	public static String SERVICE_SET_DLNA_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.service.setdlnasetting";
	public static String SERVICE_GET_DLNA_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.service.getdlnasetting";	
	public static String SERVICE_SET_FTP_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.service.setftpsetting";
	public static String SERVICE_GET_FTP_SETTING_REQUSET = "com.alcatel.smartlinkv3.business.service.getftpsetting";
	/**************************Sharing message end*********************************************************************************/
	
	/**************************LAN message start*******************************************************************************/
	public static String LAN_GET_LAN_SETTINGS=
			"com.alcatel.smartlinkv3.business.lan.getLanSettings";
	public static String LAN_SET_LAN_SETTINGS=
			"com.alcatel.smartlinkv3.business.lan.setLanSettings";
	/**************************LAN message end*********************************************************************************/
	
	/**************************power message start*******************************************************************************/
	public static String POWER_GET_BATTERY_STATE=
			"com.alcatel.smartlinkv3.business.power.getBatteryState";
	public static String POWER_GET_POWER_SAVING_MODE=
			"com.alcatel.smartlinkv3.business.power.getPowerSavingMode";
	public static String POWER_SET_POWER_SAVING_MODE=
			"com.alcatel.smartlinkv3.business.power.setPowerSavingMode";
	/**************************power message end*********************************************************************************/
	
	/**************************update message start*******************************************************************************/
	public static String UPDATE_GET_DEVICE_NEW_VERSION=
			"com.alcatel.smartlinkv3.business.update.getDeviceNewVersion";
	public static String UPDATE_SET_DEVICE_START_UPDATE=
			"com.alcatel.smartlinkv3.business.update.setDeviceStartUpdate";
	public static String UPDATE_GET_DEVICE_UPGRADE_STATE=
			"com.alcatel.smartlinkv3.business.update.getDeviceUpgradeState";
	public static String UPDATE_SET_DEVICE_STOP_UPDATE=
			"com.alcatel.smartlinkv3.business.update_setDeviceStopUpdate";
	/**************************update message end*********************************************************************************/
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static Map<String, HttpMethodUti> httpMethods = new HashMap<String, HttpMethodUti>();
	public static void intHttpMethods() {
		/********************System method start**********************/
		httpMethods.put(SYSTEM_GET_SYSTEM_INFO_REQUSET, new HttpMethodUti(SystemManager.class, "getSystemInfo"));
		httpMethods.put(SYSTEM_GET_SYSTEM_STATUS_REQUSET, new HttpMethodUti(SystemManager.class, "getSystemStatus"));
		httpMethods.put(SYSTEM_GET_MM100_SYSTEM_INFO_REQUSET, new HttpMethodUti(SystemManager.class, "getMM100SystemInfo"));
		httpMethods.put(SYSTEM_SET_DEVICE_REBOOT, 
				new HttpMethodUti(SystemManager.class, "rebootDevice"));
		httpMethods.put(SYSTEM_SET_DEVICE_RESET, 
				new HttpMethodUti(SystemManager.class, "resetDevice"));
		httpMethods.put(SYSTEM_SET_DEVICE_BACKUP, 
				new HttpMethodUti(SystemManager.class, "backupDevice"));
		httpMethods.put(SYSTEM_SET_DEVICE_RESTORE, 
				new HttpMethodUti(SystemManager.class, "restoreDevice"));
		/********************System method end**********************/
		
		/********************User method start**********************/
		httpMethods.put(USER_LOGIN_REQUEST, new HttpMethodUti(UserManager.class, "login"));
		httpMethods.put(USER_LOGOUT_REQUEST, new HttpMethodUti(UserManager.class, "logout"));
		/********************User method end**********************/
		
		/********************Statistics method start**********************/
		httpMethods.put(STATISTICS_SET_BILLING_DAY_REQUSET, new HttpMethodUti(StatisticsManager.class, "setBillingDay"));
		httpMethods.put(STATISTICS_SET_MONTHLY_PLAN_REQUSET, new HttpMethodUti(StatisticsManager.class, "setMonthlyPlan"));
		httpMethods.put(STATISTICS_SET_USED_DATA_REQUSET, new HttpMethodUti(StatisticsManager.class, "setUsedData"));
		httpMethods.put(STATISTICS_SET_TIME_LIMIT_FLAG_REQUSET, new HttpMethodUti(StatisticsManager.class, "setTimeLimitFlag"));
		httpMethods.put(STATISTICS_SET_TIME_LIMIT_TIMES_REQUSET, new HttpMethodUti(StatisticsManager.class, "setTimeLimitTimes"));
		httpMethods.put(STATISTICS_SET_USED_TIMES_REQUSET, new HttpMethodUti(StatisticsManager.class, "setUsedTimes"));
		httpMethods.put(STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET, new HttpMethodUti(StatisticsManager.class, "setAutoDisconnFlag"));	
		httpMethods.put(STATISTICS_CLEAR_ALL_RECORDS_REQUSET, new HttpMethodUti(StatisticsManager.class, "clearAllRecords"));		
		/********************Statistics method end**********************/
		
		/********************SIM method start**********************/
		httpMethods.put(SIM_UNLOCK_PIN_REQUEST, new HttpMethodUti(SimManager.class, "unlockPin"));
		httpMethods.put(SIM_UNLOCK_PUK_REQUEST, new HttpMethodUti(SimManager.class, "unlockPuk"));
		httpMethods.put(SIM_CHANGE_PIN_REQUEST, new HttpMethodUti(SimManager.class, "changePin"));
		httpMethods.put(SIM_CHANGE_PIN_STATE_REQUEST, new HttpMethodUti(SimManager.class, "changePinState"));
		httpMethods.put(SIM_GET_AUTO_ENTER_PIN_STATE_REQUEST, new HttpMethodUti(SimManager.class, "getAutoPinState"));
		httpMethods.put(SIM_SET_AUTO_ENTER_PIN_STATE_REQUEST, new HttpMethodUti(SimManager.class, "setAutoEnterPinState"));
		/********************SIM method end**********************/
		
		/********************SMS method start**********************/
		httpMethods.put(SMS_SEND_SMS_REQUSET, new HttpMethodUti(SMSManager.class, "sendSms"));
		httpMethods.put(SMS_DELETE_SMS_REQUSET, new HttpMethodUti(SMSManager.class, "deleteSms"));
		httpMethods.put(SMS_GET_SMS_CONTENT_LIST_REQUSET, new HttpMethodUti(SMSManager.class, "getSMSContentListRequest"));
		httpMethods.put(SMS_GET_SEND_STATUS_REQUSET, new HttpMethodUti(SMSManager.class, "getSmsSendResult"));
		/********************SMS method end**********************/
		
		/********************WAN method start**********************/
		httpMethods.put(WAN_CONNECT_REQUSET, new HttpMethodUti(WanManager.class, "connect"));
		httpMethods.put(WAN_DISCONNECT_REQUSET, new HttpMethodUti(WanManager.class, "disconnect"));		
		/********************WAN method end**********************/
		
		/********************Sharing method start**********************/	
		httpMethods.put(SERVICE_SET_SERVICE_STATE_REQUSET, new HttpMethodUti(SharingManager.class, "setServiceState"));	
		httpMethods.put(SERVICE_GET_SERVICE_STATE_REQUSET, new HttpMethodUti(SharingManager.class, "getServiceState"));	
		httpMethods.put(SERVICE_SET_SAMBA_SETTING_REQUSET, new HttpMethodUti(SharingManager.class, "setSambaSetting"));	
		httpMethods.put(SERVICE_GET_SAMBA_SETTING_REQUSET, new HttpMethodUti(SharingManager.class, "getSambaSetting"));	
		httpMethods.put(SERVICE_SET_DLNA_SETTING_REQUSET, new HttpMethodUti(SharingManager.class, "setDlnaSetting"));	
		httpMethods.put(SERVICE_GET_DLNA_SETTING_REQUSET, new HttpMethodUti(SharingManager.class, "getDlnaSetting"));	
		httpMethods.put(SERVICE_SET_FTP_SETTING_REQUSET, new HttpMethodUti(SharingManager.class, "setFtpSetting"));	
		httpMethods.put(SERVICE_GET_FTP_SETTING_REQUSET, new HttpMethodUti(SharingManager.class, "getFtpSetting"));		
		/********************Sharing method end**********************/			
		
		/********************WLAN method start**********************/
		httpMethods.put(WLAN_GET_WLAN_SETTING_REQUSET, new HttpMethodUti(WlanManager.class, "getWlanSetting"));	
		httpMethods.put(WLAN_SET_WLAN_SETTING_REQUSET, new HttpMethodUti(WlanManager.class, "setWlanSetting"));	
		httpMethods.put(WLAN_GET_MM100_WLAN_SETTING_REQUSET, new HttpMethodUti(WlanManager.class, "getMM100WlanSetting"));	
		httpMethods.put(WLAN_GET_MM100_ACCESS_POINTS_LIST_REQUSET, new HttpMethodUti(WlanManager.class, "getMM100AccessPointsList"));
		httpMethods.put(WLAN_SELECT_MM100_ACCESS_POINT_REQUSET, new HttpMethodUti(WlanManager.class, "selectMM100AccessPoint"));
		httpMethods.put(WLAN_DISSCONNECT_MM100_CLIENT_POINT_REQUSET, new HttpMethodUti(WlanManager.class, "disconnectClientAP"));
		httpMethods.put(WLAN_SET_WPS_PIN_REQUSET, new HttpMethodUti(WlanManager.class, "SetWPSPin"));
		httpMethods.put(WLAN_SET_WPS_PBC_REQUSET, new HttpMethodUti(WlanManager.class, "SetWPSPbc"));
		/********************WLAN method end**********************/

		/**************************LAN message start*******************************************************************************/
		httpMethods.put(LAN_GET_LAN_SETTINGS,
				new HttpMethodUti(LanManager.class, "getLanSettingsRequest"));
		httpMethods.put(LAN_SET_LAN_SETTINGS,
				new HttpMethodUti(LanManager.class, "setLanSettinsRequest"));
		/**************************LAN message end*********************************************************************************/
		
		/**************************power message start*******************************************************************************/
		httpMethods.put(POWER_GET_BATTERY_STATE,
				new HttpMethodUti(PowerManager.class, "getBatteryStateRequest"));
		httpMethods.put(POWER_GET_POWER_SAVING_MODE,
				new HttpMethodUti(PowerManager.class, "getPowerSavingModeRequest"));
		httpMethods.put(POWER_SET_POWER_SAVING_MODE,
				new HttpMethodUti(PowerManager.class, "setPowerSavingModeRequest"));
		/**************************power message end*********************************************************************************/
		
		/**************************update message start*******************************************************************************/
		httpMethods.put(UPDATE_GET_DEVICE_NEW_VERSION,
				new HttpMethodUti(UpdateManager.class, "getDeviceNewVersion"));
		httpMethods.put(UPDATE_SET_DEVICE_START_UPDATE,
				new HttpMethodUti(UpdateManager.class, "startUpdate"));
		httpMethods.put(UPDATE_GET_DEVICE_UPGRADE_STATE,
				new HttpMethodUti(UpdateManager.class, "getUpgradeState"));
		httpMethods.put(UPDATE_SET_DEVICE_STOP_UPDATE,
				new HttpMethodUti(UpdateManager.class, "stopUpdate"));
		/**************************update message end*********************************************************************************/
	}
}
