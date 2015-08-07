package com.alcatel.smartlinkv3.business.network;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult.NetworkItemList;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetNetworkRegsterState {
	public static class GetNetworkRegisterState extends BaseRequest{

		public GetNetworkRegisterState(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD, "GetNetworkRegisterState");
				
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
