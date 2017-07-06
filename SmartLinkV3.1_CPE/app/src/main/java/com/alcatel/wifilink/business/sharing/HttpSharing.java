package com.alcatel.wifilink.business.sharing;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSharing {

	/********************set ftp setting******************************************************************************************/
	public static class SetFtpSetting extends BaseRequest {
		private int m_nStatus = 0;

		public SetFtpSetting(int nStatus, IHttpFinishListener callback) {
			super("Sharing","SetFtpStatus", "14.6", callback);
			setBroadcastAction(MessageUti.SHARING_SET_FTP_SETTING_REQUSET);
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
			super("Sharing","GetFtpStatus", "14.5", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetFtpSettingResponse(m_finsishCallback);
			return new DataResponse<>(FtpSettings.class, MessageUti.SHARING_GET_FTP_SETTING_REQUSET, m_finsishCallback);
		}
	}

	/******************** set USB card setting **************************************************************************************/
	
	public static class SetUSBcardSetting extends BaseRequest {
		private int m_nStatus = 0;

		public SetUSBcardSetting(int nStatus, IHttpFinishListener callback) {
			super(null, /*"Sharing",*/ "SetUsbcardStatus", "14.11",  callback);
			setBroadcastAction(MessageUti.SHARING_SET_USBCARD_SETTING_REQUSET);
			m_nStatus = nStatus;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("UsbcardStatus", m_nStatus);
				m_requestParamJson.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	/******************** set samba setting **************************************************************************************/
	public static class SetSambaSetting extends BaseRequest {
		private int m_nStatus = 0;

		public SetSambaSetting(int nStatus, IHttpFinishListener callback) {
			super("Sharing", "SetSambaStatus", "14.4", callback);
			setBroadcastAction(MessageUti.SHARING_SET_SAMBA_SETTING_REQUSET);
			m_nStatus = nStatus;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("SambaStatus", m_nStatus);
				m_requestParamJson.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	
	/******************** get samba setting **************************************************************************************/
	public static class GetSambaSetting extends BaseRequest {	

		public GetSambaSetting(IHttpFinishListener callback) {
			super("Sharing","GetSambaStatus", "14.3", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetSambaSettingResponse(m_finsishCallback);
			return new DataResponse<>(SambaSettings.class, MessageUti.SHARING_GET_SAMBA_SETTING_REQUSET, m_finsishCallback);
		}
	}

	/******************** set DLNA setting **************************************************************************************/
	public static class SetDlnaSetting extends BaseRequest {

		private int m_status = 0;
		private String m_name;

		public SetDlnaSetting(int status, String name, IHttpFinishListener callback) {
			super("Sharing", "SetDLNASettings", "14.2",  callback);
			setBroadcastAction(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET);
			m_status = status;
			m_name = name;	
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("DlnaStatus", m_status);
				settings.put("DlnaName", m_name);
				m_requestParamJson.put(ConstValue.JSON_PARAMS, settings);
		}
	}

	
	/******************** get DLNA setting **************************************************************************************/
	public static class GetDlnaSetting extends BaseRequest {	

		public GetDlnaSetting(IHttpFinishListener callback) {
			super("Sharing", "GetDLNASettings", "14.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetDlnaSettingResponse(m_finsishCallback);
			return new DataResponse<>(DlnaSettings.class, MessageUti.SHARING_GET_DLNA_SETTING_REQUSET, m_finsishCallback);
		}
	}

	/******************** GetSDCardSpace **************************************************************************************/
	public static class GetSDCardSpace extends BaseRequest {	

		public GetSDCardSpace(IHttpFinishListener callback) {
			super("Sharing", "GetSDCardSpace", "14.7", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetSDCardSpaceResponse(m_finsishCallback);
			return new DataResponse<>(SDCardSpace.class, MessageUti.SHARING_GET_SDCARD_SPACE_REQUSET, m_finsishCallback);
		}
	}

	/******************** GetSDCardStatus **************************************************************************************/
	public static class GetSDcardStatus extends BaseRequest {	

		public GetSDcardStatus(IHttpFinishListener callback) {
			super(null , /*"Sharing",*/ "GetSDcardStatus", "14.9", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new GetSDcardStatusResponse(m_finsishCallback);
			return new DataResponse<>(SDcardStatus.class, MessageUti.SHARING_GET_SDCARD_STATUS_REQUSET, m_finsishCallback);
		}
	}
}
