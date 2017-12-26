package com.alcatel.smartlinkv3.rx.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.rx.adapter.QuickAdapter;
import com.alcatel.smartlinkv3.rx.bean.WlanSettingForY900;
import com.alcatel.smartlinkv3.rx.helper.WlanSettingY900Helper;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.alcatel.smartlinkv3.rx.tools.Logs;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.SPUtils;
import com.alcatel.smartlinkv3.utils.TimerHelper;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuickSetupRxY900Activity extends BaseRxActivity {

    @BindView(R.id.tv_wifiSecurityMode)
    TextView tvWifiSecurityMode;
    @BindView(R.id.rcv_wifiSecurityMode)
    RecyclerView rcvWifiSecurityMode;
    @BindView(R.id.et_wifiName)
    EditText etWifiName;
    @BindView(R.id.tv_wifiName_title)
    TextView tvWifiNameTitle;
    @BindView(R.id.tv_wifiPsd)
    TextView tvWifiPsd;
    @BindView(R.id.et_wifiPsd)
    EditText etWifiPsd;
    @BindView(R.id.rl_wifiFinish)
    RelativeLayout rlWifiFinish;

    private TimerHelper heartBeatTimer;
    private Activity context;
    private ProgressDialog pdg;
    private List<String> securityArray;// WEP_WPA_WPA2

    private int securityMode_default;// 首次拉取的安全策略
    private String ssid_default;// 首次拉取的SSID
    private String password_default;// 首次拉取的密码
    private int securityPosition;// 安全策略代号,0,1,2..
    private boolean cancelLogout = true;// true:需要退出登陆
    private boolean is2P4G;// 是否当前为2.4G模式
    private WlanSettingForY900 wlanResultY900;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.v("ma_rx", "QuickSetupRxY900Activity");
        setContentView(R.layout.activity_quick_setup_rx);
        ButterKnife.bind(this);
        context = this;
        initView();// 1.初始化视图
        startTimer();// 2.启动定时器防止登出
        getWifiInfo();// 3.获取WIFI信息
    }

    /**
     * 获取WIFI信息
     */
    private void getWifiInfo() {
        pdg = OtherUtils.showProgressPop(this);
        WlanSettingY900Helper wsY900 = new WlanSettingY900Helper(this);
        wsY900.setOnErrorListener(attr -> {
            Logger.t("ma_rx").e("getWifiInfo: " + attr.getMessage());
            OtherUtils.hideProgressPop(pdg);
            toast(R.string.error_info);
            to(RefreshWifiActivity.class, true);
        });
        wsY900.setOnResultErrorListener(attr -> {
            Logger.t("ma_rx").e("getWifiInfo result error: " + attr.getMessage());
            OtherUtils.hideProgressPop(pdg);
            toast(R.string.error_info);
            to(RefreshWifiActivity.class, true);
        });
        wsY900.setOnWlanSettingY900SuccessListener(result -> {
            wlanResultY900 = result;
            // 隐藏进度条并读取固定安全策略数组
            OtherUtils.hideProgressPop(pdg);
            securityArray = getStringList(R.array.wlan_settings_security);
            // 默认为2.4G
            is2P4G = result.getWMode() != Cons._5G;
            securityMode_default = is2P4G ? result.getSecurityMode() : result.getSecurityMode_5G();
            String securitymode = securityArray.get(securityMode_default - 1 < 0 ? 0 : securityMode_default - 1);
            tvWifiSecurityMode.setText(securitymode);
            // 获取SSID
            ssid_default = is2P4G ? result.getSsid() : result.getSsid_5G();
            etWifiName.setText(ssid_default);
            // 根据securityMode获取对应模式下的密码
            if (securityMode_default == Conn.WEP) {
                password_default = is2P4G ? result.getWepKey() : result.getWepKey_5G();
            } else {
                password_default = is2P4G ? result.getWpaKey() : result.getWpaKey_5G();
            }
            etWifiPsd.setText(password_default);
            // 设置security Mode点击监听
            tvWifiSecurityMode.setOnClickListener(v -> {
                // 根据模式显示面板
                if (rcvWifiSecurityMode.getVisibility() == View.GONE) {
                    rcvWifiSecurityMode.setVisibility(View.VISIBLE);
                    rcvWifiSecurityMode.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    rcvWifiSecurityMode.setAdapter(new QuickAdapter(this, securityArray) {
                        @Override
                        public void onQuickItemClick(String mode, int position) {
                            tvWifiSecurityMode.setText(mode);
                            securityPosition = position + 1;
                            if (securityPosition == Conn.WEP) {
                                etWifiPsd.setText(is2P4G ? result.getWepKey() : result.getWepKey_5G());
                            } else {
                                etWifiPsd.setText(is2P4G ? result.getWpaKey() : result.getWpaKey_5G());
                            }
                            rcvWifiSecurityMode.setVisibility(View.GONE);
                        }
                    });
                } else {
                    rcvWifiSecurityMode.setVisibility(View.GONE);
                }
            });
            // 设置edittext光标
            etWifiName.setSelection(etWifiName.getText().length());
            etWifiPsd.setSelection(etWifiPsd.getText().length());
        });
        wsY900.get();
    }


    /**
     * 启动定时器防止登出
     */
    // TOAT: 保留
    private void startTimer() {
        heartBeatTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                API.get().heartBeat(new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        toActivity(RefreshWifiActivity.class);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        toActivity(RefreshWifiActivity.class);
                    }
                });
            }
        };
        heartBeatTimer.start(3000);
    }

    /**
     * 跳转界面
     *
     * @param clazz
     */
    public void toActivity(Class clazz) {
        OtherUtils.hideProgressPop(pdg);
        heartBeatTimer.stop();
        to(clazz, true);
    }

    // TOAT: 保留
    private void initView() {
        tvWifiNameTitle.setText(getString(R.string.qs_wifi_ssid, ""));
        tvWifiPsd.setText(getString(R.string.qs_wifi_passwd, ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cancelLogout) {
            logoutAndFinish();// 登出并退出APP
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OtherUtils.hideProgressPop(pdg);
        heartBeatTimer.stop();
    }

    /**
     * 登出并退出APP
     */
    private void logoutAndFinish() {
        API.get().logout(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                heartBeatTimer.stop();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                heartBeatTimer.stop();
            }
        });
    }

    @OnClick({R.id.rl_wifiFinish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_wifiFinish:

                // 判断 [编辑域 | 安全模式] 是否有改变过
                boolean changeWlan = isChangeWlan();
                if (changeWlan) {
                    // 修改参数并提交
                    createWlanParam();
                } else {
                    // 修改退出登陆标记为false:不需要退出
                    cancelLogout = false;
                    // 提交快速设置标记
                    SPUtils.getInstance(context).putBoolean(Conn.QUICK_SETUP, true);
                    // 直接跳转到主页
                    ChangeActivity.toActivityNormal(this, MainActivity.class, true);
                }
                break;
        }
    }

    /**
     * Wlan信息是否改变过
     *
     * @return true:改变
     */
    private boolean isChangeWlan() {
        // 模式是否改变
        boolean isMode = securityArray.get(securityMode_default - 1 < 0 ? 0 : securityMode_default - 1).equalsIgnoreCase(tvWifiSecurityMode.getText().toString());
        // wifi name是否改变
        boolean isSSID = ssid_default.equalsIgnoreCase(etWifiName.getText().toString());
        // wifi密码是否改变
        boolean isPsd = password_default.equalsIgnoreCase(etWifiPsd.getText().toString());
        return !(isMode && isSSID && isPsd);
    }

    private void createWlanParam() {
        // 0.显示进度条
        pdg = OtherUtils.showProgressPop(context);
        // 1.生成wlan参数
        int index = OtherUtils.getIndexByString(securityArray, tvWifiSecurityMode.getText().toString());
        index = index == -1 ? securityMode_default : index + 1;// 需要+1,因为模式标记是从1开始
        // 设置安全策略
        if (is2P4G) {
            wlanResultY900.setSecurityMode(index);
        } else {
            wlanResultY900.setSecurityMode_5G(index);
        }
        // 设置wifi名称
        if (is2P4G) {
            wlanResultY900.setSsid(OtherUtils.getEdittext(etWifiName));
        } else {
            wlanResultY900.setSsid_5G(OtherUtils.getEdittext(etWifiName));
        }
        // 设置wifi密码
        if (index == Conn.WEP) {
            if (is2P4G) {
                wlanResultY900.setWepKey(OtherUtils.getEdittext(etWifiPsd));
            } else {
                wlanResultY900.setWepKey_5G(OtherUtils.getEdittext(etWifiPsd));
            }

        } else {
            if (is2P4G) {
                wlanResultY900.setWpaKey(OtherUtils.getEdittext(etWifiPsd));
            } else {
                wlanResultY900.setWpaKey_5G(OtherUtils.getEdittext(etWifiPsd));
            }
        }
        // 2.提交
        API.get().setWlanSettingsForY900(wlanResultY900, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                // 弹出提示
                ToastUtil_m.show(context, getString(R.string.setting_wifi_set_success));
                // 提交设置过的标记
                SPUtils.getInstance(context).putBoolean(Conn.QUICK_SETUP, true);
                // 切断wifi
                OtherUtils.setWifiActive(context, false);
                // 隐藏进度条
                OtherUtils.hideProgressPop(pdg);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                // 修改退出登陆标记
                cancelLogout = false;
                ToastUtil_m.show(context, getString(R.string.setting_wifi_set_failed));
                ChangeActivity.toActivity(context, MainActivity.class, false, true, false, 1000);
            }
        });
    }
}
