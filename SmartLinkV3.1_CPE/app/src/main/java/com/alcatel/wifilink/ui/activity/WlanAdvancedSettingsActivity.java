package com.alcatel.wifilink.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.alcatel.wifilink.R;

import java.util.Arrays;
import java.util.List;

public class WlanAdvancedSettingsActivity extends BaseActivityWithBack {

    private static final String TAG = "WlanAdvancedSettingsAct";
    public static final String EXTRA_FRE = "frequency";
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

    private String[] mAllCountryNames;

    private boolean mSsidBroadcast;

    private String mCountry;
    private int mChannel;
    private int mMode;
    private boolean mApIsolation;
    private int mBandwidth;
    private String[] mMode2GStrings;
    private String[] mMode5GStrings;
    private int mFrequency;
    private String[] mBandwidth2GStrings;
    private String[] mBandwidth5GStrings;
    private String[] mChannel5gOne;
    private String[] mChannel5gTwo;
    private String[] mChannel5gThree;
    private String[] mChannel5gfour;
    private String[] mChannel5gfive;
    private List<String> mChannel5gOneCountryCodes;
    private List<String> mChannel5gFiveCountryCodes;
    private int mChannel5g;

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
            mSsidBroadcast = intent.getBooleanExtra(EXTRA_SSID_BROADCAST, true);
            mChannel = intent.getIntExtra(EXTRA_CHANNEL, 0);
            mFrequency = intent.getIntExtra(EXTRA_FRE, 0);
            mCountry = intent.getStringExtra(EXTRA_COUNTRY);
            mBandwidth = intent.getIntExtra(EXTRA_BANDWIDTH, 0);
            mMode = intent.getIntExtra(EXTRA_MODE_80211, 0);
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

        ArrayAdapter<String> channelAdapter = null;

