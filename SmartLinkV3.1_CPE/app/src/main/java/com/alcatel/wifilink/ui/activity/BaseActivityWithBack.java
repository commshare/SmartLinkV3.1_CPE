package com.alcatel.wifilink.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.PreferenceUtil;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BaseActivityWithBack extends AppCompatActivity {

    private static final String TAG = "BaseActivityWithBack";
    private static final int LOG_OUT_DELAY = 5 * 60 * 1000;//5min中无操作 退到登录页
    private TimerTask mLogOutTask;
    private Timer mLogOutTimer;
    private BroadcastReceiver mScreenStateChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar baseActionBar = getSupportActionBar();
        if (baseActionBar != null) {
            baseActionBar.setHomeButtonEnabled(true);
            baseActionBar.setDisplayHomeAsUpEnabled(true);
            baseActionBar.setElevation(0);
        }
        Log.d(TAG,"onCreate");
        mScreenStateChangeReceiver = new ScreenStateChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenStateChangeReceiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 初始化PreferenceUtil
        PreferenceUtil.init(this);
        // 根据上次的语言设置，重新设置语言
        switchLanguage(PreferenceUtil.getString("language", "en"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
        resetTimerTask();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    /**
     * <切换语言>
     *
     * @param language
     * @see [类、类#方法、类#成员]
     */
    protected void switchLanguage(String language) {
        // 设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else if (language.equals("ar")) {
            // 阿拉伯语
            config.locale = new Locale("ar");
        } else if (language.equals("de")) {
            // 德语
            config.locale = Locale.GERMANY;
        } else if (language.equals("es")) {
            // 西班牙语
            config.locale = new Locale("es");
        } else if (language.equals("it")) {
            // 意大利语
            config.locale = Locale.ITALIAN;
        } else if (language.equals("fr")) {
            // 法语
            config.locale = Locale.FRENCH;
        }
        resources.updateConfiguration(config, dm);

        // 保存设置语言的类型
        PreferenceUtil.commitString("language", language);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_DOWN == ev.getAction()) {
            Log.e(TAG, "dispatchTouchEvent,action_down");
            resetTimerTask();
        }
        return super.dispatchTouchEvent(ev);
    }


    private void stopTimerTask() {
        if (mLogOutTask != null) {
            mLogOutTask.cancel();
            mLogOutTask = null;
        }
        if (mLogOutTimer != null) {
            mLogOutTimer.cancel();
            mLogOutTimer = null;
        }
    }

    private void resetTimerTask() {
        stopTimerTask();
        mLogOutTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("BaseActivityWithBack", "it time to log out!");
                getLogState();

            }
        };

        mLogOutTimer = new Timer();
        Log.e(TAG, "resetTimerTask: "+this);
        mLogOutTimer.schedule(mLogOutTask, LOG_OUT_DELAY);
    }


    class ScreenStateChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                Log.e(TAG, "action:" + action);
                switch (action) {
                    case Intent.ACTION_SCREEN_ON:
                        Log.e(TAG, "ACTION_SCREEN_ON");
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        getLogState();
                        Log.e(TAG, "ACTION_SCREEN_OFF");
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        Log.e(TAG, "ACTION_USER_PRESENT");
                        break;
                }
            }
        }
    }

    private void getLogState() {
        Log.e(TAG, "getLogState");
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                Log.e(TAG, "getLogState,onSuccess, state:" + result.getState());
                if (result.getState() == Cons.LOGIN) {//当前状态是登录状态
                    logout();
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.e(TAG, "getLogState,onResultError:");
            }

            @Override
            protected void onFailure() {
                Log.e(TAG, "getLogState,onFailure:");
                super.onFailure();
            }
        });
    }

    private void logout() {
        // 2. logout action
        Log.e(TAG,"logout");
        API.get().logout(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(BaseActivityWithBack.this, getString(R.string.login_logout_successful));
                stopTimerTask();
                // 3. when logout finish --> to the login Acitvity
                Intent logoutIntent = new Intent(BaseActivityWithBack.this, LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
            }

            @Override
            protected void onFailure() {
                ToastUtil_m.show(BaseActivityWithBack.this, getString(R.string.login_logout_failed));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if (mScreenStateChangeReceiver != null) {
            unregisterReceiver(mScreenStateChangeReceiver);
            mScreenStateChangeReceiver = null;
        }
    }
}
