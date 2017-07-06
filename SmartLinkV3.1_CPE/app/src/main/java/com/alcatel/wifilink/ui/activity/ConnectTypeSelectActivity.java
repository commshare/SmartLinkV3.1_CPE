package com.alcatel.wifilink.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
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

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.DataConnectManager;
import com.alcatel.wifilink.business.FeatureVersionManager;
import com.alcatel.wifilink.business.model.SimStatusModel;
import com.alcatel.wifilink.business.model.WanConnectStatusModel;
import com.alcatel.wifilink.business.sim.helper.SimPinHelper;
import com.alcatel.wifilink.common.CPEConfig;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.LinkAppSettings;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.wifilink.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.wifilink.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.wifilink.ui.dialog.ErrorDialog;
import com.alcatel.wifilink.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.wifilink.ui.dialog.LoginDialog;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;

@Deprecated
public class ConnectTypeSelectActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ConnectTypeSelect";
    private ImageView mHeaderBackIv;
    private TextView mHeaderSkipTv;
    private BusinessManager mBusinessMgr;
    private Context mContext;
    protected BroadcastReceiver mReceiver;
    private TextView mSimCardTv;
    private TextView mWanPortTv;
    private long mKeyTime; //点击2次返回键的时间
    //    private   ImageView         mSimCardPic;
    //    private   ImageView         mWanProtPic;

    private CommonErrorInfoDialog mConfirmDialog = null;
    private AutoForceLoginProgressDialog mForceLoginDlg = null;
    private AutoLoginProgressDialog mAutoLoginDialog = null;
    private ForceLoginSelectDialog forceLoginSelectDialog = null;
    private LoginDialog mLoginDialog = null;

    private RelativeLayout mHandlePinContainer;
    private LinearLayout mNormalContainer;
    private EditText mPinPassword;
    private RelativeLayout mPinPasswordDel;
    private InputMethodManager imm;
    private TextView mPinPasswordDes;
    private TextView mPasswordTimes;
    private TextView mConnectBtn;
    private ImageView mRememberPasswordSelect;
    private boolean isRememberPassword = true;
    private static final String PIN_PASSWORD = "pinPassword";
    private int pinRemainingTimes;
    private TextView mHeardTitle;
    private RelativeLayout mWaitingContainer;
    private RelativeLayout mPinSuccessContainer;
    private RelativeLayout mPinFailContainer;
    private Button mPinTryAgainBtn;
    private TextView mPinFailToHome;

    private Handler mHandler;
    private RelativeLayout mHeaderContainer;

    // TOAT: 测试阶段设置为true
    private boolean test = false;// 测试开关
    private boolean simInsert;// SIM卡是否插入
    private boolean wanConnect;// WAN口是否连接

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect_type_select);

        initView();
        initData();
    }

    private void initView() {
        // 顶部标题容器
        mHeaderContainer = (RelativeLayout) findViewById(R.id.mHead_setupWizard);
        mHeardTitle = (TextView) findViewById(R.id.tv_title_title);
        mHeardTitle.setText(R.string.main_header_linkhub);
        mHeaderBackIv = (ImageView) findViewById(R.id.main_header_back_iv);
        mHeaderBackIv.setVisibility(View.GONE);

        // 普通容器
        mNormalContainer = (LinearLayout) findViewById(R.id.connect_type_content_container);
        mHeaderSkipTv = (TextView) findViewById(R.id.main_header_right_text);
        mHeaderSkipTv.setVisibility(View.GONE);

        // mSimCardPic = (ImageView) findViewById(R.id.sim_card_pic);
        mSimCardTv = (TextView) findViewById(R.id.connect_type_sim_card_tv);
        mWanPortTv = (TextView) findViewById(R.id.connect_type_wan_port_tv);
        // mWanProtPic = (ImageView) findViewById(R.id.wan_port_pic);

        // PIN容器
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
        // 等待页
        mWaitingContainer = (RelativeLayout) findViewById(R.id.connect_type_waiting);
        // 成功页
        mPinSuccessContainer = (RelativeLayout) findViewById(R.id.pin_unlock_success_container);
        // 失败页
        mPinFailContainer = (RelativeLayout) findViewById(R.id.mRl_connectStatus_failed);
        mPinTryAgainBtn = (Button) findViewById(R.id.mRp_connectStatus_tryagain);
        mPinTryAgainBtn.setOnClickListener(this);
        mPinFailToHome = (TextView) findViewById(R.id.mTv_connectStatus_home);
        mPinFailToHome.setOnClickListener(this);
    }

    // TOGO 2017/6/14 GO
    private void initData() {

        mContext = this;
        mBusinessMgr = BusinessManager.getInstance();

        mHandler = new Handler();

        mReceiver = new QSBroadcastReceiver();
        // 切换SIM卡状态drawable视图
        showSimCard(mBusinessMgr.getSimStatus());
        // WAN口状态drawable视图
        showHaveWanPort(mBusinessMgr.getWanConnectStatus());

        // 登陆状态
        ENUM.UserLoginStatus status = mBusinessMgr.getLoginStatus();

        if (LinkAppSettings.isLoginSwitchOff() || status == ENUM.UserLoginStatus.LOGIN) {// 已登陆
            onlyTypeMode();
            return;
        }
        if (status == ENUM.UserLoginStatus.LoginTimeOut) {// 已登出
            handleLoginError(R.string.other_login_warning_title, R.string.login_login_time_used_out_msg, true, false);
        } else {
            doLogin();// 进行登陆
        }
    }

    // TOGO 2017/6/14 GO
    /**
     * 单模式下的自动跳转
     */
    private void onlyTypeMode() {
        // TOAT: 单模式下的自动跳转
        // 判断是否为单模式(only SIM or only WAN)
        if (simInsert && !wanConnect) {// only sim
            simClick();// --> execute the sim logic
        } else if (!simInsert && wanConnect) {// only wan
            // 只有WAN口的模式下的跳转
            ChangeActivity.toActivity(this, SettingNetModeActivity.class, true, true, false, 0);
        }
    }

    // TOGO 2017/6/14 Go
    /**
     * 切换SIM卡drawable状态
     *
     * @param simStatus
     */
    private void showSimCard(SimStatusModel simStatus) {
        // 默认为true
        simInsert = true;

        // 获取SIM状态
        if (simStatus.m_SIMState == ENUM.SIMState.NoSim || simStatus.m_SIMState == ENUM.SIMState.Unknown) {
            simInsert = false;
        }
        
        // TOAT: 测试阶段强制为true START 
        if (test) {
            simInsert = test;
        }
        // TOAT: 测试阶段强制为true END 

        // TOAT: 测试单模式请打开此代码
        //simInsert = false;

        mSimCardTv.setTextColor(getResources().getColor(simInsert ? R.color.black_text : R.color.red));
        mSimCardTv.setText(simInsert ? R.string.connect_type_select_sim_card_enable : R.string.connect_type_select_sim_card_disable);
        mSimCardTv.setEnabled(simInsert);

        mSimCardTv.setOnClickListener(simInsert ? this : null);
        // mSimCardPic.setImageResource(R.drawable.results_sim_nor);
        mSimCardTv.setCompoundDrawablesWithIntrinsicBounds(0, simInsert ? R.drawable.results_sim_nor : R.drawable.results_sim_dis, 0, 0);
    }


    // TOGO 2017/6/14 Go
    /**
     * 切换WAN口drawable状态
     *
     * @param wanModel
     */
    private void showHaveWanPort(WanConnectStatusModel wanModel) {
        // 检测WAN口是否接上
        wanConnect = wanModel.isConnected();

        // TOAT: 測試把該標記設置為true START
        if (test) {
            wanConnect = test;
        }
        // TOAT: 測試把該標記設置為true END

        // TOAT: 测试单模式下请打开此代码
        //wanConnect = true;

        mWanPortTv.setTextColor(getResources().getColor(wanConnect ? R.color.black_text : R.color.red));
        mWanPortTv.setText(wanConnect ? R.string.connect_type_select_wan_port_enable : R.string.connect_type_select_wan_port_disable);
        mWanPortTv.setEnabled(wanConnect);
        mWanPortTv.setOnClickListener(wanConnect ? this : null);
        mWanPortTv.setCompoundDrawablesWithIntrinsicBounds(0, wanConnect ? R.drawable.results_wan_nor : R.drawable.results_wan_dis, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // TOGO 2017/6/14 to connecttypefragment
            case R.id.main_header_back_iv:
                // 回跳到类型选择 
                mHeardTitle.setText(R.string.main_header_linkhub);
                mHeaderSkipTv.setVisibility(View.GONE);
                mHeaderBackIv.setVisibility(View.GONE);

                // 页面切换
                mHandlePinContainer.setVisibility(View.GONE);
                mWaitingContainer.setVisibility(View.GONE);
                mPinSuccessContainer.setVisibility(View.GONE);
                mPinFailContainer.setVisibility(View.GONE);
                mNormalContainer.setVisibility(View.VISIBLE);

                // 恢复初始数据
                pinRemainingTimes = 0;
                mPasswordTimes.setText(pinRemainingTimes + "");

                break;

            // TOGO 2017/6/14 to connecttypefragment
            //SIM卡是否存在
            case R.id.connect_type_sim_card_tv:
                // 执行SIM卡操作逻辑
                simClick();
                break;

            // TODO: 2017/6/14 ready to creat the netmode fragment 
            //WAN口
            case R.id.connect_type_wan_port_tv:
                // 显示WIFI设置页面
                ChangeActivity.toActivity(this, SettingNetModeActivity.class, true, true, false, 0);

                break;

            // TOGO 2017/6/14 to setupwizard
            // skip按钮
            case R.id.main_header_right_text:
                ChangeActivity.toActivity(this, SettingWifiActivity.class, true, true, false, 0);
                break;
            
            
            //pin码输入框
            case R.id.handle_pin_password:
                mPinPassword.setFocusable(true);
                mPinPassword.requestFocus();
                mPinPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                imm = (InputMethodManager) mPinPassword.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
                break;

            // TOGO 2017/6/14 to pincodefragment
            //pin码删除框
            case R.id.handle_pin_password_delete:
                mPinPassword.setText("");
                break;

            // TOGO 2017/6/14 to pincode fragment
            //pin码是否记住
            case R.id.handle_pin_remember_pin_select:
                isRememberPassword = !isRememberPassword;
                if (isRememberPassword) {
                    mRememberPasswordSelect.setImageResource(R.drawable.general_btn_remember_pre);
                } else {
                    mRememberPasswordSelect.setImageResource(R.drawable.general_btn_remember_nor);
                }
                break;

            // TOGO 2017/6/14 to pincodefragment
            // PIN界面连接按钮
            case R.id.handle_pin_connect_btn:
                // 判断次数 : <=0 不执行
                // TOAT: 测试时把该代码注释 START
                if (!currentRemain()) {
                    ToastUtil_m.show(this, "Pin had locked");
                    return;
                }
                // TOAT: 测试时把该代码注释 END
                mWaitingContainer.setVisibility(View.VISIBLE);
                // 请求前把旧数据清空
                SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).putString(PIN_PASSWORD, "");
                // 非空判断
                String pinPassword = mPinPassword.getText().toString().trim();
                if (TextUtils.isEmpty(pinPassword)) {
                    ToastUtil_m.show(this, "Not permit PIN empty");
                    mWaitingContainer.setVisibility(View.GONE);
                    return;
                }
                // 请求
                new SimPinHelper(this) {
                    @Override
                    public void isPinCorrect(boolean correct) {
                        hideAllLayout();
                        mWaitingContainer.setVisibility(View.GONE);
                        if (correct) {// 如果PIN码正确, 则保存PIN码到本地
                            SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).putString(PIN_PASSWORD, pinPassword);
                            mPinSuccessContainer.setVisibility(View.VISIBLE);
                            // 跳转到wifi setting
                            ChangeActivity.toActivity(ConnectTypeSelectActivity.this, SettingWifiActivity.class, true, true, false, 2000);
                        } else {
                            // 输入错误后--> 并当前剩余次数 < 0
                            if (!currentRemain()) {
                                // 跳转到PUK设置界面
                                ChangeActivity.toActivity(ConnectTypeSelectActivity.this, SettingPukActivity.class, true, true, false, 0);
                            }
                        }

                    }
                }.connect(mPinPassword.getText().toString().trim());


                // DataValue data = new DataValue();
                // data.addParam("pin", mPinPassword.getText().toString());
                // mBusinessMgr.sendRequestMessage(MessageUti.SIM_UNLOCK_PIN_REQUEST, data);
                break;

            // TOGO 2017/6/14 move to setupwizard activity
            // tryagain--> SIM WAN选择界面
            case R.id.mRp_connectStatus_tryagain:
                hideAllLayout();
                mHeaderContainer.setVisibility(View.VISIBLE);
                mNormalContainer.setVisibility(View.VISIBLE);
                mHeardTitle.setText(R.string.main_header_linkhub);
                break;

            // TOGO 2017/6/14 move to setupwizard activity
            // 跳到home页按钮
            case R.id.mTv_connectStatus_home:
                CPEConfig.getInstance().setQuickSetupFlag();
                ChangeActivity.toActivity(this, HomeActivity.class, true, true, false, 0);
                break;

            default:
                break;
        }
    }

    
    /* -------------------------------------------- HELPER -------------------------------------------- */

    // TOGO 2017/6/14 Go
    /**
     * 点击sim卡逻辑
     */
    private void simClick() {
        boolean sim;

        // if (mBusinessMgr.getSimStatus().m_SIMState == ENUM.SIMState.PinRequired) { // 再次判断SIM状态

        // TOAT: 测试时暂时使用标记位为true START
        if (test) {
            sim = test;
        } else {
            sim = (mBusinessMgr.getSimStatus().m_SIMState == ENUM.SIMState.PinRequired) ? true : false;
        }
        // TOAT: 测试时暂时使用标记位为true END

        if (sim) {
            hideAllLayout();
            mHeaderBackIv.setVisibility(View.VISIBLE);
            mHeaderContainer.setVisibility(View.VISIBLE);
            mHeaderSkipTv.setVisibility(View.VISIBLE);
            mHandlePinContainer.setVisibility(View.VISIBLE);
            mHeardTitle.setText(R.string.main_header_mobile_broadband);
            pinRemainingTimes = mBusinessMgr.getSimStatus().m_nPinRemainingTimes;
            mPasswordTimes.setText(pinRemainingTimes + "");
            if (pinRemainingTimes < 3) {
                mPasswordTimes.setTextColor(getResources().getColor(R.color.red));
                mPinPasswordDes.setTextColor(getResources().getColor(R.color.red));
            } else {
                mPasswordTimes.setTextColor(getResources().getColor(R.color.black_text));
                mPinPasswordDes.setTextColor(getResources().getColor(R.color.black_text));
            }

            mHeaderSkipTv.setOnClickListener(this);
            mHeaderBackIv.setOnClickListener(this);

        } else {
            // Toast.makeText(getApplicationContext(), "enter wifisetting", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(getApplicationContext(), SettingWifiActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);
            finishQuickSetup(false);
        }
    }

    // TOGO 2017/6/14 to pinCodeFragment
    /**
     * 根据当前剩余次数进行限定操作
     *
     * @return
     */
    private boolean currentRemain() {
        // 1.获取最近一次剩余次数
        int mCurrentRemainTime = mBusinessMgr.getSimStatus().m_nPinRemainingTimes;
        // 2.设置UI
        mPasswordTimes.setText(mCurrentRemainTime + "");
        mPinPassword.setTextColor(Color.RED);
        mPinPasswordDes.setText("attempts remaining");
        // 3.剩余次数如果小等于0--> 则屏蔽输入控件
        if (mCurrentRemainTime <= 0) {
            mPinPassword.setEnabled(false);
            mPinPassword.setFocusable(false);
            mPinPassword.setClickable(false);
            mPinPassword.setTextColor(Color.GRAY);
            return false;
        }
        return true;
    }

    // TOGO 2017/6/14 GO
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mKeyTime) > 2000) {
            mKeyTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.home_exit_app, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    // TOGO 2017/6/14 : split for connecttypefragment & pinCodeFragment
    @Override
    protected void onResume() {
        super.onResume();
        // 重新注册动态广播
        registerReceiver(mReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));
        registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
        registerReceiver(mReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PIN_REQUEST));

        mBusinessMgr.sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
    }

    // TOGO 2017/6/14 GO
    @Override
    protected void onPause() {
        super.onPause();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    // TOGO 2017/6/14 GO
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
        if (mForceLoginDlg != null)
            mForceLoginDlg.destroyDialog();
        mBusinessMgr = null;
    }

    // TOGO 2017/6/14 GO
    private void handleLoginError(int titleId, int messageId, boolean showDialog, final boolean retryLogin) {
        if (mConfirmDialog != null) {
            mConfirmDialog.destroyDialog();
        }
        mConfirmDialog = CommonErrorInfoDialog.getInstance(this);
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
                    ConnectTypeSelectActivity.this.finish();
                }
            }

        });
        mConfirmDialog.showDialog(getString(titleId), getString(messageId));
    }

    // TOGO 2017/6/14 Go
    //直接跳过设置到主界面中
    private void finishQuickSetup(boolean setFlag) {
        if (setFlag) {
            CPEConfig.getInstance().setQuickSetupFlag();
        }
        Intent it = new Intent(this, HomeActivity.class);
        CPEConfig.getInstance().setQuickSetupFlag();
        startActivity(it);
        finish();
    }

    // TOGO 2017/6/14 GO
    private void doLogin() {
        mForceLoginDlg = new AutoForceLoginProgressDialog(this);
        mAutoLoginDialog = new AutoLoginProgressDialog(this);
        mAutoLoginDialog.autoLoginAndShowDialog(new AutoLoginProgressDialog.OnAutoLoginFinishedListener() {
            /*
             * Auto LOGIN successfully.
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
                    if (FeatureVersionManager.getInstance().isSupportForceLogin()) {
                        forceLoginSelectDialog = ForceLoginSelectDialog.getInstance(ConnectTypeSelectActivity.this);
                        forceLoginSelectDialog.showDialogAndCancel(getString(R.string.other_login_warning_title), getString(R.string.login_other_user_logined_error_forcelogin_msg), new ForceLoginSelectDialog.OnClickButtonConfirm() {
                            public void onConfirm() {
                                mForceLoginDlg.autoForceLoginAndShowDialog(new AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener() {
                                    public void onLoginSuccess() {
                                        //
                                        // buildStateHandlerChain(false);
                                    }

                                    public void onLoginFailed(String error_code) {
                                        if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD)) {
                                            ErrorDialog errorDialog = ErrorDialog.getInstance(ConnectTypeSelectActivity.this);
                                            errorDialog.setCancelCallback(new ErrorDialog.OnClickBtnCancel() {
                                                @Override
                                                public void onCancel() {
                                                    CPEConfig.getInstance().cleanAllSetupFlag();
                                                    finish();
                                                }
                                            });
                                            errorDialog.showDialog(getString(R.string.login_psd_error_msg), new ErrorDialog.OnClickBtnRetry() {
                                                @Override
                                                public void onRetry() {
                                                    //showLoginDialog();
                                                    doLogin();
                                                }
                                            });
                                        } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                                            //m_loginDlg.getCommonErrorInfoDialog().showDialog(getString(R
                                            // .string.other_login_warning_title),    m_loginDlg
                                            // .getLoginTimeUsedOutString());
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
                    ErrorDialog errorDialog = ErrorDialog.getInstance(ConnectTypeSelectActivity.this);

                    errorDialog.setCancelCallback(new ErrorDialog.OnClickBtnCancel() {
                        @Override
                        public void onCancel() {
                            CPEConfig.getInstance().cleanAllSetupFlag();
                            finish();
                        }
                    });

                    errorDialog.showDialog(getString(R.string.login_psd_error_msg), new ErrorDialog.OnClickBtnRetry() {
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

    // TOGO 2017/6/14 GO
    private void showLoginDialog() {
        mLoginDialog = new LoginDialog(this);
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

    // TOGO 2017/6/14 GO
    class QSBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();

            if (action.equals(MessageUti.CPE_WIFI_CONNECT_CHANGE)) {
                // If WiFi disconnect router, go to Refresh WiFi activity.
                if (!DataConnectManager.getInstance().getCPEWifiConnected()) {
                    context.startActivity(new Intent(context, RefreshWifiActivity.class));
                    finish();
                }

            } else if (action.equals(MessageUti.USER_HEARTBEAT_REQUEST) || action.equals(MessageUti.USER_COMMON_ERROR_32604_REQUEST)) {
                if (response.isValid() && response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_HEARTBEAT_OTHER_USER_LOGIN)) {
                    kickoffLogout();
                }
            } else if (action.equals(MessageUti.USER_LOGOUT_REQUEST)) {
                if (ok) {
                    HomeActivity.m_blLogout = false;
                    HomeActivity.m_blkickoff_Logout = false;
                }
                handleLoginError(R.string.qs_title, R.string.login_kickoff_logout_successful, true, false);
            } else if (action.equals(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
                if (ok) {
                    showSimCard(mBusinessMgr.getSimStatus());
                }
            } else if (action.equalsIgnoreCase(MessageUti.SIM_UNLOCK_PIN_REQUEST)) {
                if (ok) {
                    //PIN解码成功
                    hideAllLayout();
                    mPinSuccessContainer.setVisibility(View.VISIBLE);

                    if (isRememberPassword) {
                        SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).putString(PIN_PASSWORD, mPinPassword.getText().toString());
                    } else {
                        SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).putString(PIN_PASSWORD, "");
                    }

                    //测试显示用的
                    String pinPassword = SharedPrefsUtil.getInstance(ConnectTypeSelectActivity.this).getString(PIN_PASSWORD, "");
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

    // TOGO 2017/6/14 GO
    public void hideAllLayout() {
        mHeaderContainer.setVisibility(View.GONE);//头部
        mHeaderSkipTv.setVisibility(View.GONE);//头部的skip按钮
        mNormalContainer.setVisibility(View.GONE);//普通的内容页
        mHandlePinContainer.setVisibility(View.GONE);//处理pin码页
        mWaitingContainer.setVisibility(View.GONE);//等待页
        mPinSuccessContainer.setVisibility(View.GONE);//pin解码成功页
        mPinFailContainer.setVisibility(View.GONE);//pin解码失败页
    }

    public void backToAllLayout() {
        mHeaderContainer.setVisibility(View.VISIBLE);//头部
        mHeaderSkipTv.setVisibility(View.VISIBLE);//头部的skip按钮
        mNormalContainer.setVisibility(View.VISIBLE);//普通的内容页
    }

    // TOGO 2017/6/14 GO
    public void kickoffLogout() {
        ENUM.UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
        if (m_loginStatus != null && m_loginStatus == ENUM.UserLoginStatus.Logout) {
            HomeActivity.setKickoffLogoutFlag(true);
            BusinessManager.getInstance().sendRequestMessage(MessageUti.USER_LOGOUT_REQUEST, null);
        }
    }

    // TOGO 2017/6/14 GO
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
