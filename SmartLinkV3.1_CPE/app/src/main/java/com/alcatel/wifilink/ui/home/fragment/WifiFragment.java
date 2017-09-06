package com.alcatel.wifilink.ui.home.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.wlan.AP;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.SharedPrefsUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.wlan.WlanSettings;
import com.alcatel.wifilink.model.wlan.WlanSupportAPMode;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.RefreshWifiActivity;
import com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.wizard.allsetup.DataPlanActivity;
import com.alcatel.wifilink.ui.wizard.allsetup.WifiGuideActivity;
import com.alcatel.wifilink.ui.wizard.helper.WepPsdHelper;
import com.alcatel.wifilink.utils.OtherUtils;

import static android.app.Activity.RESULT_OK;
import static com.alcatel.wifilink.R.id.text_advanced_settings_2g;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_AP_ISOLATION;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_BANDWIDTH;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_CHANNEL;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_COUNTRY;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_FRE;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_MODE_80211;
import static com.alcatel.wifilink.ui.activity.WlanAdvancedSettingsActivity.EXTRA_SSID_BROADCAST;

public class WifiFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
    private ProgressDialog mProgressDialog;
    private String[] mWpaEncryptionSettings;
    private String[] mWepEncryptionSettings;
    private TimerHelper wifiTimer;

    public WifiFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestWlanSupportMode();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wifi_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        m2GSettingsGroup = (ViewGroup) view.findViewById(R.id.ll_settings_2g);
        m5GSettingsGroup = (ViewGroup) view.findViewById(R.id.ll_settings_5g);

        mWifi2GSwitch = (SwitchCompat) view.findViewById(R.id.switch_wifi_2g);
        mWifi5GSwitch = (SwitchCompat) view.findViewById(R.id.switch_wifi_5g);

        m2GAdvancedText = (TextView) view.findViewById(text_advanced_settings_2g);
        m2GAdvancedText.setOnClickListener(this);
        m5GAdvancedText = (TextView) view.findViewById(R.id.text_advanced_settings_5g);
        m5GAdvancedText.setOnClickListener(this);

        mSsid2GEdit = (EditText) view.findViewById(R.id.edit_ssid_2g);
        mSsid5GEdit = (EditText) view.findViewById(R.id.edit_ssid_5g);

        mKey2GEdit = (EditText) view.findViewById(R.id.edit_key_2g);
        mKey5GEdit = (EditText) view.findViewById(R.id.edit_key_5g);

        mEncryption2GGroup = (ViewGroup) view.findViewById(R.id.rl_encryption_2g);
        mEncryption5GGroup = (ViewGroup) view.findViewById(R.id.rl_encryption_5g);
        mKey2GGroup = (ViewGroup) view.findViewById(R.id.ll_key_2g);
        mKey5GGroup = (ViewGroup) view.findViewById(R.id.ll_key_5g);

        mSecurity2GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_security_2g);
        mSecurity5GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_security_5g);
        mSecurity2GSpinner.setOnItemSelectedListener(this);
        mSecurity5GSpinner.setOnItemSelectedListener(this);

        mEncryption2GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_encryption_2g);
        mEncryption5GSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_encryption_5g);

        view.findViewById(R.id.btn_apply).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);

        mDividerView = view.findViewById(R.id.divider);

        mWpaEncryptionSettings = getActivity().getResources().getStringArray(R.array.wlan_settings_wpa_type);
        mWepEncryptionSettings = getActivity().getResources().getStringArray(R.array.wlan_settings_wep_type);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                requestWlanSettings();
            }

            @Override
            protected void onFailure() {

            }
        });
    }

    private void updateUIWithWlanSettings() {
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
            mEncryption5GSpinner.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
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
            mEncryption2GSpinner.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
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
    public void onPause() {
        super.onPause();
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
            showApplySettingsDlg();
        } else if (v.getId() == R.id.btn_cancel) {
            restoreSettings();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((REQUEST_CODE_ADVANCED_SETTINGS_2_4G == requestCode || REQUEST_CODE_ADVANCED_SETTINGS_5G == requestCode) && resultCode == RESULT_OK) {
            boolean broadcast = data.getBooleanExtra(EXTRA_SSID_BROADCAST, false);
            int channel = data.getIntExtra(EXTRA_CHANNEL, 0);
            String countryCode = data.getStringExtra(EXTRA_COUNTRY);
            int bandwidth = data.getIntExtra(EXTRA_BANDWIDTH, 0);
            int mode80211 = data.getIntExtra(EXTRA_MODE_80211, 0);
            boolean isolation = data.getBooleanExtra(EXTRA_AP_ISOLATION, false);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.connectedlist_will_be_restarted_to_apply_new_settings);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            applySettings();
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    private void applySettings() {
        if (mSupportMode == ENUM.WlanSupportMode.Mode2Point4G.ordinal() || mSupportMode == ENUM.WlanSupportMode.Mode2Point4GAnd5G.ordinal()) {
            boolean isAP2GStateChanged = mWifi2GSwitch.isChecked() != mOriginSettings.getAP2G().isApEnabled();
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
                    ToastUtil_m.show(mContext, R.string.enter_2_4ghzssid);
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
                    if (!WepPsdHelper.psdMatch(newKey2G)) {
                        ToastUtil_m.show(mContext, getString(R.string.setting_wep_password_error_prompt));
                        return;
                    }
                    mEditedSettings.getAP2G().setWepType(newEncryption2G);
                    mEditedSettings.getAP2G().setWepKey(newKey2G);
                    mEditedSettings.getAP2G().setWpaType(0);
                    mEditedSettings.getAP2G().setWpaKey("");
                    //wpa
                } else {
                    if (newKey2G.length() < 8 || newKey2G.length() > 63) {
                        ToastUtil_m.show(mContext, R.string.password_2_4g_length_must_be_8_63);
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
            boolean isAP5GStateChanged = mWifi5GSwitch.isChecked() != mOriginSettings.getAP5G().isApEnabled();
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
                    ToastUtil_m.show(mContext, "Name(5G) is missing!");
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
                    if (!WepPsdHelper.psdMatch(newKey5G)) {
                        ToastUtil_m.show(mContext, getString(R.string.setting_wep_password_error_prompt));
                        return;
                    }
                    mEditedSettings.getAP5G().setWepType(newEncryption5G);
                    mEditedSettings.getAP5G().setWepKey(newKey5G);
                    mEditedSettings.getAP5G().setWpaType(0);
                    mEditedSettings.getAP5G().setWpaKey("");
                    //wpa
                } else {
                    if (newKey5G.length() < 8 || newKey5G.length() > 63) {
                        ToastUtil_m.show(mContext, "Password(5G) length must be 8-63!");
                        return;
                    }
                    mEditedSettings.getAP5G().setWepType(0);
                    mEditedSettings.getAP5G().setWepKey("");
                    mEditedSettings.getAP5G().setWpaType(newEncryption5G);
                    mEditedSettings.getAP5G().setWpaKey(newKey5G);
                }
            }
        }
        setWlanRequest();
    }


    /**
     * 真正发送请求
     */
    private void setWlanRequest() {
        API.get().setWlanSettings(mEditedSettings, new MySubscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            protected void onSuccess(Object result) {
                Log.d("ma_wififragment", "wififragment success");
                // checkLoginState();
                OtherUtils.setWifiActive(getActivity(),false);
                popDismiss();
                ChangeActivity.toActivity(getActivity(), RefreshWifiActivity.class, false, true, false, 0);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                popDismiss();
                // 提交设置过的标记
                SharedPrefsUtil.getInstance(getActivity()).putBoolean(Cons.WIFI_GUIDE_FLAG, true);
                // 点击后, router有可能马上掉线, 造成超时错误返回
                if (e.getMessage().toString().contains("ETIMEDOUT")) {
                    ChangeActivity.toActivity(getActivity(), RefreshWifiActivity.class, false, true, false, 0);
                } else {
                    // 跳转到月流量计划界面
                    ChangeActivity.toActivity(getActivity(), DataPlanActivity.class, false, true, false, 0);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                popDismiss();
                ChangeActivity.toActivity(getActivity(), RefreshWifiActivity.class, false, true, false, 0);
            }

            @Override
            protected void onFailure() {
                popDismiss();
            }
        });
    }

    private void checkLoginState() {
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                checkLoginState();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                error();
            }

            @Override
            protected void onFailure() {
                error();
            }

            @Override
            public void onError(Throwable e) {
                error();
            }
        });
    }

    /* 列表选项 */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_security_2g:
                if (i == 0) {
                    mKey2GGroup.setVisibility(View.GONE);
                    mEncryption2GGroup.setVisibility(View.GONE);
                    mEncryption2GSpinner.setSelection(-1);
                } else {
                    mKey2GGroup.setVisibility(View.VISIBLE);
                    mEncryption2GGroup.setVisibility(View.VISIBLE);
                    if (i == 1) {
                        mEncryption2GSpinner.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                        int wepType = mOriginSettings.getAP2G().getWepType();
                        mEncryption2GSpinner.setSelection(wepType);

                    } else {
                        mEncryption2GSpinner.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                        int wpaType = mOriginSettings.getAP2G().getWpaType();
                        mEncryption2GSpinner.setSelection(wpaType);
                    }
                }
                break;
            case R.id.spinner_security_5g:
                if (i == 0) {
                    mKey5GGroup.setVisibility(View.GONE);
                    mEncryption5GGroup.setVisibility(View.GONE);
                    mEncryption5GSpinner.setSelection(-1);
                } else {
                    mKey5GGroup.setVisibility(View.VISIBLE);
                    mEncryption5GGroup.setVisibility(View.VISIBLE);
                    if (i == 1) {
                        mEncryption5GSpinner.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mWepEncryptionSettings));
                        int wepType = mOriginSettings.getAP5G().getWepType();
                        mEncryption2GSpinner.setSelection(wepType);
                    } else {
                        mEncryption5GSpinner.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mWpaEncryptionSettings));
                        int wpaType = mOriginSettings.getAP5G().getWpaType();
                        mEncryption2GSpinner.setSelection(wpaType);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /* -------------------------------------------- helper -------------------------------------------- */
    private void error() {
        popDismiss();
        ToastUtil_m.show(mContext, getString(R.string.success));
        ChangeActivity.toActivity(getActivity(), RefreshWifiActivity.class, false, true, false, 0);
    }

    private void popDismiss() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


    private void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
        }
        mProgressDialog.setMessage(getString(R.string.setting_upgrading));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }
}
