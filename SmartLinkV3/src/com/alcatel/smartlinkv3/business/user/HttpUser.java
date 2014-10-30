package com.alcatel.smartlinkv3.business.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alcatel.smartlinkv3.httpservice.BaseRequest;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.ConstValue;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;
import com.google.gson.Gson;

public class HttpUser {
	
/********************  Login  **************************************************************************************/	
	public static class Login extends BaseRequest
    {	
		String m_strUserName = new String();
		String m_strPsw = new String();
		
        public Login(String strId,String strUserName,String strPsw,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strUserName = strUserName;
        	m_strPsw = strPsw;
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Login");
	        	
	        	JSONObject userInfo = new JSONObject();
	        	userInfo.put("UserName", m_strUserName);
	        	userInfo.put("Password", m_strPsw);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, userInfo);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new LoginResponse(m_finsishCallback);
        }
        
    }
	
	public static class LoginResponse extends BaseResponse
    {
        
        public LoginResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
		protected void parseContent(String strJsonResult) {			
		}

		@Override
		public <T> T getModelResult() {		
			return null;
		}
    }
/********************  Logout  **************************************************************************************/	
	public static class Logout extends BaseRequest
    {	
        public Logout(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "Logout");

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
            return new LogoutResponse(m_finsishCallback);
        }
        
    }
	
	public static class LogoutResponse extends BaseResponse
    {
        
        public LogoutResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
		protected void parseContent(String strJsonResult) {			
		}

		@Override
		public <T> T getModelResult() {		
			return null;
		} 
    }
/********************  GetLoginState  **************************************************************************************/
	public static class GetLoginState extends BaseRequest
    {	
        public GetLoginState(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "GetLoginState");

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
            return new GetLoginStateResponse(m_finsishCallback);
        }
        
    }
	
	public static class GetLoginStateResponse extends BaseResponse
    {
		private LoginStateResult m_loginStateResult;
        
        public GetLoginStateResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_loginStateResult = gson.fromJson(strJsonResult, LoginStateResult.class);
        }

        @SuppressWarnings("unchecked")
		@Override
        public LoginStateResult getModelResult() 
        {
             return m_loginStateResult;
        }
    }
	
	/******************** UpdateLoginTime  not  defined**************************************************************************************/	
	
	public static class HeartBeat extends BaseRequest
    {			
		
        public HeartBeat(String strId, IHttpFinishListener callback) 
        {
        	super(callback);      
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "HeartBeat");
    
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, null);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {			
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new HeartBeatResponse(m_finsishCallback);
        }
        
    }
	
	public static class HeartBeatResponse extends BaseResponse
    {	
        
        public HeartBeatResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

		@Override
		protected void parseContent(String strJsonResult) {			
		}

		@Override
		public <T> T getModelResult() {		
			return null;
		}    
    }
}
