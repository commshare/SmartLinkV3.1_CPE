package com.alcatel.wifilink.rx.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.model.Usage.UsageSettings;
import com.alcatel.wifilink.rx.adapter.BillingAdaper;
import com.alcatel.wifilink.rx.helper.base.UsageHelper;
import com.alcatel.wifilink.rx.helper.base.UsageSettingHelper;
import com.alcatel.wifilink.rx.helper.business.BillingHelper;
import com.alcatel.wifilink.rx.helper.business.SetTimeLimitHelper;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.FraHelpers;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ScreenSize;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.orhanobut.logger.Logger;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by qianli.ma on 2017/12/13 0013.
 */

public class SetDataPlanRxfragment extends Fragment implements FragmentBackHandler {


    @BindView(R.id.iv_setPlan_rx_back)
    ImageView ivBack;
    @BindView(R.id.tv_setPlan_rx_monthly)
    TextView tvMonthly;
    @BindView(R.id.rl_setPlan_rx_monthly)
    PercentRelativeLayout rlMonthly;
    @BindView(R.id.tv_setPlan_rx_billing)
    TextView tvBilling;
    @BindView(R.id.rl_setPlan_rx_billing)
    PercentRelativeLayout rlBilling;
    @BindView(R.id.tv_setPlan_rx_usageAlert)
    TextView tvUsageAlert;
    @BindView(R.id.rl_setPlan_rx_usageAlert)
    PercentRelativeLayout rlUsageAlert;
    @BindView(R.id.iv_setPlan_rx_autoDisconnect)
    ImageView ivAutoDisconnect;
    @BindView(R.id.iv_setPlan_rx_timelimit)
    ImageView ivTimelimit;
    @BindView(R.id.tv_setPlan_rx_setTimelimit)
    TextView tvSetTimelimit;
    @BindView(R.id.rl_setPlan_rx_setTimelimit)
    PercentRelativeLayout rlSetTimelimit;
    Unbinder unbinder;

