package com.alcatel.wifilink.ui.setupwizard.allsetup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.AndroidException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.wlan.AP;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.wlan.WlanSettings;
import com.alcatel.wifilink.model.wlan.WlanSupportAPMode;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.activity.LoadingActivity;
import com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity;
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.type.ui.WanModeActivity;
import com.alcatel.wifilink.utils.ActionbarSetting;

import static com.alcatel.wifilink.R.id.text_advanced_settings_2g;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_AP_ISOLATION;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_BANDWIDTH;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_CHANNEL;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_COUNTRY;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_FRE;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_MODE_80211;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_SSID_BROADCAST;

public class WifiGuideActivity extends BaseActivityWithBack implements View.OnClickListener {

    private static final String TAG = "WifiFragment";

    public static final int REQUEST_CODE_ADVANCED_SETTINGS_2_4G = 0x1000;
    public static final int REQUEST_CODE_ADVANCED_SETTINGS_5G = 0x1001;


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


    private ViewGroup mEncryption2GGroup;
    private ViewGroup mEncryption5GGroup;

    private ViewGroup mKey2GGroup;
    private ViewGroup mKey5GGroup;

    private View mDividerView;

    private WlanSettings mOriginSettings;
    private WlanSettings mEditedSettings;

    private Context mContext;
    private int mSupportMode;
    private boolean isClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_guide);
        mContext = this;
        initView();
        initData();
    }

    private void initData() {
        requestWlanSupportMode();
    }

    private void initView() {
        m2GSettingsGroup = (ViewGroup) findViewById(R.id.ll_settings_2g);
        m5GSettingsGroup = (ViewGroup) findViewById(R.id.ll_settings_5g);

        mWifi2GSwitch = (SwitchCompat) findViewById(R.id.switch_wifi_2g);
        mWifi5GSwitch = (SwitchCompat) findViewById(R.id.switch_wifi_5g);

        m2GAdvancedText = (TextView) findViewById(text_advanced_settings_2g);
        m2GAdvancedText.setOnClickListener(this);
        m5GAdvancedText = (TextView) findViewById(R.id.text_advanced_settings_5g);
        m5GAdvancedText.setOnClickListener(this);

        mSsid2GEdit = (EditText) findViewById(R.id.edit_ssid_2g);
        mSsid5GEdit = (EditText) findViewById(R.id.edit_ssid_5g);

        mKey2GEdit = (EditText) findViewById(R.id.edit_key_2g);
        mKey5GEdit = (EditText) findViewById(R.id.edit_key_5g);

        mEncryption2GGroup = (ViewGroup) findViewById(R.id.rl_encryption_2g);
        mEncryption5GGroup = (ViewGroup) findViewById(R.id.rl_encryption_5g);
        mKey2GGroup = (ViewGroup) findViewById(R.id.ll_key_2g);
        mKey5GGroup = (ViewGroup) findViewById(R.id.ll_key_5g);

        mSecurity2GSpinner = (AppCompatSpinner) findViewById(R.id.spinner_security_2g);
        mSecurity5GSpinner = (AppCompatSpinner) findViewById(R.id.spinner_security_5g);

        mEncryption2GSpinner = (AppCompatSpinner) findViewById(R.id.spinner_encryption_2g);
        mEncryption5GSpinner = (AppCompatSpinner) findViewById(R.id.spinner_encryption_5g);

        findViewById(R.id.btn_apply).setOnClickListener(this);

        mDividerView = findViewById(R.id.divider);

        // set action bar
        setActionbar();

    }

    private void setActionbar() {
        new ActionbarSetting() {
            @Override
            protected void findActionbarView(View view) {
                View ib_back = view.findViewById(R.id.ib_wifiGuide_back);
                ib_back.setOnClickListener(WifiGuideActivity.this);
            }
        }.settingActionbarAttr(this, getSupportActionBar(), R.layout.actionbar_wifiguide);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_advanced_settings_2g) {
            Intent intent = new Intent(mContext, WlanAdvancedSettingsActivity.class);
            intent.putExtra(EXTRA_FRE, 2);
            intent.putExtra(EXTRA_SSID_BROADCAST, mEditedSettings.getAP2G().isSsidHiden());
            intent.putExtra(EXTRA_CHANNEL, mEditedSettings.getAP2G().getChannel());
            intent.putExtra(EXTRA_COUNTRY, mEditedSettings.getAP2G().getCountryCode());
            intent.putExtra(EXTRA_BANDWIDTH, mEditedSettings.getAP2G().getBandwidth());
            intent.putExtra(EXTRA_MODE_80211, mEditedSettings.getAP2G().getWMode());
            intent.putExtra(EXTRA_AP_ISOLATION, mEditedSettings.getAP2G().isApIsolated());
            startActivityForResult(intent, REQUEST_CODE_ADVANCED_SETTINGS_2_4G);
        } else if (v.getId() == R.id.text_advanced_settings_5g) {
            Intent intent = new Intent(mContext, WlanAdvancedSettingsActivity.class);
            intent.putExtra(EXTRA_FRE, 5);
            intent.putExtra(EXTRA_SSID_BROADCAST, mEditedSettings.getAP5G().isSsidHiden());
            intent.putExtra(EXTRA_CHANNEL, mEditedSettings.getAP5G().getChannel());
            intent.putExtra(EXTRA_COUNTRY, mEditedSettings.getAP5G().getCountryCode());
            intent.putExtra(EXTRA_BANDWIDTH, mEditedSettings.getAP5G().getBandwidth());
            intent.putExtra(EXTRA_MODE_80211, mEditedSettings.getAP5G().getWMode());
            intent.putExtra(EXTRA_AP_ISOLATION, mEditedSettings.getAP5G().isApIsolated());
            startActivityForResult(intent, REQUEST_CODE_ADVANCED_SETTINGS_5G);
        } else if (v.getId() == R.id.btn_apply) {
            isClick = true;
            showApplySettingsDlg();
        } else if (v.getId() == R.id.ib_wifiGuide_back) {
            finish();
        }
    }

    /* -------------------------------------------- helper -------------------------------------------- */
    private void requestWlanSupportMode() {
        API.get().getWlanSupportMode(new MySubscriber<WlanSupportAPMode>() {
            @Override
            protected void onSuccess(WlanSupportAPMode result) {
                updateUIWithSupportMode(result.getWlanSupportAPMode());
                requestWlanSettings();
            }

            @Override
            protected void onFailure() {

            }
        });
    }

    private void requestWlanSettings() {
        API.get().getWlanSettings(new MySubscriber<WlanSettings>() {
            @Override
            protected void onSuccess(WlanSettings result) {
                mOriginSettings = result;
                mEditedSettings = mOriginSettings.clone();
                updateUIWithWlanSettings();
                if (isClick) {
                    // TOAT: 跳过当前界面--> 主页面
                    setQuickAndSkip();
                }
            }

            @Override
            protected void onFailure() {

            }
        });
    }

    /**
     * 设置快速进入(下次启动忽略该界面)
     */
    public void setQuickAndSkip() {
        ChangeActivity.toActivity(WifiGuideActivity.this, HomeActivity.class, true, true, false, 0);
    }

    private void updateUIWithWlanSettings() {
        Log.d(TAG, "updateUIWithWlanSettings");
        if (mOriginSettings == null) {
            return;
        }
        if (mSupportMode == ENUM.WlanSupportMode.Mode2Point4G.ordinal()) {
            set2GData();
        } else if (mSupportMode == ENUM.WlanSupportMode.Mode5G.ordinal()) {
            set5GData();
        } else {
            set2GData();
            set5GData();
        }
    }

    private void set5GData() {
        mWifi5GSwitch.setChecked(mOriginSettings.getAP5G().isApEnabled());
        mSsid5GEdit.setText(mOriginSettings.getAP5G().getSsid());
        mSecurity5GSpinner.setSelection(mOriginSettings.getAP5G().getSecurityMode());
        if (mOriginSettings.getAP5G().getSecurityMode() == ENUM.SecurityMode.Disable.ordinal()) {
            mEncryption5GGroup.setVisibility(View.GONE);
            mKey5GGroup.setVisibility(View.GONE);
            mEncryption5GSpinner.setSelection(-1);
            mKey5GEdit.setText("");
        } else if (mOriginSettings.getAP5G().getSecurityMode() == ENUM.SecurityMode.WEP.ordinal()) {
            mEncryption5GGroup.setVisibility(View.VISIBLE);
            mKey5GGroup.setVisibility(View.VISIBLE);
            mEncryption5GSpinner.setSelection(mOriginSettings.getAP5G().getWepType());
            mKey5GEdit.setText(mOriginSettings.getAP5G().getWepKey());
        } else {
            mEncryption5GGroup.setVisibility(View.VISIBLE);
            mKey5GGroup.setVisibility(View.VISIBLE);
            mEncryption5GSpinner.setSelection(mOriginSettings.getAP5G().getWpaType());
            mKey5GEdit.setText(mOriginSettings.getAP5G().getWpaKey());
        }
    }

    private void set2GData() {
        mWifi2GSwitch.setChecked(mOriginSettings.getAP2G().isApEnabled());
        mSsid2GEdit.setText(mOriginSettings.getAP2G().getSsid());
        int securityMode = mOriginSettings.getAP2G().getSecurityMode();
        mSecurity2GSpinner.setSelection(securityMode);
        if (securityMode == ENUM.SecurityMode.Disable.ordinal()) {
            mEncryption2GGroup.setVisibility(View.GONE);
            mKey2GGroup.setVisibility(View.GONE);
            mEncryption2GSpinner.setSelection(-1);
            mKey2GEdit.setText("");
        } else if (securityMode == ENUM.SecurityMode.WEP.ordinal()) {
            mEncryption2GGroup.setVisibility(View.VISIBLE);
            mKey2GGroup.setVisibility(View.VISIBLE);
            mEncryption2GSpinner.setSelection(mOriginSettings.getAP2G().getWepType());
            mKey2GEdit.setText(mOriginSettings.getAP2G().getWepKey());
        } else {
            mEncryption2GGroup.setVisibility(View.VISIBLE);
            mKey2GGroup.setVisibility(View.VISIBLE);
            mEncryption2GSpinner.setSelection(mOriginSettings.getAP2G().getWpaType());
            mKey2GEdit.setText(mOriginSettings.getAP2G().getWpaKey());
        }
    }

    private void updateUIWithSupportMode(int supportMode) {
        mSupportMode = supportMode;
        if (supportMode == ENUM.WlanSupportMode.Mode2Point4G.ordinal()) {
            m2GSettingsGroup.setVisibility(View.VISIBLE);
            m5GSettingsGroup.setVisibility(View.GONE);
            mDividerView.setVisibility(View.GONE);
        } else if (supportMode == ENUM.WlanSupportMode.Mode5G.ordinal()) {
            m2GSettingsGroup.setVisibility(View.GONE);
            m5GSettingsGroup.setVisibility(View.VISIBLE);
            mDividerView.setVisibility(View.GONE);
        } else if (supportMode == ENUM.WlanSupportMode.Mode2Point4GAnd5G.ordinal()) {
            m2GSettingsGroup.setVisibility(View.VISIBLE);
            m5GSettingsGroup.setVisibility(View.VISIBLE);
        } else {
            m2GSettingsGroup.setVisibility(View.GONE);
            m5GSettingsGroup.setVisibility(View.GONE);
            mDividerView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode:" + requestCode);
        if ((REQUEST_CODE_ADVANCED_SETTINGS_2_4G == requestCode || REQUEST_CODE_ADVANCED_SETTINGS_5G == requestCode) && resultCode == RESULT_OK) {
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
            ap.setChannel(channel);
            ap.setCountryCode(countryCode);
            ap.setBandwidth(bandwidth);
            ap.setWMode(mode80211);
            ap.setApIsolated(isolation);
        }
    }

    private void restoreSettings() {
        requestWlanSettings();
    }

    private void showApplySettingsDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.connectedlist_will_be_restarted_to_apply_new_settings);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            applySettings();
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    private void applySettings() {
        boolean isAP2GStateChanged = mWifi2GSwitch.isChecked() != mOriginSettings.getAP2G().isApEnabled();
        boolean isAP5GStateChanged = mWifi5GSwitch.isChecked() != mOriginSettings.getAP5G().isApEnabled();
        if (mSupportMode == ENUM.WlanSupportMode.Mode2Point4G.ordinal() || mSupportMode == ENUM.WlanSupportMode.Mode2Point4GAnd5G.ordinal()) {
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
                    ToastUtil_m.show(this, getString(R.string.enter_2_4ghzssid));
                    return;
                }
                mEditedSettings.getAP2G().setSsid(newSsid2G);
                mEditedSettings.getAP2G().setSecurityMode(newSecurity2GMode);
                //disable
                if (newSecurity2GMode == 0) {
                    mEditedSettings.getAP2G().setWepKey("");
                    mEditedSettings.getAP2G().setWepType(0);
                    mEditedSettings.getAP2G().setWpaType(0);
                    mEditedSettings.getAP2G().setWpaKey("");
                    //wep
                } else if (newSecurity2GMode == 1) {
                    if (newKey2G.length() != 5 || newKey2G.length() != 13) {
                        ToastUtil_m.show(this, getString(R.string.wep_password_must_be_5_13_characters));
                        return;
                    }
                    mEditedSettings.getAP2G().setWepType(newEncryption2G);
                    mEditedSettings.getAP2G().setWepKey(newKey2G);
                    mEditedSettings.getAP2G().setWpaType(0);
                    mEditedSettings.getAP2G().setWpaKey("");
                    //wpa
                } else {
                    if (newKey2G.length() < 8 || newKey2G.length() > 63) {
                        ToastUtil_m.show(this, getString(R.string.password_2_4g_length_must_be_8_63));
                        return;
                    }
                    mEditedSettings.getAP2G().setWepType(0);
                    mEditedSettings.getAP2G().setWepKey("");
                    mEditedSettings.getAP2G().setWpaType(newEncryption2G);
                    mEditedSettings.getAP2G().setWpaKey(newKey2G);
                }
            }
        }
        if (mSupportMode == ENUM.WlanSupportMode.Mode5G.ordinal() || mSupportMode == ENUM.WlanSupportMode.Mode2Point4GAnd5G.ordinal()) {
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
                    ToastUtil_m.show(this, getString(R.string.wifi_5g_missing));
                    return;
                }
                mEditedSettings.getAP5G().setSsid(newSsid5G);
                mEditedSettings.getAP5G().setSecurityMode(newSecurity5GMode);
                //disable
                if (newSecurity5GMode == 0) {
                    mEditedSettings.getAP5G().setWepKey("");
                    mEditedSettings.getAP5G().setWepType(0);
                    mEditedSettings.getAP5G().setWpaType(0);
                    mEditedSettings.getAP5G().setWpaKey("");
                    //wep
                } else if (newSecurity5GMode == 1) {
                    if (newKey5G.length() != 5 || newKey5G.length() != 13) {
                        ToastUtil_m.show(this, getString(R.string.wep_psd_length_must_5_or_13));
                        return;
                    }
                    mEditedSettings.getAP5G().setWepType(newEncryption5G);
                    mEditedSettings.getAP5G().setWepKey(newKey5G);
                    mEditedSettings.getAP5G().setWpaType(0);
                    mEditedSettings.getAP5G().setWpaKey("");
                    //wpa
                } else {
                    if (newKey5G.length() < 8 || newKey5G.length() > 63) {
                        ToastUtil_m.show(this, getString(R.string.psd_length_must_be_8_63));
                        return;
                    }
                    mEditedSettings.getAP5G().setWepType(0);
                    mEditedSettings.getAP5G().setWepKey("");
                    mEditedSettings.getAP5G().setWpaType(newEncryption5G);
                    mEditedSettings.getAP5G().setWpaKey(newKey5G);
                }
            }
        }
        Log.d(TAG, "mEditedSettings, " + mEditedSettings);

        API.get().setWlanSettings(mEditedSettings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(mContext, getString(R.string.setting_success));
                // TOAT: 点击APPLY设置完毕后
                ChangeActivity.toActivity(WifiGuideActivity.this, LoadingActivity.class, true, true, false, 0);
            }

            @Override
            protected void onFailure() {

            }
        });
    }


}