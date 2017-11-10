package com.alcatel.smartlinkv3.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.sharing.SDcardStatus;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.mediaplayer.proxy.AllShareProxy;
import com.alcatel.smartlinkv3.mediaplayer.proxy.IDeviceChangeListener;
import com.alcatel.smartlinkv3.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.alcatel.smartlinkv3.mediaplayer.util.ThumbnailLoader;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.rx.ui.LoginRxActivity;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.MorePopWindow;
import com.alcatel.smartlinkv3.ui.dialog.PinDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.view.ViewHome;
import com.alcatel.smartlinkv3.ui.view.ViewIndex;
import com.alcatel.smartlinkv3.ui.view.ViewMicroSD;
import com.alcatel.smartlinkv3.ui.view.ViewSetting;
import com.alcatel.smartlinkv3.ui.view.ViewSms;
import com.alcatel.smartlinkv3.ui.view.ViewUsage;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.TimerHelper;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;

import org.cybergarage.upnp.Device;

import java.net.SocketTimeoutException;
import java.util.List;

public class MainActivity extends BaseActivity implements OnClickListener, IDeviceChangeListener {
    private int m_preButton = 0;
    private int m_nNewCount = 0;

    private ViewFlipper m_viewFlipper;

    private TextView m_homeBtn;
    private TextView m_microsdBtn;
    private TextView m_usageBtn;
    private RelativeLayout m_smsBtn;
    private TextView m_settingBtn;

    private TextView m_smsTextView;
    private TextView m_newSmsTextView;


    private TextView m_titleTextView = null;
    private Button m_Btnbar = null;

    private ViewHome m_homeView = null;
    private ViewUsage m_usageView = null;
    private ViewSms m_smsView = null;

    private ViewSetting m_settingView = null;
    private ViewMicroSD m_microsdView = null;

    public static DisplayMetrics m_displayMetrics = new DisplayMetrics();

    private PinDialog m_dlgPin = null;
    private PukDialog m_dlgPuk = null;
    private ErrorDialog m_dlgError = null;

    private int pageIndex = 0;
    static boolean m_blLogout = false;
    static boolean m_blkickoff_Logout = false;

    public static String PAGE_TO_VIEW_HOME = "com.alcatel.smartlinkv3.toPageViewHome";

    private DMSDeviceBrocastFactory mBrocastFactory;
    private AllShareProxy mAllShareProxy;
    private ThumbnailLoader thumbnailLoader;
    private static Device mDevice;
    private TimerHelper loginStateTimer;
    private TimerHelper heartBeatTimer;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        this.getWindowManager().getDefaultDisplay().getMetrics(m_displayMetrics);
        RelativeLayout rl_top = (RelativeLayout) findViewById(R.id.main_layout_top);
        RelativeLayout.LayoutParams rl_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rl_params.height = (m_displayMetrics.heightPixels * 9) / 100;
        rl_top.setLayoutParams(rl_params);
        m_homeBtn = (TextView) this.findViewById(R.id.main_home);
        m_homeBtn.setOnClickListener(this);
        m_microsdBtn = (TextView) this.findViewById(R.id.main_microsd);
        m_microsdBtn.setOnClickListener(this);
        m_usageBtn = (TextView) this.findViewById(R.id.main_usage);
        m_usageBtn.setOnClickListener(this);
        m_smsBtn = (RelativeLayout) this.findViewById(R.id.tab_sms_layout);
        m_smsBtn.setOnClickListener(this);
        m_settingBtn = (TextView) this.findViewById(R.id.main_setting);
        m_settingBtn.setOnClickListener(this);
        m_viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
        m_smsTextView = (TextView) this.findViewById(R.id.main_sms);
        m_newSmsTextView = (TextView) this.findViewById(R.id.new_sms_count);
        m_titleTextView = (TextView) this.findViewById(R.id.main_title);
        m_Btnbar = (Button) this.findViewById(R.id.btnbar);
        m_Btnbar.setOnClickListener(this);

