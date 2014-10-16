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
	public static class GetUsageHistory extends BaseRequest
    {			
		private String m_strStartDay;
		private String m_strEndDay;
        public GetUsageHistory(String strId,String strStartDay,String strEndDay,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_strStartDay = strStartDay;
        	m_strEndDay = strEndDay;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.GetUsageHistory");

	        	JSONObject usageHistory = new JSONObject();
	        	usageHistory.put("StartDay", m_strStartDay);
	        	usageHistory.put("EndDay", m_strEndDay);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, usageHistory);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetUsageHistoryResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetUsageHistoryResponse extends BaseResponse
    {
		private UsageHistoryResult m_usageHistory;
        
        public GetUsageHistoryResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_usageHistory = gson.fromJson(strJsonResult, UsageHistoryResult.class);
        }

        @Override
        public UsageHistoryResult getModelResult() 
        {
             return m_usageHistory;
        }
    }
	
	/******************** ClearAllRecords  **************************************************************************************/	
	public static class ClearAllRecords extends BaseRequest
    {	
        public ClearAllRecords(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Statistics.ClearAllRecords");
	        	
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
            return new ClearAllRecordsResponse(m_finsishCallback);
        }
        
    }
	
	public static class ClearAllRecordsResponse extends BaseResponse
    {   
        public ClearAllRecordsResponse(IHttpFinishListener callback) 
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
