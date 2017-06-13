package com.alcatel.smartlinkv3.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.wlan.AP;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity;

import static com.alcatel.smartlinkv3.R.id.text_advanced_settings_2g;

/**
 * Created by tao.j on 2017/6/7.
 */

public class ViewWifiSettings extends BaseViewImpl implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "ViewWifiSettings";
    protected ActivityBroadcastReceiver m_msgReceiver;

    private SwitchCompat switchWifi2g;
    private TextView textAdvancedSettings2g;
    private SwitchCompat switchWifi5g;
    private TextView textAdvancedSettings5g;

    private EditText mSsid2gEdit;
    private EditText mSsid5gEdit;
    private EditText mKey2gEdit;
    private EditText mKey5gEdit;

    private AP mNewAP2G;
    private AP mNewAP5G;

    public ViewWifiSettings(Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        m_view = LayoutInflater.from(m_context).inflate(R.layout.wifi_settings, null);
        switchWifi2g = (SwitchCompat) m_view.findViewById(R.id.switch_wifi_2g);
        switchWifi2g.setOnCheckedChangeListener(this);
        textAdvancedSettings2g = (TextView) m_view.findViewById(text_advanced_settings_2g);
        textAdvancedSettings2g.setOnClickListener(this);
        switchWifi5g = (SwitchCompat) m_view.findViewById(R.id.switch_wifi_5g);
        switchWifi5g.setOnCheckedChangeListener(this);
        textAdvancedSettings5g = (TextView) m_view.findViewById(R.id.text_advanced_settings_5g);
        textAdvancedSettings5g.setOnClickListener(this);

        mSsid2gEdit = (EditText) m_view.findViewById(R.id.edit_ssid_2g);
        mSsid5gEdit = (EditText) m_view.findViewById(R.id.edit_ssid_5g);
        mKey2gEdit = (EditText) m_view.findViewById(R.id.edit_key_2g);
        mKey5gEdit = (EditText) m_view.findViewById(R.id.edit_key_5g);

        m_msgReceiver = new ActivityBroadcastReceiver();
    }

    private void updateWifiSettings() {

        mNewAP2G = BusinessManager.getInstance().getAP2G().clone();
        mNewAP5G = BusinessManager.getInstance().getAP5G().clone();

        updateSupportMode();
        mSsid2gEdit.setText(BusinessManager.getInstance().getSsid());
        mSsid5gEdit.setText(BusinessManager.getInstance().getSsid_5G());
        mKey2gEdit.setText(BusinessManager.getInstance().getWifiPwd());
        mKey5gEdit.setText(BusinessManager.getInstance().getWifiPwd_5G());
    }

    private void updateSupportMode() {
        ENUM.WlanSupportMode supportMode = BusinessManager.getInstance().getWlanSupportMode();
        Log.d(TAG, "updateSupportMode: " + supportMode);
        if (supportMode == ENUM.WlanSupportMode.Mode2Point4G) {
            switchWifi2g.setChecked(true);
            switchWifi5g.setChecked(false);
        } else if (supportMode == ENUM.WlanSupportMode.Mode5G) {
            switchWifi2g.setChecked(false);
            switchWifi5g.setChecked(true);
        } else if (supportMode == ENUM.WlanSupportMode.Mode2Point4GAnd5G) {
            switchWifi2g.setChecked(true);
            switchWifi5g.setChecked(true);
        } else {
            switchWifi2g.setChecked(false);
            switchWifi5g.setChecked(false);
        }
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

        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET, null);

        updateWifiSettings();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.switch_wifi_2g) {
            set2GSettingsEnable(isChecked);
        } else if (buttonView.getId() == R.id.switch_wifi_5g) {
            set5GSettingsEnable(isChecked);
        }
    }

    private void set2GSettingsEnable(boolean enabled) {
        mSsid2gEdit.setEnabled(enabled);
        mKey2gEdit.setEnabled(enabled);
        textAdvancedSettings2g.setEnabled(enabled);
        textAdvancedSettings2g.setTextColor(enabled ? Color.BLACK : Color.LTGRAY);
    }

    private void set5GSettingsEnable(boolean enabled) {
        mSsid5gEdit.setEnabled(enabled);
        mKey5gEdit.setEnabled(enabled);
        textAdvancedSettings5g.setEnabled(enabled);
        textAdvancedSettings5g.setTextColor(enabled ? Color.BLACK : Color.LTGRAY);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(m_context, WlanAdvancedSettingsActivity.class);
        intent.putExtra("ssid_broadcast", mNewAP2G.SsidHidden);
        intent.putExtra("security", mNewAP2G.SecurityMode);
        intent.putExtra("wpa_type", mNewAP2G.WpaType);
        intent.putExtra("wep_type", mNewAP2G.WepType);
        intent.putExtra("channel", mNewAP2G.Channel);
        intent.putExtra("country", mNewAP2G.CountryCode);
        intent.putExtra("bandwidth", mNewAP2G.Bandwidth);
        intent.putExtra("80211_mode", mNewAP2G.WMode);
        intent.putExtra("ap_isolation", mNewAP2G.ApIsolation);

        if (v.getId() == R.id.text_advanced_settings_2g) {
            Log.d(TAG, "click 2g");
            intent.putExtra("wlan_type", "2g");
        } else if (v.getId() == R.id.text_advanced_settings_5g) {
            Log.d(TAG, "click 5g");
            intent.putExtra("wlan_type", "5g");
        }
        m_context.startActivity(intent);

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
                    Log.d(TAG, "rsp result:" + BusinessManager.getInstance().getSsid_5G());

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
