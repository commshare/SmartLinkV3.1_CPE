package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.drm.DrmManagerClient;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.appwidget.RippleView;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.CommonUtil;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.Usage.UsageRecord;
import com.alcatel.wifilink.model.Usage.UsageSetting;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.model.network.Network;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.InternetStatusActivity;
import com.alcatel.wifilink.ui.activity.SettingAccountActivity;
import com.alcatel.wifilink.ui.activity.SimUnlockActivity;
import com.alcatel.wifilink.ui.activity.UsageActivity;
import com.alcatel.wifilink.ui.devicec.allsetup.ActivityDeviceManager;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.pop.SimPopHelper;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.ui.view.DynamicWave;
import com.alcatel.wifilink.ui.wizard.allsetup.TypeBean;
import com.alcatel.wifilink.utils.DataUtils;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import static com.alcatel.wifilink.R.id.connected_button;
import static com.alcatel.wifilink.ui.home.helper.cons.Cons.KB;
import static com.alcatel.wifilink.ui.home.helper.cons.Cons.MB;

@SuppressLint("ValidFragment")
public class MainFragment extends Fragment implements View.OnClickListener {


    /* frame_connect */
    private RelativeLayout m_connectLayout = null;
    private TextView m_connectToNetworkTextView;
    private Button m_connectBtn = null;

    /*sigel_panel*/
    private RelativeLayout mRl_sigelPanel;
    private TextView m_networkTypeTextView;
    private ImageView m_signalImageView;
    private TextView m_networkLabelTextView;

    /*access_panel*/
    private TextView m_accessnumTextView;
    private TextView m_accessstatusTextView;
    private ImageView m_accessImageView;

    private com.alcatel.wifilink.appwidget.waveprogress.WaveLoadingView mConnectedView;
    private RelativeLayout m_connectedLayout;
    private RelativeLayout m_accessDeviceLayout;

    private View m_view;// all view
    private Typeface typeFace;// font type
    private TimerHelper timerHelper;// timer
    private long connectTime = 0;// connect time
    private String upOrDownByteData = "0";// upload or download byte data

    // status type : wan or sim
    private boolean isWan;
    // can be click?
    private boolean canClick;

    private String zeroMB = "0";// traffic used

    private Activity activity;
    public static String PRESSBUTTON = "PRESSBUTTON";// true: the sim button have pressed.

    public String TAGS = "ma";
    private RelativeLayout mRl_main_wait;
    public static String type = new String();
    private int wanStatusOnTime = -1;// 实时WAN口检测
    private PopupWindows simPop;
    private DynamicWave dw_main;// 下方波浪
    private int rate = 2;// 下方波浪高度倍率
    private int duration = 4000;// 上方波浪滚动速率
    private ProgressDialog clickProgresDialog;// 等待进度条
    private String trafficUnit = "MB";

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getConnType(TypeBean tb) {
        type = tb.getType();
    }

    public MainFragment() {

    }

    public MainFragment(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        EventBus.getDefault().register(this);

        m_view = View.inflate(getActivity(), R.layout.fragment_home_mains, null);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto_Light.ttf");
        mConnectedView = (com.alcatel.wifilink.appwidget.waveprogress.WaveLoadingView) m_view.findViewById(connected_button);
        mConnectedView.setOnClickListener(this);

        m_connectLayout = (RelativeLayout) m_view.findViewById(R.id.connect_layout);
        m_connectedLayout = ((RelativeLayout) m_view.findViewById(R.id.connected_layout));

        m_connectToNetworkTextView = (TextView) m_view.findViewById(R.id.connect_network);
        m_connectBtn = (Button) m_view.findViewById(R.id.connect_button);
        m_connectBtn.setOnClickListener(this);

        mRl_sigelPanel = (RelativeLayout) m_view.findViewById(R.id.sigel_panel);
        m_signalImageView = (ImageView) m_view.findViewById(R.id.connct_signal);// SIGNAL LOGO
        // m_networkTypeTextView = (TextView) m_view.findViewById(R.id.connct_network_type);// 4G
        m_networkLabelTextView = (TextView) m_view.findViewById(R.id.connct_network_label);// SIGNAL text

        m_accessnumTextView = (TextView) m_view.findViewById(R.id.access_num_label);
        m_accessImageView = (ImageView) m_view.findViewById(R.id.access_status);
        m_accessImageView.setOnClickListener(this);
        m_accessstatusTextView = (TextView) m_view.findViewById(R.id.access_label);

        m_accessDeviceLayout = (RelativeLayout) m_view.findViewById(R.id.access_num_layout);
        mRl_main_wait = (RelativeLayout) m_view.findViewById(R.id.rl_main_wait);

        dw_main = (DynamicWave) m_view.findViewById(R.id.dw_main);
        dw_main.setDelY(rate);

        zeroMB = getString(R.string.Home_zero_data);
        showWait();// 等待对话框
        mConnectedView.setAnimDuration(duration);// 设置波浪滚动速度

        return m_view;
    }


