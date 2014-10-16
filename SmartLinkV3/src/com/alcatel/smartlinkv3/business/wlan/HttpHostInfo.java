package com.alcatel.smartlinkv3.business.wlan;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpHostInfo {
	
/******************** GetNumOfHosts  **************************************************************************************/	
	public static class GetNumOfHosts extends BaseRequest
    {			
        public GetNumOfHosts(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Wlan.GetNumOfHosts");
	        	
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
            return new GetNumOfHostsResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetNumOfHostsResponse extends BaseResponse
    {
		private HostNumberResult m_hostNumber;
        
        public GetNumOfHostsResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_hostNumber = gson.fromJson(strJsonResult, HostNumberResult.class);
        }

        @Override
        public HostNumberResult getModelResult() 
        {
             return m_hostNumber;
        }
    }
}
