package com.alcatel.wifilink.rx.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.ui.LoginRxActivity;
import com.alcatel.wifilink.rx.ui.RefreshWifiRxActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.OtherUtils;
import com.orhanobut.logger.Logger;

import java.security.interfaces.RSAKey;

/**
 * Created by qianli.ma on 2017/11/16 0016.
 */

public class BoardSimHelper {


    private Activity activity;
    private ProgressDialog pgd;
    private Handler handler;
    private OnNownListener onNownListener;
    private OnSimReadyListener onSimReadyListener;
    private OnPinRequireListener onPinRequireListener;
    private OnpukRequireListener onpukRequireListener;
    private OnpukTimeoutListener onpukTimeoutListener;
    private OnNormalSimstatusListener onNormalSimstatusListener;
    private OnRollRequestOnResultError onRollRequestOnResultError;
    private OnRollRequestOnError onRollRequestOnError;
    private OnDetectedListener onDetectedListener;
    private OnSimLockListener onSimLockListener;
    private OnInitingListener onInitingListener;

    public BoardSimHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 点击事件调用此方法
     */
    public void boardNormal() {
        if (pgd == null) {
            pgd = OtherUtils.showProgressPop(activity);
        }
        if (!pgd.isShowing()) {
            pgd.show();
        }
        // 1.连接硬件
        new CheckBoard() {
            @Override
            public void successful() {
                // 2.登陆状态
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState result) {
                        if (result.getState() == Cons.LOGOUT) {
                            to(LoginRxActivity.class);
                            return;
                        }
                        // 3.sim卡状态(仅一次)
                        obtainSimStatusOne();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        OtherUtils.hideProgressPop(pgd);
                        Log.v("ma_couldn_connect", "boardSimHelper getLoginState error: " + error.getMessage());
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("ma_couldn_connect", "boardSimHelper getLoginState error: " + e.getMessage());
                        OtherUtils.hideProgressPop(pgd);
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }
                });
            }
        }.checkBoard(activity, LoginRxActivity.class);
    }

    /**
     * 定时器调用此方法
     */
    public void boardTimer() {
        // 1.连接硬件
        new CheckBoard() {
            @Override
            public void successful() {
                // 2.登陆状态
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState result) {
                        if (result.getState() == Cons.LOGOUT) {
                            to(LoginRxActivity.class);
                            return;
                        }
                        // 3.sim卡状态(循环)
                        obtainSimStatusRoll();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        rollRequestOnResultErrorNext(error);
                    }

                    @Override
                    public void onError(Throwable e) {
                        rollRequestOnErrorNext(e);
                    }
                });
            }
        }.checkBoard(activity, LoginRxActivity.class);
    }

    /**
     * 获取sim状态(只获取一次的时候使用)
     */
    private void obtainSimStatusOne() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                Logger.v("getSimStatus: " + result.getSIMState());
                normalSimStatusNext(result);
                int simState = result.getSIMState();
                switch (simState) {
                    case Cons.NONE:
                        nownStatusNext(result);
                        toast(R.string.Home_no_sim);
                        break;
                    case Cons.DETECTED:
                        delayRepeatGetSimstatu();
                        break;
                    case Cons.PIN_REQUIRED:
                        pinRequireNext(result);
                        break;
                    case Cons.PUK_REQUIRED:
                        pukRequireNext(result);
                        break;
                    case Cons.SIMLOCK:
                        simlockNext(result);
                        toast(R.string.home_sim_loched);
                        break;
                    case Cons.PUK_TIMESOUT:
                        pukTimeoutNext(result);
                        break;
                    case Cons.ILLEGAL:
                        toast(R.string.Home_sim_invalid);
                        break;
                    case Cons.READY:
                        simReadyNext(result);
                        break;
                    case Cons.INITING:
                        delayRepeatGetSimstatu();
                        break;
                }
                if (simState != Cons.DETECTED & simState != Cons.INITING) {
                    OtherUtils.hideProgressPop(pgd);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                toast(R.string.home_sim_not_accessible);
                to(RefreshWifiRxActivity.class);
            }

            @Override
            public void onError(Throwable e) {
                Log.v("ma_couldn_connect", "boardSimHelper obtainSimStatusOne error: " + e.getMessage());
                OtherUtils.hideProgressPop(pgd);
                toast(R.string.connect_failed);
                to(RefreshWifiRxActivity.class);
            }
        });
    }

    /**
     * 获取sim状态(循环获取的时候使用)
     */
    private void obtainSimStatusRoll() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                Logger.v("ma_sim: " + result.getSIMState());
                normalSimStatusNext(result);
                int simState = result.getSIMState();
                switch (simState) {
                    case Cons.NONE:
                        nownStatusNext(result);
                        break;
                    case Cons.DETECTED:
                        detectedNext(result);
                        break;
                    case Cons.PIN_REQUIRED:
                        pinRequireNext(result);
                        break;
                    case Cons.PUK_REQUIRED:
                        pukRequireNext(result);
                        break;
                    case Cons.SIMLOCK:
                        simlockNext(result);
                        break;
                    case Cons.PUK_TIMESOUT:
                        pukTimeoutNext(result);
                        break;
                    case Cons.ILLEGAL:
                        break;
                    case Cons.READY:
                        simReadyNext(result);
                        break;
                    case Cons.INITING:
                        initingNext(result);
                        break;
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                rollRequestOnResultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                rollRequestOnErrorNext(e);
            }
        });
    }

    /**
     * 延迟迭代请求
     */
    private void delayRepeatGetSimstatu() {
        handler = new Handler();
        handler.postDelayed(this::obtainSimStatusOne, 2500);
    }

    private void toast(int resId) {
        Log.v("ma_counld", getClass().getSimpleName());
        ToastUtil_m.show(activity, resId);
    }

    private void to(Class ac) {
        CA.toActivity(activity, ac, false, true, false, 0);
    }
    
    /* -------------------------------------------- INTERFACE -------------------------------------------- */

    public interface OnRollRequestOnResultError {
        void onResultError(ResponseBody.Error error);
    }

    public interface OnRollRequestOnError {
        void onError(Throwable e);
    }

    public interface OnDetectedListener {
        void onDetected(SimStatus simStatus);
    }

    public interface OnSimLockListener {
        void onSimlock(SimStatus simStatus);
    }

    public interface OnInitingListener {
        void onIniting(SimStatus simStatus);
    }


    public interface OnNormalSimstatusListener {
        void normalSimStatu(SimStatus simStatus);
    }

    public interface OnNownListener {
        void nownStatu(SimStatus simStatus);
    }

    public interface OnPinRequireListener {
        void pinRequire(SimStatus result);
    }

    public interface OnpukRequireListener {
        void pukRequire(SimStatus result);
    }

    public interface OnpukTimeoutListener {
        void pukTimeout(SimStatus result);
    }

    public interface OnSimReadyListener {
        void simReady(SimStatus result);
    }
    
    /* -------------------------------------------- LISTENER -------------------------------------------- */

    public void setOnRollRequestOnResultError(OnRollRequestOnResultError onRollRequestOnResultError) {
        this.onRollRequestOnResultError = onRollRequestOnResultError;
    }

    public void setOnRollRequestOnError(OnRollRequestOnError onRollRequestOnError) {
        this.onRollRequestOnError = onRollRequestOnError;
    }

    public void setOnDetectedListener(OnDetectedListener onDetectedListener) {
        this.onDetectedListener = onDetectedListener;
    }

    public void setOnSimLockListener(OnSimLockListener onSimLockListener) {
        this.onSimLockListener = onSimLockListener;
    }

    public void setOnInitingListener(OnInitingListener onInitingListener) {
        this.onInitingListener = onInitingListener;
    }

    public void setOnNormalSimstatusListener(OnNormalSimstatusListener onNormalSimstatusListener) {
        this.onNormalSimstatusListener = onNormalSimstatusListener;
    }

    public void setOnNownListener(OnNownListener onNownListener) {
        this.onNownListener = onNownListener;
    }

    public void setOnPinRequireListener(OnPinRequireListener onPinRequireListener) {
        this.onPinRequireListener = onPinRequireListener;
    }

    public void setOnpukRequireListener(OnpukRequireListener onpukRequireListener) {
        this.onpukRequireListener = onpukRequireListener;
    }

    public void setOnpukTimeoutListener(OnpukTimeoutListener onpukTimeoutListener) {
        this.onpukTimeoutListener = onpukTimeoutListener;
    }

    public void setOnSimReadyListener(OnSimReadyListener onSimReadyListener) {
        this.onSimReadyListener = onSimReadyListener;
    }

    /* -------------------------------------------- METHOD -------------------------------------------- */

    private void rollRequestOnResultErrorNext(ResponseBody.Error error) {
        if (onRollRequestOnResultError != null) {
            onRollRequestOnResultError.onResultError(error);
        }
    }

    private void rollRequestOnErrorNext(Throwable e) {
        if (onRollRequestOnError != null) {
            onRollRequestOnError.onError(e);
        }
    }

    private void detectedNext(SimStatus simStatus) {
        if (onDetectedListener != null) {
            onDetectedListener.onDetected(simStatus);
        }
    }

    private void simlockNext(SimStatus simStatus) {
        if (onSimLockListener != null) {
            onSimLockListener.onSimlock(simStatus);
        }
    }

    private void initingNext(SimStatus simStatus) {
        if (onInitingListener != null) {
            onInitingListener.onIniting(simStatus);
        }
    }

    private void normalSimStatusNext(SimStatus result) {
        if (onNormalSimstatusListener != null) {
            onNormalSimstatusListener.normalSimStatu(result);
        }
    }

    private void nownStatusNext(SimStatus result) {
        if (onNownListener != null) {
            onNownListener.nownStatu(result);
        }
    }

    private void pinRequireNext(SimStatus result) {
        if (onPinRequireListener != null) {
            onPinRequireListener.pinRequire(result);
        }
    }

    private void pukRequireNext(SimStatus result) {
        if (onpukRequireListener != null) {
            onpukRequireListener.pukRequire(result);
        }
    }

    private void pukTimeoutNext(SimStatus result) {
        if (onpukTimeoutListener != null) {
            onpukTimeoutListener.pukTimeout(result);
        }
    }

    private void simReadyNext(SimStatus result) {
        if (onSimReadyListener != null) {
            onSimReadyListener.simReady(result);
        }
    }

}
