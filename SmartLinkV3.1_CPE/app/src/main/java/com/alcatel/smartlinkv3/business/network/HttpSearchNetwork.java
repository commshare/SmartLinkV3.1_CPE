package com.alcatel.smartlinkv3.business.network;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpSearchNetwork {
	public static class SearchNetwork extends BaseRequest{

		public SearchNetwork(String strId,IHttpFinishListener callback) {
			super("SearchNetwork", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SearchNetworkResponse(m_finsishCallback);
		}
		
	}
	
	public static class SearchNetworkResponse extends BaseResponse{

		public SearchNetworkResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> T getModelResult() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
