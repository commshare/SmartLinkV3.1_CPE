package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.WanConnectStatusModel;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.LinkAppSettings;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;

public class ConnectTypeSelectActivity extends Activity implements View.OnClickListener {

    private   ImageView         mHeaderBackIv;
    private   TextView          mHeaderSkipTv;
    private   BusinessManager   mBusinessMgr;
    private   Context           mContext;
    protected BroadcastReceiver mReceiver;
    private   TextView          mSimCardTv;
    private   TextView          mWanPortTv;
    private   long              mkeyTime; //点击2次返回 键的时间
//    private   ImageView         mSimCardPic;
//    private   ImageView         mWanProtPic;

    private CommonErrorInfoDialog        mConfirmDialog         = null;
    private AutoForceLoginProgressDialog m_ForceloginDlg        = null;
    private AutoLoginProgressDialog      mAutoLoginDialog       = null;
    private ForceLoginSelectDialog       forceLoginSelectDialog = null;
    private LoginDialog                  mLoginDialog           = null;

    private RelativeLayout     mHandlePinContainer;
    private LinearLayout       mNormalContainer;
    private EditText           mPinPassword;
    private RelativeLayout     mPinPasswordDel;
    private InputMethodManager imm;
    private TextView           mPinPasswordDes;
    private TextView           mPasswordTimes;
    private TextView           mConnectBtn;
    private ImageView          mRememberPasswordSelect;
    private              boolean isRememberPassword = true;
    private static final String  PIN_PASSWORD       = "pinPassword";
    private int            pinRemainingTimes;
    private TextView       mHeardTitle;
    private RelativeLayout mWaitingContainer;
    private RelativeLayout mPinSuccessContainer;
    private RelativeLayout mPinFailContainer;
    private Button         mPinTryAaginBtn;
    private TextView       mPinFailToHome;

