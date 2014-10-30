package com.alcatel.smartlinkv3.business.device;

import org.json.JSONException;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpDevice {

	/******************** GetSDCardSpace **************************************************************************************/
	public static class GetConnectedDeviceList extends BaseRequest {	

		public GetConnectedDeviceList(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetConnectedDeviceList");			
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetConnectedDeviceListResponse(m_finsishCallback);
		}

	}

	public static class GetConnectedDeviceListResponse extends BaseResponse {

		private ConnectedDeviceList m_result = new ConnectedDeviceList();
		public GetConnectedDeviceListResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, ConnectedDeviceList.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ConnectedDeviceList getModelResult() {
			return m_result;
		}
	}
	
	
}
