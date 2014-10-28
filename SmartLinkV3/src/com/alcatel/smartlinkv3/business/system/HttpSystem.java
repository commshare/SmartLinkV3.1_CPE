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
        	//tset
        	//m_features.setModel("MM100");
        	//test
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
		private SystemInfo m_systemInfo;
        
        public GetSystemStatusResponse(IHttpFinishListener callback) 
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
	
	/******************** GetExternalStorageDevice  **************************************************************************************/	
	public static class GetExternalStorageDevice extends BaseRequest
    {	
        public GetExternalStorageDevice(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "System.GetExternalStorageDevice");

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
            return new GetExternalStorageDeviceResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetExternalStorageDeviceResponse extends BaseResponse
    {
		private StorageList m_result;
        
        public GetExternalStorageDeviceResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_result = gson.fromJson(strJsonResult, StorageList.class);
        }

        @SuppressWarnings("unchecked")
		@Override
        public StorageList getModelResult() 
        {
             return m_result;
        }
    }
	
	/*device reboot*/
	public static class deviceRebootRequest extends BaseRequest{

		public deviceRebootRequest(String strId,IHttpFinishListener callback) {
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
			return new deviceRebootResponse(m_finsishCallback);
		}
		
	}
	
	public static class deviceRebootResponse extends BaseResponse{

		private Boolean m_blResult=false;
		public deviceRebootResponse(IHttpFinishListener callback) {
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
	public static class deviceResetRequest extends BaseRequest{

		public deviceResetRequest(String strId,IHttpFinishListener callback) {
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
			return new deviceResetResponse(m_finsishCallback);
		}
		
	}
	
	public static class deviceResetResponse extends BaseResponse{

		private Boolean m_blRes= false;
		public deviceResetResponse(IHttpFinishListener callback) {
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
	public static class deviceBackupRequest extends BaseRequest{

		public deviceBackupRequest(String strId,IHttpFinishListener callback) {
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
			return new deviceBackupResponse(m_finsishCallback);
		}
		
	}
	
	public static class deviceBackupResponse extends BaseResponse{

		private Boolean m_blRes= false;
		public deviceBackupResponse(IHttpFinishListener callback) {
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
	public static class deviceRestoreRequest extends BaseRequest{

		private String m_strFileName="";
		public deviceRestoreRequest(String strId, String strFileName,IHttpFinishListener callback) {
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
			return new deviceRestoreResponse(m_finsishCallback);
		}
		
	}
	
	public static class deviceRestoreResponse extends BaseResponse{

		private Boolean m_blRes= false;
		public deviceRestoreResponse(IHttpFinishListener callback) {
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
}
