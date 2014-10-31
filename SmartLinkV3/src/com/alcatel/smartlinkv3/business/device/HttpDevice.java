package com.alcatel.smartlinkv3.business.device;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpDevice {

	/******************** GetConnectedDeviceList **************************************************************************************/
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
	
	
	/******************** GetBlockDeviceList **************************************************************************************/
	public static class GetBlockDeviceList extends BaseRequest {	

		public GetBlockDeviceList(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetBlockDeviceList");			
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetBlockDeviceListResponse(m_finsishCallback);
		}

	}

	public static class GetBlockDeviceListResponse extends BaseResponse {

		private BlockDeviceList m_result = new BlockDeviceList();
		public GetBlockDeviceListResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, BlockDeviceList.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public BlockDeviceList getModelResult() {
			return m_result;
		}
	}
	
	
	/******************** SetConnectedDeviceBlock **************************************************************************************/
	public static class SetConnectedDeviceBlock extends BaseRequest {	
		
		private String m_strName;
		private String m_strMac;

		public SetConnectedDeviceBlock(String strId, String strName, String strMac, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_strName = strName;
			m_strMac = strMac;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetConnectedDeviceBlock");		
				
				JSONObject obj = new JSONObject();
				obj.put("DeviceName", m_strName);
				obj.put("MacAddress", m_strMac);
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, obj);				
	
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetConnectedDeviceBlockResponse(m_finsishCallback);
		}

	}

	public static class SetConnectedDeviceBlockResponse extends BaseResponse {

		public SetConnectedDeviceBlockResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {		
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getModelResult() {
			return null;
		}
	}
	
	/******************** SetDeviceUnlock **************************************************************************************/
	public static class SetDeviceUnlock extends BaseRequest {	
		
		private String m_strName;
		private String m_strMac;

		public SetDeviceUnlock(String strId, String strName, String strMac, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_strName = strName;
			m_strMac = strMac;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetDeviceUnlock");		
				
				JSONObject obj = new JSONObject();
				obj.put("DeviceName", m_strName);
				obj.put("MacAddress", m_strMac);
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, obj);				
	
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetDeviceUnlockResponse(m_finsishCallback);
		}

	}

	public static class SetDeviceUnlockResponse extends BaseResponse {
		
		public SetDeviceUnlockResponse(IHttpFinishListener callback) {
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
	
	/******************** SetDeviceUnlock **************************************************************************************/
	public static class SetDeviceName extends BaseRequest {	
		
		private String m_strName;
		private String m_strMac;
		private int m_nType;

		public SetDeviceName(String strId, String strName, String strMac, int nType, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_strName = strName;
			m_strMac = strMac;
			m_nType = nType;			
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetDeviceName");		
				
				JSONObject obj = new JSONObject();
				obj.put("DeviceName", m_strName);
				obj.put("MacAddress", m_strMac);
				obj.put("DeviceType", m_nType);
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, obj);				
	
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetDeviceNameResponse(m_finsishCallback);
		}

	}

	public static class SetDeviceNameResponse extends BaseResponse {
		
		public SetDeviceNameResponse(IHttpFinishListener callback) {
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
