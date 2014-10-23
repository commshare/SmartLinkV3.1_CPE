package com.alcatel.smartlinkv3.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.NetworkInfoModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageHistoryItemModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.service.DlnaSettings;
import com.alcatel.smartlinkv3.business.service.FtpSettings;
import com.alcatel.smartlinkv3.business.service.SambaSettings;
import com.alcatel.smartlinkv3.business.system.Features;
import com.alcatel.smartlinkv3.business.system.StorageList;
import com.alcatel.smartlinkv3.business.system.SystemInfo;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.HttpMethodUti;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.AutoPinState;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.ServiceState;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WModeEnum;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;

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
	private ServiceManager m_serviceManager = null;
	
	private String m_strServerAddress = "192.168.1.1";
	
	public WifiNetworkReceiver m_wifiNetworkReceiver = null;
	
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
    	m_serviceManager = new ServiceManager(m_context);
    	
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
    			if(ServiceManager.class.getSimpleName().equalsIgnoreCase(httpMethod.getManagerClassName())) {
    				Method method = m_serviceManager.getClass().getMethod(httpMethod.getMethodString(), DataValue.class);
    				method.invoke(m_serviceManager, data);
    			}
    			
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
    
	public StorageList getStorageList()
	{
		return m_systemManager.getStorageList();
	}
	
	public boolean getAlreadyRecongniseDeviceFlag() {
		return m_systemManager.getAlreadyRecongniseDeviceFlag();
	}
	
	public String getAppVersion(){
		return m_systemManager.getAppVersion();
	}
    /********************System method end************************/
    
    /********************User manager Data start**********************/
    public String getLoginString(){
    	return m_userManager.getLoginString();
    }
    
	public UserLoginStatus getLoginStatus() {
		return m_userManager.getLoginStatus();
	}	
    /********************User manager Data end************************/
    
    /********************Statistics manager Data start**********************/
    public UsageSettingModel getUsageSettings() {
		return m_statisticsManager.getUsageSettings();
	}
    
    public ArrayList<UsageHistoryItemModel> getUsageHistory() {
		return (ArrayList<UsageHistoryItemModel>) m_statisticsManager.getUsageHistory().clone();
	}
    
    public long GetTodayUsage() {
    	return m_statisticsManager.GetTodayUsage();
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
    public NetworkInfoModel getNetworkInfo() {
		return m_networkManager.getNetworkInfo();
	}
    /********************network manager method end************************/
    
    /********************sms manager Data start**********************/
    public int getNewSmsNumber() {    	
    	return m_smsManager.GetUnreadSmsNumber();    	
    }
    /********************sms manager method end************************/
    
    /********************wan manager Data start**********************/
    public ConnectStatusModel getConnectStatus() {
		return m_wanManager.getConnectStatus();
	}
    /********************wan manager method end************************/
    
    /********************wlan manager Data start**********************/
    public int getHostNum() {
		return m_wlanManager.getHostNum();
	}
    
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
	
	public WlanFrequency getWlanFrequency() {
		return m_wlanManager.getWlanFrequency();
	}
	
    /********************wlan manager method end************************/      
    
    
    /********************service manager Data start**********************/
    public ServiceState getDlnaServiceState() {
		return m_serviceManager.getDlnaServiceState();
	}
    
    public ServiceState getSambaServiceState() {
    	return m_serviceManager.getSambaServiceState();
	}
    
    public ServiceState getFtpServiceState() {
    	return m_serviceManager.getFtpServiceState();
	}  
    
	public DlnaSettings getDlnaSettings() {
		return m_serviceManager.getDlnaSettings();
	}
	
	public SambaSettings getSambaSettings() {
		return m_serviceManager.getSambaSettings();
	}
	
	public FtpSettings getFtpSettings() {
		return m_serviceManager.getFtpSettings();
	}
    /********************service manager method end************************/      
}
