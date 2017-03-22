package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetSimStatus {
	
/******************** GetSimStatus  **************************************************************************************/	
	public static class GetSimStatus extends BaseRequest
    {			
        public GetSimStatus(IHttpFinishListener callback)
        {
        	super("GetSimStatus", "2.1", callback);
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
