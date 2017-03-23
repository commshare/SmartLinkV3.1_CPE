package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpGetNetworkRegisterState {
	public static class GetNetworkRegisterState extends BaseRequest{

		public GetNetworkRegisterState(IHttpFinishListener callback) {
			super("GetNetworkRegisterState", "4.5", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new NetworkRegisterStateResponse(m_finsishCallback);
			return new DataResponse<>(NetworkRegisterStateResult.class, m_finsishCallback);
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
