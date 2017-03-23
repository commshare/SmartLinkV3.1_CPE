package com.alcatel.smartlinkv3.business.device;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpDevice {

	/******************** GetConnectedDeviceList **************************************************************************************/
	public static class GetConnectedDeviceList extends BaseRequest {	

		public GetConnectedDeviceList(IHttpFinishListener callback) {
			super("GetConnectedDeviceList", "12.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetConnectedDeviceListResponse(m_finsishCallback);
			return new DataResponse<>(ConnectedDeviceList.class, m_finsishCallback);
		}
	}

	/******************** GetBlockDeviceList **************************************************************************************/
	public static class GetBlockDeviceList extends BaseRequest {	

		public GetBlockDeviceList(IHttpFinishListener callback) {
			super("GetBlockDeviceList", "12.2", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetBlockDeviceListResponse(m_finsishCallback);
			return new DataResponse<>(BlockDeviceList.class, m_finsishCallback);
		}
	}

	/******************** SetConnectedDeviceBlock **************************************************************************************/
	public static class SetConnectedDeviceBlock extends BaseRequest {	
		
		private String m_strName;
		private String m_strMac;

		public SetConnectedDeviceBlock(String strName, String strMac, IHttpFinishListener callback) {
			super("SetConnectedDeviceBlock", "12.3", callback);
			m_strName = strName;
			m_strMac = strMac;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("DeviceName", m_strName);
			obj.put("MacAddress", m_strMac);
			m_requestParamJson.put(ConstValue.JSON_PARAMS, obj);
		}
	}

	
	/******************** SetDeviceUnlock **************************************************************************************/
	public static class SetDeviceUnlock extends BaseRequest {	
		
		private String m_strName;
		private String m_strMac;

		public SetDeviceUnlock(String strName, String strMac, IHttpFinishListener callback) {
			super("SetDeviceUnlock", "12.4", callback);
			m_strName = strName;
			m_strMac = strMac;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("DeviceName", m_strName);
			obj.put("MacAddress", m_strMac);
			m_requestParamJson.put(ConstValue.JSON_PARAMS, obj);
		}
	}

	/******************** SetDeviceUnlock **************************************************************************************/
	public static class SetDeviceName extends BaseRequest {	
		
		private String m_strName;
		private String m_strMac;
		private int m_nType;

		public SetDeviceName(String strName, String strMac, int nType, IHttpFinishListener callback) {
			super("SetDeviceName", "12.5", callback);
			m_strName = strName;
			m_strMac = strMac;
			m_nType = nType;			
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("DeviceName", m_strName);
			obj.put("MacAddress", m_strMac);
			obj.put("DeviceType", m_nType);

			m_requestParamJson.put(ConstValue.JSON_PARAMS, obj);
		}
	}
}