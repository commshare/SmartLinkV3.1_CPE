package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.alcatel.smartlinkv3.business.model.ConnectionSettingsModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.WanConnectStatusModel;
import com.alcatel.smartlinkv3.business.wan.ConnectStatusResult;
import com.alcatel.smartlinkv3.business.wan.ConnectionSettingsResult;
import com.alcatel.smartlinkv3.business.wan.HttpConnectOperation;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;

import java.util.Timer;
import java.util.TimerTask;

public class WanManager extends BaseManager {
	private WanConnectStatusModel m_connectStatus = new WanConnectStatusModel();
	private ConnectionSettingsModel m_connectionSettings = new ConnectionSettingsModel();
	private Timer m_rollTimer = new Timer();

	private Timer m_getConnectStatusRollTimer = new Timer();
	private GetConnectStatusTask m_getConnetStatusTask = null;
	
	private GetConnectionSettingsTask m_getConnectionSettingsTask = null;
	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_connectStatus.clear();
		m_connectionSettings.clear();
	} 
	@Override
	protected void stopRollTimer() {
		/*m_getConnectStatusRollTimer.cancel();
		m_getConnectStatusRollTimer.purge();
		m_getConnectStatusRollTimer = new Timer();*/
		if(m_getConnetStatusTask != null) {
			m_getConnetStatusTask.cancel();
			m_getConnetStatusTask = null;
		}
		
		if(m_getConnectionSettingsTask != null) {
			m_getConnectionSettingsTask.cancel();
			m_getConnectionSettingsTask = null;
		}
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
			if (response != null && response.isOk()) {
				SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					startGetConnectStatusTask();
					startGetConnectSettingsTask();
				}else{
					stopRollTimer();
					m_connectStatus.clear();
					m_connectionSettings.clear();
				}
			}
    	}
	}  
	
	public WanManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET));
    }
	
	public WanConnectStatusModel getConnectStatus() {
		return m_connectStatus;
	}
	
	public ConnectionSettingsModel getConnectSettings() {
		return m_connectionSettings;
	}
	
	//GetConnectionState ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetConnectStatusTask() {
		if(m_getConnetStatusTask == null) {
			m_getConnetStatusTask = new GetConnectStatusTask();
			m_getConnectStatusRollTimer.scheduleAtFixedRate(m_getConnetStatusTask, 0, 5 * 1000);
		}
	}
	
	class GetConnectStatusTask extends TimerTask{ 
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.GetConnectionState(response -> {
                    	if(response.isOk()) {
                    		ConnectStatusResult connectStatusResult = response.getModelResult();
                    		m_connectStatus.setValue(connectStatusResult);
                    	}
//                    sendBroadcast(response, MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET);
            }));
        } 
	}
	
	//Connect  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void connect() {
		LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.Connect(response -> {
//    			sendBroadcast(response, MessageUti.WAN_CONNECT_REQUSET);
        }));
    } 
	
	//DisConnect  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void disconnect(DataValue data) {
		LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.DisConnect(response -> {
//                sendBroadcast(response, MessageUti.WAN_DISCONNECT_REQUSET);

        }));
    } 
	
	
	//GetUsageSetting ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetConnectSettingsTask() {
		
		//GetUsageSettingsTask getUsageSettingsTask = new GetUsageSettingsTask();
		//m_rollTimer.scheduleAtFixedRate(getUsageSettingsTask, 0, 10 * 1000);
		if(m_getConnectionSettingsTask == null) {
			m_getConnectionSettingsTask = new GetConnectionSettingsTask();
			m_rollTimer.scheduleAtFixedRate(m_getConnectionSettingsTask, 0, 10 * 1000);
		}
	}
		
	class GetConnectionSettingsTask extends TimerTask{ 
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.GetConnectionSettings(
        			response -> {
                	boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        			if(!bCPEWifiConnected) {
        				return;
        			}
                    	if(response.isOk()) {
                    		ConnectionSettingsResult connectionSettingResult = response.getModelResult();
                    		ConnectionSettingsModel pre = new ConnectionSettingsModel();
                    		pre.clone(m_connectionSettings);
                    		m_connectionSettings.setValue(connectionSettingResult);
                    	}
//                    	sendBroadcast(response, MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET);
            }));
        } 
	}
	
	
	//setTimeLimitFlag  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setRoamingConnectFlag(DataValue data) {
		final ENUM.OVER_ROAMING_STATE nRoamingConnectFlag = (ENUM.OVER_ROAMING_STATE) data.getParamByKey("roaming_connect_flag");
		//final ENUM.OVER_ROAMING_STATE nPreRoamingConnectFlag = m_connectionSettings.HRoamingConnect;
		final ConnectionSettingsResult nConnectionSettings = new ConnectionSettingsResult();
		nConnectionSettings.clone(m_connectionSettings);
		nConnectionSettings.RoamingConnect = ENUM.OVER_ROAMING_STATE.antiBuild(nRoamingConnectFlag);
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.SetConnectionSettings(nConnectionSettings,
				 response -> {
				if(response.isOk()) {
					m_connectionSettings.setValue(nConnectionSettings);
				}
 
//                sendBroadcast(response, MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET);
        }));
    }  
}
