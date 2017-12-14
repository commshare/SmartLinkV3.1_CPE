package com.alcatel.wifilink.rx.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.rx.helper.base.PinStatuHelper;
import com.alcatel.wifilink.rx.helper.business.ChangePinHelper;
import com.alcatel.wifilink.rx.helper.business.ConnModeHelper;
import com.alcatel.wifilink.rx.helper.base.ConnectSettingHelper;
import com.alcatel.wifilink.rx.helper.base.ConnectStatusHelper;
import com.alcatel.wifilink.rx.helper.business.DataRoamHelper;
import com.alcatel.wifilink.rx.helper.business.MobileDataHelper;
import com.alcatel.wifilink.rx.helper.business.ModeHelper;
import com.alcatel.wifilink.rx.helper.base.NetworkSettingHelper;
import com.alcatel.wifilink.rx.helper.business.SimNumImsiHelper;
import com.alcatel.wifilink.rx.helper.business.SimPinHelper;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ScreenSize;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by qianli.ma on 2017/12/8 0008.
 */

public class MobileNetworkRxFragment extends Fragment implements FragmentBackHandler {

    private int PIN = 0;
    private int PUK = 1;

    @BindView(R.id.iv_mobilenetwork_back)
    ImageView ivBack;// 返回
    @BindView(R.id.iv_mobilenetwork_mobileData)
    ImageView ivMobileData;// mobile data switch
    @BindView(R.id.iv_mobilenetwork_connMode_arrow)
    ImageView ivConnModeArrow;// connection mode arrow
    @BindView(R.id.tv_mobilenetwork_connMode_mode)
    TextView tvConnModeMode;// connection mode text
    @BindView(R.id.iv_mobilenetwork_dataRoaming)
    ImageView ivDataRoaming;// data roaming switch
    @BindView(R.id.rl_mobilenetwork_setDataPlan)
    RelativeLayout rlSetDataPlan;// set data plan
    @BindView(R.id.iv_mobilenetwork_mode_arrow)
    ImageView ivModeArrow;// mode arrow
    @BindView(R.id.tv_mobilenetwork_mode_mode)
    TextView tvModeMode;// mode text
    @BindView(R.id.rl_mobilenetwork_profile)
    RelativeLayout rlProfile;// profile
    @BindView(R.id.iv_mobilenetwork_simPin)
    ImageView ivSimPin;// sim pin switch
    @BindView(R.id.rl_mobilenetwork_changeSimPin)
    RelativeLayout rlChangeSimPin;// change sim pin
    @BindView(R.id.tv_mobilenetwork_simNum)
    TextView tvSimNum;// sim num
    @BindView(R.id.tv_mobilenetwork_imsi)
    TextView tvImsi;// imsi

