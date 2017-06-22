package com.alcatel.smartlinkv3.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.FeatureVersionManager;
import com.alcatel.smartlinkv3.business.model.WanConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.update.DeviceNewVersionInfo;
import com.alcatel.smartlinkv3.business.update.DeviceUpgradeStateInfo;
import com.alcatel.smartlinkv3.common.CommonUtil;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.common.ENUM.PinState;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog;
import com.alcatel.smartlinkv3.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.smartlinkv3.ui.dialog.InquireDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog.OnInquireApply;
import com.alcatel.smartlinkv3.ui.dialog.InquireReplaceDialog.OnInquireCancle;
import com.alcatel.smartlinkv3.ui.dialog.PinStateDialog;
import com.alcatel.smartlinkv3.ui.dialog.PinStateDialog.OnPINError;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog;
import com.alcatel.smartlinkv3.ui.dialog.PukDialog.OnPUKError;

import static com.alcatel.smartlinkv3.ui.view.ViewSetting.ISDEVICENEWVERSION;

public class SettingDeviceActivity extends BaseActivity implements OnClickListener {

    private TextView    m_tv_title = null;
    private ImageButton m_ib_back  = null;
    private TextView    m_tv_back  = null;

    private FrameLayout m_system_info      = null;
    private FrameLayout m_upgrade_system   = null;
    private FrameLayout m_backup_and_reset = null;
    private FrameLayout m_power_saving     = null;
    private FrameLayout m_pin_code         = null;
    private FrameLayout m_web_version      = null;
    private FrameLayout m_restart          = null;
    private FrameLayout m_power_off        = null;

    private ProgressBar m_pb_waiting = null;

    private FrameLayout m_pincode_editor        = null;
    private ScrollView  m_device_menu_container = null;

    private PinStateDialog m_dlgPin   = null;
    private PukDialog      m_dlgPuk   = null;
    private ErrorDialog    m_dlgError = null;

    private TextView m_switch_button = null;

    private boolean isPinRequired;
    private boolean m_edit_pin_showing = false;

    private boolean m_pin_state;
    private ENUM.PinState m_requested_pinState = ENUM.PinState.NotAvailable;

    private boolean   m_blFirst          = true;
    private ImageView m_hiddable_divider = null;

    private TextView m_pin_notice = null;

    private ENUM.PinState m_PrePinState = ENUM.PinState.NotAvailable;

    private Dialog         mUpgradeDialog;
    private RelativeLayout mWaittingContainer;
    private RelativeLayout mUpgradeContainer;
    private TextView       mVersion;
    private TextView       mUpgrade;
    private        boolean m_blHasNewFirmware   = false;//是否为更新版本的事件
    private static String  m_strNewFirmwareInfo = "";
    private        int     m_nUpdradeFWProgress = 0;
    private        boolean m_blUpdating         = false;
    private Dialog      mUpgradeProgressDialog;
    private TextView    mUpgradeProgressTv;
    private ProgressBar mUpgradeProgressBar;
    private TextView mUpgradeFlage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_setting_device);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);

        controlTitlebar();
        initUi();