    /* -------------------------------------------- 0.GET ALL STATUS -------------------------------------------- */
    // TOAT: all status
    /* **** get all status **** */
    private void getStatus() {
        /* **** check the type is wan or sim **** */
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {

            @Override
            public void onError(Throwable e) {
                if (Cons.TYPE_WAN.equalsIgnoreCase(type)) {
                    // TOAT: ********** 断网时先走此方法 ***********
                    connectUi(false);// set button logo
                    m_connectBtn.setBackgroundResource(R.drawable.wan_not_conn);
                    m_connectBtn.setText("");
                    m_connectToNetworkTextView.setText(getString(R.string.unknown));
                    m_signalImageView.setBackgroundResource(R.drawable.home_signal_0);
                    // m_networkTypeTextView.setVisibility(View.GONE);
                    m_networkLabelTextView.setText(getString(R.string.no_sim_no_wan));
                    m_networkLabelTextView.setText(getString(R.string.signal));
                    m_accessImageView.setBackgroundResource(R.drawable.device_none);
                    m_accessnumTextView.setVisibility(View.GONE);
                    m_accessstatusTextView.setText(getString(R.string.home_disconnected_to));
                    HomeActivity.mTvHomeMessageCount.setVisibility(View.GONE);
                }

                if (Cons.TYPE_SIM.equalsIgnoreCase(type)) {
                    sim_ui_setting();
                    setAccessDeviceStatus();
                }
            }

            @Override
            protected void onSuccess(WanSettingsResult result) {

                if (Cons.TYPE_SIM.equalsIgnoreCase(type)) {/* 用户主动点击SIM */
                    sim_ui_setting();
                    // 1.如果用户主动使用SIM连接--> 此时把WAN口断开的状态记录--> 同时标志位type设置为SIM卡连接
                    if (result.getStatus() == Cons.DISCONNECTED || result.getStatus() == Cons.DISCONNECTING) {
                        wanStatusOnTime = result.getStatus();
                        type = Cons.TYPE_SIM;
                    }
                    // 2.当检测到WAN口连接的时候--> 同时查看上一次的wan口状态是否为断开,如果是;--> 则说明用户插入了网线--> 将标志位改为WAN口连接
                    if (result.getStatus() == Cons.CONNECTED && (wanStatusOnTime == Cons.DISCONNECTED || wanStatusOnTime == Cons.DISCONNECTING)) {
                        type = Cons.TYPE_WAN;
                    }

                } else if (Cons.TYPE_WAN.equalsIgnoreCase(type)) {/* 用户主动点击WAN */
                    if (result.getStatus() == Cons.CONNECTED) {
                        wan_ui_setting();
                        type = Cons.TYPE_WAN;// 如果WAN口连上, 把标志位修改为WAN连接
                    }
                    if (result.getStatus() == Cons.DISCONNECTING || result.getStatus() == Cons.DISCONNECTED) {
                        sim_ui_setting();
                        type = Cons.TYPE_SIM;// 如果WAN口断开, 把标志位修改为SIM卡连接
                    }
                } else {/* 状态不明 */
                    type = Cons.TYPE_SIM;
                    int status = result.getStatus();
                    // status = Cons.CONNECTED;
                    if (status == Cons.CONNECTED) {
                        // wan connect
                        wan_ui_setting();
                    } else {
                        // sim connect
                        sim_ui_setting();
                    }
                }
                // device count layout
                setAccessDeviceStatus();
                // mRl_main_wait.setVisibility(View.GONE);
            }
        });
    }

    /* wan ui setting */
    private void wan_ui_setting() {
        isWan = true;// set wan button effect
        // wan logic
        m_connectLayout.setVisibility(View.VISIBLE);
        m_connectedLayout.setVisibility(View.GONE);
        m_connectToNetworkTextView.setVisibility(View.VISIBLE);
        m_connectToNetworkTextView.setText(getString(R.string.Ethernet));
        // m_networkTypeTextView.setVisibility(View.GONE);// TEXT: 2G\3G\4G
        m_connectBtn.setBackgroundResource(R.drawable.wan_conn);
        m_connectBtn.setText("");
        mRl_sigelPanel.setVisibility(View.GONE);
        m_networkLabelTextView.setText(getString(R.string.Ethernet));
        HomeActivity.mTvHomeMessageCount.setVisibility(View.GONE);// sms count view gone
        // when type check is finish , logo button can be click
        canClick = true;
        mRl_main_wait.setVisibility(View.GONE);
    }

    /* sim ui setting */
    private void sim_ui_setting() {
        isWan = false;// set sim button effect
        setSimButtonLogo();// logo connect/unconnected button layout
        setNetWorkType();// unicom | mobile | telcom
        setTrafficLayout();// traffic 0MB...layout
        setSignStatus();// sign level layout
        getTrafficUnit();// 获取月流量单位
        //SmsCountHelper.setSmsCount(getActivity(), HomeActivity.mTvHomeMessageCount);
    }

    /* -------------------------------------------- 0.GET ALL STATUS -------------------------------------------- */

    /* --------------------------------------------- 1.CONNECT BUTTON ---------------------------------------- */
    /* **** injust connect button status **** */
    public void setSimButtonLogo() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            public void onError(Throwable e) {
                HomeActivity.mTvHomeMessageCount.setVisibility(View.GONE);
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onSuccess(SimStatus simStatus) {
                Logs.v("ma_main", "simstatus: " + simStatus.getSIMState());
                // 1.is sim can be work
                int simState = simStatus.getSIMState();
                if (simState != Cons.READY) {
                    connectUi(false);// sim 卡没有准备好
                    HomeActivity.mTvHomeMessageCount.setVisibility(View.GONE);
                }
                if (simState == Cons.PIN_REQUIRED) {
                    canClick = true;
                }
                if (simState == Cons.READY) {
                    // 2. is network can be work
                    getTrafficInfo();// sim 卡已经准备好
                }
            }
        });
    }

    /* is network can be worked */
    private void getTrafficInfo() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int connstatus = result.getConnectionStatus();
                if (connstatus == Cons.CONNECTED) {
                    API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
                        @Override
                        public void onError(Throwable e) {
                            // 从PIN码产生的进度条消失
                            hideProgress();
                            mRl_main_wait.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onSuccess(NetworkInfos result) {
                            Logs.v("ma_main", "networktype: " + result.getNetworkType());
                            // // doesn't worked--> show 0MB
                            if (result.getNetworkType() == Cons.NOSERVER || result.getNetworkType() == Cons.UNKNOW) {
                                connectUi(false);
                            } else {
                                connectUi(true);
                            }
                            // 从PIN码产生的进度条消失
                            hideProgress();
                            mRl_main_wait.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            // 从PIN码产生的进度条消失
                            hideProgress();
                        }
                    });
                } else {
                    connectUi(false);
                    // 从PIN码产生的进度条消失
                    hideProgress();
                }
                // TOAT: 流量显示
                // tv_traffic.setText(upOrDownByteData);
                // mConnectedView.setCenterTitle(" ");
                mConnectedView.setCenterTitle(upOrDownByteData);
                // when type check is finish , logo button can be click
                canClick = true;
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                mRl_main_wait.setVisibility(View.GONE);
            }
        });
    }

    /* --------------------------------------------- 1.CONNECT BUTTON ---------------------------------------- */
    
    /* --------------------------------------------- 2.UNLOCKED/LOCKED STATUS ---------------------------------------- */

    /* **** setNetWorkType **** */
    private void setNetWorkType() {
        getNetworkedInfo();
    }

    /* injudge the network status */
    private void getNetworkedInfo() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int connstatus = result.getConnectionStatus();
                if (connstatus == Cons.CONNECTED) {
                    API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
                        @Override
                        public void onError(Throwable e) {
                            mRl_main_wait.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onSuccess(NetworkInfos result) {
                            Logs.v("ma_main", "network-type: " + result.getNetworkType());
                            m_connectToNetworkTextView.setVisibility(View.VISIBLE);
                            if (result.getNetworkType() == Cons.NOSERVER) {
                                connectUi(false);
                                m_connectToNetworkTextView.setText(getString(R.string.home_no_service));
                                mRl_main_wait.setVisibility(View.GONE);
                            } else {
                                m_connectToNetworkTextView.setText(result.getNetworkName());
                                // 3.injudge the connect status
                                isConnectStatus();
                                mRl_main_wait.setVisibility(View.GONE);
                            }

                        }
                    });
                } else {
                    m_connectToNetworkTextView.setText(getString(R.string.home_no_service));
                    mRl_main_wait.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                mRl_main_wait.setVisibility(View.GONE);
            }
        });
    }

    /* injudge the connect status */
    private void isConnectStatus() {
         /* get usage record */
        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            protected void onSuccess(UsageRecord result) {
                // get usage record
                getTrafficData(result);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                mRl_main_wait.setVisibility(View.GONE);
            }
        });

    }

    /* --------------------------------------------- 2.UNLOCKED/LOCKED STATUS ---------------------------------------- */
    
    /* --------------------------------------------- 3.TRAFFIC STATUS ---------------------------------------- */

    /* **** settrafficlayout **** */
    private void setTrafficLayout() {

        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            public void onError(Throwable e) {
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onSuccess(UsageRecord result) {
                // get traffic data and show
                getTrafficData(result);
            }
        });
    }

    /* get traffic data */
    private void getTrafficData(UsageRecord result) {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates conn) {
                int connstatus = conn.getConnectionStatus();
                if (connstatus == Cons.CONNECTED) {
                    API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
                        @Override
                        protected void onSuccess(NetworkInfos netInfos) {// TOAT: 加入漫游的判断
                            // 0: roaming 1: no roaming
                            float roaming = netInfos.getRoaming();
                            if (roaming == Cons.ROAMING) {/* 当前是漫游状态 */
                                usageShow(true, result);
                            } else {/* 非漫游状态 */
                                usageShow(false, result);
                            }
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            super.onResultError(error);
                            mRl_main_wait.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    /* **** usageShow: 非漫游状态的显示 **** */
    private void usageShow(boolean isRoaming, UsageRecord result) {
        if (isRoaming) {
            mConnectedView.setWaveColor(activity.getResources().getColor(R.color.circle_green));
            mConnectedView.setProgressValue(8);
        } else if (result.getMonthlyPlan() == 0) {// 没有设置流量
            mConnectedView.setWaveColor(activity.getResources().getColor(R.color.circle_green));
            mConnectedView.setProgressValue(8);
        } else {// 设置了流量
            long hUseData = result.getHUseData();
            long hMonthlyPlan = result.getMonthlyPlan();
            // get remain percent
            if (hUseData < hMonthlyPlan) {// 未超出流量: 用户流量 < 月计划流量
                float percent = (hUseData * 100f / hMonthlyPlan);
                mConnectedView.setWaveColor(activity.getResources().getColor(R.color.circle_green));
                mConnectedView.setProgressValue((int) percent);
            } else {// 超出流量: 用户流量 > 月计划流量
                mConnectedView.setWaveColor(activity.getResources().getColor(R.color.wave_yellow));
                mConnectedView.setProgressValue(90);
            }
        }
        // 2.set number
        showTraffic(isRoaming, result);
    }

    /* 显示已用流量(非漫游) */
    private void showTraffic(boolean isRoaming, UsageRecord result) {
        activity.runOnUiThread(() -> {

            // 漫游
            CommonUtil.TrafficBean roamingUse = CommonUtil.ConvertTraffic(getActivity(), result.getRoamUseData(), 1);
            // 非漫游
            CommonUtil.TrafficBean hadUse = CommonUtil.ConvertTraffic(getActivity(), result.getHUseData(), 1);
            CommonUtil.TrafficBean monthUse = CommonUtil.ConvertTraffic(getActivity(), result.getMonthlyPlan(), 0);

            String monthplan = "";
            if (isRoaming) {
                monthplan = getString(R.string.usage_setting_roaming);
            } else {
                if (monthUse.num <= 0) {
                    // monthplan = getString(R.string.no_month_plan);
                    monthplan = getString(R.string.used_of) + " 0" + trafficUnit;
                } else {
                    monthplan = getString(R.string.used_of) + " " + (int) monthUse.num + monthUse.type;
                }
            }
            mRl_main_wait.setVisibility(View.GONE);

            float centerData = isRoaming ? roamingUse.num : hadUse.num;
            upOrDownByteData = String.valueOf(centerData);
            // TOAT: 流量显示
            // tv_traffic.setText(upOrDownByteData);
            // tv_traffic.setTextSize(Dimension.SP, centerData > 999 ? 24 : 48);
            // mConnectedView.setCenterTitle(" ");
            mConnectedView.setCenterTitle(upOrDownByteData);// have used
            mConnectedView.setCenterTitleSize(centerData >= 999 ? 25 : 50);

            mConnectedView.setTopTitle(hadUse.type);// unit
            mConnectedView.setBottomTitle(monthplan);// month plan
            mConnectedView.setBottomTitleSize(monthUse.num >= 999 ? 10 : 14);
        });
    }

    /**
     * 获取月流量单位
     */
    private void getTrafficUnit() {
        API.get().getUsageSetting(new MySubscriber<UsageSetting>() {
            @Override
            protected void onSuccess(UsageSetting result) {
                int unit = result.getUnit();
                if (unit == MB) {
                    trafficUnit = "MB";
                } else if (unit == KB) {
                    trafficUnit = "KB";
                } else {
                    trafficUnit = "GB";
                }
            }
        });
    }
    
    /* --------------------------------------------- 3.TRAFFIC STATUS ---------------------------------------- */
    
    /* -------------------------------------------- 4.SIGNAL STATUS ------------------------------------------ */

    /* **** setSignStatus **** */
    private void setSignStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            public void onError(Throwable e) {
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onSuccess(SimStatus result) {
                // 1.is sim worked
                if (result.getSIMState() != Cons.READY) {
                    // m_networkTypeTextView.setVisibility(View.GONE);
                    m_networkLabelTextView.setVisibility(View.VISIBLE);
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
                } else {
                    // 2.injudge the networkinfo
                    issignNetworked();
                }
            }
        });
    }

    /* injudge the networkinfo */
    private void issignNetworked() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int connStatus = result.getConnectionStatus();
                if (connStatus == Cons.CONNECTED) {
                    API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
                        @Override
                        public void onError(Throwable e) {
                            mRl_main_wait.setVisibility(View.GONE);
                        }

                        @Override
                        protected void onSuccess(NetworkInfos result) {
                            setSignUi(result);
                        }
                    });
                }
            }
        });
    }

    /* setsignui according the networkstatus */
    private void setSignUi(NetworkInfos result) {
        mRl_sigelPanel.setVisibility(View.VISIBLE);
        mRl_main_wait.setVisibility(View.GONE);
        if (result != null) {
            if (result.getNetworkType() == Cons.NOSERVER) {
                // m_networkTypeTextView.setVisibility(View.GONE);
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
                m_networkLabelTextView.setVisibility(View.VISIBLE);
                return;
            }

            // TOAT: 漫游测试
            /* 如果有漫游, 则显示漫游 */
            float roaming = result.getRoaming();
            if (roaming == Cons.ROAMING) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_r);
            } else if (result.getSignalStrength() == Cons.NOSERVICE) {/* 否则显示检测强度 */
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
            } else if (result.getSignalStrength() == Cons.LEVEL_0) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
            } else if (result.getSignalStrength() == Cons.LEVEL_1) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g1);
            } else if (result.getSignalStrength() == Cons.LEVEL_2) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g2);
            } else if (result.getSignalStrength() == Cons.LEVEL_3) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g3);
            } else if (result.getSignalStrength() == Cons.LEVEL_4) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g4);
            } else if (result.getSignalStrength() == Cons.LEVEL_5) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g5);
            } else {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g5);
            }

            //show network type
            int type = result.getNetworkType();
            Logs.v("ma_network", type + "");
            if (type <= Cons.NOSERVER) {
                setNetWorkTypeString(null);
            } else if (type >= Cons.GPRS && type <= Cons.EDGE) {
                setNetWorkTypeString(getString(R.string.home_network_type_2g));
            } else if (type >= Cons.HSPA && type <= Cons.DC_HSPA_PLUS) {
                setNetWorkTypeString(getString(R.string.home_network_type_3g));
            } else if (type >= Cons.LTE && type <= Cons.LTE_PLUS) {
                setNetWorkTypeString(getString(R.string.home_network_type_4g));
            } else if (type >= Cons.CDMA && type <= Cons.GSM) {
                setNetWorkTypeString(getString(R.string.home_network_type_2g));
            } else if (type == Cons.EVDO) {
                setNetWorkTypeString(getString(R.string.home_network_type_3g));
            } else if (type >= Cons.LTE_FDD && type <= Cons.LTE_TDD) {
                setNetWorkTypeString(getString(R.string.home_network_type_4g));
            } else if (type == Cons.CDMA_Ehrpd) {
                setNetWorkTypeString(getString(R.string.home_network_type_3g_plus));
            } else {
                setNetWorkTypeString(null);
            }
        }
    }

    /**
     * 根据语言环境设置显示的网络类型表达方式
     *
     * @param netType
     */
    public void setNetWorkTypeString(String netType) {
        m_networkLabelTextView.setVisibility(View.VISIBLE);

        // 如果传入参数为null,则切换颜色和描述
        if (TextUtils.isEmpty(netType)) {
            m_networkLabelTextView.setText(getString(R.string.signal));
            m_networkLabelTextView.setTextColor(activity.getResources().getColor(R.color.grey_text));
            return;
        }

        // 如果信号正常--> 获取当前语言环境--> 切换颜色和描述
        String showType = "";
        String currentLanguage = OtherUtils.getCurrentLanguage();
        boolean isFrench = currentLanguage.equalsIgnoreCase(Constants.Language.FRENCH);
        if (isFrench) {
            showType = getString(R.string.signal) + " " + netType;
        } else {
            showType = netType + " " + getString(R.string.signal);
        }
        m_networkLabelTextView.setText(showType);
        m_networkLabelTextView.setTextColor(activity.getResources().getColor(R.color.mg_blue));
    }

    /* -------------------------------------------- 4.SIGNAL STATUS ------------------------------------------ */
    
    /* -------------------------------------------- 5.DEVICES STATUS------------------------------------------ */

    /* **** setAccessDeviceStatus **** */
    private void setAccessDeviceStatus() {
        API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {

            @Override
            public void onError(Throwable e) {
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onSuccess(ConnectedList result) {
                mRl_main_wait.setVisibility(View.GONE);
                m_accessnumTextView.setVisibility(View.VISIBLE);
                // m_accessnumTextView.setTypeface(typeFace);
                int deviceSize = result.getConnectedList().size();// 设备数量
                m_accessnumTextView.setText(String.format(Locale.ENGLISH, "%d", deviceSize));
                m_accessnumTextView.setTextColor(activity.getResources().getColor(R.color.mg_blue));
                m_accessImageView.setBackgroundResource(R.drawable.device_more);
                String strOfficial = activity.getString(R.string.access_lable);
                // TOAT: 获取当前语言决定是否加复数
                String currentLanguage = getCurrentLanguageSetting();
                if (currentLanguage.equals(Constants.Language.FRENCH)) {
                    if (deviceSize > 1) {
                        strOfficial += "S";
                    }
                }
                m_accessstatusTextView.setText(strOfficial);
            }
        });

    }

    /**
     * 获取当前语言设置
     */
    private String getCurrentLanguageSetting() {
        return PreferenceUtil.getString(Constants.Language.LANGUAGE, "");
    }

    /* -------------------------------------------- 5.DEVICES STATUS ------------------------------------------ */
    private ProgressDialog pinProgressDialog;

    @Override
    public void onResume() {
        super.onResume();
        Logs.v("ma_main", "pinlock: " + SimUnlockActivity.isPinUnlock);
        // 1.是否从解PIN界面跳转过来
        if (SimUnlockActivity.isPinUnlock) {
            pinProgressDialog = OtherUtils.showProgressPop(getActivity());
        }
        // 2. 启动定时器
        timerHelper = new TimerHelper(activity) {
            @Override
            public void doSomething() {
                getStatus();
            }
        };
        timerHelper.start(5000);
    }

    private void showWait() {
        mRl_main_wait.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        timerHelper.stop();
        count = 0;// 清空计数器
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_button:
                if (canClick) {// when the connect type is not sure--> can't click
                    ToastUtil_m.show(getActivity(), getString(R.string.connecting));
                    clickProgresDialog = OtherUtils.showProgressPop(getActivity());
                    checkConnStatusAndConnect();
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.insert_sim_or_wan));
                }
                break;
            case connected_button:
                if (canClick) {// when the connect type is not sure--> can't click
                    if (!isWan) {/* sim button click logic */
                        connectedBtnClick();
                    }
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.insert_sim_or_wan));
                }
                break;
            case R.id.access_status:
                ChangeActivity.toActivity(activity, ActivityDeviceManager.class, false, false, false, 0);
                break;
            default:
                break;
        }
    }

    int count = 0;// 用于计算失败的次数

    private void checkConnStatusAndConnect() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int connstatus = result.getConnectionStatus();
                if (connstatus == Cons.CONNECTED) {
                    if (!isWan) {/* sim button click logic */
                        // operater the button click function
                        simButtonConnect();
                    } else {/* wan button click logic */
                        // to internet status activity
                        ChangeActivity.toActivity(getActivity(), InternetStatusActivity.class, false, false, false, 0);
                    }
                    if (clickProgresDialog != null) {
                        clickProgresDialog.dismiss();
                        clickProgresDialog = null;
                    }
                }
                if (connstatus == Cons.CONNECTING) {
                    checkConnStatusAndConnect();
                }
                if (connstatus == Cons.DISCONNECTED || connstatus == Cons.DISCONNECTING) {
                    // 延迟15秒再提示用户连接失败
                    m_connectLayout.postDelayed(() -> {
                        if (clickProgresDialog != null) {
                            clickProgresDialog.dismiss();
                            clickProgresDialog = null;
                        }
                        ToastUtil_m.show(getActivity(), getString(R.string.insert_sim_or_wan));
                    }, 15 * 1000);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (clickProgresDialog != null) {
                    clickProgresDialog.dismiss();
                    clickProgresDialog = null;
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                if (clickProgresDialog != null) {
                    clickProgresDialog.dismiss();
                    clickProgresDialog = null;
                }
            }
        });
    }

    /* 初始状态: 未按下--> 去按下 */
    private void simButtonConnect() {
        connect();
    }

    /* 初始状态: 已按下--> 再次按下 */
    private void connectedBtnClick() {
        ChangeActivity.toActivity(activity, UsageActivity.class, false, false, false, 0);
    }

    /* **** core method: connect to remote **** */
    private void connect() {
        // injudge flag from settingAccount activity
        SharedPrefsUtil.getInstance(activity).putBoolean(SettingAccountActivity.LOGOUT_FLAG, false);
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.PIN_REQUIRED) {
                    showPop();
                } else if (simState == Cons.READY) {
                    API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
                        @Override
                        protected void onSuccess(ConnectionStates result) {
                            int connectStatus = result.getConnectionStatus();
                            if (connectStatus == Cons.DISCONNECTED) {
                                // set connect
                                connectHelper(true);
                                // // get monthly used
                                // getMonthlyPlan();
                                // // set logo button layout
                                // setSimButtonLogo();
                            }
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            ToastUtil_m.show(getActivity(), getString(R.string.restart_device_tip));
                            mRl_main_wait.setVisibility(View.GONE);
                        }
                    });
                } else if (simState == Cons.NOWN) {
                    ToastUtil_m.show(getActivity(), getString(R.string.Home_no_sim));
                }
            }

            @Override
            public void onError(Throwable e) {
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onFailure() {
                mRl_main_wait.setVisibility(View.GONE);
            }

        });

    }

    private void showPop() {
        simPop = new SimPopHelper() {

            private RippleView tv_unlock;
            private RippleView tv_cancel;

            @Override
            public void getView(View pop) {
                tv_cancel = (RippleView) pop.findViewById(R.id.tv_pop_sim_cancel);
                tv_unlock = (RippleView) pop.findViewById(R.id.tv_pop_sim_unlock);
                tv_cancel.setOnClickListener(v -> {
                    for (PopupWindows simPop : OtherUtils.popList) {
                        simPop.dismiss();
                    }
                    OtherUtils.popList.clear();
                });
                tv_unlock.setOnClickListener(v -> isSimInsert());
            }
        }.showPop(getActivity());
        OtherUtils.popList.add(simPop);
    }


    /* get monthly used */
    private void getMonthlyPlan() {
        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            public void onError(Throwable e) {
                mRl_main_wait.setVisibility(View.GONE);
            }

            @Override
            protected void onSuccess(UsageRecord result) {
                mRl_main_wait.setVisibility(View.GONE);
                // if monthly data is 0MB--> no toast
                if (result.getMonthlyPlan() <= 0) {
                    return;
                }
                // the traffic have over monthly used
                if ((result.getHUseData() + result.getRoamUseData()) >= result.getMonthlyPlan()) {
                    ToastUtil_m.showLong(getActivity(), getString(R.string.home_usage_over_redial_message));
                }
            }
        });
    }

    /* connect helper */
    public void connectHelper(boolean connectflag) {
        if (clickProgresDialog == null) {
            clickProgresDialog = OtherUtils.showProgressPop(getActivity());
        }
        if (connectflag) {
            API.get().connect(new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                    API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
                        @Override
                        protected void onSuccess(ConnectionStates result) {
                            int connectionStatus = result.getConnectionStatus();
                            if (connectionStatus == Cons.CONNECTED) {
                                if (clickProgresDialog != null) {
                                    clickProgresDialog.dismiss();
                                }
                                // get monthly used
                                getMonthlyPlan();
                                // set logo button layout
                                setSimButtonLogo();
                            }
                            if (connectionStatus == Cons.CONNECTING) {
                                m_connectLayout.postDelayed(() -> {
                                    connectHelper(true);
                                }, 3000);

                            }
                            if (connectionStatus == Cons.DISCONNECTING || connectionStatus == Cons.DISCONNECTED) {
                                if (clickProgresDialog != null) {
                                    clickProgresDialog.dismiss();
                                }
                            }
                        }
                    });

                }

                @Override
                public void onError(Throwable e) {
                    if (clickProgresDialog != null) {
                        clickProgresDialog.dismiss();
                    }
                }

                @Override
                protected void onFailure() {
                    if (clickProgresDialog != null) {
                        clickProgresDialog.dismiss();
                    }
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    if (clickProgresDialog != null) {
                        clickProgresDialog.dismiss();
                    }
                    Logs.d("ma_main", error.getMessage().toString());
                    ToastUtil_m.show(getActivity(), getString(R.string.connect_failed) + "\n" + getString(R.string.restart_device_tip));
                }
            });
        }
    }

    /**
     * true:connected visible
     * false:connect visible
     *
     * @param isConnected
     */
    /* connected ui helper */
    public void connectUi(boolean isConnected) {
        // set net work type ui
        if (!isConnected & type.equalsIgnoreCase(Cons.TYPE_SIM)) {
            setNetWorkTypeString(null);
        }
        // connect--> true:gone; false:visible
        m_connectLayout.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        // connected--> true:visible; false:gone
        m_connectedLayout.setVisibility(isConnected ? View.VISIBLE : View.GONE);
    }

    /* **** SIM卡是否插入 **** */
    private void isSimInsert() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                mRl_main_wait.setVisibility(View.GONE);
                int simState = result.getSIMState();
                if (simState == Cons.PIN_REQUIRED) {
                    // sim pop to unlock activity
                    EventBus.getDefault().postSticky(HomeActivity.class);
                    ChangeActivity.toActivity(getActivity(), SimUnlockActivity.class, false, false, false, 0);
                    return;
                }
                if (simState == Cons.NOWN) {
                    ToastUtil_m.show(getActivity(), getString(R.string.Home_no_sim));
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                mRl_main_wait.setVisibility(View.GONE);
            }
        });
    }

    public void hideProgress() {
        if (pinProgressDialog != null) {
            OtherUtils.hideProgressPop(pinProgressDialog);
            pinProgressDialog = null;
        }
    }

}
