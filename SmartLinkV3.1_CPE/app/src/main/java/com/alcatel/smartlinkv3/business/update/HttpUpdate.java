package com.alcatel.smartlinkv3.business.update;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.BooleanResponse;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpUpdate {

	/*Get device new version*/
	public static class getDeviceNewVersionRequest extends BaseRequest{

		public getDeviceNewVersionRequest(IHttpFinishListener callback) {
			super("GetDeviceNewVersion", "9.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new getDeviceNewVersionResponse(m_finsishCallback);
			return new DataResponse<>(DeviceNewVersionInfo.class, m_finsishCallback);
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
//			return new DeviceUpgradeStatusResponse(m_finsishCallback);
			return new DataResponse<>(DeviceUpgradeStateInfo.class, m_finsishCallback);
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
