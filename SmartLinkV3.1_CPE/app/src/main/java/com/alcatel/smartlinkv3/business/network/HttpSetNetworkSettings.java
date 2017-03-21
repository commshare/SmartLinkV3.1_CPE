package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSetNetworkSettings {
	public static class SetNetworkSettings extends BaseRequest{

		private int NetworkMode = 0;
		private int NetselectionMode = 0;
		private int NetworkBand = 0;
		public SetNetworkSettings(String strId, int networkMode, int netSelectionMode, IHttpFinishListener callback) {
			super("SetNetworkSettings", strId, callback);
			NetworkMode = networkMode;
			NetselectionMode = netSelectionMode;
		}

		@Override
        protected void buildHttpParamJson() throws JSONException {
				JSONObject settingInfo = new JSONObject();	 
				settingInfo.put("NetworkMode", NetworkMode);
				settingInfo.put("NetselectionMode", NetselectionMode);
				settingInfo.put("NetworkBand", NetworkBand);
				
				m_requestParamJson.put(ConstValue.JSON_PARAMS, settingInfo);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetNetworkSettingsResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetNetworkSettingsResponse extends BaseResponse {

		public SetNetworkSettingsResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> T getModelResult() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
