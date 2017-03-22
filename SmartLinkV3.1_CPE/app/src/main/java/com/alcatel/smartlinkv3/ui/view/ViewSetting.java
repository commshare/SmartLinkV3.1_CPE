package com.alcatel.smartlinkv3.ui.view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.SettingAboutActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingBackupRestoreActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingDeviceActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingNetworkActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingPowerSavingActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingShareActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingUpgradeActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingWifiActivity;

import java.util.ArrayList;
import java.util.List;


public class ViewSetting extends BaseViewImpl implements View.OnClickListener {

    boolean isFtpSupported  = false;
    boolean isDlnaSupported = false;
    private boolean  isSharingSupported  = false;
    private boolean m_blFirst = true;
    private settingBroadcast m_receiver;
    private int              upgradeStatus;

    public static final String ISDEVICENEWVERSION = "IS_DEVICE_NEW_VERSION";
    private RelativeLayout mLoginPassword;
    private RelativeLayout mMobileNetwork;
    private RelativeLayout mEthernetWan;
    private RelativeLayout mSharingService;
    private RelativeLayout mFirmwareUpgrade;
    private RelativeLayout mRestart;
    private RelativeLayout mBackup;
    private RelativeLayout mAbout;


    private void registerReceiver() {
        m_context.registerReceiver(m_receiver, new IntentFilter(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE));
        m_context.registerReceiver(m_receiver,
                new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
        m_context.registerReceiver(m_receiver,
                new IntentFilter(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));
        m_context.registerReceiver(m_receiver,
                new IntentFilter(MessageUti.SHARING_GET_FTP_SETTING_REQUSET));
    }

    public ViewSetting(Context context) {
        super(context);
        init();
        initUI();
        m_receiver = new settingBroadcast();
    }

    private void initUI() {
        mLoginPassword.setOnClickListener(this);
        mMobileNetwork.setOnClickListener(this);
        mEthernetWan.setOnClickListener(this);
        mSharingService.setOnClickListener(this);
        mFirmwareUpgrade.setOnClickListener(this);
        mRestart.setOnClickListener(this);
        mBackup.setOnClickListener(this);
        mAbout.setOnClickListener(this);
    }

    @Override
    protected void init() {
        m_view = LayoutInflater.from(m_context).inflate(R.layout.view_setting,
                null);
        mLoginPassword = (RelativeLayout) m_view.findViewById(R.id.setting_login_password);
        mMobileNetwork = (RelativeLayout) m_view.findViewById(R.id.setting_mobile_network);
        mEthernetWan = (RelativeLayout) m_view.findViewById(R.id.setting_ethernet_wan);
        mSharingService = (RelativeLayout) m_view.findViewById(R.id.setting_sharing_service);
        mFirmwareUpgrade = (RelativeLayout) m_view.findViewById(R.id.setting_firmware_upgrade);
        mRestart = (RelativeLayout) m_view.findViewById(R.id.setting_restart);
        mBackup = (RelativeLayout) m_view.findViewById(R.id.setting_backup);
        mAbout = (RelativeLayout) m_view.findViewById(R.id.setting_about);
    }

    @Override
    public void onResume() {
        registerReceiver();
    }

    //获取更新标志
    private void getUpgradeTag() {
        int nUpgradeStatus = BusinessManager.getInstance().getNewFirmwareInfo().getState();
        BusinessManager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET, null);
        BusinessManager.getInstance().sendRequestMessage(MessageUti.SHARING_GET_FTP_SETTING_REQUSET, null);
        if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)) {
            //DEVICE_NO_NEW_VERSION
            m_blFirst = false;
            //保存升级标志到本地SP
            SharedPrefsUtil.getInstance(m_context).putBoolean(ISDEVICENEWVERSION, true);
        } else {
            //保存升级标志到本地SP
            SharedPrefsUtil.getInstance(m_context).putBoolean(ISDEVICENEWVERSION, false);
        }
    }

    @Override
    public void onPause() {
        try {
            m_context.unregisterReceiver(m_receiver);
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
    }

    private List<SettingItem> getData(Context context) {
        List<SettingItem> list = new ArrayList<SettingItem>();

        SettingItem item = new SettingItem(context.getString(R.string.setting_account), false);
        list.add(item);

        item = new SettingItem(context.getString(R.string.setting_network), false);
        list.add(item);

        if (isSharingSupported) {
            item = new SettingItem(context.getString(R.string.setting_share), false);
            list.add(item);
        }

        upgradeStatus = BusinessManager.getInstance().getNewFirmwareInfo().getState();
        if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(upgradeStatus)) {
            //DEVICE_NEW_VERSION
            item = new SettingItem(context.getString(R.string.setting_device), true);
            list.add(item);
            //保存升级标志到本地SP
            SharedPrefsUtil.getInstance(context).putBoolean(ISDEVICENEWVERSION, true);
        } else {
            item = new SettingItem(context.getString(R.string.setting_device), false);
            list.add(item);
            //保存升级标志到本地SP
            SharedPrefsUtil.getInstance(context).putBoolean(ISDEVICENEWVERSION, false);
        }

        item = new SettingItem(context.getString(R.string.setting_about), false);
        list.add(item);

        return list;
    }

    private void goToWifiSettingPage() {
        Intent intent = new Intent(m_context, SettingWifiActivity.class);
        m_context.startActivity(intent);
    }

    private void goToShareSettingPage() {
        Intent intent = new Intent(m_context, SettingShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("FtpSupport", isFtpSupported);
        bundle.putBoolean("DlnaSupport", isDlnaSupported);
        intent.putExtra("Sharing", bundle);
        m_context.startActivity(intent);
    }

    private void goToPowerSettingPage() {
        Intent intent = new Intent(m_context, SettingPowerSavingActivity.class);
        m_context.startActivity(intent);
    }

    private void goToBackupSettingPage() {
        Intent intent = new Intent(m_context, SettingBackupRestoreActivity.class);
        m_context.startActivity(intent);
    }

    private void goToUpgradeSettingPage() {
        Intent intent = new Intent(m_context, SettingUpgradeActivity.class);
        intent.putExtra("First", m_blFirst);
        m_blFirst = false;
        m_context.startActivity(intent);
    }

    private void goToAccountSettingPage() {
        Intent intent = new Intent(m_context, SettingAccountActivity.class);
        m_context.startActivity(intent);
    }

    private void goToNetworkSettingPage() {
        Intent intent = new Intent(m_context, SettingNetworkActivity.class);
        m_context.startActivity(intent);
    }

    private void goToDeviceSettingPage() {
        //Intent intent = new Intent(m_context, SystemInfoActivity.class);
        Intent intent = new Intent(m_context, SettingDeviceActivity.class);
        m_context.startActivity(intent);
    }

    private void goToAboutSettingPage() {
        Intent intent = new Intent(m_context, SettingAboutActivity.class);
        intent.putExtra("upgradeStatus", upgradeStatus);
        m_context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_login_password:
                goToAccountSettingPage();
                break;
            case R.id.setting_mobile_network:
                Toast.makeText(m_context.getApplicationContext(), "mobile network", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_ethernet_wan:
                Toast.makeText(m_context.getApplicationContext(), "ethernet wan", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_sharing_service:
                if (isSharingSupported) {
                    goToShareSettingPage();
                } else {
                    Toast.makeText(m_context.getApplicationContext(), m_context.getString(R.string
                            .setting_not_support_sharing_service), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.setting_firmware_upgrade:
                goToDeviceSettingPage();
                break;
            case R.id.setting_restart:
                goToDeviceSettingPage();
                break;
            case R.id.setting_backup:
                goToDeviceSettingPage();
                break;
            case R.id.setting_about:
                goToAboutSettingPage();
                break;

            default:
                break;
        }
    }

    public class SettingItem {
        //Item name
        private String  m_strItem;
        private Boolean m_blHasUpgrade;

        public SettingItem(String strItem, Boolean blHasUpgrade) {
            super();
            this.m_strItem = strItem;
            this.m_blHasUpgrade = blHasUpgrade;
        }

        public Boolean getUpgradeFlag() {
            return m_blHasUpgrade;
        }

        public void setUpgradeFlag(Boolean blHasUpgrade) {
            m_blHasUpgrade = blHasUpgrade;
        }

        public String getItemName() {
            return m_strItem;
        }
    }

    private class settingBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();
            if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE)) {
                if (ok) {
                } else {

                    Toast.makeText(m_context, R.string.setting_upgrade_stop_error, Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)) {
                if (ok) {
                    int nUpgradeStatus = BusinessManager.getInstance().getNewFirmwareInfo().getState();
                    if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)) {
                        //EnumDeviceCheckingStatus.DEVICE_NEW_VERSION
                        //EnumDeviceCheckingStatus.DEVICE_NO_NEW_VERSION
                        m_blFirst = false;
//                        if (isSharingSupported) {
//                            changeUpgradeFlag(ITEM_DEVICE_SETTING2, true);
//                        } else {
//                            changeUpgradeFlag(ITEM_DEVICE_SETTING1, true);
//                        }
                        //保存升级标志到本地SP
                        SharedPrefsUtil.getInstance(context).putBoolean(ISDEVICENEWVERSION, true);
                    } else {
//                        if (isSharingSupported) {
//                            changeUpgradeFlag(ITEM_DEVICE_SETTING2, false);
//                        } else {
//                            changeUpgradeFlag(ITEM_DEVICE_SETTING1, false);
//                        }
                        //保存升级标志到本地SP
                        SharedPrefsUtil.getInstance(context).putBoolean(ISDEVICENEWVERSION, false);
                    }
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET) || intent.getAction()
                    .equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
                if (ok) {
                    if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET)) {
                        //						Log.v("CHECKING_SHARING", "FTP");
                        isFtpSupported = true;
                    }
                    if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
                        //						Log.v("CHECKING_SHARING", "DLNA");
                        isDlnaSupported = true;
                    }
                } else {
                    if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET)) {
                        //						Log.v("CHECKING_SHARING", "NOFTP");
                        isFtpSupported = false;
                    }
                    if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
                        //						Log.v("CHECKING_SHARING", "NODLNA");
                        isFtpSupported = false;
                    }
                }
                isSharingSupported = isFtpSupported || isDlnaSupported;
//                initUI();
            }
        }

    }
}
