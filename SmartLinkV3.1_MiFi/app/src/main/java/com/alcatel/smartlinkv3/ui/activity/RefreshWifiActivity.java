package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.rx.ui.BaseRxActivity;
import com.alcatel.smartlinkv3.rx.ui.LoginRxActivity;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.TimerHelper;
import com.orhanobut.logger.Logger;

public class RefreshWifiActivity extends BaseRxActivity implements OnClickListener {
    private ImageView m_connectImage = null;
    private TextView m_connectTitle = null;
    private TextView m_connectTip = null;
    private Button m_connectBtn1 = null;

    protected MsgBroadcastReceiver m_msgReceiver;
    private TimerHelper loginStatusTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.t("ma_wifi").v(getClass().getSimpleName());
        OtherUtils.setWifiActive(this, true);// 开启wifi
        clearAllContext();// 清空所有Activity(除当前)
        setContentView(R.layout.activity_refresh);
        getWindow().setBackgroundDrawable(null);
        OtherUtils.verifyPermisson(this);// 申請權限
        m_connectImage = (ImageView) this.findViewById(R.id.image_connection);
        m_connectTitle = (TextView) this.findViewById(R.id.textview_refresh_title);
        m_connectTip = (TextView) this.findViewById(R.id.textview_refresh_tip);
        m_connectBtn1 = (Button) this.findViewById(R.id.btn_refresh);
        m_connectBtn1.setOnClickListener(this);
    }

    /**
     * 設備連接定時器
     */
    private void loginStateTimer() {
        loginStatusTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                // 檢查wifi是否連接
                boolean wiFiActive = OtherUtils.isWiFiActive(RefreshWifiActivity.this);
                if (wiFiActive) {// 檢查是否連接上設備
                    API.get().getLoginState(new MySubscriber<LoginState>() {
                        @Override
                        protected void onSuccess(LoginState result) {
                            Logger.t("ma_wifi").v(getClass().getSimpleName() + ":" + "loginStateTimer");
                            ChangeActivity.toActivity(RefreshWifiActivity.this, LoginRxActivity.class, false, true, false, 0);
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            System.out.println("loginStateTimer to login failed");
                        }
                    });
                }
            }
        };
        loginStatusTimer.start(3000);
    }

    private void stopTimer() {
        if (loginStatusTimer != null) {
            loginStatusTimer.stop();
        }
    }

    /**
     * 清空所有Activity(除当前)
     */
    private void clearAllContext() {
        if (SettingWifiActivity.isDone) {
            for (Activity context : OtherUtils.contexts) {
                if (context instanceof RefreshWifiActivity) {
                    continue;
                }
                if (!context.isFinishing()) {
                    context.finish();
                }
            }
            OtherUtils.contexts.clear();
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_refresh://m_connectBtn1
                clickBtn1();
                break;
            default:
                break;
        }
    }

    private void clickBtn1() {
        // api置空
        API.resetApi();
        wifiSetting();
    }


    private class MsgBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET)) {
                showUI();
            }
        }
    }

    private boolean isHaveWifi() {
        if (!DataConnectManager.getInstance().getWifiConnected()) {
            return false;
        }

        return true;
    }

    public boolean isNoAnyConnection() {
        if (!isHaveWifi()) {
            return true;
        }

        return false;
    }

    private void showUI() {
        if (isNoAnyConnection()) {
            m_connectImage.setBackgroundResource(R.drawable.no_connection);
            m_connectTitle.setText(R.string.refresh_wifi_title);
            m_connectTip.setText(R.string.refresh_wifi_tip);
            m_connectBtn1.setText(R.string.refresh);
        }
    }

    private void showActivity(Context context) {

        boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();

        if (!bCPEWifiConnected)
            return;

        Class<?> clazz = LoginRxActivity.class;
        if (!CPEConfig.getInstance().getQuickSetupFlag()) {
            clazz = QuickSetupActivity.class;
        } else {
            if (!SettingWifiActivity.isDone) {
                // clazz = MainActivity.class;
                clazz = LoginRxActivity.class;
                System.out.println("showActivity to login");
            }
        }

        Intent it = new Intent(this, clazz);
        startActivity(it);
        finish();
    }

    private void wifiSetting() {
        try {
            this.unregisterReceiver(m_msgReceiver);
        } catch (Exception e) {

        }

        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }

        this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_msgReceiver = new MsgBroadcastReceiver();
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SYSTEM_GET_FEATURES_ROLL_REQUSET));
        // showActivity(this);
        showUI();
        System.out.println("onResume");
        loginStateTimer();// 設備連接定時器
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OtherUtils.clearAllContext();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        try {
            this.unregisterReceiver(m_msgReceiver);
        } catch (Exception e) {

        }
    }
}
