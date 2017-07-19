package com.alcatel.wifilink.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.CommonUtil;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.Usage.UsageRecord;
import com.alcatel.wifilink.model.device.response.ConnectedList;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.activity.InternetStatusActivity;
import com.alcatel.wifilink.ui.activity.SettingAccountActivity;
import com.alcatel.wifilink.ui.activity.UsageActivity;
import com.alcatel.wifilink.ui.devicec.allsetup.ActivityDeviceManager;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.utils.DataUtils;
import com.alcatel.wifilink.utils.ScreenSize;

import java.util.Locale;

import me.itangqi.waveloadingview.WaveLoadingView;

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

    private WaveLoadingView mConnectedView;
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

    public MainFragment() {
    }

    public MainFragment(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = View.inflate(getActivity(), R.layout.fragment_home_mains, null);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto_Light.ttf");
        mConnectedView = (WaveLoadingView) m_view.findViewById(R.id.connected_button);
        mConnectedView.setOnClickListener(this);

        m_connectLayout = (RelativeLayout) m_view.findViewById(R.id.connect_layout);
        m_connectedLayout = ((RelativeLayout) m_view.findViewById(R.id.connected_layout));
        m_connectToNetworkTextView = (TextView) m_view.findViewById(R.id.connect_network);
        m_connectBtn = (Button) m_view.findViewById(R.id.connect_button);
        m_connectBtn.setOnClickListener(this);

        mRl_sigelPanel = (RelativeLayout) m_view.findViewById(R.id.sigel_panel);
        m_signalImageView = (ImageView) m_view.findViewById(R.id.connct_signal);// SIGNAL LOGO
        m_networkTypeTextView = (TextView) m_view.findViewById(R.id.connct_network_type);// 4G
        m_networkLabelTextView = (TextView) m_view.findViewById(R.id.connct_network_label);// SIGNAL text

        m_accessnumTextView = (TextView) m_view.findViewById(R.id.access_num_label);
        m_accessImageView = (ImageView) m_view.findViewById(R.id.access_status);
        m_accessImageView.setOnClickListener(this);
        m_accessstatusTextView = (TextView) m_view.findViewById(R.id.access_label);

        m_accessDeviceLayout = (RelativeLayout) m_view.findViewById(R.id.access_num_layout);
        mRl_main_wait = (RelativeLayout) m_view.findViewById(R.id.rl_main_wait);

        zeroMB = getString(R.string.Home_zero_data);

        // 0. wait show
        // 1. 初始化获取
        // getStatus();
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
                // TOAT: ********** 断网时先走此方法 ***********
                Log.d("ma", "getWanSettings : " + e.getMessage().toString());
                connectUi(false);// set button logo
                m_connectBtn.setBackgroundResource(R.drawable.home_connect_wan_logo_disconnect);
                m_connectToNetworkTextView.setText(getString(R.string.unknown));
                m_signalImageView.setBackgroundResource(R.drawable.home_signal_0);
                m_networkTypeTextView.setVisibility(View.GONE);
                m_networkLabelTextView.setText(getString(R.string.signal));
                m_accessImageView.setBackgroundResource(R.drawable.home_ic_person_none);
                m_accessnumTextView.setVisibility(View.GONE);
                m_accessstatusTextView.setText(getString(R.string.home_disconnected_to));
                HomeActivity.mTvHomeMessageCount.setVisibility(View.GONE);
            }

            @Override
            protected void onSuccess(WanSettingsResult result) {
                int status = result.getStatus();
                /* connected || connecting means that wan is connect successful */
                // TOAT: force to wan
                // status = Cons.CONNECTED;
                if (status == Cons.CONNECTED) {
                    // wan connect
                    wan_ui_setting();
                } else {
                    // sim connect
                    sim_ui_setting();
                }
                // device count layout
                setAccessDeviceStatus();
                // when type check is finish , logo button can be click
                // canClick = true;
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
        m_networkTypeTextView.setVisibility(View.GONE);// TEXT: 2G\3G\4G
        m_connectBtn.setBackgroundResource(R.drawable.home_connect_wan_logo);
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
    }

    /* -------------------------------------------- 0.GET ALL STATUS -------------------------------------------- */

    /* --------------------------------------------- 1.CONNECT BUTTON ---------------------------------------- */
    /* **** injust connect button status **** */
    public void setSimButtonLogo() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            public void onError(Throwable e) {
                Log.d(TAGS, "setSimButtonLogo: " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(SimStatus simStatus) {
                // 1.is sim can be work
                if (simStatus.getSIMState() != Cons.READY) {
                    connectUi(false);// sim 卡没有准备好
                } else {
                    // 2. is network can be work
                    getTrafficInfo();// sim 卡已经准备好
                }
            }
        });
    }

    /* is network can be worked */
    private void getTrafficInfo() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            public void onError(Throwable e) {
                Log.d("ma", "getTrafficInfo : " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(NetworkInfos result) {
                // // doesn't worked--> show 0MB
                if (result.getNetworkType() == Cons.NOSERVER || result.getNetworkType() == Cons.UNKNOW) {
                    connectUi(false);
                } else {
                    // is press the connect button ?
                    API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
                        @Override
                        protected void onSuccess(ConnectionStates result) {

                            int stats = result.getConnectionStatus();
                            if (stats == Cons.CONNECTED) {
                                connectUi(true);
                            } else {
                                connectUi(false);
                            }
                            mConnectedView.setCenterTitle(upOrDownByteData);
                            // when type check is finish , logo button can be click
                            canClick = true;
                            mRl_main_wait.setVisibility(View.GONE);
                        }
                    });
                }
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
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            public void onError(Throwable e) {
                Log.d("ma", "getNetworkInfo : " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(NetworkInfos result) {
                if (result.getNetworkType() == Cons.NOSERVER || result.getNetworkType() == Cons.UNKNOW) {
                    connectUi(false);
                    m_connectToNetworkTextView.setVisibility(View.GONE);
                } else {
                    m_connectToNetworkTextView.setText(result.getNetworkName());
                    m_connectToNetworkTextView.setVisibility(View.VISIBLE);
                    // 3.injudge the connect status
                    isConnectStatus();
                }

            }
        });
    }

    /* injudge the connect status */
    private void isConnectStatus() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            public void onError(Throwable e) {
                Log.d("ma", "isConnectStatus : " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(ConnectionStates result) {
                int stats = result.getConnectionStatus();
                if (stats == Cons.CONNECTED) {
                    /* get usage record */
                    API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
                        @Override
                        protected void onSuccess(UsageRecord result) {
                            // get usage record
                            getTrafficData(result);
                        }
                    });
                }

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
                Log.d("ma", "getUsageRecord : " + e.getMessage().toString());
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
        // 1.set wave degree
        if (result.getMonthlyPlan() == 0) {// 没有设置流量
            mConnectedView.setWaveColor(activity.getResources().getColor(R.color.circle_green));
            mConnectedView.setProgressValue(92);
        } else {// 设置了流量
            long hUseData = result.getHUseData();
            long hMonthlyPlan = result.getMonthlyPlan();
            // get remain percent
            if (hUseData < hMonthlyPlan) {// 未超出流量: 用户流量 < 月计划流量
                float percent = ((hMonthlyPlan - hUseData) * 100f / hMonthlyPlan);
                mConnectedView.setWaveColor(activity.getResources().getColor(R.color.circle_green));
                mConnectedView.setProgressValue((int) percent);
            } else {// 超出流量: 用户流量 > 月计划流量
                mConnectedView.setWaveColor(activity.getResources().getColor(R.color.wave_yellow));
                mConnectedView.setProgressValue(90);
            }
        }
        // 2.set number
        showTrafficData(result);

    }

    /* 显示已用流量 */
    private void showTrafficData(UsageRecord result) {
        activity.runOnUiThread(() -> {

            CommonUtil.TrafficBean hadUse = CommonUtil.ConvertTraffic(getActivity(), result.getHUseData(), 1);
            CommonUtil.TrafficBean monthUse = CommonUtil.ConvertTraffic(getActivity(), result.getMonthlyPlan(), 0);

            String monthplan = "";
            if (monthUse.num <= 0) {
                monthplan = getString(R.string.no_month_plan);
            } else {
                monthplan = getString(R.string.used_of) + " " + (int) monthUse.num + monthUse.type;
            }

            upOrDownByteData = String.valueOf(hadUse.num);
            mConnectedView.setCenterTitle(upOrDownByteData);// have used
            mConnectedView.setCenterTitleSize(hadUse.num >= 999 ? 30 : 60);

            mConnectedView.setTopTitle(hadUse.type);// unit
            mConnectedView.setBottomTitle(monthplan);// month plan
            mConnectedView.setBottomTitleSize(monthUse.num >= 999 ? 10 : 14);
        });
    }
    
    /* --------------------------------------------- 3.TRAFFIC STATUS ---------------------------------------- */
    
    /* -------------------------------------------- 4.SIGNAL STATUS ------------------------------------------ */

    /* **** setSignStatus **** */
    private void setSignStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            public void onError(Throwable e) {
                Log.d("ma", "setSignStatus : " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(SimStatus result) {
                // 1.is sim worked
                if (result.getSIMState() != Cons.READY) {
                    m_networkTypeTextView.setVisibility(View.GONE);
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
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            public void onError(Throwable e) {
                Log.d("ma", "issignNetworked : " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(NetworkInfos result) {
                setSignUi(result);
            }
        });
    }

    /* setsignui according the networkstatus */
    private void setSignUi(NetworkInfos result) {
        if (result != null) {
            if (result.getNetworkType() == Cons.NOSERVER) {
                m_networkTypeTextView.setVisibility(View.GONE);
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
                m_networkLabelTextView.setVisibility(View.VISIBLE);
                return;
            }
            //show roaming
            if (result.getRoaming() == Cons.ROAMING) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_r);
            }

            //show signal strength
            if (result.getSignalStrength() == Cons.LEVEL_0) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
            }
            if (result.getSignalStrength() == Cons.LEVEL_1) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g1);
            }

            if (result.getSignalStrength() == Cons.LEVEL_2) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g2);
            }
            if (result.getSignalStrength() == Cons.LEVEL_3) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g3);
            }
            if (result.getSignalStrength() == Cons.LEVEL_4) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g4);
            }
            if (result.getSignalStrength() == Cons.LEVEL_5) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g5);
            }
            //show network type
            if (result.getNetworkType() == Cons.UNKNOW) {
                m_networkTypeTextView.setVisibility(View.GONE);
                m_networkLabelTextView.setVisibility(View.VISIBLE);
            }

            //2G
            if (result.getNetworkType() == Cons.EDGE || result.getNetworkType() == Cons.GPRS) {
                m_networkTypeTextView.setVisibility(View.VISIBLE);
                m_networkTypeTextView.setTypeface(typeFace);
                m_networkTypeTextView.setText(R.string.home_network_type_2g);
                m_networkTypeTextView.setTextColor(activity.getResources().getColor(R.color.mg_blue));
            }

            //3G
            if (result.getNetworkType() == Cons.HSPA || result.getNetworkType() == Cons.UMTS || result.getNetworkType() == Cons.HSUPA) {
                m_networkTypeTextView.setVisibility(View.VISIBLE);
                m_networkTypeTextView.setTypeface(typeFace);
                m_networkTypeTextView.setText(R.string.home_network_type_3g);
                m_networkTypeTextView.setTextColor(activity.getResources().getColor(R.color.mg_blue));
            }

            //3G+
            if (result.getNetworkType() == Cons.HSPA_PLUS || result.getNetworkType() == Cons.DC_HSPA_PLUS) {
                m_networkTypeTextView.setVisibility(View.VISIBLE);
                m_networkTypeTextView.setTypeface(typeFace);
                m_networkTypeTextView.setText(R.string.home_network_type_3g_plus);
                m_networkTypeTextView.setTextColor(activity.getResources().getColor(R.color.mg_blue));
            }

            //4G			
            if (result.getNetworkType() == Cons.LTE) {
                m_networkTypeTextView.setVisibility(View.VISIBLE);
                m_networkTypeTextView.setTypeface(typeFace);
                m_networkTypeTextView.setText(R.string.home_network_type_4g);
                m_networkTypeTextView.setTextColor(activity.getResources().getColor(R.color.mg_blue));
            }
        }
    }

    /* -------------------------------------------- 4.SIGNAL STATUS ------------------------------------------ */
    
    /* -------------------------------------------- 5.DEVICES STATUS------------------------------------------ */

    /* **** setAccessDeviceStatus **** */
    private void setAccessDeviceStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() == Cons.READY) {
                    API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {

                        @Override
                        public void onError(Throwable e) {
                            Log.d("ma", "setAccessDeviceStatus : " + e.getMessage().toString());
                        }

                        @Override
                        protected void onSuccess(ConnectedList result) {
                            m_accessnumTextView.setTypeface(typeFace);
                            m_accessnumTextView.setText(String.format(Locale.ENGLISH, "%d", result.getConnectedList().size()));
                            m_accessnumTextView.setTextColor(activity.getResources().getColor(R.color.mg_blue));
                            m_accessImageView.setImageResource(R.drawable.home_ic_person_many);
                            String strOfficial = activity.getString(R.string.access_lable);
                            m_accessstatusTextView.setText(strOfficial);
                        }
                    });
                } else {
                    m_accessImageView.setBackgroundResource(R.drawable.home_ic_person_none);
                    m_accessnumTextView.setVisibility(View.GONE);
                    m_accessstatusTextView.setText(getString(R.string.connect));
                }
            }
        });

    }
    
    /* -------------------------------------------- 5.DEVICES STATUS ------------------------------------------ */

    @Override
    public void onResume() {
        super.onResume();
        // 1. show wait
        showWait();
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
        timerHelper.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_button:
                if (canClick) {// when the connect type is not sure--> can't click
                    ToastUtil_m.show(getActivity(), getString(R.string.connecting));
                    if (!isWan) {/* sim button click logic */
                        // operater the button click function
                        simButtonConnect();
                    } else {/* wan button click logic */
                        // to internet status activity
                        ChangeActivity.toActivity(getActivity(), InternetStatusActivity.class, true, false, false, 0);
                    }
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.insert_sim_or_wan));
                }
                break;
            case R.id.connected_button:
                if (canClick) {// when the connect type is not sure--> can't click
                    if (!isWan) {/* sim button click logic */
                        connectedBtnClick();
                    }
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.insert_sim_or_wan));
                }
                break;
            case R.id.access_status:
                ChangeActivity.toActivity(activity, ActivityDeviceManager.class, true, false, false, 0);
                break;
            default:
                break;
        }
    }

    /* 初始状态: 未按下--> 去按下 */
    private void simButtonConnect() {
        //if (LinkAppSettings.isLoginSwitchOff()) {// test temp file flag
        // TOAT: 测试阶段将其强制为true
        if (true) {// test temp file flag
            connect();
        } else {
            ToastUtil_m.show(activity, getString(R.string.UserAccount_Disable));
        }
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
            public void onError(Throwable e) {
                Log.d("ma", "connect : " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(SimStatus result) {
                int simState = result.getSIMState();
                if (simState == Cons.READY) {
                    API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
                        @Override
                        protected void onSuccess(ConnectionStates result) {
                            int connectStatus = result.getConnectionStatus();
                            if (connectStatus == Cons.DISCONNECTED || connectStatus == Cons.DISCONNECTING) {
                                // set connect
                                connectHelper(true);
                                // get monthly used
                                getMonthlyPlan();
                                // set logo button layout
                                setSimButtonLogo();
                            }
                        }
                    });
                }
                if (simState == Cons.NOWN) {
                    ToastUtil_m.show(getActivity(), getString(R.string.Home_no_sim));
                }
            }
        });

    }


    /* get monthly used */
    private void getMonthlyPlan() {
        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            public void onError(Throwable e) {
                Log.d("ma", "getMonthlyPlan : " + e.getMessage().toString());
            }

            @Override
            protected void onSuccess(UsageRecord result) {
                // if monthly data is 0MB--> no toast
                if (result.getMonthlyPlan() <= 0) {
                    return;
                }
                // the traffic have over monthly used
                if ((result.getHUseData() + result.getRoamUseData()) >= result.getMonthlyPlan()) {
                    ToastUtil_m.show(getActivity(), getString(R.string.home_usage_over_redial_message));
                }
            }
        });
    }

    /* connect helper */
    public void connectHelper(boolean connectflag) {
        if (connectflag) {
            API.get().connect(new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                }
            });
        } else {
            API.get().disConnect(new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {

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
        // connect--> true:gone; false:visible
        m_connectLayout.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        // connected--> true:visible; false:gone
        m_connectedLayout.setVisibility(isConnected ? View.VISIBLE : View.GONE);
    }

}
