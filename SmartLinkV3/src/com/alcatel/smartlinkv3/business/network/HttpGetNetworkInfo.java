package com.alcatel.smartlinkv3.business.network;

import org.json.JSONException;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetNetworkInfo {
	
/******************** GetNetworkInfo  **************************************************************************************/	
	public static class GetNetworkInfo extends BaseRequest
    {			
        public GetNetworkInfo(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetNetworkInfo");

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
            return new GetNetworkInfoResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetNetworkInfoResponse extends BaseResponse
    {
		private NetworkInfoResult m_networkInfo;
        
        public GetNetworkInfoResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_networkInfo = gson.fromJson(strJsonResult, NetworkInfoResult.class);
        }

        @Override
        public NetworkInfoResult getModelResult() 
        {
             return m_networkInfo;
        }
    }
}
