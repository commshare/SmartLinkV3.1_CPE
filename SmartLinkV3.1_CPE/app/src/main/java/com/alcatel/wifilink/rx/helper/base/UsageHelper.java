package com.alcatel.wifilink.rx.helper.base;

import android.content.Context;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.Usage.UsageRecord;
import com.alcatel.wifilink.model.Usage.UsageSettings;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.NetworkRegisterState;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by qianli.ma on 2017/11/24 0024.
 */

public class UsageHelper {

    private Context context;

    public UsageHelper(Context context) {
        this.context = context;
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long time_l = System.currentTimeMillis();
        return sdf.format(time_l);
    }


    /**
     * 获取已经使用流量比率
     *
     * @param used
     * @param monthly
     * @return
     */
    public static int getRateUsed(long used, long monthly) {
        if (monthly <= 0) {
            return 10;// 最低10
        }
        float rate = mathRound(used * 1f / monthly);
        int v = (int) (rate * 100);
        v = v > 92 ? 92 : v;// 最大92
        return v;
    }

    /**
     * 获取单位以及换算(带小数)
     *
     * @param context
     * @param bytes
     * @return
     */
    public static Usage getUsageByte(Context context, long bytes) {
        float tempGB = bytes * 1f / 1024 / 1024 / 1024;
        float tempMB = bytes * 1f / 1024 / 1024;
        Usage usage = new Usage();
        if (tempGB >= 1) {
            usage.usage = String.valueOf(mathRound(tempGB));
            usage.unit = context.getString(R.string.gb_text);
        } else {
            usage.usage = String.valueOf(mathRound(tempMB));
            usage.unit = context.getString(R.string.mb_text);
        }
        return usage;
    }

    /**
     * 获取时间对象(传入时间以秒为单位)
     *
     * @param context
     * @param sec     总秒数
     * @return
     */
    public static Times getUsedTimeForSec(Context context, int sec) {
        int hours = sec / 60 / 60;
        int mins = sec % 60 / 60;

        Times times = new Times();
        times.hour = String.valueOf(hours);
        times.min = String.valueOf(mins);

        return times;
    }

    /**
     * 获取时间对象(传入时间以分钟为单位)
     *
     * @param context
     * @param min     总分钟数
     * @return
     */
    public static Times getUsedTimeForMin(Context context, int min) {
        int hours = min / 60;
        int mins = min % 60;

        Times times = new Times();
        times.hour = String.valueOf(hours);
        times.min = String.valueOf(mins);

        return times;
    }

    /* 保留两位小数 */
    private static float mathRound(float value) {
        long dimension = 100;// 两位小数此处为100, 4位小数, 此处为10000....依此类推
        return (float) (Math.round(value * dimension)) / dimension;
    }
    
    /* 保留N位小数 */
    private static float mathRoundByDimension(float value,int dimen) {
        long dimension = dimen;// 两位小数此处如为100,则表示保留两位小数, 4位小数, 此处为10000....依此类推
        return (float) (Math.round(value * dimension)) / dimension;
    }

    public static class Usage {
        public String unit;
        public String usage;
    }

    public static class Times {
        public String hour;
        public String min;
    }

    /* -------------------------------------------- method1 -------------------------------------------- */

