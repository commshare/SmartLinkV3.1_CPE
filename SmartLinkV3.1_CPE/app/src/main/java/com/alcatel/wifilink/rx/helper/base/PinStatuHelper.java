package com.alcatel.wifilink.rx.helper.base;

import android.app.Activity;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;

/**
 * Created by qianli.ma on 2017/12/12 0012.
 */

public class PinStatuHelper {
    private Activity activity;

    public PinStatuHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 定时器使用该方法请求pins state
     */
    public void getRoll() {
        BoardSimHelper boardSimHelper = new BoardSimHelper(activity);
        boardSimHelper.setOnNormalSimstatusListener(result -> {
            normalPinStatesNext(result);
            int pinState = result.getPinState();
            switch (pinState) {
                case Cons.PINSTATES_UNKNOWN:
                    unknownNext(result);
                    break;
                case Cons.PINSTATES_ENABLE_BUT_NOT_VERIFIED:
                    pinEnableButNotVerifyNext(result);
                    break;
                case Cons.PINSTATES_PIN_ENABLE_VERIFIED:
                    pinEnableVerifyNext(result);
                    break;
                case Cons.PINSTATES_PIN_DISABLE:
                    pinDisableNext(result);
                    break;
                case Cons.PINSTATES_PUK_REQUIRED:
                    pukRequiredNext(result);
                    break;
                case Cons.PINSTATES_PUK_TIMES_USED_OUT:
                    pukTimeoutNext(result);
                    break;
            }
        });
        boardSimHelper.boardTimer();
    }

    /**
     * 点击事件使用该方法请求pins state
     */
    public void getOne() {
        // 1.先获取SIM卡的状态
        BoardSimHelper boardSimHelper = new BoardSimHelper(activity);
        boardSimHelper.setOnNormalSimstatusListener(result -> {
            int simState = result.getSIMState();
            switch (simState) {
                case Cons.NOWN:
                    toast(R.string.not_inserted);
                    break;
                case Cons.INITING:
                    toast(R.string.home_initializing);
                    break;
                case Cons.DETECTED:
                    toast(R.string.Home_SimCard_Detected);
                    break;
                case Cons.ILLEGAL:
                    toast(R.string.Home_sim_invalid);
                    break;
                case Cons.PUK_TIMESOUT:
                    toast(R.string.Home_PukTimes_UsedOut);
                    break;
                default:
                    // 2.获取PIN status
                    getPinStatus(result);
                    break;
            }
        });
        boardSimHelper.boardNormal();
    }

    /**
     * 获取PIN status
     *
     * @param result
     */
    private void getPinStatus(SimStatus result) {
        normalPinStatesNext(result);
        int pinState = result.getPinState();
        switch (pinState) {
            case Cons.PINSTATES_UNKNOWN:
                unknownNext(result);
                break;
            case Cons.PINSTATES_ENABLE_BUT_NOT_VERIFIED:
                pinEnableButNotVerifyNext(result);
                break;
            case Cons.PINSTATES_PIN_ENABLE_VERIFIED:
                pinEnableVerifyNext(result);
                break;
            case Cons.PINSTATES_PIN_DISABLE:
                pinDisableNext(result);
                break;
            case Cons.PINSTATES_PUK_REQUIRED:
                pukRequiredNext(result);
                break;
            case Cons.PINSTATES_PUK_TIMES_USED_OUT:
                pukTimeoutNext(result);
                break;
        }
    }

    /* -------------------------------------------- interface -------------------------------------------- */

    private OnPukTimeoutListener onPukTimeoutListener;

    // 接口OnPukTimeoutListener
    public interface OnPukTimeoutListener {
        void pukTimeout(SimStatus attr);
    }

    // 对外方式setOnPukTimeoutListener
    public void setOnPukTimeoutListener(OnPukTimeoutListener onPukTimeoutListener) {
        this.onPukTimeoutListener = onPukTimeoutListener;
    }

    // 封装方法pukTimeoutNext
    private void pukTimeoutNext(SimStatus attr) {
        if (onPukTimeoutListener != null) {
            onPukTimeoutListener.pukTimeout(attr);
        }
    }

    private OnPukRequiredListener onPukRequiredListener;

