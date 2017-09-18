package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.waveprogress.WaveLoadingView;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.Usage.UsageRecord;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.InternetStatusActivity;
import com.alcatel.wifilink.ui.activity.SimUnlockActivity;
import com.alcatel.wifilink.ui.activity.UsageActivity;
import com.alcatel.wifilink.ui.devicec.allsetup.ActivityDeviceManager;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.ErrHelper;
import com.alcatel.wifilink.ui.home.helper.main.SignalHelper;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.main.TrafficHelper;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.ui.view.DynamicWave;
import com.alcatel.wifilink.ui.wizard.allsetup.TypeBean;
import com.alcatel.wifilink.utils.DataUtils;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.OtherUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by qianli.ma on 2017/9/16.
 */
@SuppressLint("ValidFragment")
public class Mainfragment_new extends Fragment {

    @BindView(R.id.bt_main_connected)
    WaveLoadingView btMainConnected;// 已连接按钮
    @BindView(R.id.bt_main_notConnect)
    Button btMainNotConnect;// 未连接按钮
    @BindView(R.id.tv_main_networkName)
    TextView tvNetworkName;// 网络类型:移动|联通|电信
    @BindView(R.id.v_main_wave)
    DynamicWave vMainWave;// 波浪
    @BindView(R.id.rl_main_signal)
    PercentRelativeLayout rlMainSignal;// 信号布局
    @BindView(R.id.iv_main_signal)
    ImageView ivMainSignal;// 信号强度
    @BindView(R.id.tv_main_signal)
    TextView tvMainSignal;// 信号类型
    @BindView(R.id.rl_main_device)
    PercentRelativeLayout rlMainDevice;// 设备布局
    @BindView(R.id.iv_main_device)
    ImageView ivMainDevice;// 设备状态
    @BindView(R.id.tv_main_device)
    TextView tvMainDevice;// 设备数量
    Unbinder unbinder;

    private String MA = "ma_main_new";// 标记:用于打印日志
    private Activity activity;
    private TypeBean tb;// 以何种类型展示
    private TimerHelper globalTimer;// 全局定时器
    private TimerHelper connTimer;// 连接定时器
    public static String type = new String();
    private boolean isWanOrSim;// 自动检测:  true: wan & false: sim
    private ProgressDialog progressDialog;
    int count = 0;// 计数器-->用于统计获取连接状态次数
    int countPin = 0;// 计数器-->用于统计获取连接状态次数
    private TimerHelper pinTimer;
    private View inflate;

    private String networkName;// 网络名称
    private int signalStrength;// 信号强度
    private int networkType;// 信号类型
    private int deviceCount;// 设备数量
    private String deviceText;// 设备数量(文本)
    private long monthlyPlan;// 月流量(字节)
    private long userTraffic;// 已用普通流量(字节)
    private long roamTraffic;// 已用漫游流量(字节)
    private boolean roaming;// 是否漫游

    public Mainfragment_new() {
    }

