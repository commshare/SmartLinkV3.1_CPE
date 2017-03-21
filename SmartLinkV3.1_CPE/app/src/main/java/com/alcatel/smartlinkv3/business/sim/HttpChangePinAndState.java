package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpChangePinAndState {
	
/******************** ChangePinCode  **************************************************************************************/	
	public static class ChangePinCode extends BaseRequest
    {	
		private String m_strNewPin = new String();
		private String m_strCurrentPin = new String();
		
        public ChangePinCode(String strId,String strNewPin,String strCurrentPin,IHttpFinishListener callback) 
        {
        	super("ChangePinCode", strId, callback);
        	m_strNewPin = strNewPin;
        	m_strCurrentPin = strCurrentPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject pinInfo = new JSONObject();
	        	pinInfo.put("NewPin", m_strNewPin);
	        	pinInfo.put("CurrentPin", m_strCurrentPin);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, pinInfo);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new ChangePinCodeResponse(m_finsishCallback);
        }
        
    }
	
	public static class ChangePinCodeResponse extends BaseResponse
    {
        
        public ChangePinCodeResponse(IHttpFinishListener callback) 
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
		
        public ChangePinState(String strId,String strPin,int nState,IHttpFinishListener callback) 
        {
        	super("ChangePinState", strId, callback);
        	m_nState = nState;
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject changeState = new JSONObject();
	        	changeState.put("Pin", m_strPin);
	        	changeState.put("State", m_nState);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, changeState);
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
