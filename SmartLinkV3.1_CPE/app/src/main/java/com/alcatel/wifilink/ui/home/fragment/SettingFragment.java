package com.alcatel.wifilink.ui.home.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import com.alcatel.wifilink.appwidget.PopupWindows;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.update.DeviceNewVersion;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.rx.helper.base.BoardWanHelper;
import com.alcatel.wifilink.rx.helper.base.CheckBoardLogin;
import com.alcatel.wifilink.rx.helper.business.FirmUpgradeHelper;
import com.alcatel.wifilink.rx.helper.business.UpgradeHelper;
import com.alcatel.wifilink.rx.ui.HomeRxActivity;
import com.alcatel.wifilink.ui.activity.AboutActivity;
import com.alcatel.wifilink.ui.activity.EthernetWanConnectionActivity;
import com.alcatel.wifilink.ui.activity.SettingAccountActivity;
import com.alcatel.wifilink.ui.activity.SettingDeviceActivity;
import com.alcatel.wifilink.ui.activity.SettingLanguageActivity;
import com.alcatel.wifilink.ui.activity.SettingShareActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.main.TimerHelper;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.FileUtils;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.SPUtils;
import com.alcatel.wifilink.utils.ScreenSize;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.net.SocketTimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;

/**
 * Created by qianli.ma on 2017/6/16.
 */
@SuppressLint("ValidFragment")
public class SettingFragment extends Fragment implements View.OnClickListener, FragmentBackHandler {

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
    private TextView mMobileNetworkSimSocket;
    private TextView mMobileNetworkWanSocket;
    private TimerHelper checkTimer;
    private HomeRxActivity activity;
    private ProgressDialog pgd;
    private CheckBoardLogin checkBoardLogin;

