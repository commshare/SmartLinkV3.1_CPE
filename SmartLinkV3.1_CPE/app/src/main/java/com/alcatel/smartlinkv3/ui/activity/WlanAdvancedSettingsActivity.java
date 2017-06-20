package com.alcatel.smartlinkv3.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.alcatel.smartlinkv3.R;

public class WlanAdvancedSettingsActivity extends BaseActivityWithBack {

    private static final String TAG = "WlanAdvancedSettingsAct";

    public static final String EXTRA_SSID_BROADCAST = "ssid_broadcast";
    public static final String EXTRA_CHANNEL = "channel";
    public static final String EXTRA_COUNTRY = "country";
    public static final String EXTRA_BANDWIDTH = "bandwidth";
    public static final String EXTRA_MODE_80211 = "80211_mode";
    public static final String EXTRA_AP_ISOLATION = "ap_isolation";

    private SwitchCompat mBroadcastSwitch;

    private AppCompatSpinner mCountrySpinner;
    private AppCompatSpinner mChannelSpinner;
    private AppCompatSpinner m80211Spinner;
    private SwitchCompat mIsolationSwitch;
    private AppCompatSpinner mBandwidthSpinner;

    private String[] mCountrySettings;

    private boolean mSsidBroadcast;

    private String mCountry;
    private int mChannel;
    private int mMode80211;
    private boolean mApIsolation;
    private int mBandwidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlan_advanced_settings);
        initArrays();
        loadSettings();
        initViews();
    }

    private void loadSettings() {

        Intent intent = getIntent();
        if (intent != null) {
            mSsidBroadcast = intent.getBooleanExtra(EXTRA_SSID_BROADCAST, false);
            mChannel = intent.getIntExtra(EXTRA_CHANNEL, 0);
            mCountry = intent.getStringExtra(EXTRA_COUNTRY);
            mBandwidth = intent.getIntExtra(EXTRA_BANDWIDTH, 0);
            mMode80211 = intent.getIntExtra(EXTRA_MODE_80211, 0);
            mApIsolation = intent.getBooleanExtra(EXTRA_AP_ISOLATION, false);
        }

    }

    private void initViews() {
        mBroadcastSwitch = (SwitchCompat) findViewById(R.id.switch_ssid_broadcast);

        mCountrySpinner = (AppCompatSpinner) findViewById(R.id.spinner_country_type);
        mChannelSpinner = (AppCompatSpinner) findViewById(R.id.spinner_channel_type);
        m80211Spinner = (AppCompatSpinner) findViewById(R.id.spinner_802_11_mode);
        mIsolationSwitch = (SwitchCompat) findViewById(R.id.switch_ap_isolation);
        mBandwidthSpinner = (AppCompatSpinner) findViewById(R.id.spinner_bandwidth_type);

        mBroadcastSwitch.setChecked(mSsidBroadcast);
        mCountrySpinner.setSelection(getCountryPos(mCountry));
        mChannelSpinner.setSelection(mChannel);
        m80211Spinner.setSelection(mMode80211);
        mIsolationSwitch.setChecked(mApIsolation);
        mBandwidthSpinner.setSelection(mBandwidth);
    }

    private int getCountryPos(String countryStr) {
        for (int i = 0; i < mCountrySettings.length; i++) {
            if (mCountrySettings[i].equals(countryStr)) {
                return i;
            }
        }
        return -1;
    }


    private void initArrays() {
        mCountrySettings = getResources().getStringArray(R.array.wlan_settings_country);
    }

    public void OnOKClick(View view) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SSID_BROADCAST, mBroadcastSwitch.isChecked());
        intent.putExtra(EXTRA_CHANNEL, mChannelSpinner.getSelectedItemPosition());
        intent.putExtra(EXTRA_COUNTRY, mCountrySettings[mCountrySpinner.getSelectedItemPosition()]);
        intent.putExtra(EXTRA_BANDWIDTH, mBandwidthSpinner.getSelectedItemPosition());
        intent.putExtra(EXTRA_MODE_80211, m80211Spinner.getSelectedItemPosition());
        intent.putExtra(EXTRA_AP_ISOLATION, mIsolationSwitch.isChecked());
        setResult(RESULT_OK, intent);
        finish();
    }

}
