package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.model.update.DeviceNewVersion;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.helper.base.BoardSimHelper;
import com.alcatel.wifilink.rx.helper.base.BoardWanHelper;
import com.alcatel.wifilink.rx.helper.base.SystemInfoHelper;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.orhanobut.logger.Logger;

/**
 * Created by qianli.ma on 2017/12/15 0015.
 */

public class FirmUpgradeHelper {
    private Activity activity;
    private ProgressDialog pgd;

    public FirmUpgradeHelper(Activity activity, boolean isDialog) {
        this.activity = activity;
        if (isDialog) {
            pgd = OtherUtils.showProgressPop(activity);
        }
    }

    /**
     * 开始升级
     */
    public void startUpgrade() {
        RX.getInstant().setDeviceStartUpdate(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                startUpgradeNext(result);
            }

            @Override
            public void onError(Throwable e) {
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                resultErrorNext(error);
            }
        });
    }

    private OnStartUpgradeListener onStartUpgradeListener;

    // 接口OnStartUpgradeListener
    public interface OnStartUpgradeListener {
        void startUpgrade(Object attr);
    }

    // 对外方式setOnStartUpgradeListener
    public void setOnStartUpgradeListener(OnStartUpgradeListener onStartUpgradeListener) {
        this.onStartUpgradeListener = onStartUpgradeListener;
    }

    // 封装方法startUpgradeNext
    private void startUpgradeNext(Object attr) {
        if (onStartUpgradeListener != null) {
            onStartUpgradeListener.startUpgrade(attr);
        }
    }

    /* -------------------------------------------- method3 -------------------------------------------- */

    /**
     * 停止升级
     */
    public void stopUpgrade() {
        RX.getInstant().setDeviceUpdateStop(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                stopUpgradeNext(result);
            }

            @Override
            public void onError(Throwable e) {
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                resultErrorNext(error);
            }
        });
    }

    private OnStopUpgradeListener onStopUpgradeListener;

    // 接口OnStopUpgradeListener
    public interface OnStopUpgradeListener {
        void stopUpgrade(Object attr);
    }

    // 对外方式setOnStopUpgradeListener
    public void setOnStopUpgradeListener(OnStopUpgradeListener onStopUpgradeListener) {
        this.onStopUpgradeListener = onStopUpgradeListener;
    }

    // 封装方法stopUpgradeNext
    private void stopUpgradeNext(Object attr) {
        if (onStopUpgradeListener != null) {
            onStopUpgradeListener.stopUpgrade(attr);
        }
    }
    
    /* -------------------------------------------- method2 -------------------------------------------- */

    /**
     * 触发FOTA下载
     */
    public void triggerFOTA() {
        RX.getInstant().SetFOTAStartDownload(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                setFOTADownSuccessNext(result);
            }

            @Override
            public void onError(Throwable e) {
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                resultErrorNext(error);
            }
        });
    }

    private OnSetFOTADownSuccessListener onSetFOTADownSuccessListener;

    // 接口OnSetFOTADownSuccessListener
    public interface OnSetFOTADownSuccessListener {
        void setFOTADownSuccess(Object attr);
    }

    // 对外方式setOnSetFOTADownSuccessListener
    public void setOnSetFOTADownSuccessListener(OnSetFOTADownSuccessListener onSetFOTADownSuccessListener) {
        this.onSetFOTADownSuccessListener = onSetFOTADownSuccessListener;
    }

    // 封装方法setFOTADownSuccessNext
    private void setFOTADownSuccessNext(Object attr) {
        if (onSetFOTADownSuccessListener != null) {
            onSetFOTADownSuccessListener.setFOTADownSuccess(attr);
        }
    }
    
    /* -------------------------------------------- method1 -------------------------------------------- */

    /**
     * 检查新版本
     */
    public void checkNewVersion() {
        checkWan();// 检测是否连接了WAN口或者SIM卡(WAN口优先)
    }

    /**
     * 检查wan口
     */
    private void checkWan() {
        BoardWanHelper wh = new BoardWanHelper(activity);
        wh.setOnError(e -> checkSim());
        wh.setOnResultError(error -> checkSim());
        wh.setOnConnetedNextListener(wanResult -> checkNw());// 检查新版本
        wh.setOnConnetingNextListener(wanResult -> checkSim());
        wh.setOnDisConnetedNextListener(wanResult -> checkSim());
        wh.setOnDisconnetingNextListener(wanResult -> checkSim());
        wh.boardTimer();
    }

    /**
     * 检查sim card
     */
    private void checkSim() {
        hideDialog();
        BoardSimHelper bsh = new BoardSimHelper(activity);
        bsh.setOnRollRequestOnError(e -> toast(R.string.qs_pin_unlock_can_not_connect_des));
        bsh.setOnRollRequestOnResultError(e -> toast(R.string.qs_pin_unlock_can_not_connect_des));
        bsh.setOnNormalSimstatusListener(simStatus -> {
            int simState = simStatus.getSIMState();
            if (simState != Cons.READY) {
                toast(R.string.qs_pin_unlock_can_not_connect_des);
            } else {
                checkNw();// 检查新版本
            }
        });
        bsh.boardTimer();
    }

    /**
     * 检查新版本
     */
    private void checkNw() {
        Logger.t("ma_upgrade").v("FirmUpgradeHelper.checkNw");
        hideDialog();
        Drawable pop_bg = activity.getResources().getDrawable(R.drawable.bg_pop_conner);
        UpgradeHelper uh = new UpgradeHelper(activity, true);
        uh.setOnResultErrorListener(attr -> toast(R.string.setting_upgrade_check_firmware_failed));
        uh.setOnErrorListener(attr -> toast(R.string.setting_upgrade_check_firmware_failed));
        uh.setOnCheckErrorListener(this::getCurrentVersion);

        uh.setOnServiceNotAvailableListener(attr -> toast(R.string.setting_upgrade_not_available));
        uh.setOnNoConnectListener(attr -> toast(R.string.setting_upgrade_no_connection));
        uh.setOnNoNewVersionListener(this::getCurrentVersion);
        uh.setOnNewVersionListener(this::newVersionNext);
        uh.checkVersion();
    }

    /**
     * 获取当前版本号
     */
    private void getCurrentVersion(DeviceNewVersion deviceNewVersion) {
        Logger.t("ma_upgrade").v("FirmUpgradeHelper.getCurrentVersion");
        SystemInfoHelper sif = new SystemInfoHelper();
        sif.setOnErrorListener(attr -> toast(R.string.setting_upgrade_check_firmware_failed));
        sif.setOnResultErrorListener(attr -> toast(R.string.setting_upgrade_check_firmware_failed));
        sif.setOnGetSystemInfoSuccessListener(systemInfo -> noNewVersionNext(deviceNewVersion, systemInfo));
        sif.get();
    }

    private OnResultErrorListener onResultErrorListener;

    // 接口OnResultErrorListener
    public interface OnResultErrorListener {
        void resultError(ResponseBody.Error attr);
    }

    // 对外方式setOnResultErrorListener
    public void setOnResultErrorListener(OnResultErrorListener onResultErrorListener) {
        this.onResultErrorListener = onResultErrorListener;
    }

    // 封装方法resultErrorNext
    private void resultErrorNext(ResponseBody.Error attr) {
        if (onResultErrorListener != null) {
            onResultErrorListener.resultError(attr);
        }
    }

    private OnErrorListener onErrorListener;

    // 接口OnErrorListener
    public interface OnErrorListener {
        void error(Throwable attr);
    }

    // 对外方式setOnErrorListener
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    // 封装方法errorNext
    private void errorNext(Throwable attr) {
        if (onErrorListener != null) {
            onErrorListener.error(attr);
        }
    }

    private OnNewVersionListener onNewVersionListener;

    // 接口OnNewVersionListener
    public interface OnNewVersionListener {
        void newVersion(DeviceNewVersion attr);
    }

    // 对外方式setOnNewVersionListener
    public void setOnNewVersionListener(OnNewVersionListener onNewVersionListener) {
        this.onNewVersionListener = onNewVersionListener;
    }

    // 封装方法newVersionNext
    private void newVersionNext(DeviceNewVersion attr) {
        if (onNewVersionListener != null) {
            onNewVersionListener.newVersion(attr);
        }
    }

    private OnNoNewVersionListener onNoNewVersionListener;

    // 接口OnNoNewVersionListener
    public interface OnNoNewVersionListener {
        void noNewVersion(DeviceNewVersion deviceNewVersion, SystemInfo attr);
    }

    // 对外方式setOnNoNewVersionListener
    public void setOnNoNewVersionListener(OnNoNewVersionListener onNoNewVersionListener) {
        this.onNoNewVersionListener = onNoNewVersionListener;
    }

    // 封装方法noNewVersionNext
    private void noNewVersionNext(DeviceNewVersion deviceNewVersion, SystemInfo attr) {
        if (onNoNewVersionListener != null) {
            onNoNewVersionListener.noNewVersion(deviceNewVersion, attr);
        }
    }

    private void hideDialog() {
        if (pgd != null) {
            pgd.dismiss();
        }
    }

    private void toast(int resId) {
        ToastUtil_m.show(activity, resId);
    }

    private void toastLong(int resId) {
        ToastUtil_m.showLong(activity, resId);
    }

    private void toast(String content) {
        ToastUtil_m.show(activity, content);
    }

    private void to(Class ac, boolean isFinish) {
        CA.toActivity(activity, ac, false, isFinish, false, 0);
    }
}
