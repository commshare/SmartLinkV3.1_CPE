package com.alcatel.smartlinkv3.common;

import java.util.HashMap;
import java.util.Map;

import com.alcatel.smartlinkv3.business.CallLogManager;
import com.alcatel.smartlinkv3.business.SMSManager;
import com.alcatel.smartlinkv3.business.ServiceManager;
import com.alcatel.smartlinkv3.business.SimManager;
import com.alcatel.smartlinkv3.business.StatisticsManager;
import com.alcatel.smartlinkv3.business.SystemManager;
import com.alcatel.smartlinkv3.business.UserManager;
import com.alcatel.smartlinkv3.business.WanManager;
import com.alcatel.smartlinkv3.business.WlanManager;

public class MessageUti {
	//response result
	public static String RESPONSE_RESULT = "com.alcatel.cpe.business.response_result";
	public static String RESPONSE_ERROR_CODE = "com.alcatel.cpe.business.response_error_code";
	public static String RESPONSE_ERROR_MESSAGE = "com.alcatel.cpe.business.response_error_message";
	/*************************************Pushed to the application layer message. start***********************************************************/
	public static String CPE_WIFI_CONNECT_CHANGE = "com.alcatel.cpe.business.cpewificonnectchange";
	public static String CPE_BUSINESS_STATUS_CHANGE = "com.alcatel.cpe.business.cpebusinessstatuschange";
	public static String CPE_NETWORK_CONNECT_CHANGE = "com.alcatel.cpe.business.cpenetworkconnectchange";
	public static String CPE_CHANGED_BILLING_MONTH = "com.alcatel.cpe.business.cpebillingmonthchanged";
	public static String CPE_CHANGED_ALERT_SWITCH = "com.alcatel.cpe.business.cpealertswitchchanged";
	public static String CPE_GET_MM100_ACCESS_POINTS_LIST_START = "com.alcatel.cpe.business.wlan.getmm100accesspointsliststart";
	
	/**************************User message start*********************************************************************************/
	public static String USER_LOGIN_REQUEST = "com.alcatel.cpe.business.user.login";
	public static String USER_LOGOUT_REQUEST = "com.alcatel.cpe.business.user.logout";
	/**************************User message end*********************************************************************************/
	
	/**************************System message start*********************************************************************************/
	public static String SYSTEM_GET_FEATURES_ROLL_REQUSET = "com.alcatel.cpe.business.system.getFeatures";
	public static String SYSTEM_GET_SYSTEM_INFO_REQUSET = "com.alcatel.cpe.business.system.getsysteminfo";
	public static String SYSTEM_GET_MM100_SYSTEM_INFO_REQUSET = "com.alcatel.cpe.business.system.getmm100systeminfo";
	public static String SYSTEM_GET_EXTERNAL_STORAGE_DEVICE_REQUSET = "com.alcatel.cpe.business.system.getexternalstoragedevice";
	/**************************System message end*********************************************************************************/
	
	/**************************Statistics message start*********************************************************************************/
	public static String STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET = "com.alcatel.cpe.business.statistics.getusagesettings";
	public static String STATISTICS_SET_BILLING_DAY_REQUSET = "com.alcatel.cpe.business.statistics.setbillingday";
	public static String STATISTICS_SET_LIMIT_VALUE_REQUSET = "com.alcatel.cpe.business.statistics.setlimitvalue";
	public static String STATISTICS_SET_TOTAL_VALUE_REQUSET = "com.alcatel.cpe.business.statistics.settotalvalue";
	public static String STATISTICS_SET_DISCONNECT_OVER_TIME_REQUSET = "com.alcatel.cpe.business.statistics.setdisconnectovertime";
	public static String STATISTICS_SET_DISCONNECT_OVER_TIME_STATUS_REQUSET = "com.alcatel.cpe.business.statistics.setdisconnectovertimestatus";
	public static String STATISTICS_SET_DISCONNECT_OVER_FLOW_STATUS_REQUSET = "com.alcatel.cpe.business.statistics.setdisconnectoverflowstatus";
	public static String STATISTICS_CLEAR_ALL_RECORDS_REQUSET = "com.alcatel.cpe.business.statistics.clearallrecords";
	public static String STATISTICS_GET_USAGE_HISTORY_ROLL_REQUSET = "com.alcatel.cpe.business.statistics.getusagehistory";
	/**************************Statistics message end*********************************************************************************/
	
