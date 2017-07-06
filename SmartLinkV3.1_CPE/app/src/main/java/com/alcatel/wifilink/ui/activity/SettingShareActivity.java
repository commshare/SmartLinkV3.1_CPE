package com.alcatel.wifilink.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.model.sharing.DLNASettings;
import com.alcatel.wifilink.model.sharing.FTPSettings;
import com.alcatel.wifilink.model.sharing.SambaSettings;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;

public class SettingShareActivity extends BaseActivityWithBack implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final String TAG = "SettingShareActivity";
    private final int USB_STATUS_ON_INSERT = 0;
    private final int USB_STATUS_USB_STORAGE = 1;
    private final int USB_STATUS_USB_PRINT = 2;
    private TextView mUSBStorageText;
    private SwitchCompat mFTPSwitch;
    private SwitchCompat mSambaSwitch;
    private SwitchCompat mDLNASwitch;
    private ImageView mFTPStorageAccessImage;
    private ImageView mSambaStorageAccessImage;
    private ImageView mDLNAStorageAccessImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_share);
        initView();
        initData();
    }

    private void initView() {
        mUSBStorageText = (TextView) findViewById(R.id.tv_usb_storage);
        mFTPSwitch = (SwitchCompat) findViewById(R.id.switch_ftp);
        mSambaSwitch = (SwitchCompat) findViewById(R.id.switch_samba);
        mDLNASwitch = (SwitchCompat) findViewById(R.id.switch_dlna);
        mFTPStorageAccessImage = (ImageView) findViewById(R.id.iv_ftp_access_storage);
        mSambaStorageAccessImage = (ImageView) findViewById(R.id.iv_samba_access_storage);
        mDLNAStorageAccessImage = (ImageView) findViewById(R.id.iv_dlna_access_storage);
        mFTPSwitch.setOnCheckedChangeListener(this);
        mSambaSwitch.setOnCheckedChangeListener(this);
        mDLNASwitch.setOnCheckedChangeListener(this);
        mFTPStorageAccessImage.setOnClickListener(this);
        mSambaStorageAccessImage.setOnClickListener(this);
        mDLNAStorageAccessImage.setOnClickListener(this);
    }

    private void initData() {

        int usbStatus = BusinessManager.getInstance().getSystemStatus().getUsbStatus();
        Log.d(TAG,"initData,usb status:"+usbStatus);
        switch (usbStatus){
            case USB_STATUS_ON_INSERT:
                mUSBStorageText.setText(R.string.no_insert);break;
            case USB_STATUS_USB_STORAGE: mUSBStorageText.setText(R.string.setting_usb_storage);break;
            case USB_STATUS_USB_PRINT:mUSBStorageText.setText(R.string.usb_printer);break;
        }
        requestGetFTPSettings();
        requestGetSambaSettings();
        requestGetDLNASettings();

    }

    private void requestGetFTPSettings() {
        API.get().getFTPSettings(new MySubscriber<FTPSettings>() {
            @Override
            protected void onSuccess(FTPSettings result) {
                Log.d(TAG, "requestGetFTPSettings, status:" + result.getFtpStatus());
                Log.d(TAG, "requestGetFTPSettings, anonymous:" + result.getAnonymous());
                Log.d(TAG, "requestGetFTPSettings, auth type:" + result.getAuthType());
                mFTPSwitch.setChecked(result.getFtpStatus() == 1 ? true : false);

            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetFTPSettings, onFailure:");
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestGetFTPSettings, onResultError:" + error);
            }
        });
    }

    private void requestGetSambaSettings() {
        API.get().getSambaSettings(new MySubscriber<SambaSettings>() {
            @Override
            protected void onSuccess(SambaSettings result) {
                Log.d(TAG, "requestGetSambaSettings, status:" + result.getSambaStatus());
                Log.d(TAG, "requestGetSambaSettings, anonymous:" + result.getAnonymous());
                Log.d(TAG, "requestGetSambaSettings, auth type:" + result.getAuthType());
                mSambaSwitch.setChecked(result.getSambaStatus() == 1 ? true : false);
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetSambaSettings, onFailure:");
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestGetSambaSettings, onResultError:" + error);
            }
        });
    }

    private void requestGetDLNASettings() {
        API.get().getDLNASettings(new MySubscriber<DLNASettings>() {
            @Override
            protected void onSuccess(DLNASettings result) {
                Log.d(TAG, "requestGetDLNASettings, status:" + result.getDlnaStatus());
                Log.d(TAG, "requestGetDLNASettings, name:" + result.getDlnaName());
                mDLNASwitch.setChecked(result.getDlnaStatus() == 1 ? true : false);
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetDLNASettings, onFailure:");
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestGetDLNASettings, onResultError:" + error);
            }
        });
    }

    private void requestSetFTPSettings() {
        FTPSettings settings = new FTPSettings();
        settings.setFtpStatus(mFTPSwitch.isChecked() ? 1 : 0);
        settings.setAnonymous(0);
        settings.setAuthType(0);
        Log.d(TAG, "requestSetFTPSettings,settings:" + settings);
        API.get().setFTPSettings(settings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetFTPSettings, result:" + result);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestSetFTPSettings, error:" + error);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                Log.d(TAG, "requestSetFTPSettings, onFailure");
            }
        });
    }

    private void requestSetSambaSettings() {
        SambaSettings settings = new SambaSettings();
        settings.setSambaStatus(mSambaSwitch.isChecked() ? 1 : 0);
        settings.setAnonymous(0);
        settings.setAuthType(0);
        Log.d(TAG, "requestSetSambaSettings,settings:" + settings);
        API.get().setSambaSettings(settings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetSambaSettings, result:" + result);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestSetSambaSettings, error:" + error);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                Log.d(TAG, "requestSetSambaSettings, onFailure");
            }
        });
    }

    private void requestSetDLNASettings() {
        DLNASettings settings = new DLNASettings();
        settings.setDlnaStatus(mDLNASwitch.isChecked() ? 1 : 0);
        settings.setDlnaName("");
        Log.d(TAG, "requestSetDLNASettings,settings:" + settings);
        API.get().setDLNASettings(settings, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetDLNASettings, result:" + result);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestSetDLNASettings, error:" + error);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                Log.d(TAG, "requestSetDLNASettings, onFailure");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_ftp:
                requestSetFTPSettings();
                break;
            case R.id.switch_samba:
                requestSetSambaSettings();
                break;
            case R.id.switch_dlna:
                requestSetDLNASettings();
                break;
        }

    }
}
