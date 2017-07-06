package com.alcatel.wifilink.business.sim;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpGetSimStatus {
	
/******************** GetSimStatus  **************************************************************************************/	
	public static class GetSimStatus extends BaseRequest
    {			
        public GetSimStatus(IHttpFinishListener callback)
        {
        	super("SIM", "GetSimStatus", "2.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetSimStatusResponse(m_finsishCallback);
            return new DataResponse<>(SIMStatusResult.class, MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET, m_finsishCallback);
        }
    }
}
