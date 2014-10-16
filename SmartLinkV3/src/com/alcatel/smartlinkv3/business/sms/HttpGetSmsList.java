package com.alcatel.smartlinkv3.business.sms;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetSmsList {
	
/******************** GetSmsList  **************************************************************************************/	
	public static class GetSmsList extends BaseRequest
    {	
		private int m_nPage = 0;//Pages that sms list separate Page from 1 start��0:all messsages
		private int m_nType = 5;//0 : Inbox 1 : Outbox 2 : Draftbox 3 : Status Report 4 : Cell broadcast message 5 : All message

        public GetSmsList(String strId,int nPage,int nType,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nPage = nPage;
        	m_nType = nType;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SMS.GetSmsList");
	        	
	        	JSONObject smsInfo = new JSONObject();	 
	        	smsInfo.put("Type", m_nType);
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
            return new GetSmsListResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSmsListResponse extends BaseResponse
    {
		private SmsListResult m_nSmsList;
        
        public GetSmsListResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_nSmsList = gson.fromJson(strJsonResult, SmsListResult.class);
        }

        @Override
        public SmsListResult getModelResult() 
        {
             return m_nSmsList;
        }
    }
}
