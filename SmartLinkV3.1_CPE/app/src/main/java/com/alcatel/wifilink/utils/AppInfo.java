package com.alcatel.wifilink.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

/**
 * @作者 qianli
 * @开发时间 下午7:06:50
 * @功能描述 检测当前处于最顶端的任务包名
 * @SVN更新者 $Author$
 * @SVN更新时间 $Date$
 * @SVN当前版本 $Rev$
 */
public class AppInfo {

    /**
     * 获取正在运行的包名
     *
     * @param context
     * @return
     */
    public static String getCurrentPackageName(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(5);
        String packageName = runningTasks.get(0).topActivity.getPackageName();// 得到最顶端的任务包名
        return packageName;
    }

    /**
     * 获取当前正在运行的Activity名(全名)
     *
     * @param context
     * @return
     */
    public static String getCurrentActivityName(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(5);
        String activityName = runningTasks.get(0).topActivity.getShortClassName();// 得到最顶端的Activity名称
        return activityName;
    }

    /**
     * 获取当前正在运行的Activity名(简称)
     *
     * @param context
     * @return
     */
    public static String getCurrentActivitySimpleName(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(5);
        String compnetName = runningTasks.get(0).topActivity.toShortString();
        String activityName = runningTasks.get(0).topActivity.getShortClassName();// 得到最顶端的Activity名称
        // String activityName = runningTasks.get(0).topActivity.getClass().getSimpleName();
        return activityName;
    }

    /**
     * 获取全部正在运行中的包名
     *
     * @param context
     * @return
     */
    public static List<String> getAllRunningPackage(Context context) {
        List<String> pkgs = new ArrayList<String>();
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(1000);
        for (RunningTaskInfo rti : runningTasks) {
            String pkgName = rti.topActivity.getPackageName();
            pkgs.add(pkgName);
        }
        return pkgs;
    }

}
