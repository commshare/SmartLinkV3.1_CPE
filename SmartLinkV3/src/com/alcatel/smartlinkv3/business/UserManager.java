package com.alcatel.smartlinkv3.business;

import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.business.user.HttpUser;
import com.alcatel.smartlinkv3.business.user.LoginStateResult;
import com.alcatel.smartlinkv3.business.user.LoginTokens;
import com.alcatel.smartlinkv3.business.user.LogoutResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UserManager extends BaseManager {
	
	private String m_strLogin = new String();
	private String m_strUserName = new String();
	
	private UpdateLoginTimeTask m_updateLoginTimeTask = null;
	private Timer m_UpdateLoginTimer = new Timer();
	
	private GetLoginStateTask m_getLoginStateTask = null;
	private Timer m_getLoginStateTimer = new Timer();
	
	private UserLoginStatus m_loginStatus = UserLoginStatus.Logout;
	
	@Override
	protected void clearData() {	
		m_strLogin = "";
		m_strUserName = "";
	} 
	
	@Override
	protected void stopRollTimer() {	
		if(null != m_updateLoginTimeTask) {
			m_updateLoginTimeTask.cancel();
			m_updateLoginTimeTask = null;
		}
		
		if(null != m_getLoginStateTask) {
			m_getLoginStateTask.cancel();
			m_getLoginStateTask = null;
		}
		
	}  
	
	@Override
	protected void onBroadcastReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
			boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
			if(bCPEWifiConnected == true) {
				startGetLoginStateTask();
			}
			else
			{
				stopRollTimer();				
			}
    	}
	}  
	
	public UserManager(Context context) {
		super(context);	
    }
	
	public String getUserName() {
		return m_strUserName;
	}
	
	public String getLoginString() {
		return m_strLogin;
	}
		
	public UserLoginStatus getLoginStatus() {
		return m_loginStatus;
	}	

	
//Login  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void login(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("User", "Login") != true)
			return;
		
		String strUserName = (String) data.getParamByKey("user_name");
    	String strPsw = (String) data.getParamByKey("password");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.UserLogin(strUserName,strPsw,"1.1", new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		LoginTokens loginTokens = response.getModelResult();
                		m_strLogin = loginTokens.getLogin();   
                		startUpdateLoginTimeTask();
                		m_loginStatus = UserLoginStatus.selfLogined;
                		
                	}else{
                		if(null != m_updateLoginTimeTask) {
                			m_updateLoginTimeTask.cancel();
                			m_updateLoginTimeTask = null;
                		}
                	}
                }else{
            		if(null != m_updateLoginTimeTask) {
            			m_updateLoginTimeTask.cancel();
            			m_updateLoginTimeTask = null;
            		}
                }
 
                Intent megIntent= new Intent(MessageUti.USER_LOGIN_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
//Logout  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void logout(DataValue data) {		
	
		if(FeatureVersionManager.getInstance().isSupportApi("User", "Logout") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.UserLogout("1.3", new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {              	
        		if(null != m_updateLoginTimeTask) {
        			m_updateLoginTimeTask.cancel();
        			m_updateLoginTimeTask = null;
        		}
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		LogoutResult logoutResult = response.getModelResult();
                		m_strLogin = "";
                		m_strUserName = logoutResult.getUserName();
                		m_loginStatus = UserLoginStatus.Logout;
                	
                	}else{
                		
                	}
                }else{
                	//Log
                }
 
                Intent megIntent= new Intent(MessageUti.USER_LOGOUT_REQUEST);
                megIntent.putExtra(MessageUti.RESPONSE_RESULT, ret);
                megIntent.putExtra(MessageUti.RESPONSE_ERROR_CODE, strErrcode);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
	
	class GetLoginStateTask extends TimerTask{ 
        @Override
		public void run() { 
        	getLoginState();  
        };        
	}
	

	private void startGetLoginStateTask()
	{
		if(m_getLoginStateTask == null) {
			m_getLoginStateTask = new GetLoginStateTask();
			m_getLoginStateTimer.scheduleAtFixedRate(m_getLoginStateTask, 0, 5000);
		}
	}	
	
//GetLoginState  Request ////////////////////////////////////////////////////////////////////////////////////////// 	
	public void getLoginState() {
		if(FeatureVersionManager.getInstance().isSupportApi("User", "GetLoginState") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.GetLoginState("1.2", new IHttpFinishListener() {           
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	LoginStateResult loginStateResult = new LoginStateResult();
            	String strErrcode = new String();
                int ret = response.getResultCode();
                if(ret == BaseResponse.RESPONSE_OK) {
                	strErrcode = response.getErrorCode();
                	if(strErrcode.length() == 0) {
                		loginStateResult = response.getModelResult();
                		m_loginStatus = UserLoginStatus.build(loginStateResult.getState());
                	}else{
                		m_loginStatus = UserLoginStatus.Logout;
                	}
                }else{
                	m_loginStatus = UserLoginStatus.Logout;
                }             
            }
        }));
    }
	
	class UpdateLoginTimeTask extends TimerTask{ 
        @Override
		public void run() { 
        	if(m_strLogin != null && m_strLogin.length() > 0)
        	{
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.UpdateLoginTime(m_strLogin, "1.5", new IHttpFinishListener() {           
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                { 
                	String strErrcode = new String();
                    int ret = response.getResultCode();                 
                    if(ret == BaseResponse.RESPONSE_OK && strErrcode.length() == 0) {
                    	
                    }
                    else{    

                		if(null != m_getLoginStateTask) {
                			m_getLoginStateTask.cancel();
                			m_getLoginStateTask = null;
                		}
                    }      
                    
                }               	
            }));
        	}
        }         
	}	
	
	private void startUpdateLoginTimeTask()
	{
		if(m_updateLoginTimeTask == null) {
			m_updateLoginTimeTask = new UpdateLoginTimeTask();
			m_UpdateLoginTimer.scheduleAtFixedRate(m_updateLoginTimeTask, 0, 30000);
		}
	}
}
