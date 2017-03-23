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
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

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
		if(FeatureVersionManager.getInstance().isSupportApi("Connection", "GetConnectionState") != true)
			return;
		if(m_getConnetStatusTask == null) {
			m_getConnetStatusTask = new GetConnectStatusTask();
			m_getConnectStatusRollTimer.scheduleAtFixedRate(m_getConnetStatusTask, 0, 5 * 1000);
		}
	}
	
	class GetConnectStatusTask extends TimerTask{ 
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.GetConnectionState(new IHttpFinishListener() {
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {               	
                	String errorCode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	errorCode = response.getErrorCode();
                    	if(errorCode.length() == 0) {
                    		ConnectStatusResult connectStatusResult = response.getModelResult();
                    		m_connectStatus.setValue(connectStatusResult);
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    sendBroadcast(response, MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET);
                }
            }));
        } 
	}
	
	//Connect  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void connect(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Connection", "Connect") != true)
			return;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.Connect(new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
         
                	}else{
                		
                	}
                }else{
                	//Log
                }

    			sendBroadcast(response, MessageUti.WAN_CONNECT_REQUSET);
            }
        }));
    } 
	
	//DisConnect  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void disconnect(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Connection", "DisConnect") != true)
			return;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.DisConnect(new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
         
                	}else{
                		
                	}
                }else{
                	//Log
                }
 
                sendBroadcast(response, MessageUti.WAN_DISCONNECT_REQUSET);
            }
        }));
    } 
	
	
	//GetUsageSetting ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetConnectSettingsTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("Connection", "GetConnectionSettings") != true)
			return;
		
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
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.GetConnectionSettings(new IHttpFinishListener() {
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {   
                	boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        			if(bCPEWifiConnected == false) {
        				return;
        			}
	            	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		ConnectionSettingsResult connectionSettingResult = response.getModelResult();
                    		ConnectionSettingsModel pre = new ConnectionSettingsModel();
                    		pre.clone(m_connectionSettings);
                    		m_connectionSettings.setValue(connectionSettingResult);
                    		
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    sendBroadcast(response, MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET);
                    }
            }));
        } 
	}
	
	
	//setTimeLimitFlag  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void setRoamingConnectFlag(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Connection", "SetConnectionSettings") != true)
			return;
		
		final ENUM.OVER_ROAMING_STATE nRoamingConnectFlag = (ENUM.OVER_ROAMING_STATE) data.getParamByKey("roaming_connect_flag");
		//final ENUM.OVER_ROAMING_STATE nPreRoamingConnectFlag = m_connectionSettings.HRoamingConnect;
		final ConnectionSettingsResult nConnectionSettings = new ConnectionSettingsResult();
		nConnectionSettings.clone(m_connectionSettings);
		nConnectionSettings.RoamingConnect = ENUM.OVER_ROAMING_STATE.antiBuild(nRoamingConnectFlag);
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpConnectOperation.SetConnectionSettings(nConnectionSettings, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		m_connectionSettings.setValue(nConnectionSettings);
                	}else{

                	}
                }else{

                }
 
                sendBroadcast(response, MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET);
            }
        }));
    }  
}
