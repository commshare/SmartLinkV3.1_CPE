package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpRegisterNetwork {
	public static class RegisterNetwork extends BaseRequest{

		private int NetworkID;
		public RegisterNetwork(int networkID, IHttpFinishListener callback) {
			super("RegisterNetwork", "4.4",  callback);
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
