package com.alcatel.smartlinkv3.ui.home.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.DataConnectManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.ConnectedDeviceItemModel;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.LinkAppSettings;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.model.Usage.UsageRecord;
import com.alcatel.smartlinkv3.model.Usage.UsageSetting;
import com.alcatel.smartlinkv3.model.battery.BatteryState;
import com.alcatel.smartlinkv3.model.network.NetworkInfos;
import com.alcatel.smartlinkv3.model.sim.SimStatus;
import com.alcatel.smartlinkv3.model.user.LoginState;
import com.alcatel.smartlinkv3.ui.activity.ActivityDeviceManager;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.ui.activity.UsageActivity;
import com.alcatel.smartlinkv3.ui.dialog.AutoForceLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog;
import com.alcatel.smartlinkv3.ui.dialog.AutoLoginProgressDialog.OnAutoLoginFinishedListener;
import com.alcatel.smartlinkv3.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.ForceLoginSelectDialog;
import com.alcatel.smartlinkv3.ui.dialog.LoginDialog;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;
import com.alcatel.smartlinkv3.ui.home.helper.cons.Cons;
import com.alcatel.smartlinkv3.ui.home.helper.main.ApiEngine;
import com.alcatel.smartlinkv3.ui.home.helper.main.ViewConnectBroadcastReceiver;
import com.alcatel.smartlinkv3.ui.home.helper.temp.ConnectionStates;
import com.alcatel.smartlinkv3.ui.view.CircleProgress;
import com.alcatel.smartlinkv3.ui.view.WaveLoadingView;

import java.util.ArrayList;
import java.util.Locale;

import static com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity.hac;

public class MainFragment extends Fragment implements View.OnClickListener, ViewConnectBroadcastReceiver.OnBatteryListener, ViewConnectBroadcastReceiver.OnDeviceConnectListener, ViewConnectBroadcastReceiver.OnNetworkRollRequestListener, ViewConnectBroadcastReceiver.OnSimRollRequestListener, ViewConnectBroadcastReceiver.OnWanConnectListener, ViewConnectBroadcastReceiver.OnWanRollRequestListener, ViewConnectBroadcastReceiver.OnWifiConnectListener {

    private static final String BATTERY_LEVEL = "Battery Level";

    /* frame_connect */
    private FrameLayout m_connectLayout = null;
    private TextView m_connectToNetworkTextView;
    private Button m_connectBtn = null;

    private LinearLayout m_simcardlockedLayout = null;
    private TextView m_simcardlockedTextView;
    private Button m_unlockSimBtn = null;

    private LinearLayout m_nosimcardLayout = null;
    private TextView m_simOrServiceTextView = null;

    private boolean m_bConnectPressd = false;
    private boolean m_bConnectReturn = false;
    private long statictime = 0;
    private int staticdata = 0;

    /*sigel_panel*/
    private TextView m_networkTypeTextView;
    private ImageView m_signalImageView;
    private TextView m_networkLabelTextView;

    /*access_panel*/
    private TextView m_accessnumTextView;
    private TextView m_accessstatusTextView;
    private ImageView m_accessImageView;


    private LoginDialog m_loginDialog = null;
    private AutoLoginProgressDialog m_autoLoginDialog = null;
    private AutoForceLoginProgressDialog m_ForceloginDlg = null;

    private String strZeroConnDuration = null;

    private Typeface typeFace;

    private ViewConnectBroadcastReceiver m_viewConnetMsgReceiver;
    private ImageView batteryView;
    private CircleProgress circleProgress;
    private WaveLoadingView mConnectedView;
    private FrameLayout m_connectedLayout;
    RelativeLayout m_accessDeviceLayout;
    private View m_view;

