package com.alcatel.wifilink.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.Constants;
import com.alcatel.wifilink.EncryptionUtil;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.user.LoginResult;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.setupwizard.allsetup.TypeBean;
import com.alcatel.wifilink.ui.setupwizard.allsetup.WizardActivity;
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

    // private int mRemainingTimes;
    private boolean ischeck = false;
    private ImageView iv_loginCheckbox;
    private TextView mTv_forgotPsd;
    private PopupWindows resetPop;
    private ProgressDialog progressPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // is remember checkbox check
        ischeck = SharedPrefsUtil.getInstance(this).getBoolean(Cons.LOGIN_CHECK, false);
        // mRemainingTimes = getIntent().getIntExtra("remain_times", 0);
        mApplyBtn = (Button) findViewById(R.id.login_apply_btn);
        mApplyBtn.setOnClickListener(this);

        mPasswdEdit = (EditText) findViewById(R.id.login_edit_view);
        // get remember psd
        String psd_remember = SharedPrefsUtil.getInstance(this).getString(Cons.LOGIN_PSD, "");
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
        // updatePromptText(mRemainingTimes);

        // 记住密码
        rl_remenberPsd = (RelativeLayout) findViewById(R.id.rl_login_remenberPsd);
        rl_remenberPsd.setOnClickListener(this);
        iv_loginCheckbox = (ImageView) findViewById(R.id.iv_login_checkbox);
        iv_loginCheckbox.setImageResource(ischeck ? general_btn_remember_pre : general_btn_remember_nor);

        // 忘记密码
        mTv_forgotPsd = (TextView) findViewById(R.id.tv_login_forgotPsd);
        mTv_forgotPsd.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OtherUtils.stopAutoTimer();
        OtherUtils.clearContexts();
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
                SharedPrefsUtil.getInstance(this).putBoolean(Cons.LOGIN_CHECK, ischeck);
                // password
                SharedPrefsUtil.getInstance(this).putString(Cons.LOGIN_PSD, ischeck ? EditUtils.getContent(mPasswdEdit) : "");
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
        int width = (int) (size.width * 0.75f);
        int height = (int) (size.height * 0.20f);
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
                ChangeActivity.toActivity(LoginActivity.this, LoadingActivity.class, false, true, false, 0);
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
        Log.d("ma_encry", "needEncrypt: " + needEncrypt);
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
                Log.d("ma_token", "token1: " + loginResult.getToken());
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState loginState) {
                        if (loginState.getState() == Cons.LOGIN) {
                            // commit the token
                            API.get().updateToken(loginResult.getToken());
                            Log.d("ma_token", "token2: " + loginResult.getToken());
                            // remember psd
                            SharedPrefsUtil.getInstance(LoginActivity.this).putString(Cons.LOGIN_PSD, oriPasswd);
                            // 判断连接模式( SIM | WAN )
                            checkConnectMode();
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
                int wanStatus = result.getStatus();
                API.get().getSimStatus(new MySubscriber<SimStatus>() {
                    @Override
                    protected void onSuccess(SimStatus result) {
                        int simState = result.getSIMState();
                        boolean simflag = simState == Cons.READY || simState == Cons.PIN_REQUIRED || simState == Cons.PUK_REQUIRED;
                        popDismiss();
                        ToastUtil_m.show(LoginActivity.this, getString(R.string.succeed));
                        if (wanStatus == Cons.CONNECTED & simflag) {/* 都有 */
                            ChangeActivity.toActivity(LoginActivity.this, WizardActivity.class, false, true, false, 0);
                            return;
                        }
                        if (wanStatus != Cons.CONNECTED && simflag) {/* 只有SIM卡 */
                            if (simState == Cons.PIN_REQUIRED) {// 要求PIN码
                                ChangeActivity.toActivity(LoginActivity.this, SimUnlockActivity.class, false, true, false, 0);
                            } else if (simState == Cons.PUK_REQUIRED) {// 要求PUK码
                                ChangeActivity.toActivity(LoginActivity.this, PukUnlockActivity.class, false, true, false, 0);
                            } else if (simState == Cons.READY) {// SIM卡已经准备好
                                EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_SIM));
                                // ChangeActivity.toActivity(LoginActivity.this, HomeActivity.class, false, true, false, 0);
                                OtherUtils.loginSkip(LoginActivity.this);
                            } else {// 其他情况
                                EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_SIM));
                                // ChangeActivity.toActivity(LoginActivity.this, HomeActivity.class, false, true, false, 0);
                                OtherUtils.loginSkip(LoginActivity.this);
                            }
                            return;
                        }
                        if (wanStatus == Cons.CONNECTED & !simflag) {/* 只有WAN口 */
                            ChangeActivity.toActivity(LoginActivity.this, WanModeActivity.class, false, true, false, 0);
                            return;
                        }
                        if (wanStatus != Cons.CONNECTED & !simflag) {/* 都没有 */
                            ChangeActivity.toActivity(LoginActivity.this, WizardActivity.class, false, true, false, 0);
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
        ChangeActivity.toActivity(this, HomeActivity.class, false, true, false, 0);
    }

    public void popDismiss() {
        if (progressPop != null) {
            progressPop.dismiss();
        }
    }
}
