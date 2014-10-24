package com.alcatel.smartlinkv3.business;

import java.util.Timer;
import java.util.TimerTask;


import com.alcatel.smartlinkv3.business.wlan.HttpWlanSetting;
import com.alcatel.smartlinkv3.business.wlan.WlanSettingResult;
import com.alcatel.smartlinkv3.common.DataValue;
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
	private String m_strWifiPwd = new String();

	private WlanSettingResult m_settings = new WlanSettingResult();

	private Timer m_rollTimer = new Timer();
	GetHostNumTask m_getHostNumTask = null;
	

	@Override
	protected void clearData() {
		m_nHostNum = 0;
		m_settings.clear();
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
			}
		}
	}

	public WlanManager(Context context) {
		super(context);
		// CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already
		// register in basemanager
	}

	public int getHostNum() {
		return m_nHostNum;
	}

	public String getSsid() {
		return m_strSsid;
	}

	public String getWifiPwd() {
		return m_strWifiPwd;
	}
	
	public SecurityMode getSecurityMode() {
		return SecurityMode.build(m_settings.SecurityMode);
	}
	
	public WPAEncryption getWPAEncryption() {
		return WPAEncryption.build(m_settings.WpaType);
	}
	
	public WEPEncryption getWEPEncryption() {
		return WEPEncryption.build(m_settings.WepType);
	}
	
	public WModeEnum getWMode() {
		return WModeEnum.build(m_settings.WMode);
	}
	
	public WlanFrequency getWlanFrequency() {
		return WlanFrequency.build(m_settings.WlanFrequency);
	}

	private void getInfoByWansetting() {
		WlanFrequency wf = WlanFrequency.build(m_settings.WlanFrequency);
		switch (wf) {
		case Frequency_24GHZ:
			m_strSsid = m_settings.Ssid;
			break;
		case Frequency_5GHZ:
			m_strSsid = m_settings.Ssid5G;
			break;

		default:
			m_strSsid = "";
			break;
		}

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
		public void run() {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpWlanSetting.GetWlanSetting("5.3",
							new IHttpFinishListener() {
								@Override
								public void onHttpRequestFinish(
										BaseResponse response) {
									String strErrcode = new String();
									int ret = response.getResultCode();
									if (ret == BaseResponse.RESPONSE_OK) {
										strErrcode = response.getErrorCode();
										if (strErrcode.length() == 0) {
											WlanSettingResult hostNumberResult = response
													.getModelResult();
											int preHostNum = m_nHostNum;
											m_nHostNum = hostNumberResult.NumOfHosts;
											if (preHostNum != m_nHostNum) {
												Intent megIntent = new Intent(
														MessageUti.WLAN_GET_NUMBER_OF_HOST_ROLL_REQUSET);
												megIntent
														.putExtra(
																MessageUti.RESPONSE_RESULT,
																ret);
												megIntent
														.putExtra(
																MessageUti.RESPONSE_ERROR_CODE,
																strErrcode);
												m_context
														.sendBroadcast(megIntent);
											}
										} else {

										}
									} else {
										// Log
									}
								}
							}));
		}
	}

	// get wlan setting
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getWlanSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"GetWlanSettings") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpWlanSetting.GetWlanSetting("5.3",
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

		String strPassword = (String) data.getParamByKey("Password");
		Integer nSecurity = (Integer)data.getParamByKey("Security");
		Integer nEncryption = (Integer)data.getParamByKey("Encryption");
		final WlanSettingResult settings = new WlanSettingResult();
		settings.clone(m_settings);
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
		HttpRequestManager.GetInstance().sendPostRequest(
				new HttpWlanSetting.SetWlanSetting("5.4", settings,
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
}
