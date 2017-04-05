package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpGetNetworkSettings {
	public static class GetNetworkSettings extends BaseRequest{

		public GetNetworkSettings(IHttpFinishListener callback) {
			super("Network", "GetNetworkSettings", "4.6", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetNetworkSettingsResponse(m_finsishCallback);
			return new DataResponse<>(GetNetworkSettingResult.class, m_finsishCallback);
		}
	}

	public static class GetNetworkSettingResult extends BaseResult{

		public int NetworkMode;
		public int NetselectionMode;
		public int NetworkBand;
		@Override
		protected void clear() {
			NetselectionMode = 0;
			NetworkMode = 0;
		}
		
	}
}
