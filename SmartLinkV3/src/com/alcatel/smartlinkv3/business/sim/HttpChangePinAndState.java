package com.alcatel.smartlinkv3.business.sim;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpChangePinAndState {
	
/******************** ChangePin  **************************************************************************************/	
	public static class ChangePin extends BaseRequest
    {	
		private String m_strNewPin = new String();
		private String m_strCurrentPin = new String();
		
        public ChangePin(String strId,String strNewPin,String strCurrentPin,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_strNewPin = strNewPin;
        	m_strCurrentPin = strCurrentPin;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "ChangePinCode");

	        	JSONObject pinInfo = new JSONObject();
	        	pinInfo.put("NewPin", m_strNewPin);
	        	pinInfo.put("CurrentPin", m_strCurrentPin);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, pinInfo);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new ChangePinResponse(m_finsishCallback);
        }
        
    }
	
	public static class ChangePinResponse extends BaseResponse
    {
        
        public ChangePinResponse(IHttpFinishListener callback) 
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

	        	JSONObject changeState = new JSONObject();
	        	changeState.put("Pin", m_strPin);
	        	changeState.put("State", m_nState);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, changeState);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
