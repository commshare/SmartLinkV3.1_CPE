package com.alcatel.smartlinkv3.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.model.user.LoginState;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.network.ResponseBody;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;

public class RefreshWifiActivity extends AppCompatActivity {
    private static final String TAG = "RefreshWifiActivity";
    private static final int MSG_REFRESHING = 0x1001;
    public static final int REFRESH_DELAY_MILLIS = 2000;

    private Button mRefreshBtn;
    private TextView mTipText;
    private ProgressBar mProgressBar;

    private boolean mIsRefreshing;

    private int mCount = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REFRESHING) {
                Log.d(TAG, "check wifi connected:" + isWifiConnected());
                if (isWifiConnected()) {
                    requestLoginState();
                } else {
                    Log.d(TAG, "mCount:" + mCount);
                    if (mCount < 5) {
                        mHandler.sendEmptyMessageDelayed(MSG_REFRESHING, REFRESH_DELAY_MILLIS);
                        mCount++;
                    } else {
                        mIsRefreshing = false;
                        updateRefreshingUI(false);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        mRefreshBtn = (Button) findViewById(R.id.btn_refresh);
        mTipText = (TextView) findViewById(R.id.tv_tip);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_refreshing);
        showGetConnectedDlg();
    }

    private void showGetConnectedDlg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.refresh_get_connected);
        builder.setMessage(R.string.refresh_manage_device_tips);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            gotoWifiSettings();
        });
        builder.create().show();
    }

    private void gotoWifiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void updateRefreshingUI(boolean refreshing) {
        mProgressBar.setVisibility(refreshing ? View.VISIBLE : View.GONE);
        mRefreshBtn.setVisibility(refreshing ? View.GONE : View.VISIBLE);
        mTipText.setVisibility(refreshing ? View.GONE : View.VISIBLE);
    }

    public void OnRefreshClick(View view) {
        if (mIsRefreshing) {
            return;
        }
        mIsRefreshing = true;
        updateRefreshingUI(true);
        WifiManager wifiManager = (WifiManager) SmartLinkV3App.getInstance().getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            mCount = 0;
            mHandler.sendEmptyMessageDelayed(MSG_REFRESHING, 2000);
        } else {
            requestLoginState();
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifiState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        return NetworkInfo.State.CONNECTED == wifiState;
    }


    private void requestLoginState() {
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                Log.d(TAG, "login state:" + result.getState());
                if (result.getState() == ENUM.UserLoginStatus.LOGIN.ordinal()) {
                    launchHomeActivity();
                } else {
                    launchLoginActivity(result.getLoginRemainingTimes());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError " + e);
                mIsRefreshing = false;
                updateRefreshingUI(false);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                mIsRefreshing = false;
                updateRefreshingUI(false);
            }
        });
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }


    private void launchLoginActivity(int remainTimes) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("remain_times", remainTimes);
        startActivity(intent);
        finish();
    }

}
