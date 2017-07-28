package com.alcatel.wifilink.ui.view;

import android.content.Context;
import android.content.Intent;
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

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.ToastUtil;
import com.alcatel.wifilink.model.wlan.WlanSupportAPMode;
import com.alcatel.wifilink.model.wlan.WlanSettings;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity;

import static com.alcatel.wifilink.R.id.text_advanced_settings_2g;

/**
 * Created by tao.j on 2017/6/7.
 */

public class ViewWifiSettings extends BaseViewImpl implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "ViewWifiSettings";
//    protected ActivityBroadcastReceiver m_msgReceiver;

    private String[] mSecuritySettings;
    private String[] mWpaEncryptionSettings;
    private String[] mWepEncryptionSettings;

    private ViewGroup m2GSettingsGroup;
    private ViewGroup m5GSettingsGroup;

    private SwitchCompat mWifi2GSwitch;
    private TextView m2GAdvancedText;
    private SwitchCompat mWifi5GSwitch;
    private TextView m5GAdvancedText;

    private AppCompatSpinner mSecurity2GSpinner;
    private AppCompatSpinner mSecurity5GSpinner;

    private AppCompatSpinner mEncryption2GSpinner;
    private AppCompatSpinner mEncryption5GSpinner;

    private EditText mSsid2GEdit;
    private EditText mSsid5GEdit;
    private EditText mKey2GEdit;
    private EditText mKey5GEdit;

    private Button mApplyBtn;
    private Button mCancelBtn;

    private ViewGroup mSwitch2GGroup;
    private ViewGroup mSwitch5GGroup;

    private ViewGroup mSsid2GGroup;
    private ViewGroup mSsid5GGroup;

    private ViewGroup mSecurity2GGroup;
    private ViewGroup mSecurity5GGroup;

    private ViewGroup mEncryption2GGroup;
    private ViewGroup mEncryption5GGroup;

    private ViewGroup mKey2GGroup;
    private ViewGroup mKey5GGroup;

    private View mDividerView;

//    private AP mNewAP2G = new AP();
//    private AP mWlanSettings.getAP5G() = new AP();

    private WlanSettings mWlanSettings;

//    private boolean mIs2GAPEnabled;
//    private boolean mIs5GAPEnabled;

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
        mSwitch2GGroup = (ViewGroup) m_view.findViewById(R.id.rl_wifi_2g_switch_group);
        mSwitch5GGroup = (ViewGroup) m_view.findViewById(R.id.rl_wifi_5g_switch_group);

        m2GSettingsGroup = (ViewGroup) m_view.findViewById(R.id.ll_settings_2g);
        m5GSettingsGroup = (ViewGroup) m_view.findViewById(R.id.ll_settings_5g);

        mWifi2GSwitch = (SwitchCompat) m_view.findViewById(R.id.switch_wifi_2g);
        mWifi2GSwitch.setOnCheckedChangeListener(this);
        mWifi5GSwitch = (SwitchCompat) m_view.findViewById(R.id.switch_wifi_5g);
        mWifi5GSwitch.setOnCheckedChangeListener(this);

        m2GAdvancedText = (TextView) m_view.findViewById(text_advanced_settings_2g);
        m2GAdvancedText.setOnClickListener(this);
        m5GAdvancedText = (TextView) m_view.findViewById(R.id.text_advanced_settings_5g);
        m5GAdvancedText.setOnClickListener(this);

        mSsid2GEdit = (EditText) m_view.findViewById(R.id.edit_ssid_2g);
        mSsid5GEdit = (EditText) m_view.findViewById(R.id.edit_ssid_5g);

        mKey2GEdit = (EditText) m_view.findViewById(R.id.edit_key_2g);
        mKey5GEdit = (EditText) m_view.findViewById(R.id.edit_key_5g);

        mSsid2GGroup = (ViewGroup) m_view.findViewById(R.id.ll_ssid_2g);
        mSsid5GGroup = (ViewGroup) m_view.findViewById(R.id.ll_ssid_5g);

        mSecurity2GGroup = (ViewGroup) m_view.findViewById(R.id.rl_security_2g);
        mSecurity5GGroup = (ViewGroup) m_view.findViewById(R.id.rl_security_5g);

        mEncryption2GGroup = (ViewGroup) m_view.findViewById(R.id.rl_encryption_2g);
        mEncryption5GGroup = (ViewGroup) m_view.findViewById(R.id.rl_encryption_5g);
        mKey2GGroup = (ViewGroup) m_view.findViewById(R.id.ll_key_2g);
        mKey5GGroup = (ViewGroup) m_view.findViewById(R.id.ll_key_5g);

        mSecurity2GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_security_2g);
        mSecurity2GSpinner.setOnItemSelectedListener(this);
        mSecurity5GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_security_5g);
        mSecurity5GSpinner.setOnItemSelectedListener(this);

        mEncryption2GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_encryption_2g);
        mEncryption2GSpinner.setOnItemSelectedListener(this);
        mEncryption5GSpinner = (AppCompatSpinner) m_view.findViewById(R.id.spinner_encryption_5g);
        mEncryption5GSpinner.setOnItemSelectedListener(this);

        mApplyBtn = (Button) m_view.findViewById(R.id.btn_apply);
        mCancelBtn = (Button) m_view.findViewById(R.id.btn_cancel);
        mApplyBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        mDividerView = m_view.findViewById(R.id.divider);