	/**************************Sim message start*********************************************************************************/
	public static String SIM_GET_SIM_STATUS_ROLL_REQUSET = "com.alcatel.cpe.business.sim.getsimstatus";
	public static String SIM_UNLOCK_PIN_REQUEST = "com.alcatel.cpe.business.sim.unlockpin";
	public static String SIM_UNLOCK_PUK_REQUEST = "com.alcatel.cpe.business.sim.unlockpuk";
	public static String SIM_CHANGE_PIN_REQUEST = "com.alcatel.cpe.business.sim.changepin";
	public static String SIM_CHANGE_PIN_STATE_REQUEST = "com.alcatel.cpe.business.sim.changepinstate";
	public static String SIM_GET_AUTO_ENTER_PIN_STATE_REQUEST = "com.alcatel.cpe.business.sim.getautopinstate";
	public static String SIM_SET_AUTO_ENTER_PIN_STATE_REQUEST = "com.alcatel.cpe.business.sim.setautopinstate";
	/**************************Sim message end*********************************************************************************/
	
	/**************************Network message start*********************************************************************************/
	public static String NETWORK_GET_NETWORK_INFO_ROLL_REQUSET = "com.alcatel.cpe.business.network.getnetworkinfo";
	/**************************Network message end*********************************************************************************/
	
	/**************************SMS message start*********************************************************************************/
	public static String SMS_GET_SMS_LIST_ROLL_REQUSET = "com.alcatel.cpe.business.sms.getsmslist";
	public static String SMS_DELETE_SMS_REQUSET = "com.alcatel.cpe.business.sms.deletesms";
	public static String SMS_SEND_SMS_REQUSET = "com.alcatel.cpe.business.sms.sendsms";
	public static String SMS_GET_SEND_STATUS_REQUSET = "com.alcatel.cpe.business.sms.getsendstatus";
	public static String SMS_MODIFY_SMS_READ_STATUS_REQUSET = "com.alcatel.cpe.business.sms.modifysmsreadstatus";
	/**************************SMS message end*********************************************************************************/
	
	/**************************Wan message start*********************************************************************************/
	public static String WAN_GET_CONNECT_STATUS_ROLL_REQUSET = "com.alcatel.cpe.business.wan.getconnectstatus";
	public static String WAN_CONNECT_REQUSET = "com.alcatel.cpe.business.wan.connect";
	public static String WAN_DISCONNECT_REQUSET = "com.alcatel.cpe.business.wan.disconnect";
	/**************************Wan message end*********************************************************************************/
	
	/**************************Wlan message start*********************************************************************************/
	public static String WLAN_GET_NUMBER_OF_HOST_ROLL_REQUSET = "com.alcatel.cpe.business.wlan.gethostnum";
	public static String WLAN_GET_WLAN_SETTING_REQUSET = "com.alcatel.cpe.business.wlan.getwalnsetting";
	public static String WLAN_SET_WLAN_SETTING_REQUSET = "com.alcatel.cpe.business.wlan.setwalnsetting";
	public static String WLAN_GET_MM100_WLAN_SETTING_REQUSET = "com.alcatel.cpe.business.wlan.getmm100walnsetting";
	public static String WLAN_GET_MM100_ACCESS_POINTS_LIST_REQUSET = "com.alcatel.cpe.business.wlan.getmm100accesspointslist";
	public static String WLAN_GET_MM100_REMOTE_AP_REQUSET = "com.alcatel.cpe.business.wlan.getmm100remoteap";
	public static String WLAN_SELECT_MM100_ACCESS_POINT_REQUSET = "com.alcatel.cpe.business.wlan.selectmm100accesspoint";
	public static String WLAN_DISSCONNECT_MM100_CLIENT_POINT_REQUSET = "com.alcatel.cpe.business.wlan.dissconnectmm100clientpoint";
	/**************************Wlan message end*********************************************************************************/
	
	
	/**************************Call Log message start*********************************************************************************/
	public static String CALLLOG_GET_CALLLOG_LIST_ROLL_REQUSET = "com.alcatel.cpe.business.calllog.getcallloglist";	
	public static String CALLLOG_DELETE_CALLLOG_REQUSET = "com.alcatel.cpe.business.calllog.deletecalllog";
	public static String CALLLOG_CLEAR_CALLLOG_REQUSET = "com.alcatel.cpe.business.calllog.clearcalllog";
	/**************************Call Log message end*********************************************************************************/
	
