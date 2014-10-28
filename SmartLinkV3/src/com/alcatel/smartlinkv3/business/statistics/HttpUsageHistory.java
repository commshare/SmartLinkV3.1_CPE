package com.alcatel.smartlinkv3.business.statistics;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpUsageHistory {
	
/******************** GetUsageHistory  **************************************************************************************/	
	public static class GetUsageRecord extends BaseRequest
    {			
        public GetUsageRecord(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetUsageRecord");
	        	
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
            return new GetUsageRecordResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetUsageRecordResponse extends BaseResponse
    {
		private UsageRecordResult m_usageRecord;
        
        public GetUsageRecordResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_usageRecord = gson.fromJson(strJsonResult, UsageRecordResult.class);
        }

        @SuppressWarnings("unchecked")
		@Override
        public UsageRecordResult getModelResult() 
        {
             return m_usageRecord;
        }
    }
	
	/******************** SetUsageRecordClear  **************************************************************************************/	
	public static class SetUsageRecordClear extends BaseRequest
    {	
        public SetUsageRecordClear(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetUsageRecordClear");
	        	
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
            return new SetUsageRecordClearResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetUsageRecordClearResponse extends BaseResponse
    {   
        public SetUsageRecordClearResponse(IHttpFinishListener callback) 
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
