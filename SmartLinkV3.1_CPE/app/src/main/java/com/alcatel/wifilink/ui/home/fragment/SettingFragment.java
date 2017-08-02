package com.alcatel.wifilink.ui.home.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.Constants;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.fileexplorer.Util;
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
import com.alcatel.wifilink.ui.home.allsetup.HomeActivity;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.utils.FileUtils;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.SPUtils;

import java.io.File;
import java.net.SocketTimeoutException;

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
    private final static String CONFIG_SPNAME = "config";
    private final static String CONFIG_FILE_PATH = "configFilePath";
    public final static int SET_LANGUAGE_REQUEST = 0x001;
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
    private final static String mdefaultSaveUrl = "/tcl/linkhub/backup";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ProgressDialog mCheckingDlg;
    private TimerHelper timerHelper;
    private AlertDialog mUpgradingDlg;
    private AlertDialog mUpgradedDlg;
    private final int PROGRESS_STYLE_WAITING = -1;
    ProgressBar mUpgradingProgressBar;
    TextView mUpgradingProgressValue;


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
                if (deviceVersion.contains("HH40")) {
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
                requestGetWanSettingRequest();
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
                if (result.getConnectionStatus() == Constants.ConnectionStatus.CONNECTED) {
                    requestSetCheckNewVersion();
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

    private void requestGetWanSettingRequest() {
        Log.d(TAG, "requestSetCheckNewVersion");
        API.get().getWanSeting(new MySubscriber<WanSetting>() {
            @Override
            protected void onSuccess(WanSetting result) {
                Log.d(TAG, "requestGetWanSettingRequest,onSuccess:" + result.getStatus());
                if (result.getStatus() == Constants.WanSettingsStatus.CONNECTED) {
                    requestSetCheckNewVersion();
                } else {
                    requestGetConnectionStatus();
                }

            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetWanSettingRequest,onFailure");
                super.onFailure();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Log.d(TAG, "requestGetWanSettingRequest,onResultError:" + error);
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

    // TODO: 2017/8/1 备份文件
    public int requestTimes = 0;

    private void backupDevice() {

        API.get().backupDevice(new MySubscriber() {
            @Override
            public void onStart() {
                super.onStart();
                if (mProgressDialog == null) {
                    showLoadingDialog();
                }
            }

            @Override
            protected void onSuccess(Object result) {
                requestTimes = 0;
                dismissLoadingDialog();
                Log.d(TAG, "backup" + "sendAgainSuccess");
                showBackupSuccessDialog();
            }

            @Override
            public void onError(Throwable e) {
                requestTimes++;
                if (requestTimes > 12) {
                    super.onError(e);
                    dismissLoadingDialog();
                    showFailedDialog(R.string.couldn_t_backup_try_again);
                } else {
                    backupDevice();
                }
                requestTimes = 0;
                Log.d("ma_back", "repeatTimes: " + requestTimes);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });

    }

    private void showBackupSuccessDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.back_up_settings);
        EditText editText = new EditText(getActivity());
        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams.setMargins(20, 0, 20, 0);
        editText.setLayoutParams(LayoutParams);
        editText.setText(mdefaultSaveUrl);
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
                String savePath = editText.getText().toString();
                SPUtils.getInstance(CONFIG_SPNAME, getActivity()).put(CONFIG_FILE_PATH, savePath);
                downLoadConfigureFile(savePath);
            }
        });
        builder.show();
    }

    private void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
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
                ToastUtil_m.show(getActivity(), R.string.succeed);
            }

            @Override
            public void onError(Throwable e) {
                dismissLoadingDialog();
                ToastUtil_m.show(getActivity(), R.string.fail);
            }

            @Override
            public void onNext(Object o) {
                dismissLoadingDialog();

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
                ToastUtil_m.show(getActivity(), R.string.succeed);
                dismissLoadingDialog();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                ToastUtil_m.show(getActivity(), R.string.fail);
                dismissLoadingDialog();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                ToastUtil_m.show(getActivity(), R.string.fail);
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

    public int restoreTimes = 0;

    private void restore() {
        String savePath = SPUtils.getInstance(CONFIG_SPNAME, getActivity()).getString(CONFIG_FILE_PATH);
        if (savePath.equals("")) {
            ToastUtil_m.show(getActivity(), "no backupFile");
            return;
        }
        File file = new File(FileUtils.createFilePath(savePath), "configure.bin");
        if (!file.exists()) {
            ToastUtil_m.show(getActivity(), "No this file");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        // RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("iptUpload", file.getName(), requestFile);
        API.get().uploadFile(new Subscriber() {
            @Override
            public void onStart() {
                super.onStart();
                if (mProgressDialog == null) {
                    showLoadingDialog();
                }
            }

            @Override
            public void onCompleted() {
                restoreTimes = 0;
                Log.d(TAG, "onCompleted ");
                dismissLoadingDialog();
                ToastUtil_m.show(getActivity(), R.string.succeed);
            }

            @Override
            public void onError(Throwable e) {

                if (restoreTimes > 12) {
                    dismissLoadingDialog();
                    Log.e(TAG, "restore,onResultError " + e.toString());
                    if (e instanceof SocketTimeoutException) {
                        ToastUtil_m.show(getActivity(), R.string.succeed);
                    } else {
                        ToastUtil_m.show(getActivity(), R.string.couldn_t_restore_try_again);
                    }
                } else {
                    restore();
                }


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
        mProgressDialog.setMessage(getActivity().getString(R.string.back_up_progress));
        mProgressDialog.show();

    }

    private void dismissLoadingDialog() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
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

    private void getDeviceFWCurrentVersion() {
        API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                Log.d(TAG, "getDeviceFWCurrentVersion,fw current version:" + result.getSwVersion());
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
                timerHelper = null;
                requestGetDeviceNewVersion();
            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestSetCheckNewVersion,onFailure");
                ToastUtil_m.show(getActivity(), R.string.fail);
                super.onFailure();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                ToastUtil_m.show(getActivity(), R.string.fail);
                Log.d(TAG, "requestSetCheckNewVersion,onResultError:" + error);
                super.onResultError(error);
            }
        });
    }


    private void getDeviceNewVersionTask() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                requestGetDeviceNewVersion();
            }
        };
        timerHelper.start(2 * 1000);
    }

    private void getUpgradeProgressTask() {
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                requestGetDeviceUpgradeState();
            }
        };
        timerHelper.start(2 * 1000);
    }

    private void requestGetDeviceNewVersion() {
        API.get().getDeviceNewVersion(new MySubscriber<DeviceNewVersion>() {
            @Override
            protected void onSuccess(DeviceNewVersion result) {
                Log.d(TAG, "requestGetDeviceNewVersion, state:" + result.getState());
                Log.d(TAG, "requestGetDeviceNewVersion, version:" + result.getVersion());
                Log.d(TAG, "requestGetDeviceNewVersion, size:" + result.getTotal_size());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processCheckVersionResult(result);
                    }
                });

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


    private void showCheckingDlg() {
        if (getActivity().isFinishing()) {
            Log.d(TAG, "showCheckingDlg, activity is finishing.");
            return;
        }
        if (mCheckingDlg == null) {
            mCheckingDlg = new ProgressDialog(getActivity());
            mCheckingDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mCheckingDlg.setMessage(getString(R.string.checking_for_update));
            mCheckingDlg.setTitle("");
            mCheckingDlg.setCancelable(false);
            mCheckingDlg.setCanceledOnTouchOutside(false);
            mCheckingDlg.show();
        } else if (!mCheckingDlg.isShowing()) {
            mCheckingDlg.show();
        }
    }

    private void showCheckResultDlg(String message, int state) {
        if (getActivity().isFinishing()) {
            Log.d(TAG, "showCheckResultDlg, activity is finishing.");
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle("");
        builder.setCancelable(false);
        if (state == Constants.DeviceVersionCheckState.DEVICE_NEW_VERSION) {
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestSetFOTAStartDownload();
                }
            });
        } else {
            builder.setPositiveButton(R.string.ok, null);
        }
        builder.create().show();

    }


    private void processCheckVersionResult(DeviceNewVersion result) {

        String message = "";
        int eStatus = result.getState();
        if (eStatus == Constants.DeviceVersionCheckState.DEVICE_CHECKING) {
            showCheckingDlg();
            if (timerHelper == null) {
                getDeviceNewVersionTask();
            }
        } else {
            if (timerHelper != null) {
                timerHelper.stop();
                timerHelper = null;
            }
            if (mCheckingDlg != null && mCheckingDlg.isShowing()) {
                mCheckingDlg.dismiss();
            }
            if (Constants.DeviceVersionCheckState.DEVICE_NEW_VERSION == eStatus) {
                Log.d(TAG, "processCheckVersionResult,size:" + result.getTotal_size());
                message = getString(R.string.setting_about_version) + ":" + result.getVersion() + " " + getString(R.string.available) + "\n" + getString(R.string.size) + ":" + Util.convertStorage(result.getTotal_size());
                showCheckResultDlg(message, eStatus);

            } else if (Constants.DeviceVersionCheckState.DEVICE_NO_NEW_VERSION == eStatus) {
                message = mDeviceVersion.getText().toString() + "\n" + getString(R.string.your_firmware_is_up_to_date);
                showCheckResultDlg(message, eStatus);

            } else if (Constants.DeviceVersionCheckState.DEVICE_NO_CONNECT == eStatus || Constants.DeviceVersionCheckState.SERVICE_NOT_AVAILABLE == eStatus) {
                message = getString(R.string.setting_upgrade_check_firmware_failed);
                showCheckResultDlg(message, eStatus);
            } else if (Constants.DeviceVersionCheckState.DEVICE_CHECK_ERROR == eStatus) {
                message = getString(R.string.setting_upgrade_check_error);
                showCheckResultDlg(message, eStatus);
            }
        }
    }


    private void showUpgradeStateResultDlg(String message, int state) {
        if (getActivity().isFinishing()) {
            Log.d(TAG, "showUpgradeStateResultDlg, activity is finishing.");
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_firmware_update, null);
        TextView messageText = (TextView) view.findViewById(R.id.tv_content);
        messageText.setText(message);
        messageText.setVisibility(View.VISIBLE);
        Drawable drawable;

        if (state == Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_COMPLETE) {
            drawable = getResources().getDrawable(R.drawable.general_ic_check);
            view.findViewById(R.id.complete_tip).setVisibility(View.VISIBLE);
        } else {
            drawable = getResources().getDrawable(R.drawable.sms_prompt);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        messageText.setCompoundDrawables(null, drawable, null, null);
        builder.setView(view);
        builder.setTitle("");
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();

    }


    private void showUpgradeProgressDlg(int state, int progress) {
        if (getActivity().isFinishing()) {
            Log.d(TAG, "showUpgradeStateResultDlg, activity is finishing.");
            return;
        }
        if (state == PROGRESS_STYLE_WAITING) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.dialog_firmware_update, null);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_update_waiting);
            progressBar.setVisibility(View.VISIBLE);
            builder.setView(view);
            mUpgradedDlg = builder.create();
            mUpgradedDlg.show();
            if (mUpgradingDlg != null) {
                mUpgradingDlg.dismiss();
            }

        } else {
            Log.d(TAG, "showUpgradeProgressDlg,mUpgradingDlg:" + mUpgradingDlg);
            if (mUpgradingDlg == null) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.dialog_firmware_update, null);
                mUpgradingProgressBar = (ProgressBar) view.findViewById(R.id.pb_update_progress);
                mUpgradingProgressBar.setVisibility(View.VISIBLE);
                mUpgradingProgressValue = (TextView) view.findViewById(R.id.tv_content);
                mUpgradingProgressValue.setVisibility(View.VISIBLE);
                mUpgradingProgressBar.setMax(100);
                mUpgradingProgressBar.setProgress(progress);
                mUpgradingProgressValue.setText(getString(R.string.updating) + progress + "%");
                builder.setView(view);
                builder.setCancelable(false);
                mUpgradingDlg = builder.create();
                mUpgradingDlg.show();
            } else {
                Log.d(TAG, "showUpgradeProgressDlg,mUpgradingDlg.isShowing():" + mUpgradingDlg.isShowing());
                if (mUpgradingDlg.isShowing()) {
                    mUpgradingProgressValue.setText(getString(R.string.updating) + progress + "%");
                    mUpgradingProgressBar.setProgress(progress);
                } else {
                    mUpgradingDlg.show();
                    mUpgradingProgressValue.setText(getString(R.string.updating) + progress + "%");
                    mUpgradingProgressBar.setProgress(progress);
                }
            }
        }
    }

    private void processUpgradeStateResult(DeviceUpgradeState result) {
        if (result == null) {
            if (timerHelper != null) {
                timerHelper.stop();
                timerHelper = null;
            }
            showUpgradeStateResultDlg(getString(R.string.could_not_update_try_again), -1);
        } else {
            int status = result.getStatus();
            if (Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_NOT_START == status) {
                if (result.getProcess() >= 99) {
                    showUpgradeProgressDlg(status, result.getProcess());
                    //                    if (timerHelper != null) {
                    //                        timerHelper.stop();
                    //                        timerHelper = null;
                    //                    }
                    //                    if (mUpgradingDlg != null) {
                    //                        mUpgradingDlg.dismiss();
                    //                    }
                    //                    requestSetDeviceStartUpdate();
                }
                Log.d(TAG, "requestGetDeviceUpgradeState,device upgrade not start,progress:" + result.getProcess());

                //                showUpgradeStateResultDlg(getString(R.string.could_not_update_try_again),status);
            } else if (Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_UPDATING == status) {
                showUpgradeProgressDlg(status, result.getProcess());
                if (timerHelper == null) {
                    getUpgradeProgressTask();
                }

            } else if (Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_COMPLETE == status) {
                if (timerHelper != null) {
                    timerHelper.stop();
                    timerHelper = null;
                }
                if (mUpgradingProgressBar != null) {
                    mUpgradingProgressBar.setProgress(100);
                    mUpgradingProgressValue.setText(getString(R.string.updating) + 100 + "%");
                }
                if (mUpgradingDlg != null) {
                    mUpgradingDlg.dismiss();
                }
                requestSetDeviceStartUpdate();
            }
        }


    }


    private void requestSetFOTAStartDownload() {
        API.get().SetFOTAStartDownload(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetFOTAStartDownload,onSuccess:" + result);
                timerHelper = null;
                requestGetDeviceUpgradeState();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                ToastUtil_m.show(getActivity(), R.string.fail);
                Log.d(TAG, "requestSetFOTAStartDownload,onResultError:" + error);
            }
        });
    }

    private void requestSetDeviceStartUpdate() {

        showUpgradeProgressDlg(PROGRESS_STYLE_WAITING, 0);
        API.get().setDeviceStartUpdate(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Log.d(TAG, "requestSetDeviceStartUpdate,onSuccess:" + result);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mUpgradedDlg != null) {
                                    mUpgradedDlg.dismiss();
                                }
                                showUpgradeStateResultDlg(getString(R.string.complete), Constants.DeviceUpgradeStatus.DEVICE_UPGRADE_COMPLETE);
                            }
                        });
                    }
                }, 10 * 1000);

            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mUpgradedDlg != null) {
                            mUpgradedDlg.dismiss();
                        }
                        showUpgradeStateResultDlg(getString(R.string.could_not_update_try_again), -1);
                    }
                });

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processUpgradeStateResult(result);
                    }
                });

            }

            @Override
            protected void onFailure() {
                Log.d(TAG, "requestGetDeviceUpgradeState, onFailure:");
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                Log.d(TAG, "requestGetDeviceUpgradeState, onResultError:" + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processUpgradeStateResult(null);
                    }
                });

            }
        });
    }

    private void goSettingLanguagePage() {
        Intent intent = new Intent(getActivity(), SettingLanguageActivity.class);
        startActivityForResult(intent, SET_LANGUAGE_REQUEST);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SettingFragment.SET_LANGUAGE_REQUEST) {
            if (data != null && data.getBooleanExtra(SettingLanguageActivity.IS_SWITCH_LANGUAGE, false)) {
                HomeActivity parentActivity = (HomeActivity) getActivity();
                parentActivity.afterSwitchLanguageReloadPage();
            }
        }
    }
}
