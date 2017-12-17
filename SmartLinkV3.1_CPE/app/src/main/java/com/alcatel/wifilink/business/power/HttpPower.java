package com.alcatel.wifilink.business.power;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.BooleanResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpPower {

	/*getInstant battery state*/
	public static class getBatteryStateRequest extends BaseRequest{

		public getBatteryStateRequest(IHttpFinishListener callback) {
			super("PowerManagement","GetBatteryState", "16.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new getBatteryStateResponse(m_finsishCallback);
			return new DataResponse<>(BatteryInfo.class, MessageUti.POWER_GET_BATTERY_STATE, m_finsishCallback);
		}
	}

	/*getInstant power saving mode*/
	public static class getPowerSavingModeRequest extends BaseRequest{

		public getPowerSavingModeRequest(IHttpFinishListener callback) {
			super("PowerManagement","GetPowerSavingMode", "16.2", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new getPowerSavingModeResponse(m_finsishCallback);
			return new DataResponse<>(PowerSavingModeInfo.class, MessageUti.POWER_GET_POWER_SAVING_MODE, m_finsishCallback);
		}
	}

	/*set power saving mode*/
	public static class setPowerSavingModeRequest extends BaseRequest{

		private PowerSavingModeInfo m_info=null;
		public setPowerSavingModeRequest(PowerSavingModeInfo info, IHttpFinishListener callback) {
			super("PowerManagement","SetPowerSavingMode", "16.3", callback);
			m_info = info;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject jInfo = new JSONObject();
				jInfo.put("SmartMode", m_info.getSmartMode());
				jInfo.put("WiFiMode", m_info.getWiFiMode());
				m_requestParamJson.put(ConstValue.JSON_PARAMS, jInfo);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new BooleanResponse(MessageUti.POWER_SET_POWER_SAVING_MODE, m_finsishCallback);
		}
	}
}
