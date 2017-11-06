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
import com.alcatel.smartlinkv3.rx.impl.wlan.WlanResult;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.SPUtils;
import com.alcatel.smartlinkv3.utils.TimerHelper;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuickSetupRxActivity extends BaseRxActivity {

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

    private WlanResult wlanResult;// 首次拉取的wifi信息1
    private WlanResult.APListBean apBean;// 首次拉取的wifi信息2
    private int securityMode_default;// 首次拉取的安全策略
    private String ssid_default;// 首次拉取的SSID
    private String password_default;// 首次拉取的密码
    private int securityPosition;// 安全策略代号,0,1,2..
    private boolean cancelLogout = true;// true:需要退出登陆
    private ProgressDialog pgd;// 进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        API.get().getWlanSettings(new MySubscriber<WlanResult>() {
            @Override
            protected void onSuccess(WlanResult result) {
                wlanResult = result;
                // 显示进度条并读取固定安全策略数组
                OtherUtils.hideProgressPop(pdg);
                securityArray = Arrays.asList(getResources().getStringArray(R.array.wlan_settings_security));
                // 根据wlan mode选定模式集合元素(默认选中第一个元素)
                apBean = matchAPID(result);
                // 获取security mode (WPA或者WEP)
                securityMode_default = apBean.getSecurityMode();
                String securitymode = securityArray.get(securityMode_default - 1);
                tvWifiSecurityMode.setText(securitymode);
                // 获取SSID
                ssid_default = apBean.getSsid();
                etWifiName.setText(ssid_default);
                // 根据securityMode获取对应模式下的密码
                if (securityMode_default == Conn.WEP) {
                    password_default = apBean.getWepKey();
                } else {
                    password_default = apBean.getWpaKey();
                }
                etWifiPsd.setText(password_default);
                // 设置security Mode点击监听
                tvWifiSecurityMode.setOnClickListener(v -> {
                    showSecurityMode();// 显示安全策略选择面板
                });
                // 设置edittext光标
                etWifiName.setSelection(etWifiName.getText().length());
                etWifiPsd.setSelection(etWifiPsd.getText().length());
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pdg);
                ToastUtil_m.show(context, getString(R.string.setting_upgrade_no_connection));
                ChangeActivity.toActivity(context, RefreshWifiActivity.class, false, true, false, 0);
            }
        });
    }

    /**
     * 匹配WLAN AP ID--> 找出对应模式的apbean
     *
     * @param result
     * @return 对应模式的apbean
     */
    public WlanResult.APListBean matchAPID(WlanResult result) {

        int wlanAPMode = result.getWlanAPMode();
        for (WlanResult.APListBean apListBean : result.getAPList()) {
            if (apListBean.getWlanAPID() == wlanAPMode) {
                return apListBean;
            }
        }
        return result.getAPList().get(0);
    }

    /**
     * 显示安全策略选择面板
     */
    private void showSecurityMode() {
        if (rcvWifiSecurityMode.getVisibility() == View.GONE) {
            rcvWifiSecurityMode.setVisibility(View.VISIBLE);
            rcvWifiSecurityMode.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rcvWifiSecurityMode.setAdapter(new QuickAdapter(this, securityArray) {
                @Override
                public void onQuickItemClick(String mode, int position) {
                    tvWifiSecurityMode.setText(mode);
                    securityPosition = position + 1;
                    if (securityPosition == Conn.WEP) {
                        etWifiPsd.setText(apBean.getWepKey());
                    } else {
                        etWifiPsd.setText(apBean.getWpaKey());
                    }
                    rcvWifiSecurityMode.setVisibility(View.GONE);
                }
            });
        } else {
            rcvWifiSecurityMode.setVisibility(View.GONE);
        }
    }

    /**
     * 启动定时器防止登出
     */
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
                        toActivity(LoginRxActivity.class);
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
        OtherUtils.hideProgressPop(pgd);
        heartBeatTimer.stop();
        ChangeActivity.toActivity(this, clazz, false, true, false, 0);
    }

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
        boolean isMode = securityArray.get(securityMode_default - 1).equalsIgnoreCase(tvWifiSecurityMode.getText().toString());
        // wifi name是否改变
        boolean isSSID = ssid_default.equalsIgnoreCase(etWifiName.getText().toString());
        // wifi密码是否改变
        boolean isPsd = password_default.equalsIgnoreCase(etWifiPsd.getText().toString());
        return !(isMode && isSSID && isPsd);
    }

    private void createWlanParam() {
        // 0.显示进度条
        pgd = OtherUtils.showProgressPop(context);
        // 1.生成wlan参数
        int index = OtherUtils.getIndexByString(securityArray, tvWifiSecurityMode.getText().toString());
        index = index == -1 ? securityMode_default : index + 1;// 需要+1,因为模式标记是从1开始
        apBean.setSecurityMode(index);// 设置安全策略
        apBean.setSsid(OtherUtils.getEdittext(etWifiName));// 设置wifi名称
        if (index == Conn.WEP) {// 设置wifi密码
            apBean.setWepKey(OtherUtils.getEdittext(etWifiPsd));
        } else {
            apBean.setWpaKey(OtherUtils.getEdittext(etWifiPsd));
        }
        // 2.提交
        API.get().setWlanSettings(wlanResult, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                // 弹出提示
                ToastUtil_m.show(context, getString(R.string.setting_wifi_set_success));
                // 提交设置过的标记
                SPUtils.getInstance(context).putBoolean(Conn.QUICK_SETUP, true);
                // 切断wifi
                OtherUtils.setWifiActive(context, false);
                // 隐藏进度条
                OtherUtils.hideProgressPop(pgd);

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
