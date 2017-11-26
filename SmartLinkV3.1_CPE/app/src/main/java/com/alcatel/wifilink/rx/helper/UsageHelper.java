package com.alcatel.wifilink.rx.helper;

import android.content.Context;

import com.alcatel.wifilink.R;

/**
 * Created by qianli.ma on 2017/11/24 0024.
 */

public class UsageHelper {

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
     * 获取单位以及换算
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

    /* 保留两位小数 */
    private static float mathRound(float value) {
        long dimension = 100;// 两位小数此处为100, 4位小数, 此处为10000....依此类推
        return (float) (Math.round(value * dimension)) / dimension;
    }

    public static class Usage {
        public String unit;
        public String usage;
    }

}
