package com.alcatel.wifilink.rx.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.helper.base.UsageSettingHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.rx.helper.base.BoardWanHelper;
import com.alcatel.wifilink.rx.helper.base.ConnectSettingHelper;
import com.alcatel.wifilink.rx.helper.base.ConnectStatusHelper;
import com.alcatel.wifilink.rx.helper.base.NetworkInfoHelper;
import com.alcatel.wifilink.rx.helper.base.UsageHelper;
import com.alcatel.wifilink.ui.activity.InternetStatusActivity;
import com.alcatel.wifilink.ui.devicec.allsetup.ActivityDeviceManager;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.view.DynamicWave;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.WaveHelper;
import com.gelitenight.waveview.library.WaveView;
import com.orhanobut.logger.Logger;
import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by qianli.ma on 2017/11/21 0021.
 */

public class mainRxFragment extends Fragment {


    @BindView(R.id.bt_mainrx_wanConnect)
    Button btWanConnect;// WAN口连接
    @BindView(R.id.bt_mainrx_simUnConnected)
    Button btSimUnConnected;// SIM卡未连接
    @BindView(R.id.rl_mainrx_simConnected)
    RelativeLayout rlSimConnected;// SIM卡已连接(面板)
    @BindView(R.id.bt_mainrx_simConnected)
    WaveView btSimConnected;// SIM卡已连接(波浪视图)
    @BindView(R.id.bt_mainrx_simLocked)
    Button btSimLocked;// SIM卡被锁定状态
    @BindView(R.id.bt_mainrx_simNown)
    Button btSimNown;// SIM卡被锁定状态

    @BindView(R.id.tv_mainrx_usedUnit)
    TextView tvUsedUnit;// 已使用流量单位
    @BindView(R.id.tv_mainrx_usedData)
    TextView tvUsedData;// 已使用流量
    @BindView(R.id.tv_mainrx_usedTotal)
    TextView tvUsedTotal;// 总流量

    @BindView(R.id.tv_mainrx_networkType)
    TextView tvNetworkType;// 运营商

    @BindView(R.id.dw_mainrx)
    DynamicWave dynamicWave;// 中间动态波浪(无实际意义)

    @BindView(R.id.rl_mainrx_signal_panel)
    PercentRelativeLayout rlSignalPanel;// 信号面板
    @BindView(R.id.iv_mainrx_signal)
    ImageView ivSignal;// 信号图标
    @BindView(R.id.tv_mainrx_signal)
    TextView tvSignal;// 信号类型文本

    @BindView(R.id.rl_mainrx_connectedPeople_panel)
    PercentRelativeLayout rlConnectedPeoplePanel;// 连接面板
    @BindView(R.id.iv_mainrx_connectedPeople)
    ImageView ivConnectedPeople;// 连接人数图标
    @BindView(R.id.tv_mainrx_connectedPeople)
    TextView tvConnectedPeople;// 连接人数文本

    Unbinder unbinder;

