package com.alcatel.smartlinkv3.business.network;

import android.util.Log;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetNetworkRegisterState {
	public static class GetNetworkRegisterState extends BaseRequest{

		public GetNetworkRegisterState(IHttpFinishListener callback) {
			super("GetNetworkRegisterState", "4.5", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new NetworkRegisterStateResponse(m_finsishCallback);
		}
		
	}
	
	public static class NetworkRegisterStateResponse extends BaseResponse{

		private NetworkRegisterStateResult registerState;
		public NetworkRegisterStateResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			registerState = gson.fromJson(strJsonResult, NetworkRegisterStateResult.class);
			try{
				Log.v("NetworkRegisterTest", "" + registerState.State);
			}catch(Exception e){
				Log.v("NetworkRegisterTest", "" + "Exception");
			}
		}

		@Override
		public NetworkRegisterStateResult getModelResult() {
			return registerState;
		}
		
	}
	
	
	public class NetworkRegisterStateResult extends BaseResult{
		public int State;

		@Override
		protected void clear() {
			State = 0;
		}
	}
}
