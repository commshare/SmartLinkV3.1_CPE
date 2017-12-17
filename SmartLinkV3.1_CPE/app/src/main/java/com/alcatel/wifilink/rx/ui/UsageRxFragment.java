package com.alcatel.wifilink.rx.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.Usage.UsageSettings;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.UsageHelper;
import com.alcatel.wifilink.ui.activity.SettingNetworkActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.FraHelpers;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by qianli.ma on 2017/12/8 0008.
 */

public class UsageRxFragment extends Fragment implements FragmentBackHandler {

    @BindView(R.id.iv_usage_rx_back)
    ImageView ivBack;
    @BindView(R.id.tv_usage_rx_homeNetwork_traffic)
    TextView tvNetworkTraffic;
    @BindView(R.id.tv_usage_rx_homeNetwork_time)
    TextView tvNetworkTime;
    @BindView(R.id.tv_usage_rx_roaming_traffic)
    TextView tvRoamingTraffic;
    @BindView(R.id.tv_usage_rx_roaming_time)
    TextView tvRoamingTime;
    @BindView(R.id.bt_usage_rx_resetStatist)
    Button btResetStatist;
    @BindView(R.id.tv_usage_rx_mobileNetworkSetting)
    TextView tvMobileNetworkSetting;
    Unbinder unbinder;

