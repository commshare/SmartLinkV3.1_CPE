package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpEditProfile {
	public static class EditProfile extends BaseRequest{

		private int m_intProfileID = -1;
		private String m_strProfileName = new String();
		private String m_strAPN = new String();
		private String m_strUserName = new String();
		private String m_strPassword = new String();
		private int m_intAuthType = -1;
		private String m_strDialNumber = "*99#";
		
		public EditProfile(String strId, int profileID, String dialNumber, String profileName, String apn, String userName, String passWord, int authType, IHttpFinishListener callback) {
			super("EditProfile", strId, callback);
			m_intProfileID = profileID;
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
	        	profileInfo.put("ProfileID", m_intProfileID);
	        	profileInfo.put("ProfileName", m_strProfileName);
	        	profileInfo.put("APN", m_strAPN);
	        	profileInfo.put("UserName", m_strUserName);
	        	profileInfo.put("Password", m_strPassword);
	        	profileInfo.put("AuthType", m_intAuthType);
	        	profileInfo.put("DailNumber", m_strDialNumber);
	        	profileInfo.put("IPAdrress", "");
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, profileInfo);
		}
		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new EditProfileResponse(m_finsishCallback);
		}
		
	}
	
	public static class EditProfileResponse extends BaseResponse
    {
        
        public EditProfileResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
		protected void parseContent(String strJsonResult) {			
		}

		@Override
		public <T> T getModelResult() {		
			return null;
		} 
    }
}
