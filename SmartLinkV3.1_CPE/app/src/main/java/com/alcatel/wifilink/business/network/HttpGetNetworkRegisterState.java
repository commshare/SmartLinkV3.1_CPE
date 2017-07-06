package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpGetNetworkRegisterState {
	public static class GetNetworkRegisterState extends BaseRequest{

		public GetNetworkRegisterState(IHttpFinishListener callback) {
			super("Network", "GetNetworkRegisterState", "4.5", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new NetworkRegisterStateResponse(m_finsishCallback);
			return new DataResponse<>(NetworkRegisterStateResult.class, null, m_finsishCallback);
		}
	}
	
	public class NetworkRegisterStateResult extends BaseResult{
		public int State;

		@Override
		protected void clear() {
			State = 0;
		}
	}
}