    private HomeRxActivity activity;
    private FraHelpers fraHelpers;
    private Class[] fraClass;
    private View inflate;
    private Drawable switch_on;
    private Drawable switch_off;
    private int blue_color;
    private int gray_color;
    private String[] days;
    private String[] alerts;
    private String gb;
    private String mb;
    private String hour;
    private String min;
    private UsageSettings usageSettings;
    private TimerHelper timerHelper;
    private UsageHelper usageHelper;
    private PopupWindows pop_billing;
    private PopupWindows pop_alert;
    private PopupWindows pop_setTimelimit;
    private PopupWindows pop_monthly;
    private Drawable pop_bg;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = View.inflate(getActivity(), R.layout.fragment_setdataplanrx, null);
        unbinder = ButterKnife.bind(this, inflate);
        initRes();
        initSome();
        resetUi();
        initTimer();
        return inflate;
    }

    private void initTimer() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                initData();
            }
        };
        timerHelper.start(2000);
    }


    private void initRes() {
        switch_on = getResources().getDrawable(R.drawable.switch_on);
        switch_off = getResources().getDrawable(R.drawable.switch_off);
        blue_color = getResources().getColor(R.color.mg_blue);
        gray_color = getResources().getColor(R.color.gray);
        pop_bg = getResources().getDrawable(R.drawable.bg_pop_conner);
        gb = getString(R.string.gb_text);
        mb = getString(R.string.mb_text);
        hour = getString(R.string.hr_s);
        min = getString(R.string.min_s);
        days = OtherUtils.getResArr(getActivity(), R.array.settings_data_plan_billing_day);
        alerts = OtherUtils.getResArr(getActivity(), R.array.settings_data_plan_usage_alert);
    }

    private void initSome() {
        activity = (HomeRxActivity) getActivity();
        fraHelpers = activity.fraHelpers;
        fraClass = activity.clazz;
        usageHelper = new UsageHelper(getActivity());
    }

    private void resetUi() {
        activity.tabFlag = Cons.TAB_SET_DATA_PLAN;
        activity.llNavigation.setVisibility(View.GONE);
        activity.rlBanner.setVisibility(View.GONE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            activity.tabFlag = Cons.TAB_SET_DATA_PLAN;
            activity.llNavigation.setVisibility(View.GONE);
            activity.rlBanner.setVisibility(View.GONE);
            initTimer();
        } else {
            stopTimer();
        }
    }

    private void stopTimer() {
        timerHelper.stop();
        timerHelper = null;
    }

    private void initData() {
        usageHelper.setOnErrorListener(attr -> toErrorUi());
        usageHelper.setOnResultErrorListener(attr -> toErrorUi());
        usageHelper.setOnUsageSettingListener(result -> {
            Logger.t("ma_setdataplan").v(result.getMonthlyPlan() + "");
            // 存放临时对象
            usageSettings = result;
            // monthly limit
            if (result.getMonthlyPlan() == 0) {
                tvMonthly.setText("");
            } else {
                UsageHelper.Usage monthly = UsageHelper.getUsageByte(getActivity(), result.getMonthlyPlan());
                tvMonthly.setText(monthly.usage + monthly.unit);
            }
            // billing day
            tvBilling.setText(days[result.getBillingDay() - 1]);
            // usage alert
            int per = SP.getInstance(getActivity()).getInt(Cons.USAGE_LIMIT, 90);
            tvUsageAlert.setText(OtherUtils.getAlert(alerts, per));
            // auto disconnect
            ivAutoDisconnect.setImageDrawable(result.getAutoDisconnFlag() == Cons.ENABLE ? switch_on : switch_off);
            // time limit
            ivTimelimit.setImageDrawable(result.getTimeLimitFlag() == Cons.ENABLE ? switch_on : switch_off);
            // set time limit show or hide
            rlSetTimelimit.setVisibility(result.getTimeLimitFlag() == Cons.ENABLE ? View.VISIBLE : View.GONE);
            // set time limit
            UsageHelper.Times timelimit_o = UsageHelper.getUsedTimeForMin(getActivity(), result.getTimeLimitTimes());
            if (timelimit_o.hour.equalsIgnoreCase("0") & timelimit_o.min.equalsIgnoreCase("0")) {
                tvSetTimelimit.setText("");
            } else if (timelimit_o.hour.equalsIgnoreCase("0")) {
                tvSetTimelimit.setText(timelimit_o.min + min);
            } else {
                tvSetTimelimit.setText(timelimit_o.hour + hour + timelimit_o.min + min);
            }

        });
        usageHelper.getUsageSettingAll();
    }

    /**
     * 出错时候的UI显示
     */
    private void toErrorUi() {
        if (tvMonthly != null & tvBilling != null & tvUsageAlert != null & ivAutoDisconnect != null & ivTimelimit != null & tvSetTimelimit != null) {
            tvMonthly.setText("");
            tvBilling.setText("");
            tvUsageAlert.setText("");
            ivAutoDisconnect.setImageDrawable(switch_off);
            ivTimelimit.setImageDrawable(switch_off);
            tvSetTimelimit.setText("");
        }
    }


    @Override
    public boolean onBackPressed() {
        fraHelpers.transfer(fraClass[Cons.TAB_MOBILE_NETWORK]);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_setPlan_rx_back,// 返回
                     R.id.rl_setPlan_rx_monthly,// 设置monthly
                     R.id.rl_setPlan_rx_billing,// 设置结算日
                     R.id.rl_setPlan_rx_usageAlert,// 设置结算日
                     R.id.iv_setPlan_rx_autoDisconnect,// auto disconnect
                     R.id.iv_setPlan_rx_timelimit,// time limit
                     R.id.rl_setPlan_rx_setTimelimit// set time limit
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_setPlan_rx_back:// 返回
                fraHelpers.transfer(fraClass[Cons.TAB_MOBILE_NETWORK]);
                break;
            case R.id.rl_setPlan_rx_monthly:// 设置monthly
                clickMonthly();
                break;
            case R.id.rl_setPlan_rx_billing:// 设置结算日
                clickBilling();
                break;
            case R.id.rl_setPlan_rx_usageAlert:// 设置流量警告
                clickAlert();
                break;
            case R.id.iv_setPlan_rx_autoDisconnect:// auto disconnect
                clickAutoDisconnect();
                break;
            case R.id.iv_setPlan_rx_timelimit:// time limit
                clickTimelimit();
                break;
            case R.id.rl_setPlan_rx_setTimelimit:// set time limit
                clickSetTimeLimit();
                break;
        }
    }

    /**
     * 提交月流量计划
     */
    private void clickMonthly() {
        View inflate = View.inflate(getActivity(), R.layout.pop_setdataplan_rx_monthly, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.30f);
        RelativeLayout rl = (RelativeLayout) inflate.findViewById(R.id.rl_pop_setPlan_rx_monthly);
        rl.setOnClickListener(null);
        EditText etNum = (EditText) inflate.findViewById(R.id.et_pop_setPlan_rx_monthly_num);
        TextView tvMb = (TextView) inflate.findViewById(R.id.tv_pop_setPlan_rx_monthly_mb);
        TextView tvGb = (TextView) inflate.findViewById(R.id.tv_pop_setPlan_rx_monthly_gb);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_pop_setPlan_rx_monthly_cancel);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_pop_setPlan_rx_monthly_ok);
        etNum.setHint("0");
        tvMb.setOnClickListener(v -> {
            tvMb.setTextColor(blue_color);
            tvGb.setTextColor(gray_color);
        });
        tvGb.setOnClickListener(v -> {
            tvMb.setTextColor(gray_color);
            tvGb.setTextColor(blue_color);
        });
        tvCancel.setOnClickListener(v -> pop_monthly.dismiss());
        tvOk.setOnClickListener(v -> setMonthly(etNum, tvMb, tvGb));
        pop_monthly = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    /**
     * 请求设置月流量计划
     */
    private void setMonthly(EditText edNum, TextView tvmb, TextView tvgb) {
        pop_monthly.dismiss();// 消隐
        String content = OtherUtils.getEdContent(edNum);
        // 非空判断
        if (TextUtils.isEmpty(content)) {
            toast(R.string.not_empty);
            content = "0";
        }
        // 范围鉴定
        int num = Integer.valueOf(content);
        if (num < 0 || num > 1024) {
            toast(R.string.input_a_data_value_between_0_1024);
            return;
        }
        // 请求提交
        ProgressDialog pgd = OtherUtils.showProgressPop(getActivity());
        UsageSettingHelper ush = new UsageSettingHelper(getActivity());
        // 1.先获取一次usage-setting
        ush.setOngetSuccessListener(attr -> {
            attr.setUnit(tvmb.getCurrentTextColor() == blue_color ? Cons.GB : Cons.MB);
            attr.setMonthlyPlan(tvmb.getCurrentTextColor() == blue_color ? num * 1024 * 1024 : num * 1024 * 1024 * 1024);
            // 2.在提交usage-setting
            UsageSettingHelper ush1 = new UsageSettingHelper(getActivity());
            ush1.setOnSetSuccessListener(attr1 -> pgd.dismiss());
            ush1.setOnErrorListener(attr1 -> pgd.dismiss());
            ush1.setOnResutlErrorListener(attr1 -> pgd.dismiss());
            ush1.setUsageSetting(attr);
        });
        ush.setOnErrorListener(attr1 -> pgd.dismiss());
        ush.setOnResutlErrorListener(attr1 -> pgd.dismiss());
        ush.getUsageSetting();
    }

    /**
     * 提交日期
     */
    private void clickBilling() {
        View inflate = View.inflate(getActivity(), R.layout.pop_setdataplan_billing, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.45f);
        RecyclerView rcv = (RecyclerView) inflate.findViewById(R.id.rcv_pop_setPlan_rx_billing);
        rcv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcv.setAdapter(new BillingAdaper(getActivity(), Arrays.asList(days)) {
            @Override
            public void clickDay(int day) {
                // 提交请求
                pop_billing.dismiss();
                BillingHelper billingHelper = new BillingHelper(getActivity());
                billingHelper.setBillingDay(day);
            }
        });
        pop_billing = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    /**
     * 提交流量警告
     */
    private void clickAlert() {
        View inflate = View.inflate(getActivity(), R.layout.pop_setdataplan_alert, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.36f);
        inflate.findViewById(R.id.tv_pop_setPlan_alert_90).setOnClickListener(v -> saveAlert(90));
        inflate.findViewById(R.id.tv_pop_setPlan_alert_80).setOnClickListener(v -> saveAlert(80));
        inflate.findViewById(R.id.tv_pop_setPlan_alert_70).setOnClickListener(v -> saveAlert(70));
        inflate.findViewById(R.id.tv_pop_setPlan_alert_60).setOnClickListener(v -> saveAlert(60));
        pop_alert = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    /**
     * 切换自动断线
     */
    private void clickAutoDisconnect() {
        ProgressDialog pgd = OtherUtils.showProgressPop(getActivity());
        UsageSettingHelper ush = new UsageSettingHelper(getActivity());
        ush.setOngetSuccessListener(attr -> {
            attr.setAutoDisconnFlag(attr.getAutoDisconnFlag() == Cons.ENABLE ? Cons.DISABLE : Cons.ENABLE);
            UsageSettingHelper ush1 = new UsageSettingHelper(getActivity());
            ush1.setOnSetSuccessListener(attr1 -> pgd.dismiss());
            ush1.setOnErrorListener(attr1 -> pgd.dismiss());
            ush1.setOnResutlErrorListener(attr1 -> pgd.dismiss());
            ush1.setUsageSetting(attr);
        });
        ush.setOnErrorListener(attr1 -> pgd.dismiss());
        ush.setOnResutlErrorListener(attr1 -> pgd.dismiss());
        ush.getUsageSetting();
    }

    /**
     * 切换时间限制按钮
     */
    private void clickTimelimit() {
        ProgressDialog pgd = OtherUtils.showProgressPop(getActivity());
        UsageSettingHelper ush = new UsageSettingHelper(getActivity());
        ush.setOngetSuccessListener(attr -> {
            attr.setTimeLimitFlag(attr.getTimeLimitFlag() == Cons.ENABLE ? Cons.DISABLE : Cons.ENABLE);
            UsageSettingHelper ush1 = new UsageSettingHelper(getActivity());
            ush1.setOnResutlErrorListener(attr1 -> pgd.dismiss());
            ush1.setOnErrorListener(attr1 -> pgd.dismiss());
            ush1.setOnSetSuccessListener(attr1 -> pgd.dismiss());
            ush1.setUsageSetting(attr);
        });
        ush.setOnErrorListener(attr1 -> pgd.dismiss());
        ush.setOnResutlErrorListener(attr1 -> pgd.dismiss());
        ush.getUsageSetting();
    }

    /**
     * 设置timelimit时间
     */
    private void clickSetTimeLimit() {
        View inflate = View.inflate(getActivity(), R.layout.pop_setdataplan_settimtlimit, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.29f);
        EditText etHour = (EditText) inflate.findViewById(R.id.et_pop_setPlan_rx_settimelimit_hour);
        EditText etMin = (EditText) inflate.findViewById(R.id.et_pop_setPlan_rx_settimelimit_min);
        etHour.setHint("0");
        etMin.setHint("5");
        SetTimeLimitHelper.addEdwatch(etHour, etMin);// 增加监听器
        View cancel = inflate.findViewById(R.id.tv_pop_setPlan_rx_settimelimit_cancel);
        View ok = inflate.findViewById(R.id.tv_pop_setPlan_rx_settimelimit_ok);
        cancel.setOnClickListener(v -> pop_setTimelimit.dismiss());
        ok.setOnClickListener(v -> {
            // 提交请求
            setSetTimeLimit(etHour, etMin);
        });
        pop_setTimelimit = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    /**
     * 提交set time limit请求
     */
    private void setSetTimeLimit(EditText etHour, EditText etMin) {
        String hour_c = OtherUtils.getEdContent(etHour);
        String min_c = OtherUtils.getEdContent(etMin);
        if (TextUtils.isEmpty(hour_c)) {
            hour_c = "0";
        }
        if (TextUtils.isEmpty(min_c)) {
            min_c = "0";
        }
        pop_setTimelimit.dismiss();
        ProgressDialog pgd = OtherUtils.showProgressPop(getActivity());
        int hour = Integer.valueOf(hour_c);
        int min = Integer.valueOf(min_c);
        int total = hour * 60 + min;
        // 用户设定的时间如果为0--> 强制关闭time limit按钮功能
        if (total <= 0) {
            UsageSettingHelper ush = new UsageSettingHelper(getActivity());
            ush.setOngetSuccessListener(attr -> {
                // 强制设定为disable
                attr.setTimeLimitFlag(Cons.DISABLE);
                UsageSettingHelper ush1 = new UsageSettingHelper(getActivity());
                ush1.setOnResutlErrorListener(attr1 -> pgd.dismiss());
                ush1.setOnErrorListener(attr1 -> pgd.dismiss());
                ush1.setOnSetSuccessListener(attr1 -> pgd.dismiss());
                ush1.setUsageSetting(attr);
            });
            ush.setOnErrorListener(attr1 -> pgd.dismiss());
            ush.setOnResutlErrorListener(attr1 -> pgd.dismiss());
            ush.getUsageSetting();
            return;
        }
        // 用户设定的时间如果不为0--> 则正常提交
        UsageSettingHelper ush = new UsageSettingHelper(getActivity());
        ush.setOngetSuccessListener(attr -> {
            attr.setTimeLimitTimes(total);
            UsageSettingHelper ush1 = new UsageSettingHelper(getActivity());
            ush1.setOnSetSuccessListener(attr1 -> pgd.dismiss());
            ush1.setOnErrorListener(attr1 -> pgd.dismiss());
            ush1.setOnResutlErrorListener(attr1 -> pgd.dismiss());
            ush1.setUsageSetting(attr);
        });
        ush.setOnErrorListener(attr1 -> pgd.dismiss());
        ush.setOnResutlErrorListener(attr1 -> pgd.dismiss());
        ush.getUsageSetting();
    }

    /**
     * 保存流量警告值
     *
     * @param value
     */
    private void saveAlert(int value) {
        pop_alert.dismiss();
        SP.getInstance(getActivity()).putInt(Cons.USAGE_LIMIT, value);
    }

    private void toast(int resId) {
        ToastUtil_m.show(getActivity(), resId);
    }

    private void toastLong(int resId) {
        ToastUtil_m.showLong(getActivity(), resId);
    }

    private void toast(String content) {
        ToastUtil_m.show(getActivity(), content);
    }

    private void to(Class ac, boolean isFinish) {
        CA.toActivity(getActivity(), ac, false, isFinish, false, 0);
    }
}
