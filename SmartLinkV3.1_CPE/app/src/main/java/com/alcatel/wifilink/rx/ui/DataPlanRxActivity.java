package com.alcatel.wifilink.rx.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.bean.PinPukBean;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.rx.helper.base.LogoutHelper;
import com.alcatel.wifilink.rx.helper.base.UsageHelper;
import com.alcatel.wifilink.rx.helper.base.WpsHelper;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ScreenSize;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataPlanRxActivity extends BaseActivityWithBack {

    @BindView(R.id.iv_dataplan_rx_back)
    ImageView ivDataplanRxBack;// 回退键
    @BindView(R.id.tv_setPlan_rx_skip)
    TextView tvSkip;// 跳过
    @BindView(R.id.iv_dataplan_rx_limit_arrow)
    ImageView ivDataplanRxLimitArrow;// 下拉箭头
    @BindView(R.id.tv_dataplan_rx_limit_unit)
    TextView tvDataplanRxLimitUnit;// 单元(MB|GB)
    @BindView(R.id.et_dataplan_rx_limit)
    EditText etDataplanRxLimit;// 编辑域
    @BindView(R.id.iv_dataplan_rx_auto)
    ImageView ivDataplanRxAuto;// 自动连接按钮
    @BindView(R.id.bt_dataplan_rx_done)
    Button btDataplanRxDone;// 提交生效

    private TimerHelper heartTimer;
    private BoardSimHelper boardSimHelper;
    private int unit = 0;// 流量单位
    private PopupWindows pop;
    private int check_color;
    private int uncheck_color;
    private String MB;
    private String GB;
    private ProgressDialog pgd;
    private UsageSetting result;
    private String flag = "";

    private void startHeartTimer() {
        heartTimer = OtherUtils.startHeartBeat(this, RefreshWifiRxActivity.class, LoginRxActivity.class);
    }

    private Drawable switch_on;
    private Drawable switch_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_data_plan_rx);
        SmartLinkV3App.getContextInstance().add(this);
        ButterKnife.bind(this);
        startHeartTimer();
        initRes();
        initBean();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getSkipFlag(String flag) {
        this.flag = flag;
    }

    private void initRes() {
        switch_on = getResources().getDrawable(R.drawable.pwd_switcher_on);
        switch_off = getResources().getDrawable(R.drawable.pwd_switcher_off);
        check_color = getResources().getColor(R.color.mg_blue);
        uncheck_color = getResources().getColor(R.color.gray);
        MB = getString(R.string.mb_text);
        GB = getString(R.string.gb_text);
    }

    private void initBean() {
        pgd = OtherUtils.showProgressPop(this);
        RX.getInstant().getUsageSetting(new ResponseObject<UsageSetting>() {
            @Override
            protected void onSuccess(UsageSetting result) {
                OtherUtils.hideProgressPop(pgd);
                DataPlanRxActivity.this.result = result;
                // 设置原有的月计划
                UsageHelper.Usage usageByte = UsageHelper.getUsageByte(DataPlanRxActivity.this, result.getMonthlyPlan());
                float aFloat = Float.valueOf(usageByte.usage);
                String usage = String.valueOf((int) aFloat);
                etDataplanRxLimit.setText(result.getMonthlyPlan() == 0 ? "0" : usage);
                // 设置单位
                tvDataplanRxLimitUnit.setText(result.getUnit() == Cons.MB ? MB : GB);
                // 设置自动断线
                boolean isAuto = result.getAutoDisconnFlag() == Cons.ENABLE_AUTODISCONNECT ? true : false;
                ivDataplanRxAuto.setImageDrawable(isAuto ? switch_on : switch_off);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                failed();
            }

            @Override
            public void onError(Throwable e) {
                failed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        clickBackButton();
    }

    /**
     * 点击了回退按钮
     */
    private void clickBackButton() {
        if (Cons.WIZARD_RX.equalsIgnoreCase(flag)) {
            to(WizardRxActivity.class);
        } else {
            new LogoutHelper(this) {
                @Override
                public void logoutFinish() {
                    to(LoginRxActivity.class);
                }
            };
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        OtherUtils.stopHeartBeat(heartTimer);
    }

    @OnClick({R.id.iv_dataplan_rx_back,// 回退
                     R.id.tv_setPlan_rx_skip,// 跳过
                     R.id.iv_dataplan_rx_limit_arrow,// 限制箭头
                     R.id.tv_dataplan_rx_limit_unit,// 单位
                     R.id.iv_dataplan_rx_auto,// 自动开关
                     R.id.bt_dataplan_rx_done})// done
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_dataplan_rx_back:
                clickBackButton();
                break;
            case R.id.tv_setPlan_rx_skip:// 跳过
                clickSkip();
                break;
            case R.id.iv_dataplan_rx_limit_arrow:
            case R.id.tv_dataplan_rx_limit_unit:
                choseUnit();
                break;
            case R.id.iv_dataplan_rx_auto:
                setAutoDisconnect();
                break;
            case R.id.bt_dataplan_rx_done:
                doneClick();
                break;
        }
    }


    /**
     * 点击跳过
     */
    private void clickSkip() {
        toAc();
    }

    /**
     * 点击了done按钮的操作
     */
    private void doneClick() {
        if (OtherUtils.isWifiConnect(this)) {

            // 是否掉线
            if (result == null) {
                toast(R.string.connect_failed);
                return;
            }
            // 空值判断
            if (TextUtils.isEmpty(OtherUtils.getEdContent(etDataplanRxLimit))) {
                toast(R.string.not_empty);
                return;
            }
            // 范围判断
            int limit = Integer.valueOf(OtherUtils.getEdContent(etDataplanRxLimit));
            if (limit < 0 | limit > 1024) {
                toast(R.string.input_a_data_value_between_0_1024);
                return;
            }
            // 提交请求
            pullRequest();
        } else {
            failed();
        }
    }

    private void failed() {
        toast(R.string.connect_failed);
        OtherUtils.hideProgressPop(pgd);
        to(RefreshWifiRxActivity.class);
        Logs.t("ma_unknown").vv("DataPlanRx--> failed");
    }

    /**
     * 提交请求
     */
    private void pullRequest() {
        boardSimHelper = new BoardSimHelper(this);
        boardSimHelper.setOnPinRequireListener(result -> toPinPukUi(new PinPukBean(Cons.PIN_FLAG)));
        boardSimHelper.setOnpukRequireListener(result -> toPinPukUi(new PinPukBean(Cons.PUK_FLAG)));
        boardSimHelper.setOnSimReadyListener(result -> usageRequest());// 发起提交请求
        boardSimHelper.boardNormal();
    }

    /**
     * 发起提交请求
     */
    private void usageRequest() {
        String monthly_s = OtherUtils.getEdContent(etDataplanRxLimit);
        int monthly_c = Integer.valueOf(TextUtils.isEmpty(monthly_s) ? "0" : monthly_s);
        long monthly_b = result.getUnit() == Cons.MB ? monthly_c * 1024l * 1024l : monthly_c * 1024l * 1024l * 1024l;
        result.setMonthlyPlan(monthly_b);
        RX.getInstant().setUsageSetting(result, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                toast(R.string.success);
                toAc();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                failed();
            }

            @Override
            public void onError(Throwable e) {
                failed();
            }
        });
    }

    private void toAc() {
        // 提交向导标记
        SP.getInstance(this).putBoolean(Cons.WIZARD_RX, true);
        SP.getInstance(this).putBoolean(Cons.DATAPLAN_RX, true);
        // 是否进入过wifi初始向导页
        if (SP.getInstance(this).getBoolean(Cons.WIFIINIT_RX, false)) {
            // --> 主页
            to(HomeRxActivity.class);
        } else {
            // 检测是否开启了WPS模式
            checkWps();
        }
    }

    /**
     * 检测是否开启了WPS模式
     */
    private void checkWps() {
        WpsHelper wpsHelper = new WpsHelper();
        wpsHelper.setOnWpsListener(attr -> to(attr ? HomeRxActivity.class : WifiInitRxActivity.class));
        wpsHelper.setOnErrorListener(attr -> to(HomeRxActivity.class));
        wpsHelper.setOnResultErrorListener(attr -> to(HomeRxActivity.class));
        wpsHelper.getWpsStatus();
    }

    /**
     * 前往PIN|PUK界面
     *
     * @param pinPukBean
     */
    private void toPinPukUi(PinPukBean pinPukBean) {
        EventBus.getDefault().postSticky(pinPukBean);
        to(PinPukIndexRxActivity.class);
    }

    /**
     * 设置自动断线
     */
    private void setAutoDisconnect() {
        if (result != null) {
            int disable_auto = Cons.DISABLE_NOTAUTODISCONNECT;
            int enable_auto = Cons.ENABLE_AUTODISCONNECT;
            result.setAutoDisconnFlag(result.getAutoDisconnFlag() == enable_auto ? disable_auto : enable_auto);
            ivDataplanRxAuto.setImageDrawable(result.getAutoDisconnFlag() == enable_auto ? switch_on : switch_off);
        }
    }

    /**
     * 显示单位弹窗
     */
    private void choseUnit() {
        Logger.t("ma_datarx").v("unit:" + result.getUnit());
        ScreenSize.SizeBean size = ScreenSize.getSize(this);
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.18f);
        View inflate = View.inflate(this, R.layout.pop_dataplan_rx, null);
        TextView mb = (TextView) inflate.findViewById(R.id.tv_dataplan_rx_pop_mb);
        TextView gb = (TextView) inflate.findViewById(R.id.tv_dataplan_rx_pop_gb);
        // 初始化颜色
        boolean isMb = result.getUnit() == Cons.MB;
        mb.setTextColor(isMb ? check_color : uncheck_color);
        gb.setTextColor(isMb ? uncheck_color : check_color);
        mb.setOnClickListener(v -> setUnit(Cons.MB));
        gb.setOnClickListener(v -> setUnit(Cons.GB));
        pop = new PopupWindows(this, inflate, width, height, new ColorDrawable(Color.TRANSPARENT));
    }

    private void setUnit(int unit) {
        if (result != null) {
            result.setUnit(unit);
            tvDataplanRxLimitUnit.setText(unit == Cons.MB ? MB : GB);
        }
        pop.dismiss();
    }

    public void toast(int resId) {
        ToastUtil_m.show(this, resId);
    }

    private void to(Class clazz) {
        CA.toActivity(this, clazz, false, true, false, 0);
    }
}
