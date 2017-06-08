package com.alcatel.smartlinkv3.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

/**
 * Created by tao.j on 2017/6/7.
 */

public class ViewWifiSettings extends BaseViewImpl {

    private static final String TAG = "ViewWifiSettings";
    protected ActivityBroadcastReceiver m_msgReceiver;

    public ViewWifiSettings(Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        m_view = LayoutInflater.from(m_context).inflate(R.layout.wifi_settings, null);
        m_msgReceiver = new ActivityBroadcastReceiver();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET));
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        m_context.unregisterReceiver(m_msgReceiver);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

    }

    private class ActivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "get action is " + action);
            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();
            if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
                if (ok) {
                    Log.d(TAG, "rsp result:" + BusinessManager.getInstance().getSsid());

                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET)) {
                if (ok) {
                    //init controls state
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
                String strTost = m_context.getString(R.string.setting_wifi_set_failed);
                if (ok) {
                    strTost = m_context.getString(R.string.setting_wifi_set_success);
                } else {
                }

                Toast.makeText(m_context, strTost, Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST)) {
            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST)) {
            }
        }
    }
}