    private View inflate;
    private String BLANK_TEXT = " ";
    private String NONE_TEXT = "- -";
    private String noUsagePlan;
    private String useOf;
    private int WAN_CONNECT_MODE = 0;// wan口连接模式
    private int SIM_DISCONNECT_MODE = 1;// SIM卡未连接模式(已解锁)
    private int SIM_CONNECT_MODE = 2;// SIM卡连接模式
    private int SIM_LOCKED = 3;// SIM被锁定状态
    private int SIM_NOWN = 4;// SIM被拔出
    private WaveHelper waveButton;// 波浪辅助类
    private String circlrDotColor = "#3798f4";// 圆环颜色
    private String behindColor_nor = "#AA39e99d";// 圆环波浪底色(正常)
    private String frontColor_nor = "#39e99d";// 圆环波浪前景色(正常)
    private String behindColor_over = "#AAf5ac1f";// 圆环波浪底色(超标)
    private String frontColor_over = "#f5ac1f";// 圆环波浪前景色(超标)
    private String behindColor_dynamic = "#AAFFFFFF";// 中间波浪底色
    private String frontColor_dynamic = "#FFFFFF";// 中间波浪前景色
    private String signal_no_text = "--";// 没有信号文本
    private String mb_unit;// 单位MB
    private String gb_unit;// 单位GB
    private Drawable[] signals;// 信号集合
    private Drawable signal0;// 信号0
    private Drawable signal1;// 信号1
    private Drawable signal2;// 信号2
    private Drawable signal3;// 信号3
    private Drawable signal4;// 信号4
    private Drawable signal5;// 信号5
    private Drawable signalR;// 信号漫游
    private String signal_2G;// 2G信号
    private String signal_3G;// 3G信号
    private String signal_3G_plus;// 超3G信号
    private String signal_4G;// 4G信号
    private String signal_text;// SIGNAL文本
    private Drawable connected_none;// 没有连接数
    private Drawable connected_more;// 有连接数
    private String connected_text;// 连接文本
    private View[] buttonsView;
    private TimerHelper timer;
    private int blue_color;
    private int gray_color;
    private BoardWanHelper wanHelper;
    private BoardWanHelper wanHelperClick;
    private BoardSimHelper simHelper;
    private BoardSimHelper simHelper_simNotConnect;
    private BoardSimHelper simHelper_simConnect;
    private BoardSimHelper simHelper_simLocked;
    private NetworkInfoHelper networkHelper;
    private ConnectStatusHelper connStatuHelper;
    private HomeRxActivity activity;
    private Class[] fragmentClazz;// fragment集合(在HomeRxActivity)
    private String unlockSim_title;
    private String unlockSim_content;
    private String cancel_text;
    private String confirm_text;
    private SweetAlertDialog dialog;
    private int usageLimit;// 默认的流量限度(用于显示波浪颜色)
    private ProgressDialog pgdWait;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = one();
        return inflate;
    }

    private View one() {
        inflate = View.inflate(getActivity(), R.layout.fragment_mainrx, null);
        unbinder = ButterKnife.bind(this, inflate);
        activity = (HomeRxActivity) getActivity();
        fragmentClazz = activity.clazz;
        SP.getInstance(getActivity()).putInt(Cons.TAB_FRA, Cons.TAB_MAIN);
        showPgd();
        initRes();
        resetUi();
        initView();
        initHelper();
        startTimer();
        return inflate;

    }

    /**
     * 全局等待
     */
    private void showPgd() {
        if (pgdWait == null) {
            pgdWait = OtherUtils.showProgressPop(getActivity());
        } else {
            pgdWait.show();
        }
    }

    private void initHelper() {
        wanHelper = new BoardWanHelper(getActivity());
        wanHelperClick = new BoardWanHelper(getActivity());
        simHelper = new BoardSimHelper(getActivity());
        simHelper_simNotConnect = new BoardSimHelper(getActivity());
        simHelper_simConnect = new BoardSimHelper(getActivity());
        simHelper_simLocked = new BoardSimHelper(getActivity());
        connStatuHelper = new ConnectStatusHelper();

        // 定时获取WAN口状态
        wanHelper.setOnResultError(error -> getSim());// 出错
        wanHelper.setOnError(e -> getSim());// 出错
        wanHelper.setOnConnetedNextListener(wanResult -> wanFirst());// 显示wan
        wanHelper.setOnConnetingNextListener(wanResult -> getSim());// 获取SIM
        wanHelper.setOnDisConnetedNextListener(wanResult -> getSim());// 获取sim
        wanHelper.setOnDisconnetingNextListener(wanResult -> getSim());// 获取sim

        // 定时获取sim状态
        simHelper.setOnNownListener(simStatus -> simNotReady());
        simHelper.setOnSimReadyListener(result -> simReady());
        simHelper.setOnSimLockListener(simStatus -> pinPukSimLock());
        simHelper.setOnPinRequireListener(result -> pinPukSimLock());
        simHelper.setOnpukRequireListener(result -> pinPukSimLock());
        simHelper.setOnpukTimeoutListener(result -> simNotReady());
        simHelper.setOnInitingListener(simStatus -> simNotReady());
        simHelper.setOnDetectedListener(simStatus -> simNotReady());
        simHelper.setOnNownListener(simStatus -> simNown());
        simHelper.setOnRollRequestOnError(e -> simNotReady());
        simHelper.setOnRollRequestOnResultError(error -> simNotReady());

        // 定时获取连接状态
        connStatuHelper.setOnConnected(result -> buttonUi(SIM_CONNECT_MODE));
        connStatuHelper.setOnConnecting(result -> buttonUi(SIM_DISCONNECT_MODE));
        connStatuHelper.setOnDisConnected(result -> buttonUi(SIM_DISCONNECT_MODE));
        connStatuHelper.setOnDisConnecting(result -> buttonUi(SIM_DISCONNECT_MODE));
        connStatuHelper.setOnResultError(error -> buttonUi(SIM_DISCONNECT_MODE));
        connStatuHelper.setOnError(error -> buttonUi(SIM_DISCONNECT_MODE));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            stopTimer();
            dialogDismiss();
        } else {
            SP.getInstance(getActivity()).putInt(Cons.TAB_FRA, Cons.MAIN);
            startTimer();
            resetUi();
        }
    }

    private void resetUi() {
        if (activity == null) {
            activity = (HomeRxActivity)getActivity();
        }
        activity.tabFlag = Cons.TAB_MAIN;
        activity.llNavigation.setVisibility(View.VISIBLE);
        activity.rlBanner.setVisibility(View.GONE);
    }


    private void initRes() {
        mb_unit = getString(R.string.mb_text);
        gb_unit = getString(R.string.gb_text);
        noUsagePlan = getString(R.string.no_month_plan);
        useOf = getString(R.string.used_of);
        signal_2G = getString(R.string.home_network_type_2g);
        signal_3G = getString(R.string.home_network_type_3g);
        signal_3G_plus = getString(R.string.home_network_type_3g_plus);
        signal_4G = getString(R.string.home_network_type_4g);
        signal_text = getString(R.string.signal);
        connected_text = getString(R.string.access_lable);
        unlockSim_title = getString(R.string.sim_unlocked);
        unlockSim_content = getString(R.string.home_pin_locked_notice);
        cancel_text = getString(R.string.cancel);
        confirm_text = getString(R.string.confirm_unit);
        blue_color = getResources().getColor(R.color.mg_blue);
        gray_color = getResources().getColor(R.color.gray);
        signal0 = getResources().getDrawable(R.drawable.home_4g_none);
        signal1 = getResources().getDrawable(R.drawable.home_4g1);
        signal2 = getResources().getDrawable(R.drawable.home_4g2);
        signal3 = getResources().getDrawable(R.drawable.home_4g3);
        signal4 = getResources().getDrawable(R.drawable.home_4g4);
        signal5 = getResources().getDrawable(R.drawable.home_4g5);
        signalR = getResources().getDrawable(R.drawable.home_4g_r);
        connected_none = getResources().getDrawable(R.drawable.device_none);
        connected_more = getResources().getDrawable(R.drawable.device_more);
        signals = new Drawable[]{signal0, signal1, signal2, signal3, signal4, signal5, signalR};
        buttonsView = new View[]{btWanConnect, btSimUnConnected, rlSimConnected, btSimLocked, btSimNown};
    }

    private void initView() {
        // 按钮波浪
        waveButton = new WaveHelper(btSimConnected);
        waveButton.setBorderAndShape(0, 0, circlrDotColor);
        waveButton.setWaveColor(behindColor_nor, frontColor_nor);
        waveButton.setShiftHorizotolAnim(60, null);
        waveButton.setAmplitude(50);
        waveButton.setLevel(10);// 默认比率为10
        waveButton.startAnim();
    }

    /**
     * 启动定时器
     */
    private void startTimer() {
        timer = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                getWan();  /* 1.先走wan口 */
            }
        };
        timer.start(2500);
        OtherUtils.timerList.add(timer);
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }


    /**
     * 先获取WAN口状态
     */
    private void getWan() {
        Logger.v("ma_sim: " + "getwan()");
         /* 2.wan口无效后再走sim卡 */
        wanHelper.boardTimer();
    }

    /**
     * 显示wan口模式视图
     */
    private void wanFirst() {
        // 0.隐藏等待
        OtherUtils.hideProgressPop(pgdWait);
        // 1.显示UI
        buttonUi(WAN_CONNECT_MODE);// 显示wan口按钮
        tvNetworkType.setVisibility(View.VISIBLE);
        tvNetworkType.setText(getString(R.string.Ethernet));// 显示网络类型文本
        rlSignalPanel.setVisibility(View.GONE);// 信号面板消隐
        // 2.获取连接设备数
        getDevice();
    }

    /**
     * 获取sim卡状态
     */
    private void getSim() {
        Logger.v("ma_sim: " + "sim going");
        simHelper.boardTimer();
    }

    /**
     * SIM卡没有插入时显示
     */
    private void simNown() {
        // 0.隐藏等待
        OtherUtils.hideProgressPop(pgdWait);
        // 1.显示UI
        buttonUi(SIM_NOWN);// SIM卡未插入
        // 2.获取注册状态(在SIM卡状态未达到CONNECTED之前切勿使用GetNetworkInfo这个接口)
        getNetworkRegister();
        // 3.获取设备数
        getDevice();
    }

    /**
     * sim卡被锁定
     */
    private void pinPukSimLock() {
        // 0.隐藏等待
        OtherUtils.hideProgressPop(pgdWait);
        // 1.显示UI
        buttonUi(SIM_LOCKED);// SIM卡锁定状态
        // 2.获取注册状态(在SIM卡状态未达到CONNECTED之前切勿使用GetNetworkInfo这个接口)
        getNetworkRegister();
        // 3.获取设备数
        getDevice();
    }

    /**
     * SIM卡已准备
     */
    private void simReady() {
        // 0.隐藏等待
        OtherUtils.hideProgressPop(pgdWait);
        connStatuHelper.getStatus();
        // 1.获取注册状态
        getNetworkRegister();
        // 2.获取设备状态
        getDevice();
        // 3. 获取流量使用情况
        getUsage();
    }

    /**
     * SIM卡未连接(但已经解锁)
     */
    private void simNotReady() {
        // 0.隐藏等待
        OtherUtils.hideProgressPop(pgdWait);
        // 1.显示UI
        buttonUi(SIM_DISCONNECT_MODE);// SIM卡未连接状态
        // 2.获取注册状态(在SIM卡状态未达到CONNECTED之前切勿使用GetNetworkInfo这个接口)
        getNetworkRegister();
        // 3.获取设备数
        getDevice();
    }

    int temp = 0;

    /**
     * 获取流量
     */
    private void getUsage() {
        RX.getInstant().getUsageSetting(new ResponseObject<UsageSetting>() {
            @Override
            protected void onSuccess(UsageSetting result) {
                if (temp == 0) {
                    Logger.t("ma_usage").v("after usage: " + result.getMonthlyPlan());
                    temp++;
                }

                // 设置已使用流量 
                UsageHelper.Usage usedUsage = UsageHelper.getUsageByte(getActivity(), result.getUsedData());
                tvUsedUnit.setVisibility(View.VISIBLE);
                tvUsedData.setVisibility(View.VISIBLE);
                tvUsedUnit.setText(usedUsage.unit);
                tvUsedData.setText(usedUsage.usage);
                // 设置月计划流量
                UsageHelper.Usage planUsage = UsageHelper.getUsageByte(getActivity(), result.getMonthlyPlan());
                String planUnit = result.getUnit() == Cons.MB ? mb_unit : gb_unit;
                long plan = result.getMonthlyPlan();
                // used of 0.88GB
                tvUsedTotal.setText(plan <= 0 ? noUsagePlan : useOf + BLANK_TEXT + planUsage.usage + planUnit);
                // 计算已使用流量比率
                usageLimit = SP.getInstance(getActivity()).getInt(Cons.USAGE_LIMIT, 90);
                int rate = UsageHelper.getRateUsed(result.getUsedData(), result.getMonthlyPlan());
                if (rate > usageLimit) {
                    waveButton.setWaveColor(behindColor_over, frontColor_over);
                } else {
                    waveButton.setWaveColor(behindColor_nor, frontColor_nor);
                }
                waveButton.setLevel(rate);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                usageError();
            }

            @Override
            public void onError(Throwable e) {
                usageError();
            }

            private void usageError() {
                if (tvUsedUnit != null) {
                    tvUsedUnit.setText(mb_unit);
                }
                if (tvUsedData != null) {
                    tvUsedData.setText("-");
                }
                if (tvUsedTotal != null) {
                    tvUsedTotal.setText(getString(R.string.used_of) + " -");
                }
            }
        });
    }


    /**
     * 获取注册状态
     */
    private void getNetworkRegister() {
        if (networkHelper == null) {
            networkHelper = new NetworkInfoHelper(getActivity()) {
                @Override
                public void noRegister() {
                    if (rlSignalPanel != null) {
                        rlSignalPanel.setVisibility(View.VISIBLE);
                    }
                    if (ivSignal != null) {
                        ivSignal.setImageDrawable(signal0);
                    }
                    if (tvNetworkType != null) {
                        tvNetworkType.setVisibility(View.GONE);
                    }
                    if (tvSignal != null) {
                        tvSignal.setText(NONE_TEXT);
                        tvSignal.setTextColor(gray_color);
                    }
                }

                @Override
                public void register(NetworkInfos result) {
                    rlSignalPanel.setVisibility(View.VISIBLE);
                    // 设置漫游+信号强度
                    boolean isRoam = result.getRoaming() == Cons.ROAMING ? true : false;
                    int signalStrength = result.getSignalStrength();
                    boolean isNoService = signalStrength == Cons.NOSERVICE;
                    ivSignal.setImageDrawable(isRoam ? signalR : isNoService ? signal0 : signals[signalStrength]);
                    // 设置网络类型
                    tvNetworkType.setVisibility(View.VISIBLE);
                    tvNetworkType.setText(result.getNetworkName());// CMCC\UNICOM\..
                    tvSignal.setText(getSignalType(getActivity(), result.getNetworkType()));// 2G\3G..
                    tvSignal.setTextColor(blue_color);
                }
            };
        }
        networkHelper.get();
    }

    /**
     * 获取连接设备数
     */
    private void getDevice() {
        RX.getInstant().getConnectedDeviceList(new ResponseObject<ConnectedList>() {
            @Override
            protected void onSuccess(ConnectedList result) {
                int deviceSize = result.getConnectedList().size();
                ivConnectedPeople.setImageDrawable(deviceSize > 0 ? connected_more : connected_none);
                tvConnectedPeople.setText(deviceSize > 0 ? deviceSize + BLANK_TEXT + connected_text : connected_text);
                tvConnectedPeople.setTextColor(deviceSize > 0 ? blue_color : gray_color);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                noneConnected();
            }

            @Override
            public void onError(Throwable e) {
                noneConnected();
            }

            /**
             * 状态错误 | 状态不良 使用此UI
             */
            private void noneConnected() {
                if (ivConnectedPeople != null) {
                    ivConnectedPeople.setImageDrawable(connected_none);
                }
                if (tvConnectedPeople != null) {
                    tvConnectedPeople.setText(NONE_TEXT);
                    tvConnectedPeople.setTextColor(gray_color);
                }
            }

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        stopTimer();
        dialogDismiss();
    }

    @OnClick({R.id.bt_mainrx_simLocked,// sim卡锁定
                     R.id.bt_mainrx_wanConnect,// wan口连接
                     R.id.bt_mainrx_simUnConnected,// sim未连接
                     R.id.bt_mainrx_simConnected,// sim卡连接
                     R.id.iv_mainrx_signal,// 信号图标
                     R.id.tv_mainrx_signal,// 信号文本
                     R.id.iv_mainrx_connectedPeople,// 连接数图标
                     R.id.tv_mainrx_connectedPeople})// 连接数文本
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_mainrx_wanConnect:// 点击wan口
                clickWhenWanConnect();
                break;
            case R.id.bt_mainrx_simUnConnected:// sim卡(未连接, 但已经解锁)点击
                clickWhenSimNotConnect();
                break;
            case R.id.bt_mainrx_simNown:// sim未插入
                clickWhenSimNotConnect();
                break;
            case R.id.bt_mainrx_simConnected:// sim 卡(连接)点击
                clickWhenSimConnect();
                break;
            case R.id.bt_mainrx_simLocked:// sim卡锁定
                clickWhenSimLocked();
                break;
            case R.id.iv_mainrx_connectedPeople:// 连接数
            case R.id.tv_mainrx_connectedPeople:
                to(ActivityDeviceManager.class, false);
                break;

        }
    }

    /**
     * SIM卡被锁定时点击了该按钮
     */
    private void clickWhenSimLocked() {
        // 检测SIM卡状态
        simHelper_simLocked = new BoardSimHelper(getActivity());
        simHelper_simLocked.setOnPinRequireListener(result -> showPinDialog());// PIN
        simHelper_simLocked.setOnpukRequireListener(result -> showPukDialog());// PUK
        simHelper_simLocked.setOnpukTimeoutListener(result -> showPukTimeoutTip());// PUK
        simHelper_simLocked.setOnSimReadyListener(result -> ConnectSettingHelper.toConnect(getActivity()));// to connect
        simHelper_simLocked.boardNormal();
    }

    /**
     * WAN口连接后点击了该按钮
     */
    private void clickWhenWanConnect() {
        /* 2.wan口无效后再走sim卡 */
        wanHelperClick.setOnResultError(error -> toast(R.string.restart_device_tip));// 出错
        wanHelperClick.setOnError(e -> toast(R.string.restart_device_tip));// 出错
        wanHelperClick.setOnConnetedNextListener(wanResult -> {
            to(InternetStatusActivity.class, false);
        });// 跳转到IP
        wanHelperClick.setOnDisConnetedNextListener(wanResult -> toast(R.string.check_your_wan_cabling));// 提示连接
        wanHelperClick.setOnDisconnetingNextListener(wanResult -> toast(R.string.check_your_wan_cabling));// 提示连接
        wanHelperClick.boardNormal();
    }

    /**
     * SIM卡连接后点击了该按钮
     */
    private void clickWhenSimConnect() {
        // 检测SIM卡状态
        simHelper_simConnect = new BoardSimHelper(getActivity());
        simHelper_simConnect.setOnPinRequireListener(result -> showPinDialog());// PIN
        simHelper_simConnect.setOnpukRequireListener(result -> showPukDialog());// PUK
        simHelper_simConnect.setOnpukTimeoutListener(result -> showPukTimeoutTip());// PUK
        simHelper_simConnect.setOnSimReadyListener(result -> {
            activity.fraHelpers.transfer(fragmentClazz[6]);
        });// to usage
        simHelper_simConnect.boardNormal();
    }

    /**
     * SIM卡未连接时,点击了该按钮的操作
     */
    private void clickWhenSimNotConnect() {
        // 检测SIM卡状态
        simHelper_simNotConnect = new BoardSimHelper(getActivity());
        simHelper_simNotConnect.setOnPinRequireListener(result -> showPinDialog());// PIN
        simHelper_simNotConnect.setOnpukRequireListener(result -> showPukDialog());// PUK
        simHelper_simNotConnect.setOnpukTimeoutListener(result -> showPukTimeoutTip());// PUK
        simHelper_simNotConnect.setOnSimReadyListener(result -> {
            // 检测是否超过流量
            UsageSettingHelper usb = new UsageSettingHelper(getActivity());
            usb.setOnErrorListener(attr -> toast(R.string.connect_failed));
            usb.setOnResutlErrorListener(attr -> toast(R.string.connect_failed));
            usb.setOngetSuccessListener(attr -> {
                long monthlyPlan = attr.getMonthlyPlan();
                long usedData = attr.getUsedData();
                if (usedData >= monthlyPlan && monthlyPlan != 0) {// 超出--> 提示
                    toast(R.string.home_usage_over_redial_message);
                } else {// 未超出--> 连接
                    ConnectSettingHelper.toConnect(getActivity());
                }
            });
            usb.getUsageSetting();

        });// to connect
        simHelper_simNotConnect.boardNormal();
    }

    /**
     * 提示PUK码次数超出限制
     */
    private void showPukTimeoutTip() {
        toastLong(R.string.puk_alarm_des1);
    }

    /**
     * 显示PUK码对话框
     */
    private void showPukDialog() {
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)// 类型
                         .setTitleText(unlockSim_title)// 标题
                         .setContentText(unlockSim_content)// 内容
                         .setCancelText(cancel_text)// 取消按钮
                         .setConfirmText(confirm_text)// 确定按钮
                         .showCancelButton(true)// 取消按钮生效
                         .setConfirmClickListener(this::toPukFragment)// 点击确定按钮
                         .setCancelClickListener(Dialog::dismiss);// 对话框消隐
        dialog.show();// 展示
    }

    /**
     * 前往PUK界面
     *
     * @param dialog
     */
    private void toPukFragment(SweetAlertDialog dialog) {
        dialog.dismiss();
        // 提交当前fragment标记以作为"上一次"跳转标记
        SP.getInstance(getActivity()).putInt(Cons.TAB_FRA, Cons.TAB_MAIN);
        changeFragment(fragmentClazz[5], Cons.TAB_PUK);
    }

    /**
     * 显示PIN码对话框
     */
    private void showPinDialog() {
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)// 类型
                         .setTitleText(unlockSim_title)// 标题
                         .setContentText(unlockSim_content)// 内容
                         .setCancelText(cancel_text)// 取消按钮
                         .setConfirmText(confirm_text)// 确定按钮
                         .showCancelButton(true)// 取消按钮生效
                         .setConfirmClickListener(this::toPinFragment)// 点击确定按钮
                         .setCancelClickListener(Dialog::dismiss);// 对话框消隐
        dialog.show();// 展示
    }

    /**
     * 前往PIN界面
     *
     * @param dialog
     */
    private void toPinFragment(SweetAlertDialog dialog) {
        dialog.dismiss();
        SP.getInstance(getActivity()).putInt(Cons.TAB_FRA, Cons.TAB_MAIN);
        changeFragment(fragmentClazz[Cons.TAB_PIN], Cons.TAB_PIN);
    }

    /**
     * 切换按钮视图
     *
     * @param modeFlag
     */
    public void buttonUi(int modeFlag) {
        for (int i = 0; i < buttonsView.length; i++) {
            buttonsView[i].setVisibility(i == modeFlag ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 切换fragment
     *
     * @param clazz
     */
    public void changeFragment(Class clazz, int flag) {
        activity.transferUi(flag);
    }

    /**
     * 对话框隐藏
     */
    private void dialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
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
}

