package com.alcatel.smartlinkv3.rx.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.rx.bean.PsdBean;
import com.alcatel.smartlinkv3.rx.bean.PsdRuleBean;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.impl.wlan.WlanResult;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.FraHelper;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.TimerHelper;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;
import com.ldoublem.loadingviewlib.view.LVCircularRing;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingwifiRxActivity extends BaseRxActivity {

    @BindView(R.id.iv_wifisetting_back)
    ImageView ivWifisettingBack;
    @BindView(R.id.tv_wifisetting_back)
    TextView tvWifisettingBack;
    @BindView(R.id.tv_wifisetting_done)
    TextView tvWifisettingDone;
    @BindView(R.id.tv_2p4_checker)
    TextView tv2p4Checker;
    @BindView(R.id.v_split)
    View vSplitBanner;// 分割线
    @BindView(R.id.tv_5G_checker)
    TextView tv5GChecker;
    @BindView(R.id.fl_wifiInfo)
    FrameLayout flWifiInfo;
    @BindView(R.id.rl_wifisetting_error)
    PercentRelativeLayout rlWifisettingError;
    @BindView(R.id.lvc_wait)
    LVCircularRing lvcWait;
    @BindView(R.id.rl_wifisetting_wait)
    PercentRelativeLayout rlWifisettingWait;

    private int container;// 容器
    private int colorCheck;
    private int colorUnCheck;
    private Typeface textBold;
    private Typeface textNormal;
    public static WlanResult.APListBean apbean_2P4;// 2.4G AP
    public static WlanResult.APListBean apbean_5G;// 5G AP
    public PsdBean pb_2P4_default = new PsdBean();
    public PsdBean pb_5G_default = new PsdBean();
    private FraHelper fraHelper;// fragment切换辅助类
    private Class[] clazzs = {// fragment类集合
            SettingwifiRx2p4Fragment.class,// 2.4G fragment
            SettingwifiRx5GFragment.class};// 5G fragment
    private WlanResult result;
    public String toastWepWarn;
    public String toastWpaWarn;
    private Activity activity;
    private ProgressDialog pgd;
    private TimerHelper timerHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifirx_setting);
        ButterKnife.bind(this);
        activity = this;
        initAttr();// 初始化属性
        initWait();// 初始化等待
        initWlan();// 初始化数据
        initTimer();// 启动定时器
    }


    private void initAttr() {
        container = R.id.fl_wifiInfo;// 容器
        colorCheck = getResources().getColor(R.color.main_title_background);
        colorUnCheck = getResources().getColor(R.color.gray);
        textBold = Typeface.defaultFromStyle(Typeface.BOLD);
        textNormal = Typeface.defaultFromStyle(Typeface.NORMAL);
        toastWepWarn = getString(R.string.setting_wep_password_error_prompt);
        toastWpaWarn = getString(R.string.setting_wpa_password_error_prompt);
    }


    private void initWait() {
        rlWifisettingWait.setVisibility(View.VISIBLE);
        lvcWait.setViewColor(colorUnCheck);
        lvcWait.setBarColor(colorCheck);
        lvcWait.startAnim(1000);
    }

    private void initWlan() {
        API.get().getWlanSettings(new MySubscriber<WlanResult>() {
            @Override
            protected void onSuccess(WlanResult result) {
                SettingwifiRxActivity.this.result = result;
                // 1.分类AP对象
                for (WlanResult.APListBean apListBean : result.getAPList()) {
                    if (apListBean.getWlanAPID() == Conn.WLANAPID_2P4G) {
                        apbean_2P4 = apListBean;
                        // 保存WEP|WPA的临时信息
                        pb_2P4_default = setDefaultInfo(Conn.WLANAPID_2P4G, apbean_2P4);

                    } else {
                        apbean_5G = apListBean;
                        // 保存WEP|WPA的临时信息
                        pb_5G_default = setDefaultInfo(Conn.WLANAPID_5G, apbean_5G);
                    }
                }

                // 1.1.通过判断2.4G & 5G 的AP对象是否为空来决定面板的显示与否
                int APExist = 0;// 默认只显示2.4G
                if (apbean_2P4 != null & apbean_5G != null) {
                    APExist = Conn.MODE_2P4G_5G;
                } else if (apbean_2P4 != null) {
                    APExist = Conn.MODE_2P4G;
                } else if (apbean_5G != null) {
                    APExist = Conn.MODE_5G;
                } else if (apbean_2P4 == null & apbean_5G == null) {
                    showErrorUi();
                }

                // TOGO 2017/11/3 0003 测试代码 START
                // apbean_5G = new WlanResult.APListBean();
                // apbean_5G.setWlanAPID(1);// 5G
                // apbean_5G.setSsid(apbean_2P4.getSsid() + "_5G");// xxxx_5G
                // apbean_5G.setSsidHidden(1);// ssid hidden enable
                // apbean_5G.setSecurityMode(1);// wep
                // apbean_5G.setWepType(1);// share
                // apbean_5G.setWepKey("12345");
                // List<WlanResult.APListBean> aps = new ArrayList<>();
                // aps.add(apbean_2P4);
                // aps.add(apbean_5G);
                // result.setAPList(aps);
                // result.setWlanAPMode(2);
                // TOGO 2017/11/3 0003 测试代码 END

                // 2.根据情况显示banner
                if (APExist == Conn.MODE_2P4G_5G) {// 两种模式
                    tv2p4Checker.setVisibility(View.VISIBLE);
                    tv5GChecker.setVisibility(View.VISIBLE);
                    vSplitBanner.setVisibility(View.VISIBLE);
                    initFra(clazzs[0], clazzs);
                } else if (APExist == Conn.MODE_2P4G) {// 2.4G模式
                    tv2p4Checker.setVisibility(View.VISIBLE);
                    initFra(clazzs[0], clazzs[0]);
                } else {// 5G模式
                    tv5GChecker.setVisibility(View.VISIBLE);
                    initFra(clazzs[1], clazzs[1]);
                }
                // 3.等待界面消隐
                stopWait();
            }

            @Override
            public void onError(Throwable e) {
                showErrorUi();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                showErrorUi();
            }
        });
    }

    private void initTimer() {
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState result) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        finishMain();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        finishMain();
                    }

                    private void finishMain() {
                        if (!MainActivity.activity.isFinishing()) {
                            MainActivity.activity.finish();
                        }
                        ChangeActivity.toActivity(activity, RefreshWifiActivity.class, false, true, false, 0);
                    }
                });
            }
        };
        timerHelper.start(2500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAP();
        timerHelper.stop();
    }

    @OnClick({R.id.iv_wifisetting_back,// 返回键
                     R.id.tv_wifisetting_back,// 返回键
                     R.id.tv_wifisetting_done,// 完成键
                     R.id.tv_2p4_checker,// 2.4G
                     R.id.tv_5G_checker,// 5G
                     R.id.rl_wifisetting_wait,// 等待界面
                     R.id.rl_wifisetting_error// 错误界面
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_wifisetting_back:
            case R.id.tv_wifisetting_back:
                finish();
                break;
            case R.id.tv_wifisetting_done:
                clickDoneButton();// 提交数据更新配置
                break;
            case R.id.tv_2p4_checker:
                // 切换2.4G fragment
                transferInfo(0);
                break;
            case R.id.tv_5G_checker:
                // 切换5G fragment
                transferInfo(1);
                break;
            case R.id.rl_wifisetting_wait:// 等待界面
            case R.id.rl_wifisetting_error:// 错误界面
                break;
        }
    }

    /* -------------------------------------------- METHOD -------------------------------------------- */

    /**
     * **** 点击done按钮的操作 *****
     */
    private void clickDoneButton() {
        // 1.根据当前安全策略恢复部分信息
        apbean_2P4 = reBackInfo(apbean_2P4, pb_2P4_default);
        apbean_5G = reBackInfo(apbean_5G, pb_5G_default);
        // 2.判断密码规则并返回各AP的状态
        List<PsdRuleBean> psdRuleBeen = OtherUtils.checkPsdRule(apbean_2P4, apbean_5G);
        // 3.查看状态,如果有错误,根据判断结果跳转到对应的fragment
        ScanAP(psdRuleBeen);
    }

    /**
     * 查看状态,检查每个AP是否有不符合规定的值
     *
     * @param psdRuleBeen
     */
    private void ScanAP(List<PsdRuleBean> psdRuleBeen) {
        boolean isDone = true;
        for (PsdRuleBean prb : psdRuleBeen) {
            // 安全策略为disable--> 略过
            if (prb.getSecurityMode() == Conn.disable) {
                continue;
            }
            // WEP模式 | WPA模式
            boolean wepNotMatch = prb.getSecurityMode() == Conn.WEP & !prb.isMatchWep();
            boolean wpaNotMatch = prb.getSecurityMode() != Conn.WEP & !prb.isMatchWpa();
            if (wepNotMatch || wpaNotMatch) {
                // 1.切换banner
                Class clazz = prb.getWlanID() == Conn.WLANAPID_2P4G ? clazzs[0] : clazzs[1];
                transferBanner(clazz);
                // 2.切换fragment
                fraHelper.transfer(clazz);
                // 3.切换标记为[不允许提交]
                isDone = false;
                // 4.弹出提示
                if (wepNotMatch) {
                    ToastUtil_m.showLong(this, toastWepWarn);
                } else {
                    ToastUtil_m.showLong(this, toastWpaWarn);
                }
                // 5.隐藏软键盘
                OtherUtils.hideKeyBoard(this);
                break;
            }
        }
        // 以上都没有问题--> 直接提交
        if (isDone) {
            // TODO: 2017/11/4 0004 正式提交
            commitWlan();
        }
    }

    /**
     * ***** 正式提交 *****
     */
    private void commitWlan() {
        pgd = OtherUtils.showProgressPop(this);
        API.get().setWlanSettings(result, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                OtherUtils.hideProgressPop(pgd);
                OtherUtils.setWifiActive(activity, false);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
                ChangeActivity.toActivity(activity, RefreshWifiActivity.class, false, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                ChangeActivity.toActivity(activity, RefreshWifiActivity.class, false, true, false, 0);
            }
        });
    }

    /**
     * 回读初始保存的wep|wpa信息
     *
     * @param ap
     * @param psdBean
     * @return
     */
    public WlanResult.APListBean reBackInfo(WlanResult.APListBean ap, PsdBean psdBean) {
        if (ap != null) {
            if (ap.getSecurityMode() == Conn.disable) {
                // 全部还原
                ap.setWepKey(psdBean.getWepKey());
                ap.setWepType(psdBean.getWepType());
                ap.setWpaKey(psdBean.getWpaKey());
                ap.setWpaType(psdBean.getWpaType());
            } else if (ap.getSecurityMode() == Conn.WEP) {
                // 还原WPA
                ap.setWpaKey(psdBean.getWpaKey());
                ap.setWpaType(psdBean.getWpaType());
            } else {
                // 还原WEP
                ap.setWepKey(psdBean.getWepKey());
                ap.setWepType(psdBean.getWepType());
            }
        }
        return ap;
    }

    /**
     * 设置初始默认的密码以及策略(用于提交的时候还原用)
     *
     * @param wlanId
     * @param ap
     */
    public PsdBean setDefaultInfo(int wlanId, WlanResult.APListBean ap) {

        if (wlanId == Conn.WLANAPID_2P4G) {
            pb_2P4_default.setWepKey(ap.getWepKey());
            pb_2P4_default.setWepType(ap.getWepType());
            pb_2P4_default.setWpaKey(ap.getWpaKey());
            pb_2P4_default.setWpaType(ap.getWpaType());
            return pb_2P4_default;
        } else {
            pb_5G_default.setWepKey(ap.getWepKey());
            pb_5G_default.setWepType(ap.getWepType());
            pb_5G_default.setWpaKey(ap.getWpaKey());
            pb_5G_default.setWpaType(ap.getWpaType());
            return pb_5G_default;
        }
    }

    /**
     * 初始化fragment
     *
     * @param initClass 首先展示的fragment class
     * @param clazzs    fragment类集合
     */
    private void initFra(Class initClass, Class... clazzs) {
        fraHelper = new FraHelper(this, clazzs, initClass, container);
    }

    /**
     * 切换fragment
     *
     * @param index
     */
    private void transferInfo(int index) {
        Class clazz = clazzs[index];
        transferBanner(clazz);// 切换banner
        fraHelper.transfer(clazz);// 切换fragment
        OtherUtils.hideKeyBoard(this);// 隐藏软键盘
    }

    /**
     * 切换banner
     *
     * @param clazz
     */
    private void transferBanner(Class clazz) {
        tv2p4Checker.setTextColor(clazz == clazzs[0] ? colorCheck : colorUnCheck);
        tv2p4Checker.setTypeface(clazz == clazzs[0] ? textBold : textNormal);
        tv5GChecker.setTextColor(clazz == clazzs[1] ? colorCheck : colorUnCheck);
        tv5GChecker.setTypeface(clazz == clazzs[1] ? textBold : textNormal);
    }

    /**
     * 等待界面dismiss
     */
    public void stopWait() {
        lvcWait.stopAnim();
        rlWifisettingWait.setVisibility(View.GONE);
    }

    /**
     * 显示错误的界面并隐藏Done按钮
     */
    private void showErrorUi() {
        stopWait();
        rlWifisettingError.setVisibility(View.VISIBLE);// 显示错误的界面
        tvWifisettingDone.setVisibility(View.GONE);// 隐藏Done按钮
    }

    /**
     * 清空静态引用
     */
    private void clearAP() {
        apbean_2P4 = null;
        apbean_5G = null;
        pb_2P4_default = null;
        pb_5G_default = null;
    }
}
