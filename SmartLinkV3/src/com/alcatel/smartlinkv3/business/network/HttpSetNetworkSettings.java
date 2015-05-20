package com.alcatel.smartlinkv3.business.network;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpSetNetworkSettings {
	public static class SetNetworkSettings extends BaseRequest{

		private int NetworkMode = 0;
		private int NetselectionMode = 0;
		private int NetworkBand = 0;
		public SetNetworkSettings(String strId, int networkMode, int netSelectionMode, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			NetworkMode = networkMode;
			NetselectionMode = netSelectionMode;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD, "SetNetworkSettings");
				
				JSONObject smsInfo = new JSONObject();	 
				smsInfo.put("NetworkMode", NetworkMode);
				smsInfo.put("NetselectionMode", NetselectionMode);
				smsInfo.put("NetworkBand", NetworkBand);
				
				m_requestParamJson.put(ConstValue.JSON_PARAMS, smsInfo);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
