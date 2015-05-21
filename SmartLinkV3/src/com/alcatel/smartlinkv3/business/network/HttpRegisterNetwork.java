package com.alcatel.smartlinkv3.business.network;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.business.network.HttpSetNetworkSettings.SetNetworkSettingsResponse;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpRegisterNetwork {
	public static class RegisterNetwork extends BaseRequest{

		private int NetworkID;
		public RegisterNetwork(String strId, int networkID, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			NetworkID = networkID;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD, "RegisterNetwork");
				
				JSONObject registerInfo = new JSONObject();	 
				registerInfo.put("NetworkID", NetworkID);
				
				m_requestParamJson.put(ConstValue.JSON_PARAMS, registerInfo);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new RegisterNetworkResponse(m_finsishCallback);
		}
		
	}
	
	public static class RegisterNetworkResponse extends BaseResponse {

		public RegisterNetworkResponse(IHttpFinishListener callback) {
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
