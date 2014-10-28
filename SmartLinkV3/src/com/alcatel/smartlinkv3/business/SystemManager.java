package com.alcatel.smartlinkv3.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.system.Features;
import com.alcatel.smartlinkv3.business.system.HttpSystem;
import com.alcatel.smartlinkv3.business.system.StorageList;
import com.alcatel.smartlinkv3.business.system.SystemInfo;
import com.alcatel.smartlinkv3.business.system.SystemStatus;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

public class SystemManager extends BaseManager {
	private Features m_features = new Features();
	private SystemInfo m_systemInfo = new SystemInfo();
	private StorageList m_storageList = new StorageList();
	private SystemStatus m_systemStatus = new SystemStatus();

	private boolean m_bAlreadyRecogniseCPEDevice = false;// whether recognise
	// this device

	private Timer m_getFeaturesTimer = new Timer();// this time not to cancel
	// when cpe wifi
	// disconnected,beacause
	// used to test the wifi
	// connect state
	private Timer m_getStorageTimer = new Timer();

	private String m_strAppVersion = "";

	private GetFeaturesTask m_getFeaturesTask = null;
	private GetExternalStorageDeviceTask m_getExternalStorageDeviceTask = null;

	public Features getFeatures() {
		return m_features;
	}

	public SystemInfo getSystemInfoModel() {
		return m_systemInfo;
	}
	
	public SystemStatus getSystemStatus() {
		return m_systemStatus;
	}

	public StorageList getStorageList() {
		return m_storageList;
	}

	public boolean getAlreadyRecongniseDeviceFlag() {
		return m_bAlreadyRecogniseCPEDevice;
	}

	public String getAppVersion() {
		return m_strAppVersion;
	}

