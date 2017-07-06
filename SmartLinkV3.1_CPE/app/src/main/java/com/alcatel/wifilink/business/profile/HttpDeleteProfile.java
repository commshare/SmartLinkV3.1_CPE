package com.alcatel.wifilink.business.profile;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpDeleteProfile {

	public static class DeleteProfile extends BaseRequest{

		private int ProfileID;
		
		public DeleteProfile(int profileID, IHttpFinishListener callback) {
			super("Profile", "DeleteProfile", "15.4",  callback);
			setBroadcastAction(MessageUti.PROFILE_DELETE_PROFILE_REQUEST);
			ProfileID = profileID;
		}

		@Override
        protected void buildHttpParamJson() throws JSONException {
	        	JSONObject profileInfo = new JSONObject();
	        	profileInfo.put("ProfileID", ProfileID);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, profileInfo);
		}
	}
}
