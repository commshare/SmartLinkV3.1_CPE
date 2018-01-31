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
import android.view.MotionEvent;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.utils.SPUtils;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.ui.HomeRxActivity;
import com.alcatel.wifilink.rx.ui.LoginRxActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.AppInfo;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.PreferenceUtil;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BaseActivityWithBack extends AppCompatActivity {

    private static final String TAG = "BaseActivityWithBack";
    private TimerTask mLogOutTask;
    private Timer mLogOutTimer;
    private BroadcastReceiver screenLockReceiver;
    private TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmartLinkV3App.getContextInstance().add(this);
        ActionBar baseActionBar = getSupportActionBar();
        if (baseActionBar != null) {
            baseActionBar.setHomeButtonEnabled(true);
            baseActionBar.setDisplayHomeAsUpEnabled(true);
            baseActionBar.setElevation(0);
        }
        screenLockReceiver = new ScreenStateChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenLockReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 初始化PreferenceUtil
        PreferenceUtil.init(this);
        // 根据上次的语言设置，重新设置语言
        if (!"".equals(PreferenceUtil.getString(Constants.Language.LANGUAGE, ""))) {
            switchLanguage(PreferenceUtil.getString(Constants.Language.LANGUAGE, ""));
        }
        // 提交当前的activity
        SPUtils.getInstance(this).put(Cons.CURRENT_ACTIVITY, getClass().getSimpleName());
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
        if (language.equals(Constants.Language.ENGLISH)) {
            config.locale = Locale.ENGLISH;
        } else if (language.equals(Constants.Language.ARABIC)) {
            // 阿拉伯语
            config.locale = new Locale(Constants.Language.ARABIC);
        } else if (language.equals(Constants.Language.GERMENIC)) {
            // 德语
            config.locale = Locale.GERMANY;
        } else if (language.equals(Constants.Language.ESPANYOL)) {
            // 西班牙语
            config.locale = new Locale(Constants.Language.ESPANYOL);
        } else if (language.equals(Constants.Language.ITALIAN)) {
            // 意大利语
            config.locale = Locale.ITALIAN;
        } else if (language.equals(Constants.Language.FRENCH)) {
            // 法语
            config.locale = Locale.FRENCH;
        } else if (language.equals(Constants.Language.SERBIAN)) {
            // 塞尔维亚
            config.locale = new Locale(Constants.Language.SERBIAN);
        } else if (language.equals(Constants.Language.CROATIAN)) {
            // 克罗地亚
            config.locale = new Locale(Constants.Language.CROATIAN);
        } else if (language.equals(Constants.Language.SLOVENIAN)) {
            // 斯洛文尼亚
            config.locale = new Locale(Constants.Language.SLOVENIAN);
        }
        resources.updateConfiguration(config, dm);

        // 保存设置语言的类型
        PreferenceUtil.commitString(Constants.Language.LANGUAGE, language);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (MotionEvent.ACTION_DOWN == action || MotionEvent.ACTION_UP == action) {
            // 每次点击时候判断是否为以下三个界面--> 是则不启动定时器--> 只有进入Home界面才启用定时器
            OtherUtils.stopHomeTimer();
            if (isSpecialAc()) {
            } else {
                HomeRxActivity.autoLogoutTask = new TimerTask() {
                    @Override
                    public void run() {
                        logout(false);
                    }
                };
                HomeRxActivity.autoLogoutTimer = new Timer();
                HomeRxActivity.autoLogoutTimer.schedule(HomeRxActivity.autoLogoutTask, Cons.AUTO_LOGOUT_PERIOD);
                OtherUtils.homeTimerList.add(HomeRxActivity.autoLogoutTimer);
                OtherUtils.homeTimerList.add(HomeRxActivity.autoLogoutTask);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /* 是否为指定的三个界面--> true: 是 */
    public boolean isSpecialAc() {
        String currentActivity = AppInfo.getCurrentActivityName(this);
        boolean la = currentActivity.contains("LoadingRxActivity");
        boolean ga = currentActivity.contains("GuideRxActivity");
        boolean loa = currentActivity.contains("LoginRxActivity");
        if (la | ga | loa) {
            return true;
        } else {
            return false;
        }
    }

    class ScreenStateChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                switch (action) {
                    case Intent.ACTION_SCREEN_ON:
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        OtherUtils.stopHomeTimer();
                        if (!isSpecialAc()) {
                            logout(true);/* 锁屏后登出 */
                        } else {
                            OtherUtils.stopHomeTimer();
                        }
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        break;
                }
            }
        }
    }

    /* 登出 */

    /**
     * @param isLockScreen 是否为锁屏调用
     */
    private void logout(boolean isLockScreen) {
        // 如果为锁屏则现判断是否为登陆--> 如果为登入,此时允许调用登出接口
        RX.getInstant().getLoginState(new ResponseObject<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                if (result.getState() == Cons.LOGIN) {
                    requestLogout();
                }
            }
        });
    }

    private void requestLogout() {
        RX.getInstant().logout(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                CA.toActivity(BaseActivityWithBack.this, LoginRxActivity.class, false, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ToastUtil_m.show(BaseActivityWithBack.this, getString(R.string.login_logout_failed));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (screenLockReceiver != null) {
            unregisterReceiver(screenLockReceiver);
            screenLockReceiver = null;
        }
    }
}
