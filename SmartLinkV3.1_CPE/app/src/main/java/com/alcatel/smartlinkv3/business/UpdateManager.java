package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.business.update.DeviceUpgradeStateInfo;
import com.alcatel.smartlinkv3.business.update.HttpUpdate;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceUpgradeStatus;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateManager extends BaseManager {

	private DeviceNewVersionInfo m_newFirmwareVersionInfo=null;
	private DeviceUpgradeStateInfo m_upgradeFirmwareStateInfo=null;
	private GetDeviceNewVersionTask m_taskGetDeviceNewVersion;
	private Timer m_getDeviceNewVersionTimer = new Timer();
	private Timer m_getDeviceUpgradeStatus = new Timer();
	private GetDeviceUpgradeStatusTask m_taskGetDeviceUpgradeStatus;
	public UpdateManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		m_newFirmwareVersionInfo = new DeviceNewVersionInfo();
		m_upgradeFirmwareStateInfo = new DeviceUpgradeStateInfo();
		m_context.registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION));
		m_context.registerReceiver(m_msgReceiver, 
				new IntentFilter(MessageUti.UPDATE_SET_DEVICE_START_UPDATE));
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION)) {
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			if (response != null && response.isOk()) {
				startGetDeviceNewVersionTask();
			}
		}
		
		if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_START_UPDATE)) {
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			if (response != null && response.isOk()) {
				startGetDeviceUpgradeStatusTask();
			}
		}
		
		
		if (intent.getAction().equalsIgnoreCase(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (!bCPEWifiConnected) {
				clearData();
				stopRollTimer();
			}else {
				setCheckNewFirmwareVersion(null);
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
		stopGetDeviceUpgradeStatusTask();
	}

	public DeviceNewVersionInfo getCheckNewVersionInfo(){
		return m_newFirmwareVersionInfo;
	}

	public DeviceUpgradeStateInfo getUpgradeStatusInfo(){
		return m_upgradeFirmwareStateInfo;
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
	
	private void startGetDeviceUpgradeStatusTask() {
		if(m_taskGetDeviceUpgradeStatus == null) {
			m_taskGetDeviceUpgradeStatus = new GetDeviceUpgradeStatusTask();
			m_getDeviceUpgradeStatus.scheduleAtFixedRate(m_taskGetDeviceUpgradeStatus, 0, 10 * 1000);
		}
	}
	
	private void stopGetDeviceUpgradeStatusTask() {
		if(m_taskGetDeviceUpgradeStatus != null) {
			m_taskGetDeviceUpgradeStatus.cancel();
			m_taskGetDeviceUpgradeStatus = null;
		}
	}
	
	public class GetDeviceUpgradeStatusTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getUpgradeState(null);
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
					new HttpUpdate.getDeviceNewVersionRequest(new IHttpFinishListener() {

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

							sendBroadcast(response, MessageUti.UPDATE_GET_DEVICE_NEW_VERSION);
						}
					}));
		}
	}

	//start update firmware
	public void startFOTAUpdate(DataValue data)
	{
		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected)
		{
			HttpRequestManager.GetInstance().sendPostRequest(new HttpUpdate.setDeviceStartUpdateRequest(new IHttpFinishListener()
			{
				@Override
				public void onHttpRequestFinish(BaseResponse response)
				{
					sendBroadcast(response, MessageUti.UPDATE_SET_DEVICE_START_FOTA_UPDATE);
				}
				}));
			}
		}
	
	public void startUpdate(DataValue data)
	{
		if(FeatureVersionManager.getInstance().isSupportApi("Update", "SetFOTAStartDownload"))
		{
			boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if (blWifiConnected) {
				HttpRequestManager.GetInstance().sendPostRequest(
						new HttpUpdate.setFOTAStartDownload(new IHttpFinishListener() {

							@Override
							public void onHttpRequestFinish(BaseResponse response) {
								sendBroadcast(response, MessageUti.UPDATE_SET_DEVICE_START_UPDATE);
							}
						}));
			}
		}
		else if(BusinessManager.getInstance().getSystemInfoModel().getSwVersion().equalsIgnoreCase("Y858_FQ_01.16_02") || BusinessManager.getInstance().getSystemInfoModel().getSwVersion().equalsIgnoreCase("Y858_FL_01.16_02")){
			boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if (blWifiConnected) {
				HttpRequestManager.GetInstance().sendPostRequest(
						new HttpUpdate.setFOTAStartDownload(new IHttpFinishListener() {

							@Override
							public void onHttpRequestFinish(BaseResponse response) {
								sendBroadcast(response, MessageUti.UPDATE_SET_DEVICE_START_UPDATE);
							}
						}));
			}
		}
		else if (FeatureVersionManager.getInstance().isSupportApi("Update", "SetDeviceStartUpdate")) {
			boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if (blWifiConnected) {
				HttpRequestManager.GetInstance().sendPostRequest(
						new HttpUpdate.setDeviceStartUpdateRequest(new IHttpFinishListener() {

							@Override
							public void onHttpRequestFinish(BaseResponse response) {
								sendBroadcast(response, MessageUti.UPDATE_SET_DEVICE_START_UPDATE);
							}
						}));
			}
		}
		else
			return;
		
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
					new HttpUpdate.setDeviceUpdateStopRequest(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							sendBroadcast(response, MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE);
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
					new HttpUpdate.getDeviceUpgradeStatusRequest(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRes = response.getResultCode();
							String strError=response.getErrorCode();
							if(BaseResponse.RESPONSE_OK == nRes &&
									strError.length() == 0){
								m_upgradeFirmwareStateInfo = response.getModelResult();
							}
							
							if (EnumDeviceUpgradeStatus.DEVICE_UPGRADE_UPDATING != 
									EnumDeviceUpgradeStatus.
									build(m_upgradeFirmwareStateInfo.getStatus())) {
								stopGetDeviceUpgradeStatusTask();
							}

							sendBroadcast(response, MessageUti.UPDATE_GET_DEVICE_UPGRADE_STATE);
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
					new HttpUpdate.SetCheckNewVersionRequest(new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							sendBroadcast(response, MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION);
						}
					}));
		}
	}
}
