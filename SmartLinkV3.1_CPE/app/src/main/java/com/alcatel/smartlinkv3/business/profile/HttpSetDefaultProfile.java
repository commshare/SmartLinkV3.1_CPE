package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSetDefaultProfile {
	
	public static class SetDefaultProfile extends BaseRequest{
		
		private int ProfileID;

		public SetDefaultProfile(String strId, int profileID, IHttpFinishListener callback) {
			super("SetDefaultProfile", strId, callback);
			m_strId = strId;
			ProfileID = profileID;
		}

		@Override
        protected void buildHttpParamJson() throws JSONException {
	        	JSONObject profileInfo = new JSONObject();
	        	profileInfo.put("ProfileID", ProfileID);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, profileInfo);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetDefaultProfileResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetDefaultProfileResponse extends BaseResponse
    {
        
        public SetDefaultProfileResponse(IHttpFinishListener callback) 
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
