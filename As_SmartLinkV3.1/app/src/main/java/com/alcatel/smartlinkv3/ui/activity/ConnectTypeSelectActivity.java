package com.alcatel.smartlinkv3.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.MessageUti;
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
    private   BusinessMannager  mBusinessMgr;
    protected BroadcastReceiver mReceiver;
    private TextView mSimCardTv;
    private TextView mWanPortTv;
    private long mkeyTime; //点击2次返回 键的时间
    private   ImageView         mSimCardPic;
    private ImageView mWanProtPic;

    private CommonErrorInfoDialog mConfirmDialog = null;
    private AutoForceLoginProgressDialog m_ForceloginDlg = null;
    private AutoLoginProgressDialog mAutoLoginDialog = null;
    private ForceLoginSelectDialog forceLoginSelectDialog = null;
    private LoginDialog mLoginDialog = null;

    private RelativeLayout mHandlePinContainer;
    private RelativeLayout mNormalContainer;
    private EditText mPinPassword;
    private RelativeLayout mPinPasswordDel;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect_type_select);

        initView();
        initData();
    }

    private void initView() {
        mHeaderBackIv = (ImageView) findViewById(R.id.main_header_back_iv);
        mHeaderBackIv.setVisibility(View.GONE);

        mNormalContainer = (RelativeLayout) findViewById(R.id.connect_type_content_container);
        mHeaderSkipTv = (TextView) findViewById(R.id.main_header_right_text);
        mHeaderSkipTv.setVisibility(View.GONE);

        mSimCardPic = (ImageView) findViewById(R.id.sim_card_pic);
        mSimCardTv = (TextView) findViewById(R.id.connect_type_sim_card_tv);
        mWanPortTv = (TextView) findViewById(R.id.connect_type_wan_port_tv);
        mWanProtPic = (ImageView) findViewById(R.id.wan_port_pic);

        mHandlePinContainer = (RelativeLayout) findViewById(R.id.connect_type_handle_pin_container);
        mPinPassword = (EditText) findViewById(R.id.handle_pin_password);
        mPinPassword.setOnClickListener(this);
        mPinPassword.addTextChangedListener(mTextWatcher);
//        mPinPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH){
//                    Toast.makeText(getApplicationContext(), "搜索中...", Toast.LENGTH_SHORT).show();
//                    //...实现搜索的功能
//                }
//                return false;
//            }
//        });
        mPinPasswordDel = (RelativeLayout) findViewById(R.id.handle_pin_password_delete);
        mPinPasswordDel.setOnClickListener(this);
    }

    private void initData() {
        mBusinessMgr = BusinessMannager.getInstance();

        mReceiver = new QSBroadcastReceiver();

        SimStatusModel simStatus = mBusinessMgr.getSimStatus();
        if (simStatus.m_SIMState == ENUM.SIMState.NoSim || simStatus.m_SIMState == ENUM.SIMState.Unknown){
            showNoSimCard();
        }else{
            showHaveSimCard();
        }

        ENUM.UserLoginStatus status = mBusinessMgr.getLoginStatus();
        if (LoginDialog.isLoginSwitchOff() || status == ENUM.UserLoginStatus.login) {
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
        mSimCardPic.setImageResource(R.drawable.results_sim_nor);
    }

    private void showNoSimCard() {
        mSimCardTv.setTextColor(getResources().getColor(R.color.red));
        mSimCardTv.setText(R.string.connect_type_select_sim_card_disable);
        mSimCardTv.setEnabled(false);
        mSimCardPic.setImageResource(R.drawable.results_sim_dis);
    }

    private void showHaveWanPort() {
        mWanPortTv.setTextColor(getResources().getColor(R.color.black_text));
        mWanPortTv.setText(R.string.connect_type_select_wan_port_enable);
        mWanPortTv.setEnabled(true);
        mWanPortTv.setOnClickListener(this);
        mWanProtPic.setImageResource(R.drawable.results_wan_nor);
    }

    private void showNoWanPort() {
        mWanPortTv.setTextColor(getResources().getColor(R.color.red));
        mWanPortTv.setText(R.string.connect_type_select_wan_port_disable);
        mWanPortTv.setEnabled(false);
        mWanProtPic.setImageResource(R.drawable.results_wan_dis);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_type_sim_card_tv:
                mHeaderSkipTv.setVisibility(View.VISIBLE);
                mHandlePinContainer.setVisibility(View.VISIBLE);
                mNormalContainer.setVisibility(View.GONE);

                mHeaderSkipTv.setOnClickListener(this);
                break;
            case R.id.connect_type_wan_port_tv:
                Toast.makeText(getApplicationContext(), "wan port setting!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_header_right_text:
                Toast.makeText(getApplicationContext(), "next!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.handle_pin_password:
                mPinPassword.setFocusable(true);
                mPinPassword.requestFocus();
                mPinPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                imm = (InputMethodManager) mPinPassword.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.handle_pin_password_delete:
                mPinPassword.setText("");
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

        mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReceiver != null){
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
                if(retryLogin) {
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
    private void finishQuickSetup(boolean setFlag){
        if(setFlag) {
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
            public void onLoginSuccess()  {
//                buildStateHandlerChain(false);
            }

            public void onLoginFailed(String error_code) {
                if(error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)){
                    //Log.d(TAG, "ForceLogin.status:"+FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin"));
                    if(FeatureVersionManager.getInstance().isSupportApi("User", "ForceLogin") == true){
                        forceLoginSelectDialog = ForceLoginSelectDialog.getInstance(ConnectTypeSelectActivity.this);
                        forceLoginSelectDialog.showDialogAndCancel(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg),
                                new ForceLoginSelectDialog.OnClickBottonConfirm()
                                {
                                    public void onConfirm()
                                    {
                                        m_ForceloginDlg.autoForceLoginAndShowDialog(new AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener() {
                                            public void onLoginSuccess()
                                            {
//                                                buildStateHandlerChain(false);
                                            }

                                            public void onLoginFailed(String error_code)
                                            {
                                                if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD))
                                                {
                                                    ErrorDialog errorDialog = ErrorDialog.getInstance(ConnectTypeSelectActivity.this);
                                                    errorDialog.setCancelCallback(new ErrorDialog.OnClickBtnCancel() {
                                                        @Override
                                                        public void onCancel() {
                                                            CPEConfig.getInstance().cleanAllSetupFlag();
                                                            finish();
                                                        }
                                                    });
                                                    errorDialog.showDialog(getString(R.string.login_psd_error_msg),
                                                            new ErrorDialog.OnClickBtnRetry()
                                                            {
                                                                @Override
                                                                public void onRetry()
                                                                {
                                                                    //showLoginDialog();
                                                                    doLogin();
                                                                }
                                                            });
                                                }else if(error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT))
                                                {
                                                    //m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R.string.other_login_warning_title),    m_loginDlg.getLoginTimeUsedOutString());
                                                    handleLoginError(R.string.other_login_warning_title,
                                                            R.string.login_login_time_used_out_msg, true, false);
                                                }
                                            }
                                        });
                                    }


                                },new ForceLoginSelectDialog.OnClickBtnCancel(){

                                    @Override
                                    public void onCancle() {
                                        handleLoginError(R.string.qs_title, R.string.qs_exit_query, true, false);
                                    }});

                    }else{
                        handleLoginError(R.string.other_login_warning_title,  R.string.login_other_user_logined_error_msg, false, false);
                    }
                } else if(error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
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
            public void onFirstLogin(){
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

            } else if (action.equals(MessageUti.USER_HEARTBEAT_REQUEST) || action.equals(MessageUti.USER_COMMON_ERROR_32604_REQUEST)) {
                if (result == BaseResponse.RESPONSE_OK && error.equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
                    kickoffLogout();
                }
            }else if (action.equals(MessageUti.USER_LOGOUT_REQUEST)) {
                if (BaseResponse.RESPONSE_OK == result && error.length() == 0){
                    MainActivity.m_blLogout = false;
                    MainActivity.m_blkickoff_Logout = false;
                }
                handleLoginError(R.string.qs_title, R.string.login_kickoff_logout_successful, true, false);
            } else if (action.equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
                if (ok) {
                    if (mBusinessMgr.getSimStatus().m_SIMState == ENUM.SIMState.NoSim){
                        showNoSimCard();
                    }else{
                        showHaveSimCard();
                    }
                }
            }
        }
    }

    public void kickoffLogout() {
        ENUM.UserLoginStatus m_loginStatus = BusinessMannager.getInstance().getLoginStatus();
        if (m_loginStatus != null && m_loginStatus == ENUM.UserLoginStatus.Logout) {
            MainActivity.setKickoffLogoutFlag(true);
            BusinessMannager.getInstance().sendRequestMessage(
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
