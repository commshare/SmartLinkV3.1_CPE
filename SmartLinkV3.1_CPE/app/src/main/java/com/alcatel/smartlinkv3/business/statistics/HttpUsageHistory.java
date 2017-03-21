package com.alcatel.smartlinkv3.business.statistics;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUsageHistory {
	
/******************** GetUsageHistory  **************************************************************************************/	
	public static class GetUsageRecord extends BaseRequest
    {			
        public GetUsageRecord(String strId,IHttpFinishListener callback) 
        {
        	super("GetUsageRecord", strId, callback);

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
		String m_strUsageCleartime = new String();
		
        public SetUsageRecordClear(String strId,String Cleartime, IHttpFinishListener callback) 
        {
        	super("SetUsageRecordClear", strId, callback);
        	m_strUsageCleartime = Cleartime;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
            JSONObject usageClearTime = new JSONObject();
            usageClearTime.put("clear_time", m_strUsageCleartime);

            m_requestParamJson.put(ConstValue.JSON_PARAMS, usageClearTime);

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
