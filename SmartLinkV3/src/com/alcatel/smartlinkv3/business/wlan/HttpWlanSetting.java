package com.alcatel.smartlinkv3.business.wlan;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;

import com.alcatel.smartlinkv3.business.lan.LanInfo;
import com.alcatel.smartlinkv3.business.lan.HttpLan.getLanSettingsResponse;
import com.alcatel.smartlinkv3.common.ENUM.WlanSupportMode;
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
						"SetWlanSettings");
				
				JSONObject settings = new JSONObject();
				settings.put("WlanAPMode", m_result.WlanAPMode);
				settings.put("CountryCode", m_result.CountryCode);
				settings.put("Ssid", m_result.Ssid);
				settings.put("SsidHidden", m_result.SsidHidden);
				settings.put("SecurityMode", m_result.SecurityMode);
				settings.put("WpaType", m_result.WpaType);				
				settings.put("WpaKey", m_result.WpaKey);
				settings.put("WepType", m_result.WepType);
				settings.put("WepKey", m_result.WepKey);
				settings.put("Channel", m_result.Channel);
				settings.put("ApIsolation", m_result.ApIsolation);
				settings.put("WMode", m_result.WMode);
				settings.put("max_numsta", m_result.max_numsta);
				
				settings.put("Ssid_5G", m_result.Ssid_5G);
				settings.put("SsidHidden_5G", m_result.SsidHidden_5G);
				settings.put("SecurityMode_5G", m_result.SecurityMode_5G);
				settings.put("WpaType_5G", m_result.WpaType_5G);				
				settings.put("WpaKey_5G", m_result.WpaKey_5G);
				settings.put("WepType_5G", m_result.WepType_5G);
				settings.put("WepKey_5G", m_result.WepKey_5G);
				settings.put("Channel_5G", m_result.Channel_5G);
				settings.put("ApIsolation_5G", m_result.ApIsolation_5G);
				settings.put("WMode_5G", m_result.WMode_5G);
				settings.put("max_numsta_5G", m_result.max_numsta_5G);
				
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
		public String m_strPin = new String();

		public SetWPSPin(String strId, String strPin,IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
			m_strPin = strPin;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetWPSPin");

				JSONObject settings = new JSONObject();
				settings.put("WpsPin", m_strPin);
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
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
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
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
	
	/*get Wlan support mode start*/
	public static class getWlanSupportModeRequest extends BaseRequest{

		public getWlanSupportModeRequest(String strID, IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strID;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetWlanSupportMode");

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
			return new getWlanSupportModeResponse(m_finsishCallback);
		}
		
	}
	
	public static class getWlanSupportModeResponse extends BaseResponse{

		private WlanSupportMode mode=WlanSupportMode.Mode2Point4G;
		public getWlanSupportModeResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson= new Gson();
			int nMode = gson.fromJson(strJsonResult, Integer.class);
			mode = WlanSupportMode.build(nMode);
		}

		@SuppressWarnings("unchecked")
		@Override
		public WlanSupportMode getModelResult() {
			// TODO Auto-generated method stub
			return mode;
		}
		
	}
	/*get Wlan support mode end***/
}
