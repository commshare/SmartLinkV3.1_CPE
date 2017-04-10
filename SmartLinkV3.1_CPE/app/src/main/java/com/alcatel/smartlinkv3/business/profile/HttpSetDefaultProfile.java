package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSetDefaultProfile {
	
	public static class SetDefaultProfile extends BaseRequest{
		
		private int ProfileID;

		public SetDefaultProfile(int profileID, IHttpFinishListener callback) {
			super("Profile", "SetDefaultProfile", "15.5",  callback);
			setBroadcastAction(MessageUti.PROFILE_SET_DEFAULT_PROFILE_REQUEST);
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
