package com.alcatel.smartlinkv3.rx.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.appwidget.PopupWindows;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.rx.impl.login.LoginResult;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.alcatel.smartlinkv3.rx.tools.Logs;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;
import com.alcatel.smartlinkv3.ui.activity.QuickSetupActivity;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.EncryptionUtil;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.SPUtils;
import com.alcatel.smartlinkv3.utils.ScreenSize;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;
import com.alcatel.smartlinkv3.utils.TokenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginRxActivity extends BaseRxActivity {

    @BindView(R.id.et_login_psd)
    EditText etLoginPsd;// 密码输入框
    @BindView(R.id.iv_login_remenberPsd_checkbox)
    ImageView ivCheckbox;// 记住密码图标
    @BindView(R.id.tv_login_rememberPsd_text)
    TextView tvCheckbox;// 记住密码文本
    @BindView(R.id.bt_login)
    Button btLogin;// 登陆按钮
    @BindView(R.id.tv_login_forgotPsd)
    TextView tvLoginForgotPsd;// 忘记密码

    private String ADMIN = "admin";
    private String REMEMBER_FLAG = "REMEMBER_FLAG";
    private String REMEMBER_TEXT = "REMEMBER_TEXT";
    private boolean isRemember;
    private ProgressDialog pgd;
    private PopupWindows resetPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_rx);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 停止所有的定时器
        if (SettingAccountActivity.isLogOutClick) {
            OtherUtils.clearAllTimer();
            OtherUtils.clearAllContext();
        }
    }

    private void init() {
        // 初始化密码输入框显示
        isRemember = SPUtils.getInstance(this).getBoolean(REMEMBER_FLAG, false);
        if (isRemember) {
            String psd = SPUtils.getInstance(this).getString(REMEMBER_TEXT, "");
            etLoginPsd.setText(psd);
            ivCheckbox.setImageResource(R.drawable.checkbox_android_on);
        }
    }

    @OnClick({R.id.iv_login_remenberPsd_checkbox, R.id.tv_login_rememberPsd_text, R.id.bt_login, R.id.tv_login_forgotPsd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_login_remenberPsd_checkbox:
            case R.id.tv_login_rememberPsd_text:
                modifyUi();// checkbox
                break;
            case R.id.bt_login:
                login();// 登陆
                break;
            case R.id.tv_login_forgotPsd:
                showResetPop();
                break;
        }
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */

    /**
     * 登陆操作
     */
    private void login() {
        // 0.记住密码
        rememberPsd();
        // 1.密码为空直接返回
        if (isNotMatch()) {
            ToastUtil_m.show(this, R.string.change_passowrd_invalid_password);
            return;
        }
        // 2.登陆
        pgd = OtherUtils.showProgressPop(this);
        // 2.1.判断是否需要加密
        API.get().getLoginState(new MySubscriber<LoginState>() {

            private String password;
            private String admin;

            @Override
            protected void onSuccess(LoginState result) {
                if (result.getLoginRemainingTimes() <= 0) {
                    showResetDeviceDialog(result);// 超过登陆限制次数
                    return;
                }
                // 2.1.1.默认帐号密码
                admin = ADMIN;
                password = OtherUtils.getEdittext(etLoginPsd);
                // 2.2.是否需要加密
                if (result.getPwEncrypt() == Cons.NEED_ENCRYPT) {
                    admin = EncryptionUtil.encrypt(admin);
                    password = EncryptionUtil.encrypt(password);
                }
                // 2.3.请求登陆接口
                API.get().login(admin, password, new MySubscriber<LoginResult>() {
                    @Override
                    protected void onSuccess(LoginResult result) {
                        TokenUtils.setToken(result.getToken() + "");// 2.4.保存token
                        OtherUtils.initBusiness();// 2.5.启动请求接口
                        OtherUtils.hideProgressPop(pgd);// 2.6.隐藏进度条
                        // 2.7.是否进入过快速设置
                        Class clazz = MainActivity.class;
                        if (!CPEConfig.getInstance().getQuickSetupFlag()) {
                            clazz = QuickSetupActivity.class;
                        }
                        ChangeActivity.toActivity(LoginRxActivity.this, clazz, false, true, false, 0);// 跳转
                    }

                    @Override
                    public void onError(Throwable e) {
                        OtherUtils.hideProgressPop(pgd);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        Logs.v("ma_login", "error: " + error.getMessage());
                        OtherUtils.hideProgressPop(pgd);
                        ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_failed));
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_failed));
            }
        });
    }

    /**
     * 超过登陆限制次数
     */
    private void showResetDeviceDialog(LoginState result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.other_login_warning_title);
        int remainTime = result.getLoginRemainingTimes();// 剩余次数
        String tip = getString(R.string.remain_times);
        String text = String.format(tip, remainTime);
        builder.setMessage(text + "\n" + getString(R.string.login_login_time_used_out_msg));
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create();
    }

    /**
     * 记住密码
     */
    private void rememberPsd() {
        SPUtils.getInstance(this).putBoolean(REMEMBER_FLAG, isRemember);// 保存回显标记
        SPUtils.getInstance(this).putString(REMEMBER_TEXT, isRemember ? OtherUtils.getEdittext(etLoginPsd) : "");// 保存回显密码
    }

    /**
     * @return 是否符合密码规则
     * @des true: 不符合规则
     */
    public boolean isNotMatch() {
        boolean empty = TextUtils.isEmpty(OtherUtils.getEdittext(etLoginPsd));// 为空
        int length = OtherUtils.getEdittext(etLoginPsd).length();
        boolean nolen = length < 4 || length > 16;// 位数不匹配
        return empty && nolen;
    }

    /**
     * 修改checkboxUI
     */
    private void modifyUi() {
        isRemember = !isRemember;// 切换标记
        ivCheckbox.setImageResource(isRemember ? R.drawable.checkbox_android_on : R.drawable.checkbox_android_off);// 切换UI
    }


    /**
     * 显示重置对话框
     */
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

    /**
     * 重启设备
     */
    private void resetDevice() {
        pgd = OtherUtils.showProgressPop(this);
        API.get().resetDevice(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(LoginRxActivity.this, getString(R.string.setting_reset_success));
                OtherUtils.setWifiActive(LoginRxActivity.this, false);
                ChangeActivity.toActivity(LoginRxActivity.this, RefreshWifiActivity.class, false, true, false, 0);
                OtherUtils.hideProgressPop(pgd);
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                ToastUtil_m.show(LoginRxActivity.this, getString(R.string.setting_reset_failed));
            }
        });
    }

}