    Unbinder unbinder;
    private View inflate;
    private String text_auto;
    private String text_manual;
    private String text_4G;
    private String text_3G;
    private String text_2G;
    private String[] modes;
    private Drawable switch_on;
    private Drawable switch_off;
    private Handler handler;
    private HomeRxActivity activity;
    private ConnectStatusHelper connHelper;
    private ConnectSettingHelper connSettingHelper;
    private NetworkSettingHelper networkSettingHelper;
    private TimerHelper timerHelper;
    private SimNumImsiHelper simNumImsiHelper;
    private ConnectSettingHelper connSettingInitHelper;
    private PopupWindows pop_profile;
    private PopupWindows pop_connMode;
    private PopupWindows pop_simpin;
    private PopupWindows pop_changpin;
    private PopupWindows popMode;
    private Drawable pop_bg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initRes();// 初始化资源
        resetUi();
        inflate = View.inflate(getActivity(), R.layout.fragment_mobilenetwork, null);
        unbinder = ButterKnife.bind(this, inflate);
        initTimer();// 初始化定时器
        return inflate;
    }

    private void initRes() {
        handler = new Handler();
        activity = (HomeRxActivity) getActivity();
        text_auto = getActivity().getString(R.string.setting_network_mode_auto);
        text_manual = getActivity().getString(R.string.maunal);
        text_4G = getActivity().getString(R.string.home_network_type_4g);
        text_3G = getActivity().getString(R.string.home_network_type_3g);
        text_2G = getActivity().getString(R.string.home_network_type_2g);
        modes = new String[]{text_auto, text_4G, text_3G, text_2G};
        switch_on = getActivity().getResources().getDrawable(R.drawable.switch_on);
        switch_off = getActivity().getResources().getDrawable(R.drawable.switch_off);
        pop_bg = getResources().getDrawable(R.drawable.bg_pop_conner);
    }

    private void resetUi() {
        activity.tabFlag = Cons.TAB_MOBILE_NETWORK;
        activity.llNavigation.setVisibility(View.GONE);
        activity.rlBanner.setVisibility(View.GONE);
    }

    private void initTimer() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                getMobileData();// 获取网络连接状态
                getConnMode();// 获取连接模式(auto | manual)
                getDataRoaming();// 获取漫游状态
                getMode();// 获取连接信号类型(4G|3G|2G)
                getSimPin();// 获取SIM是否设置PIN码
                getSimAndImsi();// 获取SIM NUM以及IMSI
            }
        };
        timerHelper.start(2500);
        OtherUtils.timerList.add(timerHelper);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initTimer();
            resetUi();
        } else {
            stopTimer();
        }
    }

    /**
     * 获取SIM NUM以及IMSI
     */
    private void getSimAndImsi() {
        if (simNumImsiHelper == null) {
            simNumImsiHelper = new SimNumImsiHelper();
            simNumImsiHelper.setOnErrorListener(attr -> simNumImsiEmpty());
            simNumImsiHelper.setOnResultErrorListener(attr -> simNumImsiEmpty());
            simNumImsiHelper.setOnSimNumberListener(simNum -> tvSimNum.setText(simNum));
            simNumImsiHelper.setOnImsiListener(imsi -> tvImsi.setText(imsi));
        }
        simNumImsiHelper.getSimNumAndImsi();
    }

    /**
     * 获取SIM是否设置PIN码
     */
    private void getSimPin() {
        PinStatuHelper pinStatuHelper = new PinStatuHelper(getActivity());
        pinStatuHelper.setOnNormalPinStatesListener(result -> {
            boolean isPinEnable = result.getPinState() == Cons.PINSTATES_PIN_ENABLE_VERIFIED;
            ivSimPin.setImageDrawable(isPinEnable ? switch_on : switch_off);
        });
        pinStatuHelper.getRoll();
    }

    /**
     * 获取连接信号类型(4G|3G|2G)
     */
    private void getMode() {
        if (networkSettingHelper == null) {
            networkSettingHelper = new NetworkSettingHelper();
            networkSettingHelper.setOnAutoListener(attr -> tvModeMode.setText(text_auto));
            networkSettingHelper.setOn2GListener(attr -> tvModeMode.setText(text_2G));
            networkSettingHelper.setOn3GListener(attr -> tvModeMode.setText(text_3G));
            networkSettingHelper.setOn4GListener(attr -> tvModeMode.setText(text_4G));
        }
        networkSettingHelper.getNetworkSetting();
    }

    /**
     * 获取漫游状态
     */
    private void getDataRoaming() {
        if (connSettingInitHelper == null) {
            connSettingInitHelper = new ConnectSettingHelper();
            connSettingInitHelper.setOnRoamConnListener(attr -> ivDataRoaming.setImageDrawable(switch_on));
            connSettingInitHelper.setOnRoamNotConnListener(attr -> ivDataRoaming.setImageDrawable(switch_off));
        }
        connSettingInitHelper.getConnWhenRoam();
    }

    /**
     * 获取网络连接状态
     */
    private void getMobileData() {
        if (connHelper == null) {
            connHelper = new ConnectStatusHelper();
            connHelper.setOnDisConnecting(result -> ivMobileData.setImageDrawable(switch_off));
            connHelper.setOnDisConnected(result -> ivMobileData.setImageDrawable(switch_off));
            connHelper.setOnConnected(result -> ivMobileData.setImageDrawable(switch_on));
        }
        connHelper.getStatus();
    }

    /**
     * 获取连接模式(auto | manual)
     */
    private void getConnMode() {
        if (connSettingHelper == null) {
            connSettingHelper = new ConnectSettingHelper();
            connSettingHelper.setOnConnAutoListener(attr -> tvConnModeMode.setText(text_auto));
            connSettingHelper.setOnConnManualListener(attr -> tvConnModeMode.setText(text_manual));
        }
        connSettingHelper.getConnSettingStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        stopTimer();
    }

    @OnClick({R.id.iv_mobilenetwork_back,// 返回
                     R.id.iv_mobilenetwork_mobileData,// mobile data switch
                     R.id.iv_mobilenetwork_connMode_arrow,// conn mode arrow
                     R.id.tv_mobilenetwork_connMode_mode,// conn mode text
                     R.id.iv_mobilenetwork_dataRoaming,// data roaming switch
                     R.id.rl_mobilenetwork_setDataPlan,// set data plan
                     R.id.iv_mobilenetwork_mode_arrow,// mode arrow
                     R.id.tv_mobilenetwork_mode_mode,// mode text
                     R.id.rl_mobilenetwork_profile,// profile
                     R.id.iv_mobilenetwork_simPin,// sim pin switch
                     R.id.rl_mobilenetwork_changeSimPin// change sim pin
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_mobilenetwork_back:// 回退
                activity.fraHelpers.transfer(activity.clazz[3]);
                break;
            case R.id.iv_mobilenetwork_mobileData:// mobile data
                clickMobileData();
                break;
            case R.id.iv_mobilenetwork_connMode_arrow:// connection mode
            case R.id.tv_mobilenetwork_connMode_mode:// connection mode
                clickConnectionMode();
                break;
            case R.id.iv_mobilenetwork_dataRoaming:// 漫游开关
                clickRoaming();
                break;
            case R.id.rl_mobilenetwork_setDataPlan:// 设置流量计划
                activity.fraHelpers.transfer(activity.clazz[Cons.TAB_SET_DATA_PLAN]);
                break;
            case R.id.iv_mobilenetwork_mode_arrow:// 模式切换4G|3G|2G
            case R.id.tv_mobilenetwork_mode_mode:// 模式切换4G|3G|2G
                clickMode();
                break;
            case R.id.rl_mobilenetwork_profile:// profile提示
                clickProfile();
                break;
            case R.id.iv_mobilenetwork_simPin:// 开启PIN码 | 关闭PIN码
                clickSimPin();
                break;
            case R.id.rl_mobilenetwork_changeSimPin:// 修改PIN码
                clickChangePin();
                break;
        }
    }

    /**
     * 点击了mobile data操作
     */
    private void clickMobileData() {
        MobileDataHelper mobileDataHelper = new MobileDataHelper(getActivity());
        mobileDataHelper.setOnConnSuccessListener(attr -> ivMobileData.setImageDrawable(switch_on));
        mobileDataHelper.setOnDisConnSuccessListener(attr -> ivMobileData.setImageDrawable(switch_off));
        mobileDataHelper.toConnOrNot();
    }

    /**
     * 点击connectionMode
     */
    private void clickConnectionMode() {
        View inflate = View.inflate(getActivity(), R.layout.pop_connmode, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.18f);
        View auto = inflate.findViewById(R.id.tv_pop_connMode_auto);
        View manual = inflate.findViewById(R.id.tv_pop_connMode_manual);
        auto.setOnClickListener(v -> setConnMode(Cons.AUTO_MODE));
        manual.setOnClickListener(v -> setConnMode(Cons.MANUAL));
        pop_connMode = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    private void setConnMode(int connMode) {
        pop_connMode.dismiss();
        ConnModeHelper connModeHelper = new ConnModeHelper(getActivity());
        connModeHelper.setOnConnModeSuccessListener(result -> {
            tvConnModeMode.setText(result.getConnectMode() == Cons.AUTO_MODE ? text_auto : text_manual);
        });
        connModeHelper.transferConnMode(connMode);
    }

    /**
     * 点击了data roaming
     */
    private void clickRoaming() {
        DataRoamHelper dataRoamHelper = new DataRoamHelper(getActivity());
        dataRoamHelper.setOnRoamConnSuccessListener(result -> {
            ivDataRoaming.setImageDrawable(result.getRoamingConnect() == Cons.WHEN_ROAM_CAN_CONNECT ? switch_on : switch_off);
        });
        dataRoamHelper.transfer();
    }

    /**
     * 点击了mode
     */
    private void clickMode() {
        View inflate = View.inflate(getActivity(), R.layout.pop_mode, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.36f);
        View auto = inflate.findViewById(R.id.tv_pop_mode_auto);
        View only_4g = inflate.findViewById(R.id.tv_pop_connMode_4G);
        View only_3g = inflate.findViewById(R.id.tv_pop_connMode_3G);
        View only_2g = inflate.findViewById(R.id.tv_pop_connMode_2G);
        auto.setOnClickListener(v -> changeMode(Cons.AUTO_MODE));
        only_4g.setOnClickListener(v -> changeMode(Cons.ONLY_LTE));
        only_3g.setOnClickListener(v -> changeMode(Cons.ONLY_3G));
        only_2g.setOnClickListener(v -> changeMode(Cons.ONLY_2G));
        popMode = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    private void changeMode(int mode) {
        popMode.dismiss();
        ModeHelper modeHelper = new ModeHelper(getActivity());
        modeHelper.setOnModeSuccessListener(attr -> {
            tvModeMode.setText(modes[attr.getNetworkMode()]);
        });
        modeHelper.transfer(mode);
    }

    /**
     * 点击了profile
     */
    private void clickProfile() {
        View inflate = View.inflate(getActivity(), R.layout.pop_profile, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.22f);
        View profileUrl = inflate.findViewById(R.id.tv_pop_profile_des);
        View profileCancel = inflate.findViewById(R.id.tv_pop_profile_cancel);
        profileCancel.setOnClickListener(v -> pop_profile.dismiss());
        pop_profile = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    /**
     * 点击了sim pin
     */
    private void clickSimPin() {
        View inflate = View.inflate(getActivity(), R.layout.pop_simpin, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.27f);
        EditText etPin = (EditText) inflate.findViewById(R.id.et_pop_simpin);
        etPin.setText("");
        View vCancel = inflate.findViewById(R.id.tv_pop_simpin_cancel);
        View vOk = inflate.findViewById(R.id.tv_pop_simpin_ok);
        vCancel.setOnClickListener(v -> pop_simpin.dismiss());
        vOk.setOnClickListener(v -> {
            pop_simpin.dismiss();
            // 提交修改
            SimPinHelper simPinHelper = new SimPinHelper(getActivity());
            simPinHelper.setOnPukLockedListener(attr -> toPukRx());
            simPinHelper.setOnPinEnableListener(attr -> {// 开启成功
                toast(R.string.success);
                ivSimPin.setImageDrawable(switch_on);
            });
            simPinHelper.setOnPinDisableListener(attr -> {// 关闭成功
                toast(R.string.success);
                ivSimPin.setImageDrawable(switch_off);
            });
            simPinHelper.setOnPinTimeoutListener(attr -> toPukRx());// PIN次数超过限制
            simPinHelper.transfer(OtherUtils.getEdContent(etPin));
        });
        pop_simpin = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
    }

    /**
     * 点击change pin
     */
    private void clickChangePin() {
        PinStatuHelper pinStatuHelper = new PinStatuHelper(getActivity());
        pinStatuHelper.setOnNormalPinStatesListener(attr1 -> {
            if (attr1.getPinState() != Cons.PINSTATES_PIN_ENABLE_VERIFIED) {
                toast(R.string.please_enable_sim_pin);
            } else {
                View inflate = View.inflate(getActivity(), R.layout.pop_changpin, null);
                ScreenSize.SizeBean size = ScreenSize.getSize(getActivity());
                int width = (int) (size.width * 0.85f);
                int height = (int) (size.height * 0.41f);
                String currentPin = OtherUtils.getEdContent((EditText) inflate.findViewById(R.id.et_pop_changpin_currentPin));
                String newPin = OtherUtils.getEdContent((EditText) inflate.findViewById(R.id.et_pop_changpin_newPin));
                String confirmPin = OtherUtils.getEdContent((EditText) inflate.findViewById(R.id.et_pop_changpin_confirmPin));
                View vCancel = inflate.findViewById(R.id.tv_pop_changpin_cancel);
                View vOk = inflate.findViewById(R.id.tv_pop_changpin_ok);
                vCancel.setOnClickListener(v -> pop_changpin.dismiss());
                vOk.setOnClickListener(v -> {
                    pop_changpin.dismiss();
                    ChangePinHelper changePinHelper = new ChangePinHelper(getActivity());
                    changePinHelper.setOnPinTimeoutListener(attr -> toPukRx());
                    changePinHelper.setOnChangePinSuccessListener(attr -> pop_changpin.dismiss());
                    changePinHelper.change(currentPin, newPin, confirmPin);
                });
                pop_changpin = new PopupWindows(getActivity(), inflate, width, height, true, pop_bg);
            }
        });
        pinStatuHelper.getRoll();
    }

    /**
     * 前往PUK界面
     */
    private void toPukRx() {
        SP.getInstance(getActivity()).putInt(Cons.TAB_FRA, Cons.TAB_MOBILE_NETWORK);
        activity.fraHelpers.transfer(activity.clazz[Cons.TAB_PUK]);
    }

    /**
     * 设置sim num & imsi为空
     */
    private void simNumImsiEmpty() {
        if (tvSimNum != null) {
            tvSimNum.setText("");
        }
        if (tvImsi != null) {
            tvImsi.setText("");
        }
    }

    private void stopTimer() {
        if (timerHelper != null) {
            timerHelper.stop();
            timerHelper = null;
        }
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

    @Override
    public boolean onBackPressed() {
        activity.fraHelpers.transfer(activity.clazz[Cons.TAB_SETTING]);
        return true;
    }
}
