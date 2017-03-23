package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.DataResponse;
import com.alcatel.smartlinkv3.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpAutoEnterPinState {
	
/******************** GetAutoValidatePinState  **************************************************************************************/	
	public static class GetAutoValidatePinState extends BaseRequest
    {	
        public GetAutoValidatePinState(IHttpFinishListener callback)
        {
        	super("GetAutoValidatePinState", "2.6", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetAutoValidatePinStateResponse(m_finsishCallback);
            return new DataResponse<>(AutoEnterPinStateResult.class, m_finsishCallback);
        }
        
    }

	/******************** SetAutoValidatePinState  **************************************************************************************/	
	public static class SetAutoValidatePinState extends BaseRequest
    {		
		private String m_strPin = new String();
		private int m_nState = 0;
		
        public SetAutoValidatePinState(int nState,String strPin,IHttpFinishListener callback)
        {
        	super("SetAutoValidatePinState", "2.7", callback);
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
        	super("ChangePinState", "2.5", callback);
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
//	        		Log.v("PINCHECK", "REQUESTDISABLE");
	        	}
	        	else{
//	        		Log.v("PINCHECK", "REQUESTENABLE");
	        	}
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, setState);
        }
    }
}