        addView();
        setMainBtnStatus(R.id.main_home);
        showView(ViewIndex.VIEW_HOME);
        updateTitleUI(ViewIndex.VIEW_HOME);
        pageIndex = ViewIndex.VIEW_HOME;

        m_dlgPin = PinDialog.getInstance(this);
        m_dlgPuk = PukDialog.getInstance(this);
        m_dlgError = ErrorDialog.getInstance(this);
        Button m_unlockSimBtn = (Button) m_homeView.getView().findViewById(R.id.unlock_sim_button);
        m_unlockSimBtn.setOnClickListener(this);

        RelativeLayout m_accessDeviceLayout = (RelativeLayout) m_homeView.getView().findViewById(R.id.access_num_layout);
        m_accessDeviceLayout.setOnClickListener(this);
        OnResponseAppWidget();

        mAllShareProxy = AllShareProxy.getInstance(this);
        mBrocastFactory = new DMSDeviceBrocastFactory(this);
        mBrocastFactory.registerListener(this);

        thumbnailLoader = new ThumbnailLoader(this);
        showMicroView();
        OtherUtils.verifyPermisson(this);// 申請權限

        // loginStateTimer();// 登陸狀態定時器
        heartbeatTimer();// 心跳定時器
    }


    /**
     * 心跳定時器
     */
    private void heartbeatTimer() {
        heartBeatTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                API.get().heartBeat(new MySubscriber() {
                    @Override
                    protected void onSuccess(Object result) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketTimeoutException) {
                            System.out.println("ma_main_rx: SocketTimeoutException");
                            ChangeActivity.toActivity(MainActivity.this, RefreshWifiActivity.class, false, true, false, 0);
                        } else {
                            System.out.println("ma_main_rx: onError");
                            ChangeActivity.toActivity(MainActivity.this, LoginRxActivity.class, false, true, false, 0);
                        }
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        System.out.println("ma_main_rx: onResultError");
                        ToastUtil_m.show(MainActivity.this, getString(R.string.login_kickoff_logout_successful));
                        ChangeActivity.toActivity(MainActivity.this, LoginRxActivity.class, false, true, false, 0);
                    }
                });
            }
        };
        heartBeatTimer.start(3000);
    }

    /**
     * 登陸狀態定時器
     */
    private void loginStateTimer() {
        loginStateTimer = new TimerHelper(this) {
            @Override
            public void doSomething() {
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState result) {
                        // 檢測發現登出--> 跳轉登陸介面
                        if (result.getState() == Cons.LOGOUT) {
                            System.out.println("ma_main_rx: onSuccess loginstate");
                            ToastUtil_m.show(MainActivity.this, getString(R.string.login_kickoff_logout_successful));
                            ChangeActivity.toActivity(MainActivity.this, LoginRxActivity.class, false, true, false, 0);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketTimeoutException) {
                            System.out.println("ma_main_rx: SocketTimeoutException loginstate");
                            ChangeActivity.toActivity(MainActivity.this, RefreshWifiActivity.class, false, true, false, 0);
                        } else {
                            System.out.println("ma_main_rx: onError loginstate");
                            ChangeActivity.toActivity(MainActivity.this, LoginRxActivity.class, false, true, false, 0);
                        }
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        System.out.println("ma_main_rx: onResultError");
                        ToastUtil_m.show(MainActivity.this, getString(R.string.login_kickoff_logout_successful));
                        ChangeActivity.toActivity(MainActivity.this, LoginRxActivity.class, false, true, false, 0);
                    }

                });
            }
        };
        loginStateTimer.start(2000, 3000);
    }


    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PIN_REQUEST));
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.SIM_UNLOCK_PUK_REQUEST));
        this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.USER_LOGOUT_REQUEST));
        this.registerReceiver(m_msgReceiver2, new IntentFilter(PAGE_TO_VIEW_HOME));
        this.registerReceiver(m_msgReceiver2, new IntentFilter(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));

        m_homeView.onResume();
        m_usageView.onResume();
        m_smsView.onResume();
        m_settingView.onResume();
        m_microsdView.onResume();

        updateBtnState();
        toPageHomeWhenPinSimNoOk();
        showMicroView();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(m_msgReceiver);
        } catch (Exception e) {
            Log.v("ma_main", e.getMessage());
        }
        m_homeView.onPause();
        m_usageView.onPause();
        m_smsView.onPause();
        m_settingView.onPause();
        m_microsdView.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        destroyDialogs();
        destroyProxy();
    }

    /**
     * 停止定時器
     */
    private void stopTimer() {
        if (loginStateTimer != null) {
            loginStateTimer.stop();
        }
        if (heartBeatTimer != null) {
            heartBeatTimer.stop();
        }
    }

    /**
     * 清理代理
     */
    private void destroyProxy() {
        m_homeView.onDestroy();
        m_usageView.onDestroy();
        m_smsView.onDestroy();
        m_settingView.onDestroy();
        m_microsdView.onDestroy();
        mBrocastFactory.unRegisterListener();
        mAllShareProxy.exitSearch();
        thumbnailLoader.clearCache();
    }

    /**
     * 清理對話框
     */
    private void destroyDialogs() {
        m_dlgPin.destroyDialog();
        m_dlgPuk.destroyDialog();
        m_dlgError.destroyDialog();
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        super.onBroadcastReceive(context, intent);

        if (intent.getAction().equalsIgnoreCase(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (BaseResponse.RESPONSE_OK == nResult && strErrorCode.length() == 0) {
                simRollRequest();
            }
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SIM_UNLOCK_PIN_REQUEST)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (BaseResponse.RESPONSE_OK == nResult && strErrorCode.length() == 0) {
                m_dlgPin.onEnterPinResponse(true);
            } else {
                m_dlgPin.onEnterPinResponse(false);
            }
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SIM_UNLOCK_PUK_REQUEST)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (BaseResponse.RESPONSE_OK == nResult && strErrorCode.length() == 0) {
                m_dlgPuk.onEnterPukResponse(true);
            } else {
                m_dlgPuk.onEnterPukResponse(false);
            }
        }

        if (intent.getAction().equalsIgnoreCase(MessageUti.USER_LOGOUT_REQUEST)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (BaseResponse.RESPONSE_OK == nResult && strErrorCode.length() == 0) {
                if (m_blLogout) {
                    String strInfo = getString(R.string.login_logout_successful);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }

                if (m_blkickoff_Logout) {
                    String strInfo = getString(R.string.login_kickoff_logout_successful);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }
                m_blLogout = false;
                m_blkickoff_Logout = false;
            }
        }

        if (intent.getAction().equalsIgnoreCase(PAGE_TO_VIEW_HOME)) {
            homeBtnClick();
        }

        if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
            String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
            if (nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0) {
                if (BusinessMannager.getInstance().getDlnaSettings().getDlnaStatus() > 0) {
                    mAllShareProxy.startSearch();
                } else {
                    mAllShareProxy.exitSearch();
                }

            }
        }
    }

    /**
     * SIM卡輪詢操作
     */
    private void simRollRequest() {
        updateBtnState();
        SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
        toPageHomeWhenPinSimNoOk();

        if (sim.m_SIMState == SIMState.PinRequired) {
            // close PUK dialog
            if (null != m_dlgPuk && PukDialog.m_isShow)
                m_dlgPuk.closeDialog();
            // set the remain times
            if (null != m_dlgPin)
                m_dlgPin.updateRemainTimes(sim.m_nPinRemainingTimes);

            if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
                if (!PinDialog.m_isShow) {
                    m_dlgPin.showDialog(sim.m_nPinRemainingTimes, () -> {
                        String strMsg = getString(R.string.pin_error_waring_title);
                        m_dlgError.showDialog(strMsg, () -> m_dlgPin.showDialog());
                    });
                } else {
                    m_dlgPin.onSimStatusReady(sim);
                }
            }
        } else if (sim.m_SIMState == SIMState.PukRequired) {// puk
            // close PIN dialog
            if (null != m_dlgPin && PinDialog.m_isShow)
                m_dlgPin.closeDialog();

            // set the remain times
            if (null != m_dlgPuk)
                m_dlgPuk.updateRemainTimes(sim.m_nPukRemainingTimes);

            if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
                if (!PukDialog.m_isShow) {
                    m_dlgPuk.showDialog(sim.m_nPukRemainingTimes, () -> {
                        String strMsg = getString(R.string.puk_error_waring_title);
                        m_dlgError.showDialog(strMsg, () -> m_dlgPuk.showDialog());
                    });
                } else {
                    m_dlgPuk.onSimStatusReady(sim);
                }
            }
        } else {
            closePinAndPukDialog();
        }
    }

    public void updateNewSmsUI(int nNewSmsCount) {
        m_nNewCount = nNewSmsCount;
        int nActiveBtnId = m_preButton;
        int nDrawable = nActiveBtnId == R.id.tab_sms_layout ? R.drawable.main_sms_no_new_active : R.drawable.main_sms_no_new_grey;
        Drawable d = getResources().getDrawable(nDrawable);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        m_smsTextView.setCompoundDrawables(null, d, null, null);
        int nTextColor = nActiveBtnId == R.id.tab_sms_layout ? R.color.color_blue : R.color.color_grey;
        m_smsTextView.setTextColor(this.getResources().getColor(nTextColor));

        if (nNewSmsCount <= 0) {
            m_newSmsTextView.setVisibility(View.GONE);
        } else if (nNewSmsCount < 10) {
            m_newSmsTextView.setVisibility(View.VISIBLE);
            m_newSmsTextView.setText(String.valueOf(nNewSmsCount));
            nDrawable = R.drawable.tab_sms_new;
            m_newSmsTextView.setBackgroundResource(nDrawable);
        } else {
            m_newSmsTextView.setVisibility(View.VISIBLE);
            m_newSmsTextView.setText("");
            nDrawable = R.drawable.tab_sms_new_9_plus;
            m_newSmsTextView.setBackgroundResource(nDrawable);
        }
    }

    private void updateBtnState() {
        SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
        if (simState.m_SIMState == SIMState.Accessable) {
            m_smsBtn.setEnabled(true);
        } else {
            m_smsBtn.setEnabled(false);
        }
    }

    private void toPageHomeWhenPinSimNoOk() {
        SimStatusModel simState = BusinessMannager.getInstance().getSimStatus();
        if (simState.m_SIMState != SIMState.Accessable) {
            if (m_preButton == R.id.tab_sms_layout) {
                setMainBtnStatus(R.id.main_home);
                showView(ViewIndex.VIEW_HOME);
                updateTitleUI(ViewIndex.VIEW_HOME);
            }

            unlockSimBtnClick(false);
        }
    }

    private void closePinAndPukDialog() {
        if (m_dlgPin != null)
            m_dlgPin.closeDialog();

        if (m_dlgPuk != null)
            m_dlgPuk.closeDialog();

        if (m_dlgError != null)
            m_dlgError.closeDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_home:
                homeBtnClick();
                break;
            case R.id.main_usage:
                usageBtnClick();
                break;
            case R.id.tab_sms_layout:
                smsBtnClick();
                break;
            case R.id.main_setting:
                settingBtnClick();
                break;
            case R.id.main_microsd:
                microsdBtnClick();
                break;
            case R.id.btnbar:
                go2Click();
                break;
            case R.id.unlock_sim_button:
                unlockSimBtnClick(true);
                break;
            case R.id.access_num_layout:
                ChangeActivity.toActivity(this, ActivityDeviceManager.class, false, false, false, 0);
                break;
            default:
                break;
        }
    }

    private void go2Click() {
        if (this.pageIndex == ViewIndex.VIEW_USAGE) {
            moreBtnClick();
        } else if (this.pageIndex == ViewIndex.VIEW_SMS) {
            editBtnClick();
        }
    }

    private void homeBtnClick() {
        if (m_preButton == R.id.main_home) {
            return;
        }
        setMainBtnStatus(R.id.main_home);
        showView(ViewIndex.VIEW_HOME);
        updateTitleUI(ViewIndex.VIEW_HOME);
        pageIndex = ViewIndex.VIEW_HOME;
    }

    private void usageBtnClick() {
        if (m_preButton == R.id.main_usage) {
            return;
        }
        go2UsageView();
    }

    private void go2UsageView() {
        SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
        if (simStatus.m_SIMState == SIMState.Accessable) {
            setMainBtnStatus(R.id.main_usage);
            showView(ViewIndex.VIEW_USAGE);
            updateTitleUI(ViewIndex.VIEW_USAGE);
            pageIndex = ViewIndex.VIEW_USAGE;
        }
    }

    private void smsBtnClick() {
        if (m_preButton == R.id.tab_sms_layout) {
            return;
        }
        go2SmsView();
    }

    private void go2SmsView() {
        SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
        if (simStatus.m_SIMState == SIMState.Accessable) {
            setMainBtnStatus(R.id.tab_sms_layout);
            showView(ViewIndex.VIEW_SMS);
            updateTitleUI(ViewIndex.VIEW_SMS);
            pageIndex = ViewIndex.VIEW_SMS;
        }
    }

    private void settingBtnClick() {
        if (m_preButton == R.id.main_setting) {
            return;
        }
        go2SettingView();
    }

    private void go2SettingView() {
        setMainBtnStatus(R.id.main_setting);
        showView(ViewIndex.VIEW_SETTINGE);
        updateTitleUI(ViewIndex.VIEW_SETTINGE);
        pageIndex = ViewIndex.VIEW_SETTINGE;
    }

    private void microsdBtnClick() {
        if (m_preButton == R.id.main_microsd) {
            return;
        }
        go2MicroSDView();
    }

    private void go2MicroSDView() {
        SDcardStatus m_sdcardstatus = BusinessMannager.getInstance().getSDCardStatus();
        if (m_sdcardstatus.SDcardStatus > 0) {
            if ((FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetDLNASettings")) || (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900"))) {
                BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET, null);
            }

            if (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetFtpStatus")) {
                BusinessMannager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_FTP_SETTING_REQUSET, null);
            }

            setMainBtnStatus(R.id.main_microsd);
            showView(ViewIndex.VIEW_MICROSD);
            updateTitleUI(ViewIndex.VIEW_MICROSD);
            pageIndex = ViewIndex.VIEW_MICROSD;
        } else {
            String strInfo = getString(R.string.microsd_no_sdcard);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
        }
    }

    private void addView() {
        m_homeView = new ViewHome(this);
        m_viewFlipper.addView(m_homeView.getView(), ViewIndex.VIEW_HOME, m_viewFlipper.getLayoutParams());

        m_usageView = new ViewUsage(this);
        m_viewFlipper.addView(m_usageView.getView(), ViewIndex.VIEW_USAGE, m_viewFlipper.getLayoutParams());

        m_microsdView = new ViewMicroSD(this);
        m_viewFlipper.addView(m_microsdView.getView(), ViewIndex.VIEW_MICROSD, m_viewFlipper.getLayoutParams());

        m_smsView = new ViewSms(this);
        m_viewFlipper.addView(m_smsView.getView(), ViewIndex.VIEW_SMS, m_viewFlipper.getLayoutParams());

        m_settingView = new ViewSetting(this);
        m_viewFlipper.addView(m_settingView.getView(), ViewIndex.VIEW_SETTINGE, m_viewFlipper.getLayoutParams());

    }

    public void showView(int viewIndex) {
        m_viewFlipper.setDisplayedChild(viewIndex);
    }

    public void updateTitleUI(int viewIndex) {
        if (viewIndex == ViewIndex.VIEW_HOME) {
            m_titleTextView.setText(R.string.main_home);
            m_Btnbar.setVisibility(View.GONE);
            setMainBtnStatus(R.id.main_home);
        }
        if (viewIndex == ViewIndex.VIEW_USAGE) {
            m_titleTextView.setText(R.string.main_usage);
            m_Btnbar.setVisibility(View.VISIBLE);
            m_Btnbar.setBackgroundResource(R.drawable.actionbar_more_icon);
            setMainBtnStatus(R.id.main_usage);
        }
        if (viewIndex == ViewIndex.VIEW_SMS) {
            m_titleTextView.setText(R.string.sms_title);
            m_Btnbar.setVisibility(View.VISIBLE);
            m_Btnbar.setBackgroundResource(R.drawable.actionbar_edit_icon);
            setMainBtnStatus(R.id.tab_sms_layout);
        }
        if (viewIndex == ViewIndex.VIEW_SETTINGE) {
            m_titleTextView.setText(R.string.main_setting);
            m_Btnbar.setVisibility(View.GONE);
            setMainBtnStatus(R.id.main_setting);
        }
        if (viewIndex == ViewIndex.VIEW_MICROSD) {
            m_titleTextView.setText(R.string.main_sdsharing);
            m_Btnbar.setVisibility(View.GONE);
        }
    }

    private void setMainBtnStatus(int nActiveBtnId) {
        m_preButton = nActiveBtnId;
        int nDrawable = nActiveBtnId == R.id.main_home ? R.drawable.main_home_active : R.drawable.main_home_grey;
        Drawable d = getResources().getDrawable(nDrawable);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        m_homeBtn.setCompoundDrawables(null, d, null, null);
        int nTextColor = nActiveBtnId == R.id.main_home ? R.color.color_blue : R.color.color_grey;
        m_homeBtn.setTextColor(this.getResources().getColor(nTextColor));

        nDrawable = nActiveBtnId == R.id.main_usage ? R.drawable.main_usage_active : R.drawable.main_usage_grey;
        d = getResources().getDrawable(nDrawable);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        m_usageBtn.setCompoundDrawables(null, d, null, null);
        nTextColor = nActiveBtnId == R.id.main_usage ? R.color.color_blue : R.color.color_grey;
        m_usageBtn.setTextColor(this.getResources().getColor(nTextColor));

        updateNewSmsUI(m_nNewCount);

        nDrawable = nActiveBtnId == R.id.main_setting ? R.drawable.main_setting_active : R.drawable.main_setting_grey;
        d = getResources().getDrawable(nDrawable);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        m_settingBtn.setCompoundDrawables(null, d, null, null);
        nTextColor = nActiveBtnId == R.id.main_setting ? R.color.color_blue : R.color.color_grey;
        m_settingBtn.setTextColor(this.getResources().getColor(nTextColor));

        nDrawable = nActiveBtnId == R.id.main_microsd ? R.drawable.main_microssd_active : R.drawable.main_microssd_grey;
        d = getResources().getDrawable(nDrawable);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        m_microsdBtn.setCompoundDrawables(null, d, null, null);
        nTextColor = nActiveBtnId == R.id.main_microsd ? R.color.color_blue : R.color.color_grey;
        m_microsdBtn.setTextColor(this.getResources().getColor(nTextColor));
    }

    public static void setLogoutFlag(boolean blLogout) {
        m_blLogout = blLogout;
    }

    public static void setKickoffLogoutFlag(boolean blLogout) {
        m_blkickoff_Logout = blLogout;
    }

    private void moreBtnClick() {
        MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
        morePopWindow.showPopupWindow(m_Btnbar);
    }

    private void editBtnClick() {
        Intent intent = new Intent();
        intent.setClass(this, ActivityNewSms.class);
        this.startActivity(intent);
    }

    private void unlockSimBtnClick(boolean blCancelUserClose) {
        SimStatusModel sim = BusinessMannager.getInstance().getSimStatus();
        if (SIMState.PinRequired == sim.m_SIMState) {
            if (blCancelUserClose) {
                m_dlgPin.cancelUserClose();
                m_dlgPuk.cancelUserClose();
            }
            ShowPinDialog();
        } else if (SIMState.PukRequired == sim.m_SIMState) {
            if (blCancelUserClose) {
                m_dlgPin.cancelUserClose();
                m_dlgPuk.cancelUserClose();
            }
            ShowPukDialog();
        }
    }

    private void ShowPinDialog() {
        // close PUK dialog
        if (null != m_dlgPuk && PukDialog.m_isShow) {
            m_dlgPuk.closeDialog();
        }

        SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
        // set the remain times
        if (null != m_dlgPin) {
            m_dlgPin.updateRemainTimes(simStatus.m_nPinRemainingTimes);
        }
        if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
            if (!PinDialog.m_isShow) {
                m_dlgPin.showDialog(simStatus.m_nPinRemainingTimes, () -> {
                    String strMsg = getString(R.string.pin_error_waring_title);
                    m_dlgError.showDialog(strMsg, () -> m_dlgPin.showDialog());
                });
            }
        }
    }

    private void ShowPukDialog() {
        // close PIN dialog
        if (null != m_dlgPin && PinDialog.m_isShow) {
            m_dlgPin.closeDialog();
        }

        SimStatusModel simStatus = BusinessMannager.getInstance().getSimStatus();
        // set the remain times
        if (null != m_dlgPuk) {
            m_dlgPuk.updateRemainTimes(simStatus.m_nPukRemainingTimes);
        }
        if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
            if (!PukDialog.m_isShow) {
                m_dlgPuk.showDialog(simStatus.m_nPukRemainingTimes, () -> {
                    String strMsg = getString(R.string.puk_error_waring_title);
                    m_dlgError.showDialog(strMsg, () -> m_dlgPuk.showDialog());
                });
            }
        }
    }

    private void go2SettingPowerSavingActivity() {
        go2SettingView();
        Intent intent = new Intent(this, SettingPowerSavingActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        OnResponseAppWidget();
    }


    private void OnResponseAppWidget() {
        Intent it = getIntent();
        int nPage = it.getIntExtra("com.alcatel.smartlinkv3.business.openPage", 100);
        switch (nPage) {
            case 1:
                homeBtnClick();
                break;
            case 2:
                smsBtnClick();
                break;
            case 3:
                go2SettingPowerSavingActivity();
                break;
            case 4:
                usageBtnClick();
                break;
        }
    }

    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {
        updateDeviceList();
    }

    private String getServerAddress(Context ctx) {
        WifiManager wifi_service = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        return Formatter.formatIpAddress(dhcpInfo.gateway);
    }

    private void updateDeviceList() {
        List<Device> list = mAllShareProxy.getDMSDeviceList();
        String str1;
        String str2;
        for (Device tmp : list) {
            str1 = tmp.getLocation().substring(7);
            str2 = str1.substring(0, str1.indexOf(":"));
            if (str2.equalsIgnoreCase(getServerAddress(this))) {
                mDevice = tmp;
            }
        }

        mAllShareProxy.setDMSSelectedDevice(mDevice);
        Intent msdIntent = new Intent(ViewMicroSD.DLNA_DEVICES_SUCCESS);
        sendBroadcast(msdIntent);
    }

    public void showMicroView() {
        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y900")) {
            m_microsdBtn.setVisibility(View.VISIBLE);
            return;
        }

        if (BusinessMannager.getInstance().getFeatures().getDeviceName().equalsIgnoreCase("Y858")) {
            m_microsdBtn.setVisibility(View.GONE);
            return;
        }

        boolean bSupport = FeatureVersionManager.getInstance().isSupportModule("Sharing");
        if (bSupport) {
            if ((FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetFtpStatus")) || (FeatureVersionManager.getInstance().isSupportApi("Sharing", "GetDLNASettings"))) {
                m_microsdBtn.setVisibility(View.VISIBLE);
            }
        } else {
            m_microsdBtn.setVisibility(View.GONE);
        }
    }
}
