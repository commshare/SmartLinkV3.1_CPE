package com.alcatel.wifilink.ui.wizard.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alcatel.wifilink.business.DataConnectManager;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;

public class QSBroadcastReceiver extends BroadcastReceiver {

    private OnWifiConnectListener onRefreshUiListener;
    private OnLogoutListener onLogoutListener;
    private OnLonginErrorListener onLoginErrorListener;
    private OnSimListener onSimListener;
    private OnPinFailedListener onPinFailedListener;
    private OnPinSuccessListener onPinSuccessListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();

        if (action.equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
            // If WiFi disconnect router, go to Refresh WiFi activity.
            if (!DataConnectManager.getInstance().getCPEWifiConnected()) {
                // TOAT: 2017/6/12 to refresh activity 
                if (onRefreshUiListener != null) {
                    onRefreshUiListener.wifiConnect();
                }
                //context.startActivity(new Intent(context, RefreshWifiActivity.class));
                //getActivity().finish();
            }

        } else if (action.equals(MessageUti.USER_HEARTBEAT_REQUEST) || action.equals(MessageUti.USER_COMMON_ERROR_32604_REQUEST)) {
            if (response.isValid() && response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
                // TOAT: 2017/6/12 loginout 
                if (onLogoutListener != null) {
                    onLogoutListener.logouts();
                }
                // kickoffLogout();
            }
        } else if (action.equals(MessageUti.USER_LOGOUT_REQUEST)) {
            if (ok) {
                HomeActivity.m_blLogout = false;
                HomeActivity.m_blkickoff_Logout = false;
            }
            // TOAT: 2017/6/12  handleLoginError
            if (onLoginErrorListener != null) {
                onLoginErrorListener.loginError();
            }
            //handleLoginError(R.string.qs_title, R.string.login_kickoff_logout_successful, true, false);
        } else if (action.equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
            if (ok) {
                // TOAT: 2017/6/12 showSimcard 
                if (onSimListener != null) {
                    onSimListener.showSim();
                }
                // showSimCard(mBusinessMgr.getSimStatus());
            }
        } else if (action.equalsIgnoreCase(MessageUti.SIM_UNLOCK_PIN_REQUEST)) {
            if (ok) {
                //PIN解码成功
                // TOAT: 2017/6/12 pin unlocked sendAgainSuccess 
                if (onPinSuccessListener != null) {
                    onPinSuccessListener.pinSuccess();
                }

                // SetupWizardActivity.mRl_Success.setVisibility(View.VISIBLE);
                //
                // if (isRememberPassword) {
                //     SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, mPinPassword.getText().toString());
                // } else {
                //     SharedPrefsUtil.getInstance(getActivity()).putString(PIN_PASSWORD, "");
                // }
                //
                // //测试显示用的
                // String pinPassword = SharedPrefsUtil.getInstance(getActivity()).getString(PIN_PASSWORD, "");
                // if (!pinPassword.equals("")) {
                //     Toast.makeText(getActivity(), pinPassword, Toast.LENGTH_SHORT).show();
                // } else {
                //     Toast.makeText(getActivity(), "don't remember password", Toast.LENGTH_SHORT).show();
                // }
                //
                // //2秒后跳到主页面
                // Runnable filterRunnable = new Runnable() {
                //     @Override
                //     public void run() {
                //         Toast.makeText(getActivity(), "跳转到主页面中。。。", Toast.LENGTH_SHORT).show();
                //     }
                // };
                // mHandler.postDelayed(filterRunnable, 2000);
            } else {//PIN解码失败
                // TOAT: 2017/6/12 pin unlocked failed 
                if (onPinFailedListener != null) {
                    onPinFailedListener.pinFailed();
                }

                //PIN 的输入机会还有
                //                    pinRemainingTimes--;//由于是定时获取的sim卡状态，故手动自减一次机会
                //                    mPinPasswordDes.setTextColor(getResources().getColor(R.color.red));
                //                    if (pinRemainingTimes > 0) {
                //                        mWaitingContainer.setVisibility(View.GONE);
                //                        mHandlePinContainer.setVisibility(View.VISIBLE);
                //                        mHeaderSkipTv.setVisibility(View.VISIBLE);
                //                        mPasswordTimes.setText(pinRemainingTimes + "");
                //                        mPasswordTimes.setTextColor(getResources().getColor(R.color.red));
                //                    } else {
                //                        // PIN 输入机会用完
                //                        Toast.makeText(getApplicationContext(), "PIN码输入次数已经用完！！！", Toast.LENGTH_SHORT).show();
                //                    }

                // hideAllLayout();
                // mPinFailContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    /* --------------------------------------------------------- interface ----------------------------------------------- */
    
    // refresh
    public interface OnWifiConnectListener {
        void wifiConnect();
    }

    public void setOnWifiConnectListener(OnWifiConnectListener onRefreshUiListener) {
        this.onRefreshUiListener = onRefreshUiListener;
    }

    // logout
    public interface OnLogoutListener {
        void logouts();
    }

    public void setOnLogoutListener(OnLogoutListener OnLogoutListener) {
        onLogoutListener = OnLogoutListener;
    }

    // loginerror
    public interface OnLonginErrorListener {
        void loginError();
    }

    public void setOnLoginErrorListener(OnLonginErrorListener OnLonginErrorListener) {
        onLoginErrorListener = OnLonginErrorListener;
    }


    // sim ui
    public interface OnSimListener {
        void showSim();
    }

    public void setOnSimListener(OnSimListener OnSimListener) {
        onSimListener = OnSimListener;
    }

    // pin sendAgainSuccess
    public interface OnPinSuccessListener {
        void pinSuccess();
    }

    public void setOnPinSuccessListener(OnPinSuccessListener onPinSuccessListener) {
        this.onPinSuccessListener = onPinSuccessListener;
    }


    // pin failed
    public interface OnPinFailedListener {
        void pinFailed();
    }

    public void setOnPinFailedListener(OnPinFailedListener OnPinFailedListener) {
        onPinFailedListener = OnPinFailedListener;
    }

}
