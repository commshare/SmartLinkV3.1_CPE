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
	public static class UserLogin extends BaseRequest
    {	
		String m_strUserName = new String();
		String m_strPsw = new String();
		
        public UserLogin(String strUserName,String strPsw,String strId,IHttpFinishListener callback) 
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
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "User.Login");
	        	
	        	JSONObject userInfo = new JSONObject();
	        	userInfo.put("Username", m_strUserName);
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
            return new UserLoginResponse(m_finsishCallback);
        }
        
    }
	
	public static class UserLoginResponse extends BaseResponse
    {
		private LoginTokens m_loginTokens;
        
        public UserLoginResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult) 
        {
        	Gson gson = new Gson();
        	m_loginTokens = gson.fromJson(strJsonResult, LoginTokens.class);
        }

        @Override
        public LoginTokens getModelResult() 
        {
             return m_loginTokens;
        }
    }
/********************  Logout  **************************************************************************************/	
	public static class UserLogout extends BaseRequest
    {	
        public UserLogout(String strId,IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "User.Logout");

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
            return new UserLogoutResponse(m_finsishCallback);
        }
        
    }
	
	public static class UserLogoutResponse extends BaseResponse
    {
		private LogoutResult m_logoutResult;
        
        public UserLogoutResponse(IHttpFinishListener callback) 
        {
            super(callback);            
        }

        @Override
        protected void parseContent(String strJsonResult)  
        {
        	Gson gson = new Gson();
        	m_logoutResult = gson.fromJson(strJsonResult, LogoutResult.class);
        }

        @Override
        public LogoutResult getModelResult() 
        {
             return m_logoutResult;
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
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "User.GetLoginState");

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

        @Override
        public LoginStateResult getModelResult() 
        {
             return m_loginStateResult;
        }
    }
	
	/******************** UpdateLoginTime  **************************************************************************************/	
	public static class UpdateLoginTime extends BaseRequest
    {	
		String m_sysauth = new String();		
		
        public UpdateLoginTime(String sysauth, String strId, IHttpFinishListener callback) 
        {
        	super(callback);  
        	m_sysauth = sysauth;      
        	m_strId = strId;
        }

        @Override
        protected void buildHttpParamJson() 
        {
        	try {
				m_requestParamJson.put(ConstValue.JSON_RPC, ConstValue.JSON_RPC_VERSION);
	        	m_requestParamJson.put(ConstValue.JSON_METHOD, "User.UpdateLoginTime");
	        	
	        	JSONObject userInfo = new JSONObject();
	        	userInfo.put("sysauth", m_sysauth);      
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, userInfo);
	        	m_requestParamJson.put(ConstValue.JSON_ID, m_strId);
        	} catch (JSONException e) {			
				e.printStackTrace();
			}
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
            return new UpdateLoginTimeResponse(m_finsishCallback);
        }
        
    }
	
	public static class UpdateLoginTimeResponse extends BaseResponse
    {	
        
        public UpdateLoginTimeResponse(IHttpFinishListener callback) 
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
