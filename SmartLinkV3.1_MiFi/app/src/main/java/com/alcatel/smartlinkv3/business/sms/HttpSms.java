package com.alcatel.smartlinkv3.business.sms;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HttpSms {
/******************** GetSMSInitStatus  **************************************************************************************/	
	public static class GetSMSInitStatus extends BaseRequest
    {	
        public GetSMSInitStatus(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSMSInitStatus");
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSMSInitStatusResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSMSInitStatusResponse extends BaseResponse
    {
        private SmsInitResult m_smsInit = new SmsInitResult();
        public GetSMSInitStatusResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	Gson gson = new Gson();
        	m_smsInit = gson.fromJson(strJsonResult, SmsInitResult.class);
        }

		@Override
        public SmsInitResult getModelResult() 
        {
             return m_smsInit;
        }
    }
	
/******************** GetSMSContactList  **************************************************************************************/	
	public static class GetSMSContactList extends BaseRequest
    {	
		private int m_nPage = 0;//0: return all Contact SMS.1: the first page ,every page has 10 contacts SMS list.
        public GetSMSContactList(String strId,int nPage,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nPage = nPage;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSMSContactList");
	        	
	        	JSONObject smsInfo = new JSONObject();	 
	        	smsInfo.put("Page", m_nPage);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, smsInfo);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSMSContactListResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSMSContactListResponse extends BaseResponse
    {
		private SmsContactListResult m_nSmsList;
        
        public GetSMSContactListResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_nSmsList = gson.fromJson(strJsonResult, SmsContactListResult.class);
        }

        @Override
        public SmsContactListResult getModelResult() 
        {
             return m_nSmsList;
        }
    }
	
/******************** GetSMSContentList  **************************************************************************************/	
	public static class GetSMSContentList extends BaseRequest
    {	
		private int m_nPage = 0;//Pages that SMS Content list separate,0: return allSMS content.1 : the first page ,every page has 10  SMS content list.
		private int m_nContactId = 0;
		
        public GetSMSContentList(String strId,int nPage,int nContactId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nPage = nPage;
        	m_nContactId = nContactId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSMSContentList");
	        	
	        	JSONObject smsInfo = new JSONObject();	 
	        	smsInfo.put("Page", m_nPage);
	        	smsInfo.put("ContactId", m_nContactId);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, smsInfo);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSMSContentListResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSMSContentListResponse extends BaseResponse
    {
		private SmsContentListResult m_nSmsList;
        
        public GetSMSContentListResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_nSmsList = gson.fromJson(strJsonResult, SmsContentListResult.class);
        }

        @Override
        public SmsContentListResult getModelResult() 
        {
             return m_nSmsList;
        }
    }
	
/******************** GetSMSStorageState  **************************************************************************************/	
	public static class GetSMSStorageState extends BaseRequest
    {	
        public GetSMSStorageState(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSMSStorageState");
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSMSStorageStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSMSStorageStateResponse extends BaseResponse
    {
		private SmsStorageStateResult m_nSmsState;
        
        public GetSMSStorageStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_nSmsState = gson.fromJson(strJsonResult, SmsStorageStateResult.class);
        }

        @Override
        public SmsStorageStateResult getModelResult() 
        {
             return m_nSmsState;
        }
    }
	
/******************** DeleteSMS  **************************************************************************************/	
	public static class DeleteSMS extends BaseRequest
    {	
		private int m_nDelFlag = 0;//This flag means the SMS that want to delete.0: delete all SMS1: delete one record in Contact SMS list 2:delete one record in Content  SMS list
		private int m_nContactId = 0;
		private int m_nSMSId = 0;
		
        public DeleteSMS(String strId,int nDelFlag,int nContactId,int nSMSId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nDelFlag = nDelFlag;
        	m_nContactId = nContactId;
        	m_nSMSId = nSMSId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "DeleteSMS");
	        	
	        	JSONObject obj = new JSONObject();	 
	        	obj.put("DelFlag", m_nDelFlag);
	        	obj.put("ContactId", m_nContactId);
	        	obj.put("SMSId", m_nSMSId);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, obj);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new DeleteSMSResponse(m_finsishCallback);
        }
        
    }
	
	public static class DeleteSMSResponse extends BaseResponse
    {
        
        public DeleteSMSResponse(IHttpFinishListener callback) 
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
	
/******************** SendSMS  **************************************************************************************/	
	public static class SendSMS extends BaseRequest
    {	
		private int m_nSMSId = 0;
		private String m_strSMSContent = new String();
		private ArrayList<String> m_lstNumber = new ArrayList<String>();
		
        public SendSMS(String strId,int nSMSId,String strSMSContent,ArrayList<String> lstNumber,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nSMSId = nSMSId;
        	m_strSMSContent = strSMSContent;
        	m_lstNumber = lstNumber;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SendSMS");
	        	
	        	JSONObject obj = new JSONObject();	 
	        	obj.put("SMSId", m_nSMSId);
	        	obj.put("SMSContent", m_strSMSContent);
	        	JSONArray array = new JSONArray();
	        	for(int i = 0;i < m_lstNumber.size();i++) {
	        		array.put(m_lstNumber.get(i));
	        	}
	        	obj.put("PhoneNumber", array);
	        	Date now = new Date();
	        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strTimeText = format.format(now);
	        	obj.put("SMSTime", strTimeText);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, obj);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SendSMSResponse(m_finsishCallback);
        }
        
    }
	
	public static class SendSMSResponse extends BaseResponse
    {

        public SendSMSResponse(IHttpFinishListener callback) 
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
	
/******************** GetSendSMSResult  **************************************************************************************/	
	public static class GetSendSMSResult extends BaseRequest
    {	
        public GetSendSMSResult(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSendSMSResult");
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSendSMSResultResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSendSMSResultResponse extends BaseResponse
    {
		private SendStatusResult result;

        public GetSendSMSResultResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	Gson gson = new Gson();
        	result = gson.fromJson(strJsonResult, SendStatusResult.class);
        }

        @Override
        public SendStatusResult getModelResult() 
        {
             return result;
        }
    }
	
	/******************** SaveSMS  **************************************************************************************/	
	public static class SaveSMS extends BaseRequest
    {	
		private int m_nSMSId = -1;
		private String m_strSMSContent = new String();
		private ArrayList<String> m_lstNumber = new ArrayList<String>();
		
        public SaveSMS(String strId,int nSMSId,String strSMSContent,ArrayList<String> lstNumber,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nSMSId = nSMSId;
        	m_strSMSContent = strSMSContent;
        	m_lstNumber = (ArrayList<String>) lstNumber.clone();
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SaveSMS");
	        	
	        	JSONObject obj = new JSONObject();	 
	        	obj.put("SMSId", m_nSMSId);
	        	obj.put("SMSContent", m_strSMSContent);
	        	JSONArray array = new JSONArray();
	        	for(int i = 0;i < m_lstNumber.size();i++) {
	        		array.put(m_lstNumber.get(i));
	        	}
	        	obj.put("PhoneNumber", array);
	        	Date now = new Date();
	        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strTimeText = format.format(now);
	        	obj.put("SMSTime", strTimeText);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, obj);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSendSMSResultResponse(m_finsishCallback);
        }
        
    }
	
	public static class SaveSMSResponse extends BaseResponse
    {
        public SaveSMSResponse(IHttpFinishListener callback) 
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
