package com.alcatel.smartlinkv3.ui.home.helper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;


public class ActivityBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ViewWifiSettings";
    private OnUpdateWifiSettingListener wifiSettingListener;

    public interface OnUpdateWifiSettingListener {
        void updateWifiSettings();
    }

    public void setOnOnUpdateWifiSettingListener(OnUpdateWifiSettingListener wifiSettingListener) {
        this.wifiSettingListener = wifiSettingListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "get action is " + action);
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();
        if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
            if (ok) {
                if (wifiSettingListener != null) {
                    wifiSettingListener.updateWifiSettings();
                }
            }
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET)) {
            if (ok) {
                //init controls state
            }
        } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
            String strTost;
            if (ok) {
                strTost = context.getString(R.string.setting_wifi_set_success);
            } else {
                strTost = context.getString(R.string.setting_wifi_set_failed);
            }
            Toast.makeText(context, strTost, Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST)) {
        } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST)) {
        }
    }
}
