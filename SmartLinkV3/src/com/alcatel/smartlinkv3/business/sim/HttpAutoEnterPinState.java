package com.alcatel.smartlinkv3.business.sim;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpAutoEnterPinState {
	
/******************** GetAutoEnterPinState  **************************************************************************************/	
	public static class GetAutoEnterPinState extends BaseRequest
    {	
        public GetAutoEnterPinState(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetAutoValidatePinState");

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
            return new GetAutoEnterPinStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetAutoEnterPinStateResponse extends BaseResponse
    {
		AutoEnterPinStateResult m_autoEnterPinState;
        
        public GetAutoEnterPinStateResponse(IHttpFinishListener callback) 
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
	
	/******************** SetAutoEnterPinState  **************************************************************************************/	
	public static class SetAutoEnterPinState extends BaseRequest
    {		
		private String m_strPin = new String();
		private int m_nState = 0;
		
        public SetAutoEnterPinState(String strId,int nState,String strPin,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_nState = nState;
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "ChangePinState");

	        	JSONObject setState = new JSONObject();
	        	setState.put("State", m_nState);
	        	setState.put("Pin", m_strPin);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, setState);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new SetAutoEnterPinStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class SetAutoEnterPinStateResponse extends BaseResponse
    {
        
        public SetAutoEnterPinStateResponse(IHttpFinishListener callback) 
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
