package com.alcatel.smartlinkv3.business.system;

import org.json.JSONException;

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
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "System.GetFeatureList");

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
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "System.GetSystemInfo");

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
}