    private View inflate;
    private HomeRxActivity activity;
    private FraHelpers fraHelpers;
    private Class[] clazz;
    private TimerHelper timerHelper;
    private SweetAlertDialog dialog;
    private String popTitle;
    private String popCancel;
    private String popReset;
    private ProgressDialog pgd;
    private String resetFailed;
    private String reseting;
    private String resetSuccess;
    private UsageHelper usageHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (HomeRxActivity) getActivity();
        inflate = View.inflate(getActivity(), R.layout.fragment_usagerx, null);
        unbinder = ButterKnife.bind(this, inflate);
        resetUi();
        initRes();
        initData();
        return inflate;
    }

    private void initRes() {
        fraHelpers = activity.fraHelpers;
        clazz = activity.clazz;
        popTitle = getString(R.string.reset_monthly_data_usage_statistics);
        popCancel = getString(R.string.cancel);
        popReset = getString(R.string.reset);
        reseting = getString(R.string.resetting);
        resetFailed = getString(R.string.couldn_t_reset_try_again);
        resetSuccess = getString(R.string.success);
        usageHelper = new UsageHelper(getActivity());
    }

    private void initData() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                getUsed();// 已经使用 / 月计划流量
                getRoamingAndConnTime();// 获取漫游信息
            }
        };
        timerHelper.start(3000);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initData();
            resetUi();
        } else {
            stopTimer();
        }
    }

    private void resetUi() {
        activity.tabFlag = Cons.TAB_USAGE;
        activity.llNavigation.setVisibility(View.GONE);
        activity.rlBanner.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    /**
     * 获取漫游信息
     */
    private void getRoamingAndConnTime() {
        usageHelper.setOnRoamingListener(result -> {// 有漫游
            long roamUseData = result.getRoamUseData();
            UsageHelper.Usage usageByte = UsageHelper.getUsageByte(getActivity(), roamUseData);
            String roam = usageByte.usage;
            String roam_unit = usageByte.unit;
            tvRoamingTraffic.setText(roam + roam_unit);
            // 获取当前连接时间
            int currTime = (int) result.getCurrConnTimes();
            UsageHelper.Times currTime_s = UsageHelper.getUsedTimeForSec(getActivity(), currTime);
            String rhour = currTime_s.hour;
            String rmin = currTime_s.min;
            boolean isNoRHour = "0".equalsIgnoreCase(rhour);
            tvRoamingTime.setText(isNoRHour ? rmin + "m" : rhour + "h" + rmin + "m");
            // 获取总连接时间
            int tConnTimes = (int) result.getTConnTimes();
            UsageHelper.Times usedTime = UsageHelper.getUsedTimeForSec(getActivity(), tConnTimes);
            String nhour = usedTime.hour;
            String nmin = usedTime.min;
            boolean isNoTHour = "0".equalsIgnoreCase(nhour);
            tvNetworkTime.setText(isNoTHour ? nmin + "m" : nhour + "h" + nmin + "m");
        });
        usageHelper.setOnNoRoamingListener(result -> {// 没有漫游
            int tConnTimes = (int) result.getTConnTimes();
            UsageHelper.Times usedTime = UsageHelper.getUsedTimeForSec(getActivity(), tConnTimes);
            String hour = usedTime.hour;
            String min = usedTime.min;
            boolean isNoTHour = "0".equalsIgnoreCase(hour);
            tvNetworkTime.setText(isNoTHour ? min + "m" : hour + "h" + min + "m");
        });
        usageHelper.getRoamingInfo();
    }

    /**
     * 已经使用 / 月计划流量
     */
    private void getUsed() {
        RX.getInstant().getUsageSettings(new ResponseObject<UsageSettings>() {
            @Override
            protected void onSuccess(UsageSettings result) {
                // 已经使用
                long usedData = result.getUsedData();
                Logger.t("ma_usage").v(usedData + "");
                UsageHelper.Usage usedByte = UsageHelper.getUsageByte(getActivity(), usedData);
                String used = usedByte.usage;
                String used_unit = usedByte.unit;
                //  月流量计划
                long monthlyPlan = result.getMonthlyPlan();
                Logger.t("ma_usage").v(monthlyPlan + "");
                UsageHelper.Usage monthByte = UsageHelper.getUsageByte(getActivity(), monthlyPlan);
                String month = monthByte.usage;
                String month_unit = monthByte.unit;
                // 显示流量使用情况
                String unLimitShow = used + used_unit;
                String normal = used + used_unit + "/" + month + month_unit;
                tvNetworkTraffic.setText(monthlyPlan <= 0 ? unLimitShow : normal);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_usage_rx_back,// 回退
                     R.id.bt_usage_rx_resetStatist,// 重设
                     R.id.tv_usage_rx_mobileNetworkSetting})// 跳转到设置
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_usage_rx_back:
                stopTimer();
                fraHelpers.transfer(clazz[0]);
                break;
            case R.id.bt_usage_rx_resetStatist:
                clickResetButton();
                break;
            case R.id.tv_usage_rx_mobileNetworkSetting:
                to(SettingNetworkActivity.class, false);
                break;
        }
    }

    /**
     * 点击了reset按钮
     */
    private void clickResetButton() {
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)// type
                         .setTitleText(popReset)// popTitle
                         .setContentText(popTitle)// content
                         .setCancelText(popCancel)// confirm
                         .setConfirmText(popReset)// cancel
                         .setConfirmClickListener(this::resetRecord)// reset record
                         .showCancelButton(true)// cancel set true
                         .setCancelClickListener(Dialog::dismiss);
        dialog.show();
    }

    /**
     * 清空记录
     *
     * @param dialog
     */
    private void resetRecord(SweetAlertDialog dialog) {
        dialog.dismiss();
        pgd = OtherUtils.showProgressPop(getActivity());
        String currentTime = UsageHelper.getCurrentTime();
        RX.getInstant().setUsageRecordClear(currentTime, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                OtherUtils.hideProgressPop(pgd);
                toast(resetSuccess);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                toast(resetFailed);
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
                toast(resetFailed);
            }
        });
    }

    public void toast(int resId) {
        ToastUtil_m.show(getActivity(), resId);
    }

    public void toastLong(int resId) {
        ToastUtil_m.showLong(getActivity(), resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(getActivity(), content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(getActivity(), ac, false, isFinish, false, 0);
    }

    public void stopTimer() {
        if (timerHelper != null) {
            timerHelper.stop();
        }
    }

    @Override
    public boolean onBackPressed() {
        // 返回主页
        activity.fraHelpers.transfer(clazz[0]);
        return true;
    }
}
