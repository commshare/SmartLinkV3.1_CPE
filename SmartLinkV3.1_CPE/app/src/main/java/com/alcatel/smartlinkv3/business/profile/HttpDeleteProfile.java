package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpDeleteProfile {

	public static class DeleteProfile extends BaseRequest{

		private int ProfileID;
		
		public DeleteProfile(String strId, int profileID, IHttpFinishListener callback) {
			super("DeleteProfile", strId, callback);
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
			return new DeleteProfileResponse(m_finsishCallback);
		}
		
	}
	
	public static class DeleteProfileResponse extends BaseResponse
    {
        
        public DeleteProfileResponse(IHttpFinishListener callback) 
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
