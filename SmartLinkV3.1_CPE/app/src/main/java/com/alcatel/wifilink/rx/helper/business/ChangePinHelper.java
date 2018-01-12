package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;
import android.text.TextUtils;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.sim.SimStatus;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.PinStatuHelper;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.orhanobut.logger.Logger;

/**
 * Created by qianli.ma on 2017/12/12 0012.
 */

public class ChangePinHelper {

    private Activity activity;

    public ChangePinHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 修改
     *
     * @param currentCode 当前PIN码
     * @param refreshCode 新的PIN码
     */
    public void change(String currentCode, String refreshCode, String confirmCode) {
        PinStatuHelper pinStatuHelper = new PinStatuHelper(activity);
        pinStatuHelper.setOnNormalPinStatesListener(attr -> {
            if (attr.getPinState() != Cons.PINSTATES_PIN_ENABLE_VERIFIED) {// PIN码没有开启状态下
                Logger.t("ma_changpin").v("not changpincode");
                toast(R.string.please_enable_sim_pin);
                return;
            } else {// PIN码已经开启且有效
                changePin(currentCode, refreshCode, confirmCode);
            }

        });
        pinStatuHelper.getOne();
    }

    private void changePin(String currentCode, String refreshCode, String confirmCode) {
        // 条件判断
        boolean currentCodeEmpty = TextUtils.isEmpty(currentCode);
        boolean refreshCodeEmpty = TextUtils.isEmpty(refreshCode);
        boolean confirmCodeEmpty = TextUtils.isEmpty(confirmCode);
        if (currentCodeEmpty || refreshCodeEmpty || confirmCodeEmpty) {
            toast(R.string.pin_empty);
            return;
        }

        boolean currentLengthNotMatch = currentCode.length() < 4 || currentCode.length() > 8;
        boolean refreshLengthNotMatch = refreshCode.length() < 4 || refreshCode.length() > 8;
        boolean confirmLengthNotMatch = confirmCode.length() < 4 || confirmCode.length() > 8;
        if (currentLengthNotMatch || refreshLengthNotMatch || confirmLengthNotMatch) {
            toast(R.string.the_pin_code_should_be_4_8_characters);
            return;
        }

        if (!refreshCode.equals(confirmCode)) {
            toast(R.string.pin_no_same);
            return;
        }

        // 提交
        RX.getInstant().unlockPin(currentCode, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                RX.getInstant().changePinCode(refreshCode, currentCode, new ResponseObject() {
                    @Override
                    protected void onSuccess(Object result) {
                        Logger.t("ma_changpin").v("changpincode");
                        toast(R.string.success);
                        changePinSuccessNext(result);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        toast(R.string.sim_unlocked_failed);
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast(R.string.sim_unlocked_failed);
                    }
                });
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                getPinRemaingTime(error);
            }

            @Override
            public void onError(Throwable e) {
                toast(R.string.fail);
            }
        });
    }

    /**
     * 获取剩余次数
     */
    private void getPinRemaingTime(ResponseBody.Error error) {
        // 提示
        if (error.getCode().equalsIgnoreCase("020201")) {
            toast(R.string.pin_error_waring_title);
        } else {
            toast(R.string.setting_failed);
        }
        // 获取剩余次数
        RX.getInstant().getSimStatus(new ResponseObject<SimStatus>() {
            @Override
            protected void onSuccess(SimStatus result) {
                if (result.getSIMState() == Cons.PIN_REQUIRED) {
                    int pinRemainingTimes = result.getPinRemainingTimes();
                    if (pinRemainingTimes >= 1) {
                        String tip = pinRemainingTimes + " " + activity.getString(R.string.sim_unlocked_attempts);
                        toast(tip);
                    } else {
                        toast(R.string.Home_PinTimes_UsedOut);
                    }
                } else if (result.getSIMState() == Cons.PUK_REQUIRED) {
                    pinTimeoutNext(result);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                toast(R.string.setting_failed);
            }

            @Override
            public void onError(Throwable e) {
                toast(R.string.setting_failed);
            }
        });
    }

    private OnChangePinSuccessListener onChangePinSuccessListener;

    // 接口OnChangePinSuccessListener
    public interface OnChangePinSuccessListener {
        void changePinSuccess(Object attr);
    }

    // 对外方式setOnChangePinSuccessListener
    public void setOnChangePinSuccessListener(OnChangePinSuccessListener onChangePinSuccessListener) {
        this.onChangePinSuccessListener = onChangePinSuccessListener;
    }

    // 封装方法changePinSuccessNext
    private void changePinSuccessNext(Object attr) {
        if (onChangePinSuccessListener != null) {
            onChangePinSuccessListener.changePinSuccess(attr);
        }
    }

    private OnPinTimeoutListener onPinTimeoutListener;

    // 接口OnPinTimeoutListener
    public interface OnPinTimeoutListener {
        void pinTimeout(Object attr);
    }

    // 对外方式setOnPinTimeoutListener
    public void setOnPinTimeoutListener(OnPinTimeoutListener onPinTimeoutListener) {
        this.onPinTimeoutListener = onPinTimeoutListener;
    }

    // 封装方法pinTimeoutNext
    private void pinTimeoutNext(Object attr) {
        if (onPinTimeoutListener != null) {
            onPinTimeoutListener.pinTimeout(attr);
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
