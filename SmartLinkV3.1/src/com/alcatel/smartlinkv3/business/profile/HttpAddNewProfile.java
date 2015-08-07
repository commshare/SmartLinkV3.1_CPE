package com.alcatel.smartlinkv3.business.profile;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpAddNewProfile {
	public static class AddNewProfile extends BaseRequest{

		private String m_strProfileName = new String();
		private String m_strAPN = new String();
		private String m_strUserName = new String();
		private String m_strPassword = new String();
		private int m_intAuthType = -1;
		private String m_strDialNumber = "*99#";
		
		public AddNewProfile(String strId, String profileName, String dialNumber, String apn, String userName, String passWord, int authType, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_strProfileName = profileName;
			m_strAPN = apn;
			m_strUserName = userName;
			m_strPassword = passWord;
			m_intAuthType = authType;
			m_strDialNumber = dialNumber;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "AddNewProfile");
	        	
	        	JSONObject profileInfo = new JSONObject();
	        	profileInfo.put("ProfileName", m_strProfileName);
	        	profileInfo.put("APN", m_strAPN);
	        	profileInfo.put("UserName", m_strUserName);
	        	profileInfo.put("Password", m_strPassword);
	        	profileInfo.put("AuthType", m_intAuthType);
	        	profileInfo.put("DailNumber", m_strDialNumber);
	        	profileInfo.put("IPAdrress", "");
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, profileInfo);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new AddNewProfileResponse(m_finsishCallback);
		}
		
	}
	
	
	public static class AddNewProfileResponse extends BaseResponse
    {
        
        public AddNewProfileResponse(IHttpFinishListener callback) 
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
