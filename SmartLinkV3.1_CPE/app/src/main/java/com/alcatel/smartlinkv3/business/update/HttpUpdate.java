package com.alcatel.smartlinkv3.business.update;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpUpdate {

	/*Get device new version*/
	public static class getDeviceNewVersionRequest extends BaseRequest{

		public getDeviceNewVersionRequest(String strId, IHttpFinishListener callback) {
			super("GetDeviceNewVersion", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new getDeviceNewVersionResponse(m_finsishCallback);
		}
		
	}
	
	public static class getDeviceNewVersionResponse extends BaseResponse{

		private DeviceNewVersionInfo m_info = null;
		public getDeviceNewVersionResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_info = gson.fromJson(strJsonResult, DeviceNewVersionInfo.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public DeviceNewVersionInfo getModelResult() {
			// TODO Auto-generated method stub
			return m_info;
		}
		
	}
	
	/*start to update device*/
	public static class setDeviceStartUpdateRequest extends BaseRequest{

		public setDeviceStartUpdateRequest(String strId, IHttpFinishListener callback) {
			super("SetDeviceStartUpdate", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new setDeviceStartUpdateResponse(m_finsishCallback);
		}
		
	}
	
	public static class setDeviceStartUpdateResponse extends BaseResponse{

		private Boolean m_blRes = false;
		public setDeviceStartUpdateResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			if (strJsonResult.length() != 0) {
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
	
	/*FOTA start to update device*/
	public static class setFOTAStartDownload extends BaseRequest{

		public setFOTAStartDownload(String strId, IHttpFinishListener callback) {
			super("SetFOTAStartDownload", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new setFOTAStartDownloadResponse(m_finsishCallback);
		}
		
	}
	
	public static class setFOTAStartDownloadResponse extends BaseResponse{

		private Boolean m_blRes = false;
		public setFOTAStartDownloadResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			if (strJsonResult.length() != 0) {
				m_blRes = true;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Boolean getModelResult() {
			return m_blRes;
		}
	}
	
	
	
	/*upgrade status*/
	public static class getDeviceUpgradeStatusRequest extends BaseRequest{

		public getDeviceUpgradeStatusRequest(String strId, IHttpFinishListener callback) {
			super("GetDeviceUpgradeState", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new getDeviceUpgradeStatusResponse(m_finsishCallback);
		}
		
	}
	
	public static class getDeviceUpgradeStatusResponse extends BaseResponse{

		private DeviceUpgradeStateInfo m_info=null;
		public getDeviceUpgradeStatusResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_info = gson.fromJson(strJsonResult, DeviceUpgradeStateInfo.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public DeviceUpgradeStateInfo getModelResult() {
			// TODO Auto-generated method stub
			return m_info;
		}
		
	}
	/*stop updating device*/
	public static class setDeviceUpdateStopRequest extends BaseRequest{

		public setDeviceUpdateStopRequest(String strId, IHttpFinishListener callback) {
			super("SetDeviceUpdateStop", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new setDeviceUpdateStopResponse(m_finsishCallback);
		}
		
	}
	
	public static class setDeviceUpdateStopResponse extends BaseResponse{

		private Boolean m_blRes = false;
		public setDeviceUpdateStopResponse(IHttpFinishListener callback) {
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
	
	public static class SetCheckNewVersionRequest extends BaseRequest{

		public SetCheckNewVersionRequest(String strID, IHttpFinishListener callback) {
			super("SetCheckNewVersion", strID, callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetCheckNewVersionResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetCheckNewVersionResponse extends BaseResponse{

		public SetCheckNewVersionResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> T getModelResult() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
	public static class setFOTAStartUpdate extends BaseRequest
	{
		public setFOTAStartUpdate(String strId, IHttpFinishListener callback) 
		{
			super("setFOTAStartUpdate", strId, callback);
		}

		@Override
		public BaseResponse createResponseObject() 
		{
			return new setFOTAStartDownloadResponse(m_finsishCallback);
		}
		
	}
	
}
