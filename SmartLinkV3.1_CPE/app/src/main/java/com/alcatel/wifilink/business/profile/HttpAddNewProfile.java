package com.alcatel.wifilink.business.profile;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpAddNewProfile {
	public static class AddNewProfile extends BaseRequest{

		private String m_strProfileName = new String();
		private String m_strAPN = new String();
		private String m_strUserName = new String();
		private String m_strPassword = new String();
		private int m_intAuthType = -1;
		private String m_strDialNumber = "*99#";
		
		public AddNewProfile(String profileName, String dialNumber, String apn, String userName, String passWord, int authType, IHttpFinishListener callback) {
			super("Profile", "AddNewProfile", "15.2", callback);
			setBroadcastAction(MessageUti.PROFILE_ADD_NEW_PROFILE_REQUEST);
			m_strProfileName = profileName;
			m_strAPN = apn;
			m_strUserName = userName;
			m_strPassword = passWord;
			m_intAuthType = authType;
			m_strDialNumber = dialNumber;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
	        	JSONObject profileInfo = new JSONObject();
	        	profileInfo.put("ProfileName", m_strProfileName);
	        	profileInfo.put("APN", m_strAPN);
	        	profileInfo.put("UserName", m_strUserName);
	        	profileInfo.put("Password", m_strPassword);
	        	profileInfo.put("AuthType", m_intAuthType);
	        	profileInfo.put("DailNumber", m_strDialNumber);
	        	profileInfo.put("IPAdrress", "");
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, profileInfo);
		}
	}
}
