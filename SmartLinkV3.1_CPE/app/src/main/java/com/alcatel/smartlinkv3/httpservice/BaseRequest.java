package com.alcatel.smartlinkv3.httpservice;

import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public abstract class BaseRequest 
{
	protected JSONObject m_requestParamJson = new JSONObject();
//	protected String m_url;
	protected Hashtable<String,String> m_headers;
	protected IHttpFinishListener m_finsishCallback; 
	
	protected String m_strId;
	protected String method;
	
	public BaseRequest(String method, String id, IHttpFinishListener callback)
	{
		this.method = method;
		m_strId = id;
		m_finsishCallback = callback;
//	    m_url = "";
	}
	
//
//	public String getHttpUrl()
//	{
//	    return m_url;
//	}
//
//	public void setHttpUrl(String url)
//	{
//	    m_url = url;
//	}
	
	public JSONObject getRequestParamJson()
	{
	    return m_requestParamJson;
	}
	
	public IHttpFinishListener getCallback()
	{
	    return m_finsishCallback;
	}
	
	protected void buildHttpParamJson() throws JSONException {
		m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	}
	
	public void buildRequestParamJson()
	{
		try {
			m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);

			m_requestParamJson.put(ConstValue.JSON_METHOD, method);
			buildHttpParamJson();

			m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	

	public BaseResponse createResponseObject()
	{
		return new BaseResponse(m_finsishCallback);
	}
}