    private Handler        mHandler;
    private RelativeLayout mHeaderContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect_type_select);

        initView();
        initData();
    }

    private void initView() {
        mHeaderContainer = (RelativeLayout) findViewById(R.id.main_header);
        mHeardTitle = (TextView) findViewById(R.id.tv_title_title);
        mHeardTitle.setText(R.string.main_header_linkhub);
        mHeaderBackIv = (ImageView) findViewById(R.id.main_header_back_iv);
        mHeaderBackIv.setVisibility(View.GONE);

        mNormalContainer = (LinearLayout) findViewById(R.id.connect_type_content_container);
        mHeaderSkipTv = (TextView) findViewById(R.id.main_header_right_text);
        mHeaderSkipTv.setVisibility(View.GONE);

//        mSimCardPic = (ImageView) findViewById(R.id.sim_card_pic);
        mSimCardTv = (TextView) findViewById(R.id.connect_type_sim_card_tv);
        mWanPortTv = (TextView) findViewById(R.id.connect_type_wan_port_tv);
//        mWanProtPic = (ImageView) findViewById(R.id.wan_port_pic);

        mHandlePinContainer = (RelativeLayout) findViewById(R.id.connect_type_handle_pin_container);
        mPinPasswordDes = (TextView) findViewById(R.id.handle_pin_password_times_des);
        mPinPassword = (EditText) findViewById(R.id.handle_pin_password);
        mPinPassword.setOnClickListener(this);
        mPinPassword.addTextChangedListener(mTextWatcher);
        mPinPasswordDel = (RelativeLayout) findViewById(R.id.handle_pin_password_delete);
        mPinPasswordDel.setOnClickListener(this);
        mPasswordTimes = (TextView) findViewById(R.id.handle_pin_password_times);
        mRememberPasswordSelect = (ImageView) findViewById(R.id.handle_pin_remember_pin_select);
        if (isRememberPassword) {
            mRememberPasswordSelect.setImageResource(R.drawable.general_btn_remember_pre);
        } else {
            mRememberPasswordSelect.setImageResource(R.drawable.general_btn_remember_nor);
        }
        mRememberPasswordSelect.setOnClickListener(this);
        mConnectBtn = (TextView) findViewById(R.id.handle_pin_connect_btn);
        mConnectBtn.setOnClickListener(this);

        mWaitingContainer = (RelativeLayout) findViewById(R.id.connect_type_waiting);

        mPinSuccessContainer = (RelativeLayout) findViewById(R.id.pin_unlock_success_container);

        mPinFailContainer = (RelativeLayout) findViewById(R.id.pin_unlock_fail_container);
        mPinTryAaginBtn = (Button) findViewById(R.id.pin_unlock_try_again);
        mPinTryAaginBtn.setOnClickListener(this);
        mPinFailToHome = (TextView) findViewById(R.id.pin_unlock_fail_to_home);
        mPinFailToHome.setOnClickListener(this);
    }

    private void initData() {
        mContext = this;
        mBusinessMgr = BusinessManager.getInstance();

        mHandler = new Handler();

        mReceiver = new QSBroadcastReceiver();

        SimStatusModel simStatus = mBusinessMgr.getSimStatus();
        if (simStatus.m_SIMState == ENUM.SIMState.NoSim || simStatus.m_SIMState == ENUM.SIMState.Unknown) {
            showNoSimCard();
        } else {
            showHaveSimCard();
        }

        WanConnectStatusModel wanModel = mBusinessMgr.getWanConnectStatus();
        if (wanModel.isConnected()) {
            showHaveWanPort();
        } else {
            showNoWanPort();
        }

        ENUM.UserLoginStatus status = mBusinessMgr.getLoginStatus();
        if (LinkAppSettings.isLoginSwitchOff() || status == ENUM.UserLoginStatus.login) {
            return;
        }
        if (status == ENUM.UserLoginStatus.LoginTimeOut) {
            handleLoginError(R.string.other_login_warning_title,
                    R.string.login_login_time_used_out_msg, true, false);
        } else {
            doLogin();
        }
    }

    private void showHaveSimCard() {
        mSimCardTv.setTextColor(getResources().getColor(R.color.black_text));
        mSimCardTv.setText(R.string.connect_type_select_sim_card_enable);
        mSimCardTv.setEnabled(true);
        mSimCardTv.setOnClickListener(this);
//        mSimCardPic.setImageResource(R.drawable.results_sim_nor);
        mSimCardTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.results_sim_nor, 0, 0);
    }

    private void showNoSimCard() {
        mSimCardTv.setTextColor(getResources().getColor(R.color.red));
        mSimCardTv.setText(R.string.connect_type_select_sim_card_disable);
        mSimCardTv.setEnabled(false);
//        mSimCardPic.setImageResource(R.drawable.results_sim_dis);
        mSimCardTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.results_sim_dis, 0, 0);
    }

    private void showHaveWanPort() {
        mWanPortTv.setTextColor(getResources().getColor(R.color.black_text));
        mWanPortTv.setText(R.string.connect_type_select_wan_port_enable);
        mWanPortTv.setEnabled(true);
        mWanPortTv.setOnClickListener(this);
//        mWanProtPic.setImageResource(R.drawable.results_wan_nor);
        mWanPortTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.results_wan_nor, 0, 0);
    }

    private void showNoWanPort() {
        mWanPortTv.setTextColor(getResources().getColor(R.color.red));
        mWanPortTv.setText(R.string.connect_type_select_wan_port_disable);
        mWanPortTv.setEnabled(false);
//        mWanProtPic.setImageResource(R.drawable.results_wan_dis);
        mWanPortTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.results_wan_dis, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //SIM卡是否存在
            case R.id.connect_type_sim_card_tv:
                if (mBusinessMgr.getSimStatus().m_SIMState == ENUM.SIMState.PinRequired) {
                    hideAllLayout();
                    mHeaderContainer.setVisibility(View.VISIBLE);
                    mHeaderSkipTv.setVisibility(View.VISIBLE);
                    mHandlePinContainer.setVisibility(View.VISIBLE);
                    mHeardTitle.setText(R.string.main_header_mobile_broadband);
                    pinRemainingTimes = mBusinessMgr.getSimStatus().m_nPinRemainingTimes;
                    mPasswordTimes.setText(pinRemainingTimes + "");
                    if (pinRemainingTimes < 3){
                        mPasswordTimes.setTextColor(getResources().getColor(R.color.red));
                        mPinPasswordDes.setTextColor(getResources().getColor(R.color.red));
                    }else{
                        mPasswordTimes.setTextColor(getResources().getColor(R.color.black_text));
                        mPinPasswordDes.setTextColor(getResources().getColor(R.color.black_text));
                    }

                    mHeaderSkipTv.setOnClickListener(this);
                } else {
                    Toast.makeText(getApplicationContext(), "enter wifisetting", Toast.LENGTH_SHORT).show();
                }
                break;
            //WAN口是否存在
            case R.id.connect_type_wan_port_tv:
                Toast.makeText(getApplicationContext(), "wan port setting!", Toast.LENGTH_SHORT).show();
                break;
            //头部的下一步
            case R.id.main_header_right_text:
                Toast.makeText(getApplicationContext(), "next!", Toast.LENGTH_SHORT).show();
                break;
            //pin码输入框
            case R.id.handle_pin_password:
                mPinPassword.setFocusable(true);
                mPinPassword.requestFocus();
                mPinPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                imm = (InputMethodManager) mPinPassword.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
                break;
            //pin码删除框
            case R.id.handle_pin_password_delete:
                mPinPassword.setText("");
                break;
            //pin码是否记住
            case R.id.handle_pin_remember_pin_select:
                //TODO:
                isRememberPassword = !isRememberPassword;
                if (isRememberPassword) {
                    mRememberPasswordSelect.setImageResource(R.drawable.general_btn_remember_pre);
                } else {
                    mRememberPasswordSelect.setImageResource(R.drawable.general_btn_remember_nor);
                }
                break;
            //点击连接按钮
            case R.id.handle_pin_connect_btn:
                //TODO:
                //只记录验证正确的，请求前把旧数据清空。
                SharedPrefsUtil.getInstance(this).putString(PIN_PASSWORD, "");

                hideAllLayout();
                mWaitingContainer.setVisibility(View.VISIBLE);

                DataValue data = new DataValue();
                data.addParam("pin", mPinPassword.getText().toString());
                mBusinessMgr.sendRequestMessage(MessageUti.SIM_UNLOCK_PIN_REQUEST, data);
                break;
            //重新解pin
            case R.id.pin_unlock_try_again:
                //TODO:
                hideAllLayout();
                mHeaderContainer.setVisibility(View.VISIBLE);
                mNormalContainer.setVisibility(View.VISIBLE);
                mHeardTitle.setText(R.string.main_header_linkhub);
                break;
            //解pin失败，跳到home页按钮
            case R.id.pin_unlock_fail_to_home:
                //TODO:
                Toast.makeText(getApplicationContext(), "to home", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mkeyTime) > 2000) {
            mkeyTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.home_exit_app, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
        registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
        registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PIN_REQUEST));

        mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onDestroy() {
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
        mBusinessMgr = null;
    }

    private void handleLoginError(int titleId, int messageId, boolean showDialog, final boolean retryLogin) {
        if (mConfirmDialog != null) {
            mConfirmDialog.destroyDialog();
        }
        mConfirmDialog = CommonErrorInfoDialog.getInstance(this);
        mConfirmDialog.setCancelCallback(new CommonErrorInfoDialog.OnClickConfirmBotton() {

            @Override
            public void onConfirm() {
                if (retryLogin) {
                    //If timeout, let user re-login
                    //showLoginDialog();
                    doLogin();
                } else {
                    //                    //If other user enter, do not need setup
                    //                    finishQuickSetup(true);

                    CPEConfig.getInstance().cleanAllSetupFlag();
                    mConfirmDialog.destroyDialog();
                    ConnectTypeSelectActivity.this.finish();
                }
            }

        });
        mConfirmDialog.showDialog(
                getString(titleId),
                getString(messageId));
    }

    //直接跳过设置到主界面中
    private void finishQuickSetup(boolean setFlag) {
        if (setFlag) {
            CPEConfig.getInstance().setQuickSetupFlag();
        }
        Intent it = new Intent(this, MainActivity.class);
        //TODO:
        startActivity(it);
        finish();
    }

    private void doLogin() {
        m_ForceloginDlg = new AutoForceLoginProgressDialog(this);
        mAutoLoginDialog = new AutoLoginProgressDialog(this);
        mAutoLoginDialog.autoLoginAndShowDialog(new AutoLoginProgressDialog.OnAutoLoginFinishedListener() {
            /*
             * Auto login successfully.
             * Scenario: user enter correct password, then exit activity by press home key,
             * later launch smartlink again.
             */
            public void onLoginSuccess() {
                //                buildStateHandlerChain(false);
            }

            public void onLoginFailed(String error_code) {
                if (error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
                    //Log.d(TAG, "ForceLogin.status:"+FeatureVersionManager.getInstance().isSupportApi("User",
                    // "ForceLogin"));
                    if (FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") == true) {
                        forceLoginSelectDialog = ForceLoginSelectDialog.getInstance(ConnectTypeSelectActivity.this);
                        forceLoginSelectDialog.showDialogAndCancel(getString(R.string.other_login_warning_title),
                                getString(R.string.login_other_user_logined_error_forcelogin_msg),
                                new ForceLoginSelectDialog.OnClickBottonConfirm() {
                                    public void onConfirm() {
                                        m_ForceloginDlg.autoForceLoginAndShowDialog(new AutoForceLoginProgressDialog
                                                .OnAutoForceLoginFinishedListener() {
                                            public void onLoginSuccess() {
                                                //
                                                // buildStateHandlerChain(false);
                                            }

                                            public void onLoginFailed(String error_code) {
                                                if (error_code.equalsIgnoreCase(ErrorCode
                                                        .ERR_FORCE_USERNAME_OR_PASSWORD)) {
                                                    ErrorDialog errorDialog = ErrorDialog.getInstance
                                                            (ConnectTypeSelectActivity.this);
                                                    errorDialog.setCancelCallback(new ErrorDialog.OnClickBtnCancel() {
                                                        @Override
                                                        public void onCancel() {
                                                            CPEConfig.getInstance().cleanAllSetupFlag();
                                                            finish();
                                                        }
                                                    });
                                                    errorDialog.showDialog(getString(R.string.login_psd_error_msg),
                                                            new ErrorDialog.OnClickBtnRetry() {
                                                                @Override
                                                                public void onRetry() {
                                                                    //showLoginDialog();
                                                                    doLogin();
                                                                }
                                                            });
                                                } else if (error_code.equalsIgnoreCase(ErrorCode
                                                        .ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                                                    //m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R
                                                    // .string.other_login_warning_title),    m_loginDlg
                                                    // .getLoginTimeUsedOutString());
                                                    handleLoginError(R.string.other_login_warning_title,
                                                            R.string.login_login_time_used_out_msg, true, false);
                                                }
                                            }
                                        });
                                    }


                                }, new ForceLoginSelectDialog.OnClickBtnCancel() {

                                    @Override
                                    public void onCancle() {
                                        handleLoginError(R.string.qs_title, R.string.qs_exit_query, true, false);
                                    }
                                });

                    } else {
                        handleLoginError(R.string.other_login_warning_title, R.string
                                .login_other_user_logined_error_msg, false, false);
                    }
                } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
                    handleLoginError(R.string.other_login_warning_title,
                            R.string.login_login_time_used_out_msg, true, false);
                } else {
                    ErrorDialog errorDialog = ErrorDialog.getInstance(ConnectTypeSelectActivity.this);

                    errorDialog.setCancelCallback(new ErrorDialog.OnClickBtnCancel() {
                        @Override
                        public void onCancel() {
                            CPEConfig.getInstance().cleanAllSetupFlag();
                            finish();
                        }
                    });

                    errorDialog.showDialog(
                            getString(R.string.login_psd_error_msg),
                            new ErrorDialog.OnClickBtnRetry() {
                                @Override
                                public void onRetry() {
                                    //showLoginDialog();
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

    /*
   * A dialog that let use enter password.
   */
    private void showLoginDialog() {
        mLoginDialog = new LoginDialog(this);
        mLoginDialog.setCancelCallback(new LoginDialog.CancelLoginListener() {

            @Override
            public void onCancelLogin() {
                handleLoginError(R.string.qs_title, R.string.qs_exit_query, true, false);
            }

        });
        mLoginDialog.showDialogDoLogin();
    }

    class QSBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int result = intent.getIntExtra(MessageUti.RESPONSE_RESULT,
                    BaseResponse.RESPONSE_OK);
            String error = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (error == null) {
                error = "Error";
            }
            boolean ok = BaseResponse.RESPONSE_OK == result && 0 == error.length();
            if (action.equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
                // If WiFi disconnect router, go to Refresh WiFi activity.
                if (!DataConnectManager.getInstance().getCPEWifiConnected()) {
                    context.startActivity(new Intent(context, RefreshWifiActivity.class));
                    finish();
                }

            } else if (action.equals(MessageUti.USER_HEARTBEAT_REQUEST) || action.equals(MessageUti
                    .USER_COMMON_ERROR_32604_REQUEST)) {
                if (result == BaseResponse.RESPONSE_OK && error.equalsIgnoreCase(ErrorCode
                        .ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
                    kickoffLogout();
                }
            } else if (action.equals(MessageUti.USER_LOGOUT_REQUEST)) {
                if (BaseResponse.RESPONSE_OK == result && error.length() == 0) {
                    MainActivity.m_blLogout = false;
                    MainActivity.m_blkickoff_Logout = false;
                }
                handleLoginError(R.string.qs_title, R.string.login_kickoff_logout_successful, true, false);
            } else if (action.equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
                if (ok) {
                    if (mBusinessMgr.getSimStatus().m_SIMState == ENUM.SIMState.NoSim) {
                        showNoSimCard();
                    } else {
                        showHaveSimCard();
                    }
                }
            } else if (action.equalsIgnoreCase(MessageUti.SIM_UNLOCK_PIN_REQUEST)) {
                if (BaseResponse.RESPONSE_OK == result && error.length() == 0) {
                    //PIN解码成功
                    hideAllLayout();
                    mPinSuccessContainer.setVisibility(View.VISIBLE);

                    if (isRememberPassword) {
                        SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).putString(PIN_PASSWORD,
                                mPinPassword.getText().toString());
                    } else {
                        SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).putString(PIN_PASSWORD, "");
                    }

                    //测试显示用的
                    String pinPassword = SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).getString
                            (PIN_PASSWORD, "");
                    if (!pinPassword.equals("")) {
                        Toast.makeText(getApplicationContext(), pinPassword, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "don't remember password", Toast.LENGTH_SHORT).show();
                    }

                    //2秒后跳到主页面
                    Runnable filterRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "跳转到主页面中。。。", Toast.LENGTH_SHORT).show();
                        }
                    };
                    mHandler.postDelayed(filterRunnable, 2000);
                } else {//PIN解码失败
                    //PIN 的输入机会还有
//                    pinRemainingTimes--;//由于是定时获取的sim卡状态，故手动自减一次机会
//                    mPinPasswordDes.setTextColor(getResources().getColor(R.color.red));
//                    if (pinRemainingTimes > 0) {
//                        mWaitingContainer.setVisibility(View.GONE);
//                        mHandlePinContainer.setVisibility(View.VISIBLE);
//                        mHeaderSkipTv.setVisibility(View.VISIBLE);
//                        mPasswordTimes.setText(pinRemainingTimes + "");
//                        mPasswordTimes.setTextColor(getResources().getColor(R.color.red));
//                    } else {
//                        // PIN 输入机会用完
//                        Toast.makeText(getApplicationContext(), "PIN码输入次数已经用完！！！", Toast.LENGTH_SHORT).show();
//                    }

                    hideAllLayout();
                    mPinFailContainer.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void hideAllLayout(){
        mHeaderContainer.setVisibility(View.GONE);//头部
        mHeaderSkipTv.setVisibility(View.GONE);//头部的skip按钮
        mNormalContainer.setVisibility(View.GONE);//普通的内容页
        mHandlePinContainer.setVisibility(View.GONE);//处理pin码页
        mWaitingContainer.setVisibility(View.GONE);//等待页
        mPinSuccessContainer.setVisibility(View.GONE);//pin解码成功页
        mPinFailContainer.setVisibility(View.GONE);//pin解码失败页
    }

    public void kickoffLogout() {
        ENUM.UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
        if (m_loginStatus != null && m_loginStatus == ENUM.UserLoginStatus.Logout) {
            MainActivity.setKickoffLogoutFlag(true);
            BusinessManager.getInstance().sendRequestMessage(
                    MessageUti.USER_LOGOUT_REQUEST, null);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (mPinPassword.getText().toString() != null && !mPinPassword.getText().toString().equals("")) {
                mPinPasswordDel.setVisibility(View.VISIBLE);
            } else {
                mPinPasswordDel.setVisibility(View.INVISIBLE);
            }
        }
    };
}
