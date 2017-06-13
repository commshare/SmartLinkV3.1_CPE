package com.alcatel.smartlinkv3.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

public class WlanAdvancedSettingsActivity extends AppCompatActivity {

    private static final String TAG = "WlanAdvancedSettingsAct";

    public static final String EXTRA_SSID_BROADCAST = "ssid_broadcast";
    public static final String EXTRA_CHANNEL = "channel";
    public static final String EXTRA_COUNTRY = "country";
    public static final String EXTRA_BANDWIDTH = "bandwidth";
    public static final String EXTRA_MODE_80211 = "80211_mode";
    public static final String EXTRA_AP_ISOLATION = "ap_isolation";

    private String mWlanType;

    private SwitchCompat switchSsidBroadcast;
//    private TextView textSecurityType;
//    private TextView textEncryptionType;
    private TextView textCountryType;
    private TextView textChannelType;
    private TextView text80211Mode;
    private SwitchCompat switchApIsolation;
    private TextView textBandwidthType;

//    private String[] mSecuritySettings;
//    private String[] mWpaEncryptionSettings;
//    private String[] mWepEncryptionSettings;
    private String[] mCountrySettings;
    private String[] mChannelSettings;
    private String[] m80211Settings;
    private String[] mBandwidthSettings;

//    private static final int DLG_TYPE_SECURITY = 0;
//    private static final int DLG_TYPE_WPA_ENCRYPTION = 1;
//    private static final int DLG_TYPE_WEP_ENCRYPTION = 2;
    private static final int DLG_TYPE_COUNTRY = 3;
    private static final int DLG_TYPE_CHANNEL = 4;
    private static final int DLG_TYPE_80211 = 5;
    private static final int DLG_TYPE_BANDWIDTH = 6;

    private boolean mSsidBroadcast;
//    private int mSecurity;
//    private int mWpaEncryption;
//    private int mWepEncryption;
    private String mCountry;
    private int mChannel;
    private int mMode80211;
    private boolean mApIsolation;
    private int mBandwidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlan_advanced_settings);
        ActionBar baseActionBar = getSupportActionBar();
        if (baseActionBar != null) {
            baseActionBar.setDisplayHomeAsUpEnabled(true);
            baseActionBar.setElevation(0);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initArrays();
        loadSettings();
        initViews();
    }

    private void loadSettings(){

        Intent intent = getIntent();
        if (intent != null) {
            mWlanType = intent.getStringExtra("wlan_type");
            Log.d(TAG, "onCreate, mWlanType:" + mWlanType);
        }
        mSsidBroadcast = intent.getIntExtra(EXTRA_SSID_BROADCAST, 0) == 0;
//        mSecurity = intent.getIntExtra("security", 0);
//        mWpaEncryption = intent.getIntExtra("wpa_type", 0);
//        mWepEncryption = intent.getIntExtra("wep_type", 0);
        mChannel = intent.getIntExtra(EXTRA_CHANNEL, 0);
        mCountry = intent.getStringExtra(EXTRA_COUNTRY);
        mBandwidth = intent.getIntExtra(EXTRA_BANDWIDTH, 0);
        mMode80211 = intent.getIntExtra(EXTRA_MODE_80211, 0);
        mApIsolation = intent.getIntExtra(EXTRA_AP_ISOLATION, 0) == 1;
    }

    private void initViews() {
        switchSsidBroadcast = (SwitchCompat) findViewById(R.id.switch_ssid_broadcast);
//        textSecurityType = (TextView) findViewById(R.id.text_security_type);
//        textEncryptionType = (TextView) findViewById(R.id.text_encryption_type);
        textCountryType = (TextView) findViewById(R.id.text_country_type);
        textChannelType = (TextView) findViewById(R.id.text_channel_type);
        text80211Mode = (TextView) findViewById(R.id.text_802_11_mode);
        switchApIsolation = (SwitchCompat) findViewById(R.id.switch_ap_isolation);
        textBandwidthType = (TextView) findViewById(R.id.text_bandwidth_type);

        switchSsidBroadcast.setChecked(mSsidBroadcast);
//        textSecurityType.setText(mSecuritySettings[mSecurity]);
//        updateEncryptionText();
        textCountryType.setText(mCountry);
        textChannelType.setText(mChannelSettings[mChannel]);
        text80211Mode.setText(m80211Settings[mMode80211]);
        switchApIsolation.setChecked(mApIsolation);
        textBandwidthType.setText(mBandwidthSettings[mBandwidth]);
    }

