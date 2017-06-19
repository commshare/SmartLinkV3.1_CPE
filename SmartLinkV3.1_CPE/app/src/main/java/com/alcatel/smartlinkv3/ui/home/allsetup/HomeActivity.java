package com.alcatel.smartlinkv3.ui.home.allsetup;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
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
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.LinkAppSettings;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.IDeviceChangeListener;
import com.alcatel.smartlinkv3.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.smartlinkv3.mediaplayer.util.ThumbnailLoader;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.ActivityNewSms;
import com.alcatel.smartlinkv3.ui.activity.BaseActivity;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.smartlinkv3.ui.home.helper.utils.FraHomeHelper;
import com.alcatel.smartlinkv3.ui.home.helper.utils.FragmentHomeEnum;
import com.alcatel.smartlinkv3.ui.view.ViewMicroSD;

import org.cybergarage.upnp.Device;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.alcatel.smartlinkv3.R.drawable.tab_home_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_home_pre;
import static com.alcatel.smartlinkv3.R.drawable.tab_settings_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_settings_pre;
import static com.alcatel.smartlinkv3.R.drawable.tab_sms_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_sms_pre;
import static com.alcatel.smartlinkv3.R.drawable.tab_wifi_nor;
import static com.alcatel.smartlinkv3.R.drawable.tab_wifi_pre;
import static com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import static com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity.LOGOUT_FLAG;

public class HomeActivity extends BaseActivity implements IDeviceChangeListener {

    @BindView(R.id.layout_main)
    RelativeLayout layoutMain;// 父布局

    @BindView(R.id.mRl_home_topbanner)
    RelativeLayout mRlHomeTopbanner;// 顶部栏
    @BindView(R.id.mTv_home_Title)
    TextView mTvHomeTitle;// 标题
    @BindView(R.id.mTv_home_logout)
    TextView mTvHomeLogout;// Logout
    @BindView(R.id.mIv_home_editMessage)
    ImageView mIvHomeEditMessage;// sms edit ui按钮

