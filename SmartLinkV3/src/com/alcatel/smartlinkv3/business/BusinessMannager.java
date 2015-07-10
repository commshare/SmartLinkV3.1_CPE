package com.alcatel.smartlinkv3.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.business.DeviceManager.GetBlockDeviceListTask;
import com.alcatel.smartlinkv3.business.DeviceManager.GetConnectedDeviceTask;
import com.alcatel.smartlinkv3.business.device.BlockDeviceList;
import com.alcatel.smartlinkv3.business.device.ConnectedDeviceList;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.ConnectedDeviceItemModel;
import com.alcatel.smartlinkv3.business.model.NetworkInfoModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.SmsContactMessagesModel;
import com.alcatel.smartlinkv3.business.model.SystemInfoModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.power.PowerSavingModeInfo;
import com.alcatel.smartlinkv3.business.sharing.DlnaSettings;
import com.alcatel.smartlinkv3.business.sharing.FtpSettings;
import com.alcatel.smartlinkv3.business.sharing.SDCardSpace;
import com.alcatel.smartlinkv3.business.sharing.SDcardStatus;
import com.alcatel.smartlinkv3.business.sharing.SambaSettings;
import com.alcatel.smartlinkv3.business.statistics.UsageRecordResult;
import com.alcatel.smartlinkv3.business.system.Features;
import com.alcatel.smartlinkv3.business.system.RestoreError;
import com.alcatel.smartlinkv3.business.system.SystemInfo;
import com.alcatel.smartlinkv3.business.system.SystemStatus;
import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.business.update.DeviceUpgradeStateInfo;
import com.alcatel.smartlinkv3.business.wlan.WlanSettingResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.common.ENUM.SsidHiddenEnum;
import com.alcatel.smartlinkv3.common.ENUM.WlanSupportMode;
import com.alcatel.smartlinkv3.common.HttpMethodUti;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.AutoPinState;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WModeEnum;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.business.model.ConnectionSettingsModel;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class BusinessMannager {
	public String m_preDeviceImei = new String();//used ui when changed device,when ui used once,set current imei
	
	private static final String TAG = "BusinessMannager";
	private Context m_context = null;
	
	private SystemManager m_systemManager = null;
	private UserManager m_userManager = null;
	private StatisticsManager m_statisticsManager = null;
	private SimManager m_simManager = null;
	private NetworkManager m_networkManager = null;
	private SMSManager m_smsManager = null;
	private WanManager m_wanManager = null;
	private WlanManager m_wlanManager = null;
	private SharingManager m_sharingManager = null;
	private PowerManager m_powerManager=null;
	private LanManager m_lanManager=null;
	private UpdateManager m_updateManager=null;
	private DeviceManager m_deviceManager = null;
	private ProfileManager m_profileManager = null;
	
	private String m_strServerAddress = "192.168.1.1";
	
	public WifiNetworkReceiver m_wifiNetworkReceiver = null;
	private SystemInfoModel m_systemInfoModel=null;
	
	public HashMap<String, BaseManager> m_class = new HashMap<String, BaseManager>();
	
	private static BusinessMannager m_instance;
    public static synchronized   BusinessMannager getInstance()
    {
        if(m_instance == null) {
        	Log.d(TAG, "new BusinessMannager()");
            m_instance = new BusinessMannager();
            m_instance.init();
        }
        return m_instance;
    }
    
    private BusinessMannager() {	
    }  
    
    private void init() {
    	m_context = SmartLinkV3App.getInstance().getApplicationContext();
    	DataConnectManager.getInstance().setContext(m_context);
    	MessageUti.intHttpMethods();
    	
    	m_systemManager = new SystemManager(m_context);
    	m_userManager = new UserManager(m_context);
    	m_statisticsManager = new StatisticsManager(m_context);
    	m_simManager = new SimManager(m_context);
    	m_networkManager = new NetworkManager(m_context);
    	m_smsManager = new SMSManager(m_context);
    	m_wanManager = new WanManager(m_context);
    	m_wlanManager = new WlanManager(m_context);
    	m_sharingManager = new SharingManager(m_context);
    	
    	m_updateManager = new UpdateManager(m_context);
    	m_lanManager = new LanManager(m_context);
    	m_powerManager = new PowerManager(m_context);
    	
    	m_deviceManager = new DeviceManager(m_context);
    	
    	m_profileManager = new ProfileManager(m_context);
    	
    	m_class.put("SystemManager", m_systemManager);
    	m_class.put("UserManager", m_userManager);
    	m_class.put("StatisticsManager", m_statisticsManager);
    	m_class.put("SimManager", m_simManager);
    	m_class.put("NetworkManager", m_networkManager);
    	m_class.put("SMSManager", m_smsManager);
    	m_class.put("WanManager", m_wanManager);
    	m_class.put("WlanManager", m_wlanManager);
    	m_class.put("SharingManager", m_sharingManager);
    	m_class.put("UpdateManager", m_updateManager);
    	m_class.put("LanManager", m_lanManager);
    	m_class.put("PowerManager", m_powerManager);
    	m_class.put("DeviceManager", m_deviceManager);
    	m_class.put("ProfileManager", m_profileManager);
    	
    	m_systemInfoModel = new SystemInfoModel();
    	m_wifiNetworkReceiver = new WifiNetworkReceiver();
    	m_context.registerReceiver(m_wifiNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    
    public void setServerAddress(String strIp) {
    	m_strServerAddress = strIp;
    }
    
    public String getServerAddress() {
    	return m_strServerAddress;
    }
    
    //Be extended:Such as power-saving mode can not send the request
    private boolean isAllowedSendRequest() {
    	return true;
    }
    
    //send http request message
    //param:strMessageId: MessageUti defined message string
    public void sendRequestMessage(String strMessageId,DataValue data) {
    	if(!isAllowedSendRequest()) {
    		Log.d(TAG, "Do not allowed send http request!!!");
    		return;
    	}
    	
    	try {
    		HttpMethodUti httpMethod = MessageUti.httpMethods.get(strMessageId);
    		if(httpMethod == null) {
    			Log.d(TAG,"MessageUti.httpMethods map have not http message:" + strMessageId);
    		}else{
    			
    			BaseManager mclass = m_class.get(httpMethod.getManagerClassName());
    			Method method = mclass.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    			method.invoke(mclass, data);
    			/*
    			//System manager
    			if(SystemManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_systemManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_systemManager, data);
    			}
    			//User manager
    			if(UserManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_userManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_userManager, data);
    			}
    			//Statistics manager
    			if(StatisticsManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_statisticsManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_statisticsManager, data);
    			}
    			
    			//Sim manager
    			if(SimManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_simManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_simManager, data);
    			}
    			
    			//Network manager
    			if(NetworkManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_networkManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_networkManager, data);
    			}
    			//SMS manager
    			if(SMSManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_smsManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_smsManager, data);
    			}
    			//Wan Manager
    			if(WanManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_wanManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_wanManager, data);
    			}
    			
    			//Wlan Manager
    			if(WlanManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_wlanManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_wlanManager, data);
    			}
    			
    			//Service Manager
    			if(SharingManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_sharingManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_sharingManager, data);
    			}
    			
    			//Device Manager
    			if(DeviceManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_deviceManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_deviceManager, data);
    			}
    			
    			//power Manager
    			if(PowerManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_powerManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_powerManager, data);
    			}
    			
    			//Lan Manager
    			if(LanManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_lanManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_lanManager, data);
    			}
    			
    			//update Manager
    			if(UpdateManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_updateManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_updateManager, data);
    			}
    			*/
    			//to add manager
    		}
		} catch (NoSuchMethodException e) {
			Log.d(TAG,"No have this method:" + strMessageId);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			
		} catch (IllegalAccessException e) {
			
		} catch (InvocationTargetException e) {
			
		} catch (Exception e) {
			
		}
    }
    
    /********************System manager Data start**********************/
    public Features getFeatures(){
    	return m_systemManager.getFeatures();
    }
    
    public SystemInfo getSystemInfo() {
		return m_systemManager.getSystemInfoModel();
	}
    
	public boolean getAlreadyRecongniseDeviceFlag() {
		return m_systemManager.getAlreadyRecongniseDeviceFlag();
	}
	
	public String getAppVersion(){
		return m_systemManager.getAppVersion();
	}
	
	public SystemInfoModel getSystemInfoModel(){
		return m_systemInfoModel;
	}
	
	public SystemStatus getSystemStatus(){
		return m_systemManager.getSystemStatus();
	}
	
	public RestoreError getRestoreError(){
		return m_systemManager.getRestoreError();
	}
    /********************System method end************************/
    
    /********************User manager Data start**********************/
	public UserLoginStatus getLoginStatus() {
		return m_userManager.getLoginStatus();
	}	
    /********************User manager Data end************************/
    
    /********************Statistics manager Data start**********************/
    public UsageSettingModel getUsageSettings() {
		return m_statisticsManager.getUsageSettings();
	}
    
    public UsageRecordResult getUsageRecord() {
		return m_statisticsManager.getUsageRecord();
	}
    
    public long GetBillingMonthTotalUsage() {
    	return m_statisticsManager.GetBillingMonthTotalUsage();
    }
    /********************Statistics method end************************/
    
    /********************sim manager Data start**********************/
    public SimStatusModel getSimStatus() {
		return m_simManager.getSimStatus();
	}
    
    public AutoPinState getAutoPinState() {
		return m_simManager.getAutoPinState();
	}
    /********************sim method end************************/
    
    /********************network manager Data start**********************/
    public NetworkManager getNetworkManager() {
		return m_networkManager;
	}
    
    public NetworkInfoModel getNetworkInfo() {
		return m_networkManager.getNetworkInfo();
	}
    /********************network manager method end************************/
    
    /********************profile manager method start*************************/
    public ProfileManager getProfileManager(){
    	return m_profileManager;
    }
    /********************profile manager method end*************************/
    
    /********************sms manager Data start**********************/
    public SMSInit getSMSInit() {
		return m_smsManager.getSMSInit();
	}
    
    public int getNewSmsNumber() {    	
    	return m_smsManager.GetUnreadSmsNumber();    	
    }
    
    public void getContactMessagesAtOnceRequest() {
    	m_smsManager.getContactMessagesAtOnceRequest();
    }
    
    public SmsContactMessagesModel getContactMessages() {
		return m_smsManager.getContactMessages();
	}
    /********************sms manager method end************************/
    
    /********************wan manager Data start**********************/
    public ConnectStatusModel getConnectStatus() {
		return m_wanManager.getConnectStatus();
	}
    
    public ConnectionSettingsModel getConnectSettings() {
		return m_wanManager.getConnectSettings();
	}
    /********************wan manager method end************************/
    
    /********************wlan manager Data start**********************/
    
    public String getSsid() {
  		return m_wlanManager.getSsid();
  	} 
       
    public String getWifiPwd() {
  		return m_wlanManager.getWifiPwd();
  	}     
    
    public SecurityMode getSecurityMode() {
		return m_wlanManager.getSecurityMode();
	}
	
	public WPAEncryption getWPAEncryption() {
		return m_wlanManager.getWPAEncryption();
	}
	
	public WEPEncryption getWEPEncryption() {
		return m_wlanManager.getWEPEncryption();
	}
	
	public WModeEnum getWMode() {
		return m_wlanManager.getWMode();
	}
	
	public String getSsid_5G() {
  		return m_wlanManager.getSsid_5G();
  	} 
       
    public String getWifiPwd_5G() {
  		return m_wlanManager.getWifiPwd_5G();
  	}     
    
    public SecurityMode getSecurityMode_5G() {
		return m_wlanManager.getSecurityMode_5G();
	}
	
	public WPAEncryption getWPAEncryption_5G() {
		return m_wlanManager.getWPAEncryption();
	}
	
	public WEPEncryption getWEPEncryption_5G() {
		return m_wlanManager.getWEPEncryption();
	}
	
	public WModeEnum getWMode_5G() {
		return m_wlanManager.getWMode();
	}
	
	public WlanFrequency getWlanFrequency() {
		return m_wlanManager.getWlanFrequency();
	}
	
	public WlanSupportMode getWlanSupportMode(){
		return m_wlanManager.getCurSupportWlanMode();
	}
	
	public WlanSettingResult getWlanSettingResult(){
		return m_wlanManager.getWlanSettingResult();
	}
	
	public SsidHiddenEnum getSsidStatus(){
		return m_wlanManager.getSsidHidden();
	}
	
	public SsidHiddenEnum getSsidStatus_5G(){
		return m_wlanManager.getSsidHidden_5G();
	}
    /********************wlan manager method end************************/      
    
    
    /********************sharing manager method start**********************/
	public DlnaSettings getDlnaSettings() {
		return m_sharingManager.getDlnaSettings();
	}
	
	public FtpSettings getFtpSettings(){
		return m_sharingManager.getFtpSettings();
	}
	
	public void StartRefreshingSharingStatus(){
		m_sharingManager.startGetDlnaStatus();
		m_sharingManager.startGetFtpStatus();
	}
	public void StopRefreshingSharingStatus(){
		m_sharingManager.stopRollTimer();
	}
	
	public SambaSettings getSambaSettings() {
		return m_sharingManager.getSambaSettings();
	}	
	
	public SDCardSpace getSDCardSpace()
	{
		return m_sharingManager.getSDCardSpace();
	}
	
	public SDcardStatus getSDCardStatus()
	{
		return m_sharingManager.getSDCardStatus();
	}
	
	public String getSambaPath()
	{
		return String.format(ConstValue.SMB_SERVER_ADDRESS, m_strServerAddress);
	}	
    /********************sharing manager method end************************/   
	/*Power manager start*/
	public BatteryInfo getBatteryInfo(){
		return m_powerManager.getBatteryInfo();
	}
	
	public PowerSavingModeInfo getPowerSavingModeInfo(){
		return m_powerManager.getPowerSavingModeInfo();
	}
	
	public DeviceNewVersionInfo getNewFirmwareInfo(){
		return m_updateManager.getCheckNewVersionInfo();
	}
	
	public DeviceUpgradeStateInfo getUpgradeStateInfo(){
		return m_updateManager.getUpgradeStatusInfo();
	}
	/*Power manager end***/
	
	
	  
    /********************Device manager method start**********************/
	public ArrayList<ConnectedDeviceItemModel> getConnectedDeviceList()
	{
		return m_deviceManager.getConnectedDeviceList();
	}
	
	public ArrayList<ConnectedDeviceItemModel> getBlockDeviceList()
	{
		return m_deviceManager.getBlockDeviceList();
	}
	
	public void getGetConnectedDeviceTaskAtOnceRequest(){
		m_deviceManager.getGetConnectedDeviceTaskAtOnceRequest();
	}
	
	public void getGetBlockDeviceListTaskAtOnceRequest(){
		m_deviceManager.getGetBlockDeviceListTaskAtOnceRequest();
	}
    /********************Device manager method end************************/   

}
