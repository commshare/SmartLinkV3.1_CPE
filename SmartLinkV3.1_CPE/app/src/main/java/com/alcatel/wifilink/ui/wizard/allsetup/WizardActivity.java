package com.alcatel.wifilink.ui.wizard.allsetup;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.LoginActivity;
import com.alcatel.wifilink.ui.activity.PukUnlockActivity;
import com.alcatel.wifilink.ui.activity.SimUnlockActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.type.ui.WanModeActivity;
import com.alcatel.wifilink.utils.ActionbarSetting;
import com.alcatel.wifilink.utils.OtherUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WizardActivity extends BaseActivityWithBack implements View.OnClickListener {

    @BindView(R.id.tv_sim_insert)
    TextView tvSimInsert;
    @BindView(R.id.tv_wan_insert)
    TextView tvWanInsert;

    private ActionBar actionbar;
    private TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        ButterKnife.bind(this);
        initActionbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerHelper = new TimerHelper(this) {
            @Override
            public void doSomething() {
                heartBeat();// 发送心跳--> 保持登陆
                getStatus();// 获取状态
            }
        };
        timerHelper.start(2000);
    }

    private void heartBeat() {
        RX.getInstant().heartBeat(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ToastUtil_m.show(WizardActivity.this, getString(R.string.login_logout_successful));
                CA.toActivity(WizardActivity.this, LoginActivity.class, false, true, false, 0);
            }
        });
    }

    private void getStatus() {
        // wan status
        RX.getInstant().getWanSettings(new ResponseObject<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                setWanConnectStatus(result.getStatus() == Cons.CONNECTED);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                setWanConnectStatus(false);
            }
        });

        // sim status
        RX.getInstant().getSimStatus(new ResponseObject<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                setSimConnectStatus(simState == Cons.READY || simState == Cons.PIN_REQUIRED || simState == Cons.PUK_REQUIRED);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                setSimConnectStatus(false);
            }
        });
    }

    /* 设置SIM卡UI状态 */
    private void setSimConnectStatus(boolean simInsert) {
        tvSimInsert.setTextColor(getResources().getColor(simInsert ? R.color.black_text : R.color.red));
        tvSimInsert.setText(simInsert ? R.string.connect_type_select_sim_card_enable : R.string.connect_type_select_sim_card_disable);
        tvSimInsert.setEnabled(simInsert);
        tvSimInsert.setCompoundDrawablesWithIntrinsicBounds(0, simInsert ? R.drawable.results_sim_nor : R.drawable.results_sim_dis, 0, 0);
    }

    /* 设置WAN口UI状态 */
    private void setWanConnectStatus(boolean wanConnect) {
        tvWanInsert.setTextColor(getResources().getColor(wanConnect ? R.color.black_text : R.color.red));
        tvWanInsert.setText(wanConnect ? R.string.connect_type_select_wan_port_enable : R.string.connect_type_select_wan_port_disable);
        tvWanInsert.setEnabled(wanConnect);
        tvWanInsert.setCompoundDrawablesWithIntrinsicBounds(0, wanConnect ? R.drawable.results_wan_nor : R.drawable.results_wan_dis, 0, 0);
    }

    private void initActionbar() {
        actionbar = getSupportActionBar();
        new ActionbarSetting() {
            @Override
            protected void findActionbarView(View view) {
                View ib_back = view.findViewById(R.id.ib_wizard_back);
                View tv_skip = view.findViewById(R.id.tv_main_skip);
                ib_back.setOnClickListener(WizardActivity.this);
                tv_skip.setOnClickListener(WizardActivity.this);
            }
        }.settingActionbarAttr(this, actionbar, R.layout.actionbar_wizard);
    }

    @OnClick({R.id.tv_sim_insert, R.id.tv_wan_insert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_sim_insert:
                checkSimStatus();
                break;
            case R.id.tv_wan_insert:
                EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_WAN));// WAN连接信号
                CA.toActivity(this, WanModeActivity.class, true, true, false, 0);
                break;
        }
    }

    /* 检测SIM卡状态 */
    private void checkSimStatus() {
        RX.getInstant().getSimStatus(new ResponseObject<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.PIN_REQUIRED) {
                    CA.toActivity(WizardActivity.this, SimUnlockActivity.class, false, true, false, 0);
                    return;
                }
                if (simState == Cons.PUK_REQUIRED) {
                    CA.toActivity(WizardActivity.this, PukUnlockActivity.class, false, true, false, 0);
                    return;
                }

                if (simState == Cons.READY) {
                    EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_SIM));// SIM连接信号
                    /* 检测是否设置过WIFI-GUIDE向导页 */
                    OtherUtils.loginSkip(WizardActivity.this);
                    return;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logout();// 登出
        finish();
    }

    public void logout() {
        RX.getInstant().logout(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    private void stopTimer() {
        if (timerHelper != null) {
            timerHelper.stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_wizard_back:
                finish();
                break;
            case R.id.tv_main_skip:
                OtherUtils.loginSkip(WizardActivity.this);
                break;
        }

    }
}
