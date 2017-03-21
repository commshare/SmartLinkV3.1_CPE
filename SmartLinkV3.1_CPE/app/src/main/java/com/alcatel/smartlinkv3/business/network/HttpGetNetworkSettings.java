package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetNetworkSettings {
	public static class GetNetworkSettings extends BaseRequest{

		public GetNetworkSettings(String strId, IHttpFinishListener callback) {
			super("GetNetworkSettings", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new GetNetworkSettingsResponse(m_finsishCallback);
		}
		
	}
	
	public static class GetNetworkSettingsResponse extends BaseResponse{

		GetNetworkSettingResult m_net_worksetting_result;
		public GetNetworkSettingsResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_net_worksetting_result = gson.fromJson(strJsonResult, GetNetworkSettingResult.class);
		}

		@Override
		public GetNetworkSettingResult getModelResult() {
			// TODO Auto-generated method stub
			return m_net_worksetting_result;
		}
		
	}
	
	public static class GetNetworkSettingResult extends BaseResult{

		public int NetworkMode;
		public int NetselectionMode;
		public int NetworkBand;
		@Override
		protected void clear() {
			// TODO Auto-generated method stub
			NetselectionMode = 0;
			NetworkMode = 0;
		}
		
	}
}
