package com.alcatel.wifilink.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.model.sharing.DLNASettings;
import com.alcatel.wifilink.model.sharing.FTPSettings;
import com.alcatel.wifilink.model.sharing.SambaSettings;
import com.alcatel.wifilink.model.system.SysStatus;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;

public class SettingShareActivity extends BaseActivityWithBack implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final String TAG = "SettingShareActivity";
    private TextView mUSBStorageText;
    private SwitchCompat mFTPSwitch;
    private SwitchCompat mSambaSwitch;
    private SwitchCompat mDLNASwitch;
    private ImageView mFTPStorageAccessImage;
    private ImageView mSambaStorageAccessImage;
    private ImageView mDLNAStorageAccessImage;
    private TimerHelper timerHelper;

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
        requestGetSystemStatus();
        requestGetFTPSettings();
        requestGetSambaSettings();
        requestGetDLNASettings();

    }

    private void requestGetSystemStatus() {
        RX.getInstant().getSystemStatus(new ResponseObject<SysStatus>() {
            @Override
            protected void onSuccess(SysStatus result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (result.getUsbStatus()) {
                            case Constants.DeviceUSBStatus.NOT_INSERT:
                                mUSBStorageText.setText(R.string.not_inserted);
                                break;
                            case Constants.DeviceUSBStatus.USB_STORAGE:
                                mUSBStorageText.setText(R.string.setting_usb_storage);
                                break;
                            case Constants.DeviceUSBStatus.USB_PRINT:
                                mUSBStorageText.setText(R.string.usb_printer);
                                showPrinterNameDlg(result.getUsbName());
                                break;
                        }
                    }
                });

            }
            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
            }
        });
    }

    private void requestGetFTPSettings() {
        RX.getInstant().getFTPSettings(new ResponseObject<FTPSettings>() {
            @Override
            protected void onSuccess(FTPSettings result) {
                mFTPSwitch.setChecked(result.getFtpStatus() == 1 ? true : false);

            }

            @Override
            protected void onFailure() {
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }
        });
    }

    private void requestGetSambaSettings() {
        RX.getInstant().getSambaSettings(new ResponseObject<SambaSettings>() {
            @Override
            protected void onSuccess(SambaSettings result) {
                mSambaSwitch.setChecked(result.getSambaStatus() == 1 ? true : false);
            }

            @Override
            protected void onFailure() {
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }
        });
    }

    private void requestGetDLNASettings() {
        RX.getInstant().getDLNASettings(new ResponseObject<DLNASettings>() {
            @Override
            protected void onSuccess(DLNASettings result) {
                mDLNASwitch.setChecked(result.getDlnaStatus() == 1 ? true : false);
            }

            @Override
            protected void onFailure() {
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }
        });
    }

    private void requestSetFTPSettings() {
        FTPSettings settings = new FTPSettings();
        settings.setFtpStatus(mFTPSwitch.isChecked() ? 1 : 0);
        settings.setAnonymous(0);
        settings.setAuthType(0);
        RX.getInstant().setFTPSettings(settings, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
            }
        });
    }

    private void requestSetSambaSettings() {
        SambaSettings settings = new SambaSettings();
        settings.setSambaStatus(mSambaSwitch.isChecked() ? 1 : 0);
        settings.setAnonymous(0);
        settings.setAuthType(0);
        RX.getInstant().setSambaSettings(settings, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
            }
        });
    }

    private void requestSetDLNASettings() {
        DLNASettings settings = new DLNASettings();
        settings.setDlnaStatus(mDLNASwitch.isChecked() ? 1 : 0);
        settings.setDlnaName("");
        RX.getInstant().setDLNASettings(settings, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }

            @Override
            protected void onFailure() {
                super.onFailure();
            }
        });
    }

    private void showPrinterNameDlg(String printerName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setTitle(R.string.usb_printers_nearby);
        builder.setMessage(printerName);
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timerHelper == null){

            timerHelper = new TimerHelper(SettingShareActivity.this) {
                @Override
                public void doSomething() {
                    requestGetSystemStatus();
                }
            };
            timerHelper.start(10*1000);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timerHelper != null) {
            timerHelper.stop();
            timerHelper = null;
        }
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
