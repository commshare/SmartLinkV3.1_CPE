package com.alcatel.smartlinkv3.ui.home.allsetup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.IDeviceChangeListener;
import com.alcatel.smartlinkv3.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.smartlinkv3.mediaplayer.util.ThumbnailLoader;
import com.alcatel.smartlinkv3.model.sim.SimStatus;
import com.alcatel.smartlinkv3.model.user.LoginState;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.ActivityNewSms;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.home.helper.cons.Cons;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;
import com.alcatel.smartlinkv3.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.smartlinkv3.ui.home.helper.utils.FraHomeHelper;
import com.alcatel.smartlinkv3.ui.home.helper.utils.FragmentHomeEnum;
import com.alcatel.smartlinkv3.ui.view.ViewMicroSD;

import org.cybergarage.upnp.Device;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.alcatel.smartlinkv3.R.drawable.tab_home_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_home_pre;
import static com.alcatel.smartlinkv3.R.drawable.tab_settings_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_settings_pre;
import static com.alcatel.smartlinkv3.R.drawable.tab_sms_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_sms_pre;
import static com.alcatel.smartlinkv3.R.drawable.tab_wifi_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_wifi_pre;
import static com.alcatel.smartlinkv3.R.string.main_setting;
import static com.alcatel.smartlinkv3.R.string.main_sms;
import static com.alcatel.smartlinkv3.R.string.wifi_settings;
import static com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity.LOGOUT_FLAG;

public class HomeActivity extends AppCompatActivity implements IDeviceChangeListener, View.OnClickListener {

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
    RelativeLayout mRlHomeMessagebutton;// sms按钮
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

    // dialog
    private LoginDialog m_loginDlg;
    private AutoForceLoginProgressDialog m_ForceloginDlg;
    private AutoLoginProgressDialog m_autoLoginDialog;
    private PinDialog m_dlgPin;
    private PukDialog m_dlgPuk;
    private ErrorDialog m_dlgError;

    private DMSDeviceBrocastFactory mBrocastFactory;
    private AllShareProxy mAllShareProxy;
    private ThumbnailLoader thumbnailLoader;

    /* -------------------------------------------- TEMP -------------------------------------------- */
    public static boolean m_blLogout;
    public static boolean m_blkickoff_Logout;
    private long mkeyTime; //点击2次返回键的时间
    private static Device mDevice;
    public static String PAGE_TO_VIEW_HOME = "com.alcatel.smartlinkv3.toPageViewHome";
    protected boolean m_bNeedBack = true;//whether need to back main activity.
    public static ActionBar supportActionBar;

    private OnTimerStatus onTimerStatus;
    private TimerHelper timerHelper;

