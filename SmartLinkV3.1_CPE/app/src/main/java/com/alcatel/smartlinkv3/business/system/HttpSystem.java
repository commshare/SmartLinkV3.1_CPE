package com.alcatel.smartlinkv3.business.system;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpSystem {
	
/********************  Get feature list  **************************************************************************************/	
	public static class GetFeature extends BaseRequest
    {	
        public GetFeature(IHttpFinishListener callback)
        {
        	super("GetFeatureList", "16.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetFeatureResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetFeatureResponse extends BaseResponse
    {
		private Features m_features;
        
        public GetFeatureResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_features = gson.fromJson(strJsonResult, Features.class);
        }

        @SuppressWarnings("unchecked")
		@Override
        public Features getModelResult() 
        {
             return m_features;
        }
    }
	
/******************** GetSystemInfo  **************************************************************************************/
	public static class GetSystemInfo extends BaseRequest
    {	
        public GetSystemInfo(IHttpFinishListener callback)
        {
        	super("GetSystemInfo", "13.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSystemInfoResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSystemInfoResponse extends BaseResponse
    {
		private SystemInfo m_systemInfo;
        
        public GetSystemInfoResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_systemInfo = gson.fromJson(strJsonResult, SystemInfo.class);
        }

        @SuppressWarnings("unchecked")
		@Override
        public SystemInfo getModelResult() 
        {
             return m_systemInfo;
        }
    }
	
	/******************** GetSystemStatus  **************************************************************************************/
	public static class GetSystemStatus extends BaseRequest
    {	
        public GetSystemStatus(IHttpFinishListener callback)
        {
        	super("GetSystemStatus", "13.4", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetSystemStatusResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetSystemStatusResponse extends BaseResponse
    {
		private SystemStatus m_systemStatus;
        
        public GetSystemStatusResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_systemStatus = gson.fromJson(strJsonResult, SystemStatus.class);
        }

        @SuppressWarnings("unchecked")
		@Override
        public SystemStatus getModelResult() 
        {
             return m_systemStatus;
        }
    }
	
	/*device reboot*/
	public static class SetDeviceReboot extends BaseRequest{

		public SetDeviceReboot(IHttpFinishListener callback) {
			super("SetDeviceReboot", "13.5", callback);
		}
	}
	

	/*Device reset*/
	public static class SetDeviceReset extends BaseRequest{

		public SetDeviceReset(IHttpFinishListener callback) {
			super("SetDeviceReset", "13.6", callback);
		}

	}

	
	/*Device backup*/
	public static class SetDeviceBackup extends BaseRequest{

		public SetDeviceBackup(IHttpFinishListener callback) {
			super("SetDeviceBackup", "13.7", callback);
		}
	}

	
	/*Device backup*/
	public static class SetDeviceRestore extends BaseRequest{

		private String m_strFileName="";
		public SetDeviceRestore(String strFileName,IHttpFinishListener callback) {
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
	public static class setDevicePowerOffRequest extends BaseRequest{

		public setDevicePowerOffRequest(IHttpFinishListener callback) {
			super("SetDevicePowerOff", "13.9", callback);
		}

	}

	
	public static class setAppBackupRequest extends BaseRequest{

		public setAppBackupRequest(IHttpFinishListener callback) {
			super("SetAppBackup", "13.10", callback);
		}

	}

	
	public static class setAppRestoreBackupRequest extends BaseRequest{

		public setAppRestoreBackupRequest(IHttpFinishListener callback) {
			super("SetAppRestoreBackup", "13.11", callback);
		}

		@Override
		public BaseResponse createResponseObject() {
			return new setAppRestoreBackupResponse(m_finsishCallback);
		}
		
	}
	
	public static class setAppRestoreBackupResponse extends BaseResponse{

		private RestoreError m_errorInfo = null;
		public setAppRestoreBackupResponse(IHttpFinishListener callback) {
			super(callback);
		}

		@Override
		protected void parseContent(String strJsonResult) {
			Gson gson = new Gson();
			m_errorInfo = gson.fromJson(strJsonResult, RestoreError.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public RestoreError getModelResult() {
			return m_errorInfo;
		}
		
	}
}
