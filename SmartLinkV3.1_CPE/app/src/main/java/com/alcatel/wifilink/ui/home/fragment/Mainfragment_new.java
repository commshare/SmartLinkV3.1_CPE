package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.alcatel.wifilink.appwidget.waveprogress.WaveLoadingView;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.SignalHelper;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.ui.view.DynamicWave;
import com.alcatel.wifilink.ui.wizard.allsetup.TypeBean;
import com.alcatel.wifilink.utils.Logs;
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

    private String MA = "ma_main_new";
    private Activity activity;
    private TypeBean tb;
    private TimerHelper timerHelper;
    public static String type = new String();
    private boolean isWanOrSim = false;// true: wan & false: sim


    private String networkName;// 网络名称
    private int signalStrength;// 信号强度
    private int networkType;// 信号类型
    private int deviceCount;// 设备数量
    private String deviceText;// 设备数量(文本)

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
        View inflate = View.inflate(getActivity(), R.layout.fragment_home_main_new, null);
        unbinder = ButterKnife.bind(this, inflate);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        btMainConnected.setAnimDuration(4000);// 已连接按钮的波浪速率
        vMainWave.setDelY(2);// 中间波浪高度倍率
        deviceText = getString(R.string.access_lable);
    }

    private void initData() {
        // 启动定时器
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                getWanStatus();// 获取WAN口
                getSimStates();// 获取SIM卡
                getNetworkSome();// 获取与network相关
                getDevices();// 获取连接设备数
                // TODO: 2017/9/16 获取月流量计划以及已使用流量
            }

        };
        timerHelper.start(5000);
    }

    private void getNetworkSome() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                
            }
        });
        // getNetWorkName();// 获取网络类型:移动|电信...
        // getSignalStrength();// 获取信号强度
        // getsignalType();// 获取信号类型:4G|3G..
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
                errlog("networkType", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                networkType = Cons.NOSERVER;
                errlog("networkType", null, error);
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
                errlog("signal strength", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                signalStrength = Cons.LEVEL_0;
                errlog("signal strength", null, error);
            }
        });
    }

    /* 获取网络类型:移动|电信... */
    private void getNetWorkName() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                networkName = String.valueOf(result.getNetworkName());
            }

            @Override
            public void onError(Throwable e) {
                networkName = getString(R.string.home_no_service);
                errlog("network name", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                networkName = getString(R.string.home_no_service);
                errlog("network name", null, error);
            }
        });
    }

    /* 获取连接设备数 */
    private void getDevices() {
        API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {
            @Override
            protected void onSuccess(ConnectedList result) {
                deviceCount = result.getConnectedList().size();
                deviceText = deviceCount + " " + getResources().getString(R.string.access_lable);
            }

            @Override
            public void onError(Throwable e) {
                deviceText = getResources().getString(R.string.access_lable);
                errlog("deviceText", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                deviceText = getResources().getString(R.string.access_lable);
                errlog("deviceText", null, error);
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
                    case Cons.DISCONNECTED:// WAN口掉线状态
                        isWanOrSim = false;
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                isWanOrSim = false;
                errlog("wan", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                isWanOrSim = false;
                errlog("wan", null, error);
            }
        });
    }

    /* 获取SIM卡状态 */
    private void getSimStates() {
        if (!isWanOrSim) {
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
                            break;
                    }
                }

                @Override
                public void onError(Throwable e) {
                    showSimNormal();
                    errlog("sim", e, null);
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    showSimNormal();
                    errlog("sim", null, error);
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
                        // TODO: 2017/9/16
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
                errlog("connect", e, null);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                showSimNormal();
                errlog("connect", null, error);
            }
        });
    }

    /**
     * 显示SIM卡拨号成功UI
     */
    public void showSimSuccess() {
        // TODO: 2017/9/16
        // button logo
        btMainNotConnect.setVisibility(View.GONE);
        btMainConnected.setVisibility(View.VISIBLE);
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
        tvMainDevice.setTextColor(deviceCount > 0 ? getResources().getColor(R.color.AA009AFF) : getResources().getColor(R.color.grey_text));
    }

    /**
     * 显示SIM未连接状态UI
     */
    private void showSimNormal() {
        // button logo
        btMainNotConnect.setVisibility(View.VISIBLE);
        btMainConnected.setVisibility(View.GONE);
        // mobile type text
        tvNetworkName.setText(getString(R.string.home_no_service));
        // signal logo
        rlMainSignal.setVisibility(View.VISIBLE);
        ivMainSignal.setBackgroundResource(R.drawable.home_4g_none);
        // signal text
        tvMainSignal.setText(getString(R.string.signal));
        tvMainSignal.setTextColor(getResources().getColor(R.color.grey_text));
        // device logo
        rlMainDevice.setVisibility(View.VISIBLE);
        ivMainDevice.setBackgroundResource(R.drawable.device_none);
        // device text
        tvMainDevice.setText(getString(R.string.access_lable));
        tvMainDevice.setTextColor(getResources().getColor(R.color.grey_text));
    }


    /**
     * 显示WAN口连接成功UI
     */
    private void showWanSuccess() {
        // button logo
        btMainNotConnect.setVisibility(View.GONE);
        btMainConnected.setVisibility(View.VISIBLE);
        // mobile type text
        tvNetworkName.setText(getString(R.string.Ethernet));
        // signal logo
        rlMainSignal.setVisibility(View.GONE);
        // device logo
        rlMainDevice.setVisibility(View.VISIBLE);
        ivMainDevice.setBackgroundResource(R.drawable.device_more);
        // device text
        tvMainDevice.setText(deviceText);
        tvMainDevice.setTextColor(getResources().getColor(R.color.AA009AFF));
    }

    /**
     * 日志打印器
     *
     * @param pre   前缀
     * @param e     异常实体
     * @param error 错误码
     */
    public void errlog(String pre, Throwable e, ResponseBody.Error error) {
        if (e != null) {
            Logs.v(MA, pre + " error: " + e.getMessage());
        }
        if (error != null) {
            Logs.v(MA, pre + " code: " + error.getCode() + "; " + pre + "error msg: " + error.getMessage());
        }
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
        timerHelper.stop();
    }

    @OnClick({R.id.bt_main_connected, R.id.bt_main_notConnect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_main_connected:// 已经连接上
                break;
            case R.id.bt_main_notConnect:// 没有连接上
                API.get().connect(new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {

                    }
                });
                break;
        }
    }
}
