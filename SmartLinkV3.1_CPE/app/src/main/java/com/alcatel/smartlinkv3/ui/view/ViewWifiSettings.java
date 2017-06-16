package com.alcatel.smartlinkv3.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.wlan.AP;
import com.alcatel.smartlinkv3.business.wlan.WlanNewSettingResult;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity;

import static com.alcatel.smartlinkv3.R.id.text_advanced_settings_2g;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_AP_ISOLATION;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_BANDWIDTH;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_CHANNEL;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_COUNTRY;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_MODE_80211;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_SSID_BROADCAST;

/**
 * Created by tao.j on 2017/6/7.
 */

public class ViewWifiSettings extends BaseViewImpl implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "ViewWifiSettings";
    protected ActivityBroadcastReceiver m_msgReceiver;

    private String[] mSecuritySettings;
    private String[] mWpaEncryptionSettings;
    private String[] mWepEncryptionSettings;

    private SwitchCompat switchWifi2g;
    private TextView textAdvancedSettings2g;
    private SwitchCompat switchWifi5g;
    private TextView textAdvancedSettings5g;

    private AppCompatSpinner mSecurity2GSpinner;
    private AppCompatSpinner mSecurity5GSpinner;

    private AppCompatSpinner mEncryption2GSpinner;
    private AppCompatSpinner mEncryption5GSpinner;

    private EditText mSsid2gEdit;
    private EditText mSsid5gEdit;
    private EditText mKey2gEdit;
    private EditText mKey5gEdit;

    private Button mApplyBtn;

    private ViewGroup mEncryption2GGroup;
    private ViewGroup mPasswd2GGroup;

    private ViewGroup mEncryption5GGroup;
    private ViewGroup mPasswd5GGroup;

    private AP mNewAP2G;
    private AP mNewAP5G;

    public ViewWifiSettings(Context context) {
        super(context);
        mSecuritySettings = context.getResources().getStringArray(R.array.wlan_settings_security);
        mWpaEncryptionSettings = context.getResources().getStringArray(R.array.wlan_settings_wpa_type);
        mWepEncryptionSettings = context.getResources().getStringArray(R.array.wlan_settings_wep_type);
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

        mEncryption2GGroup = (ViewGroup) m_view.findViewById(R.id.rl_encryption_2g);
        mEncryption5GGroup = (ViewGroup) m_view.findViewById(R.id.rl_encryption_5g);
        mPasswd2GGroup = (ViewGroup) m_view.findViewById(R.id.ll_key_2g);
        mPasswd5GGroup = (ViewGroup) m_view.findViewById(R.id.ll_key_5g);

        mSecurity2GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_security_2g);
        mSecurity2GSpinner.setOnItemSelectedListener(this);
        mSecurity5GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_security_5g);
        mSecurity5GSpinner.setOnItemSelectedListener(this);

        mEncryption2GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_encryption_2g);
        mEncryption2GSpinner.setOnItemSelectedListener(this);
        mEncryption5GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_encryption_5g);
        mEncryption5GSpinner.setOnItemSelectedListener(this);

        mApplyBtn = (Button) m_view.findViewById(R.id.btn_apply);
        mApplyBtn.setOnClickListener(this);
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

        mSecurity2GSpinner.setSelection(mNewAP2G.SecurityMode);
        mSecurity5GSpinner.setSelection(mNewAP5G.SecurityMode);

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


        if (v.getId() == R.id.text_advanced_settings_2g || v.getId() == R.id.text_advanced_settings_5g) {
            Intent intent = new Intent(m_context, WlanAdvancedSettingsActivity.class);
            intent.putExtra(EXTRA_SSID_BROADCAST, mNewAP2G.SsidHidden);
            intent.putExtra(EXTRA_CHANNEL, mNewAP2G.Channel);
            intent.putExtra(EXTRA_COUNTRY, mNewAP2G.CountryCode);
            intent.putExtra(EXTRA_BANDWIDTH, mNewAP2G.Bandwidth);
            intent.putExtra(EXTRA_MODE_80211, mNewAP2G.WMode);
            intent.putExtra(EXTRA_AP_ISOLATION, mNewAP2G.ApIsolation);
            m_context.startActivity(intent);
        } else if (v.getId() == R.id.btn_apply) {
//            applySettings();

            API.get().login("admin", "admin", new MySubscriber() {

                @Override
                public void onSuccess(Object result) {
                    Log.d("OkHttp", "onSuccess" + result);
                }

                @Override
                protected void onFailure() {

                }
            });
        }

    }

    private void applySettings() {
        WlanNewSettingResult settings = BusinessManager.getInstance().getWlanSettings();
        mNewAP2G.Ssid = mSsid2gEdit.getText().toString().trim();
        mNewAP2G.WpaKey = mKey2gEdit.getText().toString().trim();

        mNewAP5G.Ssid = mSsid5gEdit.getText().toString().trim();
        mNewAP5G.WpaKey = mKey5gEdit.getText().toString().trim();
        settings.AP2G = mNewAP2G;
        settings.AP5G = mNewAP5G;
        BusinessManager.getInstance().sendSetWlanRequest(settings);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_security_2g) {
            if (position == 0) {
                mEncryption2GGroup.setVisibility(View.GONE);
                mPasswd2GGroup.setVisibility(View.GONE);
            } else if (position == 1) {
                mEncryption2GGroup.setVisibility(View.VISIBLE);
                mEncryption2GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                mEncryption2GSpinner.setSelection(mNewAP2G.WepType);
                mPasswd2GGroup.setVisibility(View.VISIBLE);
            } else {
                mEncryption2GGroup.setVisibility(View.VISIBLE);
                mEncryption2GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                mEncryption2GSpinner.setSelection(mNewAP2G.WpaType);
                mPasswd2GGroup.setVisibility(View.VISIBLE);
            }
        } else if (parent.getId() == R.id.spinner_security_5g) {
            if (position == 0) {
                mEncryption5GGroup.setVisibility(View.GONE);
                mPasswd5GGroup.setVisibility(View.GONE);
            } else if (position == 1) {
                mEncryption5GGroup.setVisibility(View.VISIBLE);
                mEncryption5GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                mEncryption2GSpinner.setSelection(mNewAP5G.WepType);
                mPasswd5GGroup.setVisibility(View.VISIBLE);
            } else {
                mEncryption5GGroup.setVisibility(View.VISIBLE);
                mEncryption5GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                mEncryption2GSpinner.setSelection(mNewAP5G.WpaType);
                mPasswd5GGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                    updateWifiSettings();
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET)) {
                if (ok) {
                    //init controls state
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
                String strTost;
                if (ok) {
                    strTost = m_context.getString(R.string.setting_wifi_set_success);
                } else {
                    strTost = m_context.getString(R.string.setting_wifi_set_failed);
                }
                Toast.makeText(m_context, strTost, Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST)) {
            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST)) {
            }
        }
    }
}
