package com.alcatel.smartlinkv3.business;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.SmsContactMessagesModel;
import com.alcatel.smartlinkv3.business.model.SmsContentMessagesModel;
import com.alcatel.smartlinkv3.business.sms.HttpSms;
import com.alcatel.smartlinkv3.business.sms.SMSContactItem;
import com.alcatel.smartlinkv3.business.sms.SendSmsResult;
import com.alcatel.smartlinkv3.business.sms.SendStatusResult;
import com.alcatel.smartlinkv3.business.sms.SmsContactListResult;
import com.alcatel.smartlinkv3.business.sms.SmsContentListResult;
import com.alcatel.smartlinkv3.business.sms.SmsInitResult;
import com.alcatel.smartlinkv3.common.Const;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.EnumSMSDelFlag;
import com.alcatel.smartlinkv3.common.ENUM.SMSInit;
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
		// TODO Auto-generated method stub
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {
				
			}
    	}
		
		if(intent.getAction().equals(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				if(m_smsInit == SMSInit.Complete) {
					stopGetSmsInitTask();
					startGetContactMessagesTask();
				}
			}
    	}
		
		if(intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
			int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
				SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
				if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
					startGetSmsInitTask();
				}else{
					m_smsInit = SMSInit.Initing;
					
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
		return m_contactMessages.clone();
	}	
	
	public int GetUnreadSmsNumber(){
		int nUnreadCount = 0;
		ArrayList<SMSContactItem> lst = (ArrayList<SMSContactItem>) m_contactMessages.SMSContactList.clone();
		if(null != lst){
			for(int nIndex = 0; nIndex < lst.size(); nIndex++)
			{
				SMSContactItem sms = lst.get(nIndex);
				nUnreadCount += sms.UnreadCount; 
			}		
		}
		
    	return nUnreadCount;	    	
	} 
	//GetSMSInitStatus ////////////////////////////////////////////////////////////////////////////////////////// 
	private void startGetSmsInitTask() {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "GetSMSInitStatus") != true)
			return;
		
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
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
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpSms.GetSMSInitStatus("6.1", new IHttpFinishListener() {           
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
                    
                    Intent megIntent= new Intent(MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        			m_context.sendBroadcast(megIntent);
                }
            }));
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
			m_getSmsRollTimer.scheduleAtFixedRate(m_getContactMessagesTask, 0, 120 * 1000);
		}
	}
	
	public void stopGetContactMessagesTask() {
		if(m_getContactMessagesTask != null) {
			m_getContactMessagesTask.cancel();
			m_getContactMessagesTask = null;
		}
	}
	
	public void getContactMessagesAtOnceRequest(){
		SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
		if(simStatus.m_SIMState == ENUM.SIMState.Accessable) {
			GetContactMessagesTask task = new GetContactMessagesTask();
			m_getSmsRollTimer.schedule(task, 0);
		}
	}
    
	class GetContactMessagesTask extends TimerTask{ 
        @Override
		public void run() { 
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpSms.GetSMSContactList("6.2",0, new IHttpFinishListener() {           
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
                    
                    Intent megIntent= new Intent(MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET);
                    megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                    megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
        			m_context.sendBroadcast(megIntent);
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
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSms.GetSMSContentList("6.3",0,nContactId, new IHttpFinishListener() {           
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
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
                megIntent.putExtra(SMS_CONTENT_LIST_EXTRA, model);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	//DeleteSMS ////////////////////////////////////////////////////////////////////////////////////////// 
	public void deleteSms(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("SMS", "DeleteSMS") != true)
			return;
		
		EnumSMSDelFlag delFlag = (EnumSMSDelFlag) data.getParamByKey("delete_falg");
		Integer temp =  (Integer)data.getParamByKey("contact_id");
		int nContactId = 0;
		if(temp != null)
			nContactId = temp;
		
		temp =  (Integer)data.getParamByKey("sms_id");
		int nSMSId = 0;
		if(temp != null)
			nSMSId = temp;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpSms.DeleteSMS("6.5",EnumSMSDelFlag.antiBuild(delFlag),nContactId,nSMSId, new IHttpFinishListener() {           
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
	
	/*//SendSms ////////////////////////////////////////////////////////////////////////////////////////// 
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
    }*/
}
