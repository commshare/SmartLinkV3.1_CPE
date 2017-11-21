package com.alcatel.wifilink.rx.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.SP;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.wlan.WlanSetting;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.CheckBoardLogin;
import com.alcatel.wifilink.rx.helper.LogoutHelper;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.OtherUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * WIFI设置向导
 */
public class WifiInitRxActivity extends BaseActivityWithBack {

    // 回退
    @BindView(R.id.iv_wifiInit_back)
    ImageView ivBack;

    // 2.4G面板
    @BindView(R.id.rl_wifiInit_2p4G)
    PercentRelativeLayout rl2p4GPanel;
    @BindView(R.id.iv_wifiInit_wlanStatus_socket_2p4G)
    ImageView ivSocket2p4G;// 状态开关
    @BindView(R.id.et_wifiInit_wlanStatus_account_2p4G)
    EditText etAccount2p4G;// 用户名
    @BindView(R.id.iv_wifiInit_password_eyes_2p4G)
    ImageView ivPasswordEyes2p4G;// 可见开关
    @BindView(R.id.et_wifiInit_password_2p4G)
    EditText etPassword2p4G;// 密码

    // 5G面板
    @BindView(R.id.rl_wifiInit_5G)
    PercentRelativeLayout rl5GPanel;
    @BindView(R.id.iv_wifiInit_wlanStatus_socket_5G)
    ImageView ivSocket5G;// 状态开关
    @BindView(R.id.et_wifiInit_wlanStatus_account_5G)
    EditText etAccount5G;// 用户名
    @BindView(R.id.iv_wifiInit_password_eyes_5G)
    ImageView ivPasswordEyes5G;// 可见开关
    @BindView(R.id.et_wifiInit_password_5G)
    EditText etPassword5G;// 密码

    // 点击done按钮
    @BindView(R.id.tv_wifiInit_done)
    TextView btNext;

    // 失败界面
    @BindView(R.id.rl_wifiInit_rx_failed)
    PercentRelativeLayout rlFailedPanel;
    @BindView(R.id.tv_wifiInit_rx_tryagain)
    TextView tvTryagain;// 重试按钮
    @BindView(R.id.tv_wifiInit_rx_tohome)
    TextView tvTohome;// 前往主页

