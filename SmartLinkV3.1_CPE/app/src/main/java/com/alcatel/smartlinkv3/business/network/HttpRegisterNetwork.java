package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpRegisterNetwork {
	public static class RegisterNetwork extends BaseRequest{

		private int NetworkID;
		public RegisterNetwork(String strId, int networkID, IHttpFinishListener callback) {
			super("RegisterNetwork", strId, callback);
			NetworkID = networkID;
		}

		@Override
        protected void buildHttpParamJson() throws JSONException {

				JSONObject registerInfo = new JSONObject();	 
				registerInfo.put("NetworkID", NetworkID);
				
				m_requestParamJson.put(ConstValue.JSON_PARAMS, registerInfo);

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
