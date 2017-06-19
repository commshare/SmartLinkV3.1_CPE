package com.alcatel.smartlinkv3.ui.home.helper.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.SharedPrefsUtil;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.home.fragment.SettingFragment;

import static com.alcatel.smartlinkv3.ui.home.fragment.SettingFragment.isDlnaSupported;
import static com.alcatel.smartlinkv3.ui.home.fragment.SettingFragment.isFtpSupported;

public class settingBroadcast extends BroadcastReceiver {
    public static final String ISDEVICENEWVERSION = "IS_DEVICE_NEW_VERSION";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();
        if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE)) {
            if (ok) {
            } else {

                Toast.makeText(context, R.string.setting_upgrade_stop_error, Toast.LENGTH_SHORT).show();
            }
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION)) {
            if (ok) {
                int nUpgradeStatus = BusinessManager.getInstance().getNewFirmwareInfo().getState();
                if (EnumDeviceCheckingStatus.DEVICE_NEW_VERSION == EnumDeviceCheckingStatus.build(nUpgradeStatus)) {
                    SettingFragment.m_blFirst = false;
                    SharedPrefsUtil.getInstance(context).putBoolean(ISDEVICENEWVERSION, true);
                } else {
                    SharedPrefsUtil.getInstance(context).putBoolean(ISDEVICENEWVERSION, false);
                }
            }
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET) || intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
            if (ok) {
                if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET)) {
                    Log.v("CHECKING_SHARING", "FTP");
                    isFtpSupported = true;
                }
                if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
                    Log.v("CHECKING_SHARING", "DLNA");
                    isDlnaSupported = true;
                }
            } else {
                if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET)) {
                    Log.v("CHECKING_SHARING", "NOFTP");
                    isFtpSupported = false;
                }
                if (intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)) {
                    Log.v("CHECKING_SHARING", "NODLNA");
                    SettingFragment.isFtpSupported = false;
                }
            }
            SettingFragment.isSharingSupported = isFtpSupported || isDlnaSupported;
        }
    }

}