//        m_msgReceiver = new ActivityBroadcastReceiver();
    }

    private void updateUIWithWlanSettings() {
        Log.d(TAG, "updateUIWithWlanSettings");
        if (mWlanSettings == null) {
            return;
        }

        mWifi2GSwitch.setChecked(mWlanSettings.getAP2G().isApEnabled());
        mWifi5GSwitch.setChecked(mWlanSettings.getAP5G().isApEnabled());

        mSsid2GEdit.setText(mWlanSettings.getAP2G().getSsid());
        int securityMode = mWlanSettings.getAP2G().getSecurityMode();
        mSecurity2GSpinner.setSelection(securityMode);
        if (securityMode == ENUM.SecurityMode.Disable.ordinal()) {
            mEncryption2GGroup.setVisibility(View.GONE);
            mKey2GGroup.setVisibility(View.GONE);
            mEncryption2GSpinner.setSelection(-1);
            mKey2GEdit.setText("");
        } else if (securityMode == ENUM.SecurityMode.WEP.ordinal()) {
            mEncryption2GGroup.setVisibility(View.VISIBLE);
            mKey2GGroup.setVisibility(View.VISIBLE);
            mEncryption2GSpinner.setSelection(mWlanSettings.getAP2G().getWepType());
            mKey2GEdit.setText(mWlanSettings.getAP2G().getWepKey());
        } else {
            mEncryption2GGroup.setVisibility(View.VISIBLE);
            mKey2GGroup.setVisibility(View.VISIBLE);
            mEncryption2GSpinner.setSelection(mWlanSettings.getAP2G().getWpaType());
            mKey2GEdit.setText(mWlanSettings.getAP2G().getWpaKey());
        }

        mSsid5GEdit.setText(mWlanSettings.getAP5G().getSsid());
        mSecurity5GSpinner.setSelection(mWlanSettings.getAP5G().getSecurityMode());
        if (mWlanSettings.getAP5G().getSecurityMode() == ENUM.SecurityMode.Disable.ordinal()) {
            mEncryption5GGroup.setVisibility(View.GONE);
            mKey5GGroup.setVisibility(View.GONE);
            mEncryption5GSpinner.setSelection(-1);
            mKey5GEdit.setText("");
        } else if (mWlanSettings.getAP5G().getSecurityMode() == ENUM.SecurityMode.WEP.ordinal()) {
            mEncryption5GGroup.setVisibility(View.VISIBLE);
            mKey5GGroup.setVisibility(View.VISIBLE);
            mEncryption5GSpinner.setSelection(mWlanSettings.getAP5G().getWepType());
            mKey5GEdit.setText(mWlanSettings.getAP5G().getWepKey());
        } else {
            mEncryption5GGroup.setVisibility(View.VISIBLE);
            mKey5GGroup.setVisibility(View.VISIBLE);
            mEncryption5GSpinner.setSelection(mWlanSettings.getAP5G().getWpaType());
            mKey5GEdit.setText(mWlanSettings.getAP5G().getWpaKey());
        }

        m2GSettingsGroup.setVisibility(mWlanSettings.getAP2G().isApEnabled() ? View.VISIBLE : View.GONE);
        m5GSettingsGroup.setVisibility(mWlanSettings.getAP5G().isApEnabled() ? View.VISIBLE : View.GONE);

    }


    private void updateUIWithSupportMode(int supportMode) {
        if (supportMode == ENUM.WlanSupportMode.Mode2Point4G.ordinal()) {
            mSwitch2GGroup.setVisibility(View.VISIBLE);
            mSwitch5GGroup.setVisibility(View.GONE);

            m2GSettingsGroup.setVisibility(View.VISIBLE);
            m5GSettingsGroup.setVisibility(View.GONE);

            mDividerView.setVisibility(View.GONE);
        } else if (supportMode == ENUM.WlanSupportMode.Mode5G.ordinal()) {
            mSwitch2GGroup.setVisibility(View.GONE);
            mSwitch5GGroup.setVisibility(View.VISIBLE);

            m2GSettingsGroup.setVisibility(View.GONE);
            m5GSettingsGroup.setVisibility(View.VISIBLE);

            mDividerView.setVisibility(View.GONE);

        } else if (supportMode == ENUM.WlanSupportMode.Mode2Point4GAnd5G.ordinal()) {
            mSwitch2GGroup.setVisibility(View.VISIBLE);
            mSwitch5GGroup.setVisibility(View.VISIBLE);

            m2GSettingsGroup.setVisibility(View.VISIBLE);
            m5GSettingsGroup.setVisibility(View.VISIBLE);
        } else {
            mSwitch2GGroup.setVisibility(View.GONE);
            mSwitch5GGroup.setVisibility(View.GONE);

            m2GSettingsGroup.setVisibility(View.GONE);
            m5GSettingsGroup.setVisibility(View.GONE);

            mDividerView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
//        m_context.registerReceiver(m_msgReceiver,
//                new IntentFilter(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST));
//        m_context.registerReceiver(m_msgReceiver,
//                new IntentFilter(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST));
//        m_context.registerReceiver(m_msgReceiver,
//                new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));
//        m_context.registerReceiver(m_msgReceiver,
//                new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
//        m_context.registerReceiver(m_msgReceiver,
//                new IntentFilter(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET));

        requestWlanSupportMode();
//        requestWlanState();
        requestWlanSettings();
//        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
//        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET, null);
//
//        updateUIWithWlanSettings();
    }

    private void requestWlanSettings() {
        API.get().getWlanSettings(new MySubscriber<WlanSettings>() {
            @Override
            protected void onSuccess(WlanSettings result) {
                mWlanSettings = result;
                updateUIWithWlanSettings();
            }

            @Override
            protected void onFailure() {

            }
        });
    }

//    private void requestWlanState() {
//        API.get().getWlanState(new MySubscriber<WlanState>() {
//            @Override
//            protected void onSuccess(WlanState result) {
//                mIs2GAPEnabled = result.isAP2GEnabled();
//                mIs5GAPEnabled = result.isAP5GEnabled();
//                mWifi2GSwitch.setChecked(mIs2GAPEnabled);
//                mWifi5GSwitch.setChecked(mIs5GAPEnabled);
//            }
//
//            @Override
//            protected void onFailure() {
//
//            }
//        });
//    }

    private void requestWlanSupportMode() {
        API.get().getWlanSupportMode(new MySubscriber<WlanSupportAPMode>() {
            @Override
            protected void onSuccess(WlanSupportAPMode result) {
                updateUIWithSupportMode(result.getWlanSupportAPMode());
            }

            @Override
            protected void onFailure() {

            }
        });
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
//        m_context.unregisterReceiver(m_msgReceiver);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged buttonView:" + buttonView + ", isChecked:" + isChecked);

        if (buttonView.getId() == R.id.switch_wifi_2g) {
            m2GSettingsGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        } else if (buttonView.getId() == R.id.switch_wifi_5g) {
            m5GSettingsGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        }
    }

//    private void setWlanState() {
//        Log.d(TAG, "setWlanState");
//        boolean is2GEnabled = mWifi2GSwitch.isChecked();
//        boolean is5GEnabled = mWifi5GSwitch.isChecked();
//        WlanState wlanState = new WlanState(is2GEnabled, is5GEnabled);
//        API.get().setWlanState(wlanState, new MySubscriber() {
//            @Override
//            protected void onSuccess(Object result) {
//                Log.d(TAG, "onSuccess");
//
//                mIs2GAPEnabled = is2GEnabled;
//                mIs5GAPEnabled = is5GEnabled;
//            }
//
//            @Override
//            protected void onFailure() {
//                Log.d(TAG, "onFailure");
//                mWifi2GSwitch.setChecked(mIs2GAPEnabled);
//                mWifi5GSwitch.setChecked(mIs5GAPEnabled);
//            }
//        });
//    }

    private void set2GSettingsEnable(boolean enabled) {
        int visibility = enabled ? View.VISIBLE : View.GONE;
        mSsid2GGroup.setVisibility(visibility);
        mSecurity2GGroup.setVisibility(visibility);
        mEncryption2GGroup.setVisibility(visibility);
        mKey2GGroup.setVisibility(visibility);
        m2GAdvancedText.setVisibility(visibility);
    }

    private void set5GSettingsEnable(boolean enabled) {
        int visibility = enabled ? View.VISIBLE : View.GONE;
        mSsid5GGroup.setVisibility(visibility);
        mSecurity5GGroup.setVisibility(visibility);
        mEncryption5GGroup.setVisibility(visibility);
        mKey5GGroup.setVisibility(visibility);
        m5GAdvancedText.setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.text_advanced_settings_2g || v.getId() == R.id.text_advanced_settings_5g) {
            Intent intent = new Intent(m_context, WlanAdvancedSettingsActivity.class);
//            intent.putExtra(EXTRA_SSID_BROADCAST, mWlanSettings.getAP2G().SsidHidden);
//            intent.putExtra(EXTRA_CHANNEL, mWlanSettings.getAP2G().Channel);
//            intent.putExtra(EXTRA_COUNTRY, mWlanSettings.getAP2G().CountryCode);
//            intent.putExtra(EXTRA_BANDWIDTH, mWlanSettings.getAP2G().Bandwidth);
//            intent.putExtra(EXTRA_MODE_80211, mWlanSettings.getAP2G().WMode);
//            intent.putExtra(EXTRA_AP_ISOLATION, mWlanSettings.getAP2G().ApIsolation);
            m_context.startActivity(intent);
        } else if (v.getId() == R.id.btn_apply) {
            applySettings();
        } else if (v.getId() == R.id.btn_cancel) {
            restoreSettings();
        }

    }

    private void restoreSettings() {
        requestWlanSettings();
    }

    private void applySettings() {
        boolean isAP2GStateChanged = mWifi2GSwitch.isChecked() != mWlanSettings.getAP2G().isApEnabled();
        boolean isAP5GStateChanged = mWifi5GSwitch.isChecked() != mWlanSettings.getAP5G().isApEnabled();

        WlanSettings newSettings = mWlanSettings.clone();

        if (isAP2GStateChanged && !mWifi2GSwitch.isChecked()) {
            newSettings.getAP2G().setApEnabled(false);
        } else {
            newSettings.getAP2G().setApEnabled(true);
            String newSsid2G = mSsid2GEdit.getText().toString().trim();
            int newSecurity2GMode = mSecurity2GSpinner.getSelectedItemPosition();
            int newEncryption2G = mEncryption2GSpinner.getSelectedItemPosition();
            String newKey2G = mKey2GEdit.getText().toString().trim();

            if (newSsid2G.isEmpty()) {
                ToastUtil.showMessage(m_context, "Name(2.4G) is missing!");
                return;
            }
            newSettings.getAP2G().setSsid(newSsid2G);
            ;
            newSettings.getAP2G().setSecurityMode(newSecurity2GMode);
            //disable
            if (newSecurity2GMode == 0) {
                newSettings.getAP2G().setWepKey("");
                newSettings.getAP2G().setWepType(0);
                newSettings.getAP2G().setWpaType(0);
                newSettings.getAP2G().setWpaKey("");
                //wep
            } else if (newSecurity2GMode == 1) {
                if (newKey2G.length() != 5 || newKey2G.length() != 13) {
                    ToastUtil.showMessage(m_context, "Wep password(2.4G) length must be 5 or 13!");
                    return;
                }
                newSettings.getAP2G().setWepType(newEncryption2G);
                newSettings.getAP2G().setWepKey(newKey2G);
                newSettings.getAP2G().setWpaType(0);
                newSettings.getAP2G().setWpaKey("");
                //wpa
            } else {
                if (newKey2G.length() < 8 || newKey2G.length() > 63) {
                    ToastUtil.showMessage(m_context, "Password(2.4G) length must be 8-63!");
                    return;
                }
                newSettings.getAP2G().setWepType(0);
                newSettings.getAP2G().setWepKey("");
                newSettings.getAP2G().setWpaType(newEncryption2G);
                newSettings.getAP2G().setWpaKey(newKey2G);
            }
        }

        if (isAP5GStateChanged && !mWifi5GSwitch.isChecked()) {
            newSettings.getAP5G().setApEnabled(false);
        } else {
            newSettings.getAP5G().setApEnabled(true);
            String newSsid5G = mSsid5GEdit.getText().toString().trim();
            int newSecurity5GMode = mSecurity5GSpinner.getSelectedItemPosition();
            int newEncryption5G = mEncryption5GSpinner.getSelectedItemPosition();
            String newKey5G = mKey5GEdit.getText().toString().trim();

            if (newSsid5G.isEmpty()) {
                ToastUtil.showMessage(m_context, "Name(5G) is missing!");
                return;
            }
            newSettings.getAP5G().setSsid(newSsid5G);
            newSettings.getAP5G().setSecurityMode(newSecurity5GMode);
            //disable
            if (newSecurity5GMode == 0) {
                newSettings.getAP5G().setWepKey("");
                newSettings.getAP5G().setWepType(0);
                newSettings.getAP5G().setWpaType(0);
                newSettings.getAP5G().setWpaKey("");
                //wep
            } else if (newSecurity5GMode == 1) {
                if (newKey5G.length() != 5 || newKey5G.length() != 13) {
                    ToastUtil.showMessage(m_context, "Wep password(5G) length must be 5 or 13!");
                    return;
                }
                newSettings.getAP5G().setWepType(newEncryption5G);
                newSettings.getAP5G().setWepKey(newKey5G);
                newSettings.getAP5G().setWpaType(0);
                newSettings.getAP5G().setWpaKey("");
                //wpa
            } else {
                if (newKey5G.length() < 8 || newKey5G.length() > 63) {
                    ToastUtil.showMessage(m_context, "Password(5G) length must be 8-63!");
                    return;
                }
                newSettings.getAP5G().setWepType(0);
                newSettings.getAP5G().setWepKey("");
                newSettings.getAP5G().setWpaType(newEncryption5G);
                newSettings.getAP5G().setWpaKey(newKey5G);
            }
        }
        Log.d(TAG, "newSettings, " + newSettings);

        API.get().setWlanSettings(newSettings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil.showMessage(m_context, m_context.getString(R.string.success));
            }

            @Override
            protected void onFailure() {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected");
        if (parent.getId() == R.id.spinner_security_2g) {
            if (position == 0) {
                mEncryption2GGroup.setVisibility(View.GONE);
                mKey2GGroup.setVisibility(View.GONE);
            } else if (position == 1) {
                mEncryption2GGroup.setVisibility(View.VISIBLE);
                mEncryption2GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                if (mWlanSettings != null) {
                    mEncryption2GSpinner.setSelection(mWlanSettings.getAP2G().getWepType());
                }
                mKey2GGroup.setVisibility(View.VISIBLE);
            } else {
                mEncryption2GGroup.setVisibility(View.VISIBLE);
                mEncryption2GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                if (mWlanSettings != null) {
                    mEncryption2GSpinner.setSelection(mWlanSettings.getAP2G().getWpaType());
                }
                mKey2GGroup.setVisibility(View.VISIBLE);
            }
        } else if (parent.getId() == R.id.spinner_security_5g) {
            if (position == 0) {
                mEncryption5GGroup.setVisibility(View.GONE);
                mKey5GGroup.setVisibility(View.GONE);
            } else if (position == 1) {
                mEncryption5GGroup.setVisibility(View.VISIBLE);
                mEncryption5GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                if (mWlanSettings != null) {
                    mEncryption2GSpinner.setSelection(mWlanSettings.getAP5G().getWepType());
                }
                mKey5GGroup.setVisibility(View.VISIBLE);
            } else {
                mEncryption5GGroup.setVisibility(View.VISIBLE);
                mEncryption5GSpinner.setAdapter(new ArrayAdapter<>(m_context, android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                if (mWlanSettings != null) {
                    mEncryption2GSpinner.setSelection(mWlanSettings.getAP5G().getWpaType());
                }
                mKey5GGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    private class ActivityBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.e(TAG, "get action is " + action);
//            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
//            Boolean ok = response != null && response.isOk();
//            if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
//                if (ok) {
//                    updateUIWithWlanSettings();
//                }
//            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET)) {
//                if (ok) {
//                    //init controls state
//                }
//            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
//                String strTost;
//                if (ok) {
//                    strTost = m_context.getString(R.string.setting_wifi_set_success);
//                } else {
//                    strTost = m_context.getString(R.string.setting_wifi_set_failed);
//                }
//                Toast.makeText(m_context, strTost, Toast.LENGTH_SHORT).show();
//
//            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST)) {
//            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST)) {
//            }
//        }
//    }
}
