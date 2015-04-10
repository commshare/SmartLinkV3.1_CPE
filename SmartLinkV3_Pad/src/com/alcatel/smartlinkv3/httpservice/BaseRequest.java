package com.alcatel.smartlinkv3.httpservice;

import java.util.Hashtable;

import org.json.JSONObject;

import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

public abstract class BaseRequest 
{
	protected JSONObject m_requestParamJson = new JSONObject();
	protected String m_url;
	protected Hashtable<String,String> m_headers;
	protected IHttpFinishListener m_finsishCallback; 
	
	protected String m_strId = new String();
	
	public BaseRequest(IHttpFinishListener callback)
	{
	    m_url = "";
	    m_finsishCallback = callback;
	}
	
	
	public String getHttpUrl()
	{
	    return m_url;
	}
	
	public void setHttpUrl(String url)
	{
	    m_url = url;
	}
	
	public JSONObject getRequsetParmJson()
	{
	    return m_requestParamJson;
	}
	
	public IHttpFinishListener getCallback()
	{
	    return m_finsishCallback;
	}
	
	protected abstract void buildHttpParamJson();
	
	public void buildRequestParamJson()
	{
		buildHttpParamJson();
	}
	
	public abstract BaseResponse createResponseObject();
}
