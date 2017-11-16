package com.alcatel.wifilink.rx.ui;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.SP;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.system.WanSetting;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.bean.PinPukBean;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.OtherUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WizardRxActivity extends BaseActivityWithBack {

    @BindView(R.id.rl_sim_rx)
    PercentRelativeLayout rlSimRx;
    @BindView(R.id.rl_wan_rx)
    PercentRelativeLayout rlWanRx;
    @BindView(R.id.iv_sim_rx)
    ImageView ivSimRx;
    @BindView(R.id.iv_wan_rx)
    ImageView ivWanRx;
    @BindView(R.id.v_rx_split_wizard)
    View vRxSplitWizard;
    @BindView(R.id.tv_sim_rx)
    TextView tvSimRx;
    @BindView(R.id.tv_wan_rx)
    TextView tvWanRx;

    private TimerHelper heartTimerHelper;// 心跳定时器
    private TimerHelper connectTimer;// 连接定时器
    private Drawable sim_unchecked_pic;
    private Drawable sim_checked_pic;
    private Drawable wan_unchecked_pic;
    private Drawable wan_checked_pic;
    private String sim_checked_str;
    private String sim_unchecked_str;
    private String wan_checked_str;
    private String wan_unchecked_str;
    private int red_color;
    private int blue_color;
    private ProgressDialog pgd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmartLinkV3App.getContextInstance().add(this);
        setContentView(R.layout.activity_wizard_rx);
        ButterKnife.bind(this);
        initRes();
        // 启动心跳防止登出
        heartTimer();
    }

    private void initRes() {
        sim_unchecked_pic = getResources().getDrawable(R.drawable.results_sim_dis);
        sim_checked_pic = getResources().getDrawable(R.drawable.results_sim_nor);
        wan_unchecked_pic = getResources().getDrawable(R.drawable.results_wan_dis);
        wan_checked_pic = getResources().getDrawable(R.drawable.results_wan_nor);
        sim_checked_str = getResources().getString(R.string.connect_type_select_sim_card_enable);
        sim_unchecked_str = getResources().getString(R.string.connect_type_select_sim_card_disable);
        wan_checked_str = getResources().getString(R.string.connect_type_select_wan_port_enable);
        wan_unchecked_str = getResources().getString(R.string.connect_type_select_wan_port_disable);
        red_color = getResources().getColor(R.color.color_red);
        blue_color = getResources().getColor(R.color.mg_blue);
    }

    /**
     * 检测连接信号
     */
    private void connectTimer() {
        connectTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                // 检测WAN口连接
                API.get().getWanSeting(new MySubscriber<WanSetting>() {
                    @Override
                    protected void onSuccess(WanSetting result) {
                        boolean isWanConnect = result.getStatus() == Cons.CONNECTED;
                        ivWanRx.setImageDrawable(isWanConnect ? wan_checked_pic : wan_unchecked_pic);
                        tvWanRx.setText(isWanConnect ? wan_checked_str : wan_unchecked_str);
                        tvWanRx.setTextColor(isWanConnect ? blue_color : red_color);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        ivWanRx.setImageDrawable(wan_unchecked_pic);
                        tvWanRx.setText(wan_unchecked_str);
                        tvWanRx.setTextColor(red_color);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ivWanRx.setImageDrawable(wan_unchecked_pic);
                        tvWanRx.setText(wan_unchecked_str);
                        tvWanRx.setTextColor(red_color);
                    }
                });
                // 检测SIM卡连接
                API.get().getSimStatus(new MySubscriber<SimStatus>() {
                    @Override
                    protected void onSuccess(SimStatus result) {
                        int simState = result.getSIMState();
                        boolean isSimEffect = simState == Cons.PIN_REQUIRED | simState == Cons.PUK_REQUIRED | simState == Cons.READY;
                        ivSimRx.setImageDrawable(isSimEffect ? sim_checked_pic : sim_unchecked_pic);
                        tvSimRx.setText(isSimEffect ? sim_checked_str : sim_unchecked_str);
                        tvSimRx.setTextColor(isSimEffect ? blue_color : red_color);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        ivSimRx.setImageDrawable(sim_unchecked_pic);
                        tvSimRx.setText(sim_unchecked_str);
                        tvSimRx.setTextColor(red_color);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ivSimRx.setImageDrawable(sim_unchecked_pic);
                        tvSimRx.setText(sim_unchecked_str);
                        tvSimRx.setTextColor(red_color);
                    }
                });
            }
        };
        connectTimer.start(3000);
    }

    private void heartTimer() {
        OtherUtils.setOnHeartBeatListener(this::connectTimer);
        heartTimerHelper = OtherUtils.startHeartBeat(this, LoginRxActivity.class);
    }

    @OnClick({R.id.rl_sim_rx, R.id.rl_wan_rx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_sim_rx:
                toSimSetting();
                break;
            case R.id.rl_wan_rx:
                toWanSetting();
                break;
        }
    }

    private void toWanSetting() {
        if (pgd == null) {
            pgd = OtherUtils.showProgressPop(this);
        }
        API.get().getWanSeting(new MySubscriber<WanSetting>() {
            @Override
            protected void onSuccess(WanSetting result) {
                int status = result.getStatus();
                if (status == Cons.CONNECTED) {
                    OtherUtils.hideProgressPop(pgd);
                    to(WanModeRxActivity.class);
                } else if (status == Cons.CONNECTING) {
                    toWanSetting();
                } else {
                    OtherUtils.hideProgressPop(pgd);
                    toast(R.string.connect_failed);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                toast(R.string.connect_failed);
                OtherUtils.hideProgressPop(pgd);
                to(RefreshWifiRxActivity.class);
            }

            @Override
            public void onError(Throwable e) {
                toast(R.string.connect_failed);
                OtherUtils.hideProgressPop(pgd);
                to(RefreshWifiRxActivity.class);
            }
        });
    }

    private void toSimSetting() {
        if (pgd == null) {
            pgd = OtherUtils.showProgressPop(this);
        }
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.READY) {
                    OtherUtils.hideProgressPop(pgd);
                    // 检测是否进入过向导设置
                    if (SP.getInstance(WizardRxActivity.this).getBoolean(Cons.DATAPLAN_RX, false)) {
                        if (SP.getInstance(WizardRxActivity.this).getBoolean(Cons.WIFIINIT_RX, false)) {
                            to(HomeRxActivity.class);
                        } else {
                            to(WifiInitRxActivity.class);
                        }
                    } else {
                        to(DataPlanRxActivity.class);
                    }
                    return;
                }
                if (simState == Cons.NONE) {
                    OtherUtils.hideProgressPop(pgd);
                    toast(R.string.connect_type_select_sim_card_disable);
                    return;
                }
                if (simState == Cons.DETECTED || simState == Cons.INITING) {
                    toSimSetting();
                    return;
                }
                if (simState == Cons.SIMLOCK) {
                    OtherUtils.hideProgressPop(pgd);
                    toast(R.string.Home_SimLock_Required);
                    return;
                }
                if (simState == Cons.ILLEGAL) {
                    OtherUtils.hideProgressPop(pgd);
                    toast(R.string.Home_sim_invalid);
                    return;
                }
                if (simState == Cons.PIN_REQUIRED) {
                    OtherUtils.hideProgressPop(pgd);
                    EventBus.getDefault().postSticky(new PinPukBean(Cons.PIN_FLAG));
                    to(PinPukIndexRxActivity.class);
                    return;
                }
                if (simState == Cons.PUK_REQUIRED) {
                    OtherUtils.hideProgressPop(pgd);
                    EventBus.getDefault().postSticky(new PinPukBean(Cons.PUK_FLAG));
                    to(PinPukIndexRxActivity.class);
                    return;
                }
                if (simState == Cons.PUK_TIMESOUT) {
                    OtherUtils.hideProgressPop(pgd);
                    toast(R.string.puk_alarm_des1);
                    return;
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                toast(R.string.connect_failed);
                OtherUtils.hideProgressPop(pgd);
                to(RefreshWifiRxActivity.class);
            }

            @Override
            public void onError(Throwable e) {
                toast(R.string.connect_failed);
                OtherUtils.hideProgressPop(pgd);
                to(RefreshWifiRxActivity.class);
            }
        });
    }

    /**
     * 跳转ACTIVITY
     *
     * @param target
     */
    public void to(Class target) {
        CA.toActivity(this, target, false, true, false, 0);
    }

    /**
     * 显示吐司
     *
     * @param resId
     */
    public void toast(int resId) {
        ToastUtil_m.show(WizardRxActivity.this, getString(resId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止心跳定时器
        OtherUtils.stopHeartBeat(heartTimerHelper);
        // 停止检测连接定时器
        connectTimer.stop();
    }
}
