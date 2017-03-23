package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.alcatel.smartlinkv3.business.model.SMSContactItemModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.SmsContactMessagesModel;
import com.alcatel.smartlinkv3.business.model.SmsContentMessagesModel;
import com.alcatel.smartlinkv3.business.sms.HttpSms;
import com.alcatel.smartlinkv3.business.sms.SendStatusResult;
import com.alcatel.smartlinkv3.business.sms.SmsContactListResult;
import com.alcatel.smartlinkv3.business.sms.SmsContentListResult;
import com.alcatel.smartlinkv3.business.sms.SmsInitResult;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.EnumSMSDelFlag;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
import com.alcatel.smartlinkv3.common.ENUM.SendStatus;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SMSManager extends BaseManager {
	private SMSInit m_smsInit = SMSInit.Initing;
	private SmsContactMessagesModel m_contactMessages = new SmsContactMessagesModel();
	
	private Timer m_getSmsRollTimer = new Timer();
	GetSMSInitTask m_getSmsInitTask = null;
	GetContactMessagesTask m_getContactMessagesTask = null;
	
	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		m_smsInit = SMSInit.Initing;
		m_contactMessages.clear();
	} 
	
	@Override
	protected void stopRollTimer() {
		stopGetSmsInitTask();
		stopGetContactMessagesTask();
	}
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		String action = intent.getAction();
		BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
		Boolean ok = response != null && response.isOk();

		if(MessageUti.CPE_WIFI_CONNECT_CHANGE.equals(action)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {
				
			}
    	}
		
		if(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET.equals(action)) {
			if(ok) {
				if(m_smsInit == SMSInit.Complete) {
					stopGetSmsInitTask();
					startGetContactMessagesTask();
				}
			}
    	}
		
		if(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET.equals(action)) {
			if(ok) {
				SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					startGetSmsInitTask();
				}else{
					sentSMSInitChangedMessage(SMSInit.Initing);
					stopRollTimer();
					m_contactMessages.clear();
				}
			}
    	}
	}  
	
	public SMSManager(Context context) {
		super(context);
		//CPE_BUSINESS_STATUS_CHANGE and CPE_WIFI_CONNECT_CHANGE already register in basemanager
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET));
		
		m_context.registerReceiver(m_msgReceiver, new  IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
    }
	
	
	public SMSInit getSMSInit() {
		return m_smsInit;
	}
	
	public SmsContactMessagesModel getContactMessages() {
		return m_contactMessages;
	}	
	
	public int GetUnreadSmsNumber(){
		int nUnreadCount = 0;
		ArrayList<SMSContactItemModel> lst = m_contactMessages.SMSContactList;
		if(null != lst){
			for(int nIndex = 0; nIndex < lst.size(); nIndex++)
			{
				SMSContactItemModel sms = lst.get(nIndex);
				nUnreadCount += sms.UnreadCount; 
			}		
		}
		
    	return nUnreadCount;	    	
	} 
	//GetSMSInitStatus ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetSmsInitTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSMSInitStatus") != true)
			return;
		
		SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
		if(simStatus.m_SIMState != ENUM.SIMState.Accessable) 
			return;
		
		if(m_getSmsInitTask == null) {
			m_getSmsInitTask = new GetSMSInitTask();
			m_getSmsRollTimer.scheduleAtFixedRate(m_getSmsInitTask, 0, 10 * 1000);
		}
	}
	
	private void stopGetSmsInitTask() {
		if(m_getSmsInitTask != null) {
			m_getSmsInitTask.cancel();
			m_getSmsInitTask = null;
		}
	}
    
	class GetSMSInitTask extends TimerTask{ 
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSMSInitStatus(new IHttpFinishListener() {
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {               	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		SmsInitResult result = response.getModelResult();
                    		m_smsInit = SMSInit.build(result.Status);
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }

        			sendBroadcast(response, MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET);
                }
            }));
        } 
	}
	
	private void sentSMSInitChangedMessage(SMSInit cur) {
		if(m_smsInit != cur) {
			m_smsInit = cur;
			sendBroadcast(BaseResponse.SUCCESS, MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET);
		}
	}
	//GetSMSContactList ////////////////////////////////////////////////////////////////////////////////////////// 
	public void startGetContactMessagesTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSMSContactList") != true)
			return;
		
		if(m_smsInit == SMSInit.Initing)
			return;
		
		if(m_getContactMessagesTask == null) {
			m_getContactMessagesTask = new GetContactMessagesTask();
			m_getSmsRollTimer.scheduleAtFixedRate(m_getContactMessagesTask, 0, 30 * 1000);
		}
	}
	
	public void stopGetContactMessagesTask() {
		if(m_getContactMessagesTask != null) {
			m_getContactMessagesTask.cancel();
			m_getContactMessagesTask = null;
		}
	}
	
	public void getContactMessagesAtOnceRequest(){
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSMSContactList") != true)
			return;
		
		if(m_smsInit == SMSInit.Complete) {
			GetContactMessagesTask task = new GetContactMessagesTask();
			m_getSmsRollTimer.schedule(task, 0);
		}
	}
    
	class GetContactMessagesTask extends TimerTask{ 
        @Override
		public void run() { 
        	LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSMSContactList(0, new IHttpFinishListener() {
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {               	
                	String strErrcode = new String();
                    int ret = response.getResultCode();
                    if(ret == BaseResponse.RESPONSE_OK) {
                    	strErrcode = response.getErrorCode();
                    	if(strErrcode.length() == 0) {
                    		SmsContactListResult result = response.getModelResult();
                    		m_contactMessages.bulidFromResult(result);
                    	}else{
                    		
                    	}
                    }else{
                    	//Log
                    }

        			sendBroadcast(response, MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET);
                }
            }));
        } 
	}
	
	//GetSMSContentList ////////////////////////////////////////////////////////////////////////////////////////// 
	public static String SMS_CONTENT_LIST_EXTRA = "com.alcatel.smartlinkv3.business.smscontentlistextra";
	public void getSMSContentListRequest(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSMSContentList") != true)
			return;
		
		int nContactId = (Integer) data.getParamByKey("ContactId");
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSMSContentList(0,nContactId, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	SmsContentMessagesModel model = new SmsContentMessagesModel();
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		SmsContentListResult result = response.getModelResult();
                		model.buildFromResult(result);
                	}else{
                		
                	}
                }else{
                	//Log
                }
 
                Intent megIntent= new Intent(MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(SMS_CONTENT_LIST_EXTRA, model);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//DeleteSMS ////////////////////////////////////////////////////////////////////////////////////////// 
	public void deleteSms(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "DeleteSMS") != true)
			return;
		
		EnumSMSDelFlag delFlag = (EnumSMSDelFlag) data.getParamByKey("DelFlag");
		Integer temp =  (Integer)data.getParamByKey("ContactId");
		int nContactId = 0;
		if(temp != null)
			nContactId = temp;
		
		temp =  (Integer)data.getParamByKey("SMSId");
		int nSMSId = 0;
		if(temp != null)
			nSMSId = temp;
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.DeleteSMS(EnumSMSDelFlag.antiBuild(delFlag),nContactId,nSMSId, new IHttpFinishListener() {
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

    			sendBroadcast(response, MessageUti.SMS_DELETE_SMS_REQUSET);
            }
        }));
    } 
	
	private ArrayList<String> getNumberFromString(String number) {
		String[] listNumbers = number.split(";");
		ArrayList<String> phoneNumberLst = new ArrayList<String>();
		for (int i = 0; i < listNumbers.length; i++) {
			if(null == listNumbers[i] || listNumbers[i].length() == 0)
				continue;
			phoneNumberLst.add(listNumbers[i]);
		}
		
		return phoneNumberLst;
	}
	
	//SendSMS ////////////////////////////////////////////////////////////////////////////////////////// 
	public void sendSms(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "SendSMS") != true)
			return;
		
		String strContent = (String) data.getParamByKey("content");
		String strNumbers = (String)data.getParamByKey("phone_number");
		ArrayList<String> phoneNumberLst = getNumberFromString(strNumbers);
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.SendSMS(-1,strContent,phoneNumberLst, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
//            	int nSendId = 0;
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
//                		SendSmsResult sendSmsResult = response.getModelResult();
//                		nSendId = sendSmsResult.SmsSendId;
//                		
//                		//get send status
//                		DataValue dataValue = new DataValue();
//						dataValue.addParam("sms_send_id", nSendId);
						getSmsSendResult(null);
                	}else{
                		
                	}
                }else{
                	//Log
                }
     			sendBroadcast(response, MessageUti.SMS_SEND_SMS_REQUSET);
            }
        }));
    } 
	
