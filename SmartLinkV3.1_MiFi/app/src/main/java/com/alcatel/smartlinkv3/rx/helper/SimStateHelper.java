package com.alcatel.smartlinkv3.rx.helper;

import android.app.Activity;
import android.os.Handler;

import com.alcatel.smartlinkv3.rx.bean.SimState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;

/**
 * Created by qianli.ma on 2017/12/28 0028.
 */

public class SimStateHelper {

    private Activity activity;
    private Handler handler;

    public SimStateHelper(Activity activity) {
        this.activity = activity;
        handler = new Handler();
    }

    public void get() {
        API.get().getSimStatus(new MySubscriber<SimState>() {
            @Override
            protected void onSuccess(SimState result) {
                normalNext(result);
                switch (result.getSIMState()) {
                    case Cons.NOWN:
                        nownNext(result);
                        break;
                    case Cons.SIM_CARD_DETECTED:
                        detectedNext(result);
                        handler.postDelayed(() -> get(), 2000);
                        break;
                    case Cons.PIN_REQUIRED:
                        pinRequiredNext(result);
                        break;
                    case Cons.PUK_REQUIRED:
                        pukRequiredNext(result);
                        break;
                    case Cons.SIM_LOCK_REQUIRED:
                        simlockedNext(result);
                        break;
                    case Cons.PUK_TIMES_USED_OUT:
                        pukTimeoutNext(result);
                        break;
                    case Cons.SIM_CARD_ILLEGAL:
                        simIllegalNext(result);
                        break;
                    case Cons.SIM_CARD_READY:
                        simReadyNext(result);
                        break;
                    case Cons.SIM_CARD_IS_INITING:
                        simInitingNext(result);
                        handler.postDelayed(() -> get(), 2000);
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

    private OnSimInitingListener onSimInitingListener;

    // 接口OnSimInitingListener
    public interface OnSimInitingListener {
        void simIniting(SimState attr);
    }

    // 对外方式setOnSimInitingListener
    public void setOnSimInitingListener(OnSimInitingListener onSimInitingListener) {
        this.onSimInitingListener = onSimInitingListener;
    }

    // 封装方法simInitingNext
    private void simInitingNext(SimState attr) {
        if (onSimInitingListener != null) {
            onSimInitingListener.simIniting(attr);
        }
    }

    private OnSimReadyListener onSimReadyListener;

    // 接口OnSimReadyListener
    public interface OnSimReadyListener {
        void simReady(SimState attr);
    }

    // 对外方式setOnSimReadyListener
    public void setOnSimReadyListener(OnSimReadyListener onSimReadyListener) {
        this.onSimReadyListener = onSimReadyListener;
    }

    // 封装方法simReadyNext
    private void simReadyNext(SimState attr) {
        if (onSimReadyListener != null) {
            onSimReadyListener.simReady(attr);
        }
    }

    private OnSimIllegalListener onSimIllegalListener;

    // 接口OnPinIllegalListener
    public interface OnSimIllegalListener {
        void simIllegal(SimState attr);
    }

    // 对外方式setOnPinIllegalListener
    public void setOnSimIllegalListener(OnSimIllegalListener onSimIllegalListener) {
        this.onSimIllegalListener = onSimIllegalListener;
    }

    // 封装方法pinIllegalNext
    private void simIllegalNext(SimState attr) {
        if (onSimIllegalListener != null) {
            onSimIllegalListener.simIllegal(attr);
        }
    }

    private OnPukTimeoutListener onPukTimeoutListener;

    // 接口OnPukTimeoutListener
    public interface OnPukTimeoutListener {
        void pukTimeout(SimState attr);
    }

    // 对外方式setOnPukTimeoutListener
    public void setOnPukTimeoutListener(OnPukTimeoutListener onPukTimeoutListener) {
        this.onPukTimeoutListener = onPukTimeoutListener;
    }

    // 封装方法pukTimeoutNext
    private void pukTimeoutNext(SimState attr) {
        if (onPukTimeoutListener != null) {
            onPukTimeoutListener.pukTimeout(attr);
        }
    }

    private OnSimLockedListener onSimLockedListener;

    // 接口OnSimLockedListener
    public interface OnSimLockedListener {
        void simlocked(SimState attr);
    }

    // 对外方式setOnSimLockedListener
    public void setOnSimLockedListener(OnSimLockedListener onSimLockedListener) {
        this.onSimLockedListener = onSimLockedListener;
    }

    // 封装方法simlockedNext
    private void simlockedNext(SimState attr) {
        if (onSimLockedListener != null) {
            onSimLockedListener.simlocked(attr);
        }
    }

    private OnPukRequiredListener onPukRequiredListener;

    // 接口OnPukRequiredListener
    public interface OnPukRequiredListener {
        void pukRequired(SimState attr);
    }

    // 对外方式setOnPukRequiredListener
    public void setOnPukRequiredListener(OnPukRequiredListener onPukRequiredListener) {
        this.onPukRequiredListener = onPukRequiredListener;
    }

    // 封装方法pukRequiredNext
    private void pukRequiredNext(SimState attr) {
        if (onPukRequiredListener != null) {
            onPukRequiredListener.pukRequired(attr);
        }
    }

    private OnPinRequiredListener onPinRequiredListener;

    // 接口OnPinRequiredListener
    public interface OnPinRequiredListener {
        void pinRequired(SimState attr);
    }

    // 对外方式setOnPinRequiredListener
    public void setOnPinRequiredListener(OnPinRequiredListener onPinRequiredListener) {
        this.onPinRequiredListener = onPinRequiredListener;
    }

    // 封装方法pinRequiredNext
    private void pinRequiredNext(SimState attr) {
        if (onPinRequiredListener != null) {
            onPinRequiredListener.pinRequired(attr);
        }
    }

    private OnDetectedListener onDetectedListener;

    // 接口OnDetectedListener
    public interface OnDetectedListener {
        void detected(SimState attr);
    }

    // 对外方式setOnDetectedListener
    public void setOnDetectedListener(OnDetectedListener onDetectedListener) {
        this.onDetectedListener = onDetectedListener;
    }

    // 封装方法detectedNext
    private void detectedNext(SimState attr) {
        if (onDetectedListener != null) {
            onDetectedListener.detected(attr);
        }
    }

    private OnNownListener onNownListener;

    // 接口OnNownListener
    public interface OnNownListener {
        void nown(SimState attr);
    }

    // 对外方式setOnNownListener
    public void setOnNownListener(OnNownListener onNownListener) {
        this.onNownListener = onNownListener;
    }

    // 封装方法nownNext
    private void nownNext(SimState attr) {
        if (onNownListener != null) {
            onNownListener.nown(attr);
        }
    }

    private OnNormalListener onNormalListener;

    // 接口OnNormalListener
    public interface OnNormalListener {
        void normal(SimState simState);
    }

    // 对外方式setOnNormalListener
    public void setOnNormalListener(OnNormalListener onNormalListener) {
        this.onNormalListener = onNormalListener;
    }

    // 封装方法normalNext
    private void normalNext(SimState simState) {
        if (onNormalListener != null) {
            onNormalListener.normal(simState);
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
