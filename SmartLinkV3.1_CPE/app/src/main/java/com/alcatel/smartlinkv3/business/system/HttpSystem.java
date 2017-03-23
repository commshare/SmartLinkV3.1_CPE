package com.alcatel.smartlinkv3.business.system;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSystem {

    /********************  Get feature list  *************************************/
    public static class GetFeature extends BaseRequest {
        public GetFeature(IHttpFinishListener callback) {
            super("GetFeatureList", "16.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
//            return new GetFeatureResponse(m_finsishCallback);
            return new DataResponse<>(Features.class, m_finsishCallback);
        }
    }



    /******************** GetSystemInfo  **************************************************************************************/
    public static class GetSystemInfo extends BaseRequest {
        public GetSystemInfo(IHttpFinishListener callback) {
            super("GetSystemInfo", "13.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
//            return new GetSystemInfoResponse(m_finsishCallback);
            return new DataResponse<>(SystemInfo.class, m_finsishCallback);
        }

    }

    /******************** GetSystemStatus  **************************************************************************************/
    public static class GetSystemStatus extends BaseRequest {
        public GetSystemStatus(IHttpFinishListener callback) {
            super("GetSystemStatus", "13.4", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
//            return new GetSystemStatusResponse(m_finsishCallback);
            return new DataResponse<>(SystemStatus.class, m_finsishCallback);
        }

    }


    /*device reboot*/
    public static class SetDeviceReboot extends BaseRequest {

        public SetDeviceReboot(IHttpFinishListener callback) {
            super("SetDeviceReboot", "13.5", callback);
        }
    }


    /*Device reset*/
    public static class SetDeviceReset extends BaseRequest {

        public SetDeviceReset(IHttpFinishListener callback) {
            super("SetDeviceReset", "13.6", callback);
        }

    }


    /*Device backup*/
    public static class SetDeviceBackup extends BaseRequest {

        public SetDeviceBackup(IHttpFinishListener callback) {
            super("SetDeviceBackup", "13.7", callback);
        }
    }


    /*Device backup*/
    public static class SetDeviceRestore extends BaseRequest {

        private String m_strFileName = "";

        public SetDeviceRestore(String strFileName, IHttpFinishListener callback) {
            super("SetDeviceRestore", "13.8", callback);
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
            super("SetDevicePowerOff", "13.9", callback);
        }
    }

    public static class setAppBackupRequest extends BaseRequest {
        public setAppBackupRequest(IHttpFinishListener callback) {
            super("SetAppBackup", "13.10", callback);
        }
    }


    public static class setAppRestoreBackupRequest extends BaseRequest {
        public setAppRestoreBackupRequest(IHttpFinishListener callback) {
            super("SetAppRestoreBackup", "13.11", callback);
        }

        @Override
        public BaseResponse createResponseObject() {
//            return new setAppRestoreBackupResponse(m_finsishCallback);
            return new DataResponse<>(RestoreError.class, m_finsishCallback);
        }
    }
}
