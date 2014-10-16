package com.alcatel.smartlinkv3.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.SimManager.GetSimStatusTask;
import com.alcatel.smartlinkv3.business.model.ConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageHistoryItemModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.sim.HttpAutoEnterPinState;
import com.alcatel.smartlinkv3.business.sim.HttpGetSimStatus;
import com.alcatel.smartlinkv3.business.sim.SIMStatusResult;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageHistory;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageSettings;
import com.alcatel.smartlinkv3.business.statistics.UsageHistoryResult;
import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.business.wan.ConnectStatusResult;
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

	private Timer m_getConnectStatusRollTimer = new Timer();
	private GetConnectStatusTask m_getConnetStatusTask = null;
	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_connectStatus.clear();
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
				}else{
					stopRollTimer();
					m_connectStatus.clear();
				}
			}
    	}
	}  
	
	public WanManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    }
	
	public ConnectStatusModel getConnectStatus() {
		return m_connectStatus;
	}
	
	//GetConnectionState ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetConnectStatusTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("WanConnection", "GetConnectionState") != true)
			return;
		if(m_getConnetStatusTask == null) {
			m_getConnetStatusTask = new GetConnectStatusTask();
			m_getConnectStatusRollTimer.scheduleAtFixedRate(m_getConnetStatusTask, 0, 5 * 1000);
		}
	}
	
	class GetConnectStatusTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.GetConnectionState("8.1", new IHttpFinishListener() {           
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
		if(FeatureVersionManager.getInstance().isSupportApi("WanConnection", "Connect") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.Connect("8.2", new IHttpFinishListener() {           
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
		if(FeatureVersionManager.getInstance().isSupportApi("WanConnection", "DisConnect") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpConnectOperation.DisConnect("8.3", new IHttpFinishListener() {           
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
}
