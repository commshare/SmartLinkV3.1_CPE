package com.alcatel.wifilink.ui.wizard.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.FeatureVersionManager;
import com.alcatel.wifilink.common.CPEConfig;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.LinkAppSettings;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.activity.MainActivity;
import com.alcatel.wifilink.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.wifilink.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.wifilink.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.wifilink.ui.dialog.ErrorDialog;
import com.alcatel.wifilink.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.wifilink.ui.dialog.LoginDialog;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.wizard.allsetup.SetupWizardActivity;
import com.alcatel.wifilink.ui.wizard.helper.FraHelper;
import com.alcatel.wifilink.ui.wizard.helper.FragmentEnum;
import com.alcatel.wifilink.ui.wizard.helper.QSBroadcastReceiver;

@SuppressLint("ValidFragment")
public class ConnectTypeFragment extends Fragment implements View.OnClickListener {

    TextView mTv_sim;
    TextView mTv_wan;

    private BusinessManager mBusinessMgr;
    private View inflate;
    private Handler mHandler;
    protected QSBroadcastReceiver mReceiver;
    private boolean isRememberPassword = true;
    private SetupWizardActivity activity;

    private CommonErrorInfoDialog mConfirmDialog = null;
    private AutoForceLoginProgressDialog mForceLoginDlg = null;
    private AutoLoginProgressDialog mAutoLoginDialog = null;
    private ForceLoginSelectDialog forceLoginSelectDialog = null;
    private LoginDialog mLoginDialog = null;

    // TOAT: 测试阶段设置为true
    private boolean test = false;// 测试开关
    private boolean simInsert;// SIM卡是否插入
    private boolean wanConnect;// WAN口是否连接

    public ConnectTypeFragment() {

    }

    public ConnectTypeFragment(Activity activity) {
        this.activity = (SetupWizardActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_setupwizard_connecttype, null);
        return inflate;
    }

    private void initView() {
        mTv_sim = (TextView) inflate.findViewById(R.id.connect_type_sim_card_tv);
        mTv_wan = (TextView) inflate.findViewById(R.id.connect_type_wan_port_tv);
    }

    private void clickEvent() {
        mTv_sim.setOnClickListener(this);
        mTv_wan.setOnClickListener(this);
    }