    @BindView(R.id.mView_split_top)
    View mViewSplitTop;// 顶部分割线

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_homes);
        fm = getSupportFragmentManager();
        ButterKnife.bind(this);
        initView();
        initDialog();
        initUi();
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
            // m_wifiKeyView.revertWifiModeSetting();
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PIN_REQUEST));
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PUK_REQUEST));
        this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_LOGOUT_REQUEST));
        this.registerReceiver(m_msgReceiver2, new IntentFilter(PAGE_TO_VIEW_HOME));

        this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));

        registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));
        registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
        registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET));

        updateBtnState();
        toPageHomeWhenPinSimNoOk();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(m_msgReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDialogs();
        mBrocastFactory.unRegisterListener();
        mAllShareProxy.exitSearch();
        thumbnailLoader.clearCache();
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        super.onBroadcastReceive(context, intent);

        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();
        super.onBroadcastReceive(context, intent);

        if (MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET.equals(action)) {
            if (ok) {
                simRollRequest();
            }
        } else if (MessageUti.SIM_UNLOCK_PIN_REQUEST.equals(action)) {
            m_dlgPin.onEnterPinResponse(ok);
        } else if (MessageUti.SIM_UNLOCK_PUK_REQUEST.equals(action)) {
            m_dlgPuk.onEnterPukResponse(ok);
        }

        if (PAGE_TO_VIEW_HOME.equals(action)) {
            refreshUi_fragment(FragmentHomeEnum.MAIN);
        }

        if (MessageUti.USER_LOGOUT_REQUEST.equals(action)) {
            if (ok) {
                if (m_blLogout) {
                    String strInfo = getString(R.string.login_logout_successful);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }

                if (m_blkickoff_Logout) {
                    String strInfo = getString(R.string.login_kickoff_logout_successful);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }
                m_blLogout = false;
                m_blkickoff_Logout = false;
            }
        }

        if (MessageUti.SHARING_GET_DLNA_SETTING_REQUSET.equals(action)) {
            if (ok) {
                if (BusinessManager.getInstance().getDlnaSettings().getDlnaStatus() > 0) {
                    mAllShareProxy.startSearch();
                } else {
                    mAllShareProxy.exitSearch();
                }

            }
        }

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
        SmsCountHelper.setSmsCount(mTvHomeMessageCount);// init show sms count

        mAllShareProxy = AllShareProxy.getInstance(this);
        mBrocastFactory = new DMSDeviceBrocastFactory(this);
        mBrocastFactory.registerListener(this);
        thumbnailLoader = new ThumbnailLoader(this);
    }

    private void initUi() {
        // 1.init button ui arrays
        initRes();
        // 2.set topbanner
        setTopBannerUi(false, "", false, false);
        // 3.init main button ui & refresh fragment
        refreshUi_fragment(FragmentHomeEnum.MAIN);
    }

    @OnClick({R.id.mTv_home_logout,// logout
                     R.id.mIv_home_editMessage,// message edit
                     R.id.mRl_home_mainbutton,// home
                     R.id.mRl_home_wifibutton,// wifi
                     R.id.mRl_home_messagebutton,// message
                     R.id.mRl_home_settingbutton})// setting
    public void onViewClicked(View view) {

        switch (view.getId()) {
            /* topbanner buttons */
            case R.id.mTv_home_logout:// logout
                logout();
                break;
            case R.id.mIv_home_editMessage:// edit message
                // to message send ui
                navigateAfterLogin(this::toSmsActivity);
                break;
            
            /* group groupButtons */
            case R.id.mRl_home_mainbutton:// home button
                setTopBannerUi(false, "", false, false);
                refreshUi_fragment(FragmentHomeEnum.MAIN);
                break;
            case R.id.mRl_home_wifibutton:// wifi button
                setTopBannerUi(true, getString(R.string.wifi_settings), false, false);
                // if login --> then go to wifi fragment
                navigateAfterLogin(() -> {
                    refreshUi_fragment(FragmentHomeEnum.WIFI);
                });

                break;
            case R.id.mRl_home_messagebutton:// message button
                setTopBannerUi(true, getString(R.string.main_sms), true, false);
                // if login --> then sim card is effect then go to sms fragment
                navigateAfterLogin(() -> {
                    SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
                    if (simStatus.m_SIMState == SIMState.Accessable) {
                        refreshUi_fragment(FragmentHomeEnum.MESSAGE);
                    }
                });

                break;
            case R.id.mRl_home_settingbutton:// setting button
                setTopBannerUi(true, getString(R.string.main_setting), false, true);
                // if login --> then go to the setting fragment
                navigateAfterLogin(() -> {
                    refreshUi_fragment(FragmentHomeEnum.SETTING);
                });
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
        UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
        if (m_loginStatus != null && m_loginStatus == UserLoginStatus.LOGIN) {
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
                setTopBannerUi(false, "", false, false);
                Intent intent2 = new Intent(HomeActivity.PAGE_TO_VIEW_HOME);
                this.sendBroadcast(intent2);
            }
            // 4.reset the logout flag in CPEconfig
            CPEConfig.getInstance().userLogout();
        }
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
        } else if (enums.equals(FragmentHomeEnum.MESSAGE)) {
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
        // 1.groupbutton change
        setGroupButtonUi(en);
        // 2.transfer the fragment
        FraHomeHelper.commit(fm, R.id.mFl_home_container, en);
    }

    /**
     * H4.顶部栏是否显示 & 显示的标题
     *
     * @param isTopBannerVisible
     * @param title
     */
    private void setTopBannerUi(boolean isTopBannerVisible, String title, boolean isMessageLogo, boolean isLogOutLogo) {
        mRlHomeTopbanner.setVisibility(isTopBannerVisible ? View.VISIBLE : View.GONE);// 顶部总布局
        mTvHomeTitle.setText(title);// title
        mIvHomeEditMessage.setVisibility(isMessageLogo ? View.VISIBLE : View.GONE);// message edit
        mTvHomeLogout.setVisibility(isLogOutLogo ? View.VISIBLE : View.GONE);// logout
    }

    /**
     * H5.登陆后的操作
     *
     * @param listener
     */
    public void navigateAfterLogin(OnLoginFinishedListener listener) {
        if (LinkAppSettings.isLoginSwitchOff()) {
            listener.onLoginFinished();
            return;
        }

        UserLoginStatus status = BusinessManager.getInstance().getLoginStatus();

        if (status == UserLoginStatus.LOGIN) {
            listener.onLoginFinished();
            return;
        } else if (status != UserLoginStatus.Logout) {
            CommonErrorInfoDialog m_dialog_timeout_info = CommonErrorInfoDialog.getInstance(this);
            m_dialog_timeout_info.showDialog(getString(R.string.other_login_warning_title), getString(R.string.login_login_time_used_out_msg));
            return;
        }

        m_autoLoginDialog.autoLoginAndShowDialog(new AutoLoginProgressDialog.OnAutoLoginFinishedListener() {
            public void onLoginSuccess() {
                listener.onLoginFinished();
            }

            public void onLoginFailed(String error_code) {
                if (error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
                    m_loginDlg.showTimeout();
                    return;
                } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD)) {
                    ErrorDialog.getInstance(getBaseContext()).showDialog(R.string.login_psd_error_msg, () -> m_loginDlg.showDialog(listener));
                    return;
                } else if (!error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
                    return;
                }

                if (!FeatureVersionManager.getInstance().isSupportForceLogin()) {
                    m_loginDlg.showOtherLogin();
                    return;
                }

                ForceLoginSelectDialog.getInstance(HomeActivity.this).showDialog(() -> m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
                    public void onLoginSuccess() {
                        listener.onLoginFinished();
                    }

                    public void onLoginFailed(String error_code) {
                        if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD)) {
                            SmartLinkV3App.getInstance().setForcesLogin(true);
                            ErrorDialog.getInstance(HomeActivity.this).
                                                                              showDialog(R.string.login_psd_error_msg, () -> m_loginDlg.showDialog(listener));
                        } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                            m_loginDlg.showTimeout();
                        }
                    }
                }));
            }

            @Override
            public void onFirstLogin() {
                m_loginDlg.showDialog(listener);
            }
        });
    }

    /* -------------------------------------------- TEMP -------------------------------------------- */
    /* -------------------------------------------- TEMP -------------------------------------------- */
    public static void setLogoutFlag(boolean blLogout) {
        m_blLogout = blLogout;
    }

    public static void setKickoffLogoutFlag(boolean blLogout) {
        m_blkickoff_Logout = blLogout;
    }

    private void updateBtnState() {
        SimStatusModel simState = BusinessManager.getInstance().getSimStatus();
        if (simState.m_SIMState == SIMState.Accessable) {
            mRlHomeMessagebutton.setEnabled(true);
        } else {
            mRlHomeMessagebutton.setEnabled(false);
        }
    }

    private void toPageHomeWhenPinSimNoOk() {
        SimStatusModel simState = BusinessManager.getInstance().getSimStatus();
        if (simState.m_SIMState != SIMState.Accessable) {
            setTopBannerUi(false, "", false, false);
            refreshUi_fragment(FragmentHomeEnum.MAIN);
            unlockSimBtnClick(false);
        }
    }

    public void unlockSimBtnClick(boolean blCancelUserClose) {
        SimStatusModel sim = BusinessManager.getInstance().getSimStatus();
        if (SIMState.PinRequired == sim.m_SIMState) {
            if (blCancelUserClose) {
                m_dlgPin.cancelUserClose();
                m_dlgPuk.cancelUserClose();
            }
            ShowPinDialog();
        } else if (SIMState.PukRequired == sim.m_SIMState) {
            if (blCancelUserClose) {
                m_dlgPin.cancelUserClose();
                m_dlgPuk.cancelUserClose();
            }
            ShowPukDialog();
        }
    }

    public void ShowPinDialog() {
        // close PUK dialog
        if (null != m_dlgPuk && PukDialog.m_isShow) {
            m_dlgPuk.closeDialog();
        }

        SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
        // set the remain times
        if (null != m_dlgPin) {
            m_dlgPin.updateRemainTimes(simStatus.m_nPinRemainingTimes);
        }
        if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
            if (!PinDialog.m_isShow) {
                m_dlgPin.showDialog(simStatus.m_nPinRemainingTimes, () -> {
                    String strMsg = getString(R.string.pin_error_waring_title);
                    m_dlgError.showDialog(strMsg, () -> {
                        m_dlgPin.showDialog();
                    });
                });
            }
        }
    }

    public void ShowPukDialog() {
        // close PIN dialog
        if (null != m_dlgPin && PinDialog.m_isShow) {
            m_dlgPin.closeDialog();
        }

        SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
        // set the remain times
        if (null != m_dlgPuk) {
            m_dlgPuk.updateRemainTimes(simStatus.m_nPukRemainingTimes);
        }
        if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
            if (!PukDialog.m_isShow) {
                m_dlgPuk.showDialog(simStatus.m_nPinRemainingTimes, () -> {
                    String strMsg = getString(R.string.puk_error_waring_title);
                    m_dlgError.showDialog(strMsg, () -> {
                        m_dlgPuk.showDialog();
                    });
                });
            }
        }
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
