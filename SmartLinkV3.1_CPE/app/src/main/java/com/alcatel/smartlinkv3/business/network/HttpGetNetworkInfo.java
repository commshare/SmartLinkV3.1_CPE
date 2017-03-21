package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetNetworkInfo {
	
/******************** GetNetworkInfo  **************************************************************************************/	
	public static class GetNetworkInfo extends BaseRequest
    {			
        public GetNetworkInfo(String strId,IHttpFinishListener callback) 
        {
        	super("GetNetworkInfo", strId, callback);
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
