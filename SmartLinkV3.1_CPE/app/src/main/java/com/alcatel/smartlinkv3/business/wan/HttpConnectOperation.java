package com.alcatel.smartlinkv3.business.wan;

import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpConnectOperation {
	
/******************** GetConnectionState  **************************************************************************************/	
	public static class GetConnectionState extends BaseRequest
    {			
        public GetConnectionState(IHttpFinishListener callback)
        {
        	super("Connection","GetConnectionState", "3.1", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetConnectionStateResponse(m_finsishCallback);
            return new DataResponse<>(ConnectStatusResult.class,
                    MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET,
                    m_finsishCallback);
        }
        
    }

	/******************** Connect  **************************************************************************************/	
	public static class Connect extends BaseRequest
    {	
        public Connect(IHttpFinishListener callback)
        {
        	super("Connection", "Connect", "3.2", callback);
            setBroadcastAction(MessageUti.WAN_CONNECT_REQUSET);
        }
    }

	/******************** DisConnect  **************************************************************************************/	
	public static class DisConnect extends BaseRequest
    {	
        public DisConnect(IHttpFinishListener callback)
        {
        	super("Connection", "DisConnect", "3.3", callback);
            setBroadcastAction(MessageUti.WAN_DISCONNECT_REQUSET);
        }
    }

	/******************** GetConnectionSettings  **************************************************************************************/	
	public static class GetConnectionSettings extends BaseRequest
    {			
        public GetConnectionSettings(IHttpFinishListener callback)
        {
        	super("Connection", "GetConnectionSettings", "3.4", callback);
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
            m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetConnectionSettingsResponse(m_finsishCallback);
            return new DataResponse<>(ConnectionSettingsResult.class,
                    MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET,
                    m_finsishCallback);
        }
    }

	
	/******************** SetConnectionSettings **************************************************************************************/
	public static class SetConnectionSettings extends BaseRequest {

		public ConnectionSettingsResult m_result = new ConnectionSettingsResult();

		public SetConnectionSettings(ConnectionSettingsResult result, IHttpFinishListener callback) {
			super("Connection", "SetConnectionSettings", "3.5", callback);
            setBroadcastAction(MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET);
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
