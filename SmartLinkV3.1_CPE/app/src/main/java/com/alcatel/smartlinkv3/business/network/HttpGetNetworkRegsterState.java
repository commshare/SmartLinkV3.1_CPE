package com.alcatel.smartlinkv3.business.network;

import android.util.Log;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetNetworkRegsterState {
	public static class GetNetworkRegisterState extends BaseRequest{

		public GetNetworkRegisterState(String strId, IHttpFinishListener callback) {
			super("GetNetworkRegisterState", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new GetNetworkRegisterStateResponse(m_finsishCallback);
		}
		
	}
	
	public static class GetNetworkRegisterStateResponse extends BaseResponse{

		private GetNetworkRegsterStateResult RegisterState;
		public GetNetworkRegisterStateResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			RegisterState = gson.fromJson(strJsonResult, GetNetworkRegsterStateResult.class);
			try{
				Log.v("NetworkRegisterTest", "" + RegisterState.State);
			}catch(Exception e){
				Log.v("NetworkRegisterTest", "" + "Exception");
			}
		}

		@Override
		public GetNetworkRegsterStateResult getModelResult() {
			// TODO Auto-generated method stub
			return RegisterState;
		}
		
	}
	
	
	public class GetNetworkRegsterStateResult extends BaseResult{
		public int State;

		@Override
		protected void clear() {
			// TODO Auto-generated method stub
			State = 0;
		}
	}
}
