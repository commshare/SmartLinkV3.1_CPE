package com.alcatel.smartlinkv3.business.sim;

import org.json.JSONException;
import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public class HttpUnlockPinPuk {
	
/******************** UnlockPin  **************************************************************************************/	
	public static class UnlockPin extends BaseRequest
    {		
		private String m_strPin = new String();
		
        public UnlockPin(String strId,String strPin,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "UnlockPin");

	        	JSONObject pin = new JSONObject();
	        	pin.put("Pin", m_strPin);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, pin);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new UnlockPinResponse(m_finsishCallback);
        }
        
    }
	
	public static class UnlockPinResponse extends BaseResponse
    {
        
        public UnlockPinResponse(IHttpFinishListener callback) 
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
	
	/******************** UnlockPuk  **************************************************************************************/	
	public static class UnlockPuk extends BaseRequest
    {		
		private String m_strPuk = new String();
		private String m_strPin = new String();
		
        public UnlockPuk(String strId,String strPuk,String strPin,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        	m_strPuk = strPuk;
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "UnlockPuk");

	        	JSONObject pinPuk = new JSONObject();
	        	pinPuk.put("Puk", m_strPuk);
	        	pinPuk.put("Pin", m_strPin);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, pinPuk);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new UnlockPinResponse(m_finsishCallback);
        }
        
    }
	
	public static class UnlockPukResponse extends BaseResponse
    {
        
        public UnlockPukResponse(IHttpFinishListener callback) 
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
