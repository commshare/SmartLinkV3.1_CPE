package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.common.MessageUti;
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
