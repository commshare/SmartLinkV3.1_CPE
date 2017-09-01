package com.alcatel.wifilink.ui.home.allsetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ShareperfrenceUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.ActivityNewSms;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.LoginActivity;
import com.alcatel.wifilink.ui.activity.PukUnlockActivity;
import com.alcatel.wifilink.ui.activity.RefreshWifiActivity;
import com.alcatel.wifilink.ui.activity.SimUnlockActivity;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.fragment.MainFragment;
import com.alcatel.wifilink.ui.home.fragment.SettingFragment;
import com.alcatel.wifilink.ui.home.fragment.SmsFragments;
import com.alcatel.wifilink.ui.home.fragment.WifiFragment;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.pop.SimPopHelper;
import com.alcatel.wifilink.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.wifilink.ui.home.helper.utils.FragmentHomeBucket;
import com.alcatel.wifilink.ui.home.helper.utils.FragmentHomeEnum;
import com.alcatel.wifilink.utils.ActionbarSetting;
import com.alcatel.wifilink.utils.AppInfo;
import com.alcatel.wifilink.utils.OtherUtils;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.alcatel.wifilink.R.drawable.tab_home_nor;
import static com.alcatel.wifilink.R.drawable.tab_home_pre;
import static com.alcatel.wifilink.R.drawable.tab_settings_nor;
import static com.alcatel.wifilink.R.drawable.tab_settings_pre;
import static com.alcatel.wifilink.R.drawable.tab_sms_nor;
import static com.alcatel.wifilink.R.drawable.tab_sms_pre;
import static com.alcatel.wifilink.R.drawable.tab_wifi_nor;
import static com.alcatel.wifilink.R.drawable.tab_wifi_pre;
import static com.alcatel.wifilink.R.id.mFl_home_container;
import static com.alcatel.wifilink.R.string.main_setting;
import static com.alcatel.wifilink.R.string.main_sms;
import static com.alcatel.wifilink.R.string.wifi_settings;

public class HomeActivity extends BaseActivityWithBack implements View.OnClickListener {

    @BindView(R.id.layout_main)
    RelativeLayout layoutMain;// 父布局

    @BindView(R.id.mLl_home_bottomGroup)
    LinearLayout mLlHomeBottomGroup;// 底部选项组

    @BindView(R.id.mRl_home_mainbutton)
    RelativeLayout mRlHomeHomebutton;// home按钮
    @BindView(R.id.mIv_home_main)
    ImageView mIv_home_main;

    @BindView(R.id.mRl_home_wifibutton)
    RelativeLayout mRlHomeWifibutton;// wifi按钮
    @BindView(R.id.mIv_home_wifi)
    ImageView mIv_home_wifi;

    @BindView(R.id.mTv_home_shareSD)
    TextView mTvHomeShareSD;// SD卡按钮

    @BindView(R.id.mRl_home_messagebutton)
    RelativeLayout mRl_smsButton;// sms按钮
    @BindView(R.id.mIv_home_message)
    ImageView mIv_home_message;
    //@BindView(R.id.mTv_home_messageCount)
    public static TextView mTvHomeMessageCount;// 消息数

    @BindView(R.id.mRl_home_settingbutton)
    RelativeLayout mRlHomeSettingbutton;// setting按钮
    @BindView(R.id.mIv_home_setting)
    ImageView mIv_home_setting;


    @BindView(R.id.mView_split_bottom)
    View mViewSplitBottom;// 底部分割线

    @BindView(mFl_home_container)
    FrameLayout mFlHomeContainer;/* 切换容器 */

    // group buttons
    int index = 0;
    private int[] press;
    private int[] normal;
    private FragmentManager fm;
    private ImageView[] groupButtons;

    /* -------------------------------------------- TEMP -------------------------------------------- */
    public static boolean m_blLogout;
    public static boolean m_blkickoff_Logout;
    private long mkeyTime; //点击2次返回键的时间
    private static Device mDevice;
    public static String PAGE_TO_VIEW_HOME = "com.alcatel.smartlinkv3.toPageViewHome";
    protected boolean m_bNeedBack = true;//whether need to back main activity.
    public ActionBar supportActionBar;

    public TimerHelper homeTimerHelper;// 全局定时器
    public static HomeActivity hac;

    /* action bar 的按钮 */
    private TextView barTitle;
    private RelativeLayout barLogout;
    private TextView tv_logout;
    private ImageView barSms;


    /* 弹出的按钮 */
    private PopupWindows simPop;
    private RippleView tv_cancel;
    private RippleView tv_unlock;