    public static HomeActivity hac;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes);
        hac = this;
        // get action bar
        supportActionBar = getSupportActionBar();
        fm = getSupportFragmentManager();
        ButterKnife.bind(this);
        initView();
        initDialog();
        initUi();
        // 启动定时器
        startTimer();
    }

    /**
     * 心跳定时器
     */
    private void startTimer() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                // 检测sim状态
                // getAllStatus();
                getSmsCount();

                if (onTimerStatus != null) {
                    onTimerStatus.sendTimerFlag();
                }
            }
        };
        timerHelper.start(5000);
    }

    /**
     * 获取sms未读消息数量
     */
    private void getSmsCount() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() == Cons.READY) {
                    // 获取消息数
                    // TODO: 2017/6/23  获取消息数
                    // SmsCountHelper.setSmsCount(mTvHomeMessageCount);
                }

            }
        });
    }

    public interface OnTimerStatus {
        void sendTimerFlag();
    }

    public void setOnTimerStatus(OnTimerStatus onTimerStatus) {
        this.onTimerStatus = onTimerStatus;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mkeyTime) > 2000) {
            mkeyTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.home_exit_app, Toast.LENGTH_SHORT).show();
        } else {
            // 停止定时器
            timerHelper.stop();
            // m_wifiKeyView.revertWifiModeSetting();
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBtnState();
        toPageHomeWhenPinSimNoOk();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDialogs();
        mBrocastFactory.unRegisterListener();
        mAllShareProxy.exitSearch();
        thumbnailLoader.clearCache();
    }


    private void destroyDialogs() {
        m_dlgPin.destroyDialog();
        m_dlgPuk.destroyDialog();
        m_dlgError.destroyDialog();
        m_loginDlg.destroyDialog();
        m_ForceloginDlg.destroyDialog();
        m_autoLoginDialog.destroyDialog();
    }

    private void initView() {
        mTvHomeMessageCount = (TextView) findViewById(R.id.mTv_home_messageCount);
        SmsCountHelper.setSmsCount(mTvHomeMessageCount);// getInstance show sms count

        mAllShareProxy = AllShareProxy.getInstance(this);
        mBrocastFactory = new DMSDeviceBrocastFactory(this);
        mBrocastFactory.registerListener(this);
        thumbnailLoader = new ThumbnailLoader(this);
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
            case R.id.mTv_home_logout:// logout
                logout();
                break;
            case R.id.mIv_home_editSms:// edit message
                // to message send ui
                toSmsActivity();
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
            case R.id.mRl_home_messagebutton:// message button
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
     * A0.初始话必需的弹窗
     */
    public void initDialog() {
        m_dlgPin = PinDialog.getInstance(this);
        m_dlgPuk = PukDialog.getInstance(this);
        m_dlgError = ErrorDialog.getInstance(this);
        m_loginDlg = new LoginDialog(this);
        m_ForceloginDlg = new AutoForceLoginProgressDialog(this);
        m_autoLoginDialog = new AutoLoginProgressDialog(this);
    }

    /**
     * A1.登出
     */
    private void logout() {
        // 1.injust the login status flag
        // UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                if (result.getState() == Cons.LOGIN) {
                    setLogoutFlag(true);// set logout flag = true
                    SharedPrefsUtil.getInstance(HomeActivity.this).putBoolean(LOGOUT_FLAG, true);
                    // 2. logout action
                    API.get().logout(new MySubscriber() {
                        @Override
                        protected void onSuccess(Object result) {
                            ToastUtil_m.show(HomeActivity.this, getString(R.string.login_logout_successful));
                        }

                        @Override
                        protected void onFailure() {
                            ToastUtil_m.show(HomeActivity.this, getString(R.string.login_logout_failed));
                        }
                    });
                    // 3.injust is force login --> set to the main fragment
                    if (FeatureVersionManager.getInstance().isSupportForceLogin()) {
                        setActionBarUi(false, -1, false, false);
                        refreshUi_fragment(FragmentHomeEnum.MAIN);
                    }
                    // 4.reset the logout flag in CPEconfig
                    CPEConfig.getInstance().userLogout();
                    // 登出后--> 显示登陆界面
                    m_loginDlg.showDialog();
                }
            }
        });
    }

    /**
     * A2.切换到SMS编辑界面
     */
    private void toSmsActivity() {
        ChangeActivity.toActivity(this, ActivityNewSms.class, true, true, false, 0);
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

    /**
     * H3.刷新ui以及切换Fragment
     *
     * @param en 枚举
     */
    private void refreshUi_fragment(FragmentHomeEnum en) {
        // 0.actionbar ui
        refreshActionbar(en);
        // 1.groupbutton change
        setGroupButtonUi(en);
        // 2.transfer the fragment
        FraHomeHelper.commit(this, fm, R.id.mFl_home_container, en);
    }


    /* +++++++++++++++++++++++++++++++++++++++++++ action bar +++++++++++++++++++++++++++++++++++++++++++++ */

    /**
     * H6.设置action各项属性
     *
     * @param isshow
     */
    private void setActionBarUi(boolean isshow, int titleId, boolean isLogout, boolean isSMS) {
        // 1.clear the animation
        disableABCShowHideAnimation(supportActionBar);
        // 2.show attrs-self view
        supportActionBar.setDisplayShowCustomEnabled(isshow);
        if (isshow) {
            supportActionBar.setCustomView(R.layout.actionbarhome);
            supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            supportActionBar.show();
            findActionBarView(titleId, isLogout, isSMS);
        } else {
            supportActionBar.hide();
        }

    }

    /**
     * H7.Actionbar 视图元素
     *
     * @param titleId
     * @param isLogout
     * @param isSMS
     */
    private void findActionBarView(int titleId, boolean isLogout, boolean isSMS) {
        // 1.get view
        View customView = supportActionBar.getCustomView();
        // 2.find widget
        TextView tv_title = (TextView) customView.findViewById(R.id.mTv_home_Title);
        TextView tv_logout = (TextView) customView.findViewById(R.id.mTv_home_logout);
        ImageView iv_smsedit = (ImageView) customView.findViewById(R.id.mIv_home_editSms);
        // 3.set title
        tv_title.setText(titleId < 0 ? "" : getString(titleId));
        // 4.set right function ui 
        tv_logout.setVisibility(isLogout ? VISIBLE : GONE);
        iv_smsedit.setVisibility(isSMS ? VISIBLE : GONE);
        // 5.set right function click
        tv_logout.setOnClickListener(this);
        iv_smsedit.setOnClickListener(this);
    }

    /**
     * H8.更新Actionui
     *
     * @param en
     */
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

    /**
     * H9.消除ActionBar隐藏|显示时候的动画
     *
     * @param actionBar
     */
    public static void disableABCShowHideAnimation(ActionBar actionBar) {
        try {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        } catch (Exception exception) {
            try {
                Field mActionBarField = actionBar.getClass().getSuperclass().getDeclaredField("mActionBar");
                mActionBarField.setAccessible(true);
                Object icsActionBar = mActionBarField.get(actionBar);
                Field mShowHideAnimationEnabledField = icsActionBar.getClass().getDeclaredField("mShowHideAnimationEnabled");
                mShowHideAnimationEnabledField.setAccessible(true);
                mShowHideAnimationEnabledField.set(icsActionBar, false);
                Field mCurrentShowAnimField = icsActionBar.getClass().getDeclaredField("mCurrentShowAnim");
                mCurrentShowAnimField.setAccessible(true);
                mCurrentShowAnimField.set(icsActionBar, null);
            } catch (Exception e) {
            }
        }
    }

    /* +++++++++++++++++++++++++++++++++++++++++++ action bar +++++++++++++++++++++++++++++++++++++++++++++ */


    /* -------------------------------------------- TEMP -------------------------------------------- */
    /* -------------------------------------------- TEMP -------------------------------------------- */
    public static void setLogoutFlag(boolean blLogout) {
        m_blLogout = blLogout;
    }

    public static void setKickoffLogoutFlag(boolean blLogout) {
        m_blkickoff_Logout = blLogout;
    }

    private void updateBtnState() {
        //SimStatusModel simState = BusinessManager.getInstance().getSimStatus();
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                mRlHomeMessagebutton.setEnabled(result.getSIMState() == Cons.READY ? true : false);
            }
        });
    }

    private void toPageHomeWhenPinSimNoOk() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() != Cons.READY) {
                    refreshUi_fragment(FragmentHomeEnum.MAIN);
                    unlockSimBtnClick(false);
                }
            }
        });
    }

    public void unlockSimBtnClick(boolean blCancelUserClose) {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() == Cons.PIN_REQUIRED) {
                    if (blCancelUserClose) {
                        m_dlgPin.cancelUserClose();
                        m_dlgPuk.cancelUserClose();
                    }
                    ShowPinDialog();
                } else if (result.getSIMState() == Cons.PUK_REQUIRED) {
                    if (blCancelUserClose) {
                        m_dlgPin.cancelUserClose();
                        m_dlgPuk.cancelUserClose();
                    }
                    ShowPukDialog();
                }
            }
        });
    }

    public void ShowPinDialog() {
        // close PUK dialog
        if (null != m_dlgPuk && PukDialog.m_isShow) {
            m_dlgPuk.closeDialog();
        }

        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                // set the remain times
                if (null != m_dlgPin) {
                    m_dlgPin.updateRemainTimes(result.getPinRemainingTimes());
                }
                if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
                    if (!PinDialog.m_isShow) {
                        m_dlgPin.showDialog(result.getPinRemainingTimes(), () -> {
                            String strMsg = getString(R.string.pin_error_waring_title);
                            m_dlgError.showDialog(strMsg, () -> {
                                m_dlgPin.showDialog();
                            });
                        });
                    }
                }
            }
        });

    }

    public void ShowPukDialog() {
        // close PIN dialog
        if (null != m_dlgPin && PinDialog.m_isShow) {
            m_dlgPin.closeDialog();
        }

        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                // set the remain times
                if (null != m_dlgPuk) {
                    m_dlgPuk.updateRemainTimes(result.getPukRemainingTimes());
                }
                if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
                    if (!PukDialog.m_isShow) {
                        m_dlgPuk.showDialog(result.getPinRemainingTimes(), () -> {
                            String strMsg = getString(R.string.puk_error_waring_title);
                            m_dlgError.showDialog(strMsg, () -> {
                                m_dlgPuk.showDialog();
                            });
                        });
                    }
                }
            }
        });

    }

    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {
        updateDeviceList();
    }

    private void simRollRequest() {
        updateBtnState();
        SimStatusModel sim = BusinessManager.getInstance().getSimStatus();
        toPageHomeWhenPinSimNoOk();

        if (sim.m_SIMState == SIMState.PinRequired) {
            // close PUK dialog
            if (null != m_dlgPuk && PukDialog.m_isShow)
                m_dlgPuk.closeDialog();
            // set the remain times
            if (null != m_dlgPin)
                m_dlgPin.updateRemainTimes(sim.m_nPinRemainingTimes);

            if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
                if (!PinDialog.m_isShow) {
                    m_dlgPin.showDialog(sim.m_nPinRemainingTimes, new PinDialog.OnPINError() {
                        @Override
                        public void onPinError() {
                            String strMsg = getString(R.string.pin_error_waring_title);
                            m_dlgError.showDialog(strMsg, new ErrorDialog.OnClickBtnRetry() {
                                @Override
                                public void onRetry() {
                                    m_dlgPin.showDialog();
                                }
                            });
                        }
                    });
                } else {
                    m_dlgPin.onSimStatusReady(sim);
                }
            }
        } else if (sim.m_SIMState == SIMState.PukRequired) {// puk
            // close PIN dialog
            if (null != m_dlgPin && PinDialog.m_isShow)
                m_dlgPin.closeDialog();

            // set the remain times
            if (null != m_dlgPuk)
                m_dlgPuk.updateRemainTimes(sim.m_nPukRemainingTimes);

            if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
                if (!PukDialog.m_isShow) {
                    m_dlgPuk.showDialog(sim.m_nPukRemainingTimes, new PukDialog.OnPUKError() {

                        @Override
                        public void onPukError() {
                            String strMsg = getString(R.string.puk_error_waring_title);
                            m_dlgError.showDialog(strMsg, new ErrorDialog.OnClickBtnRetry() {

                                @Override
                                public void onRetry() {
                                    m_dlgPuk.showDialog();
                                }
                            });
                        }
                    });
                } else {
                    m_dlgPuk.onSimStatusReady(sim);
                }
            }
        } else {
            closePinAndPukDialog();
        }
    }

    private void closePinAndPukDialog() {
        if (m_dlgPin != null)
            m_dlgPin.closeDialog();

        if (m_dlgPuk != null)
            m_dlgPuk.closeDialog();

        if (m_dlgError != null)
            m_dlgError.closeDialog();
    }

    private String getServerAddress(Context ctx) {
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        return Formatter.formatIpAddress(dhcpInfo.gateway);
    }

    private void updateDeviceList() {
        List<Device> list = mAllShareProxy.getDMSDeviceList();
        String str1;
        String str2;
        Iterator<Device> iterator = list.iterator();


        for (Device tmp : list) {
            str1 = tmp.getLocation().substring(7);
            str2 = str1.substring(0, str1.indexOf(":"));
            if (str2.equalsIgnoreCase(getServerAddress(this))) {
                mDevice = tmp;
            }
        }

        mAllShareProxy.setDMSSelectedDevice(mDevice);
        Intent msdIntent = new Intent(ViewMicroSD.DLNA_DEVICES_SUCCESS);
        sendBroadcast(msdIntent);
    }
}