	/**************************Service message start*********************************************************************************/
	public static String SERVICE_SET_SERVICE_STATE_REQUSET = "com.alcatel.cpe.business.service.setservicestate";
	public static String SERVICE_GET_SERVICE_STATE_REQUSET = "com.alcatel.cpe.business.service.getservicestate";
	public static String SERVICE_SET_SAMBA_SETTING_REQUSET = "com.alcatel.cpe.business.service.setsambasetting";
	public static String SERVICE_GET_SAMBA_SETTING_REQUSET = "com.alcatel.cpe.business.service.getsambasetting";	
	public static String SERVICE_SET_DLNA_SETTING_REQUSET = "com.alcatel.cpe.business.service.setdlnasetting";
	public static String SERVICE_GET_DLNA_SETTING_REQUSET = "com.alcatel.cpe.business.service.getdlnasetting";	
	public static String SERVICE_SET_FTP_SETTING_REQUSET = "com.alcatel.cpe.business.service.setftpsetting";
	public static String SERVICE_GET_FTP_SETTING_REQUSET = "com.alcatel.cpe.business.service.getftpsetting";	
	public static String SERVICE_GET_MM100_SAMBA_SETTING_REQUSET = "com.alcatel.cpe.business.service.getmm100sambasetting";
	/**************************Service message end*********************************************************************************/
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static Map<String, HttpMethodUti> httpMethods = new HashMap<String, HttpMethodUti>();
	public static void intHttpMethods() {
		/********************System method start**********************/
		httpMethods.put(SYSTEM_GET_SYSTEM_INFO_REQUSET, new HttpMethodUti(SystemManager.class, "getSystemInfo"));
		httpMethods.put(SYSTEM_GET_MM100_SYSTEM_INFO_REQUSET, new HttpMethodUti(SystemManager.class, "getMM100SystemInfo"));
		/********************System method end**********************/
		
		/********************User method start**********************/
		httpMethods.put(USER_LOGIN_REQUEST, new HttpMethodUti(UserManager.class, "login"));
		httpMethods.put(USER_LOGOUT_REQUEST, new HttpMethodUti(UserManager.class, "logout"));
		/********************User method end**********************/
		
		/********************Statistics method start**********************/
		httpMethods.put(STATISTICS_SET_BILLING_DAY_REQUSET, new HttpMethodUti(StatisticsManager.class, "setBillingDay"));
		httpMethods.put(STATISTICS_SET_LIMIT_VALUE_REQUSET, new HttpMethodUti(StatisticsManager.class, "setLimitValue"));
		httpMethods.put(STATISTICS_SET_TOTAL_VALUE_REQUSET, new HttpMethodUti(StatisticsManager.class, "setTotalValue"));
		httpMethods.put(STATISTICS_SET_DISCONNECT_OVER_TIME_REQUSET, new HttpMethodUti(StatisticsManager.class, "setDisconnectOvertime"));
		httpMethods.put(STATISTICS_SET_DISCONNECT_OVER_TIME_STATUS_REQUSET, new HttpMethodUti(StatisticsManager.class, "setDisconnectOvertimeState"));
		httpMethods.put(STATISTICS_SET_DISCONNECT_OVER_FLOW_STATUS_REQUSET, new HttpMethodUti(StatisticsManager.class, "setDisconnectOverflowState"));
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
		httpMethods.put(SMS_DELETE_SMS_REQUSET, new HttpMethodUti(SMSManager.class, "deleteSms"));
		httpMethods.put(SMS_SEND_SMS_REQUSET, new HttpMethodUti(SMSManager.class, "sendSms"));
		httpMethods.put(SMS_GET_SEND_STATUS_REQUSET, new HttpMethodUti(SMSManager.class, "getSmsSendResult"));
		httpMethods.put(SMS_MODIFY_SMS_READ_STATUS_REQUSET, new HttpMethodUti(SMSManager.class, "modifySMSReadStatus"));
		
		/********************SMS method end**********************/
		
		/********************WAN method start**********************/
		httpMethods.put(WAN_CONNECT_REQUSET, new HttpMethodUti(WanManager.class, "connect"));
		httpMethods.put(WAN_DISCONNECT_REQUSET, new HttpMethodUti(WanManager.class, "disconnect"));		
		/********************WAN method end**********************/
		
		/********************call log method start**********************/	
		httpMethods.put(CALLLOG_DELETE_CALLLOG_REQUSET, new HttpMethodUti(CallLogManager.class, "deleteCallLog"));
		httpMethods.put(CALLLOG_CLEAR_CALLLOG_REQUSET, new HttpMethodUti(CallLogManager.class, "clearCallLog"));		
		/********************call log method end**********************/
		
		/********************service method start**********************/	
		httpMethods.put(SERVICE_SET_SERVICE_STATE_REQUSET, new HttpMethodUti(ServiceManager.class, "setServiceState"));	
		httpMethods.put(SERVICE_GET_SERVICE_STATE_REQUSET, new HttpMethodUti(ServiceManager.class, "getServiceState"));	
		httpMethods.put(SERVICE_SET_SAMBA_SETTING_REQUSET, new HttpMethodUti(ServiceManager.class, "setSambaSetting"));	
		httpMethods.put(SERVICE_GET_SAMBA_SETTING_REQUSET, new HttpMethodUti(ServiceManager.class, "getSambaSetting"));	
		httpMethods.put(SERVICE_SET_DLNA_SETTING_REQUSET, new HttpMethodUti(ServiceManager.class, "setDlnaSetting"));	
		httpMethods.put(SERVICE_GET_DLNA_SETTING_REQUSET, new HttpMethodUti(ServiceManager.class, "getDlnaSetting"));	
		httpMethods.put(SERVICE_SET_FTP_SETTING_REQUSET, new HttpMethodUti(ServiceManager.class, "setFtpSetting"));	
		httpMethods.put(SERVICE_GET_FTP_SETTING_REQUSET, new HttpMethodUti(ServiceManager.class, "getFtpSetting"));	
		httpMethods.put(SERVICE_GET_MM100_SAMBA_SETTING_REQUSET, new HttpMethodUti(ServiceManager.class, "getMM100SambaSetting"));		
		/********************service method end**********************/			
		
		/********************WLAN method start**********************/
		httpMethods.put(WLAN_GET_WLAN_SETTING_REQUSET, new HttpMethodUti(WlanManager.class, "getWlanSetting"));	
		httpMethods.put(WLAN_SET_WLAN_SETTING_REQUSET, new HttpMethodUti(WlanManager.class, "setWlanSetting"));	
		httpMethods.put(WLAN_GET_MM100_WLAN_SETTING_REQUSET, new HttpMethodUti(WlanManager.class, "getMM100WlanSetting"));	
		httpMethods.put(WLAN_GET_MM100_ACCESS_POINTS_LIST_REQUSET, new HttpMethodUti(WlanManager.class, "getMM100AccessPointsList"));
		httpMethods.put(WLAN_SELECT_MM100_ACCESS_POINT_REQUSET, new HttpMethodUti(WlanManager.class, "selectMM100AccessPoint"));
		httpMethods.put(WLAN_DISSCONNECT_MM100_CLIENT_POINT_REQUSET, new HttpMethodUti(WlanManager.class, "disconnectClientAP"));
		
		/********************WLAN method end**********************/

	}
}