    public SimStatus home_simStatus;
    public UsageSetting home_usageSetting;
    public UsageRecord home_usageRecord;
    public BatteryState home_batteryState;
    public LoginState home_loginStatu;
    public ConnectionStates home_connectionState;
    public NetworkInfos home_networkInfos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = View.inflate(getActivity(), R.layout.fragment_home_mains, null);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto_Light.ttf");
        circleProgress = ((CircleProgress) m_view.findViewById(R.id.home_circleProgress));
        batteryView = ((ImageView) m_view.findViewById(R.id.home_battery_image));
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

        m_networkTypeTextView = (TextView) m_view.findViewById(R.id.connct_network_type);
        m_signalImageView = (ImageView) m_view.findViewById(R.id.connct_signal);
        m_networkLabelTextView = (TextView) m_view.findViewById(R.id.connct_network_label);

        m_accessnumTextView = (TextView) m_view.findViewById(R.id.access_num_label);
        m_accessImageView = (ImageView) m_view.findViewById(R.id.access_status);
        m_accessImageView.setOnClickListener(this);
        m_accessstatusTextView = (TextView) m_view.findViewById(R.id.access_label);

        m_loginDialog = new LoginDialog(getActivity());
        m_autoLoginDialog = new AutoLoginProgressDialog(getActivity());
        m_ForceloginDlg = new AutoForceLoginProgressDialog(getActivity());

        m_unlockSimBtn = (Button) m_view.findViewById(R.id.unlock_sim_button);
        m_unlockSimBtn.setOnClickListener(this);

        m_accessDeviceLayout = (RelativeLayout) m_view.findViewById(R.id.access_num_layout);

        strZeroConnDuration = getString(R.string.Home_zero_data);

        m_viewConnetMsgReceiver = new ViewConnectBroadcastReceiver();
        m_viewConnetMsgReceiver.setOnBatteryListener(this);
        m_viewConnetMsgReceiver.setOnDeviceConnectListener(this);
        m_viewConnetMsgReceiver.setOnNetworkRollRequestListener(this);
        m_viewConnetMsgReceiver.setOnSimRollRequestListener(this);
        m_viewConnetMsgReceiver.setOnWanConnectListener(this);
        m_viewConnetMsgReceiver.setOnWanRollRequestListener(this);
        m_viewConnetMsgReceiver.setOnWifiConnectListener(this);