    /**
     * 获取流量设置的相关信息(monthly data limit,biilling day,usage alert,auto disconnect....)
     */
    public void getUsageSettingAll() {
        RX.getInstant().getUsageSettings(new ResponseObject<UsageSettings>() {
            @Override
            protected void onSuccess(UsageSettings result) {
                usageSettingNext(result);
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

    private OnUsageSettingListener onUsageSettingListener;

    // 接口OnUsageSettingListener
    public interface OnUsageSettingListener {
        void usageSetting(UsageSettings attr);
    }

    // 对外方式setOnUsageSettingListener
    public void setOnUsageSettingListener(OnUsageSettingListener onUsageSettingListener) {
        this.onUsageSettingListener = onUsageSettingListener;
    }

    // 封装方法usageSettingNext
    private void usageSettingNext(UsageSettings attr) {
        if (onUsageSettingListener != null) {
            onUsageSettingListener.usageSetting(attr);
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

    /* -------------------------------------------- method2 -------------------------------------------- */

    /**
     * 获取流量超限状况
     */
    public void getOverUsage() {
        RX.getInstant().getUsageSettings(new ResponseObject<UsageSettings>() {
            @Override
            protected void onSuccess(UsageSettings result) {
                long monthlyPlan = result.getMonthlyPlan();
                long usedData = result.getUsedData();
                if (usedData >= monthlyPlan) {
                    overMonthlyNext(result);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                getUsageResultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                getUsageErrorNext(e);
            }
        });
    }

    private OnOverMonthlyListener onOverMonthlyListener;

    // 接口OnOverMonthlyListener
    public interface OnOverMonthlyListener {
        void overMonthly(UsageSettings attr);
    }

    // 对外方式setOnOverMonthlyListener
    public void setOnOverMonthlyListener(OnOverMonthlyListener onOverMonthlyListener) {
        this.onOverMonthlyListener = onOverMonthlyListener;
    }

    // 封装方法overMonthlyNext
    private void overMonthlyNext(UsageSettings attr) {
        if (onOverMonthlyListener != null) {
            onOverMonthlyListener.overMonthly(attr);
        }
    }

    private OnGetUsageResultErrorListener onGetUsageResultErrorListener;

    // 接口OnGetUsageResultErrorListener
    public interface OnGetUsageResultErrorListener {
        void getUsageResultError(ResponseBody.Error attr);
    }

    // 对外方式setOnGetUsageResultErrorListener
    public void setOnGetUsageResultErrorListener(OnGetUsageResultErrorListener onGetUsageResultErrorListener) {
        this.onGetUsageResultErrorListener = onGetUsageResultErrorListener;
    }

    // 封装方法getUsageResultErrorNext
    private void getUsageResultErrorNext(ResponseBody.Error attr) {
        if (onGetUsageResultErrorListener != null) {
            onGetUsageResultErrorListener.getUsageResultError(attr);
        }
    }

    private OnGetUsageErrorListener onGetUsageErrorListener;

    // 接口OnGetUsageErrorListener
    public interface OnGetUsageErrorListener {
        void getUsageError(Throwable attr);
    }

    // 对外方式setOnGetUsageErrorListener
    public void setOnGetUsageErrorListener(OnGetUsageErrorListener onGetUsageErrorListener) {
        this.onGetUsageErrorListener = onGetUsageErrorListener;
    }

    // 封装方法getUsageErrorNext
    private void getUsageErrorNext(Throwable attr) {
        if (onGetUsageErrorListener != null) {
            onGetUsageErrorListener.getUsageError(attr);
        }
    }

    /* -------------------------------------------- method3 -------------------------------------------- */

    /**
     * 获取漫游信息
     */
    public void getRoamingInfo() {
        String currentTime = UsageHelper.getCurrentTime();
        RX.getInstant().getNetworkRegisterState(new ResponseObject<NetworkRegisterState>() {
            @Override
            protected void onSuccess(NetworkRegisterState result) {
                int regist_state = result.getRegist_state();
                if (regist_state == Cons.REGISTER_SUCCESSFUL) {
                    RX.getInstant().getNetworkInfo(new ResponseObject<NetworkInfos>() {
                        @Override
                        protected void onSuccess(NetworkInfos networkInfos) {
                            RX.getInstant().getUsageRecord(currentTime, new ResponseObject<UsageRecord>() {
                                @Override
                                protected void onSuccess(UsageRecord result) {
                                    if (networkInfos.getRoaming() == Cons.ROAMING) {
                                        roamingNext(result);
                                    } else {
                                        noRoamingNext(result);
                                    }
                                }

                                @Override
                                protected void onResultError(ResponseBody.Error error) {
                                    Logger.t("ma_usage").v(error.getMessage());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Logger.t("ma_usage").v(e.getMessage());
                                }
                            });

                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            Logger.t("ma_usage").v(error.getMessage());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.t("ma_usage").v(e.getMessage());
                        }
                    });
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Logger.t("ma_usage").v(error.getMessage());
            }

            @Override
            public void onError(Throwable e) {
                Logger.t("ma_usage").v(e.getMessage());
            }
        });
    }

    private OnNoRoamingListener onNoRoamingListener;

    // 接口OnNoRoamingListener
    public interface OnNoRoamingListener {
        void noRoaming(UsageRecord attr);
    }

    // 对外方式setOnNoRoamingListener
    public void setOnNoRoamingListener(OnNoRoamingListener onNoRoamingListener) {
        this.onNoRoamingListener = onNoRoamingListener;
    }

    // 封装方法noRoamingNext
    private void noRoamingNext(UsageRecord attr) {
        if (onNoRoamingListener != null) {
            onNoRoamingListener.noRoaming(attr);
        }
    }

    private OnRoamingListener onRoamingListener;

    // 接口OnRoamingListener
    public interface OnRoamingListener {
        void roaming(UsageRecord result);
    }

    // 对外方式setOnRoamingListener
    public void setOnRoamingListener(OnRoamingListener onRoamingListener) {
        this.onRoamingListener = onRoamingListener;
    }

    // 封装方法roamingNext
    private void roamingNext(UsageRecord attr) {
        if (onRoamingListener != null) {
            onRoamingListener.roaming(attr);
        }
    }

}
