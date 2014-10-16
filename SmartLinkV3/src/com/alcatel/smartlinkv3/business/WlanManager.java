package com.alcatel.smartlinkv3.business;

import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.wlan.HostNumberResult;
import com.alcatel.smartlinkv3.business.wlan.HttpHostInfo;
import com.alcatel.smartlinkv3.business.wlan.HttpWlanSetting;
import com.alcatel.smartlinkv3.business.wlan.WlanSettingResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SMSStoreIn;
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
	private String m_strMM100Ssid = new String();

	private WlanSettingResult m_settings = new WlanSettingResult();
	//private MM100WlanSettingResult m_mm100Settings = new MM100WlanSettingResult();
	//private MM100AccessPointsList m_mm100AccessPointsList = new MM100AccessPointsList();/*pchong*/
	//private MM100RemoteAPModel m_remoteAP = new MM100RemoteAPModel();

	private Timer m_rollTimer = new Timer();
	GetHostNumTask m_getHostNumTask = null;
	//GetMM100APListTask m_getMM100APList = null;
	//GetMM100RemoteAPTask m_getMM100RemoteAPTask = null;*//*pchong*/
	

	@Override
	protected void clearData() {
		m_nHostNum = 0;
		m_settings.clear();
		//m_mm100Settings.clear();
		//m_mm100AccessPointsList.clear();/*pchong*/
		//m_remoteAP.clear();
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
	/*
	public void requestAPInfo()
	{
		startGetMM100APListTask();
		startGetMM100RemoteAPTask();	
	}
	
	public void removeAPInfo()
	{
		if(m_getMM100APList != null) {
			m_getMM100APList.cancel();
			m_getMM100APList = null;
		}
		
		if(m_getMM100RemoteAPTask != null) {
			m_getMM100RemoteAPTask.cancel();
			m_getMM100RemoteAPTask = null;
		}	
	}*/

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected == true) {
				if(FeatureVersionManager.getInstance().isSupportDevice(FeatureVersionManager.VERSION_DEVICE_M100) == true) {
					//startGetMM100APListTask();
					//startGetMM100RemoteAPTask();
				}else{
					startGetHostNumTask();
				}
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
	
	/*
	public MM100RemoteAPModel getMM100RemoteAP() {
		return m_remoteAP;
	}
	
	public MM100AccessPointsList getMM100AccessPoints() {
		return m_mm100AccessPointsList;
	}
	
	public String getMM100MacAddress() {
		return m_mm100Settings.MacAddress;
	}

	public String getMM100Ssid() {

		WlanFrequency wf = WlanFrequency.build(m_mm100Settings.WlanFrequency);
		switch (wf) {
		case Frequency_24GHZ:
			m_strMM100Ssid = m_mm100Settings.Ssid;
			break;
		case Frequency_5GHZ:
			m_strMM100Ssid = m_mm100Settings.Ssid5G;
			break;

		default:
			m_strMM100Ssid = "";
			break;
		}
		return m_strMM100Ssid;
	}
*//*pchong*/
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
					new HttpHostInfo.GetNumOfHosts("5.6",
							new IHttpFinishListener() {
								@Override
								public void onHttpRequestFinish(
										BaseResponse response) {
									String strErrcode = new String();
									int ret = response.getResultCode();
									if (ret == BaseResponse.RESPONSE_OK) {
										strErrcode = response.getErrorCode();
										if (strErrcode.length() == 0) {
											HostNumberResult hostNumberResult = response
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

	// get MM100 wlan setting
	// //////////////////////////////////////////////////////////////////////////////////////////
	/*
	public void getMM100WlanSetting(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"GetWlanSettings") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(
				new MM100HttpWlanSetting.GetWlanSetting("2.1",
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {

								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										m_mm100Settings = response
												.getModelResult();

									} else {

									}
								} else {
									// Log
								}

								Intent megIntent = new Intent(
										MessageUti.WLAN_GET_MM100_WLAN_SETTING_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}

	// get MM100 Access point list
	// //////////////////////////////////////////////////////////////////////////////////////////
	private void startGetMM100APListTask() {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan","GetAccessPointsList") != true)
			return;
		if(m_getMM100APList == null) {
			m_getMM100APList = new GetMM100APListTask();
			m_rollTimer.scheduleAtFixedRate(m_getMM100APList, 0, 20 * 1000);
		}
	}

	class GetMM100APListTask extends TimerTask {
		@Override
		public void run() {
			Intent intent = new Intent(MessageUti.CPE_GET_MM100_ACCESS_POINTS_LIST_START);
			m_context.sendBroadcast(intent);
			
			HttpRequestManager.GetInstance().sendPostRequest(new MM100HttpWlanSetting.GetAccessPointsList("2.2",new IHttpFinishListener() {
				@Override
				public void onHttpRequestFinish(BaseResponse response) {
					String strErrcode = new String();
					int ret = response.getResultCode();
					if (ret == BaseResponse.RESPONSE_OK) {
						strErrcode = response.getErrorCode();
						if (strErrcode.length() == 0) {
							m_mm100AccessPointsList = response.getModelResult();
						} else {

						}
					} else {
						// Log
					}

					Intent megIntent = new Intent(MessageUti.WLAN_GET_MM100_ACCESS_POINTS_LIST_REQUSET);
					megIntent.putExtra(MessageUti.RESPONSE_RESULT,ret);
					megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE,strErrcode);
					m_context.sendBroadcast(megIntent);
				}
			}));
		}
	}
	
	// get MM100 remote ap
	// //////////////////////////////////////////////////////////////////////////////////////////
	private void startGetMM100RemoteAPTask() {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan","GetWlanRemoteAP") != true)
			return;
		if(m_getMM100RemoteAPTask == null) {
			m_getMM100RemoteAPTask = new GetMM100RemoteAPTask();
			m_rollTimer.scheduleAtFixedRate(m_getMM100RemoteAPTask, 0, 16 * 1000);
		}
	}

	private void getMM100RemoteAP(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan","GetWlanRemoteAP") != true)
			return;
		
		GetMM100RemoteAPTask task = new GetMM100RemoteAPTask();
		m_rollTimer.schedule(task, 0);
	}
	
	class GetMM100RemoteAPTask extends TimerTask {
		@Override
		public void run() {
			HttpRequestManager.GetInstance().sendPostRequest(new MM100HttpWlanSetting.GetWlanRemoteAP("2.4",new IHttpFinishListener() {
				@Override
				public void onHttpRequestFinish(BaseResponse response) {
					String strErrcode = new String();
					int ret = response.getResultCode();
					if (ret == BaseResponse.RESPONSE_OK) {
						strErrcode = response.getErrorCode();
						if (strErrcode.length() == 0) {
							MM100RemoteAPResult remoteAP = response.getModelResult();
							m_remoteAP.setValue(remoteAP);
						} else {

						}
					} else {
						// Log
					}

					Intent megIntent = new Intent(MessageUti.WLAN_GET_MM100_REMOTE_AP_REQUSET);
					megIntent.putExtra(MessageUti.RESPONSE_RESULT,ret);
					megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE,strErrcode);
					m_context.sendBroadcast(megIntent);
				}
			}));
		}
	}
		
	// Select Access Point
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void selectMM100AccessPoint(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan",
				"SelectAccessPoint") != true)
			return;
		
		int nId = (Integer) data.getParamByKey("id");
		String  password = (String) data.getParamByKey("password");
		
		String  strSsid = (String) data.getParamByKey("ssid");
		String  strEncrypt = (String) data.getParamByKey("encrypt");
		String  strTkipAes = (String) data.getParamByKey("tkip_aes");
		String  strChannel = (String) data.getParamByKey("channel");
		
		HttpRequestManager.GetInstance().sendPostRequest(
				new MM100HttpWlanSetting.SelectAccessPoint("2.3", nId, password,strSsid,strEncrypt,strTkipAes,strChannel,
						new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {

								String strErrcode = new String();
								int ret = response.getResultCode();
								if (ret == BaseResponse.RESPONSE_OK) {
									strErrcode = response.getErrorCode();
									if (strErrcode.length() == 0) {
										getMM100RemoteAP(null);
									} else {

									}
								} else {
									// Log
								}

								Intent megIntent = new Intent(
										MessageUti.WLAN_SELECT_MM100_ACCESS_POINT_REQUSET);
								megIntent.putExtra(MessageUti.RESPONSE_RESULT,
										ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
	}
	
	// Disconnect client Point
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void disconnectClientAP(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("Wlan","DisconnectClientAP") != true)
			return;

		HttpRequestManager.GetInstance().sendPostRequest(new MM100HttpWlanSetting.DisconnectClientAP("2.5",new IHttpFinishListener() {
			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				String strErrcode = new String();
				int ret = response.getResultCode();
				if (ret == BaseResponse.RESPONSE_OK) {
					strErrcode = response.getErrorCode();
					if (strErrcode.length() == 0) {
						getMM100RemoteAP(null);
					} else {

					}
				} else {
					// Log
				}

				Intent megIntent = new Intent(MessageUti.WLAN_DISSCONNECT_MM100_CLIENT_POINT_REQUSET);
				megIntent.putExtra(MessageUti.RESPONSE_RESULT,ret);
				megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE,strErrcode);
				m_context.sendBroadcast(megIntent);
			}
		}));
	}*//*pchong*/
}
