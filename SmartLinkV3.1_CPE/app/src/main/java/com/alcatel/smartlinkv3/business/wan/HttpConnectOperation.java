package com.alcatel.smartlinkv3.business.wan;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpConnectOperation {
	
/******************** GetConnectionState  **************************************************************************************/	
	public static class GetConnectionState extends BaseRequest
    {			
        public GetConnectionState(IHttpFinishListener callback)
        {
        	super("GetConnectionState", "3.1", callback);
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
        public Connect(IHttpFinishListener callback)
        {
        	super("Connect", "3.2", callback);
        }
    }
	

	
	/******************** DisConnect  **************************************************************************************/	
	public static class DisConnect extends BaseRequest
    {	
        public DisConnect(IHttpFinishListener callback)
        {
        	super("DisConnect", "3.3", callback);
        }
    }
	

	/******************** GetConnectionSettings  **************************************************************************************/	
	public static class GetConnectionSettings extends BaseRequest
    {			
        public GetConnectionSettings(IHttpFinishListener callback)
        {
        	super("GetConnectionSettings", "3.4", callback);
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
            m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
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

		public SetConnectionSettings(ConnectionSettingsResult result, IHttpFinishListener callback) {
			super("SetConnectionSettings", "3.5", callback);
			m_result.setValue(result);
		}

		@Override
        protected void buildHttpParamJson() throws JSONException {
				JSONObject settings = new JSONObject();
				settings.put("IdleTime", m_result.IdleTime);
				settings.put("RoamingConnect", m_result.RoamingConnect);
				settings.put("ConnectMode", m_result.ConnectMode);
				settings.put("PdpType", m_result.PdpType);

				m_requestParamJson.put(ConstValue.JSON_PARAMS, settings);
		}
	}
}
