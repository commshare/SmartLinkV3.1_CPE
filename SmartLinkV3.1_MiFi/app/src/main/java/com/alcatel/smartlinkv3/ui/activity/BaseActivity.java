package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.TimerHelper;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseActivity extends Activity {
    protected ActivityBroadcastReceiver m_msgReceiver;
    protected ActivityBroadcastReceiver m_msgReceiver2;
    private ArrayList<Dialog> m_dialogManager = new ArrayList<Dialog>();
    protected boolean m_bNeedBack = true;//whether need to back main activity.
    private TimerHelper timerHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startHeart();
        OtherUtils.contexts.add(this);
    }

    private void dismissAllDialog() {
        for (int i = 0; i < m_dialogManager.size(); i++) {
            m_dialogManager.get(i).dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_msgReceiver = new ActivityBroadcastReceiver();
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));

        m_msgReceiver2 = new ActivityBroadcastReceiver();
        if (FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") != true) {
            this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_LOGOUT_REQUEST));
        }
        this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_HEARTBEAT_REQUEST));
        this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_COMMON_ERROR_32604_REQUEST));

        // showActivity(this);
        if (FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") != true) {
            // backMainActivityOnResume(this);
        }
    }

    private void startHeart() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState result) {
                        API.get().heartBeat(new MySubscriber() {
                            @Override
                            protected void onSuccess(Object result) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.t("ma_rx").v(e.getMessage());
                                to(RefreshWifiActivity.class, true);
                            }

                            @Override
                            protected void onResultError(ResponseBody.Error error) {
                                Logger.t("ma_rx").v(error.getMessage());
                                // to(LoginRxActivity.class, true);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.t("ma_rx").v(e.getMessage());
                        to(RefreshWifiActivity.class, true);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        Logger.t("ma_rx").v(error.getMessage());
                        to(RefreshWifiActivity.class, true);
                    }
                });
            }
        };
        timerHelper.start(3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(m_msgReceiver);
            //checkLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHelper.stop();
        try {
            this.unregisterReceiver(m_msgReceiver2);
            //checkLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onBroadcastReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
                back2MainActivity(context);
            }
        } else if (intent.getAction().equals(MessageUti.USER_LOGOUT_REQUEST)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0 && isForeground(this)) {
                // backMainActivity(context);
            }
        } else if (intent.getAction().equals(MessageUti.USER_HEARTBEAT_REQUEST)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
                // backMainActivity(context);
                kickoffLogout();
            }
        } else if (intent.getAction().equals(MessageUti.USER_COMMON_ERROR_32604_REQUEST)) {
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (strErrorCode.equalsIgnoreCase(ErrorCode.ERR_COMMON_ERROR_32604) && isForeground(this)) {
                // backMainActivity(context);
                kickoffLogout();
            }
        }
    }

    private class ActivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceive(context, intent);
        }
    }

    private void back2MainActivity(Context context) {
        if (m_bNeedBack == false)
            return;
        boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();

        if (bCPEWifiConnected && sim.m_SIMState != SIMState.Accessable) {
            if (!this.getClass().getName().equalsIgnoreCase(MainActivity.class.getName())) {
                dismissAllDialog();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                finish();
            }
        }
    }

    // private void backMainActivityOnResume(Context context) {
    //     boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
    //     UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
    //
    //     if (bCPEWifiConnected && m_loginStatus != UserLoginStatus.login) {
    //         if (!this.getClass().getSimpleName().contains(MainActivity.class.getSimpleName())) {// 把主页过滤掉
    //             Intent intent = new Intent(context, LoginRxActivity.class);
    //             context.startActivity(intent);
    //             finish();
    //         }
    //     }
    // }

    // private void backMainActivity(Context context) {
    //     boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
    //     UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
    //
    //     if (bCPEWifiConnected && m_loginStatus != UserLoginStatus.login) {
    //         dismissAllDialog();
    //         if (!this.getClass().getSimpleName().contains(MainActivity.class.getSimpleName())) {// 把主页过滤掉
    //             Intent intent = new Intent(context, LoginRxActivity.class);
    //             context.startActivity(intent);
    //             finish();
    //         }
    //     }
    // }

    public boolean isForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void kickoffLogout() {
        UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
        if (m_loginStatus != null && m_loginStatus == UserLoginStatus.Logout) {
            //			MainActivity.setKickoffLogoutFlag(true);
            String strInfo = getString(R.string.login_kickoff_logout_successful);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
        }
    }

    public void toast(int resid) {
        ToastUtil_m.show(this, resid);
    }

    public void toast(String content) {
        ToastUtil_m.show(this, content);
    }

    public void to(Class target, boolean isFinish) {
        Intent intent = new Intent(this, target);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public String[] getStringArr(@ArrayRes int arrayId) {
        return getResources().getStringArray(arrayId);
    }

    public List<String> getStringList(@ArrayRes int resId) {
        return Arrays.asList(getStringArr(resId));
    }

    public Drawable getDrawabl(@DrawableRes int resId) {
        return getResources().getDrawable(resId);
    }
}
