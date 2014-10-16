package com.alcatel.smartlinkv3.business.sms;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpSmsOperation {
	
/******************** DeleteSms  **************************************************************************************/	
	public static class DeleteSms extends BaseRequest
    {	
		private int m_nId = 0;
		private int m_nStoreIn = 1;

        public DeleteSms(String strId,int nId,int nStoreIn,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nId = nId;
        	m_nStoreIn = nStoreIn;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SMS.DeleteSms");
	        	
	        	JSONObject delete = new JSONObject();
	        	delete.put("Id", m_nId);
	        	delete.put("StoreIn", m_nStoreIn);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, delete);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new DeleteSmsResponse(m_finsishCallback);
        }
        
    }
	
	public static class DeleteSmsResponse extends BaseResponse
    {
        
        public DeleteSmsResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	
	/******************** SendSms  **************************************************************************************/	
	public static class SendSms extends BaseRequest
    {	
		private String m_strContent = new String();
		private String m_strPhoneNumber = new String();

        public SendSms(String strId,String strContent,String strPhoneNumber,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_strContent = strContent;
        	m_strPhoneNumber = strPhoneNumber;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SMS.SendSms");
	        	
	        	JSONObject send = new JSONObject();
	        	send.put("Id", -1);//the ID of the SMS in draft to send, and other SMS send, the value is -1
	        	send.put("Content", m_strContent);
	        	send.put("PhoneNumber", m_strPhoneNumber);
	        	send.put("Priority", "Normal");//The message priority
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, send);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SendSmsResponse(m_finsishCallback);
        }
        
    }
	
	public static class SendSmsResponse extends BaseResponse
    {
		private SendSmsResult m_sendSmsResult;
		
        public SendSmsResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	Gson gson = new Gson();
        	m_sendSmsResult = gson.fromJson(strJsonResult, SendSmsResult.class);
        }

        @Override
        public SendSmsResult getModelResult() 
        {
             return m_sendSmsResult;
        }
    }
	
	/******************** GetSmsSendResult  **************************************************************************************/	
	public static class GetSmsSendResult extends BaseRequest
    {	
		private int m_nSmsSendId = 0;

        public GetSmsSendResult(String strId,int nSmsSendId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nSmsSendId = nSmsSendId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SMS.GetSendSmsResult");
	        	
	        	JSONObject sendStatus = new JSONObject();
	        	sendStatus.put("SmsSendId", m_nSmsSendId);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, sendStatus);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSmsSendResultResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSmsSendResultResponse extends BaseResponse
    {
		private SendStatusResult m_sendStatusResult;
		
        public GetSmsSendResultResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	Gson gson = new Gson();
        	m_sendStatusResult = gson.fromJson(strJsonResult, SendStatusResult.class);
        }

        @Override
        public SendStatusResult getModelResult() 
        {
             return m_sendStatusResult;
        }
    }
	
	/******************** ModifySMSReadStatus  **************************************************************************************/	
	public static class ModifySMSReadStatus extends BaseRequest
    {	
		private int m_nId = 0;
		private int m_nStatus = 0;//Message read status. 0 - Read   1 - Unread 


        public ModifySMSReadStatus(String strId,int nId,int nStatus,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nId = nId;
        	m_nStatus = nStatus;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SMS.ModifySmsReadStatus");
	        	
	        	JSONObject sendStatus = new JSONObject();
	        	sendStatus.put("Id", m_nId);
	        	sendStatus.put("Status", m_nStatus);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, sendStatus);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new  ModifySMSReadStatusResponse(m_finsishCallback);
        }
        
    }
	
	public static class ModifySMSReadStatusResponse extends BaseResponse
    {
        public ModifySMSReadStatusResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {

        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
}
