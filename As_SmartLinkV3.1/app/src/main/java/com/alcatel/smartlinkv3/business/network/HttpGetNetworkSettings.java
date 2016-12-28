package com.alcatel.smartlinkv3.business.network;

import org.json.JSONException;

import android.util.Log;

import com.alcatel.smartlinkv3.business.BaseResult;
import com.alcatel.smartlinkv3.business.network.HttpSearchNetworkResult.NetworkItemList;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpGetNetworkSettings {
	public static class GetNetworkSettings extends BaseRequest{

		public GetNetworkSettings(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetNetworkSettings");

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
			return new GetNetworkSettingsResponse(m_finsishCallback);
		}
		
	}
	
	public static class GetNetworkSettingsResponse extends BaseResponse{

		GetNetworkSettingResult m_net_worksetting_result;
		public GetNetworkSettingsResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_net_worksetting_result = gson.fromJson(strJsonResult, GetNetworkSettingResult.class);
		}

		@Override
		public GetNetworkSettingResult getModelResult() {
			// TODO Auto-generated method stub
			return m_net_worksetting_result;
		}
		
	}
	
	public static class GetNetworkSettingResult extends BaseResult{

		public int NetworkMode;
		public int NetselectionMode;
		public int NetworkBand;
		@Override
		protected void clear() {
			// TODO Auto-generated method stub
			NetselectionMode = 0;
			NetworkMode = 0;
		}
		
	}
}
