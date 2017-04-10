package com.alcatel.smartlinkv3.business.device;

import com.alcatel.smartlinkv3.common.MessageUti;
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
			super("ConnectionDevices", "GetConnectedDeviceList", "12.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetConnectedDeviceListResponse(m_finsishCallback);
			return new DataResponse<>(ConnectedDeviceList.class, MessageUti.DEVICE_GET_CONNECTED_DEVICE_LIST, m_finsishCallback);
		}
	}

	/******************** GetBlockDeviceList **************************************************************************************/
	public static class GetBlockDeviceList extends BaseRequest {	

		public GetBlockDeviceList(IHttpFinishListener callback) {
			super("ConnectionDevices", "GetBlockDeviceList", "12.2", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetBlockDeviceListResponse(m_finsishCallback);
			return new DataResponse<>(BlockDeviceList.class, MessageUti.DEVICE_GET_BLOCK_DEVICE_LIST, m_finsishCallback);
		}
	}

	/******************** SetConnectedDeviceBlock **************************************************************************************/
	public static class SetConnectedDeviceBlock extends BaseRequest {	
		
		private String m_strName;
		private String m_strMac;

		public SetConnectedDeviceBlock(String strName, String strMac, IHttpFinishListener callback) {
			super("ConnectionDevices", "SetConnectedDeviceBlock", "12.3", callback);
			setBroadcastAction(MessageUti.DEVICE_SET_CONNECTED_DEVICE_BLOCK);
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
			super("ConnectionDevices", "SetDeviceUnlock", "12.4", callback);
			setBroadcastAction(MessageUti.DEVICE_SET_DEVICE_UNLOCK);
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
			super("ConnectionDevices", "SetDeviceName", "12.5", callback);
			setBroadcastAction(MessageUti.DEVICE_SET_DEVICE_NAME);
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