    // 接口OnPukRequiredListener
    public interface OnPukRequiredListener {
        void pukRequired(SimStatus attr);
    }

    // 对外方式setOnPukRequiredListener
    public void setOnPukRequiredListener(OnPukRequiredListener onPukRequiredListener) {
        this.onPukRequiredListener = onPukRequiredListener;
    }

    // 封装方法pukRequiredNext
    private void pukRequiredNext(SimStatus attr) {
        if (onPukRequiredListener != null) {
            onPukRequiredListener.pukRequired(attr);
        }
    }

    private OnPinDisableListener onPinDisableListener;

    // 接口OnPinDisableListener
    public interface OnPinDisableListener {
        void pinDisable(SimStatus attr);
    }

    // 对外方式setOnPinDisableListener
    public void setOnPinDisableListener(OnPinDisableListener onPinDisableListener) {
        this.onPinDisableListener = onPinDisableListener;
    }

    // 封装方法pinDisableNext
    private void pinDisableNext(SimStatus attr) {
        if (onPinDisableListener != null) {
            onPinDisableListener.pinDisable(attr);
        }
    }

    private OnPinEnableVerifyListener onPinEnableVerifyListener;

    // 接口OnPinEnableVerifyListener
    public interface OnPinEnableVerifyListener {
        void pinEnableVerify(SimStatus attr);
    }

    // 对外方式setOnPinEnableVerifyListener
    public void setOnPinEnableVerifyListener(OnPinEnableVerifyListener onPinEnableVerifyListener) {
        this.onPinEnableVerifyListener = onPinEnableVerifyListener;
    }

    // 封装方法pinEnableVerifyNext
    private void pinEnableVerifyNext(SimStatus attr) {
        if (onPinEnableVerifyListener != null) {
            onPinEnableVerifyListener.pinEnableVerify(attr);
        }
    }

    private OnPinEnableButNotVerifyListener onPinEnableButNotVerifyListener;

    // 接口OnPinEnableButNotVerifyListener
    public interface OnPinEnableButNotVerifyListener {
        void pinEnableButNotVerify(SimStatus attr);
    }

    // 对外方式setOnPinEnableButNotVerifyListener
    public void setOnPinEnableButNotVerifyListener(OnPinEnableButNotVerifyListener onPinEnableButNotVerifyListener) {
        this.onPinEnableButNotVerifyListener = onPinEnableButNotVerifyListener;
    }

    // 封装方法pinEnableButNotVerifyNext
    private void pinEnableButNotVerifyNext(SimStatus attr) {
        if (onPinEnableButNotVerifyListener != null) {
            onPinEnableButNotVerifyListener.pinEnableButNotVerify(attr);
        }
    }

    private OnUnkonwnListener onUnkonwnListener;

    // 接口OnUnkonwnListener
    public interface OnUnkonwnListener {
        void unknown(SimStatus attr);
    }

    // 对外方式setOnUnkonwnListener
    public void setOnUnkonwnListener(OnUnkonwnListener onUnkonwnListener) {
        this.onUnkonwnListener = onUnkonwnListener;
    }

    // 封装方法unknownNext
    private void unknownNext(SimStatus attr) {
        if (onUnkonwnListener != null) {
            onUnkonwnListener.unknown(attr);
        }
    }

    private OnNormalPinStatesListener onNormalPinStatesListener;

    // 接口OnNormalPinStatesListener
    public interface OnNormalPinStatesListener {
        void normalPinStates(SimStatus attr);
    }

    // 对外方式setOnNormalPinStatesListener
    public void setOnNormalPinStatesListener(OnNormalPinStatesListener onNormalPinStatesListener) {
        this.onNormalPinStatesListener = onNormalPinStatesListener;
    }

    // 封装方法normalPinStatesNext
    private void normalPinStatesNext(SimStatus attr) {
        if (onNormalPinStatesListener != null) {
            onNormalPinStatesListener.normalPinStates(attr);
        }
    }

    /* -------------------------------------------- helper -------------------------------------------- */
    public void toast(int resId) {
        ToastUtil_m.show(activity, resId);
    }

    public void toastLong(int resId) {
        ToastUtil_m.showLong(activity, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(activity, content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(activity, ac, false, isFinish, false, 0);
    }

}
