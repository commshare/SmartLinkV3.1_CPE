package com.alcatel.smartlinkv3.ui.home.allsetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.LinkAppSettings;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.ActivityNewSms;
import com.alcatel.smartlinkv3.ui.activity.BaseActivity;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.home.helper.sms.SmsCountHelper;
import com.alcatel.smartlinkv3.ui.home.helper.utils.FraHomeHelper;
import com.alcatel.smartlinkv3.ui.home.helper.utils.FragmentHomeEnum;

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
import static com.alcatel.smartlinkv3.common.ENUM.SIMState;
import static com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import static com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener;
import static com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog.OnAutoLoginFinishedListener;
import static com.alcatel.smartlinkv3.ui.dialog.LoginDialog.OnLoginFinishedListener;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.layout_main)
    RelativeLayout layoutMain;// 父布局

    @BindView(R.id.mRl_home_topbanner)
    RelativeLayout mRlHomeTopbanner;// 顶部栏
    @BindView(R.id.mTv_home_Title)
    TextView mTvHomeTitle;// 标题
    @BindView(R.id.mTv_home_logout)
    TextView mTvHomeLogout;// Logout
    @BindView(R.id.mIv_home_editMessage)
    ImageView mIvHomeEditMessage;// message ui按钮

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
    RelativeLayout mRlHomeMessagebutton;// message按钮
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

    int index = 0;
    private int[] press;
    private int[] normal;
    private FragmentManager fm;
    private ImageView[] groupButtons;

    private LoginDialog m_loginDlg;
    private AutoForceLoginProgressDialog m_ForceloginDlg;
    private AutoLoginProgressDialog m_autoLoginDialog;
    private PinDialog m_dlgPin;
    private PukDialog m_dlgPuk;
    private ErrorDialog m_dlgError;
    private int m_nNewCount;// SMS消息数量


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

    private void initView() {
        mTvHomeMessageCount = (TextView) findViewById(R.id.mTv_home_messageCount);
        SmsCountHelper.setSmsCount(mTvHomeMessageCount);// init show sms count
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
        API.get().logout(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(HomeActivity.this, getString(R.string.login_logout_successful));
                CPEConfig.getInstance().userLogout();
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
    private void navigateAfterLogin(OnLoginFinishedListener listener) {
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

        m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener() {
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

}
