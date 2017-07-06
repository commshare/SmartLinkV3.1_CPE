package com.alcatel.wifilink.business.system;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSystem {

    /********************  Get feature list  *************************************/
    public static class GetFeature extends BaseRequest {
        public GetFeature(IHttpFinishListener callback) {
            super(ANY_MODULE, "GetFeatureList", "16.1", callback);
        }

        public GetFeature() {
            this(null);
        }

        @Override
        public BaseResponse createResponseObject() {
            return new DataResponse<>(Features.class, MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET, m_finsishCallback);
        }
    }


    /******************** GetSystemInfo  **************************************************************************************/
    public static class GetSystemInfo extends BaseRequest {
        public GetSystemInfo(IHttpFinishListener callback) {
            super("System", "GetSystemInfo", "13.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
//            return new GetSystemInfoResponse(m_finsishCallback);
            return new DataResponse<>(SystemInfo.class, MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUEST, m_finsishCallback);
        }

    }

    /******************** GetSystemStatus  **************************************************************************************/
    public static class GetSystemStatus extends BaseRequest {
        public GetSystemStatus(IHttpFinishListener callback) {
            super("System", "GetSystemStatus", "13.4", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
//            return new GetSystemStatusResponse(m_finsishCallback);
            return new DataResponse<>(SystemStatus.class, MessageUti.SYSTEM_GET_SYSTEM_STATUS_REQUSET, m_finsishCallback);
        }

    }


    /*device reboot*/
    public static class SetDeviceReboot extends BaseRequest {

        public SetDeviceReboot(IHttpFinishListener callback) {
            super("System", "SetDeviceReboot", "13.5", callback);
            setBroadcastAction(MessageUti.SYSTEM_SET_DEVICE_REBOOT);
        }
    }


    /*Device reset*/
    public static class SetDeviceReset extends BaseRequest {

        public SetDeviceReset(IHttpFinishListener callback) {
            super("System", "SetDeviceReset", "13.6", callback);
            setBroadcastAction(MessageUti.SYSTEM_SET_DEVICE_RESET);
        }

    }


    /*Device backup*/
    public static class SetDeviceBackup extends BaseRequest {

        public SetDeviceBackup(IHttpFinishListener callback) {
            super("System", "SetDeviceBackup", "13.7", callback);
            setBroadcastAction(MessageUti.SYSTEM_SET_DEVICE_BACKUP);
        }
    }


    /*Device backup*/
    public static class SetDeviceRestore extends BaseRequest {

        private String m_strFileName = "";

        public SetDeviceRestore(String strFileName, IHttpFinishListener callback) {
            super("System", "SetDeviceRestore", "13.8", callback);
            setBroadcastAction(MessageUti.SYSTEM_SET_DEVICE_RESTORE);
            m_strFileName = strFileName;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException {
            JSONObject jFile = new JSONObject();
            jFile.put("filename", m_strFileName);
            m_requestParamJson.put(ConstValue.JSON_PARAMS, jFile);
        }
    }


    /*power off*/
    public static class setDevicePowerOffRequest extends BaseRequest {
        public setDevicePowerOffRequest(IHttpFinishListener callback) {
            super("System", "SetDevicePowerOff", "13.9", callback);
            setBroadcastAction(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF);
        }
    }

    public static class setAppBackupRequest extends BaseRequest {
        public setAppBackupRequest(IHttpFinishListener callback) {
            super(null, "SetAppBackup", "13.10", callback);
            setBroadcastAction(MessageUti.SYSTEM_SET_APP_BACKUP);
        }
    }


    public static class setAppRestoreBackupRequest extends BaseRequest {
        public setAppRestoreBackupRequest(IHttpFinishListener callback) {
            super(null, "SetAppRestoreBackup", "13.11", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
//            return new setAppRestoreBackupResponse(m_finsishCallback);
            return new DataResponse<>(RestoreError.class, MessageUti.SYSTEM_SET_APP_RESTORE_BACKUP, m_finsishCallback);
        }
    }
}
