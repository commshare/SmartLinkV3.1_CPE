package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpAutoEnterPinState {
	
/******************** GetAutoValidatePinState  **************************************************************************************/	
	public static class GetAutoValidatePinState extends BaseRequest
    {	
        public GetAutoValidatePinState(String strId,IHttpFinishListener callback) 
        {
        	super("GetAutoValidatePinState", strId, callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new GetAutoValidatePinStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetAutoValidatePinStateResponse extends BaseResponse
    {
		AutoEnterPinStateResult m_autoEnterPinState;
        
        public GetAutoValidatePinStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) {
        	Gson gson = new Gson();
        	m_autoEnterPinState = gson.fromJson(strJsonResult, AutoEnterPinStateResult.class);
        }

        @Override
        public AutoEnterPinStateResult getModelResult() 
        {
             return m_autoEnterPinState;
        }
    }
	/******************** SetAutoValidatePinState  **************************************************************************************/	
	public static class SetAutoValidatePinState extends BaseRequest
    {		
		private String m_strPin = new String();
		private int m_nState = 0;
		
        public SetAutoValidatePinState(String strId,int nState,String strPin,IHttpFinishListener callback) 
        {
        	super("SetAutoValidatePinState", strId, callback);
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

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetAutoValidatePinStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetAutoValidatePinStateResponse extends BaseResponse
    {
        
        public SetAutoValidatePinStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
	/******************** ChangePinState  **************************************************************************************/	
	public static class ChangePinState extends BaseRequest
    {		
		private String m_strPin = new String();
		private int m_nState = 0;
		
        public ChangePinState(String strId,int nState,String strPin,IHttpFinishListener callback) 
        {
        	super("ChangePinState", strId, callback);
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

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new ChangePinStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class ChangePinStateResponse extends BaseResponse
    {
        
        public ChangePinStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	
        }

        @Override
        public <T> T getModelResult() 
        {
             return null;
        }
    }
}
