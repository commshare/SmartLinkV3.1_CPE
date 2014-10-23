package com.alcatel.smartlinkv3.business.sim;

import org.json.JSONException;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetSimStatus {
	
/******************** GetSimStatus  **************************************************************************************/	
	public static class GetSimStatus extends BaseRequest
    {			
        public GetSimStatus(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSimStatus");

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
            return new GetSimStatusResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSimStatusResponse extends BaseResponse
    {
		private SIMStatusResult m_simStatus;
        
        public GetSimStatusResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_simStatus = gson.fromJson(strJsonResult, SIMStatusResult.class);
        }

        @Override
        public SIMStatusResult getModelResult() 
        {
             return m_simStatus;
        }
    }
}
