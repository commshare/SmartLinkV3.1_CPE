package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.power.BatteryInfo;
import com.alcatel.smartlinkv3.business.power.HttpPower;
import com.alcatel.smartlinkv3.business.power.PowerSavingModeInfo;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

public class PowerManager extends BaseManager {

	private PowerSavingModeInfo m_powerSavingMode=null;
	private BatteryInfo m_battery=null;
	private getBatteryStateTask m_getBatteryStateTask=null;
	private Timer m_getBatteryRollTimer = new Timer();
	
	public PowerManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		m_powerSavingMode = new PowerSavingModeInfo();
		m_battery = new BatteryInfo();
	}

	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance()
					.getCPEWifiConnected();
			if (bCPEWifiConnected == true) {
				startGetBatteryStatusTask();
				getPowerSavingModeInfo(null);
			} else {
				clearData();
				stopBatteryStatusTask();
			}
		}
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub

		m_powerSavingMode.clear();
		m_battery.clear();
	}

	@Override
	protected void stopRollTimer() {
		// TODO Auto-generated method stub

	}
	
	public BatteryInfo getBatteryInfo(){
		return m_battery;
	}
	
	public PowerSavingModeInfo getPowerSavingModeInfo(){
		return m_powerSavingMode;
	}

	private void startGetBatteryStatusTask() {
		if(m_getBatteryStateTask == null) {
			m_getBatteryStateTask = new getBatteryStateTask();
			m_getBatteryRollTimer.scheduleAtFixedRate(m_getBatteryStateTask, 0, 10 * 1000);
		}
	}
	
	private void stopBatteryStatusTask() {
		if(m_getBatteryStateTask != null) {
			m_getBatteryStateTask.cancel();
			m_getBatteryStateTask = null;
		}
	}
	class getBatteryStateTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getBatteryState(null);
		}
		
	}
	//get battery state
	public void getBatteryState(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("PowerManagement", "GetBatteryState")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpPower.getBatteryStateRequest(
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRes = response.getResultCode();
							String strErr = response.getErrorMessage();
							if (BaseResponse.RESPONSE_OK == nRes &&
									0 == strErr.length()) {
								m_battery = response.getModelResult();
							}

		        			sendBroadcast(response, MessageUti.POWER_GET_BATTERY_STATE);
						}
					}));
		}
	}

	//get power saving mode
	public void getPowerSavingModeInfo(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("PowerManagement", "GetPowerSavingMode")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpPower.getPowerSavingModeRequest(
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
							// TODO Auto-generated method stub
							int nRes = response.getResultCode();
							String strErr = response.getErrorMessage();
							if (BaseResponse.RESPONSE_OK == nRes &&
									0 == strErr.length()) {
								m_powerSavingMode = response.getModelResult();
							}else {
								getPowerSavingModeInfo(null);
							}

		        			sendBroadcast(response, MessageUti.POWER_GET_POWER_SAVING_MODE);
						}
					}));
		}
	}

	//set power saving mode
	public void setPowerSavingModeInfo(DataValue data){
		if (!FeatureVersionManager.getInstance().
				isSupportApi("PowerManagement", "SetPowerSavingMode")) {
			return;
		}

		boolean blWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
		if (blWifiConnected) {
			PowerSavingModeInfo info = (PowerSavingModeInfo)data.getParamByKey("PowerSavingMode");
			LegacyHttpClient.getInstance().sendPostRequest(
					new HttpPower.setPowerSavingModeRequest(info,
							new IHttpFinishListener() {

						@Override
						public void onHttpRequestFinish(BaseResponse response) {
		        			sendBroadcast(response, MessageUti.POWER_SET_POWER_SAVING_MODE);
						}
					}));
		}
	}
}