	@Override
	protected void clearData() {	
		m_features.clear();
		m_systemInfo.clear();
		m_systemInfo.clear();
		m_storageList.clear();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if (intent.getAction().equals(MessageUti.CPE_BUSINESS_STATUS_CHANGE)) {
			m_bStopBusiness = intent.getBooleanExtra("stop", false);
			stwichBusiness();
		}

		if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected == true) {
				getSystemInfo(null);
				getSystemStatus(null);

				if (FeatureVersionManager.getInstance().isSupportApi("System",
						"GetExternalStorageDevice")) {
					startGetStorageTask();
				}
			} else {
				stopRollTimer();
				m_storageList.clear();
				m_systemInfo.clear();
				m_systemStatus.clear();
				BusinessMannager.getInstance().getSystemInfoModel().clear();
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
		PackageManager manager;
		PackageInfo info = null;
		manager = m_context.getPackageManager();
		try {
			info = manager.getPackageInfo(m_context.getPackageName(), 0);
			m_strAppVersion = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	protected void stopRollTimer() {
		/*
		 * m_getStorageTimer.cancel(); m_getStorageTimer.purge();
		 * m_getStorageTimer = new Timer();
		 */
		if (null != m_getExternalStorageDeviceTask) {
			m_getExternalStorageDeviceTask.cancel();
			m_getExternalStorageDeviceTask = null;
		}
	}

	public void stwichBusiness() {
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
						5000);
			}
		}
	}

	// Get feature list
	// //////////////////////////////////////////////////////////////////////////////////////////
	class GetFeaturesTask extends TimerTask {
		@Override
		public void run() {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpSystem.GetFeature("16.1", new IHttpFinishListener() {
						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							String strErrcode = new String();
							int ret = response.getResultCode();
							if (ret == BaseResponse.RESPONSE_OK) {
								strErrcode = response.getErrorCode();
								if (strErrcode.length() == 0) {
									m_features = response.getModelResult();
								} else {

								}
							} else {
								// Log
							}

							m_bAlreadyRecogniseCPEDevice = true;

							// change billing month,because to next billing
							// month
//							SimpleDateFormat startTemp = new SimpleDateFormat(
//									Const.DATE_FORMATE);
//							Calendar caNow = Calendar.getInstance();
//							String strNow = startTemp.format(caNow.getTime());
//							if (!(BusinessMannager.getInstance()
//									.getUsageSettings().m_strStartBillDate
//									.compareTo(strNow) <= 0 && BusinessMannager
//									.getInstance().getUsageSettings().m_strEndBillDate
//									.compareTo(strNow) >= 0)) {
//								BusinessMannager.getInstance()
//										.getUsageSettings()
//										.calStartAndEndCalendar();
//								Intent megIntent = new Intent(
//										MessageUti.CPE_CHANGED_BILLING_MONTH);
//								m_context.sendBroadcast(megIntent);
//							}

							Intent megIntent = new Intent(
									MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET);
							megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
							megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE,
									strErrcode);
							m_context.sendBroadcast(megIntent);
						}
					}));
		}
	}

	// Get System info
	// //////////////////////////////////////////////////////////////////////////////////////////
	public void getSystemInfo(DataValue data) {
		if (FeatureVersionManager.getInstance().isSupportApi("System",
				"GetSystemInfo") != true)
			return;

		boolean bCPEWifiConnected = DataConnectManager.getInstance()
				.getCPEWifiConnected();
		if (bCPEWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpSystem.GetSystemInfo("13.1",
							new IHttpFinishListener() {
						@Override
						public void onHttpRequestFinish(
								BaseResponse response) {
							int ret = response.getResultCode();
							String strErrcode = response.getErrorCode();
							if (ret == BaseResponse.RESPONSE_OK
									&& strErrcode.length() == 0) {
								m_systemInfo = response
										.getModelResult();
								BusinessMannager.getInstance().getSystemInfoModel().
								setDeviceName(m_systemInfo.getDeviceName());
								BusinessMannager.getInstance().getSystemInfoModel().
								setHwVersion(m_systemInfo.getHwVersion());
								BusinessMannager.getInstance().getSystemInfoModel().
								setSwVersion(m_systemInfo.getSwVersion());
								BusinessMannager.getInstance().getSystemInfoModel().
								setIMEI(m_systemInfo.getIMEI());
							} else {
								new Handler().postDelayed(
										new Runnable() {
											@Override
											public void run() {
												getSystemInfo(null);
											}
										}, 1000);
							}

							Intent megIntent = new Intent(
									MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET);
							megIntent.putExtra(
									MessageUti.RESPONSE_RESULT, ret);
							megIntent.putExtra(
									MessageUti.RESPONSE_ERROR_CODE,
									strErrcode);
							m_context.sendBroadcast(megIntent);
						}
					}));
		}
	}
	
	// Get System Status
		// //////////////////////////////////////////////////////////////////////////////////////////
		public void getSystemStatus(DataValue data) {
			if (FeatureVersionManager.getInstance().isSupportApi("System",
					"GetSystemStatus") != true)
				return;

			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected) {
				HttpRequestManager.GetInstance().sendPostRequest(
						new HttpSystem.GetSystemStatus("13.4",
								new IHttpFinishListener() {
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
									new Handler().postDelayed(
											new Runnable() {
												@Override
												public void run() {
													getSystemInfo(null);
												}
											}, 1000);
								}

								Intent megIntent = new Intent(
										MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUSET);
								megIntent.putExtra(
										MessageUti.RESPONSE_RESULT, ret);
								megIntent.putExtra(
										MessageUti.RESPONSE_ERROR_CODE,
										strErrcode);
								m_context.sendBroadcast(megIntent);
							}
						}));
			}
		}

	// GetExternalStorageDevice
	// //////////////////////////////////////////////////////////////////////////////////////////

	private void startGetStorageTask() {
		if (m_getExternalStorageDeviceTask == null) {
			m_getExternalStorageDeviceTask = new GetExternalStorageDeviceTask();
			m_getStorageTimer.scheduleAtFixedRate(
					m_getExternalStorageDeviceTask, 0, 10000);
		}
	}

	class GetExternalStorageDeviceTask extends TimerTask {
		@Override
		public void run() {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpSystem.GetExternalStorageDevice("2.7",
							new IHttpFinishListener() {
						@Override
						public void onHttpRequestFinish(
								BaseResponse response) {
							String strErrcode = new String();
							int ret = response.getResultCode();
							if (ret == BaseResponse.RESPONSE_OK) {
								strErrcode = response.getErrorCode();
								if (strErrcode.length() == 0) {
									m_storageList = response
											.getModelResult();

								} else {
									m_storageList.clear();
								}
							} else {
								m_storageList.clear();
							}

							Intent megIntent = new Intent(
									MessageUti.SYSTEM_GET_EXTERNAL_STORAGE_DEVICE_REQUSET);
							megIntent.putExtra(
									MessageUti.RESPONSE_RESULT, ret);
							megIntent.putExtra(
									MessageUti.RESPONSE_ERROR_CODE,
									strErrcode);
							m_context.sendBroadcast(megIntent);

						}
					}));
		}
	}

	//device reboot
	public void rebootDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("system", "SetDeviceReboot")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpSystem.deviceRebootRequest("13.5", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							Boolean blRes = false;
							int nRet = response.getResultCode();
							String strError = response.getErrorCode();
							if (BaseResponse.RESPONSE_OK == nRet && 0 != strError.length()) {
								blRes = true;
							}
							Intent intent = new Intent(MessageUti.SYSTEM_SET_DEVICE_REBOOT);
							intent.putExtra("Result", blRes);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}

	//reset device
	public void resetDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("system", "SetDeviceReset")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpSystem.deviceResetRequest("13.6", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							Boolean blRes = false;
							int nRet = response.getResultCode();
							String strError = response.getErrorCode();
							if (BaseResponse.RESPONSE_OK == nRet && 0 != strError.length()) {
								blRes = true;
							}
							Intent intent = new Intent(MessageUti.SYSTEM_SET_DEVICE_RESET);
							intent.putExtra("Result", blRes);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}

	//device backup
	public void backupDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("system", "SetDeviceBackup")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpSystem.deviceBackupRequest("13.7", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							Boolean blRes = false;
							int nRet = response.getResultCode();
							String strError = response.getErrorCode();
							if (BaseResponse.RESPONSE_OK == nRet && 0 != strError.length()) {
								blRes = true;
							}
							Intent intent = new Intent(MessageUti.SYSTEM_SET_DEVICE_BACKUP);
							intent.putExtra("Result", blRes);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}

	//device restore
	public void restoreDevice(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("system", "SetDeviceRestore")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			String strFile = data.getParamByKey("FileName").toString();
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpSystem.deviceRestoreRequest("13.8", strFile,
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							Boolean blRes = false;
							int nRet = response.getResultCode();
							String strError = response.getErrorCode();
							if (BaseResponse.RESPONSE_OK == nRet && 0 != strError.length()) {
								blRes = true;
							}
							Intent intent = new Intent(MessageUti.SYSTEM_SET_DEVICE_RESTORE);
							intent.putExtra("Result", blRes);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}
}
