package com.alcatel.wifilink.business.update;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.BooleanResponse;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

public class HttpUpdate {

	/*Get device new version*/
	public static class getDeviceNewVersionRequest extends BaseRequest{

		public getDeviceNewVersionRequest(IHttpFinishListener callback) {
			super("Update", "GetDeviceNewVersion", "9.1", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new getDeviceNewVersionResponse(m_finsishCallback);
			return new DataResponse<>(DeviceNewVersionInfo.class, MessageUti.UPDATE_GET_DEVICE_NEW_VERSION, m_finsishCallback);
		}
	}

	
	/*start to update device*/
	public static class setDeviceStartUpdateRequest extends BaseRequest{
		private boolean fota;
		public setDeviceStartUpdateRequest(boolean fota, IHttpFinishListener callback) {
			super("Update", "SetDeviceStartUpdate", "9.2", callback);
			this.fota = fota;
		}

		@Override
		public BaseResponse createResponseObject() {
			if (fota)
				return new BooleanResponse(MessageUti.UPDATE_SET_DEVICE_START_FOTA_UPDATE, m_finsishCallback);
			else
				return new BooleanResponse(MessageUti.UPDATE_SET_DEVICE_START_UPDATE, m_finsishCallback);
		}
	}
	

	
	/*FOTA start to update device*/
	public static class setFOTAStartDownload extends BaseRequest{

		public setFOTAStartDownload(IHttpFinishListener callback) {
			super("Update", "SetFOTAStartDownload", "9.2", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new BooleanResponse(MessageUti.UPDATE_SET_DEVICE_START_UPDATE, m_finsishCallback);
		}
	}

	
	/*upgrade status*/
	public static class getDeviceUpgradeStatusRequest extends BaseRequest{

		public getDeviceUpgradeStatusRequest(IHttpFinishListener callback) {
			super("Update", "GetDeviceUpgradeState", "9.3", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
//			return new DeviceUpgradeStatusResponse(m_finsishCallback);
			return new DataResponse<>(DeviceUpgradeStateInfo.class, MessageUti.UPDATE_GET_DEVICE_UPGRADE_STATE, m_finsishCallback);
		}
	}

	/*stop updating device*/
	public static class setDeviceUpdateStopRequest extends BaseRequest{

		public setDeviceUpdateStopRequest(IHttpFinishListener callback) {
			super("Update", "SetDeviceUpdateStop", "9.4", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new BooleanResponse(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE, m_finsishCallback);
		}
		
	}

	public static class SetCheckNewVersionRequest extends BaseRequest{

		public SetCheckNewVersionRequest(IHttpFinishListener callback) {
			super("Update","SetCheckNewVersion", "9.5", callback);
			setBroadcastAction(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION);
		}
	}
}
