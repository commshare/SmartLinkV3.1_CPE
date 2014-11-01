package com.alcatel.smartlinkv3.business.sharing;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpSharing {


	/******************** set samba setting **************************************************************************************/
	public static class SetSambaSetting extends BaseRequest {
		private int m_nStatus = 0;

		public SetSambaSetting(String strId, int nStatus, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_nStatus = nStatus;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetSambaStatus");

				JSONObject settings = new JSONObject();
				settings.put("SambaStatus", m_nStatus);
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetSambaSettingResponse(m_finsishCallback);
		}

	}

	public static class SetSambaSettingResponse extends BaseResponse {

		public SetSambaSettingResponse(IHttpFinishListener callback) {
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
	
	
	/******************** get samba setting **************************************************************************************/
	public static class GetSambaSetting extends BaseRequest {	

		public GetSambaSetting(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetSambaStatus");			
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetSambaSettingResponse(m_finsishCallback);
		}

	}

	public static class GetSambaSettingResponse extends BaseResponse {

		private SambaSettings m_result = new SambaSettings();
		public GetSambaSettingResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, SambaSettings.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public SambaSettings getModelResult() {
			return m_result;
		}
	}
	

	/******************** set DLNA setting **************************************************************************************/
	public static class SetDlnaSetting extends BaseRequest {

		private int m_status = 0;
		private String m_name;

		public SetDlnaSetting(String strId, int status, String name, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_status = status;
			m_name = name;	
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetDLNASettings");

				JSONObject settings = new JSONObject();
				settings.put("DlnaStatus", m_status);
				settings.put("DlnaName", m_name);
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetDlnaSettingResponse(m_finsishCallback);
		}

	}

	public static class SetDlnaSettingResponse extends BaseResponse {

		public SetDlnaSettingResponse(IHttpFinishListener callback) {
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
	
	
	/******************** get DLNA setting **************************************************************************************/
	public static class GetDlnaSetting extends BaseRequest {	

		public GetDlnaSetting(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetDLNASettings");			
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetDlnaSettingResponse(m_finsishCallback);
		}

	}

	public static class GetDlnaSettingResponse extends BaseResponse {

		private DlnaSettings m_result = new DlnaSettings();
		public GetDlnaSettingResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, DlnaSettings.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public DlnaSettings getModelResult() {
			return m_result;
		}
	}	
	/******************** GetSDCardSpace **************************************************************************************/
	public static class GetSDCardSpace extends BaseRequest {	

		public GetSDCardSpace(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetSDCardSpace");			
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetSDCardSpaceResponse(m_finsishCallback);
		}

	}

	public static class GetSDCardSpaceResponse extends BaseResponse {

		private SDCardSpace m_result = new SDCardSpace();
		public GetSDCardSpaceResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, SDCardSpace.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public SDCardSpace getModelResult() {
			return m_result;
		}
	}
	
	/******************** GetSDCardStatus **************************************************************************************/
	public static class GetSDcardStatus extends BaseRequest {	

		public GetSDcardStatus(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"GetSDcardStatus");			
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetSDcardStatusResponse(m_finsishCallback);
		}

	}

	public static class GetSDcardStatusResponse extends BaseResponse {

		private SDcardStatus m_result = new SDcardStatus();
		public GetSDcardStatusResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, SDcardStatus.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public SDcardStatus getModelResult() {
			return m_result;
		}
	}
	
	
}
