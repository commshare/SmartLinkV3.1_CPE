package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.List;

public class HttpGetProfileList {
	
	public static class GetProfileList extends BaseRequest{

		public GetProfileList(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetProfileList");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new GetProfileListResponse(m_finsishCallback);
		}
		
	}
	
	public static class GetProfileListResponse extends BaseResponse{

		private GetProfileListResult getProfileListResult;
		
		public GetProfileListResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			getProfileListResult = gson.fromJson(strJsonResult, GetProfileListResult.class);
		}

		@Override
		public GetProfileListResult getModelResult() {
			// TODO Auto-generated method stub
			return getProfileListResult;
		}
		
	}
	
	public class GetProfileListResult extends BaseResult{

		public List<ProfileItem> ProfileList;
		@Override
		protected void clear() {
			// TODO Auto-generated method stub
			ProfileList.clear();
		}
		
	}
	
	
	
	public class ProfileItem {
		public String ProfileName;
		public String APN;
		public String UserName;
		public String Password;
		public int Default;
		public int ProfileID;
		public int IsPredefine;
		public int AuthType;
		public String DailNumber;
		public String IPAddress;
		public boolean IsDeleteCheck;
	}

}
