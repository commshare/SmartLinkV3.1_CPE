package com.alcatel.smartlinkv3.business.system;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpSystem {
	
/********************  Get feature list  **************************************************************************************/	
	public static class GetFeature extends BaseRequest
    {	
        public GetFeature(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetFeatureList");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
        public GetSystemInfo(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSystemInfo");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
        public GetSystemStatus(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetSystemStatus");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		public SetDeviceReboot(String strId,IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetDeviceReboot");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetDeviceRebootResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetDeviceRebootResponse extends BaseResponse{

		private Boolean m_blResult=false;
		public SetDeviceRebootResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			m_blResult = true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Boolean getModelResult() {
			// TODO Auto-generated method stub
			return m_blResult;
		}
		
	}
	/*Device reset*/
	public static class SetDeviceReset extends BaseRequest{

		public SetDeviceReset(String strId,IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetDeviceReset");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetDeviceResetResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetDeviceResetResponse extends BaseResponse{

		private Boolean m_blRes= false;
		public SetDeviceResetResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			m_blRes = true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Boolean getModelResult() {
			// TODO Auto-generated method stub
			return m_blRes;
		}
		
	}
	
	/*Device backup*/
	public static class SetDeviceBackup extends BaseRequest{

		public SetDeviceBackup(String strId,IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetDeviceBackup");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetDeviceBackupResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetDeviceBackupResponse extends BaseResponse{

		private Boolean m_blRes= false;
		public SetDeviceBackupResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			m_blRes = true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Boolean getModelResult() {
			// TODO Auto-generated method stub
			return m_blRes;
		}
		
	}
	
	/*Device backup*/
	public static class SetDeviceRestore extends BaseRequest{

		private String m_strFileName="";
		public SetDeviceRestore(String strId, String strFileName,IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strId;
			m_strFileName = strFileName;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetDeviceRestore");

	        	JSONObject jFile = new JSONObject();
	        	jFile.put("filename", m_strFileName);
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, jFile);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new SetDeviceRestoreResponse(m_finsishCallback);
		}
		
	}
	
	public static class SetDeviceRestoreResponse extends BaseResponse{

		private Boolean m_blRes= false;
		public SetDeviceRestoreResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			m_blRes = true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Boolean getModelResult() {
			// TODO Auto-generated method stub
			return m_blRes;
		}
		
	}
	
	/*power off*/
	public static class setDevicePowerOffRequest extends BaseRequest{

		public setDevicePowerOffRequest(String strID, IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strID;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetDevicePowerOff");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new setDevicePowerOffResponse(m_finsishCallback);
		}
		
	}
	
	public static class setDevicePowerOffResponse extends BaseResponse{

		public setDevicePowerOffResponse(IHttpFinishListener callback) {
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
	
	public static class setAppBackupRequest extends BaseRequest{

		public setAppBackupRequest(String strID, IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strID;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetAppBackup");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new setAppBackupResponse(m_finsishCallback);
		}
		
	}
	
	public static class setAppBackupResponse extends BaseResponse{

		public setAppBackupResponse(IHttpFinishListener callback) {
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
	
	public static class setAppRestoreBackupRequest extends BaseRequest{

		public setAppRestoreBackupRequest(String strID, IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
			m_strId = strID;
		}

		@Override
		protected void buildHttpParamJson() {
			// TODO Auto-generated method stub
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "SetAppRestoreBackup");

	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			// TODO Auto-generated method stub
			return new setAppRestoreBackupResponse(m_finsishCallback);
		}
		
	}
	
	public static class setAppRestoreBackupResponse extends BaseResponse{

		private RestoreError m_errorInfo = null;
		public setAppRestoreBackupResponse(IHttpFinishListener callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void parseContent(String strJsonResult) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			m_errorInfo = gson.fromJson(strJsonResult, RestoreError.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public RestoreError getModelResult() {
			// TODO Auto-generated method stub
			return m_errorInfo;
		}
		
	}
}
