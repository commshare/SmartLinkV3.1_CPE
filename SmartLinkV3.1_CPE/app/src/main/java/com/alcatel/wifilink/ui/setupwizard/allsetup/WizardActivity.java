package com.alcatel.wifilink.ui.setupwizard.allsetup;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.PukUnlockActivity;
import com.alcatel.wifilink.ui.activity.SimUnlockActivity;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.ui.type.ui.WanModeActivity;
import com.alcatel.wifilink.utils.ActionbarSetting;

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
                getStatus();
            }
        };
        timerHelper.start(2000);
    }

    private void getStatus() {
        // wan status
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
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
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
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
                ChangeActivity.toActivity(this, WanModeActivity.class, true, true, false, 0);
                break;
        }
    }

    /* 检测SIM卡状态 */
    private void checkSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.PIN_REQUIRED) {
                    ChangeActivity.toActivity(WizardActivity.this, SimUnlockActivity.class, true, true, false, 0);
                    return;
                }
                if (simState == Cons.PUK_REQUIRED) {
                    ChangeActivity.toActivity(WizardActivity.this, PukUnlockActivity.class, true, true, false, 0);
                    return;
                }

                if (simState == Cons.READY) {
                    EventBus.getDefault().postSticky(new TypeBean(Cons.TYPE_SIM));// SIM连接信号
                    ChangeActivity.toActivity(WizardActivity.this, HomeActivity.class, true, true, false, 0);
                    return;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHelper.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_wizard_back:
                finish();
                break;
            case R.id.tv_main_skip:
                ChangeActivity.toActivity(this, HomeActivity.class, true, true, false, 0);
                break;
        }

    }
}
