package com.alcatel.smartlinkv3.httpservice;

import org.json.JSONObject;

import com.alcatel.smartlinkv3.common.DataUti;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.util.Log;

public abstract class BaseResponse
{
	public final static String ENCODING = "utf-8";
	
	public final static int RESPONSE_OK = 0;
	public final static int RESPONSE_CONNECTION_ERROR = 1;
	public final static int RESPONSE_INVALID = 2;
	
	protected String m_strErrorCode = new String();
	protected String m_strErrorMessage = new String();
	protected String m_strId = new String();
	protected int m_response_result;
	protected IHttpFinishListener m_finsishCallback;
	
	public BaseResponse(IHttpFinishListener callback)
	{
	    m_response_result = RESPONSE_OK;
	    m_finsishCallback = callback;
	}
	
	public String getErrorCode() {
		return m_strErrorCode;
	}
	
	public String getErrorMessage() {
		return m_strErrorMessage;
	}
	
	public int getResultCode() 
	{
		return m_response_result;
	}
	
	public void setResult(int result)
	{
	    m_response_result = result;
	}
	
	protected abstract void parseContent(String strJsonResult);
	
	
	public int parseResult(JSONObject jsonResult)
	{
	    if(jsonResult != null)
	    {
	    	try
            {
            	 m_response_result = RESPONSE_OK;
            	 JSONObject resultObj = jsonResult.optJSONObject(ConstValue.JSON_RESULT);
            	 if(resultObj == null) {
            		 JSONObject errorObj = jsonResult.optJSONObject(ConstValue.JSON_ERROR);
            		 if(errorObj != null) {
            			 m_strErrorCode = DataUti.parseString(errorObj.optString(ConstValue.JSON_ERROR_CODE));
            			 m_strErrorMessage = DataUti.parseString(errorObj.optString(ConstValue.JSON_ERROR_MESSAGE));
            		 }else{
            			 m_response_result = RESPONSE_INVALID;
            		 }
            	 }else{
            		 parseContent(resultObj.toString());
            	 }
            	 
            	 m_strId = DataUti.parseString(jsonResult.optString(ConstValue.JSON_ID));
             }catch(Exception e){
                 m_response_result = RESPONSE_INVALID;
                 e.printStackTrace();
             }
	    }
	    else
	    {
	        m_response_result = RESPONSE_INVALID;	        
	        Log.d("%s: Empty response!", this.getClass().getName());
	    }
	    return m_response_result;
	}
	
	public void invokeFinishCallback()
	{
		m_finsishCallback.onHttpRequestFinish(this);
	}
	
	public abstract <T> T getModelResult();
}