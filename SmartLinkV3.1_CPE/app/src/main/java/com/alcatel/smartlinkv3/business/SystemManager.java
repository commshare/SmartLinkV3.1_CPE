package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

import com.alcatel.smartlinkv3.business.system.Features;
import com.alcatel.smartlinkv3.business.system.HttpSystem;
import com.alcatel.smartlinkv3.business.system.RestoreError;
import com.alcatel.smartlinkv3.business.system.SystemInfo;
import com.alcatel.smartlinkv3.business.system.SystemStatus;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class SystemManager extends BaseManager {
	private Features m_features = new Features();
	private SystemInfo m_systemInfo = new SystemInfo();
	private SystemStatus m_systemStatus = new SystemStatus();

	private boolean m_bAlreadyRecogniseCPEDevice = false;// whether recognise
	// this device

	private Timer m_getFeaturesTimer = new Timer();// this time not to cancel
	// when cpe wifi
	// disconnected,beacause
	// used to test the wifi
	// connect state
//	private Timer m_getStorageTimer = new Timer();
	private Timer m_getSystemStatusTimer = new Timer();

	private String m_strAppVersion = "";

	private GetFeaturesTask m_getFeaturesTask = null;
	private GetSystemStatusTask m_getSystemStatusTask = null;
	
	private RestoreError m_errorInfo=new RestoreError();

	public Features getFeatures() {
		return m_features;
	}
	
	public SystemInfo getSystemInfoModel() {
		return m_systemInfo;
	}

	public SystemStatus getSystemStatus() {
		return m_systemStatus;
	}

	public boolean getAlreadyRecongniseDeviceFlag() {
		return m_bAlreadyRecogniseCPEDevice;
	}

	public String getAppVersion() {
		return m_strAppVersion;
	}
	
	public RestoreError getRestoreError(){
		return m_errorInfo;
	}

	@Override
	protected void clearData() {	
		m_features.clear();
		m_systemInfo.clear();
		m_systemInfo.clear();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if (intent.getAction().equals(MessageUti.CPE_BUSINESS_STATUS_CHANGE)) {
			m_bStopBusiness = intent.getBooleanExtra("stop", false);
			switchBusiness();
		}

		if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected == true) {
				getSystemInfo();
				startSystemStatusTask();

			} else {
				stopRollTimer();			
				m_systemInfo.clear();
				m_systemStatus.clear();
				BusinessManager.getInstance().getSystemInfoModel().clear();
			}
		}
	}

	public SystemManager(Context context) {
		super(context);
		// CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already
		// register in basemanager
		// m_context.registerReceiver(m_msgReceiver, new
		// IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));

		//app version
		FetchAppVersion();
	}

	private void FetchAppVersion(){
		PackageManager	manager = m_context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(m_context.getPackageName(), 0);
			m_strAppVersion = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	protected void stopRollTimer() {
		
		if (null != m_getSystemStatusTask) {
			m_getSystemStatusTask.cancel();
			m_getSystemStatusTask = null;
		}
	}

	public void switchBusiness() {
		if (m_bStopBusiness) {
			m_bAlreadyRecogniseCPEDevice = false;
			if (m_getFeaturesTask != null) {
				m_getFeaturesTask.cancel();
				m_getFeaturesTask = null;
			}
			// m_getFeaturesTimer.cancel();
			// m_getFeaturesTimer.purge();
			// m_getFeaturesTimer = new Timer();
		} else {
			// GetFeaturesTask getFeaturesTask = new GetFeaturesTask();
			// m_getFeaturesTimer.scheduleAtFixedRate(getFeaturesTask, 0, 5000);
			if (m_getFeaturesTask == null) {
				m_getFeaturesTask = new GetFeaturesTask();
				m_getFeaturesTimer.scheduleAtFixedRate(m_getFeaturesTask, 0,
						10*1000);
			}
		}
	}

	// Get feature list
	// //////////////////////////////////////////////////////////////////////////////////////////
	class GetFeaturesTask extends TimerTask {
		@Override
		public void run() {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.GetFeature(new IHttpFinishListener() {
						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							if (response.isOk()) {
									m_features = response.getModelResult();
							}

							m_bAlreadyRecogniseCPEDevice = true;

							// change billing month,because to next billing
							// month
							//							SimpleDateFormat startTemp = new SimpleDateFormat(
							//									Const.DATE_FORMATE);
							//							Calendar caNow = Calendar.getInstance();
							//							String strNow = startTemp.format(caNow.getTime());
							//							if (!(BusinessManager.getInstance()
							//									.getUsageSettings().m_strStartBillDate
							//									.compareTo(strNow) <= 0 && BusinessManager
							//									.getInstance().getUsageSettings().m_strEndBillDate
							//									.compareTo(strNow) >= 0)) {
							//								BusinessManager.getInstance()
							//										.getUsageSettings()
							//										.calStartAndEndCalendar();
							//								Intent megIntent = new Intent(
							//										MessageUti.CPE_CHANGED_BILLING_MONTH);
							//								m_context.sendBroadcast(megIntent);
							//							}

							sendBroadcast(response, MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET);
						}
					}));
		}
	}

	// Get System info
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getSystemInfo() {
		if (FeatureVersionManager.getInstance().isSupportApi("System",
				"GetSystemInfo") != true)
			return;

		boolean bCPEWifiConnected = DataConnectManager.getInstance()
				.getCPEWifiConnected();
		if (bCPEWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.GetSystemInfo(new IHttpFinishListener() {
						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							int ret = response.getResultCode();
							String strErrcode = response.getErrorCode();
							if (ret == BaseResponse.RESPONSE_OK	&& strErrcode.length() == 0) {
								m_systemInfo = response.getModelResult();
								BusinessManager.getInstance().getSystemInfoModel().
								setDeviceName(m_systemInfo.getDeviceName());
								BusinessManager.getInstance().getSystemInfoModel().
								setHwVersion(m_systemInfo.getHwVersion());
								BusinessManager.getInstance().getSystemInfoModel().
								setSwVersion(m_systemInfo.getSwVersion());
								BusinessManager.getInstance().getSystemInfoModel().
								setIMEI(m_systemInfo.getIMEI());
							} else {
								new Handler().postDelayed(
										new Runnable() {
											@Override
											public void run() {
												getSystemInfo();
											}
										}, 1000);
							}

							sendBroadcast(response, MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET);
						}
					}));
		}
	}

	// startSystemStatusTask
		// //////////////////////////////////////////////////////////////////////////////////////////

		private void startSystemStatusTask() {
			if(FeatureVersionManager.getInstance().isSupportApi("System", "GetSystemStatus") != true)
				return;
			
			if(m_getSystemStatusTask == null) {
				m_getSystemStatusTask = new GetSystemStatusTask();
				m_getSystemStatusTimer.scheduleAtFixedRate(m_getSystemStatusTask, 0, 5 * 1000);
			}
		}

		class GetSystemStatusTask extends TimerTask {
			@Override
			public void run() {
				LegacyHttpClient.getInstance().sendPostRequest(
						new HttpSystem.GetSystemStatus(new IHttpFinishListener() {
							@Override
							public void onHttpRequestFinish(
									BaseResponse response) {
								int ret = response.getResultCode();
								String strErrcode = response.getErrorCode();
								if (ret == BaseResponse.RESPONSE_OK
										&& strErrcode.length() == 0) {
									m_systemStatus = response
											.getModelResult();
								} else {
//									new Handler().postDelayed(
//											new Runnable() {
//												@Override
//												public void run() {
//													getSystemStatus(null);
//												}
//											}, 1000);
								}

								sendBroadcast(response, MessageUti.SYSTEM_GET_SYSTEM_STATUS_REQUSET);
							}
						}));
				}
		}


	//device reboot
	public void rebootDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("System", "SetDeviceReboot")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.SetDeviceReboot(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_REBOOT);
						}
					}));
		}
	}

	//reset device
	public void resetDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("System", "SetDeviceReset")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.SetDeviceReset(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {

							sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_RESET);
						}
					}));
		}
	}

	//device backup
	public void backupDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("System", "SetDeviceBackup")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.SetDeviceBackup(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_BACKUP);
						}
					}));
		}
	}

	//device restore
	public void restoreDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("System", "SetDeviceRestore")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			String strFile = data.getParamByKey("FileName").toString();
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.SetDeviceRestore(strFile,
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							sendBroadcast(response,MessageUti.SYSTEM_SET_DEVICE_RESTORE);
						}
					}));
		}
	}

	public void setDevicePowerOff(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("System", "SetDevicePowerOff")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.setDevicePowerOffRequest(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							sendBroadcast(response, MessageUti.SYSTEM_SET_DEVICE_POWER_OFF);
						}
					}));
		}
	}
	
	public void setAppBackup(DataValue data){
//		if (!FeatureVersionManager.getInstance().
//				isSupportApi("System", "SetAppBackup")) {
//			return;
//		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.setAppBackupRequest(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							sendBroadcast(response, MessageUti.SYSTEM_SET_APP_BACKUP);
						}
					}));
		}
	}
	
	public void setAppRestoreBackup(DataValue data){
//		if (!FeatureVersionManager.getInstance().
//				isSupportApi("System", "SetAppRestoreBackup")) {
//			return;
//		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpSystem.setAppRestoreBackupRequest(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRet = response.getResultCode();
							String strError = response.getErrorCode();
							if (nRet == BaseResponse.RESPONSE_OK
									&& strError.length() == 0){
								m_errorInfo = response.getModelResult();
							}
							sendBroadcast(response, MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP);
						}
					}));
		}
	}
}
