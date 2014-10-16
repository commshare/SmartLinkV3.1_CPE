package com.alcatel.smartlinkv3.business;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import com.alcatel.smartlinkv3.business.calllog.CallLogListResult;
import com.alcatel.smartlinkv3.business.calllog.HttpCallLogOperation;
import com.alcatel.smartlinkv3.business.calllog.HttpGetCallLogList.GetCallLogList;
import com.alcatel.smartlinkv3.business.model.CallLogModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class CallLogManager extends BaseManager {
	private ArrayList<CallLogModel> m_callLogList = new ArrayList<CallLogModel>();
	private Timer m_getCallLogRollTimer = new Timer();
	GetCallLogListTask m_getCallLogListTask = null;
	
	@Override
	protected void clearData() {	
		m_callLogList.clear();
	} 
	
	@Override
	protected void stopRollTimer() {
		/*m_getCallLogRollTimer.cancel();
		m_getCallLogRollTimer.purge();
		m_getCallLogRollTimer = new Timer();*/
		stopGetCallLogTask();
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {
				
			}
    	}
		
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					//startGetCallLogTask();
				}else{
					stopRollTimer();
					m_callLogList.clear();
				}
			}
    	}
	}  
	
	public CallLogManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    }
	
	public ArrayList<CallLogModel> getCallLogList() {
		return m_callLogList;
	}
	
	public CallLogModel getCallLogById(int nId) {
		ArrayList<CallLogModel> lst = (ArrayList<CallLogModel>) m_callLogList.clone();
		for(int i = 0;i < lst.size();i++) {
			if(lst.get(i).m_Id == nId)
				return lst.get(i);
		}
		
		return null;
	}
	
	//Get Call Log List ////////////////////////////////////////////////////////////////////////////////////////// 
	public void startGetCallLogTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("CallLog", "GetCallLogList") != true)
			return;
		
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
		if(simStatus.m_SIMState != ENUM.SIMState.Accessable) 
			return;
		
		if(m_getCallLogListTask == null) {
			m_getCallLogListTask = new GetCallLogListTask();
			m_getCallLogRollTimer.scheduleAtFixedRate(m_getCallLogListTask, 0, 120 * 1000);
		}
	}
	
	public void stopGetCallLogTask() {
		if(m_getCallLogListTask != null) {
			m_getCallLogListTask.cancel();
			m_getCallLogListTask = null;
		}
	}
	
	public void refreshCallLogListAtOnce(){
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
		if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
			GetCallLogListTask getCallLogListTask = new GetCallLogListTask();
			m_getCallLogRollTimer.schedule(getCallLogListTask, 0);
		}
	}
    
	class GetCallLogListTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new GetCallLogList("11.1", 0, new IHttpFinishListener() {           
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {               	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		CallLogListResult callLogResult = response.getModelResult();
                    		m_callLogList.clear();
                    		for(int i = 0;i < callLogResult.CallLogList.size();i++) {
                    			CallLogModel callLog = new CallLogModel();
                    			callLog.setValue(callLogResult.CallLogList.get(i));
                    			m_callLogList.add(callLog);
                    		}
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    Intent megIntent= new Intent(MessageUti.CALLLOG_GET_CALLLOG_LIST_ROLL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        			m_context.sendBroadcast(megIntent);
                }
            }));
        } 
	}
	

	//Delete call Log ////////////////////////////////////////////////////////////////////////////////////////// 
	public void deleteCallLog(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("CallLog", "DeleteCallLog") != true)
			return;
		
		int nId = (Integer) data.getParamByKey("id");	
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpCallLogOperation.DeleteCallLog("11.2",nId,  new IHttpFinishListener() {           
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
                	
                }
 
                Intent megIntent= new Intent(MessageUti.CALLLOG_DELETE_CALLLOG_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//Clear call Log ////////////////////////////////////////////////////////////////////////////////////////// 
	public void clearCallLog(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("CallLog", "ClearCallLog") != true)
			return;
		
		int nType = (Integer) data.getParamByKey("type");	
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpCallLogOperation.DeleteCallLog("11.3", nType,  new IHttpFinishListener() {           
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
                	
                }
 
                Intent megIntent= new Intent(MessageUti.CALLLOG_CLEAR_CALLLOG_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 

}
