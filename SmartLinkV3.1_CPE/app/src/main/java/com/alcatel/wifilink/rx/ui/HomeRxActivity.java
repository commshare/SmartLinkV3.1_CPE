package com.alcatel.wifilink.rx.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.rx.helper.base.LogoutHelper;
import com.alcatel.wifilink.rx.helper.business.SmsHelper;
import com.alcatel.wifilink.rx.service.CheckService;
import com.alcatel.wifilink.rx.service.CheckServiceConnection;
import com.alcatel.wifilink.ui.activity.ActivityNewSms;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.allsetup.HomeService;
import com.alcatel.wifilink.ui.home.fragment.SettingFragment;
import com.alcatel.wifilink.ui.home.fragment.SmsFragments;
import com.alcatel.wifilink.ui.home.fragment.WifiFragment;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.AppInfo;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.FraHelpers;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.orhanobut.logger.Logger;
import com.zhy.android.percent.support.PercentFrameLayout;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeRxActivity extends BaseActivityWithBack {

    // 顶部banner
    @BindView(R.id.rl_homeRx_banner)
    public RelativeLayout rlBanner;
    @BindView(R.id.iv_homeRx_back)
    public ImageView ivBack;
    @BindView(R.id.tv_homeRx_title)
    public TextView tvTitle;
    @BindView(R.id.tv_homeRx_logout)
    public TextView tvLogout;
    @BindView(R.id.iv_homeRx_smsNew)
    public ImageView ivSmsNew;

    // fragment容器
    @BindView(R.id.fl_homeRx_container)
    PercentFrameLayout flContainer;

    // 底部navigation
    @BindView(R.id.ll_homeRx_navigation)
    public LinearLayout llNavigation;
    @BindView(R.id.ll_homeRx_home)
    PercentLinearLayout llHome;// HOME
    @BindView(R.id.iv_homeRx_tab_home)
    ImageView ivTabHome;
    @BindView(R.id.tv_homeRx_tab_home)
    public TextView tvTabHome;
    @BindView(R.id.ll_homeRx_wifi)
    PercentLinearLayout llWifi;// WIFI
    @BindView(R.id.iv_homeRx_tab_wifi)
    ImageView ivTabWifi;
    @BindView(R.id.tv_homeRx_tab_wifi)
    public TextView tvTabWifi;
    @BindView(R.id.ll_homeRx_sms)
    PercentRelativeLayout llSms;// SMS
    @BindView(R.id.iv_homeRx_tab_sms)
    ImageView ivTabSms;
    @BindView(R.id.tv_homeRx_tab_sms)
    public TextView tvTabSms;
    @BindView(R.id.tv_homeRx_smsDot)
    TextView tvSmsDot;
    @BindView(R.id.ll_homeRx_setting)
    PercentLinearLayout llSetting;// SETTING
    @BindView(R.id.iv_homeRx_tab_setting)
    ImageView ivTabSetting;
    @BindView(R.id.tv_homeRx_tab_setting)
    public TextView tvTabSetting;

    private long mkeyTime; //点击2次返回键的时间
    public int tabFlag = Cons.TAB_MAIN;
    private Drawable home_logo_pre;
    private Drawable home_logo_nor;
    private Drawable wifi_logo_pre;
    private Drawable wifi_logo_nor;
    private Drawable sms_logo_pre;
    private Drawable sms_logo_nor;
    private Drawable setting_logo_pre;
    private Drawable setting_logo_nor;
    private int blue_color;
    private int gray_color;
    private ImageView[] ivTabs;
    private TextView[] tvTabs;
    private Drawable[] logo_pres;
    private Drawable[] logo_nors;
    private int container;
    public Class[] clazz = {// 
            mainRxFragment.class,// main--> 0
            WifiFragment.class,// wifi--> 1
            SmsFragments.class,// sms--> 2
            SettingFragment.class,// setting--> 3
            PinRxFragment.class,// pin--> 4
            PukRxFragment.class,// puk--> 5
            UsageRxFragment.class,// puk--> 6
            MobileNetworkRxFragment.class, // mobile network--> 7
            SetDataPlanRxfragment.class, // set data plan--> 8
            WifiExtenderRxFragment.class,// wifi extender--> 9
            FeedbackFragment.class// feedback--> 10
    };
    public FraHelpers fraHelpers;
    private BoardSimHelper boardSimHelper;
    public static String CURRENT_ACTIVITY;// 当前运行的AC
    public static HashMap<Long, Integer> smsUnreadMap = new HashMap<>();// 未读消息缓冲集合

    public static TimerHelper heartTimer;
    private TimerHelper curActTimer;
    public static TimerTask autoLogoutTask;
    public static Timer autoLogoutTimer;
    private TimerHelper smsTimer;
    private Activity activity;
    private SmsHelper smsHelper;
    private Handler handler;
    private CheckServiceConnection checkServiceConn;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* 注意1: 重写这个方法并把super.onSaveInstanceState(outState);这个方法注销,避免该activity保存fragment状态 */
        /* 注意2: 该做法是为了防止fragment的重叠问题,因为此处的机制是采用了fragment的show or hide的方式来显示 */
        // super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        SmartLinkV3App.getContextInstance().add(this);
        setContentView(R.layout.activity_home_rx);
        ButterKnife.bind(this);
        startHeartTimer();
        startCurrentAcTimer();
        startHomeTimer();
        startServices();
        bindServiceToHomeActivity();// 与当前的activity绑定服务
        startSmsMessageTimer();
        initRes();
        initFragment();
    }

    /**
     * 与当前的activity绑定服务(用于homeservice的检测)
     */
    private void bindServiceToHomeActivity() {
        Intent checkService = new Intent(this, CheckService.class);
        checkServiceConn = new CheckServiceConnection();
        bindService(checkService, checkServiceConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TOAT: 此处需要重复调用一下刷新fragment的方法--> 否则的话无法切换回setting界面
        Logs.t("ma_home").vv("tabFlag: " + tabFlag);
        if (tabFlag == Cons.TAB_SETTING) {
            transferTabAndFragment(Cons.TAB_SETTING);
        }
    }

    private void initRes() {
        handler = new Handler();
        container = R.id.fl_homeRx_container;
        home_logo_pre = getResources().getDrawable(R.drawable.tab_home_pre);
        home_logo_nor = getResources().getDrawable(R.drawable.tab_home_nor);
        wifi_logo_pre = getResources().getDrawable(R.drawable.wifikey_pre);
        wifi_logo_nor = getResources().getDrawable(R.drawable.wifikey_nor);
        sms_logo_pre = getResources().getDrawable(R.drawable.tab_sms_pre);
        sms_logo_nor = getResources().getDrawable(R.drawable.tab_sms_nor);
        setting_logo_pre = getResources().getDrawable(R.drawable.tab_settings_pre);
        setting_logo_nor = getResources().getDrawable(R.drawable.tab_settings_nor);
        blue_color = getResources().getColor(R.color.mg_blue);
        gray_color = getResources().getColor(R.color.gray);
        ivTabs = new ImageView[]{ivTabHome, ivTabWifi, ivTabSms, ivTabSetting};// 控件
        tvTabs = new TextView[]{tvTabHome, tvTabWifi, tvTabSms, tvTabSetting};// 控件
        logo_pres = new Drawable[]{home_logo_pre, wifi_logo_pre, sms_logo_pre, setting_logo_pre};// 资源
        logo_nors = new Drawable[]{home_logo_nor, wifi_logo_nor, sms_logo_nor, setting_logo_nor};// 资源
        fraHelpers = new FraHelpers(this, clazz, clazz[0], container);// fragment辅助
    }

    private void initFragment() {
        transferTabAndFragment(Cons.TAB_MAIN);
    }

    @Override
    public void onBackPressed() {
        // backupPlan();
        if (!BackHandlerHelper.handleBackPress(this)) {
            // 主要的4个fragment
            if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                mkeyTime = System.currentTimeMillis();
                toast(R.string.home_exit_app);
            } else {
                logout();
            }
        }
    }

    private void backupPlan() {
        if (tabFlag == Cons.TAB_PIN || tabFlag == Cons.TAB_PUK) {
            int anInt = SP.getInstance(this).getInt(Cons.TAB_FRA, Cons.TAB_MAIN);
            fraHelpers.transfer(clazz[anInt]);
            return;
        }
        if (tabFlag == Cons.TAB_USAGE) {
            fraHelpers.transfer(clazz[Cons.TAB_MAIN]);
            return;
        }
        if (tabFlag == Cons.TAB_MOBILE_NETWORK) {
            fraHelpers.transfer(clazz[Cons.TAB_SETTING]);
            return;
        }
        // 主要的4个fragment
        if ((System.currentTimeMillis() - mkeyTime) > 2000) {
            mkeyTime = System.currentTimeMillis();
            toast(R.string.home_exit_app);
        } else {
            logout();
        }
    }

    /**
     * 切换语言后重新加载
     */
    public void reloadFragment(Class targetClass) {
        fraHelpers.reload(targetClass);
    }

    /**
     * 退出
     */
    private void logout() {
        OtherUtils.clearAllTimer();
        new LogoutHelper(this) {
            @Override
            public void logoutFinish() {
                to(LoginRxActivity.class, true);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.t("ma_permission").vv("home destroy");
        unbindService(checkServiceConn);
        OtherUtils.stopHeartBeat(heartTimer);
        OtherUtils.clearAllTimer();
    }

    /**
     * 启动心跳定时器
     */
    private void startHeartTimer() {
        heartTimer = OtherUtils.startHeartBeat(this, RefreshWifiRxActivity.class, LoginRxActivity.class);
    }

    /**
     * 启动检测当前UI定时器
     */
    private void startCurrentAcTimer() {
        curActTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                CURRENT_ACTIVITY = AppInfo.getCurrentActivitySimpleName(HomeRxActivity.this);
            }
        };
        curActTimer.start(200);
        // TOGO 2018/1/5 0005 注意: 如果添加该段代码后出现了bug,请注销
        OtherUtils.timerList.add(curActTimer);
    }

    /**
     * 启动自动计时定时器
     */
    private void startHomeTimer() {
        autoLogoutTask = new TimerTask() {
            @Override
            public void run() {
                logout();
            }
        };
        autoLogoutTimer = new Timer();
        autoLogoutTimer.schedule(autoLogoutTask, Cons.AUTO_LOGOUT_PERIOD);
        OtherUtils.homeTimerList.add(autoLogoutTask);
        OtherUtils.homeTimerList.add(autoLogoutTimer);
    }

    /**
     * 启动后台服务,检测APP是否处于前台进程
     */
    private void startServices() {
        // 查看正在运行的服务
        boolean homeServiceWorked = OtherUtils.isServiceWork(this, HomeService.class);
        if (!homeServiceWorked) {// 指定的服务没有运行--> 创建(用于检测APP是否被杀死)
            Intent intent = new Intent(this, HomeService.class);
            startService(intent);
            Map<Activity, Intent> mapservice = new HashMap<>();
            mapservice.put(this, intent);
            OtherUtils.timerList.add(mapservice);
        }
    }

    /**
     * 启动SMS短信通知定时器
     */
    private void startSmsMessageTimer() {
        smsTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                Logger.v("ma_check:" + "smsTimer");
                if (smsHelper == null) {
                    smsHelper = new SmsHelper(activity);
                }
                smsHelper.setOnUnreadListener(unreadCount -> showUnreadDot(unreadCount > 0 ? true : false));
                smsHelper.setOnNownListener(simStatus -> showUnreadDot(false));
                smsHelper.setOnResultErrorListener(error -> showUnreadDot(false));
                smsHelper.setOnErrorListener(e -> showUnreadDot(false));
                smsHelper.getUnread();
            }
        };
        smsTimer.start(3000);
        OtherUtils.timerList.add(smsTimer);
    }

    /**
     * 显示未读小圆点
     *
     * @param isUnread
     */
    private void showUnreadDot(boolean isUnread) {
        tvSmsDot.setVisibility(isUnread ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示消息提示小数点
     *
     * @param isDotShow
     */
    private void smsDot(boolean isDotShow) {
        tvSmsDot.setVisibility(isDotShow ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.iv_homeRx_back,// 点击回退(用于PIN|PUK界面)
                     R.id.tv_homeRx_logout,// 点击退出
                     R.id.iv_homeRx_smsNew,// 新建短信
                     R.id.ll_homeRx_home,// 导航1
                     R.id.ll_homeRx_wifi,// 导航2
                     R.id.ll_homeRx_sms,// 导航3
                     R.id.ll_homeRx_setting})// 导航4
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_homeRx_back:// 回退(用于PIN|PUK界面)
                int position = SP.getInstance(this).getInt(Cons.TAB_FRA, Cons.TAB_MAIN);
                transferTabAndFragment(position);
                break;
            case R.id.tv_homeRx_logout:// 登出
                logout();
                break;
            case R.id.iv_homeRx_smsNew:// 前往新建短信界面
                to(ActivityNewSms.class, false);
                break;
            case R.id.ll_homeRx_home:// HOME
                transferTabAndFragment(Cons.TAB_MAIN);
                break;
            case R.id.ll_homeRx_wifi:// WIFI
                transferTabAndFragment(Cons.TAB_WIFI);
                break;
            case R.id.ll_homeRx_sms:// SMS
                clickSmsTab();
                break;
            case R.id.ll_homeRx_setting:// SETTING
                ProgressDialog pgds = OtherUtils.showProgressPop(this);
                handler.postDelayed(() -> {
                    OtherUtils.hideProgressPop(pgds);
                    transferTabAndFragment(Cons.TAB_SETTING);
                }, 1000);
                break;
        }
    }

    /**
     * 点击了SMS TAB的逻辑
     */
    private void clickSmsTab() {
        boardSimHelper = new BoardSimHelper(this);
        boardSimHelper.setOnSimReadyListener(result -> transferTabAndFragment(Cons.TAB_SMS));
        boardSimHelper.setOnNownListener(simStatus -> toast(R.string.not_inserted));
        boardSimHelper.boardNormal();
    }

    /**
     * 切换底部导航栏+fragment
     *
     * @param tabFlag TAB角标
     */
    public void transferTabAndFragment(int tabFlag) {
        // 1.切换图标
        for (int i = 0; i < ivTabs.length; i++) {
            ivTabs[i].setImageDrawable(i == tabFlag ? logo_pres[i] : logo_nors[i]);
        }
        // 2.切换文字颜色
        for (int i = 0; i < tvTabs.length; i++) {
            tvTabs[i].setTextColor(i == tabFlag ? blue_color : gray_color);
        }
        // 4.切换其他UI
        transferUi(tabFlag);
    }

    /**
     * 切换其他UI
     *
     * @param tabFlag
     */
    public void transferUi(int tabFlag) {
        // 1.切换标题栏
        rlBanner.setVisibility(tabFlag == Cons.TAB_MAIN || tabFlag == Cons.TAB_SETTING ? View.GONE : View.VISIBLE);
        ivBack.setVisibility(tabFlag == Cons.TAB_PIN | tabFlag == Cons.TAB_PUK ? View.VISIBLE : View.GONE);
        tvLogout.setVisibility(tabFlag == Cons.TAB_SETTING ? View.VISIBLE : View.GONE);
        ivSmsNew.setVisibility(tabFlag == Cons.TAB_SMS ? View.VISIBLE : View.GONE);
        // 2.切换fragment
        fraHelpers.transfer(clazz[tabFlag]);
        // 3.显示标题栏文本
        if (tabFlag == Cons.TAB_WIFI) {
            tvTitle.setText(getString(R.string.wifi_settings));
        } else if (tabFlag == Cons.TAB_SMS) {
            tvTitle.setText(getString(R.string.sms_title));
        } else if (tabFlag == Cons.TAB_SETTING) {
            tvTitle.setText(getString(R.string.main_setting));
        } else if (tabFlag == Cons.TAB_PIN) {
            tvTitle.setText(getString(R.string.IDS_PIN_LOCKED));
        } else if (tabFlag == Cons.TAB_PUK) {
            tvTitle.setText(getString(R.string.IDS_PUK_LOCKED));
        }
    }

    public void toast(int resId) {
        ToastUtil_m.show(this, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(this, content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(this, ac, false, isFinish, false, 0);
    }
}