    private int FLAG_2P4G = 0;
    private int FLAG_5G = 1;
    private TimerHelper heartTimer;
    private Activity activity;
    private Drawable switch_on;
    private Drawable switch_off;
    private Drawable eyes_on;
    private Drawable eyes_off;
    private Drawable unClick;
    private int showPsd;
    private int hidePsd;
    private int blue_color;
    private int gray_color;
    private int showPsd_hex;
    private int hidePsd_hex;
    private WlanSetting result;
    private WlanSetting resultCache;// 备份缓存
    private SweetAlertDialog rebootDialog;// 重启设备对话框
    private ProgressDialog pgd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        SmartLinkV3App.getContextInstance().add(this);
        setContentView(R.layout.activity_wifi_init_rx);
        ButterKnife.bind(this);
        startHeartTimer();
        initRes();
        initData();
    }

    /**
     * 初始化资源
     */
    private void initRes() {
        switch_on = getResources().getDrawable(R.drawable.pwd_switcher_on);
        switch_off = getResources().getDrawable(R.drawable.pwd_switcher_off);
        eyes_on = getResources().getDrawable(R.drawable.general_password_show);
        eyes_off = getResources().getDrawable(R.drawable.general_password_hidden);
        unClick = getResources().getDrawable(R.drawable.bg_unclick);
        showPsd = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        hidePsd = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        showPsd_hex = 0x90;
        hidePsd_hex = 0x80;
        blue_color = getResources().getColor(R.color.mg_blue);
        gray_color = getResources().getColor(R.color.gray);
    }

    private void initData() {
        // 获取支持的模式(2.4|5)以及当前模式的状态
        getSupportModeAndCurrentStatus();
    }

    /**
     * 获取支持的模式(2.4|5)以及当前模式的状态
     */
    private void getSupportModeAndCurrentStatus() {
        // 1.检测是否连接上硬件以及是否处于登陆状态
        new CheckBoardLogin(this) {
            @Override
            public void afterCheckSuccess(ProgressDialog pgd) {
                // 2.获取wlan信息
                API.get().getWlanSetting(new MySubscriber<WlanSetting>() {
                    @Override
                    protected void onSuccess(WlanSetting result) {
                        WifiInitRxActivity.this.result = result;
                        WifiInitRxActivity.this.resultCache = result.deepClone();// 备份缓存
                        boolean is2P4GEnable = result.getAP2G().getApStatus() == Cons.ENABLE;
                        boolean is5GEnable = result.getAP5G().getApStatus() == Cons.ENABLE;
                        // 面板
                        rl2p4GPanel.setVisibility(is2P4GEnable ? View.VISIBLE : View.GONE);
                        rl5GPanel.setVisibility(is5GEnable ? View.VISIBLE : View.GONE);
                        // 开关UI
                        ivSocket2p4G.setImageDrawable(is2P4GEnable ? switch_on : switch_off);
                        ivSocket5G.setImageDrawable(is5GEnable ? switch_on : switch_off);
                        // 编辑可操作状态
                        OtherUtils.setEdittextEditable(etAccount2p4G, is2P4GEnable);
                        OtherUtils.setEdittextEditable(etPassword2p4G, is2P4GEnable);
                        OtherUtils.setEdittextEditable(etAccount5G, is5GEnable);
                        OtherUtils.setEdittextEditable(etPassword5G, is5GEnable);
                        // 编辑可视状态
                        etPassword2p4G.setInputType(hidePsd);
                        etPassword5G.setInputType(hidePsd);
                        // 编辑文本颜色
                        etAccount2p4G.setTextColor(is2P4GEnable ? blue_color : gray_color);
                        etPassword2p4G.setTextColor(is2P4GEnable ? blue_color : gray_color);
                        etAccount5G.setTextColor(is5GEnable ? blue_color : gray_color);
                        etPassword5G.setTextColor(is5GEnable ? blue_color : gray_color);
                        // 编辑SSID
                        etAccount2p4G.setText(result.getAP2G().getSsid());
                        etAccount5G.setText(result.getAP5G().getSsid());
                        // 编辑密码
                        etPassword2p4G.setText(result.getAP2G().getWpaKey());
                        etPassword5G.setText(result.getAP5G().getWpaKey());
                    }

                    @Override
                    public void onError(Throwable e) {
                        rlFailedPanel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        rlFailedPanel.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OtherUtils.stopHeartBeat(heartTimer);
    }

    @Override
    public void onBackPressed() {
        clickBack();
    }

    @OnClick({R.id.iv_wifiInit_back,// 回退
                     R.id.iv_wifiInit_wlanStatus_socket_2p4G,// 2.4G状态开关
                     R.id.iv_wifiInit_password_eyes_2p4G,// 2.4G可视按钮
                     R.id.iv_wifiInit_wlanStatus_socket_5G,// 5G状态开关
                     R.id.iv_wifiInit_password_eyes_5G,// 5G可视按钮
                     R.id.tv_wifiInit_done,// 点击提交按钮
                     R.id.tv_wifiInit_rx_tryagain,// 重试按钮
                     R.id.tv_wifiInit_rx_tohome// 前往主页
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_wifiInit_back:// 回退
                clickBack();
                break;
            case R.id.iv_wifiInit_wlanStatus_socket_2p4G:// 2.4G状态开关
                checkWlanStatusSocket(FLAG_2P4G);
                break;
            case R.id.iv_wifiInit_password_eyes_2p4G:// 2.4G可视
                checkVisibleEyes(FLAG_2P4G);
                break;
            case R.id.iv_wifiInit_wlanStatus_socket_5G:// 5G状态开关
                checkWlanStatusSocket(FLAG_5G);
                break;
            case R.id.iv_wifiInit_password_eyes_5G:// 5G可视
                checkVisibleEyes(FLAG_5G);
                break;
            case R.id.tv_wifiInit_done:// done按钮
                clickDone();
                break;
            case R.id.tv_wifiInit_rx_tryagain:// 重试按钮
                // 退出登陆--> 前往登陆界面
                toLoginRx();
                break;
            case R.id.tv_wifiInit_rx_tohome:
                toHome();// 前往主页
                break;
        }
    }

    /**
     * 点击done按钮
     */
    private void clickDone() {
        // 0.获取开关状态
        boolean is2P4GEnable = ivSocket2p4G.getDrawable() == switch_on;
        boolean is5GEnable = ivSocket5G.getDrawable() == switch_on;
        // 1.空值判断
        if (is2P4GEnable && TextUtils.isEmpty(OtherUtils.getEdContentIncludeSpace(etAccount2p4G))) {
            toast(R.string.setting_ssid_invalid);
            return;
        }
        if (is2P4GEnable && TextUtils.isEmpty(OtherUtils.getEdContentIncludeSpace(etPassword2p4G))) {
            toast(R.string.qs_wifi_passwd_prompt);
            return;
        }
        if (is5GEnable && TextUtils.isEmpty(OtherUtils.getEdContentIncludeSpace(etAccount5G))) {
            toast(R.string.setting_ssid_invalid);
            return;
        }
        if (is5GEnable && TextUtils.isEmpty(OtherUtils.getEdContentIncludeSpace(etPassword5G))) {
            toast(R.string.qs_wifi_passwd_prompt);
            return;
        }
        // 2.位数判断
        int digits_2P4G = OtherUtils.getEdContentIncludeSpace(etPassword2p4G).length();
        int digits_5G = OtherUtils.getEdContentIncludeSpace(etPassword5G).length();
        if (is2P4GEnable && (digits_2P4G < 8 | digits_2P4G > 63)) {
            toast(R.string.qs_wifi_passwd_prompt);
            return;
        }
        if (is5GEnable && (digits_5G < 8 | digits_5G > 63)) {
            toast(R.string.qs_wifi_passwd_prompt);
            return;
        }
        // 3.封装
        if (is2P4GEnable) {
            result.getAP2G().setApStatus(Cons.ENABLE);
            result.getAP2G().setSecurityMode(Cons.SECURITY_WPA_WPA2);// 强制为WPA\WPA2模式
            result.getAP2G().setSsid(OtherUtils.getEdContentIncludeSpace(etAccount2p4G));
            result.getAP2G().setWpaKey(OtherUtils.getEdContentIncludeSpace(etPassword2p4G));
        } else {
            result.getAP2G().setApStatus(Cons.DISABLE);
        }

        if (is5GEnable) {
            result.getAP5G().setApStatus(Cons.ENABLE);
            result.getAP5G().setSecurityMode(Cons.SECURITY_WPA_WPA2);// 强制为WPA\WPA2模式
            result.getAP5G().setSsid(OtherUtils.getEdContentIncludeSpace(etAccount5G));
            result.getAP5G().setWpaKey(OtherUtils.getEdContentIncludeSpace(etPassword5G));
        } else {
            result.getAP5G().setApStatus(Cons.DISABLE);
        }
        // 4.检测是否连接上硬件以及内容是否发生改变
        checkBoardAndContent();
    }

    /**
     * 检测是否连接上硬件以及内容是否发生改变
     */
    private void checkBoardAndContent() {
        // 1.检查硬件是否连接上
        new CheckBoardLogin(this) {
            @Override
            public void afterCheckSuccess(ProgressDialog pop) {
                // 2.判断是否有修改过
                checkChange();
            }
        };
    }

    /**
     * 判断是否有修改过
     */
    private void checkChange() {
        // 1.状态是否相同
        boolean is2P4GAPStatusSame = resultCache.getAP2G().getApStatus() == result.getAP2G().getApStatus();
        boolean is5GAPStatusSame = resultCache.getAP5G().getApStatus() == result.getAP5G().getApStatus();
        // 2.SSID是否相同
        boolean is2P4GSSIDSame = resultCache.getAP2G().getSsid().equals(result.getAP2G().getSsid());
        boolean is5GSSIDSame = resultCache.getAP5G().getSsid().equals(result.getAP5G().getSsid());
        // 3.password是否相同
        boolean is2P4GWpaSame = resultCache.getAP2G().getWpaKey().equals(result.getAP2G().getWpaKey());
        boolean is5GWpaSame = resultCache.getAP5G().getWpaKey().equals(result.getAP5G().getWpaKey());

        if (is2P4GAPStatusSame & is5GAPStatusSame & is2P4GSSIDSame & is5GSSIDSame & is2P4GWpaSame & is5GWpaSame) {
            // 4.直接跳转到主页
            SP.getInstance(activity).putBoolean(Cons.WIFIINIT_RX, true);
            to(HomeRxActivity.class);
        } else {
            // 4.弹出登陆会话框
            showReStartDeviceDialog();
        }

    }

    /**
     * 显示重启提示对话框
     */
    private void showReStartDeviceDialog() {
        rebootDialog = new SweetAlertDialog(WifiInitRxActivity.this,// context
                                                   SweetAlertDialog.WARNING_TYPE)// type
                               .setTitleText(getString(R.string.restart))// title
                               .setContentText(getString(R.string.setting_restore_success))// descript
                               .setCancelText(getString(R.string.cancel))// cancel
                               .setConfirmText(getString(R.string.confirm_unit))// comfirm
                               .showCancelButton(true)// set cancel enable
                               .setConfirmClickListener(this::pullSetting)// confirm logic
                               .setCancelClickListener(Dialog::dismiss);
        rebootDialog.show();// do it
    }

    /**
     * 提交设置
     */
    private void pullSetting(SweetAlertDialog alertDialog) {
        alertDialog.dismiss();
        pgd = OtherUtils.showProgressPop(this);
        // 1.提交设置
        API.get().setWlanSetting(result, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                // 2.切断wifi
                OtherUtils.setWifiActive(activity, false);
                // 提交标记位
                SP.getInstance(activity).putBoolean(Cons.WIFIINIT_RX, true);
                toast(R.string.setting_reboot_success);
                OtherUtils.hideProgressPop(pgd);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                toast(R.string.setting_wifi_set_failed);
                OtherUtils.hideProgressPop(pgd);
            }

            @Override
            public void onError(Throwable e) {
                toast(R.string.setting_wifi_set_failed);
                OtherUtils.hideProgressPop(pgd);
            }
        });
    }


    /**
     * 前往主页
     */

    private void toHome() {
        // 1.检查是否连接上硬件以及处于登陆状态
        new CheckBoardLogin(this) {
            @Override
            public void afterCheckSuccess(ProgressDialog pgd) {
                // 提交标记
                SP.getInstance(activity).putBoolean(Cons.WIFIINIT_RX, true);
                // 跳转
                to(HomeRxActivity.class);
            }
        };
    }

    /**
     * 切换密码可视化开关
     *
     * @param flag
     */
    private void checkVisibleEyes(int flag) {
        if (flag == FLAG_2P4G) {
            // 切换UI
            Drawable eye_2p4g = ivPasswordEyes2p4G.getDrawable() == eyes_on ? eyes_off : eyes_on;
            ivPasswordEyes2p4G.setImageDrawable(eye_2p4g);
            // 切换编辑域可视状态
            etPassword2p4G.setInputType(etPassword2p4G.getInputType() == showPsd ? hidePsd : showPsd);
        } else {
            Drawable eye_5g = ivPasswordEyes5G.getDrawable() == eyes_on ? eyes_off : eyes_on;
            ivPasswordEyes5G.setImageDrawable(eye_5g);
            etPassword5G.setInputType(etPassword5G.getInputType() == showPsd ? hidePsd : showPsd);
        }
    }

    /**
     * 切换状态开关
     *
     * @param flag
     */
    private void checkWlanStatusSocket(int flag) {
        if (flag == FLAG_2P4G) {
            // 切换视图
            Drawable switch_2p4g = ivSocket2p4G.getDrawable() == switch_on ? switch_off : switch_on;
            ivSocket2p4G.setImageDrawable(switch_2p4g);
            // 设置编辑域编辑权限
            boolean isAfterSwitchOn = ivSocket2p4G.getDrawable() == switch_on;
            OtherUtils.setEdittextEditable(etAccount2p4G, isAfterSwitchOn);
            OtherUtils.setEdittextEditable(etPassword2p4G, isAfterSwitchOn);
            etAccount2p4G.setTextColor(isAfterSwitchOn ? blue_color : gray_color);
            etPassword2p4G.setTextColor(isAfterSwitchOn ? blue_color : gray_color);
            ivPasswordEyes2p4G.setClickable(isAfterSwitchOn ? true : false);
        } else {
            Drawable switch_5g = ivSocket5G.getDrawable() == switch_on ? switch_off : switch_on;
            ivSocket5G.setImageDrawable(switch_5g);
            boolean isAfterSwitchOn = ivSocket5G.getDrawable() == switch_on;
            OtherUtils.setEdittextEditable(etAccount5G, isAfterSwitchOn);
            OtherUtils.setEdittextEditable(etPassword5G, isAfterSwitchOn);
            etAccount5G.setTextColor(isAfterSwitchOn ? blue_color : gray_color);
            etPassword5G.setTextColor(isAfterSwitchOn ? blue_color : gray_color);
            ivPasswordEyes5G.setClickable(isAfterSwitchOn ? true : false);
        }
    }

    private void startHeartTimer() {
        heartTimer = OtherUtils.startHeartBeat(this, RefreshWifiRxActivity.class);
    }

    /**
     * 点击了回退按钮
     */
    private void clickBack() {
        toLoginRx();// 退出登陆--> 前往登陆界面
    }

    /**
     * 退出登陆--> 前往登陆界面
     */
    private void toLoginRx() {
        // 1.退出登陆
        new LogoutHelper(this) {
            @Override
            public void logoutFinish() {
                // 2.跳转到登陆界面
                to(LoginRxActivity.class);
            }
        };
    }

    public void toast(int resId) {
        ToastUtil_m.show(this, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(this, content);
    }

    private void to(Class clazz) {
        CA.toActivity(this, clazz, false, true, false, 0);
    }
}
