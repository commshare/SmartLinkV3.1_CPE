package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

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