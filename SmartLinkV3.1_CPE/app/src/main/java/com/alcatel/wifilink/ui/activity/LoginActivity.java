package com.alcatel.wifilink.ui.activity;

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
import com.alcatel.wifilink.common.CPEConfig;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.user.LoginResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.setupwizard.allsetup.SetupWizardActivity;
import com.alcatel.wifilink.utils.EditUtils;
import com.alcatel.wifilink.utils.OtherUtils;

import static android.R.attr.version;
import static com.alcatel.wifilink.R.drawable.general_btn_remember_nor;
import static com.alcatel.wifilink.R.drawable.general_btn_remember_pre;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private Button mApplyBtn;
    private EditText mPasswdEdit;

    private TextView mPromptText;
    private RelativeLayout rl_remenberPsd;

    // private int mRemainingTimes;
    private boolean ischeck;
    private ImageView iv_loginCheckbox;
    private TextView mTv_forgotPsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // mRemainingTimes = getIntent().getIntExtra("remain_times", 0);
        mApplyBtn = (Button) findViewById(R.id.login_apply_btn);
        mApplyBtn.setOnClickListener(this);

        mPasswdEdit = (EditText) findViewById(R.id.login_edit_view);
        // get remember psd
        String psd_remember = SharedPrefsUtil.getInstance(this).getString(Cons.LOGIN_PSD, "");
        mPasswdEdit.setText(psd_remember);
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
        ischeck = SharedPrefsUtil.getInstance(this).getBoolean(Cons.LOGIN_CHECK, false);
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
                // psd
                SharedPrefsUtil.getInstance(this).putString(Cons.LOGIN_PSD, ischeck ? EditUtils.getContent(mPasswdEdit) : "");
                break;

            case R.id.tv_login_forgotPsd:
                // to reset account activity
                ChangeActivity.toActivity(this, SettingAccountActivity.class, true, false, false, 0);
                break;

            case R.id.login_apply_btn:// 登陆按钮
                OtherUtils otherUtils = new OtherUtils();
                otherUtils.setOnVersionListener(needToEncrypt -> toLogin(needToEncrypt));
                otherUtils.getDeviceVersion();
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
                boolean quickSetupFlag = CPEConfig.getInstance().getQuickSetupFlag();
                if (quickSetupFlag) {
                    launchHomeActivity();
                } else {
                    ChangeActivity.toActivity(LoginActivity.this, SetupWizardActivity.class, true, true, false, 0);
                }
                Log.d(TAG, "token = " + result.getToken());
                // commit the token
                API.get().updateToken(result.getToken());
                // remember psd
                SharedPrefsUtil.getInstance(LoginActivity.this).putString(Cons.LOGIN_PSD, oriPasswd);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                if (Cons.PASSWORD_IS_NOT_CORRECT.equals(error.getCode())) {
                    // mRemainingTimes--;
                    // updatePromptText(mRemainingTimes);
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_psd_error_msg));
                } else if (Cons.OTHER_USER_IS_LOGIN.equals(error.getCode())) {
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_other_user_logined_error_msg));
                } else if (Cons.DEVICE_REBOOT.equals(error.getCode())) {
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_login_time_used_out_msg));
                } else if (Cons.GUEST_AP_OR_WEBUI.equals(error.getCode())) {
                    ToastUtil_m.show(LoginActivity.this, getString(R.string.login_login_app_or_webui));
                }
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
        // Intent intent = new Intent(this, HomeActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // this.startActivity(intent);
        ChangeActivity.toActivity(this, HomeActivity.class, true, true, false, 0);
    }
}
