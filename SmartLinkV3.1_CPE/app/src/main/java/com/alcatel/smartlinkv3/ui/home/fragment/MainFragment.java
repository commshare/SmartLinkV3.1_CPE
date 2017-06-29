package com.alcatel.smartlinkv3.ui.home.fragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.model.Usage.UsageRecord;
import com.alcatel.smartlinkv3.model.device.response.ConnectedList;
import com.alcatel.smartlinkv3.model.network.NetworkInfos;
import com.alcatel.smartlinkv3.model.sim.SimStatus;
import com.alcatel.smartlinkv3.model.wan.WanSettingsResult;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.ActivityDeviceManager;
import com.alcatel.smartlinkv3.ui.activity.InternetStatusActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.ui.activity.UsageActivity;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;
import com.alcatel.smartlinkv3.ui.home.helper.cons.Cons;
import com.alcatel.smartlinkv3.ui.home.helper.main.TimerHelper;
import com.alcatel.smartlinkv3.ui.home.helper.temp.ConnectionStates;
import com.alcatel.smartlinkv3.ui.view.WaveLoadingView;
import com.alcatel.smartlinkv3.utils.DataUtils;

import java.util.Locale;


public class MainFragment extends Fragment implements View.OnClickListener {

    /* frame_connect */
    private FrameLayout m_connectLayout = null;
    private TextView m_connectToNetworkTextView;
    private Button m_connectBtn = null;

    private LinearLayout m_simcardlockedLayout = null;
    private TextView m_simcardlockedTextView;
    private Button m_unlockSimBtn = null;

    private LinearLayout m_nosimcardLayout = null;
    private TextView m_simOrServiceTextView = null;

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
    private FrameLayout m_connectedLayout;
    RelativeLayout m_accessDeviceLayout;

    private View m_view;// all view
    private Typeface typeFace;// font type
    private TimerHelper timerHelper;// timer
    private long connectTime = 0;// connect time
    private long upOrDownByteData = 0;// upload or download byte data

    // if status is sim then use this flag to injudge the press button status
    private boolean m_simConnectFlag = false;
    // TOGO if status is wan then use this flag to injudge the press button status
    // private boolean m_wanConnectFlag = false;

    // status type : wan or sim
    private boolean isWan;
    // can be click?
    private boolean canClick;

    private String zeroMB = "0";// traffic used


    private Activity activity;
    public static String PRESSBUTTON = "PRESSBUTTON";// true: the sim button have pressed.

    public String TAGS = "ma";

    public MainFragment(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = View.inflate(activity, R.layout.fragment_home_mains, null);
        typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto_Light.ttf");
        mConnectedView = ((WaveLoadingView) m_view.findViewById(R.id.connected_button));
        mConnectedView.setOnClickListener(this);

        m_connectLayout = (FrameLayout) m_view.findViewById(R.id.connect_layout);
        m_connectedLayout = ((FrameLayout) m_view.findViewById(R.id.connected_layout));
        m_connectToNetworkTextView = (TextView) m_view.findViewById(R.id.connect_network);
        m_connectBtn = (Button) m_view.findViewById(R.id.connect_button);
        m_connectBtn.setOnClickListener(this);

        m_simcardlockedLayout = (LinearLayout) m_view.findViewById(R.id.sim_card_locked_layout);
        m_simcardlockedTextView = (TextView) m_view.findViewById(R.id.sim_card_locked_state);
        m_unlockSimBtn = (Button) m_view.findViewById(R.id.unlock_sim_button);

        m_nosimcardLayout = (LinearLayout) m_view.findViewById(R.id.no_sim_card_layout);
        m_simOrServiceTextView = (TextView) m_view.findViewById(R.id.no_sim_card_state);

        mRl_sigelPanel = (RelativeLayout) m_view.findViewById(R.id.sigel_panel);
        m_signalImageView = (ImageView) m_view.findViewById(R.id.connct_signal);// SIGNAL LOGO
        m_networkTypeTextView = (TextView) m_view.findViewById(R.id.connct_network_type);// 4G
        m_networkLabelTextView = (TextView) m_view.findViewById(R.id.connct_network_label);// SIGNAL text

        m_accessnumTextView = (TextView) m_view.findViewById(R.id.access_num_label);
        m_accessImageView = (ImageView) m_view.findViewById(R.id.access_status);
        m_accessImageView.setOnClickListener(this);
        m_accessstatusTextView = (TextView) m_view.findViewById(R.id.access_label);

        m_unlockSimBtn = (Button) m_view.findViewById(R.id.unlock_sim_button);
        m_unlockSimBtn.setOnClickListener(this);

        m_accessDeviceLayout = (RelativeLayout) m_view.findViewById(R.id.access_num_layout);

        zeroMB = getString(R.string.Home_zero_data);

        // 1. 初始化获取
        getStatus();

        return m_view;
    }

