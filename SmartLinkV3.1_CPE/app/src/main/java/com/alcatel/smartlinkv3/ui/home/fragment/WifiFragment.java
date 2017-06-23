package com.alcatel.smartlinkv3.ui.home.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.wlan.AP;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ToastUtil;
import com.alcatel.smartlinkv3.model.wlan.WlanSettings;
import com.alcatel.smartlinkv3.model.wlan.WlanSupportAPMode;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity;

import static android.app.Activity.RESULT_OK;
import static com.alcatel.smartlinkv3.R.id.text_advanced_settings_2g;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_AP_ISOLATION;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_BANDWIDTH;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_CHANNEL;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_COUNTRY;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_MODE_80211;
import static com.alcatel.smartlinkv3.ui.activity.WlanAdvancedSettingsActivity.EXTRA_SSID_BROADCAST;

public class WifiFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "WifiFragment";

    public static final int REQUEST_CODE_ADVANCED_SETTINGS_2_4G = 0x1000;
    public static final int REQUEST_CODE_ADVANCED_SETTINGS_5G = 0x1001;

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

    private WlanSettings mOriginSettings;
    private WlanSettings mEditedSettings;

    private Context mContext;

    public WifiFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        mContext = activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        mSecuritySettings = getResources().getStringArray(R.array.wlan_settings_security);
        mWpaEncryptionSettings = getResources().getStringArray(R.array.wlan_settings_wpa_type);
        mWepEncryptionSettings = getResources().getStringArray(R.array.wlan_settings_wep_type);
        requestWlanSupportMode();
        requestWlanSettings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wifi_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSwitch2GGroup = (ViewGroup) view.findViewById(R.id.rl_wifi_2g_switch_group);
        mSwitch5GGroup = (ViewGroup) view.findViewById(R.id.rl_wifi_5g_switch_group);

        m2GSettingsGroup = (ViewGroup) view.findViewById(R.id.ll_settings_2g);
        m5GSettingsGroup = (ViewGroup) view.findViewById(R.id.ll_settings_5g);

        mWifi2GSwitch = (SwitchCompat) view.findViewById(R.id.switch_wifi_2g);
        mWifi2GSwitch.setOnCheckedChangeListener(this);
        mWifi5GSwitch = (SwitchCompat) view.findViewById(R.id.switch_wifi_5g);
        mWifi5GSwitch.setOnCheckedChangeListener(this);

        m2GAdvancedText = (TextView) view.findViewById(text_advanced_settings_2g);
        m2GAdvancedText.setOnClickListener(this);
        m5GAdvancedText = (TextView) view.findViewById(R.id.text_advanced_settings_5g);
        m5GAdvancedText.setOnClickListener(this);

        mSsid2GEdit = (EditText) view.findViewById(R.id.edit_ssid_2g);
        mSsid5GEdit = (EditText) view.findViewById(R.id.edit_ssid_5g);

        mKey2GEdit = (EditText) view.findViewById(R.id.edit_key_2g);
        mKey5GEdit = (EditText) view.findViewById(R.id.edit_key_5g);

        mSsid2GGroup = (ViewGroup) view.findViewById(R.id.ll_ssid_2g);
        mSsid5GGroup = (ViewGroup) view.findViewById(R.id.ll_ssid_5g);

        mSecurity2GGroup = (ViewGroup) view.findViewById(R.id.rl_security_2g);
        mSecurity5GGroup = (ViewGroup) view.findViewById(R.id.rl_security_5g);

        mEncryption2GGroup = (ViewGroup) view.findViewById(R.id.rl_encryption_2g);
        mEncryption5GGroup = (ViewGroup) view.findViewById(R.id.rl_encryption_5g);
        mKey2GGroup = (ViewGroup) view.findViewById(R.id.ll_key_2g);
        mKey5GGroup = (ViewGroup) view.findViewById(R.id.ll_key_5g);

        mSecurity2GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_security_2g);
        mSecurity2GSpinner.setOnItemSelectedListener(this);
        mSecurity5GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_security_5g);
        mSecurity5GSpinner.setOnItemSelectedListener(this);

        mEncryption2GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_encryption_2g);
        mEncryption2GSpinner.setOnItemSelectedListener(this);
        mEncryption5GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_encryption_5g);
        mEncryption5GSpinner.setOnItemSelectedListener(this);

        view.findViewById(R.id.btn_apply).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);

        mDividerView = view.findViewById(R.id.divider);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");

    }

    private void requestWlanSettings() {
        API.get().getWlanSettings(new MySubscriber<WlanSettings>() {
            @Override
            protected void onSuccess(WlanSettings result) {
                mOriginSettings = result;
                mEditedSettings = mOriginSettings.clone();
                updateUIWithWlanSettings();
            }

            @Override
            protected void onFailure() {

            }
        });
    }


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

    private void updateUIWithWlanSettings() {
        Log.d(TAG, "updateUIWithWlanSettings");
        if (mOriginSettings == null) {
            return;
        }

        mWifi2GSwitch.setChecked(mOriginSettings.getAP2G().isApEnabled());
        mWifi5GSwitch.setChecked(mOriginSettings.getAP5G().isApEnabled());

        mSsid2GEdit.setText(mOriginSettings.getAP2G().Ssid);
        int securityMode = mOriginSettings.getAP2G().SecurityMode;
        mSecurity2GSpinner.setSelection(securityMode);
        if (securityMode == ENUM.SecurityMode.Disable.ordinal()) {
            mEncryption2GGroup.setVisibility(View.GONE);
            mKey2GGroup.setVisibility(View.GONE);
            mEncryption2GSpinner.setSelection(-1);
            mKey2GEdit.setText("");
        } else if (securityMode == ENUM.SecurityMode.WEP.ordinal()) {
            mEncryption2GGroup.setVisibility(View.VISIBLE);
            mKey2GGroup.setVisibility(View.VISIBLE);
            mEncryption2GSpinner.setSelection(mOriginSettings.getAP2G().WepType);
            mKey2GEdit.setText(mOriginSettings.getAP2G().WepKey);
        } else {
            mEncryption2GGroup.setVisibility(View.VISIBLE);
            mKey2GGroup.setVisibility(View.VISIBLE);
            mEncryption2GSpinner.setSelection(mOriginSettings.getAP2G().WpaType);
            mKey2GEdit.setText(mOriginSettings.getAP2G().WpaKey);
        }

        mSsid5GEdit.setText(mOriginSettings.getAP5G().Ssid);
        mSecurity5GSpinner.setSelection(mOriginSettings.getAP5G().SecurityMode);
        if (mOriginSettings.getAP5G().SecurityMode == ENUM.SecurityMode.Disable.ordinal()) {
            mEncryption5GGroup.setVisibility(View.GONE);
            mKey5GGroup.setVisibility(View.GONE);
            mEncryption5GSpinner.setSelection(-1);
            mKey5GEdit.setText("");
        } else if (mOriginSettings.getAP5G().SecurityMode == ENUM.SecurityMode.WEP.ordinal()) {
            mEncryption5GGroup.setVisibility(View.VISIBLE);
            mKey5GGroup.setVisibility(View.VISIBLE);
            mEncryption5GSpinner.setSelection(mOriginSettings.getAP5G().WepType);
            mKey5GEdit.setText(mOriginSettings.getAP5G().WepKey);
        } else {
            mEncryption5GGroup.setVisibility(View.VISIBLE);
            mKey5GGroup.setVisibility(View.VISIBLE);
            mEncryption5GSpinner.setSelection(mOriginSettings.getAP5G().WpaType);
            mKey5GEdit.setText(mOriginSettings.getAP5G().WpaKey);
        }

        m2GSettingsGroup.setVisibility(mOriginSettings.getAP2G().isApEnabled() ? View.VISIBLE : View.GONE);
        m5GSettingsGroup.setVisibility(mOriginSettings.getAP5G().isApEnabled() ? View.VISIBLE : View.GONE);

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
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.switch_wifi_2g) {
            m2GSettingsGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        } else if (buttonView.getId() == R.id.switch_wifi_5g) {
            m5GSettingsGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_advanced_settings_2g) {
            Intent intent = new Intent(mContext, WlanAdvancedSettingsActivity.class);
            intent.putExtra(EXTRA_SSID_BROADCAST, mEditedSettings.getAP2G().isSsidHiden());
            intent.putExtra(EXTRA_CHANNEL, mEditedSettings.getAP2G().Channel);
            intent.putExtra(EXTRA_COUNTRY, mEditedSettings.getAP2G().CountryCode);
            intent.putExtra(EXTRA_BANDWIDTH, mEditedSettings.getAP2G().Bandwidth);
            intent.putExtra(EXTRA_MODE_80211, mEditedSettings.getAP2G().WMode);
            intent.putExtra(EXTRA_AP_ISOLATION, mEditedSettings.getAP2G().isApIsolated());
            startActivityForResult(intent, REQUEST_CODE_ADVANCED_SETTINGS_2_4G);
        } else if (v.getId() == R.id.text_advanced_settings_5g) {
            Intent intent = new Intent(mContext, WlanAdvancedSettingsActivity.class);
            intent.putExtra(EXTRA_SSID_BROADCAST, mEditedSettings.getAP5G().isSsidHiden());
            intent.putExtra(EXTRA_CHANNEL, mEditedSettings.getAP5G().Channel);
            intent.putExtra(EXTRA_COUNTRY, mEditedSettings.getAP5G().CountryCode);
            intent.putExtra(EXTRA_BANDWIDTH, mEditedSettings.getAP5G().Bandwidth);
            intent.putExtra(EXTRA_MODE_80211, mEditedSettings.getAP5G().WMode);
            intent.putExtra(EXTRA_AP_ISOLATION, mEditedSettings.getAP5G().isApIsolated());
            startActivityForResult(intent, REQUEST_CODE_ADVANCED_SETTINGS_5G);
        } else if (v.getId() == R.id.btn_apply) {
            showApplySettingsDlg();
        } else if (v.getId() == R.id.btn_cancel) {
            restoreSettings();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode:" + requestCode);
        if ((REQUEST_CODE_ADVANCED_SETTINGS_2_4G == requestCode || REQUEST_CODE_ADVANCED_SETTINGS_5G == requestCode)
                && resultCode == RESULT_OK) {
            boolean broadcast = data.getBooleanExtra(EXTRA_SSID_BROADCAST, false);
            int channel = data.getIntExtra(EXTRA_CHANNEL, 0);
            String countryCode = data.getStringExtra(EXTRA_COUNTRY);
            int bandwidth = data.getIntExtra(EXTRA_BANDWIDTH, 0);
            int mode80211 = data.getIntExtra(EXTRA_MODE_80211, 0);
            boolean isolation = data.getBooleanExtra(EXTRA_AP_ISOLATION, false);

            Log.d(TAG, "broadcast:" + broadcast);
            Log.d(TAG, "channel:" + channel);
            Log.d(TAG, "countryCode:" + countryCode);
            Log.d(TAG, "bandwidth:" + bandwidth);
            Log.d(TAG, "mode80211:" + mode80211);
            Log.d(TAG, "isolation:" + isolation);

            AP ap = requestCode == REQUEST_CODE_ADVANCED_SETTINGS_2_4G ? mEditedSettings.getAP2G() : mEditedSettings.getAP5G();
            ap.setSsidHidden(broadcast);
            ap.Channel = channel;
            ap.CountryCode = countryCode;
            ap.Bandwidth = bandwidth;
            ap.WMode = mode80211;
            ap.setApIsolated(isolation);
        }
    }

    private void restoreSettings() {
        requestWlanSettings();
    }

    private void showApplySettingsDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Warning");
        builder.setMessage("ConnectedList will be restarted to apply new settings.");
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            applySettings();
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    private void applySettings() {
        boolean isAP2GStateChanged = mWifi2GSwitch.isChecked() != mOriginSettings.getAP2G().isApEnabled();
        boolean isAP5GStateChanged = mWifi5GSwitch.isChecked() != mOriginSettings.getAP5G().isApEnabled();

        // check 2.4g settings
        if (isAP2GStateChanged && !mWifi2GSwitch.isChecked()) {
            mEditedSettings.getAP2G().setApEnabled(false);
        } else {
            mEditedSettings.getAP2G().setApEnabled(true);
            String newSsid2G = mSsid2GEdit.getText().toString().trim();
            int newSecurity2GMode = mSecurity2GSpinner.getSelectedItemPosition();
            int newEncryption2G = mEncryption2GSpinner.getSelectedItemPosition();
            String newKey2G = mKey2GEdit.getText().toString().trim();

            if (newSsid2G.isEmpty()) {
                ToastUtil.showMessage(mContext, "Name(2.4G) is missing!");
                return;
            }
            mEditedSettings.getAP2G().Ssid = newSsid2G;
            mEditedSettings.getAP2G().SecurityMode = newSecurity2GMode;
            //disable
            if (newSecurity2GMode == 0) {
                mEditedSettings.getAP2G().WepKey = "";
                mEditedSettings.getAP2G().WepType = 0;
                mEditedSettings.getAP2G().WpaType = 0;
                mEditedSettings.getAP2G().WpaKey = "";
                //wep
            } else if (newSecurity2GMode == 1) {
                if (newKey2G.length() != 5 || newKey2G.length() != 13) {
                    ToastUtil.showMessage(mContext, "Wep password(2.4G) length must be 5 or 13!");
                    return;
                }
                mEditedSettings.getAP2G().WepType = newEncryption2G;
                mEditedSettings.getAP2G().WepKey = newKey2G;
                mEditedSettings.getAP2G().WpaType = 0;
                mEditedSettings.getAP2G().WpaKey = "";
                //wpa
            } else {
                if (newKey2G.length() < 8 || newKey2G.length() > 63) {
                    ToastUtil.showMessage(mContext, "Password(2.4G) length must be 8-63!");
                    return;
                }
                mEditedSettings.getAP2G().WepType = 0;
                mEditedSettings.getAP2G().WepKey = "";
                mEditedSettings.getAP2G().WpaType = newEncryption2G;
                mEditedSettings.getAP2G().WpaKey = newKey2G;
            }
        }

        // check 5g settings
        if (isAP5GStateChanged && !mWifi5GSwitch.isChecked()) {
            mEditedSettings.getAP5G().setApEnabled(false);
        } else {
            mEditedSettings.getAP5G().setApEnabled(true);
            String newSsid5G = mSsid5GEdit.getText().toString().trim();
            int newSecurity5GMode = mSecurity5GSpinner.getSelectedItemPosition();
            int newEncryption5G = mEncryption5GSpinner.getSelectedItemPosition();
            String newKey5G = mKey5GEdit.getText().toString().trim();

            if (newSsid5G.isEmpty()) {
                ToastUtil.showMessage(mContext, "Name(5G) is missing!");
                return;
            }
            mEditedSettings.getAP5G().Ssid = newSsid5G;
            mEditedSettings.getAP5G().SecurityMode = newSecurity5GMode;
            //disable
            if (newSecurity5GMode == 0) {
                mEditedSettings.getAP5G().WepKey = "";
                mEditedSettings.getAP5G().WepType = 0;
                mEditedSettings.getAP5G().WpaType = 0;
                mEditedSettings.getAP5G().WpaKey = "";
                //wep
            } else if (newSecurity5GMode == 1) {
                if (newKey5G.length() != 5 || newKey5G.length() != 13) {
                    ToastUtil.showMessage(mContext, "Wep password(5G) length must be 5 or 13!");
                    return;
                }
                mEditedSettings.getAP5G().WepType = newEncryption5G;
                mEditedSettings.getAP5G().WepKey = newKey5G;
                mEditedSettings.getAP5G().WpaType = 0;
                mEditedSettings.getAP5G().WpaKey = "";
                //wpa
            } else {
                if (newKey5G.length() < 8 || newKey5G.length() > 63) {
                    ToastUtil.showMessage(mContext, "Password(5G) length must be 8-63!");
                    return;
                }
                mEditedSettings.getAP5G().WepType = 0;
                mEditedSettings.getAP5G().WepKey = "";
                mEditedSettings.getAP5G().WpaType = newEncryption5G;
                mEditedSettings.getAP5G().WpaKey = newKey5G;
            }
        }
        Log.d(TAG, "mEditedSettings, " + mEditedSettings);

        API.get().setWlanSettings(mEditedSettings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil.showMessage(mContext, "Success");
            }

            @Override
            protected void onFailure() {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_security_2g) {
            if (position == 0) {
                mEncryption2GGroup.setVisibility(View.GONE);
                mKey2GGroup.setVisibility(View.GONE);
            } else if (position == 1) {
                mEncryption2GGroup.setVisibility(View.VISIBLE);
                mEncryption2GSpinner.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                if (mOriginSettings != null) {
                    mEncryption2GSpinner.setSelection(mOriginSettings.getAP2G().WepType);
                }
                mKey2GGroup.setVisibility(View.VISIBLE);
            } else {
                mEncryption2GGroup.setVisibility(View.VISIBLE);
                mEncryption2GSpinner.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                if (mOriginSettings != null) {
                    mEncryption2GSpinner.setSelection(mOriginSettings.getAP2G().WpaType);
                }
                mKey2GGroup.setVisibility(View.VISIBLE);
            }
        } else if (parent.getId() == R.id.spinner_security_5g) {
            if (position == 0) {
                mEncryption5GGroup.setVisibility(View.GONE);
                mKey5GGroup.setVisibility(View.GONE);
            } else if (position == 1) {
                mEncryption5GGroup.setVisibility(View.VISIBLE);
                mEncryption5GSpinner.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                if (mOriginSettings != null) {
                    mEncryption2GSpinner.setSelection(mOriginSettings.getAP5G().WepType);
                }
                mKey5GGroup.setVisibility(View.VISIBLE);
            } else {
                mEncryption5GGroup.setVisibility(View.VISIBLE);
                mEncryption5GSpinner.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                if (mOriginSettings != null) {
                    mEncryption2GSpinner.setSelection(mOriginSettings.getAP5G().WpaType);
                }
                mKey5GGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nothing to do
    }


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

}
