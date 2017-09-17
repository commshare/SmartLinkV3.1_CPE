package com.alcatel.wifilink.ui.home.helper.main;

/**
 * Created by qianli.ma on 2017/9/17.
 */

public class TrafficHelper {

    /**
     * 计算百分比
     *
     * @param user
     * @param monthly
     * @return
     */
    public static double cacuPercent(long user, long monthly) {
        float per = user * 1f / monthly;
        return mathRound(per, 2) * 100;
    }

    /**
     * 选定类型(GB|MB|KB...)
     */
    public static String getTrafficType(long traffic) {
        String type = "MB";
        double B = traffic * 1d;
        double KB = B / 1024;
        double MB = KB / 1024;
        double GB = MB / 1024;
        double TB = GB / 1024;
        double PB = TB / 1024;
        if (TB >= 1024) {
            type = "PB";
        } else if (GB >= 1024) {
            type = "TB";
        } else if (MB >= 1024) {
            type = "GB";
        } else if (KB >= 1024) {
            type = "MB";
        } else if (B >= 1024) {
            type = "KB";
        } else {
            type = "B";
        }
        return type;
    }

    /**
     * 获取已用流量显示字符
     *
     * @param userTraffic 流量字节
     * @return
     */
    public static String getUserTrafficString(long userTraffic) {
        double defaultNum = 0.00;// 默认流量
        double B = userTraffic * 1d;
        double KB = B / 1024;
        double MB = KB / 1024;
        double GB = MB / 1024;
        double TB = GB / 1024;
        double PB = TB / 1024;
        // 获取流量类型
        String type = getTrafficType(userTraffic);
        // 1MB以下1位小数,1MB以上2位小数
        switch (type) {
            case "B":
                defaultNum = B;
                break;
            case "KB":
                defaultNum = mathRound(KB, 1);
                break;
            case "MB":
                defaultNum = mathRound(MB, 2);
                break;
            case "GB":
                defaultNum = mathRound(GB, 2);
                break;
            case "TB":
                defaultNum = mathRound(TB, 2);
                break;
            case "PB":
                defaultNum = mathRound(PB, 2);
                break;
        }
        return String.valueOf(defaultNum);
    }

    /**
     * 获取月流量显示字符
     *
     * @param montylyTraffic
     * @return
     */
    public static String getMonthlyTrafficString(long montylyTraffic) {
        return getUserTrafficString(montylyTraffic);
    }

    /* floats: 需要保留的小数位数 */
    private static double mathRound(double value, int n) {
        double dimension = Math.pow(10, n);// 表示10的N次方
        return (double) (Math.round(value * dimension)) / dimension;
    }

}