        return m_view;
    }

    /**
     * 初始化所有状态
     */
    private void initStatus() {
        home_simStatus = ApiEngine.home_simStatus;
        home_usageSetting = ApiEngine.home_usageSetting;
        home_usageRecord = ApiEngine.home_usageRecord;
        home_batteryState = ApiEngine.home_batteryState;
        home_loginStatu = ApiEngine.home_loginStatu;
        home_connectionState = ApiEngine.home_connectionState;
        home_networkInfos = ApiEngine.home_networkInfos;
    }

    /**
     * 获取所有状态
     */
    private void getAllStatus() {
        ApiEngine.getSimStatus();
        ApiEngine.getUserLoginStatus();
        ApiEngine.getConnectStatus();
        ApiEngine.getNetworkInfo();
        ApiEngine.getUsageSetting();
        ApiEngine.getUsageRecord();

        initStatus();
    }

    /**
     * 刷新所有视图
     */
    private void showAllView() {
        showConnectBtnView();
        showNetworkState();
        showTrafficUsageView();
        showSignalAndNetworkType();
        showAccessDeviceState();
        repeatDataUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        initStatus();

        // 接收HomeActivity的定时信号
        // TOAT: null point exception
        hac.setOnTimerStatus(() -> {
            hac.runOnUiThread(() -> {
                System.out.println(" MainFragment running ");
                // 1.get all status
                getAllStatus();
                // 2.refresh ui
                showAllView();
            });
        });
    }


    private void repeatDataUi() {
        // 确保流量界面显示
        // TOAT: 登陆
        // UserLoginStatus m_loginStatus = BusinessManager.getInstance().getLoginStatus();
        if (home_loginStatu != null && home_loginStatu.getState() == Cons.LOGIN) {
            connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(m_viewConnetMsgReceiver);
        } catch (Exception e) {

        }
        m_bConnectReturn = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        m_loginDialog.destroyDialog();
        m_autoLoginDialog.destroyDialog();
        m_ForceloginDlg.destroyDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_button:
                connectBtnClick();
                break;
            case R.id.connected_button:
                connectedBtnClick();
                break;

            case R.id.unlock_sim_button:
                ((HomeActivity) getActivity()).unlockSimBtnClick(true);
                break;
            case R.id.access_status:
                ((HomeActivity) getActivity()).navigateAfterLogin(() -> {
                    ChangeActivity.toActivity(getActivity(), ActivityDeviceManager.class, true, false, false, 0);
                });
                break;
            default:
                break;
        }
    }

    private void resetConnectBtnFlag() {
        // TOAT: SIM
        //SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
        boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
        if ((home_simStatus.getSIMState() != Cons.READY || !bCPEWifiConnected) & home_simStatus != null) {
            m_bConnectPressd = false;
            m_bConnectReturn = false;
            return;
        }

        // TOAT: connect
        // WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
        if ((home_connectionState.getConnectionStatus() == Cons.CONNECTED || home_connectionState.getConnectionStatus() == Cons.DISCONNECTED) & home_connectionState != null) {
            if (m_bConnectReturn) {
                m_bConnectReturn = false;
            }
        }


    }

    private void showNetworkState() {
        // TOAT: SIM
        // SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
        if (Cons.READY != home_simStatus.getSIMState() & home_simStatus != null) {
            int nStatusId = R.string.Home_sim_invalid;
            if (Cons.ILLEGAL == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_sim_invalid;
                m_unlockSimBtn.setVisibility(View.GONE);
            } else if (Cons.NOWN == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_no_sim;
                m_unlockSimBtn.setVisibility(View.GONE);
            } else if (Cons.DETECTED == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_SimCard_Detected;
                m_unlockSimBtn.setVisibility(View.GONE);
            } else if (Cons.SIMLOCK == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_SimLock_Required;
                m_unlockSimBtn.setVisibility(View.GONE);
            } else if (Cons.PUK_TIMESOUT == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_PukTimes_UsedOut;
                m_unlockSimBtn.setVisibility(View.GONE);
            } else if (Cons.INITING == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_SimCard_IsIniting;
                m_unlockSimBtn.setVisibility(View.GONE);
            } else if (Cons.PIN_REQUIRED == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_pin_locked;
                m_unlockSimBtn.setVisibility(View.VISIBLE);
            } else if (Cons.PUK_REQUIRED == home_simStatus.getSIMState()) {
                nStatusId = R.string.Home_puk_locked;
                m_unlockSimBtn.setVisibility(View.VISIBLE);
            } else {
                nStatusId = R.string.Home_sim_invalid;
                m_unlockSimBtn.setVisibility(View.GONE);
            }

            if (Cons.PIN_REQUIRED == home_simStatus.getSIMState() || Cons.PUK_REQUIRED == home_simStatus.getSIMState()) {
                m_nosimcardLayout.setVisibility(View.GONE);
                m_simcardlockedLayout.setVisibility(View.VISIBLE);
                m_simcardlockedTextView.setText(nStatusId);
            } else {
                m_simOrServiceTextView.setText(nStatusId);
                m_simcardlockedLayout.setVisibility(View.GONE);
                m_nosimcardLayout.setVisibility(View.VISIBLE);
            }
            mConnectedView.setCenterTitle(strZeroConnDuration);
            m_connectLayout.setVisibility(View.GONE);

            return;
        }

        m_simcardlockedLayout.setVisibility(View.GONE);

        // TOAT: network
        // NetworkInfoModel curNetwork = BusinessManager.getInstance().getNetworkInfo();
        if (home_networkInfos.getNetworkType() == Cons.NOSERVER & home_networkInfos != null) {
            m_simOrServiceTextView.setText(R.string.home_no_service);
            m_nosimcardLayout.setVisibility(View.VISIBLE);
            m_connectLayout.setVisibility(View.GONE);
            mConnectedView.setCenterTitle(strZeroConnDuration);
            return;
        }

        if (home_networkInfos.getNetworkType() == Cons.UNKNOW & home_networkInfos != null) {
            m_simOrServiceTextView.setText(R.string.home_initializing);
            m_nosimcardLayout.setVisibility(View.VISIBLE);
            m_connectLayout.setVisibility(View.GONE);
            mConnectedView.setCenterTitle(strZeroConnDuration);
            return;
        }

        m_nosimcardLayout.setVisibility(View.GONE);
        m_connectToNetworkTextView.setText(home_networkInfos.getNetworkName());
        m_connectToNetworkTextView.setVisibility(View.VISIBLE);

        // TOAT: connect
        // WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
        if (!m_bConnectPressd & home_connectionState != null) {
            if (home_connectionState.getConnectionStatus() == Cons.CONNECTED) {
                statictime = home_connectionState.getConnectionTime();
                staticdata = (int) (home_connectionState.getDlBytes() + home_connectionState.getUlBytes());

            }
            if (home_connectionState.getConnectionStatus() == Cons.DISCONNECTED) {
            }
            if (home_connectionState.getConnectionStatus() == Cons.DISCONNECTED) {
                statictime = home_connectionState.getConnectionTime();
                staticdata = (int) (home_connectionState.getDlBytes() + home_connectionState.getUlBytes());
            }
            if (home_connectionState.getConnectionStatus() == Cons.CONNECTING) {
            }
        } else {
            if (home_connectionState.getConnectionStatus() == Cons.CONNECTED || home_connectionState.getConnectionStatus() == Cons.DISCONNECTING) {
            } else {
            }
        }

        int staticDataMB = staticdata / 1024 / 1024;
        mConnectedView.setCenterTitle(String.valueOf(staticDataMB));

    }

    private void showConnectBtnView() {

        // TOAT: SIM
        //SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
        if (home_simStatus != null) {
            if (home_simStatus.getSIMState() != Cons.READY) {
                mConnectedView.setCenterTitle(strZeroConnDuration);
                return;
            }

        }

        // TOAT: network
        //NetworkInfoModel curNetwork = BusinessManager.getInstance().getNetworkInfo();
        if (home_networkInfos != null & home_networkInfos.getNetworkType() == Cons.NOSERVER || home_networkInfos.getNetworkType() == Cons.UNKNOW) {
            mConnectedView.setCenterTitle(strZeroConnDuration);
            return;
        }

        // TOAT: connect
        // WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
        if (!m_bConnectPressd) {
            if (home_connectionState.getConnectionStatus() == Cons.CONNECTED & home_connectionState != null) {
                boolean logoutFlag = SharedPrefsUtil.getInstance(getActivity()).getBoolean(SettingAccountActivity.LOGOUT_FLAG, true);
                if (logoutFlag) {
                    m_connectLayout.setVisibility(View.VISIBLE);
                    m_connectedLayout.setVisibility(View.GONE);
                } else {
                    m_connectLayout.setVisibility(View.GONE);
                    m_connectedLayout.setVisibility(View.VISIBLE);
                }
                statictime = home_connectionState.getConnectionTime();
                staticdata = (int) (home_connectionState.getDlBytes() + home_connectionState.getUlBytes());
            }

            if (home_connectionState.getConnectionStatus() == Cons.DISCONNECTING & home_connectionState != null) {
                m_connectLayout.setVisibility(View.VISIBLE);
                m_connectedLayout.setVisibility(View.GONE);
            }

            if (home_connectionState.getConnectionStatus() == Cons.DISCONNECTED & home_connectionState != null) {
                m_connectLayout.setVisibility(View.VISIBLE);
                m_connectedLayout.setVisibility(View.GONE);
            }

            if (home_connectionState.getConnectionStatus() == Cons.CONNECTING & home_connectionState != null) {
                m_connectLayout.setVisibility(View.GONE);
                m_connectedLayout.setVisibility(View.VISIBLE);
            }
        } else {
            boolean logoutFlag = SharedPrefsUtil.getInstance(getActivity()).getBoolean(SettingAccountActivity.LOGOUT_FLAG, false);
            if (logoutFlag) {
                m_connectLayout.setVisibility(View.VISIBLE);
                m_connectedLayout.setVisibility(View.GONE);
            } else {
                m_connectLayout.setVisibility(View.GONE);
                m_connectedLayout.setVisibility(View.VISIBLE);
            }
            statictime = home_connectionState.getConnectionTime();
            staticdata = (int) (home_connectionState.getDlBytes() + home_connectionState.getUlBytes());
        }

        int staticDataMB = staticdata / 1024 / 1024;
        mConnectedView.setCenterTitle(String.valueOf(staticDataMB));

    }

    private void connectBtnClick() {
        if (LinkAppSettings.isLoginSwitchOff()) {
            connect();
        } else {
            // TOAT: login
            // UserLoginStatus status = BusinessManager.getInstance().getLoginStatus();
            if (home_loginStatu != null) {
                if (home_loginStatu.getState() == Cons.LOGOUT) {
                    m_autoLoginDialog.autoLoginAndShowDialog(new OnAutoLoginFinishedListener() {
                        public void onLoginSuccess() {
                            connect();
                        }

                        public void onLoginFailed(String error_code) {
                            loginerrorhandle(error_code);
                        }

                        @Override
                        public void onFirstLogin() {
                            m_loginDialog.showDialog(() -> {
                                connect();
                            });
                        }
                    });
                } else if (home_loginStatu.getState() == Cons.LOGIN & home_loginStatu != null) {
                    connect();
                } else {
                    PromptUserLogined();
                }
            }
            
        }
    }

    private void loginerrorhandle(String error_code) {
        if (error_code.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
            if (!FeatureVersionManager.getInstance().isSupportForceLogin()) {
                m_loginDialog.showOtherLogin();
            } else {
                ForceLoginSelectDialog.getInstance(getActivity()).showDialog(() -> {
                    m_ForceloginDlg.autoForceLoginAndShowDialog(new AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener() {
                        public void onLoginSuccess() {
                            connect();
                        }

                        public void onLoginFailed(String error_code) {
                            if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD)) {
                                SmartLinkV3App.getInstance().setForcesLogin(true);
                                ErrorDialog.getInstance(getActivity()).showDialog(getActivity().getString(R.string.login_psd_error_msg), new OnClickBtnRetry() {
                                    @Override
                                    public void onRetry() {
                                        m_loginDialog.showDialog(() -> connect());
                                    }
                                });
                            } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                                m_loginDialog.showTimeout();
                            }
                        }
                    });
                });
            }
        } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
            m_loginDialog.showTimeout();
        } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_USERNAME_OR_PASSWORD)) {
            ErrorDialog.getInstance(getActivity()).showDialog(getActivity().getString(R.string.login_psd_error_msg), new OnClickBtnRetry() {
                @Override
                public void onRetry() {
                    m_loginDialog.showDialog();
                }
            });
        }
    }

    private void connectedBtnClick() {
        getActivity().startActivity(new Intent(getActivity(), UsageActivity.class));
    }

    private void connect() {
        SharedPrefsUtil.getInstance(getActivity()).putBoolean(SettingAccountActivity.LOGOUT_FLAG, false);
        // TOAT: usagesetting
        //UsageSettingModel settings = BusinessManager.getInstance().getUsageSettings();
        //UsageSetting home_usageSetting = APIManager.getInstance().getUsageSetting();
        // TOAT: UsageRecordResult
        //UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();
        // TOAT: connect
        //WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();

        if (home_connectionState != null) {
            if ((home_connectionState.getConnectionStatus() == Cons.DISCONNECTED || home_connectionState.getConnectionStatus() == Cons.DISCONNECTING) ) {
                if (home_usageSetting != null & home_usageRecord != null) {
                    if (home_usageSetting.getAutoDisconnFlag() == Cons.ENABLE && home_usageRecord.getMonthlyPlan() > 0 ) {
                        if ((home_usageRecord.getHUseData() + home_usageRecord.getRoamUseData()) >= home_usageRecord.getMonthlyPlan()) {
                            String msgRes = getActivity().getString(R.string.home_usage_over_redial_message);
                            Toast.makeText(getActivity(), msgRes, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }
        }
       

        if (!m_bConnectPressd) {
            m_bConnectPressd = true;
        } else {
            return;
        }
        m_bConnectReturn = false;
        showNetworkState();
        showConnectBtnView();

        if ((home_connectionState.getConnectionStatus() == Cons.CONNECTED || home_connectionState.getConnectionStatus() == Cons.DISCONNECTING) & home_connectionState != null) {
            // TOAT: disconnect
            ApiEngine.disconnect();
        } else {
            // TOAT: to connect
            ApiEngine.connectto();
        }
    }


    private CommonErrorInfoDialog m_dialog_timeout_info;

    private void PromptUserLogined() {
        if (null == m_dialog_timeout_info) {
            m_dialog_timeout_info = CommonErrorInfoDialog.getInstance(getActivity());
        }
        m_dialog_timeout_info.showDialog(getActivity().getString(R.string.other_login_warning_title), getActivity().getResources().getString(R.string.login_login_time_used_out_msg));
    }

    private void showSignalAndNetworkType() {
        // TOAT: SIM
        // SIMState simStatus = BusinessManager.getInstance().getSimStatus().m_SIMState;
        if (home_simStatus.getSIMState() != Cons.READY & home_simStatus != null) {
            m_networkTypeTextView.setVisibility(View.GONE);
            m_networkLabelTextView.setVisibility(View.VISIBLE);
            m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
        } else {
            // TOAT: NETWORK
            // NetworkInfoModel curNetwork = BusinessManager.getInstance().getNetworkInfo();
            if (home_networkInfos != null) {
                if (home_networkInfos.getNetworkType() == Cons.NOSERVER) {
                    m_networkTypeTextView.setVisibility(View.GONE);
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
                    m_networkLabelTextView.setVisibility(View.VISIBLE);
                    return;
                }
                //show roaming
                if (home_networkInfos.getRoaming() == Cons.ROAMING)
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g_r);
                //show signal strength
                if (home_networkInfos.getSignalStrength() == Cons.LEVEL_0) {
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g_none);
                }
                if (home_networkInfos.getSignalStrength() == Cons.LEVEL_1)
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g1);
                if (home_networkInfos.getSignalStrength() == Cons.LEVEL_2)
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g2);
                if (home_networkInfos.getSignalStrength() == Cons.LEVEL_3)
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g3);
                if (home_networkInfos.getSignalStrength() == Cons.LEVEL_4)
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g4);
                if (home_networkInfos.getSignalStrength() == Cons.LEVEL_5)
                    m_signalImageView.setBackgroundResource(R.drawable.home_4g5);
                //show network type
                if (home_networkInfos.getNetworkType() == Cons.UNKNOW) {
                    m_networkTypeTextView.setVisibility(View.GONE);
                    m_networkLabelTextView.setVisibility(View.VISIBLE);
                }

                //2G
                if (home_networkInfos.getNetworkType() == Cons.EDGE || home_networkInfos.getNetworkType() == Cons.GPRS) {
                    m_networkTypeTextView.setVisibility(View.VISIBLE);
                    m_networkTypeTextView.setTypeface(typeFace);
                    m_networkTypeTextView.setText(R.string.home_network_type_2g);
                    m_networkTypeTextView.setTextColor(getActivity().getResources().getColor(R.color.mg_blue));
                }

                //3G
                if (home_networkInfos.getNetworkType() == Cons.HSPA || home_networkInfos.getNetworkType() == Cons.UMTS || home_networkInfos.getNetworkType() == Cons.HSUPA) {

                    m_networkTypeTextView.setVisibility(View.VISIBLE);
                    m_networkTypeTextView.setTypeface(typeFace);
                    m_networkTypeTextView.setText(R.string.home_network_type_3g);
                    m_networkTypeTextView.setTextColor(getActivity().getResources().getColor(R.color.mg_blue));
                }

                //3G+
                if (home_networkInfos.getNetworkType() == Cons.HSPA_PLUS || home_networkInfos.getNetworkType() == Cons.DC_HSPA_PLUS) {
                    m_networkTypeTextView.setVisibility(View.VISIBLE);
                    m_networkTypeTextView.setTypeface(typeFace);
                    m_networkTypeTextView.setText(R.string.home_network_type_3g_plus);
                    m_networkTypeTextView.setTextColor(getActivity().getResources().getColor(R.color.mg_blue));
                }

                //4G			
                if (home_networkInfos.getNetworkType() == Cons.LTE) {
                    m_networkTypeTextView.setVisibility(View.VISIBLE);
                    m_networkTypeTextView.setTypeface(typeFace);
                    m_networkTypeTextView.setText(R.string.home_network_type_4g);
                    m_networkTypeTextView.setTextColor(getActivity().getResources().getColor(R.color.mg_blue));
                }
            }
        }
    }

    // private void showBatteryState() {
    //
    //     if (!FeatureVersionManager.getInstance().isSupportApi("PowerManagement", "GetBatteryState")) {
    //         batteryView.setVisibility(View.GONE);
    //         return;
    //     }
    //     int nProgress;
    //
    //     // TOAT: BatteryState
    //     // BatteryInfo batteryinfo = BusinessManager.getInstance().getBatteryInfo();
    //     if (ConstValue.CHARGE_STATE_REMOVED == home_batteryState.getChg_state()) {
    //         nProgress = home_batteryState.getBatteryLevel();
    //         if (nProgress > 20 && nProgress <= 40) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery2);
    //         } else if ((nProgress > 40) && nProgress <= 60) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery3);
    //         } else if ((nProgress > 60) && nProgress <= 80) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery4);
    //         } else if ((nProgress > 80) && nProgress <= 100) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery5);
    //         } else {
    //             batteryView.setImageResource(R.drawable.home_ic_battery1);
    //         }
    //     } else if (ConstValue.CHARGE_STATE_CHARGING == home_batteryState.getChg_state()) {
    //         batteryView.setImageResource(R.drawable.home_ic_battery_charging);
    //     } else if (ConstValue.CHARGE_STATE_COMPLETED == home_batteryState.getChg_state()) {
    //         nProgress = home_batteryState.getBatteryLevel();
    //         if (nProgress > 20 && nProgress <= 40) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery2);
    //         } else if ((nProgress > 40) && nProgress <= 60) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery3);
    //         } else if ((nProgress > 60) && nProgress <= 80) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery4);
    //         } else if ((nProgress > 80) && nProgress <= 100) {
    //             batteryView.setImageResource(R.drawable.home_ic_battery5);
    //         } else {
    //             batteryView.setImageResource(R.drawable.home_ic_battery1);
    //         }
    //     } else if (ConstValue.CHARGE_STATE_ABORT == home_batteryState.getChg_state()) {
    //
    //     }
    //
    //     int batterLevel = home_batteryState.getBatteryLevel();
    //
    //     if (batterLevel <= 30) {
    //         circleProgress.setProgress(getActivity().getResources().getColor(R.color.circle_yellow));
    //     }
    //     circleProgress.setValue(batterLevel);
    //
    //     showBatteryOnProgressPosition(batterLevel);
    //     SharedPrefsUtil.getInstance(getActivity()).putInt(BATTERY_LEVEL, batterLevel);
    // }

    private void showBatteryOnProgressPosition(int progressValue) {
        int startProgressValue = SharedPrefsUtil.getInstance(getActivity()).getInt(BATTERY_LEVEL, 0);
        float startRotateValue = (float) (3.6 * startProgressValue);
        float rotateValue = (float) (3.6 * progressValue);

        AnimationSet animationSet = new AnimationSet(true);
        RotateAnimation animation1 = new RotateAnimation(-startRotateValue, -rotateValue, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //实例化RotateAnimation
        //以自身中心为圆心，旋转360度 正值为顺时针旋转，负值为逆时针旋转
        RotateAnimation animation2 = new RotateAnimation(startRotateValue, rotateValue, Animation.ABSOLUTE, 28, Animation.ABSOLUTE, -300);
        animationSet.addAnimation(animation1);
        animationSet.addAnimation(animation2);

        //设置动画插值器 被用来修饰动画效果,定义动画的变化率
        animationSet.setInterpolator(new LinearInterpolator());
        //设置动画执行时间
        animationSet.setDuration(1000);
        //动画执行完毕后是否停在结束时的角度上
        animationSet.setFillAfter(true);
        batteryView.startAnimation(animationSet);
    }

    private void showTrafficUsageView() {
        // TOAT: UsageRecordResult
        //UsageRecordResult m_UsageRecordResult = BusinessManager.getInstance().getUsageRecord();
        // TOAT:  UsageSetting
        //UsageSettingModel statistic = BusinessManager.getInstance().getUsageSettings();

        if (home_usageSetting.getMonthlyPlan() != 0 & home_usageSetting != null) {
            long hUseData = home_usageRecord.getHUseData();
            long hMonthlyPlan = home_usageSetting.getMonthlyPlan();
            long circleUseDataProgressValue = (hUseData * 100) / hMonthlyPlan;
            if (circleUseDataProgressValue >= 80) {
                mConnectedView.setWaveColor(getActivity().getResources().getColor(R.color.wave_yellow));
            } else {
                mConnectedView.setWaveColor(getActivity().getResources().getColor(R.color.circle_green));
            }
            if (circleUseDataProgressValue <= 100) {
                mConnectedView.setProgressValue((int) circleUseDataProgressValue - 8);
            } else {
                mConnectedView.setProgressValue(92);
            }
        } else {
            mConnectedView.setWaveColor(getActivity().getResources().getColor(R.color.circle_green));
            mConnectedView.setProgressValue(92);
        }
    }

    private void showAccessDeviceState() {
        ArrayList<ConnectedDeviceItemModel> connecedDeviceLstData = BusinessManager.getInstance().getConnectedDeviceList();
        m_accessnumTextView.setTypeface(typeFace);
        m_accessnumTextView.setText(String.format(Locale.ENGLISH, "%d", connecedDeviceLstData.size()));
        m_accessnumTextView.setTextColor(getActivity().getResources().getColor(R.color.mg_blue));
        m_accessImageView.setImageResource(R.drawable.home_ic_person_many);

        String strOfficial = this.getActivity().getString(R.string.access_lable);
        m_accessstatusTextView.setText(strOfficial);
    }


    @Override
    public void batteryStatus() {
        // showBatteryState();
    }

    @Override
    public void deviceConnect() {
        showAccessDeviceState();
    }

    @Override
    public void networkRollRequest() {
        showSignalAndNetworkType();
        showNetworkState();
        showConnectBtnView();
    }

    @Override
    public void simRollRequest() {
        showSignalAndNetworkType();
        resetConnectBtnFlag();
        showNetworkState();
        showConnectBtnView();
    }

    @Override
    public void wanConnect(boolean isOk) {
        if (isOk) {
            m_bConnectReturn = true;
        } else {
            //operation fail
            m_bConnectPressd = false;
            showNetworkState();
            showConnectBtnView();
        }
    }

    @Override
    public void wanRollRequest() {
        resetConnectBtnFlag();
        showNetworkState();
        showConnectBtnView();
    }

    @Override
    public void wificonnect() {
        resetConnectBtnFlag();
        m_connectLayout.setVisibility(View.VISIBLE);
        m_connectedLayout.setVisibility(View.GONE);
        showConnectBtnView();
    }
}
