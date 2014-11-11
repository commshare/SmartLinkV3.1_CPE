package com.alcatel.smartlinkv3.business;

import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.business.update.DeviceUpgradeStateInfo;
import com.alcatel.smartlinkv3.business.update.HttpUpdate;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class UpdateManager extends BaseManager {

	private DeviceNewVersionInfo m_newFirmwareVersionInfo=null;
	private DeviceUpgradeStateInfo m_upgradeFirmwareStateInfo=null;
	private GetDeviceNewVersionTask m_taskGetDeviceNewVersion;
	private Timer m_getDeviceNewVersionTimer = new Timer();
	public UpdateManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		m_newFirmwareVersionInfo = new DeviceNewVersionInfo();
		m_upgradeFirmwareStateInfo = new DeviceUpgradeStateInfo();
		m_context.registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION));
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				startGetDeviceNewVersionTask();
			}
		}
		
		if (intent.getAction().equalsIgnoreCase(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (!bCPEWifiConnected) {
				clearData();
				stopRollTimer();
			}
		}
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_newFirmwareVersionInfo.clear();
		m_upgradeFirmwareStateInfo.clear();
	}

	@Override
	protected void stopRollTimer() {
		// TODO Auto-generated method stub
		stopGetDeviceNewVersionTask();
	}

	public DeviceNewVersionInfo getCheckNewVersionInfo(){
		return m_newFirmwareVersionInfo;
	}

	private void startGetDeviceNewVersionTask() {
		if(m_taskGetDeviceNewVersion == null) {
			m_taskGetDeviceNewVersion = new GetDeviceNewVersionTask();
			m_getDeviceNewVersionTimer.scheduleAtFixedRate(m_taskGetDeviceNewVersion, 0, 5 * 1000);
		}
	}
	
	private void stopGetDeviceNewVersionTask() {
		if(m_taskGetDeviceNewVersion != null) {
			m_taskGetDeviceNewVersion.cancel();
			m_taskGetDeviceNewVersion = null;
		}
	}
	
	public class GetDeviceNewVersionTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getDeviceNewVersion(null);
		}
		
	}
	//get firmware new version
	public void getDeviceNewVersion(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("Update", "GetDeviceNewVersion")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpUpdate.getDeviceNewVersionRequest("9.1", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRes = response.getResultCode();
							String strError=response.getErrorCode();
							if(BaseResponse.RESPONSE_OK == nRes &&
									strError.length() == 0){
								m_newFirmwareVersionInfo = response.getModelResult();
								if(EnumDeviceCheckingStatus.
										antiBuild(EnumDeviceCheckingStatus.DEVICE_CHECKING) 
										!= m_newFirmwareVersionInfo.getState()){
									stopGetDeviceNewVersionTask();
								}
							}else {
								stopGetDeviceNewVersionTask();
							}

							Intent intent = new Intent(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION);
							intent.putExtra(MessageUti.RESPONSE_RESULT, nRes);
							intent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strError);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}

	//start update firmware
	public void startUpdate(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("Update", "SetDeviceStartUpdate")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpUpdate.setDeviceStartUpdateRequest("9.2", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							boolean blStartRes = false;
							int nRes = response.getResultCode();
							String strError=response.getErrorCode();
							if(BaseResponse.RESPONSE_OK == nRes &&
									strError.length() == 0){
								blStartRes = true;
							}

							Intent intent= new Intent(
									MessageUti.UPDATE_SET_DEVICE_START_UPDATE);
							intent.putExtra("Result", blStartRes);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}

	//stop update firmware
	public void stopUpdate(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("Update", "SetDeviceUpdateStop")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpUpdate.setDeviceUpdateStopRequest("9.4", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							boolean blStopRes = false;
							int nRes = response.getResultCode();
							String strError=response.getErrorCode();
							if(BaseResponse.RESPONSE_OK == nRes &&
									strError.length() == 0){
								blStopRes = true;
							}

							Intent intent= new Intent(
									MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE);
							intent.putExtra("Result", blStopRes);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}

	//get update state
	public void getUpgradeState(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("Update", "GetDeviceUpgradeState")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpUpdate.getDeviceUpgradeStatusRequest("9.3", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRes = response.getResultCode();
							String strError=response.getErrorCode();
							if(BaseResponse.RESPONSE_OK == nRes &&
									strError.length() == 0){
								m_upgradeFirmwareStateInfo = response.getModelResult();
							}

							Intent intent= new Intent(
									MessageUti.UPDATE_GET_DEVICE_UPGRADE_STATE);
							intent.putExtra("Result", nRes);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}

	public void setCheckNewFirmwareVersion(DataValue data){
//		if (!FeatureVersionManager.getInstance().
//				isSupportApi("Update", "SetCheckNewVersion")) {
//			return;
//		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			HttpRequestManager.GetInstance().sendPostRequest(
					new HttpUpdate.SetCheckNewVersionRequest("9.5", 
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRet = response.getResultCode();
							String strError = response.getErrorCode();
							Intent intent = new Intent(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION);
							intent.putExtra(MessageUti.RESPONSE_RESULT, nRet);
							intent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strError);
							m_context.sendBroadcast(intent);
						}
					}));
		}
	}
}
