package com.alcatel.wifilink.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.utils.Constants;
import com.alcatel.wifilink.utils.EncryptionUtil;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.user.LoginResult;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.bean.AcBean;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.wizard.allsetup.TypeBean;
import com.alcatel.wifilink.ui.wizard.allsetup.WizardActivity;
import com.alcatel.wifilink.ui.type.ui.WanModeActivity;
import com.alcatel.wifilink.utils.EditUtils;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.ProgressUtils;
import com.alcatel.wifilink.utils.ScreenSize;

import org.greenrobot.eventbus.EventBus;

import static com.alcatel.wifilink.R.drawable.general_btn_remember_nor;
import static com.alcatel.wifilink.R.drawable.general_btn_remember_pre;

public class LoginActivity extends BaseActivityWithBack implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private Button mApplyBtn;
    private EditText mPasswdEdit;

    private TextView mPromptText;
    private RelativeLayout rl_remenberPsd;
    public static String GUIDE_FLAG = "GUIDE_FLAG";// 向导页引导标记

    // private int mRemainingTimes;
    private boolean ischeck = false;
    private ImageView iv_loginCheckbox;
    private TextView mTv_forgotPsd;
    private PopupWindows resetPop;
    private ProgressDialog progressPop;
    private TextView tvRemberPsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // is remember checkbox check
        ischeck = SP.getInstance(this).getBoolean(Cons.LOGIN_CHECK, false);
        // mRemainingTimes = getIntent().getIntExtra("remain_times", 0);
        mApplyBtn = (Button) findViewById(R.id.login_apply_btn);
        mApplyBtn.setText(getString(R.string.login_login_btn));
        mApplyBtn.setOnClickListener(this);

        mPasswdEdit = (EditText) findViewById(R.id.login_edit_view);
        // get remember psd
        String psd_remember = SP.getInstance(this).getString(Cons.LOGIN_PSD, "");
        mPasswdEdit.setText(ischeck ? psd_remember : "");
        mApplyBtn.setEnabled(psd_remember.length() >= 4);
        OtherUtils.setLastSelection(mPasswdEdit);
        // set text watcher
        mPasswdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwd = mPasswdEdit.getText().toString().trim();
                mApplyBtn.setEnabled(passwd.length() >= 4);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPromptText = (TextView) findViewById(R.id.tv_time_remain);
        mPromptText.setText(getString(R.string.login_enter_password_tip));
        // updatePromptText(mRemainingTimes);

        tvRemberPsd = (TextView) findViewById(R.id.tv_remember_psd);
        tvRemberPsd.setText(getString(R.string.login_remenber_psd));

        // 记住密码
        rl_remenberPsd = (RelativeLayout) findViewById(R.id.rl_login_remenberPsd);
        rl_remenberPsd.setOnClickListener(this);
        iv_loginCheckbox = (ImageView) findViewById(R.id.iv_login_checkbox);
        iv_loginCheckbox.setImageResource(ischeck ? general_btn_remember_pre : general_btn_remember_nor);

        // 忘记密码
        mTv_forgotPsd = (TextView) findViewById(R.id.tv_login_forgotPsd);
        mTv_forgotPsd.setText(getString(R.string.login_forgot_password));
        mTv_forgotPsd.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OtherUtils.stopHomeTimer();
        OtherUtils.clearContexts(this.getClass().getSimpleName());
    }

    @Override
    public void onBackPressed() {
        OtherUtils.kill();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_login_remenberPsd:// 记住密码
                ischeck = !ischeck;
                iv_loginCheckbox.setImageResource(ischeck ? general_btn_remember_pre : general_btn_remember_nor);
                // remember flag
                SP.getInstance(this).putBoolean(Cons.LOGIN_CHECK, ischeck);
                // password
                SP.getInstance(this).putString(Cons.LOGIN_PSD, ischeck ? EditUtils.getContent(mPasswdEdit) : "");
                break;

            case R.id.tv_login_forgotPsd:
                showResetPop();
                break;

            case R.id.login_apply_btn:// 登陆按钮
                progressPop = new ProgressUtils(this).getProgressPop(getString(R.string.connecting));
                OtherUtils otherUtils = new OtherUtils();
                otherUtils.setOnSwVersionListener(needToEncrypt -> toLogin(needToEncrypt));
                otherUtils.getDeviceSwVersion();
                break;
        }
    }

    /* **** showResetPop **** */
    private void showResetPop() {
        ScreenSize.SizeBean size = ScreenSize.getSize(this);
        int width = (int) (size.width * 0.80f);
        int height = (int) (size.height * 0.25f);
        View inflate = View.inflate(this, R.layout.pop_resetdevice, null);
        View cancel_reset = inflate.findViewById(R.id.tv_pop_forgot_cancel);
        View ok_reset = inflate.findViewById(R.id.tv_pop_forgot_ok);
        cancel_reset.setOnClickListener(v -> resetPop.dismiss());
        ok_reset.setOnClickListener(v -> resetDevice());/* 重启设备 */
        resetPop = new PopupWindows(this, inflate, width, height, true);
    }

    /* 重启设备 */
    private void resetDevice() {
        if (resetPop != null) {
            resetPop.dismiss();
        }

        ProgressDialog progressPop = new ProgressUtils(this).getProgressPop(getString(R.string.resetting));
        API.get().resetDevice(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                if (progressPop != null) {
                    progressPop.dismiss();
                }
                ToastUtil_m.show(LoginActivity.this, getString(R.string.setting_reset_success));
                // CA.toActivity(LoginActivity.this, LoadingActivity.class, false, true, false, 0);
                LoginActivity.this.finish();
                OtherUtils.kill();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                if (progressPop != null) {
                    progressPop.dismiss();
                }
                ToastUtil_m.show(LoginActivity.this, getString(R.string.setting_reset_failed));
            }
        });
    }

    /* **** 登陆: toLogin **** */
    private void toLogin(boolean needEncrypt) {
        // TODO: 2017/7/13 加密算法--> FW完成加密机制后将下一句代码注释
        //needEncrypt = false;// FW完成加密机制后将该句代码注释

        // 明文用户+密码
        String account = Constants.USER_NAME_ADMIN;
        String oriAccount = account;
        String passwd = mPasswdEdit.getText().toString().trim();
        String oriPasswd = passwd;
        // 加密
        account = needEncrypt ? EncryptionUtil.encryptUser(account) : account;
        passwd = needEncrypt ? EncryptionUtil.encryptUser(passwd) : passwd;

        // get data
        String finalPasswd = passwd;
        API.get().login(account, passwd, new MySubscriber<LoginResult>() {
            @Override
            protected void onSuccess(LoginResult loginResult) {
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState loginState) {
                        if (loginState.getState() == Cons.LOGIN) {
                            // get token
                            API.get().updateToken(loginResult.getToken());
                            // remember psd
                            SP.getInstance(LoginActivity.this).putString(Cons.LOGIN_PSD, oriPasswd);
                            checkConnectMode();  // 判断连接模式( SIM | WAN )
                        }
                    }

                    @Override
                    protected void onFailure() {
                        super.onFailure();
                        popDismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        popDismiss();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        popDismiss();
                        if (error.getCode().equalsIgnoreCase(Cons.GET_LOGIN_STATE_FAILED)) {
                            ToastUtil_m.show(LoginActivity.this, getString(R.string.connection_timed_out));
                        } else {
                            ToastUtil_m.show(LoginActivity.this, getString(R.string.login_failed));
                        }
                    }
                });
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                if (progressPop != null) {
                    progressPop.dismiss();
                }
                if (Cons.PASSWORD_IS_NOT_CORRECT.equals(error.getCode())) {
                    showRemainTimes();
                } else if (Cons.OTHER_USER_IS_LOGIN.equals(error.getCode())) {
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_other_user_logined_error_msg));
                } else if (Cons.DEVICE_REBOOT.equals(error.getCode())) {
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_login_time_used_out_msg));
                } else if (Cons.GUEST_AP_OR_WEBUI.equals(error.getCode())) {
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_login_app_or_webui));
                } else {
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_failed));
                }
            }
        });
    }

    // TODO: 2017/7/21  
    private void checkConnectMode() {

        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                int wanStatus = result.getStatus();// 获取WAN口状态
                API.get().getSimStatus(new MySubscriber<SimStatus>() {
                    @Override
                    protected void onSuccess(SimStatus result) {
                        int simState = result.getSIMState();// 获取SIM卡状态
                        boolean simflag = simState == Cons.READY || simState == Cons.PIN_REQUIRED || simState == Cons.PUK_REQUIRED;
                        popDismiss();
                        ToastUtil_m.show(LoginActivity.this, getString(R.string.succeed));
                        if (wanStatus == Cons.CONNECTED & simflag) {/* 都有 */
                            // 是否进入过向导页: GUIDE_FLAG = WIFI_GUIDE_FLAG & DATA_PLAN_FLAG
                            if (SP.getInstance(LoginActivity.this).getBoolean(GUIDE_FLAG, false)) {
                                EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_WAN));
                                CA.toActivity(LoginActivity.this, HomeActivity.class, false, true, false, 0);
                                return;
                            } else {
                                CA.toActivity(LoginActivity.this, WizardActivity.class, false, true, false, 0);
                                return;
                            }
                        }
                        if (wanStatus != Cons.CONNECTED && simflag) {/* 只有SIM卡 */
                            if (simState == Cons.PIN_REQUIRED) {// 要求PIN码
                                EventBus.getDefault().postSticky(new AcBean(LoginActivity.class));
                                CA.toActivity(LoginActivity.this, SimUnlockActivity.class, false, true, false, 0);
                            } else if (simState == Cons.PUK_REQUIRED) {// 要求PUK码
                                CA.toActivity(LoginActivity.this, PukUnlockActivity.class, false, true, false, 0);
                            } else if (simState == Cons.READY) {// SIM卡已经准备好
                                EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_SIM));
                                // CA.toActivity(LoginActivity.this, HomeActivity.class, false, true, false, 0);
                                OtherUtils.loginSkip(LoginActivity.this);
                            } else {// 其他情况
                                EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_SIM));
                                // CA.toActivity(LoginActivity.this, HomeActivity.class, false, true, false, 0);
                                OtherUtils.loginSkip(LoginActivity.this);
                            }
                            return;
                        }
                        if (wanStatus == Cons.CONNECTED & !simflag) {/* 只有WAN口 */

                            // 是否进入过wan连接模式
                            boolean isWanmode = SP.getInstance(LoginActivity.this).getBoolean(Cons.WAN_MODE_FLAG, false);
                            if (isWanmode) {// 进入过wan连接界面--> 进入其他界面
                                OtherUtils.loginSkip(LoginActivity.this);
                            } else {// 进入wan类型选择界面
                                CA.toActivity(LoginActivity.this, WanModeActivity.class, false, true, false, 0);
                            }

                            return;
                        }
                        if (wanStatus != Cons.CONNECTED & !simflag) {/* 都没有 */
                            CA.toActivity(LoginActivity.this, WizardActivity.class, false, true, false, 0);
                            return;
                        }
                    }
                });
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                popDismiss();
                ToastUtil_m.show(LoginActivity.this, getString(R.string.login_failed));
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                popDismiss();
                ToastUtil_m.show(LoginActivity.this, getString(R.string.login_failed));
            }
        });
    }

    // 显示剩余次数
    private void showRemainTimes() {
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                int remainingTimes = result.getLoginRemainingTimes();
                String tips = getString(R.string.login_psd_error_msg);
                String remainTips = String.format(tips, remainingTimes);
                ToastUtil_m.show(LoginActivity.this, remainTips);
            }
        });
    }

    private void updatePromptText(int remainingTimes) {
        String text;
        if (remainingTimes == 0) {
            text = getString(R.string.times_remain_zero);
        } else if (remainingTimes == 1) {
            text = getString(R.string.times_remain_one);
        } else {
            text = getString(R.string.times_remain_many, remainingTimes);
        }
        mPromptText.setText(text);
    }

    private void launchHomeActivity() {
        CA.toActivity(this, HomeActivity.class, false, true, false, 0);
    }

    public void popDismiss() {
        if (progressPop != null) {
            progressPop.dismiss();
        }
    }
}
