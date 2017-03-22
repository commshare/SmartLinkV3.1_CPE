package com.alcatel.smartlinkv3.business.sharing;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSharing {

	/********************set ftp setting******************************************************************************************/
	public static class SetFtpSetting extends BaseRequest {
		private int m_nStatus = 0;

		public SetFtpSetting(int nStatus, IHttpFinishListener callback) {
			super("SetFtpStatus", "14.6", callback);
			m_nStatus = nStatus;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("FtpStatus", m_nStatus);
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	/*******************get ftp setting******************************************************************************************/
	public static class GetFtpSetting extends BaseRequest {	

		public GetFtpSetting(IHttpFinishListener callback) {
			super("GetFtpStatus", "14.5", callback);
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
	/******************** set USB card setting **************************************************************************************/
	
	public static class SetUSBcardSetting extends BaseRequest {
		private int m_nStatus = 0;

		public SetUSBcardSetting(int nStatus, IHttpFinishListener callback) {
			super("SetUsbcardStatus", "14.11",  callback);
			m_nStatus = nStatus;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("UsbcardStatus", m_nStatus);
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	/******************** set samba setting **************************************************************************************/
	public static class SetSambaSetting extends BaseRequest {
		private int m_nStatus = 0;

		public SetSambaSetting(int nStatus, IHttpFinishListener callback) {
			super("SetSambaStatus", "14.4", callback);
			m_nStatus = nStatus;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("SambaStatus", m_nStatus);
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	
	/******************** get samba setting **************************************************************************************/
	public static class GetSambaSetting extends BaseRequest {	

		public GetSambaSetting(IHttpFinishListener callback) {
			super("GetSambaStatus", "14.3", callback);
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

		public SetDlnaSetting(int status, String name, IHttpFinishListener callback) {
			super("SetDLNASettings", "14.2",  callback);
			m_status = status;
			m_name = name;	
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("DlnaStatus", m_status);
				settings.put("DlnaName", m_name);
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	
	/******************** get DLNA setting **************************************************************************************/
	public static class GetDlnaSetting extends BaseRequest {	

		public GetDlnaSetting(IHttpFinishListener callback) {
			super("GetDLNASettings", "14.1", callback);
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

		public GetSDCardSpace(IHttpFinishListener callback) {
			super("GetSDCardSpace", "14.7", callback);
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

		public GetSDcardStatus(IHttpFinishListener callback) {
			super("GetSDcardStatus", "14.9", callback);
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
