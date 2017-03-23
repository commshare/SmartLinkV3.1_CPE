package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

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
			super("AddNewProfile", "15.2", callback);
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