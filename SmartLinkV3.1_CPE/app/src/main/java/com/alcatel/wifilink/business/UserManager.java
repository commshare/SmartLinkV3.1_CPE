package com.alcatel.wifilink.business;

import android.content.Context;
import android.content.Intent;

import com.alcatel.wifilink.business.user.HttpUser;
import com.alcatel.wifilink.business.user.LoginStateResult;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM.UserLoginStatus;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.httpservice.LegacyHttpClient;
import com.alcatel.wifilink.httpservice.LegacyHttpClient.IHttpFinishListener;

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
    private OnLoginStatusListener onLoginStatusListener;

    @Override
    protected void clearData() {
        m_strLogin = "";
        m_strUserName = "";
    }

    @Override
    protected void stopRollTimer() {
        if (null != m_updateLoginTimeTask) {
            m_updateLoginTimeTask.cancel();
            m_updateLoginTimeTask = null;
        }

        if (null != m_getLoginStateTask) {
            m_getLoginStateTask.cancel();
            m_getLoginStateTask = null;
        }

    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {

        if (intent.getAction().equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
            if (bCPEWifiConnected == true) {
                startGetLoginStateTask();
            } else {
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
        String strUserName = (String) data.getParamByKey("user_name");
        String strPsw = (String) data.getParamByKey("password");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpUser.Login(strUserName, strPsw, new IHttpFinishListener() {
            @Override
            public void onHttpRequestFinish(BaseResponse response) {
                if (response.isValid()) {
                    if (response.isNoError()) {
                        startUpdateLoginTimeTask();
                        m_loginStatus = UserLoginStatus.LOGIN;
                        if (onLoginStatusListener != null) {
                            onLoginStatusListener.isLoginSuccessful(true);
                        }

                    } else if (null != m_updateLoginTimeTask) {
                        m_updateLoginTimeTask.cancel();
                        m_updateLoginTimeTask = null;
                    }
                } else if (null != m_updateLoginTimeTask) {
                    m_updateLoginTimeTask.cancel();
                    m_updateLoginTimeTask = null;
                }

                Intent megIntent = new Intent(MessageUti.USER_LOGIN_REQUEST);
                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
                m_context.sendBroadcast(megIntent);
            }
        }));
    }

    public interface OnLoginStatusListener {
        void isLoginSuccessful(boolean success);
    }

    public void setOnLoginStatusListener(OnLoginStatusListener onLoginStatusListener) {
        this.onLoginStatusListener = onLoginStatusListener;
    }

    //Login  Request ////////////////////////////////////////////////////////////////////////////////////////// 
    public void forcelogin(DataValue data) {
        String strUserName = (String) data.getParamByKey("user_name");
        String strPsw = (String) data.getParamByKey("password");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpUser.ForceLogin(strUserName, strPsw, response -> {
            if (response.isOk()) {
                startUpdateLoginTimeTask();
                m_loginStatus = UserLoginStatus.LOGIN;
            } else if (null != m_updateLoginTimeTask) {
                m_updateLoginTimeTask.cancel();
                m_updateLoginTimeTask = null;
            }

            //                Intent megIntent= new Intent(MessageUti.USER_FORCE_LOGIN_REQUEST);
            //                megIntent.putExtra(MessageUti.HTTP_RESPONSE, response);
            //    			m_context.sendBroadcast(megIntent);
        }));
    }

    //Logout  Request ////////////////////////////////////////////////////////////////////////////////////////// 
    public void logout(DataValue data) {
        LegacyHttpClient.getInstance().sendPostRequest(new HttpUser.Logout(response -> {
            if (null != m_updateLoginTimeTask) {
                m_updateLoginTimeTask.cancel();
                m_updateLoginTimeTask = null;
            }
            if (response.isOk()) {
                m_loginStatus = UserLoginStatus.Logout;
            }

            //				sendBroadcast(response, MessageUti.USER_LOGOUT_REQUEST);
        }));
    }


    class GetLoginStateTask extends TimerTask {
        @Override
        public void run() {
            getLoginState();
        }
    }


    private void startGetLoginStateTask() {
        if (m_getLoginStateTask == null) {
            m_getLoginStateTask = new GetLoginStateTask();
            m_getLoginStateTimer.scheduleAtFixedRate(m_getLoginStateTask, 0, 5000);
        }
    }

    //GetLoginState  Request ////////////////////////////////////////////////////////////////////////////////////////// 	
    public void getLoginState() {
        LegacyHttpClient.getInstance().sendPostRequest(new HttpUser.GetLoginState(response -> {
            if (response.isOk()) {
                LoginStateResult loginStateResult = response.getModelResult();
                m_loginStatus = UserLoginStatus.build(loginStateResult.getState());
            } else {
                m_loginStatus = UserLoginStatus.Logout;
            }
        }));
    }

    class UpdateLoginTimeTask extends TimerTask {
        @Override
        public void run() {

            LegacyHttpClient.getInstance().sendPostRequest(new HttpUser.HeartBeat(response -> {
                if (response.isValid()) {
                    if (response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
                        if (null != m_updateLoginTimeTask) {
                            m_updateLoginTimeTask.cancel();
                            m_updateLoginTimeTask = null;
                        }
                        m_loginStatus = UserLoginStatus.Logout;
                    }
                } else if (null != m_getLoginStateTask) {
                    m_getLoginStateTask.cancel();
                    m_getLoginStateTask = null;

                }
                //					sendBroadcast(response, MessageUti.USER_HEARTBEAT_REQUEST);
            }));
        }
    }

    private void startUpdateLoginTimeTask() {
        if (m_updateLoginTimeTask == null) {
            m_updateLoginTimeTask = new UpdateLoginTimeTask();
            m_UpdateLoginTimer.scheduleAtFixedRate(m_updateLoginTimeTask, 0, 6000);
        }
    }

    //Change Password Request///////////////////////////////////////////////////////////////////////////////////
    public void changepassword(DataValue data) {
        String strUserName = (String) data.getParamByKey("user_name");
        String strCurrPsw = (String) data.getParamByKey("current_password");
        String strNewPsw = (String) data.getParamByKey("new_password");

        LegacyHttpClient.getInstance().sendPostRequest(new HttpUser.ChangePassword(strUserName, strCurrPsw, strNewPsw, response -> {
            //    			sendBroadcast(response, MessageUti.USER_CHANGE_PASSWORD_REQUEST);
        }));
    }
}