    public Mainfragment_new(Activity activity) {
        this.activity = activity;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getType(TypeBean tb) {
        this.tb = tb;
        type = this.tb.getType();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        inflate = View.inflate(getActivity(), R.layout.fragment_home_main_new, null);
        unbinder = ButterKnife.bind(this, inflate);
        initSomeAttr();
        initData();
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从解PIN界面返回时显示进度条
        showPinUnlockWait();
    }

    /**
     * 显示从pin界面跳转回来的进度条
     */
    private void showPinUnlockWait() {
        if (SimUnlockActivity.isPinUnlock) {
            if (progressDialog == null) {
                progressDialog = OtherUtils.showProgressPop(getActivity());
            }
            pinTimer = new TimerHelper(getActivity()) {
                @Override
                public void doSomething() {
                    API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
                        @Override
                        protected void onSuccess(ConnectionStates result) {
                            SimUnlockActivity.isPinUnlock = false;
                            int connStatu = result.getConnectionStatus();
                            if (connStatu == Cons.CONNECTED) {
                                countPin = 0;
                                if (progressDialog != null) {
                                    hideProgressPop();
                                }
                                if (pinTimer != null) {
                                    pinTimer.stop();
                                }
                            } else {
                                if (countPin >= 15) {
                                    countPin = 0;
                                    hideProgressPop();
                                    ToastUtil_m.show(getActivity(), getString(R.string.smsdetail_tryagain_confirm));
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            SimUnlockActivity.isPinUnlock = false;
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            SimUnlockActivity.isPinUnlock = false;
                        }
                    });
                    countPin++;
                }

            };
            pinTimer.start(3000);
            OtherUtils.homeTimerList.add(pinTimer);
        }
    }

    /**
     * 隐藏进度条
     */
    private void hideProgressPop() {
        OtherUtils.hideProgressPop(progressDialog);
        progressDialog = null;
    }

    /* 初始化某些属性 */
    private void initSomeAttr() {
        btMainConnected.setAnimDuration(4000);// 已连接按钮的波浪速率
        vMainWave.setDelY(2);// 中间波浪高度倍率
        deviceText = getString(R.string.access_lable);
    }

    /* 初始化数据 */
    private void initData() {
        // 启动定时器
        globalTimer = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                getAllStatus();
            }
        };
        globalTimer.start(3000);
        OtherUtils.homeTimerList.add(globalTimer);
    }

    /**
     * 获取全部的状态
     */
    private void getAllStatus() {
        getWanStatus();// 获取WAN口
        getSimStates();// 获取SIM卡
        getNetworkSome();// 获取与network相关
        getDevices();// 获取连接设备数
        getTraffic();// 获取流量
    }

    /* 获取流量 */
    private void getTraffic() {
        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            protected void onSuccess(UsageRecord result) {
                // 月流量计划
                monthlyPlan = result.getMonthlyPlan();
                // 已经使用的普通流量
                userTraffic = result.getHUseData();
                // 已经使用的漫游流量
                roamTraffic = result.getRoamUseData();
            }
        });
    }

    /* 获取与network相关 */
    private void getNetworkSome() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int connStatus = result.getConnectionStatus();
                if (connStatus == Cons.CONNECTED) {// 只有拨号成功后才能获取network相关的信息
                    getNetWorkName();// 获取网络类型:移动|电信...
                    getRoaming();// 获取漫游状态
                    getSignalStrength();// 获取信号强度
                    getsignalType();// 获取信号类型:4G|3G..
                }
            }

            @Override
            public void onError(Throwable e) {
                ErrHelper.errlog("ConnectionStates", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ErrHelper.errlog("ConnectionStates", null, error);
            }
        });

    }

    /* 获取漫游状态 */
    private void getRoaming() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                //  0:roaming 1: no roaming
                roaming = result.getRoaming() == Cons.ROAMING ? true : false;
            }

            @Override
            public void onError(Throwable e) {
                ErrHelper.errlog("Roaming", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ErrHelper.errlog("Roaming", null, error);
            }
        });
    }

    /* 获取信号类型:4G|3G... */
    private void getsignalType() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                networkType = result.getNetworkType();
            }

            @Override
            public void onError(Throwable e) {
                networkType = Cons.NOSERVER;
                ErrHelper.errlog("networkType", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                networkType = Cons.NOSERVER;
                ErrHelper.errlog("networkType", null, error);
            }
        });
    }

    /* 获取信号强度 */
    private void getSignalStrength() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                signalStrength = result.getSignalStrength();
            }

            @Override
            public void onError(Throwable e) {
                signalStrength = Cons.LEVEL_0;
                ErrHelper.errlog("signal strength", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                signalStrength = Cons.LEVEL_0;
                ErrHelper.errlog("signal strength", null, error);
            }
        });
    }

    /* 获取网络类型:移动|电信... */
    private void getNetWorkName() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                String nn = String.valueOf(result.getNetworkName());
                if (!nn.contains("460")) {
                    networkName = nn;
                }
            }

            @Override
            public void onError(Throwable e) {
                networkName = getString(R.string.home_no_service);
                ErrHelper.errlog("network name", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                networkName = getString(R.string.home_no_service);
                ErrHelper.errlog("network name", null, error);
            }
        });
    }

    /* 获取连接设备数 */
    private void getDevices() {
        API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {
            @Override
            protected void onSuccess(ConnectedList result) {
                deviceCount = result.getConnectedList().size();
                deviceText = deviceCount + " " + activity.getResources().getString(R.string.access_lable);
            }

            @Override
            public void onError(Throwable e) {
                deviceText = activity.getResources().getString(R.string.access_lable);
                ErrHelper.errlog("deviceText", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                deviceText = activity.getResources().getString(R.string.access_lable);
                ErrHelper.errlog("deviceText", null, error);
            }
        });
    }

    /* 获取WAN口 */
    private void getWanStatus() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                switch (result.getStatus()) {
                    case Cons.CONNECTED:// WAN口连接状态
                        isWanOrSim = true;
                        showWanSuccess();
                        break;
                    case Cons.CONNECTING:// WAN口正在连接状态
                        isWanOrSim = true;
                        showWanNormal();
                        break;
                    case Cons.DISCONNECTING:// WAN口正在掉线状态
                        isWanOrSim = false;
                        getAllStatus();
                        break;
                    case Cons.DISCONNECTED:// WAN口已经掉线状态
                        isWanOrSim = false;
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                isWanOrSim = false;
                getAllStatus();
                ErrHelper.errlog("wan", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                isWanOrSim = false;
                getAllStatus();
                ErrHelper.errlog("wan", null, error);
            }
        });
    }

    /* 获取SIM卡状态 */
    private void getSimStates() {
        if (!isWanOrSim) {// 非wan口连接状态--> 进行SIM状态获取
            API.get().getSimStatus(new MySubscriber<SimStatus>() {
                @Override
                protected void onSuccess(SimStatus result) {
                    int simState = result.getSIMState();
                    Logs.v(MA, "simstate: " + simState);
                    switch (simState) {
                        case Cons.READY:
                            // 是否拨号成功
                            getConnStatus();
                            break;
                        default:
                            showSimNormal();
                            getAllStatus();
                            break;
                    }
                }

                @Override
                public void onError(Throwable e) {
                    // showSimNormal();
                    ErrHelper.errlog("sim", e, null);
                    getAllStatus();
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    ErrHelper.errlog("sim", null, error);
                    getAllStatus();
                }
            });
        }
    }

    /* 获取拨号连接状态 */
    private void getConnStatus() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int connectionStatus = result.getConnectionStatus();
                Logs.v(MA, "conn: " + connectionStatus);
                switch (connectionStatus) {
                    case Cons.CONNECTED:
                        showSimSuccess();
                        break;
                    default:
                        showSimNormal();
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                showSimNormal();
                ErrHelper.errlog("simConnect", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                showSimNormal();
                ErrHelper.errlog("simConnect", null, error);
            }
        });
    }

    /**
     * 显示SIM卡拨号成功UI
     */
    public void showSimSuccess() {
        if (isWanOrSim) {
            return;
        }
        // button logo
        btMainNotConnect.setVisibility(View.GONE);
        btMainConnected.setVisibility(View.VISIBLE);
        // is roaming
        if (roaming) {
            btMainConnected.setProgressValue(8);
            btMainConnected.setTopTitle(TrafficHelper.getTrafficType(roamTraffic));
            btMainConnected.setWaveColor(Color.GREEN);
        } else {
            int perTraffic = (int) TrafficHelper.cacuPercent(userTraffic, monthlyPlan);// 百分比
            btMainConnected.setProgressValue(perTraffic <= 8 ? 8 : perTraffic > 92 ? 92 : perTraffic);// 占有率
            btMainConnected.setTopTitle(TrafficHelper.getTrafficType(userTraffic));// 类型MB|GB|..
            btMainConnected.setCenterTitle(TrafficHelper.getUserTrafficString(userTraffic));// 流量
            String monthType = TrafficHelper.getTrafficType(monthlyPlan);// 月流量类型
            String monthNum = TrafficHelper.getMonthlyTrafficString(monthlyPlan);// 月流量
            btMainConnected.setBottomTitle(getString(R.string.used_of) + " " + monthNum + monthType);// 整体显示
            btMainConnected.setWaveColor(perTraffic > 92 ? activity.getResources().getColor(R.color.wave_yellow) : Color.GREEN);// 颜色
        }
        // mobile type text
        tvNetworkName.setText(networkName);
        // signal logo
        rlMainSignal.setVisibility(View.VISIBLE);
        SignalHelper.showSignalStregth(ivMainSignal, signalStrength);
        // signal text
        SignalHelper.showSignalType(getActivity(), tvMainSignal, networkType);
        SignalHelper.showSignalTextColor(getActivity(), tvMainSignal, networkType);
        // device logo
        rlMainDevice.setVisibility(View.VISIBLE);
        ivMainDevice.setBackgroundResource(deviceCount > 0 ? R.drawable.device_more : R.drawable.device_none);
        // device text
        tvMainDevice.setText(deviceText);
        tvMainDevice.setTextColor(deviceCount > 0 ? activity.getResources().getColor(R.color.AA009AFF) : activity.getResources().getColor(R.color.grey_text));
    }

    /**
     * 显示SIM未连接状态UI
     */
    private void showSimNormal() {
        if (isWanOrSim) {// 如果此时WAN口连接了则不再走以下逻辑
            return;
        }
        // button logo
        if (btMainNotConnect != null) {
            btMainNotConnect.setVisibility(View.VISIBLE);
            btMainNotConnect.setBackgroundResource(R.drawable.home_btn_connected_nor);
            btMainNotConnect.setText(getString(R.string.connect));
        }
        btMainConnected.setVisibility(View.GONE);
        // mobile type text
        tvNetworkName.setText(getString(R.string.home_no_service));
        // signal logo
        rlMainSignal.setVisibility(View.VISIBLE);
        ivMainSignal.setBackgroundResource(R.drawable.home_4g_none);
        // signal text
        tvMainSignal.setText(getString(R.string.signal));
        tvMainSignal.setTextColor(activity.getResources().getColor(R.color.grey_text));
        // device logo
        rlMainDevice.setVisibility(View.VISIBLE);
        ivMainDevice.setBackgroundResource(R.drawable.device_more);
        // device text
        tvMainDevice.setText(deviceText);
        tvMainDevice.setTextColor(activity.getResources().getColor(R.color.AA009AFF));
    }


    /**
     * 显示WAN口连接成功UI
     */
    private void showWanSuccess() {
        if (!isWanOrSim) {// 如果此时SIM连接了则不再走以下逻辑
            return;
        }
        // button logo
        if (btMainNotConnect != null) {
            btMainNotConnect.setVisibility(View.VISIBLE);
            btMainNotConnect.setBackgroundResource(R.drawable.wan_conn);
            btMainNotConnect.setText("");
        }
        btMainConnected.setVisibility(View.GONE);
        // mobile type text
        tvNetworkName.setText(getString(R.string.Ethernet));
        // signal logo
        rlMainSignal.setVisibility(View.GONE);
        // device logo
        rlMainDevice.setVisibility(View.VISIBLE);
        ivMainDevice.setBackgroundResource(R.drawable.device_more);
        // device text
        tvMainDevice.setText(deviceText);
        tvMainDevice.setTextColor(activity.getResources().getColor(R.color.AA009AFF));
    }

    /**
     * 显示WAN口掉线UI
     */
    private void showWanNormal() {
        if (!isWanOrSim) {// 如果此时SIM连接了则不再走以下逻辑
            return;
        }
        // button logo
        btMainNotConnect.setVisibility(View.VISIBLE);
        btMainNotConnect.setBackgroundResource(R.drawable.wan_not_conn);
        btMainNotConnect.setText("");
        btMainConnected.setVisibility(View.GONE);
        // mobile type text
        tvNetworkName.setText(getString(R.string.Ethernet));
        // signal logo
        rlMainSignal.setVisibility(View.GONE);
        // device logo
        rlMainDevice.setVisibility(View.VISIBLE);
        ivMainDevice.setBackgroundResource(R.drawable.device_more);
        // device text
        tvMainDevice.setText(deviceText);
        tvMainDevice.setTextColor(activity.getResources().getColor(R.color.AA009AFF));
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        globalTimer.stop();
    }

    @OnClick({R.id.bt_main_connected, R.id.bt_main_notConnect, R.id.iv_main_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_main_connected:/* SIM拨号成功 */
                ChangeActivity.toActivity(activity, UsageActivity.class, false, false, false, 0);
                break;
            case R.id.bt_main_notConnect:/* SIM没有拨号 */
                if (isWanOrSim) {// 处于WAN口连接状态
                    ChangeActivity.toActivity(getActivity(), InternetStatusActivity.class, false, false, false, 0);
                } else {// 处于SIM卡连接状态
                    simConnect();
                }
                break;
            case R.id.iv_main_device: /* 设备按钮 */
                ChangeActivity.toActivity(getActivity(), ActivityDeviceManager.class, false, false, false, 0);
                break;
        }
    }

    /**
     * 发送SIM拨号请求
     */
    private void simConnect() {

        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.PUK_REQUIRED) {

                }
            }
        });

        count = 0;
        if (progressDialog == null) {
            progressDialog = OtherUtils.showProgressPop(getActivity());
        }
        // 发送连接请求
        API.get().connect(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                connTimer = new TimerHelper(getActivity()) {
                    @Override
                    public void doSomething() {
                        // 获取连接状态
                        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
                            @Override
                            protected void onSuccess(ConnectionStates result) {
                                int connStatus = result.getConnectionStatus();
                                if (connStatus == Cons.CONNECTED) {
                                    hideProgressPop();
                                    getAllStatus();
                                    connTimer.stop();
                                } else if (connStatus == Cons.DISCONNECTED) {
                                    if (count > 5) {
                                        hideProgressPop();
                                        ToastUtil_m.show(getActivity(), getString(R.string.restart_device_tip));
                                        connTimer.stop();
                                    }
                                }

                            }
                        });
                        // 计数递增
                        count++;
                    }
                };
                connTimer.start(3000);
                OtherUtils.homeTimerList.add(connTimer);
            }

            @Override
            public void onError(Throwable e) {
                ErrHelper.errlog("simConnect", e, null);
                ToastUtil_m.show(getActivity(), getString(R.string.restart_device_tip));
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ErrHelper.errlog("simConnect", null, error);
            }
        });
    }
}
