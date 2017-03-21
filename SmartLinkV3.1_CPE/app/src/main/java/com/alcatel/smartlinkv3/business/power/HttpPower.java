package com.alcatel.smartlinkv3.business.power;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpPower {

	/*get battery state*/
	public static class getBatteryStateRequest extends BaseRequest{

		public getBatteryStateRequest(String strId, IHttpFinishListener callback) {
			super("GetBatteryState", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new getBatteryStateResponse(m_finsishCallback);
		}
		
	}
	
	public static class getBatteryStateResponse extends BaseResponse{

		private BatteryInfo m_info = null;
		public getBatteryStateResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_info = gson.fromJson(strJsonResult, BatteryInfo.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public BatteryInfo getModelResult() {
			// TODO Auto-generated method stub
			return m_info;
		}
		
	}
	/*get power saving mode*/
	public static class getPowerSavingModeRequest extends BaseRequest{

		public getPowerSavingModeRequest(String strId, IHttpFinishListener callback) {
			super("GetPowerSavingMode", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new getPowerSavingModeResponse(m_finsishCallback);
		}
		
	}
	
	public static class getPowerSavingModeResponse extends BaseResponse{

		private PowerSavingModeInfo m_info = null;
		public getPowerSavingModeResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_info = gson.fromJson(strJsonResult, PowerSavingModeInfo.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public PowerSavingModeInfo getModelResult() {
			// TODO Auto-generated method stub
			return m_info;
		}
		
	}
	/*set power saving mode*/
	public static class setPowerSavingModeRequest extends BaseRequest{

		private PowerSavingModeInfo m_info=null;
		public setPowerSavingModeRequest(String strId, PowerSavingModeInfo info, IHttpFinishListener callback) {
			super("SetPowerSavingMode", strId, callback);
			m_info = info;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject jInfo = new JSONObject();
				jInfo.put("SmartMode", m_info.getSmartMode());
				jInfo.put("WiFiMode", m_info.getWiFiMode());
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, jInfo);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new setPowerSavingModeResponse(m_finsishCallback);
		}
		
	}
	
	public static class setPowerSavingModeResponse extends BaseResponse{

		private Boolean m_blRes = false;
		public setPowerSavingModeResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			if (0 != strJsonResult.length()) {
				m_blRes = true;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Boolean getModelResult() {
			// TODO Auto-generated method stub
			return m_blRes;
		}
		
	}
}
