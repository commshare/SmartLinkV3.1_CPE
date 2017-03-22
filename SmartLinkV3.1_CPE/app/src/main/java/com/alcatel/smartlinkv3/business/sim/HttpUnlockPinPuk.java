package com.alcatel.smartlinkv3.business.sim;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUnlockPinPuk {
	
/******************** UnlockPin  **************************************************************************************/	
	public static class UnlockPin extends BaseRequest
    {		
		private String m_strPin = new String();
		
        public UnlockPin(String strPin,IHttpFinishListener callback)
        {
        	super("UnlockPin", "2.2", callback);
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject pin = new JSONObject();
	        	pin.put("Pin", m_strPin);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, pin);
        }
    }

	/******************** UnlockPuk  **************************************************************************************/	
	public static class UnlockPuk extends BaseRequest
    {		
		private String m_strPuk = new String();
		private String m_strPin = new String();
		
        public UnlockPuk(String strPuk,String strPin,IHttpFinishListener callback)
        {
        	super("UnlockPuk", "2.3", callback);
        	m_strPuk = strPuk;
        	m_strPin = strPin;
        }

        @Override
        protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject pinPuk = new JSONObject();
	        	pinPuk.put("Puk", m_strPuk);
	        	pinPuk.put("Pin", m_strPin);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, pinPuk);
        }
    }
}
