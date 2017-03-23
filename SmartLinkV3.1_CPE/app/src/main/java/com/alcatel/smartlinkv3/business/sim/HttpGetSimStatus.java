package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

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
//            return new GetSimStatusResponse(m_finsishCallback);
            return new DataResponse<>(SIMStatusResult.class, m_finsishCallback);
        }
    }
}
