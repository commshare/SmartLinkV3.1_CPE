package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import java.util.List;

public class HttpGetProfileList {
	
	public static class GetProfileList extends BaseRequest{

		public GetProfileList(IHttpFinishListener callback) {
			super("GetProfileList", "15.1", callback);
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
