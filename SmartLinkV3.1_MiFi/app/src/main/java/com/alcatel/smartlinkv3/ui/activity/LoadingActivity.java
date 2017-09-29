package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.common.CPEConfig;

@Deprecated
public class LoadingActivity extends Activity {
    private final int SPLASH_DISPLAY_INTERNAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getWindow().setBackgroundDrawable(null);
        goHome();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void goHome() {
        Thread searchingThread = new Thread() {
            @Override
            public void run() {
                if (!CPEConfig.getInstance().getInitialLaunchedFlag()) {
                    goGuide();// 引导页
                } else if (!checkConnectState()) {
                    searchTimeOut();
                } else if (!CPEConfig.getInstance().getQuickSetupFlag()) {
                    startQuickSetupActivity();// 快速设置
                } else {
                    startMainActivity();// 主页
                }
            }
        };

        new Handler().postDelayed(searchingThread, SPLASH_DISPLAY_INTERNAL);
    }

    private void goGuide() {
        Intent intent = new Intent(LoadingActivity.this, GuideActivity.class);
        LoadingActivity.this.startActivity(intent);
        LoadingActivity.this.finish();
    }

    private void startMainActivity() {
        Intent it = new Intent(this, MainActivity.class);
        this.startActivity(it);
        this.finish();
    }

    private void startRefreshWifiActivity() {
        Intent it = new Intent(this, RefreshWifiActivity.class);
        this.startActivity(it);
        finish();
    }

    private void startQuickSetupActivity() {
        Intent it = new Intent(this, QuickSetupActivity.class);
        startActivity(it);
        finish();
    }

    private void searchTimeOut() {
        startRefreshWifiActivity();
    }

    private boolean checkConnectState() {
        Log.v("pchong", "p11111  checkConnectState m_bCPEWifiConnected = " + DataConnectManager.getInstance().getCPEWifiConnected());
        return DataConnectManager.getInstance().getCPEWifiConnected();
    }
}
