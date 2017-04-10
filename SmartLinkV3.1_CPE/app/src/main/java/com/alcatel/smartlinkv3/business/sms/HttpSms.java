package com.alcatel.smartlinkv3.business.sms;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

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
        public GetSMSInitStatus(IHttpFinishListener callback)
        {
        	super("SMS", "GetSMSInitStatus", "6.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetSMSInitStatusResponse(m_finsishCallback);
            return new DataResponse<>(SmsInitResult.class, MessageUti.SMS_GET_SMS_INIT_ROLL_REQUSET,m_finsishCallback);
        }
        
    }

/******************** GetSMSContactList  **************************************************************************************/	
	public static class GetSMSContactList extends BaseRequest
    {	
		private int m_nPage = 0;//0: return all Contact SMS.1: the first page ,every page has 10 contacts SMS list.
        public GetSMSContactList(int nPage,IHttpFinishListener callback)
        {
        	super("SMS", "GetSMSContactList", "6.2", callback);
        	m_nPage = nPage;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject smsInfo = new JSONObject();	 
	        	smsInfo.put("Page", m_nPage);
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, smsInfo);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetSMSContactListResponse(m_finsishCallback);
            return new DataResponse<>(SmsContactListResult.class, MessageUti.SMS_GET_SMS_CONTACT_LIST_ROLL_REQUSET,
                    m_finsishCallback);
        }
    }

	
/******************** GetSMSContentList  **************************************************************************************/	
	public static class GetSMSContentList extends BaseRequest
    {	
		private int m_nPage = 0;//Pages that SMS Content list separate,0: return allSMS content.1 : the first page ,every page has 10  SMS content +
        // list.2��..
		private int m_nContactId = 0;
		
        public GetSMSContentList(int nPage,int nContactId,IHttpFinishListener callback)
        {
        	super("SMS", "GetSMSContentList", "6.3", callback);
        	m_nPage = nPage;
        	m_nContactId = nContactId;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject smsInfo = new JSONObject();	 
	        	smsInfo.put("Page", m_nPage);
	        	smsInfo.put("ContactId", m_nContactId);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, smsInfo);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetSMSContentListResponse(m_finsishCallback);
            return new DataResponse<>(SmsContentListResult.class, MessageUti.SMS_GET_SMS_CONTENT_LIST_REQUSET, m_finsishCallback);
        }
        
    }

/******************** GetSMSStorageState  **************************************************************************************/	
	public static class GetSMSStorageState extends BaseRequest
    {	
        public GetSMSStorageState(IHttpFinishListener callback)
        {
        	super("SMS", "GetSMSStorageState", "6.4", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetSMSStorageStateResponse(m_finsishCallback);
            return new DataResponse<>(SmsStorageStateResult.class, null, m_finsishCallback);
        }
    }

	
/******************** DeleteSMS  **************************************************************************************/	
	public static class DeleteSMS extends BaseRequest
    {	
		private int m_nDelFlag = 0;//This flag means the SMS that want to delete.0: delete all SMS1: delete one record in Contact SMS list 2:delete one record in Content  SMS list
		private int m_nContactId = 0;
		private int m_nSMSId = 0;
		
        public DeleteSMS(int nDelFlag,int nContactId,int nSMSId,IHttpFinishListener callback)
        {
        	super("SMS", "DeleteSMS", "6.5", callback);
            setBroadcastAction(MessageUti.SMS_DELETE_SMS_REQUSET);
        	m_nDelFlag = nDelFlag;
        	m_nContactId = nContactId;
        	m_nSMSId = nSMSId;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject obj = new JSONObject();	 
	        	obj.put("DelFlag", m_nDelFlag);
	        	obj.put("ContactId", m_nContactId);
	        	obj.put("SMSId", m_nSMSId);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, obj);
        }
    }

	
/******************** SendSMS  **************************************************************************************/	
	public static class SendSMS extends BaseRequest
    {	
		private int m_nSMSId = 0;
		private String m_strSMSContent = new String();
		private ArrayList<String> m_lstNumber = new ArrayList<String>();
		
        public SendSMS(int nSMSId,String strSMSContent,ArrayList<String> lstNumber,IHttpFinishListener callback)
        {
        	super("SMS", "SendSMS", "6.6", callback);
            setBroadcastAction(MessageUti.SMS_SEND_SMS_REQUSET);
        	m_nSMSId = nSMSId;
        	m_strSMSContent = strSMSContent;
        	m_lstNumber = lstNumber;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
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
        }
    }

	
/******************** GetSendSMSResult  **************************************************************************************/	
	public static class GetSendSMSResult extends BaseRequest
    {	
        public GetSendSMSResult(IHttpFinishListener callback)
        {
        	super("SMS", "GetSendSMSResult", "6.7", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetSendSMSResultResponse(m_finsishCallback);
            return new DataResponse<>(SendStatusResult.class, MessageUti.SMS_GET_SEND_STATUS_REQUSET, m_finsishCallback);
        }
    }

	
	/******************** SaveSMS  **************************************************************************************/	
	public static class SaveSMS extends BaseRequest
    {	
		private int m_nSMSId = -1;
		private String m_strSMSContent = "";
		private ArrayList<String> m_lstNumber = new ArrayList<>();
		
        public SaveSMS(int nSMSId,String strSMSContent,ArrayList<String> lstNumber,IHttpFinishListener callback)
        {
        	super("SMS", "SaveSMS", "6.8", callback);
        	m_nSMSId = nSMSId;
        	m_strSMSContent = strSMSContent;
        	m_lstNumber = (ArrayList<String>) lstNumber.clone();
//            Arrays.copyOf()
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	
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
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetSendSMSResultResponse(m_finsishCallback);
            return new DataResponse<>(SendStatusResult.class,  MessageUti.SMS_SAVE_SMS_REQUSET, m_finsishCallback);
        }
        
    }

}
