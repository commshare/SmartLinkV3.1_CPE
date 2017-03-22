package com.alcatel.smartlinkv3.business.update;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.BooleanResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpUpdate {

	/*Get device new version*/
	public static class getDeviceNewVersionRequest extends BaseRequest{

		public getDeviceNewVersionRequest(IHttpFinishListener callback) {
			super("GetDeviceNewVersion", "9.1", callback);
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

		public setDeviceStartUpdateRequest(IHttpFinishListener callback) {
			super("SetDeviceStartUpdate", "9.2", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new BooleanResponse(m_finsishCallback);
		}
		
	}
	

	
	/*FOTA start to update device*/
	public static class setFOTAStartDownload extends BaseRequest{

		public setFOTAStartDownload(IHttpFinishListener callback) {
			super("SetFOTAStartDownload", "9.2", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new BooleanResponse(m_finsishCallback);
		}
	}

	
	/*upgrade status*/
	public static class getDeviceUpgradeStatusRequest extends BaseRequest{

		public getDeviceUpgradeStatusRequest(IHttpFinishListener callback) {
			super("GetDeviceUpgradeState", "9.3", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new DeviceUpgradeStatusResponse(m_finsishCallback);
		}
		
	}
	
	public static class DeviceUpgradeStatusResponse extends BaseResponse{

		private DeviceUpgradeStateInfo m_info=null;
		public DeviceUpgradeStatusResponse(IHttpFinishListener callback) {
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

		public setDeviceUpdateStopRequest(IHttpFinishListener callback) {
			super("SetDeviceUpdateStop", "9.4", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new BooleanResponse(m_finsishCallback);
		}
		
	}

	public static class SetCheckNewVersionRequest extends BaseRequest{

		public SetCheckNewVersionRequest(IHttpFinishListener callback) {
			super("SetCheckNewVersion", "9.5", callback);
		}
	}
}
