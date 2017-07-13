package com.alcatel.wifilink.ui.home.allsetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.business.FeatureVersionManager;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.ActivityNewSms;
import com.alcatel.wifilink.ui.activity.LoginActivity;
import com.alcatel.wifilink.ui.activity.PukUnlockActivity;
import com.alcatel.wifilink.ui.activity.SimUnlockActivity;
import com.alcatel.wifilink.ui.home.fragment.MainFragment;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.pop.SimPopHelper;
import com.alcatel.wifilink.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.wifilink.ui.home.helper.utils.FraHomeHelper;
import com.alcatel.wifilink.ui.home.helper.utils.FragmentHomeEnum;
import com.alcatel.wifilink.utils.ActionbarSetting;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.ScreenSize;

import org.cybergarage.upnp.Device;

import java.lang.reflect.Field;

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
import static com.alcatel.wifilink.R.string.main_setting;
import static com.alcatel.wifilink.R.string.main_sms;
import static com.alcatel.wifilink.R.string.wifi_settings;
import static com.alcatel.wifilink.fileexplorer.FileSortHelper.SortMethod.size;
import static com.alcatel.wifilink.ui.activity.SettingAccountActivity.LOGOUT_FLAG;
import static com.alcatel.wifilink.ui.home.helper.main.ApiEngine.getSimStatus;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

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

    @BindView(R.id.mFl_home_container)
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

    private TimerHelper timerHelper;

    public static HomeActivity hac;

    /* action bar 的按钮 */
    private TextView barTitle;
    private RelativeLayout barLogout;
    private ImageView barSms;


    /* 弹出的按钮 */
    private PopupWindows simPop;
    private RippleView tv_cancel;
    private RippleView tv_unlock;
    private ActionbarSetting barSetting;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes);
        hac = this;
        supportActionBar = getSupportActionBar();
        fm = getSupportFragmentManager();
        ButterKnife.bind(this);
        initActionbar();
        initView();
        initUi();
    }

    /* **** initActionbar **** */
    private void initActionbar() {
        barSetting = new ActionbarSetting() {
            @Override
            public void findActionbarView(View view) {
                barTitle = (TextView) view.findViewById(R.id.mTv_home_Title);
                barLogout = (RelativeLayout) view.findViewById(R.id.mRl_home_logout);
                barSms = (ImageView) view.findViewById(R.id.mIv_home_editSms);
                barLogout.setOnClickListener(HomeActivity.this);
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
            OtherUtils.kill();
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start timer
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHelper.stop();
    }

    private void initView() {
        mTvHomeMessageCount = (TextView) findViewById(R.id.mTv_home_messageCount);
        SmsCountHelper.setSmsCount(this, mTvHomeMessageCount);// getInstance show sms count
    }

    private void initUi() {
        // 1.getInstance button ui arrays
        initRes();
        // 2.getInstance main button ui & refresh fragment
        refreshUi_fragment(FragmentHomeEnum.MAIN);
    }


    /* action bar click */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* topbanner buttons */
            case R.id.mRl_home_logout:// logout
                logout();
                break;
            case R.id.mIv_home_editSms:// edit message
                toSmsActivity();
                break;
            case R.id.tv_pop_sim_cancel:// sim pop cancel
                OtherUtils.kill();
                finish();
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
                        if (result.getSIMState() == Cons.READY) {
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
        // 1.injust the login status flag
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                if (result.getState() == Cons.LOGIN) {
                    // 2. logout action
                    API.get().logout(new MySubscriber() {
                        @Override
                        protected void onSuccess(Object result) {
                            ToastUtil_m.show(HomeActivity.this, getString(R.string.login_logout_successful));
                            // 3. when logout finish --> to the login Acitvity
                            ChangeActivity.toActivity(HomeActivity.this, LoginActivity.class, true, true, false, 0);
                        }

                        @Override
                        protected void onFailure() {
                            ToastUtil_m.show(HomeActivity.this, getString(R.string.login_logout_failed));
                        }
                    });
                }
            }
        });
    }

    /**
     * A2.切换到SMS编辑界面
     */
    private void toSmsActivity() {
        ChangeActivity.toActivity(this, ActivityNewSms.class, true, false, false, 0);
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
            index = 0;
        } else if (enums.equals(FragmentHomeEnum.WIFI)) {
            index = 1;
        } else if (enums.equals(FragmentHomeEnum.SMS)) {
            index = 2;
        } else if (enums.equals(FragmentHomeEnum.SETTING)) {
            index = 3;
        }

        for (int i = 0; i < groupButtons.length; i++) {
            groupButtons[i].setImageDrawable(getResources().getDrawable(i == index ? press[i] : normal[i]));
        }

    }

    /* **** 定时器 **** */
    private void startTimer() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                // is wan or sim
                isWanInsert();
            }
        };
        timerHelper.start(5000);
    }

    /* 刷新ui以及切换Fragment */
    private void refreshUi_fragment(FragmentHomeEnum en) {
        // 0.actionbar ui
        // 1.groupbutton change
        refreshActionbar(en);
        setGroupButtonUi(en);
        // 2.transfer the fragment
        FraHomeHelper.commit(this, fm, R.id.mFl_home_container, en);
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
        } else {
            barSetting.hideActionbar(supportActionBar);
        }
        // title
        barTitle.setText(titleID < 0 ? "" : getString(titleID));
        // logout button effect?
        barLogout.setVisibility(isLogout ? VISIBLE : GONE);
        barLogout.setClickable(isLogout);
        // is sms effect?
        barSms.setVisibility(isSms ? VISIBLE : GONE);
        barSms.setClickable(isSms);
    }

    /* **** 获取SIM卡状态 **** */
    private void getSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                mRl_smsButton.setEnabled(simState == Cons.READY ? true : false);
                
                /* 需要输入PIN码 */
                if (simState == Cons.PIN_REQUIRED) {
                    // show pop to check pin
                    showSimPop();
                }

                /* 需要输入PUK码 */
                if (simState == Cons.PUK_REQUIRED) {
                    ChangeActivity.toActivity(HomeActivity.this, PukUnlockActivity.class, true, false, false, 0);
                }
                
                /* SIM卡已经插入并且已经准备好 */
                if (simState == Cons.READY && simPop != null && simState != Cons.PIN_REQUIRED) {
                    // 获取消息数
                    SmsCountHelper.setSmsCount(HomeActivity.this, mTvHomeMessageCount);
                    // pop dismiss
                    if (simPop != null) {
                        simPop.dismiss();
                    }
                }
            }

            @Override
            public void onError(Throwable e){
                
            }
        });
    }

    /* **** showSimPop: 显示SIM卡弹窗 **** */
    private void showSimPop() {
        simPop = new SimPopHelper() {
            @Override
            public void getView(View pop) {
                tv_cancel = (RippleView) pop.findViewById(R.id.tv_pop_sim_cancel);
                tv_unlock = (RippleView) pop.findViewById(R.id.tv_pop_sim_unlock);
                tv_cancel.setOnClickListener(HomeActivity.this);
                tv_unlock.setOnClickListener(HomeActivity.this);
            }
        }.showPop(this);
    }

    /* **** WAN口是否有效 **** */
    public void isWanInsert() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                int wanStatus = result.getStatus();
                if (wanStatus == Cons.CONNECTING || wanStatus == Cons.CONNECTED) {// wan insert
                    if (simPop != null) {
                        simPop.dismiss();
                    }
                } else {// sim insert
                    getSimStatus();
                }
            }

            @Override
            public void onError(Throwable e){
                
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
                    ChangeActivity.toActivity(HomeActivity.this, SimUnlockActivity.class, true, false, false, 0);
                    return;
                }
                if (simState == Cons.NOWN) {
                    ToastUtil_m.show(HomeActivity.this, getString(R.string.Home_no_sim));
                }

            }
        });
    }

}