//GetSendSMSResult ////////////////////////////////////////////////////////////////////////////////////////// 
	public void getSmsSendResult(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSendSMSResult") != true)
			return;
		
		if(m_smsInit == SMSInit.Initing)
			return;
		
		//final int nSmsSendId = (Integer) data.getParamByKey("sms_send_id");
    	
		LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.GetSendSMSResult(new IHttpFinishListener() {
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
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                megIntent.putExtra(Const.SMS_SNED_STATUS, nSendStatus);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//SaveSMS ////////////////////////////////////////////////////////////////////////////////////////// 
	public void SaveSMS(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "SaveSMS") != true)
			return;
		
		int nSmsId = (Integer) data.getParamByKey("SMSId");//the index of SMS, if savenew SMS is -1, else other.
		String strCotent =  (String)data.getParamByKey("Content");
		String strNumber =  (String)data.getParamByKey("Number");
		ArrayList<String> phoneNumberLst = getNumberFromString(strNumber);
		
		LegacyHttpClient.getInstance().sendPostRequest(new HttpSms.SaveSMS(nSmsId,strCotent,phoneNumberLst, new IHttpFinishListener() {
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
    			sendBroadcast(response, MessageUti.SMS_SAVE_SMS_REQUSET);
            }
        }));
    } 	
}
