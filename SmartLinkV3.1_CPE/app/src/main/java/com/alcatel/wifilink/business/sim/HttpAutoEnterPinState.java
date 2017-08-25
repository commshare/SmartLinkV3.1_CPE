package com.alcatel.wifilink.business.sim;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpAutoEnterPinState {
	
/******************** GetAutoValidatePinState  **************************************************************************************/	
	public static class GetAutoValidatePinState extends BaseRequest
    {	
        public GetAutoValidatePinState(IHttpFinishListener callback)
        {
        	super("SIM", "GetAutoValidatePinState", "2.6", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetAutoValidatePinStateResponse(m_finsishCallback);
            return new DataResponse<>(AutoEnterPinStateResult.class, MessageUti.SIM_GET_AUTO_ENTER_PIN_STATE_REQUEST, m_finsishCallback);
        }
        
    }

	/******************** SetAutoValidatePinState  **************************************************************************************/	
	public static class SetAutoValidatePinState extends BaseRequest
    {		
		private String m_strPin = new String();
		private int m_nState = 0;
		
        public SetAutoValidatePinState(int nState,String strPin,IHttpFinishListener callback)
        {
        	super("SIM", "SetAutoValidatePinState", "2.7", callback);
			setBroadcastAction(MessageUti.SIM_SET_AUTO_ENTER_PIN_STATE_REQUEST);
        	m_nState = nState;
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject setState = new JSONObject();
	        	setState.put("State", m_nState);
	        	setState.put("Pin", m_strPin);
	        	if(m_nState == 0){
	        		
	        	}
	        	else{
	        		
	        	}
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, setState);
        }
    }

	/******************** ChangePinState  **************************************************************************************/	
	public static class ChangePinState extends BaseRequest
    {		
		private String m_strPin = new String();
		private int m_nState = 0;
		
        public ChangePinState(int nState,String strPin,IHttpFinishListener callback)
        {
        	super("SIM", "ChangePinState", "2.5", callback);
			setBroadcastAction(MessageUti.SIM_CHANGE_PIN_STATE_REQUEST);
        	m_nState = nState;
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject setState = new JSONObject();
	        	setState.put("State", m_nState);
	        	setState.put("Pin", m_strPin);
	        	if(m_nState == 0){
	        	}
	        	else{
	        	}
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, setState);
        }
    }
}
