package com.alcatel.wifilink.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.Constants;
import com.alcatel.wifilink.EncryptionUtil;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.wlan.AP;
import com.alcatel.wifilink.common.CPEConfig;
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
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.ui.setupwizard.allsetup.TypeBean;
import com.alcatel.wifilink.ui.setupwizard.allsetup.WizardActivity;
import com.alcatel.wifilink.ui.type.ui.WanModeActivity;
import com.alcatel.wifilink.utils.EditUtils;
import com.alcatel.wifilink.utils.OtherUtils;

import org.greenrobot.eventbus.EventBus;

import static com.alcatel.wifilink.R.drawable.general_btn_remember_nor;
import static com.alcatel.wifilink.R.drawable.general_btn_remember_pre;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private Button mApplyBtn;
    private EditText mPasswdEdit;

    private TextView mPromptText;
    private RelativeLayout rl_remenberPsd;

    // private int mRemainingTimes;
    private boolean ischeck = false;
    private ImageView iv_loginCheckbox;
    private TextView mTv_forgotPsd;

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
        mApplyBtn.setEnabled(psd_remember.length() >= 5);
        OtherUtils.setLastSelection(mPasswdEdit);
        // set text watcher
        mPasswdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwd = mPasswdEdit.getText().toString().trim();
                mApplyBtn.setEnabled(passwd.length() >= 5);
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
                // to reset account activity
                ChangeActivity.toActivity(this, SettingAccountActivity.class, true, false, false, 0);
                break;

            case R.id.login_apply_btn:// 登陆按钮
                OtherUtils otherUtils = new OtherUtils();
                otherUtils.setOnVersionListener(needToEncrypt -> toLogin(needToEncrypt));
                otherUtils.getDeviceSwVersion();
                break;
        }
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
            protected void onSuccess(LoginResult result) {
                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                // commit the token
                API.get().updateToken(result.getToken());
                // remember psd
                SharedPrefsUtil.getInstance(LoginActivity.this).putString(Cons.LOGIN_PSD, oriPasswd);
                // 判断连接模式( SIM | WAN )
                checkConnectMode();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
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
                        if (wanStatus == Cons.CONNECTED & simflag) {/* 都有 */
                            ChangeActivity.toActivity(LoginActivity.this, WizardActivity.class, true, true, false, 0);
                            return;
                        }
                        if (wanStatus != Cons.CONNECTED && simflag) {/* 只有SIM卡 */
                            if (simState == Cons.PIN_REQUIRED) {
                                ChangeActivity.toActivity(LoginActivity.this, SimUnlockActivity.class, true, true, false, 0);
                            } else if (simState == Cons.PUK_REQUIRED) {
                                ChangeActivity.toActivity(LoginActivity.this, PukUnlockActivity.class, true, true, false, 0);
                            } else if (simState == Cons.READY) {
                                EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_SIM));
                                ChangeActivity.toActivity(LoginActivity.this, HomeActivity.class, true, true, false, 0);
                            }
                            return;
                        }
                        if (wanStatus == Cons.CONNECTED & !simflag) {/* 只有WAN口 */
                            ChangeActivity.toActivity(LoginActivity.this, WanModeActivity.class, true, true, false, 0);
                            return;
                        }
                        if (wanStatus != Cons.CONNECTED & !simflag) {/* 都没有 */
                            ChangeActivity.toActivity(LoginActivity.this, WizardActivity.class, true, true, false, 0);
                            return;
                        }

                    }
                });
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
        ChangeActivity.toActivity(this, HomeActivity.class, true, true, false, 0);
    }
}
