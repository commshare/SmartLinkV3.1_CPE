package com.alcatel.wifilink.business.user;

import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseRequest;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.ConstValue;
import com.alcatel.wifilink.httpservice.DataResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUser {
	
/********************  Login  **************************************************************************************/	
	public static class Login extends BaseRequest
    {	
		String m_strUserName;
		String m_strPsw;
		
        public Login(String strUserName,String strPsw,IHttpFinishListener callback)
        {
        	super("User", "Login", "1.1", callback);
        	m_strUserName = strUserName;
        	m_strPsw = strPsw;
        }

        @Override
		protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject userInfo = new JSONObject();
	        	userInfo.put("UserName", m_strUserName);
	        	userInfo.put("Password", m_strPsw);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, userInfo);
        }
    }

	
	/********************  ForceLogin  **************************************************************************************/	
	public static class ForceLogin extends BaseRequest
    {	
		String m_strUserName = new String();
		String m_strPsw = new String();
		
        public ForceLogin(String strUserName,String strPsw,IHttpFinishListener callback)
        {
        	super("User", "ForceLogin", "1.6", callback);
			setBroadcastAction(MessageUti.USER_FORCE_LOGIN_REQUEST);
        	m_strUserName = strUserName;
        	m_strPsw = strPsw;
        }

        @Override
		protected void buildHttpParamJson() throws JSONException
        {
	        	JSONObject userInfo = new JSONObject();
	        	userInfo.put("UserName", m_strUserName);
	        	userInfo.put("Password", m_strPsw);
	        	
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, userInfo);
        }
    }

	
/********************  Logout  **************************************************************************************/	
	public static class Logout extends BaseRequest
    {	
        public Logout(IHttpFinishListener callback)
        {
        	super("User", "Logout", "1.2", callback);
			setBroadcastAction(MessageUti.USER_LOGOUT_REQUEST);
        }
    }

/********************  GetLoginState  **************************************************************************************/
	public static class GetLoginState extends BaseRequest
    {	
        public GetLoginState(IHttpFinishListener callback)
        {
        	super("User", "GetLoginState", "1.3", callback);
        }

        @Override
        public BaseResponse createResponseObject() 
        {            
//            return new GetLoginStateResponse(m_finsishCallback);
			return new DataResponse<>(LoginStateResult.class, null, m_finsishCallback);
        }
        
    }

	
	/******************** UpdateLoginTime  not  defined**************************************************************************************/	
	
	public static class HeartBeat extends BaseRequest
    {			
        public HeartBeat(IHttpFinishListener callback)
        {
        	super("User", "HeartBeat", "1.5", callback);
			setBroadcastAction(MessageUti.USER_HEARTBEAT_REQUEST);
        }
    }

	
	/******************** Change Password**************************************************************************************/	
	public static class ChangePassword extends BaseRequest{
		
		String m_strUserName = new String();
		String m_strCurrPsw = new String();
		String m_strNewPsw = new String();

		public ChangePassword(String strUserName,String strCurrPsw,String strNewPsw,IHttpFinishListener callback) {
			super("User", "ChangePassword", "1.4", callback);
			setBroadcastAction(MessageUti.USER_CHANGE_PASSWORD_REQUEST);
			m_strUserName = strUserName;
			m_strCurrPsw = strCurrPsw;
			m_strNewPsw = strNewPsw;
		}

		@Override
		protected void buildHttpParamJson() throws JSONException {
	        	JSONObject userInfo = new JSONObject();
	        	userInfo.put("UserName", m_strUserName);
	        	userInfo.put("CurrPassword", m_strCurrPsw);
	        	userInfo.put("NewPassword", m_strNewPsw);
	        	m_requestParamJson.put(ConstValue.JSON_PARAMS, userInfo);
		}
	}
}