    private ActionbarSetting barSetting;
    private int container;
    private FragmentHomeEnum temp_en;
    private boolean isSimPop;// 是否允许弹窗
    private PopupWindow pop;

    public static String CURRENT_ACTIVITY = "";// 当前正在运行的activity
    private TimerHelper curActTimer;// 定时器:实时监控当前顶层的activity
    public static HashMap<Long, Integer> smsUnreadMap = new HashMap<>();// 未读消息缓冲集合
    private WindowManager windowManager;
    private View inflate;
    private TimerHelper logoutTimer;

    public static TimerTask autoTask;
    public static Timer autoTimer;//
    private TimerHelper heartBeatTimer;
    private String SP_PAGE_FILE = "SP_PAGE_FILE";

    List<Object> timerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerList = new ArrayList<>();
        SmartLinkV3App.getContextInstance().add(this);
        setContentView(R.layout.activity_homes);
        hac = this;
        supportActionBar = getSupportActionBar();
        fm = getSupportFragmentManager();
        isSimPop = true;
        ButterKnife.bind(this);
        initActionbar();
        initView();
        initUi();

        timerList.add(startTimer()); // 定时器在此处而不是在Onresume是为了防止界面重复刷新
        timerList.add(getCurrentActivity());// 定时获取当前位于顶层运行的ACTIVITY
        timerList.add(heartBeanTimer());// 心跳包发送
        timerList.add(autoLogoutTimer());// 启动定时退出计时器
        startAPPPackageService();// 后台服务: 检测当前APP是否被杀死
    }


    private void startAPPPackageService() {
        // 查看正在运行的服务
        boolean homeServiceWorked = OtherUtils.isServiceWork(this, HomeService.class);
        if (!homeServiceWorked) {// 指定的服务没有运行--> 创建(用于检测APP是否被杀死)
            Intent intent = new Intent(this, HomeService.class);
            startService(intent);
        }
    }

    /* 启动定时退出 */
    private Timer autoLogoutTimer() {
        autoTask = new TimerTask() {
            @Override
            public void run() {
                logout();
            }
        };
        autoTimer = new Timer();
        autoTimer.schedule(autoTask, Cons.AUTO_LOGOUT_PERIOD);
        return autoTimer;
    }

    /* **** heartBeanTimer:心跳包 **** */
    private TimerHelper heartBeanTimer() {
        // TODO: 2017/8/10 单点登陆--> 待FW确定
        heartBeatTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                API.get().heartBeat(new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        logout();
                    }
                });
            }
        };
        heartBeatTimer.start(Cons.PERIOD);
        return heartBeatTimer;
    }


    /* **** getCurrentActivity:循环获取当前顶层的ACTIVITY(用于辅助未读短信的判断) **** */
    private TimerHelper getCurrentActivity() {
        curActTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                CURRENT_ACTIVITY = AppInfo.getCurrentActivitySimpleName(HomeActivity.this);
            }
        };
        curActTimer.start(200);
        return curActTimer;
    }

    /* **** initActionbar **** */
    private void initActionbar() {
        barSetting = new ActionbarSetting() {
            @Override
            public void findActionbarView(View view) {
                barTitle = (TextView) view.findViewById(R.id.mTv_home_Title);
                barLogout = (RelativeLayout) view.findViewById(R.id.mRl_home_logout);
                tv_logout = (TextView) view.findViewById(R.id.tv_home_logout);
                barSms = (ImageView) view.findViewById(R.id.mIv_home_editSms);
                barLogout.setOnClickListener(HomeActivity.this);
                tv_logout.setOnClickListener(HomeActivity.this);
                barSms.setOnClickListener(HomeActivity.this);
            }
        };
        barSetting.settingActionbarAttr(this, supportActionBar, R.layout.actionbarhome);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mkeyTime) > 2000) {
            mkeyTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.home_exit_app, Toast.LENGTH_SHORT).show();
        } else {
            destroyOperate();/* 用户退出销毁定时器 */
            API.get().logout(new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                    OtherUtils.kill();
                    finish();
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    OtherUtils.kill();
                    finish();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        int page = getPage();
        // int page = SharedPrefsUtil.getInstance(this).getInt(Cons.PAGE, Cons.MAIN);
        // TODO: 2017/8/7 切换到对应的界面 
        setLastPage(page);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // heartBeatTimer.stop();
        clearAllTimer();
        timerList = null;
    }


    private void initView() {
        container = R.id.mFl_home_container;
        mTvHomeMessageCount = (TextView) findViewById(R.id.mTv_home_messageCount);
        if (MainFragment.type.equalsIgnoreCase(Cons.TYPE_SIM)) {
            SmsCountHelper.setSmsCount(this, mTvHomeMessageCount);// getInstance show sms count
        }
    }

    private void initUi() {
        // 1.getInstance button ui arrays
        initRes();
        // 3.其他界面返回时会触发该方法
        int page = getPage();
        setFirstPage(page);
    }

    private void setFirstPage(int page) {
        switch (page) {
            case Cons.MAIN:
                // 2.getInstance main button ui & refresh fragment
                // 首次commit
                refreshActionbar(FragmentHomeEnum.MAIN);
                setGroupButtonUi(FragmentHomeEnum.MAIN);
                Fragment mainFragment = new MainFragment(this);
                fm.beginTransaction().replace(container, mainFragment, FragmentHomeBucket.MAIN_FRA).commit();
                break;
            case Cons.WIFI:
                // 2.getInstance main button ui & refresh fragment
                // 首次commit
                refreshActionbar(FragmentHomeEnum.WIFI);
                setGroupButtonUi(FragmentHomeEnum.WIFI);
                Fragment wifiFragment = new WifiFragment();
                fm.beginTransaction().replace(container, wifiFragment, FragmentHomeBucket.WIFI_FRA).commit();
                break;
            case Cons.SMS:
                // 2.getInstance main button ui & refresh fragment
                // 首次commit
                refreshActionbar(FragmentHomeEnum.SMS);
                setGroupButtonUi(FragmentHomeEnum.SMS);
                Fragment smsfragment = new SmsFragments(this);
                fm.beginTransaction().replace(container, smsfragment, FragmentHomeBucket.SMS_FRA).commit();
                break;
            case Cons.SETTING:
                // 2.getInstance main button ui & refresh fragment
                // 首次commit
                refreshActionbar(FragmentHomeEnum.SETTING);
                setGroupButtonUi(FragmentHomeEnum.SETTING);
                Fragment settingfragment = new SettingFragment();
                fm.beginTransaction().replace(container, settingfragment, FragmentHomeBucket.SETTING_FRA).commit();
                break;
        }

    }

    public void afterSwitchLanguageReloadPage() {
        FragmentHomeBucket.afterSwitchLanguageReloadPage(this, fm, container);
        refreshActionbar(FragmentHomeEnum.SETTING);
        setGroupButtonUi(FragmentHomeEnum.SETTING);
    }

    /* action bar click */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* topbanner buttons */
            case R.id.tv_home_logout:
            case R.id.mRl_home_logout:// logout
                ShareperfrenceUtil.setSp(this, SP_PAGE_FILE, Cons.PAGE, Cons.MAIN);
                logout();
                break;
            case R.id.mIv_home_editSms:// edit message
                toSmsActivity();
                break;
            case R.id.tv_pop_sim_cancel:// sim pop cancel
                if (simPop != null) {
                    isSimPop = false;
                    popdismiss();
                }
                break;
            case R.id.tv_pop_sim_unlock:
                // check the sim is insert
                isSimInsert();
                break;
        }
    }


    /* bottom group click */
    @OnClick({R.id.mRl_home_mainbutton,// home
                     R.id.mRl_home_wifibutton,// wifi
                     R.id.mRl_home_messagebutton,// message
                     R.id.mRl_home_settingbutton})// setting
    public void onViewClicked(View view) {

        switch (view.getId()) {
            
            /* group groupButtons */
            case R.id.mRl_home_mainbutton:// home button
                refreshUi_fragment(FragmentHomeEnum.MAIN);
                break;
            case R.id.mRl_home_wifibutton:// wifi button
                refreshUi_fragment(FragmentHomeEnum.WIFI);
                break;
            case R.id.mRl_home_messagebutton:// sms button
                API.get().getSimStatus(new MySubscriber<SimStatus>() {
                    @Override
                    protected void onSuccess(SimStatus result) {
                        if (result.getSIMState() == Cons.READY && MainFragment.type.equalsIgnoreCase(Cons.TYPE_SIM)) {
                            refreshUi_fragment(FragmentHomeEnum.SMS);
                        }
                    }
                });
                break;
            case R.id.mRl_home_settingbutton:// setting button
                refreshUi_fragment(FragmentHomeEnum.SETTING);
                break;

        }

    }

    /* -------------------------------------------- BUSINESS -------------------------------------------- */
    /* -------------------------------------------- BUSINESS -------------------------------------------- */

    /**
     * A1.登出
     */
    private void logout() {
        // 2. logout action
        API.get().logout(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(HomeActivity.this, getString(R.string.login_logout_successful));
                // 3. when logout finish --> to the login Acitvity
                ChangeActivity.toActivity(HomeActivity.this, LoginActivity.class, false, true, false, 0);
            }

            @Override
            protected void onFailure() {
                ToastUtil_m.show(HomeActivity.this, getString(R.string.login_logout_failed));
            }
        });
    }

    /**
     * A2.切换到SMS编辑界面
     */
    private void toSmsActivity() {
        ChangeActivity.toActivity(this, ActivityNewSms.class, false, false, false, 0);
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */
    /* -------------------------------------------- HELPER -------------------------------------------- */

    /**
     * H1.初始化资源
     */
    private void initRes() {
        groupButtons = new ImageView[]{mIv_home_main, mIv_home_wifi, mIv_home_message, mIv_home_setting};
        press = new int[]{tab_home_pre, tab_wifi_pre, tab_sms_pre, tab_settings_pre};
        normal = new int[]{tab_home_nor, tab_wifi_nor, tab_sms_nor, tab_settings_nor};
    }

    /**
     * H2.根据列表设置被点击的元素背景
     *
     * @param enums 枚举
     */
    private void setGroupButtonUi(FragmentHomeEnum enums) {

        if (enums.equals(FragmentHomeEnum.MAIN)) {
            index = Cons.MAIN;
        } else if (enums.equals(FragmentHomeEnum.WIFI)) {
            index = Cons.WIFI;
        } else if (enums.equals(FragmentHomeEnum.SMS)) {
            index = Cons.SMS;
        } else if (enums.equals(FragmentHomeEnum.SETTING)) {
            index = Cons.SETTING;
        }

        for (int i = 0; i < groupButtons.length; i++) {
            groupButtons[i].setImageDrawable(getResources().getDrawable(i == index ? press[i] : normal[i]));
        }

    }

    /* **** 定时器 **** */
    private TimerHelper startTimer() {
        homeTimerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                checkWifi();
            }
        };
        homeTimerHelper.start(2000);
        return homeTimerHelper;
    }

    /* 检测WIFI是否有连接 */
    private void checkWifi() {
        boolean isWifi = OtherUtils.isWifiConnect(this);
        if (isWifi) {
            // is wan or sim
            isWanInsert();
        } else {
            ChangeActivity.toActivity(this, RefreshWifiActivity.class, false, true, false, 0);
        }
    }

    /* 刷新ui以及切换Fragment */
    private void refreshUi_fragment(FragmentHomeEnum en) {
        temp_en = en;
        // -1.set page flag
        setPageFlag(en);
        // 0.actionbar ui
        // 1.groupbutton change
        refreshActionbar(en);
        setGroupButtonUi(en);
        // 2.transfer the fragment
        FragmentHomeBucket.showOrHideFragment(this, fm, container, en);

    }

    /* 提交当前的fragment page标记 */
    private void setPageFlag(FragmentHomeEnum en) {
        switch (en) {
            case MAIN:
                ShareperfrenceUtil.setSp(this, SP_PAGE_FILE, Cons.PAGE, Cons.MAIN);
                break;
            case WIFI:
                ShareperfrenceUtil.setSp(this, SP_PAGE_FILE, Cons.PAGE, Cons.WIFI);
                break;
            case SMS:
                ShareperfrenceUtil.setSp(this, SP_PAGE_FILE, Cons.PAGE, Cons.SMS);
                break;
            case SETTING:
                ShareperfrenceUtil.setSp(this, SP_PAGE_FILE, Cons.PAGE, Cons.SETTING);
                break;
        }

    }

    /* **** ACTION BAR根据切换改变UI **** */
    private void refreshActionbar(FragmentHomeEnum en) {
        switch (en) {
            case MAIN:
                setActionBarUi(false, -1, false, false);
                break;
            case WIFI:
                setActionBarUi(true, wifi_settings, false, false);
                break;
            case SMS:
                setActionBarUi(true, main_sms, false, true);
                break;
            case SETTING:
                setActionBarUi(true, main_setting, true, false);
                break;

        }
    }

    /* **** ACTION BAR切换不同的标题和UI样式 **** */
    private void setActionBarUi(boolean isShowBar, int titleID, boolean isLogout, boolean isSms) {
        // show or hide
        if (isShowBar) {
            barSetting.showActionbar(supportActionBar);
            tv_logout.setText(R.string.log_out);
        } else {
            barSetting.hideActionbar(supportActionBar);
        }
        // title
        barTitle.setText(titleID < 0 ? "" : getString(titleID));
        // logout button effect?
        barLogout.setVisibility(isLogout ? VISIBLE : GONE);
        // is sms effect?
        barSms.setVisibility(isSms ? VISIBLE : GONE);
    }

    /* **** 获取SIM卡状态 **** */
    private void getSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                mRl_smsButton.setEnabled(simState == Cons.READY ? true : false);
                
                /* 需要输入PIN码 */
                if (simState == Cons.PIN_REQUIRED && isSimPop) {
                    // show pop to check pin
                    showSimPop();
                }

                if (simState == Cons.NOWN) {
                    isSimPop = true;
                }

                /* 需要输入PUK码 */
                if (simState == Cons.PUK_REQUIRED) {
                    ChangeActivity.toActivity(HomeActivity.this, PukUnlockActivity.class, false, false, false, 0);
                }
                
                /* SIM卡已经插入并且已经准备好 */
                if (simState == Cons.READY && simPop == null && simState != Cons.PIN_REQUIRED) {
                    // 获取消息数
                    SmsCountHelper.setSmsCount(HomeActivity.this, mTvHomeMessageCount);
                    // pop dismiss
                    if (simPop != null) {
                        popdismiss();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /* **** showSimPop: 显示SIM卡弹窗 **** */
    private void showSimPop() {

        if (pop == null) {
            simPop = new SimPopHelper() {
                @Override
                public void getView(View pop) {
                    tv_cancel = (RippleView) pop.findViewById(R.id.tv_pop_sim_cancel);
                    tv_unlock = (RippleView) pop.findViewById(R.id.tv_pop_sim_unlock);
                    tv_cancel.setOnClickListener(HomeActivity.this);
                    tv_unlock.setOnClickListener(HomeActivity.this);
                }
            }.showPop(this);
            pop = simPop.getPopupWindow();
        }
    }

    /* **** WAN口是否有效 **** */
    public void isWanInsert() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                int wanStatus = result.getStatus();
                if (wanStatus == Cons.CONNECTED) {// wan insert
                    if (simPop != null) {
                        popdismiss();
                    }
                } else {// sim insert
                    getSimStatus();
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });
    }

    /* **** SIM卡是否插入 **** */
    private void isSimInsert() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.PIN_REQUIRED) {
                    // sim pop to unlock activity
                    ChangeActivity.toActivity(HomeActivity.this, SimUnlockActivity.class, false, false, false, 0);
                    return;
                }
                if (simState == Cons.NOWN) {
                    ToastUtil_m.show(HomeActivity.this, getString(R.string.Home_no_sim));
                }

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });
    }

    /* 注销弹窗 */
    public void popdismiss() {
        simPop.dismiss();
        pop = null;
    }

    /**
     * 跳转到上一次界面(避免其他activity回来时总是跳到首页)
     *
     * @param page
     */
    private void setLastPage(int page) {
        switch (page) {
            case Cons.MAIN:
                refreshUi_fragment(FragmentHomeEnum.MAIN);
                break;
            case Cons.WIFI:
                refreshUi_fragment(FragmentHomeEnum.WIFI);
                break;
            case Cons.SMS:
                refreshUi_fragment(FragmentHomeEnum.SMS);
                break;
            case Cons.SETTING:
                refreshUi_fragment(FragmentHomeEnum.SETTING);
                break;
        }
    }

    private int getPage() {
        String sp = ShareperfrenceUtil.getSp(this, SP_PAGE_FILE, Cons.PAGE);
        return TextUtils.isEmpty(sp) ? Cons.MAIN : Integer.valueOf(sp);
    }

    private void destroyOperate() {
        // 复位page标记
        ShareperfrenceUtil.setSp(this, SP_PAGE_FILE, Cons.PAGE, Cons.MAIN);
        // 停止所有的定时器
        //homeTimerHelper.stop();
        // logoutTimer.stop();
        //curActTimer.stop();
        clearAllTimer();
    }

    /**
     * 清除全部定时器
     */
    public void clearAllTimer() {
        for (Object o : timerList) {
            if (o instanceof TimerHelper) {
                TimerHelper th = (TimerHelper) o;
                th.stop();
            }
            if (o instanceof Timer) {
                Timer t = (Timer) o;
                t.cancel();
                t.purge();
                t = null;
            }
        }
        timerList.clear();
    }
}