    private void inidata() {
        mBusinessMgr = BusinessManager.getInstance();
        mHandler = new Handler();

        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {

                // 切换SIM卡状态drawable视图
                showSimCard(result.getSIMState());
            }
        });

        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                // WAN口状态drawable视图
                showHaveWanPort(result.getStatus());
            }
        });

        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                // 登陆状态

                if (LinkAppSettings.isLoginSwitchOff() || result.getState() == Constants.Loginstate.LOGIN) {// 已登陆
                    onlyTypeMode();
                    return;
                }
                if (result.getState() == Constants.Loginstate.THE_LOGIN_TIMES_USED_OUT) {// 已登出
                    int warnTitle = R.string.other_login_warning_title;
                    int timeUsedOut = R.string.login_login_time_used_out_msg;
                    handleLoginError(warnTitle, timeUsedOut, true, false);
                } else {
                    doLogin();// 进行登陆
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_type_sim_card_tv:// SIM卡
                // SIM按钮点击事件
                simClick();
                break;
            case R.id.connect_type_wan_port_tv:// WAN口
                // WAN按钮点击事件
                FraHelper.commit(activity, activity.fm, activity.flid_setupWizard, FragmentEnum.WAN_NETMODE_CHOICE_FRA);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        clickEvent();
        inidata();
        // 重新注册动态广播
        getActivity().registerReceiver(mReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
        getActivity().registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
        mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoginDialog != null)
            mLoginDialog.destroyDialog();
        if (mConfirmDialog != null)
            mConfirmDialog.destroyDialog();
        if (mAutoLoginDialog != null)
            mAutoLoginDialog.destroyDialog();
        if (forceLoginSelectDialog != null) {
            forceLoginSelectDialog.destroyDialog();
        }
        if (mForceLoginDlg != null)
            mForceLoginDlg.destroyDialog();
        mBusinessMgr = null;
    }
    
    /* ------------------------------------------------ helper ------------------------------------------------ */
    /* ------------------------------------------------ helper ------------------------------------------------ */

    /**
     * H1.切换SIM卡drawable状态
     *
     * @param simStatus
     */
    private void showSimCard(int simStatus) {
        // 默认为true
        simInsert = true;

        // 获取SIM状态

        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.NOWN || simState == Cons.ILLEGAL) {
                    simInsert = false;
                    // TOAT: 测试阶段强制为true START 
                    if (test) {
                        simInsert = test;
                    }
                    // TOAT: 测试阶段强制为true END 

                    // TOAT: 测试单模式请打开此代码
                    // simInsert = false;
                    getActivity().runOnUiThread(() -> {
                        mTv_sim.setTextColor(getResources().getColor(simInsert ? R.color.black_text : R.color.red));
                        mTv_sim.setText(simInsert ? R.string.connect_type_select_sim_card_enable : R.string.connect_type_select_sim_card_disable);
                        mTv_sim.setEnabled(simInsert);

                        mTv_sim.setOnClickListener(simInsert ? ConnectTypeFragment.this : null);
                        mTv_sim.setCompoundDrawablesWithIntrinsicBounds(0, simInsert ? R.drawable.results_sim_nor : R.drawable.results_sim_dis, 0, 0);
                    });
                }
            }
        });

        if (simStatus == Constants.SIMState.NOWN || simStatus == Constants.SIMState.SIM_CARD_ILLEGAL) {

        }


    }

    /**
     * H2.切换WAN口drawable状态
     *
     * @param wanModel
     */
    private void showHaveWanPort(int wanModel) {
        // 检测WAN口是否接上
        //0: disconnected 1: connecting 2: connected 3: disconnecting
        wanConnect = wanModel == 2;
        // TOAT: 測試把該標記設置為true START
        if (test) {
            wanConnect = test;
        }
        // TOAT: 測試把該標記設置為true END

        // TOAT: 测试单模式下请打开此代码
        // wanConnect = true;

        mTv_wan.setTextColor(getResources().getColor(wanConnect ? R.color.black_text : R.color.red));
        mTv_wan.setText(wanConnect ? R.string.connect_type_select_wan_port_enable : R.string.connect_type_select_wan_port_disable);
        mTv_wan.setEnabled(wanConnect);
        mTv_wan.setOnClickListener(wanConnect ? (View.OnClickListener) this : null);
        mTv_wan.setCompoundDrawablesWithIntrinsicBounds(0, wanConnect ? R.drawable.results_wan_nor : R.drawable.results_wan_dis, 0, 0);
    }

    /**
     * H3.单模式下的自动跳转
     */
    private void onlyTypeMode() {
        // TOAT: 单模式下的自动跳转
        // 判断是否为单模式(only SIM or only WAN)
        if (simInsert && !wanConnect) {// only sim
            simClick();// --> execute the sim logic
        } else if (!simInsert && wanConnect) {// only wan
            // 只有WAN口的模式下的跳转
            // to net mode fragment
            FraHelper.commit(activity, activity.fm, activity.flid_setupWizard, FragmentEnum.WAN_NETMODE_CHOICE_FRA);
        }
    }

    /**
     * H4.登陆发生错误的处理
     *
     * @param titleId
     * @param messageId
     * @param showDialog
     * @param retryLogin
     */
    private void handleLoginError(int titleId, int messageId, boolean showDialog, final boolean retryLogin) {
        if (mConfirmDialog != null) {
            mConfirmDialog.destroyDialog();
        }
        mConfirmDialog = CommonErrorInfoDialog.getInstance(getActivity());
        mConfirmDialog.setCancelCallback(new CommonErrorInfoDialog.OnClickConfirmButton() {

            @Override
            public void onConfirm() {
                if (retryLogin) {
                    //If timeout, let user re-LOGIN
                    //showLoginDialog();
                    doLogin();
                } else {
                    //If other user enter, do not need setup
                    // finishQuickSetup(true);

                    CPEConfig.getInstance().cleanAllSetupFlag();
                    mConfirmDialog.destroyDialog();
                    getActivity().finish();
                }
            }

        });
        mConfirmDialog.showDialog(getString(titleId), getString(messageId));
    }

    /**
     * H5.登陆
     */
    private void doLogin() {


        mForceLoginDlg = new AutoForceLoginProgressDialog(getActivity());
        mAutoLoginDialog = new AutoLoginProgressDialog(getActivity());


        mAutoLoginDialog.autoLoginAndShowDialog(new AutoLoginProgressDialog.OnAutoLoginFinishedListener() {
            /*
             * Auto LOGIN successfully.
             * Scenario: user enter correct password, then exit activity by press home key,
             * later launch smartlink again.
             */
            public void onLoginSuccess() {
            }

            public void onLoginFailed(String error_code) {
                if (error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
                    if (FeatureVersionManager.getInstance().isSupportForceLogin()) {
                        forceLoginSelectDialog = ForceLoginSelectDialog.getInstance(getActivity());
                        forceLoginSelectDialog.showDialogAndCancel(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg), new ForceLoginSelectDialog.OnClickButtonConfirm() {
                            public void onConfirm() {
                                mForceLoginDlg.autoForceLoginAndShowDialog(new AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener() {
                                    public void onLoginSuccess() {
                                    }

                                    public void onLoginFailed(String error_code) {
                                        if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD)) {
                                            ErrorDialog errorDialog = ErrorDialog.getInstance(getActivity());
                                            errorDialog.setCancelCallback(new ErrorDialog.OnClickBtnCancel() {
                                                @Override
                                                public void onCancel() {
                                                    CPEConfig.getInstance().cleanAllSetupFlag();
                                                    getActivity().finish();
                                                }
                                            });
                                            errorDialog.showDialog(getString(R.string.login_psd_error_msg), new ErrorDialog.OnClickBtnRetry() {
                                                @Override
                                                public void onRetry() {
                                                    doLogin();
                                                }
                                            });
                                        } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                                            handleLoginError(R.string.other_login_warning_title, R.string.login_login_time_used_out_msg, true, false);
                                        }
                                    }
                                });
                            }


                        }, new ForceLoginSelectDialog.OnClickBtnCancel() {

                            @Override
                            public void onCancel() {
                                handleLoginError(R.string.qs_title, R.string.qs_exit_query, true, false);
                            }
                        });

                    } else {
                        handleLoginError(R.string.other_login_warning_title, R.string.login_other_user_logined_error_msg, false, false);
                    }
                } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
                    handleLoginError(R.string.other_login_warning_title, R.string.login_login_time_used_out_msg, true, false);
                } else {
                    ErrorDialog errorDialog = ErrorDialog.getInstance(getActivity());

                    errorDialog.setCancelCallback(new ErrorDialog.OnClickBtnCancel() {
                        @Override
                        public void onCancel() {
                            CPEConfig.getInstance().cleanAllSetupFlag();
                            getActivity().finish();
                        }
                    });

                    errorDialog.showDialog(getString(R.string.login_psd_error_msg), new ErrorDialog.OnClickBtnRetry() {
                        @Override
                        public void onRetry() {
                            doLogin();
                        }
                    });
                }
            }

            @Override
            public void onFirstLogin() {
                showLoginDialog();
            }
        });
    }

    /**
     * H6.剔除登陆状态
     */
    public void kickoffLogout() {
        ENUM.UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
        if (m_loginStatus != null && m_loginStatus == ENUM.UserLoginStatus.Logout) {
            MainActivity.setKickoffLogoutFlag(true);
            BusinessManager.getInstance().sendRequestMessage(MessageUti.USER_LOGOUT_REQUEST, null);
        }
    }

    /**
     * H7.点击sim卡逻辑
     */
    private void simClick() {
        boolean sim;

        // TOAT: 测试时暂时使用标记位为true START
        if (test) {
            sim = test;
        } else {

            API.get().getSimStatus(new MySubscriber<SimStatus>() {
                @Override
                protected void onSuccess(SimStatus result) {
                    getActivity().runOnUiThread(() -> {
                        int simState = result.getSIMState();
                        if (simState == Cons.PIN_REQUIRED) {
                            // to pin code fragment
                            SetupWizardActivity activity = (SetupWizardActivity) getActivity();
                            FraHelper.commit(activity, activity.fm, activity.flid_setupWizard, FragmentEnum.PIN_FRA);
                        } else {
                            finishQuickSetup(false);
                        }
                    });
                }
            });
            sim = (mBusinessMgr.getSimStatus().m_SIMState == ENUM.SIMState.PinRequired) ? true : false;
        }
        // TOAT: 测试时暂时使用标记位为true END
    }

    /**
     * H8.显示登陆窗口
     */
    private void showLoginDialog() {
        mLoginDialog = new LoginDialog(getActivity());
        mLoginDialog.setCancelCallback(new LoginDialog.CancelLoginListener() {

            @Override
            public void onCancelLogin() {
                handleLoginError(R.string.qs_title, R.string.qs_exit_query, true, false);
            }

        });

        // TOAT: 单模式下的自动跳转行为
        mLoginDialog.setOnLoginStatusListener(success -> {
            if (success) {
                onlyTypeMode();
            }
        });

        mLoginDialog.showDialogDoLogin();
    }


    /**
     * H9.直接跳过设置到主界面中
     *
     * @param setFlag
     */
    private void finishQuickSetup(boolean setFlag) {
        if (setFlag) {
            CPEConfig.getInstance().setQuickSetupFlag();
        }
        Intent it = new Intent(getActivity(), HomeActivity.class);
        CPEConfig.getInstance().setQuickSetupFlag();
        startActivity(it);
        getActivity().finish();
    }
}