//        checkNewVersion();//检查新版本

    }

    private void controlTitlebar() {
        m_tv_title = (TextView) findViewById(R.id.tv_title_title);
        m_tv_title.setText(R.string.setting_device);
        //back button and text
        m_ib_back = (ImageButton) findViewById(R.id.ib_title_back);
        m_tv_back = (TextView) findViewById(R.id.tv_title_back);
        m_ib_back.setOnClickListener(this);
        m_tv_back.setOnClickListener(this);
    }

    private void initUi() {
        m_system_info = (FrameLayout) findViewById(R.id.device_system_info);
        m_system_info.setOnClickListener(this);
        m_upgrade_system = (FrameLayout) findViewById(R.id.device_upgrade_system);
        m_upgrade_system.setOnClickListener(this);
        mUpgradeFlage = (TextView) findViewById(R.id.device_upgrade_flag);
        //获取是否有升级
        boolean isDeviceNewVersion = SharedPrefsUtil.getInstance(this).getBoolean(ISDEVICENEWVERSION, false);
        if (isDeviceNewVersion){
            mUpgradeFlage.setVisibility(View.VISIBLE);
        }else{
            mUpgradeFlage.setVisibility(View.GONE);
        }
        m_backup_and_reset = (FrameLayout) findViewById(R.id.device_backup_and_reset);
        m_backup_and_reset.setOnClickListener(this);
        //		m_power_saving = (FrameLayout) findViewById(R.id.device_power_saving);
        //		m_power_saving.setOnClickListener(this);
        m_pin_code = (FrameLayout) findViewById(R.id.device_pin_code);
        m_pin_code.setOnClickListener(this);
        m_web_version = (FrameLayout) findViewById(R.id.device_web_version);
        m_web_version.setOnClickListener(this);
        m_hiddable_divider = (ImageView) findViewById(R.id.device_hidable_divider);
        if (BusinessManager.getInstance().getSystemInfo().getWebUiVersion().length() == 0) {
            m_hiddable_divider.setVisibility(View.GONE);
            m_web_version.setVisibility(View.GONE);
        }
        //		m_webversion_desc.setText(webVersion);
        m_restart = (FrameLayout) findViewById(R.id.device_restart);
        m_restart.setOnClickListener(this);
        //		m_power_off = (FrameLayout) findViewById(R.id.device_power_off);
        //		m_power_off.setOnClickListener(this);

        m_pb_waiting = (ProgressBar) findViewById(R.id.pb_device_waiting_progress);

        m_switch_button = (TextView) findViewById(R.id.btn_default_switch);

        m_pincode_editor = (FrameLayout) findViewById(R.id.setting_device_pincode_editor);
        m_pincode_editor.setVisibility(View.GONE);
        m_pincode_editor.setOnClickListener(this);

        m_dlgPin = PinStateDialog.getInstance(this);
        m_dlgPuk = PukDialog.getInstance(this);
        m_dlgError = ErrorDialog.getInstance(this);

        m_device_menu_container = (ScrollView) findViewById(R.id.device_menu_container);
        m_device_menu_container.setVisibility(View.VISIBLE);

        ShowWaiting(false);

        isPinRequired = false;
        if (BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.PinEnableVerified
                || BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.EnableButNotVerified
                || BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.RequirePUK) {
            m_switch_button.setBackgroundResource(R.drawable.general_btn_on);
            m_requested_pinState = ENUM.PinState.Disable;
        } else if (BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.Disable) {
            m_switch_button.setBackgroundResource(R.drawable.general_btn_off);
            m_requested_pinState = ENUM.PinState.PinEnableVerified;
        }
        m_PrePinState = BusinessManager.getInstance().getSimStatus().m_PinState;

        m_pin_notice = (TextView) findViewById(R.id.setting_device_pincode_editor_notice);
        m_pin_notice.setVisibility(View.GONE);
    }

    private void ShowWaiting(boolean blShow) {
        if (blShow) {
            m_pb_waiting.setVisibility(View.VISIBLE);
        } else {
            m_pb_waiting.setVisibility(View.GONE);
        }
        m_system_info.setEnabled(!blShow);
        //		m_power_off.setEnabled(!blShow);
        m_restart.setEnabled(!blShow);
        m_backup_and_reset.setEnabled(!blShow);
        m_ib_back.setEnabled(!blShow);
        m_tv_back.setEnabled(!blShow);
        m_upgrade_system.setEnabled(!blShow);
        //		m_power_saving.setEnabled(!blShow);
        m_pin_code.setEnabled(!blShow);
        m_web_version.setEnabled(!blShow);
    }


    private void simRollRequest() {
        final SimStatusModel sim = BusinessManager.getInstance().getSimStatus();

        if (sim.m_PinState != m_PrePinState && sim.m_PinState != ENUM.PinState.RequirePUK) {
            m_PrePinState = sim.m_PinState;
            if (BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.PinEnableVerified
                    || BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.EnableButNotVerified
                    || BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.RequirePUK) {
                m_switch_button.setBackgroundResource(R.drawable.general_btn_on);
                m_requested_pinState = ENUM.PinState.Disable;
            } else if (BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.Disable) {
                m_switch_button.setBackgroundResource(R.drawable.general_btn_off);
                m_requested_pinState = ENUM.PinState.PinEnableVerified;
            }
            closePinAndPukDialog();
            return;
        }
        if (isPinRequired && sim.m_nPinRemainingTimes > 0) {
            // close PUK dialog
            if (null != m_dlgPuk && PukDialog.m_isShow)
                m_dlgPuk.closeDialog();
            // set the remain times
            if (null != m_dlgPin)
                m_dlgPin.updateRemainTimes(sim.m_nPinRemainingTimes);

            if (null != m_dlgPin && !m_dlgPin.isUserClose()) {
                if (!PinStateDialog.m_isShow) {
                    m_dlgPin.showDialog(sim.m_nPinRemainingTimes,
                            new OnPINError() {
                                @Override
                                public void onPinError() {
                                    String strMsg = getString(R.string.pin_error_waring_title);
                                    m_dlgError.showDialog(strMsg,
                                            new OnClickBtnRetry() {
                                                @Override
                                                public void onRetry() {
                                                    m_dlgPin.showDialog();
                                                }
                                            });
                                }
                            });
                } else {
                    m_dlgPin.onSimStatusReady(sim);
                }
            }
        } else if (sim.m_SIMState == SIMState.PukRequired) {// puk
            // close PIN dialog
            m_pin_notice.setVisibility(View.VISIBLE);
            if (null != m_dlgPin && PinStateDialog.m_isShow)
                m_dlgPin.closeDialog();

            final InquireReplaceDialog inquireDlg = new InquireReplaceDialog(
                    SettingDeviceActivity.this);
            inquireDlg.setCancelDisabled();
            inquireDlg.m_titleTextView.setText(R.string.dialog_warning_title);
            inquireDlg.m_contentTextView
                    .setText(R.string.dialog_warning_error_pin_code_error_3times);
            inquireDlg.m_confirmBtn.setText(R.string.confirm);
            inquireDlg.showDialog(new OnInquireApply() {

                @Override
                public void onInquireApply() {
                    inquireDlg.closeDialog();
                    // set the remain times
                    if (null != m_dlgPuk)
                        m_dlgPuk.updateRemainTimes(sim.m_nPukRemainingTimes);

                    if (null != m_dlgPuk && !m_dlgPuk.isUserClose()) {
                        if (!PukDialog.m_isShow) {
                            m_dlgPuk.showDialog(sim.m_nPukRemainingTimes,
                                    new OnPUKError() {

                                        @Override
                                        public void onPukError() {
                                            String strMsg = getString(R.string.puk_error_waring_title);
                                            m_dlgError.showDialog(strMsg,
                                                    new OnClickBtnRetry() {

                                                        @Override
                                                        public void onRetry() {
                                                            m_dlgPuk.showDialog();
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            m_dlgPuk.onSimStatusReady(sim);
                        }
                    }
                }
            }, new OnInquireCancle() {

                @Override
                public void onInquireCancel() {
                    inquireDlg.closeDialog();
                    closePinAndPukDialog();
                }

            });

        } else {
            closePinAndPukDialog();
            if (BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.PinEnableVerified
                    || BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.EnableButNotVerified
                    || BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.RequirePUK) {
                m_switch_button.setBackgroundResource(R.drawable.general_btn_on);
                m_requested_pinState = ENUM.PinState.Disable;
            } else if (BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.Disable) {
                m_switch_button.setBackgroundResource(R.drawable.general_btn_off);
                m_requested_pinState = ENUM.PinState.PinEnableVerified;
            }
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


    private void ShowPinDialog() {
        // close PUK dialog
        if (null != m_dlgPuk && PukDialog.m_isShow) {
            m_dlgPuk.closeDialog();
        }

        SimStatusModel simStatus = BusinessManager.getInstance()
                .getSimStatus();
        // set the remain times
        if (null != m_dlgPin) {
            m_dlgPin.updateRemainTimes(simStatus.m_nPinRemainingTimes);
        }
        if (null != m_dlgPin) {
            if (!PinStateDialog.m_isShow) {
                m_dlgPin.showDialog(simStatus.m_nPinRemainingTimes,
                        new OnPINError() {

                            @Override
                            public void onPinError() {
                                String strMsg = getString(R.string.pin_error_waring_title);
                                m_dlgError.showDialog(strMsg,
                                        new OnClickBtnRetry() {

                                            @Override
                                            public void onRetry() {
                                                m_dlgPin.showDialog();
                                            }
                                        });
                            }
                        });
            }
        }
    }

    private void ShowPukDialog() {
        // close PIN dialog
        if (null != m_dlgPin && PinStateDialog.m_isShow) {
            m_dlgPin.closeDialog();
        }
        SimStatusModel simStatus = BusinessManager.getInstance()
                .getSimStatus();
        // set the remain times
        if (null != m_dlgPuk) {
            m_dlgPuk.updateRemainTimes(simStatus.m_nPukRemainingTimes);
        }
        if (null != m_dlgPuk) {
            if (!PukDialog.m_isShow) {
                m_dlgPuk.showDialog(simStatus.m_nPukRemainingTimes,
                        new OnPUKError() {

                            @Override
                            public void onPukError() {
                                String strMsg = getString(R.string.puk_error_waring_title);
                                m_dlgError.showDialog(strMsg,
                                        new OnClickBtnRetry() {

                                            @Override
                                            public void onRetry() {
                                                m_dlgPuk.showDialog();
                                            }
                                        });

                            }
                        });
            }
        }
    }


    private void goToSystemInfoPage() {
        Intent intent = new Intent(this, SystemInfoActivity.class);
        startActivity(intent);
    }

    private void goToBackupSettingPage() {
        Intent intent = new Intent(this, SettingBackupRestoreActivity.class);
        startActivity(intent);
    }

    private void goToPowerSettingPage() {
        Intent intent = new Intent(this, SettingPowerSavingActivity.class);
        startActivity(intent);
    }

    private void goToUpgradeSettingPage() {
        Intent intent = new Intent(this, SettingUpgradeActivity.class);
        intent.putExtra("First", m_blFirst);
        m_blFirst = false;
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int nID = v.getId();
        switch (nID) {
            case R.id.tv_title_back:
            case R.id.ib_title_back:
                onBtnBack();
                break;
            case R.id.device_system_info:
                goToSystemInfoPage();
                break;
            case R.id.device_upgrade_system:
                //			goToUpgradeSettingPage();
                showUpgradeDialog();
                break;
            case R.id.device_backup_and_reset:
                goToBackupSettingPage();
                break;
            //		case R.id.device_power_saving:
            //			goToPowerSettingPage();
            //			break;
            case R.id.device_pin_code:
                SimStatusModel simStatus0 = BusinessManager.getInstance().getSimStatus();
                if (simStatus0.m_PinState == PinState.EnableButNotVerified) {
                    String strInfo = getString(R.string.home_pin_locked_notice);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                    return;
                }
                onBtnPincodeSetting();
                break;
            case R.id.device_web_version:
                String strTemp = "http://" + BusinessManager.getInstance().getServerAddress();
                CommonUtil.openWebPage(this, strTemp);
                break;
            case R.id.device_restart:
                onBtnRestart();
                ShowWaiting(true);
                break;
            //		case R.id.device_power_off:
            //			onBtnPowerOff();
            //			ShowWaiting(true);
            //			break;
            case R.id.setting_device_pincode_editor:
                //			onDoneEditPincodeSetting();
                //			m_dlgPin.cancelUserClose();
                //			m_dlgPuk.cancelUserClose();
                WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
                if (internetConnState.m_connectionStatus != ConnectionStatus.Disconnected) {
                    if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
                        String strInfo = getString(R.string.setting_network_try_again);
                        Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        String strInfo = getString(R.string.setting_network_disconnect_first);
                        Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                SimStatusModel simStatus = BusinessManager.getInstance().getSimStatus();
                if (simStatus.m_PinState == PinState.EnableButNotVerified) {
                    String strInfo = getString(R.string.home_pin_locked_notice);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (simStatus.m_SIMState == SIMState.Accessable ||
                            simStatus.m_SIMState == SIMState.PinRequired ||
                            simStatus.m_SIMState == SIMState.PukRequired) {
                        if (simStatus.m_nPinRemainingTimes == 0) {
                            ShowPukDialog();
                        } else if (simStatus.m_nPinRemainingTimes > 0) {
                            ShowPinDialog();
                        }
                    } else {
                        String strInfo = getString(R.string.home_sim_not_accessible);
                        Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showUpgradeDialog() {
        mUpgradeDialog = new Dialog(this, R.style.UpgradeMyDialog);
        mUpgradeDialog.setCanceledOnTouchOutside(false);
        mUpgradeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout deleteDialogLLayout = (RelativeLayout) View.inflate(this,
                R.layout.dialog_upgrade, null);

        mWaittingContainer = (RelativeLayout) deleteDialogLLayout.findViewById(R.id.upgrade_waiting);
        mWaittingContainer.setVisibility(View.VISIBLE);
        mUpgradeContainer = (RelativeLayout) deleteDialogLLayout.findViewById(R.id.upgrade_container);
        mUpgradeContainer.setVisibility(View.GONE);

        ImageView cancel = (ImageView) deleteDialogLLayout.findViewById(R.id.upgrade_cancel);
        mVersion = (TextView) deleteDialogLLayout.findViewById(R.id.upgrade_version);
        mUpgrade = (TextView) deleteDialogLLayout.findViewById(R.id.upgrade_confirm);

        mUpgradeDialog.setContentView(deleteDialogLLayout);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissUpgradeDialog();
            }
        });
        mUpgradeDialog.show();

        //获取最新版本信息
        getNewVersion();
    }

    private void dismissUpgradeDialog() {
        if (mUpgradeDialog != null && mUpgradeDialog.isShowing()) {
            mUpgradeDialog.dismiss();
        }
    }

    private void dismissUpgradeProgressDialog() {
        if (mUpgradeProgressDialog != null && mUpgradeProgressDialog.isShowing()) {
            mUpgradeProgressDialog.dismiss();
        }
    }

    private void getNewVersion() {
        m_blUpdating = false;
        WanConnectStatusModel status = BusinessManager.getInstance().getWanConnectStatus();
        ENUM.ConnectionStatus result = status.m_connectionStatus;
        if (result != ENUM.ConnectionStatus.Connected) {
            showCheckFwWaiting(false);
            mVersion.setText(R.string.setting_upgrade_no_connection);
            m_strNewFirmwareInfo = "";
            mUpgrade.setVisibility(View.GONE);
        } else {
            mUpgrade.setVisibility(View.VISIBLE);
            onBtnAppCheck();
        }
    }

    private void showCheckFwWaiting(boolean isWait) {
        if (mWaittingContainer == null && mUpgradeContainer == null){
            return;
        }
        if (isWait) {
            mWaittingContainer.setVisibility(View.VISIBLE);
            mUpgradeContainer.setVisibility(View.GONE);
        } else {
            mWaittingContainer.setVisibility(View.GONE);
            mUpgradeContainer.setVisibility(View.VISIBLE);
        }
    }

    private void onBtnAppCheck() {
        if (m_blHasNewFirmware) {
            //进行版本升级
            showCheckFwWaiting(false);
            onBtnFirmwareCheck();
        } else {
            checkNewVersion();
        }
    }

    //检查版本
    private void checkNewVersion() {
        BusinessManager.getInstance().sendRequestMessage(
                MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION, null);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void onDoneEditPincodeSetting() {
        //		m_device_menu_container.setVisibility(View.VISIBLE);
        //		m_pincode_editor.setVisibility(View.GONE);
        if (m_pin_state) {
            m_pin_state = false;
            m_switch_button.setBackgroundResource(R.drawable.general_btn_off);
        } else {
            m_pin_state = true;
            m_switch_button.setBackgroundResource(R.drawable.general_btn_on);
        }

    }

    private void onBtnPincodeSetting() {
        m_edit_pin_showing = true;
        if (BusinessManager.getInstance().getSimStatus().m_SIMState == SIMState.NoSim ||
                BusinessManager.getInstance().getSimStatus().m_SIMState == SIMState.InvalidSim ||
                BusinessManager.getInstance().getSimStatus().m_SIMState == SIMState.SimCardIsIniting) {
            String strInfo = getString(R.string.home_sim_not_accessible);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        m_device_menu_container.setVisibility(View.GONE);
        m_pincode_editor.setVisibility(View.VISIBLE);
        if (BusinessManager.getInstance().getSimStatus().m_nPinRemainingTimes <= 0) {
            m_pin_notice.setVisibility(View.VISIBLE);
        }
    }

    private void onBtnPowerOff() {
        BusinessManager.getInstance().
                sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF, null);
    }

    private void onBtnRestart() {
        BusinessManager.getInstance().
                sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_REBOOT, null);
    }

    private void onBtnReset() {
        BusinessManager.getInstance().
                sendRequestMessage(MessageUti.SYSTEM_SET_DEVICE_RESET, null);
    }

    @Override
    public void onBackPressed() {
        if (!m_edit_pin_showing) {
            super.onBackPressed();
        } else {
            m_device_menu_container.setVisibility(View.VISIBLE);
            m_pincode_editor.setVisibility(View.GONE);
            m_edit_pin_showing = false;
            m_pin_notice.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        m_bNeedBack = false;
        super.onResume();
        registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.SYSTEM_SET_DEVICE_REBOOT));
        registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.SYSTEM_SET_DEVICE_RESET));
        registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF));
        registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUEST));

        this.registerReceiver(m_msgReceiver, new IntentFilter(
                MessageUti.SIM_UNLOCK_PUK_REQUEST));
        this.registerReceiver(m_msgReceiver, new IntentFilter(
                MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET));
        this.registerReceiver(m_msgReceiver, new IntentFilter(
                MessageUti.SIM_CHANGE_PIN_STATE_REQUEST));
        this.registerReceiver(m_msgReceiver, new IntentFilter(
                MessageUti.USER_LOGOUT_REQUEST));
        this.registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE));
        this.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));

        /*--------------- add start 2016.12.30 ---------------*/
        registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION));

        registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.UPDATE_SET_DEVICE_START_UPDATE));

        registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.UPDATE_GET_DEVICE_UPGRADE_STATE));

        /*--------------- add end 2016.12.30 ---------------*/

        int nUpgradeStatus = BusinessManager.getInstance().getNewFirmwareInfo().getState();
        if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)) {
            m_blFirst = false;
            //			changeUpgradeFlag(ITEM_UPGRADE_SETTING,true);
        } else {
            //			changeUpgradeFlag(ITEM_UPGRADE_SETTING,false);
        }

        BusinessManager.getInstance().sendRequestMessage(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUEST, null);
        ShowWaiting(true);
    }


    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        super.onBroadcastReceive(context, intent);

        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();

        /*--------------- add start 2016.12.30 ---------------*/
        if (MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION.equals(action)) {
            if (ok) {
                //do nothing

            } else {
                showCheckFwWaiting(false);
                //                String strNew = getString(R.string.setting_upgrade_set_check_new_version_failed);
                mVersion.setText(R.string.setting_upgrade_check_failed);
                mUpgrade.setText(R.string.setting_upgrade_btn_check);
            }
        }

        if (MessageUti.UPDATE_GET_DEVICE_NEW_VERSION.equals(action)) {
            if (ok) {
                updateNewDeviceInfo(true);
            } else {
                showCheckFwWaiting(false);
                String strNew = getString(R.string.setting_upgrade_check_failed);
                setNewDeviceVersion(strNew);
            }
        }

        //正在更新驱动设备
        if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_START_UPDATE)) {
            if (ok) {
                //do nothing
            } else {
                dismissUpgradeProgressDialog();
                Toast.makeText(this, R.string.setting_upgrade_start_update_failed, Toast.LENGTH_SHORT).show();
            }
        }

        //更新的进度状态
        if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_UPGRADE_STATE)) {
            if (ok) {
                //do nothing
                DeviceUpgradeStateInfo info = BusinessManager.getInstance().getUpgradeStateInfo();
                ENUM.EnumDeviceUpgradeStatus status = ENUM.EnumDeviceUpgradeStatus.build(info.getStatus());
                int nProgress = info.getProcess();
                m_pb_waiting.setProgress(nProgress);
                if (ENUM.EnumDeviceUpgradeStatus.DEVICE_UPGRADE_NOT_START == status) {
                    dismissUpgradeProgressDialog();
                    Toast.makeText(this, R.string.setting_upgrade_not_start, Toast.LENGTH_SHORT).show();
                } else if (ENUM.EnumDeviceUpgradeStatus.DEVICE_UPGRADE_COMPLETE == status) {
                    dismissUpgradeProgressDialog();
                    if (!FeatureVersionManager.getInstance().isSupportApi("System", "AccessSqliteDB")) {
                        Toast.makeText(this, R.string.setting_upgrade_complete, Toast.LENGTH_SHORT).show();
                    } else {
                        BusinessManager.getInstance().sendRequestMessage(MessageUti
                                .UPDATE_SET_DEVICE_START_FOTA_UPDATE, null);
                        Toast.makeText(this, R.string.setting_upgrade_complete, Toast.LENGTH_SHORT).show();
                    }
                } else if (ENUM.EnumDeviceUpgradeStatus.DEVICE_UPGRADE_UPDATING == status) {
                    m_nUpdradeFWProgress = info.getProcess();
                    String strProgress = m_nUpdradeFWProgress + "%";
                    mUpgradeProgressTv.setText(strProgress);
                }
            } else {
                dismissUpgradeProgressDialog();
                Toast.makeText(this, R.string.setting_upgrade_get_update_state_failed, Toast.LENGTH_SHORT).show();
            }
        }
        /*--------------- add end 2016.12.30 ---------------*/

        if (intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_REBOOT)) {
            String strTost = getString(R.string.setting_reboot_failed);
            if (ok) {
                strTost = getString(R.string.setting_reboot_success);
            }
            ShowWaiting(false);
            Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
        }

        if (intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_GET_SYSTEM_INFO_REQUEST)) {
            ShowWaiting(false);
        }


        if (intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_RESET)) {
            String strTost = getString(R.string.setting_reset_failed);
            if (ok) {
                strTost = getString(R.string.setting_reset_success);
            }
            ShowWaiting(false);
            Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
        }

        if (intent.getAction().equalsIgnoreCase(MessageUti.SYSTEM_SET_DEVICE_POWER_OFF)) {
            String strTost = getString(R.string.setting_power_off_failed);
            if (ok) {
                strTost = getString(R.string.setting_power_off_success);
            }
            ShowWaiting(false);
            Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
        }

        if (intent.getAction().equalsIgnoreCase(
                MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
            if (ok) {
                simRollRequest();
            } else {
                closePinAndPukDialog();
                String strTost = getString(R.string.unknown_error);
                Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
            }
        } else if (intent.getAction().equalsIgnoreCase(
                MessageUti.SIM_CHANGE_PIN_STATE_REQUEST)) {

            if (ok) {
                m_dlgPin.onEnterPinResponse(true);
                isPinRequired = false;
            } else {
                m_dlgPin.onEnterPinResponse(false);
                isPinRequired = true;
            }
        } else if (intent.getAction().equalsIgnoreCase(
                MessageUti.SIM_UNLOCK_PUK_REQUEST)) {

            if (ok) {
                m_pin_notice.setVisibility(View.GONE);
                m_dlgPuk.onEnterPukResponse(true);
            } else {
                m_dlgPuk.onEnterPukResponse(false);
            }
        }

        if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE)) {
            if (ok) {
            } else {

                Toast.makeText(getBaseContext(), R.string.setting_upgrade_stop_error, Toast.LENGTH_SHORT).show();
            }
        }

		/*if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)) {
            int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
			String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
			if(nResult == BaseResponse.RESPONSE_OK && strErrorCode.length() == 0){
				int nUpgradeStatus = BusinessManager.getInstance().getNewFirmwareInfo().getState();
				if(EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)){
					m_blFirst = false;
//					changeUpgradeFlag(ITEM_UPGRADE_SETTING,true);
				}else {
//					changeUpgradeFlag(ITEM_UPGRADE_SETTING,false);
				}
			}
		}*/


    }

    /*--------------- add start 2017.1.20 by haodi.liang ---------------*/
    private void updateNewDeviceInfo(boolean blNeedBackupNewVersionInfo) {
        DeviceNewVersionInfo info = BusinessManager.getInstance().getNewFirmwareInfo();
        int nState = info.getState();
        EnumDeviceCheckingStatus eStatus = EnumDeviceCheckingStatus.build(nState);
        if (EnumDeviceCheckingStatus.DEVICE_CHECKING == eStatus) {
            //检查中
            showCheckFwWaiting(true);
            return;
        } else if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == eStatus) {
            //EnumDeviceCheckingStatus.DEVICE_NO_NEW_VERSION == eStatus
            //发现新版本
            if (mUpgrade == null){
                mUpgradeFlage.setVisibility(View.VISIBLE);
                return;
            }
            mUpgradeFlage.setVisibility(View.VISIBLE);
            m_blHasNewFirmware = true;
            String strNew = getString(R.string.setting_upgrade_new_device_version);
            strNew = strNew + " " + info.getVersion();
            if (blNeedBackupNewVersionInfo) {
                m_strNewFirmwareInfo = strNew;
            }
            mUpgrade.setVisibility(View.VISIBLE);
            mUpgrade.setText(getString(R.string.setting_upgrade));
            mUpgrade.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置更新的事件
                    dismissUpgradeDialog();
                    onBtnFirmwareCheck();
                }
            });
            showCheckFwWaiting(false);
            setNewDeviceVersion(m_strNewFirmwareInfo);
            return;
        } else if (EnumDeviceCheckingStatus.DEVICE_NO_NEW_VERSION == eStatus) {
            //没有新版本
            if (mUpgrade == null){
                return;
            }
            Toast.makeText(getApplicationContext(), getString(R.string.setting_upgrade_no_new_version), Toast
                    .LENGTH_SHORT).show();
            showCheckFwWaiting(false);
            String strCurFWVersion = getString(R.string.setting_upgrade_device_version);
            strCurFWVersion += BusinessManager.getInstance().getSystemInfo().getSwVersion();
            if (blNeedBackupNewVersionInfo) {
                m_strNewFirmwareInfo = strCurFWVersion;
            }
        } else if (EnumDeviceCheckingStatus.DEVICE_NO_CONNECT == eStatus) {
            //无连接
            showCheckFwWaiting(false);
            String strNew = getString(R.string.setting_upgrade_no_connection);
            if (blNeedBackupNewVersionInfo) {
                m_strNewFirmwareInfo = strNew;
            }
        } else if (EnumDeviceCheckingStatus.SERVICE_NOT_AVAILABLE == eStatus) {
            //设备不可用
            showCheckFwWaiting(false);
            String strNew = getString(R.string.setting_upgrade_not_available);
            if (blNeedBackupNewVersionInfo) {
                m_strNewFirmwareInfo = strNew;
            }
        } else if (EnumDeviceCheckingStatus.DEVICE_CHECK_ERROR == eStatus) {
            //检查出错
            showCheckFwWaiting(false);
            String strNew = getString(R.string.setting_upgrade_check_error);
            if (blNeedBackupNewVersionInfo) {
                m_strNewFirmwareInfo = strNew;
            }
        } else {
            showCheckFwWaiting(false);
        }

        if (mUpgrade == null){
            mUpgradeFlage.setVisibility(View.GONE);
        }else{
            showCheckButton();
            setNewDeviceVersion(m_strNewFirmwareInfo);
        }
    }

    private void showCheckButton() {
        mUpgrade.setVisibility(View.VISIBLE);
        mUpgrade.setText(getString(R.string.setting_upgrade_btn_check));
        mUpgrade.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置为检查更新的事件
                getNewVersion();
            }
        });
    }

    private void setNewDeviceVersion(String m_strNewFirmwareInfo) {
        mVersion.setText(m_strNewFirmwareInfo);
    }

    private void onBtnFirmwareCheck() {
        dismissUpgradeDialog();//先关闭更新提示dialog
        if (m_blHasNewFirmware) {
            m_blUpdating = true;
            m_nUpdradeFWProgress = 0;
            final com.alcatel.smartlinkv3.ui.dialog.InquireDialog inquireDlg = new com.alcatel.smartlinkv3.ui.dialog
                    .InquireDialog(this);
            inquireDlg.m_titleTextView.setText(R.string.setting_upgrade_btn_upgrade);
            inquireDlg.m_contentTextView.setGravity(Gravity.LEFT);
            inquireDlg.m_contentTextView
                    .setText(R.string.setting_upgrade_firmware_warning);
            inquireDlg.m_contentDescriptionTextView.setText("");
            inquireDlg.m_confirmBtn
                    .setBackgroundResource(R.drawable.selector_common_button);
            inquireDlg.m_confirmBtn.setText(R.string.ok);
            inquireDlg.showDialog(new InquireDialog.OnInquireApply() {

                @Override
                public void onInquireApply() {
                    //upgrade firmware
                    inquireDlg.closeDialog();
                    dismissUpgradeDialog();
                    BusinessManager.getInstance().sendRequestMessage(
                            MessageUti.UPDATE_SET_DEVICE_START_UPDATE, null);
                    //弹出更新进度
                    showUpgradeProgressDialog();
                }
            });
        } else {
            BusinessManager.getInstance().sendRequestMessage(
                    MessageUti.UPDATE_SET_CHECK_DEVICE_NEW_VERSION, null);
            showCheckFwWaiting(true);

        }
    }

    private void showUpgradeProgressDialog() {
        mUpgradeProgressDialog = new Dialog(this, R.style.UpgradeMyDialog);
        DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && KeyEvent.ACTION_UP==event.getAction()) {
                    if (!CommonUtil.isFastClick()){
                        onBtnBack();
                        return true;
                    }else{
                        return false;
                    }
                } else {
                    return false;
                }
            }
        };
        mUpgradeProgressDialog.setOnKeyListener(keylistener);
        mUpgradeProgressDialog.setCancelable(false);
        mUpgradeProgressDialog.setCanceledOnTouchOutside(false);
        mUpgradeProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout upgradProgressLayout = (RelativeLayout) View.inflate(this,
                R.layout.dialog_upgrade_progress, null);

        mUpgradeProgressTv = (TextView) upgradProgressLayout.findViewById(R.id.upgrade_progress_tv);
        mUpgradeProgressBar = (ProgressBar) upgradProgressLayout.findViewById(R.id.upgrade_progressbar);

        mUpgradeProgressDialog.setContentView(upgradProgressLayout);
        mUpgradeProgressDialog.show();
    }

    //停止更新
    private void showStopUpdateDialog() {
        final InquireDialog inquireDlg =  new InquireDialog(this);
        inquireDlg.m_titleTextView.setText(R.string.setting_upgrade_btn_upgrade);
        inquireDlg.m_contentTextView.setGravity(Gravity.LEFT);
        inquireDlg.m_contentTextView
                .setText(R.string.setting_upgrade_firmware_warning);
        inquireDlg.m_contentDescriptionTextView.setText("");
        inquireDlg.m_confirmBtn
                .setBackgroundResource(R.drawable.selector_common_button);
        inquireDlg.m_confirmBtn.setText(R.string.ok);
        inquireDlg.showDialog(new InquireDialog.OnInquireApply() {

            @Override
            public void onInquireApply() {
                //upgrade firmware
                dismissUpgradeProgressDialog();
                inquireDlg.closeDialog();
                BusinessManager.getInstance().sendRequestMessage(
                        MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE, null);
                SettingDeviceActivity.this.finish();
            }
        });
    }

    private void onBtnBack() {
        if (mUpgradeProgressDialog != null && mUpgradeProgressDialog.isShowing() && m_blUpdating) {
            showStopUpdateDialog();
        } else {
            SettingDeviceActivity.this.finish();
        }
    }


    /*--------------- add end 2017.1.20 by haodi.liang ---------------*/

}
