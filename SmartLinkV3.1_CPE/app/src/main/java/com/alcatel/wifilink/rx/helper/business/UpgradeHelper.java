package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;

import com.alcatel.wifilink.model.update.DeviceNewVersion;
import com.alcatel.wifilink.model.update.DeviceUpgradeState;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.OtherUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by qianli.ma on 2017/12/14 0014.
 */

public class UpgradeHelper {
    private Activity activity;
    private boolean isShowWaiting;
    private ProgressDialog pgd;
    private Handler handler;

    public UpgradeHelper(Activity activity, boolean isShowWaiting) {
        this.activity = activity;
        this.isShowWaiting = isShowWaiting;
        handler = new Handler();
    }

    /**
     * 获取下载进度
     */
    public void getDownState() {
        RX.getInstant().getDeviceUpgradeState(new ResponseObject<DeviceUpgradeState>() {
            @Override
            protected void onSuccess(DeviceUpgradeState result) {
                // 0: No start update(UI does not send the start update command)
                // 1: updating (Download Firmware phase)
                // 2: complete
                upgradeStateNormalNext(result);
                int status = result.getStatus();
                switch (status) {
                    case Cons.NO_START_UPDATE:
                        noStartUpdateNext(result);
                        break;
                    case Cons.UPDATING:
                        Log.i("ma_upgrade",result.getProcess() + "%");
                        updatingNext(result);
                        break;
                    case Cons.COMPLETE:
                        Logger.t("ma_upgrade").i("success: " + result.getProcess() + "%");
                        completeNext(result);
                        break;
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                resultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                errorNext(e);
            }
        });
    }

    private OnCompleteListener onCompleteListener;

    // 接口OnCompleteListener
    public interface OnCompleteListener {
        void complete(DeviceUpgradeState attr);
    }

    // 对外方式setOnCompleteListener
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    // 封装方法completeNext
    private void completeNext(DeviceUpgradeState attr) {
        if (onCompleteListener != null) {
            onCompleteListener.complete(attr);
        }
    }

    private OnUpdatingListener onUpdatingListener;

    // 接口OnUpdatingListener
    public interface OnUpdatingListener {
        void updating(DeviceUpgradeState attr);
    }

    // 对外方式setOnUpdatingListener
    public void setOnUpdatingListener(OnUpdatingListener onUpdatingListener) {
        this.onUpdatingListener = onUpdatingListener;
    }

    // 封装方法updatingNext
    private void updatingNext(DeviceUpgradeState attr) {
        if (onUpdatingListener != null) {
            onUpdatingListener.updating(attr);
        }
    }

    private OnNoStartUpdateListener onNoStartUpdateListener;

    // 接口OnNoStartUpdateListener
    public interface OnNoStartUpdateListener {
        void noStartUpdate(DeviceUpgradeState attr);
    }

    // 对外方式setOnNoStartUpdateListener
    public void setOnNoStartUpdateListener(OnNoStartUpdateListener onNoStartUpdateListener) {
        this.onNoStartUpdateListener = onNoStartUpdateListener;
    }

    // 封装方法noStartUpdateNext
    private void noStartUpdateNext(DeviceUpgradeState attr) {
        if (onNoStartUpdateListener != null) {
            onNoStartUpdateListener.noStartUpdate(attr);
        }
    }

    private OnUpgradeStateNormalListener onUpgradeStateListener;

    // 接口OnUpgradeStateListener
    public interface OnUpgradeStateNormalListener {
        void upgradeState(DeviceUpgradeState attr);
    }

    // 对外方式setOnUpgradeStateListener
    public void setOnUpgradeStateNormalListener(OnUpgradeStateNormalListener onUpgradeStateListener) {
        this.onUpgradeStateListener = onUpgradeStateListener;
    }

    // 封装方法upgradeStateNext
    private void upgradeStateNormalNext(DeviceUpgradeState attr) {
        if (onUpgradeStateListener != null) {
            onUpgradeStateListener.upgradeState(attr);
        }
    }
    
    /* -------------------------------------------- method1 -------------------------------------------- */

    /**
     * 检查版本
     */
    public void checkVersion() {
        if (isShowWaiting) {
            pgd = OtherUtils.showProgressPop(activity);
        }
        setCheck();
    }

    private void setCheck() {
        // 1.先触发检查new version
        RX.getInstant().setCheckNewVersion(new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                // 2.在获取查询new version
                getNewVersionDo();
            }

            @Override
            public void onError(Throwable e) {
                hideDialog();
                Logger.t("ma_upgrade").e("setCheckNewVersion:" + e.getMessage());
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                hideDialog();
                Logger.t("ma_upgrade").e("setCheckNewVersion:" + error.getMessage());
                resultErrorNext(error);
            }
        });
    }

    /**
     * 获取查询new version
     */
    private void getNewVersionDo() {
        RX.getInstant().getDeviceNewVersion(new ResponseObject<DeviceNewVersion>() {
            @Override
            protected void onSuccess(DeviceNewVersion result) {
                Logger.t("ma_upgrade").v("new version state: "+result.getState());
                switch (result.getState()) {
                    case Cons.CHECKING:
                        checkingNext(result);
                        handler.postDelayed(() -> getNewVersionDo(), 3000);
                        break;
                    case Cons.NEW_VERSION:
                        hideDialog();
                        newVersionNext(result);
                        break;
                    case Cons.NO_NEW_VERSION:
                        hideDialog();
                        noNewVersionNext(result);
                        break;
                    case Cons.NO_CONNECT:
                        hideDialog();
                        noConnectNext(result);
                        break;
                    case Cons.SERVICE_NOT_AVAILABLE:
                        hideDialog();
                        serviceNotAvailableNext(result);
                        break;
                    case Cons.CHECK_ERROR:
                        hideDialog();
                        checkErrorNext(result);
                        Logger.t("ma_upgrade").e("CHECK_ERROR");
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                hideDialog();
                Logger.t("ma_upgrade").e("getDeviceNewVersion:" + e.getMessage());
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                hideDialog();
                Logger.t("ma_upgrade").e("getDeviceNewVersion:" + error.getMessage());
                resultErrorNext(error);
            }
        });
    }

    private void hideDialog() {
        if (pgd != null) {
            pgd.dismiss();
        }
    }

    private OnCheckErrorListener onCheckErrorListener;

    // 接口OnCheckErrorListener
    public interface OnCheckErrorListener {
        void checkError(DeviceNewVersion attr);
    }

    // 对外方式setOnCheckErrorListener
    public void setOnCheckErrorListener(OnCheckErrorListener onCheckErrorListener) {
        this.onCheckErrorListener = onCheckErrorListener;
    }

    // 封装方法checkErrorNext
    private void checkErrorNext(DeviceNewVersion attr) {
        if (onCheckErrorListener != null) {
            onCheckErrorListener.checkError(attr);
        }
    }

    private OnServiceNotAvailableListener onServiceNotAvailableListener;

    // 接口OnServiceNotAvailableListener
    public interface OnServiceNotAvailableListener {
        void serviceNotAvailable(DeviceNewVersion attr);
    }

    // 对外方式setOnServiceNotAvailableListener
    public void setOnServiceNotAvailableListener(OnServiceNotAvailableListener onServiceNotAvailableListener) {
        this.onServiceNotAvailableListener = onServiceNotAvailableListener;
    }

    // 封装方法serviceNotAvailableNext
    private void serviceNotAvailableNext(DeviceNewVersion attr) {
        if (onServiceNotAvailableListener != null) {
            onServiceNotAvailableListener.serviceNotAvailable(attr);
        }
    }

    private OnNoConnectListener onNoConnectListener;

    // 接口OnNoConnectListener
    public interface OnNoConnectListener {
        void noConnect(DeviceNewVersion attr);
    }

    // 对外方式setOnNoConnectListener
    public void setOnNoConnectListener(OnNoConnectListener onNoConnectListener) {
        this.onNoConnectListener = onNoConnectListener;
    }

    // 封装方法noConnectNext
    private void noConnectNext(DeviceNewVersion attr) {
        if (onNoConnectListener != null) {
            onNoConnectListener.noConnect(attr);
        }
    }

    private OnNoNewVersionListener onNoNewVersionListener;

    // 接口OnNoNewVersionListener
    public interface OnNoNewVersionListener {
        void noNewVersion(DeviceNewVersion attr);
    }

    // 对外方式setOnNoNewVersionListener
    public void setOnNoNewVersionListener(OnNoNewVersionListener onNoNewVersionListener) {
        this.onNoNewVersionListener = onNoNewVersionListener;
    }

    // 封装方法noNewVersionNext
    private void noNewVersionNext(DeviceNewVersion attr) {
        if (onNoNewVersionListener != null) {
            onNoNewVersionListener.noNewVersion(attr);
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

    private OnCheckingListener onCheckingListener;

    // 接口OnCheckingListener
    public interface OnCheckingListener {
        void checking(DeviceNewVersion attr);
    }

    // 对外方式setOnCheckingListener
    public void setOnCheckingListener(OnCheckingListener onCheckingListener) {
        this.onCheckingListener = onCheckingListener;
    }

    // 封装方法checkingNext
    private void checkingNext(DeviceNewVersion attr) {
        if (onCheckingListener != null) {
            onCheckingListener.checking(attr);
        }
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
}
