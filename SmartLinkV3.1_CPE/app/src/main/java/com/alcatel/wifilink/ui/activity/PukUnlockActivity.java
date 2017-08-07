package com.alcatel.wifilink.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.setupwizard.allsetup.WizardActivity;
import com.alcatel.wifilink.utils.ActionbarSetting;
import com.alcatel.wifilink.utils.EditUtils;
import com.alcatel.wifilink.utils.OtherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PukUnlockActivity extends BaseActivityWithBack implements View.OnClickListener {

    @BindView(R.id.et_puk_unlock)
    EditText etPukUnlock;// PUK CODE
    @BindView(R.id.tv_puk_unlock_remainCount)
    TextView tvPukUnlockRemainCount;// remain times
    @BindView(R.id.tv_puk_unlock_remainDes)
    TextView tvPukUnlockRemainDes;// des: attempts times
    @BindView(R.id.rl_puk_remain)
    RelativeLayout rlPukRemain;
    @BindView(R.id.et_puk_pinUnlock)
    EditText etPukPinUnlock;// new sim pin
    @BindView(R.id.et_puk_pinConfirm)
    EditText etPukPinConfirm;// confirm new pin
    @BindView(R.id.rv_puk_unlock_done)
    RippleView rvPukUnlockDone;// done button

    private ActionBar actionbar;
    private ImageView iv_back;
    private int pukRemain;// PUK剩余次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtherUtils.stopAutoTimer();
        setContentView(R.layout.activity_unlockpuk);
        actionbar = getSupportActionBar();
        ButterKnife.bind(this);
        // init action bar
        initActionbar();
        // init puk remain
        getPUKInfo();
    }

    /* **** initActionbar **** */
    private void initActionbar() {
        new ActionbarSetting() {
            @Override
            protected void findActionbarView(View view) {
                iv_back = (ImageView) view.findViewById(R.id.iv_simUnlock_back);
                iv_back.setOnClickListener(PukUnlockActivity.this);
            }
        }.settingActionbarAttr(this, actionbar, R.layout.actionbar_simunlock);
    }

    /* **** getPUKInfo **** */
    private void getPUKInfo() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.NOWN) {// no sim card
                    ToastUtil_m.show(PukUnlockActivity.this, getString(R.string.Home_no_sim));
                    return;
                }
                if (simState == Cons.PUK_REQUIRED) {// puk required
                    pukRemain = result.getPukRemainingTimes();// get remaining times
                    runOnUiThread(() -> {

                        if (pukRemain == 0) {
                            setTipUi(12, Color.RED, pukRemain, getString(R.string.puk_alarm_des1));
                        }

                        if (pukRemain == 1) {
                            setTipUi(12, Color.RED, pukRemain, getString(R.string.puk_remain_1time));
                        }

                        if (pukRemain > 1) {
                            setTipUi(16, Color.RED, pukRemain, getString(R.string.sim_unlocked_attempts));
                        }
                    });
                }
            }
        });
    }

    /**
     * @param fontsize  字体大小
     * @param colorId   字体颜色
     * @param textCount 剩余次数
     * @param textDes   描述
     */
    private void setTipUi(int fontsize, int colorId, int textCount, String textDes) {
        tvPukUnlockRemainCount.setText(textCount + "");
        tvPukUnlockRemainCount.setTextSize(Dimension.SP, fontsize);
        tvPukUnlockRemainCount.setTextColor(colorId);

        tvPukUnlockRemainDes.setTextSize(Dimension.SP, fontsize);
        tvPukUnlockRemainDes.setTextColor(colorId);
        tvPukUnlockRemainDes.setText(textDes);
    }

    @OnClick(R.id.rv_puk_unlock_done)
    public void onViewClicked() {
        // 检查编辑域 true: not empty; false: empty
        if (!isEtEmpty()) {// not empty
            API.get().getSimStatus(new MySubscriber<SimStatus>() {
                @Override
                protected void onSuccess(SimStatus result) {
                    int simState = result.getSIMState();
                    if (simState == Cons.NOWN) {// no sim
                        ToastUtil_m.show(PukUnlockActivity.this, getString(R.string.home_no_sim));
                        return;
                    }
                    if (simState == Cons.PUK_REQUIRED) {// puk required
                        String pukcode = EditUtils.getContent(etPukUnlock);
                        String pincode = EditUtils.getContent(etPukPinUnlock);
                        unlockPuk(pukcode, pincode);// unlock puk
                    }
                }
            });
        }

    }

    /* **** unlockPuk **** */
    private void unlockPuk(String pukcode, String pincode) {
        API.get().unlockPuk(pukcode, pincode, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(PukUnlockActivity.this, getString(R.string.puk_unlock_success));
                ChangeActivity.toActivity(PukUnlockActivity.this, LoadingActivity.class, true, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                ToastUtil_m.show(PukUnlockActivity.this, getString(R.string.puk_unlock_failed));
                getPUKInfo();
            }
        });
    }

    /* 检查编辑域是否为空 */
    private boolean isEtEmpty() {
        if (!EditUtils.isRegular(etPukUnlock)) {
            ToastUtil_m.show(this, getString(R.string.puk_empty));
            return true;
        }
        if (!EditUtils.isRegular(etPukPinUnlock)) {
            ToastUtil_m.show(this, getString(R.string.pin_empty));
            return true;
        }
        if (!EditUtils.isRegular(etPukPinConfirm)) {
            ToastUtil_m.show(this, getString(R.string.pin_confirm_empty));
            return true;
        }

        if (!EditUtils.getContent(etPukPinUnlock).equalsIgnoreCase(EditUtils.getContent(etPukPinConfirm))) {
            ToastUtil_m.show(this, getString(R.string.pin_no_same));
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_simUnlock_back:
                ChangeActivity.toActivity(PukUnlockActivity.this, LoadingActivity.class, true, true, false, 0);
                break;
        }

    }
}
