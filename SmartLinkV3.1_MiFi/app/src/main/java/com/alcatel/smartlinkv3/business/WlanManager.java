package com.alcatel.smartlinkv3.business;

import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.system.HttpSystem.GetFeature;
import com.alcatel.smartlinkv3.business.wlan.HttpWlanSetting;
import com.alcatel.smartlinkv3.business.wlan.WlanNewSettingResult;
import com.alcatel.smartlinkv3.business.wlan.WlanSettingResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.SsidHiddenEnum;
import com.alcatel.smartlinkv3.common.ENUM.WlanSupportMode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SecurityMode;
import com.alcatel.smartlinkv3.common.ENUM.WEPEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WModeEnum;
import com.alcatel.smartlinkv3.common.ENUM.WPAEncryption;
import com.alcatel.smartlinkv3.common.ENUM.WlanFrequency;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;

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
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"GetNumOfHosts") != true)
			return;
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
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"GetWlanSettings") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpWlanSetting.GetWlanSetting("5.4",
						new IHttpFinishListener() {
					@Override
					public void onHttpRequestFinish(
							BaseResponse response) {

						String strErrcode = new String();
						int ret = response.getResultCode();
						if (ret == BaseResponse.RESPONSE_OK) {
							strErrcode = response.getErrorCode();
							if (strErrcode.length() == 0) {
								m_settings = response.getModelResult();

								getInfoByWansetting();

							} else {

							}
						} else {
							// Log
						}

						Intent megIntent = new Intent(
								MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
						megIntent.putExtra(MessageUti.RESPONSE_RESULT,
								ret);
						megIntent.putExtra(
								MessageUti.RESPONSE_ERROR_CODE,
								strErrcode);
						m_context.sendBroadcast(megIntent);
					}
				}));
	}

	// set wlan setting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void setWlanSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"SetWlanSettings") != true)
			return;

		int nWlanAPMode = (Integer)data.getParamByKey("WlanAPMode");
		String strSsid = data.getParamByKey("Ssid").toString();
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
			settings.SsidHidden = nSsidStatus;
			settings.ApStatus_2G=1;
			settings.ApStatus_5G=0;
			bl24GHZ = true;
		}else if (WlanFrequency.Frequency_5GHZ == frequencyMode) {
			settings.Ssid_5G = strSsid;
			settings.SsidHidden_5G = nSsidStatus;
			settings.ApStatus_2G=0;
			settings.ApStatus_5G=1;
			bl5GHZ = true;
		}else{
			settings.Ssid = strSsid;
			settings.Ssid_5G = strSsid;
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
		
		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpWlanSetting.SetWlanSetting("5.5", settings,
						new IHttpFinishListener() {
					@Override
					public void onHttpRequestFinish(
							BaseResponse response) {

						String strErrcode = new String();
						int ret = response.getResultCode();
						if (ret == BaseResponse.RESPONSE_OK) {
							strErrcode = response.getErrorCode();
							if (strErrcode.length() == 0) {
								m_settings.clone(settings);
								getInfoByWansetting();
							} else {

							}
						} else {
							// Log
						}

						Intent megIntent = new Intent(
								MessageUti.WLAN_SET_WLAN_SETTING_REQUSET);
						megIntent.putExtra(MessageUti.RESPONSE_RESULT,
								ret);
						megIntent.putExtra(
								MessageUti.RESPONSE_ERROR_CODE,
								strErrcode);
						m_context.sendBroadcast(megIntent);
					}
				}));
	}


	// Set WPS Pin
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void SetWPSPin(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"SetWPSPin") != true)
			return;

		String m_strPin = (String) data.getParamByKey("WpsPin");

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpWlanSetting.SetWPSPin("5.6",m_strPin,
						new IHttpFinishListener() {
					@Override
					public void onHttpRequestFinish(
							BaseResponse response) {

						String strErrcode = new String();
						int ret = response.getResultCode();
						if (ret == BaseResponse.RESPONSE_OK) {
							strErrcode = response.getErrorCode();
							if (strErrcode.length() == 0) {
								m_strWpsPin = response.getModelResult();

							} else {

							}
						} else {
							// Log
						}

						Intent megIntent = new Intent(
								MessageUti.WLAN_SET_WPS_PIN_REQUSET);
						megIntent.putExtra(MessageUti.RESPONSE_RESULT,
								ret);
						megIntent.putExtra(
								MessageUti.RESPONSE_ERROR_CODE,
								strErrcode);
						m_context.sendBroadcast(megIntent);
					}
				}));
	}

	// Set WPS Pbc
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void SetWPSPbc(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"SetWPSPbc") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpWlanSetting.SetWPSPbc("5.7",
						new IHttpFinishListener() {
					@Override
					public void onHttpRequestFinish(
							BaseResponse response) {

						String strErrcode = new String();
						int ret = response.getResultCode();
						if (ret == BaseResponse.RESPONSE_OK) {
							strErrcode = response.getErrorCode();
							if (strErrcode.length() == 0) {

							} else {

							}
						} else {
							// Log
						}

						Intent megIntent = new Intent(
								MessageUti.WLAN_SET_WPS_PBC_REQUSET);
						megIntent.putExtra(MessageUti.RESPONSE_RESULT,
								ret);
						megIntent.putExtra(
								MessageUti.RESPONSE_ERROR_CODE,
								strErrcode);
						m_context.sendBroadcast(megIntent);
					}
				}));
	}

	//get wlan support mode
	public void getWlanSupportMode(DataValue data){
		String strDeviceName = BusinessMannager.getInstance().getFeatures().getDeviceName();
		if (!FeatureVersionManager.getInstance().
				isSupportApi("Wlan", "GetWlanSupportMode")) {
			if (0 != strDeviceName.compareToIgnoreCase("Y900")) {
				m_wlanSupportMode = WlanSupportMode.Mode2Point4G;
				return;				
			}
		}

		boolean blCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blCPEWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpWlanSetting.getWlanSupportModeRequest("5.8", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRes = response.getResultCode();
							String strErr = response.getErrorMessage();
							if (BaseResponse.RESPONSE_OK == nRes && 0 == strErr.length()) {
								m_wlanSupportMode = response.getModelResult();
							}else{
								getWlanSupportMode(null);
							}
							
							Intent megIntent = new Intent(MessageUti.WLAN_SET_WPS_PBC_REQUSET);
							megIntent.putExtra(MessageUti.RESPONSE_RESULT,nRes);
							megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE,strErr);
							m_context.sendBroadcast(megIntent);
						}
					}));
		}
	}
}
