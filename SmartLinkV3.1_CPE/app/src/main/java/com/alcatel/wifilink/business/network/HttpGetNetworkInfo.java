package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpGetNetworkInfo {
	
/******************** GetNetworkInfo  **************************************************************************************/	
	public static class GetNetworkInfo extends BaseRequest
    {			
        public GetNetworkInfo(IHttpFinishListener callback)
        {
        	super("Network", "GetNetworkInfo", "4.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetNetworkInfoResponse(m_finsishCallback);
            return new DataResponse<>(NetworkInfoResult.class, MessageUti.NETWORK_GET_NETWORK_INFO_ROLL_REQUSET, m_finsishCallback);
        }
    }
}
