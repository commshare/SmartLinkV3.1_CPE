package com.alcatel.smartlinkv3.business.profile;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import java.util.List;

public class HttpGetProfileList {
	
	public static class GetProfileList extends BaseRequest{

		public GetProfileList(IHttpFinishListener callback) {
			super("GetProfileList", "15.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetProfileListResponse(m_finsishCallback);
			return new DataResponse<>(GetProfileListResult.class, m_finsishCallback);
		}
	}

	public class GetProfileListResult extends BaseResult{

		public List<ProfileItem> ProfileList;
		@Override
		protected void clear() {
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
