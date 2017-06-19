package com.alcatel.smartlinkv3.ui.home.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.ui.activity.SettingAboutActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingDeviceActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingNetworkActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingShareActivity;
import com.alcatel.smartlinkv3.ui.home.helper.setting.settingBroadcast;

/**
 * Created by qianli.ma on 2017/6/16.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    public static boolean isFtpSupported = false;
    public static boolean isDlnaSupported = false;
    public static boolean isSharingSupported = false;
    public static boolean m_blFirst = true;
    private settingBroadcast m_receiver;
    private int upgradeStatus;

    public static final String ISDEVICENEWVERSION = "IS_DEVICE_NEW_VERSION";
    private RelativeLayout mLoginPassword;
    private RelativeLayout mMobileNetwork;
    private RelativeLayout mEthernetWan;
    private RelativeLayout mSharingService;
    private RelativeLayout mFirmwareUpgrade;
    private RelativeLayout mRestart;
    private RelativeLayout mBackup;
    private RelativeLayout mAbout;
    private View m_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = View.inflate(getActivity(), R.layout.fragment_home_setting, null);
        init();
        initEvent();
        initBroadcast();
        return m_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(m_receiver);
        } catch (Exception e) {

        }
    }

    private void registerReceiver() {
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE));
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.SHARING_GET_FTP_SETTING_REQUSET));
    }

    private void initBroadcast() {
        m_receiver = new settingBroadcast();
    }

    private void init() {
        m_view = LayoutInflater.from(getActivity()).inflate(R.layout.view_setting, null);
        mLoginPassword = (RelativeLayout) m_view.findViewById(R.id.setting_login_password);
        mMobileNetwork = (RelativeLayout) m_view.findViewById(R.id.setting_mobile_network);
        mEthernetWan = (RelativeLayout) m_view.findViewById(R.id.setting_ethernet_wan);
        mSharingService = (RelativeLayout) m_view.findViewById(R.id.setting_sharing_service);
        mFirmwareUpgrade = (RelativeLayout) m_view.findViewById(R.id.setting_firmware_upgrade);
        mRestart = (RelativeLayout) m_view.findViewById(R.id.setting_restart);
        mBackup = (RelativeLayout) m_view.findViewById(R.id.setting_backup);
        mAbout = (RelativeLayout) m_view.findViewById(R.id.setting_about);
    }

    private void initEvent() {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_login_password:
                goToAccountSettingPage();
                break;
            case R.id.setting_mobile_network:
                // TODO
                goToMobileNetworkSettingPage();
                break;
            case R.id.setting_ethernet_wan:
                ToastUtil_m.show(getActivity(), "ethernet wan");
                break;
            case R.id.setting_sharing_service:
                if (isSharingSupported) {
                    goToShareSettingPage();
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.setting_not_support_sharing_service));
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

    /* -------------------------------------------- HELPER -------------------------------------------- */
    /* -------------------------------------------- HELPER -------------------------------------------- */
    private void goToShareSettingPage() {
        Intent intent = new Intent(getActivity(), SettingShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("FtpSupport", isFtpSupported);
        bundle.putBoolean("DlnaSupport", isDlnaSupported);
        intent.putExtra("Sharing", bundle);
        getActivity().startActivity(intent);
    }

    private void goToAccountSettingPage() {
        // to setting account activity
        ChangeActivity.toActivity(getActivity(), SettingAccountActivity.class, true, true, false, 0);
    }

    private void goToMobileNetworkSettingPage() {
        // to setting network activity
        ChangeActivity.toActivity(getActivity(), SettingNetworkActivity.class, true, true, false, 0);
    }

    private void goToDeviceSettingPage() {
        // to setting device activity
        ChangeActivity.toActivity(getActivity(), SettingDeviceActivity.class, true, true, false, 0);
    }

    private void goToAboutSettingPage() {
        Intent intent = new Intent(getActivity(), SettingAboutActivity.class);
        intent.putExtra("upgradeStatus", upgradeStatus);
        getActivity().startActivity(intent);
    }

}
