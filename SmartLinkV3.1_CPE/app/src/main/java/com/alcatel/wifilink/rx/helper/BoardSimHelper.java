package com.alcatel.wifilink.rx.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

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

/**
 * Created by qianli.ma on 2017/11/16 0016.
 */

public class BoardSimHelper {


    private Activity activity;
    private ProgressDialog pgd;
    private Handler handler;
    private OnPinRequireListener onPinRequireListener;
    private OnpukRequireListener onpukRequireListener;
    private OnpukTimeoutListener onpukTimeoutListener;
    private OnSimReadyListener onSimReadyListener;
    private OnNormalSimstatusListener onNormalSimstatusListener;

    public BoardSimHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 调用此方法
     */
    public void boardNormal() {
        if (pgd == null) {
            pgd = OtherUtils.showProgressPop(activity);
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
                            toast(R.string.login_kickoff_logout_successful);
                            to(LoginRxActivity.class);
                            return;
                        }
                        // 3.sim卡状态
                        obtainSimStatus();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        OtherUtils.hideProgressPop(pgd);
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }

                    @Override
                    public void onError(Throwable e) {
                        OtherUtils.hideProgressPop(pgd);
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }
                });
            }
        }.checkBoard(activity, RefreshWifiRxActivity.class);
    }

    /**
     * 获取sim状态
     */
    private void obtainSimStatus() {
        API.get().getSimStatus(new MySubscriber<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                normalSimStatusNext(result);
                int simState = result.getSIMState();
                switch (simState) {
                    case Cons.NONE:
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
                OtherUtils.hideProgressPop(pgd);
                toast(R.string.connect_failed);
                to(RefreshWifiRxActivity.class);
            }
        });
    }

    /**
     * 延迟迭代请求
     */
    private void delayRepeatGetSimstatu() {
        handler = new Handler();
        handler.postDelayed(this::obtainSimStatus, 2500);
    }

    private void toast(int resId) {
        ToastUtil_m.show(activity, resId);
    }

    private void to(Class ac) {
        CA.toActivity(activity, ac, false, true, false, 0);
    }
    
    /* -------------------------------------------- INTERFACE -------------------------------------------- */

    public interface OnNormalSimstatusListener {
        void normalSimStatu(SimStatus simStatus);
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

    public void setOnNormalSimstatusListener(OnNormalSimstatusListener onNormalSimstatusListener) {
        this.onNormalSimstatusListener = onNormalSimstatusListener;
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
    private void normalSimStatusNext(SimStatus result) {
        if (onNormalSimstatusListener != null) {
            onNormalSimstatusListener.normalSimStatu(result);
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
