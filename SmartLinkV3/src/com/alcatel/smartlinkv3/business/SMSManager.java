package com.alcatel.smartlinkv3.business;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.SmsMessageModel;
import com.alcatel.smartlinkv3.business.sms.HttpGetSmsList;
import com.alcatel.smartlinkv3.business.sms.HttpSmsOperation;
import com.alcatel.smartlinkv3.business.sms.SendSmsResult;
import com.alcatel.smartlinkv3.business.sms.SendStatusResult;
import com.alcatel.smartlinkv3.business.sms.SmsListResult;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.SMSStoreIn;
import com.alcatel.smartlinkv3.common.ENUM.SMSTag;
import com.alcatel.smartlinkv3.common.ENUM.SendStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class SMSManager extends BaseManager {
	private ArrayList<SmsMessageModel> m_messageList = new ArrayList<SmsMessageModel>();
	private Timer m_getSmsRollTimer = new Timer();
	GetSMSListTask m_getSmsListTask = null;
	
	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_messageList.clear();
	} 
	
	@Override
	protected void stopRollTimer() {
		/*m_getSmsRollTimer.cancel();
		m_getSmsRollTimer.purge();
		m_getSmsRollTimer = new Timer();*/
		stopGetSmsListTask();
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
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
					//startGetSmsListTask();
				}else{
					stopRollTimer();
					m_messageList.clear();
				}
			}
    	}
	}  
	
	public SMSManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    }
	
	public ArrayList<SmsMessageModel> getSMSList() {
		return m_messageList;
	}	
	
	public int GetUnreadSmsNumber(){
		int nUnreadCount = 0;
		if(null != m_messageList){
			for(int nIndex = 0; nIndex < m_messageList.size(); nIndex++)
			{
				SmsMessageModel sms = m_messageList.get(nIndex);
				if(sms.m_nTag == SMSTag.NotRead) {			
	    			nUnreadCount += 1;
	    		}
			}		
		}
		
    	return nUnreadCount;	    	
	} 
	
	//GetSmsList ////////////////////////////////////////////////////////////////////////////////////////// 
	public void startGetSmsListTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSmsList") != true)
			return;
		
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
		if(simStatus.m_SIMState != ENUM.SIMState.Accessable) 
			return;
		
		if(m_getSmsListTask == null) {
			m_getSmsListTask = new GetSMSListTask();
			m_getSmsRollTimer.scheduleAtFixedRate(m_getSmsListTask, 0, 120 * 1000);
		}
	}
	
	public void stopGetSmsListTask() {
		if(m_getSmsListTask != null) {
			m_getSmsListTask.cancel();
			m_getSmsListTask = null;
		}
	}
	
	public void refreshSmsListAtOnce(){
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
		if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
			GetSMSListTask getNetworkInfoTask = new GetSMSListTask();
			m_getSmsRollTimer.schedule(getNetworkInfoTask, 0);
		}
	}
    
	class GetSMSListTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpGetSmsList.GetSmsList("6.1",0,5, new IHttpFinishListener() {           
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {               	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		SmsListResult smsListResult = response.getModelResult();
                    		m_messageList.clear();
                    		for(int i = 0;i < smsListResult.SmsList.size();i++) {
                    			SmsMessageModel sms = new SmsMessageModel();
                    			sms.setValue(smsListResult.SmsList.get(i));
                    			if(sms.m_nTag == SMSTag.Report) 
                    				sms.m_strContent = m_context.getResources().getString(R.string.sms_report_message);
                    			m_messageList.add(sms);
                    		}
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }
                    
                    Intent megIntent= new Intent(MessageUti.SMS_GET_SMS_LIST_ROLL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        			m_context.sendBroadcast(megIntent);
                }
            }));
        } 
	}
	
	//DeleteSms ////////////////////////////////////////////////////////////////////////////////////////// 
	public void deleteSms(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "DeleteSms") != true)
			return;
		
		int nId = (Integer) data.getParamByKey("id");
		SMSStoreIn storeIn = (SMSStoreIn) data.getParamByKey("store_in");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSmsOperation.DeleteSms("6.2",nId,SMSStoreIn.antiBuild(storeIn), new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.SMS_DELETE_SMS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//SendSms ////////////////////////////////////////////////////////////////////////////////////////// 
	public void sendSms(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "SendSms") != true)
			return;
		
		String strContent = (String) data.getParamByKey("content");
		String strPhoneNumber = (String) data.getParamByKey("phone_number");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSmsOperation.SendSms("6.3",strContent,strPhoneNumber, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	int nSendId = 0;
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		SendSmsResult sendSmsResult = response.getModelResult();
                		nSendId = sendSmsResult.SmsSendId;
                		
                		//get send status
                		DataValue dataValue = new DataValue();
						dataValue.addParam("sms_send_id", nSendId);
						getSmsSendResult(dataValue);
                	}else{
                		
                	}
                }else{
                	//Log
                }
 
                Intent megIntent= new Intent(MessageUti.SMS_SEND_SMS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(Const.SMS_SNED_ID, nSendId);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//GetSmsSendResult ////////////////////////////////////////////////////////////////////////////////////////// 
	public void getSmsSendResult(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSendSmsResult") != true)
			return;
		
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
		if(simStatus.m_SIMState != ENUM.SIMState.Accessable)
			return;
		
		final int nSmsSendId = (Integer) data.getParamByKey("sms_send_id");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSmsOperation.GetSmsSendResult("6.5",nSmsSendId, new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	int nSendStatus = 0;
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		SendStatusResult sendStatusResult = response.getModelResult();
                		nSendStatus = sendStatusResult.SendStatus;
                		SendStatus sendStatus = SendStatus.build(nSendStatus);
                		if(sendStatus == SendStatus.None) {
                			nSendStatus = 1;
                			sendStatus = SendStatus.build(nSendStatus);
                		}
                		if(sendStatus != SendStatus.Fail && sendStatus != SendStatus.Success && 
                				sendStatus != SendStatus.Fail_Memory_Full) {
                			new Handler().postDelayed(new Runnable() {
        						
        						public void run() {
        							// TODO Auto-generated method stub
        							DataValue dataValue = new DataValue();
        							dataValue.addParam("sms_send_id", nSmsSendId);
        							getSmsSendResult(dataValue);
        						}
        					}, 1000);
                		}
                	}else{
                		
                	}
                }else{
                	//Log
                }
 
                Intent megIntent= new Intent(MessageUti.SMS_GET_SEND_STATUS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(Const.SMS_SNED_STATUS, nSendStatus);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//ModifySMSReadStatus ////////////////////////////////////////////////////////////////////////////////////////// 
	public void modifySMSReadStatus(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "ModifySmsReadStatus") != true)
			return;
		
		int nId = (Integer) data.getParamByKey("id");
		boolean bStatus = (Boolean) data.getParamByKey("status");//Message read status. 0 - Read   1 - Unread 
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSmsOperation.ModifySMSReadStatus("6.4",nId,bStatus == true?0:1, new IHttpFinishListener() {           
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
 
                Intent megIntent= new Intent(MessageUti.SMS_MODIFY_SMS_READ_STATUS_REQUSET);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    }
}
