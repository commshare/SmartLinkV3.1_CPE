package com.alcatel.wifilink.ui.type.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.wan.WanSettingsParams;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.SimUnlockActivity;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.setupwizard.allsetup.TypeBean;
import com.alcatel.wifilink.ui.setupwizard.allsetup.WifiGuideActivity;
import com.alcatel.wifilink.ui.setupwizard.allsetup.WizardActivity;
import com.alcatel.wifilink.ui.type.helper.WanModeHelper;
import com.alcatel.wifilink.utils.ActionbarSetting;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WanModeActivity extends BaseActivityWithBack implements View.OnClickListener {

    @BindView(R.id.rb_dhcp)
    RadioButton rbDhcp;// dhcp
    @BindView(R.id.rb_pppoe)
    RadioButton rbpppoe;// pppoe
    @BindView(R.id.rb_staticIp)
    RadioButton rbStaticIp;// static IP
    @BindView(R.id.rg_mode)
    RadioGroup rgMode;// group
    @BindView(R.id.ll_mode)
    LinearLayout llModeType;// 待变容器(根据类型展示)
    @BindView(R.id.rv_connect)
    RippleView rvConnect;// 连接按钮

    // 等待页
    @BindView(R.id.rl_detecting)
    RelativeLayout rlDetecting;

    // 成功页
    @BindView(R.id.rl_success)
    RelativeLayout rlSuccess;

    // 失败页
    @BindView(R.id.rl_failed)
    RelativeLayout rlFailed;
    @BindView(R.id.bt_tryagain)
    Button btTryagain;
    @BindView(R.id.tv_toHome)
    TextView tvToHome;

    private RadioButton[] rbs;
    private EditText[] eds;
    private WanSettingsResult result;
    private int DELAY = 2000;
    private ActionBar actionbar;
    private ActionbarSetting actionbarSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        UiChange(true, false, false);
        rbs = new RadioButton[]{rbDhcp, rbpppoe, rbStaticIp};
    }

    private void initActionbar() {
        actionbar = getSupportActionBar();
        actionbarSetting = new ActionbarSetting() {
            @Override
            protected void findActionbarView(View view) {
                View ib_skip = view.findViewById(R.id.tv_wanmode_skip);
                View ib_back = view.findViewById(R.id.ib_wanmode_back);
                ib_skip.setOnClickListener(WanModeActivity.this);
                ib_back.setOnClickListener(WanModeActivity.this);
            }
        };
        actionbarSetting.settingActionbarAttr(this, actionbar, R.layout.actionbar_wanmode);
    }

    private void initData() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {

            @Override
            protected void onSuccess(WanSettingsResult result) {
                Log.d("ma_wanmode", result.getConnectType() + "");
                WanModeActivity.this.result = result;
                UiChange(false, false, false);// 切换状态UI
                initActionbar();// show actionbar
                TypeChange(result);// 根据类型填充待变容器UI
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                UiChange(false, false, true);
            }
        });
    }

    @OnClick({R.id.rv_connect, R.id.bt_tryagain, R.id.tv_toHome})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rv_connect:// 连接按钮
                connectToWan();
                actionbarSetting.hideActionbar(actionbar);
                break;
            case R.id.bt_tryagain:// 重试
                UiChange(true, false, false);
                initData();
                break;
            case R.id.tv_toHome:// 跳到主页
                ChangeActivity.toActivity(this, HomeActivity.class, true, true, false, 0);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_wanmode_skip:
                ChangeActivity.toActivity(this, WifiGuideActivity.class, true, false, false, 0);
                break;
            case R.id.ib_wanmode_back:// 回退按钮
                ChangeActivity.toActivity(this, WizardActivity.class, true, true, false, 0);
                break;
        }
    }

    /* -------------------------------------------- helper -------------------------------------------- */

    /**
     * @param isDetect  等待页
     * @param isSuccess 成功页
     * @param isFailed  失败页
     */
    public void UiChange(boolean isDetect, boolean isSuccess, boolean isFailed) {
        rlDetecting.setVisibility(isDetect ? View.VISIBLE : View.GONE);
        rlSuccess.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        rlFailed.setVisibility(isFailed ? View.VISIBLE : View.GONE);
    }

    /**
     * 切换类型
     *
     * @param result
     */
    private void TypeChange(WanSettingsResult result) {
        WanModeHelper wh = new WanModeHelper(WanModeActivity.this);
        rbs = wh.setNetModeUi(result, rbs);
        wh.setOnNetModeItemListener(itemEd -> eds = itemEd);
        wh.setOnClickAndInit(rbs, llModeType, result);
    }

    /**
     * 连接到WAN口
     */
    private void connectToWan() {
        int type = result.getConnectType();
        if (type == Cons.DHCP) {
            getWanStatus();
        } else if (type == Cons.PPPOE) {
            if (isEmptyEd(eds[0], eds[1], eds[2])) {
                ToastUtil_m.show(this, getString(R.string.not_empty));
                return;
            }
            connectPPPOE();// 连接
        } else if (type == Cons.STATIC) {
            if (isEmptyEd(eds[0], eds[1])) {
                ToastUtil_m.show(this, getString(R.string.not_empty));
                return;
            }
            connectStatic();
        }
    }

    /**
     * 启动连接(static ip)
     */
    private void connectStatic() {
        WanSettingsParams wsp = new WanSettingsParams();

        wsp.setIpAddress(getEd(eds[0]));// ipaddress
        wsp.setSubNetMask(getEd(eds[1]));// submask

        wsp.setAccount(result.getAccount());
        wsp.setPassword(result.getPassword());
        wsp.setMtu(result.getMtu());
        wsp.setGateway(result.getGateway());
        wsp.setConnectType(result.getConnectType());
        wsp.setPrimaryDNS(result.getPrimaryDNS());
        wsp.setSecondaryDNS(result.getSecondaryDNS());
        wsp.setStatus(Cons.CONNECTED);
        wsp.setStaticIpAddress(result.getStaticIpAddress());
        wsp.setPppoeMtu(result.getPppoeMtu());

        API.get().setWanSettings(wsp, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                getWanStatus();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                UiChange(false, false, true);
            }
        });
    }


    /**
     * 启动连接(pppoe)
     */
    private void connectPPPOE() {
        WanSettingsParams wsp = new WanSettingsParams();
        wsp.setAccount(getEd(eds[0]));// Account
        wsp.setPassword(getEd(eds[1]));// Password
        wsp.setMtu(result.getMtu());// MTU

        wsp.setSubNetMask(result.getSubNetMask());
        wsp.setGateway(result.getGateway());
        wsp.setIpAddress(result.getIpAddress());
        wsp.setConnectType(result.getConnectType());
        wsp.setPrimaryDNS(result.getPrimaryDNS());
        wsp.setSecondaryDNS(result.getSecondaryDNS());
        wsp.setStatus(Cons.CONNECTED);
        wsp.setStaticIpAddress(result.getStaticIpAddress());
        wsp.setPppoeMtu(result.getPppoeMtu());

        API.get().setWanSettings(wsp, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                getWanStatus();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                UiChange(false, false, true);
            }
        });
    }

    /* 获取WAN口是否连接(迭代调用) */
    private void getWanStatus() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                int status = result.getStatus();
                if (status == Cons.CONNECTED) {/* 连上 */
                    success();
                } else if (status == Cons.CONNECTING) {/* 正在连接 */
                    getWanStatus();
                } else {/* 没有连上 */
                    UiChange(false, false, true);
                }

            }
        });
    }

    /**
     * 连接成功后的逻辑
     */
    private void success() {
        UiChange(false, true, false);
        EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_WAN));
        ChangeActivity.toActivity(this, HomeActivity.class, true, true, false, DELAY);
    }

    /**
     * 获取编辑域内容
     *
     * @param ed
     * @return
     */
    private String getEd(EditText ed) {
        return ed.getText().toString().trim().replace(" ", "");
    }

    /**
     * 编辑域是否为空
     *
     * @param eds
     * @return
     */
    public boolean isEmptyEd(EditText... eds) {
        boolean empty = false;
        for (EditText ed : eds) {
            if (TextUtils.isEmpty(ed.getText().toString().trim().replace(" ", ""))) {
                empty = true;
            }
        }
        return empty;
    }


}
