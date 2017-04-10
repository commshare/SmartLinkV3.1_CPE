package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.wlan.HttpWlanSetting;
import com.alcatel.smartlinkv3.business.wlan.WlanNewSettingResult;
import com.alcatel.smartlinkv3.business.wlan.WlanSettingResult;
import com.alcatel.smartlinkv3.business.wlan.WlanSupportModeType;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.SsidHiddenEnum;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WModeEnum;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;
import com.alcatel.smartlinkv3.common.ENUM.WlanSupportMode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class WlanManager extends BaseManager {
	private int m_nHostNum = 0;
	private String m_strSsid = new String();
	private String m_strSsid_5G = new String();
	private String m_strWifiPwd = new String();
	private String m_strWifiPwd_5G = new String();

	private WlanSettingResult m_settings = new WlanSettingResult();

	private Timer m_rollTimer = new Timer();
	GetHostNumTask m_getHostNumTask = null;

	private String m_strWpsPin = new String();

	private WlanSupportMode m_wlanSupportMode=WlanSupportMode.Mode2Point4G;


	@Override
	protected void clearData() {
		m_nHostNum = 0;
		m_settings.clear();
		m_wlanSupportMode=WlanSupportMode.Mode2Point4G;
	}

	@Override
	protected void stopRollTimer() {
		/*m_getHostNumRollTimer.cancel();
		m_getHostNumRollTimer.purge();
		m_getHostNumRollTimer = new Timer();*/
		if(m_getHostNumTask != null) {
			m_getHostNumTask.cancel();
			m_getHostNumTask = null;
		}		
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected == true) {
				startGetHostNumTask();
				getWlanSupportMode(null);
				getWlanSetting(null);
			}else {
				m_wlanSupportMode = WlanSupportMode.Mode2Point4G;
			}
		}
	}

	public WlanManager(Context context) {
		super(context);
		// CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already
		// register in basemanager
	}


	public String getSsid() {
		return m_strSsid;
	}

	public String getSsid_5G(){
		return m_strSsid_5G;
	}
	
	public String getWifiPwd() {
		return m_strWifiPwd;
	}
	
	public String getWifiPwd_5G(){
		return m_strWifiPwd_5G;
	}

	public WlanSupportMode getCurSupportWlanMode(){
		return m_wlanSupportMode;
	}
	public SecurityMode getSecurityMode() {
		return SecurityMode.build(m_settings.SecurityMode);
	}
	
	public SecurityMode getSecurityMode_5G() {
			return SecurityMode.build(m_settings.SecurityMode_5G);
	}

	public WPAEncryption getWPAEncryption() {
		return WPAEncryption.build(m_settings.WpaType);
	}
	
	public WPAEncryption getWPAEncryption_5G() {
			return WPAEncryption.build(m_settings.WpaType_5G);
	}

	public WEPEncryption getWEPEncryption() {
		return WEPEncryption.build(m_settings.WepType);
	}
	
	public WEPEncryption getWEPEncryption_5G() {
			return WEPEncryption.build(m_settings.WepType_5G);
	}

	public WModeEnum getWMode() {
		return WModeEnum.build(m_settings.WMode);
	}
	
	public WModeEnum getWMode_5G() {
			return WModeEnum.build(m_settings.WMode_5G);
	}

	public WlanFrequency getWlanFrequency() {
		return WlanFrequency.build(m_settings.WlanAPMode);
	}

	public WlanSettingResult getWlanSettingResult(){
		return m_settings;
	}
	
	public SsidHiddenEnum getSsidHidden() {
		return SsidHiddenEnum.build(m_settings.SsidHidden);
	}
	
	public SsidHiddenEnum getSsidHidden_5G() {
		return SsidHiddenEnum.build(m_settings.SsidHidden_5G);
	}

    public String getCountryCode(){
        return m_settings.CountryCode;
    }
	
	private void getInfoByWansetting() {
		m_strSsid = m_settings.Ssid;
		m_strSsid_5G = m_settings.Ssid_5G;

		SecurityMode secMode = SecurityMode.build(m_settings.SecurityMode);
		switch (secMode) {
		case WEP:
		case Disable:
			m_strWifiPwd = m_settings.WepKey;
			break;
		case WPA:
		case WPA2:
		case WPA_WPA2:
			m_strWifiPwd = m_settings.WpaKey;
			break;

		default:
			m_strWifiPwd = "";
			break;
		}
		

		secMode = SecurityMode.build(m_settings.SecurityMode_5G);
		switch (secMode) {
		case WEP:
		case Disable:
			m_strWifiPwd_5G = m_settings.WepKey_5G;
			break;
		case WPA:
		case WPA2:
		case WPA_WPA2:
			m_strWifiPwd_5G = m_settings.WpaKey_5G;
			break;

		default:
			m_strWifiPwd_5G = "";
			break;
		}

	}

	// GetNumOfHosts
	// //////////////////////////////////////////////////////////////////////////////////////////
	private void startGetHostNumTask() {
		if(m_getHostNumTask == null) {
			m_getHostNumTask = new GetHostNumTask();
			m_rollTimer.scheduleAtFixedRate(m_getHostNumTask, 0, 10 * 1000);
		}
	}

	class GetHostNumTask extends TimerTask {
		@Override
		public void run() {}
	}

	// get wlan setting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getWlanSetting(DataValue data) {
		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpWlanSetting.GetWlanSetting(new IHttpFinishListener() {
					@Override
					public void onHttpRequestFinish(BaseResponse response) {
							if (response.isOk()) {
								m_settings = response.getModelResult();
								getInfoByWansetting();
							}
					}
				}));
	}

	// set wlan setting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setWlanSetting(DataValue data) {
		int nWlanAPMode = (Integer)data.getParamByKey("WlanAPMode");
		String strSsid = data.getParamByKey("Ssid").toString();
		String strCountryCode = data.getParamByKey("CountryCode").toString();
		String strPassword = (String) data.getParamByKey("Password");
		Integer nSecurity = (Integer)data.getParamByKey("Security");
		Integer nEncryption = (Integer)data.getParamByKey("Encryption");
		Integer nSsidStatus = (Integer) data.getParamByKey("SsidStatus");
		final WlanSettingResult settings = new WlanSettingResult();
		final WlanNewSettingResult Newsettings = new WlanNewSettingResult();
		settings.clone(m_settings);
		settings.WlanAPMode = nWlanAPMode;
		WlanFrequency frequencyMode = WlanFrequency.build(nWlanAPMode);
		boolean bl24GHZ = false;
		boolean bl5GHZ = false;
		if (WlanFrequency.Frequency_24GHZ == frequencyMode) {
			settings.Ssid = strSsid;
			settings.CountryCode = strCountryCode;
			settings.SsidHidden = nSsidStatus;
			settings.ApStatus_2G=1;
			settings.ApStatus_5G=0;
			bl24GHZ = true;
		}else if (WlanFrequency.Frequency_5GHZ == frequencyMode) {
			settings.Ssid_5G = strSsid;
            settings.CountryCode = strCountryCode;
			settings.SsidHidden_5G = nSsidStatus;
			settings.ApStatus_2G=0;
			settings.ApStatus_5G=1;
			bl5GHZ = true;
		}else{
			settings.Ssid = strSsid;
			settings.Ssid_5G = strSsid;
            settings.CountryCode = strCountryCode;
			settings.SsidHidden = nSsidStatus;
			settings.SsidHidden_5G = nSsidStatus;
			bl24GHZ = true;
			bl5GHZ = true;
		}

		if (bl24GHZ) {
			settings.SecurityMode = nSecurity;
			if(settings.SecurityMode != SecurityMode.antiBuild(SecurityMode.Disable)) {
				if(settings.SecurityMode == SecurityMode.antiBuild(SecurityMode.WEP)) {
					settings.WepType = nEncryption;
					settings.WepKey = strPassword;
				}else{
					settings.WpaType = nEncryption;
					settings.WpaKey = strPassword;
				}
			}
		}

		if (bl5GHZ) {
			settings.SecurityMode_5G = nSecurity;
			if(settings.SecurityMode_5G != SecurityMode.antiBuild(SecurityMode.Disable)) {
				if(settings.SecurityMode_5G == SecurityMode.antiBuild(SecurityMode.WEP)) {
					settings.WepType_5G = nEncryption;
					settings.WepKey_5G = strPassword;
				}else{
					settings.WpaType_5G = nEncryption;
					settings.WpaKey_5G = strPassword;
				}
			}

		}
		
		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpWlanSetting.SetWlanSetting(settings,
						new IHttpFinishListener() {
					@Override
					public void onHttpRequestFinish(BaseResponse response) {
							if (response.isOk()) {
								m_settings.clone(settings);
								getInfoByWansetting();
							}
//						sendBroadcast(response, MessageUti.WLAN_SET_WLAN_SETTING_REQUSET);
					}
				}));
	}


	// Set WPS Pin
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void SetWPSPin(DataValue data) {
		String m_strPin = (String) data.getParamByKey("WpsPin");

		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpWlanSetting.SetWPSPin(m_strPin,
					 response-> {
						if (response.isOk()) {
								m_strWpsPin = response.getModelResult();
						}

//						sendBroadcast(response, MessageUti.WLAN_SET_WPS_PIN_REQUSET);
				}));
	}

	// Set WPS Pbc
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void SetWPSPbc(DataValue data) {
		LegacyHttpClient.getInstance().sendPostRequest(
				new HttpWlanSetting.SetWPSPbc(response-> {
//						sendBroadcast(response, MessageUti.WLAN_SET_WPS_PBC_REQUSET);
				}));
	}

	//get wlan support mode
	public void getWlanSupportMode(DataValue data){
		String strDeviceName = BusinessManager.getInstance().getFeatures().getDeviceName();
		if (!FeatureVersionManager.getInstance().isSupportApi("Wlan", "GetWlanSupportMode")) {
			if (0 != strDeviceName.compareToIgnoreCase("Y900")) {
				m_wlanSupportMode = WlanSupportMode.Mode2Point4G;
				return;				
			}
		}

		boolean blCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blCPEWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpWlanSetting.getWlanSupportModeRequest( response -> {
							if (response.isOk()) {
								WlanSupportModeType modeType = response.getModelResult();
								m_wlanSupportMode = WlanSupportMode.build(modeType.getWlanSupportMode());
							}else{
								getWlanSupportMode(null);
							}
							
//							sendBroadcast(response, MessageUti.WLAN_SET_WPS_PBC_REQUSET);
					}));
		}
	}
}
