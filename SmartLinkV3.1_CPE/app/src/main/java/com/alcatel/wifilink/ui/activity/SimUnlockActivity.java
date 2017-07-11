package com.alcatel.wifilink.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.ActionbarSetting;
import com.alcatel.wifilink.utils.EditUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.alcatel.wifilink.R.id.et_sim_unlock;

public class SimUnlockActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(et_sim_unlock)
    EditText etSimUnlock;// SIM PIN
    @BindView(R.id.tv_sim_unlock_remainCount)
    TextView tvSimUnlockRemainCount;// 1,2,3...
    @BindView(R.id.tv_sim_unlock_remainDes)
    TextView tvSimUnlockRemainDes;// attempts remaining
    @BindView(R.id.rv_sim_unlock_done)
    RippleView rvSimUnlockDone;// Done

    private ActionBar actionbar;
    private int pinRemainingTimes;// 剩余次数
    boolean isDoneClick;// 是否允许可点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_unlock);
        ButterKnife.bind(this);
        actionbar = getSupportActionBar();
        // init actionbar
        initActionbar();
        // init sim status
        initSimStatus();
    }

    /* **** initSimStatus **** */
    private void initSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                runOnUiThread(() -> {
                    isDoneClick = true;
                    pinRemainingTimes = result.getPinRemainingTimes();
                    tvSimUnlockRemainCount.setText(String.valueOf(pinRemainingTimes));
                });
            }
        });
    }


    /* -------------------------------------------- Click -------------------------------------------- */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_simUnlock_back:
                finish();
                break;
        }
    }

    @OnClick(R.id.rv_sim_unlock_done)
    public void onViewClicked() {

        // check sim insert
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                
                /* sim have no insert */
                if (simState == Cons.NOWN) {
                    ToastUtil_m.show(SimUnlockActivity.this, getString(R.string.Home_no_sim));
                    return;
                }
                
                /* sim have insert */
                unlocksim();
            }
        });


    }

    /* **** unlocksim **** */
    private void unlocksim() {
        // 编辑域为空
        if (!EditUtils.isRegular(etSimUnlock)) {
            ToastUtil_m.show(this, getString(R.string.sim_unlocked_notEmpty));
            return;
        }
        // 网络还没加载完毕
        if (!isDoneClick) {
            ToastUtil_m.show(this, "Please wait...");
            return;
        }
        // PIN码次数已经用光
        if (pinRemainingTimes <= 0) {
            ToastUtil_m.show(this, getString(R.string.Home_PinTimes_UsedOut));
            toPukActivity();
            return;
        }
        // 一切正常
        String pincode = EditUtils.getContent(etSimUnlock);
        unlockPin(pincode);// to puk ui
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */

    /* **** initActionbar **** */
    private void initActionbar() {
        new ActionbarSetting() {
            private ImageView iv_back;

            @Override
            protected void findActionbarView(View view) {
                iv_back = (ImageView) view.findViewById(R.id.iv_simUnlock_back);
                iv_back.setOnClickListener(SimUnlockActivity.this);
            }
        }.settingActionbarAttr(this, actionbar, R.layout.actionbar_simunlock);
    }

    /* **** unlockPin **** */
    private void unlockPin(String pincode) {
        API.get().unlockPin(pincode, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(SimUnlockActivity.this, getString(R.string.sim_unlocked_success));
                ChangeActivity.toActivity(SimUnlockActivity.this, HomeActivity.class, true, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                getRemainTimes();
            }
        });
    }

    /* **** getRemainTimes **** */
    private void getRemainTimes() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                pinRemainingTimes = result.getPinRemainingTimes();
                runOnUiThread(() -> {
                    tvSimUnlockRemainCount.setText(String.valueOf(pinRemainingTimes));
                });

                if (pinRemainingTimes <= 0) {// 次数用光
                    ToastUtil_m.show(SimUnlockActivity.this, getString(R.string.Home_PinTimes_UsedOut));
                    toPukActivity();// to puk ui
                } else {// 输错PIN码
                    ToastUtil_m.show(SimUnlockActivity.this, getString(R.string.sim_unlocked_failed));
                    etSimUnlock.setText("");
                }
            }
        });
    }

    /* **** toPukActivity **** */
    public void toPukActivity() {
        ChangeActivity.toActivity(this, PukUnlockActivity.class, true, true, false, 0);
    }


}
