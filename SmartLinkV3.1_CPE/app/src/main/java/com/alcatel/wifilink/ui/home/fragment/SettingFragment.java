package com.alcatel.wifilink.ui.home.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.common.ToastUtil;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.connection.ConnectionState;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.system.WanSetting;
import com.alcatel.wifilink.model.update.DeviceNewVersion;
import com.alcatel.wifilink.model.update.DeviceUpgradeState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.activity.AboutActivity;
import com.alcatel.wifilink.ui.activity.EthernetWanConnectionActivity;
import com.alcatel.wifilink.ui.activity.SettingAccountActivity;
import com.alcatel.wifilink.ui.activity.SettingDeviceActivity;
import com.alcatel.wifilink.ui.activity.SettingLanguageActivity;
import com.alcatel.wifilink.ui.activity.SettingNetworkActivity;
import com.alcatel.wifilink.ui.activity.SettingShareActivity;
import com.alcatel.wifilink.utils.FileUtils;
import com.alcatel.wifilink.utils.OtherUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;

/**
 * Created by qianli.ma on 2017/6/16.
 */
@SuppressLint("ValidFragment")
public class SettingFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "SettingFragment";
    public static boolean isFtpSupported = false;
    public static boolean isDlnaSupported = false;
    public static boolean isSharingSupported = true;
    public static boolean m_blFirst = true;
    private int upgradeStatus;

    public static final String ISDEVICENEWVERSION = "IS_DEVICE_NEW_VERSION";
    private RelativeLayout mLoginPassword;
    private RelativeLayout mMobileNetwork;
    private RelativeLayout mEthernetWan;
    private RelativeLayout mSharingService;
    private RelativeLayout mLanguage;
    private RelativeLayout mFirmwareUpgrade;
    private RelativeLayout mRestart;
    private RelativeLayout mBackup;
    private RelativeLayout mAbout;
    private View m_view;

    private static final int RESTART_RESET = 1;
    private static final int Backup_Restore = 2;
    private ProgressDialog mProgressDialog;

    private TextView mDeviceVersion;
    private AlertDialog mCheckVersionDlg;
    private AlertDialog mUpdatingDlg;
    private final static String mSaveUrl = "/tcl/linkhub/backup";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = View.inflate(getActivity(), R.layout.fragment_home_setting, null);
        init();
        initEvent();
        return m_view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void init() {
        mLoginPassword = (RelativeLayout) m_view.findViewById(R.id.setting_login_password);
        mMobileNetwork = (RelativeLayout) m_view.findViewById(R.id.setting_mobile_network);
        mEthernetWan = (RelativeLayout) m_view.findViewById(R.id.setting_ethernet_wan);
        mSharingService = (RelativeLayout) m_view.findViewById(R.id.setting_sharing_service);
        mLanguage = (RelativeLayout) m_view.findViewById(R.id.setting_language);
        mFirmwareUpgrade = (RelativeLayout) m_view.findViewById(R.id.setting_firmware_upgrade);
        mRestart = (RelativeLayout) m_view.findViewById(R.id.setting_restart);
        mBackup = (RelativeLayout) m_view.findViewById(R.id.setting_backup);
        mAbout = (RelativeLayout) m_view.findViewById(R.id.setting_about);
        mDeviceVersion = (TextView) m_view.findViewById(R.id.setting_firmware_upgrade_version);
        getDeviceFWCurrentVersion();
        showSharingService();
    }

    private void showSharingService() {
       OtherUtils otherUtils = new OtherUtils();
       otherUtils.setOnDeviceVersionListener(new OtherUtils.OnDeviceVersionListener() {
           @Override
           public void getVersion(String deviceVersion) {
               if(deviceVersion.contains("HH40")){
                   mSharingService.setVisibility(View.GONE);
               }
           }
       });
        otherUtils.getDeviceHWVersion();
    }

    private void initEvent() {
        mLoginPassword.setOnClickListener(this);
        mMobileNetwork.setOnClickListener(this);
        mEthernetWan.setOnClickListener(this);
        mSharingService.setOnClickListener(this);
        mLanguage.setOnClickListener(this);
        mFirmwareUpgrade.setOnClickListener(this);
        mRestart.setOnClickListener(this);
        mBackup.setOnClickListener(this);
        mAbout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_login_password:
                goToAccountSettingPage();
                break;
            case R.id.setting_mobile_network:
                // TODO
                goToMobileNetworkSettingPage();
                break;
            case R.id.setting_ethernet_wan:
                goEthernetWanConnectionPage();
                break;
            case R.id.setting_sharing_service:
                if (isSharingSupported) {
                    goToShareSettingPage();
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.setting_not_support_sharing_service));
                }
                break;
            case R.id.setting_language:
                goSettingLanguagePage();
                break;
            case R.id.setting_firmware_upgrade:
                requestGetConnectionStatus();
                break;
            case R.id.setting_restart:
                popDialogFromBottom(RESTART_RESET);
                break;
            case R.id.setting_backup:
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    verifyStoragePermissions(getActivity());
                }
                popDialogFromBottom(Backup_Restore);
                break;
            case R.id.setting_about:
                goToAboutSettingPage();
                break;
            default:
                break;
        }
    }


    private void requestGetConnectionStatus() {
        Log.d(TAG, "requestGetConnectionStatus");
        API.get().getConnectionState(new MySubscriber<ConnectionState>() {
            @Override
            protected void onSuccess(ConnectionState result) {
                Log.d(TAG, "requestGetConnectionStatus,ConnectionStatus:" + result.getConnectionStatus());
                if (result.getConnectionStatus() == Constants.ConnectionStatus.CONNECTED || result.getConnectionStatus() == Constants.ConnectionStatus.CONNECTING) {
                    requestGetWanSetingRequest();
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.setting_upgrade_no_connection));
                }

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestGetConnectionStatus,onResultError:" + error);

            }

            @Override
            protected void onFailure() {
                super.onFailure();
                Log.d(TAG, "requestGetConnectionStatus");
            }
        });


    }

    private void requestGetWanSetingRequest() {
        Log.d(TAG, "requestSetCheckNewVersion");
        API.get().getWanSeting(new MySubscriber<WanSetting>() {
            @Override
            protected void onSuccess(WanSetting result) {
                Log.d(TAG, "requestGetWanSetingRequest,onSuccess:" + result.getStatus());
                if (result.getStatus() == Constants.WanSettingsStatus.CONNECTED || result.getStatus() == Constants.WanSettingsStatus.CONNECTING) {
                    requestSetCheckNewVersion();
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.check_your_wan_cabling));
                }

            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetWanSetingRequest,onFailure");
                super.onFailure();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Log.d(TAG, "requestGetWanSetingRequest,onResultError:" + error);
                super.onResultError(error);
            }
        });
    }

    private void popDialogFromBottom(int itemType) {
        PopupWindow popupWindow = new PopupWindow(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_from_bottom, null);

        TextView mFirstTxt = (TextView) view.findViewById(R.id.first_txt);
        TextView mSecondTxt = (TextView) view.findViewById(R.id.second_txt);
        TextView mCancelTxt = (TextView) view.findViewById(R.id.cancel_txt);
        popupWindow.setContentView(view);
        if (itemType == RESTART_RESET) {
            mFirstTxt.setText(R.string.restart);
            mSecondTxt.setText(R.string.reset_to_factory_settings);

        } else {
            mFirstTxt.setText(R.string.backup);
            mSecondTxt.setText(R.string.restore);
        }

        mFirstTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemType == RESTART_RESET) {
                    restartDevice();
                } else {
                    backupDevice();
                }
                popupWindow.dismiss();
            }
        });
        mSecondTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemType == RESTART_RESET) {
                    showDialogResetFactorySetting();
                } else {
                    restore();
                }
                popupWindow.dismiss();
            }
        });
        mCancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                backgroundAlpha(getActivity(), 1f);
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(getActivity(), 1f);
            }
        });
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        backgroundAlpha(getActivity(), 0.5f);
        popupWindow.setAnimationStyle(R.style.dialogStyle);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

    }

    private void backupDevice() {

        API.get().backupDevice(new MySubscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            protected void onSuccess(Object result) {
                dismissLoadingDialog();
                Log.d(TAG, "backup" + "sendAgainSuccess");
                showBackupSuccessDialog();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                dismissLoadingDialog();
                showFailedDialog(R.string.couldn_t_backup_try_again);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                dismissLoadingDialog();
                showFailedDialog(R.string.couldn_t_backup_try_again);
            }
        });


    }

    private void showBackupSuccessDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.back_up_settings);
        EditText editText = new EditText(getActivity());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParams);
        editText.setText(mSaveUrl);
        builder.setView(editText);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.backup, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downLoadConfigureFile(editText.getText().toString());
            }
        });
        builder.show();
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void downLoadConfigureFile(String saveUrl) {
        if (!saveUrl.startsWith("/")) {
            saveUrl = "/" + saveUrl;
        }
        File file = new File(FileUtils.createFilePath(saveUrl), "configure.bin");
        if (file.exists()) {
            file.delete();
        }
        Log.d(TAG, "downLoadConfigureFile:  " + file.getAbsolutePath());
        String downloadFileUrl = "/cfgbak/configure.bin";
        API.get().downConfigureFile(new Subscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onCompleted() {
                dismissLoadingDialog();
                ToastUtil.showMessage(getActivity(), "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                dismissLoadingDialog();
                ToastUtil.showMessage(getActivity(), "onError");
            }

            @Override
            public void onNext(Object o) {
                dismissLoadingDialog();
                ToastUtil.showMessage(getActivity(), "onNext");

            }
        }, downloadFileUrl, file);
    }


    private void showDialogResetFactorySetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.reset_router);
        builder.setMessage(R.string.This_will_reset_all_settings_on_your_router_to_factory_defaults_This_action_can_not_be_undone);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetDevice();

            }
        });
        builder.show();
    }

    private void restartDevice() {
        API.get().restartDevice(new MySubscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            protected void onSuccess(Object result) {
                ToastUtil.showMessage(getActivity(), "restart device sendAgainSuccess");
                dismissLoadingDialog();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                dismissLoadingDialog();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                dismissLoadingDialog();
            }
        });
    }


    private void resetDevice() {
        API.get().resetDevice(new MySubscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            protected void onSuccess(Object result) {
                dismissLoadingDialog();
                showSuccessDialog();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                dismissLoadingDialog();
                showFailedDialog(R.string.couldn_t_reset_try_again);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                dismissLoadingDialog();
                showFailedDialog(R.string.couldn_t_reset_try_again);
            }
        });

    }

    private void restore() {
        File file = new File(FileUtils.createFilePath(mSaveUrl), "configure.bin");
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("iptUpload", file.getName(), requestFile);
        API.get().uploadFile(new Subscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onCompleted() {
                dismissLoadingDialog();
                showSuccessDialog();
                Log.d(TAG, " restore onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onResultError " + e);
                ToastUtil.showMessage(getActivity(), R.string.couldn_t_restore_try_again);
                dismissLoadingDialog();
            }

            @Override
            public void onNext(Object o) {

            }

        }, body);
    }


    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.complete));
        builder.setCancelable(true);
        builder.show();
    }

    private void showFailedDialog(int stringId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(stringId));
        builder.setCancelable(true);
        builder.show();
    }

    private void showLoadingDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getActivity().getString(R.string.video_loading));
        mProgressDialog.show();

    }

    private void dismissLoadingDialog() {
        mProgressDialog.dismiss();
    }

    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }


    /* -------------------------------------------- HELPER -------------------------------------------- */
    /* -------------------------------------------- HELPER -------------------------------------------- */
    private void goToShareSettingPage() {
        Intent intent = new Intent(getActivity(), SettingShareActivity.class);
        getActivity().startActivity(intent);
    }

    private  void getDeviceFWCurrentVersion(){
        API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                Log.d(TAG,"getDeviceFWCurrentVersion,fw current version:"+result.getSwVersion());
                mDeviceVersion.setText(result.getSwVersion());
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

    private void requestSetCheckNewVersion() {
        Log.d(TAG, "requestSetCheckNewVersion,");
        API.get().setCheckNewVersion(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetCheckNewVersion,onSuccess:" + result);
                requestGetDeviceNewVersion();
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestSetCheckNewVersion,onFailure");
                super.onFailure();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Log.d(TAG, "requestSetCheckNewVersion,onResultError:" + error);
                super.onResultError(error);
            }
        });
    }

    private void requestGetDeviceNewVersion() {
        API.get().getDeviceNewVersion(new MySubscriber<DeviceNewVersion>() {
            @Override
            protected void onSuccess(DeviceNewVersion result) {
                Log.d(TAG, "requestGetDeviceNewVersion, state:" + result.getState());
                Log.d(TAG, "requestGetDeviceNewVersion, version:" + result.getVersion());
                Log.d(TAG, "requestGetDeviceNewVersion, size:" + result.getTotal_size());
                showCheckVersionDlg(result);
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetDeviceNewVersion, onFailure:");
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestGetDeviceNewVersion, onResultError:" + error);
            }
        });
    }

    private void showCheckVersionDlg(DeviceNewVersion result) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_firmware_update, null);
        builder.setView(view);
        builder.setCancelable(false);
        TextView mMessageContent = (TextView) view.findViewById(R.id.tv_content);
        mMessageContent.setVisibility(View.VISIBLE);
        String message = "";
        String positiveButtonStr = getString(R.string.ok);
        String negativeButtonStr = "";
        int eStatus = result.getState();
        if (eStatus == Constants.DeviceVersionCheckState.DEVICE_CHECKING) {
            message = getString(R.string.checking_for_update);
            positiveButtonStr = " ";
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    requestGetDeviceNewVersion();
                }
            }, 2 * 1000);
        } else if (Constants.DeviceVersionCheckState.DEVICE_NEW_VERSION == eStatus) {
            message = result.getVersion() + getString(R.string.available);
            positiveButtonStr = getString(R.string.update);
            negativeButtonStr = getString(R.string.cancel);

        } else if (Constants.DeviceVersionCheckState.DEVICE_NO_NEW_VERSION == eStatus) {
            message = mDeviceVersion.getText().toString() + "\n" + getString(R.string.your_firmware_is_up_to_date);
            positiveButtonStr = getString(R.string.ok);

        } else if (Constants.DeviceVersionCheckState.DEVICE_NO_CONNECT == eStatus || Constants.DeviceVersionCheckState.SERVICE_NOT_AVAILABLE == eStatus) {
            message = getString(R.string.setting_upgrade_check_firmware_failed);
            positiveButtonStr = getString(R.string.ok);
        } else if (Constants.DeviceVersionCheckState.DEVICE_CHECK_ERROR == eStatus) {
            message = getString(R.string.setting_upgrade_check_error);
            positiveButtonStr = getString(R.string.ok);
        }

        mMessageContent.setText(message);


        builder.setPositiveButton(positiveButtonStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (eStatus == Constants.DeviceVersionCheckState.DEVICE_NEW_VERSION) {
                    requestSetFOTAStartDownload();
                } else {
                    mCheckVersionDlg.dismiss();
                    mCheckVersionDlg.cancel();
                    mCheckVersionDlg = null;
                }
            }
        });

        builder.setNegativeButton(negativeButtonStr, null);
        if (mCheckVersionDlg == null) {
            mCheckVersionDlg = builder.create();
            mCheckVersionDlg.show();
        } else {
            if (eStatus != Constants.DeviceVersionCheckState.DEVICE_CHECKING) {
                mCheckVersionDlg.dismiss();
                mCheckVersionDlg.cancel();
                mCheckVersionDlg = builder.create();
                mCheckVersionDlg.show();
            }

        }


    }

    private void showUpgradeStateDlg(DeviceUpgradeState result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_firmware_update, null);
        TextView mContentText = (TextView) view.findViewById(R.id.tv_content);
        mContentText.setText("");
        ProgressBar mUpgradeBar = (ProgressBar) view.findViewById(R.id.pb_update_progress);
        TextView mUpgradeStatus = (TextView) view.findViewById(R.id.tv_update_status);
        if (result == null) {
            mUpgradeStatus.setVisibility(View.VISIBLE);
            mUpgradeBar.setVisibility(View.GONE);
            mUpgradeStatus.setCompoundDrawables(null, getResources().getDrawable(R.drawable.sms_prompt), null, null);
            mUpgradeStatus.setText(R.string.could_not_update_try_again);
            builder.setPositiveButton(R.string.ok, null);
        } else {
            int status = result.getStatus();
            if (Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_NOT_START == status) {
                Log.d(TAG, "requestGetDeviceUpgradeState,device upgrade not start");
            } else if (Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_UPDATING == status) {
                mUpgradeBar.setVisibility(View.VISIBLE);
                mUpgradeStatus.setVisibility(View.GONE);
                mContentText.setText(getString(R.string.updating) + result.getProcess());
                mUpgradeBar.setProgress(result.getProcess());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestGetDeviceUpgradeState();
                    }
                }, 2 * 1000);

            } else if (Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_COMPLETE == status) {
                requestSetDeviceStartUpdate();
                mUpgradeStatus.setVisibility(View.VISIBLE);
                mUpgradeBar.setVisibility(View.GONE);
                mUpgradeStatus.setCompoundDrawables(null, getResources().getDrawable(R.drawable.general_ic_check), null, null);
                mUpgradeStatus.setText(R.string.complete);

                builder.setPositiveButton(R.string.ok, null);
            }
        }

        builder.setView(view);
        builder.setCancelable(false);
        if (mUpdatingDlg == null) {
            mUpdatingDlg = builder.create();
            mUpdatingDlg.show();
        } else {
            if (result != null && result.getStatus() != 1) {
                mUpdatingDlg.dismiss();
                mUpdatingDlg.cancel();
                mUpdatingDlg = builder.create();
                mUpdatingDlg.show();
            }

        }


    }


    private void requestSetFOTAStartDownload() {
        API.get().SetFOTAStartDownload(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetFOTAStartDownload,onSuccess:" + result);
                requestGetDeviceUpgradeState();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestSetFOTAStartDownload,onResultError:" + error);
            }
        });
    }

    private void requestSetDeviceStartUpdate() {
        API.get().setDeviceStartUpdate(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetDeviceStartUpdate,onSuccess:" + result);

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestSetDeviceStartUpdate,onResultError:" + error);
            }
        });


    }

    private void requestGetDeviceUpgradeState() {
        API.get().getDeviceUpgradeState(new MySubscriber<DeviceUpgradeState>() {
            @Override
            protected void onSuccess(DeviceUpgradeState result) {
                Log.d(TAG, "requestGetDeviceUpgradeState, status:" + result.getStatus());
                Log.d(TAG, "requestGetDeviceUpgradeState, process:" + result.getProcess());
                showUpgradeStateDlg(result);
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetDeviceUpgradeState, onFailure:");
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestGetDeviceUpgradeState, onResultError:" + error);
                showUpgradeStateDlg(null);
            }
        });
    }

    private void goSettingLanguagePage() {
        Intent intent = new Intent(getActivity(), SettingLanguageActivity.class);
        getActivity().startActivity(intent);
    }

    private void goToAccountSettingPage() {
        // to setting account activity
        Intent intent = new Intent(getActivity(), SettingAccountActivity.class);
        getActivity().startActivity(intent);
    }

    private void goToMobileNetworkSettingPage() {
        // to setting network activity
        Intent intent = new Intent(getActivity(), SettingNetworkActivity.class);
        getActivity().startActivity(intent);
    }

    private void goEthernetWanConnectionPage() {
        Intent intent = new Intent(getActivity(), EthernetWanConnectionActivity.class);
        getActivity().startActivity(intent);
    }

    private void goToDeviceSettingPage() {
        // to setting connectDeviceList activity
        ChangeActivity.toActivity(getActivity(), SettingDeviceActivity.class, true, true, false, 0);
    }

    private void goToAboutSettingPage() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        intent.putExtra("upgradeStatus", upgradeStatus);
        getActivity().startActivity(intent);
    }

}
