package com.alcatel.wifilink.rx.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.SP;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.user.LoginResult;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.bean.PinPukBean;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.rx.helper.CheckBoard;
import com.alcatel.wifilink.utils.Constants;
import com.alcatel.wifilink.utils.EncryptionUtil;
import com.alcatel.wifilink.utils.OtherUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginRxActivity extends BaseActivityWithBack {

    @BindView(R.id.et_loginRx)
    EditText etLoginRx;
    @BindView(R.id.iv_loginRx_checkbox)
    ImageView ivLoginRxCheckbox;
    @BindView(R.id.tv_remember_psd)
    TextView tvRememberPsd;
    @BindView(R.id.rl_login_remenberPsd)
    RelativeLayout rlLoginRemenberPsd;
    @BindView(R.id.bt_loginRx)
    Button btLoginRx;
    @BindView(R.id.tv_loginRx_forgot)
    TextView tvLoginRxForgot;

    private boolean isRemem;
    private ProgressDialog pgd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_rx);
        ButterKnife.bind(this);
        initRes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OtherUtils.clearAllTimer();
        OtherUtils.stopHomeTimer();
        OtherUtils.clearContexts(getClass().getSimpleName());
        stopHomeHeart();
    }

    public void stopHomeHeart() {
        if (HomeRxActivity.heartTimer != null) {
            HomeRxActivity.heartTimer.stop();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OtherUtils.clearAllTimer();
        OtherUtils.clearContexts(getClass().getSimpleName());
        finish();
        OtherUtils.kill();
    }

    private void initRes() {
        isRemem = SP.getInstance(this).getBoolean(Cons.LOGIN_REMEM, false);
        String password = SP.getInstance(this).getString(Cons.LOGIN_RXPSD, "");
        ivLoginRxCheckbox.setImageResource(isRemem ? R.drawable.general_btn_remember_pre : R.drawable.general_btn_remember_nor);
        etLoginRx.setText(isRemem ? password : "");
        etLoginRx.setSelection(OtherUtils.getEdContent(etLoginRx).length());
    }

    @OnClick({R.id.rl_login_remenberPsd, R.id.bt_loginRx, R.id.tv_loginRx_forgot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_login_remenberPsd:
                isRemem = !isRemem;
                ivLoginRxCheckbox.setImageResource(isRemem ? R.drawable.general_btn_remember_pre : R.drawable.general_btn_remember_nor);
                break;
            case R.id.bt_loginRx:
                loginButtonClick();// 点击登陆按钮后的逻辑
                break;
            case R.id.tv_loginRx_forgot:
                showAlertDialog();// 显示对话框
                break;
        }
    }

    /**
     * 保存密码
     */
    private void loginButtonClick() {
        // 1. 检测空值
        String loginText = OtherUtils.getEdContent(etLoginRx);
        if (TextUtils.isEmpty(loginText)) {
            ToastUtil_m.show(this, getString(R.string.setting_password_note));
            return;
        }
        // 2.检测密码位数
        int length = loginText.length();
        if (length < 4 | length > 16) {
            ToastUtil_m.show(this, getString(R.string.setting_password_note));
            return;
        }

        // 3.保存密码
        SP.getInstance(this).putBoolean(Cons.LOGIN_REMEM, isRemem);
        if (isRemem) {
            SP.getInstance(this).putString(Cons.LOGIN_RXPSD, OtherUtils.getEdContent(etLoginRx));
        } else {
            SP.getInstance(this).putString(Cons.LOGIN_RXPSD, "");
        }

        // 4.启动登陆
        startLogin();
    }

    /**
     * 启动登陆
     */
    private void startLogin() {
        // 检测硬件是否连接正常
        new CheckBoard() {
            @Override
            public void successful() {
                OtherUtils otherUtils = new OtherUtils();
                otherUtils.setOnSwVersionListener(needToEncrypt -> toLogin(needToEncrypt));
                otherUtils.getDeviceSwVersion();
            }
        }.checkBoard(this, LoginRxActivity.class);
    }

    /**
     * 登陆操作
     *
     * @param needEncrypt
     */
    private void toLogin(boolean needEncrypt) {
        pgd = OtherUtils.showProgressPop(this);
        // TOAT: 2017/7/13 加密算法--> FW完成加密机制后将下一句代码注释
        //needEncrypt = false;// FW完成加密机制后将该句代码注释

        // 明文用户+密码
        String account = Constants.USER_NAME_ADMIN;
        String oriAccount = account;
        String passwd = etLoginRx.getText().toString().trim();
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
                            // 判断连接的模式从而决定是否进入wizard向导页
                            getConnectMode();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        OtherUtils.hideProgressPop(pgd);
                        CA.toActivity(LoginRxActivity.this, RefreshWifiRxActivity.class, false, true, false, 0);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        System.out.println("ma_rx_loging: getLoginstatus" + error.getMessage() + ":" + error.getCode());
                        OtherUtils.hideProgressPop(pgd);
                        if (error.getCode().equalsIgnoreCase(Cons.GET_LOGIN_STATE_FAILED)) {
                            ToastUtil_m.show(LoginRxActivity.this, getString(R.string.connection_timed_out));
                        } else {
                            Log.v("ma_loginrx", "error: " + error.getCode() + ";errormes: " + error.getMessage());
                            ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_failed));
                        }
                        CA.toActivity(LoginRxActivity.this, RefreshWifiRxActivity.class, false, true, false, 0);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
                CA.toActivity(LoginRxActivity.this, RefreshWifiRxActivity.class, false, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Log.v("ma_loginrx", "error: " + error.getCode() + ";errormes: " + error.getMessage());
                OtherUtils.hideProgressPop(pgd);
                if (Cons.PASSWORD_IS_NOT_CORRECT.equals(error.getCode())) {
                    showRemainTimes();// 显示剩余次数
                } else if (Cons.OTHER_USER_IS_LOGIN.equals(error.getCode())) {
                    ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_other_user_logined_error_msg));
                } else if (Cons.DEVICE_REBOOT.equals(error.getCode())) {
                    ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_login_time_used_out_msg));
                } else if (Cons.GUEST_AP_OR_WEBUI.equals(error.getCode())) {
                    ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_login_app_or_webui));
                } else {
                    ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_failed));
                }
            }
        });
    }

    /**
     * 检测连接模式
     */
    private void getConnectMode() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                /* 获取WAN口状态 */
                int wanStatus = result.getStatus();
                API.get().getSimStatus(new MySubscriber<SimStatus>() {
                    @Override
                    protected void onSuccess(SimStatus result) {
                        /* 获取SIM卡状态 */
                        int simState = result.getSIMState();
                        boolean simflag = simState == Cons.READY || simState == Cons.PIN_REQUIRED || simState == Cons.PUK_REQUIRED;
                        OtherUtils.hideProgressPop(pgd);
                        if (wanStatus == Cons.CONNECTED & simflag) {// 都有
                            isToWizard();
                            return;
                        }
                        if (wanStatus != Cons.CONNECTED && simflag) {// 只有SIM卡
                            if (simState == Cons.READY) {
                                simHadReady();
                            } else if (simState == Cons.PIN_REQUIRED) {
                                EventBus.getDefault().postSticky(new PinPukBean(Cons.PIN_FLAG));
                                to(PinPukIndexRxActivity.class);
                            } else if (simState == Cons.PUK_REQUIRED) {
                                EventBus.getDefault().postSticky(new PinPukBean(Cons.PUK_FLAG));
                                to(PinPukIndexRxActivity.class);
                            } else {
                                to(HomeRxActivity.class);
                            }
                            return;
                        }
                        if (wanStatus == Cons.CONNECTED & !simflag) {// 只有WAN口
                            wanReady();
                            return;
                        }
                        if (wanStatus != Cons.CONNECTED & !simflag) {// 都没有
                            isToWizard();
                            return;
                        }

                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        isToWizard();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isToWizard();
                    }

                    /**
                     * WAN口连接完成
                     */
                    private void wanReady() {
                        // 是否进入过wan口设置向导页
                        if (SP.getInstance(LoginRxActivity.this).getBoolean(Cons.WANMODE_RX, false)) {
                            // 是否进入过wifi向导页
                            if (SP.getInstance(LoginRxActivity.this).getBoolean(Cons.WIFIINIT_RX, false)) {
                                to(HomeRxActivity.class);
                            } else {
                                to(WifiInitRxActivity.class);
                            }
                        } else {
                            to(WanModeRxActivity.class);
                        }
                    }

                    /**
                     * SIM卡能正常使用后
                     * (包含解PIN完毕后 | SIM本身已经ready)
                     */
                    private void simHadReady() {
                        // 1.是否进入过流量向导界面
                        if (SP.getInstance(LoginRxActivity.this).getBoolean(Cons.DATAPLAN_RX, false)) {
                            // 1.1.是否进入过wifi初始化向导界面
                            if (SP.getInstance(LoginRxActivity.this).getBoolean(Cons.WIFIINIT_RX, false)) {
                                to(HomeRxActivity.class);// --> 主页
                            } else {
                                to(WifiInitRxActivity.class);// --> wifi初始化向导页
                            }
                        } else {
                            to(DataPlanRxActivity.class);
                        }
                    }

                    /**
                     * 是否进入连接设置向导页
                     */
                    private void isToWizard() {
                        if (SP.getInstance(LoginRxActivity.this).getBoolean(Cons.WIZARD_RX, false)) {
                            to(HomeRxActivity.class);
                        } else {
                            to(WizardRxActivity.class);
                        }
                    }
                });
            }

            @Override
            protected void onFailure() {
                OtherUtils.hideProgressPop(pgd);
                Log.v("ma_loginrx", "onfailed");
                ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_failed));
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Log.v("ma_loginrx", "error: " + error.getCode() + ";errormes: " + error.getMessage());
                OtherUtils.hideProgressPop(pgd);
                ToastUtil_m.show(LoginRxActivity.this, getString(R.string.login_failed));
            }
        });
    }

    /**
     * 按情况跳转
     *
     * @param clazz
     */
    private void to(Class clazz) {
        CA.toActivity(this, clazz, false, true, false, 0);
    }

    /**
     * 显示剩余次数
     */
    public void showRemainTimes() {
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                int remainingTimes = result.getLoginRemainingTimes();
                String tips = getString(R.string.login_psd_error_msg);
                String remainTips = String.format(tips, remainingTimes);
                ToastUtil_m.show(LoginRxActivity.this, remainTips);
            }
        });
    }

    /**
     * 显示对话框
     */
    private void showAlertDialog() {
        // 重启设备
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)// 类型
                .setTitleText(getString(R.string.warning))// 警告
                .setContentText(getString(R.string.This_will_reset_all_settings_on_your_router_to_factory_defaults_This_action_can_not_be_undone))// 描述
                .setCancelText(getString(R.string.cancel))// 取消
                .setConfirmText(getString(R.string.ok))// 确定
                .showCancelButton(true)// 设置取消按钮
                .setCancelClickListener(Dialog::dismiss)// 设置取消监听
                .setConfirmClickListener(this::resetDevice)// 设置确定监听
                .show();

    }

    /**
     * 重启设备
     */
    private void resetDevice(SweetAlertDialog dialog) {
        dialog.dismiss();
        new CheckBoard() {
            @Override
            public void successful() {
                pgd = OtherUtils.showProgressPop(LoginRxActivity.this);
                API.get().resetDevice(new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {
                        OtherUtils.hideProgressPop(pgd);
                        ToastUtil_m.show(LoginRxActivity.this, getString(R.string.setting_reset_success));
                        LoginRxActivity.this.finish();
                        OtherUtils.kill();
                    }

                    @Override
                    public void onError(Throwable e) {
                        OtherUtils.hideProgressPop(pgd);
                        if (e instanceof SocketTimeoutException) {
                            Log.v("ma_couldn_connect", "loginrx resetDevice error: " + e.getMessage());
                            ToastUtil_m.show(LoginRxActivity.this, getString(R.string.connect_failed));
                            CA.toActivity(LoginRxActivity.this, RefreshWifiRxActivity.class, false, true, false, 0);
                        } else {
                            ToastUtil_m.show(LoginRxActivity.this, getString(R.string.setting_reset_failed));
                        }
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        System.out.println("ma_rx_loging: " + error.getMessage() + ":" + error.getCode());
                        OtherUtils.hideProgressPop(pgd);
                        ToastUtil_m.show(LoginRxActivity.this, getString(R.string.setting_reset_failed));
                    }
                });
            }
        }.checkBoard(this, LoginRxActivity.class);
    }
}
