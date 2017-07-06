package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpGetNetworkSettings {
	public static class GetNetworkSettings extends BaseRequest{

		public GetNetworkSettings(IHttpFinishListener callback) {
			super("Network", "GetNetworkSettings", "4.6", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetNetworkSettingsResponse(m_finsishCallback);
			return new DataResponse<>(GetNetworkSettingResult.class, MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST, m_finsishCallback);
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
