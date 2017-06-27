package com.alcatel.smartlinkv3.ui.dialog;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.model.user.LoginResult;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.network.ResponseBody;

public class AutoLoginProgressDialog {
    private ProgressDialog m_dlgProgress = null;
    private Context m_context = null;
    private static OnAutoLoginFinishedListener s_callback;
    private String loginCheckDialogTitle;
    private String loginCheckDialogContent;
    private AuthenticationBroadcastReceiver m_auReceiver;

    public AutoLoginProgressDialog(Context context) {
        m_context = context;
        loginCheckDialogTitle = m_context.getString(R.string.login_check_dialog_title);
        loginCheckDialogContent = m_context.getString(R.string.login_check_dialog_content) + "...";

        m_auReceiver = new AuthenticationBroadcastReceiver();

//        m_context.registerReceiver(m_auReceiver, new IntentFilter(MessageUti.USER_LOGIN_REQUEST));
        m_context.registerReceiver(m_auReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
    }

    public void autoLoginAndShowDialog(OnAutoLoginFinishedListener callback) {
        s_callback = callback;

        if (CPEConfig.getInstance().getAutoLoginFlag()) {
//            DataValue data = new DataValue();
//            data.addParam("user_name", CPEConfig.getInstance().getLoginUsername());
//            data.addParam("password", CPEConfig.getInstance().getLoginPassword());
//            BusinessManager.getInstance().sendRequestMessage(MessageUti.USER_LOGIN_REQUEST, data);

            String loginUsername = CPEConfig.getInstance().getLoginUsername();
            String loginPassword = CPEConfig.getInstance().getLoginPassword();
            API.get().login(loginUsername, loginPassword, new MySubscriber<LoginResult>() {
                @Override
                protected void onSuccess(LoginResult result) {
                    s_callback.onLoginSuccess();
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    super.onResultError(error);
                    s_callback.onLoginFailed(error.getCode());
                }

                @Override
                public void onCompleted() {
                    super.onCompleted();
                    s_callback = null;

                }
            });
        } else if (s_callback != null) {
            s_callback.onFirstLogin();
            return;
        }

        if (m_dlgProgress != null && m_dlgProgress.isShowing())
            return;


        m_dlgProgress = ProgressDialog.show(m_context, loginCheckDialogTitle, loginCheckDialogContent, true, false);
        m_dlgProgress.setCancelable(true);
    }

    public void closeDialog() {
        if (m_dlgProgress != null && m_dlgProgress.isShowing())
            m_dlgProgress.dismiss();
    }

    public void destroyDialog() {
        try {
            m_context.unregisterReceiver(m_auReceiver);
        } catch (Exception e) {
        }
    }

    private class AuthenticationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
//            Boolean ok = response != null && response.isOk();

            if (intent.getAction().equalsIgnoreCase(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
                boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
                if (!bCPEWifiConnected) {
                    closeDialog();
                }
            }
//            else if (intent.getAction().equalsIgnoreCase(MessageUti.USER_LOGIN_REQUEST)) {
//                if (m_dlgProgress != null && !m_dlgProgress.isShowing())
//                    return;
//                closeDialog();
//                if (null != s_callback) {
//                    if (ok) {
//                        s_callback.onLoginSuccess();
//                    } else {
//                        s_callback.onLoginFailed(response.getErrorCode());
//                    }
//                    s_callback = null;
//                }
//            }
        }
    }

    public interface OnAutoLoginFinishedListener {
        void onLoginSuccess();

        void onLoginFailed(String error_code);

        void onFirstLogin();
    }
}
