package com.alcatel.smartlinkv3.business.calllog;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpCallLogOperation {
	
/******************** Delete call log  **************************************************************************************/	
	public static class DeleteCallLog extends BaseRequest
    {	
		private int m_nId = 0;

        public DeleteCallLog(String strId,int nId, IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nId = nId;        
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "CallLog.DeleteCallLog");
	        	
	        	JSONObject delete = new JSONObject();
	        	delete.put("Id", m_nId);	       
	        	
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
            return new DeleteCallLogResponse(m_finsishCallback);
        }
        
    }
	
	public static class DeleteCallLogResponse extends BaseResponse
    {
        
        public DeleteCallLogResponse(IHttpFinishListener callback) 
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
	
	/******************** Clear call log  **************************************************************************************/	
	public static class ClearCallLog extends BaseRequest
    {	
		private int m_nType = 0;

        public ClearCallLog(String strId,int nType, IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nType = nType;        
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "CallLog.DeleteCallLog");
	        	
	        	JSONObject delete = new JSONObject();
	        	delete.put("Type", m_nType);	       
	        	
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
            return new ClearCallLogResponse(m_finsishCallback);
        }
        
    }
	
	public static class ClearCallLogResponse extends BaseResponse
    {
        
        public ClearCallLogResponse(IHttpFinishListener callback) 
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
