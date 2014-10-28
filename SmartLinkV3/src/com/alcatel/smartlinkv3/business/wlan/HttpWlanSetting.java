package com.alcatel.smartlinkv3.business.wlan;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;

import com.alcatel.smartlinkv3.business.lan.LanInfo;
import com.alcatel.smartlinkv3.business.lan.HttpLan.getLanSettingsResponse;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpWlanSetting {

	/******************** get wlan setting **************************************************************************************/
	public static class GetWlanSetting extends BaseRequest {
		
		public GetWlanSetting(String strId,	IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetWlanSettings");

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetWlanSettingResponse(m_finsishCallback);
		}

	}

	public static class GetWlanSettingResponse extends BaseResponse {

		WlanSettingResult m_result = new WlanSettingResult();
		public GetWlanSettingResponse(IHttpFinishListener callback) {
			super(callback);
		}
		
		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, WlanSettingResult.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public WlanSettingResult getModelResult() {
			return m_result;
		}
	}	
	
	/******************** set wlan setting **************************************************************************************/
	public static class SetWlanSetting extends BaseRequest {

		public WlanSettingResult m_result = new WlanSettingResult();

		public SetWlanSetting(String strId,	WlanSettingResult result, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_result = result;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"Wlan.SetWlanSettings");
				
				JSONObject settings = new JSONObject();
				settings.put("WlanFrequency", m_result.WlanFrequency);
				settings.put("Ssid5G", m_result.SsidHidden5G);
				settings.put("Ssid", m_result.Ssid);
				settings.put("SsidHidden5G", m_result.SsidHidden5G);
				settings.put("SsidHidden", m_result.SsidHidden);
				settings.put("CountryCode", m_result.CountryCode);
				settings.put("SecurityMode", m_result.SecurityMode);
				settings.put("WpaType", m_result.WpaType);				
				settings.put("WpaKey", m_result.WpaKey);
				settings.put("WepType", m_result.WepType);
				settings.put("WepKey", m_result.WepKey);
				settings.put("Channel", m_result.Channel);
				settings.put("ApIsolation5G", m_result.ApIsolation5G);
				settings.put("ApIsolation", m_result.ApIsolation);
				settings.put("Bandwidth", m_result.Bandwidth);
				settings.put("WMode", m_result.WMode);
				settings.put("NumOfHosts", m_result.NumOfHosts);
				settings.put("MaxNumOfHosts", m_result.MaxNumOfHosts);
				
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetWlanSettingResponse(m_finsishCallback);
		}

	}

	public static class SetWlanSettingResponse extends BaseResponse {

	
		public SetWlanSettingResponse(IHttpFinishListener callback) {
			super(callback);
		}
		
		@Override
		protected void parseContent(String strJsonResult) {
		
		}

		@Override
		public <T> T getModelResult() {
			// TODO Auto-generated method stub
			return null;
		}	
	}	
	
	
	
	/*Set WPS Pin*/
	public static class SetWPSPin extends BaseRequest{

		public SetWPSPin(String strId, IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetWPSPin");

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetWPSPinResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetWPSPinResponse extends BaseResponse{

		private string WpsPinString =null;
		public SetWPSPinResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			WpsPinString = gson.fromJson(strJsonResult, string.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public string getModelResult() {
			// TODO Auto-generated method stub
			return WpsPinString;
		}
		
	}
	
	/*Setb WPS Pbc*/
	public static class SetWPSPbc extends BaseRequest{

		public SetWPSPbc(String strId, IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetWPSPbc");

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetWPSPbcResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetWPSPbcResponse extends BaseResponse
    {   
        public SetWPSPbcResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
}
