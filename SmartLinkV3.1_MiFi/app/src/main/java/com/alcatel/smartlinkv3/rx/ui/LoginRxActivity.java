package com.alcatel.smartlinkv3.rx.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.appwidget.PopupWindows;
import com.alcatel.smartlinkv3.appwidget.RippleView;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.rx.helper.SystemInfoHelper;
import com.alcatel.smartlinkv3.rx.impl.login.LoginResult;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.alcatel.smartlinkv3.rx.tools.Logs;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.EncryptionUtil;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.SPUtils;
import com.alcatel.smartlinkv3.utils.ScreenSize;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;
import com.alcatel.smartlinkv3.utils.TokenUtils;
import com.orhanobut.logger.Logger;

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
    RippleView btLogin;// 登陆按钮
    @BindView(R.id.tv_login_forgotPsd)
    TextView tvLoginForgotPsd;// 忘记密码

    private String ADMIN = "admin";
    private String REMEMBER_FLAG = "REMEMBER_FLAG";
    private String REMEMBER_TEXT = "REMEMBER_TEXT";
    private boolean isRemember;
    private ProgressDialog pgd;
    private PopupWindows resetPop;// 重啟會話框
    private PopupWindows forcePop;// 強制登陸會話框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.t("ma_wifi").v(getClass().getSimpleName());
        OtherUtils.setWifiActive(this, true);
        setContentView(R.layout.activity_login_rx);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();// 初始化密码框
        OtherUtils.setWifiActive(this, true);
        // 停止所有的定时器
        if (SettingAccountActivity.isLogOutClick) {
            OtherUtils.clearAllTimer();
            OtherUtils.clearAllContext();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OtherUtils.clearAllContext();
        finish();
    }

    private void init() {
        etLoginPsd.setSelection(OtherUtils.getEdittext(etLoginPsd).length());
        // 初始化密码输入框显示
        isRemember = SPUtils.getInstance(this).getBoolean(REMEMBER_FLAG, false);
        if (isRemember) {
            String psd = SPUtils.getInstance(this).getString(REMEMBER_TEXT, "");
            etLoginPsd.setText(psd);
            ivCheckbox.setImageResource(R.drawable.checkbox_android_on);
        } else {
            etLoginPsd.setText("");
            ivCheckbox.setImageResource(R.drawable.checkbox_android_off);
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
        // 延遲2秒
        btLogin.postDelayed(this::checkBoardConn, 2000);

    }

    /**
     * 检测是否连接上硬件
     */
    private void checkBoardConn() {
        // 2.1.判断是否需要加密
        API.get().getLoginState(new MySubscriber<LoginState>() {

            private String password;
            private String admin;

            @Override
            protected void onSuccess(LoginState result) {
                Logs.v("ma_rx", "login LoginState");
                if (result.getState() == Cons.THE_LOGIN_TIMES_USED_OUT) {
                    OtherUtils.hideProgressPop(pgd);
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
                        Logs.v("ma_rx", "login success");
                        API.get().updateToken(result.getToken());
                        TokenUtils.setToken(result.getToken() + "");// 2.4.保存token
                        API.get().getLoginState(new MySubscriber<LoginState>() {
                            @Override
                            protected void onSuccess(LoginState result) {
                                if (result.getState() == Cons.LOGIN) {
                                    //OtherUtils.initBusiness();// 2.5.启动请求接口
                                    OtherUtils.hideProgressPop(pgd);// 2.6.隐藏进度条
                                    toWizard();
                                }
                            }

                            @Override
                            protected void onResultError(ResponseBody.Error error) {
                                Logs.v("ma_login", "get loginstatus failed");
                                OtherUtils.hideProgressPop(pgd);// 2.6.隐藏进度条
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logs.v("ma_login", "login onError");
                        OtherUtils.hideProgressPop(pgd);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        Logs.v("ma_login", "error: " + error.getMessage() + ";code:" + error.getCode());
                        OtherUtils.hideProgressPop(pgd);
                        showErrorToast(error);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("ma_rx:login onError");
                OtherUtils.hideProgressPop(pgd);
                if (!OtherUtils.isWiFiActive(LoginRxActivity.this)) {
                    ToastUtil_m.show(LoginRxActivity.this, getString(R.string.no_wifi));
                }
                ChangeActivity.toActivity(LoginRxActivity.this, RefreshWifiActivity.class, false, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                System.out.println("ma_rx:login ResponseBody");
                ToastUtil_m.show(LoginRxActivity.this, getString(R.string.refresh_wifi_tip));
            }
        });
    }

    /**
     * 是否进入向导页
     */
    private void toWizard() {
        // 2.7.是否进入过快速设置
        boolean quick = SPUtils.getInstance(this).getBoolean(Conn.QUICK_SETUP, false);
        if (quick) {
            ChangeActivity.toActivityNormal(this, MainActivity.class, false);
        } else {
            SystemInfoHelper sf = new SystemInfoHelper(this);
            sf.setOnResultErrorListener(attr -> to(QuickSetupRxActivity.class, true));
            sf.setOnOldVersionListener(attr -> to(QuickSetupRxY900Activity.class, true));
            sf.setOnNewVersionListener(attr -> to(QuickSetupRxActivity.class, true));
            sf.get();
        }
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
            OtherUtils.hideProgressPop(pgd);
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        builder.show();
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
        int height = (int) (size.height * 0.21f);
        Drawable bg = getDrawables(R.drawable.bg_pop_conner);
        View inflate = View.inflate(this, R.layout.pop_resetdevice, null);
        inflate.findViewById(R.id.tv_pop_login_reset_ok).setOnClickListener(v -> resetPop.dismiss());
        resetPop = new PopupWindows(this, inflate, width, height, true, bg);
    }

    /**
     * 錯誤碼分類處理
     *
     * @param error
     */
    public void showErrorToast(ResponseBody.Error error) {
        switch (error.getCode()) {
            case "010101":
                ToastUtil_m.show(this, getString(R.string.login_psd_error_msg));
                break;
            case "010102":
                showForcePop();// 強制登陸
                ToastUtil_m.show(this, getString(R.string.login_other_user_logined_error_msg));
                break;
            case "010103":
                ToastUtil_m.show(this, getString(R.string.login_login_time_used_out_msg));
                break;
            case "010104":
                ToastUtil_m.show(this, getString(R.string.login_failed));
                break;
            case "010601":
                ToastUtil_m.show(this, getString(R.string.login_psd_error_msg));
                break;
            case "010602":
                ToastUtil_m.show(this, getString(R.string.login_login_time_used_out_msg));
                break;
            default:
                ChangeActivity.toActivity(this, RefreshWifiActivity.class, false, true, false, 0);
                break;
        }
    }

    /**
     * 顯示強制登陸對話框
     */
    private void showForcePop() {
        ScreenSize.SizeBean size = ScreenSize.getSize(this);
        int w = (int) (size.width * 0.8f);
        int h = (int) (size.height * 0.25f);
        View inflate = View.inflate(this, R.layout.pop_forlogin, null);
        inflate.findViewById(R.id.tv_pop_forceLogin_ok).setOnClickListener(v -> forcePop.dismiss());
        forcePop = new PopupWindows(this, inflate, w, h, new ColorDrawable(Color.TRANSPARENT));
    }

}
