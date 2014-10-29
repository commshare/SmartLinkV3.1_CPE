package com.alcatel.smartlinkv3.business.sharing;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpService {

	/******************** set service state **************************************************************************************/
	public static class SetServiceState extends BaseRequest {

		public int m_nServiceType = 0;
		public int m_nState = 0;

		public SetServiceState(String strId, int serviceType, int state,
				IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_nServiceType = serviceType;
			m_nState = state;

		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"Services.SetServiceState");

				JSONObject serviceStateInfo = new JSONObject();
				serviceStateInfo.put("ServiceType", m_nServiceType);
				serviceStateInfo.put("State", m_nState);

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, serviceStateInfo);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetServiceStateResponse(m_finsishCallback);
		}

	}

	public static class SetServiceStateResponse extends BaseResponse {

		public SetServiceStateResponse(IHttpFinishListener callback) {
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

	/******************** Get service state **************************************************************************************/
	public static class GetServiceState extends BaseRequest {
		public int m_nServiceType = 0;

		public GetServiceState(String strId, int serviceType,
				IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_nServiceType = serviceType;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"Services.GetServiceState");

				JSONObject serviceStateInfo = new JSONObject();
				serviceStateInfo.put("ServiceType", m_nServiceType);

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, serviceStateInfo);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetServiceStateResponse(m_finsishCallback);
		}

	}

	public static class GetServiceStateResponse extends BaseResponse {
		private ServiceStateResult m_result;

		public GetServiceStateResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, ServiceStateResult.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public ServiceStateResult getModelResult() {
			return m_result;
		}
	}

	/******************** set samba setting **************************************************************************************/
	public static class SetSambaSetting extends BaseRequest {

		private int m_nDevType = 0;
		private int m_nAnonymous = 0;
		private String m_strUserName = new String();
		private String m_strPassword = new String();
		private int m_nAuthType = 0;

		public SetSambaSetting(String strId, int nDevType, int nAnonymous, String strUserName, String strPassword, int nAuthType,
				IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_nDevType = nDevType;
			m_nAnonymous = nAnonymous;
			m_strUserName = strUserName;
			m_strPassword = strPassword;
			m_nAuthType = nAuthType;

		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"Services.Samba.SetSettings");

				JSONObject settings = new JSONObject();
				settings.put("DevType", m_nDevType);
				settings.put("Anonymous", m_nAnonymous);	
				settings.put("UserName", m_strUserName);
				settings.put("Password", m_strPassword);
				settings.put("AuthType", m_nAuthType);				

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
						"Services.Samba.GetSettings");			
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
	
	

	/******************** set ftp setting **************************************************************************************/
	public static class SetFtpSetting extends BaseRequest {	
		
		private int m_nDevType = 0;
		private int m_nAnonymous = 0;
		private String m_strUserName = new String();
		private String m_strPassword = new String();
		private int m_nAuthType = 0;

		public SetFtpSetting(String strId, int nDevType, int nAnonymous, String strUserName, String strPassword, int nAuthType,
				IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_nDevType = nDevType;
			m_nAnonymous = nAnonymous;
			m_strUserName = strUserName;
			m_strPassword = strPassword;
			m_nAuthType = nAuthType;

		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"Services.FTP.SetSettings");

				JSONObject settings = new JSONObject();
				settings.put("DevType", m_nDevType);
				settings.put("Anonymous", m_nAnonymous);	
				settings.put("UserName", m_strUserName);
				settings.put("Password", m_strPassword);
				settings.put("AuthType", m_nAuthType);				

				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetFtpSettingResponse(m_finsishCallback);
		}

	}

	public static class SetFtpSettingResponse extends BaseResponse {

		public SetFtpSettingResponse(IHttpFinishListener callback) {
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
	
	/******************** get FTP setting **************************************************************************************/
	public static class GetFtpSetting extends BaseRequest {	

		public GetFtpSetting(String strId, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"Services.FTP.GetSettings");			
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, null);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new GetFtpSettingResponse(m_finsishCallback);
		}

	}

	public static class GetFtpSettingResponse extends BaseResponse {

		private FtpSettings m_result = new FtpSettings();
		public GetFtpSettingResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_result = gson.fromJson(strJsonResult, FtpSettings.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public FtpSettings getModelResult() {
			return m_result;
		}
	}
	
	
}