    /* -------------------------------------------- 0.GET ALL STATUS -------------------------------------------- */
    // TOAT: all status
    /* **** get all status **** */
    private void getStatus() {
        /* **** check the type is wan or sim **** */
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                int status = result.getStatus();
                System.out.println("status = " + status);
                /* connected || connecting means that wan is connect successful */
                // TOAT: force to wan
                // status = Cons.CONNECTED;
                if (status == Cons.CONNECTED || status == Cons.CONNECTING) {
                    // wan connect
                    wan_ui_setting();
                } else {
                    // sim connect
                    // TODO: NumberFormat Exception
                    sim_ui_setting();
                }
                // device count layout
                setAccessDeviceStatus();
                // when type check is finish , logo button can be click
                canClick = true;
            }
        });


    }

    /* wan ui setting */
    private void wan_ui_setting() {
        isWan = true;// set wan button effect
        // wan logic
        m_connectLayout.setVisibility(View.VISIBLE);
        m_connectedLayout.setVisibility(View.GONE);
        m_networkTypeTextView.setVisibility(View.GONE);// TEXT: 2G\3G\4G
        m_connectBtn.setBackgroundResource(R.drawable.home_connect_wan_logo);
        // m_signalImageView.setBackgroundResource(R.drawable.storage_toolbar_download_normal);
        mRl_sigelPanel.setVisibility(View.GONE);
        // TOGO 2017/6/27 there is the function of wan download speed, but hardware have no interface provider
        m_networkLabelTextView.setText("Ethernet");
    }

    /* sim ui setting */
    private void sim_ui_setting() {
        isWan = false;// set sim button effect
        // sim logic
        m_simConnectFlag = SharedPrefsUtil.getInstance(getActivity()).getBoolean(PRESSBUTTON, false);
        if (m_simConnectFlag) {
            setSimButtonLogo();// logo connect/unconnected button layout
        }
        setUnlockedLayout();// sim unlocked/locked layout 
        setTrafficLayout();// traffic 0MB...layout
        setSignStatus();// sign level layout
    }

    /* -------------------------------------------- 0.GET ALL STATUS -------------------------------------------- */

    /* --------------------------------------------- 1.CONNECT BUTTON ---------------------------------------- */
    /* **** injust connect button status **** */
    public void setSimButtonLogo() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus simStatus) {
                // 1.is sim can be work
                if (simStatus.getSIMState() != Cons.READY) {
                    mConnectedView.setCenterTitle(zeroMB);
                } else {
                    // 2. is network can be work
                    isNetworkInfo();
                }

            }
        });
    }

    /* is network can be worked */
    private void isNetworkInfo() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                
                // // doesn't worked--> show 0MB
                if (result.getNetworkType() == Cons.NOSERVER || result.getNetworkType() == Cons.UNKNOW) {
                    mConnectedView.setCenterTitle(zeroMB);
                } else {
                    // is press the connect button ?
                    API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
                        @Override
                        protected void onSuccess(ConnectionStates result) {
                            if (!m_simConnectFlag) {
                                // 3.is connected worked ?
                                noPressSituation(result);
                            } else {
                                pressSituation(result);
                            }
                            long staticDataMB = upOrDownByteData / 1024 / 1024;
                            mConnectedView.setCenterTitle(staticDataMB + "");
                        }
                    });

                }
            }
        });
    }

    /* pressSituation */
    private void pressSituation(ConnectionStates result) {
        m_connectLayout.setVisibility(isLogout() ? View.VISIBLE : View.GONE);
        m_connectedLayout.setVisibility(isLogout() ? View.GONE : View.VISIBLE);

        connectTime = result.getConnectionTime();
        upOrDownByteData = (result.getDlBytes() + result.getUlBytes());
    }

    /* noPressSituation--> is connected work ? */
    private void noPressSituation(ConnectionStates result) {
        if (result.getConnectionStatus() == Cons.CONNECTED) {
            // 4. is logout ?
            boolean logoutflag = isLogout();
            // changeui
            m_connectLayout.setVisibility(logoutflag ? View.VISIBLE : View.GONE);
            m_connectedLayout.setVisibility(logoutflag ? View.GONE : View.VISIBLE);
            // caculate the connect time and bytes
            connectTime = result.getConnectionTime();
            upOrDownByteData = result.getDlBytes() + result.getUlBytes();
        } else if (result.getConnectionStatus() == Cons.DISCONNECTING) {
            m_connectLayout.setVisibility(View.VISIBLE);
            m_connectedLayout.setVisibility(View.GONE);
        } else if (result.getConnectionStatus() == Cons.DISCONNECTED) {
            m_connectLayout.setVisibility(View.VISIBLE);
            m_connectedLayout.setVisibility(View.GONE);
        } else if (result.getConnectionStatus() == Cons.CONNECTING) {
            m_connectLayout.setVisibility(View.GONE);
            m_connectedLayout.setVisibility(View.VISIBLE);
        }
    }

    /* is logout from settingAccount activity ? */
    private boolean isLogout() {
        return SharedPrefsUtil.getInstance(activity).getBoolean(SettingAccountActivity.LOGOUT_FLAG, true);
    }

    /* --------------------------------------------- 1.CONNECT BUTTON ---------------------------------------- */
    
    /* --------------------------------------------- 2.UNLOCKED/LOCKED STATUS ---------------------------------------- */

    /* **** show sim network status **** */
    private void setUnlockedLayout() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (Cons.READY != result.getSIMState()) {
                    // 1.set unlocked ui
                    setUnLockedUi(result);
                } else {
                    // 2.injudge the network status
                    m_simcardlockedLayout.setVisibility(View.GONE);
                    isNetworkedInfo();
                }
            }
        });
    }

    /* injudge the network status */
    private void isNetworkedInfo() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                if (result.getNetworkType() == Cons.NOSERVER) {
                    m_simOrServiceTextView.setText(R.string.home_no_service);
                    m_nosimcardLayout.setVisibility(View.VISIBLE);
                    m_connectLayout.setVisibility(View.GONE);
                    mConnectedView.setCenterTitle(zeroMB);
                } else if (result.getNetworkType() == Cons.UNKNOW) {
                    m_simOrServiceTextView.setText(R.string.home_initializing);
                    m_nosimcardLayout.setVisibility(View.VISIBLE);
                    m_connectLayout.setVisibility(View.GONE);
                    mConnectedView.setCenterTitle(zeroMB);
                } else {
                    m_nosimcardLayout.setVisibility(View.GONE);
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
            protected void onSuccess(ConnectionStates result) {
                if (!m_simConnectFlag) {
                    if (result.getConnectionStatus() == Cons.CONNECTED) {
                        connectTime = result.getConnectionTime();
                        upOrDownByteData = (result.getDlBytes() + result.getUlBytes());
                    }
                    if (result.getConnectionStatus() == Cons.DISCONNECTED) {
                        connectTime = result.getConnectionTime();
                        upOrDownByteData = (result.getDlBytes() + result.getUlBytes());
                    }
                } else {
                    if (result.getConnectionStatus() == Cons.CONNECTED || result.getConnectionStatus() == Cons.DISCONNECTING) {
                    }
                }
                long staticDataMB = upOrDownByteData / 1024 / 1024;
                mConnectedView.setCenterTitle(staticDataMB + "");
            }
        });

    }

    /* set unlocked ui */
    private void setUnLockedUi(SimStatus result) {
        int nStatusId = R.string.Home_sim_invalid;
        if (Cons.ILLEGAL == result.getSIMState()) {
            nStatusId = R.string.Home_sim_invalid;
            m_unlockSimBtn.setVisibility(View.GONE);
        } else if (Cons.NOWN == result.getSIMState()) {
            nStatusId = R.string.Home_no_sim;
            m_unlockSimBtn.setVisibility(View.GONE);
        } else if (Cons.DETECTED == result.getSIMState()) {
            nStatusId = R.string.Home_SimCard_Detected;
            m_unlockSimBtn.setVisibility(View.GONE);
        } else if (Cons.SIMLOCK == result.getSIMState()) {
            nStatusId = R.string.Home_SimLock_Required;
            m_unlockSimBtn.setVisibility(View.GONE);
        } else if (Cons.PUK_TIMESOUT == result.getSIMState()) {
            nStatusId = R.string.Home_PukTimes_UsedOut;
            m_unlockSimBtn.setVisibility(View.GONE);
        } else if (Cons.INITING == result.getSIMState()) {
            nStatusId = R.string.Home_SimCard_IsIniting;
            m_unlockSimBtn.setVisibility(View.GONE);
        } else if (Cons.PIN_REQUIRED == result.getSIMState()) {
            nStatusId = R.string.Home_pin_locked;
            m_unlockSimBtn.setVisibility(View.VISIBLE);
        } else if (Cons.PUK_REQUIRED == result.getSIMState()) {
            nStatusId = R.string.Home_puk_locked;
            m_unlockSimBtn.setVisibility(View.VISIBLE);
        } else {
            nStatusId = R.string.Home_sim_invalid;
            m_unlockSimBtn.setVisibility(View.GONE);
        }

        if (Cons.PIN_REQUIRED == result.getSIMState() || Cons.PUK_REQUIRED == result.getSIMState()) {
            m_nosimcardLayout.setVisibility(View.GONE);
            m_simcardlockedLayout.setVisibility(View.VISIBLE);
            m_simcardlockedTextView.setText(nStatusId);
        } else {
            m_simOrServiceTextView.setText(nStatusId);
            m_simcardlockedLayout.setVisibility(View.GONE);
            m_nosimcardLayout.setVisibility(View.VISIBLE);
        }
        mConnectedView.setCenterTitle(zeroMB);
        m_connectLayout.setVisibility(View.GONE);
    }
    
    /* --------------------------------------------- 2.UNLOCKED/LOCKED STATUS ---------------------------------------- */
    
    /* --------------------------------------------- 3.TRAFFIC STATUS ---------------------------------------- */

    /* **** settrafficlayout **** */
    private void setTrafficLayout() {
        API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
            @Override
            protected void onSuccess(UsageRecord result) {
                // get traffic data and show
                getTrafficData(result);
            }
        });
    }

    /* get traffic data */
    private void getTrafficData(UsageRecord result) {
        if (result.getMonthlyPlan() != 0) {
            long hUseData = result.getHUseData();
            long hMonthlyPlan = result.getMonthlyPlan();
            long circleUseDataProgressValue = (hUseData * 100) / hMonthlyPlan;
            if (circleUseDataProgressValue >= 80) {
                mConnectedView.setWaveColor(activity.getResources().getColor(R.color.wave_yellow));
            } else {
                mConnectedView.setWaveColor(activity.getResources().getColor(R.color.circle_green));
            }
            if (circleUseDataProgressValue <= 100) {
                mConnectedView.setProgressValue((int) circleUseDataProgressValue - 8);
            } else {
                mConnectedView.setProgressValue(92);
            }
        } else {
            mConnectedView.setWaveColor(activity.getResources().getColor(R.color.circle_green));
            mConnectedView.setProgressValue(92);
        }
    }
    
    /* --------------------------------------------- 3.TRAFFIC STATUS ---------------------------------------- */
    
    /* -------------------------------------------- 4.SIGNAL STATUS ------------------------------------------ */

    /* **** setSignStatus **** */
    private void setSignStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
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
            if (result.getRoaming() == Cons.ROAMING)
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_r);
            //show signal strength
            if (result.getSignalStrength() == Cons.LEVEL_0) {
                m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
            }
            if (result.getSignalStrength() == Cons.LEVEL_1)
                m_signalImageView.setBackgroundResource(R.drawable.home_4g1);
            if (result.getSignalStrength() == Cons.LEVEL_2)
                m_signalImageView.setBackgroundResource(R.drawable.home_4g2);
            if (result.getSignalStrength() == Cons.LEVEL_3)
                m_signalImageView.setBackgroundResource(R.drawable.home_4g3);
            if (result.getSignalStrength() == Cons.LEVEL_4)
                m_signalImageView.setBackgroundResource(R.drawable.home_4g4);
            if (result.getSignalStrength() == Cons.LEVEL_5)
                m_signalImageView.setBackgroundResource(R.drawable.home_4g5);
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
        API.get().getConnectedDeviceList(new MySubscriber<ConnectedList>() {
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
    }
    
    /* -------------------------------------------- 5.DEVICES STATUS ------------------------------------------ */

    @Override
    public void onResume() {
        super.onResume();
        // 2. 启动定时器
        timerHelper = new TimerHelper(activity) {
            @Override
            public void doSomething() {
                getStatus();
            }
        };
        timerHelper.start(5000);
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
                    if (!isWan) {/* sim button click logic */
                        // commit the flag for keep logo visible
                        SharedPrefsUtil.getInstance(getActivity()).putBoolean(PRESSBUTTON, true);
                        // operater the button click function
                        simButtonConnect();
                    } else {/* wwan button click logic */
                        // to internet status activity
                        ChangeActivity.toActivity(getActivity(), InternetStatusActivity.class, true, false, false, 0);
                    }
                } else {
                    ToastUtil_m.show(getActivity(), "Please wait...");
                }
                break;
            case R.id.connected_button:
                if (canClick) {// when the connect type is not sure--> can't click
                    if (!isWan) {/* sim button click logic */
                        connectedBtnClick();
                    }
                }
                break;
            case R.id.unlock_sim_button:
                ((HomeActivity) activity).unlockSimBtnClick(true);
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
        ChangeActivity.toActivity(activity, UsageActivity.class, true, false, false, 0);
    }

    /* **** core method: connect to remote **** */
    private void connect() {
        // injudge flag from settingAccount activity
        SharedPrefsUtil.getInstance(activity).putBoolean(SettingAccountActivity.LOGOUT_FLAG, false);
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int connectStatus = result.getConnectionStatus();
                if (connectStatus == Cons.DISCONNECTED || connectStatus == Cons.DISCONNECTING) {
                    // reset flag
                    m_simConnectFlag = !m_simConnectFlag;
                    // get monthly used
                    getMonthlyPlan();
                    // set logo button layout
                    setSimButtonLogo();
                    // set unlocked/locked layout
                    setUnlockedLayout();
                } else {
                    if (connectStatus == Cons.CONNECTED || connectStatus == Cons.CONNECTING) {
                        connectHelper(false);
                    } else {
                        connectHelper(true);
                    }
                }
            }

            /* get monthly used */
            private void getMonthlyPlan() {
                API.get().getUsageRecord(DataUtils.getCurrent(), new MySubscriber<UsageRecord>() {
                    @Override
                    protected void onSuccess(UsageRecord result) {
                        // the traffic have over monthly used
                        if ((result.getHUseData() + result.getRoamUseData()) >= result.getMonthlyPlan()) {
                            String msgRes = activity.getString(R.string.home_usage_over_redial_message);
                            Toast.makeText(activity, msgRes, Toast.LENGTH_LONG).show();
                        }
                    }
                });
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

}