//    private void updateEncryptionText() {
//        if (mSecurity == 0){
//            textEncryptionType.setText("");
//        }else if (mSecurity == 1){
//            textEncryptionType.setText(mWepEncryptionSettings[mWepEncryption]);
//        }else {
//            textEncryptionType.setText(mWpaEncryptionSettings[mWpaEncryption]);
//        }
//    }

    private void initArrays() {
//        mSecuritySettings = getResources().getStringArray(R.array.wlan_settings_security);
//        mWpaEncryptionSettings = getResources().getStringArray(R.array.wlan_settings_wpa_type);
//        mWepEncryptionSettings = getResources().getStringArray(R.array.wlan_settings_wep_type);
        mCountrySettings = getResources().getStringArray(R.array.wlan_settings_country);
        mChannelSettings = getResources().getStringArray(R.array.wlan_settings_channel);
        m80211Settings = getResources().getStringArray(R.array.wlan_settings_80211);
        mBandwidthSettings = getResources().getStringArray(R.array.wlan_settings_bandwidth);
    }

//    public void onSecurityClick(View view) {
//        showSelectionDlg(DLG_TYPE_SECURITY);
//    }
//
//    public void onEncryptionClick(View view) {
//        if (mSecurity > 1) {
//            showSelectionDlg(DLG_TYPE_WPA_ENCRYPTION);
//        }else if (mSecurity == 1){
//            showSelectionDlg(DLG_TYPE_WEP_ENCRYPTION);
//        }
//    }

    public void onCountryClick(View view) {
        showSelectionDlg(DLG_TYPE_COUNTRY);
    }

    public void onChannelClick(View view) {
        showSelectionDlg(DLG_TYPE_CHANNEL);
    }

    public void on80211Click(View view) {
        showSelectionDlg(DLG_TYPE_80211);
    }

    public void onBandwidthClick(View view) {
        showSelectionDlg(DLG_TYPE_BANDWIDTH);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void showSelectionDlg(int type) {
        String title = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (type) {
//            case DLG_TYPE_SECURITY:
//                title = getResources().getString(R.string.security);
//                builder.setSingleChoiceItems(mSecuritySettings, mSecurity, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d(TAG, "which = " + which);
//                        mSecurity = which;
//                        updateEncryptionText();
//                    }
//                });
//                break;
//            case DLG_TYPE_WPA_ENCRYPTION:
//            case DLG_TYPE_WEP_ENCRYPTION:
//                title = getResources().getString(R.string.encryption);
//                if (mSecurity == 0){
//                    return;
//                }else if (mSecurity == 1){
//                    builder.setSingleChoiceItems(mWepEncryptionSettings, mWepEncryption, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Log.d(TAG, "which = " + which);
//                        }
//                    });
//                }else {
//                    builder.setSingleChoiceItems(mWpaEncryptionSettings, mWpaEncryption, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Log.d(TAG, "which = " + which);
//                        }
//                    });
//                }
//                break;
            case DLG_TYPE_CHANNEL:
                title = getResources().getString(R.string.channel);
                builder.setSingleChoiceItems(mChannelSettings, mChannel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "which = " + which);
                    }
                });
                break;
            case DLG_TYPE_COUNTRY:
                title = getResources().getString(R.string.country);
                int countryIndex = -1;
                for (int i = 0; i < mCountrySettings.length; i++){
                    if (mCountrySettings[i].equals(mCountry)){
                        countryIndex = i;
                    }
                }
                builder.setSingleChoiceItems(mCountrySettings, countryIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "which = " + which);
                    }
                });
                break;
            case DLG_TYPE_80211:
                title = getResources().getString(R.string.mode_802_11);
                builder.setSingleChoiceItems(m80211Settings, mMode80211, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "which = " + which);
                    }
                });
                break;
            case DLG_TYPE_BANDWIDTH:
                title = getResources().getString(R.string.bandwidth);
                builder.setSingleChoiceItems(mBandwidthSettings, mBandwidth, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "which = " + which);
                    }
                });
                break;
            default:
                break;
        }

        builder.setTitle(title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    public void OnOKClick(View view) {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
