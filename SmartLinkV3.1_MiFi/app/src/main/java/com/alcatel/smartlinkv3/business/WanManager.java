package com.alcatel.smartlinkv3.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.SimManager.GetSimStatusTask;
import com.alcatel.smartlinkv3.business.StatisticsManager.GetUsageSettingsTask;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.ConnectionSettingsModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.sim.HttpGetSimStatus;
import com.alcatel.smartlinkv3.business.sim.SIMStatusResult;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageHistory;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageSettings;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.business.wan.ConnectStatusResult;
import com.alcatel.smartlinkv3.business.wan.ConnectionSettingsResult;
import com.alcatel.smartlinkv3.business.wan.HttpConnectOperation;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class WanManager extends BaseManager {
	private ConnectStatusModel m_connectStatus = new ConnectStatusModel();
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
		// TODO Auto-generated method stub
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
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
	
	public ConnectStatusModel getConnectStatus() {
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
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.GetConnectionState("3.1", new IHttpFinishListener() {           
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {               	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		ConnectStatusResult connectStatusResult = response.getModelResult();
                    		m_connectStatus.setValue(connectStatusResult);
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    Intent megIntent= new Intent(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        			m_context.sendBroadcast(megIntent);
                }
            }));
        } 
	}
	
	//Connect  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void connect(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Connection", "Connect") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.Connect("3.2", new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.WAN_CONNECT_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//DisConnect  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void disconnect(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("Connection", "DisConnect") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.DisConnect("3.3", new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.WAN_DISCONNECT_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
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
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.GetConnectionSettings("3.4", new IHttpFinishListener() {           
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
                    
                    Intent megIntent= new Intent(MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        			m_context.sendBroadcast(megIntent);
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
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.SetConnectionSettings("3.5",nConnectionSettings, new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    }  
}