    private SweetAlertDialog upgradeTipDialog;
    private SweetAlertDialog boradUpgradeDialog;
    private PopupWindows pop_noNewVersion;
    private PopupWindows pop_downloading;
    boolean isDownloading = false;
    private TimerHelper downTimer;
    private BoardSimHelper simTimerHelper;
    private BoardWanHelper wanTimerHelper;
    private String off;
    private String on;
    private String noSimcard;
    private String detected;
    private String initing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (HomeRxActivity) getActivity();
        m_view = View.inflate(getActivity(), R.layout.fragment_home_setting, null);
        initRes();
        initSome();
        resetUi();
        init();
        initEvent();
        startTimer();
        return m_view;
    }

    private void initRes() {
        on = getString(R.string.setting_on_state);
        off = getString(R.string.setting_off_state);
        noSimcard = getString(R.string.home_no_sim);
        detected = getString(R.string.Home_SimCard_Detected);
        initing = getString(R.string.home_initializing);
    }

    private void initSome() {
        simTimerHelper = new BoardSimHelper(getActivity());
        wanTimerHelper = new BoardWanHelper(getActivity());

        wanTimerHelper.setOnError(e -> mMobileNetworkWanSocket.setText(off));
        wanTimerHelper.setOnResultError(e -> mMobileNetworkWanSocket.setText(off));
        wanTimerHelper.setOnDisconnetingNextListener(wanResult -> mMobileNetworkWanSocket.setText(off));
        wanTimerHelper.setOnDisConnetedNextListener(wanResult -> mMobileNetworkWanSocket.setText(off));
        wanTimerHelper.setOnConnetingNextListener(wanResult -> mMobileNetworkWanSocket.setText(off));
        wanTimerHelper.setOnConnetedNextListener(wanResult -> mMobileNetworkWanSocket.setText(on));

        simTimerHelper.setOnRollRequestOnResultError(error -> mMobileNetworkSimSocket.setText(noSimcard));
        simTimerHelper.setOnRollRequestOnError(error -> mMobileNetworkSimSocket.setText(noSimcard));
        simTimerHelper.setOnNownListener(simStatus -> mMobileNetworkSimSocket.setText(noSimcard));
        simTimerHelper.setOnDetectedListener(simStatus -> mMobileNetworkSimSocket.setText(detected));
        simTimerHelper.setOnInitingListener(simStatus -> mMobileNetworkSimSocket.setText(initing));
        simTimerHelper.setOnPinRequireListener(result -> mMobileNetworkSimSocket.setText(off));
        simTimerHelper.setOnpukRequireListener(result -> mMobileNetworkSimSocket.setText(off));
        simTimerHelper.setOnpukTimeoutListener(result -> mMobileNetworkSimSocket.setText(off));
        simTimerHelper.setOnSimLockListener(result -> mMobileNetworkSimSocket.setText(off));
        simTimerHelper.setOnSimReadyListener(result -> mMobileNetworkSimSocket.setText(on));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {// 隐藏界面
            stopTimer();
        } else {
            startTimer();
            resetUi();
        }
    }

    private void resetUi() {
        if (activity != null) {
            activity.tabFlag = Cons.TAB_SETTING;
            activity.llNavigation.setVisibility(View.VISIBLE);
            activity.rlBanner.setVisibility(View.VISIBLE);
        }else {
            ((HomeRxActivity)getActivity()).tabFlag = Cons.TAB_SETTING;
            ((HomeRxActivity)getActivity()).llNavigation.setVisibility(View.VISIBLE);
            ((HomeRxActivity)getActivity()).rlBanner.setVisibility(View.VISIBLE);
        }
    }

    private void stopTimer() {
        if (checkTimer != null) {
            checkTimer.stop();
            checkTimer = null;
        }
    }

    private void startTimer() {
        if (checkTimer == null) {
            checkTimer = new TimerHelper(activity) {
                @Override
                public void doSomething() {
                    Logger.v("ma_check:" + "settingfragment--> timer");
                    // 检测WAN口 | SIM是否连接
                    wanTimerHelper.boardTimer();
                    simTimerHelper.boardTimer();
                }
            };
        }
        checkTimer.start(2500);
        OtherUtils.timerList.add(checkTimer);
    }


    private void init() {
        mLoginPassword = (RelativeLayout) m_view.findViewById(R.id.setting_login_password);
        mMobileNetwork = (RelativeLayout) m_view.findViewById(R.id.setting_mobile_network);
        mMobileNetworkSimSocket = (TextView) m_view.findViewById(R.id.tv_setting_sim_socket);// SIM开关显示
        mMobileNetworkWanSocket = (TextView) m_view.findViewById(R.id.tv_setting_wan_socket);// WAN开关显示
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
        otherUtils.setOnHwVersionListener(deviceVersion -> {
            if (deviceVersion.contains("HH40")) {
                mSharingService.setVisibility(View.GONE);
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
            case R.id.setting_login_password:// 修改密码
                goToAccountSettingPage();
                break;
            case R.id.setting_mobile_network:// 进入Mobile network
                goToMobileNetworkSettingPage();
                break;
            case R.id.setting_ethernet_wan:// 进入wan info
                goEthernetWanConnectionPage();
                break;
            case R.id.setting_sharing_service:// 进入USB界面
                if (isSharingSupported) {
                    goToShareSettingPage();
                } else {
                    ToastUtil_m.show(activity, getString(R.string.setting_not_support_sharing_service));
                }
                break;
            case R.id.setting_language:// 进入语言设置
                goSettingLanguagePage();
                break;
            case R.id.setting_firmware_upgrade:// 点击升级
                clickUpgrade();
                break;
            case R.id.setting_restart:// 重启设备
                popDialogFromBottom(RESTART_RESET);
                break;
            case R.id.setting_backup:// 备份配置
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    verifyStoragePermissions(activity);
                }
                popDialogFromBottom(Backup_Restore);
                break;
            case R.id.setting_about:// 进入about
                goToAboutSettingPage();
                break;
            default:
                break;
        }
    }

    /**
     * U1.点击了升级
     */
    private void clickUpgrade() {
        FirmUpgradeHelper fh = new FirmUpgradeHelper(getActivity(), true);
        fh.setOnNoNewVersionListener(this::popversion);
        fh.setOnNewVersionListener(this::popversion);
        fh.checkNewVersion();
    }

    /**
     * U2.弹窗(有新版本 | 没有新版本)
     *
     * @param attr
     */
    private void popversion(DeviceNewVersion attr) {
        upgradeTipDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);// 升级提示
        // 1.获取状态
        boolean noNewVersion = attr.getState() == Cons.NO_NEW_VERSION;
        // 2.显示弹窗
        Drawable pop_bg = getResources().getDrawable(R.drawable.bg_pop_conner);
        View inflate = View.inflate(activity, R.layout.pop_setting_upgrade_checkversion, null);
        ScreenSize.SizeBean size = ScreenSize.getSize(activity);
        int width = (int) (size.width * 0.85f);
        int height = (int) (size.height * 0.19f);
        // 3.修改弹窗属性信息
        TextView versionName = (TextView) inflate.findViewById(R.id.tv_pop_setting_rx_upgrade_noNewVersion_version);
        TextView ok = (TextView) inflate.findViewById(R.id.tv_pop_setting_rx_upgrade_ok);
        // 3.1.同上
        versionName.setText(noNewVersion ? attr.getVersion() : attr.getVersion() + " " + getString(R.string.available));
        ok.setText(noNewVersion ? getString(R.string.ok) : getString(R.string.setting_upgrade));
        ok.setOnClickListener(v -> {
            // 版本弹窗消隐
            pop_noNewVersion.dismiss();
            // 需要升级的话则弹出新的确认升级的弹窗
            if (!noNewVersion) {
                upgradeTipDialog // 升级弹窗
                        .setTitleText(getString(R.string.setting_upgrade))// title
                        .setContentText(getString(R.string.setting_upgrade_firmware_warning))// content
                        .setCancelText(getString(R.string.cancel))// cancel text
                        .setCancelClickListener(Dialog::dismiss)// cance button click
                        .showCancelButton(true)// cancel button show
                        .setConfirmText(getString(R.string.ok))// ok
                        .setConfirmClickListener(dialog -> {
                            upgradeTipDialog.dismiss();// 消隐
                            beginDownLoadFOTA();
                        });
                upgradeTipDialog.show();
            }
        });
        pop_noNewVersion = new PopupWindows(activity, inflate, width, height, true, pop_bg);
    }

    /**
     * U3.触发FOTA下载
     */
    private void beginDownLoadFOTA() {
        isDownloading = true;
        FirmUpgradeHelper fuh = new FirmUpgradeHelper(getActivity(), false);
        fuh.setOnErrorListener(attr -> {
            toast(R.string.connect_failed);
            isDownloading = false;
        });
        fuh.setOnResultErrorListener(attr -> {
            toast(R.string.connect_failed);
            isDownloading = false;
        });
        // 触发成功
        fuh.setOnSetFOTADownSuccessListener(attr -> {

            /* 1.显示进度弹窗 */
            Drawable pop_bg = getResources().getDrawable(R.drawable.bg_pop_conner);
            View v = View.inflate(activity, R.layout.pop_setting_dowing, null);
            ScreenSize.SizeBean size = ScreenSize.getSize(activity);
            int width = (int) (size.width * 0.85f);
            int height = (int) (size.height * 0.21f);
            RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl_pop_setting_download_all);
            rl.setOnClickListener(null);
            TextView per = (TextView) v.findViewById(R.id.tv_pop_setting_download_per);
            NumberProgressBar progressBar = (NumberProgressBar) v.findViewById(R.id.pg_pop_setting_download);
            TextView cancel = (TextView) v.findViewById(R.id.tv_pop_setting_download_cancel);
            cancel.setOnClickListener(v1 -> {
                // 1.1.消隐
                pop_downloading.dismiss();
                // 1.2.停止定时器
                stopDownTimerAndPop();
                // 1.3.请求停止
                FirmUpgradeHelper fuh1 = new FirmUpgradeHelper(getActivity(), false);
                fuh1.setOnErrorListener(attr1 -> downError(-1));
                fuh1.setOnResultErrorListener(attr1 -> downError(-1));
                fuh1.setOnStopUpgradeListener(attr1 -> {
                    stopDownTimerAndPop();
                    toast(R.string.setting_upgrade_stop_error);
                    isDownloading = false;
                });
                fuh1.stopUpgrade();
            });

            /* 2.启动定时器 */
            UpgradeHelper uh = new UpgradeHelper(getActivity(), false);
            uh.setOnErrorListener(attr1 -> downError(R.string.setting_upgrade_get_update_state_failed));
            uh.setOnResultErrorListener(attr1 -> downError(R.string.setting_upgrade_get_update_state_failed));
            uh.setOnNoStartUpdateListener(attr1 -> per.setText(String.valueOf(attr1.getProcess())));
            uh.setOnUpdatingListener(attr1 -> per.setText(String.valueOf(attr1.getProcess())));
            uh.setOnCompleteListener(attr1 -> {
                // 2.1.显示进度为100%
                per.setText(String.valueOf(attr1.getProcess()));
                // 2.2.停止定时器 + 进度条消隐
                stopDownTimerAndPop();
                // 2.3.修改标记位
                isDownloading = false;
                // 2.3.延迟2秒弹窗提示用户是否触发底层升级
                new Handler().postDelayed(this::triggerFOTAUpgrade, 500);
            });
            downTimer = new TimerHelper(getActivity()) {
                @Override
                public void doSomething() {
                    uh.getDownState();// 请求下载进度
                }
            };
            downTimer.start(3000);
            pop_downloading = new PopupWindows(activity, v, width, height, false, pop_bg);
        });
        fuh.triggerFOTA();// 触发FOTA下载
    }

    /**
     * U4.触发硬件底层进行升级操作
     */
    private void triggerFOTAUpgrade() {
        boradUpgradeDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)// 升级提示
                                     .setTitleText(getString(R.string.setting_upgrade))// title
                                     .setContentText(getString(R.string.setting_upgrade_firmware_warning))// content
                                     .setCancelText(getString(R.string.cancel))// cancel
                                     .setConfirmText(getString(R.string.ok))// ok
                                     .setConfirmClickListener(dialog -> {
                                         boradUpgradeDialog.dismiss();// 消隐
                                         startDeviceUpgrade();
                                     }).showCancelButton(true)// cancel
                                     .setCancelClickListener(Dialog::dismiss);// 设置取消按钮点击
        boradUpgradeDialog.show();
    }

    /**
     * 正式开始升级
     */
    private void startDeviceUpgrade() {
        FirmUpgradeHelper fuh = new FirmUpgradeHelper(getActivity(), false);
        fuh.setOnErrorListener(attr -> toast(R.string.setting_upgrade_start_update_failed));
        fuh.setOnResultErrorListener(attr -> toast(R.string.setting_upgrade_start_update_failed));
        fuh.setOnStartUpgradeListener(attr -> {
            toast(R.string.device_will_restart_later);
            OtherUtils.showProgressPop(getActivity(), getString(R.string.updating));
        });
        fuh.startUpgrade();
    }

    /**
     * U3.Error.下载中发生错误
     */
    private void downError(int resId) {
        // 1.停止定时器 + 2.弹窗消隐
        stopDownTimerAndPop();
        // 3.提示
        if (resId == -1) {
            toast(R.string.connect_failed);
        } else {
            getString(resId);
        }
        // 4.恢复标记位
        isDownloading = false;
    }


    private void popDialogFromBottom(int itemType) {
        PopupWindow popupWindow = new PopupWindow(activity);
        View view = View.inflate(activity, R.layout.dialog_from_bottom, null);

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

        mFirstTxt.setOnClickListener(v -> {
            if (itemType == RESTART_RESET) {
                restartDevice();
            } else {
                backupDevice();
            }
            popupWindow.dismiss();
        });
        mSecondTxt.setOnClickListener(v -> {
            if (itemType == RESTART_RESET) {
                showDialogResetFactorySetting();
            } else {
                restore();
            }
            popupWindow.dismiss();
        });
        mCancelTxt.setOnClickListener(v -> {
            popupWindow.dismiss();
            backgroundAlpha(activity, 1f);
        });
        popupWindow.setOnDismissListener(() -> backgroundAlpha(activity, 1f));
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        backgroundAlpha(activity, 0.5f);
        popupWindow.setAnimationStyle(R.style.dialogStyle);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public int requestTimes = 0;

    private void backupDevice() {

        RX.getInstant().backupDevice(new ResponseObject() {
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
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }
        });

    }

    private void showBackupSuccessDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.back_up_settings);
        EditText editText = new EditText(activity);
        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams.setMargins(20, 0, 20, 0);
        editText.setLayoutParams(LayoutParams);
        editText.setText(mdefaultSaveUrl);
        builder.setView(editText);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(R.string.backup, (dialog, which) -> {
            String savePath = editText.getText().toString();
            SPUtils.getInstance(CONFIG_SPNAME, activity).put(CONFIG_FILE_PATH, savePath);
            downLoadConfigureFile(savePath);
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
        String downloadFileUrl = "/cfgbak/configure.bin";
        RX.getInstant().downConfigureFile(new Subscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onCompleted() {
                dismissLoadingDialog();
                ToastUtil_m.show(activity, R.string.succeed);
            }

            @Override
            public void onError(Throwable e) {
                dismissLoadingDialog();
                ToastUtil_m.show(activity, R.string.fail);
            }

            @Override
            public void onNext(Object o) {
                dismissLoadingDialog();

            }
        }, downloadFileUrl, file);
    }


    private void showDialogResetFactorySetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
        RX.getInstant().rebootDevice(new ResponseObject() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            protected void onSuccess(Object result) {
                ToastUtil_m.show(activity, R.string.succeed);
                dismissLoadingDialog();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                ToastUtil_m.show(activity, R.string.fail);
                dismissLoadingDialog();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                ToastUtil_m.show(activity, R.string.fail);
                dismissLoadingDialog();
            }
        });
    }


    private void resetDevice() {
        RX.getInstant().resetDevice(new ResponseObject() {
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
        String savePath = SPUtils.getInstance(CONFIG_SPNAME, activity).getString(CONFIG_FILE_PATH);
        if (savePath.equals("")) {
            ToastUtil_m.show(activity, "no backupFile");
            return;
        }
        File file = new File(FileUtils.createFilePath(savePath), "configure.bin");
        if (!file.exists()) {
            ToastUtil_m.show(activity, "No this file");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        // RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // TOAT: 2017/8/11 多添加一个参数 [ _TclRequestVerificationToken ]
        MultipartBody.Part body = MultipartBody.Part.createFormData("iptUpload", file.getName(), requestFile);
        RX.getInstant().uploadFile(new Subscriber() {
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
                dismissLoadingDialog();
                ToastUtil_m.show(activity, R.string.succeed);
            }

            @Override
            public void onError(Throwable e) {

                if (restoreTimes > 12) {
                    dismissLoadingDialog();
                    if (e instanceof SocketTimeoutException) {
                        ToastUtil_m.show(activity, R.string.succeed);
                    } else {
                        ToastUtil_m.show(activity, R.string.couldn_t_restore_try_again);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.complete));
        builder.setCancelable(true);
        builder.show();
    }

    private void showFailedDialog(int stringId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(stringId));
        builder.setCancelable(true);
        builder.show();
    }

    private void showLoadingDialog() {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(activity.getString(R.string.back_up_progress));
        mProgressDialog.show();

    }

    private void dismissLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
        Intent intent = new Intent(activity, SettingShareActivity.class);
        activity.startActivity(intent);
    }

    private void getDeviceFWCurrentVersion() {
        RX.getInstant().getSystemInfo(new ResponseObject<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
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

    private void showCheckingDlg() {
        if (activity.isFinishing()) {
            return;
        }
        if (mCheckingDlg == null) {
            mCheckingDlg = new ProgressDialog(activity);
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

    private void goSettingLanguagePage() {
        Intent intent = new Intent(activity, SettingLanguageActivity.class);
        startActivityForResult(intent, SET_LANGUAGE_REQUEST);
    }

    private void goToAccountSettingPage() {
        // to setting account activity
        Intent intent = new Intent(activity, SettingAccountActivity.class);
        activity.startActivity(intent);
    }

    private void goToMobileNetworkSettingPage() {
        activity.fraHelpers.transfer(activity.clazz[7]);
    }

    private void goEthernetWanConnectionPage() {
        Intent intent = new Intent(activity, EthernetWanConnectionActivity.class);
        activity.startActivity(intent);
    }

    private void goToDeviceSettingPage() {
        // to setting connectDeviceList activity
        CA.toActivity(activity, SettingDeviceActivity.class, false, true, false, 0);
    }

    private void goToAboutSettingPage() {
        Intent intent = new Intent(activity, AboutActivity.class);
        intent.putExtra("upgradeStatus", upgradeStatus);
        activity.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SettingFragment.SET_LANGUAGE_REQUEST) {
            if (data != null && data.getBooleanExtra(SettingLanguageActivity.IS_SWITCH_LANGUAGE, false)) {
                // 切换语言(重新加载fragment) + 跳转到setting-fragment
                activity.reloadFragment();
                activity.fraHelpers.transfer(activity.clazz[Cons.TAB_SETTING]);
            }
        }
    }

    private void toast(int resId) {
        ToastUtil_m.show(getActivity(), resId);
    }

    private void toastLong(int resId) {
        ToastUtil_m.showLong(getActivity(), resId);
    }

    private void toast(String content) {
        ToastUtil_m.show(getActivity(), content);
    }

    private void to(Class ac, boolean isFinish) {
        CA.toActivity(getActivity(), ac, false, isFinish, false, 0);
    }

    /**
     * 停止下载定时器
     */
    public void stopDownTimerAndPop() {
        if (downTimer != null) {
            downTimer.stop();
        }
        if (pop_downloading != null) {
            pop_downloading.dismiss();
        }
    }

    @Override
    public boolean onBackPressed() {
        // 如果在下载中, 则自己处理返回按钮的逻辑
        return isDownloading;
    }
}
