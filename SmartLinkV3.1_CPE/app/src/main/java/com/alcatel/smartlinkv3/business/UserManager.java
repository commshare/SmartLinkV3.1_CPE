package com.alcatel.smartlinkv3.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.smartlinkv3.business.user.HttpUser;
import com.alcatel.smartlinkv3.business.user.LoginStateResult;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager;
import com.alcatel.smartlinkv3.httpservice.HttpRequestManager.IHttpFinishListener;

import java.util.Timer;
import java.util.TimerTask;

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
	
		
	public UserLoginStatus getLoginStatus() {
		return m_loginStatus;
	}	

	
//Login  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void login(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("User", "Login") != true)
			return;
		
		String strUserName = (String) data.getParamByKey("user_name");
    	String strPsw = (String) data.getParamByKey("password");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.Login(strUserName, strPsw, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {
                if(response.isValid()) {
                	if(response.isNoError()) {
                		startUpdateLoginTimeTask();
                		m_loginStatus = UserLoginStatus.login;
                		
                	}else if(null != m_updateLoginTimeTask) {
						m_updateLoginTimeTask.cancel();
						m_updateLoginTimeTask = null;
                	}
                }else if(null != m_updateLoginTimeTask) {
            			m_updateLoginTimeTask.cancel();
            			m_updateLoginTimeTask = null;
            		}
 
                Intent megIntent= new Intent(MessageUti.USER_LOGIN_REQUEST);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
	
//Login  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void forcelogin(DataValue data) {
		if(FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") != true)
			return;
		
		String strUserName = (String) data.getParamByKey("user_name");
    	String strPsw = (String) data.getParamByKey("password");
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.ForceLogin(strUserName, strPsw, new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {
                if(response.isValid()) {
                	if(response.isNoError()) {
                		startUpdateLoginTimeTask();
                		m_loginStatus = UserLoginStatus.login;
                		
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
 
                Intent megIntent= new Intent(MessageUti.USER_FORCE_LOGIN_REQUEST);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
    			m_context.sendBroadcast(megIntent);
            }
        }));
    } 
		
//Logout  Request ////////////////////////////////////////////////////////////////////////////////////////// 
	public void logout(DataValue data) {		
	
		if(FeatureVersionManager.getInstance().isSupportApi("User", "Logout") != true)
			return;
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.Logout( new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {              	
        		if(null != m_updateLoginTimeTask) {
        			m_updateLoginTimeTask.cancel();
        			m_updateLoginTimeTask = null;
        		}
                if(response.isValid()) {
                	if(response.isNoError()) {
                		m_loginStatus = UserLoginStatus.Logout;
                	
                	}else{
                		
                	}
                }else{
                	//Log
                }

				sendBroadcast(response, MessageUti.USER_LOGOUT_REQUEST);
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
    	
		HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.GetLoginState(new IHttpFinishListener() {
            @Override
			public void onHttpRequestFinish(BaseResponse response) 
            {   
            	LoginStateResult loginStateResult;
                if(response.isValid()) {
                	if(response.isNoError()) {
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
        	
        	HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.HeartBeat(new IHttpFinishListener() {
                @Override
				public void onHttpRequestFinish(BaseResponse response) 
                {
                    if(response.isValid()) {
                    	if(response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
                    		if(null != m_updateLoginTimeTask) {
                    			m_updateLoginTimeTask.cancel();
                    			m_updateLoginTimeTask = null;
                    		}
                    		m_loginStatus = UserLoginStatus.Logout;
                    	}else{
                    		
                    	}
                    }
                    else{
                		if(null != m_getLoginStateTask) {
                			m_getLoginStateTask.cancel();
                			m_getLoginStateTask = null;
                		}
                    }
					sendBroadcast(response, MessageUti.USER_HEARTBEAT_REQUEST);
                }               	
            }));
        	}        
	}	
	
	private void startUpdateLoginTimeTask()
	{
		if(FeatureVersionManager.getInstance().isSupportApi("User", "HeartBeat") != true)
			return;
		if(m_updateLoginTimeTask == null) {
			m_updateLoginTimeTask = new UpdateLoginTimeTask();
			m_UpdateLoginTimer.scheduleAtFixedRate(m_updateLoginTimeTask, 0, 6000);
		}
	}
	
	//Change Password Request///////////////////////////////////////////////////////////////////////////////////
	public void changepassword(DataValue data){
		if(FeatureVersionManager.getInstance().isSupportApi("User", "ChangePassword") != true){
			return;
		}
		String strUserName = (String) data.getParamByKey("user_name");
    	String strCurrPsw = (String) data.getParamByKey("current_password");
    	String strNewPsw = (String) data.getParamByKey("new_password");
    	
    	HttpRequestManager.GetInstance().sendPostRequest(new HttpUser.ChangePassword(strUserName, strCurrPsw, strNewPsw, new IHttpFinishListener(){

			@Override
			public void onHttpRequestFinish(BaseResponse response) {
				// TODO Auto-generated method stub
				String strErrcode = new String();
                int ret = response.getResultCode();
                
                if(response.isValid()) {
                	strErrcode = response.getErrorCode();
                }
                else{
                	strErrcode = ErrorCode.UNKNOWN_ERROR;
                }

    			sendBroadcast(response, MessageUti.USER_CHANGE_PASSWORD_REQUEST);
			}
    	}));
	}
}
