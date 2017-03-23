package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpGetNetworkInfo {
	
/******************** GetNetworkInfo  **************************************************************************************/	
	public static class GetNetworkInfo extends BaseRequest
    {			
        public GetNetworkInfo(IHttpFinishListener callback)
        {
        	super("GetNetworkInfo", "4.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetNetworkInfoResponse(m_finsishCallback);
            return new DataResponse<>(NetworkInfoResult.class, m_finsishCallback);
        }
    }
}
