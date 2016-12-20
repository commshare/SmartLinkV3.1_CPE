package com.alcatel.smartlinkv3.business.wan;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.business.statistics.UsageSettingsResult;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageSettings.GetUsageSettingsResponse;
import com.alcatel.smartlinkv3.business.statistics.HttpUsageSettings.SetUsageSettingsResponse;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpConnectOperation {
	
/******************** GetConnectionState  **************************************************************************************/	
	public static class GetConnectionState extends BaseRequest
    {			
        public GetConnectionState(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetConnectionState");
	        	
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
            return new GetConnectionStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetConnectionStateResponse extends BaseResponse
    {
		private ConnectStatusResult m_ConnectStatus;
        
        public GetConnectionStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_ConnectStatus = gson.fromJson(strJsonResult, ConnectStatusResult.class);
        }

        @Override
        public ConnectStatusResult getModelResult() 
        {
             return m_ConnectStatus;
        }
    }
	
	/******************** Connect  **************************************************************************************/	
	public static class Connect extends BaseRequest
    {	
        public Connect(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Connect");
	        	
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
            return new ConnectResponse(m_finsishCallback);
        }
        
    }
	
	public static class ConnectResponse extends BaseResponse
    {   
        public ConnectResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	
	/******************** DisConnect  **************************************************************************************/	
	public static class DisConnect extends BaseRequest
    {	
        public DisConnect(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "DisConnect");
	        	
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
            return new DisConnectResponse(m_finsishCallback);
        }
        
    }
	
	public static class DisConnectResponse extends BaseResponse
    {   
        public DisConnectResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	
	
	/******************** GetConnectionSettings  **************************************************************************************/	
	public static class GetConnectionSettings extends BaseRequest
    {			
        public GetConnectionSettings(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetConnectionSettings");

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
            return new GetConnectionSettingsResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetConnectionSettingsResponse extends BaseResponse
    {
		private ConnectionSettingsResult m_ConnectionSettings;
        
        public GetConnectionSettingsResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_ConnectionSettings = gson.fromJson(strJsonResult, ConnectionSettingsResult.class);
        }

        @Override
        public ConnectionSettingsResult getModelResult() 
        {
             return m_ConnectionSettings;
        }
    }
	
	/******************** SetConnectionSettings **************************************************************************************/
	public static class SetConnectionSettings extends BaseRequest {

		public ConnectionSettingsResult m_result = new ConnectionSettingsResult();

		public SetConnectionSettings(String strId,	ConnectionSettingsResult result, IHttpFinishListener callback) {
			super(callback);
			m_strId = strId;
			m_result.setValue(result);
		}

		@Override
		protected void buildHttpParamJson() {
			try {
				m_requestParamJson.put(ConstValue.JSON_RPC,
						ConstValue.JSON_RPC_VERSION);
				m_requestParamJson.put(ConstValue.JSON_METHOD,
						"SetConnectionSettings");
				
				JSONObject settings = new JSONObject();
				settings.put("IdleTime", m_result.IdleTime);
				settings.put("RoamingConnect", m_result.RoamingConnect);
				settings.put("ConnectMode", m_result.ConnectMode);
				settings.put("PdpType", m_result.PdpType);
				
				
				m_requestParamJson
						.put(ConstValue.JSON_PARAMS, settings);
				m_requestParamJson.put(ConstValue.JSON_ID, m_strId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public BaseResponse createResponseObject() {
			return new SetUsageSettingsResponse(m_finsishCallback);
		}

	}
	

	public static class SetConnectionSettingsResponse extends BaseResponse {

		
		public SetConnectionSettingsResponse(IHttpFinishListener callback) {
			super(callback);
		}
		
		@Override
		protected void parseContent(String strJsonResult) {
		
		}

		@Override
		public <T> T getModelResult() {
			// TODO Auto-generated method stub
			return null;
		}	
	}

}
