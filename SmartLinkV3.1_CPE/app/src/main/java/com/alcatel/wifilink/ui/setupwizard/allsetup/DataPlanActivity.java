package com.alcatel.wifilink.ui.setupwizard.allsetup;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.ActionbarSetting;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataPlanActivity extends BaseActivityWithBack {

    @BindView(R.id.et_limit)
    EditText etLimit;
    @BindView(R.id.tv_limit_kb)
    TextView tvLimitKb;
    @BindView(R.id.tv_limit_mb)
    TextView tvLimitMb;
    @BindView(R.id.tv_limit_gb)
    TextView tvLimitGb;
    @BindView(R.id.sc_limit_autodisconnect)
    ImageView scLimitAutodisconnect;
    @BindView(R.id.rl_sc_limit)
    RelativeLayout rlScLimit;
    @BindView(R.id.rp_limit)
    RippleView rpLimit;

    List<TextView> tv_units;
    boolean isAutoDisconnect = true;
    int unit = 2;// 0: MB 1: GB 2: KB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_plan);
        ButterKnife.bind(this);
        initActionbar();
        initView();
    }

    private void initView() {
        tv_units = new ArrayList<>();
        tv_units.add(tvLimitKb);
        tv_units.add(tvLimitMb);
        tv_units.add(tvLimitGb);
    }

    private void initActionbar() {
        new ActionbarSetting() {
            @Override
            protected void findActionbarView(View view) {

            }
        }.settingActionbarAttr(this, getSupportActionBar(), R.layout.actionbar_setdataplan);
    }

    @OnClick({R.id.tv_limit_kb, R.id.tv_limit_mb, R.id.tv_limit_gb, R.id.sc_limit_autodisconnect, R.id.rp_limit, R.id.rl_sc_limit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_limit_kb:
                unit = 2;
                setTvUnit(tvLimitKb);
                break;
            case R.id.tv_limit_mb:
                unit = 0;
                setTvUnit(tvLimitMb);
                break;
            case R.id.tv_limit_gb:
                unit = 1;
                setTvUnit(tvLimitGb);
                break;
            case R.id.rl_sc_limit:
            case R.id.sc_limit_autodisconnect:
                changeAutoDisUi();
                break;
            case R.id.rp_limit:
                commitData();
                break;
        }
    }

   

    /* -------------------------------------------- helper -------------------------------------------- */

    /**
     * 修改自动掉线UI
     */
    private void changeAutoDisUi() {
        isAutoDisconnect = !isAutoDisconnect;
        scLimitAutodisconnect.setImageResource(isAutoDisconnect ? R.drawable.pwd_switcher_on : R.drawable.pwd_switcher_off);
    }

    /**
     * 提交并修改数据
     */
    private void commitData() {
        // 编辑计划
        String plan = etLimit.getText().toString().trim().replace(" ", "");
        long data = !TextUtils.isEmpty(plan) ? Long.valueOf(plan) : 0;
        data = getUnitByte(data, unit);
        // 编辑自动掉线
        int autoDisconnect = isAutoDisconnect ? 1 : 0;
        // 提交
        commit(data, autoDisconnect);
    }

    /**
     * 真正提交数据
     *
     * @param data
     * @param autoDisconnect
     */
    private void commit(long data, int autoDisconnect) {
        SharedPrefsUtil.getInstance(DataPlanActivity.this).putBoolean(Cons.DATA_PLAN_FLAG, true);
        UsageSetting us = new UsageSetting();
        us.setAutoDisconnFlag(autoDisconnect);
        us.setMonthlyPlan(data);
        API.get().setUsageSetting(us, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(DataPlanActivity.this, getString(R.string.succeed));
                ChangeActivity.toActivity(DataPlanActivity.this, HomeActivity.class, false, true, false, 0);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                
                ChangeActivity.toActivity(DataPlanActivity.this, HomeActivity.class, false, true, false, 0);
            }
        });
    }

    /**
     * 设置选项UI
     *
     * @param tv
     */
    private void setTvUnit(TextView tv) {
        for (TextView tv_unit : tv_units) {
            if (tv.getId() == tv_unit.getId()) {
                tv_unit.setBackgroundColor(getResources().getColor(R.color.AA009AFF));
            } else {
                tv_unit.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }
    }

    /**
     * 转换单位
     *
     * @param data edittext获取的数据
     * @param unit 单位
     * @return 转换后的数据
     */
    private long getUnitByte(long data, int unit) {
        if (unit == Constants.UsageSetting.UNIT_MB) {
            data = data * 1024 * 1024;
        } else if (unit == Constants.UsageSetting.UNIT_GB) {
            data = data * 1024 * 1024 * 1024;
        } else if (unit == Constants.UsageSetting.UNIT_KB) {
            data = data * 1024;
        }
        return data;
    }
}