        if (mFrequency == 5) {
            if (mChannel5gOneCountryCodes.contains(mCountry)) {
                for (int i = 0; i < mChannel5gOne.length; i++) {
                    String channel = String.valueOf(mChannel);
                    if (channel.equals(mChannel5gOne[i])) {
                        mChannel = i;
                    }

                }
                channelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mChannel5gOne);
                channelAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                mChannelSpinner.setAdapter(channelAdapter);
            } else if (mChannel5gFiveCountryCodes.contains(mCountry)) {
                for (int i = 0; i < mChannel5gfive.length; i++) {
                    String channel = String.valueOf(mChannel);
                    if (channel.equals(mChannel5gfive[i])) {
                        mChannel = i;
                    }
                    channelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mChannel5gfive);
                    channelAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    mChannelSpinner.setAdapter(channelAdapter);
                }
            }
        }

        // 0: Auto
        // 1: 802.11b
        // 2: 802.11b/g
        // 3: 802.11b/g/n
        // 4: 802.11a
        // 5: 802.11a/n
        ArrayAdapter<String> modeAdapter = null;
        if (mFrequency == 2) {
            modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mMode2GStrings);
            modeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            m80211Spinner.setAdapter(modeAdapter);
        } else if (mFrequency == 5) {
            if (mMode > 3) {
                mMode = mMode - 3;
            }
            modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mMode5GStrings);
            modeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            m80211Spinner.setAdapter(modeAdapter);
        }
        //0: 20MHz/40MHz
        //1: 20MHz
        //2: 40MHz
        //3: 80MHz
        //4: 40MHz/80MHz
        ArrayAdapter<String> bandwidthAdapter = null;
        if (mFrequency == 2) {
            bandwidthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBandwidth2GStrings);
            bandwidthAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mBandwidthSpinner.setAdapter(bandwidthAdapter);
        } else if (mFrequency == 5) {
            if (mBandwidth == 4) {
                mBandwidth = 0;
            }
            bandwidthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBandwidth5GStrings);
            bandwidthAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mBandwidthSpinner.setAdapter(bandwidthAdapter);
        }

        m80211Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMode = i;
                if (mFrequency == 5 && mMode != 0) {
                    mMode = mMode + 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mBandwidthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mBandwidth = i;
                if (mFrequency == 5 && mBandwidth == 0) {
                    mBandwidth = 4;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final int[] time = {0};
        if (mFrequency == 5) {
            mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String countryCode = getCountryCode(mAllCountryNames[i]);
                    if (mChannel5gFiveCountryCodes.contains(countryCode)) {
                        ArrayAdapter<String> channelAdapter = new ArrayAdapter(WlanAdvancedSettingsActivity.this, android.R.layout.simple_list_item_1, mChannel5gfive);
                        channelAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        mChannelSpinner.setAdapter(channelAdapter);
                    } else if (mChannel5gOneCountryCodes.contains(countryCode)) {
                        ArrayAdapter<String> channelAdapter = new ArrayAdapter(WlanAdvancedSettingsActivity.this, android.R.layout.simple_list_item_1, mChannel5gOne);
                        channelAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        mChannelSpinner.setAdapter(channelAdapter);
                    }
                    if (time[0] == 0) {
                        mChannelSpinner.setSelection(mChannel);
                        time[0]++;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        mChannelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mFrequency == 5) {
                    mChannel5g = Integer.decode((String) mChannelSpinner.getAdapter().getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mBroadcastSwitch.setChecked(mSsidBroadcast);
        mChannelSpinner.setSelection(mChannel);
        mCountrySpinner.setSelection(getCountryPos(mCountry));
        m80211Spinner.setSelection(mMode);
        mIsolationSwitch.setChecked(mApIsolation);
        mBandwidthSpinner.setSelection(mBandwidth);
    }

    private int getCountryPos(String countryStr) {
        String countryName = getCountryName(countryStr);
        for (int i = 0; i < mAllCountryNames.length; i++) {
            if (mAllCountryNames[i].equals(countryName)) {
                return i;
            }
        }
        return -1;
    }

    private void initArrays() {
        mAllCountryNames = getResources().getStringArray(R.array.wlan_settings_country);
        mMode2GStrings = getResources().getStringArray(R.array.wlan_settings_80211_2g);
        mMode5GStrings = getResources().getStringArray(R.array.wlan_settings_80211_5g);
        mBandwidth2GStrings = getResources().getStringArray(R.array.wlan_settings_bandwidth_2g);
        mBandwidth5GStrings = getResources().getStringArray(R.array.wlan_settings_bandwidth_5g);
        mChannel5gOne = getResources().getStringArray(R.array.Channel5g_one);
        mChannel5gTwo = getResources().getStringArray(R.array.Channel5g_two);
        mChannel5gThree = getResources().getStringArray(R.array.Channel5g_three);
        mChannel5gfour = getResources().getStringArray(R.array.Channel5g_four);
        mChannel5gfive = getResources().getStringArray(R.array.Channel5g_five);

        String[] oneStrings = {"GB", "IT", "FR", "PT", "ES", "MX"};
        String[] fiveStrings = new String[]{"US", "HK", "CN"};
        mChannel5gFiveCountryCodes = Arrays.asList(oneStrings);
        mChannel5gOneCountryCodes = Arrays.asList(fiveStrings);
    }

    public void OnOKClick(View view) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SSID_BROADCAST, mBroadcastSwitch.isChecked());
        if (mFrequency == 5) {
            intent.putExtra(EXTRA_CHANNEL, mChannel5g);
        } else {
            intent.putExtra(EXTRA_CHANNEL, mChannelSpinner.getSelectedItemPosition());
        }
        intent.putExtra(EXTRA_COUNTRY, getCountryCode(mAllCountryNames[mCountrySpinner.getSelectedItemPosition()]));
        intent.putExtra(EXTRA_BANDWIDTH, mBandwidth);
        intent.putExtra(EXTRA_MODE_80211, mMode);
        intent.putExtra(EXTRA_AP_ISOLATION, mIsolationSwitch.isChecked());
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getCountryCode(String countryName) {
        /*
        UNITED KINGDOM
        ITALY
        FRANCE
        PORTUGAL
        ESPAÑA
        UNITED STATES
        HONG KONG
        CHINA
         */
        String countryCode = "GB";
        switch (countryName) {
            case "UNITED KINGDOM":
                countryCode = "GB";// Channel5g.five
                break;
            case "ITALY":
                countryCode = "IT";//Channel5g.five
                break;
            case "FRANCE":
                countryCode = "FR";//Channel5g.five
                break;
            case "PORTUGAL":
                countryCode = "PT";//Channel5g.five
                break;
            case "ESPAÑA":
                countryCode = "ES";//Channel5g.five
                break;
            case "UNITED STATES":
                countryCode = "US";//Channel5g.one
                break;
            case "HONG KONG":
                countryCode = "HK";//Channel5g.one
                break;
            case "CHINA":
                countryCode = "CN";//Channel5g.one
                break;
            case "MEXICO":
                countryCode = "MX";
                break;
        }
        return countryCode;
    }

    private String getCountryName(String counstrCode) {

        String countryName = "UNITED KINGDOM";
        switch (counstrCode) {
            case "GB":
                countryName = "UNITED KINGDOM";// Channel5g.five
                break;
            case "IT":
                countryName = "ITALY";//Channel5g.five
                break;
            case "FR":
                countryName = "FRANCE";//Channel5g.five
                break;
            case "PT":
                countryName = "PORTUGAL";//Channel5g.five
                break;
            case "ES":
                countryName = "ESPAÑA";//Channel5g.five
                break;
            case "US":
                countryName = "UNITED STATES";//Channel5g.one
                break;
            case "HK":
                countryName = "HONG KONG";//Channel5g.one
                break;
            case "CN":
                countryName = "CHINA";//Channel5g.one
                break;
            case "MX":
                countryName = "MEXICO";
                break;
        }
        return countryName;
    }

}
