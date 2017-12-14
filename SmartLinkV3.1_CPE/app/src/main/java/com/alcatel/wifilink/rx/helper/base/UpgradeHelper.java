package com.alcatel.wifilink.rx.helper.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.update.DeviceNewVersion;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/12/1 0001.
 */

public class UpgradeHelper {

    private Activity activity;
    private CheckBoardLogin checkBoardLogin;
    private int count = 0;
    private Handler handler;

    public UpgradeHelper(Activity activity) {
        this.activity = activity;
        count = 0;
        handler = new Handler();
    }

    public void getNewVersion() {
        checkBoardLogin = new CheckBoardLogin(activity) {
            @Override
            public void afterCheckSuccess(ProgressDialog pop) {
                API.get().getDeviceNewVersion(new MySubscriber<DeviceNewVersion>() {
                    @Override
                    protected void onSuccess(DeviceNewVersion result) {

                        // 0: checking
                        // 1: New version
                        // 2: no new version
                        // 3: no connect
                        // 4: service not available
                        // 5: check error(time out etc…)

                        int state = result.getState();
                        normalNext(result);
                        switch (state) {
                            case Cons.CHECKING:
                                checkingNext(result);
                                if (count <= 5) {
                                    handler.postDelayed(UpgradeHelper.this::getNewVersion, 1500);
                                } else {
                                    count = 0;
                                    toast(R.string.setting_upgrade_no_new_version);
                                }
                                break;
                            case Cons.NEW_VERSION:
                                newVersionNext(result);
                                break;
                            case Cons.NO_NEW_VERSION:
                                noNewVersionNext(result);
                                toast(R.string.setting_upgrade_no_new_version);
                                break;
                            case Cons.NO_CONNECT:
                                noConnectNext(result);
                                toast(R.string.setting_upgrade_no_connection);
                                break;
                            case Cons.SERVICE_NOT_AVAILABLE:
                                serviceNotAvaNext(result);
                                toast(R.string.setting_upgrade_not_available);
                                break;
                            case Cons.CHECK_ERROR:
                                checkErrorNext(result);
                                toast(R.string.setting_upgrade_check_error);
                                break;
                        }

                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        toast(R.string.setting_upgrade_check_failed);
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast(R.string.setting_upgrade_check_failed);
                    }
                });
            }
        };
    }

    /* -------------------------------------------- HELPER -------------------------------------------- */

    /* normal */
    private OnNormalListener onNormalListener;

    // 接口OnNormalListener
    public interface OnNormalListener {
        void normal(DeviceNewVersion attr);
    }

    // 对外方式setOnNormalListener
    public void setOnNormalListener(OnNormalListener onNormalListener) {
        this.onNormalListener = onNormalListener;
    }

    // 封装方法normalNext
    private void normalNext(DeviceNewVersion attr) {
        if (onNormalListener != null) {
            onNormalListener.normal(attr);
        }
    }

    /* checking */
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

    /* new version */
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

    /* no new version */
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

    /* no connect */
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

    /* service not available */
    private OnServiceNotAvaListener onServiceNotAvaListener;

    // 接口OnServiceNotAvaListener
    public interface OnServiceNotAvaListener {
        void serviceNotAva(DeviceNewVersion attr);
    }

    // 对外方式setOnServiceNotAvaListener
    public void setOnServiceNotAvaListener(OnServiceNotAvaListener onServiceNotAvaListener) {
        this.onServiceNotAvaListener = onServiceNotAvaListener;
    }

    // 封装方法serviceNotAvaNext
    private void serviceNotAvaNext(DeviceNewVersion attr) {
        if (onServiceNotAvaListener != null) {
            onServiceNotAvaListener.serviceNotAva(attr);
        }
    }

    /* check error */
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

}
