package com.alcatel.wifilink.business.network;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpRegisterNetwork {
	public static class RegisterNetwork extends BaseRequest{

		private int NetworkID;
		public RegisterNetwork(int networkID, IHttpFinishListener callback) {
			super("Network", "RegisterNetwork", "4.4",  callback);
			setBroadcastAction(MessageUti.NETWORK_REGESTER_NETWORK_REQUEST);
			NetworkID = networkID;
		}

		@Override
        protected void buildHttpParamJson() throws JSONException {
				JSONObject registerInfo = new JSONObject();
				registerInfo.put("NetworkID", NetworkID);
				
				m_requestParamJson.put(ConstValue.JSON_PARAMS, registerInfo);
		}
	}
}
